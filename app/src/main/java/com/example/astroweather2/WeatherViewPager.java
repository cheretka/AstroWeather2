package com.example.astroweather2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class WeatherViewPager extends FragmentPagerAdapter {

    public WeatherViewPager (FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return LocationFragment.newInstance();
        } else if(position==1) {
            return WindFragment.newInstance();
        } else {
            return ForecastFragment.newInstance();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
