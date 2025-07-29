package com.lanmeng.functiontest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.lanmeng.functiontest.R;


public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String TARGET_NUMBER = "10010"; // 替换为你想监听的号码

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) return;

            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = sms.getDisplayOriginatingAddress();
                String message = sms.getMessageBody();

                Log.d(TAG, "SMS from: " + sender + ", content: " + message);

                Toast.makeText(context, "Receive sms", Toast.LENGTH_SHORT).show();
                if (sender.contains(TARGET_NUMBER)) {
                    Toast.makeText(context, "Receive sms", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Target SMS detected. Playing sound...");

                    MediaPlayer player = MediaPlayer.create(context, R.raw.that_girl); // alert.mp3 放到 res/raw 目录
                    player.start();
                }
            }
        }
    }
}