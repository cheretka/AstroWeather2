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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.example.astroweather2.viewmodels.ForecastViewModel;
import com.example.astroweather2.viewmodels.LocationViewModel;
import com.example.astroweather2.viewmodels.MoonViewModel;
import com.example.astroweather2.viewmodels.SunViewModel;
import com.example.astroweather2.viewmodels.WindViewModel;

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
    private final String PREF_CHANGED = "changed";
    private SharedPreferences preferences;

    private final String WEATHER_JSON_FILE_NAME = "weather.json";
    private final String FORECAST_JSON_FILE_NAME = "forecast.json";

    private final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd\nHH:mm:ss z");
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private ViewPager vp;
    private ViewPager vpWeather;
    private ViewPager vpSunMoon;
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
            vpWeather = findViewById(R.id.vpWeather);
            FragmentPagerAdapter weatherFragmentPagerAdapter = new AdapterWeather(getSupportFragmentManager());
            vpWeather.setAdapter(weatherFragmentPagerAdapter);

            vpSunMoon = findViewById(R.id.vpSunMoon);
            FragmentPagerAdapter sunMoonFragmentPagerAdapter = new AdapterSunMoon(getSupportFragmentManager());
            vpSunMoon.setAdapter(sunMoonFragmentPagerAdapter);
        } else {
            vp = findViewById(R.id.vp);
            FragmentPagerAdapter fragmentPagerAdapter = new AdapterInfo(getSupportFragmentManager());
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

        Button optionsBtn = findViewById(R.id.optionsButton);
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
                startActivityForResult(intent,1);
            }
        });

        Button changeCityBtn = findViewById(R.id.changeCityBtn);
        changeCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                startActivityForResult(intent,2);
            }
        });

        Button refreshBtn = findViewById(R.id.refreshBtn);
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
                        vpWeather.getAdapter().notifyDataSetChanged();
                        vpSunMoon.getAdapter().notifyDataSetChanged();
                    } else {
                        vp.getAdapter().notifyDataSetChanged();
                    }
                    handler.postDelayed(this,Integer.valueOf(frequency)*1000);
                }
            }
        };

        boolean changed = preferences.getBoolean(PREF_CHANGED, false);
        updateWeatherAndForecastData(changed);
        if(changed){
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putBoolean(PREF_CHANGED, false);
            preferencesEditor.commit();
        }

    }

    private void updateWeatherAndForecastData(boolean newUnits) {

        if(!isTimeForUpdate() && !newUnits ){
            try {
                Toast.makeText(getApplicationContext(), "aktualne dane z pliku", Toast.LENGTH_SHORT).show();
                getDataFromFile();
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        } else if (!isConnectedToNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "dane mogą być nie aktualne", Toast.LENGTH_SHORT).show();
            try {
                getDataFromFile();
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        } else {
            String id = preferences.getString(PREF_CITY_ID_FIELD, "");
            units = preferences.getString(PREF_UNITS_FIELD,"default");

            if(!id.isEmpty()){
                refreshment(id, units);
                Toast.makeText(getApplicationContext(), "aktualne dane", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private boolean isTimeForUpdate() {
        String savedDateString = preferences.getString(PREF_LAST_SAVED_DATE, "");
        if(!savedDateString.isEmpty()) {
            Date date = null;
            try {
                date = simpleDateTimeFormat.parse(savedDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int SECONDS_BETWEEN_REFRESH = 500;
            c.add(SECOND, SECONDS_BETWEEN_REFRESH);
            if (c.before(Calendar.getInstance())) {
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
                            vpWeather.getAdapter().notifyDataSetChanged();
                            vpSunMoon.getAdapter().notifyDataSetChanged();
                        } else {
                            vp.getAdapter().notifyDataSetChanged();
                        }
                        handler.postDelayed(this,Integer.valueOf(frequency)*1000);
                    }
                }
            };
            updateWeatherAndForecastData(true);
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            latitudeTextView.setText(preferences.getString(PREF_LATITUDE_FIELD, ""));
            longitudeTextView.setText(preferences.getString(PREF_LONGITUDE_FIELD, ""));
            cityTextView.setText(preferences.getString(PREF_CITY_NAME_FIELD,""));
            updateWeatherAndForecastData(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(sunAndMoonRunnable);
        handler.post(timeRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timeRunnable);
        handler.removeCallbacks(sunAndMoonRunnable);
    }



    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        }

        return isConnected;
    }



    public void refreshment(String cityId, String units){
        StringBuilder urlBuilderWeather = new StringBuilder();
        urlBuilderWeather.append("http://api.openweathermap.org/data/2.5/weather?id=").append(cityId).append("&appid=2e4f5773b45fa8319b89a903841ba0c4&units=").append(units);
        String url = urlBuilderWeather.toString();

        JsonObjectRequest jsonWeather = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    String currentDate = simpleDateTimeFormat.format(new Date());
                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString(PREF_LAST_SAVED_DATE, currentDate);
                    preferencesEditor.commit();

                    writeDataToFile(WEATHER_JSON_FILE_NAME, response.toString());
                    getWeather(response);

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }
        );

        StringBuilder urlBuilderForecast = new StringBuilder();
        urlBuilderForecast.append("http://api.openweathermap.org/data/2.5/forecast?id=").append(cityId).append("&appid=2e4f5773b45fa8319b89a903841ba0c4&units=").append(units);
        String url2 = urlBuilderForecast.toString();

        JsonObjectRequest jsonForecast = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    writeDataToFile(FORECAST_JSON_FILE_NAME, response.toString());
                    getForecast(response);

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonWeather);
        queue.add(jsonForecast);

    }



    private void getDataFromFile() throws JSONException {
        String s = readDataFromFile(WEATHER_JSON_FILE_NAME);
        JSONObject weather = new JSONObject(s);
        getWeather(weather);

        String f = readDataFromFile(FORECAST_JSON_FILE_NAME);
        JSONObject forecast = new JSONObject(f);
        getForecast(forecast);
    }

    private void getForecast(JSONObject response) throws JSONException {
        Calendar c = getInstance();
        JSONArray array = response.getJSONArray("list");

        JSONObject object = array.getJSONObject(8);
        JSONObject main_object = object.getJSONObject("main");
        String temp = String.valueOf(main_object.getDouble("temp"));
        JSONArray weather_array = object.getJSONArray("weather");
        JSONObject weather_object = weather_array.getJSONObject(0);
        String description = weather_object.getString("description");
        String icon = "my_" + weather_object.getString("icon");
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
        icon = "my_" + weather_object.getString("icon");
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
        icon = "my_" + weather_object.getString("icon");
        c.add(DAY_OF_MONTH,1);

        forecastViewModel.setDate3(simpleDateFormat.format(c.getTime()));
        forecastViewModel.setDate3Temp(temp);
        forecastViewModel.setDate3Desc(description);
        forecastViewModel.setDate3Image(icon);

        object = array.getJSONObject(34);
        main_object = object.getJSONObject("main");
        temp = String.valueOf(main_object.getDouble("temp"));
        weather_array = object.getJSONArray("weather");
        weather_object = weather_array.getJSONObject(0);
        description = weather_object.getString("description");
        icon = "my_" + weather_object.getString("icon");
        c.add(DAY_OF_MONTH,1);

        forecastViewModel.setDate4(simpleDateFormat.format(c.getTime()));
        forecastViewModel.setDate4Temp(temp);
        forecastViewModel.setDate4Desc(description);
        forecastViewModel.setDate4Image(icon);

        if(isTablet){
            vpWeather.getAdapter().notifyDataSetChanged();
            vpSunMoon.getAdapter().notifyDataSetChanged();
        } else {
            vp.getAdapter().notifyDataSetChanged();
        }
    }

    private void getWeather(JSONObject response) throws JSONException {

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
        String icon = "my_" + object.getString("icon");

        locationViewModel.setTemp(temp);
        locationViewModel.setWeather(description);
        locationViewModel.setPressure(press);
        locationViewModel.setIcon(icon);
        locationViewModel.setUnit(preferences.getString(PREF_UNITS_FIELD, "default"));

        windViewModel.setWindForce(wind_speed);
        windViewModel.setWindDirection(wind_deg);
        windViewModel.setHumidity(hum);
        windViewModel.setVisibility(visibility);
        windViewModel.setUnit(preferences.getString(PREF_UNITS_FIELD, "default"));

        if(isTablet){
            vpWeather.getAdapter().notifyDataSetChanged();
            vpSunMoon.getAdapter().notifyDataSetChanged();
        } else {
            vp.getAdapter().notifyDataSetChanged();
        }
    }



    private void writeDataToFile(String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readDataFromFile(String filename) {

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
            Toast.makeText(getApplicationContext(), "nie ma połączenia z internetem", Toast.LENGTH_SHORT).show();
//            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
//            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
