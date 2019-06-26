package com.example.astroweather2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.Calendar;

public class LocationListActivity extends AppCompatActivity {

    private DBManager dbManager;

    private ListView listView;
    private Button addBtn;
    private Button chooseBtn;

    private String cityName;
    private String cityId;
    private String cityLati;
    private String cityLongi;

    private SharedPreferences preferences;

    private final String PREF_FILE_NAME = "astroPreferences";
    private final String PREF_LATITUDE_FIELD = "latitudeField";
    private final String PREF_LONGITUDE_FIELD = "longitudeField";
    private final String PREF_FREQUENCY_FIELD = "frequencyField";
    private final String PREF_CITY_NAME_FIELD = "cityNameField";
    private final String PREF_CITY_ID_FIELD = "cityIdField";
    private final String PREF_UNITS_FIELD = "unitsField";

    private SimpleCursorAdapter adapter;

    final String[] from = new String[] {
            DatabaseHelper.NAME, DatabaseHelper.CITY_ID,
            DatabaseHelper.LATI, DatabaseHelper.LONGI };

    final int[] to = new int[] { R.id.city_name, R.id.city_id, R.id.city_lati, R.id.city_longi };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locations_list);

        preferences = getSharedPreferences(PREF_FILE_NAME, Activity.MODE_PRIVATE);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();

        listView = findViewById(R.id.list_view);
        addBtn = findViewById(R.id.addBtn);
        chooseBtn = findViewById(R.id.chooseBtn);

        adapter = new SimpleCursorAdapter(this, R.layout.locations_view_record, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView cityNameTextView = view.findViewById(R.id.city_name);
                TextView cityIdTextView = view.findViewById(R.id.city_id);
                TextView cityLatiTextView = view.findViewById(R.id.city_lati);
                TextView cityLongiTextView = view.findViewById(R.id.city_longi);

                cityName = cityNameTextView.getText().toString();
                cityId = cityIdTextView.getText().toString();
                cityLati = cityLatiTextView.getText().toString();
                cityLongi = cityLongiTextView.getText().toString();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cityName == null || cityId == null || cityLati == null || cityLongi == null ){
                    Toast.makeText(getApplicationContext(), "No city selected", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString(PREF_CITY_ID_FIELD, cityId);
                    preferencesEditor.putString(PREF_CITY_NAME_FIELD, cityName);
                    preferencesEditor.putString(PREF_LATITUDE_FIELD, cityLati);
                    preferencesEditor.putString(PREF_LONGITUDE_FIELD, cityLongi);
                    preferencesEditor.commit();

                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//
//
//        }
//    }
}
