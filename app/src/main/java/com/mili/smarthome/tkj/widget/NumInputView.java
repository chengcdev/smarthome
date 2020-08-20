package com.mili.smarthome.tkj.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.mili.smarthome.tkj.R;

public class NumInputView extends InputView {

    private boolean mPaddingZero = false;

    public NumInputView(Context context) {
        this(context, null, 0);
    }

    public NumInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NumInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.NumInputView, defStyleAttr, defStyleRes);
        if (a != null) {
            mPaddingZero = a.getBoolean(R.styleable.NumInputView_paddingZero, false);
            a.recycle();
        }
    }

    @Override
    public void setText(CharSequence text) {
        if (TextUtils.isDigitsOnly(text)) {
            super.setText(text);
        }
    }

    @Override
    public void setText(int resid) {
        setText(getResources().getText(resid));
    }

    @Override
    public void setMaxLength(int maxLength) {
        super.setMaxLength(maxLength);
        requestLayout();
    }

    @Override
    public void input(int num) {
        char c = (char) ((num % 10) + 48);
        if (mText.length() >= mMaxLength
                && mCursorIndex == mText.length())
            return;
        if (mCursorIndex < mText.length()) {
            mText.setCharAt(mCursorIndex, c);
        } else {
            mText.append(c);
        }
        mCursorIndex++;
        if (mCursorIndex == mText.length()
                && mText.length() == mMaxLength) {
            focusNext();
        }
        invalidate();
    }

    @Override
    public boolean backspace() {
        if (mPaddingZero) {
            int index;
            if (mCursorIndex < mText.length()) {
                index = mCursorIndex;
            } else {
                index = mText.length() - 1;
            }
            mText.setCharAt(index, '0');
            if (index == mCursorStart) {
                return focusPrevious();
            } else {
                mCursorIndex = (index - 1);
            }
        } else {
            if (mCursorIndex == mCursorStart
                    && mText.length() == mCursorStart) {
                return focusPrevious();
            } else {
                int start;
                if (mCursorIndex >= mText.length())
                    start = mText.length() - 1;
                else
                    start = mCursorIndex;
                start = Math.max(0, start);
                int end = mText.length();
                if (start < end) {
                    mText.delete(start, end);
                    mCursorIndex = start;
                }
            }
        }
        invalidate();
        return true;
    }

    @Override
    protected int measureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            int widthSize = (int) Math.ceil(mPaint.measureText("0") * mMaxLength);
            widthSize += getPaddingLeft();
            widthSize += getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        }
        return widthMeasureSpec;
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
        final float baseline = paddingTop - fontMetrics.top;

        final float numWidth = mPaint.measureText("0");
        final float lineWidth = numWidth - dip2px(2f);
        final float linePadding = dip2px(1f);
        final float offestY = paddingTop + fontMetrics.descent - fontMetrics.top;

        final int length = (mText == null) ? 0 : mText.length();

        // 绘制文本
        if (length > 0) {
            mPaint.setStyle(Paint.Style.FILL);
            if (mMask) {
                final String maskText = "*";
                final float maskWidth = mPaint.measureText(maskText);
                for (int i = 0; i < length; i++) {
                    float x = paddingLeft + numWidth * i + ((numWidth - maskWidth) / 2);
                    canvas.drawText(maskText, 0, 1, x, baseline, mPaint);
                }
            } else {
                canvas.drawText(mText, 0, length, paddingLeft, baseline, mPaint);
            }
        }
        //
        if (!mPaddingZero) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dip2px(1.5f));

            int start;
            if (isFocused()) {
                start = mCursorIndex + 1;
            } else {
                start = length;
            }
            for (int i = start; i < mMaxLength; i++) {
                float startX = paddingLeft + numWidth * i + linePadding;
                float stopX = startX + lineWidth;
                canvas.drawLine(startX, offestY, stopX, offestY, mPaint);
            }
        }

        // 绘制光标
        if (isFocused() && mDrawCursor && mCursorIndex < mMaxLength) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dip2px(1.5f));

            float startX = paddingLeft + numWidth * mCursorIndex + linePadding;
            float stopX = startX + lineWidth;
            canvas.drawLine(startX, offestY, stopX, offestY, mPaint);
        }

        canvas.restoreToCount(saveCount);
    }
}
