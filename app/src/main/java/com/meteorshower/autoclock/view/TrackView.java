package com.meteorshower.autoclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
/**
 * 绘制轨迹
 * */
public class TrackView extends View {
    private Paint mPaint;
    private List<Point> mTrackPoints = new ArrayList<>();

    public TrackView(Context context) {
        super(context);
        initPaint();
    }

    public TrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(200);
    }

    public void setTrackPoints(List<Point> trackPoints) {
        mTrackPoints.clear();
        mTrackPoints.addAll(trackPoints);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTrackPoints.size() > 1) {
            Point startPoint = mTrackPoints.get(0);
            for (int i = 1; i < mTrackPoints.size(); i++) {
                Point endPoint = mTrackPoints.get(i);
                canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, mPaint);
                startPoint = endPoint;
            }
        }
    }
}
