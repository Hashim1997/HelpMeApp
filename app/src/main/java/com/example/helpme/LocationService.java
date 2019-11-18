package com.example.helpme;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;

public class LocationService extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private static final int CHANNEL_ID=100;
    public LocationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationEnabled();
        fetchLocation();
    }

    private void locationEnabled () {
        LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            assert lm != null;
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"100")
                    .setSmallIcon(R.drawable.close_icon)
                    .setContentTitle("Location")
                    .setContentText("Please Enable Location & Internet Service")
                    .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(false);
            NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
            managerCompat.notify(CHANNEL_ID,builder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Service is stopped",Toast.LENGTH_SHORT).show();
    }

    //get user location
    private void fetchLocation() {

        fusedLocationProviderClient= new FusedLocationProviderClient(getApplicationContext());
        locationRequest=new LocationRequest();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i("curLat",String.valueOf(locationResult.getLastLocation().getLatitude()));
                Log.i("curLon",String.valueOf(locationResult.getLastLocation().getLongitude()));

                Toast.makeText(getApplicationContext(), locationResult.getLastLocation()
                        .getLatitude() +" "+ locationResult.getLastLocation().getLongitude(),Toast.LENGTH_SHORT).show();
                saveHelperLocation(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

            }
        },getMainLooper());
    }

    //save helper location for user
    private void saveHelperLocation(Double lat, Double lon){
        SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
        String email=preferences.getString("email","empty");
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("ActiveLocation");
        HashMap<String,Double> loc=new HashMap<>();
        loc.put("lat",lat);
        loc.put("lon",lon);
        reference.child(email).setValue(loc).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Statues","Successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Statues","Failed");
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
