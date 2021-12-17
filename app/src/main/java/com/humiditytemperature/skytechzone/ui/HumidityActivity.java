package com.humiditytemperature.skytechzone.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.humiditytemperature.skytechzone.BuildConfig;
import com.humiditytemperature.skytechzone.adsutils.AdmobAdsUtils;
import com.humiditytemperature.skytechzone.adsutils.OnIntersitialAdListener;
import com.humiditytemperature.skytechzone.utils.BitmapsDrawable;
import com.humiditytemperature.skytechzone.utils.CustomDistanceType;
import com.humiditytemperature.skytechzone.R;
import com.humiditytemperature.skytechzone.utils.CustomTemperatureType;
import com.humiditytemperature.skytechzone.utils.CustomUtils;
import com.humiditytemperature.skytechzone.databinding.ActivityHumidityBinding;
import com.humiditytemperature.skytechzone.db.PreferencesOffline;
import com.humiditytemperature.skytechzone.models.OutsideWeatherModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HumidityActivity extends AppCompatActivity implements LocationListener {

    private  final float calibrationValueRange = (Build.VERSION.SDK_INT > 22 ? 3.4f : 1.3f);
    private  String cityName;
    private  String countryName;
    private  float feelsLike;
    private  Location mLastLocation;
    private  float outdoorHumidity;
    private  float outdoorTemperature;
    private  String pressure;
    private  String weatherDes;
    private  boolean weatherInfoSet;
    private  float wind;
    private OutsideWeatherModel outsideWeatherModel;
    ActivityHumidityBinding activityHumidityBinding;

    

    SharedPreferences sharedPreferences;
    public float calibrationValue;


    public boolean isCelcius;
    private LocationManager locationManager;

    PreferencesOffline prefs = null;
    private String sunrize;
    private String sunset;

    private FrameLayout adContainerView;
    private AdView adView;

    private String weatherId;

    private CustomDistanceType getDistnaceUnit() {
        return "US".equals(Locale.getDefault().getCountry()) ? CustomDistanceType.MILES : CustomDistanceType.KILOMETERS;
    }

    private CustomTemperatureType getLocalUnit() {
        String storedUnitType = getStoredUnitType();
        return storedUnitType == null ? "US".equals(Locale.getDefault().getCountry()) ? CustomTemperatureType.FAHRENAYT : CustomTemperatureType.CELCIUS : storedUnitType.equals("C") ? CustomTemperatureType.CELCIUS : CustomTemperatureType.FAHRENAYT;
    }

    private float getCelcius(float f) {
        return f - 273.15f;
    }

    private float getFahrenayt(float f) {
        return ((f * 9.0f) / 5.0f) + 32.0f;
    }

    private Location getLastKnownLocationData() {
        if (ActivityCompat.checkSelfPermission(HumidityActivity.this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(HumidityActivity.this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            return this.locationManager.getLastKnownLocation("network");
        }
        return null;
    }

    private String getStoredUnitType() {
        return HumidityActivity.this.getSharedPreferences("com.humiditytemperature.skytechzone", 0).getString("UNIT_TYPE", null);
    }


    private void dolocationStuff() {
        if (ContextCompat.checkSelfPermission(HumidityActivity.this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            this.locationManager = (LocationManager) HumidityActivity.this.getSystemService(Context.LOCATION_SERVICE);
            mLastLocation = getLastKnownLocationData();
            callWeatherData();
            doLocationListener();
        }
    }

    private void doLocationListener() {
        try {
            if ((ActivityCompat.checkSelfPermission(HumidityActivity.this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(HumidityActivity.this, "android.permission.ACCESS_COARSE_LOCATION") == 0) && this.locationManager.getAllProviders().contains("network")) {
                this.locationManager.requestLocationUpdates("network", 0, 0.0f, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        activityHumidityBinding= DataBindingUtil. setContentView(this,R.layout.activity_humidity);
        outsideWeatherModel = new OutsideWeatherModel();
        if (this.prefs == null) {
            this.prefs = new PreferencesOffline(this);

        }
        switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(prefs.getBackgroundColor(), HumidityActivity.this), prefs.getBackgroundColor());
        adContainerView = findViewById(R.id.ad_view_container);

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        adContainerView.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });
        if (getLocalUnit() == CustomTemperatureType.CELCIUS) {
            this.isCelcius = true;
        }
        HumidityActivity.this.getWindow().setFlags(1024, 1024);
         sharedPreferences = HumidityActivity.this.getSharedPreferences("com.humiditytemperature.skytechzone", 0);

        this.calibrationValue = sharedPreferences.contains("CALIBRATION_VALUE") ? this.sharedPreferences.getFloat("CALIBRATION_VALUE", 0.0f) : calibrationValueRange;
        requestLocationPermission();
        dolocationStuff();
        setTextData();

        activityHumidityBinding.lnlFragmentTodayRefresh.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                AdmobAdsUtils.getInstance(HumidityActivity.this).showIntersitialAd(new OnIntersitialAdListener() {
                    @Override
                    public void onAdFailded() {
                        HumidityActivity.this.outsideWeatherModel.setOutside(null);
                        dolocationStuff();
                    }

                    @Override
                    public void onAdDismis() {
                        HumidityActivity.this.outsideWeatherModel.setOutside(null);
                        dolocationStuff();
                    }
                });


            }
        });

        activityHumidityBinding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void showPermissionDilouge() {
        new AlertDialog.Builder(HumidityActivity.this).setTitle("Permission Disclousre").setMessage("We need location permission to get weather data according to your current location. we never share your location its just for fetching weather data ").setPositiveButton("Allow", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                if (ContextCompat.checkSelfPermission(HumidityActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    ActivityCompat.requestPermissions(HumidityActivity.this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 1000);
                }
            }
        }).setNegativeButton("No", (DialogInterface.OnClickListener) null).show();
    }


    private void storeweatherData() {
        CustomTemperatureType customTemperatureType = this.isCelcius ? CustomTemperatureType.CELCIUS : CustomTemperatureType.FAHRENAYT;
        if (weatherInfoSet) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                Object[] objArr = new Object[1];
                objArr[0] = Float.valueOf(this.isCelcius ? outdoorTemperature : getFahrenayt(outdoorTemperature));
                sb.append(String.format("%.1f", objArr));
                String replace = sb.toString().replace(",", ".");
                StringBuilder sb2 = new StringBuilder();
                sb2.append("");
                Object[] objArr2 = new Object[1];
                objArr2[0] = Float.valueOf(this.isCelcius ? feelsLike : getFahrenayt(feelsLike));
                sb2.append(String.format("%.1f", objArr2));
                sb2.append(customTemperatureType.sign());
                String replace2 = sb2.toString().replace(",", ".");
                CustomDistanceType getDistnaceUnit = getDistnaceUnit();
                StringBuilder sb3 = new StringBuilder();
                sb3.append("");
                double d = (double) wind;
                double value = getDistnaceUnit.value();
                Double.isNaN(d);
                sb3.append(String.format("%.1f", Double.valueOf(d * value)));
                sb3.append(getDistnaceUnit.sign());
                String sb4 = sb3.toString();
                if (CustomUtils.isArabic()) {
                    replace = replace.replace(Character.toString((char) 1643), ".");
                    replace2 = replace2.replace(Character.toString((char) 1643), ".");
                    sb4 = sb4.replace(Character.toString((char) 1643), ".");
                }
                outsideWeatherModel.setTempUnit(customTemperatureType.sign());
                outsideWeatherModel.setOutside(replace);
                outsideWeatherModel.setHumidity(String.valueOf(outdoorHumidity));
                outsideWeatherModel.setWindSpeed(sb4);
                outsideWeatherModel.setFeelsLike(replace2);

                outsideWeatherModel.setLocation(cityName + ", " + countryName);
                outsideWeatherModel.setPressure(pressure);
                outsideWeatherModel.setWeatherDes(weatherDes);
                outsideWeatherModel.setSunrise(this.sunrize);
                outsideWeatherModel.setSunset(this.sunset);
                String format = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm").format(Calendar.getInstance().getTime());
                SharedPreferences.Editor edit = HumidityActivity.this.getSharedPreferences("com.humiditytemperature.skytechzone", 0).edit();
                edit.putString("tempUnit", customTemperatureType.sign());
                storeUnitType("C");
                edit.putString("tempOutside", replace);
                edit.putString("humidity", String.valueOf(outdoorHumidity));
                edit.putString("wind", sb4);
                edit.putString("feelsLike", replace2);
                edit.putString("loc", cityName + ", " + countryName);
                edit.putString("pressure", pressure);
                edit.putString("weatherDes", weatherDes);
                edit.putString("sunrise", this.sunrize);
                edit.putString("sunset", this.sunset);
                edit.putString("time", format);
                edit.putString("weatherId", this.weatherId);

                activityHumidityBinding.updateTV.setText("Updated: " + format);
                edit.apply();
                setTextData();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }
        try {
            FragmentActivity requireActivity = HumidityActivity.this;
            Toast.makeText(requireActivity, "" + this.activityHumidityBinding.errorTV.getText().toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void callWeatherData() {
        Location location = mLastLocation;
        if (outsideWeatherModel.getOutside() != null) {
            this.activityHumidityBinding.errorTV.setVisibility(View.GONE);
        } else if (!CustomUtils.isInternetConnectionAvailable(HumidityActivity.this)) {
            this.activityHumidityBinding.errorTV.setVisibility(View.VISIBLE);
            this.activityHumidityBinding.errorTV.setText("Please enable your internet connection");
        } else if (location == null) {
            this.activityHumidityBinding.errorTV.setVisibility(View.VISIBLE);
            this.activityHumidityBinding.errorTV.setText("Please enable your GPS to get updated location data");
            if (sharedPreferences.contains("lat")) {
                getWeatherData(this.sharedPreferences.getString("lat", "31.582045"), this.sharedPreferences.getString("lon", "74.329376"));
            }
        } else {
            this.activityHumidityBinding.errorTV.setVisibility(View.GONE);
            getWeatherData(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
        }
    }

    public void getWeatherData(final String str, final String str2) {
        this.activityHumidityBinding.progBar.setVisibility(View.VISIBLE);
        RequestQueue newRequestQueue = Volley.newRequestQueue(HumidityActivity.this);
        newRequestQueue.add(new StringRequest(0, String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&APPID=", str, str2) + BuildConfig.ApiKey, new Response.Listener<String>() {


            public void onResponse(String str) {
                HumidityActivity.this.activityHumidityBinding.progBar.setVisibility(View.GONE);
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    String string = jSONObject.getJSONObject("main").getString("temp");
                    String string2 = jSONObject.getJSONObject("main").getString("humidity");
                    String string3 = jSONObject.getJSONObject("main").getString("feels_like");
                    String string4 = jSONObject.getJSONObject("main").getString("pressure");
                    String string5 = jSONObject.getJSONObject("wind").getString("speed");
                    String string6 = jSONObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    String string7 = jSONObject.getJSONObject(NotificationCompat.CATEGORY_SYSTEM).getString("country");
                    String string8 = jSONObject.getJSONObject(NotificationCompat.CATEGORY_SYSTEM).getString("sunrise");
                    String string9 = jSONObject.getJSONObject(NotificationCompat.CATEGORY_SYSTEM).getString("sunset");
                    HumidityActivity.this.setWeatherText(Float.parseFloat(string), Float.parseFloat(string2), jSONObject.getJSONArray("weather").getJSONObject(0).getString("id"), Float.parseFloat(string3), Float.parseFloat(string5), string4, string6, string7, jSONObject.getString(AppMeasurementSdk.ConditionalUserProperty.NAME), Float.parseFloat(string8), Float.parseFloat(string9), str, str2);
                    HumidityActivity.this.updateWeatherText();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError volleyError) {
                HumidityActivity.this.activityHumidityBinding.progBar.setVisibility(View.GONE);
            }
        }));
    }

    public void onLocationChanged(Location location) {
        mLastLocation = location;
        callWeatherData();
        this.locationManager.removeUpdates(this);
    }

    public void onProviderDisabled(String str) {
    }

    public void onProviderEnabled(String str) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        dolocationStuff();
        if (this.prefs == null) {
            this.prefs = new PreferencesOffline(this);

        }
        switchBackground(BitmapsDrawable.getRoundedRectangleDrawableForColor(prefs.getBackgroundColor(), HumidityActivity.this), prefs.getBackgroundColor());

    }

    public void onStatusChanged(String str, int i, Bundle bundle) {
    }

    public void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(HumidityActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            showPermissionDilouge();
        }
    }





    public void setWeatherText(float f, float f2, String str, float f3, float f4, String str2, String str3, String str4, String str5, float f5, float f6, String str6, String str7) {
        outdoorTemperature = getCelcius(f);
        outdoorHumidity = f2;
        this.weatherId = str;
        feelsLike = getCelcius(f3);
        wind = f4;
        weatherInfoSet = true;
        pressure = str2;
        weatherDes = str3;
        countryName = str4;
        cityName = str5;
        try {
            SharedPreferences.Editor edit = HumidityActivity.this.getSharedPreferences("com.humiditytemperature.skytechzone", 0).edit();
            Date date = new Date(((long) f5) * 1000);
            Date date2 = new Date(((long) f6) * 1000);
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            int i = instance.get(11);
            int i2 = instance.get(12);
            instance.setTime(date2);
            int i3 = instance.get(11);
            int i4 = instance.get(12);
            edit.putInt("sunRiseHour", i);
            edit.putInt("sunRiseMin", i2);
            edit.putInt("sunSetHour", i3);
            edit.putInt("sunSetMin", i4);
            edit.putString("lat", str6);
            edit.putString("lon", str7);
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeUnitType(String str) {
        SharedPreferences.Editor edit = HumidityActivity.this.getSharedPreferences("com.humiditytemperature.skytechzone", 0).edit();
        edit.putString("UNIT_TYPE", str);
        edit.apply();
    }

    public void updateWeatherText() {
        storeweatherData();

    }

    private void setTextData() {

        try {


            this.activityHumidityBinding.updateTV.setText("Updated: " + this.sharedPreferences.getString("time", ""));
            this.activityHumidityBinding.locationTV.setText(this.sharedPreferences.getString("loc", "Unknown"));

            this.activityHumidityBinding.speedviewHumidity.updateSpeed(Double.valueOf(this.sharedPreferences.getString("humidity", "0")).intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void switchBackground(final Drawable newBg, final int baseColor) {

            activityHumidityBinding.bgBottom.setBackgroundDrawable(newBg);

            ObjectAnimator a1 = ObjectAnimator.ofFloat(activityHumidityBinding.bgTop, "alpha", 1.0f, 0.0f);
            a1.setDuration(500L);
            a1.addListener(new Animator.AnimatorListener() {
                /* class com.colortiger.thermo.HumidityActivity.AnonymousClass42 */

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    activityHumidityBinding.bgTop.setBackgroundDrawable(newBg);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            ObjectAnimator a2 = ObjectAnimator.ofFloat(activityHumidityBinding.bgBottom, "alpha", 0.0f, 1.0f);
            a2.setDuration(500L);
            a1.start();
            a2.start();


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
}