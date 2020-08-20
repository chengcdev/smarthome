package com.mili.smarthome.tkj.main.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.android.CommStorePathDef;
import com.android.interf.IBodyInductionListener;
import com.android.interf.ICardStateListener;
import com.android.interf.IDoorAlarmListener;
import com.android.interf.IFingerEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.call.ICallMonitorListener;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.face.FaceDelFragment;
import com.mili.smarthome.tkj.face.FaceMegviiOpenFragment;
import com.mili.smarthome.tkj.face.FacePromptFragment;
import com.mili.smarthome.tkj.face.FaceWffrOpenFragment;
import com.mili.smarthome.tkj.fragment.CallCenterFragment;
import com.mili.smarthome.tkj.fragment.CallResidentFragment;
import com.mili.smarthome.tkj.fragment.HintDialogFragment;
import com.mili.smarthome.tkj.fragment.MainFragment;
import com.mili.smarthome.tkj.fragment.MessageDialogFragment;
import com.mili.smarthome.tkj.fragment.PwdOpenFragment;
import com.mili.smarthome.tkj.fragment.QrOpenFragment;
import com.mili.smarthome.tkj.fragment.ScreenProFragment;
import com.mili.smarthome.tkj.fragment.ShowOpenQrFragment;
import com.mili.smarthome.tkj.present.IDriverSingerListener;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.view.CustomButtonView;

import java.util.ArrayList;
import java.util.List;

import static com.mili.smarthome.tkj.constant.Constant.OPENDOOR_ROOMNO;

public class MainActivity extends BaseMainActivity implements View.OnClickListener, IDoorAlarmListener, ICallMonitorListener,
        IFingerEventListener, IDriverSingerListener, ICardStateListener, IBodyInductionListener {

    private CustomButtonView btnFaceOpen;
    private CustomButtonView btnCallHouse;
    private CustomButtonView btnCallCenter;
    private CustomButtonView btnQrOpen;
    private CustomButtonView btnPwdOpen;
    public Fragment currentFrag;
    private MainFragment mainFragment;
    private FaceWffrOpenFragment faceWffrFragment;
    private FaceMegviiOpenFragment faceMegviiFragment;
    private FacePromptFragment facePromptFragment;
    private FaceDelFragment faceDelFragment;
    private CallResidentFragment residentFragment;
    private CallCenterFragment callCenterFragment;
    private Fragment qrOpenFragment;
    private PwdOpenFragment pwdOpenFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private MainReceiver mainReceiver;
    private HintDialogFragment hintDialogFragment;
    private String TAG = "MainActivity";
    public LinearLayout mLinBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        registerBrocastReceiver();
        initScreen();
        //初始化监听
        initListener();
        initBottomBtn();
    }

    private void initScreen() {
        Constant.ScreenId.SCREEN_CLOSE = AppConfig.getInstance().getPowerSaving() != 0;
        Constant.ScreenId.SCREEN_PROTECT =  AppConfig.getInstance().getScreenSaver() != 0;
    }

    private void initListener() {
        //卡操作接口
        SinglechipClientProxy.getInstance().addCardStateListener(this);
        SinglechipClientProxy.getInstance().setBodyInductionListener(this);
        SinglechipClientProxy.getInstance().setDoorAlarmListener(this);
        //指纹
        SinglechipClientProxy.getInstance().addFingerEventListener(this);
    }

    private void initCallListener() {
        //对讲接口
        CallManage.getInstance().initCallClient(this);
        CallManage.getInstance().setCallMonitorListener(this);
    }


    private void registerBrocastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.Action.MAIN_BOTTOM_BTN);
        filter.addAction(Constant.Action.MAIN_REFRESH_ACTION);
        filter.addAction(Constant.Action.BODY_FACE_RECOGNITION_ACTION);
        filter.addAction(Constant.Action.BODY_FACE_ENROLL_ACTION);
        filter.addAction(Constant.Action.BODY_FACE_DEL_ACTION);
        filter.addAction(Constant.Action.ACTION_TO_SCREEN_PRO);
        filter.addAction(Constant.Action.ACTION_SHOW_BOTTOM_BTN);
        filter.addAction(Constant.Action.ACTION_HIDE_BOTTOM_BTN);
        filter.addAction(Const.ActionId.KEY_DOWN_UPDATETOUCH);
        filter.addAction(Constant.Action.CALL_MONITOR_TALK);
        filter.addAction(Const.ActionId.ACTION_MULTI_MEDIA);
        filter.addAction(Const.ActionId.SCREEN_SAVER_EXIT);
        mainReceiver = new MainReceiver();
        registerReceiver(mainReceiver, filter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mLinBottom = (LinearLayout) findViewById(R.id.lin_bottom_btn);
        btnPwdOpen = (CustomButtonView) findViewById(R.id.btn_pwd_open);
        btnQrOpen = (CustomButtonView) findViewById(R.id.btn_qr_open);
        btnCallCenter = (CustomButtonView) findViewById(R.id.btn_call_center);
        btnCallHouse = (CustomButtonView) findViewById(R.id.btn_call_house);
        btnFaceOpen = (CustomButtonView) findViewById(R.id.btn_face_open);

        btnPwdOpen.setOnClickListener(this);
        btnQrOpen.setOnClickListener(this);
        btnCallCenter.setOnClickListener(this);
        btnCallHouse.setOnClickListener(this);
        btnFaceOpen.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        //关闭卡操作
//        CardManageHelper.getInstance().stopCardClient();
//        //关闭对讲
        CallManage.getInstance().stopCallCommClient();
//        //关闭指纹
//        SinglechipClientProxy.getInstance().setFingerEventListener(null);
        unregisterReceiver(mainReceiver);
        //关闭服务
        AppUtils.getInstance().stopScreenService();
    }

    public void initBottomBtn() {
        initCallListener();
        //是否显示二维码的按钮
        showBtnQrOpen();
        //是否显示人脸识别按钮
        showBtnFace();
        @SuppressLint("CommitTransaction")
        FragmentTransaction ft = initBottomTabState();
        mainFragment = new MainFragment();
        currentFrag = mainFragment;
        fragmentList.add(mainFragment);
        ft.replace(R.id.fl, mainFragment);
        ft.commitAllowingStateLoss();
        setBottomBtnEnable(true);
    }


    //恢复底部按钮状态，删除底部的按钮add的fragment
    public FragmentTransaction initBottomTabState() {
        setBottomBtnEnable(true);
        initBottomBtnState();
        //删除其他fragment
        @SuppressLint("CommitTransaction") FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentList.size() > 0) {
            for (int i = 0; i < fragmentList.size(); i++) {
                if (fragmentList.get(i) != null) {
                    ft.remove(fragmentList.get(i));
                }
            }
        }
        return ft;
    }

    private void initBottomBtnState() {
        btnPwdOpen.setView(R.drawable.main_key_icon, R.drawable.main_key_icon_down, getString(R.string.main_btn_1));
        btnQrOpen.setView(R.drawable.main_qr_scan, R.drawable.main_qr_scan_down, getString(R.string.main_btn_2));
        btnCallCenter.setView(R.drawable.main_call_center, R.drawable.main_call_center_down, getString(R.string.main_btn_3));
        btnCallHouse.setView(R.drawable.main_call_resident, R.drawable.main_call_resident_down, getString(R.string.main_btn_4));
        btnFaceOpen.setView(R.drawable.main_face_scan, R.drawable.main_face_scan_down, getString(R.string.main_btn_5));
    }

    //设置底部按钮是否可以点击
    public void setBottomBtnEnable(boolean isEnable) {
        btnPwdOpen.setEnabled(isEnable);
        btnQrOpen.setEnabled(isEnable);
        btnCallCenter.setEnabled(isEnable);
        btnCallHouse.setEnabled(isEnable);
        btnFaceOpen.setEnabled(isEnable);
    }

    public void setBottomBtnState(CustomButtonView focusView) {
        int childCount = mLinBottom.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CustomButtonView childAt = (CustomButtonView) mLinBottom.getChildAt(i);
            if (childAt == focusView) {
                childAt.setClickState(childAt, true);
            } else {
                childAt.setClickState(childAt, false);
            }
        }
    }

    private void showBtnFace() {
        if (AppConfig.getInstance().isFaceEnabled()) {
            //启用
            btnFaceOpen.setVisibility(View.VISIBLE);
        } else {
            //禁用
            btnFaceOpen.setVisibility(View.GONE);
        }
    }

    private void showBtnQrOpen() {
        //是否启用二维码
        int qrOpenDoorType = AppConfig.getInstance().getQrOpenType();
        if (qrOpenDoorType == 0) {
            //扫码开门
            int sweepCodeOpen = AppConfig.getInstance().getQrScanEnabled();
            if (sweepCodeOpen == 0) {
                //禁用
                btnQrOpen.setVisibility(View.GONE);
            } else {
                //启用
                btnQrOpen.setVisibility(View.VISIBLE);
            }
        } else {
            //蓝牙开门器
            String registerId = AppConfig.getInstance().getBluetoothDevId();
            if (!registerId.equals("")) {
                //启用
                btnQrOpen.setVisibility(View.VISIBLE);
            } else {
                //禁用
                btnQrOpen.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pwd_open:
                if (currentFrag == pwdOpenFragment) {
                    return;
                } else {
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1301_PATH);
                    disableBody();
                }
                setBtnState1();
                break;
            case R.id.btn_qr_open:
                if (currentFrag == qrOpenFragment) {
                    return;
                } else {
                    disableBody();
                }
                setBtnState2();
                break;
            case R.id.btn_call_center:
                if (currentFrag == callCenterFragment) {
                    return;
                } else {
                    disableBody();
                }
                setBtnState3(null);
                break;
            case R.id.btn_call_house:
                if (currentFrag == residentFragment) {
                    return;
                } else {
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1201_PATH);
                    disableBody();
                }
                setBtnState4(null);
                break;
            case R.id.btn_face_open:
                if (currentFrag == faceWffrFragment || currentFrag == faceMegviiFragment) {
                    return;
                } else {
                    disableBody();
                }
                setBtnState5();
                break;
        }
    }

    private void setBtnState5() {

        if (currentFrag == faceWffrFragment || currentFrag == faceMegviiFragment) {
            return;
        }

        setBottomBtnState(btnFaceOpen);
        if (AppConfig.getInstance().getFaceManufacturer() == 0) {
            faceWffrFragment = new FaceWffrOpenFragment();
            fragmentList.add(faceWffrFragment);
            currentFrag = faceWffrFragment;
            AppUtils.getInstance().replaceFragment(this, faceWffrFragment, R.id.fl);
        } else if (AppConfig.getInstance().getFaceManufacturer() == 1) {
            faceMegviiFragment = new FaceMegviiOpenFragment();
            fragmentList.add(faceMegviiFragment);
            currentFrag = faceMegviiFragment;
            AppUtils.getInstance().replaceFragment(this, faceMegviiFragment, R.id.fl);
        }
    }

    private void setBtnState51() {

        if (currentFrag == facePromptFragment) {
            return;
        }
        initBottomBtnState();
        facePromptFragment = new FacePromptFragment();
        fragmentList.add(facePromptFragment);
        currentFrag = facePromptFragment;

        AppUtils.getInstance().replaceFragment(this, facePromptFragment, R.id.fl);
    }

    private void setBtnState52(Bundle data) {

        if (currentFrag == faceDelFragment) {
            return;
        }

        initBottomBtnState();
        if (faceDelFragment == null) {
            faceDelFragment = new FaceDelFragment();
            fragmentList.add(faceDelFragment);
        }
        faceDelFragment.setArguments(data);
        currentFrag = faceDelFragment;
        AppUtils.getInstance().replaceFragment(this, faceDelFragment, R.id.fl);
    }

    private void setBtnState4(CallMonitorBean callMonitorBean) {

        if (currentFrag == residentFragment) {
            if (actCallBackListener != null) {
                //如果在呼叫界面，回调监视数据
                actCallBackListener.callBack(callMonitorBean);
            }
            return;
        }
        setBottomBtnState(btnCallHouse);

        residentFragment = new CallResidentFragment();
        fragmentList.add(residentFragment);
        currentFrag = residentFragment;

        if (callMonitorBean == null) {
            AppUtils.getInstance().replaceFragment(MainActivity.this, residentFragment, R.id.fl, "CallResidentFragment",
                    Const.CallAction.KEY_PARAM, null);
        } else {
            //如果是屏保，退出屏保
            if (AppUtils.getInstance().isSetAndScreenProAct()) {
                App.getInstance().getCurrentActivity().finish();
            }
            AppUtils.getInstance().replaceFragment(MainActivity.this, residentFragment, R.id.fl, "CallResidentFragment",
                    Const.CallAction.KEY_PARAM, Const.CallAction.CALL_FROM_RESIDENT);
        }

    }

    private void setBtnState3(CallMonitorBean callMonitorBean) {

        if (currentFrag == callCenterFragment) {
            if (actCallBackListener != null) {
                //如果在呼叫界面，回调监视数据
                actCallBackListener.callBack(callMonitorBean);
            }
            return;
        }
        setBottomBtnState(btnCallCenter);
        callCenterFragment = new CallCenterFragment();
        fragmentList.add(callCenterFragment);
        currentFrag = callCenterFragment;

        if (callMonitorBean == null) {
            AppUtils.getInstance().replaceFragment(MainActivity.this, callCenterFragment, R.id.fl, "CallCenterFragment",
                    Const.CallAction.KEY_PARAM, null);
        } else {
            //如果是屏保，退出屏保
            if (AppUtils.getInstance().isSetAndScreenProAct()) {
                App.getInstance().getCurrentActivity().finish();
            }
            AppUtils.getInstance().replaceFragment(MainActivity.this, callCenterFragment, R.id.fl, "CallCenterFragment",
                    Const.CallAction.KEY_PARAM, Const.CallAction.CALL_FROM_CENTER);
        }

    }

    private void setBtnState2() {
        if (currentFrag == qrOpenFragment) {
            return;
        }
        setBottomBtnState(btnQrOpen);
        if (AppConfig.getInstance().getQrOpenType() == 0) {
            //显示扫描二维码界面
            qrOpenFragment = new QrOpenFragment();
        } else {
            //显示生成二维码界面
            qrOpenFragment = new ShowOpenQrFragment();
        }
        fragmentList.add(qrOpenFragment);
        currentFrag = qrOpenFragment;
        AppUtils.getInstance().replaceFragment(this, qrOpenFragment, R.id.fl);
    }

    private void setBtnState1() {
        if (currentFrag == pwdOpenFragment) {
            return;
        }
        setBottomBtnState(btnPwdOpen);
        pwdOpenFragment = new PwdOpenFragment();
        fragmentList.add(pwdOpenFragment);
        currentFrag = pwdOpenFragment;
        AppUtils.getInstance().replaceFragment(this, pwdOpenFragment, R.id.fl);
    }

    /**
     * 卡状态回报
     */
    @Override
    public void onCardState(int state, String roomNo) {
        //亮屏，显示主界面
        AppUtils.getInstance().toLauncherAct();
        hintDialogFragment = new HintDialogFragment();
//        LogUtils.w(TAG + "=====onCardState state" + state);
        switch (state) {
            //无效卡刷卡
            case 0:
                //设置界面不跳转
                if (AppUtils.getInstance().isToTipAct()) {
                    //播放语音
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1503_PATH);
                    return;
                }

                if (currentFrag instanceof CallResidentFragment && (CallResidentFragment.isCalling || CallResidentFragment.isTalking) ||
                        currentFrag instanceof CallCenterFragment) {
                    //在呼叫界面
                    if (actCallBackListener != null) {
                        actCallBackListener.callBackValue(Constant.IntentId.INTENT_INVALID_CARD, null);
                    }

                } else {
                    //不再呼叫界面
                    AppUtils.getInstance().replaceFragment(this, hintDialogFragment, R.id.fl, "HintDialogFragment",
                            Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_INVALID_CARD);
                }

                break;
            //有效卡刷卡进门
            case 1:
                //设置界面不跳转
                if (AppUtils.getInstance().isToTipAct()) {
                    //播放语音
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
                    return;
                }
                if (currentFrag instanceof CallResidentFragment && (CallResidentFragment.isCalling || CallResidentFragment.isTalking) ||
                        currentFrag instanceof CallCenterFragment) {
                    //在呼叫界面
                    if (actCallBackListener != null) {
                        actCallBackListener.callBackValue(Constant.IntentId.INTENT_OPNE_DOOR, roomNo);
                    }
                } else {
                    //不再呼叫界面
                    OPENDOOR_ROOMNO = roomNo;
                    AppUtils.getInstance().replaceFragment(this, hintDialogFragment, R.id.fl, "HintDialogFragment",
                            Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_OPNE_DOOR_REMIND);
                }
                break;
            default:
                break;
        }
        AppUtils.getInstance().startScreenService();
    }

    /**
     * 门状态报警回报
     * 1:强行开门报警     2: 门未关超时报警  3:防拆破坏报警
     */
    @Override
    public void onDoorAlarm(int doorAlarmType) {
        hintDialogFragment = new HintDialogFragment();
        switch (doorAlarmType) {
            case 1:
                if (AlarmParamDao.getForceOpen() == 1) {
                    if (alarmShowMainAct()) return;
                }
                if (!AppUtils.getInstance().isToTipAct()) {
                    initBottomBtn();
                    //亮屏，显示主界面
                    AppUtils.getInstance().toLauncherAct();
                    AppUtils.getInstance().replaceFragment(this, hintDialogFragment, R.id.fl, "HintDialogFragment",
                            Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_DOOR_NOT_CLOSE);
                }
                break;
            case 2:
                if (EntranceGuardDao.getAlarmOut() == 1) {
                    if (alarmShowMainAct()) return;
                }
                if (!AppUtils.getInstance().isToTipAct()) {
                    initBottomBtn();
                    //亮屏，显示主界面
                    AppUtils.getInstance().toLauncherAct();
                    AppUtils.getInstance().replaceFragment(this, hintDialogFragment, R.id.fl, "HintDialogFragment",
                            Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_DOOR_NOT_CLOSE);
                }
                break;
            case 3:
                if (alarmShowMainAct()) return;
                if (!AppUtils.getInstance().isToTipAct()) {
                    initBottomBtn();
                    //亮屏，显示主界面
                    AppUtils.getInstance().toLauncherAct();
                }
                break;
        }
    }

    private boolean alarmShowMainAct() {
        if ((currentFrag instanceof CallResidentFragment && CallResidentFragment.isTalking) ||
                currentFrag instanceof CallCenterFragment && CallCenterFragment.isTalking) {
            if (actCallBackListener != null) {
                actCallBackListener.callBackValue(Constant.IntentId.ALARM_CLOSE_DOOD, null);
            }
            Constant.IS_ALARM = false;
            return true;
        }
        PlaySoundUtils.playAlarmSound();
        Constant.IS_ALARM = true;
        return false;
    }

    /**
     * 监视对讲
     */
    @Override
    public void onCallMonitor(CallMonitorBean callMonitorBean) {
        //亮屏，显示主界面
        AppUtils.getInstance().toLauncherAct();
        hintDialogFragment = new HintDialogFragment();
        String callFrom = callMonitorBean.getCallFrom();
        boolean openDoor = callMonitorBean.isOpenDoor();
        boolean isCallEnd = callMonitorBean.isCallEnd();
        boolean isCallTalk = callMonitorBean.isCallTalk();

        if ((currentFrag instanceof CallResidentFragment && (CallResidentFragment.isCalling || CallResidentFragment.isTalking)) ||
                currentFrag instanceof CallCenterFragment) {
            if (isCallTalk || isCallEnd) {
                if (currentFrag instanceof CallCenterFragment) {
                    if (callFrom.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                        //跳转到呼叫住户界面
                        setBtnState4(callMonitorBean);
                    } else if (callFrom.equals(Const.CallAction.CALL_FROM_CENTER)) {
                        if (actCallBackListener != null) {
                            //如果在呼叫界面，回调监视数据
                            actCallBackListener.callBack(callMonitorBean);
                        }
                    }
                } else {
                    if (callFrom.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                        if (actCallBackListener != null) {
                            //如果在呼叫界面，回调监视数据
                            actCallBackListener.callBack(callMonitorBean);
                        }
                    } else if (callFrom.equals(Const.CallAction.CALL_FROM_CENTER)) {
                        //跳转到呼叫中心界面
                        setBtnState3(callMonitorBean);
                    }
                }
            } else {
                if (actCallBackListener != null) {
                    //如果在呼叫界面，回调监视数据
                    actCallBackListener.callBack(callMonitorBean);
                }
            }
        } else {
            if (openDoor) {
                if (AppUtils.getInstance().isToTipAct()) {
                    //播放语音门开了
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH);
                } else {
                    //监视直接开门
                    AppUtils.getInstance().replaceFragment(MainActivity.this, hintDialogFragment, R.id.fl, "HintDialogFragment",
                            Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_OPNE_DOOR);
                }
            } else {
                if (!isCallEnd) {
                    if (callFrom.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                        //跳转到呼叫住户界面
                        setBtnState4(callMonitorBean);
                    } else if (callFrom.equals(Const.CallAction.CALL_FROM_CENTER)) {
                        //跳转到呼叫中心界面
                        setBtnState3(callMonitorBean);
                    }

                }
            }
        }

    }

    @Override
    public void onFingerCollect(int code, int press, int count) {

    }

    @Override
    public void onFingerAdd(int code, int fingerId, int valid, byte[] fingerData) {

    }

    /**
     * @param code 1-指纹正确开锁；0-指纹错误；2-保持手指按下；3-请稍候；4-请重按手指
     */
    @Override
    public void onFingerOpen(int code, String roomNo) {
//        LogUtils.w(TAG + "=====onFingerOpen code" + code);
        hintDialogFragment = new HintDialogFragment();
        switch (code) {
            //无效指纹
            case 0:
                if (AppUtils.getInstance().isToTipAct()) {
                    //播放语音
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1506_PATH);
                } else {
                    toFingerHint(Constant.IntentId.INTENT_INVALID_FINGER);
                }
                SinglechipClientProxy.getInstance().setFingerDevNo("");
                break;
            //开门
            case 1:
                if (AppUtils.getInstance().isToTipAct()) {
                    //播放语音
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
                } else {
                    OPENDOOR_ROOMNO = roomNo;
                    toFingerHint(Constant.IntentId.INTENT_OPNE_DOOR_REMIND);
                }
                SinglechipClientProxy.getInstance().setFingerDevNo("");
                break;
            //请保持手指按下
            case 2:
                if (!AppUtils.getInstance().isToTipAct()) {
                    toFingerHint(Constant.IntentId.INTENT_KEEP_PRESS);
                }
                break;
            //正在对比指纹，请等候！
            case 3:
                if (!AppUtils.getInstance().isToTipAct()) {
                    toFingerHint(Constant.IntentId.INTENT_VERIFYING_FINGER);
                }
                break;
            //请重按手指
            case 4:
                if (!AppUtils.getInstance().isToTipAct()) {
                    toFingerHint(Constant.IntentId.INTENT_FINGER_PRESS_AGAIN);
                }
                break;
        }
        AppUtils.getInstance().startScreenService();
    }

    private void toFingerHint(String value) {
        if (currentFrag instanceof HintDialogFragment) {
            AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_HINT_DIALOG, Constant.IntentId.INTENT_KEY, value);
        } else {
            AppUtils.getInstance().toLauncherAct();
            AppUtils.getInstance().replaceFragment(MainActivity.this, hintDialogFragment, R.id.fl, "HintDialogFragment",
                    Constant.IntentId.INTENT_KEY, value);
            currentFrag = hintDialogFragment;
        }
    }


    /**
     * 人体感应跳转到对应界面
     */
    private void bodyInductionToAct() {
        final int bodyFeeling = AppConfig.getInstance().getBodyInduction();
//        LogUtils.w(TAG + "====== 人体感应上报 ====== bodyFeeling: " + bodyFeeling);
        switch (bodyFeeling) {
            //0 触发开屏
            case 0:
                if (AppUtils.getInstance().isScreenProAct()) {
                    //计时关屏时间
                    String screenServiceName = "com.mili.smarthome.tkj.service.ScreenService";
                    if (!AppUtils.getInstance().isServiceRunning(screenServiceName)) {
                        AppUtils.getInstance().refreshScreenService();
                    }
                }
                break;
            //人脸识别
            case 1:
                //亮屏，显示主界面
                AppUtils.getInstance().toLauncherAct();
                if (btnFaceOpen.getVisibility() == View.VISIBLE) {
                    setBtnState5();
                }
                break;
            //扫码开门
            case 2:
                //亮屏，显示主界面
                AppUtils.getInstance().toLauncherAct();
                if (btnQrOpen.getVisibility() == View.VISIBLE) {
                    setBtnState2();
                }
                break;
            //蓝牙开门器
            case 3:
                //亮屏，显示主界面
                AppUtils.getInstance().toLauncherAct();
                if (btnQrOpen.getVisibility() == View.VISIBLE) {
                    setBtnState2();
                }
                break;
        }
    }

    /**
     * 人体感应
     */
    @Override
    public void onBodyInduction() {
        LogUtils.w(TAG + " onBodyInduction ");
        if (App.getInstance().getCurrentActivity() instanceof MainActivity &&
                (currentFrag instanceof MainFragment || currentFrag instanceof MessageDialogFragment
                        || AppUtils.getInstance().isScreenProAct())) {
            bodyInductionToAct();
        }
    }

    class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case Constant.Action.MAIN_BOTTOM_BTN:
                    //底部按钮变化
                    initBottomTabState();
                    setBottomBtnEnable(false);
                    break;
                case Constant.Action.MAIN_REFRESH_ACTION:
                    //显示主界面
                    initBottomBtn();
                    break;
                case Constant.Action.BODY_FACE_RECOGNITION_ACTION:
                    //关闭屏保
                    Intent intent1 = new Intent(Constant.Action.CLOSE_SCREEN_PROTECT);
                    sendBroadcast(intent1);
                    //显示人脸识别界面
                    setBtnState5();
                    break;
                case Constant.Action.BODY_FACE_ENROLL_ACTION:
                    //显示人脸注册成功提示界面
                    setBtnState51();
                    setBottomBtnEnable(false);
                    break;
                case Constant.Action.BODY_FACE_DEL_ACTION:
                    //显示人脸删除界面
                    setBtnState52(intent.getExtras());
                    setBottomBtnEnable(false);
                    break;
                case Constant.Action.ACTION_TO_SCREEN_PRO:
                    currentFrag = new ScreenProFragment();
                    AppUtils.getInstance().replaceFragment(MainActivity.this, currentFrag, R.id.fl, "ScreenProFragment");
                    initBottomBtnState();
                    mLinBottom.setVisibility(View.GONE);
                    break;
                case Constant.Action.ACTION_SHOW_BOTTOM_BTN:
                    mLinBottom.setVisibility(View.VISIBLE);
                    break;
                case Constant.Action.ACTION_HIDE_BOTTOM_BTN:
                    mLinBottom.setVisibility(View.GONE);
                    break;
                case Const.ActionId.KEY_DOWN_UPDATETOUCH:
                    //回到主界面
                    initBottomBtn();
                    break;
                case Const.ActionId.SCREEN_SAVER_EXIT:
                    //回到主界面
                    initBottomBtn();
                    break;
            }
        }
    }

    /**
     * 禁用人脸10秒
     */
    private void disableBody() {
        if (currentFrag instanceof QrOpenFragment) {
            SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(2);
        } else if (currentFrag instanceof ShowOpenQrFragment) {
            SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(3);
        } else if (currentFrag instanceof FaceWffrOpenFragment || currentFrag instanceof FaceMegviiOpenFragment) {
            SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(1);
        }
    }

}

