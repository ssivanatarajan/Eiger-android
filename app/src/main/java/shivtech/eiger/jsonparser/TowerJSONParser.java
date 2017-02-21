package shivtech.eiger.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shivtech.eiger.models.Team;
import shivtech.eiger.models.Tower;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 03-02-2017.
 */

public class TowerJSONParser {
    String json;
    public TowerJSONParser(String json) {
        this.json=json;
    }

    public ArrayList<Tower> parseJSON()
    {
        ArrayList<Tower> towerArrayList=new ArrayList<Tower>();
        try {
            JSONArray jsonArray=new JSONArray(json);
            Log.e("tower json array",jsonArray.toString());
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject towerJSON=jsonArray.getJSONObject(i);
                int towerid=towerJSON.getInt(Constants.towerId);
                String towerName=towerJSON.getString(Constants.towername);
                Tower tower=new Tower();
                tower.setTowerId(towerid);
                tower.setTowerName(towerName);
                towerArrayList.add(tower);

            }
            Log.e("tower array length",towerArrayList.size()+"");
            return towerArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return towerArrayList;
    }

}
