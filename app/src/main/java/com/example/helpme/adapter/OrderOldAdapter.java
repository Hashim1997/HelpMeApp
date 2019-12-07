package com.example.helpme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.model.OrderOld;

import java.util.List;

public class OrderOldAdapter extends RecyclerView.Adapter<OrderOldAdapter.ViewHolderOld> {


    //list to hold old order object
    private final List<OrderOld> oldList;

    // a constructor
    public OrderOldAdapter(List<OrderOld> oldList) {
        this.oldList = oldList;
    }


    //a view holder enable to access each list item view without need to lockup
    @NonNull
    @Override
    public ViewHolderOld onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //view coupled with layout
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_user_order,parent,false);
        return new  ViewHolderOld(view);
    }

    /*
    on bind view holder will recycle old items and fill them with required data
    instead of create all items (best performance)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderOld holder, int position) {
        //create old order object and fetch the object inside list
        OrderOld old=oldList.get(position);
        //fill holder items with data
        holder.helperName.setText(old.getHelperName());
        holder.helperExp.setText(old.getHelperExp());
        holder.orderRate.setRating(old.getOrderRate());
    }

    //determine list length
    @Override
    public int getItemCount() {
        return oldList.size();
    }


    /*
    a viewHolder describe an item view and metadata about its place within RecyclerView
     */
    class ViewHolderOld extends RecyclerView.ViewHolder{
        //card element
        final TextView helperName;
        final TextView helperExp;
        final RatingBar orderRate;

        //and it's id
        ViewHolderOld(@NonNull View itemView) {
            super(itemView);
            helperName=itemView.findViewById(R.id.helperName);
            helperExp=itemView.findViewById(R.id.helperExperience);
            orderRate=itemView.findViewById(R.id.orderRate);
        }
    }
}
