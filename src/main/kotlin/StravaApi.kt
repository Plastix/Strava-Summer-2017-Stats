import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface StravaApi {

    @retrofit2.http.GET("athlete/activities")
    fun getActivities(@retrofit2.http.Query("before") before: Long,
                      @retrofit2.http.Query("after") after: Long,
                      @retrofit2.http.Query("per_page") perPage: Int = 200): retrofit2.Call<List<Activity>>

    @retrofit2.http.GET("segments/{id}/all_efforts")
    fun getSegmentEfforts(@retrofit2.http.Path("id") id: Int,
                          @retrofit2.http.Query("athlete_id") athleteId: Long,
                          @retrofit2.http.Query("start_date_local") startDate: String,
                          @retrofit2.http.Query("end_date_local") endDate: String,
                          @retrofit2.http.Query("per_page") perPage: Int = 200): retrofit2.Call<List<SegmentEffort>>

    companion object {
        val baseUrl = "https://www.strava.com/api/v3/"
        val hawkHillSegmentId = 229781

        fun createApiService(): StravaApi =
                Retrofit.Builder()
                        .baseUrl(StravaApi.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(OkHttpClient.Builder()
                                .addInterceptor(ApiAuthInterceptor(config[keys.accessToken]))
                                .build())
                        .build()
                        .create(StravaApi::class.java)
    }
}
