package com.mili.smarthome.tkj.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;

import com.mili.smarthome.tkj.R;

public class LabelTextView extends View {

    private CharSequence mLabel;
    private CharSequence mText;
    private ColorStateList mTextColor;
    private int mTextSize;

    private Paint mPaint;

    public LabelTextView(Context context) {
        this(context, null, 0);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.LabelTextView, defStyleAttr, defStyleRes);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.LabelTextView_label:
                        mLabel = a.getText(attr);
                        break;
                    case R.styleable.LabelTextView_text:
                        mText = a.getText(attr);
                        break;
                    case R.styleable.LabelTextView_textColor:
                        mTextColor = a.getColorStateList(attr);
                        break;
                    case R.styleable.LabelTextView_textSize:
                        mTextSize = a.getDimensionPixelSize(attr, 15);
                        break;
                }
            }
            a.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//防锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mPaint.setTextSize(mTextSize);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            int widthSize = 0;
            if (mLabel != null) {
                widthSize += (int) Math.ceil(mPaint.measureText(mLabel, 0, mLabel.length()));
            }
            if (mText != null) {
                // TODO 英文冒号和逗号measureText不准确？
                String temp = mText.toString()
                        .replaceAll(":", "-")
                        .replaceAll(",", "-");
                widthSize += (int) Math.ceil(mPaint.measureText(temp, 0, temp.length()));
            }
            widthSize += getPaddingLeft();
            widthSize += getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int heightSize = (int) Math.ceil(fontMetrics.bottom - fontMetrics.top);
            heightSize += getPaddingTop();
            heightSize += getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

        int x = paddingLeft;
        if (mLabel != null) {
            canvas.drawText(mLabel, 0, mLabel.length(), x, baseline, mPaint);
            x += (int) Math.ceil(mPaint.measureText(mLabel, 0, mLabel.length()));
        }
        if (mText != null) {
            canvas.drawText(mText, 0, mText.length(), x, baseline, mPaint);
            x += (int) Math.ceil(mPaint.measureText(mText, 0, mText.length()));
        }

        canvas.restoreToCount(saveCount);
    }

    public void setLabel(@StringRes int resid) {
        mLabel = getContext().getText(resid);
        invalidate();
    }

    public void setLabel(CharSequence label) {
        mLabel = label;
        invalidate();
    }

    public void setText(@StringRes int resid) {
        mText = getContext().getText(resid);
        invalidate();
    }

    public void setText(CharSequence text) {
        mText = text;
        invalidate();
    }

}
