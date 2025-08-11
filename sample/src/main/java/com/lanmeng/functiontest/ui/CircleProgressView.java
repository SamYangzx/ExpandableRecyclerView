package com.lanmeng.functiontest.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleProgressView extends View {

    private static final int PROGRESS_COLOR = 0xFF006975;
    private static final int PROGRESS_TEXT_COLOR = 0xFF000000;
    private static final int PROGRESS_WIDTH = 25;
    private int progress = 0; // 当前进度
    private int max = 100;    // 最大进度

    private Paint bgPaint;
    private Paint progressPaint;
    private Paint textPaint;
    // 小字体 % 的 Paint
    Paint smallTextPaint ;
    private RectF circleRect;

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.LTGRAY);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(PROGRESS_WIDTH);
        bgPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setColor(PROGRESS_COLOR);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(PROGRESS_WIDTH);
        progressPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(PROGRESS_TEXT_COLOR);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        smallTextPaint = new Paint(textPaint);
        circleRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 20;

        int cx = width / 2;
        int cy = height / 2;

        circleRect.set(cx - radius, cy - radius, cx + radius, cy + radius);

        // 背景圆环
        canvas.drawArc(circleRect, 0, 360, false, bgPaint);

        // 进度圆弧
        float sweepAngle = 360f * progress / max;
        canvas.drawArc(circleRect, -90, sweepAngle, false, progressPaint);

        // 中心文字（数值 + 百分号）
        String numberText = String.valueOf(progress);
        String percentSign = "%";

        // 获取 baseline
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float baseline = cy - (fontMetrics.ascent + fontMetrics.descent) / 2;

        // 计算两段文字的总宽度
        float numberWidth = textPaint.measureText(numberText);


        smallTextPaint.setTextSize(textPaint.getTextSize() * 0.5f); // 缩小为60%
        float percentWidth = smallTextPaint.measureText(percentSign);

        float totalWidth = numberWidth + percentWidth;

        // 分别绘制数字和 %
        canvas.drawText(numberText, cx - totalWidth / 2 + numberWidth / 2, baseline, textPaint);
        canvas.drawText(percentSign, cx + totalWidth / 2 - percentWidth / 2, baseline, smallTextPaint);
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, max));
        invalidate(); // 重绘
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }
}
