package me.smbduknow.transport.commons

import android.content.Context
import me.smbduknow.transport.model.Route
import java.util.*

object CSVUtil {

    fun readCsv(context: Context): List<Route> {
        val resultList = ArrayList<Route>()
        val assetManager = context.assets

        assetManager.open("routes.txt").bufferedReader().use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                val rowData = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val route = Route(
                        id = rowData[0],
                        label = rowData[2],
                        type = rowData[4],
                        typeLabel = rowData[5]
                )
                resultList.add(route)
                line = reader.readLine()
            }
        }

        Collections.sort(resultList)
        return resultList
    }

}
