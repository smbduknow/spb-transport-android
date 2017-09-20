package me.smbduknow.transport.model


data class Route(
        val id: String,
        val label: String = "",
        val type: String = "",
        val typeLabel: String = ""
) : Comparable<Route> {
    override fun compareTo(other: Route) = id.compareTo(other.id)
}

data class Vehicle(
        val id: String,
        val label: String,
        val type: String,
        val latitude: Double,
        val longitude: Double,
        val bearing: Float
)