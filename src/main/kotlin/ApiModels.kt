import com.google.gson.annotations.SerializedName

data class Activity(val distance: Double, // meters
                    @SerializedName("total_elevation_gain")
                    val elevationGain: Double, // meters
                    @SerializedName("moving_time")
                    val movingTime: Int, // seconds
                    val type: String, // run, ride, swim...
                    @SerializedName("kudos_count")
                    val kudos: Int,
                    @SerializedName("athlete_count")
                    val athleteCount: Int,
                    @SerializedName("total_photo_count")
                    val photoNum: Int,
                    @SerializedName("commute")
                    val isCommute: Boolean
)

class SegmentEffort() // Empty (we just care about the number of segment efforts, not the data)
