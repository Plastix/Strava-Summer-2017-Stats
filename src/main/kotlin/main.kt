import api.Activity
import api.SegmentEffort
import api.StravaApi


fun main(args: Array<String>) {
    val api = StravaApi.createApiService()

    println("Querying Strava...hang tight!")
    // Synchronously query the Strava api
    // Get list of activities
    val response = api.getActivities(StravaApi.summerEnd, StravaApi.summerStart)
            .execute()
    // Get efforts up Hawk Hill
    val response2 = api.getSegmentEfforts(StravaApi.hawkHillSegmentId, StravaApi.athleteId,
            StravaApi.summerStartString, StravaApi.summerEndString)
            .execute()

    if (response.isSuccessful && response2.isSuccessful) {
        val activities = response.body()
        val efforts = response2.body()
        if (activities != null && efforts != null) {
            println(calculateStats(activities, efforts))
        } else {
            println("Wrong data returned from Strava API!")
        }
    } else {
        println("Error contacting Strava! (${response.message()})")
    }
}

fun calculateStats(activities: List<Activity>, efforts: List<SegmentEffort>): Stats {
    val totals = activities.fold(Stats(),
            { acc, activity ->
                if (activity.type == "Ride")
                    acc + activity
                else
                    acc
            })

    return totals.copy(hawkHills = efforts.size)
}

data class Stats(val metersBiked: Double = 0.0,
                 val numRides: Int = 0,
                 val secondsBiked: Long = 0,
                 val metersClimbed: Double = 0.0,
                 val numCommutes: Int = 0,
                 val kudosRecieved: Int = 0,
                 val numPhotos: Int = 0,
                 val groupRides: Int = 0,
                 val hawkHills: Int = 0) {

    override fun toString() = """
    ${metersBiked.metersToMiles().toInt()} miles biked over $numRides activities.
    ${secondsBiked.secondsToHours()} hours spent in the saddle.
    ${metersClimbed.metersToFeet().toInt()} total feet climbed.
    $numCommutes commutes to/from Strava HQ.
    $kudosRecieved kudos received.
    $numCommutes photos uploaded.
    $groupRides group rides.
    $hawkHills Hawk Hill efforts.
    """
}

operator fun Stats.plus(activity: Activity) = Stats(
        metersBiked + activity.distance,
        numRides + 1,
        secondsBiked + activity.movingTime,
        metersClimbed + activity.elevationGain,
        numCommutes + if (activity.isCommute) 1 else 0,
        kudosRecieved + activity.kudos,
        numPhotos + activity.photoNum,
        groupRides + if (activity.athleteCount > 1) 1 else 0
)

