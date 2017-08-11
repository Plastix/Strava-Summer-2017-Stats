package api

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StravaApi {

    @GET("athlete/activities")
    fun getActivities(@Query("before") before: Int,
                      @Query("after") after: Int,
                      @Query("per_page") perPage: Int = 200): Call<List<Activity>>

    @GET("segments/{id}/all_efforts")
    fun getSegmentEfforts(@Path("id") id: Int,
                          @Query("athlete_id") athleteId: Int,
                          @Query("start_date_local") startDate: String,
                          @Query("end_date_local") endDate: String,
                          @Query("per_page") perPage: Int = 200): Call<List<SegmentEffort>>

    companion object {
        val baseUrl = "https://www.strava.com/api/v3/"
        val hawkHillSegmentId = 229781
        val summerStart = 1497225600
        val summerEnd = 1504051200
        val summerStartString = "2017-06-11T17:00:00-07:00"
        val summerEndString = "2017-08-29T17:00:00-07:00"
        val athleteId = 11346071

        fun createApiService(): StravaApi {
            val client = OkHttpClient.Builder()
                    .addInterceptor(ApiAuthInterceptor(config[auth.accessToken]))
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(StravaApi.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            return retrofit.create(StravaApi::class.java)
        }

        object auth : PropertyGroup() {
            val accessToken by stringType
        }

        val config = ConfigurationProperties.fromResource("auth.properties")
    }
}