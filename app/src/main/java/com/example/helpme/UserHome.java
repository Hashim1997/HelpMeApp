package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.helpme.model.User;
import com.example.helpme.model.UserOrder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Locale;
import java.util.Objects;

public class UserHome extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout drawerLayout;
    private ImageView drawerUser;
    private EditText userLoc;
    private Location mLastKnownLocation;
    private final int DEFAULT_ZOOM=15;
    private final LatLng mDefaultLocation=new LatLng(31.8569,35.4865);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        drawerLayout=findViewById(R.id.home_user);
        drawerUser=findViewById(R.id.drawerSwitchUser);
        ImageView locBtn = findViewById(R.id.getLocBtn);
        userLoc=findViewById(R.id.userLoc);

        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLoc.getText().toString().equals(""))
                getDeviceLocation();
                else
                    Toast.makeText(getApplicationContext(),userLoc.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView viewNav=findViewById(R.id.navViewUser);
        viewNav.setItemIconTintList(null);
        View headerView=viewNav.getHeaderView(0);
        viewNav.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);

        LinearLayout layout=headerView.findViewById(R.id.linearHeader);

        drawerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                drawerUser.setVisibility(View.GONE);
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                drawerUser.setVisibility(View.VISIBLE);
            }
        });

        SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        viewNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id){
                    case R.id.profile_user:
                        Intent intentProfile=new Intent(UserHome.this,Profile.class);
                        startActivity(intentProfile);
                        break;

                    case R.id.order_layout:
                        Toast.makeText(getApplicationContext(),"Order Layout",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.logout_user:
                        Toast.makeText(getApplicationContext(),"Login Out",Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.apply();
                        Intent intent1=new Intent(UserHome.this,AccountChooseActivity.class);
                        startActivity(intent1);
                        UserHome.this.finish();
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
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
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient=new FusedLocationProviderClient(this);
        try {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            assert mLastKnownLocation != null;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            sendOrder(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                        } else {
                            Log.d("State", "Current location is null. Using defaults.");
                            Log.e("State", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void sendOrder(final Double lat, final Double lon){

        SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
        String email=preferences.getString("email","empty");
        Log.i("emailX",email);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("Users");
        reference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if (user !=null){
                    UserOrder order=new UserOrder();
                    order.setCarType(user.getCarType());
                    order.setCarColor(user.getCarColor());
                    order.setFullName(user.getFullName());
                    order.setLongitude(lon);
                    order.setLatitude(lat);
                    order.setEmail(user.getEmail());
                    saveOrder(order);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();
                Log.i("DataBaseError",databaseError.getDetails());
            }
        });
    }

    private void saveOrder(UserOrder order1){
        FirebaseDatabase orderDataBase=FirebaseDatabase.getInstance();
        DatabaseReference orderReference=orderDataBase.getReference("Orders");
        orderReference.child(order1.getEmail()).setValue(order1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"SuccessFul",Toast.LENGTH_SHORT).show();
                Dialog dialog=new Dialog(UserHome.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.wait_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                //dialog.setCancelable(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed While Submit",Toast.LENGTH_SHORT).show();
                Log.i("Error", Objects.requireNonNull(e.getMessage()));
            }
        });
    }
}
