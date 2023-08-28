package com.hw.datepickerlibrary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.hw.datepickerlibrary.R;

@SuppressLint({"AppCompatCustomView"})
public class LineTextview extends TextView {
    private Paint mPaint;
    private int mColor;
    private Context context;

    public LineTextview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.init();
    }

    private void init() {
        this.mPaint = new Paint(1);
        this.mColor = this.getResources().getColor(R.color.text_enable);
        this.mPaint.setColor(this.mColor);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        int startY = this.dip2px(this.context, 2.0F);
        int endX = 0;
        int endY = height - this.dip2px(this.context, 3.0F);
        canvas.drawLine((float)width, (float)startY, (float)endX, (float)endY, this.mPaint);
    }

    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }
}

