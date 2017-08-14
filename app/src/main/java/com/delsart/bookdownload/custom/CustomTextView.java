package com.delsart.bookdownload.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Delsart on 2017/7/28.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;
private int mColor;

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mColor =color;
        mPaint.setColor(mColor);
    }

    public CustomTextView(Context context) {
        super(context);
        initPaint(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }

    private void initPaint(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2.5f);
        mPaint.setColor(mColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        canvas.clipRect(0, 0, w, h);
        super.onDraw(canvas);
        canvas.drawLine(0, h, w, h, mPaint);
        canvas.drawLine(w, h / 1.8f, w, h, mPaint);
        canvas.drawLine(0, h / 1.2f, 0, h, mPaint);

    }

}
