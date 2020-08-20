package com.mili.smarthome.tkj.fragment;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.provider.RoomSubDest;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.MessageDao;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainFragment extends BaseMainFragment{


    private TextView mTvTime;
    private TextView mTvLocation;
    private TextView mTvDeviceNo;
    /**
     * 时间更新间隔
     */
    private static final long TIME_REFRESH_INTERVAL = 5 * 1000;
    /**
     * 更新时间任务
     */
    private Runnable mTimeTask = new TimeTask();
    /**
     * 更新信息任务
     */
    private Runnable mMsgTask;
    private RoomSubDest roomSubDest;
    private MessageDao messageDao;
    private MainActivity currentActivity;
    private String TAG = "MainFragment";

    @Override
    public void initView(View view) {
        mTvTime = (TextView) view.findViewById(R.id.tv_time);
        mTvLocation = (TextView) view.findViewById(R.id.tv_location);
        mTvDeviceNo = (TextView) view.findViewById(R.id.tv_deviceno);

        initData();
    }

    private void initData() {
        mTvLocation.setText(getString(R.string.main_location));
        mTvTime.setText(getTime());
        //显示编号规则
        showDeviceNo();
        mTimeTask.run();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        currentActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取人体感应
        Constant.ScreenId.SCREEN_BODY_STATE = AppConfig.getInstance().getBodyInduction();

        if (currentActivity != null) {
            currentActivity.currentFrag = this;
        }

        //显示信息滚动
        if (messageDao == null) {
            messageDao = new MessageDao();
        }
        //五秒后开始滚动
        mMsgTask = new MsgTask(0);
        mMsgTask.run();
    }

    private void showDeviceNo() {
        if (roomSubDest == null) {
            roomSubDest = new RoomSubDest(getContext());
        }
        String subDestDevNumber = roomSubDest.getSubDestDevNumber();

        mTvDeviceNo.setText(subDestDevNumber);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }


    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 更新时间任务
     */
    private class TimeTask implements Runnable {

        @Override
        public void run() {
            mMainHandler.postDelayed(TimeTask.this, TIME_REFRESH_INTERVAL);
            mTvTime.setText(getTime());
        }
    }

    /**
     * 更新信息任务
     */
    private class MsgTask implements Runnable {

        int count;

        private MsgTask(int count) {
            this.count = count;
        }

        @Override
        public void run() {
//            LogUtils.e(TAG + "  MsgTask count: " + count);
            if (App.getInstance().getCurrentActivity() instanceof MainActivity) {
                if (count == 5 && currentActivity != null && currentActivity.currentFrag instanceof MainFragment) {
                    count = 0;
                    if (messageDao.queryAllCount() > 0) {

//                    LogUtils.e(TAG + "  MsgTask toMessageDialogFragment: " + messageDao.queryAllCount());

                        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
                        if (currentActivity != null) {
                            currentActivity.currentFrag = messageDialogFragment;
                        }
                        AppUtils.getInstance().replaceFragment(getActivity(), messageDialogFragment, R.id.fl, "messageDialogFragment");
                        mMainHandler.removeCallbacks(this);
                        return;
                    } else {

//                    LogUtils.e(TAG + "  MsgTask toMessageDialogFragment: " + messageDao.queryAllCount());

                        if (currentActivity != null) {
                            if (currentActivity.currentFrag instanceof MainFragment) {
                                count = 0;
                                mMainHandler.postDelayed(this, 1000);
                                count++;
                                return;
                            } else {
                                count = 0;
                                mMainHandler.removeCallbacks(this);
                                return;
                            }
                        }
                    }
                }
                count++;
                mMainHandler.postDelayed(this, 1000);
            }

        }
    }

}
