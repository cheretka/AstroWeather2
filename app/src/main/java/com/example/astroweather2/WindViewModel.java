package com.example.astroweather2;

import android.arch.lifecycle.ViewModel;

public class WindViewModel extends ViewModel {

    private String windForce;
    private String windDirection;
    private String humidity;
    private String visibility;

    public WindViewModel(String windForce, String windDirection, String humidity, String visibility) {
        this.windForce = windForce;
        this.windDirection = windDirection;
        this.humidity = humidity;
        this.visibility = visibility;
    }

    public WindViewModel() {
    }

    public String getWindForce() {
        return windForce;
    }

    public void setWindForce(String windForce) {
        this.windForce = windForce;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
