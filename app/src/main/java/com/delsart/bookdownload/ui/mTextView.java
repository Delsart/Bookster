package com.delsart.bookdownload.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.delsart.bookdownload.R;

/**
 * Created by Delsart on 2017/7/28.
 */

public class mTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;

    public mTextView(Context context) {
        super(context);
    }

    public mTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public mTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        canvas.drawLine(w, h/2.2f, w, h, mPaint);
        canvas.drawLine(0, h/1.5f, 0, h, mPaint);

    }

}
