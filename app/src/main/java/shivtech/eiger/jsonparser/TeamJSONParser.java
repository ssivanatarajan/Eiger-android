package shivtech.eiger.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shivtech.eiger.models.Team;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 03-02-2017.
 */

public class TeamJSONParser {
    String json;
    public TeamJSONParser(String json) {
        this.json=json;
    }

    public ArrayList<Team> parseJSON()
    {
        ArrayList<Team> teamArrayList=new ArrayList<Team>();

        try {
            JSONArray jsonArray=new JSONArray(json);
            Log.e("team josn",jsonArray.toString() +" "+jsonArray.length());
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject teamJSON=jsonArray.getJSONObject(i);
                Log.e("team josn",teamJSON.toString());


                Team team=new Team();
                team.setTeamID(teamJSON.getInt(Constants.teamID));
                team.setTeamName(teamJSON.getString(Constants.teamname));
                team.setTowerID(teamJSON.getInt(Constants.towerId));
                teamArrayList.add(team);


            }

        } catch (JSONException e) {
            Log.e("team json exp",e.toString());
            e.printStackTrace();
        }
        Log.e("Team arraylist data",teamArrayList.get(0).getTeamID()+" "+teamArrayList.get(0).getTeamName()+teamArrayList.get(1).getTeamName()+" "+teamArrayList.get(2).getTeamName());
        return teamArrayList;
    }
}
