package com.mili.smarthome.tkj.set.fragment;


import android.app.Activity;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.base.BaseFragment;
import com.mili.smarthome.tkj.dao.MessageDao;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.manage.MessageManage;
import com.mili.smarthome.tkj.message.MessageBean;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.CustomScrollView;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.util.List;

/**
 * 信息
 */

public class MessageDialogFragment extends BaseFragment {

    private MyRun myRun = new MyRun();
    private TextView mTvMessageTitle;
    private MessageDao messageDao;
    private int count = 0;
    private CustomScrollView mTvScroll;
    private List<MessageBean> mMsgList;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_dialog;
    }

    @Override
    protected void bindView() {
        mTvMessageTitle = findView(R.id.custom_scrollview_title);
        mTvScroll = findView(R.id.custom_scrollview);
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        exitFragment(this);
        mMainHandler.removeCallbacks(myRun);
    }


    private void initData() {
        //标题停止滚动
        mTvMessageTitle.setSelected(false);
        if (messageDao == null) {
            messageDao = new MessageDao();
        }
        mMsgList = messageDao.getAllMessage();
        if (count >= mMsgList.size()) {
            count = 0;
            Activity currentActivity = App.getInstance().getCurrentActivity();
            if (currentActivity instanceof MainActivity) {
                AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_INIT_MAIN);
            }else if (currentActivity instanceof DirectPressMainActivity) {
                exitFragment(this);
            }
            MessageManage.getInstance().initMessage();
            return;
        }
        MessageBean messageBean = mMsgList.get(count);
        mTvMessageTitle.setText(messageBean.getTitle());
        mTvScroll.setText(messageBean.getContent(),4);
        mMainHandler.postDelayed(myRun, 15 * 1000);

        mTvMessageTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTvMessageTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //延时一秒开始滚动
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTvMessageTitle.setSelected(true);
                    }
                }, 1000);
            }
        });
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
