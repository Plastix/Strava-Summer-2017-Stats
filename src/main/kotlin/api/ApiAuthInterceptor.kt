package api

import okhttp3.Interceptor
import okhttp3.Response

class ApiAuthInterceptor(val accessToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

        return chain.proceed(request)
    }
}