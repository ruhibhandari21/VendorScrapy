package com.vendor.scrapy.vendorscrapy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Admin on 10/4/2016.
 */
public class Preferences {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public static final String MOBILE_NO = "mobile_no";
    public static final String OTP = "otp";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String GPS_LAT = "latitude";
    public static final String GPS_LNG = "longitude";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";
    public static final String EMAIL_ID = "email";
    public static final String SHOP_LIC_NO = "shop_lic_no";
    public static final String SHOP_REG_NO = "shop_reg_no";
    public static final String TAN_NO = "tan_no";
    public static final String MIN_QTY = "min_qty";
    public static final String SHOP_NAME = "shop_name";
    public static final String SHOP_ADDR = "shop_addr";

    public static final String IS_PROFILE_SET = "is_profile_set";


    public Preferences(Context ctx) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = preferences.edit();
    }

    public void clearAll(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public float getFloat(String key) {
        return preferences.getFloat(key, 0f);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

}
