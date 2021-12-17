package com.humiditytemperature.skytechzone.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

public class TypefacesFonts {

    public static final String REGULAR = "regular";

    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    public static Typeface get(Context c, String name) {
        Typeface typeface;
        synchronized (cache) {
            if (!cache.containsKey(name)) {
                cache.put(name, Typeface.createFromAsset(c.getAssets(), String.format("fonts/%s.ttf", name)));
            }
            typeface = cache.get(name);
        }
        return typeface;
    }

}
