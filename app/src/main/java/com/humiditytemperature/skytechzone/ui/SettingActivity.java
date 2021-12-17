package com.humiditytemperature.skytechzone.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import com.humiditytemperature.skytechzone.adsutils.AdmobAdsUtils;
import com.humiditytemperature.skytechzone.adsutils.OnIntersitialAdListener;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.humiditytemperature.skytechzone.utils.BitmapsDrawable;
import com.humiditytemperature.skytechzone.R;
import com.humiditytemperature.skytechzone.utils.CustomTemperatureType;
import com.humiditytemperature.skytechzone.databinding.ActivitySettingBinding;
import com.humiditytemperature.skytechzone.db.PreferencesOffline;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    public static final double[] STEPS_C = {-10.0d, 0.0d, 6.0d, 14.0d, 23.0d, 28.0d};
    public static final double[] STEPS_F = {PreferencesOffline.toFahrenheit(STEPS_C[0]), PreferencesOffline.toFahrenheit(STEPS_C[1]), PreferencesOffline.toFahrenheit(STEPS_C[2]), PreferencesOffline.toFahrenheit(STEPS_C[3]), PreferencesOffline.toFahrenheit(STEPS_C[4]), PreferencesOffline.toFahrenheit(STEPS_C[5])};
    int currentBg = R.drawable.bg_offline;
    int currentBgColor = -10157825;
    View settingsPanel;

    private ActivitySettingBinding mBinding;
    RadioButton radioCelsius;
    RadioButton radioFahrenheit;

    CheckBox bgColor;
    double temp = -2.147483648E9d;



    public int tempUnit = 101;



    PreferencesOffline prefs = null;
    boolean isCelcius=true;
    private FrameLayout adContainerView;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_setting);
        this.isCelcius = decideLocaleSign() == CustomTemperatureType.CELCIUS;
        this.settingsPanel = mBinding.settingPanal;

        adContainerView = findViewById(R.id.ad_view_container);

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        adContainerView.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        radioCelsius=mBinding.settingCelsius;
       radioFahrenheit=mBinding.settingFahrenheit;

       if (isCelcius){
           radioCelsius.setChecked(true);
           radioFahrenheit.setChecked(false);
       }else {
           radioCelsius.setChecked(false);
           radioFahrenheit.setChecked(true);
       }

        bgColor=mBinding.settingColor;

        mBinding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        this.radioCelsius.setOnCheckedChangeListener(null);
        this.radioFahrenheit.setOnCheckedChangeListener(null);



        if (this.prefs == null) {
            this.prefs = new PreferencesOffline(this);
            int color2 = prefs.getBackgroundColor();
            switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(color2, SettingActivity.this), color2);

        }else {
            int color2 = prefs.getBackgroundColor();
            switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(color2, SettingActivity.this), color2);
        }


        this.bgColor.setChecked(this.prefs.isSingleColor());

        prefs.setSingleColor(true);
        switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(prefs.getBackgroundColor(), SettingActivity.this), prefs.getBackgroundColor());
        if (mBinding.pickerSample.getBackground() != null) {
            ((GradientDrawable) mBinding.pickerSample.getBackground()).setColor(prefs.getBackgroundColor());
        }



        this.bgColor.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (!bgColor.isChecked()) {
                    prefs.setSingleColor(false);
                    int color = getColorForTemperature(temp);
                    switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(color, SettingActivity.this), color);
                } else if (prefs.getBackgroundColor() == -10157825) {
                    hideSettings();

                    showPicker();
                } else {
                    int color2 = prefs.getBackgroundColor();
                    switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(color2, SettingActivity.this), color2);
                    currentBg = R.drawable.bg_offline;
                    prefs.setSingleColor(true);
                }
            }
        });
        ((GradientDrawable) mBinding.pickerSample.getBackground().mutate()).setColor(this.prefs.getBackgroundColor() == -10157825 ? getColorForTemperature(this.temp) : this.prefs.getBackgroundColor());
        mBinding.pickerSample.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AdmobAdsUtils.getInstance(SettingActivity.this).showIntersitialAd(new OnIntersitialAdListener() {
                    @Override
                    public void onAdFailded() {
                        hideSettings();
                        showPicker();
                    }

                    @Override
                    public void onAdDismis() {
                        hideSettings();
                        showPicker();
                    }
                });

            }
        });


        CompoundButton.OnCheckedChangeListener radioCelsiusListener = new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    storeUnitType("C");
                } else {
                    storeUnitType("F");
                }
            }
        };
        this.radioCelsius.setOnCheckedChangeListener(radioCelsiusListener);
        CompoundButton.OnCheckedChangeListener radioFahrenheitListener = new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    storeUnitType("F");
                } else {
                    storeUnitType("C");
                }
            }
        };
        this.radioFahrenheit.setOnCheckedChangeListener(radioFahrenheitListener);



    }
    public void showSettings() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this.settingsPanel, "alpha", 0.0f, 1.0f);
        alpha.setDuration(300L);
        alpha.setInterpolator(new DecelerateInterpolator());
        alpha.addListener(new Animator.AnimatorListener() {


            @Override
            public void onAnimationStart(Animator animator) {
              settingsPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        alpha.start();
    }
    public void showPicker() {
        int color;
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.pickerPanel, "alpha", 0.0f, 1.0f);
        alpha.setDuration(300L);
        alpha.setInterpolator(new DecelerateInterpolator());
        alpha.addListener(new Animator.AnimatorListener() {


            @Override
            public void onAnimationStart(Animator animator) {
                mBinding.pickerPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        alpha.start();
        final ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
        if (this.prefs.isSingleColor()) {
            color = this.prefs.getBackgroundColor();
        } else {
            color = getColorForTemperature(this.temp);
        }
        picker.setColor(color);
        picker.setOldCenterColor(color);
        picker.setNewCenterColor(color);
        picker.addSVBar((SVBar) findViewById(R.id.svbar));
        picker.addOpacityBar((OpacityBar) findViewById(R.id.opacitybar));
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {


            @Override
            public void onColorChanged(int i) {
                Log.d("TAG", "onColorChanged: "+i);
                switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(i, SettingActivity.this), i);
            }
        });

        findViewById(R.id.picker_save).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
               prefs.setSingleColor(true);
               prefs.setBackgroundColor(picker.getColor());
               switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(picker.getColor(), SettingActivity.this), picker.getColor());
               bgColor.setChecked(true);
                if (mBinding.pickerSample.getBackground() != null) {
                    ((GradientDrawable) mBinding.pickerSample.getBackground()).setColor(picker.getColor());
                }
                hidePicker();
            }
        });
        findViewById(R.id.picker_cancel).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (prefs.isSingleColor()) {
                    int color = prefs.getBackgroundColor();
                    switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(color, SettingActivity.this), color);
                } else {
                    int color2 = getColorForTemperature(SettingActivity.this.temp);
                    switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(color2, SettingActivity.this), color2);
                }
                bgColor.setChecked(prefs.isSingleColor());
               hidePicker();
            }
        });


    }
    public void hidePicker() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.pickerPanel, "alpha", 1.0f, 0.0f);
        alpha.setDuration(300L);
        alpha.setInterpolator(new AccelerateInterpolator());
        alpha.addListener(new Animator.AnimatorListener() {


            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                showSettings();
                mBinding.pickerPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        alpha.start();
    }
    public void hideSettings() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this.settingsPanel, "alpha", 1.0f, 0.0f);
        alpha.setDuration(300L);
        alpha.setInterpolator(new AccelerateInterpolator());
        alpha.addListener(new Animator.AnimatorListener() {


            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mBinding.settingPanal.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        alpha.start();


    }

    public void switchBackground(final Drawable newBg, final int baseColor) {

            mBinding.bgBottom.setBackgroundDrawable(newBg);
            ObjectAnimator a1 = ObjectAnimator.ofFloat(mBinding.bgTop, "alpha", 1.0f, 0.0f);
            a1.setDuration(500L);
            a1.addListener(new Animator.AnimatorListener() {


                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    currentBgColor = baseColor;
                    mBinding.bgTop.setBackgroundDrawable(newBg);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            ObjectAnimator a2 = ObjectAnimator.ofFloat(mBinding.bgBottom, "alpha", 0.0f, 1.0f);
            a2.setDuration(500L);
            a1.start();
            a2.start();

    }


    public int getColorForTemperature(double temp2) {
        if (temp2 == 999999.0d || temp2 == -2.147483648E9d) {
            return -10157825;
        }
        int color = R.color.hot3;
        double[] STEPS = STEPS_C;
        if (this.tempUnit == 102) {
            STEPS = STEPS_F;
        }
        if (temp2 < STEPS[5]) {
            color = R.color.hot2;
        }
        if (temp2 < STEPS[4]) {
            color = R.color.hot1;
        }
        if (temp2 < STEPS[3]) {
            color = R.color.neutral;
        }
        if (temp2 < STEPS[2]) {
            color = R.color.cold1;
        }
        if (temp2 < STEPS[1]) {
            color = R.color.cold2;
        }
        if (temp2 < STEPS[0]) {
            color = R.color.cold3;
        }
        return getResources().getColor(color);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.pause();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.pause();
        }
    }

    public void storeUnitType(String str) {
        SharedPreferences.Editor edit = getSharedPreferences("com.humiditytemperature.skytechzone", 0).edit();
        edit.putString("UNIT_TYPE", str);
        edit.apply();
    }

    private CustomTemperatureType decideLocaleSign() {
        String storedUnitType = getStoredUnitType();
        return storedUnitType == null ? "US".equals(Locale.getDefault().getCountry()) ? CustomTemperatureType.FAHRENAYT : CustomTemperatureType.CELCIUS : storedUnitType.equals("C") ? CustomTemperatureType.CELCIUS : CustomTemperatureType.FAHRENAYT;
    }

    private String getStoredUnitType() {
        return getSharedPreferences("com.humiditytemperature.skytechzone", 0).getString("UNIT_TYPE", null);
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
}