package com.example.weatherapp.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Helper object for fetching device location using Google Play Services.
 * 
 * Uses FusedLocationProviderClient for efficient, battery-friendly location access.
 * Implements defensive permission checking to prevent SecurityExceptions.
 */
object LocationHelper {

    /**
     * Fetches the current device location and invokes callback with coordinates.
     * 
     * Uses HIGH_ACCURACY priority for precise location data, which is important
     * for accurate weather information. Includes defensive permission check
     * before attempting location access to prevent crashes.
     * 
     * @param context Android context for accessing location services
     * @param onLocationFetched Callback invoked with latitude and longitude on success
     */
    fun fetchLastKnownLocation(
        context: Context,
        onLocationFetched: (latitude: Double, longitude: Double) -> Unit
    ) {
        // Defensive check: Verify permission before accessing location
        if (!hasLocationPermission(context)) {
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        // Cancellation token allows stopping the location request if needed
        val cancellationTokenSource = CancellationTokenSource()

        try {
            fusedLocationClient.getCurrentLocation(
                // HIGH_ACCURACY provides best precision for weather data
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                // Defensive null check: location can be null if unavailable
                location?.let {
                    onLocationFetched(it.latitude, it.longitude)
                }
            }.addOnFailureListener { exception ->
                // Gracefully handle location fetch failures (GPS disabled, timeout, etc.)
                // In production, consider showing user-friendly error message
                exception.printStackTrace()
            }
        } catch (e: SecurityException) {
            // Extra safety: Catch SecurityException in case permission was revoked mid-execution
            e.printStackTrace()
        }
    }

    /**
     * Checks if the app has fine location permission.
     * 
     * Defensive helper to prevent SecurityException when accessing location services.
     * 
     * @param context Android context for permission check
     * @return true if ACCESS_FINE_LOCATION permission is granted
     */
    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}