package me.smbduknow.transport.domain.model

/**
* Describes single vehicle object
* */
data class Vehicle(
        val id: String,
        val type: String,
        val label: String,
        val latitude: Double,
        val longitude: Double,
        val bearing: Float,
        val routeId: String
) {
    companion object {
        const val TYPE_BUS = "bus"
        const val TYPE_TRAM = "tram"
        const val TYPE_TROLLEY = "trolley"
    }
}

/**
 * Describes route meta of vehicle
 * */
data class Route(
        val id: String,
        val label: String = "",
        val type: String = "",
        val typeLabel: String = ""
) : Comparable<Route> {
    override fun compareTo(other: Route) = id.compareTo(other.id)
}

/**
 * Geo-coordinates
 * @property lat latitude
 * @property lon longitude
 * */
data class Coordinates(
        val lat: Double,
        val lon: Double
)

/**
 * Bounds of rectangle area where vehicles will be requested
 * @property sw southwest coordinates of scope rect
 * @property ne northeast coordinates of scope rect
 * */
data class MapScope(
        val sw: Coordinates,
        val ne: Coordinates
)