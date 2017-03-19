package shivtech.eiger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.models.App;
import shivtech.eiger.models.Team;
import shivtech.eiger.models.Tower;
import shivtech.eiger.models.User;
import shivtech.eiger.utils.Constants;
import shivtech.eiger.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfessionalProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfessionalProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessionalProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static ArrayList<Team> selectedTeams;
    static ArrayList<App> selectedPrimaryApps;
    static ArrayList<App> selectedSecondaryApps;
    DBHandler dbHandler;
    boolean[] checkedItems;
    ProgressDialog progressDialog;
    Team teams[];
    SharedPreferences sharedPreferences;
    String currentUser;
    int selectedTowerid;
    Integer selectedTeamIds[], selectedPriAppIDs[], selectedSecAppIDs[];
    App primaryApps[], secondaryApps[];
    Button pp_save, pp_reset;
    EditText Et_primaryApps, Et_secondaryApps, Et_tools, Et_programming_langs;
    String[] teamNames;
    EditText select_teams;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //ArrayList<String> selectedTeamNames;
    //ArrayList<Integer> selectedTeamIDs;
    private OnFragmentInteractionListener mListener;

    public ProfessionalProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfessionalProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfessionalProfile newInstance(String param1, String param2) {
        ProfessionalProfile fragment = new ProfessionalProfile();
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
        View view = inflater.inflate(R.layout.fragment_professional_profile, container, false);
        final Spinner tower_spinner = (Spinner) view.findViewById(R.id.pp_tower_spinner);
        dbHandler = new DBHandler(getContext());
        ArrayList<Tower> towers = dbHandler.getAllTowers();
        ArrayAdapter<Tower> towerAdapter = new ArrayAdapter<Tower>(getContext(), android.R.layout.simple_spinner_dropdown_item, towers);
        towerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tower_spinner.setAdapter(towerAdapter);
        tower_spinner.setSelection(0, false);
        sharedPreferences = getContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        int empid = sharedPreferences.getInt(Constants.sp_cur_user_empId, 0);
        currentUser = String.valueOf(empid);
      /*  selectedTeamIDs=new ArrayList<Integer>();
        selectedTeamNames=new ArrayList<String>();*/
        Et_programming_langs = (EditText) view.findViewById(R.id.pp_prgm_lang_frmworks);
        Et_tools = (EditText) view.findViewById(R.id.pp_tools);
        select_teams = (EditText) view.findViewById(R.id.pp_teams);
        pp_save = (Button) view.findViewById(R.id.pp_save);
        pp_reset = (Button) view.findViewById(R.id.pp_reset);
        Et_primaryApps = (EditText) view.findViewById(R.id.pp_primary_apps);
        Et_secondaryApps = (EditText) view.findViewById(R.id.pp_secondary_apps);

        primaryApps=new App[0];
        secondaryApps=new App[0];

        User user = dbHandler.getUserdetails(empid);
        Et_tools.setText(user.getTools());
        Et_programming_langs.setText(user.getProgramming_langs());
        int usertowerid = user.getUserTowerID();

        for (int i = 0; i < towers.size(); i++) {
            if (towers.get(i).getTowerId() == usertowerid) {
                Log.e("set tower",i+"");
                tower_spinner.setSelection(i);
            }
        }

        if (user.getUserTeams() != null) {
            selectedTeams = user.getUserTeams();
            onChangeSelectedItems("Teams");
        } else
            selectedTeams = new ArrayList<Team>();
        if (user.getPrimaryApps() != null) {
            selectedPrimaryApps = user.getPrimaryApps();
            onChangeSelectedItems("Primary Apps");
        } else
            selectedPrimaryApps = new ArrayList<App>();
        if (user.getSecondaryApps() != null) {
            selectedSecondaryApps = user.getSecondaryApps();
            onChangeSelectedItems("Secondary Apps");

        } else
            selectedSecondaryApps = new ArrayList<App>();


        pp_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tower_spinner.setSelection(0);
                reset_towerSpinner();
                select_teams.setText("");
                Et_primaryApps.setText("");
                Et_tools.setText("");
                selectedTeams = new ArrayList<Team>();
                selectedPriAppIDs = new Integer[0];
                selectedPrimaryApps = new ArrayList<App>();
                selectedSecAppIDs = new Integer[0];;
                selectedSecondaryApps = new ArrayList<App>();

                Et_secondaryApps.setText("");
                Et_programming_langs.setText(" ");
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        pp_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* if (!Utils.checkInternetConnection()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage("No Internet Connection, please check your connectivity")
                            .setIcon(android.R.drawable.stat_sys_warning)
                            .setPositiveButton("Ok", null);

                    AlertDialog dialog = builder.show();


                } else {*/
                professionalProfileSave();
                //}


            }
        });

       tower_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              Log.e("tower_spinner","onitemselected");
               reset_towerSpinner();
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });
        select_teams.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.e("select teams", "click" + tower_spinner.getSelectedItem());

                selectedTowerid = ((Tower) tower_spinner.getSelectedItem()).getTowerId();
                Log.e("selectedTowerId", selectedTowerid + "");

                Object[] teamObjectArray = dbHandler.getTeamsByTower(selectedTowerid).toArray();

                showSelectItemsDialog(teamObjectArray, "Teams");
            }
        });

        Et_primaryApps.setInputType(InputType.TYPE_NULL);
        Et_primaryApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("select pri apps clicked", selectedTeams.toString());
                Integer selectedTeamIds[] = new Integer[selectedTeams.size()];
                Log.e("selectedteams size",selectedTeamIds.length+"");
                Log.e("selectedSecAppsSize",selectedSecondaryApps.size()+"");

                for (int i = 0; i < selectedTeams.size(); i++)
                    selectedTeamIds[i] = selectedTeams.get(i).getTeamID();
                if (selectedTeamIds.length > 0) {
                    ArrayList<App> primaryApps = dbHandler.getAppsbyTeams(selectedTeamIds);
                    Log.e("primaryAppsbyselteam",primaryApps.size()+"");
                    int priSize=primaryApps.size();
                    if (selectedSecondaryApps.size() > 0) {
                        Collection<App> selectedsecAppcollection=new ArrayList<App>(selectedSecondaryApps);
                        primaryApps.removeAll(selectedsecAppcollection);
                        /*for (int i = 0; i < priSize; i++) {
                            Log.e("primaryapp",i+"");
                            Log.e("primaryapp appid",primaryApps.get(i).getAppId()+"");
                            for (int j = 0; j < selectedSecondaryApps.size(); j++) {
                                Log.e("secapp",j+"");
                                Log.e("secapp appid",selectedSecondaryApps.get(j).getAppId()+"");

                                if (primaryApps.get(i).getAppId() == selectedSecondaryApps.get(j).getAppId()) {
                                    Log.e("primaryappcheck","sameappid"+primaryApps.get(i).getAppId());
                                    primaryApps.remove(i);
                                    break;
                                }
                            }
                        }*/
                    }
                    Log.e("primaryappsdialogsize",primaryApps.size()+"");
                    Object[] primaryAppsObjArray = primaryApps.toArray();
                    showSelectItemsDialog(primaryAppsObjArray, "Primary Apps");
                } else {
                    select_teams.requestFocus();
                    select_teams.setError("please select team");
                    Toast.makeText(getContext(),"Please select team",Toast.LENGTH_LONG).show();

                }
            }
        });

        Et_secondaryApps.setInputType(InputType.TYPE_NULL);
        Et_secondaryApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("select pri apps clicked", selectedTeams.toString());
                Integer[] selectedTeamIds = new Integer[selectedTeams.size()];
                for (int i = 0; i < selectedTeams.size(); i++)
                    selectedTeamIds[i] = selectedTeams.get(i).getTeamID();
                if (selectedTeamIds.length > 0) {
                    ArrayList<App> secondaryApps = dbHandler.getAppsbyTeams(selectedTeamIds);

                    if (selectedPrimaryApps.size() > 0) {
                        Collection<App> selectedpriAppcollection=new ArrayList<App>(selectedPrimaryApps);
                        secondaryApps.removeAll(selectedpriAppcollection);

                       /* for (int i = 0; i < secondaryApps.size(); i++) {
                            for (int j = 0; j < selectedPrimaryApps.size(); j++)
                                if (secondaryApps.get(i).getAppId() == selectedPrimaryApps.get(j).getAppId()) {
                                    Log.e("secondaryappcheck","sameappid"+secondaryApps.get(i).getAppId());
                                    secondaryApps.remove(i);
                                    break;
                                }
                        }*/
                    }
                    Log.e("Secondaryappsdialogsize",secondaryApps.size()+"");
                    Object[] SecondaryAppsObjArray = secondaryApps.toArray();
                    showSelectItemsDialog(SecondaryAppsObjArray, "Secondary Apps");
                } else {
                    select_teams.requestFocus();
                    select_teams.setError("please select team");
                    Toast.makeText(getContext(),"Please select team",Toast.LENGTH_LONG).show();

                }
            }
        });

        return view;
    }


    private void reset_towerSpinner() {
        Log.e("reset towerspinner","reset");
        Et_primaryApps.setText("");
        Et_secondaryApps.setText("");
        select_teams.setText("");

        selectedTeams = new ArrayList<Team>();
        selectedPrimaryApps = new ArrayList<App>();
        selectedSecondaryApps = new ArrayList<App>();
    }

    protected void showSelectItemsDialog(Object[] objArray, final String type) {
        checkedItems = new boolean[objArray.length];
        int count = objArray.length;

        String[] Names = new String[objArray.length];
        if (type.equals("Teams")) {
            teams = Arrays.copyOf(objArray, objArray.length, Team[].class);
            Log.e("teams", teams + " " + teams.length);
            if (teams.length > 0)
                Log.e("team name", teams[0].getTeamName());
            for (int i = 0; i < teams.length; i++)
                Names[i] = teams[i].getTeamName();
            Log.e("checked teams length", "" + selectedTeams.size());
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < selectedTeams.size(); j++) {
                    if (selectedTeams.get(j).getTeamID() == teams[i].getTeamID())
                        checkedItems[i] = true;
                    Log.e("checked team indx", checkedItems[i] + "");
                }

            }

        } else if (type.equals("Primary Apps")) {
            primaryApps = Arrays.copyOf(objArray, objArray.length, App[].class);
            Log.e("primaryApps", primaryApps + " " + primaryApps.length);
            if (primaryApps.length > 0)
                Log.e("team name", primaryApps[0].getAppName());
            for (int i = 0; i < primaryApps.length; i++)
                Names[i] = primaryApps[i].getAppName();
            Log.e("checked primary apps", selectedPrimaryApps.toString());
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < selectedPrimaryApps.size(); j++) {
                    if (selectedPrimaryApps.get(j).getAppId() == primaryApps[i].getAppId())
                        checkedItems[i] = true;

                }
                Log.e("checked primaryapp indx", checkedItems[i] + "");
            }

        } else if (type.equals("Secondary Apps")) {
            secondaryApps = Arrays.copyOf(objArray, objArray.length, App[].class);
            Log.e("secondaryApps", secondaryApps + " " + secondaryApps.length);
            if (secondaryApps.length > 0)
                Log.e("team name", secondaryApps[0].getAppName());
            for (int i = 0; i < secondaryApps.length; i++)
                Names[i] = secondaryApps[i].getAppName();
            Log.e("checked sec apps", selectedSecondaryApps.toString());
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < selectedSecondaryApps.size(); j++) {
                    if (selectedSecondaryApps.get(j).getAppId() == secondaryApps[i].getAppId())
                        checkedItems[i] = true;

                }
                Log.e("checked secapp indx", checkedItems[i] + "");
            }

        }


        DialogInterface.OnMultiChoiceClickListener DialogListener = new DialogInterface.OnMultiChoiceClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (type.equals("Teams")) {
                    if (isChecked) {
                        selectedTeams.add(teams[which]);

                    } else {
                        Integer[] unselectedTeamIds=new Integer[0];
                        for (int i = 0; i < selectedTeams.size(); i++) {
                            if (selectedTeams.get(i).getTeamID() == teams[which].getTeamID()) {
                                Log.e("team unselect", teams[which] + "");
                                unselectedTeamIds = new Integer[]{selectedTeams.get(i).getTeamID()};
                                selectedTeams.remove(i);
                            }
                        }
                                if (unselectedTeamIds.length > 0) {
                                    ArrayList<App> AppsInRemovedTeam = dbHandler.getAppsbyTeams(unselectedTeamIds);
                                    Log.e("AppsremovedTeam",AppsInRemovedTeam.size()+"");
                                    Collection<App> AppsInRemovedTeamCollection = new ArrayList(AppsInRemovedTeam);
                                    Log.e("APPSinremovedteamsie",AppsInRemovedTeamCollection.size()+"");
                                    selectedPrimaryApps.removeAll(AppsInRemovedTeamCollection);
                                    selectedSecondaryApps.removeAll(AppsInRemovedTeamCollection);
                                    Log.e("selpriappsafterremoved",selectedPrimaryApps.size()+"");
                                    Log.e("selsecappsafterremoved",selectedSecondaryApps.size()+"");
                                    onChangeSelectedItems("Primary Apps");
                                    onChangeSelectedItems("Secondary Apps");

                        }
                    }
                } else if (type.equals("Primary Apps")) {
                    if (isChecked)
                        selectedPrimaryApps.add(primaryApps[which]);
                    else {
                        Log.e("Selectedprimaryappsize",selectedPrimaryApps.size()+"");
                        Log.e("primaryAppslength",primaryApps.length+"");
                        if(primaryApps.length>0) {
                            for (int i = 0; i < selectedPrimaryApps.size(); i++) {
                                if (selectedPrimaryApps.get(i).getAppId() == primaryApps[which].getAppId()) {
                                    selectedPrimaryApps.remove(i);
                                    Log.e("primary app unselect", primaryApps[which] + "");
                                    break;
                                }
                            }
                        }
                    }
                } else if (type.equals("Secondary Apps")) {
                    if (isChecked)
                        selectedSecondaryApps.add(secondaryApps[which]);
                    else {
                        Log.e("Selectedsecappsize",selectedSecondaryApps.size()+"");
                        Log.e("secondaryAppslength",secondaryApps.length+"");
                        if(secondaryApps.length>0) {
                        for (int i = 0; i < selectedSecondaryApps.size(); i++) {

                                if (selectedSecondaryApps.get(i).getAppId() == secondaryApps[which].getAppId()) {
                                    selectedSecondaryApps.remove(i);
                                    Log.e("secapp unselect", secondaryApps[which] + "");
                                    break;
                                }
                            }
                        }


                    }
                }

            }

        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Select " + type);
        for (boolean b : checkedItems)
            Log.e("checkeditem value", b + "");
        builder.setMultiChoiceItems(Names, checkedItems, DialogListener);
        AlertDialog dialog;
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onChangeSelectedItems(type);

                dialogInterface.cancel();
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


       dialog.show();

    }


    protected void onChangeSelectedItems(String type) {


        StringBuilder stringBuilder = new StringBuilder();
        if (type.equals("Teams")) {
            for (Team t : selectedTeams) {
                stringBuilder.append(t.getTeamName() + ",");
                Log.e("selected team", t.getTeamName());
            }
            Log.e("selected teams text", stringBuilder.toString());
            select_teams.setText(stringBuilder.toString());
        } else if (type.equals("Primary Apps")) {
            for (App app : selectedPrimaryApps) {
                stringBuilder.append(app.getAppName() + " ,");
                Log.e("selected primary app", app.getAppName());
            }
            Log.e("selected pri apps text", stringBuilder.toString());
            Et_primaryApps.setText(stringBuilder);

        } else if (type.equals("Secondary Apps")) {
            for (App app : selectedSecondaryApps) {
                stringBuilder.append(app.getAppName() + " ,");
                Log.e("selected secondary app", app.getAppName());
            }
            Log.e("selected secapps text", stringBuilder.toString());
            Et_secondaryApps.setText(stringBuilder);

        }
    }


    private void professionalProfileSave() {
        boolean isValid = true;
        String profile_save_url = "https://eigerapp.herokuapp.com/api/userProfessionalProfile/";
        selectedPriAppIDs = new Integer[selectedPrimaryApps.size()];
        for (int i = 0; i < selectedPrimaryApps.size(); i++)
            selectedPriAppIDs[i] = selectedPrimaryApps.get(i).getAppId();
        selectedSecAppIDs = new Integer[selectedSecondaryApps.size()];
        for (int i = 0; i < selectedSecondaryApps.size(); i++)
            selectedSecAppIDs[i] = selectedSecondaryApps.get(i).getAppId();
        Log.e("selectedprimary apps", selectedPriAppIDs.toString());
        Log.e("selected seco app ids", selectedSecAppIDs.toString());

        JSONArray primaryappIDs = new JSONArray();
        for (int id : selectedPriAppIDs)
            primaryappIDs.put(id);
        JSONArray secondaryappIDs = new JSONArray();
        for (int id : selectedSecAppIDs)
            secondaryappIDs.put(id);
        String str_tools = Et_tools.getText().toString();
        String str_program_langs = Et_programming_langs.getText().toString();
        if (isValid) {
            Log.e("isvalid", "true");
            int res = dbHandler.updateProfessionalProfile(currentUser, str_tools, selectedPriAppIDs, selectedSecAppIDs, str_program_langs, selectedTowerid);
            if (res == 1)
                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_LONG).show();
            if (Utils.checkInternetConnection()) {

                progressDialog.setMessage("Saving data..");
                progressDialog.show();

                final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                JSONObject params = new JSONObject();
                try {
                    params.put("empId", currentUser);
                    params.put("tools", str_tools);
                    params.put("primaryapps", primaryappIDs);
                    params.put("secondaryapps", secondaryappIDs);
                    params.put("programming_langs", str_program_langs);
                    params.put("tower", selectedTowerid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String requestBody = params.toString();
                Log.e("request body", requestBody);
                final JsonObjectRequest update_profile_request = new JsonObjectRequest(Request.Method.PUT, profile_save_url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("save res", response.toString());
                                try {
                                    if (response.getString("msg").equals("updated successfully"))
                                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("save err", error.toString());
                        Toast.makeText(getContext(), "Error occurs " + error.toString(), Toast.LENGTH_LONG).show();
                        dbHandler.makeDirty(Constants.appPrimaryUserTable, 1, currentUser);
                        dbHandler.makeDirty(Constants.appSecondaryUserTable, 1, currentUser);
                        dbHandler.makeDirty(Constants.userTable, 1, currentUser);

                        progressDialog.setMessage("Error occurs");
                        progressDialog.dismiss();
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
                requestQueue.add(update_profile_request);

            } else {
                dbHandler.makeDirty(Constants.appPrimaryUserTable, 1, currentUser);
                dbHandler.makeDirty(Constants.appSecondaryUserTable, 1, currentUser);
                dbHandler.makeDirty(Constants.userTable, 1, currentUser);
            }
        }
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
