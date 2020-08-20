package com.mili.smarthome.tkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class MyTextView extends InputView {

    public MyTextView(Context context) {
        this(context, null , 0);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
    }

    @Override
    public void input(int num) {

    }

    @Override
    public boolean backspace() {
        return focusPrevious();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saveCount = canvas.getSaveCount();
        canvas.save();

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        int color = mTextColor.getColorForState(getDrawableState(), Color.WHITE);
        mPaint.setColor(color);
        mPaint.setTextSize(mTextSize);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float baseline = paddingTop - fontMetrics.top;

        // 绘制文本
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(mText, 0, mText.length(), paddingLeft, baseline, mPaint);

        // 绘制光标
        if (isFocused() && mDrawCursor && mCursorIndex < mText.length()) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dip2px(1.5f));

            float linePadding = dip2px(1f);
            float offestY = paddingTop + fontMetrics.descent - fontMetrics.top;
            float startX = paddingLeft + linePadding;
            float stopX = paddingLeft + mPaint.measureText(mText, 0, mText.length()) - linePadding;
            canvas.drawLine(startX, offestY, stopX, offestY, mPaint);
        }

        canvas.restoreToCount(saveCount);
    }
}
