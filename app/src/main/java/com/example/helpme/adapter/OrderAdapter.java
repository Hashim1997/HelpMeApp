package com.example.helpme.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.UserLocation;
import com.example.helpme.model.UserOrder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolderOrder> {

    private final Context context;
    private final Context mContext;
    private final List<UserOrder> userOrderList;
    private final Location location;

    public OrderAdapter(Context context, List<UserOrder> userOrderList, Location location, Context mContext) {
        this.context = context;
        this.userOrderList = userOrderList;
        this.location=location;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolderOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order,parent,false);
        return new ViewHolderOrder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderOrder holder, final int position) {
        final UserOrder order=userOrderList.get(position);
        Location startPoint=new Location("LocationA");
        startPoint.setLatitude(order.getLatitude());
        startPoint.setLongitude(order.getLongitude());
        double distance =location.distanceTo(startPoint);
        holder.nameUser.setText(order.getFullName()+" Needs Help");
        if (distance<=1000.0f){
            order.setLocation(String.valueOf(Math.round(distance)));
            holder.location.setText(order.getLocation()+" M AWAY FROM YOU");
        }
        else if (distance>1000.0f){
            order.setLocation(String.valueOf(Math.round(distance)/1000));
            holder.location.setText(order.getLocation()+" KM AWAY FROM YOU");
        }
        holder.cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Canceled",Toast.LENGTH_SHORT).show();
                removeItem(position);
            }
        });

        holder.approveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDialogPrice(order,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userOrderList.size();
    }

    private void viewDialogPrice(final UserOrder order, final int pos){
        final Dialog dialogPrice=new Dialog(mContext);
        dialogPrice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPrice.setContentView(R.layout.price_dialog);

        dialogPrice.show();

        TextView textProblem=dialogPrice.findViewById(R.id.descriptionProblemPrice);
        final EditText priceEdit=dialogPrice.findViewById(R.id.priceOrder);
        Button submitPrice=dialogPrice.findViewById(R.id.donePriceBtn);
        ImageView cancelBtn=dialogPrice.findViewById(R.id.cancelButtonPrice);

        textProblem.setText(order.getDescription());
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrice.dismiss();
            }
        });

        submitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price=priceEdit.getText().toString().trim();
                if (!price.isEmpty()){
                    order.setPrice(price);
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    DatabaseReference reference=database.getReference();
                    reference.child("Orders").child(order.getEmail()).child("price").setValue(price);
                    reference.child("Orders").child(order.getEmail()).child("state").setValue(true);
                    viewWaitDialog(order,pos);
                    dialogPrice.dismiss();
                }
                else
                    Toast.makeText(context,"Please insert price",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void viewWaitDialog(final UserOrder order, final int p) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wait_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        final FirebaseDatabase databaseOrder = FirebaseDatabase.getInstance();
        DatabaseReference referenceOrder = databaseOrder.getReference();
        referenceOrder.child("Orders").child(order.getEmail()).child("accept").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String accept= Objects.requireNonNull(dataSnapshot.getValue()).toString();
                if (accept.equals("1")) {
                    viewDialog(order,p);
                    dialog.cancel();
                }
                else if (accept.equals("2")){
                    Toast.makeText(mContext,"Price Refused",Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void viewDialog(final UserOrder order, final int pos){
        final Dialog dialog=new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.approve_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        SharedPreferences preferences=context.getSharedPreferences("login",MODE_PRIVATE);
        final String email=preferences.getString("email","empty");

        TextView userName=dialog.findViewById(R.id.orderUserName);
        TextView carType=dialog.findViewById(R.id.orderCarType);
        TextView carColor=dialog.findViewById(R.id.orderCarColor);
        TextView viewDesc=dialog.findViewById(R.id.descriptionProblem);
        ImageView viewLocationBtn=dialog.findViewById(R.id.locationBtn);
        ImageView completeBtn=dialog.findViewById(R.id.completeBtn);

        userName.setText(order.getFullName());
        carType.setText(order.getCarType());
        carColor.setText(order.getCarColor());
        viewDesc.setText(order.getDescription());

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference reference=database.getReference();

        reference.child("Orders").child(order.getEmail()).child("helperID").setValue(email);

        viewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,order.getLatitude()+" "+order.getLongitude(),Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putDouble("lat",order.getLatitude());
                bundle.putDouble("lon",order.getLongitude());
                Intent mapIntent=new Intent(context, UserLocation.class);
                mapIntent.putExtras(bundle);
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mapIntent);
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase databaseOrderOld=FirebaseDatabase.getInstance();
                DatabaseReference referenceOld=databaseOrderOld.getReference();

                referenceOld.child("Orders").child(order.getEmail()).child("complete").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"SuccessFul",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
                    }
                });
                removeItem(pos);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void removeItem(int position){
        userOrderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,userOrderList.size());
    }

    class ViewHolderOrder extends RecyclerView.ViewHolder{

        final TextView nameUser;
        final TextView location;
        final ImageView cancelOrder;
        final ImageView approveOrder;

        ViewHolderOrder(@NonNull View itemView) {
            super(itemView);
            nameUser=itemView.findViewById(R.id.orderName);
            location=itemView.findViewById(R.id.distance);
            cancelOrder=itemView.findViewById(R.id.cancelButton);
            approveOrder=itemView.findViewById(R.id.approveBtn);
        }
    }
}
