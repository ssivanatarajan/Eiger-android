package shivtech.eiger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.db.DataBaseHandler;
import shivtech.eiger.models.App;
import shivtech.eiger.models.User;

public class AppProfile extends AppCompatActivity {
TextView apptower,appCategory,appAlias,appteam,appPrimaryres,appSecondaryRes,appSupportLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        apptower=(TextView)findViewById(R.id.Ap_app_tower);
        appCategory=(TextView)findViewById(R.id.Ap_app_category);
        appAlias=(TextView)findViewById(R.id.Ap_app_alias);
        appteam=(TextView)findViewById(R.id.Ap_app_team);
        appPrimaryres=(TextView)findViewById(R.id.Ap_app_primary);
        appSecondaryRes=(TextView)findViewById(R.id.Ap_app_secondary);
        appSupportLevel=(TextView)findViewById(R.id.Ap_app_support_level);
        int appId=getIntent().getExtras().getInt("AppID");
        DBHandler dataBaseHandler=new DBHandler(getApplicationContext());
       final App app=dataBaseHandler.getAppDetails(appId);
        getSupportActionBar().setTitle(app.getAppName());

        apptower.setText(app.getAppTower());
        appCategory.setText(app.getAppCategorry());
        appAlias.setText(app.getAppAlias());
        appteam.setText(app.getAppTeam());
        appSupportLevel.setText(app.getAppSupportLevel());
        User primaryUser=app.getAppPrimaryRes();
        appPrimaryres.setText(primaryUser.getUserName());
        User secUser=app.getAppSecondaryRes().get(0);
        appSecondaryRes.setText(secUser.getUserName());

        ImageButton primaryCallbtn=(ImageButton)findViewById(R.id.Ap_app_primary_Call_btn);
        ImageButton primarySMSbtn=(ImageButton)findViewById(R.id.Ap_app_primary_msg_btn);
        ImageButton secondaryCallbtn=(ImageButton)findViewById(R.id.Ap_app_secondary_call_btn);
        ImageButton secondarySMSbtn=(ImageButton)findViewById(R.id.Ap_app_secondary_msg_btn);

        primaryCallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number= Uri.parse("tel:"+app.getAppPrimaryRes().getUserMobile());
                Intent callIntent=new Intent(Intent.ACTION_DIAL,number);
                startActivity(callIntent);

            }
        });

        primarySMSbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", app.getAppPrimaryRes().getUserMobile());
               // smsIntent.putExtra("sms_body","Body of Message");
                startActivity(smsIntent);

            }
        });

        secondaryCallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number= Uri.parse("tel:"+app.getAppSecondaryRes().get(0).getUserMobile());
                Intent callIntent=new Intent(Intent.ACTION_DIAL,number);
                startActivity(callIntent);
            }
        });

        secondarySMSbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", app.getAppSecondaryRes().get(0).getUserMobile());
                // smsIntent.putExtra("sms_body","Body of Message");
                startActivity(smsIntent);
            }
        });

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
