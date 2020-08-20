package com.mili.smarthome.tkj.set.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;

@SuppressLint("AppCompatCustomView")
public class DeviceInfoTextView extends RelativeLayout {
    private Context mContext;
    private TextView mTV;

    public DeviceInfoTextView(Context context) {
        super(context);
        init(context);
    }

    public DeviceInfoTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DeviceInfoTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.device_info_textview, this);
        mTV = (TextView) findViewById(R.id.tv);
    }

    public void setText(String content){
        mTV.setText(content);
    }

    public void setLayoutParam(){
        mTV.setHeight(180);
        invalidate();
    }
}
