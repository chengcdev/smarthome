package com.mili.smarthome.tkj.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.DisplayUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

public class CallHintView extends FrameLayout{

    private View mContentView;
    private TextView mTvHint, mTvStateHint;
    private TextView mTvDoorHint;

    public CallHintView(Context context) {
        this(context, null);
    }

    public CallHintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (mContentView == null) {
            mContentView = LayoutInflater.from(context).inflate(R.layout.hintview_call, this);
        }
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mTvStateHint = (TextView) findViewById(R.id.tv_statehint);
        mTvDoorHint = (TextView) findViewById(R.id.tv_doorhint);
    }

    public void hideHint() {
        mTvHint.setVisibility(INVISIBLE);
        mTvStateHint.setVisibility(INVISIBLE);
        mTvDoorHint.setVisibility(INVISIBLE);
    }

    public void showHint(int resId) {
        if (mTvHint != null) {
            mTvHint.setTextSize(getResources().getDimension(R.dimen.sp_36));
            mTvHint.setTextColor(getResources().getColor(R.color.txt_white));
            mTvHint.setText(resId);
            mTvHint.setVisibility(VISIBLE);
        }
        mTvStateHint.setVisibility(INVISIBLE);
        mTvDoorHint.setVisibility(INVISIBLE);
    }

    public void showMonitorHint(int resId) {
        mTvHint.setTextSize(getResources().getDimension(R.dimen.sp_20));
        mTvHint.setTextColor(getResources().getColor(R.color.txt_green));
        mTvHint.setText(resId);
        mTvHint.setVisibility(VISIBLE);
        mTvStateHint.setVisibility(INVISIBLE);
        mTvDoorHint.setVisibility(INVISIBLE);
    }

    public void showCallHint(int resId, int stateResId, int stateColorId) {
        mTvHint.setTextSize(getResources().getDimension(R.dimen.sp_48));
        mTvHint.setTextColor(getResources().getColor(R.color.txt_white));
        mTvHint.setText(resId);
        mTvHint.setVisibility(VISIBLE);

        mTvStateHint.setTextColor(getResources().getColor(stateColorId));
        mTvStateHint.setText(stateResId);
        mTvStateHint.setVisibility(VISIBLE);
        mTvDoorHint.setVisibility(INVISIBLE);
    }

    public void showCallHint(String hint, int stateTextId, int stateColorId) {
        if (hint != null && hint.length() > 9) {
            mTvHint.setTextSize(getResources().getDimension(R.dimen.sp_28));
        } else {
            mTvHint.setTextSize(getResources().getDimension(R.dimen.sp_36));
        }
        mTvHint.setTextColor(getResources().getColor(R.color.txt_white));
        mTvHint.setText(hint);
        mTvHint.setVisibility(VISIBLE);

        mTvStateHint.setTextColor(getResources().getColor(stateColorId));
        mTvStateHint.setText(stateTextId);
        mTvStateHint.setVisibility(VISIBLE);
        mTvDoorHint.setVisibility(INVISIBLE);
    }

    public void showDoorHint(int textId, int colorId) {
        mTvDoorHint.setText(textId);
        mTvDoorHint.setTextColor(getResources().getColor(colorId));
        mTvDoorHint.setVisibility(VISIBLE);
    }

    public void hideDoorHint() {
        mTvDoorHint.setVisibility(INVISIBLE);
    }
}
