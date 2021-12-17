package com.humiditytemperature.skytechzone.models;

public class OutsideWeatherModel {
    private String battery;
    private String cpu;
    private String feelsLike;
    private String humidity;
    private String location;
    private String outside;
    private String pressure;
    private String sunrise;
    private String sunset;
    private String tempUnit;
    private String weatherDes;
    private String windSpeed;

    public String getBattery() {
        return this.battery;
    }

    public String getCpu() {
        return this.cpu;
    }

    public String getFeelsLike() {
        return this.feelsLike;
    }

    public String getHumidity() {
        return this.humidity;
    }

    public String getLocation() {
        return this.location;
    }

    public String getOutside() {
        return this.outside;
    }

    public String getPressure() {
        return this.pressure;
    }

    public String getSunrise() {
        return this.sunrise;
    }

    public String getSunset() {
        return this.sunset;
    }

    public String getTempUnit() {
        return this.tempUnit;
    }

    public String getWeatherDes() {
        return this.weatherDes;
    }

    public String getWindSpeed() {
        return this.windSpeed;
    }

    public void setBattery(String str) {
        this.battery = str;
    }

    public void setCpu(String str) {
        this.cpu = str;
    }

    public void setFeelsLike(String str) {
        this.feelsLike = str;
    }

    public void setHumidity(String str) {
        this.humidity = str;
    }

    public void setLocation(String str) {
        this.location = str;
    }

    public void setOutside(String str) {
        this.outside = str;
    }

    public void setPressure(String str) {
        this.pressure = str;
    }

    public void setSunrise(String str) {
        this.sunrise = str;
    }

    public void setSunset(String str) {
        this.sunset = str;
    }

    public void setTempUnit(String str) {
        this.tempUnit = str;
    }

    public void setWeatherDes(String str) {
        this.weatherDes = str;
    }

    public void setWindSpeed(String str) {
        this.windSpeed = str;
    }
}
