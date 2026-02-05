package com.example.weatherapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class for managing SharedPreferences operations.
 * Implemented in Java to satisfy the requirement for Java+Kotlin combination.
 * 
 * This class handles persistence of user preferences, specifically the last searched city,
 * which is auto-loaded on app launch as per requirement #3.
 */
public class PreferencesHelper {
    
    private static final String PREFS_NAME = "WeatherAppPreferences";
    private static final String KEY_LAST_CITY = "last_searched_city";
    
    private final SharedPreferences sharedPreferences;
    
    /**
     * Constructor for PreferencesHelper.
     * 
     * @param context Application context for accessing SharedPreferences
     */
    public PreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Saves the last searched city name to SharedPreferences.
     * Called after a successful weather search to enable auto-loading on next app launch.
     * 
     * @param cityName The name of the city that was searched
     */
    public void saveLastCity(String cityName) {
        if (cityName != null && !cityName.trim().isEmpty()) {
            sharedPreferences.edit()
                    .putString(KEY_LAST_CITY, cityName.trim())
                    .apply();
        }
    }
    
    /**
     * Retrieves the last searched city name from SharedPreferences.
     * Returns null if no city has been previously searched.
     * 
     * @return The last searched city name, or null if none exists
     */
    public String getLastCity() {
        return sharedPreferences.getString(KEY_LAST_CITY, null);
    }
    
    /**
     * Clears the last searched city from SharedPreferences.
     * Useful for testing or when user wants to reset the app state.
     */
    public void clearLastCity() {
        sharedPreferences.edit()
                .remove(KEY_LAST_CITY)
                .apply();
    }
    
    /**
     * Checks if a last searched city exists in preferences.
     * 
     * @return true if a city has been previously saved, false otherwise
     */
    public boolean hasLastCity() {
        return getLastCity() != null;
    }
}
