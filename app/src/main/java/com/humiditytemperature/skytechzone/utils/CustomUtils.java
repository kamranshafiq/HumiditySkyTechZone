package com.humiditytemperature.skytechzone.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

public class CustomUtils {
    private final Context context;
    public CustomUtils(Context context2) {
        this.context = context2;
    }


    public static boolean isArabic() {
        return Resources.getSystem().getConfiguration().locale.getLanguage().equals("ar");
    }

    public static boolean isInternetConnectionAvailable(Context context) {
        NetworkInfo activeNetworkInfo;
        return context != null && (activeNetworkInfo = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()) != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
    }

    public void moreApps() {
        try {
            this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:DevSol+Technology")));
        } catch (ActivityNotFoundException unused) {
            this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/developer?id=")));
        }
    }

    public void rateApp() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + this.context.getPackageName()));
        if (Build.VERSION.SDK_INT >= 21) {
            intent.addFlags(1208483840);
        }
        try {
            this.context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Context context2 = this.context;
            context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + this.context.getPackageName())));
        }
    }

    public void shareApp() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "2131820573 (Open it in Google Play Store to Download the Application)");
        intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + this.context.getPackageName());
        this.context.startActivity(Intent.createChooser(intent, "Share via"));
    }
}
