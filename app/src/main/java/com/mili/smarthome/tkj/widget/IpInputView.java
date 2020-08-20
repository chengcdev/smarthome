package com.mili.smarthome.tkj.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class IpInputView extends FormatInputView {

    public IpInputView(Context context) {
        this(context, null, 0);
    }

    public IpInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IpInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public IpInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setInputFormat(FORMAT_IP);
    }
}
