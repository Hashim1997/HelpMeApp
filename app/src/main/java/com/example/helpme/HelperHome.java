package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.helpme.adapter.OrderAdapter;
import com.example.helpme.model.Helper;
import com.example.helpme.model.UserOrder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HelperHome extends AppCompatActivity {

    private RecyclerView helpOrderRecycler;
    private List<UserOrder> orderList=new ArrayList<>();
    private double latitude, longitude;
    private ImageView drawerImage;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private static final int REQUEST_CODE=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_home);

        fetchLocation();

        helpOrderRecycler=findViewById(R.id.orderList);
        drawerImage=findViewById(R.id.drawerSwitch);
        drawerLayout=findViewById(R.id.home_Screen);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        drawerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                drawerImage.setVisibility(View.GONE);
            }
        });

        NavigationView viewNav=findViewById(R.id.navView);
        viewNav.setItemIconTintList(null);

        if (drawerLayout.isDrawerVisible(GravityCompat.END)){
            drawerImage.setVisibility(View.GONE);
        }

        View headerView=viewNav.getHeaderView(0);
        viewNav.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);

        LinearLayout layout=headerView.findViewById(R.id.linearHeader);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                drawerImage.setVisibility(View.VISIBLE);
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
                            HelperHome.this.finish();
                            break;

                        default:
                            return true;
                }
                return true;
            }
        });


        SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
        String email=preferences.getString("email","empty");
        retrieveHelperData(email);


        UserOrder order=new UserOrder();
        order.setFullName("Hashim Abu Alraghib");
        order.setLatitude(31.9805);
        order.setLongitude(35.8377);
        order.setCarColor("Black");
        order.setCarType("BMMW");


        UserOrder order3=new UserOrder();
        order3.setFullName("Ahmad Abd");
        order3.setLatitude(31.9513);
        order3.setLongitude(35.9195);
        order3.setCarColor("SILVER");
        order3.setCarType("FORD");

        UserOrder order4=new UserOrder();
        order4.setFullName("Ahmad Abd");
        order4.setLatitude(31.9805);
        order4.setLongitude(35.8377);
        order4.setCarColor("SILVER");
        order4.setCarType("FORD");

        UserOrder order5=new UserOrder();
        order5.setFullName("Ahmad Abd");
        order5.setLatitude(31.9539);
        order5.setLongitude(35.9106);
        order5.setCarColor("SILVER");
        order5.setCarType("FORD");

        UserOrder order6=new UserOrder();
        order6.setFullName("Ahmad Abd");
        order6.setLatitude(32.5568);
        order6.setLongitude(35.8469);
        order6.setCarColor("SILVER");
        order6.setCarType("FORD");

        UserOrder order7=new UserOrder();
        order7.setFullName("Ahmad Abd");
        order7.setLatitude(32.0346);
        order7.setLongitude(35.7269);
        order7.setCarColor("SILVER");
        order7.setCarType("FORD");

        UserOrder order8=new UserOrder();
        order8.setFullName("Ahmad Abd");
        order8.setLatitude(32.0189);
        order8.setLongitude(35.8819);
        order8.setCarColor("SILVER");
        order8.setCarType("FORD");

        UserOrder order9=new UserOrder();
        order9.setFullName("Ahmad Abd");
        order9.setLatitude(29.5221);
        order9.setLongitude(35.4502);
        order9.setCarColor("SILVER");
        order9.setCarType("FORD");



        orderList.add(order);
        orderList.add(order3);
        orderList.add(order);
        orderList.add(order3);
        orderList.add(order4);
        orderList.add(order5);
        orderList.add(order6);
        orderList.add(order7);
        orderList.add(order8);
        orderList.add(order9);



    }

    private void fetchLocation() {


        fusedLocationProviderClient= new FusedLocationProviderClient(this);
        locationRequest=new LocationRequest();

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }

        else {
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i("curLat",String.valueOf(locationResult.getLastLocation().getLatitude()));
                Log.i("curLon",String.valueOf(locationResult.getLastLocation().getLongitude()));

            }
        },getMainLooper());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }


    private void setupRecycler(Context context, List<UserOrder> orders, Location location){
        OrderAdapter adapter=new OrderAdapter(context,orders,location,HelperHome.this);
        helpOrderRecycler.setAdapter(adapter);
        helpOrderRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void retrieveHelperData(String email){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        reference.child("Helpers").child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Helper helper=dataSnapshot.getValue(Helper.class);
                if (helper != null) {
                    latitude=31.8569;
                    longitude=35.4865;
                    Location location=new Location("LocationHelper");
                    location.setLongitude(longitude);
                    location.setLatitude(latitude);
                    Log.i("coordinate", String.valueOf(longitude+latitude));
                    setupRecycler(getApplicationContext(),orderList,location);
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
