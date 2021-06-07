package com.example.astroweather2.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.astroweather2.viewmodels.ForecastViewModel;
import com.example.astroweather2.R;

public class ForecastFragment extends Fragment {

    private ForecastViewModel forecastViewModel;


    public static ForecastFragment newInstance() {
        return new ForecastFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forecastViewModel = ViewModelProviders.of(getActivity()).get(ForecastViewModel.class);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.forecast_fragment, container, false);

        TextView date1 = view.findViewById(R.id.date1Label);
        TextView date1Desc = view.findViewById(R.id.date1Desc);
        ImageView date1Image = view.findViewById(R.id.date1Image);
        date1.setText(forecastViewModel.getDate1() != null ? forecastViewModel.getDate1() : "");
        date1Desc.setText(forecastViewModel.getDate1Desc() != null ? forecastViewModel.getDate1Desc() : "");
        String icon = forecastViewModel.getDate1Image();
        if(icon != null){
            date1Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        TextView date2 = view.findViewById(R.id.date2Label);
        TextView date2Desc = view.findViewById(R.id.date2Desc);
        ImageView date2Image = view.findViewById(R.id.date2Image);
        date2.setText(forecastViewModel.getDate2() != null ? forecastViewModel.getDate2() : "");
        date2Desc.setText(forecastViewModel.getDate2Desc() != null ? forecastViewModel.getDate2Desc() : "");
        icon = forecastViewModel.getDate2Image();
        if(icon != null){
            date2Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        TextView date3 = view.findViewById(R.id.date3Label);
        TextView date3Desc = view.findViewById(R.id.date3Desc);
        ImageView date3Image = view.findViewById(R.id.date3Image);
        date3.setText(forecastViewModel.getDate3() != null ? forecastViewModel.getDate3() : "");
        date3Desc.setText(forecastViewModel.getDate3Desc() != null ? forecastViewModel.getDate3Desc() : "");
        icon = forecastViewModel.getDate3Image();
        if(icon != null){
            date3Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        TextView date4 = view.findViewById(R.id.date4Label);
        TextView date4Desc = view.findViewById(R.id.date4Desc);
        ImageView date4Image = view.findViewById(R.id.date4Image);
        date4.setText(forecastViewModel.getDate4() != null ? forecastViewModel.getDate4() : "");
        date4Desc.setText(forecastViewModel.getDate4Desc() != null ? forecastViewModel.getDate4Desc() : "");
        icon = forecastViewModel.getDate4Image();
        if(icon != null){
            date4Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        return view;

    }

}
