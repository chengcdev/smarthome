package com.mili.smarthome.tkj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;

public class CustomButtonView extends RelativeLayout{

    private ImageView mImaIcon;
    private TextView mTvTitle;
    private Context mContext;
    private LinearLayout mLinRoot;
    private int mDefaulIconId;
    private int mFocusIconId;

    public CustomButtonView(Context context) {
        this(context, null);
    }

    public CustomButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_button_layout, this);
        mImaIcon = (ImageView) view.findViewById(R.id.img_icon);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mLinRoot = (LinearLayout) view.findViewById(R.id.lin_root);
    }

    public void setView(int defaultIconId,int focusIconId, String content) {
        mDefaulIconId = defaultIconId;
        mFocusIconId = focusIconId;
        mLinRoot.setBackgroundResource(R.drawable.comm_down_btn_white4);
        mImaIcon.setImageResource(mDefaulIconId);
        mTvTitle.setText(content);
        mTvTitle.setTextColor(getResources().getColor(R.color.txt_black));
    }

    public void setClickState(View view, boolean isFocus) {
        if (isFocus) {
            mLinRoot.setBackgroundResource(R.drawable.comm_down_btn_blue4);
            mImaIcon.setImageResource(mFocusIconId);
            mTvTitle.setTextColor(getResources().getColor(R.color.txt_white));
        }else {
            mLinRoot.setBackgroundResource(R.drawable.comm_down_btn_white4);
            mImaIcon.setImageResource(mDefaulIconId);
            mTvTitle.setTextColor(getResources().getColor(R.color.txt_black));
        }
    }
}
