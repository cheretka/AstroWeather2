package com.example.astroweather2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WindFragment extends Fragment {

    private WindViewModel windViewModel;
    
    private TextView windForceText;
    private TextView windDestinationText;
    private TextView humidityText;
    private TextView visibilityText;

    public static WindFragment newInstance() {
        WindFragment fragment = new WindFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        windViewModel = ViewModelProviders.of(getActivity()).get(WindViewModel.class);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wind_fragment, container,
                false);
        
        windForceText = view.findViewById(R.id.windForceText);
        windDestinationText = view.findViewById(R.id.windDirectionText);
        humidityText = view.findViewById(R.id.humidityText);
        visibilityText = view.findViewById(R.id.visibilityText);
        
        windForceText.setText(windViewModel.getWindForce() != null ? windViewModel.getWindForce() : "");
        windDestinationText.setText(windViewModel.getWindDirection() != null ? windViewModel.getWindDirection() : "");
        humidityText.setText(windViewModel.getHumidity() != null ? windViewModel.getHumidity() : "");
        visibilityText.setText(windViewModel.getVisibility() != null ? windViewModel.getVisibility() : "");

        return view;

    }

}
