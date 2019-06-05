package com.example.astroweather2;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final String PREF_FILE_NAME = "astroPreferences";
    private final String PREF_LATITUDE_FIELD = "latitudeField";
    private final String PREF_LONGITUDE_FIELD = "longitudeField";
    private final String PREF_FREQUENCY_FIELD = "frequencyField";
    private SharedPreferences preferences;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd\nHH:mm:ss z");
    private FragmentPagerAdapter fragmentPagerAdapter;
    private final FragmentManager fm = getSupportFragmentManager();
    private ViewPager vp;
    private Fragment moonInfoFragment;
    private Fragment sunInfoFragment;
    private boolean isTablet;

    private Handler handler;
    private Runnable timeRunnable;
    private Runnable sunAndMoonRunnable;

    private TextView currentTimeTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView frequencyTextView;
    private Button optionsBtn;

    private MoonViewModel moonViewModel;
    private SunViewModel sunViewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moonViewModel = ViewModelProviders.of(this).get(MoonViewModel.class);
        sunViewModel = ViewModelProviders.of(this).get(SunViewModel.class);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        if(isTablet){
            FragmentTransaction ft = this.fm.beginTransaction();

            moonInfoFragment = MoonInfoFragment.newInstance();
            ft.replace(R.id.fragment_container, moonInfoFragment);

            sunInfoFragment = SunInfoFragment.newInstance();
            ft.replace(R.id.fragment_container2, sunInfoFragment);
            ft.commit();
        } else {
            vp = findViewById(R.id.vp);
            fragmentPagerAdapter = new InfoViewPager(getSupportFragmentManager());
            vp.setAdapter(fragmentPagerAdapter);
        }

        currentTimeTextView = findViewById(R.id.timeText);
        latitudeTextView = findViewById(R.id.latitudeText);
        longitudeTextView = findViewById(R.id.longitudeText);
        frequencyTextView = findViewById(R.id.frequencyText);

        preferences = getSharedPreferences(PREF_FILE_NAME, Activity.MODE_PRIVATE);

        latitudeTextView.setText(preferences.getString(PREF_LATITUDE_FIELD, ""));
        longitudeTextView.setText(preferences.getString(PREF_LONGITUDE_FIELD, ""));
        frequencyTextView.setText(preferences.getString(PREF_FREQUENCY_FIELD, ""));

        optionsBtn = findViewById(R.id.optionsButton);
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivityForResult(intent,1);
            }
        });

        handler = new Handler();

        timeRunnable = new Runnable() {
            @Override
            public void run() {
                String currentTime = simpleDateFormat.format(new Date());
                currentTimeTextView.setText(currentTime);
                //Toast.makeText(getApplicationContext(), "timeRunnable", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, 1000);
            }
        };

        sunAndMoonRunnable = new Runnable() {
            @Override
            public void run() {
                String latitude = latitudeTextView.getText().toString();
                String longitude = longitudeTextView.getText().toString();
                String freq = frequencyTextView.getText().toString();

                if(!(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(freq))){
                    Toast.makeText(getApplicationContext(), "sunAndMoonRunnable", Toast.LENGTH_SHORT).show();

                    double lati = Double.valueOf(latitude);
                    double longi = Double.valueOf(longitude);
                    Calendar c = Calendar.getInstance();

                    AstroDateTime astroDateTime = new AstroDateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1,
                            c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                            c.get(Calendar.SECOND),c.get(Calendar.ZONE_OFFSET)/3600_000,true);
                    AstroCalculator astroCalculator = new AstroCalculator(astroDateTime, new AstroCalculator.Location(lati, longi));

                    AstroCalculator.MoonInfo moonInfo = astroCalculator.getMoonInfo();
                    AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();
                    moonViewModel.setMoonInfo(moonInfo);
                    sunViewModel.setSunInfo(sunInfo);
                    if(isTablet){
                        FragmentTransaction ft = fm.beginTransaction();

                        moonInfoFragment = MoonInfoFragment.newInstance();
                        ft.replace(R.id.fragment_container, moonInfoFragment);

                        sunInfoFragment = SunInfoFragment.newInstance();
                        ft.replace(R.id.fragment_container2, sunInfoFragment);
                        ft.commit();
                    } else {
                        vp.getAdapter().notifyDataSetChanged();
                    }
                    handler.postDelayed(this,Integer.valueOf(freq)*1000);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            latitudeTextView.setText(preferences.getString(PREF_LATITUDE_FIELD, ""));
            longitudeTextView.setText(preferences.getString(PREF_LONGITUDE_FIELD, ""));
            frequencyTextView.setText(preferences.getString(PREF_FREQUENCY_FIELD, ""));

            sunAndMoonRunnable = new Runnable() {
                @Override
                public void run() {
                    String latitude = latitudeTextView.getText().toString();
                    String longitude = longitudeTextView.getText().toString();
                    String freq = frequencyTextView.getText().toString();

                    if(!(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(freq))){
                        Toast.makeText(getApplicationContext(), "sunAndMoonRunnable", Toast.LENGTH_SHORT).show();

                        double lati = Double.valueOf(latitude);
                        double longi = Double.valueOf(longitude);
                        Calendar c = Calendar.getInstance();

                        AstroDateTime astroDateTime = new AstroDateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1,
                                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                                c.get(Calendar.SECOND),c.get(Calendar.ZONE_OFFSET)/3600_000,true);
                        AstroCalculator astroCalculator = new AstroCalculator(astroDateTime, new AstroCalculator.Location(lati, longi));

                        AstroCalculator.MoonInfo moonInfo = astroCalculator.getMoonInfo();
                        AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();
                        moonViewModel.setMoonInfo(moonInfo);
                        sunViewModel.setSunInfo(sunInfo);
                        if(isTablet){
                            FragmentTransaction ft = fm.beginTransaction();

                            moonInfoFragment = MoonInfoFragment.newInstance();
                            ft.replace(R.id.fragment_container, moonInfoFragment);

                            sunInfoFragment = SunInfoFragment.newInstance();
                            ft.replace(R.id.fragment_container2, sunInfoFragment);
                            ft.commit();
                        } else {
                            vp.getAdapter().notifyDataSetChanged();
                        }
                        handler.postDelayed(this,Integer.valueOf(freq)*1000);
                    }
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.post(sunAndMoonRunnable);
        handler.post(timeRunnable);

        Toast.makeText(getApplicationContext(), "resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timeRunnable);
        handler.removeCallbacks(sunAndMoonRunnable);
        Toast.makeText(getApplicationContext(), "paused", Toast.LENGTH_SHORT).show();
    }

}
