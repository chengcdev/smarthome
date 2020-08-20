package com.mili.smarthome.tkj.main.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HintView extends FrameLayout{

    private View mContentView;
    private TextView mTvHint;
    private LinearLayout mLlHead;
    private TextView mDevnoDesc;

    public HintView(Context context) {
        this(context, null);
    }

    public HintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (mContentView == null) {
            mContentView = LayoutInflater.from(context).inflate(R.layout.hintview, this);
        }
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mLlHead = (LinearLayout) findViewById(R.id.ll_net);
        mDevnoDesc = (TextView) findViewById(R.id.tv_desc);

        // 延迟显示设备编号描述，否则可能由于未初始化Realm先调用获取而导致死机
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showTime();
            }
        }, 1000);
    }

    public void setHint(int resId, int colorId) {
        if (colorId > 0 && mTvHint != null) {
            mTvHint.setTextColor(getResources().getColor(colorId));
        }
        if (resId > 0 && mTvHint != null) {
            mTvHint.setText(resId);
        }

        mLlHead.setVisibility(View.VISIBLE);
        mDevnoDesc.setVisibility(View.VISIBLE);
        showTime();
    }

    private void showTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mDevnoDesc.setText(sdf.format(System.currentTimeMillis()));
    }
}
