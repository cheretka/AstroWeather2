package com.example.astroweather2;

import android.app.Activity;
import android.content.SharedPreferences;
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
import com.example.astroweather2.database.MyDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class AddLocationActivity extends AppCompatActivity {

    private EditText cityNameEditText;
    private EditText countryCodeEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private String cityName;
    private String cityId;
    private String cityLati;
    private String cityLongi;

    private SharedPreferences preferences;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add);

        preferences = getSharedPreferences("astroPreferences", Activity.MODE_PRIVATE);

        cityNameEditText = findViewById(R.id.name);
        countryCodeEditText = findViewById(R.id.countryCode);
        latitudeEditText = findViewById(R.id.latitudeedit);
        longitudeEditText = findViewById(R.id.longitudeedit);

        Button addBtn = findViewById(R.id.add);
        Button justSelectBtn = findViewById(R.id.justselectBtn);

        myDatabase = new MyDatabase(this);
        myDatabase.open();
//        addBtn.setOnClickListener(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = cityNameEditText.getText().toString();
                final String country_code = countryCodeEditText.getText().toString();
                final String lat = latitudeEditText.getText().toString();
                final String lon = longitudeEditText.getText().toString();

                if(MainActivity.isConnectedToNetwork(getApplicationContext())){
                    findCity(name, country_code, lat, lon,  true);
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        justSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = cityNameEditText.getText().toString();
                final String country_code = countryCodeEditText.getText().toString();
                final String lat = latitudeEditText.getText().toString();
                final String lon = longitudeEditText.getText().toString();

                if(MainActivity.isConnectedToNetwork(getApplicationContext())){
                    findCity(name, country_code, lat, lon,false);
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

//    http://api.openweathermap.org/data/2.5/weather?lat=-34.603722&lon=-58.381592&appid=2e4f5773b45fa8319b89a903841ba0c4

    public void findCity(String name, String country_code, String lat, String lon, final boolean toInsert){
        StringBuilder urlBuilder = new StringBuilder();

        if (name.length() != 0 && country_code.length() != 0)
            urlBuilder.append("http://api.openweathermap.org/data/2.5/weather?q=").append(name).append(",").append(country_code).append("&appid=2e4f5773b45fa8319b89a903841ba0c4");
        else if (lat.length() !=0 && lon.length() != 0){
            urlBuilder.append("http://api.openweathermap.org/data/2.5/weather?lat=").append(lat).append("&lon=").append(lon).append("&appid=2e4f5773b45fa8319b89a903841ba0c4");
        }
        else
            urlBuilder.append("http://api.openweathermap.org/data/2.5/weather?q=").append(name).append("&appid=2e4f5773b45fa8319b89a903841ba0c4");

        String url = urlBuilder.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    cityName = response.getString("name");
                    cityId = String.valueOf(response.getInt("id"));
                    JSONObject coord_object = response.getJSONObject("coord");
                    cityLati = String.valueOf(coord_object.getDouble("lat"));
                    cityLongi = String.valueOf(coord_object.getDouble("lon"));

                    if(toInsert){
                        myDatabase.insert(cityName, cityId, cityLati, cityLongi);
                    }

                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString("cityIdField", cityId);
                    preferencesEditor.putString("cityNameField", cityName);
                    preferencesEditor.putString("latitudeField", cityLati);
                    preferencesEditor.putString("longitudeField", cityLongi);
                    preferencesEditor.putBoolean("changed", true);
                    preferencesEditor.commit();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "miasto nie znaleziono", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

//    public void deleteFromTable(long idItem){
//        myDatabase.delete(idItem);
//    }


}
