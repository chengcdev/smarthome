package com.mili.smarthome.tkj.setting.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ViewUtils;

public class ResultView extends FrameLayout {

    private TextView tvResult;

    public ResultView(Context context) {
        this(context, null, 0);
    }

    public ResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ResultView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.view_result, this);
        tvResult = ViewUtils.findView(this, R.id.tv_result);
    }

    public void showResult(String text) {
        tvResult.setText(text);
    }

    public void showResult(@StringRes int resid) {
        tvResult.setText(resid);
    }
}
