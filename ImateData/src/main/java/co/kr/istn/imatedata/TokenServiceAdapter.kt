package co.kr.istn.imatedata

import co.kr.istn.utility.*
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import okhttp3.CookieJar
import okhttp3.Credentials
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import java.lang.Exception
import java.net.CookieManager
import java.util.concurrent.TimeUnit

/**
 * 토근 서비스는 V2에서만 사용함
 */
class TokenServiceAdapter(val baseUrl:String, var cookieJar : CookieJar,
                          var userId: String = "", var userPassword: String = "", var sslIngnore : Boolean = false,
                          var connectTimeout : Long = 30, var readTimeOut : Long = 1200, var writeTimeout : Long = 300) {

    //쿼리 서비스
    private val tokenService: TokenService
    //HTTP 클라이언트
    private val tokenServiceHttpClient : OkHttpClient.Builder
    //토근 관리자
    private var tokenManager : ImateTokenManager

    /**
     * 마지막 오류 메시지
     */
    var lastErrorMessage : String
    var scope : CoroutineScope

    //초기화
    init {

        lastErrorMessage = ""
        tokenManager = ImateTokenManager("")

        //https일 경우 테스트를 위해 안전하지 않은 SSL을 사용 한다.
        tokenServiceHttpClient = if(baseUrl.startsWith("https", true) && sslIngnore)
            UnsafeOkHttpClient.getUnsafeOkHttpClient() else OkHttpClient.Builder()

        val authData = getImateAuthData(userId, userPassword)
        val interceptor = ImateAuthenticationInterceptor(authData, tokenManager)

        if(!tokenServiceHttpClient.interceptors().contains(interceptor))
            tokenServiceHttpClient.addInterceptor(interceptor)

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val gsonFactory = GsonConverterFactory.create(gson)

        tokenServiceHttpClient
            .cookieJar(cookieJar)
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeOut, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)

        //쿼리 서비스를 생성 한다.
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(tokenServiceHttpClient.build())
            .addConverterFactory(gsonFactory)
            .build()

        tokenService = retrofit.create(TokenService::class.java)
        scope = CoroutineScope(Job() + Dispatchers.IO)
    }

    /**
     *  OTP 인증을 한다.
     *
     * @param authInfo 인증 정보
     */
    fun otpAuth(authInfo: ImateAuthInfo) : Boolean
    {
        var result = try {
            val response = tokenService.otpAuth(authInfo).execute()

            if (response.isSuccessful)
            {
                lastErrorMessage = "";
                response.body()
            }
            else
            {
                lastErrorMessage = response.errorBody()?.string()?:""
                false;
            }
        }catch (e: Exception) {
            lastErrorMessage = e.message?:""
            false
        }

        return result?:false
    }

    /**
     * Devide의 코든 정보를 등록 한다.
     */
    fun registerId(tokenIdInfo: TokenIdInfo) : ExecuteResult?
    {
        var result = try {
            val response = tokenService.registerId(tokenIdInfo).execute()

            if (response.isSuccessful)
            {
                lastErrorMessage = "";
                response.body()
            }
            else
            {
                lastErrorMessage = response.errorBody()?.string()?:""
                ExecuteResult(false, response.errorBody()?.string()?:"");
            }
        }catch (e: Exception) {
            lastErrorMessage = e.message?:""
            ExecuteResult(false, e.message?:"")
        }

        return result
    }

    /**
     * Devide Id의 승인요청 상태를 반환 한다.
     * @param deviceId 디바이스 ID
     */
    fun registerStatus(deviceId: String) : ExecuteResult?
    {
        var result = try {
            val response = tokenService.registerStatus(deviceId).execute()

            if (response.isSuccessful)
            {
                lastErrorMessage = "";
                response.body()
            }
            else
            {
                lastErrorMessage = response.errorBody()?.string()?:""
                ExecuteResult(false, response.errorBody()?.string()?:"");
            }
        }catch (e: Exception) {
            lastErrorMessage = e.message?:""
            ExecuteResult(false, e.message?:"")
        }

        return result
    }

    /**
     * 기본 JWT토근을 반환 한다.
     * @param deviceId 디바이스 ID
     */
    fun getToken(deviceId: String) : String
    {
        var result = try {
            val response = tokenService.getToken(deviceId).execute()

            if (response.isSuccessful)
            {
                lastErrorMessage = "";
                response.body()
            }
            else
            {
                lastErrorMessage = response.errorBody()?.string()?:""
                ""
            }
        }catch (e: Exception) {
            lastErrorMessage = e.message?:""
            ""
        }

        return result?:""
    }

    /**
     * 기본 JWT토근을 반환 한다.
     */
    fun getDefaultToken() : String
    {
        var result = try {
            val response = tokenService.getDefaultToken().execute()

            if (response.isSuccessful)
            {
                lastErrorMessage = "";
                response.body()
            }
            else
            {
                lastErrorMessage = response.errorBody()?.string()?:""
                ""
            }
        }catch (e: Exception) {
            lastErrorMessage = e.message?:""
           ""
        }

        return result?:""
    }

    /**
     * 토근의 유효기간을 반환 한다.
     */
    fun getTokenExpires() : Double
    {
        var result = try {
            val response = tokenService.getTokenExpires().execute()

            if (response.isSuccessful)
            {
                lastErrorMessage = "";
                response.body()
            }
            else
            {
                lastErrorMessage = response.errorBody()?.string()?:""
                -1.0
            }
        }catch (e: Exception) {
            lastErrorMessage = e.message?:""
            -1.0
        }

        return result?:0.0
    }

}