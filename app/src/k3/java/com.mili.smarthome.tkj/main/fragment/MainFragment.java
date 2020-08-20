package com.mili.smarthome.tkj.main.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.android.provider.RoomSubDest;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.dao.MessageDao;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.view.MessageView;
import com.mili.smarthome.tkj.message.MessageBean;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainFragment extends K3BaseFragment {

    public static final String TEXT_ID = "extra_text_id";
    public static final String COLOR_ID = "extra_color_id";

    /** 时间更新间隔 */
    private static final long TIME_REFRESH_INTERVAL = 5 * 1000;

    private static final int MSG_DEFAULT = 0xFF;
    private static final int MSG_MESSAGE_QUERY = 0x10;
    private static final int MSG_MESSAGE = 0x11;

    private TextView tvAreaName;
    private TextView tvTime;
    private TextView tvDevNo;
    private MessageView vwMessage;

    /** 更新时间任务 */
    private Runnable mTimeTask = new TimeTask();

    private RoomSubDest mRoomSubDest;
    private String mDevDesc;

    private MessageDao msgDao;
    private List<MessageBean> msgList;
    private int msgIndex = -1;

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DEFAULT:
                showDefault();
                break;
            case MSG_MESSAGE_QUERY:
                msgList = msgDao.getAllMessage();
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
        tvAreaName = findView(R.id.tv_area_name);
        tvTime = findView(R.id.tv_time);
        tvDevNo = findView(R.id.tv_dev_no);
        vwMessage = findView(R.id.vw_message);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        if (mContext == null) {
            super.setArguments(args);
        } else {
            showHint(args);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAreaName.setText(AppConfig.getInstance().getAreaName());
        if (mRoomSubDest == null) {
            mRoomSubDest = new RoomSubDest(getContext());
        }
        mDevDesc = mRoomSubDest.getSubDestDevNumber();
        if (msgDao == null) {
            msgDao = new MessageDao();
        }
        showHint(getArguments());
    }

    @Override
    public void onResume() {
        super.onResume();
        String devDesc = mRoomSubDest.getSubDestDevNumber();
        if (!devDesc.equals(mDevDesc)) {
            mDevDesc = devDesc;
            showDefault();
        }
        FreeObservable.getInstance().resetFreeTime();
    }

    @Override
    public void onDestroyView() {
        mMainHandler.removeCallbacks(mTimeTask);
        super.onDestroyView();
    }

    public void showHint(Bundle args) {
        if (args != null) {
            int textId = args.getInt(TEXT_ID);
            int colorId = args.getInt(COLOR_ID, R.color.txt_white);
            showHint(textId, colorId);
        } else {
            showDefault();
        }
    }

    public void showHint(@StringRes int textId, @ColorRes int colorId) {
        FreeObservable.getInstance().resetFreeTime();
        mMainHandler.removeCallbacksAndMessages(null);

        setMainTabEnabled(false);
        tvTime.setText(mDevDesc);
        tvDevNo.setText(getResources().getText(textId));
        tvDevNo.setTextColor(getResources().getColor(colorId));
        tvDevNo.setVisibility(View.VISIBLE);
        vwMessage.setVisibility(View.GONE);

        mMainHandler.sendEmptyMessageDelayed(MSG_DEFAULT, 5000);
    }

    public void showDefault() {
        mMainHandler.removeCallbacksAndMessages(null);

        setMainTabEnabled(true);
        mTimeTask.run();
        tvDevNo.setText(mDevDesc);
        tvDevNo.setTextColor(getResources().getColor(R.color.txt_white));
        tvDevNo.setVisibility(View.VISIBLE);
        vwMessage.setVisibility(View.GONE);

        mMainHandler.sendEmptyMessageDelayed(MSG_MESSAGE_QUERY, 5000);
    }

    public void showMessage(MessageBean msg) {
        mMainHandler.removeCallbacksAndMessages(null);

        setMainTabEnabled(true);
        tvTime.setText(mDevDesc);
        vwMessage.setMessage(msg.getTitle(), msg.getContent());
        vwMessage.setVisibility(View.VISIBLE);
        tvDevNo.setVisibility(View.GONE);

        mMainHandler.sendEmptyMessageDelayed(MSG_MESSAGE, 15000);
    }

    private void setMainTabEnabled(boolean enabled) {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setTabEnabled(enabled);
        }
    }

    /** 更新时间任务 */
    private class TimeTask implements Runnable {

        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvTime.setText(sdf.format(System.currentTimeMillis()));
            mMainHandler.postDelayed(TimeTask.this, TIME_REFRESH_INTERVAL);
        }
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
        return super.onFreeReport(freeTime);
    }
}
