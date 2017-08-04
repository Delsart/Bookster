package com.delsart.bookdownload.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.delsart.bookdownload.R;

/**
 * Created by Delsart on 2017/7/28.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int w=getWidth();
        int h=getHeight();
        canvas.clipRect(0, 0, w, h);
        super.onDraw(canvas);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.maincolor));
        mPaint.setStrokeWidth(2.5f);

        canvas.drawLine(0,h,w,h,mPaint);
        canvas.drawLine(w, h/1.8f, w, h, mPaint);
        canvas.drawLine(0, h/1.2f, 0, h, mPaint);

    }

}
