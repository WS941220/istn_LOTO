package co.kr.istn.smartLock

import co.kr.istn.imatedata.*
import co.kr.istn.utility.*
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import java.lang.Exception
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class SmartLockAdapter (val baseUrl:String,
                        var userId: String = "", var userPassword: String = "", var sslIgnore : Boolean = false,
                        var connectTimeout : Long = 30, var readTimeOut : Long = 1200, var writeTimeout : Long = 300 ) {
    //쿼리 서비스
    private val smartLockService : SmartLockService
    //HTTP 클라이언트
    private val smartLockServiceHttpClient : OkHttpClient.Builder
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
        smartLockServiceHttpClient = if(baseUrl.startsWith("https", true) && sslIgnore)
            UnsafeOkHttpClient.getUnsafeOkHttpClient() else OkHttpClient.Builder()

        tokenManager = ImateTokenManager("");
        tokenServiceAdapter = TokenServiceAdapter(baseUrl, imateCookieJar, userId, userPassword, sslIgnore, connectTimeout, readTimeOut, writeTimeout)

        val authData = getImateAuthData(userId, userPassword)
        val interceptor = ImateAuthenticationInterceptor(authData, tokenManager!!)

        if(!smartLockServiceHttpClient.interceptors().contains(interceptor))
            smartLockServiceHttpClient.addInterceptor(interceptor)

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        val gsonFactory = GsonConverterFactory.create(gson)

        smartLockServiceHttpClient
            .cookieJar(imateCookieJar)
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeOut, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)

        //smart Lock 서비스를 생성 한다.
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(smartLockServiceHttpClient.build())
            .addConverterFactory(gsonFactory)
            .build()

        smartLockService = retrofit.create(SmartLockService::class.java)
    }

    /**
     * Token을 발행 한다.
     */
    fun tokenIssue() : String
    {
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
     * 비동기 Lock정보를 가져온다
     *
     * @param lockInfo Lock 정보
     */
    fun getLockInfo(@Body() lockInfo : LockInfo ) : Deferred<LockInfo>
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            tokenManager?.jwtToken = tokenIssue();

            val Result = try {
                val response = smartLockService.getLockInfo(
                    lockInfo = lockInfo
                ).execute()

                if(response.isSuccessful)
                    response.body()!!
                else
                    LockInfo(serial = lockInfo.serial, userId = lockInfo.userId,  mac = "ERROR(${response.code().toString()})", tracking_key = response.errorBody()?.string()?:"")

            }catch (e: Exception) {
                lockInfo.mac = "ERROR"
                lockInfo.tracking_key = e.message?:""

                lockInfo
            }

            if(Result.mac.startsWith("ERROR"))
                lastErrorMessage = Result.tracking_key
            else
                lastErrorMessage = ""

            Result
        }
    }

    /**
     * 비동기 Lock정보를 가져온다
     *
     * @param lockInfo Lock 정보
     * @param callback Callback 함수
     */
    fun getLockInfoAsync(@Body() lockInfo : LockInfo, callback: (result: LockInfo?) -> Unit)
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.async {
            tokenManager?.jwtToken = tokenIssue();

            smartLockService.getLockInfo(lockInfo)
                .enqueue(object : Callback<LockInfo> {
                    /**
                     * 요청 실패
                     */
                    override fun onFailure(call: Call<LockInfo>, t: Throwable) {
                        callback(
                            LockInfo(serial = lockInfo.serial,
                                    userId = lockInfo.userId,
                                    mac = "ERROR",
                                    tracking_key = t.message?:"")
                        )
                    }

                    /**
                     * 요청 성공
                     */
                    override fun onResponse(
                        call: Call<LockInfo>,
                        response: Response<LockInfo>
                    ) {
                        if (response.isSuccessful)
                            callback(response.body())
                        else
                            callback(
                                LockInfo(serial = lockInfo.serial,
                                    userId = lockInfo.userId,
                                    mac = "ERROR(${response.code().toString()})",
                                    tracking_key = response.errorBody()?.string()?:"")
                            )
                    }
                })
        }
    }

    /**
     * Unlock Command를 얻는다.
     *
     * @param unlockRequest unlock 요청
     */
    fun unlock(@Body() unlockRequest : NokeUnlockRequest ) : Deferred<NokeUnlockResponse>
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            tokenManager?.jwtToken = tokenIssue();

            val Result = try {
                val response = smartLockService.Unlock(
                    unlockRequest = unlockRequest
                ).execute()

                if(response.isSuccessful)
                    response.body()!!
                else
                    NokeUnlockResponse(result = "error", message = response.errorBody()?.string()?:"", error_code = response.code(), request = "unlock", data = null, api_version = null, token =  null)

            }catch (e: Exception) {
                NokeUnlockResponse(result = "error", message = e.message?:"", error_code = -8, request = "unlock", data = null, api_version = null, token =  null)
            }

            if(!Result.result.startsWith("success"))
                lastErrorMessage = Result.message
            else
                lastErrorMessage = ""

            Result
        }
    }

    /**
     * Unlock Command를 얻는다.
     *
     * @param unlockRequest unlock 요청
     * @param callback 결과 Callback
     */
    fun unlockAsync(@Body() unlockRequest : NokeUnlockRequest , callback: (result: NokeUnlockResponse?) -> Unit)
    {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        scope.async {
            tokenManager?.jwtToken = tokenIssue();

            smartLockService.Unlock(unlockRequest)
                .enqueue(object : Callback<NokeUnlockResponse> {
                    /**
                     * 요청 실패
                     */
                    override fun onFailure(call: Call<NokeUnlockResponse>, t: Throwable) {
                        callback(
                            NokeUnlockResponse(result = "error",
                                message = t.message?:"",
                                error_code = -8,
                                request = "unlock",
                                data = null, api_version = null, token =  null)
                        )
                    }

                    /**
                     * 요청 성공
                     */
                    override fun onResponse(
                        call: Call<NokeUnlockResponse>,
                        response: Response<NokeUnlockResponse>
                    ) {
                        if (response.isSuccessful)
                            callback(response.body())
                        else
                            callback(
                                NokeUnlockResponse(result = "error",
                                    message = response.errorBody()?.string()?:"",
                                    error_code = response.code(),
                                    request = "unlock",
                                    data = null, api_version = null, token =  null)
                            )
                    }
                })
        }
    }
}