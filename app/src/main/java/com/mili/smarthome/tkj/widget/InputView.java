package com.mili.smarthome.tkj.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.mili.smarthome.tkj.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义输入框
 * <p>
 * <p>{@link #setMaxLength(int)} 设置文本的最大输入长度
 * <p>{@link #setText(CharSequence)} 设置文本
 * <p>{@link #clearText()} 清空文本
 * <p>{@link #input(int)} 输入
 * <p>{@link #backspace()} 回退
 * <p>{@link #setCursorIndex(int)} 设置光标索引
 * <p>
 * <p> 2019-03-09: Created by zenghm.
 * <p>
 */
public abstract class InputView extends View {

    protected int mMaxLength = 4;
    protected StringBuilder mText = new StringBuilder();
    protected ColorStateList mTextColor;
    protected int mTextSize = 15;
    protected boolean mMask = false;

    protected int mCursorIndex = 0; // 光标索引
    protected int mCursorStart = 0; // 光标起始索引
    protected boolean mDrawCursor = true; // 是否绘制光标
    protected Timer mCursorTimer = new Timer(); // 定时绘制光标
    protected TimerTask mCursorDrawer;

    protected Paint mPaint;

    public InputView(Context context) {
        this(context, null, 0);
    }

    public InputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.InputView, defStyleAttr, defStyleRes);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.InputView_maxLength:
                        mMaxLength = a.getInt(attr, 4);
                        break;
                    case R.styleable.InputView_text:
                        mText = new StringBuilder(a.getText(attr));
                        break;
                    case R.styleable.InputView_textColor:
                        mTextColor = a.getColorStateList(attr);
                        break;
                    case R.styleable.InputView_textSize:
                        mTextSize = a.getDimensionPixelSize(attr, 15);
                        break;
                    case R.styleable.InputView_mask:
                        mMask = a.getBoolean(attr, false);
                        break;
                }
            }
            a.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//防锯齿
    }

    /** 获取文本的最大长度 */
    public int getMaxLength() {
        return mMaxLength;
    }

    /** 设置文本的最大长度 */
    public void setMaxLength(int maxLength) {
        mMaxLength = maxLength;
    }

    /** 获取文本 */
    public CharSequence getText() {
        return mText;
    }

    /** 设置文本 */
    public void setText(@StringRes int resid) {
        setText(getResources().getText(resid));
    }

    /** 设置文本 */
    public void setText(CharSequence text) {
        mText.delete(0, mText.length());
        mText.append(text);
        invalidate();
    }

    /** 清空文本 */
    public void clearText() {
        mText.delete(0, mText.length());
        invalidate();
    }

    /** 输入 */
    public abstract void input(int num);

    /** 删除 */
    public abstract boolean backspace();

    /** 获取文本颜色 */
    public ColorStateList getTextColor() {
        return mTextColor;
    }

    /** 设置文本颜色 */
    public void setTextColor(@ColorInt int color) {
        setTextColor(ColorStateList.valueOf(color));
        invalidate();
    }

    /** 设置文本颜色 */
    public void setTextColor(ColorStateList colors) {
        if (colors == null) {
            throw new NullPointerException();
        }
        mTextColor = colors;
        invalidate();
    }

    /** 获取文本字体大小 */
    public int getTextSize () {
        return mTextSize;
    }

    /** 设置文本字体大小 */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
        invalidate();
    }

    public int getCursorIndex() {
        return mCursorIndex;
    }

    /** 设置光标索引 */
    public void setCursorIndex(int cursorIndex) {
        if (cursorIndex < mCursorStart)
            return;
        if (cursorIndex > mText.length())
            return;
        mCursorIndex = cursorIndex;
        invalidate();
    }

    public void moveCursorToStart() {
        mCursorIndex = mCursorStart;
        invalidate();
    }

    @Override
    public boolean performClick() {
        if (isFocused()) {
            mCursorIndex = mCursorStart;
        }
        return super.performClick();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            mCursorIndex = mCursorStart;
            mCursorDrawer = new TimerTask() {
                @Override
                public void run() {
                    if (isFocused() && mCursorIndex < mMaxLength) {
                        mDrawCursor = !mDrawCursor;
                        postInvalidate();
                    }
                }
            };
            mCursorTimer.schedule(mCursorDrawer, 500, 500);
        } else {
            mCursorDrawer.cancel();
            mCursorDrawer = null;
            mCursorTimer.purge();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPaint.setTextSize(mTextSize);
        widthMeasureSpec = measureWidth(widthMeasureSpec);
        heightMeasureSpec = measureHeight(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected int measureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            int widthSize = (int) Math.ceil(mPaint.measureText(mText, 0, mText.length()));
            widthSize += getPaddingLeft();
            widthSize += getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        }
        return widthMeasureSpec;
    }

    protected int measureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int heightSize = (int) Math.ceil(fontMetrics.bottom - fontMetrics.top);
            heightSize += getPaddingTop();
            heightSize += getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        }
        return heightMeasureSpec;
    }

    protected boolean focusNext() {
        View next = focusSearch(View.FOCUS_DOWN);
        if (next instanceof InputView) {
            next.requestFocus();
            return true;
        }
        return false;
    }

    protected boolean focusPrevious() {
        View pre = focusSearch(View.FOCUS_UP);
        if (pre != null) {
            pre.requestFocus();
            if (pre instanceof InputView) {
                InputView ipt = (InputView) pre;
                int textLen = ipt.getText().length();
                ipt.setCursorIndex(textLen - 1);
            }
            return true;
        }
        return false;
    }

    protected float dip2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    protected float sp2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

}
