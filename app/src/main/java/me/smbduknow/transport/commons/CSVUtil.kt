package me.smbduknow.transport.commons

import android.content.Context
import me.smbduknow.transport.model.Route
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object CSVUtil {

    fun readCsv(context: Context): List<Route> {
        val resultList = ArrayList<Route>()
        val assetManager = context.assets

        try {
            val `is` = assetManager.open("routes.txt")
            val reader = BufferedReader(InputStreamReader(`is`))
            var line: String? = reader.readLine()
            while (line != null) {
                val rowData = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val route = Route()
                route.id = rowData[0]
                route.label = rowData[2]
                route.type = rowData[4]
                route.typeLabel = rowData[5]
                resultList.add(route)
                line = reader.readLine()
            }
            Collections.sort(resultList)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return resultList
    }

}
