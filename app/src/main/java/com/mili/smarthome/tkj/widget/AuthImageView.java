package com.mili.smarthome.tkj.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.auth.AuthManage;

@SuppressLint("AppCompatCustomView")
public class AuthImageView extends ImageView implements View.OnClickListener {

    private Context mContext;

    public AuthImageView(Context context) {
        this(context, null);
    }

    public AuthImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuthImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOnClickListener(this);
        setImageResource(R.drawable.main_cloud);
        if (AuthManage.isAuth()) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        //授权
        AuthManage.startAuth(mContext);
    }
}
