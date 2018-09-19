package com.vendor.scrapy.vendorscrapy.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.vendor.scrapy.vendorscrapy.R;
import com.vendor.scrapy.vendorscrapy.utils.Preferences;
import com.vendor.scrapy.vendorscrapy.webservice.WSConstants;

import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by shadaf on 13/1/18.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    OrderActivity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {

        Button btAccept, btDecline, btCall;

        public ViewHolder(View view) {
            super(view);
            btAccept = (Button) view.findViewById(R.id.btAccept);
            btDecline = (Button) view.findViewById(R.id.btDecline);
            btCall = (Button) view.findViewById(R.id.btCall);

            btAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callDialog(1);
                }
            });

            btDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callDialog(2);
                }
            });

            btCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:9579795761";
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    activity.startActivity(intent);
                }
            });
        }
    }

    public RequestAdapter(OrderActivity activity) {

        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_request_order, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {



    }

    @Override
    public int getItemCount() {
        return 5;
    }

    private void callDialog(final int status){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if(status == 1){
            builder.setTitle("Accept request");
            builder.setMessage("After confirming this request you will be redirected to the Current screen, where you can track the details of the request.");
        }else {
            builder.setTitle("Decline request");
            builder.setMessage("Once confirmed, you will see this request in the past history.");
        }

        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startProgress(status);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startProgress(final int status){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_progress, null,false);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                callNoteDialog(status);
            }
        }, 3000);

    }

    private void callNoteDialog(int status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("NOTE");
        if(status == 1){
            builder.setMessage("Request accepted, now you can see this request on current screen left to the request screen");
        }else {
            builder.setMessage("Request declined, now you can see this request on past screen right to the request screen");
        }

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
