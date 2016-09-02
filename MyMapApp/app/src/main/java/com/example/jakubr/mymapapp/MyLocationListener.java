package com.example.jakubr.mymapapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;


public class MyLocationListener implements LocationListener {

    private static Location currentLocation;
    private Context context = MapsActivity.getContext();
    private GoogleApiClient googleApiClient = MapsActivity.getGoogleApiClient();

    public MyLocationListener(){
        startLocationUpdates();
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(currentLocation != null && location != null){
            currentLocation.set(location);
            MapsActivity.getActualLocation().remove();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CommonFunctions.placeSourceMarker(latLng, "Im here");
            CommonFunctions.showMessageToast("location changed", Toast.LENGTH_SHORT);
        }else{
            currentLocation = new Location(location);
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
            if(MapsActivity.getActualLocation() != null){
                MapsActivity.getActualLocation().remove();
            }
            CommonFunctions.placeSourceMarker(MyLocation.getMyLocation(), "Im here");
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
}
