package shivtech.eiger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.models.Team;
import shivtech.eiger.models.User;
import shivtech.eiger.utils.Constants;

public class UserProfile extends AppCompatActivity {

    public static User user;
    TextView username, summary, tower, manager, mobile, tcsEmail, projEmail, dob, bloodgroup, prgrm_langs, tools;
    ListView primaryApps, secApps, teams;
    /* String username,summary,tower,manager,mobile,tcsEmail,projEmail,dob,bloodgroup,prgrm_langs,tools;
     ArrayList<App> primaryApps,secApps;
     ArrayList<Team>teams;*/
    TextView UPT_usersummary, UPT_userTower, UPT_userManager, UPT_userTeams;
    SharedPreferences sharedPreferences;

    boolean isCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        int currentuser = sharedPreferences.getInt(Constants.sp_cur_user_empId, 0);
        DBHandler dbHandler = new DBHandler(this);
        int empid = getIntent().getExtras().getInt("EmpID");
        if (empid == currentuser)
            isCurrentUser = true;
        invalidateOptionsMenu();
        user = dbHandler.getUserdetails(empid);
        getSupportActionBar().setTitle(user.getUserName());
        ViewPager viewPager = (ViewPager) findViewById(R.id.UPT_viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.UPT_tabs);
        tabLayout.setupWithViewPager(viewPager);
        final ImageButton user_callbtn = (ImageButton) findViewById(R.id.UPT_user_Call_btn);
        ImageButton user_smsbtn = (ImageButton) findViewById(R.id.UPT_user_msg_btn);
        final String usermobile = user.getUserMobile();
        UPT_usersummary = (TextView) findViewById(R.id.UPT_user_summary);
        UPT_userTower = (TextView) findViewById(R.id.UPT_tower);
        UPT_userManager = (TextView) findViewById(R.id.UPT_manager);
        UPT_userManager.setText(user.getTowerManager());
        UPT_userTower.setText(user.getUserTowerName());
        UPT_usersummary.setText(user.getSummary());
        UPT_userTeams = (TextView) findViewById(R.id.UPT_teams);
        ArrayList<Team> teams = user.getUserTeams();
        String userteams = "";
        if (teams != null && teams.size() > 0) {
            for (int i = 0; i < teams.size(); i++) {
                if (i != teams.size() - 1)
                    userteams += teams.get(i).getTeamName() + " | ";
                else
                    userteams += teams.get(i).getTeamName();
            }
            UPT_userTeams.setText(userteams);
        }
        Log.e("user summary ,tower", user.getSummary() + " " + user.getUserTowerName() + " " + user.getTowerManager());

        if (usermobile != null) {
            user_callbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (usermobile != null) {
                        Uri number = Uri.parse("tel:" + usermobile);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(callIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Mobile number not added", Toast.LENGTH_LONG).show();
                    }

                }
            });

            user_smsbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (usermobile != null) {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", usermobile);
                        // smsIntent.putExtra("sms_body","Body of Message");
                        startActivity(smsIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Mobile number not added", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
       /* username = (TextView) findViewById(R.id.UP_user_name);
        summary = (TextView) findViewById(R.id.UP_user_summary);
        tower = (TextView) findViewById(R.id.UP_tower);
        manager = (TextView) findViewById(R.id.UP_manager);
        mobile = (TextView) findViewById(R.id.UP_user_mobile);
        tcsEmail = (TextView) findViewById(R.id.UP_user_tcs_email);
        dob = (TextView) findViewById(R.id.UP_user_dob);
        bloodgroup = (TextView) findViewById(R.id.UP_user_bloodgrp);
        projEmail = (TextView) findViewById(R.id.UP_user_proj_email);
        prgrm_langs = (TextView) findViewById(R.id.UP_prgm_langs);
        tools = (TextView) findViewById(R.id.UP_tools);

        primaryApps = (ListView) findViewById(R.id.UP_primaryappsList);
        secApps = (ListView) findViewById(R.id.UP_secappsList);
        teams = (ListView) findViewById(R.id.UP_team_list);
        Log.e("Users profile details",user.getUserName() +" "+user.getSummary()+" "+user.getUserMobile()+" "+user.getUserTowerName()
                +""+user.getBloodGroup()+" "+user.getTcsEmail()+" "+user.getTowerManager()+" "+user.getDob()+" "+
                user.getProgramming_langs()+" "+user.getTools());
        Log.e("user profile list sizes","teams "+user.getUserTeams().size()+"pri apps"+user.getPrimaryApps().size()
                +"sec apps"+user.getSecondaryApps().size());

        ArrayList<Team> userTeams=user.getUserTeams();
        String []teamnames=new String[userTeams.size()];
        for(int i=0;i<userTeams.size();i++)
        {
            teamnames[i]=userTeams.get(i).getTeamName();
        }
        final String usermobile=user.getUserMobile();
        username.setText(user.getUserName());
        summary.setText(user.getSummary());
        tower.setText(user.getUserTowerName());
        manager.setText(user.getTowerManager());
        mobile.setText(usermobile);
        tcsEmail.setText(user.getTcsEmail());
        projEmail.setText(user.getProjectEmail());
        dob.setText(user.getDob());
        bloodgroup.setText(user.getBloodGroup());
        prgrm_langs.setText(user.getProgramming_langs());
        tools.setText(user.getTools());
        ArrayAdapter<String> teamsAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,teamnames);
        teams.setAdapter(teamsAdapter);
        ArrayList<App> primaryappsList,secappsList;
        primaryappsList=user.getPrimaryApps();
        PriSecAppsAdapter primaryAppsAdapter=new PriSecAppsAdapter(UserProfile.this,primaryappsList);
        primaryApps.setAdapter(primaryAppsAdapter);
        secappsList=user.getSecondaryApps();
        PriSecAppsAdapter secondaryAppsAdapter=new PriSecAppsAdapter(UserProfile.this,secappsList);
        secApps.setAdapter(secondaryAppsAdapter);
        if(usermobile.trim().length()>0)
        {
            ImageButton user_callbtn=(ImageButton)findViewById(R.id.UP_user_Call_btn);
            ImageButton user_smsbtn=(ImageButton)findViewById(R.id.UP_user_msg_btn);
            user_callbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri number= Uri.parse("tel:"+usermobile);
                    Intent callIntent=new Intent(Intent.ACTION_DIAL,number);
                    startActivity(callIntent);

                }
            });

            user_smsbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", usermobile);
                    // smsIntent.putExtra("sms_body","Body of Message");
                    startActivity(smsIntent);

                }
            });


        }
        else
        {
            RelativeLayout usermobLayout=(RelativeLayout)findViewById(R.id.User_mobile_layout);
            usermobLayout.setVisibility(View.GONE);
        }*/

       /* Object objArray[];
        objArray = user.getPrimaryApps().toArray();
        setListAdapter(objArray,primaryApps);
        objArray = user.getSecondaryApps().toArray();
        setListAdapter(objArray,secApps);
        objArray = user.getUserTeams().toArray();
        setListAdapter(objArray,teams);*/


    }

    /*
        public void setListAdapter(Object[] objArray,ListView listview)
        {
            String values[] = Arrays.copyOf(objArray, objArray.length, String[].class);
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,values);
            listview.setAdapter(adapter);

        }
    */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserProfile_Apps(), "Apps");
        adapter.addFragment(new UserProfile_about(), "About");


        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        if (!isCurrentUser)
            menu.findItem(R.id.edit_profile).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent edit_profileIntent = new Intent(UserProfile.this, EditProfile.class);

                startActivity(edit_profileIntent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
