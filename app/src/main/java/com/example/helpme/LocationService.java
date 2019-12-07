package com.example.helpme;

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

import com.example.helpme.model.HelperLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//background service class to execute code in background
public class LocationService extends Service {

    private static final int CHANNEL_ID=100;
    public LocationService() {

    }

    //to send and receive intent data from other services or activity
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

    //check if location enabled and show notification
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

    //when service start
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //when service destroyed or stopped
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Service is stopped",Toast.LENGTH_SHORT).show();
    }

    //get user location
    private void fetchLocation() {

        //FusedLocationProviderClient api to get live location from device by google
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getApplicationContext());
        LocationRequest locationRequest = new LocationRequest();

        //service properties
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);
        //on update listener
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Toast.makeText(getApplicationContext(), locationResult.getLastLocation()
                        .getLatitude() +" "+ locationResult.getLastLocation().getLongitude(),Toast.LENGTH_SHORT).show();
                saveHelperLocation(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

            }
        },getMainLooper());
    }

    //save helper location for user in firebase real tome database
    private void saveHelperLocation(Double lat, Double lon){
        SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
        String email=preferences.getString("email","empty");

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("ActiveLocation");

        HelperLocation location=new HelperLocation();
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setKey(email);

        reference.child(email).setValue(location).addOnSuccessListener(new OnSuccessListener<Void>() {
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
