package co.kr.istn.utility

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 수신 쿠기 처리 Interceptor
 */
class ReceivedCookiesInterceptor(val cookieSet : HashSet<String>) : Interceptor {
    /**
     *  수신한 쿠기를 보관 한다.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            for (header in originalResponse.headers("Set-Cookie")) {
                cookieSet.add(header)
            }

            // Preference에 cookies를 넣어주는 작업을 수행
            //SharedPreferenceBase.putSharedPreference(APIPreferences.SHARED_PREFERENCE_NAME_COOKIE, cookies)
        }

        return originalResponse
    }
}