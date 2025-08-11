package com.lanmeng.functiontest;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lanmeng.functiontest.ui.CircleProgressView;

public class UIActivity extends AppCompatActivity {

    private CircleProgressView circleProgressView;
    private int progress = 0;
    private Handler handler = new Handler();

    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            if (progress <= 100) {
                circleProgressView.setProgress(progress);
                progress++;
                handler.postDelayed(this, 50);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiactivity);

        circleProgressView = findViewById(R.id.circleProgress);
        handler.post(updateTask);
    }
}