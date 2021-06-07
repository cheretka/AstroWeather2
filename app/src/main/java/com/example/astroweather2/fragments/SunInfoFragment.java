package com.example.astroweather2.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.example.astroweather2.R;
import com.example.astroweather2.viewmodels.SunViewModel;

public class SunInfoFragment extends Fragment {

    private SunViewModel sunViewModel;


    public static SunInfoFragment newInstance() {
        return new SunInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sunViewModel = ViewModelProviders.of(getActivity()).get(SunViewModel.class);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sun_info_fragment, container, false);

        TextView sunriseTextView = view.findViewById(R.id.sunriseTimeText);
        TextView sunsetTextView = view.findViewById(R.id.sunsetTimeText);
        TextView sunriseAzimuthTextView = view.findViewById(R.id.sunriseAzimuthText);
        TextView sunsetAzimuthTextView = view.findViewById(R.id.sunsetAzimuthText);
        TextView morningTwilightTextView = view.findViewById(R.id.morningTwilightText);
        TextView eveningTwilightTextView = view.findViewById(R.id.eveningTwilightText);

        AstroCalculator.SunInfo sunInfo = sunViewModel.getSunInfo();
        if(sunInfo!=null){
            sunriseTextView.setText(sunInfo.getSunrise().toString());
            sunsetTextView.setText(sunInfo.getSunset().toString());
            sunriseAzimuthTextView.setText(String.valueOf(sunInfo.getAzimuthRise()));
            sunsetAzimuthTextView.setText(String.valueOf(sunInfo.getAzimuthSet()));
            morningTwilightTextView.setText(sunInfo.getTwilightMorning().toString());
            eveningTwilightTextView.setText(sunInfo.getTwilightEvening().toString());
        }

        return view;

    }
}
