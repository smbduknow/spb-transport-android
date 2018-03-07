package me.smbduknow.transport.data.assets

import android.content.Context
import me.smbduknow.transport.domain.model.Route
import java.util.*
import javax.inject.Inject


class RoutesProvider @Inject constructor(
        context: Context
) {

    private val cachedRoutes = ArrayList<Route>()

    init {
        context.assets.open("routes.txt").bufferedReader().use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                val rowData = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val route = Route(
                        id = rowData[0],
                        label = rowData[2],
                        type = rowData[4],
                        typeLabel = rowData[5]
                )
                cachedRoutes.add(route)
                line = reader.readLine()
            }
        }
        cachedRoutes.sort()
    }

    fun getRoutes(): List<Route> = cachedRoutes.toList()

}
