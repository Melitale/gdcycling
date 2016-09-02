package com.example.jakubr.mymapapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.wallet.LineItem;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static GoogleMap mMap;
    private static GoogleApiClient googleApiClient;
    private MyLocationListener locationListener;

    private static final int PLACE_PICKER_REQUEST = 1;

    private Button searchButton;
    private Button drawRouteButton;
    private Button showActualPosition;
    private Button placePicker;

    private static LinearLayout routeInfoTab;
    private static LinearLayout locationChooser;
    private static LinearLayout locationChooserTitle;

    private static TextView destinationTabText;
    private static TextView sourceTabText;
    private static TextView timeTabText;
    private static TextView distanceTabText;


    private EditText searchPhrase;


    private static Context context;
    private Activity activity = this;

    protected static Marker actualLocation;
    protected static Marker destination;

    protected static JSONObject route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUp();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setUpMap(googleMap);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                LatLngBounds place = PlacePicker.getLatLngBounds(data);
                String toastMsg = String.format("Place: %s", place);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        CommonFunctions.showMessageToast("Successfully connected to google services", Toast.LENGTH_SHORT);
        locationListener = new MyLocationListener();
    }

    @Override
    public void onConnectionSuspended(int i) {
        CommonFunctions.showMessageToast("Connection to google services suspended", Toast.LENGTH_SHORT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        CommonFunctions.showMessageToast("Failed to connected to google services", Toast.LENGTH_SHORT);
    }

    private void setUpGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void changeLayoutVisibility(LinearLayout layout){
        if(layout.getVisibility() == View.VISIBLE){
            layout.setVisibility(View.GONE);
        }else{
            layout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpMap(GoogleMap googleMap){
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                CommonFunctions.showMessageToast(latLng.toString(), Toast.LENGTH_SHORT);
                LocationFinder.findLocationByLatLng(latLng);
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                changeLayoutVisibility(locationChooser);
            }
        });
    }

    private void setUp(){
        context = getApplicationContext();

        setUpGoogleApiClient();

        searchPhrase = (EditText) findViewById(R.id.searchPhrase);

        routeInfoTab = (LinearLayout) findViewById(R.id.routeInfoTab);
        locationChooser = (LinearLayout) findViewById(R.id.locationChooser);
        locationChooserTitle = (LinearLayout) findViewById(R.id.locationChooserTitle);

        searchButton = (Button) findViewById(R.id.searchButton);
        drawRouteButton = (Button) findViewById(R.id.drawRouteButton);
        showActualPosition = (Button) findViewById(R.id.showMyLocation);
        placePicker = (Button) findViewById(R.id.placePicker);

        sourceTabText = (TextView) findViewById(R.id.sourceTabText);
        destinationTabText = (TextView) findViewById(R.id.destinationTabText);
        timeTabText = (TextView) findViewById(R.id.timeTabText);
        distanceTabText = (TextView) findViewById(R.id.distanceTabText);

        routeInfoTab.setVisibility(LinearLayout.GONE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationFinder.findLocationByName(searchPhrase.getText().toString());
            }
        });

        drawRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Route route = new Route();
                if(destination != null){
                    mMap.clear();
                    CommonFunctions.placeDestinationMarker(destination.getPosition(), actualLocation.getTitle());
                    CommonFunctions.placeSourceMarker(actualLocation.getPosition(), actualLocation.getTitle());
                    route.drawRoute(mMap, context, actualLocation.getPosition(), destination.getPosition(), Route.TRANSPORT_BIKE, true, Route.LANGUAGE_POLISH);
                }else{
                    CommonFunctions.showMessageToast("Firstly choose destination", Toast.LENGTH_SHORT);
                }
            }
        });

        showActualPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions.setUpCamera(actualLocation.getPosition());
            }
        });

        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(new LatLngBounds(actualLocation.getPosition(), actualLocation.getPosition()));
                try {
                    startActivityForResult(intentBuilder.build(activity), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        locationChooser.setVisibility(View.GONE);

        locationChooserTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayoutVisibility(locationChooser);
            }
        });
    }

    public static JSONObject getRoute() { return route; }

    public static void setRoute(JSONObject route) { MapsActivity.route = route; }

    public static Marker getDestination() {
        return destination;
    }

    public static void setDestination(Marker destination) { MapsActivity.destination = destination; }

    public static void setActualLocation(Marker marker) {
        actualLocation = marker;
    }

    public static Marker getActualLocation() {
        return actualLocation;
    }

    public static Context getContext() {
        return context;
    }

    public static GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public static GoogleMap getmMap() {
        return mMap;
    }
    public static LinearLayout getRouteInfoTab() { return routeInfoTab; }

    public static void setRouteInfoTab(LinearLayout routeInfoTab) { MapsActivity.routeInfoTab = routeInfoTab; }

    public static TextView getDestinationTabText() { return destinationTabText; }

    public static void setDestinationTabText(TextView destinationTabText) { MapsActivity.destinationTabText = destinationTabText; }

    public static TextView getSourceTabText() { return sourceTabText; }

    public static void setSourceTabText(TextView sourceTabText) { MapsActivity.sourceTabText = sourceTabText; }

    public static TextView getTimeTabText() { return timeTabText; }

    public static void setTimeTabText(TextView timeTabText) { MapsActivity.timeTabText = timeTabText; }

    public static TextView getDistanceTabText() { return distanceTabText; }

    public void setDistanceTabText(TextView distanceTabText) { this.distanceTabText = distanceTabText; }
}
