package com.lanmeng.functiontest.service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lanmeng.functiontest.R;
import com.lanmeng.functiontest.util.LogUtil;


public class MyForegroundService extends Service {
    private static final String TAG = MyForegroundService.class.getSimpleName();

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_STOP = "ACTION_STOP";



    // 通知渠道 ID（Android 8.0+ 必须）
    private static final String CHANNEL_ID = "MyForegroundService";
    // 通知 ID

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "下载服务",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("正在下载文件")
                .setContentText("准备下载...")
                .setSmallIcon(R.drawable.notification)
                .setOnlyAlertOnce(true) // 防止每次更新都震动或响铃
                .setProgress(100, 0, false);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void updateProgress(int progress) {
        LogUtil.d(TAG, "progress: " + progress);
        builder.setContentText("下载进度：" + progress + "%")
                .setProgress(100, progress, false);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private volatile boolean mIsThreadRuning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) {
                return START_STICKY; // 服务被杀死后尝试重启
            }
            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    if(!mIsThreadRuning){
                        new Thread(() -> {
                            if (mIsThreadRuning) {
                                return;
                            }
                            mIsThreadRuning = true;
                            for (int i = 0; i <= 100; i += 10) {
                                updateProgress(i);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "sleep: ", e);
                                }
                            }
                            builder.setContentText("下载完成")
                                    .setProgress(0, 0, false);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            stopForeground(false); // 通知保留，但服务退出前台
//                        stopSelf();
                            mIsThreadRuning = false;
                        }).start();
                    }
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
//                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP:
                    stopForegroundService();
//                    Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;

            }
        }
        return START_STICKY; // 服务被杀死后尝试重启
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        // 服务销毁时的逻辑（可选）
        stopForegroundService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // 不提供 Binder，纯前台服务
    }


    private void stopForegroundService() {
        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }
}