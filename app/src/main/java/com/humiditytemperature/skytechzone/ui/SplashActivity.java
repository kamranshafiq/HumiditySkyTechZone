package com.humiditytemperature.skytechzone.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.humiditytemperature.skytechzone.R;
import com.humiditytemperature.skytechzone.adsutils.AdmobAdsUtils;
import com.humiditytemperature.skytechzone.adsutils.MyApplication;
import com.humiditytemperature.skytechzone.db.PreferencesOffline;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AdmobAdsUtils.getInstance(this).initAds(this);
        thread();


    }


    private void thread() {



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Application application = getApplication();

                // If the application is not an instance of MyApplication, log an error message and
                // start the MainActivity without showing the app open ad.
                if (!(application instanceof MyApplication)) {

                    SplashActivity.this.GoToMainScreen();
                    return;
                }

                // Show the app open ad.
                ((MyApplication) application)
                        .showAdIfAvailable(
                                SplashActivity.this,
                                new MyApplication.OnShowAdCompleteListener() {
                                    @Override
                                    public void onShowAdComplete() {
                                        SplashActivity.this.GoToMainScreen();
                                    }
                                });
            }
        }, 4000);
    }

    public void GoToMainScreen() {


        if (PreferencesOffline.IsTermsAccept(this)) {


            startActivity(new Intent(this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


        } else {

            startActivity(new Intent(this, DisclousreActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


        }

    }
}