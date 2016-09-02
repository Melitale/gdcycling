package com.example.jakubr.mymapapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakubr on 31/08/16.
 */
public class LocationFinder {

    private static Context context = MapsActivity.getContext();
    private static Geocoder geocoder = new Geocoder(context);

    public static void findLocationByName(String locationName){
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!addressList.isEmpty()){
            LatLng latLng = new LatLng(addressList.get(0).getLatitude(),
            addressList.get(0).getLongitude());
            CommonFunctions.placeDestinationMarker(latLng, addressList.get(0).getLocality());
            CommonFunctions.showMessageToast("Location successfully found", Toast.LENGTH_SHORT);
        }else{
            CommonFunctions.showMessageToast("There is no location named like this.", Toast.LENGTH_SHORT);
        }
    }

    public static void findLocationByLatLng(LatLng latLng){
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!addressList.isEmpty()){
            CommonFunctions.placeDestinationMarker(latLng, addressList.get(0).getLocality() + ", " + addressList.get(0).getAddressLine(0));
            CommonFunctions.showMessageToast("Location successfully found", Toast.LENGTH_SHORT);
        }else{
            CommonFunctions.showMessageToast("There is no location named like this.", Toast.LENGTH_SHORT);
        }
    }
}
