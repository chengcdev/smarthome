package com.mili.smarthome.tkj.setting.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ViewUtils;

public class KeyHintView extends LinearLayout {

    private TextView tvBackHint;
    private View llPageHint;

    public KeyHintView(Context context) {
        this(context, null, 0);
    }

    public KeyHintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public KeyHintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        View content = LayoutInflater.from(context).inflate(R.layout.view_key_hint, this);
        tvBackHint = ViewUtils.findView(content, R.id.tv_back_hint);
        tvBackHint.setText(R.string.back_hint);
        llPageHint = ViewUtils.findView(content, R.id.ll_page_hint);
        llPageHint.setVisibility(GONE);

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.KeyHintView, defStyleAttr, defStyleRes);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.KeyHintView_deletable:
                        setDeletable(a.getBoolean(attr, false));
                        break;
                    case R.styleable.KeyHintView_turnable:
                        setTurnable(a.getBoolean(attr, false));
                        break;
                }
            }
            a.recycle();
        }
    }

    public void setDeletable(boolean deletable) {
        tvBackHint.setText(deletable ? R.string.backspace_hint : R.string.back_hint);
    }

    public void setTurnable(boolean turnable) {
        llPageHint.setVisibility(turnable ? VISIBLE : GONE);
    }
}
