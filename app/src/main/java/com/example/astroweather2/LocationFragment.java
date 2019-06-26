package com.example.astroweather2;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationFragment extends Fragment {

    private LocationViewModel locationViewModel;

    private TextView tempTextView;
    private TextView pressureTextView;
    private TextView weatherTextView;
    private ImageView imageView;

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

        tempTextView = view.findViewById(R.id.tempText);
        pressureTextView = view.findViewById(R.id.pressureText);
        weatherTextView = view.findViewById(R.id.weatherText);
        imageView = view.findViewById(R.id.imageView);

        tempTextView.setText(locationViewModel.getTemp() != null ? locationViewModel.getTemp() : "");
        pressureTextView.setText(locationViewModel.getPressure() != null ? locationViewModel.getPressure() : "");
        weatherTextView.setText(locationViewModel.getWeather() != null ? locationViewModel.getWeather() : "");

        String icon = locationViewModel.getIcon();
        if(icon != null){
            imageView.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        return view;

    }
}


