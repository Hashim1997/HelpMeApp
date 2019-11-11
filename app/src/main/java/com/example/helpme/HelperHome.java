package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HelperHome extends AppCompatActivity {

    private RecyclerView helpOrderRecycler;
    private List<UserOrder> orderList=new ArrayList<>();
    private double latitude, longitude;
    private ImageView drawerImage;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_home);
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

        orderList.add(order);
        orderList.add(order3);
        orderList.add(order);
        orderList.add(order3);


    }
    private void setupRecycler(Context context,List<UserOrder> orders,Location location){
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
                    latitude=helper.getLatitude();
                    longitude=helper.getLongitude();
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
