package shivtech.eiger.datasync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 04-03-2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    static int tabledwnldCounter, tableuploadCounter, tableDelCounter;
    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    Context mContext;
    ContentResolver mContentResolver;
    String currentUser;
    String tables[] = new String[]{Constants.userTable, Constants.appTable, Constants.teamTable, Constants.towerTable, Constants.appPrimaryUserTable, Constants.appSecondaryUserTable};

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        Log.e("Sync Adapter", "constructor");
        mContext = context;
        mContentResolver = context.getContentResolver();
        dbHandler = new DBHandler(context);


    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext = context;


    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.e("Sync", "onperformsync");
        Cursor cursor;
        sharedPreferences = getContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        int empid = sharedPreferences.getInt(Constants.sp_cur_user_empId, 0);
        currentUser = String.valueOf(empid);

        checkforDeletion(Constants.userTable);
        checkforDeletion(Constants.appTable);
        checkforDeletion(Constants.teamTable);
        checkforDeletion(Constants.towerTable);

        downloadData(Constants.userTable);
        downloadData(Constants.appTable);
        downloadData(Constants.teamTable);
        downloadData(Constants.towerTable);
        downloadData(Constants.appPrimaryUserTable);
        downloadData(Constants.appSecondaryUserTable);


        cursor = dbHandler.getDirtyTableValues(Constants.appTable);
        uploadData(cursor, Constants.appTable);
        cursor = dbHandler.getDirtyTableValues(Constants.appPrimaryUserTable);
        uploadData(cursor, Constants.appPrimaryUserTable);
        cursor = dbHandler.getDirtyTableValues(Constants.appSecondaryUserTable);
        uploadData(cursor, Constants.appSecondaryUserTable);
        cursor = dbHandler.getDirtyTableValues(Constants.teamTable);
        uploadData(cursor, Constants.teamTable);
        cursor = dbHandler.getDirtyTableValues(Constants.towerTable);
        uploadData(cursor, Constants.towerTable);

    }

    private void downloadData(final String table) {
        String lastModified = dbHandler.getLastModifiedDate(table);
        Log.e("download data", table);
        String dbSyncDownloadURL = "https://eigerapp.herokuapp.com/api/dbSync?table=" + table + "&lastModified=" + lastModified + "&currentUser=" + currentUser;
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest downloadRequest = new StringRequest(dbSyncDownloadURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("dbsyncdownlod " + table, response);
                        writeDatatoDB(table, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("dbsyncdownlod " + table, error.toString());
            }
        });

        downloadRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(downloadRequest);
    }

    private void checkforDeletion(final String table) {

        Log.e("check for deletion", table);
        String checkforDeletionURl = "https://eigerapp.herokuapp.com/api/checkForDeletion?tableName=" + table;
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest checkDeletionRequest = new StringRequest(checkforDeletionURl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("checkfordeletion", response);
                        getIDsForDeletion(table, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("checkfordeletion", error.toString());
            }
        });

        checkDeletionRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(checkDeletionRequest);

    }

    private void getIDsForDeletion(String table, String response) {
        Log.e("getids for deletions", table + " " + response);
        ArrayList<Integer> keysInServer;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (table.equals(Constants.appTable)) {
                keysInServer = new ArrayList<Integer>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    keysInServer.add(jsonObject.getInt(Constants.appId));
                }
                dbHandler.deleteAppIDs(keysInServer);
            } else if (table.equals(Constants.userTable)) {
                keysInServer = new ArrayList<Integer>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int Sempid = jsonObject.getInt(Constants.empId);
                    Log.e("db sync empid", Sempid + "");
                    keysInServer.add(Sempid);
                }
                dbHandler.deleteEmpIDs(keysInServer);
            } else if (table.equals(Constants.teamTable)) {
                keysInServer = new ArrayList<Integer>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    keysInServer.add(jsonObject.getInt(Constants.teamID));
                }
                dbHandler.deleteTeamIDs(keysInServer);
            } else if (table.equals(Constants.towerTable)) {
                keysInServer = new ArrayList<Integer>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    keysInServer.add(jsonObject.getInt(Constants.towerId));
                }
                dbHandler.deleteTowerIDs(keysInServer);
            }
            /*tableDelCounter++;
            Log.e("tabledelcounter",tableDelCounter+"");
            if(tableDelCounter<tables.length-2)
                checkforDeletion(tables[tableDelCounter]);
            else
                downloadData(Constants.userTable);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeDatatoDB(String table, String response) {
        DBHandler dbHandler = new DBHandler(mContext);
        ArrayList<AppJSONModel> appsJSON;

        ArrayList<User> users;
        ArrayList<Team> teams;
        ArrayList<Tower> towers;
        ArrayList<AppPrimaryUser> appPrimaryUsers;
        ArrayList<AppSecondaryUser> appSecondaryUsers;

        if (table.equals(Constants.appTable)) {
            AppsJSONParser appsJSONParser = new AppsJSONParser(response);
            appsJSON = appsJSONParser.parseJson();
            Log.e("Apps", "adding apps " + appsJSON.size());
            dbHandler.addOrUpdateApps(appsJSON);
        } else if (table.equals(Constants.userTable)) {
            UserJSONParser userJSONParser = new UserJSONParser(response);
            users = userJSONParser.parseUserJSON();
            Log.e("Users", "adding users " + users.size());
            dbHandler.addOrUpdateUsers(users);
        } else if (table.equals(Constants.teamTable)) {
            TeamJSONParser teamJSONParser = new TeamJSONParser(response);
            teams = teamJSONParser.parseJSON();
            Log.e("Teams", "adding teams " + teams.size());
            dbHandler.addOrUpdateTeams(teams);
        } else if (table.equals(Constants.towerTable)) {
            TowerJSONParser towerJSONParser = new TowerJSONParser(response);
            Log.e("towers json", response);
            towers = towerJSONParser.parseJSON();
            Log.e("Towers", "adding towers " + towers.size());
            dbHandler.addOrUpdateTower(towers);
        } else if (table.equals(Constants.appPrimaryUserTable)) {
            AppsPrimaryJSONParser appsPrimaryJSONParser = new AppsPrimaryJSONParser(response);
            appPrimaryUsers = appsPrimaryJSONParser.parseJSON();
            Log.e("primaryuser", "adding primaryuser " + appPrimaryUsers.size());
            dbHandler.addOrUpdateAppPrimarymapping(appPrimaryUsers);
        } else if (table.equals(Constants.appSecondaryUserTable)) {
            AppsSecondaryJSONParser appsSecondaryJSONParser = new AppsSecondaryJSONParser(response);
            appSecondaryUsers = appsSecondaryJSONParser.parseJSON();
            Log.e("Secondaryusers", "adding secondaryusers " + appSecondaryUsers.size());
            dbHandler.addOrUpdateAppSecondaryUsermapping(appSecondaryUsers);
        }
     /*   tabledwnldCounter++;
        if(tabledwnldCounter<tables.length)
            downloadData(tables[tabledwnldCounter]);
        else
        {
            Cursor cursor=dbHandler.getDirtyTableValues(Constants.userTable);
            uploadData(cursor,Constants.userTable);

        }*/
    }


    private void uploadData(Cursor cursor, final String type) {
        Log.e("upload data", type);
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String dbSyncUploadURL = "https://eigerapp.herokuapp.com/api/dbSync/";
        JSONObject paramsJson = new JSONObject();
        Log.e("cursor count", "" + cursor.getCount());
        if (cursor.getCount() > 0) {

            JSONArray paramsArray = new JSONArray();
            String[] columns = cursor.getColumnNames();
            cursor.moveToFirst();
            do {
                JSONObject param = new JSONObject();
                for (int i = 0; i < columns.length; i++) {
                    String col = columns[i];
                    if (col.equals(Constants.empId) || col.equals(Constants.tower) || col.equals(Constants.isupdate)
                            || col.equals(Constants.appId) || col.equals(Constants.team) || col.equals(Constants.towerId)
                            || col.equals(Constants.teamID) || col.equals(Constants.manager)) {
                        try {
                            param.put(col, cursor.getInt(cursor.getColumnIndex(col)));
                        } catch (JSONException e) {
                            Log.e("dirtytbljsonexp", e.toString() + " " + col);
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            param.put(col, cursor.getString(cursor.getColumnIndex(col)));
                        } catch (JSONException e) {
                            Log.e("dirtytbljsonexp", e.toString() + " " + col);
                            e.printStackTrace();
                        }
                    }
                }
                paramsArray.put(param);
            } while (cursor.moveToNext());
            try {
                paramsJson.put("Values", paramsArray);
                paramsJson.put("table", type);
                Log.e("Json obj", paramsJson.toString());
            } catch (JSONException e) {
                Log.e("exp", e.toString());
                e.printStackTrace();
            }
            final String requestBody = paramsJson.toString();
            Log.e("request body", requestBody);
            final JsonObjectRequest upload_tableData = new JsonObjectRequest(Request.Method.POST,
                    dbSyncUploadURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("dbsync response", response.toString());
                    dbHandler.setDirtytoDefault(type);
                   /* tableuploadCounter++;
                    if(tableuploadCounter<tables.length) {
                        Cursor uploadcursor = dbHandler.getDirtyTableValues(tables[tableuploadCounter]);
                        uploadData(uploadcursor, tables[tableuploadCounter]);
                    }*/


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("db sync err", error.toString());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }


                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }


            };
            upload_tableData.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(upload_tableData);
        }
    }
}
