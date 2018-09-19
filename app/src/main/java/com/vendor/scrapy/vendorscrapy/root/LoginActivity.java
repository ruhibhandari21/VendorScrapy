        package com.vendor.scrapy.vendorscrapy.root;

        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
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
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
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


public class LoginActivity extends AppCompatActivity implements OnTaskCompleted {

    private FusedLocationProviderClient mFusedLocationClient;
    private Preferences preferences;
    private AddressResultReceiver mResultReceiver;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;
    private int cityId = 1;
    private ProgressDialog progressDialog;
    private EditText edOtp;
    private Receiver receiver;
    private AlertDialog otpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        initUI();
        initListener();


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

    private void initListener() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                validate();
                startActivity(new Intent(LoginActivity.this, OrderActivity.class));
            }
        });

        ((Spinner) findViewById(R.id.spinCity)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityId = i;
                putData(Preferences.CITY_NAME, ((TextView) view).getText().toString());
                putData(Preferences.CITY_ID, cityId + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void validate() {

        String mobile = ((EditText) findViewById(R.id.edMobile)).getText().toString();
        boolean valid = true;
        if (mobile.equalsIgnoreCase("") && mobile.length() < 10) {
            valid = false;
            ((EditText) findViewById(R.id.edMobile)).setError("Invalid mobile no");
        }

        if (cityId == 0) {
            valid = false;
            Toast.makeText(this, "Please choose city", Toast.LENGTH_SHORT).show();
        }

        if (valid) {
            callService(WSConstants._LOGIN);
        }

    }

    private void initUI() {
        ((Spinner) findViewById(R.id.spinCity)).setSelection(cityId);
    }

    private void initialize() {
        receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RECEIVER_SMS);
        registerReceiver(receiver,filter);
        preferences = new Preferences(this);
        preferences.clearAll();
//        mResultReceiver = new AddressResultReceiver(new Handler());
//        getAccessPermission(100);
    }

    private void locationListener() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    putData(Preferences.GPS_LAT, location.getLatitude()+"");
                    putData(Preferences.GPS_LNG, location.getLongitude()+"");
                    startIntentService(location);
                    stopLocationUpdates();
                    break;
                }
            }

            ;
        };
    }

    public void getAccessPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                ) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.READ_SMS
                    , Manifest.permission.RECEIVE_SMS
            }, requestCode);

        } else {
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
    private void fetchLocation() {
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
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
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
                        resolvable.startResolutionForResult(LoginActivity.this,
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
        return mLocationRequest;
    }

    protected void startIntentService(Location location) {
        startProgress();
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1://gps enable dialog
                switch (resultCode) {
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

    private void onCancelGpsRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("It is mandatory to enable gps to fetch accurate location.");
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void callService(String tag) {

        String requestUrl = WSConstants._BASE_URL + tag;
        HashMap<String, String> hashMap = new HashMap();

        switch (tag) {
            case WSConstants._LOGIN:
                hashMap.put("mobile_no", ((EditText) findViewById(R.id.edMobile)).getText().toString());
                hashMap.put("city_id", "1");
                String deviceId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                hashMap.put("device_id", deviceId);
                break;

            case WSConstants._OTP_VERIFY:
                hashMap.put("mobile_no", ((EditText) findViewById(R.id.edMobile)).getText().toString());
                hashMap.put("otp", preferences.getString(Preferences.OTP));
                break;
        }


        new OkHttpHandler(LoginActivity.this, this, hashMap, tag).execute(requestUrl);
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

                            case WSConstants._LOGIN:
                                loginResponse(object);
                                break;

                            case WSConstants._OTP_VERIFY:
                                verificationResponse(object);
                                break;

                        }


                    } else {
                        Toast.makeText(this, object.optString("respMsg"), Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(this, object.optString("errorMsg"), Toast.LENGTH_LONG)
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

    private void verificationResponse(JSONObject object) {

        putData(Preferences.USER_ID, object.optJSONObject("respData").optString("user_id"));
        putData(Preferences.IS_PROFILE_SET, object.optJSONObject("respData").optString("is_profile_set"));
        if (getData(Preferences.IS_PROFILE_SET).equals("1")) {
            JSONObject user_data = object.optJSONObject("respData").optJSONObject("user_data");
            putData(Preferences.USER_NAME, user_data.optString("full_name"));
            putData(Preferences.GPS_LAT, user_data.optString("latitude"));
            putData(Preferences.GPS_LNG, user_data.optString("longitude"));
            putData(Preferences.CITY_ID, user_data.optString("city"));
            putData(Preferences.MOBILE_NO, user_data.optString("mobile_no"));
            putData(Preferences.EMAIL_ID, user_data.optString("email"));
            putData(Preferences.SHOP_LIC_NO, user_data.optString("shop_licence_number"));
            putData(Preferences.SHOP_REG_NO, user_data.optString("shop_registration_number"));
            putData(Preferences.TAN_NO, user_data.optString("tan_number"));
            putData(Preferences.MIN_QTY, user_data.optString("minimum_quantity"));
            putData(Preferences.SHOP_NAME, user_data.optString("shop_name"));
            putData(Preferences.SHOP_ADDR, user_data.optString("shop_address"));


        } else {
            //Not registered

        }
        finish();
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.putExtra(Constants.RESULT_DATA_KEY, getData(Preferences.IS_PROFILE_SET));
        startActivity(intent);

    }

    private void putData(String key, String value) {
        preferences.putString(key, value);
    }

    private String getData(String key) {
        return preferences.getString(key);
    }

    private void loginResponse(JSONObject object) {

        Toast.makeText(this, "Soon you will receive OTP on your number",
                Toast.LENGTH_LONG).show();
        preferences.putString(Preferences.MOBILE_NO,
                ((EditText) findViewById(R.id.edMobile)).getText().toString());
        preferences.putString(Preferences.OTP, object.optString("otp_number"));
        showOtpDialog();

    }

    private void showOtpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_otp, null,false);
        builder.setView(view);
        builder.setCancelable(false);
        edOtp = (EditText)view.findViewById(R.id.otp);
        ((TextView)view.findViewById(R.id.tvNo)).setText("No: "+
                ((EditText) findViewById(R.id.edMobile)).getText().toString());
        view.findViewById(R.id.dialogOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edOtp.getText().toString().equalsIgnoreCase("") ||
                edOtp.getText().toString().length() != 5){
                    edOtp.setError("Invalid otp");
                }else{
                    putData(Preferences.OTP,edOtp.getText().toString());
                    callService(WSConstants._OTP_VERIFY);
                }

            }
        });
        view.findViewById(R.id.dialogRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callService(WSConstants._LOGIN);
            }
        });

        otpDialog = builder.create();
        otpDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            stopLocationUpdates();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
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
            String mLocality = resultData.getString(Constants.RESULT_DATA_KEY_1);
//            Toast.makeText(LoginActivity.this, ""+mLocality, Toast.LENGTH_LONG).show();
            putData(Preferences.SHOP_ADDR, mAddressOutput);
            putData(Preferences.CITY_NAME, mLocality);
            putData(Preferences.CITY_ID, cityId + "1");
            stopProgress();
        }
    }

    class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                String otp = intent.getStringExtra(Constants.RESULT_DATA_KEY);
                edOtp.setText(otp);
            }
        }
    }
}

