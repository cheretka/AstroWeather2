package com.example.astroweather2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private Spinner spinner;
    private EditText frequencyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        spinner = findViewById(R.id.spinner1);
        frequencyEditText = findViewById(R.id.frequencyEdit);

        preferences = getSharedPreferences("astroPreferences", Activity.MODE_PRIVATE);
        frequencyEditText.setText(preferences.getString("frequencyField", ""));

        Button okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEverythingValid()){
                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString("unitsField", spinner.getSelectedItem().toString());
                    preferencesEditor.putString("frequencyField", frequencyEditText.getText().toString());
                    preferencesEditor.commit();

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private boolean isEverythingValid(){
        String freq = frequencyEditText.getText().toString();

        if(freq.isEmpty() || Integer.parseInt(freq)<1 || Integer.parseInt(freq)>900){
            Toast.makeText(this, "niepoprawna wartość", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
