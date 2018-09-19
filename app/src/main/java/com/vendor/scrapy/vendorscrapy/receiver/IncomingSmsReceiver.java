package com.vendor.scrapy.vendorscrapy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.vendor.scrapy.vendorscrapy.utils.Constants;

public class IncomingSmsReceiver extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    if(message.toLowerCase().contains("scrapykart")){
                        Intent intent1 = new Intent();
                        intent1.setAction(Constants.RECEIVER_SMS);
                        intent1.putExtra(Constants.RESULT_DATA_KEY, message.substring(message.length()-5,message.length()));
                        context.sendBroadcast(intent1);
                    }

//                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: "
//                            + message);
//
//
//                    // Show Alert
//                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context,
//                            "senderNum: " + senderNum + ", message: " + message, duration);
//                    toast.show();

                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
