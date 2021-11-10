package co.kr.istn.utility

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 송신 할때 쿠기를 추가 한다.
 */
class AddCookiesInterceptor(val cookieSet : HashSet<String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        //쿠기 저장
        for (cookie in cookieSet) {
            builder.addHeader("Cookie", cookie)
        }

        // Preference에서 cookies를 가져오는 작업을 수행
        //val preferences: Set<String> = SharedPreferenceBase.getSharedPreference(APIPreferences.SHARED_PREFERENCE_NAME_COOKIE, HashSet<String>())

        // Web,Android,iOS 구분을 위해 User-Agent세팅
        //builder.removeHeader("User-Agent").addHeader("User-Agent", "Android")
        return chain.proceed(builder.build())
    }
}