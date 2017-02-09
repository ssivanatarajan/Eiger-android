package shivtech.eiger.jsonparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.crypto.Cipher;

import shivtech.eiger.models.AppSecondaryUser;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 03-02-2017.
 */

public class AppsSecondaryJSONParser {
    String json;
    public AppsSecondaryJSONParser(String json) {
        this.json=json;
    }

    public ArrayList<AppSecondaryUser> parseJSON() {
        ArrayList<AppSecondaryUser> appSecondaryUserArrayList = new ArrayList<AppSecondaryUser>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JSON = jsonArray.getJSONObject(i);
                int id=JSON.getInt(Constants.id);
                int appid = JSON.getInt(Constants.appId);
                int userid = JSON.getInt(Constants.userId);
                AppSecondaryUser appSecondaryUser = new AppSecondaryUser();
                appSecondaryUser.setId(id);
                appSecondaryUser.setAppId(appid);
                appSecondaryUser.setUserId(userid);
                appSecondaryUserArrayList.add(appSecondaryUser);

            }
            return appSecondaryUserArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appSecondaryUserArrayList;
    }


}
