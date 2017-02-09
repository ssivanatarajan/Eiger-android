package shivtech.eiger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AppListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AppListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<App> apps,filteredApps;
    AppListAdapter mAppsAdapter;
    SharedPreferences.Editor sp_editor;
    private Context mContext;
    TextView jsonView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean firstLoad;
    ProgressBar progressBar;
    ListView listView;
     SearchView searchView;
    RequestQueue requestQueue;
    private static  final String getTablesURL="https://eigerapp.herokuapp.com/api/downloadTables";
    private OnFragmentInteractionListener mListener;
    ProgressDialog progressDialog;
    String reqTypes[]={"Towers","Users","Teams","Apps","AppPrimaryUsers","AppSecondaryUsers"};
    static  int typeCounter;
    SharedPreferences sharedPreferences;
    public AppListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppListFragment newInstance(String param1, String param2) {
        AppListFragment fragment = new AppListFragment();
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
        mContext=getContext();
        sharedPreferences=getContext().getSharedPreferences(Constants.shared_prefs,Context.MODE_PRIVATE);
        View view= inflater.inflate(R.layout.fragment_app_list, container, false);
        listView=(ListView) view.findViewById(R.id.appList);
         searchView=(SearchView) view.findViewById(R.id.applist_searchview);
       // jsonView=(TextView)view.findViewById(R.id.jsonresponse);
        progressBar=(ProgressBar)view.findViewById(R.id.app_list_progressbar);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        listView.setFastScrollEnabled(true);
        View empty_list_view=LayoutInflater.from(getContext()).inflate(R.layout.empty_list_view,null);
        listView.setEmptyView(empty_list_view);
          firstLoad=true;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Intent appProfIntent=new Intent(mContext,AppProfile.class);
                TextView appIdview=(TextView)view.findViewById(R.id.app_id);
                int appId=Integer.parseInt(appIdview.getText().toString());
                appProfIntent.putExtra("AppID",appId);
                Log.i("itemclick","itemclick");
                mContext.startActivity(appProfIntent);
            }
        });


        /*Log.v("no of apps", apps.size()+"");
        filteredApps=apps;*/
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                    Log.i("searchview onfocus","in");
                    if(firstLoad)
                    {
                        Log.i("firstLoad-searchview","true");
                        searchView.clearFocus();
                        firstLoad=false;
                    }

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.trim().length()>0) {
                    query = query.toLowerCase();
                    filteredApps = new ArrayList<App>();
                    for(App app:apps) {
                        if (app.getAppAlias() != null) {
                            if (app.getAppName().toLowerCase().contains(query) || app.getAppAlias().toLowerCase().contains(query))
                                filteredApps.add(app);
                        }
                        else
                            if (app.getAppName().toLowerCase().contains(query))
                                filteredApps.add(app);

                    }
                }
                else
                    filteredApps=apps;
                sortApps();
                mAppsAdapter=new AppListAdapter(getContext(),filteredApps);
                listView.setAdapter(mAppsAdapter);

                mAppsAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(query.trim().length()>0) {
                    query = query.toLowerCase();
                    filteredApps = new ArrayList<App>();
                    for (App app : apps) {
                        Log.d("appnme", app.getAppName());
                        if (app.getAppAlias() != null) {
                            if (app.getAppName().toLowerCase().contains(query) || app.getAppAlias().toLowerCase().contains(query))
                                filteredApps.add(app);
                        } else if (app.getAppName().toLowerCase().contains(query))
                            filteredApps.add(app);

                    }
                }
                else
                    filteredApps=apps;
                sortApps();
                mAppsAdapter=new AppListAdapter(getContext(),filteredApps);
                listView.setAdapter(mAppsAdapter);

                mAppsAdapter.notifyDataSetChanged();
                return false;
            }
        });
        Log.v("Sorted list"," "+apps);
       // new LoadDatabase().execute("execute");

      /*  boolean firstload=sharedPreferences.getBoolean(Constants.sp_firstLoad,false);
         sp_editor=sharedPreferences.edit();



        if(!firstload) {
            if(!checkInternetConnection()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setMessage("No Internet Connection, plesae check your connectivity")
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .setPositiveButton("Ok", null);

                AlertDialog dialog = builder.show();
            }
            else {
                progressDialog = new ProgressDialog(getContext());
                //progressDialog.setMessage("Loading...");
                requestQueue = Volley.newRequestQueue(getContext());
                sendRequest(reqTypes[typeCounter]);
           /* sendRequest("Users");
            sendRequest("Teams");
            sendRequest("Apps");
            sendRequest("AppPrimaryUsers");
            sendRequest("AppSecondaryUsers");*/

            /*    sp_editor.putBoolean(Constants.sp_firstLoad, true);
                sp_editor.commit();
            }
        }
        else
        {*/
            DBHandler dbHandler=new DBHandler(getContext());
            apps=dbHandler.getApps();
            filteredApps=apps;

            sortApps();
            mAppsAdapter=new AppListAdapter(getContext(),filteredApps);
            listView.setAdapter(mAppsAdapter);
            mAppsAdapter.notifyDataSetChanged();

        //}
        return view;
    }

    private boolean checkInternetConnection() {

        String command = "ping -c 1 google.com";
        try {
            return (Runtime.getRuntime().exec (command).waitFor() == 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sortApps() {
        Collections.sort(filteredApps, new Comparator<App>() {
            @Override
            public int compare(App lhs, App rhs) {
                return lhs.getAppName().compareTo(rhs.getAppName());
            }
        });
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
    private void sendRequest(final String type){
       if(typeCounter<reqTypes.length) {
           Log.e("reqtypes",reqTypes[typeCounter] +" "+typeCounter +" "+type);
           String URL = getTablesURL + "/" + type;
           progressDialog.setMessage("Loading "+type+" ...");
           progressDialog.show();
           StringRequest stringRequest = new StringRequest(URL,
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           Log.i("response", response);
                           progressDialog.dismiss();
                           showJSON(response, type);
                            typeCounter++;
                           if(typeCounter<reqTypes.length)
                            sendRequest(reqTypes[typeCounter]);

                       }
                   },
                   new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           sp_editor.putBoolean(Constants.sp_firstLoad,false);
                           sp_editor.commit();
                           progressDialog.dismiss();
                           Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                           Log.e("send request", error.toString());


                       }
                   });



           requestQueue.add(stringRequest);
       }
    }
private void showJSON(String response,String type)
{
    DBHandler dbHandler=new DBHandler(getContext());
    ArrayList<AppJSONModel> appsJSON;
    ArrayList<User> users;
    ArrayList<Team> teams;
    ArrayList<Tower> towers;
    ArrayList<AppPrimaryUser> appPrimaryUsers;
    ArrayList<AppSecondaryUser> appSecondaryUsers;
    switch(type)
    {
        case "Apps":
            AppsJSONParser appsJSONParser=new AppsJSONParser(response);
            appsJSON=appsJSONParser.parseJson();

            dbHandler.addApps(appsJSON);
            Log.e("Apps","adding apps "+appsJSON.size());
            apps=dbHandler.getApps();
            filteredApps=apps;
            Log.i("Apps Json",apps+"");

            sortApps();
            mAppsAdapter=new AppListAdapter(getContext(),filteredApps);
            listView.setAdapter(mAppsAdapter);
            mAppsAdapter.notifyDataSetChanged();
            break;
        case "Users":
            UserJSONParser userJSONParser=new UserJSONParser(response);
            users=userJSONParser.parseUserJSON();
            Log.e("Users","adding users "+users.size());
            dbHandler.addUsers(users);
            break;
        case "Teams":
            TeamJSONParser teamJSONParser=new TeamJSONParser(response);
            teams=teamJSONParser.parseJSON();
            Log.e("Teams","adding teams "+teams.size());
            dbHandler.addTeams(teams);
            break;
        case "Towers":
            TowerJSONParser towerJSONParser=new TowerJSONParser(response);
            towers=towerJSONParser.parseJSON();
            Log.e("Towers","adding towers "+towers.size());
            dbHandler.addTowers(towers);
            break;
        case "AppPrimaryUsers":
            AppsPrimaryJSONParser appsPrimaryJSONParser=new AppsPrimaryJSONParser(response);
            appPrimaryUsers=appsPrimaryJSONParser.parseJSON();
            Log.e("primaryuser","adding primaryuser " +appPrimaryUsers.size());
            dbHandler.appPrimaryUserMapping(appPrimaryUsers);
            break;
        case "AppSecondaryUsers":
            AppsSecondaryJSONParser appsSecondaryJSONParser=new AppsSecondaryJSONParser(response);
            appSecondaryUsers=appsSecondaryJSONParser.parseJSON();
            Log.e("Secondaryusers","adding secondaryusers "+appSecondaryUsers.size());
            dbHandler.appSecondaryUserMapping(appSecondaryUsers);
            break;
        default:
            Log.i("adding to tables","default case");
            break;
    }





//    jsonView.setText(response);
}
    private class LoadDatabase extends AsyncTask<String,Integer,ArrayList<App>>
    {
        ProgressDialog progressDialog;
        @Override
        protected ArrayList<App> doInBackground(String... strings) {
            Log.i("Asynctask","doInbackgroud");
         /*   DataBaseHandler dbHandler=new DataBaseHandler(getContext());
            try {
                dbHandler.createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // dbHandler.addApp("Global Customer Database","Online MS Apps","GCD","Gold","Online");
            apps= dbHandler.getApps();*/
            return apps;
        }
        @Override
        protected void onPostExecute(ArrayList<App> result) {
            Log.i("Asynctask","post execute");
          //  progressBar.setVisibility(View.INVISIBLE);
            filteredApps=result;
            sortApps();

            mAppsAdapter=new AppListAdapter(getContext(),filteredApps);
            listView.setAdapter(mAppsAdapter);
            progressDialog.dismiss();

        }
        @Override
        protected void onPreExecute() {
            //textView.setText("Hello !!!");
            //progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            Log.i("Asynctask","preexecute");
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Data...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("Asynctask","progress update" +values);
            super.onProgressUpdate(values);

        }
    }

    private class LoadAppDatafromserevr extends AsyncTask<String,Integer,ArrayList<App>> {

        @Override
        protected ArrayList<App> doInBackground(String... strings) {
            return null;
        }
    }

}
