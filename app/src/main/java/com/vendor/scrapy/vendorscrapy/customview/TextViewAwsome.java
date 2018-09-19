package com.vendor.scrapy.vendorscrapy.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by shadaf on 12/1/2016.
 */


public class TextViewAwsome extends android.support.v7.widget.AppCompatTextView {

    public TextViewAwsome(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewAwsome(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewAwsome(Context context) {
        super(context);
        init();
    }

    static Typeface tf;

    public void init() {
        if (tf == null) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "brlnsdb.TTF");
        }

        setTypeface(tf, 1);

    }

}
