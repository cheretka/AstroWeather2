package com.example.astroweather2;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

public class MainActivity extends AppCompatActivity {
    private final String PREF_FILE_NAME = "astroPreferences";
    private final String PREF_LATITUDE_FIELD = "latitudeField";
    private final String PREF_LONGITUDE_FIELD = "longitudeField";
    private final String PREF_FREQUENCY_FIELD = "frequencyField";
    private final String PREF_CITY_NAME_FIELD = "cityNameField";
    private final String PREF_CITY_ID_FIELD = "cityIdField";
    private final String PREF_UNITS_FIELD = "unitsField";
    private final String PREF_LAST_SAVED_DATE = "lastSavedDate";
    private SharedPreferences preferences;

    private final String WEATHER_JSON_FILE_NAME = "weather.json";
    private final String FORECAST_JSON_FILE_NAME = "forecast";

    private final int SECONDS_BETWEEN_REFRESH = 20;

    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd\nHH:mm:ss z");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private FragmentPagerAdapter fragmentPagerAdapter;
    private final FragmentManager fm = getSupportFragmentManager();
    private ViewPager vp;
    private Fragment moonInfoFragment;
    private Fragment sunInfoFragment;
    private Fragment locationFragment;
    private Fragment windFragment;
    private Fragment forecastFragment;
    private boolean isTablet;

    private Handler handler;
    private Runnable timeRunnable;
    private Runnable sunAndMoonRunnable;

    private TextView currentTimeTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView cityTextView;

    private String frequency;
    private String units;

    private Button optionsBtn;
    private Button changeCityBtn;
    private Button refreshBtn;

    private MoonViewModel moonViewModel;
    private SunViewModel sunViewModel;
    private LocationViewModel locationViewModel;
    private WindViewModel windViewModel;
    private ForecastViewModel forecastViewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moonViewModel = ViewModelProviders.of(this).get(MoonViewModel.class);
        sunViewModel = ViewModelProviders.of(this).get(SunViewModel.class);
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        windViewModel = ViewModelProviders.of(this).get(WindViewModel.class);
        forecastViewModel = ViewModelProviders.of(this).get(ForecastViewModel.class);

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
        cityTextView = findViewById(R.id.cityText);

        preferences = getSharedPreferences(PREF_FILE_NAME, Activity.MODE_PRIVATE);

        latitudeTextView.setText(preferences.getString(PREF_LATITUDE_FIELD, ""));
        longitudeTextView.setText(preferences.getString(PREF_LONGITUDE_FIELD, ""));
        cityTextView.setText(preferences.getString(PREF_CITY_NAME_FIELD, ""));

        frequency = preferences.getString(PREF_FREQUENCY_FIELD, "");
        units = preferences.getString(PREF_UNITS_FIELD, "default");

        optionsBtn = findViewById(R.id.optionsButton);
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivityForResult(intent,1);
            }
        });

        changeCityBtn = findViewById(R.id.changeCityBtn);
        changeCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                startActivity(intent);
            }
        });

        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWeatherAndForecastData(true);
            }
        });

        handler = new Handler();

        timeRunnable = new Runnable() {
            @Override
            public void run() {
                String currentTime = simpleDateTimeFormat.format(new Date());
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

                if(!(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(frequency))){
//                    Toast.makeText(getApplicationContext(), "sunAndMoonRunnable", Toast.LENGTH_SHORT).show();

                    double lati = Double.valueOf(latitude);
                    double longi = Double.valueOf(longitude);
                    Calendar c = getInstance();

                    AstroDateTime astroDateTime = new AstroDateTime(c.get(YEAR), c.get(MONTH)+1,
                            c.get(DAY_OF_MONTH), c.get(HOUR_OF_DAY), c.get(MINUTE),
                            c.get(SECOND),c.get(ZONE_OFFSET)/3600_000,true);
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
                    handler.postDelayed(this,Integer.valueOf(frequency)*1000);
                }
            }
        };

        updateWeatherAndForecastData(false);
    }

    private void updateWeatherAndForecastData(boolean changedUnits) {

        if(!isPastEstablishedNoRefreshTime() && !changedUnits){
            try {
                Toast.makeText(getApplicationContext(), "from file", Toast.LENGTH_SHORT).show();
                getWeatherAndForecastFromFile();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (!isConnectedToNetwork(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network connection. \nWeather data may be outdated.", Toast.LENGTH_SHORT).show();
                try {
                    getWeatherAndForecastFromFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String id = preferences.getString(PREF_CITY_ID_FIELD, "");
                units = preferences.getString(PREF_UNITS_FIELD,"default");

                if(!id.isEmpty()){
                    findWeather(id, units);
                    Toast.makeText(getApplicationContext(), "from api", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private boolean isPastEstablishedNoRefreshTime() {
        String savedDateString = preferences.getString(PREF_LAST_SAVED_DATE, "");
        if(!savedDateString.isEmpty()) {
//            Toast.makeText(getApplicationContext(), "saved :" + savedDateString, Toast.LENGTH_LONG).show();
            Date date = null;
            try {
                date = simpleDateTimeFormat.parse(savedDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(SECOND, SECONDS_BETWEEN_REFRESH);
            if (c.before(Calendar.getInstance())) {
//                Toast.makeText(getApplicationContext(), "20 sec passed", Toast.LENGTH_LONG).show();
                return true;
            } else{
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            latitudeTextView.setText(preferences.getString(PREF_LATITUDE_FIELD, ""));
            longitudeTextView.setText(preferences.getString(PREF_LONGITUDE_FIELD, ""));
            frequency = preferences.getString(PREF_FREQUENCY_FIELD, "");

            sunAndMoonRunnable = new Runnable() {
                @Override
                public void run() {
                    String latitude = latitudeTextView.getText().toString();
                    String longitude = longitudeTextView.getText().toString();

                    if(!(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(frequency))){
//                        Toast.makeText(getApplicationContext(), "sunAndMoonRunnable", Toast.LENGTH_SHORT).show();

                        double lati = Double.valueOf(latitude);
                        double longi = Double.valueOf(longitude);
                        Calendar c = getInstance();

                        AstroDateTime astroDateTime = new AstroDateTime(c.get(YEAR), c.get(MONTH)+1,
                                c.get(DAY_OF_MONTH), c.get(HOUR_OF_DAY), c.get(MINUTE),
                                c.get(SECOND),c.get(ZONE_OFFSET)/3600_000,true);
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
                        handler.postDelayed(this,Integer.valueOf(frequency)*1000);
                    }
                }
            };
            updateWeatherAndForecastData(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.post(sunAndMoonRunnable);
        handler.post(timeRunnable);

//        Toast.makeText(getApplicationContext(), "resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timeRunnable);
        handler.removeCallbacks(sunAndMoonRunnable);
//        Toast.makeText(getApplicationContext(), "paused", Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        }

        return isConnected;
    }

    public void findWeather(String cityId, String units)
    {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://api.openweathermap.org/data/2.5/weather?id=").append(cityId)
                .append("&appid=2e4f5773b45fa8319b89a903841ba0c4&units=").append(units);
        String url = urlBuilder.toString();

        JsonObjectRequest jorw = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    String currentDate = simpleDateTimeFormat.format(new Date());
                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString(PREF_LAST_SAVED_DATE, currentDate);
                    preferencesEditor.commit();

                    writeToFile(WEATHER_JSON_FILE_NAME, response.toString());
                    updateWeatherFromJsonObject(response);

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error today weather", Toast.LENGTH_SHORT).show();
            }
        }
        );

        StringBuilder urlBuilder2 = new StringBuilder();
        urlBuilder2.append("http://api.openweathermap.org/data/2.5/forecast?id=").append(cityId)
                .append("&appid=2e4f5773b45fa8319b89a903841ba0c4&units=").append(units);
        String url2 = urlBuilder2.toString();

        JsonObjectRequest jorf = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    writeToFile(FORECAST_JSON_FILE_NAME, response.toString());
                    updateForecastFromJsonObject(response);

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error forecast", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jorw);
        queue.add(jorf);

    }

    private void getWeatherAndForecastFromFile() throws JSONException {
        String s = readFromFile(WEATHER_JSON_FILE_NAME);
        JSONObject weather = new JSONObject(s);
        updateWeatherFromJsonObject(weather);

        s = readFromFile(FORECAST_JSON_FILE_NAME);
        JSONObject forecast = new JSONObject(s);
        updateWeatherFromJsonObject(forecast);
    }

    private void updateForecastFromJsonObject(JSONObject response) throws JSONException {
        Calendar c = getInstance();
        JSONArray array = response.getJSONArray("list");

        JSONObject object = array.getJSONObject(8);
        JSONObject main_object = object.getJSONObject("main");
        String temp = String.valueOf(main_object.getDouble("temp"));
        JSONArray weather_array = object.getJSONArray("weather");
        JSONObject weather_object = weather_array.getJSONObject(0);
        String description = weather_object.getString("description");
        String icon = "o"+weather_object.getString("icon");
        c.add(DAY_OF_MONTH,1);

        forecastViewModel.setDate1(simpleDateFormat.format(c.getTime()));
        forecastViewModel.setDate1Temp(temp);
        forecastViewModel.setDate1Desc(description);
        forecastViewModel.setDate1Image(icon);

        object = array.getJSONObject(16);
        main_object = object.getJSONObject("main");
        temp = String.valueOf(main_object.getDouble("temp"));
        weather_array = object.getJSONArray("weather");
        weather_object = weather_array.getJSONObject(0);
        description = weather_object.getString("description");
        icon = "o"+weather_object.getString("icon");
        c.add(DAY_OF_MONTH,1);

        forecastViewModel.setDate2(simpleDateFormat.format(c.getTime()));
        forecastViewModel.setDate2Temp(temp);
        forecastViewModel.setDate2Desc(description);
        forecastViewModel.setDate2Image(icon);

        object = array.getJSONObject(24);
        main_object = object.getJSONObject("main");
        temp = String.valueOf(main_object.getDouble("temp"));
        weather_array = object.getJSONArray("weather");
        weather_object = weather_array.getJSONObject(0);
        description = weather_object.getString("description");
        icon = "o"+weather_object.getString("icon");
        c.add(DAY_OF_MONTH,1);

        forecastViewModel.setDate3(simpleDateFormat.format(c.getTime()));
        forecastViewModel.setDate3Temp(temp);
        forecastViewModel.setDate3Desc(description);
        forecastViewModel.setDate3Image(icon);

        object = array.getJSONObject(32);
        main_object = object.getJSONObject("main");
        temp = String.valueOf(main_object.getDouble("temp"));
        weather_array = object.getJSONArray("weather");
        weather_object = weather_array.getJSONObject(0);
        description = weather_object.getString("description");
        icon = "o"+weather_object.getString("icon");
        c.add(DAY_OF_MONTH,1);

        forecastViewModel.setDate4(simpleDateFormat.format(c.getTime()));
        forecastViewModel.setDate4Temp(temp);
        forecastViewModel.setDate4Desc(description);
        forecastViewModel.setDate4Image(icon);

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
    }


    private void updateWeatherFromJsonObject(JSONObject response) throws JSONException {

        JSONObject main_object = response.getJSONObject("main");
        JSONObject wind_object = response.getJSONObject("wind");
        JSONArray array = response.getJSONArray("weather");
        JSONObject object = array.getJSONObject(0);
        String temp = String.valueOf(main_object.getDouble("temp"));
        String press = String.valueOf(main_object.getDouble("pressure"));
        String hum = String.valueOf(main_object.getDouble("humidity"));
        String wind_speed = String.valueOf(wind_object.getDouble("speed"));
        double deg = wind_object.optDouble("deg",Double.NaN);

        String wind_deg = String.valueOf(Double.isNaN(deg) ? "no data" : deg);
        String visibility = response.getString("visibility");
        String description = object.getString("description");
        String icon = "o"+object.getString("icon");

        locationViewModel.setTemp(temp);
        locationViewModel.setWeather(description);
        locationViewModel.setPressure(press);
        locationViewModel.setIcon(icon);

        windViewModel.setWindForce(wind_speed);
        windViewModel.setWindDirection(wind_deg);
        windViewModel.setHumidity(hum);
        windViewModel.setVisibility(visibility);

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
    }

    private void writeToFile(String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile(String filename) {

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No data to show. Turn on internet connection.", Toast.LENGTH_SHORT).show();
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


}
