package com.humiditytemperature.skytechzone.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.humiditytemperature.skytechzone.adsutils.AdmobAdsUtils;
import com.humiditytemperature.skytechzone.adsutils.OnIntersitialAdListener;
import com.humiditytemperature.skytechzone.utils.BitmapsDrawable;
import com.humiditytemperature.skytechzone.R;
import com.humiditytemperature.skytechzone.databinding.ActivityDashboardBinding;
import com.humiditytemperature.skytechzone.db.PreferencesOffline;

public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding activityDashboardBinding;
    PreferencesOffline prefs = null;
    private FrameLayout adContainerView;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        activityDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        if (this.prefs == null) {
            this.prefs = new PreferencesOffline(this);

        }
        switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(prefs.getBackgroundColor(), DashboardActivity.this), prefs.getBackgroundColor());
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdmobAdsUtils.getInstance(DashboardActivity.this).initAds(DashboardActivity.this);
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        adContainerView.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        activityDashboardBinding.humidityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmobAdsUtils.getInstance(DashboardActivity.this).showIntersitialAd(new OnIntersitialAdListener() {
                    @Override
                    public void onAdFailded() {
                        startActivity(new Intent(DashboardActivity.this, HumidityActivity.class));

                    }

                    @Override
                    public void onAdDismis() {
                        startActivity(new Intent(DashboardActivity.this, HumidityActivity.class));

                    }
                });
            }
        });

        activityDashboardBinding.setttingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmobAdsUtils.getInstance(DashboardActivity.this).showIntersitialAd(new OnIntersitialAdListener() {
                    @Override
                    public void onAdFailded() {
                        startActivity(new Intent(DashboardActivity.this, SettingActivity.class));

                    }

                    @Override
                    public void onAdDismis() {
                        startActivity(new Intent(DashboardActivity.this, SettingActivity.class));

                    }
                });
            }
        });

        activityDashboardBinding.weatherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmobAdsUtils.getInstance(DashboardActivity.this).showIntersitialAd(new OnIntersitialAdListener() {
                    @Override
                    public void onAdFailded() {
                        startActivity(new Intent(DashboardActivity.this, WeatherActivity.class));

                    }

                    @Override
                    public void onAdDismis() {
                        startActivity(new Intent(DashboardActivity.this, WeatherActivity.class));

                    }
                });
            }
        });

        activityDashboardBinding.phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmobAdsUtils.getInstance(DashboardActivity.this).showIntersitialAd(new OnIntersitialAdListener() {
                    @Override
                    public void onAdFailded() {
                        startActivity(new Intent(DashboardActivity.this, MobileActivity.class));

                    }

                    @Override
                    public void onAdDismis() {
                        startActivity(new Intent(DashboardActivity.this, MobileActivity.class));

                    }
                });
            }
        });

        activityDashboardBinding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, DisclousreActivity.class));

            }
        });
        activityDashboardBinding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void switchBackground(final Drawable newBg, final int baseColor) {

        activityDashboardBinding.bgBottom.setBackgroundDrawable(newBg);

        ObjectAnimator a1 = ObjectAnimator.ofFloat(activityDashboardBinding.bgTop, "alpha", 1.0f, 0.0f);
        a1.setDuration(500L);
        a1.addListener(new Animator.AnimatorListener() {


            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                activityDashboardBinding.bgTop.setBackgroundDrawable(newBg);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        ObjectAnimator a2 = ObjectAnimator.ofFloat(activityDashboardBinding.bgBottom, "alpha", 0.0f, 1.0f);
        a2.setDuration(500L);
        a1.start();
        a2.start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        if (this.prefs == null) {
            this.prefs = new PreferencesOffline(this);

        }
        switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(prefs.getBackgroundColor(), DashboardActivity.this), prefs.getBackgroundColor());

    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }



    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void loadBanner() {
        // Create an ad request.
        adView = new AdView(this);
        adView.setAdUnitId(this.getResources().getString(R.string.admob_banner_ad_unit_id));
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onStart() {
        if (!PreferencesOffline.IsTermsAccept(this)) {
            startActivity(new Intent(this, DisclousreActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }

        super.onStart();
    }

}