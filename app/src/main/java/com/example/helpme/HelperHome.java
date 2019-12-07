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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;

//an activity class for helper
public class HelperHome extends AppCompatActivity {

    //define object and variable
    private RecyclerView helpOrderRecycler;
    private final List<UserOrder> orderList=new ArrayList<>();
    private DrawerLayout drawerLayout;
    private static final int REQUEST_CODE=101;
    private String email;
    private static final int FINE_LOCATION_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_home);


        //setup the drawer layout
        helpOrderRecycler=findViewById(R.id.orderList);
        ImageView drawerImage = findViewById(R.id.drawerSwitch);
        drawerLayout=findViewById(R.id.home_Screen);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //request location permission
        requestLocationPermission();

        //get email from offline storage
        SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        //drawer button to pull drawer
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

        //drawer header click to hide drawer
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        //menu items select options drawer
        viewNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();

                switch (id){
                    //open profile
                    case R.id.profile_layout:
                        Intent intent=new Intent(HelperHome.this,Profile.class);
                        startActivity(intent);
                        break;

                        //open feedback
                    case R.id.feedbackHelper:
                        Toast.makeText(getApplicationContext(),"FeedBack Activity",Toast.LENGTH_SHORT).show();
                        Intent intentFeed=new Intent(HelperHome.this,FeedBackActivity.class);
                        intentFeed.putExtra("type","helper");
                        intentFeed.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentFeed);
                        break;

                        //open about
                    case R.id.aboutHelper:
                        Toast.makeText(getApplicationContext(),"About Activity",Toast.LENGTH_SHORT).show();
                        Intent intentAbout=new Intent(HelperHome.this,AboutActivity.class);
                        intentAbout.putExtra("type","helper");
                        intentAbout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAbout);
                        break;

                        //logout
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


        //fetch order list from firebase and must not be complete
        FirebaseDatabase databaseOrder = FirebaseDatabase.getInstance();
        DatabaseReference referenceOrder = databaseOrder.getReference();
        referenceOrder.child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    UserOrder order=dataSnapshot1.getValue(UserOrder.class);
                    if (order != null && !order.isComplete())
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

        //location id key and location service
        int locationFinePermission = ContextCompat.checkSelfPermission(HelperHome.this, Manifest.permission.ACCESS_FINE_LOCATION);
        LocationManager lm = (LocationManager) getSystemService(Context. LOCATION_SERVICE ) ;

        boolean gps_enabled;
        boolean network_enabled;

        assert lm != null;
        gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;

        //service intent to operate it
        Intent intentService=new Intent(getApplicationContext(),LocationService.class);
        //if the android system higher tha 6 call runtime permission
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (locationFinePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HelperHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
            }
            else{
                //check if location is on
                if (!gps_enabled && !network_enabled){
                    openLocationSetting();
                }//run background service
                else
                    startService(intentService);
            }
        }
        else{
            if (!gps_enabled && !network_enabled){
                openLocationSetting();
            }
            else
            startService(intentService);
        }
    }

    // dialog to ask for open location setting
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

    // method to open location setting intent
    private void openSettings(){
        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        startService(new Intent(getApplicationContext(),LocationService.class));
    }

    //override method after calling runtime permission to check request key
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openSettings();
            }
        }
    }

    //method to create recycler view list and pass argument to it's class
    private void setupRecycler(Context context, List<UserOrder> orders, Location location){
        OrderAdapter adapter=new OrderAdapter(context,orders,location,HelperHome.this);
        helpOrderRecycler.setAdapter(adapter);
        helpOrderRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    //retrieve helper data included live location update from firebase
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
