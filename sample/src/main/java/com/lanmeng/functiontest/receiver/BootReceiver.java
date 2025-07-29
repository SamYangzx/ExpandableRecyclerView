package com.lanmeng.functiontest.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.lanmeng.functiontest.util.LogUtil;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();
    static final String ACTION_XWXT_BOOT_COMPLETED = "com.xwxt.smartpos.coreservice.ACTION_BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || ACTION_XWXT_BOOT_COMPLETED.equals(action)) {
            LogUtil.d(TAG, "receive action: " + action);
            LogUtil.d(TAG, "Pkg: " + context.getPackageName());

//            context.startForegroundService(new Intent(context, MyForegroundService.class).setAction(ACTION_START_FOREGROUND_SERVICE));

            // 在这里编写代码来拉起当前apk
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(launchIntent);
            }
        }
    }


    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }
}