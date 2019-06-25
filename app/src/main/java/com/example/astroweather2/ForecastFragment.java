package com.example.astroweather2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ForecastFragment extends Fragment {

    private ForecastViewModel forecastViewModel;

    private TextView date1TextView;
    private TextView date1descTextView;

    public static ForecastFragment newInstance() {
        ForecastFragment fragment = new ForecastFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forecastViewModel = ViewModelProviders.of(getActivity()).get(ForecastViewModel.class);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.forecast_fragment, container,
                false);

        date1TextView = view.findViewById(R.id.date1Label);
        date1descTextView = view.findViewById(R.id.date1Text);

        date1TextView.setText(forecastViewModel.getDate1() != null ? forecastViewModel.getDate1() : "");
        date1descTextView.setText(forecastViewModel.getDate1Text() != null ? forecastViewModel.getDate1Text() : "");

        return view;

    }

}
