package shivtech.eiger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.models.Team;
import shivtech.eiger.models.Tower;
import shivtech.eiger.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminAppManagementFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminAppManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAppManagementFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RequestQueue requestQueue;
    EditText removeAppID;
    EditText appName, appAlias;
    Spinner appSupportLevel, appcategory, appTeam, appTower;
    Button addAppBtn, removeAppBtn;
    DBHandler dbHandler;
    ProgressDialog progressDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public AdminAppManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminAppManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminAppManagementFragment newInstance(String param1, String param2) {
        AdminAppManagementFragment fragment = new AdminAppManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View admin_app_view = inflater.inflate(R.layout.fragment_admin_app_management, container, false);
        appName = (EditText) admin_app_view.findViewById(R.id.ad_add_app_appname);
        appAlias = (EditText) admin_app_view.findViewById(R.id.ad_add_app_alias);
        appTower = (Spinner) admin_app_view.findViewById(R.id.ad_add_app_tower_spinner);
        appTeam = (Spinner) admin_app_view.findViewById(R.id.ad_add_app_team_spinner);
        appcategory = (Spinner) admin_app_view.findViewById(R.id.ad_add_app_category_spinner);
        appSupportLevel = (Spinner) admin_app_view.findViewById(R.id.ad_add_app_supportLevel_spinner);

        removeAppID = (EditText) admin_app_view.findViewById(R.id.ad_remove_app_appid);
        addAppBtn = (Button) admin_app_view.findViewById(R.id.ad_add_app_btn);
        removeAppBtn = (Button) admin_app_view.findViewById(R.id.ad_remove_app_btn);

        progressDialog = new ProgressDialog(getContext());
        addAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appname = appName.getText().toString();
                final String appaias = appAlias.getText().toString();
                final String category = appcategory.getSelectedItem().toString();
                final String supportLevel = appSupportLevel.getSelectedItem().toString();
                final int towerid = ((Tower) appTower.getSelectedItem()).getTowerId();
                final int teamid = ((Team) appTeam.getSelectedItem()).getTeamID();
                boolean isValid = true;

                if (appname.trim().length() == 0) {
                    appName.requestFocus();
                    appName.setError("App Name is required");
                    isValid = false;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final AlertDialog dialog = builder.create();
                dialog.setMessage("Do you want to add this app?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        addApp(appname, appaias, category, supportLevel, towerid, teamid);
                        dialog.dismiss();
                    }
                });

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });

                if (isValid)
                    dialog.show();

            }
        });

        removeAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;
                final String appid = removeAppID.getText().toString();
                if (appid.trim().length() == 0)
                    isValid = false;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final AlertDialog dialog = builder.create();
                dialog.setMessage("Do you want to Remove this app?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeApp(appid);
                        dialog.dismiss();
                    }
                });

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });

                if (isValid)
                    dialog.show();

            }
        });

        String[] categories = getResources().getStringArray(R.array.appCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appcategory.setAdapter(categoryAdapter);
        requestQueue = Volley.newRequestQueue(getContext());
        String[] supportLevels = getResources().getStringArray(R.array.appSupportlevel);
        ArrayAdapter<String> supportLevelAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, supportLevels);
        supportLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appSupportLevel.setAdapter(supportLevelAdapter);
        dbHandler = new DBHandler(getContext());
        ArrayList<Tower> towers = dbHandler.getAllTowers();
        ArrayAdapter<Tower> towerAdapter = new ArrayAdapter<Tower>(getContext(), android.R.layout.simple_spinner_dropdown_item, towers);
        towerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appTower.setAdapter(towerAdapter);

        int selectedTowerId = ((Tower) appTower.getSelectedItem()).getTowerId();
        Log.e("SelectedTowerid", selectedTowerId + "");
        ArrayList<Team> teams = dbHandler.getTeamsByTower(selectedTowerId);
        Log.e("team spinner length", teams.size() + "");
        ArrayAdapter<Team> teamAdapter = new ArrayAdapter<Team>(getContext(), android.R.layout.simple_spinner_dropdown_item, teams);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appTeam.setAdapter(teamAdapter);


        appTower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                initTeamSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return admin_app_view;

    }

    private void addApp(String appname, String appalias, String category, String supportLevel, int towerid, int teamid) {
        progressDialog.setMessage(" Adding App" + " ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        JSONObject params = new JSONObject();
        try {
            params.put("lastModifiedBy", Constants.currentUserEmpid);
            params.put("appname", appname);
            params.put("alias", appalias);
            params.put("category", category);
            params.put("supportLevel", supportLevel);
            params.put("tower", towerid);
            params.put("team", teamid);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = params.toString();
        Log.e("add App request body", requestBody);

        String add_app_url = "https://eigerapp.herokuapp.com/api/apps/";
        final JsonObjectRequest addAppReq = new JsonObjectRequest(Request.Method.POST, add_app_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Adding App", "" + response.toString());
                try {
                    if (response.getString("msg").equals("App successfully Added")) {
                        progressDialog.dismiss();
                        Log.e("Adding App", "Added" + response.toString());
                        Toast.makeText(getContext(), "App Successfully added", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Unable to add App ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("unable to add App", error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

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
        requestQueue.add(addAppReq);
    }

    private void removeApp(String appid) {
        progressDialog.setMessage(" Removing App" + " ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String add_app_url = "https://eigerapp.herokuapp.com/api/apps/" + appid;
        final JsonObjectRequest addAppReq = new JsonObjectRequest(Request.Method.DELETE, add_app_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Removing App", "" + response.toString());
                try {
                    if (response.getString("msg").equals("Unable to find App")) {
                        progressDialog.dismiss();
                        Log.e("removing App", "no app found" + response.toString());
                        Toast.makeText(getContext(), "No APP found", Toast.LENGTH_LONG).show();
                    } else if (response.getString("msg").equals("App successfully Removed")) {
                        progressDialog.dismiss();
                        Log.e("Adding App", "Added" + response.toString());
                        Toast.makeText(getContext(), "App Successfully removed", Toast.LENGTH_LONG).show();
                    } else if (response.getString("msg").equals("Unable to remove")) {
                        Toast.makeText(getContext(), "Unable to Remove App ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("unable to Remove App", error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

        requestQueue.add(addAppReq);


    }

    private void initTeamSpinner() {
        int selectedTowerId = ((Tower) appTower.getSelectedItem()).getTowerId();
        Log.e("SelectedTowerid", selectedTowerId + "");
        ArrayList<Team> teams = dbHandler.getTeamsByTower(selectedTowerId);
        Log.e("team spinner length", teams.size() + "");
        ArrayAdapter<Team> teamAdapter = new ArrayAdapter<Team>(getContext(), android.R.layout.simple_spinner_dropdown_item, teams);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appTeam.setAdapter(teamAdapter);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
