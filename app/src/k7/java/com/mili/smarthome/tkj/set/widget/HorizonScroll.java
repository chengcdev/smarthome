package com.mili.smarthome.tkj.set.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.utils.LogUtils;


public class HorizonScroll extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener, Runnable {

    private TextView textView;
    private Handler mHandler = new Handler();
    private Context mContext;
    private HorizontalScrollView mScrollView;
    private int mScollViewWidth;
    private int mCurrentScrollWidth;
    private int mTvWidth;
    private String tag = "HorizonScroll";

    public HorizonScroll(Context context) {
        super(context);
        mContext = context;
    }

    public HorizonScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public HorizonScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacks(this);
        super.onDetachedFromWindow();
    }


    @Override
    public void onGlobalLayout() {
        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        //获取textview宽度
        mTvWidth = textView.getMeasuredWidth();
        mScollViewWidth = mScrollView.getMeasuredWidth();
        LogUtils.w(tag + " tvWidth : " + mTvWidth + " scollViewWidth: " + mScollViewWidth);
        if (mTvWidth > mScollViewWidth) {
            //延时三秒滚动
            mCurrentScrollWidth = mScollViewWidth;
            mHandler.postDelayed(this, 1000);
        }
    }

    @Override
    public void run() {
        int charWidth = mTvWidth / textView.length();
        int textLen = textView.length();
        LogUtils.w(tag + " charWidth : " + charWidth + " textLen: " + textLen);
        LogUtils.w(tag + " mCurrentScrollWidth : " + mCurrentScrollWidth);
        mScrollView.smoothScrollTo(mCurrentScrollWidth - charWidth, 0);
        if (mCurrentScrollWidth > mTvWidth) {
            mScrollView.smoothScrollTo(0, 0);
            mCurrentScrollWidth = mScollViewWidth;
        } else {
            mCurrentScrollWidth += mScollViewWidth;
        }
        mHandler.postDelayed(this, 3000);
    }


    public void setText(String textContent) {

        mHandler.removeCallbacks(this);
        removeAllViews();
        mTvWidth = 0;
        mCurrentScrollWidth = 0;
        mScollViewWidth = 0;

        mScrollView = new HorizontalScrollView(mContext);
        mScrollView.setHorizontalScrollBarEnabled(false);
        mScrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView = new TextView(mContext);
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setSingleLine(true);
        textView.setTextSize(28);
        textView.setTextColor(Color.WHITE);
        textView.setText(textContent);
        mScrollView.addView(textView);
        addView(mScrollView);

        textView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }


}
