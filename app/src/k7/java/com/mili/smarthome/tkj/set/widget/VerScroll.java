package com.mili.smarthome.tkj.set.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class VerScroll extends RelativeLayout {

    private TextView textView;
    private Handler handler = new Handler();
    private HorizontalScrollRun scrollRun;
    private Context mContext;
    private String currentTitle;
    private int textWidth;
    //是否滚动
    private boolean isScroll;
    private IScrollCompleteListener scrollCompleteListener;

    public VerScroll(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public VerScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public VerScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void init() {
        ScrollView scrollView = new ScrollView(mContext);
        scrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        textView = new TextView(mContext);
        if (currentTitle == null || currentTitle.equals("")) {
            textWidth = getWidth();
        }else {
            textWidth = (int) getTextWidth(currentTitle,30)*10;
        }
        textView.setLayoutParams(new LayoutParams(textWidth,LayoutParams.MATCH_PARENT));
        textView.setSingleLine(true);
        textView.setTextSize(25);
        textView.setTextColor(Color.WHITE);
        scrollView.addView(textView);
        addView(scrollView);
    }

    class HorizontalScrollRun implements Runnable {
        @Override
        public void run() {
            removeAllViews();
            init();
            textView.setText(currentTitle);
            ObjectAnimator animator = startAniamation();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startHorizonScroll();
                    if (scrollCompleteListener != null) {
                        scrollCompleteListener.completeScroll();
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }

                @Override
                public void onAnimationPause(Animator animation) {
                    super.onAnimationPause(animation);
                }

                @Override
                public void onAnimationResume(Animator animation) {
                    super.onAnimationResume(animation);
                }
            });
        }
    }

    /**
     * 根据文本的
     * @param text
     * @param textSize
     * @return
     */
    public float getTextWidth(String text,float textSize) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        return textPaint.measureText(text);
    }


    public ObjectAnimator startAniamation() {
        //显示动画
        @SuppressLint("ObjectAnimatorBinding")
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "translationX", 0,-(getTextWidth(currentTitle,30)+100));
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        return animator;
    }

    public void setTextContent(String textContent) {
        currentTitle = textContent;
    }

    public void startHorizonScroll() {
        if (isScroll) {
            if (scrollRun == null) {
                scrollRun = new HorizontalScrollRun();
            }
            handler.post(scrollRun);
        }
    }

    public void stopHorizonScroll() {
        if (scrollRun != null) {
            handler.removeCallbacks(scrollRun);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //字符串长度
        float strWidth = getTextWidth(currentTitle, 30);
        //界面宽度
        int width = getWidth();
        if (strWidth > width) {
            isScroll = true;
        }else {
            isScroll = false;
        }
        removeAllViews();
        init();
        textView.setText(currentTitle);
        startHorizonScroll();
    }

    public interface IScrollCompleteListener{
        void completeScroll();
    }

    public void setOnScrollCompleteListener(IScrollCompleteListener scrollCompleteListener){
        this.scrollCompleteListener = scrollCompleteListener;
    }

    public void restartHorizonScroll() {
        if (scrollRun != null) {
            handler.removeCallbacks(scrollRun);
        }
        //字符串长度
        float strWidth = getTextWidth(currentTitle, 30);
        //界面宽度
        int width = getWidth();
        if (strWidth > width) {
            isScroll = true;
        }else {
            isScroll = false;
        }
        removeAllViews();
        init();
        textView.setText(currentTitle);
        startHorizonScroll();
    }
}
