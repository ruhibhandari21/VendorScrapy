package com.vendor.scrapy.vendorscrapy.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.vendor.scrapy.vendorscrapy.R;

public class OrderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        setupRecycler();
        perform();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void perform() {
        Intent intent = getIntent();
        if(intent.getBooleanExtra("Past", true)){
        //past
        }else{
            //current
            findViewById(R.id.tvTitle3).setVisibility(View.GONE);
            findViewById(R.id.relThird).setVisibility(View.GONE);
            findViewById(R.id.relForth).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.btAccept)).setText("ACCEPTED");
            ((Button)findViewById(R.id.btCancel)).setVisibility(View.INVISIBLE);

        }
    }

    private void setupRecycler() {

        RecyclerView recyclerOrder = (RecyclerView) findViewById(R.id.recyclerRateCard);
        OrderCardAdapter currentAdapter = new OrderCardAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerOrder.setLayoutManager(mLayoutManager);
        recyclerOrder.setItemAnimator(new DefaultItemAnimator());
        recyclerOrder.setAdapter(currentAdapter);

    }

}
