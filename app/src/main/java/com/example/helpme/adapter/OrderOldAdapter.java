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


    private final List<OrderOld> oldList;

    public OrderOldAdapter(List<OrderOld> oldList) {
        this.oldList = oldList;
    }


    @NonNull
    @Override
    public ViewHolderOld onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_user_order,parent,false);
        return new  ViewHolderOld(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderOld holder, int position) {
        OrderOld old=oldList.get(position);
        holder.helperName.setText(old.getHelperName());
        holder.helperExp.setText(old.getHelperExp());
        holder.orderRate.setRating(old.getOrderRate());
    }

    @Override
    public int getItemCount() {
        return oldList.size();
    }


    class ViewHolderOld extends RecyclerView.ViewHolder{
        final TextView helperName;
        final TextView helperExp;
        final RatingBar orderRate;

        ViewHolderOld(@NonNull View itemView) {
            super(itemView);
            helperName=itemView.findViewById(R.id.helperName);
            helperExp=itemView.findViewById(R.id.helperExperience);
            orderRate=itemView.findViewById(R.id.orderRate);
        }
    }
}
