package co.kr.istn.utility

import co.kr.istn.imatedata.ImateApiVersion
import com.google.gson.GsonBuilder
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.Response
import java.math.BigDecimal
import java.net.CookieManager
import java.util.*
import javax.print.DocFlavor
import kotlin.collections.HashSet
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.defaultType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

/**
 * 쿠기 Set
 */
var logisticCookieSet = HashSet<String>()

/**
 * API 버전 정보
 */
var imateApiVersion = ImateApiVersion.V2;

/**
 * imate CookieJar
 */
var imateCookieJar : CookieJar = JavaNetCookieJar(CookieManager())

/**
 * 문자를 지정한 형식으로 변경 한다
 *
 * @param value 변경할 값
 */
inline fun <reified T> stringConvert(value : String?): T? {
    return when (T::class) {
        Int::class -> value?.toInt() as? T
        Long::class -> value?.toLong() as? T
        Float::class -> value?.toFloat() as? T
        Double::class -> value?.toDouble() as? T
        Short::class -> value?.toShort() as? T
        Byte::class -> value?.toByte() as? T
        BigDecimal::class->value?.toBigDecimalOrNull() as? T
        String::class -> value as? T
        Any::class -> value as? T
        else -> throw IllegalStateException("${T::class} Unknown Generic Type")
    }
}

/**
 * 문자를 지정한 형식으로 변경 한다
 *
 * @param value 변경할 값
 * @param type 변경할 TYPE
 */
fun stringConvert(value : String?, type : KType): Any? {
    return when (type.jvmErasure) {
        Int::class -> value?.toInt()
        Long::class -> value?.toLong()
        Float::class -> value?.toFloat()
        Double::class -> value?.toDouble()
        Short::class -> value?.toShort()
        Byte::class -> value?.toByte()
        BigDecimal::class->value?.toBigDecimalOrNull()
        String::class -> value
        Any::class -> value
        else -> throw IllegalStateException("Unknown Generic Type")
    }
}

/**
 * 무작위 문자열을 반환 한다.
 */
fun getRandomString(length: Int) : String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
            .map { charset.random() }
            .joinToString(separator = "")
}

/**
 * RFC Json 파라미터를 반환 한다.
 */
fun getRFCParameter(dataObj: Any) : String{
    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
    var dataJson = gson.toJson(dataObj)
                        .replace("'", "#0x27")
                        .replace("`", "#0x60")
                        .replace("\"", "'")

    return "\"json@${dataJson}\"";
}

/**
 * 인증 정보를 만든다.
 */
fun getImateAuthData(userId : String, passwd: String) : String
{
    return Base64.getEncoder().encodeToString("${userId}:${passwd}".toByteArray())
}

/**
 * 토근 관리자
 */
data class ImateTokenManager(var jwtToken: String)

/**
 * 기본 인증 인터셉터
 *
 * @param token 인증토근
 */
class BasicAuthenticationInterceptor(var token: String) : Interceptor {
    //인증 토근
    private var authToken : String

    init {
        this.authToken = token
    }

    /**
     * 인증정보 추가
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request();
        var builder = original.newBuilder().header("Authorization", authToken)

        return  chain.proceed(builder.build())
    }
}

/**
 * 기본 인증 인터셉터
 *
 * @param authData api 인증 데이터
 */
class ImateAuthenticationInterceptor(var authData: String, var tokenManager : ImateTokenManager) : Interceptor {
    //인증 토근
    private var apiAuthData : String

    private  var apiJwtTokenManager :ImateTokenManager

    init {
        this.apiAuthData = authData
        this.apiJwtTokenManager = tokenManager;
    }

    /**
     * 인증정보 추가
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request();
        var builder = original.newBuilder().header("X-Imate-api-auth", apiAuthData)
        if(!this.apiJwtTokenManager.jwtToken.isNullOrEmpty())
            builder.addHeader("Authorization", "Bearer ${this.apiJwtTokenManager.jwtToken}");

        return  chain.proceed(builder.build())
    }
}