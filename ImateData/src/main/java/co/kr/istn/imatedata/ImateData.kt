package co.kr.istn.imatedata

import co.kr.istn.utility.*
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.CookieJar
import okhttp3.Credentials
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.net.CookieManager
import java.net.CookiePolicy
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ImateData 처리 클래스
 *
 * @param baseUrl 가준 URL
 * @param userId 사용자ID
 * @param userPassword 암호
 * @param connectTimeout 연결 Timeout
 * @param readTimeOut 요청후 응답 까지의 Timeout
 * @param writeTimeout 응답시작 부터 완료 까지 Timeout
 */
class ImateData(val baseUrl:String,
                var userId: String = "",  var userPassword: String = "", var sslIngnore : Boolean = false,
                var connectTimeout : Long = 30, var readTimeOut : Long = 1200, var writeTimeout : Long = 300 ) {
    //쿼리 서비스
    private val queryService : ImateQueryService
    //HTTP 클라이언트
    private val queryServiceHttpClient : OkHttpClient.Builder
    //Token 유료 기간
    private var tokenExpires : Double = -1.0
    //토근
    private var jwtToken : String = ""
    //현재일
    private var tokenIssueTime : LocalDateTime = LocalDateTime.now()
    //토근 관리
    private var tokenManager : ImateTokenManager?

    /**
     * Token Service
     */
    var tokenServiceAdapter : TokenServiceAdapter?

    /**
     * Device Token Id
     */
    var deviceTokenId : String

    /**
     * 마지막 오류 메시지
     */
    var lastErrorMessage : String


    //초기화
    init {

        lastErrorMessage = ""
        deviceTokenId = ""

        //https일 경우 테스트를 위해 안전하지 않은 SSL을 사용 한다.
        queryServiceHttpClient = if(baseUrl.startsWith("https", true) && sslIngnore)
                                    UnsafeOkHttpClient.getUnsafeOkHttpClient() else OkHttpClient.Builder()

        if(imateApiVersion == ImateApiVersion.V1 )
        {
            //V1은 토근 서비스를 사용 안함
            tokenManager = null;
            tokenServiceAdapter = null;

            //사용자 정보와 암호가 있으면 기본인증을 드록 한다.
            if(userId.isNotEmpty() && userPassword.isNotEmpty() )
            {
                val authToken = Credentials.basic(userId, userPassword)
                val interceptor = BasicAuthenticationInterceptor(authToken)
                if(!queryServiceHttpClient.interceptors().contains(interceptor))
                    queryServiceHttpClient.addInterceptor(interceptor)
            }
        }
        else
        {
            tokenManager = ImateTokenManager("");
            tokenServiceAdapter = TokenServiceAdapter(baseUrl, imateCookieJar, userId, userPassword, sslIngnore, connectTimeout, readTimeOut, writeTimeout)

            val authData = getImateAuthData(userId, userPassword)
            val interceptor = ImateAuthenticationInterceptor(authData, tokenManager!!)

            if(!queryServiceHttpClient.interceptors().contains(interceptor))
                queryServiceHttpClient.addInterceptor(interceptor)
        }

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val gsonFactory = GsonConverterFactory.create(gson)


        queryServiceHttpClient
            .cookieJar(imateCookieJar)
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeOut, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)

        //쿼리 서비스를 생성 한다.
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(queryServiceHttpClient.build())
                .addConverterFactory(gsonFactory)
                .build()

        queryService = retrofit.create(ImateQueryService::class.java)
    }

    /**
     * Token을 발행 한다.
     */
    fun tokenIssue() : String
    {
        if(imateApiVersion == ImateApiVersion.V1)
            return "";

        if(this.tokenExpires < 0.0)
            this.tokenExpires = tokenServiceAdapter!!.getTokenExpires()

        val toDay = LocalDateTime.now();

        var min = Duration.between(tokenIssueTime, toDay).toMinutes()
        if (!this.jwtToken.isNullOrEmpty() && this.tokenExpires > 0 && min < this.tokenExpires)
            return this.jwtToken;

        if (this.jwtToken.isNullOrEmpty()) {
            this.jwtToken = if(this.deviceTokenId.isNotEmpty())
                this.tokenServiceAdapter!!.getToken(this.deviceTokenId)
            else
                this.tokenServiceAdapter!!.getDefaultToken()

            this.tokenIssueTime = LocalDateTime.now();
        }

        return this.jwtToken;
    }

    /**
     * OTP 인증 비동기
     */
    fun otpAuthAsync(authInfo : ImateAuthInfo) : Deferred<Boolean>
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val Result = try {
                lastErrorMessage = ""
                tokenServiceAdapter?.otpAuth(authInfo)?:false;
            }catch (e: Exception) {
                lastErrorMessage = e.message?:""
                false
            }

            Result
        }
    }
    /**
     * 데이터 쿼리 배치 처리
     *
     * @param transactionId 트랜잭션ID
     * @param queryMesssages 쿼리 메시지
     */
    fun executeQueryBatchAsync(transactionId: String, queryMesssages : List<QueryMessage> ) : Deferred<QueryRunResult?>
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            tokenManager?.jwtToken = tokenIssue();

            val Result = try {
                val response = queryService.executeQueryBatch(
                    transactionId = transactionId,
                    queryMesssages = queryMesssages
                ).execute()

                if (response.isSuccessful)
                    response.body()
                else
                    QueryRunResult(transactionId, listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()?:"")
            }catch (e: Exception) {
                QueryRunResult(transactionId, listOf(), "FAIL", e.message?:"", e.localizedMessage?:"")
            }

            lastErrorMessage = Result?.apiMessage?:""
            Result
        }
    }

    /**
     * 비동기 데이터 쿼리 배치 처리
     *
     * @param transactionId 트랜잭션ID
     * @param queryMesssages 쿼리 메시지
     */
    fun executeQueryBatchAsync(transactionId: String, queryMesssages : List<QueryMessage>, callback: (result: QueryRunResult?) -> Unit)
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.async {
            tokenManager?.jwtToken = tokenIssue();

            queryService.executeQueryBatch(transactionId, queryMesssages)
                .enqueue(object : Callback<QueryRunResult> {
                    /**
                     * 요청 실패
                     */
                    override fun onFailure(call: Call<QueryRunResult>, t: Throwable) {
                        callback(
                            QueryRunResult(
                                transactionId,
                                listOf(),
                                "FAIL",
                                t.message ?: "",
                                t.localizedMessage ?: ""
                            )
                        )
                    }

                    /**
                     * 요청 성공
                     */
                    override fun onResponse(
                        call: Call<QueryRunResult>,
                        response: Response<QueryRunResult>
                    ) {
                        if (response.isSuccessful)
                            callback(response.body())
                        else
                            callback(
                                QueryRunResult(
                                    transactionId,
                                    listOf(),
                                    "FAIL",
                                    response.code().toString(),
                                    response.errorBody()?.string() ?: ""
                                )
                            )
                    }
                })
        }
    }

    /**
     * 쿼리를 실행 한다.
     *
     * @param transactionId 트랜잭션ID
     * @param dataSource 데이터 소스
     * @param query 쿼리
     */
    fun executeQueryAsync(transactionId: String, dataSource : String, query : String) : Deferred<QueryRunResult?>
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            tokenManager?.jwtToken = tokenIssue()

            var Result = try {
                val response = queryService.executeQuery(
                        transactionId = transactionId,
                        dataSource = dataSource,
                        query = query
                ).execute()

                if (response.isSuccessful)
                    response.body()
                else
                    QueryRunResult(transactionId, listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()?:"")
            }catch (e: Exception) {
                QueryRunResult(transactionId, listOf(), "FAIL", e.message?:"", e.localizedMessage?:"")
            }

            lastErrorMessage = Result?.apiMessage?:""
            Result
        }
    }

    /**
     * 쿼리를  비동기로 실행 한다.
     *
     * @param transactionId 트랜잭션ID
     * @param dataSource 데이터 소스
     * @param query 쿼리
     * @param callback 비동기 처리 완료후 호출 함수
     */
    fun executeQueryAsync(transactionId: String, dataSource : String, query : String, callback: (result: QueryRunResult?) -> Unit)
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.async {
            tokenManager?.jwtToken = tokenIssue();

            queryService.executeQuery(transactionId, dataSource, query)
                .enqueue(object : Callback<QueryRunResult> {
                    /**
                     * 요청 실패
                     */
                    override fun onFailure(call: Call<QueryRunResult>, t: Throwable) {
                        callback(
                            QueryRunResult(
                                transactionId,
                                listOf<QueryValue>(),
                                "FAIL",
                                t.message ?: "",
                                t.localizedMessage ?: ""
                            )
                        )
                    }

                    /**
                     * 요청 성공
                     */
                    override fun onResponse(
                        call: Call<QueryRunResult>,
                        response: Response<QueryRunResult>
                    ) {
                        if (response.isSuccessful)
                            callback(response.body())
                        else
                            callback(
                                QueryRunResult(
                                    transactionId,
                                    listOf<QueryValue>(),
                                    "FAIL",
                                    response.code().toString(),
                                    response.errorBody()?.string() ?: ""
                                )
                            )
                    }
                })
        }
    }

    /**
     * 쿼리를 실행 한다.
     *
     * @param transactionId 트랜잭션ID
     * @param dataSource 데이터 소스
     * @param query 쿼리
     */
    fun executeNoneQueryAsync(transactionId: String, dataSource : String, query : String) : Deferred<Int>
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            tokenManager?.jwtToken = tokenIssue()

            val Result = try {
                val response = queryService.executeQuery(
                        transactionId = transactionId,
                        dataSource = dataSource,
                        query = query
                ).execute()

                if(response.isSuccessful)
                    response.body()
                else
                    QueryRunResult(transactionId, listOf<QueryValue>(), "FAIL", response.code().toString(), response.errorBody()?.string()?:"" )
            }catch (e: Exception) {
                QueryRunResult(transactionId, listOf<QueryValue>(), "FAIL", e.message?:"", e.localizedMessage?:"")
            }

            lastErrorMessage = Result?.apiMessage?:""
            if(Result?.apiResult != "OK")
                -1
            else
                Result?.apiMessage!!.toInt()
        }
    }

    /**
     * 쿼리를  비동기로 실행 한다.
     *
     * @param transactionId 트랜잭션ID
     * @param dataSource 데이터 소스
     * @param query 쿼리
     * @param callback 비동기 처리 완료후 호출 함수
     */
    fun executeNoneQueryAsync(transactionId: String, dataSource : String, query : String, callback: (transactionId : String, apiResult: String, apiMessage: String, result: Int) -> Unit)
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.async {
            tokenManager?.jwtToken = tokenIssue();

            queryService.executeQuery(transactionId, dataSource, query)
                .enqueue(object : Callback<QueryRunResult> {
                    /**
                     * 요청 실패
                     */
                    override fun onFailure(call: Call<QueryRunResult>, t: Throwable) {
                        callback(transactionId, "FAIL", t.message ?: "", 0)
                    }

                    /**
                     * 요청 성공
                     */
                    override fun onResponse(
                        call: Call<QueryRunResult>,
                        response: Response<QueryRunResult>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result?.apiResult != "OK")
                                callback(
                                    transactionId,
                                    result?.apiResult ?: "FAIL",
                                    result?.apiMessage ?: "Result or Api Message Is Null",
                                    -1
                                )
                            else
                                callback(
                                    transactionId,
                                    result.apiResult,
                                    "",
                                    result.apiMessage.toInt()
                                )
                        } else {
                            callback(
                                transactionId,
                                "FAIL",
                                "${response.code()},${response.errorBody()?.string()}",
                                -1
                            )
                        }
                    }
                })
        }
    }
}