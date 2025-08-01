package com.lanmeng.functiontest.util;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class StatusBarUtil {

    public static void hideNavigation(Context context, int status) {
        try {
            //Context.STATUS_BAR_SERVICE的值是隐藏的，无法直接获取，只能通过反射获取
            Class clazz = Class.forName("android.content.Context");
            Field field = clazz.getField("STATUS_BAR_SERVICE");
            Object service =
                    context.getSystemService((String) Objects.requireNonNull(field.get(clazz)));
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method disable = statusBarManager.getMethod("disable",
                    int.class);
            disable.invoke(service, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
