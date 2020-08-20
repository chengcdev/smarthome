package com.mili.smarthome.tkj.set.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;

public class AutoScrollView extends ScrollView {

    private Context mContext;
    private TextView textView;
    private String content = "";
    private int measuredHeight;
    private ObjectAnimator animator;
    //竖向滚动
    public static final int VERTICAL_TYPE = 0;
    //横向滚动
    public static final int HORIZON_TYPE = 1;
    private int scrollType;
    private int textWidth;

    public AutoScrollView(Context context) {
        super(context);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
        initView();
    }

    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        initView();
    }

    private void init(AttributeSet attrs) {
        TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.AutoScrollView);
        scrollType = attributes.getInteger(R.styleable.AutoScrollView_direct, 0);
    }


    private void initView() {
        removeAllViews();
        textView = new TextView(mContext);
        if (HORIZON_TYPE == scrollType) {
            textView.setSingleLine(true);
            textView.setTextSize(30);
            textView.setLayoutParams(new ViewGroup.LayoutParams(2000, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            textView.setTextSize(28);
        }
        textView.setTextColor(Color.WHITE);
        textView.setText(content);
        addView(textView);
    }

    public void setContent(String content) {
        this.content = content;
        initView();
    }


    /**
     * 开始滚动
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("WrongConstant")
    public void startScroll() {
        if (animator != null) {
            animator.cancel();
        }
        ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);//避免重复监听
                if (HORIZON_TYPE == scrollType) {
                    //横向滚动
                    horizonScroll();
                } else {
                    //竖向滚动
                    verticalScroll();
                }
            }
        });
    }

    private void verticalScroll() {
        measuredHeight = textView.getMeasuredHeight();
        if (measuredHeight > getHeight()) {
            animator = ObjectAnimator.ofFloat(textView, "translationY", getHeight(), -(measuredHeight));
            animator.setDuration(20000);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }
    }

    private void horizonScroll() {
        textWidth = (int) getTextWidth(content, 30);
        animator = ObjectAnimator.ofFloat(textView, "translationX", getWidth(), -(getTextWidth(content, 30)));
        animator.setDuration(15000);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 根据文本的
     */
    public float getTextWidth(String text, float textSize) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        return textPaint.measureText(text);
    }


    /**
     * 停止滚动
     */
    public void stopScroll() {
        if (animator != null) {
            animator.cancel();
        }
    }

}
