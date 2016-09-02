package com.example.jakubr.mymapapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class MyLocation {

    private static Context context = MapsActivity.getContext();;
    private static GoogleApiClient googleApiClient = MapsActivity.getGoogleApiClient();

    protected static LatLng getMyLocation() {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(location != null){
                String message = "Lat : " + location.getLatitude() + ", Lon : " + location.getLongitude();
                CommonFunctions.showMessageToast(message, Toast.LENGTH_LONG);
            }else{
                CommonFunctions.showMessageToast("Error", Toast.LENGTH_LONG);
            }
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        return new LatLng(0.00, 0.00);
    }
}
