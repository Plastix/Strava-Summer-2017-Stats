import com.natpryce.konfig.*

object keys : PropertyGroup() {
    val accessToken by stringType
    val athleteId by longType
    val summerStart by longType
    val summerEnd by longType
    val summerStartIso by stringType
    val summerEndIso by stringType
}

val config = ConfigurationProperties.fromResource("config.properties")

fun main(args: Array<String>) {
    val api = StravaApi.createApiService()

    println("Querying Strava...hang tight!")

    // Synchronously query the Strava api

    // Get list of activities
    val activityResponse = api.getActivities(config[keys.summerEnd], config[keys.summerStart])
            .execute()
    // Get efforts up Hawk Hill
    val effortResponse = api.getSegmentEfforts(StravaApi.hawkHillSegmentId, config[keys.athleteId],
            config[keys.summerStartIso], config[keys.summerEndIso])
            .execute()

    activityResponse.body()?.let {
        activities ->
        effortResponse.body()?.let {
            efforts ->
            println(calculateStats(activities, efforts))
        }
    }
}

fun calculateStats(activities: List<Activity>, efforts: List<SegmentEffort>): Stats =
        // Calculate total
        activities.fold(Stats()) { stats, activity -> stats + activity }
                // Add Hawk Hill attempts at the end (separate API call)
                .copy(hawkHills = efforts.size)

data class Stats(val metersBiked: Double = 0.0,
                 val numRides: Int = 0,
                 val secondsBiked: Long = 0,
                 val metersClimbed: Double = 0.0,
                 val numCommutes: Int = 0,
                 val kudosReceived: Int = 0,
                 val numPhotos: Int = 0,
                 val groupRides: Int = 0,
                 val hawkHills: Int = 0) {

    override fun toString() = """
    ${metersBiked.metersToMiles().round(1)} miles biked over $numRides activities.
    ${secondsBiked.secondsToHours().round(1)} hours spent in the saddle.
    ${metersClimbed.metersToFeet().toInt()} feet climbed.
    $numCommutes commutes to/from Strava HQ.
    $kudosReceived kudos received.
    $numCommutes photos uploaded.
    $groupRides group rides.
    $hawkHills Hawk Hill efforts.
    """
}

operator fun Stats.plus(activity: Activity) =
        // Count kudos and photos for ALL activity types
        copy(kudosReceived = kudosReceived + activity.kudos,
                numPhotos = numPhotos + activity.photoNum)
                .run {
                    // Calculate totals specific to bike rides
                    if (activity.isRide())
                        this.copy(metersBiked = metersBiked + activity.distance,
                                numRides = numRides + 1,
                                secondsBiked = secondsBiked + activity.movingTime,
                                metersClimbed = metersClimbed + activity.elevationGain,
                                numCommutes = numCommutes + if (activity.isCommute) 1 else 0,
                                // Don't include commutes as group rides
                                // (sometimes you get matched with random people)
                                groupRides = groupRides + if (!activity.isCommute && activity.athleteCount > 1) 1 else 0
                        )
                    else this
                }
