package com.vendor.scrapy.vendorscrapy.order;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;


import com.vendor.scrapy.vendorscrapy.R;

import java.util.ArrayList;

/**
 * Created by shadaf on 13/1/18.
 */

public class PastAdapter extends RecyclerView.Adapter<PastAdapter.ViewHolder> {


    ArrayList<OrderSetter> orderSetters = null;
    PastFragment pastFragment;
    boolean selection = false;

    public class ViewHolder extends RecyclerView.ViewHolder {

        Button btStatus, btDetails;
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            btStatus = (Button) view.findViewById(R.id.btStatus);
            btDetails = (Button) view.findViewById(R.id.btDetails);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

            btDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(pastFragment.getActivity(), OrderDetailsActivity.class);
                    intent.putExtra("Past", false);
                    pastFragment.getActivity().startActivity(intent);
                }
            });
        }
    }

    public PastAdapter(PastFragment pastFragment, ArrayList<OrderSetter> orderSetters) {

        this.pastFragment = pastFragment;
        this.orderSetters = orderSetters;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.past_order_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {




        switch(position){
            case 0:
                holder.btStatus.setText("COMPLETED");
                holder.btStatus.setTextColor(pastFragment.getResources().getColor(R.color.colorGreenPrime));
                holder.ratingBar.setRating(3.0f);
                break;
            case 1:
                holder.btStatus.setText("CANCELLED");
                holder.btStatus.setTextColor(pastFragment.getResources().getColor(R.color.colorRed));
                holder.ratingBar.setRating(0.0f);
                break;
            case 2:
                holder.btStatus.setText("DECLINED");
                holder.btStatus.setTextColor(pastFragment.getResources().getColor(R.color.colorRed));
                holder.ratingBar.setRating(0.0f);
                break;
            case 3:
                holder.btStatus.setText("COMPLETED");
                holder.btStatus.setTextColor(pastFragment.getResources().getColor(R.color.colorGreenPrime));
                holder.ratingBar.setRating(4.0f);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return orderSetters.size();
    }


}
