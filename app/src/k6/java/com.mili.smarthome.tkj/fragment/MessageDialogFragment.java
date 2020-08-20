package com.mili.smarthome.tkj.fragment;


import android.widget.TextView;

import com.android.provider.RoomSubDest;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.base.BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.MessageDao;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.message.MessageBean;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.widget.ScrollTextView;

import java.util.List;

/**
 * 信息
 */

public class MessageDialogFragment extends BaseFragment {

    private MyRun myRun = new MyRun();
    private TextView mTvRightTitle;
    private TextView mTvLeftTitle;
    private TextView mTvMessageTitle;
    private RoomSubDest roomSubDest;
    private MessageDao messageDao;
    private int count = 0;
    private ScrollTextView mTvScroll;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_dialog;
    }

    @Override
    protected void bindView() {
        mTvLeftTitle = findView(R.id.tv_left_title);
        mTvRightTitle = findView(R.id.tv_right_title);
        mTvMessageTitle = findView(R.id.tv_message_title);
        mTvScroll = findView(R.id.tv_scroll);

        mTvLeftTitle.setText(getString(R.string.main_location));
        //显示编号规则
        showDeviceNo();
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }


    private void showDeviceNo() {
        if (roomSubDest == null) {
            roomSubDest = new RoomSubDest(getContext());
        }
        String subDestDevNumber = roomSubDest.getSubDestDevNumber();

        mTvRightTitle.setText(subDestDevNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacks(myRun);
    }

    private void initData() {
        if (messageDao == null) {
            messageDao = new MessageDao();
        }
        List<MessageBean> msgList = messageDao.getAllMessage();
        if (count >= msgList.size()) {
            count = 0;
            if (AppUtils.getInstance().isMainAct() && isAdded()) {
                Constant.IS_MSGDIALOG_EXIT = true;
                if (App.getInstance().getCurrentActivity() instanceof MainActivity) {
                    exitFragment(this);
                }
            }
            return;
        }
        MessageBean messageBean = msgList.get(count);
        mTvMessageTitle.setText(messageBean.getTitle());
        mTvScroll.setText(messageBean.getContent());
        mMainHandler.postDelayed(myRun, 15*1000);
    }


    class MyRun implements Runnable {
        @Override
        public void run() {
            if (SystemSetUtils.isScreenOn() && isAdded()) {
                count++;
                initData();
            }
        }
    }
}
