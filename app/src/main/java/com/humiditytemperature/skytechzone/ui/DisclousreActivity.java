package com.humiditytemperature.skytechzone.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.humiditytemperature.skytechzone.R;
import com.humiditytemperature.skytechzone.db.PreferencesOffline;

public class DisclousreActivity extends AppCompatActivity {
    public static String strPrivacyUri = "https://sites.google.com/view/skytechzone/home";
    public static String strTermsUri = "https://sites.google.com/view/skytechzone/home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclousre);

        findViewById(R.id.tvPrivacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriparse(strPrivacyUri);
            }
        });
        findViewById(R.id.tvAgreement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeAndContinueDialog();
            }
        });

        findViewById(R.id.tvTerms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriparse(strTermsUri);
            }
        });

        findViewById(R.id.addeptContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToMainScreen();
            }
        });
    }

    public void uriparse(String str) {
        String str2 = "android.intent.action.VIEW";
        Intent intent = new Intent(str2, Uri.parse(str));
        intent.addFlags(1208483840);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            startActivity(new Intent(str2, Uri.parse(strPrivacyUri)));
        }
    }

    private void GoToMainScreen() {
        PreferencesOffline.setIsTermsAccept(this, true);

        startActivity(new Intent(this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        finish();


    }

    public void agreeAndContinueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "We would like to inform you regarding the 'Consent to Collection and Use Of Data'\n\n To get humidity and weather data upon your current location, allow access to location permission.\n\nWe never share your location data we just fetch details of weather.");
        builder.setCancelable(false);
        builder.setPositiveButton((CharSequence) "Ok", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }
}