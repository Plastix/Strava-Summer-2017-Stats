package api

import config
import keys
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StravaApi {

    @GET("athlete/activities")
    fun getActivities(@Query("before") before: Long,
                      @Query("after") after: Long,
                      @Query("per_page") perPage: Int = 200): Call<List<Activity>>

    @GET("segments/{id}/all_efforts")
    fun getSegmentEfforts(@Path("id") id: Int,
                          @Query("athlete_id") athleteId: Long,
                          @Query("start_date_local") startDate: String,
                          @Query("end_date_local") endDate: String,
                          @Query("per_page") perPage: Int = 200): Call<List<SegmentEffort>>

    companion object {
        val baseUrl = "https://www.strava.com/api/v3/"
        val hawkHillSegmentId = 229781

        fun createApiService(): StravaApi {
            val client = OkHttpClient.Builder()
                    .addInterceptor(ApiAuthInterceptor(config[keys.accessToken]))
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(StravaApi.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            return retrofit.create(StravaApi::class.java)
        }
    }
}