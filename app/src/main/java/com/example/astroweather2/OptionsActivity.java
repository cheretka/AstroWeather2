package com.example.astroweather2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;

public class OptionsActivity extends AppCompatActivity {

    private final String PREF_FILE_NAME = "astroPreferences";
    private final String PREF_LATITUDE_FIELD = "latitudeField";
    private final String PREF_LONGITUDE_FIELD = "longitudeField";
    private final String PREF_FREQUENCY_FIELD = "frequencyField";
    private final String PREF_CITY_NAME_FIELD = "cityNameField";
    private final String PREF_CITY_ID_FIELD = "cityIdField";
    private final String PREF_UNITS_FIELD = "unitsField";

    private SharedPreferences preferences;
    private Spinner spinner1;
    private EditText frequencyEdit;
    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        spinner1 = findViewById(R.id.spinner1);
        frequencyEdit = findViewById(R.id.frequencyEdit);

        preferences = getSharedPreferences(PREF_FILE_NAME, Activity.MODE_PRIVATE);
        frequencyEdit.setText(preferences.getString(PREF_FREQUENCY_FIELD, ""));

        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEverythingValid()){
                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString(PREF_UNITS_FIELD, spinner1.getSelectedItem().toString());
//                    preferencesEditor.putString(PREF_LONGITUDE_FIELD, longitudeEdit.getText().toString());
                    preferencesEditor.putString(PREF_FREQUENCY_FIELD, frequencyEdit.getText().toString());
                    preferencesEditor.commit();

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private boolean isEverythingValid(){
//        String longitude = longitudeEdit.getText().toString();
        String freq = frequencyEdit.getText().toString();
//
//        if(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(freq)){
//            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        BigDecimal latitudeBD = new BigDecimal(latitude);
//        if(latitudeBD.compareTo(BigDecimal.valueOf(90))==1 || latitudeBD.compareTo(BigDecimal.valueOf(-90))==-1){
//            Toast.makeText(this, "Latitude value must be between -90 and 90.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        BigDecimal longitudeBD = new BigDecimal(longitude);
//        if(longitudeBD.compareTo(BigDecimal.valueOf(180))==1 || longitudeBD.compareTo(BigDecimal.valueOf(-180))==-1){
//            Toast.makeText(this, "Longitude value must be between -180 and 180.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if(Integer.valueOf(freq)==0){
            Toast.makeText(this, "Frequency value must be bigger than 0 [s].", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
