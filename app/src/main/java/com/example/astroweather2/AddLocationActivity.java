package com.example.astroweather2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AddLocationActivity extends AppCompatActivity implements OnClickListener {

    private Button addBtn;
    private EditText cityNameEditText;

    private String cityName;
    private String cityId;
    private String cityLati;
    private String cityLongi;
    boolean found;

    private SharedPreferences preferences;

    private final String PREF_FILE_NAME = "astroPreferences";
    private final String PREF_LATITUDE_FIELD = "latitudeField";
    private final String PREF_LONGITUDE_FIELD = "longitudeField";
    private final String PREF_FREQUENCY_FIELD = "frequencyField";
    private final String PREF_CITY_NAME_FIELD = "cityNameField";
    private final String PREF_CITY_ID_FIELD = "cityIdField";
    private final String PREF_UNITS_FIELD = "unitsField";
    private final String PREF_CHANGED = "changed";


    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add);

        preferences = getSharedPreferences(PREF_FILE_NAME, Activity.MODE_PRIVATE);

        cityNameEditText = findViewById(R.id.name);
        addBtn = findViewById(R.id.add);

        dbManager = new DBManager(this);
        dbManager.open();
        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        final String name = cityNameEditText.getText().toString();

        if(MainActivity.isConnectedToNetwork(getApplicationContext())){
            findCity(name);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void findCity(String name)
    {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://api.openweathermap.org/data/2.5/weather?q=").append(name).append("&appid=2e4f5773b45fa8319b89a903841ba0c4");
        String url = urlBuilder.toString();

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    String n = response.getString("name");
                    cityName = n;
                    String id = String.valueOf(response.getInt("id"));
                    cityId = id;
                    JSONObject coord_object = response.getJSONObject("coord");
                    String lati = String.valueOf(coord_object.getDouble("lat"));
                    cityLati = lati;
                    String longi = String.valueOf(coord_object.getDouble("lon"));
                    cityLongi = longi;

                    dbManager.insert(cityName, cityId, cityLati, cityLongi);

                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString(PREF_CITY_ID_FIELD, cityId);
                    preferencesEditor.putString(PREF_CITY_NAME_FIELD, cityName);
                    preferencesEditor.putString(PREF_LATITUDE_FIELD, cityLati);
                    preferencesEditor.putString(PREF_LONGITUDE_FIELD, cityLongi);
                    preferencesEditor.putBoolean(PREF_CHANGED, true);
                    preferencesEditor.commit();


                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "City not found", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

}
