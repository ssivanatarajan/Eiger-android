package shivtech.eiger.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;

import shivtech.eiger.models.App;
import shivtech.eiger.models.AppJSONModel;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 29-01-2017.
 */

public class AppsJSONParser {
    String json;

public AppsJSONParser(String json)
{
    this.json=json;
}
    public ArrayList<AppJSONModel> parseJson()
    {
        ArrayList<AppJSONModel> appList=new ArrayList<AppJSONModel>();

        try {
            JSONArray appsArray=new JSONArray(json);
            Log.i("JSONArray length",appsArray.length()+"");
            for(int i=0;i<appsArray.length();i++)
            {
                JSONObject appJSON= appsArray.getJSONObject(i);
                String appname=appJSON.getString(Constants.appName);
                int appid=appJSON.getInt(Constants.appId);
                int tower=appJSON.getInt(Constants.tower);
                String category=appJSON.getString(Constants.category);
                int team=appJSON.getInt(Constants.team);
                String alias=" ";
                if(!appJSON.isNull(Constants.alias))
                    alias=appJSON.getString(Constants.alias);
                String supportLevel=appJSON.getString(Constants.supportLevel);
                AppJSONModel app=new AppJSONModel(appid,appname,alias,category,tower,team,supportLevel);

                Log.i("app parsed JSon",app+"");
                appList.add(app);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("app parse exp",e.toString());
        }
        return appList;
    }
}