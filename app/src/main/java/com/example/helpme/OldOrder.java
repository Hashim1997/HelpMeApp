package com.example.helpme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.adapter.OrderOldAdapter;
import com.example.helpme.model.OrderOld;

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

        setupRecycler(oldList);
    }

    private void setupRecycler(List<OrderOld> list){
        OrderOldAdapter adapter=new OrderOldAdapter(list);
        oldRecycler.setAdapter(adapter);
        oldRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
