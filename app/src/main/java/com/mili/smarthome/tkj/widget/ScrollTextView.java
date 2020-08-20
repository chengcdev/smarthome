package com.mili.smarthome.tkj.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * 文本滚动视图（用于梯口主界面消息的滚动）
 */
@SuppressLint("AppCompatCustomView")
public class ScrollTextView extends TextView {

    private ScrollTask mScrollTask;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScrollTask = new ScrollTask();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        removeCallbacks(mScrollTask);
        scrollTo(0, 0);
        postDelayed(mScrollTask, 5000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mScrollTask);
    }

    private class ScrollTask implements Runnable {
        @Override
        public void run() {

            int scrollY = getScrollY();
            int height = getHeight();
            if (height == 0) {
                return;
            }
            int pageCur = scrollY / height;

            int pageEnd = (getLineCount() - 1) / getMaxLines();

            if (pageCur < pageEnd) {

                ValueAnimator animator = ValueAnimator.ofInt(scrollY, scrollY + height);
                animator.setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        scrollTo(0, (int) valueAnimator.getAnimatedValue());
                    }
                });
                animator.start();

                if (pageCur + 1 < pageEnd) {
                    postDelayed(this, 5000);
                }
            }
        }
    }
}
