package com.vendor.scrapy.vendorscrapy.ratecard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vendor.scrapy.vendorscrapy.R;

import java.util.ArrayList;

/**
 * Created by shadaf on 10/2/18.
 */

public class RateCardAdapter extends RecyclerView.Adapter<RateCardAdapter.ViewHolder> {

    ArrayList<RateCardSetter> cardSetters;
    RateActivity rateActivity;

    public RateCardAdapter(RateActivity rateActivity, ArrayList<RateCardSetter> cardSetters){
        this.rateActivity = rateActivity; this.cardSetters = cardSetters;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem, tvItemDetails,tvUnit,tvUnitLimit;
        RelativeLayout relRoot;
        public ViewHolder(View view) {
            super(view);
            tvItem = (TextView) view.findViewById(R.id.tvItem);
            tvItemDetails = (TextView) view.findViewById(R.id.tvItemDetails);
            tvUnit = (TextView) view.findViewById(R.id.tvUnit);
            tvUnitLimit = (TextView) view.findViewById(R.id.tvUnitLimit);
            relRoot = (RelativeLayout) view.findViewById(R.id.relRoot);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rate_card_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RateCardSetter setter = cardSetters.get(position);

        holder.tvItem.setText(setter.getName());
//        holder.tvItemDetails.setText(setter.getDescription());
        holder.tvUnit.setText(setter.getPrice()+"Rs/"+setter.getUnit());
        holder.tvUnitLimit.setText("Range: "+setter.getMinQty()+setter.getUnit()+" - "
        +setter.getMaxQty()+setter.getUnit());

        holder.relRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateActivity.showDialog();
            }
        });
    }


    @Override
    public int getItemCount() {
        return cardSetters.size();
    }
}
