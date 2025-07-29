package com.lanmeng.functiontest.util;

import android.os.Build;

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
    private static int logType = Build.TYPE.equals("userdebug") ? DETAIL : NORMAL;

    private static int level = VERBOSE;

    private static class SingleTonHolder {
        private static final LogUtil INSTANCE = new LogUtil();
    }

    public static LogUtil getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    public static void setLogType(int type) {
        logType = type;
    }

    public static int getLogType() {
        return logType;
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
            if (logType == NORMAL) {
                return android.util.Log.v(tag, msg);
            }
            if (logType == DETAIL) {
                return android.util.Log.v(tag, buildLogMsg(msg));
            }
        }
        return -1;
    }

    public static int d(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= DEBUG) {
            if (logType == NORMAL) {
                return android.util.Log.d(tag, msg);
            }
            if (logType == DETAIL) {
                return android.util.Log.d(tag, buildLogMsg(msg));
            }
        }
        return -1;
    }

    public static int i(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= INFO) {
            if (logType == NORMAL) {
                return android.util.Log.i(tag, msg);
            }
            if (logType == DETAIL) {
                return android.util.Log.i(tag, buildLogMsg(msg));
            }
        }
        return -1;
    }

    public static int e(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= ERROR) {
            if (logType == NORMAL) {
                return android.util.Log.e(tag, msg);
            }
            if (logType == DETAIL) {
                return android.util.Log.e(tag, buildLogMsg(msg));
            }
        }
        return -1;
    }

    public static int w(String tag, String msg) {
        if (!mbLoggable) {
            return -1;
        }
        if (level <= WARN) {
            if (logType == NORMAL) {
                return android.util.Log.w(tag, msg);
            }
            if (logType == DETAIL) {
                return android.util.Log.w(tag, buildLogMsg(msg));
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
