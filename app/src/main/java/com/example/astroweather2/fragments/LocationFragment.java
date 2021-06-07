package com.example.astroweather2.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.astroweather2.viewmodels.LocationViewModel;
import com.example.astroweather2.R;

public class LocationFragment extends Fragment {

    private LocationViewModel locationViewModel;

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.location_fragment, container, false);

        TextView tempTextView = view.findViewById(R.id.tempText);
        TextView pressureTextView = view.findViewById(R.id.pressureText);
        TextView weatherTextView = view.findViewById(R.id.weatherText);
        ImageView imageView = view.findViewById(R.id.imageView);

        if(locationViewModel.getTemp() != null){
            if(locationViewModel.getUnit().equals("default"))
                tempTextView.setText(locationViewModel.getTemp()+" K" );
            else if (locationViewModel.getUnit().equals("metric")){
                double d = Double.parseDouble(locationViewModel.getTemp());
                d = d - 273.15;
                tempTextView.setText(locationViewModel.getTemp()+" C" );
            }
            else
            {
                double d = Double.parseDouble(locationViewModel.getTemp());
                d =  Double.parseDouble(locationViewModel.getTemp()) - 273.15F;
                d = d*9f/5f-459.67f;
                tempTextView.setText(locationViewModel.getTemp()+" F" );
            }
        }else{
            tempTextView.setText( "");
        }

//        tempTextView.setText(locationViewModel.getTemp() != null ? locationViewModel.getTemp()+" K" : "");
        pressureTextView.setText(locationViewModel.getPressure() != null ? locationViewModel.getPressure()+" hPa" : "");
        weatherTextView.setText(locationViewModel.getWeather() != null ? locationViewModel.getWeather() : "");
        String icon = locationViewModel.getIcon();
        if(icon != null){
            imageView.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        return view;
    }
}


