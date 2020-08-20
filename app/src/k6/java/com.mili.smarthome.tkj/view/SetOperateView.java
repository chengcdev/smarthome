package com.mili.smarthome.tkj.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.DisplayUtils;


public class SetOperateView extends LinearLayout {
    private Context mContext;
    private Handler handler = new Handler();
    private IOperateListener operateListener;
    public SetOperateView(Context context) {
        super(context);
        init(context);
    }


    public SetOperateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SetOperateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        setFocusable(false);
    }

    /**
     * 设置成功后显示隐藏的界面
     * @param text 成功失败提示
     * @param operateListener 设置成功失败提示回调
     */
    public void operateBackState(final String text, final IOperateListener operateListener) {
        final TextView textView = getTextView(text);
        final Runnable myRun = new Runnable() {
            @Override
            public void run() {
                if (operateListener != null) {
                    removeView(textView);
                    showChildView();
                    if (text.equals(mContext.getString(R.string.set_success))) {
                        operateListener.success();
                    } else {
                        operateListener.fail();
                    }
                }
                handler.removeCallbacks(this);
            }
        };
        handler.postDelayed(myRun, 1000);
        //点击退出
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView(textView);
                showChildView();
                handler.removeCallbacks(myRun);
            }
        });
    }

    /**
     * 设置成功后显示隐藏的界面
     * @param text 成功失败提示
     */
    public void operateBackState(final String text) {
        final TextView textView = getTextView(text);
        final Runnable myRun = new Runnable() {
            @Override
            public void run() {
                if (operateListener != null) {
                    if (text.equals(mContext.getString(R.string.set_success))) {
                        operateListener.success();
                    } else {
                        operateListener.fail();
                    }
                    removeView(textView);
                    showChildView();
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.postDelayed(myRun, 2000);
        //点击退出
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView(textView);
                showChildView();
                handler.removeCallbacks(myRun);
            }
        });
    }

    /**
     * 设置成功后显示隐藏的界面
     * @param text 成功失败提示
     * @param time 设置成功后延时time显示界面
     */
    public void operateBackState(final String text, long time, final IOperateListener operateListener) {
        final TextView textView = getTextView(text);
        final Runnable myRun = new Runnable() {
            @Override
            public void run() {
                if (operateListener != null) {
                    removeView(textView);
                    showChildView();
                    handler.removeCallbacks(this);
                    operateListener.success();
                }
            }
        };
        handler.postDelayed(myRun, time);
        //点击文字退出
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView(textView);
                showChildView();
                handler.removeCallbacks(myRun);
                if (operateListener != null) {
                    operateListener.success();
                }
            }
        });
    }

    /**
     * 退出当前fragment
     * @param text
     */
    public void exitFragment(final String text, final FragmentActivity activity) {
        final TextView textView = getTextView(text);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (operateListener != null) {
                    removeView(textView);
                    showChildView();
                    if (text.equals(mContext.getString(R.string.set_success))) {
                        operateListener.success();
                    } else {
                        operateListener.fail();
                    }
                    handler.removeCallbacks(this);
                    //退出当前栈
                    activity.getSupportFragmentManager().popBackStack();
                }
            }
        }, 1000);
    }


    /**
     * 处理中
     */
    public void showProcessing(final String text) {
        getTextView(text);
    }


    @NonNull
    TextView getTextView(String text) {
        //隐藏所有view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            childAt.setVisibility(View.GONE);
        }
        final TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(DisplayUtils.px2sp(mContext,30));
        textView.setGravity(Gravity.CENTER);
        if (textView.getText().toString().equals(mContext.getString(R.string.set_success)) ||
                textView.getText().toString().equals(mContext.getString(R.string.set_fail))) {
            textView.setEnabled(false);
        }else {
            textView.setEnabled(true);
        }
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView(textView);
                showChildView();
            }
        });
        addView(textView);
        return textView;
    }

    private void showChildView(){
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            childAt.setVisibility(View.VISIBLE);
        }
    }


    public void setSuccessListener(IOperateListener operateListener) {
        this.operateListener = operateListener;
    }


    public interface IOperateListener {
        void success();

        void fail();
    }

}
