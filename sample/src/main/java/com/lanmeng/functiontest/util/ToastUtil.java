package com.lanmeng.functiontest.util;

import android.widget.Toast;

import com.lanmeng.functiontest.MainApplication;

public class ToastUtil {
    public static void showTest(String name, boolean result) {
        Toast.makeText(MainApplication.getContext(), String.format("%s : %b", name, result), Toast.LENGTH_SHORT).show();
    }

    public static void showTest(String name, String result) {
        Toast.makeText(MainApplication.getContext(), String.format("%s : %s", name, result), Toast.LENGTH_SHORT).show();
    }
}
