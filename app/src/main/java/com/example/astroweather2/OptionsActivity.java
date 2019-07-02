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
    private final String PREF_FREQUENCY_FIELD = "frequencyField";
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
        String freq = frequencyEdit.getText().toString();

        if(freq.isEmpty() || Integer.valueOf(freq)<1){
            Toast.makeText(this, "Frequency value must be bigger than 0 [s].", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
