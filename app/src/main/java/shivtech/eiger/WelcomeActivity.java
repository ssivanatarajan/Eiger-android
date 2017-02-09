package shivtech.eiger;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.jsonparser.AppsJSONParser;
import shivtech.eiger.jsonparser.AppsPrimaryJSONParser;
import shivtech.eiger.jsonparser.AppsSecondaryJSONParser;
import shivtech.eiger.jsonparser.TeamJSONParser;
import shivtech.eiger.jsonparser.TowerJSONParser;
import shivtech.eiger.jsonparser.UserJSONParser;
import shivtech.eiger.models.App;
import shivtech.eiger.models.AppJSONModel;
import shivtech.eiger.models.AppPrimaryUser;
import shivtech.eiger.models.AppSecondaryUser;
import shivtech.eiger.models.Team;
import shivtech.eiger.models.Tower;
import shivtech.eiger.models.User;
import shivtech.eiger.utils.Constants;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeActivity extends AppCompatActivity {
    private View mContentView;
    Button submit, reset;
    SharedPreferences.Editor sp_editor;
    private Context mContext;
    RequestQueue requestQueue;
    private static  final String getTablesURL="https://eigerapp.herokuapp.com/api/downloadTables";
    private AppListFragment.OnFragmentInteractionListener mListener;
    ProgressDialog progressDialog;
    String reqTypes[]={"Towers","Users","Teams","Apps","AppPrimaryUsers","AppSecondaryUsers"};
    static  int typeCounter;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        sharedPreferences=getApplicationContext().getSharedPreferences(Constants.shared_prefs,Context.MODE_PRIVATE);
        //  mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);*/
                submit();
            }
        });

        boolean firstload=sharedPreferences.getBoolean(Constants.sp_firstLoad,false);
        sp_editor=sharedPreferences.edit();



        if(firstload) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);

        }


        //
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    }

    private void submit()
    {
        boolean firstload=sharedPreferences.getBoolean(Constants.sp_firstLoad,false);
        sp_editor=sharedPreferences.edit();

        if(firstload) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);

        }
        else
        {
            if(!checkInternetConnection()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage("No Internet Connection, plesae check your connectivity")
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .setPositiveButton("Ok", null);

                AlertDialog dialog = builder.show();
            }
            else {
                progressDialog = new ProgressDialog(this);
                //progressDialog.setMessage("Loading...");
                requestQueue = Volley.newRequestQueue(this);
                sendRequest(reqTypes[typeCounter]);
           /* sendRequest("Users");
            sendRequest("Teams");
            sendRequest("Apps");
            sendRequest("AppPrimaryUsers");
            sendRequest("AppSecondaryUsers");*/

                sp_editor.putBoolean(Constants.sp_firstLoad, true);
                sp_editor.commit();

                         }
        }



    }
    private boolean checkInternetConnection() {

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

    private void sendRequest(final String type) {
        if (typeCounter < reqTypes.length) {
            Log.e("reqtypes", reqTypes[typeCounter] + " " + typeCounter + " " + type);
            String URL = getTablesURL + "/" + type;
            progressDialog.setMessage("Loading " + type + " ...");
            progressDialog.show();
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

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            sp_editor.putBoolean(Constants.sp_firstLoad, false);
                            sp_editor.commit();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            Log.e("send request", error.toString());


                        }
                    });


            requestQueue.add(stringRequest);
        }
        else {

            boolean firstload = sharedPreferences.getBoolean(Constants.sp_firstLoad, false);
            if (firstload) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        }

    }

    private void loadTables(String response, String type) {
        DBHandler dbHandler = new DBHandler(getApplicationContext());
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
