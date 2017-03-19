package shivtech.eiger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.digits.sdk.android.DigitsAuthButton;

import shivtech.eiger.utils.Constants;

public class VerifyMobile extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String phoneNumber = intent.getStringExtra(Constants.EXTRA_PHONE_NUMBER);
            Intent signupintent = new Intent(VerifyMobile.this, Signup.class);
            Log.e("registered mobile", phoneNumber);
            startActivity(signupintent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_verify_mobile);
        final EditText mobileNo = (EditText) findViewById(R.id.VM_mobile);
        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(((Eiger) getApplication()).getAuthCallback());
        digitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("digits", "click");
                String mobileno = mobileNo.getText().toString().trim();
                if (mobileno.length() == 13)
                    ((Eiger) getApplication()).authMobile(mobileno);
                else
                    mobileNo.setError("Invalid Mobile No");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(VerifyMobile.this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.verifyIntentFilter));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(VerifyMobile.this).unregisterReceiver(broadcastReceiver);
    }
}
