package com.vendor.scrapy.vendorscrapy.ratecard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.vendor.scrapy.vendorscrapy.R;

/**
 * Created by Admin on 2/16/2018.
 */

public class RateCardDialog extends DialogFragment implements View.OnClickListener {


    public RateCardDialog(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_rate_card, container, false);

        initializeVariable();
        initUI(rootView);
        initListener(rootView);

        return rootView;
    }

    private void initUI(View rootView) {
        String[] nameList = {"Book", "Newspaper", "Paper", "Plastic", "Iron", "Mixed", "Tyre", "Glass"};
        String[] unitList = {"Kilogram", "Tonne", "Gram", "Milligram", "Micrograms", "Pound", "Other"};

        AutoCompleteTextView edName = (AutoCompleteTextView) rootView.findViewById(R.id.edName);
        AutoCompleteTextView edUnit = (AutoCompleteTextView) rootView.findViewById(R.id.edUnit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (getActivity(), R.layout.common_search, android.R.id.text1, nameList);
        edName.setThreshold(1);//will start working from first character
        edName.setAdapter(adapter);//setting the adapter data into the

        adapter = new ArrayAdapter<>
                (getActivity(), R.layout.common_search, android.R.id.text1, unitList);
        edUnit.setThreshold(1);//will start working from first character
        edUnit.setAdapter(adapter);//setting the adapter data into the
    }

    private void initListener(View rootView) {
        rootView.findViewById(R.id.btCancel).setOnClickListener(this);
        rootView.findViewById(R.id.btAction).setOnClickListener(this);
    }


    private void initializeVariable() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btCancel:
                dismiss();
                break;

            case R.id.btAction:
                break;
        }
    }
}
