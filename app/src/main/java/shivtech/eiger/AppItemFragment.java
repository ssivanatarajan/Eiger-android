package shivtech.eiger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import shivtech.eiger.models.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AppItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    MyAppItemRecyclerViewAdapter mAppsAdapter;
    RecyclerView recyclerView;
    ArrayList<App> apps,filteredApps;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AppItemFragment newInstance(int columnCount) {
        AppItemFragment fragment = new AppItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appitem_list, container, false);
        SearchView searchView=(SearchView) view.findViewById(R.id.app_searchview);
       recyclerView=(RecyclerView)view.findViewById(R.id.app_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Set the adapter
          /*  DataBaseHandler dbHandler=new DataBaseHandler(getContext());
            try {
                dbHandler.createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // dbHandler.addApp("Global Customer Database","Online MS Apps","GCD","Gold","Online");
             apps= dbHandler.getApps();*/
            Log.v("no of apps", apps.size()+"");
             filteredApps=apps;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(query.trim().length()>0) {
                        query = query.toLowerCase();
                        filteredApps = new ArrayList<App>();
                        for(App app:apps){
                            if(app.getAppName().toLowerCase().contains(query) )//|| app.getAppAlias().toLowerCase().contains(query))
                                filteredApps.add(app);
                        }

                    }
                    else
                        filteredApps=apps;
                    sortApps();
                    mAppsAdapter=new MyAppItemRecyclerViewAdapter(filteredApps, mListener);
                    recyclerView.setAdapter(mAppsAdapter);
                    mAppsAdapter.notifyDataSetChanged();

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if(query.trim().length()>0) {
                        query = query.toLowerCase();
                        filteredApps = new ArrayList<App>();
                        for(App app:apps){
                            Log.d("appnme",app.getAppName());
                            if(app.getAppName().toLowerCase().contains(query))// || app.getAppAlias().toLowerCase().contains(query))
                                filteredApps.add(app);
                        }
                    }
                    else
                        filteredApps=apps;
                    sortApps();
                    mAppsAdapter=new MyAppItemRecyclerViewAdapter(filteredApps, mListener);
                    recyclerView.setAdapter(mAppsAdapter);
                    mAppsAdapter.notifyDataSetChanged();
                    return false;
                }
            });
            Log.v("Sorted list"," "+apps);

            int no_of_apps=apps.size();
            int [] viewTypes=new int[no_of_apps];
            char [] alphabet_index=new char[no_of_apps];
            String firstLetter="A";

            sortApps();
            mAppsAdapter=new MyAppItemRecyclerViewAdapter(filteredApps, mListener);
            recyclerView.setAdapter(mAppsAdapter);

        return view;
    }

    private void sortApps() {
        Collections.sort(filteredApps, new Comparator<App>() {
            @Override
            public int compare(App lhs, App rhs) {
                return lhs.getAppName().compareTo(rhs.getAppName());
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(App item);
    }
}
