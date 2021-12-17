package com.humiditytemperature.skytechzone.db;

import android.content.Context;
import android.content.SharedPreferences;



public class PreferencesOffline {

    private static final String SP_NAME = "com.humiditytemperature.skytechzone.prefs";
    static final String IS_TERMS_ACCEPT = "IS_TERMS_ACCEPT";
    private static final int US_MCC_310 = 310;
    private static final int US_MCC_311 = 311;
    private static final int US_MCC_316 = 316;
    protected boolean autoCommit;
    protected boolean isUS;
    private SharedPreferences.Editor lazyEditor;
    private String prefSuffix;
    private SharedPreferences sp;

    public PreferencesOffline(Context ct) {
        this(ct, "", true);
    }

    public PreferencesOffline(Context ct, String prefSuffix2) {
        this(ct, prefSuffix2, true);
    }

    public PreferencesOffline(Context ct, String prefSuffix2, boolean autoCommit2) {
        boolean z = false;
        this.autoCommit = true;
        this.isUS = false;
        this.prefSuffix = "";
        this.sp = ct.getSharedPreferences(SP_NAME + prefSuffix2, 0);
        int mcc = ct.getResources().getConfiguration().mcc;
        this.isUS = (mcc == US_MCC_310 || mcc == US_MCC_311 || mcc == US_MCC_316) ? true : z;
        this.prefSuffix = prefSuffix2;
        this.autoCommit = autoCommit2;
    }








    public static double toFahrenheit(double celsius) {
        return (1.7999999523162842d * celsius) + 32.0d;
    }


    public SharedPreferences getSp() {
        if (!this.autoCommit) {
            this.lazyEditor = this.sp.edit();
        }
        return this.sp;
    }


    public SharedPreferences.Editor getEditor() {
        SharedPreferences sp2 = getSp();
        if (!this.autoCommit) {
            return this.lazyEditor;
        }
        return sp2.edit();
    }



    public void commit(SharedPreferences.Editor edit) {
        edit.apply();
    }




    public int getBackgroundColor() {
        return getSp().getInt("settings_bg_color", -10157825);
    }

    public void setBackgroundColor(int val) {
        SharedPreferences.Editor edit = getEditor();
        edit.putInt("settings_bg_color", val);
        if (this.autoCommit) {
            edit.apply();
        }
    }

    public boolean isSingleColor() {
        return getSp().getBoolean("settings_one_color", false);
    }

    public void setSingleColor(boolean val) {
        SharedPreferences.Editor edit = getEditor();
        edit.putBoolean("settings_one_color", val);
        if (this.autoCommit) {
            edit.apply();
        }
    }


    public static boolean IsTermsAccept(Context context) {
        return context.getApplicationContext().getSharedPreferences(SP_NAME, 0).getBoolean(IS_TERMS_ACCEPT, false);
    }

    public static void setIsTermsAccept(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(SP_NAME, 0).edit();
        edit.putBoolean(IS_TERMS_ACCEPT, z);
        edit.commit();

    }

}
