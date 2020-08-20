package com.mili.smarthome.tkj.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class ScrollTextView extends TextView implements ViewTreeObserver.OnGlobalLayoutListener {

    private Context mContext;
    private ObjectAnimator animator;

    public ScrollTextView(Context context) {
        super(context);
        init(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }


    public void startScroll() {
        int height = getHeight();

        //开始滚动
        TranslateAnimation ani = new TranslateAnimation(Animation.ABSOLUTE,0f,
                Animation.ABSOLUTE,0,Animation.ABSOLUTE,height,Animation.ABSOLUTE,0);
        ani.setDuration(5000);
        startAnimation(ani);
    }


    @Override
    public void onGlobalLayout() {
        //开始滚动
//        startScroll();
    }

}
