package shivtech.eiger.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;

import shivtech.eiger.EditProfile;
import shivtech.eiger.LoginActivity;
import shivtech.eiger.MainActivity;
import shivtech.eiger.Signup;
import shivtech.eiger.db.DBHandler;
import shivtech.eiger.jsonparser.AppsJSONParser;
import shivtech.eiger.jsonparser.AppsPrimaryJSONParser;
import shivtech.eiger.jsonparser.AppsSecondaryJSONParser;
import shivtech.eiger.jsonparser.TeamJSONParser;
import shivtech.eiger.jsonparser.TowerJSONParser;
import shivtech.eiger.jsonparser.UserJSONParser;
import shivtech.eiger.models.AppJSONModel;
import shivtech.eiger.models.AppPrimaryUser;
import shivtech.eiger.models.AppSecondaryUser;
import shivtech.eiger.models.Team;
import shivtech.eiger.models.Tower;
import shivtech.eiger.models.User;

/**
 * Created by Sivanatarajan on 14-02-2017.
 */

public class Utils {

    private static final String getTablesURL = "https://eigerapp.herokuapp.com/api/downloadTables";
    static String reqTypes[] = {"Towers", "Users", "Teams", "Apps", "AppPrimaryUsers", "AppSecondaryUsers"};
    static int typeCounter;
    static SharedPreferences.Editor sp_editor;
    static RequestQueue requestQueue;
    static ProgressDialog progressDialog;
    static SharedPreferences sharedPreferences;
    Context mContext;

    public Utils(Activity activity) {
        this.mContext = activity;
    }

    public static boolean checkInternetConnection() {

        String command = "ping -c 1 google.com";
        try {
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void downloadDumps() {

        sharedPreferences = mContext.getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(mContext);
        progressDialog = new ProgressDialog(mContext);
        sendRequest(reqTypes[typeCounter]);
        sp_editor = sharedPreferences.edit();
        sp_editor.putBoolean(Constants.sp_firstLoad, true);
        sp_editor.commit();
    }

    private void sendRequest(final String type) {

        Log.e("reqtypes", reqTypes[typeCounter] + " " + typeCounter + " " + type);
        String URL = getTablesURL + "/" + type;
        progressDialog.setMessage("Loading " + type + " ...");
//        progressDialog.show();
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response", response);
                        progressDialog.dismiss();
                        loadTables(response, type);
                        typeCounter++;
                        if (typeCounter < reqTypes.length)
                            sendRequest(reqTypes[typeCounter]);
                        else {

                            boolean firstload = sharedPreferences.getBoolean(Constants.sp_firstLoad, false);
                            if (firstload) {
                                if (mContext instanceof LoginActivity) {
                                    Intent mainIntent = new Intent(mContext, MainActivity.class);
                                    mContext.startActivity(mainIntent);
                                    ((Activity) mContext).finish();
                                }
                                if (mContext instanceof Signup) {
                                    Intent profile_intent = new Intent(mContext, EditProfile.class);
                                    mContext.startActivity(profile_intent);
                                    ((Activity) mContext).finish();
                                }
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sp_editor.putBoolean(Constants.sp_firstLoad, false);
                        sp_editor.commit();
                        progressDialog.dismiss();
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
                        Log.e("send request", error.toString());


                    }
                });


        requestQueue.add(stringRequest);


    }

    private void loadTables(String response, String type) {
        DBHandler dbHandler = new DBHandler(mContext);
        ArrayList<AppJSONModel> appsJSON;

        ArrayList<User> users;
        ArrayList<Team> teams;
        ArrayList<Tower> towers;
        ArrayList<AppPrimaryUser> appPrimaryUsers;
        ArrayList<AppSecondaryUser> appSecondaryUsers;
        switch (type) {
            case "Apps":
                AppsJSONParser appsJSONParser = new AppsJSONParser(response);
                appsJSON = appsJSONParser.parseJson();

                dbHandler.addApps(appsJSON);
                Log.e("Apps", "adding apps " + appsJSON.size());

                break;
            case "Users":
                UserJSONParser userJSONParser = new UserJSONParser(response);
                users = userJSONParser.parseUserJSON();
                Log.e("Users", "adding users " + users.size());
                dbHandler.addUsers(users);
                break;
            case "Teams":
                TeamJSONParser teamJSONParser = new TeamJSONParser(response);
                teams = teamJSONParser.parseJSON();
                Log.e("Teams", "adding teams " + teams.size());
                dbHandler.addTeams(teams);
                break;
            case "Towers":
                TowerJSONParser towerJSONParser = new TowerJSONParser(response);
                Log.e("towers json", response);
                towers = towerJSONParser.parseJSON();
                Log.e("Towers", "adding towers " + towers.size());
                dbHandler.addTowers(towers);
                break;
            case "AppPrimaryUsers":
                AppsPrimaryJSONParser appsPrimaryJSONParser = new AppsPrimaryJSONParser(response);
                appPrimaryUsers = appsPrimaryJSONParser.parseJSON();
                Log.e("primaryuser", "adding primaryuser " + appPrimaryUsers.size());
                dbHandler.appPrimaryUserMapping(appPrimaryUsers);
                break;
            case "AppSecondaryUsers":
                AppsSecondaryJSONParser appsSecondaryJSONParser = new AppsSecondaryJSONParser(response);
                appSecondaryUsers = appsSecondaryJSONParser.parseJSON();
                Log.e("Secondaryusers", "adding secondaryusers " + appSecondaryUsers.size());
                dbHandler.appSecondaryUserMapping(appSecondaryUsers);
                break;
            default:
                Log.i("adding to tables", "default case");
                break;
        }

    }
}
