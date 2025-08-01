package com.lanmeng.functiontest.util;

import java.io.File;

public class RootChecker {
    public static boolean isDeviceRooted() {
        return checkRootFiles() ||
//                checkInstalledRootApps() ||
                checkSuCommandAvailable() ||
                isTestKeysBuild();
    }

    private static boolean checkRootFiles() {
        String[] paths = {
                "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su",
                "/system/bin/.ext/su", "/data/local/su", "/data/local/bin/su",
                "/data/local/xbin/su", "/vendor/bin/su", "/magisk"
        };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

//    private static boolean checkInstalledRootApps() {
//        String[] rootPackages = {
//                "com.noshufou.android.su",
//                "com.noshufou.android.su.elite",
//                "eu.chainfire.supersu",
//                "com.koushikdutta.superuser",
//                "com.thirdparty.superuser",
//                "com.yellowes.su",
//                "com.topjohnwu.magisk"
//        };
//        for (String pkg : rootPackages) {
//            try {
//                RootCheckerHelper.getContext().getPackageManager().getPackageInfo(pkg, 0);
//                return true;
//            } catch (Exception ignored) {}
//        }
//        return false;
//    }

    private static boolean checkSuCommandAvailable() {
        String[] commands = {"which su", "command -v su"};
        for (String cmd : commands) {
            if (execShellCommand(cmd)) return true;
        }
        return false;
    }

    private static boolean isTestKeysBuild() {
        String tags = android.os.Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    private static boolean execShellCommand(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
