package com.vendor.scrapy.vendorscrapy.root;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.vendor.scrapy.vendorscrapy.R;
import com.vendor.scrapy.vendorscrapy.ratecard.RateActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        RelativeLayout relRoot = (RelativeLayout) findViewById(R.id.relRoot);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splash_transition);
        relRoot.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
