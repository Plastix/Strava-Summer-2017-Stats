import java.math.BigDecimal
import java.math.RoundingMode

fun Double.round(places: Int): Double =
        if (places < 0) throw IllegalArgumentException()
        else BigDecimal(this).setScale(places, RoundingMode.HALF_UP).toDouble()

fun Double.metersToFeet() = this * 3.28084

fun Double.metersToMiles() = this * 0.000621371

fun Long.secondsToHours() = this / 3600.0

fun Activity.isRide() = this.type == "Ride"

fun Activity.isVirtualRide() = this.type == "VirtualRide"
