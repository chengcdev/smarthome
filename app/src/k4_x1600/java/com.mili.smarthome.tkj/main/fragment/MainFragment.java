package com.mili.smarthome.tkj.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.base.K4Config;
import com.mili.smarthome.tkj.dao.MessageDao;
import com.mili.smarthome.tkj.message.MessageBean;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.widget.ScrollTextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainFragment extends K4BaseFragment implements View.OnClickListener, FreeObservable.FreeObserver {

    /*时间刷新间隔*/
    private static final long TIME_REFRESH_INTERVAL = 5*1000;
    private static final int MSG_MESSAGE_QUERY = 0x10;
    private static final int MSG_MESSAGE = 0x11;

    private LinearLayout mLlNet;
    private TextView mTvTime;
    private TextView mTvDevno;
    private TextView mTvHint;
    private TextView mTvHead;
    private ScrollTextView mTvMessage;
    private ImageView ivAuth;

    private List<MessageBean> msgList;
    private int msgIndex = -1;
    private boolean mShowHint = false;
    private String mDevDesc;

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_MESSAGE_QUERY:
                msgList = new MessageDao().getAllMessage();
                if (msgList != null && msgList.size() > 0) {
                    msgIndex = 0;
                    showMessage(msgList.get(msgIndex));
                } else {
                    mMainHandler.sendEmptyMessageDelayed(MSG_MESSAGE_QUERY, 5000);
                }
                break;
            case MSG_MESSAGE:
                msgIndex++;
                if (msgList == null || msgIndex >= (msgList.size())) {
                    msgIndex = -1;
                    showDefault();
                } else {
                    showMessage(msgList.get(msgIndex));
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mLlNet = findView(R.id.ll_net);
        mTvTime = findView(R.id.tv_time);
        mTvDevno = findView(R.id.tv_devno);
        mTvHint = findView(R.id.tv_hint);
        mTvHead = findView(R.id.tv_head);
        mTvMessage = findView(R.id.tv_message);
        ivAuth = findView(R.id.iv_auth);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDevDesc = K4Config.getInstance().getDeviceDesc(false);
        LogUtils.d(" devDesc is " + mDevDesc);
    }

    @Override
    protected void bindData() {
        super.bindData();
        //是否授权
        if (AuthManage.isAuth()) {
            ivAuth.setVisibility(View.GONE);
        }else {
            ivAuth.setVisibility(View.VISIBLE);
            ivAuth.setOnClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FreeObservable.getInstance().addObserver(this);
        FreeObservable.getInstance().resetFreeTime();
        setKeyboardMode(0);
        setKeyboardText("");
        if (!mShowHint) {
            showDefault();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        FreeObservable.getInstance().removeObserver(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_auth:
                //授权
                AuthManage.startAuth(getActivity());
                break;
        }
    }

    /** 更新时间任务 */
    private class TimeTask implements Runnable {

        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mTvTime.setText(sdf.format(System.currentTimeMillis()));
            mMainHandler.postDelayed(TimeTask.this, TIME_REFRESH_INTERVAL);
        }
    }

    /**
     * 显示默认主界面
     */
    private void showDefault() {
        mMainHandler.removeCallbacksAndMessages(null);

        if (mTvDevno != null) {
            mTvDevno.setText(mDevDesc);
            mTvDevno.setVisibility(View.VISIBLE);
        }
        mLlNet.setVisibility(View.VISIBLE);
        mTvTime.setVisibility(View.VISIBLE);
        mTvHead.setVisibility(View.GONE);
        mTvMessage.setVisibility(View.GONE);
        mTvHint.setVisibility(View.GONE);

        //系统时间刷新
        TimeTask timeTask = new TimeTask();
        timeTask.run();

        //延迟5秒后查询信息
        mMainHandler.sendEmptyMessageDelayed(MSG_MESSAGE_QUERY, 5000);
    }

    /**
     * 显示信息界面
     * @param msg   信息内容
     */
    public void showMessage(MessageBean msg) {
        mMainHandler.removeCallbacksAndMessages(null);
        mTvHead.setText(msg.getTitle());
        mTvMessage.setText(msg.getContent());
        mTvHead.setVisibility(View.VISIBLE);
        mTvMessage.setVisibility(View.VISIBLE);
        mLlNet.setVisibility(View.GONE);
        mTvTime.setVisibility(View.GONE);
        mTvDevno.setVisibility(View.GONE);
        mMainHandler.sendEmptyMessageDelayed(MSG_MESSAGE, 15000);
    }

    /**
     * 显示提示信息界面
     * @param textId    提示内容
     * @param colorId   内容颜色
     */
    public void showHint(Context context, int textId, int colorId) {
        mShowHint = true;
        mMainHandler.removeCallbacksAndMessages(null);
        mLlNet.setVisibility(View.VISIBLE);
        mTvTime.setVisibility(View.VISIBLE);
        mTvHead.setVisibility(View.GONE);
        mTvMessage.setVisibility(View.GONE);
        mTvDevno.setVisibility(View.GONE);
        mTvHint.setVisibility(View.VISIBLE);
        mTvHint.setText(textId);
        mTvHint.setTextColor(context.getResources().getColor(colorId));
        FreeObservable.getInstance().resetFreeTime();
    }

    /**
     * 隐藏提示界面
     */
    public void hideHint() {
        mShowHint = false;
        showDefault();
        FreeObservable.getInstance().resetFreeTime();
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (AppConfig.getInstance().getBodyInduction() == 1) {
            if (freeTime / 1000 == 10) {
                if (AppConfig.getInstance().getScreenSaver() == 1) {
                    FreeObservable.getInstance().startScreenSaver();
                } else if (AppConfig.getInstance().getPowerSaving() == 1) {
                    FreeObservable.getInstance().systemSleep();
                }
                return true;
            }
        }
        return false;
    }
}
