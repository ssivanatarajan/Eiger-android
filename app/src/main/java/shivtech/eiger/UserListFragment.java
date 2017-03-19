package shivtech.eiger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.models.User;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    ListView listView;
    SearchView searchView;
    ArrayList<User> users, filteredUsers;
    UserListAdapter mUsersAdapter;
    boolean firstLoad;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private Context mContext;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserListFragment newInstance(int columnCount) {
        UserListFragment fragment = new UserListFragment();
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
        View view = inflater.inflate(R.layout.fragment_userlist, container, false);
        listView = (ListView) view.findViewById(R.id.userList);
        searchView = (SearchView) view.findViewById(R.id.userlist_searchview);
        // jsonView=(TextView)view.findViewById(R.id.jsonresponse);
        mContext = getContext();
        DBHandler dbHandler = new DBHandler(getContext());
        users = dbHandler.getUsers();
        filteredUsers = users;
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        listView.setFastScrollEnabled(true);
        View empty_list_view = LayoutInflater.from(getContext()).inflate(R.layout.empty_list_view, null);
        listView.setEmptyView(empty_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Intent userProfIntent = new Intent(mContext, UserProfile.class);
                TextView userIdview = (TextView) view.findViewById(R.id.user_id);
                int empId = Integer.parseInt(userIdview.getText().toString());
                userProfIntent.putExtra("EmpID", empId);
                Log.i("itemclick", "itemclick");
                mContext.startActivity(userProfIntent);
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.i("searchview onfocus", "in");
                if (firstLoad) {
                    Log.i("firstLoad-searchview", "true");
                    searchView.clearFocus();
                    firstLoad = false;
                }

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 0) {
                    query = query.toLowerCase();
                    filteredUsers = new ArrayList<User>();
                    for (User user : users) {
                       /* if (user.getUserMobile() != null) {
                            if (user.getUserName().toLowerCase().contains(query) )
                                filteredUsers.add(user);
                        }
                        else*/
                        if (user.getUserName().toLowerCase().contains(query))
                            filteredUsers.add(user);

                    }
                } else
                    filteredUsers = users;
                sortUsers();
                mUsersAdapter = new UserListAdapter(getContext(), filteredUsers);
                listView.setAdapter(mUsersAdapter);

                mUsersAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query.trim().length() > 0) {
                    query = query.toLowerCase();
                    filteredUsers = new ArrayList<User>();
                    for (User user : users) {
                        Log.d("username", user.getUserName());
                        /*if (app.getAppAlias() != null) {
                            if (app.getAppName().toLowerCase().contains(query) || app.getAppAlias().toLowerCase().contains(query))
                                filteredApps.add(app);
                        } else*/
                        if (user.getUserName().toLowerCase().contains(query))
                            filteredUsers.add(user);

                    }
                } else
                    filteredUsers = users;
                sortUsers();
                mUsersAdapter = new UserListAdapter(getContext(), filteredUsers);
                listView.setAdapter(mUsersAdapter);

                mUsersAdapter.notifyDataSetChanged();
                return false;
            }
        });
        mUsersAdapter = new UserListAdapter(getContext(), filteredUsers);
        listView.setAdapter(mUsersAdapter);


        return view;
    }

    private void sortUsers() {
        Collections.sort(filteredUsers, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getUserName().compareTo(rhs.getUserName());
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
        void onListFragmentInteraction(User item);
    }
}
