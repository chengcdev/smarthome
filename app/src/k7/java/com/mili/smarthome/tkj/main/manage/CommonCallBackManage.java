package com.mili.smarthome.tkj.main.manage;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.android.CommStorePathDef;
import com.android.interf.IBodyInductionListener;
import com.android.interf.ICardStateListener;
import com.android.interf.IDoorAlarmListener;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.activity.ScreenSaverActivity;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressMainActivity;
import com.mili.smarthome.tkj.main.entity.HintBean;
import com.mili.smarthome.tkj.main.face.activity.BaseFaceActivity;
import com.mili.smarthome.tkj.main.face.activity.MegviiFaceRecogActivity;
import com.mili.smarthome.tkj.main.face.activity.WffrFaceRecogActivity;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.qrcode.CaptureActivity;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.activity.HintActivity;
import com.mili.smarthome.tkj.set.fragment.MessageDialogFragment;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import static com.mili.smarthome.tkj.set.Constant.OPENDOOR_ROOMNO;

public class CommonCallBackManage implements ICardStateListener, IBodyInductionListener, IDoorAlarmListener {

    private static CommonCallBackManage commonInterfaceManage;
    private String TAG = "CommonCallBackManage";
    private Context mContext;
    public static final String QR_DECODE = "decode";
    public static final String QR_ENCODE = "encode";


    public static CommonCallBackManage getInstance() {
        if (commonInterfaceManage == null) {
            commonInterfaceManage = new CommonCallBackManage();
        }
        return commonInterfaceManage;
    }

    public void initCallBack(Context context) {
        mContext = context;
        //卡操作接口
        SinglechipClientProxy.getInstance().addCardStateListener(this);
        //人体感应
        SinglechipClientProxy.getInstance().setBodyInductionListener(this);
        //报警上报
        SinglechipClientProxy.getInstance().setDoorAlarmListener(this);
    }


    @Override
    public void onCardState(int state, String roomNo) {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        if (AppPreferences.isReset() || Constant.ScreenId.SCREEN_IS_SET ||
                currentActivity instanceof BaseFaceActivity) {
            switch (state) {
                case 0:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1503_PATH);
                    break;
                case 1:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
                    break;
            }
        } else {
            switch (state) {
                case 0:
                    HintEventManage.getInstance().toHintAct(mContext, new HintBean(Constant.MONITOR_INVALID_CARD));
                    break;
                case 1:
                    OPENDOOR_ROOMNO = roomNo;
                    HintEventManage.getInstance().toHintAct(mContext, new HintBean(Constant.OPEN_DOOR_REMIND));
                    break;
            }
        }
    }

    @Override
    public void onBodyInduction() {
        Activity currentActivity = App.getInstance().getCurrentActivity();
        Fragment frgCurrent = AppManage.getInstance().frgCurrent;
        if (AppConfig.getInstance().getCallType() == 0) {
            //编码式
            if (currentActivity instanceof MainActivity) {
                if (frgCurrent instanceof MainFragment && ((MainFragment) frgCurrent).numview != null &&
                        ((MainFragment) frgCurrent).numview.getNum().equals("") &&
                        ((MainFragment) frgCurrent).adminCount <= 0) {
                    bodyInductionToAct(currentActivity);
                } else if (frgCurrent instanceof MessageDialogFragment) {
                    bodyInductionToAct(currentActivity);
                }
            } else if (currentActivity instanceof ScreenSaverActivity) {
                bodyInductionToAct(currentActivity);
            }
        } else {
            //直按式
            if (currentActivity instanceof DirectPressMainActivity && !DirectPressMainActivity.isEdit
                    || currentActivity instanceof ScreenSaverActivity) {
                bodyInductionToAct(currentActivity);
            }
        }
    }

    /**
     * 人体感应跳转到对应界面
     */
    private void bodyInductionToAct(Activity topAct) {
        final int bodyFeeling = AppConfig.getInstance().getBodyInduction();
        LogUtils.w(TAG + "====== 人体感应上报 ====== bodyFeeling: " + bodyFeeling);
        switch (bodyFeeling) {
            //0 触发开屏
            case 0:
                if (AppManage.getInstance().isScreenProAct()) {
                    //计时关屏时间
                    String screenServiceName = "com.mili.smarthome.tkj.main.service.ScreenService";
                    if (!AppManage.getInstance().isServiceRunning(screenServiceName)) {
                        AppManage.getInstance().startScreenService();
                    }
                }
                break;
            //人脸识别
            case 1:
                if (topAct instanceof ScreenSaverActivity) {
                    topAct.finish();
                }
                switch (AppConfig.getInstance().getFaceManufacturer()) {
                    case 0:
                        AppManage.getInstance().toAct(WffrFaceRecogActivity.class);
                        break;
                    case 1:
                        AppManage.getInstance().toAct(MegviiFaceRecogActivity.class);
                        break;
                }
                break;
            //扫码开门
            case 2:
                if (topAct instanceof ScreenSaverActivity) {
                    topAct.finish();
                }
                AppManage.getInstance().toActExtra(mContext, CaptureActivity.class, Constant.KEY_PARAM, QR_DECODE);
                break;
            //蓝牙开门器
            case 3:
                if (topAct instanceof ScreenSaverActivity) {
                    topAct.finish();
                }
                AppManage.getInstance().toActExtra(mContext, CaptureActivity.class, Constant.KEY_PARAM, QR_ENCODE);
                break;
        }
    }

    // 1:强行开门报警   2:门未关超时报警  3:防拆破坏报警
    @Override
    public void onDoorAlarm(int doorAlarmType) {
        LogUtils.w(TAG + " onDoorAlarm: " + doorAlarmType);
        if (!SystemSetUtils.isScreenOn()) {
            SystemSetUtils.screenOn();
        }
        //设置界面,人脸界面不做跳转，只播声音
        Activity currentActivity = App.getInstance().getCurrentActivity();
        switch (doorAlarmType) {
            case 1:
                if (AlarmParamDao.getForceOpen() == 1) {
                    if (currentActivity instanceof BaseFaceActivity && Constant.ScreenId.SCREEN_IS_SET) {
                        PlaySoundUtils.playAlarmSound();
                    } else {
                        HintEventManage.getInstance().toHintAct(mContext, new HintBean(Constant.MONITOR_NOT_CLOSE_DOOR));
                    }
                }
                break;
            case 2:
                if (EntranceGuardDao.getAlarmOut() == 1) {
                    if (currentActivity instanceof BaseFaceActivity && Constant.ScreenId.SCREEN_IS_SET) {
                        PlaySoundUtils.playAlarmSound();
                    } else {
                        HintEventManage.getInstance().toHintAct(mContext, new HintBean(Constant.MONITOR_NOT_CLOSE_DOOR));
                    }
                }
                break;
            case 3:
                if (currentActivity instanceof HintActivity && HintActivity.isTalking) {
                    return;
                }
                PlaySoundUtils.playAlarmSound();
                break;
        }
    }

}
