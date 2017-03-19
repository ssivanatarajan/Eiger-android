package shivtech.eiger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.utils.Constants;
import shivtech.eiger.utils.Utils;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String getTablesURL = "https://eigerapp.herokuapp.com/api/downloadTables";
    static int typeCounter;
    boolean isLoggedin = false;
    Button signup, login;
    SharedPreferences.Editor sp_editor;
    EditText empid, password;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    String reqTypes[] = {"Towers", "Users", "Teams", "Apps", "AppPrimaryUsers", "AppSecondaryUsers"};
    SharedPreferences sharedPreferences;
    private View mContentView;
    private Context mContext;
    private AppListFragment.OnFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        sp_editor = sharedPreferences.edit();
        boolean firstload = sharedPreferences.getBoolean(Constants.sp_firstLoad, false);


       /* if(firstload) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
            finish();
        }*/

        //  mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        progressDialog = new ProgressDialog(this);
        mContentView = findViewById(R.id.fullscreen_content);
        login = (Button) findViewById(R.id.wl_login);
        empid = (EditText) findViewById(R.id.empid);
        password = (EditText) findViewById(R.id.passwword);

        signup = (Button) findViewById(R.id.wl_sign_up);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup_intent = new Intent(getApplicationContext(), VerifyMobile.class);
                startActivity(signup_intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);*/
                if (!Utils.checkInternetConnection()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("No Internet Connection, please check your connectivity")
                            .setIcon(android.R.drawable.stat_sys_warning)
                            .setPositiveButton("Ok", null);

                    AlertDialog dialog = builder.show();
                }
                login();
            }
        });


        //
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    }

    private void login() {
        String login_url = "https://eigerapp.herokuapp.com/api/sessions/";
        boolean isValid = true;

        final String empId = empid.getText().toString();
        String pwd = password.getText().toString();
        if (empId.trim().length() < 6) {
            empid.requestFocus();
            empid.setError("Invalid Emp ID");
            isValid = false;
        }
        if (pwd.trim().length() == 0) {
            password.requestFocus();
            password.setError("please enter password");
            isValid = false;
        }

        if (isValid) {
            progressDialog.setMessage("Logging in...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            Log.e("login form", "valid");
            JSONObject params = new JSONObject();
            try {
                params.put("username", empId);
                params.put("password", pwd);
            } catch (JSONException e) {
                Log.e("jsonexp", e.toString());
                e.printStackTrace();
            }
            final String requestBody = params.toString();
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JsonObjectRequest login_request = new JsonObjectRequest(Request.Method.POST, login_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e("Log in res", response.toString());
                                isLoggedin = response.getBoolean("loginStatus");
                                if (isLoggedin) {
                                    Log.e("Logged in", "true");
                                    String userRole = response.getString("userRole");
                                    String authToken = response.getString("token");
                                    int int_empid = Integer.parseInt(empId);
                                    sp_editor.putString(Constants.sp_cur_user_rol, userRole);
                                    sp_editor.putInt(Constants.sp_cur_user_empId, int_empid);
                                    Constants.currentUserEmpid = int_empid;

                                    DBHandler dbHandler=new DBHandler(getApplicationContext());
                                    String loggedinUsername=dbHandler.getUsername(int_empid);
                                    sp_editor.putString(Constants.sp_cur_user_name,loggedinUsername);
                                    sp_editor.commit();
                                    progressDialog.dismiss();
                                    loginResult();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Log.e("login json exp", e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("login res err", error.toString());
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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

            requestQueue.add(login_request);

        }
    }

    private void loginResult() {

        boolean firstload = sharedPreferences.getBoolean(Constants.sp_firstLoad, false);


        if (firstload) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
            finish();

        } else {

               /* progressDialog = new ProgressDialog(this);
                //progressDialog.setMessage("Loading...");
                requestQueue = Volley.newRequestQueue(this);
                sendRequest(reqTypes[typeCounter]);
           /* sendRequest("Users");
            sendRequest("Teams");
            sendRequest("Apps");
            sendRequest("AppPrimaryUsers");
            sendRequest("AppSecondaryUsers");*/
            Utils utils = new Utils(LoginActivity.this);
            utils.downloadDumps();
            // finish();
                /*sp_editor.putBoolean(Constants.sp_firstLoad, true);
                sp_editor.commit();*/

        }
    }


}
    /* moved into Utils class


     */

    /*private void sendRequest(final String type) {
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


    }*/

