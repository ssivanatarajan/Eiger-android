package shivtech.eiger;


import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 10-03-2017.
 */

public class Eiger extends Application {

    private AuthCallback authCallback;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);
        Digits.Builder digitsBuilder = new Digits.Builder().withTheme(R.style.CustomDigitsTheme);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), digitsBuilder.build());
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session
                Intent signup_intent = new Intent(Constants.verifyIntentFilter);
                Toast.makeText(getApplicationContext(), "Verified successfully", Toast.LENGTH_LONG).show();
                signup_intent.putExtra(Constants.EXTRA_PHONE_NUMBER, phoneNumber);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(signup_intent);
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
            }
        };

    }

    public void authMobile(String mobileno) {


        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback)
                .withPhoneNumber(mobileno);

        Digits.authenticate(authConfigBuilder.build());

    }

    public AuthCallback getAuthCallback() {
        return authCallback;
    }

}
