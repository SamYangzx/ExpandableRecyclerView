package com.lanmeng.functiontest.util;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.lanmeng.functiontest.R;


public class NotificationUtil {
    private static String getId(Context context) {
        return context.getString(R.string.app_name) + "_notification_id";
    }

    public static NotificationCompat.Builder getBuilder(Context context) {
        return getBuilder(context, getId(context));
    }

    public static NotificationCompat.Builder getBuilder(Context context, String id) {

        if(context == null || TextUtils.isEmpty(id)) {
            return null;
        }

        context = context.getApplicationContext();

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(id, context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(context, id);
        } else {
            //noinspection deprecation
            builder = new NotificationCompat.Builder(context);
        }

        return builder;
    }
}
