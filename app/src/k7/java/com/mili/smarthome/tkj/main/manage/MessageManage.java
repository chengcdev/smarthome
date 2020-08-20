package com.mili.smarthome.tkj.main.manage;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.MessageDao;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageManage {

    private static MessageManage messageManage;
    private MessageDao mMessageDao;
    private ScheduledExecutorService mExecutorService;

    public static MessageManage getInstance() {
        if (messageManage == null) {
            messageManage = new MessageManage();
        }
        return messageManage;
    }

    public void initMessage() {
        if (mMessageDao == null) {
            mMessageDao = new MessageDao();
        }
        if (mMessageDao.queryAllCount() > 0) {
            if (mExecutorService != null) {
                mExecutorService.shutdownNow();
            }
            mExecutorService = Executors.newSingleThreadScheduledExecutor();
            mExecutorService.scheduleAtFixedRate(mRunnable, 5, 5, TimeUnit.SECONDS);
        }

    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.w(" MessageManage run mMessageDao.queryAllCount()： " + mMessageDao.queryAllCount());
            Activity currentActivity = App.getInstance().getCurrentActivity();
            if (currentActivity instanceof MainActivity || currentActivity instanceof DirectPressMainActivity) {
                if (mMessageDao.queryAllCount() > 0) {
                    if (AppConfig.getInstance().getCallType() == 0) {
                        //编码式
                        Fragment fragment = AppManage.getInstance().frgCurrent;
                        if (fragment instanceof MainFragment) {
                            if (((MainFragment) fragment).numview != null &&
                                    ((MainFragment) fragment).numview.getNum().equals("") &&
                                    (((MainFragment) fragment).adminCount <= 0)) {
                                AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_SHOW_MESSAGE);
                            }
                        }
                    } else {
                        if (currentActivity instanceof DirectPressMainActivity) {
                            //直按式
                            AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_SHOW_MESSAGE);
                        }
                    }
                    mExecutorService.shutdownNow();
                }
            }
        }
    };

}
