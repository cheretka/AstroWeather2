package com.example.astroweather2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;

public class SunInfoFragment extends Fragment {

    private SunViewModel sunViewModel;

    private TextView sunriseTextView;
    private TextView sunsetTextView;
    private TextView sunriseAzimuthTextView;
    private TextView sunsetAzimuthTextView;
    private TextView morningTwilightTextView;
    private TextView eveningTwilightTextView;


    public static SunInfoFragment newInstance() {
        SunInfoFragment fragment = new SunInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sunViewModel = ViewModelProviders.of(getActivity()).get(SunViewModel.class);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sun_info_fragment, container,
                false);

        sunriseTextView = view.findViewById(R.id.sunriseTimeText);
        sunsetTextView = view.findViewById(R.id.sunsetTimeText);
        sunriseAzimuthTextView = view.findViewById(R.id.sunriseAzimuthText);
        sunsetAzimuthTextView = view.findViewById(R.id.sunsetAzimuthText);
        morningTwilightTextView = view.findViewById(R.id.morningTwilightText);
        eveningTwilightTextView = view.findViewById(R.id.eveningTwilightText);

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
