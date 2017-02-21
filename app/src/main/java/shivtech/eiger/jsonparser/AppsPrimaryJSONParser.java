package shivtech.eiger.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shivtech.eiger.models.AppPrimaryUser;
import shivtech.eiger.models.Tower;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 03-02-2017.
 */

public class AppsPrimaryJSONParser {
String json;
    public AppsPrimaryJSONParser(String json) {
    this.json=json;
    }

    public ArrayList<AppPrimaryUser> parseJSON() {
        ArrayList<AppPrimaryUser> appPrimaryUserArrayList = new ArrayList<AppPrimaryUser>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            Log.e("Appprimary json array",jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject JSON = jsonArray.getJSONObject(i);
                    int appid = JSON.getInt(Constants.appId);
                    int empid = JSON.getInt(Constants.empId);

                    AppPrimaryUser appPrimaryUser = new AppPrimaryUser();
                    appPrimaryUser.setAppId(appid);
                    appPrimaryUser.setEmpId(empid);

                    appPrimaryUserArrayList.add(appPrimaryUser);
                }
                catch (JSONException e){
                    Log.e("app primary exp",e.toString());
                }
            }
            return appPrimaryUserArrayList;
        } catch (JSONException e) {
            Log.e("app primary exp",e.toString());
            e.printStackTrace();
        }
    return appPrimaryUserArrayList;
    }

}
