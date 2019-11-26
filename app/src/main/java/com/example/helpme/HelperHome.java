package com.example.helpme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.adapter.OrderAdapter;
import com.example.helpme.model.HelperLocation;
import com.example.helpme.model.UserOrder;
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

import java.util.ArrayList;
import java.util.List;

public class HelperHome extends AppCompatActivity {

    private RecyclerView helpOrderRecycler;
    private final List<UserOrder> orderList=new ArrayList<>();
    private DrawerLayout drawerLayout;
    private static final int REQUEST_CODE=101;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_home);

        LocationManager lm = (LocationManager) getSystemService(Context. LOCATION_SERVICE ) ;

        boolean gps_enabled;
        boolean network_enabled;

        assert lm != null;
        gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;

        Intent intentService=new Intent(getApplicationContext(),LocationService.class);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestLocationPermission();
            startService(intentService);
        }
        else{

            if (!gps_enabled && !network_enabled)
                openLocationSetting();

            startService(intentService);
        }

        helpOrderRecycler=findViewById(R.id.orderList);
        ImageView drawerImage = findViewById(R.id.drawerSwitch);
        drawerLayout=findViewById(R.id.home_Screen);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        drawerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView viewNav=findViewById(R.id.navView);
        viewNav.setItemIconTintList(null);


        View headerView=viewNav.getHeaderView(0);
        viewNav.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);

        LinearLayout layout=headerView.findViewById(R.id.linearHeader);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        viewNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();

                switch (id){
                    case R.id.profile_layout:
                        Intent intent=new Intent(HelperHome.this,Profile.class);
                        startActivity(intent);
                        break;

                        case R.id.logout:
                            Toast.makeText(getApplicationContext(),"Login Out",Toast.LENGTH_SHORT).show();
                            editor.clear();
                            editor.apply();
                            Intent intent1=new Intent(HelperHome.this,AccountChooseActivity.class);
                            startActivity(intent1);
                            stopService(new Intent(getApplicationContext(),LocationService.class));
                            HelperHome.this.finish();
                            break;

                        default:
                            return true;
                }
                return true;
            }
        });


        SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
        email=preferences.getString("email","empty");


        FirebaseDatabase databaseOrder = FirebaseDatabase.getInstance();
        DatabaseReference referenceOrder = databaseOrder.getReference();
        referenceOrder.child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    UserOrder order=dataSnapshot1.getValue(UserOrder.class);
                    assert order != null;
                    order.setKey(dataSnapshot1.getKey());
                    orderList.add(order);
                }
                retrieveHelperData(email,orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();
            }
        });




    }

    private void requestLocationPermission() {
        Dexter.withActivity(HelperHome.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                startService(new Intent(getApplicationContext(),LocationService.class));
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()){
                    openLocationSetting();
                }
            }
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        });
    }

    private void openLocationSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HelperHome.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings(){
        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(getApplicationContext(),LocationService.class));
            }
        }
    }

    private void setupRecycler(Context context, List<UserOrder> orders, Location location){
        OrderAdapter adapter=new OrderAdapter(context,orders,location,HelperHome.this);
        helpOrderRecycler.setAdapter(adapter);
        helpOrderRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }



    private void retrieveHelperData(String email, final List<UserOrder> orders){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        reference.child("ActiveLocation").child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HelperLocation helperLocation=dataSnapshot.getValue(HelperLocation.class);
                if (helperLocation != null) {
                        Location location=new Location("LocationHelper");
                        location.setLongitude(helperLocation.getLongitude());
                        location.setLatitude(helperLocation.getLatitude());
                        Log.i("coordinate", String.valueOf(helperLocation.getLongitude()+helperLocation.getLatitude()));
                        setupRecycler(getApplicationContext(),orders,location);
                }
                else
                    Toast.makeText(getApplicationContext(),"No Data available",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
