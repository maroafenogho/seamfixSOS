package com.maro.seamfixsos.util;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class GpsUtils {
    private final Context context;
    private final SettingsClient settingsClient;
    private final LocationSettingsRequest locationSettingsRequest;
    private final LocationManager locationManager;

    public GpsUtils(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        settingsClient = LocationServices.getSettingsClient(context);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();

        builder.setAlwaysShow(true);
    }

    public void turnOnGps(onGpsListener onGpsListener) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (onGpsListener != null) {
                onGpsListener.gpsStatus(true);
            } else {
                settingsClient
                        .checkLocationSettings(locationSettingsRequest)
                        .addOnSuccessListener((Activity) context, locationSettingsResponse -> {

                            assert false;
                            onGpsListener.gpsStatus(true);
                        })
                        .addOnFailureListener((Activity) context, e -> {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                        resolvableApiException.startResolutionForResult((Activity) context, Constants.GPS_REQUEST);
                                    } catch (IntentSender.SendIntentException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Log.e("Error met", "onFailure: " + e.toString());
                            }
                        });
            }
        }
    }

    public interface onGpsListener {
        void gpsStatus(boolean isGPSEnable);
    }
}
