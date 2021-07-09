package app.findp.data

import app.findp.entities.utils.ElementId
import app.findp.entities.utils.Location
import java.io.Serializable
import java.text.DecimalFormat

class ParkingData(var name: String, var location: Location, var elementId: ElementId, var isTaken: Boolean) : Serializable {
    var distance = 0.0
    fun calculateDistance(userLocation: Location) {
        val R = 6371.0 // Radius of the earth in km
        val dLat = deg2rad(userLocation.lat!!.minus(location.lat!!)) // deg2rad below
        val dLon = deg2rad(userLocation.lng!!.minus(location.lng!!))
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(location.lat!!)) * Math.cos(deg2rad(userLocation.lat!!)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val d = R * c // Distance in km
        val df = DecimalFormat("#.##")
        distance = df.format(d).toDouble()
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }
}