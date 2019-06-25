package com.example.astroweather2;

import android.arch.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {

    private String temp;
    private String pressure;
    private String weather;

    public LocationViewModel(String temp, String pressure, String weather) {
        this.temp = temp;
        this.pressure = pressure;
        this.weather = weather;
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
}
