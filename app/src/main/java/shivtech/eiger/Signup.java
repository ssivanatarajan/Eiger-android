package shivtech.eiger;

import android.app.ProgressDialog;
import android.app.SearchableInfo;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import shivtech.eiger.utils.Constants;
import shivtech.eiger.utils.Utils;

public class Signup extends AppCompatActivity {

    EditText empid, password, confirm_password;
    boolean isvalid;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sp_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_signup);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        sp_editor = sharedPreferences.edit();
        empid = (EditText) findViewById(R.id.empid);
        password = (EditText) findViewById(R.id.passwword);
        confirm_password = (EditText) findViewById(R.id.confirm_passwword);


        Button login_btn = (Button) findViewById(R.id.login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        Button sign_up = (Button) findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.checkInternetConnection()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                            .setMessage("No Internet Connection, plesae check your connectivity")
                            .setIcon(android.R.drawable.stat_sys_warning)
                            .setPositiveButton("Ok", null);

                    AlertDialog dialog = builder.show();
                } else {
                    sign_up();
                }
            }
        });


    }

    private void sign_up() {
        progressDialog.setMessage(" Checking Emp ID" + " ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        Log.e("in", "sig up");

        String sign_up_url = "https://eigerapp.herokuapp.com/api/users/";
        String empId_check_url = "https://eigerapp.herokuapp.com/api/users/";

        final String emp_id = empid.getText().toString();
        final String pwd = password.getText().toString();
        final String confirm_pwd = confirm_password.getText().toString();

        empId_check_url += "empId?value=" + emp_id;
        sign_up_url += emp_id + "?password=" + pwd;

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final JsonObjectRequest sign_up_request = new JsonObjectRequest(Request.Method.PUT, sign_up_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            if (response.getString("msg").equals("registered successfully")) {
                                int int_empid = Integer.parseInt(emp_id);
                                sp_editor.putInt(Constants.sp_cur_user_empId, int_empid);
                                /*DBHandler dbHandler=new DBHandler(getApplicationContext());
                                String loggedinUsername=dbHandler.getUsername(int_empid);
                                sp_editor.putString(Constants.sp_cur_user_name,loggedinUsername);*/
                                sp_editor.commit();

                                boolean firstload = sharedPreferences.getBoolean(Constants.sp_firstLoad, false);
                                progressDialog.dismiss();
                                if (!firstload) {
                                    Utils utils = new Utils(Signup.this);
                                    utils.downloadDumps();
                                }

                                Intent EditProfileIntent=new Intent(Signup.this, EditProfile.class);
                                startActivity(EditProfileIntent);



                            }
                            Log.e("sign up res", response.getString("msg"));
                        } catch (JSONException exp) {
                            Log.e("json exp", exp.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        Log.e("signup err", error.toString());
                        progressDialog.dismiss();
                    }
                }

        );


        JsonObjectRequest empId_check_req = new JsonObjectRequest(Request.Method.GET, empId_check_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObj) {
                if (responseObj.length() > 0) {
                    try {
                        Log.e("emp id check res", responseObj.toString());

                        Log.e("res type", responseObj.getString("type"));
                        if (responseObj.getString("type").equals("empId")) {
                            if (responseObj.getBoolean("hasPassword")) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "EMP ID is already registered ", Toast.LENGTH_LONG).show();
                                Log.e("emp ID resgisterd", "has password");
                            } else if (responseObj.getBoolean("empIdRegistered")) {
                                isvalid = true;
                                if (!pwd.equals(confirm_pwd)) {
                                    Log.e("pwd check", "not equal");
                                    progressDialog.dismiss();
                                    confirm_password.requestFocus();
                                    confirm_password.setError("Password mismatch");
                                    isvalid = false;
                                }
                                if (confirm_pwd.length() < 8) {
                                    confirm_password.setError("Password min length is 8 ");
                                    confirm_password.requestFocus();
                                    progressDialog.dismiss();

                                    Log.e("confirm pwd check", "pwd mn length");
                                    isvalid = false;
                                }

                                if (pwd.length() < 8) {
                                    password.setError("Password min length is 8 ");
                                    password.requestFocus();
                                    progressDialog.dismiss();
                                    Log.e("pwd check", "pwd mn length");
                                    isvalid = false;
                                }
                                if (isvalid) {

                                    progressDialog.setMessage("Signing in ...");
                                    progressDialog.show();
                                    requestQueue.add(sign_up_request);
                                }
                            } else {
                                empid.requestFocus();
                                empid.setError("Emp ID is not added");
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Emp ID is not added. please contact your Supervisor", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("json exp", e.toString());

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Log.e("emp id check err", error.toString());
                progressDialog.dismiss();
            }
        });
        requestQueue.add(empId_check_req);


    }
}
