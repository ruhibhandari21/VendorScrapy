package com.vendor.scrapy.vendorscrapy.order;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.vendor.scrapy.vendorscrapy.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestOrderFragment extends Fragment {

    RecyclerView recyclerOrder;
    RequestAdapter requestAdapter;
    View view;
    ArrayList<OrderSetter> orderSetters;


    public RequestOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_request_order, container, false);
        initUI();
        initiateSetter();
        setupRecycler();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    private void initUI() {
        recyclerOrder = (RecyclerView) view.findViewById(R.id.recyclerOrder);
    }

    private void setupRecycler() {

        requestAdapter = new RequestAdapter((OrderActivity) getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerOrder.setLayoutManager(mLayoutManager);
        recyclerOrder.setItemAnimator(new DefaultItemAnimator());
        recyclerOrder.setAdapter(requestAdapter);


    }

    private void initiateSetter() {

        orderSetters = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            OrderSetter setter = new OrderSetter();
            setter.setVendorName("Scrapper name "+i);
            setter.setOrderDate("10th Jun 17, 2.15PM");
            setter.setOrderId("SCRP8953"+i+"12"+i);
            setter.setPrice(i*10);
            setter.setQuantity(i*3);
            if(i == 0 || i == 2 || i == 4){
                setter.setStatus(OrderSetter.ORDER_PENDING);
            }else{
                setter.setStatus(OrderSetter.ORDER_CONFIRMED);
            }

            orderSetters.add(setter);
        }

    }

}

