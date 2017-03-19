package shivtech.eiger.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shivtech.eiger.models.AppSecondaryUser;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 03-02-2017.
 */

public class AppsSecondaryJSONParser {
    String json;

    public AppsSecondaryJSONParser(String json) {
        this.json = json;
    }

    public ArrayList<AppSecondaryUser> parseJSON() {
        ArrayList<AppSecondaryUser> appSecondaryUserArrayList = new ArrayList<AppSecondaryUser>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            Log.e("Appsecondary json array", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject JSON = jsonArray.getJSONObject(i);

                    int appid = JSON.getInt(Constants.appId);
                    int empid = JSON.getInt(Constants.empId);
                    String lastModified = JSON.getString(Constants.lastModified);
                    AppSecondaryUser appSecondaryUser = new AppSecondaryUser();
                    appSecondaryUser.setLastModified(lastModified);
                    appSecondaryUser.setAppId(appid);
                    appSecondaryUser.setEmpId(empid);
                    appSecondaryUserArrayList.add(appSecondaryUser);
                } catch (JSONException exp) {
                    Log.e("appsec exp", i + " " + exp.toString());
                }
            }
            return appSecondaryUserArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appSecondaryUserArrayList;
    }


}
