package com.example.jakubr.mymapapp;

import android.content.Context;
import android.location.Location;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonFunctions {

    private static Context context = MapsActivity.getContext();;
    private static GoogleMap mMap = MapsActivity.getmMap();
    public CommonFunctions(){
    }

    protected static void showMessageToast(String text, int duration) {
        Toast informationToast = Toast.makeText(context, text, duration);
        informationToast.show();
    }

    protected static void placeSourceMarker(LatLng location, String title){
        if(MapsActivity.getActualLocation() != null){
            MapsActivity.getActualLocation().remove();
        }
        if(location != null){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .flat(true)
                    .rotation(245)
                    .title(title));
            setUpCamera(marker.getPosition());
            MapsActivity.setActualLocation(marker);
        }
    }

    protected static void placeDestinationMarker(LatLng location, String title){
        if(MapsActivity.getDestination() != null){
            MapsActivity.getDestination().remove();
        }
        if(location != null){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(title));
            setUpCamera(marker.getPosition());
            MapsActivity.setDestination(marker);
        }
    }

    protected static void setUpCamera(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .tilt(45)
                .zoom(17.0f)
                .bearing(90)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }

    protected static void showRouteParams() throws JSONException {
        if(MapsActivity.getRoute() != null){
            JSONObject routes = MapsActivity.getRoute();
            JSONArray arrayLegs = routes.getJSONArray("legs");
            JSONObject legs = arrayLegs.getJSONObject(0);

            MapsActivity.getSourceTabText().setText(legs.getString("start_address"));
            MapsActivity.getDestinationTabText().setText(legs.getString("end_address"));
            MapsActivity.getTimeTabText().setText("Estimate time of trip: " + legs.getJSONObject("duration").getString("text"));
            MapsActivity.getDistanceTabText().setText("Distance: " + legs.getJSONObject("distance").getString("text"));
            MapsActivity.getRouteInfoTab().setVisibility(LinearLayout.VISIBLE);

        }else
            System.out.print("route is null");
    }
}
