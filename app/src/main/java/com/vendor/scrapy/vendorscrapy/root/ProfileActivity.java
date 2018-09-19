package com.vendor.scrapy.vendorscrapy.root;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vendor.scrapy.vendorscrapy.R;
import com.vendor.scrapy.vendorscrapy.order.OrderActivity;
import com.vendor.scrapy.vendorscrapy.services.FetchAddressIntentService;
import com.vendor.scrapy.vendorscrapy.utils.Constants;
import com.vendor.scrapy.vendorscrapy.utils.Preferences;
import com.vendor.scrapy.vendorscrapy.webservice.OkHttpHandler;
import com.vendor.scrapy.vendorscrapy.webservice.OnTaskCompleted;
import com.vendor.scrapy.vendorscrapy.webservice.WSConstants;

import org.json.JSONObject;

import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted {

    private FusedLocationProviderClient mFusedLocationClient;
    private Preferences preferences;
    private AddressResultReceiver mResultReceiver;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;
    private boolean profileRegistered = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();
        initUI();
        initListener();
//        getAccessPermission(100);

    }

    private void initListener() {
        findViewById(R.id.btSignUp).setOnClickListener(this);
        findViewById(R.id.btLocation).setOnClickListener(this);
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");

        String value = (!getData(Preferences.MOBILE_NO).equalsIgnoreCase("")) ?
                getData(Preferences.MOBILE_NO) : null;
        ((EditText)findViewById(R.id.edContactNo)).setText(value);
        ((EditText)findViewById(R.id.edContactNo)).setEnabled(false);
        value = (!getData(Preferences.SHOP_ADDR).equalsIgnoreCase("")) ?
                getData(Preferences.SHOP_ADDR) : null;
        ((EditText)findViewById(R.id.edAddress)).setText(value);

        if(profileRegistered){
            value = (!getData(Preferences.USER_NAME).equalsIgnoreCase("")) ?
                    getData(Preferences.USER_NAME) : null;
            ((EditText)findViewById(R.id.edName)).setText(value);

            value = (!getData(Preferences.EMAIL_ID).equalsIgnoreCase("")) ?
                    getData(Preferences.EMAIL_ID) : null;
            ((EditText)findViewById(R.id.edEmail)).setText(value);
            value = (!getData(Preferences.SHOP_NAME).equalsIgnoreCase("")) ?
                    getData(Preferences.SHOP_NAME) : null;
            ((EditText)findViewById(R.id.edStore)).setText(value);
            value = (!getData(Preferences.SHOP_LIC_NO).equalsIgnoreCase("")) ?
                    getData(Preferences.SHOP_LIC_NO) : null;
            ((EditText)findViewById(R.id.edLicense)).setText(value);
            value = (!getData(Preferences.SHOP_REG_NO).equalsIgnoreCase("")) ?
                    getData(Preferences.SHOP_REG_NO) : null;
            ((EditText)findViewById(R.id.edReg)).setText(value);
            value = (!getData(Preferences.TAN_NO).equalsIgnoreCase("")) ?
                    getData(Preferences.TAN_NO) : null;
            ((EditText)findViewById(R.id.edTan)).setText(value);
            value = (!getData(Preferences.MIN_QTY).equalsIgnoreCase("") &&
                    !getData(Preferences.MIN_QTY).equalsIgnoreCase("0")) ?
                    getData(Preferences.MIN_QTY) : null;
            ((EditText)findViewById(R.id.edMinQty)).setText(value);


            findViewById(R.id.btSignUp).setVisibility(View.GONE);
            findViewById(R.id.btLocation).setVisibility(View.GONE);
        }
    }

    private void initialize() {
        preferences = new Preferences(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        if(getIntent().getStringExtra(Constants.RESULT_DATA_KEY).equalsIgnoreCase("1"))
            profileRegistered = true;
    }

    private void startProgress(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("Fetching location...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void stopProgress(){
        if(progressDialog != null) progressDialog.dismiss();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //location code
    private void locationListener() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    putData(Preferences.GPS_LAT, location.getLatitude()+"");
                    putData(Preferences.GPS_LNG, location.getLongitude()+"");
                    startIntentService(location);
                    stopLocationUpdates();
                    break;
                }
            };
        };
    }

    public void getAccessPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION
            }, requestCode);

        } else {
            startProgress();
            locationMethodsCall();
        }


    }

    private void locationMethodsCall() {
        fetchLocation();
        locationListener();
        requestLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                boolean granted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        granted = false;
                        break;
                    }
                }

                if (granted) {
                    locationMethodsCall();
                } else {
                    finish();
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint({"AddedPermission", "MissingPermission"})
    private void fetchLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                                startIntentService(location);
//                        }else{
//                            requestLocation();
//                        }
//                    }
//                });


    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates(){
        try{
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void requestLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                LocationSettingsStates states = locationSettingsResponse.getLocationSettingsStates();
                states.isGpsPresent();
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(ProfileActivity.this,
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    protected LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return  mLocationRequest;
    }

    protected void startIntentService(Location location) {

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1://gps enable dialog
                switch (resultCode){
                    //
                    case Activity.RESULT_CANCELED:
                        onCancelGpsRequest();
                        break;

                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                }
                break;
        }
    }

    private void onCancelGpsRequest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.WARNING);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.TRY, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestLocation();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

    }

    private void putData(String key, String value){
        preferences.putString(key,value);
    }

    private String getData(String key){
        return preferences.getString(key);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btSignUp:
                validate();
                break;
            case R.id.btLocation:
                getAccessPermission(100);
                break;
        }
    }

    private void validate() {

        boolean valid = true;
        if(getTextInfo(R.id.edName).equalsIgnoreCase("")){
            valid = false;
            getEditText(R.id.edName).setError("Invalid name, please enter name");
        }

        if(getTextInfo(R.id.edStore).equalsIgnoreCase("")){
            valid = false;
            getEditText(R.id.edStore).setError("Invalid store name, please enter store name");
        }

        if(getTextInfo(R.id.edAddress).equalsIgnoreCase("")){
            valid = false;
            getEditText(R.id.edAddress).setError("Invalid address, please enter address");
        }

        if(valid){
            callService();
        }

    }

    //Receiver
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            putData(Preferences.SHOP_ADDR, mAddressOutput);
            getEditText(R.id.edAddress).setText(mAddressOutput);
            stopProgress();
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void callService() {

        String requestUrl = WSConstants._BASE_URL + WSConstants._REGISTER;
        HashMap<String, String> hashMap = new HashMap();

                hashMap.put("user_id", getData(Preferences.USER_ID));
                hashMap.put("full_name", getTextInfo(R.id.edName));
                hashMap.put("city", "1");
                hashMap.put("latitude", getData(Preferences.GPS_LAT));
                hashMap.put("longitude", getData(Preferences.GPS_LNG));
                hashMap.put("mobile_no", getTextInfo(R.id.edContactNo));
                hashMap.put("email", getTextInfo(R.id.edEmail));
                hashMap.put("shop_licence_number", getTextInfo(R.id.edLicense));
                hashMap.put("shop_registration_number", getTextInfo(R.id.edReg));
                hashMap.put("tan_number", getTextInfo(R.id.edTan));
                hashMap.put("minimum_quantity", getTextInfo(R.id.edMinQty));
                hashMap.put("shop_name", getTextInfo(R.id.edStore));
                hashMap.put("shop_address", getTextInfo(R.id.edAddress));

        new OkHttpHandler(ProfileActivity.this, this, hashMap, WSConstants._REGISTER).execute(requestUrl);
    }

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        /*{"status":"true","respMsg":"User logged in successfully","user_id":"16"}*/
        try {
            boolean allOk = true;
            if (result != null && !result.equalsIgnoreCase("")) {

                JSONObject object = new JSONObject(result).getJSONObject("response");
                if (object.has("respCode")) {

                    if (object.optString("respCode", "").equals("0")) {
                        switch (TAG) {

                            case WSConstants._REGISTER:
                                findViewById(R.id.btSignUp).setVisibility(View.GONE);
                                findViewById(R.id.btLocation).setVisibility(View.GONE);
                            showSuccessDialog();
                                break;


                        }


                    } else {
                        Toast.makeText(this, object.optString("respMsg"), Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(this, object.optJSONObject("response").optString("errorMsg"), Toast.LENGTH_LONG)
                            .show();
                }

            } else {
                allOk = false;
            }
            if (!allOk)
                Toast.makeText(this, "Something went wrong, please try again..", Toast.LENGTH_LONG)
                        .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSuccessDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("You are successfully registered as vendor with ScrapyKart");
        builder.setCancelable(false);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.show();

    }

    private String getTextInfo(int id){
        return ((EditText)findViewById(id)).getText().toString().trim();
    }

    private EditText getEditText(int id){
        return ((EditText)findViewById(id));
    }

}
