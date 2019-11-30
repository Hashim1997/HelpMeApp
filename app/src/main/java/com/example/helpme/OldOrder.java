package com.example.helpme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class OldOrder extends AppCompatActivity {

    private RecyclerView oldRecycler;
    private final List<OrderOld> oldList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_order);

        oldRecycler=findViewById(R.id.oldOrderList);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        String email = preferences.getString("email", "empty");

        Log.i("emailX",email);

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
/*
        OrderOld old1=new OrderOld();
        old1.setHelperName("MOHAMMAD ALI");
        old1.setHelperExp("CAR MECHANIC");
        old1.setOrderRate(4);

        OrderOld old2=new OrderOld();
        old2.setHelperName("HOSAM AHMAD");
        old2.setHelperExp("CAR MECHANIC");
        old2.setOrderRate(4);

        OrderOld old3=new OrderOld();
        old3.setHelperName("NEDAL ZYAD");
        old3.setHelperExp("CAR MECHANIC");
        old3.setOrderRate(4);

        oldList.add(old1);
        oldList.add(old2);
        oldList.add(old3);
*/
    }

    private void setupRecycler(List<OrderOld> list){
        OrderOldAdapter adapter=new OrderOldAdapter(list);
        oldRecycler.setAdapter(adapter);
        oldRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
