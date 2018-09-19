package com.vendor.scrapy.vendorscrapy.ratecard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.vendor.scrapy.vendorscrapy.R;

import java.util.ArrayList;

public class RateActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerRateList;
    RateCardAdapter rateCardAdapter;
    ArrayList<RateCardSetter> cardSetters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        initiateSetter();
        initUI();
        initListener();

    }

    private void initListener() {
        findViewById(R.id.fabAdd).setOnClickListener(this);
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rate card");

        recyclerRateList = (RecyclerView) findViewById(R.id.recyclerRateList);
        setupRecycler();
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


    private void setupRecycler() {

        rateCardAdapter = new RateCardAdapter(this, cardSetters);
        recyclerRateList.setLayoutManager(new LinearLayoutManager(this));
        recyclerRateList.setItemAnimator(new DefaultItemAnimator());
        recyclerRateList.setAdapter(rateCardAdapter);

    }

    private void initiateSetter() {

        cardSetters = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            RateCardSetter setter = new RateCardSetter();
            setter.setName("Card "+i);
            setter.setDescription("Description(Optional)");
            setter.setRateId(i);
            setter.setUnit("kg");
            setter.setPrice(12);
            setter.setMaxQty(100);
            setter.setMinQty(1);
            cardSetters.add(setter);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.fabAdd:
                showDialog();
                break;
        }

    }

    void showDialog() {
        RateCardDialog newFragment = new RateCardDialog();
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
}
