package com.example.helpme;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.example.helpme.model.Helper;
import com.example.helpme.model.HelperLocation;
import com.example.helpme.model.OrderOld;
import com.example.helpme.model.User;
import com.example.helpme.model.UserOrder;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UserHome extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout drawerLayout;
    private EditText userLoc;
    private Location mLastKnownLocation;
    private final int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(31.8569, 35.4865);
    private Helper helper1;
    public static final int CALL_PERMISSION = 100;
    public static final int FINE_LOCATION_PERMISSION = 101;
    public static final int AUTOCOMPLETE_REQUEST_CODE=102;
    private TextView helperName,helperExp;
    private RatingBar helperRate;
    private ImageView callBtn;
    private String name,experience;
    private float rateHelper;
    private RatingBar helperRateOrder;
    private int counter=0;
    private String dataDesc;
    private LatLng latLng;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        drawerLayout = findViewById(R.id.home_user);
        ImageView drawerUser = findViewById(R.id.drawerSwitchUser);
        ImageView locBtn = findViewById(R.id.getLocBtn);
        userLoc = findViewById(R.id.userLoc);

        String apiKey = getString(R.string.api_key_place);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        final Dialog dialogDesc=new Dialog(UserHome.this);
        dialogDesc.setContentView(R.layout.add_description_dialog);
        dialogDesc.setTitle("Add Problem Description");
        final EditText textDesc=dialogDesc.findViewById(R.id.description);
        Button submitDesc=dialogDesc.findViewById(R.id.doneDescBtn);

        userLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchCalled();
            }
        });

        submitDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataDesc=textDesc.getText().toString().trim();
                getDeviceLocation(dataDesc);
                dialogDesc.dismiss();
            }
        });
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLoc.getText().toString().equals("")) {
                    dialogDesc.show();
                }
                else{
                    onSearchCalled();
                }

            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView viewNav = findViewById(R.id.navViewUser);
        viewNav.setItemIconTintList(null);
        View headerView = viewNav.getHeaderView(0);
        viewNav.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);

        LinearLayout layout = headerView.findViewById(R.id.linearHeader);

        drawerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        viewNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.profile_user:
                        Intent intentProfile = new Intent(UserHome.this, Profile.class);
                        startActivity(intentProfile);
                        break;

                    case R.id.order_layout:
                        Toast.makeText(getApplicationContext(), "Order Layout", Toast.LENGTH_SHORT).show();
                        Intent intentOrder = new Intent(getBaseContext(), OldOrder.class);
                        startActivity(intentOrder);
                        break;

                    case R.id.feedbackUser:
                        Toast.makeText(getApplicationContext(),"Feedback Activity",Toast.LENGTH_SHORT).show();
                        Intent intentFeedBack=new Intent(UserHome.this,FeedBackActivity.class);
                        intentFeedBack.putExtra("type","user");
                        intentFeedBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentFeedBack);
                        break;

                    case R.id.aboutUser:
                        Toast.makeText(getApplicationContext(),"About Activity",Toast.LENGTH_SHORT).show();
                        Intent intentAbout=new Intent(UserHome.this,AboutActivity.class);
                        intentAbout.putExtra("type","user");
                        intentAbout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAbout);
                        break;

                    case R.id.logout_user:
                        Toast.makeText(getApplicationContext(), "Login Out", Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.apply();
                        Intent intent1 = new Intent(UserHome.this, AccountChooseActivity.class);
                        startActivity(intent1);
                        UserHome.this.finish();
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        int locationFinePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled;
        boolean network_enabled;

        assert lm != null;
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (locationFinePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
            }
            else {
                assert mapFragment != null;
                mapFragment.getMapAsync(this);
                openSettings();
            }
        } else {

            assert mapFragment != null;
            mapFragment.getMapAsync(this);

            if (!gps_enabled && !network_enabled)
                openLocationSetting();
        }

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

        FirebaseDatabase databaseHelperLoc = FirebaseDatabase.getInstance();
        DatabaseReference referenceHelperLoc = databaseHelperLoc.getReference();
        referenceHelperLoc.child("ActiveLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    HelperLocation location = dataSnapshot1.getValue(HelperLocation.class);
                    if (location != null) {
                        mMap.clear();
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location.getKey()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Successful Location permission", Toast.LENGTH_SHORT).show();
                    assert mapFragment != null;
                    mapFragment.getMapAsync(this);
                    openSettings();
                } else
                    Toast.makeText(getApplicationContext(), "Sorry map will not work without permission", Toast.LENGTH_LONG).show();
                return;
            }

            case CALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Successful Call permission", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Sorry you will not call without permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("JO")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("UserHomeX", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(UserHome.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                assert address != null;
                Log.i("addressX",address);
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(UserHome.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                assert status.getStatusMessage() != null;
                Log.i("UserHomeX", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openLocationSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserHome.this);
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

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void getDeviceLocation(final String desc) {

        FusedLocationProviderClient mFusedLocationProviderClient = new FusedLocationProviderClient(this);
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
                        sendOrder(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), desc);
                    } else {
                        Log.d("State", "Current location is null. Using defaults.");
                        Log.e("State", "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void sendOrder(final Double lat, final Double lon, final String dataDesc) {

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        String email = preferences.getString("email", "empty");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    UserOrder order = new UserOrder();
                    order.setCarType(user.getCarType());
                    order.setCarColor(user.getCarColor());
                    order.setFullName(user.getFullName());
                    order.setLongitude(lon);
                    order.setLatitude(lat);
                    order.setEmail(user.getEmail());
                    order.setDescription(dataDesc);
                    order.setState(false);
                    order.setComplete(false);
                    order.setHelperID("empty");
                    saveOrder(order);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
                Log.i("DataBaseError", databaseError.getDetails());
            }
        });
    }

    private void saveOrder(final UserOrder order1) {
        FirebaseDatabase orderDataBase = FirebaseDatabase.getInstance();
        DatabaseReference orderReference = orderDataBase.getReference("Orders");
        orderReference.child(order1.getEmail()).setValue(order1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "SuccessFul", Toast.LENGTH_SHORT).show();
                viewWaitDialog(order1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed While Submit", Toast.LENGTH_SHORT).show();
                Log.i("Error", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    private void viewWaitDialog(final UserOrder order) {
        final Dialog dialog = new Dialog(UserHome.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wait_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        final FirebaseDatabase databaseOrder = FirebaseDatabase.getInstance();
        DatabaseReference referenceOrder = databaseOrder.getReference();
        referenceOrder.child("Orders").child(order.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserOrder order1=dataSnapshot.getValue(UserOrder.class);
                assert order1 != null;
                if (order1.isState()) {
                    dialog.cancel();
                    viewInfoDialog(order1.getHelperID(), order1.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
    }

    private void viewInfoDialog(String helper, final String order) {

        final Dialog dialog1 = new Dialog(UserHome.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.helper_info_dialog);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
        dialog1.show();


        helperName = dialog1.findViewById(R.id.helperName);
        helperExp = dialog1.findViewById(R.id.helperExp);
        helperRate = dialog1.findViewById(R.id.helperRate);
        callBtn = dialog1.findViewById(R.id.callHelper);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference();
        reference.child("Helpers").child(helper).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helper1 = dataSnapshot.getValue(Helper.class);
                if (helper1 != null) {
                    helperName.setText(helper1.getFullName());
                    helperExp.setText(helper1.getTypeOfExperience());
                    helperRate.setRating(helper1.getRate());

                    callBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeACall(helper1.getPhoneNum());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });

        reference.child("Orders").child(order).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserOrder order2=dataSnapshot.getValue(UserOrder.class);
                if (order2 != null && order2.isComplete()) {
                    if (counter<=0)
                    viewFeedOrder(helper1);
                    counter++;
                    dialog1.cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();
                dialog1.cancel();
            }
        });
    }

    private void viewFeedOrder(Helper helper2) {
        final Dialog dialogFinish=new Dialog(UserHome.this);
        dialogFinish.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFinish.setContentView(R.layout.finish_dialog);
        dialogFinish.setCancelable(false);
        dialogFinish.setCanceledOnTouchOutside(false);

        final TextView helperName=dialogFinish.findViewById(R.id.helperFinish);
        final TextView helperEx=dialogFinish.findViewById(R.id.helperExpFinish);
        helperRateOrder=dialogFinish.findViewById(R.id.helperRateFinish);
        Button doneBtn=dialogFinish.findViewById(R.id.doneBtn);

        helperName.setText(helper2.getFullName());
        helperEx.setText(helper2.getTypeOfExperience());
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=helperName.getText().toString().trim();
                experience=helperEx.getText().toString().trim();
                rateHelper=helperRateOrder.getRating();
                sendFeedBack(name, experience, rateHelper);
                dialogFinish.dismiss();

            }
        });

        dialogFinish.show();
    }

    private void sendFeedBack(String nameHelper, String exHelper, float rateHelper) {

        String format="dd-MM-yyyy";
        Date c= Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat(format,Locale.US);
        String formatDate=dateFormat.format(c);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        String email = preferences.getString("email", "empty");

        OrderOld old=new OrderOld();
        old.setOrderRate(rateHelper);
        old.setHelperExp(exHelper);
        old.setHelperName(nameHelper);

        FirebaseDatabase databaseFeed=FirebaseDatabase.getInstance();
        DatabaseReference referenceFeed=databaseFeed.getReference();
        referenceFeed.child("OldOrder").child(email).child(formatDate).setValue(old).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"SuccessFul",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void makeACall(String phone) {
        Intent intentCall = new Intent(Intent.ACTION_CALL);
        intentCall.setData(Uri.parse("tel:" + phone));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserHome.this,new String[]{Manifest.permission.CALL_PHONE},CALL_PERMISSION);
            }
            else
                startActivity(intentCall);
        }
        else
        startActivity(intentCall);
    }
}
