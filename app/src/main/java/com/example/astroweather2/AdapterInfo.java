package com.example.astroweather2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.astroweather2.fragments.ForecastFragment;
import com.example.astroweather2.fragments.LocationFragment;
import com.example.astroweather2.fragments.MoonInfoFragment;
import com.example.astroweather2.fragments.SunInfoFragment;
import com.example.astroweather2.fragments.WindFragment;

public class AdapterInfo extends FragmentPagerAdapter {

    public AdapterInfo(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return LocationFragment.newInstance();
        } else if(position==1) {
            return WindFragment.newInstance();
        } else if(position==2) {
            return ForecastFragment.newInstance();
        } else if(position==3) {
            return SunInfoFragment.newInstance();
        } else {
            return MoonInfoFragment.newInstance();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
