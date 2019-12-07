package com.example.helpme;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

//an activity to show user location for helper in location button click
public class UserLocation extends FragmentActivity implements OnMapReadyCallback {

    //latitude and longitude
    private Double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        Bundle bundle=getIntent().getExtras();

        //check if data not null
        if (bundle != null) {
            lat = bundle.getDouble("lat");
            lon = bundle.getDouble("lon");
        }
        else{
            Log.i("latitude","it equal null");
            Log.i("longitude","it equal null");
            Toast.makeText(getApplicationContext(),"Error Occurred",Toast.LENGTH_LONG).show();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Objects.requireNonNull(mapFragment).getMapAsync(this);
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
        //add zoom enabled for helper to zoom user location
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        double latitude= lat;
        double longitude=lon;

        // Add a marker to user coordinate and move the camera
        LatLng userLocation=new LatLng(latitude,longitude);
        googleMap.addMarker(new MarkerOptions().position(userLocation).title("User Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15),2000,null);

    }
}
