package com.example.astroweather2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.astroweather2.fragments.MoonInfoFragment;
import com.example.astroweather2.fragments.SunInfoFragment;

class AdapterSunMoon extends FragmentPagerAdapter {

    public AdapterSunMoon(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
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
