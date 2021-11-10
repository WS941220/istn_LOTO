package co.kr.istn.imatedata

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Token Service API
 */
interface TokenService {
    /**
     * 토근 발급을 위한 Device 등록
     *
     * @param tokenIdInfo 토근 발급 정보
     */
    @POST( value = "api/TokenService/otpauth")
    fun otpAuth(@Body() authInfo: ImateAuthInfo) : Call<Boolean>
    /**
     * 토근 발급을 위한 Device 등록
     *
     * @param tokenIdInfo 토근 발급 정보
     */
    @POST( value = "api/TokenService/RegisterId")
    fun registerId(@Body() tokenIdInfo: TokenIdInfo) : Call<ExecuteResult>

    /**
     * Device 등록 상태
     *
     * @param id Device의 ID
     */
    @GET( value = "api/TokenService/RegisterStatus/{id}")
    fun registerStatus(@Path(value = "id") id: String) : Call<ExecuteResult>

    /**
     * Device의 토근 정보
     *
     * @param id Device Id
     */
    @GET( value = "api/TokenService/GetToken/{id}")
    fun getToken(@Path(value = "id") id: String) : Call<String>

    /**
     * 기본 토근 정보
     */
    @GET( value = "api/TokenService/GetDefaultToken")
    fun getDefaultToken() : Call<String>

    /**
     * 토근 유효 기간간
    */
    @GET( value = "api/TokenService/GetTokenExpires")
    fun getTokenExpires() : Call<Double?>
}