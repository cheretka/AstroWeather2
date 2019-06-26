package com.example.astroweather2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastFragment extends Fragment {

    private ForecastViewModel forecastViewModel;

    private TextView date1;
    private TextView date1Temp;
    private TextView date1Desc;
    private ImageView date1Image;

    private TextView date2;
    private TextView date2Temp;
    private TextView date2Desc;
    private ImageView date2Image;

    private TextView date3;
    private TextView date3Temp;
    private TextView date3Desc;
    private ImageView date3Image;

    private TextView date4;
    private TextView date4Temp;
    private TextView date4Desc;
    private ImageView date4Image;

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

        date1 = view.findViewById(R.id.date1Label);
        date1Temp = view.findViewById(R.id.date1Temp);
        date1Desc = view.findViewById(R.id.date1Desc);
        date1Image = view.findViewById(R.id.date1Image);

        date2 = view.findViewById(R.id.date2Label);
        date2Temp = view.findViewById(R.id.date2Temp);
        date2Desc = view.findViewById(R.id.date2Desc);
        date2Image = view.findViewById(R.id.date2Image);

        date3 = view.findViewById(R.id.date3Label);
        date3Temp = view.findViewById(R.id.date3Temp);
        date3Desc = view.findViewById(R.id.date3Desc);
        date3Image = view.findViewById(R.id.date3Image);

        date4 = view.findViewById(R.id.date4Label);
        date4Temp = view.findViewById(R.id.date4Temp);
        date4Desc = view.findViewById(R.id.date4Desc);
        date4Image = view.findViewById(R.id.date4Image);

        date1.setText(forecastViewModel.getDate1() != null ? forecastViewModel.getDate1() : "");
        date1Temp.setText(forecastViewModel.getDate1Temp() != null ? forecastViewModel.getDate1Temp() : "");
        date1Desc.setText(forecastViewModel.getDate1Desc() != null ? forecastViewModel.getDate1Desc() : "");
        String icon = forecastViewModel.getDate1Image();
        if(icon != null){
            date1Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        date2.setText(forecastViewModel.getDate2() != null ? forecastViewModel.getDate2() : "");
        date2Temp.setText(forecastViewModel.getDate2Temp() != null ? forecastViewModel.getDate2Temp() : "");
        date2Desc.setText(forecastViewModel.getDate2Desc() != null ? forecastViewModel.getDate2Desc() : "");
        icon = forecastViewModel.getDate2Image();
        if(icon != null){
            date2Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        date3.setText(forecastViewModel.getDate3() != null ? forecastViewModel.getDate3() : "");
        date3Temp.setText(forecastViewModel.getDate3Temp() != null ? forecastViewModel.getDate3Temp() : "");
        date3Desc.setText(forecastViewModel.getDate3Desc() != null ? forecastViewModel.getDate3Desc() : "");
        icon = forecastViewModel.getDate3Image();
        if(icon != null){
            date3Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        date4.setText(forecastViewModel.getDate4() != null ? forecastViewModel.getDate4() : "");
        date4Temp.setText(forecastViewModel.getDate4Temp() != null ? forecastViewModel.getDate4Temp() : "");
        date4Desc.setText(forecastViewModel.getDate4Desc() != null ? forecastViewModel.getDate4Desc() : "");
        icon = forecastViewModel.getDate4Image();
        if(icon != null){
            date4Image.setImageResource(getContext().getResources().getIdentifier(icon, "drawable", getContext().getPackageName()));
        }

        return view;

    }

}
