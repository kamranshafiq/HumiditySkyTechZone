package com.humiditytemperature.skytechzone.utils;

public enum CustomTemperatureType {
    CELCIUS(" °C"),
    FAHRENAYT(" °F");
    
    private final String sign;

    private CustomTemperatureType(String str) {
        this.sign = str;
    }

    public String sign() {
        return this.sign;
    }
}
