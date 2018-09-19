package com.vendor.scrapy.vendorscrapy.webservice;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.vendor.scrapy.vendorscrapy.R;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpHandler extends AsyncTask<Object, Object, Object> {
    private Activity baseActivity;
    private OnTaskCompleted listener;
    private HashMap<String, String> postDataParams;
    private String TAG, dup_url = "";
    private ProgressDialog progress;
    private boolean visible = true;

    private Context mContext;

    public OkHttpHandler(Context mContext, OnTaskCompleted listener, HashMap<String, String> postDataParams,
                         String TAG) {
        this.listener = listener;
        this.TAG = TAG;
        this.mContext = mContext;
        this.postDataParams = postDataParams;
    }

    public OkHttpHandler(OnTaskCompleted listener, String TAG, Activity baseActivity) {
        this.listener = listener;
        this.TAG = TAG;
        this.baseActivity = baseActivity;
    }

    private static String decodeResponse(InputStream is) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            result = sb.toString();

        } catch (Exception EXCEPTION) {
            EXCEPTION.printStackTrace();
        }

        return result;
    }

    public void setProgressFlag(boolean visible) {
        this.visible = visible;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private String uploadData(String requestURL, HashMap<String, String> postDataParams, String TAG) {
        URL url;
        String finalresponse = "";
        try {

                if (postDataParams != null) {

                    MultipartBody.Builder multipartBody = new MultipartBody.Builder();
                    multipartBody.setType(MultipartBody.FORM);
                    for (Map.Entry<String, String> map : postDataParams.entrySet()) {
                        multipartBody.addFormDataPart(map.getKey(), map.getValue());
                    }

//                    multipartBody.build();

                    RequestBody requestBody = multipartBody.build();
                    OkHttpClient client =
                            new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS).build();
                    Request.Builder builder = new Request.Builder();
                    builder.url(requestURL);
                    Request request = builder.method("POST", RequestBody.create(null, new byte[0]))
                            .post(requestBody)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.code() == 200) {
                            finalresponse = (response.body() != null) ? response.body().string() : "";
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    url = new URL(requestURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(30000);
                    conn.setConnectTimeout(30000);
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            finalresponse += line;
                        }
                    } else {
                        finalresponse = "";

                    }
                }



        } catch (SocketTimeoutException s) {
            s.printStackTrace();
            cancel(true);
            progress.dismiss();
            showAlert();

        } catch (Exception e) {
            cancel(true);
            e.printStackTrace();
            progress.dismiss();
            showAlert();
        }
        Log.v(TAG, "Response: " + finalresponse);
        return finalresponse;
    }

    @Override
    protected String doInBackground(Object[] objects) {
        dup_url = objects[0].toString();
        return uploadData(objects[0].toString(), postDataParams, TAG);
    }

    @Override
    protected void onPostExecute(Object o) {
        try {

            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            listener.onTaskCompleted(o.toString(), TAG);
        } catch (Exception e) {
            Log.e("ReadJSONFeedTask", e.getLocalizedMessage() + "");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

          if (visible) {
        progress = new ProgressDialog(mContext);
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        }
    }

    private void showAlert() {

        ((Activity) listener).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Dialog dialog = new Dialog((Context) listener);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                LayoutInflater lf = (LayoutInflater) ((Context) listener)
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogview = lf.inflate(R.layout.retry_dialog, null);
                TextView title = (TextView) dialogview.findViewById(R.id.title);
                title.setText("Note");
                TextView body = (TextView) dialogview
                        .findViewById(R.id.dialogBody);
                body.setText("Please check your network connection");
                dialog.setContentView(dialogview);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);
                dialog.show();
                TextView cancel = (TextView) dialogview
                        .findViewById(R.id.dialogCancel);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        progress.dismiss();
                        dialog.dismiss();
                    }
                });

                TextView retry = (TextView) dialogview
                        .findViewById(R.id.dialogRetry);
                retry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new OkHttpHandler(mContext, listener, postDataParams, TAG).execute(dup_url);
                        dialog.dismiss();
                    }
                });

            }

        });
    }


}
