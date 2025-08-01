package com.lanmeng.functiontest.util;

import android.os.Build;
import android.util.Log;

import java.util.Locale;

public class LogUtil {

    private static boolean mbLoggable = true;

    // Log type
    private static final int NORMAL = 1;
    private static int DETAIL = 2;

    // 5 level of Log
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int ERROR = 4;
    private static final int WARN = 5;

    /**
     * debug 系统版本默认开启详细log。
     */
    private static boolean detailLog = Build.TYPE.equals("userdebug");

    private static int level = VERBOSE;

    private static class SingleTonHolder {
        private static final LogUtil INSTANCE = new LogUtil();
    }

    public static LogUtil getInstance() {
        return SingleTonHolder.INSTANCE;
    }


    public static void setLogLevel(int l) {
        level = l;
    }

    public static int getLogLevel() {
        return level;
    }

    public static void setLoggable(boolean bLoggable) {
        mbLoggable = bLoggable;
    }

    public static boolean isLoggable() {
        return mbLoggable;
    }

    public static int v(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= VERBOSE) {
            if (detailLog) {
                return Log.v(tag, buildLogMsg(msg));
            }else {
                return Log.v(tag, msg);
            }
        }
        return -1;
    }

    public static int d(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= DEBUG) {
            if (detailLog) {
                return Log.d(tag, buildLogMsg(msg));
            }else {
                return Log.d(tag, msg);
            }
        }
        return -1;
    }

    public static int i(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= INFO) {
            if (detailLog) {
                return Log.i(tag, buildLogMsg(msg));
            }else {
                return Log.i(tag, msg);
            }
        }
        return -1;
    }
    public static int w(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= WARN) {
            if (detailLog) {
                return Log.w(tag, buildLogMsg(msg));
            }else {
                return Log.w(tag, msg);
            }
        }
        return -1;
    }

    public static int e(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= ERROR) {
            if (detailLog) {
                return Log.e(tag, buildLogMsg(msg));
            }else {
                return Log.e(tag, msg);
            }
        }
        return -1;
    }

    /**
     * 只额外要函数名+代码行数做快速定位
     * @return 带函数名，代码行数 log
     */
    private static String buildLogMsg(String msg) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[4 /*若封装了函数为4，直接写在LogUtil.d中为3 */];

        return String.format(Locale.ROOT, "[%s:%d]  %s",
                element.getMethodName(),
                element.getLineNumber(),
                msg
        );
    }
}
