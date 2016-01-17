package me.smbduknow.transport;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    public static List<Route> readCsv(Context context) {
        List<Route> resultList = new ArrayList<Route>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream is = assetManager.open("routes.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");
                    Route route = new Route();
                    route.id = rowData[0];
                    route.label = rowData[2];
                    route.type = rowData[4];
                    route.typeLabel = rowData[5];
                    resultList.add(route);
                }
        } catch (IOException e) {
            e.printStackTrace();
//        } finally {
//            try {
//                is.close();
//            }
//            catch (IOException e) {
//                // handle exception
//            }
        }
        return resultList;
    }

}
