package com.vendor.scrapy.vendorscrapy.order;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.vendor.scrapy.vendorscrapy.R;

import java.util.ArrayList;

/**
 * Created by shadaf on 13/1/18.
 */

public class CurrentAdapter extends RecyclerView.Adapter<CurrentAdapter.ViewHolder> {

    OrderActivity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {

        Button btInfo;

        public ViewHolder(View view) {
            super(view);
            btInfo = (Button) view.findViewById(R.id.btInfo);
        }
    }

    public CurrentAdapter(OrderActivity activity) {

        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.current_order, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        holder.btInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, OrderDetailsActivity.class);
                intent.putExtra("Current", false);
                activity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return 5;
    }


}
