import okhttp3.Interceptor
import okhttp3.Response

class ApiAuthInterceptor(val accessToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
            chain.proceed(chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build())
}
