package com.mili.smarthome.tkj.set.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.mili.smarthome.tkj.R;

@SuppressLint("AppCompatCustomView")
public class MainLayoutView extends RelativeLayout {
    private Context mContext;

    public MainLayoutView(Context context) {
        super(context);
        init(context);
    }

    public MainLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        setFocusable(false);
        setFocusableInTouchMode(false);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.main_layout, this);
        setPadding(70,75,40,0);
    }


}
