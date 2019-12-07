package com.example.helpme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.adapter.OrderOldAdapter;
import com.example.helpme.model.OrderOld;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//activity class to show completed order in user history
public class OldOrder extends AppCompatActivity {

    private RecyclerView oldRecycler;
    private final List<OrderOld> oldList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_order);

        oldRecycler=findViewById(R.id.oldOrderList);
        ImageView backImage=findViewById(R.id.imageBackBtn);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OldOrder.this.finish();
            }
        });

        //offline storage
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        String email = preferences.getString("email", "empty");


        //retrieve old order data from firebase
        FirebaseDatabase databaseOld=FirebaseDatabase.getInstance();
        DatabaseReference referenceOld=databaseOld.getReference();
        referenceOld.child("OldOrder").child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    OrderOld oldOrder=dataSnapshot1.getValue(OrderOld.class);
                    oldList.add(oldOrder);
                }
                setupRecycler(oldList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //setup recycler view argument
    private void setupRecycler(List<OrderOld> list){
        OrderOldAdapter adapter=new OrderOldAdapter(list);
        oldRecycler.setAdapter(adapter);
        oldRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
