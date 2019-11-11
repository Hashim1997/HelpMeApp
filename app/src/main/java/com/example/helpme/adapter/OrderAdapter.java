package com.example.helpme.adapter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helpme.R;
import com.example.helpme.UserLocation;
import com.example.helpme.model.UserOrder;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolderOrder> {

    private Context context,mContext;
    private List<UserOrder> userOrderList;
    private Location location;

    public OrderAdapter(Context context, List<UserOrder> userOrderList, Location location, Context mContext) {
        this.context = context;
        this.userOrderList = userOrderList;
        this.location=location;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolderOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order,null);
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
        order.setLocation(String.valueOf(Math.round(distance/1000)));
        holder.nameUser.setText(order.getFullName()+" Needs Help");
        holder.location.setText(order.getLocation()+" KM AWAY FROM YOU");
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
                viewDialog(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userOrderList.size();
    }

    private void viewDialog(final UserOrder order){
        final Dialog dialog=new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.approve_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);



        TextView userName=dialog.findViewById(R.id.orderUserName);
        TextView carType=dialog.findViewById(R.id.orderCarType);
        TextView carColor=dialog.findViewById(R.id.orderCarColor);
        ImageView viewLocationBtn=dialog.findViewById(R.id.locationBtn);
        ImageView completeBtn=dialog.findViewById(R.id.completeBtn);

        userName.setText(order.getFullName());
        carType.setText(order.getCarType());
        carColor.setText(order.getCarColor());

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
                Toast.makeText(context,"Completed",Toast.LENGTH_SHORT).show();
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

        TextView nameUser,location;
        ImageView cancelOrder,approveOrder;

        ViewHolderOrder(@NonNull View itemView) {
            super(itemView);
            nameUser=itemView.findViewById(R.id.orderName);
            location=itemView.findViewById(R.id.distance);
            cancelOrder=itemView.findViewById(R.id.cancelButton);
            approveOrder=itemView.findViewById(R.id.approveBtn);
        }
    }
}
