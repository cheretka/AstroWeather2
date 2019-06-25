package com.example.astroweather2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationFragment extends Fragment {

    private LocationViewModel locationViewModel;

    private TextView locationTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView timeTextView;
    private TextView tempTextView;
    private TextView pressureTextView;
    private TextView weatherTextView;

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.location_fragment, container,
                false);

        locationTextView = view.findViewById(R.id.locationText);
        latitudeTextView = view.findViewById(R.id.latitudeText);
        longitudeTextView = view.findViewById(R.id.longitudeText);
        timeTextView = view.findViewById(R.id.timeText);
        tempTextView = view.findViewById(R.id.tempText);
        pressureTextView = view.findViewById(R.id.pressureText);
        weatherTextView = view.findViewById(R.id.weatherText);

        locationTextView.setText(locationViewModel.getLocation() != null ? locationViewModel.getLocation() : "");
        latitudeTextView.setText(locationViewModel.getLatitude() != null ? locationViewModel.getLatitude() : "");
        longitudeTextView.setText(locationViewModel.getLongitude() != null ? locationViewModel.getLongitude() : "");
        timeTextView.setText(locationViewModel.getTime() != null ? locationViewModel.getTime() : "");
        tempTextView.setText(locationViewModel.getTemp() != null ? locationViewModel.getTemp() : "");
        pressureTextView.setText(locationViewModel.getPressure() != null ? locationViewModel.getPressure() : "");
        weatherTextView.setText(locationViewModel.getWeather() != null ? locationViewModel.getWeather() : "");

        return view;

    }
}


