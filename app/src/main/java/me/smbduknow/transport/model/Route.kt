package me.smbduknow.transport.model

data class Route(
        var id: String? = null,
        var label: String? = null,
        var type: String? = null,
        var typeLabel: String? = null
) : Comparable<Route> {

    override fun compareTo(other: Route): Int {
        return id!!.compareTo(other.id!!)
    }
}
