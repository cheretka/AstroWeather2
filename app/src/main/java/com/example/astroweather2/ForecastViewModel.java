package com.example.astroweather2;

import android.arch.lifecycle.ViewModel;

public class ForecastViewModel extends ViewModel {

    private String date1;
    private String date1Text;

    public ForecastViewModel() {
    }

    public ForecastViewModel(String date1, String date1Text) {
        this.date1 = date1;
        this.date1Text = date1Text;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate1Text() {
        return date1Text;
    }

    public void setDate1Text(String date1Text) {
        this.date1Text = date1Text;
    }
}
