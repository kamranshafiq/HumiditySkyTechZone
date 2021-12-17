package com.humiditytemperature.skytechzone.utils;

public enum CustomDistanceType {
    KILOMETERS(" km/h", 3.6d),
    MILES(" mph", 2.236936292054402d);
    
    private String sign;
    private double value;

    private CustomDistanceType(String str, double d) {
        this.sign = str;
        this.value = d;
    }

    public String sign() {
        return this.sign;
    }

    public double value() {
        return this.value;
    }
}
