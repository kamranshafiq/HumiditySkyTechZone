package com.humiditytemperature.skytechzone.adsutils;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.humiditytemperature.skytechzone.R;


public class AdmobAdsUtils {


    private static final String TAG = "LoadAds";
    public static AdmobAdsUtils instance;
    private Context context;
    private InterstitialAd mInterstitialAd;


    public AdmobAdsUtils(Context context) {

        this.context = context;
    }

    public static AdmobAdsUtils getInstance(Context context) {
        if (instance == null) {
            instance = new AdmobAdsUtils(context);

        }
        return instance;
    }


    public void initAds(Context context) {

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadInterstitialAd();
            }
        });


    }


    public void loadInterstitialAd() {

        String AD_UNIT_ID = context.getResources().getString(R.string.admob_interstitial_id);
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                context,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        AdmobAdsUtils.this.mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");


                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;


                    }
                });

    }


    public void showIntersitialAd(OnIntersitialAdListener onIntersitialAdListener) {

        if (mInterstitialAd != null) {

            mInterstitialAd.show((Activity) context);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    onIntersitialAdListener.onAdFailded();
                    AdmobAdsUtils.this.mInterstitialAd = null;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    onIntersitialAdListener.onAdDismis();
                    AdmobAdsUtils.this.mInterstitialAd = null;
                    Log.d("TAG", "The ad was dismissed.");


                    loadInterstitialAd();
                }
            });


        } else {
            onIntersitialAdListener.onAdFailded();
            loadInterstitialAd();
        }
    }



}
