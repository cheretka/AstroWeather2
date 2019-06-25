package com.example.astroweather2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class InfoViewPager extends FragmentPagerAdapter {

    public InfoViewPager (FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return SunInfoFragment.newInstance();
        } else if(position==1) {
            return MoonInfoFragment.newInstance();
        } else if(position==2) {
            return LocationFragment.newInstance();
        } else if(position==3) {
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
