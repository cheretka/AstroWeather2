package com.example.astroweather2.viewmodels;

import android.arch.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {

    private String temp;
    private String pressure;
    private String weather;
    private String icon;
    private String unit;

    public LocationViewModel(String temp, String pressure, String weather, String icon) {
        this.temp = temp;
        this.pressure = pressure;
        this.weather = weather;
        this.icon = icon;
    }

    public LocationViewModel() {
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
