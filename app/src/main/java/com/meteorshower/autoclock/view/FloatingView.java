package com.meteorshower.autoclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.constant.AppConstant;

public class FloatingView extends View {

    public int height = AppConstant.FloatingViewSize;
    public int width = AppConstant.FloatingViewSize;
    private Paint paint;
    private boolean isRunning = false;

    public FloatingView(Context context) {
        super(context);
        paint = new Paint();
    }

    public FloatingView(Context context, int height, int width) {
        super(context);
        paint = new Paint();
        this.height = height;
        this.width = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(height, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画大圆
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.floating_view));
        paint.setAlpha(200);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        //画小圆圈
        paint.setStyle(Paint.Style.STROKE);
        if (isRunning) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.WHITE);
        }
        canvas.drawCircle(width / 2, width / 2, (float) (width * 1.0 / 4), paint);

    }

    public void changeState(boolean isRunning) {
        this.isRunning = isRunning;
        invalidate();
    }

    public void changeSize(int size) {
        this.height = size;
        this.width = size;
        invalidate();
    }

}
