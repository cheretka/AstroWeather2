package com.example.astroweather2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.astroweather2.database.MyDatabase;
import com.example.astroweather2.database.DatabaseHelper;

import org.json.JSONObject;


public class LocationListActivity extends AppCompatActivity {

    private String cityName;
    private String cityId;
    private String cityLati;
    private String cityLongi;
    private SharedPreferences preferences;
    private SimpleCursorAdapter adapter;
    private MyDatabase myDatabase;


    final String[] fromArray = new String[] {DatabaseHelper.NAME, DatabaseHelper.CITY_ID, DatabaseHelper.LATI, DatabaseHelper.LONGI };
    final int[] toArray = new int[] { R.id.city_name, R.id.city_id, R.id.city_lati, R.id.city_longi };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locations_list);

        preferences = getSharedPreferences("astroPreferences", Activity.MODE_PRIVATE);

        myDatabase = new MyDatabase(this);
        myDatabase.open();
        Cursor cursor = myDatabase.fetch();

        ListView listView = findViewById(R.id.list_view);
        Button addBtn = findViewById(R.id.addBtn);
        Button chooseBtn = findViewById(R.id.chooseBtn);
        Button deleteBtn = findViewById(R.id.deleteBtn);

        adapter = new SimpleCursorAdapter(this, R.layout.locations_view_record, cursor, fromArray, toArray, 0);
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
                    Toast.makeText(getApplicationContext(), "proszę wybrać miasto z listy", Toast.LENGTH_SHORT).show();
                } else {
                    if(MainActivity.isConnectedToNetwork(getApplicationContext())){
                        SharedPreferences.Editor preferencesEditor = preferences.edit();
                        preferencesEditor.putString("cityIdField", cityId);
                        preferencesEditor.putString("cityNameField", cityName);
                        preferencesEditor.putString("latitudeField", cityLati);
                        preferencesEditor.putString("longitudeField", cityLongi);
                        preferencesEditor.commit();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "nie ma połączenia z netem", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cityName == null || cityId == null || cityLati == null || cityLongi == null ){
                    Toast.makeText(getApplicationContext(), "proszę wybrać miasto z listy", Toast.LENGTH_SHORT).show();
                } else {
                    myDatabase.delete(Long.parseLong(cityId));
                    Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
