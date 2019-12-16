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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/*
An adapter recycler view class to deal of list order in helper home
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolderOrder> {

    /*
    context, mContext to access activity resources and info
    list is array list hold order object
    Location that hold longitude and latitude
     */
    private final Context context;
    private final Context mContext;
    private final List<UserOrder> userOrderList;
    private final Location location;
    private static boolean dialogShowOnce=false;

    //constructor for class
    public OrderAdapter(Context context, List<UserOrder> userOrderList, Location location, Context mContext) {
        this.context = context;
        this.userOrderList = userOrderList;
        this.location=location;
        this.mContext=mContext;
    }

    //a view holder
    @NonNull
    @Override
    public OrderAdapter.ViewHolderOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //view coupled with an layout xml
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order,parent,false);
        return new ViewHolderOrder(view);
    }

    /*
    on bind view holder will recycle old items and fill them with required data
    instead of create all items (best performance)
     */

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderOrder holder, final int position) {
        //create order object and get the data from object inside array list
        final UserOrder order=userOrderList.get(position);
        //calculate the distance between user and helper
        Location startPoint=new Location("LocationA");
        startPoint.setLatitude(order.getLatitude());
        startPoint.setLongitude(order.getLongitude());
        double distance =location.distanceTo(startPoint);
        //fill card inside list with required data
        holder.nameUser.setText(order.getFullName()+" Needs Help");
        //if the distance less than 1 km convert to meter else type it km set distance inside card
        if (distance<=1000.0f){
            order.setLocation(String.valueOf(Math.round(distance)));
            holder.location.setText(order.getLocation()+" M AWAY FROM YOU");
        }
        else if (distance>1000.0f){
            order.setLocation(String.valueOf(Math.round(distance)/1000));
            holder.location.setText(order.getLocation()+" KM AWAY FROM YOU");
        }

        //add listener for button click (remove items)
        holder.cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Canceled",Toast.LENGTH_SHORT).show();
                removeItem(position);
            }
        });

        //add listener for button click (accept and send price)
        holder.approveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call dialog method to input price
                viewDialogPrice(order,position);
            }
        });
    }

    //override method to get length of list
    @Override
    public int getItemCount() {
        return userOrderList.size();
    }

    //method to show dialog to insert a price in it
    private void viewDialogPrice(final UserOrder order, final int pos){
        //create and initial dialog with layout
        final Dialog dialogPrice=new Dialog(mContext);
        dialogPrice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPrice.setContentView(R.layout.price_dialog);

        if (!dialogPrice.isShowing())
        dialogPrice.show();

        //find view by id for dialog layout
        TextView textProblem=dialogPrice.findViewById(R.id.descriptionProblemPrice);
        final EditText priceEdit=dialogPrice.findViewById(R.id.priceOrder);
        Button submitPrice=dialogPrice.findViewById(R.id.donePriceBtn);
        ImageView cancelBtn=dialogPrice.findViewById(R.id.cancelButtonPrice);

        textProblem.setText(order.getDescription());
        //cancel button click to dismiss
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrice.dismiss();
            }
        });

        //submit the price for user
        submitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price=priceEdit.getText().toString().trim();
                if (!price.isEmpty()){
                    order.setPrice(price);
                    //retrieve an email from offline storage by key
                    SharedPreferences preferences=context.getSharedPreferences("login",MODE_PRIVATE);
                    final String email=preferences.getString("email","empty");
                    //initial firebase instance with specific root and save price and set state to reserve the oder
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    DatabaseReference reference=database.getReference();
                    HashMap<String,Object> updateData=new HashMap<>();
                    updateData.put("price",price);
                    updateData.put("state",true);
                    updateData.put("helperID",email);
                    reference.child("Orders").child(order.getEmail()).updateChildren(updateData);
                    viewWaitDialog(order,pos);
                    dialogPrice.dismiss();
                }
                else
                    Toast.makeText(context,"Please insert price",Toast.LENGTH_LONG).show();
            }
        });

    }

    //method to wait for user accept or refuse for price with dialog
    private void viewWaitDialog(final UserOrder order, final int p) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wait_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (!dialog.isShowing())
        dialog.show();

        //create listener for data change in firebase if the user accept new value added 1 refuse=2
        final FirebaseDatabase databaseOrder = FirebaseDatabase.getInstance();
        DatabaseReference referenceOrder = databaseOrder.getReference();
        referenceOrder.child("Orders").child(order.getEmail()).child("accept").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String accept= Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    if (accept.equals("1")) {
                        viewDialog(order,p);
                        dialog.dismiss();
                    }
                    else if (accept.equals("2")){
                        Toast.makeText(mContext,"Price Refuse",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,databaseError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //view dialog consist of order data beside user id
    private void viewDialog(final UserOrder order, final int pos){
        final Dialog dialog=new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.approve_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        //retrieve an email from offline storage by key
        SharedPreferences preferences=context.getSharedPreferences("login",MODE_PRIVATE);
        final String email=preferences.getString("email","empty");

        //create and bind element of dialog layout
        TextView userName=dialog.findViewById(R.id.orderUserName);
        TextView carType=dialog.findViewById(R.id.orderCarType);
        TextView carColor=dialog.findViewById(R.id.orderCarColor);
        TextView viewDesc=dialog.findViewById(R.id.descriptionProblem);
        ImageView viewLocationBtn=dialog.findViewById(R.id.locationBtn);
        ImageView completeBtn=dialog.findViewById(R.id.completeBtn);

        //set data inside dialog
        userName.setText(order.getFullName());
        carType.setText(order.getCarType());
        carColor.setText(order.getCarColor());
        viewDesc.setText(order.getDescription());

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference reference=database.getReference();

        //save helper id to user dialog after approve
        reference.child("Orders").child(order.getEmail()).child("helperID").setValue(email);

        //location button click lead to map activity to view user location
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

        //complete button after the services done
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase databaseOrderOld=FirebaseDatabase.getInstance();
                DatabaseReference referenceOld=databaseOrderOld.getReference();

                //set complete true indicate the fix is done
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
                dialogShowOnce=false;
                //remove item by position
                removeItem(pos);
                dialog.cancel();
            }
        });
        if (!dialog.isShowing() && !dialogShowOnce){
            dialog.show();
            dialogShowOnce=true;
        }
    }

    // method to remove specific item by position
    private void removeItem(int position){
        userOrderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,userOrderList.size());
    }

    /*
    a viewHolder describe an item view and metadata about its place within RecyclerView
     */
    class ViewHolderOrder extends RecyclerView.ViewHolder{

        //card element
        final TextView nameUser;
        final TextView location;
        final ImageView cancelOrder;
        final ImageView approveOrder;

        //and it's id
        ViewHolderOrder(@NonNull View itemView) {
            super(itemView);
            nameUser=itemView.findViewById(R.id.orderName);
            location=itemView.findViewById(R.id.distance);
            cancelOrder=itemView.findViewById(R.id.cancelButton);
            approveOrder=itemView.findViewById(R.id.approveBtn);
        }
    }
}
