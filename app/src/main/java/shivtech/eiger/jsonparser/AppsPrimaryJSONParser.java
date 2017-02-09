package shivtech.eiger.jsonparser;

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
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JSON = jsonArray.getJSONObject(i);
                int appid = JSON.getInt(Constants.appId);
                int userid = JSON.getInt(Constants.userId);
                AppPrimaryUser appPrimaryUser = new AppPrimaryUser();
                appPrimaryUser.setAppId(appid);
                appPrimaryUser.setUserId(userid);
                appPrimaryUserArrayList.add(appPrimaryUser);

            }
            return appPrimaryUserArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return appPrimaryUserArrayList;
    }

}
