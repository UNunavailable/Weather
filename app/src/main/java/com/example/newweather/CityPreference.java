package com.example.newweather;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity)
    {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // Default
    String getCity()
    {
        return prefs.getString("city", "Альметьевск");
    }

    void setCity(String city)
    {
        prefs.edit().putString("city", city).apply();
    }
}
