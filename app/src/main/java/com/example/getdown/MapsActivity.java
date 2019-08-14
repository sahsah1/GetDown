package com.example.getdown;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String apiKey = BuildConfig.API_KEY;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // Widgets
    private AutocompleteSupportFragment mAutocompleteSupportFragment;
    private EditText mSearchText;
    private ImageView mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAutocompleteSupportFragment = (AutocompleteSupportFragment) (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_input);
        mAutocompleteSupportFragment.setHint(getString(R.string.search_hint));
        mSearchText = (EditText) mAutocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        getLocationPermissions();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");

        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            // The search bar interferes with this button anyway
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private void init(){
        Log.d(TAG, "init: Initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if(actionID == EditorInfo.IME_ACTION_SEARCH
                        || actionID == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    // Execute search method
                    geoLocate();
                }

                return false;
            }
        });

        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);
        // Specify the types of place data to return.
        mAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS));
        mAutocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);

        mAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mSearchText.setText(place.getAddress());
                moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getAddress());
                Log.d(TAG, "onPlaceSelected: Place info: " +place.getWebsiteUri());
                Log.d(TAG, "onPlaceSelected: Place info: " +place.getPhoneNumber());
                Log.d(TAG, "onPlaceSelected: Place info: " +place.getViewport());
                Log.d(TAG, "onPlaceSelected: Place info: " +place.getName());
                Log.d(TAG, "onPlaceSelected: Place info: " +place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "onError: An error occurred " + status.getStatusMessage());
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked GPS icon.");
                getDeviceLocation();
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: Geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> results = new ArrayList<>();

        try{
            results = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if(results.size() > 0){
            Address address = results.get(0);
            Log.d(TAG, "geoLocate: Found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: Getting the device current location.");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found location!");

                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        }
                        else{
                            Log.d(TAG, "onComplete: Current location is null.");
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: Moving camera to: Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(title != "My Location"){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(markerOptions);
        }
    }

    private void getLocationPermissions() {
        Log.d(TAG, "getLocationPermissions: Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                        return;
                    }
                }
                mLocationPermissionsGranted = true;
                Log.d(TAG, "onRequestPermissionsResult: Permission granted");
            }
        }
    }
}

