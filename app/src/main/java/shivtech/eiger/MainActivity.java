package shivtech.eiger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.models.App;
import shivtech.eiger.models.User;
import shivtech.eiger.utils.Constants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AppListFragment.OnFragmentInteractionListener, UserListFragment.OnListFragmentInteractionListener,
        AdminAppManagementFragment.OnFragmentInteractionListener, AdminUserManagementFragment.OnFragmentInteractionListener,
        Postfragment.OnFragmentInteractionListener
{
    public static final String AUTHORITY = "shivtech.eiger.datasync";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "shivtech.eiger.datasync";
    // The account name
    public static final int SYNC_FREQUENCY = 300;
    public static final String ACCOUNT = "Eiger";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sp_editor;
    NavExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    // Instance fields
    Account mAccount;
    ContentResolver mResolver;
    NavigationView navigationView;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(
                    newAccount, AUTHORITY, new Bundle(), SYNC_FREQUENCY);

        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAccount = CreateSyncAccount(getApplicationContext());
        mResolver = getContentResolver();

        mResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, 300);
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                mAccount,      // Sync account
                AUTHORITY, // Content authority
                b);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        sp_editor = sharedPreferences.edit();
        DBHandler dbHandler = new DBHandler(getApplicationContext());
        final int int_empid = sharedPreferences.getInt(Constants.sp_cur_user_empId, 0);
        Log.e("Curr emp id", int_empid + "");
        String loggedinUsername = dbHandler.getUsername(int_empid);
        sp_editor.putString(Constants.sp_cur_user_name, loggedinUsername);
        Log.e("logged in user", loggedinUsername);
        Constants.currentUserName = loggedinUsername;

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setCheckable(true);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));


        View mHeaderView = navigationView.getHeaderView(0);
        TextView nav_bar_title = (TextView) mHeaderView.findViewById(R.id.nav_header_title);

        if (loggedinUsername.trim().length() > 0)
            nav_bar_title.setText(loggedinUsername);
        else
            nav_bar_title.setText("Eiger");
        nav_bar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userprofile = new Intent(MainActivity.this, UserProfile.class);
                userprofile.putExtra("EmpID", int_empid);
                startActivity(userprofile);
            }
        });
        String userRole = sharedPreferences.getString(Constants.userRole, "U");
        if (userRole.equals("A"))
            enableExpandableList();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void enableExpandableList() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        expListView = (ExpandableListView) findViewById(R.id.left_drawer);

        prepareListData(listDataHeader, listDataChild);
        listAdapter = new NavExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                    }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                // Temporary code:

                // till here
                Fragment fragment = null;
                if (groupPosition == 0) {
                    //admin
                    if (childPosition == 0) {
                        fragment = new AdminUserManagementFragment();
                    } else if (childPosition == 1) {
                        fragment = new AdminAppManagementFragment();
                    }


                }


                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return false;
            }
        });
    }

    private void prepareListData(List<String> listDataHeader, Map<String,
            List<String>> listDataChild) {


        // Adding headings
        listDataHeader.add("Admin");

        // Adding child data
        List<String> top = new ArrayList<String>();
        top.add("User Management");
        top.add("App Management");


        listDataChild.put(listDataHeader.get(0), top); // Header, Child data
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_apps) {
            fragment = new AppListFragment();
            // Handle the camera action
        } else if (id == R.id.nav_people) {
            fragment = new UserListFragment();
        }
        else if(id==R.id.nav_posts)
            fragment=new Postfragment();


        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(User item) {

    }
}
