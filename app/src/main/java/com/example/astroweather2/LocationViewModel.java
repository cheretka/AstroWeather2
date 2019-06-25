package com.example.astroweather2;

import android.arch.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {

    private String location;
    private String latitude;
    private String longitude;
    private String time;
    private String temp;
    private String pressure;
    private String weather;

    public LocationViewModel(String location, String latitude, String longitude, String time, String temp, String pressure, String weather) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.temp = temp;
        this.pressure = pressure;
        this.weather = weather;
    }

    public LocationViewModel() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
