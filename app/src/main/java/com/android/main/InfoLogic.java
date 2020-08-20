package com.android.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.Common;
import com.android.IntentDef;
import com.android.client.SetDriverSinglechipClient;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.dao.MessageDao;
import com.mili.smarthome.tkj.main.activity.ScreenSaverActivity;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SysTimeSetUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class InfoLogic extends ServiceLogic implements IntentDef.OnNetCommDataReportListener {

    private final static String tag = "InfoLogic";
    private Context mContext;
    private MessageDao messageDao;

    public InfoLogic(String action) {
        super(action);
        MainJni.setmInfoCommListener(this);
    }

    public void InfoLogicStart(Context context) {
        mContext = context;
    }

    private void saveInfo(byte[] data) {
        int len = 0;

        // 下发时间
        int year = Common.bytes2short(data, len);    // 年
        len = len + 2;

        int month = data[len];    // 月
        len = len + 1;

        int day = data[len];    // 日
        len = len + 1;

        int week = data[len];    // 星期
        len = len + 1;

        int hour = data[len];    // 时
        len = len + 1;

        int min = data[len];    // 分
        len = len + 1;

        int sec = data[len];    // 秒
        len = len + 1;

        int saveDay = data[len];    // 保留天数
        len = len + 1;

        byte[] title = new byte[45];
        System.arraycopy(data, len, title, 0, 40);    // 标题
        len = len + 40;

        byte[] path = new byte[55];
        System.arraycopy(data, len, path, 0, 50);    // 文本路径

        String t = Common.byteToString(title);

        String p = Common.byteToString(path);

//        String time = year +
//                SysTimeSetUtils.getDate(month) +
//                SysTimeSetUtils.getDate(day) +
//                SysTimeSetUtils.getDate(hour) +
//                SysTimeSetUtils.getDate(min) +
//                SysTimeSetUtils.getDate(sec);

        LogUtils.w("  saveDay>>>> :" + saveDay);

        try {
            //格式"yyyyMMdd hh:mm:ss"
            String timeSource = year + SysTimeSetUtils.getDate(month) + SysTimeSetUtils.getDate(day) +
                    " " +
                    SysTimeSetUtils.getDate(hour) + ":" + SysTimeSetUtils.getDate(min) + ":" + SysTimeSetUtils.getDate(sec);
            //获取装换成long类型的时间

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault());

            long times = simpleDateFormat.parse(timeSource).getTime();

            LogUtils.w(tag + "  times>>>> : " + times);

            //添加数据到数据库
            if (messageDao == null) {
                messageDao = new MessageDao();
            }
            messageDao.addModel(t, p, String.valueOf(times), saveDay);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {

        if (!action.equals(IntentDef.MODULE_INFO)) {
            return;
        }

        LogUtils.e(tag + "  info message type : " + type);

        //下载信息时需开屏关屏保
        if (type == IntentDef.PubIntentTypeE.Info_NewInfoNofity
                || type == IntentDef.PubIntentTypeE.Info_ClearInfoNofity) {

            //开屏，开背光灯
            if (!SystemSetUtils.isScreenOn()) {
                LogUtils.e(tag + " open screen....");
                SystemSetUtils.screenOn();
            }
        }

        switch (type) {
            case IntentDef.PubIntentTypeE.Info_NewInfoNofity:// 新消息
                saveInfo(data);
                break;

            case IntentDef.PubIntentTypeE.Info_ClearInfoNofity:// 清空信息
                if (messageDao == null) {
                    messageDao = new MessageDao();
                }
                messageDao.clearAllMessage();
                break;

            default:
                break;
        }

        if (BuildConfig.DevType == MainCommDefind.DEVICE_TYPE_K4_X1600_REL) {
            if (!SetDriverSinglechipClient.getInstance().getSystemSleep()) {
                SetDriverSinglechipClient.getInstance().setSystemSleep(1);
            }
        }
        //退出屏保
        Activity topActivity = App.getInstance().getCurrentActivity();
        if (topActivity instanceof ScreenSaverActivity) {
            topActivity.finish();
        }
        //K6退出屏保
        App.getInstance().sendBroadcast(new Intent(Const.ActionId.SCREEN_SAVER_EXIT));
    }

}

