package com.example.astroweather2.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.astroweather2.R;
import com.example.astroweather2.viewmodels.WindViewModel;

public class WindFragment extends Fragment {

    private WindViewModel windViewModel;

    public static WindFragment newInstance() {
        return new WindFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windViewModel = ViewModelProviders.of(getActivity()).get(WindViewModel.class);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wind_fragment, container, false);

        TextView windForceText = view.findViewById(R.id.windForceText);
        TextView windDestinationText = view.findViewById(R.id.windDirectionText);
        TextView humidityText = view.findViewById(R.id.humidityText);
        TextView visibilityText = view.findViewById(R.id.visibilityText);

        if(windViewModel.getWindForce() != null){
            if (windViewModel.getUnit().equals("imperial")){
                windForceText.setText(windViewModel.getWindForce()+" miles/hour" );
            }
            else{
                windForceText.setText(windViewModel.getWindForce()+" meter/sec" );
            }
        }else{
            windForceText.setText( "");
        }


        windDestinationText.setText(windViewModel.getWindDirection() != null ? windViewModel.getWindDirection()+" degrees" : "");
        humidityText.setText(windViewModel.getHumidity() != null ? windViewModel.getHumidity()+" %" : "");
        visibilityText.setText(windViewModel.getVisibility() != null ? windViewModel.getVisibility()+" m" : "");

        return view;

    }

}
