package com.mili.smarthome.tkj.main.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ViewUtils;

public class MessageView extends FrameLayout {

    private TextView tvTitle;
    private TextView tvContent;

    public MessageView(@NonNull Context context) {
        this(context, null, 0);
    }

    public MessageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.view_message, this);
        tvTitle = ViewUtils.findView(this, R.id.tv_title);
        tvContent = ViewUtils.findView(this, R.id.tv_content);
    }

    public void setMessage(String title, String content) {
        tvTitle.setText(title);
        tvContent.setText(content);
    }
}
