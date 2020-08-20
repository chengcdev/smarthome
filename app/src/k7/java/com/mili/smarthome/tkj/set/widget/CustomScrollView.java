package com.mili.smarthome.tkj.set.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.LogUtils;

public class CustomScrollView extends RelativeLayout implements Runnable {

    private Context mContext;
    private ScrollView mScrollView;
    private TextView mTv;
    private int curScrollHeight;
    private Handler mHandler = new Handler();
    private int mTvHeight;
    private double mScrollHeight;
    private int mMaxLineCount = 4; //默认显示的行数
    private ObjectAnimator mObjectAnimator;

    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacks(this);
        super.onDetachedFromWindow();
    }

    @SuppressLint("ResourceAsColor")
    private void init() {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.custom_scroll_view, this);
        mScrollView = (ScrollView) inflate.findViewById(R.id.scrollView);
        mTv = (TextView) inflate.findViewById(R.id.tv);
    }


    public void setText(final String text, int maxLineCount) {
        mMaxLineCount = maxLineCount;
        mHandler.removeCallbacks(this);
        mTvHeight = 0;
        curScrollHeight = 0;
        mScrollHeight = 0;
        mScrollView.scrollTo(0, 0);
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
        }
        mTv.setText(text);

        mTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                LogUtils.w(" CustomScrollView addOnGlobalLayoutListener ");

                mTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mTvHeight = mTv.getHeight();
                int lineCount = mTv.getLineCount();

                if (lineCount > mMaxLineCount) {
                    //需要填充的行数
                    int surplusLines = mMaxLineCount - lineCount % mMaxLineCount;
                    if (surplusLines < mMaxLineCount) {
                        for (int i = 0; i < surplusLines; i++) {
                            //填充空行
                            mTv.append("\n");
                        }
                        mTv.getViewTreeObserver().addOnGlobalLayoutListener(this);
                        LogUtils.w(" CustomScrollView surplusLines: " + surplusLines);
                        return;
                    }

                    mTvHeight = mTv.getMeasuredHeight();
                    Layout layout = mTv.getLayout();
                    int height = layout.getHeight();
                    double lineHeight = mTvHeight / mTv.getLineCount();
                    LogUtils.w(" CustomScrollView mTvHeight: " + mTvHeight + " height: " + height);
                    LogUtils.w(" CustomScrollView LineCount: " + mTv.getLineCount());
                    mScrollHeight = lineHeight * mMaxLineCount + 3;
                    ViewGroup.LayoutParams layoutParams = mScrollView.getLayoutParams();
                    layoutParams.height = (int) mScrollHeight;
                    curScrollHeight = (int) mScrollHeight;
                    mScrollView.setLayoutParams(layoutParams);
                    mHandler.postDelayed(CustomScrollView.this, 5000);
                }
            }
        });

    }


    @Override
    public void run() {
        LogUtils.w(" CustomScrollView curScrollHeight: " + curScrollHeight);
        mScrollView.scrollTo(0, curScrollHeight);
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
            mObjectAnimator = null;
        }
        mObjectAnimator = ObjectAnimator.ofFloat(mScrollView, "translationY", (float) mScrollHeight, 0f);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mObjectAnimator.setDuration(2000);
        mObjectAnimator.start();

        if (curScrollHeight >= mTvHeight) {
            mScrollView.scrollTo(0, 0);
            curScrollHeight = (int) mScrollHeight;
        } else {
            curScrollHeight += curScrollHeight;
        }
        mHandler.postDelayed(this, 5000);
    }
}
