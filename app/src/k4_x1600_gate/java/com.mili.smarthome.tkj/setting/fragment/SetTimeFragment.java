package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SysTimeSetUtils;
import com.mili.smarthome.tkj.widget.FormatInputView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 时间设置
 */
public class SetTimeFragment extends K4BaseFragment implements View.OnTouchListener {

    private FormatInputView mIvDate, mIvTime;
    private LinearLayout mLlcontent;
    private RelativeLayout mLlButton;
    private TextView mTvHint;
    private TextView mTvHead;


    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        backspaceExit();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        save();
        mTvHint.setText(R.string.set_ok);
        showView(false);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitFragment();
            }
        }, Constant.SET_HINT_TIMEOUT);
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        int lastCursor = mIvTime.getCursorIndex();
        inputNum(code);
        if (mIvTime.isFocused() && mIvTime.getCursorIndex() == lastCursor
                && mIvTime.getCursorIndex() == mIvTime.getMaxLength()-1) {
            mIvDate.requestFocus();
            mIvDate.setCursorIndex(0);
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_time;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView() {
        super.bindView();
        mIvDate = findView(R.id.iv_date);
        mIvTime = findView(R.id.iv_time);
        mIvDate.setOnTouchListener(this);
        mIvTime.setOnTouchListener(this);

        mLlcontent = findView(R.id.ll_content);
        mLlButton = findView(R.id.ll_button);

        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
    }

    @Override
    protected void bindData() {
        super.bindData();
        if (mTvHead != null) {
            mTvHead.setText(R.string.setting_0405);
        }
        showView(true);
        mIvDate.requestFocus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    private void showView(boolean show) {
        if (show) {
            mLlcontent.setVisibility(View.VISIBLE);
            mLlButton.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.INVISIBLE);
        } else {
            mLlcontent.setVisibility(View.INVISIBLE);
            mLlButton.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
        }
    }

    private void save() {
        StringBuilder datetime = new StringBuilder()
                .append(mIvDate.getText())
                .append(" ")
                .append(mIvTime.getText());

        try {
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            SysTimeSetUtils.setSysTime(mContext, sdf.parse(datetime.toString()));
        } catch (ParseException e) {
            LogUtils.e(e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        if (view.getId() == R.id.iv_date) {
            mIvDate.requestFocus();
            mIvDate.setCursorIndex(2);
        } else if (view.getId() == R.id.iv_time) {
            mIvTime.requestFocus();
            mIvTime.setCursorIndex(0);
        }
        return false;
    }
}
