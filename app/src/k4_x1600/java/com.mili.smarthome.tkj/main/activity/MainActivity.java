package com.mili.smarthome.tkj.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.android.CommStorePathDef;
import com.android.client.SetDriverSinglechipClient;
import com.android.interf.ICardStateListener;
import com.android.interf.IDoorAlarmListener;
import com.hobot.hrxtrans.HRXTrans;
import com.hobot.models.ModelFiles;
import com.android.provider.RoomSubDest;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.base.FragmentProxy;
import com.mili.smarthome.tkj.base.K4BaseActivity;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.base.K4Config;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.base.KeyboardProxy;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.call.ICallMonitorListener;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.face.FuncCodeListener;
import com.mili.smarthome.tkj.face.HorizonScanFragment;
import com.mili.smarthome.tkj.face.horizon.HorizonFacePresenter;
import com.mili.smarthome.tkj.face.horizon.util.CameraDisplayUtil;
import com.mili.smarthome.tkj.face.horizon.util.GpioUtil;
import com.mili.smarthome.tkj.face.horizon.util.HorizonPreferences;
import com.mili.smarthome.tkj.face.horizon.util.LiveNessUtil;
import com.mili.smarthome.tkj.face.horizon.util.SunriseSdkUtil;
import com.mili.smarthome.tkj.face.horizon.util.XWareHouseUtil;
import com.mili.smarthome.tkj.main.fragment.CenterFragment;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.fragment.PasswordFragment;
import com.mili.smarthome.tkj.main.fragment.QrcodeFragment;
import com.mili.smarthome.tkj.main.fragment.ResidentFragment;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.activity.SettingActivity;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

public class MainActivity extends K4BaseActivity implements ICardStateListener, IDoorAlarmListener, ICallMonitorListener, View.OnClickListener, FuncCodeListener {

    public static final String ARGS_FUNC = "args_func_code";
    public static final int FUNCTION_MAIN = 0;
    public static final int FUNCTION_PASSWORD = 1;
    public static final int FUNCTION_QRCODE = 2;
    public static final int FUNCTION_CENTER = 3;
    public static final int FUNCTION_RESIDENT = 4;
    public static final int FUNCTION_FACE = 5;

    private static final String Tag = "MainActivity";

    private MainFragment mFmMain = null;
    private PasswordFragment mFmPassword = null;
    private QrcodeFragment mFmQrcode = null;
    private CenterFragment mFmCenter = null;
    private ResidentFragment mFmResident = null;
    private HorizonScanFragment mFmHorizonFace = null;
    private K4BaseFragment mFmCurrent = null;

    private RadioButton mRbPassword, mRbQrcode, mRbCenter;
    private RadioButton mRbResident, mRbFace;
    private KeyboardCtrl mKeybordUtil = null;
//    private HintView mHvHint;

    private String mDefaultText = null;
    private Bundle mMonitorBundle = null;
    private int mFaceResultCode = -1;
    private int mFuncCode;

    private FrameLayout mFrameLayout;
    private LinearLayout mMainLayout;
    private LinearLayout mFuncLayout;
    private boolean mIsFullScreen = false;

    private boolean mDoorAlarming = false;// 门报警

    @Override
    public boolean onKeyConfirm() {
        if (mFmCurrent instanceof MainFragment) {
            showFunction(FUNCTION_PASSWORD, true);
        }
        return true;
    }

    @Override
    public boolean onKeyCancel() {
        return true;
    }

    @Override
    public boolean onKey(int code) {
        // 在主界面时输入数字跳转到呼叫住户界面
        if (mFmCurrent instanceof MainFragment) {
            mDefaultText = String.valueOf(code);
            showFunction(FUNCTION_RESIDENT, true);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (AuthManage.isAuth()) {
            initHorizon();
        }

        setContentView(R.layout.activity_main);
        initView();
        parseIntent(getIntent());
        LogUtils.d(Tag + " ====== onCreate ======");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {
        mFuncCode = intent.getIntExtra(ARGS_FUNC, -1);
    }

    private void initHorizon() {
        HorizonScanFragment.appFirstStart = true;
        GpioUtil.init();
        CameraDisplayUtil.setCameraDisplayRatio(HRXTrans.SysGetProp("persist.sys.csi.lines"));
        SunriseSdkUtil.init();
        SunriseSdkUtil.setJniLogLevel(HorizonPreferences.getLogLevel());
        String horizonPath = HorizonPreferences.getHorizonPath();
        if (!ModelFiles.IsOnDir(getApplicationContext(), horizonPath)) {
            ModelFiles.CopyToDir(getApplicationContext(), horizonPath);
        }
        XWareHouseUtil.init(horizonPath, horizonPath + "/model_conf.json");
        XWareHouseUtil.setJniLogLevel(1);
        SunriseSdkUtil.setJniFaceAE(HorizonPreferences.getFaceAeEnable());
        SunriseSdkUtil.config();
        SunriseSdkUtil.setCb();
        FacePresenterProxy.init();
        FacePresenterProxy.setFacePresenter(new HorizonFacePresenter());
    }

    @Override
    protected void onResume() {
        initListener();
        super.onResume();
        if (SystemSetUtils.isScreenOn()) {
            FreeObservable.getInstance().observeFree();
        }
        initCallClient();
        initFunction();
        showFaceRadio();
        showQcodeRadio();
        LogUtils.d(Tag + " ====== onResume ======");
        SunriseSdkUtil.start();

        RoomSubDest roomSubDest = new RoomSubDest(this);
        String devDesc = roomSubDest.getSubDestDevNumber();
        K4Config.getInstance().setDeviceDesc(devDesc);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SunriseSdkUtil.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FreeObservable.getInstance().cancelObserveFree();
        SunriseSdkUtil.deinit();
        LiveNessUtil.deinit();
        XWareHouseUtil.deinit();
        GpioUtil.close();
    }

    /** 初始化监听器 */
    private void initListener() {
        SinglechipClientProxy.getInstance().setDoorAlarmListener(this);
        KeyboardProxy.getInstance().setKeyboard(mKeybordUtil);
        FragmentProxy.getInstance().setFragmentManager(getSupportFragmentManager());
        FragmentProxy.getInstance().setFragmentListener(mFragmentListener);
    }

    /**
     *  初始显示相应功能
     */
    private void initFunction() {
        //屏保界面人体感应时返回
        if (mFuncCode > 0) {
            LogUtils.d(Tag + " initFunction: funcCode is " + mFuncCode);
            showFunction(mFuncCode, true);
            mFuncCode = -1;
            return;
        }

        //人脸识别全屏界面返回
        if (mFaceResultCode > 0) {
            LogUtils.d(Tag + " initFunction: mFaceResultCode is " + mFaceResultCode);
            showFunction(mFaceResultCode, true);
            mFaceResultCode = -1;
            return;
        }

        // 防止关屏时人体感应显示人脸界面后又回到主界面问题
        if (mFmCurrent instanceof HorizonScanFragment) {
            LogUtils.d(Tag + " current is HorizonScanFragment, donot show mainFragment.");
            return;
        }

        // 解决屏保界面时监视通话会回到主界面问题
        if (K4Config.getInstance().getMonitorTalk() &&
                (mFmCurrent instanceof ResidentFragment || mFmCurrent instanceof CenterFragment)) {
            LogUtils.d(Tag + " in monitor talk, can not show mainFragment.");
            return;
        }

        //默认时显示主界面
        showFunction(FUNCTION_MAIN, true);
    }

    private void initView() {
        mRbPassword = findView(R.id.main_password);
        mRbQrcode = findView(R.id.main_qrcode);
        mRbCenter = findView(R.id.main_center);
        mRbResident = findView(R.id.main_resident);
        mRbFace = findView(R.id.main_face);
        mRbPassword.setOnClickListener(this);
        mRbQrcode.setOnClickListener(this);
        mRbCenter.setOnClickListener(this);
        mRbResident.setOnClickListener(this);
        mRbFace.setOnClickListener(this);

        mKeybordUtil = findView(R.id.keyboardutil);

//        mHvHint = findView(R.id.hv_hint);
//        mHvHint.setVisibility(View.INVISIBLE);

        setRbDrawbleSize(mRbPassword,R.drawable.main_lock);
        setRbDrawbleSize(mRbQrcode,R.drawable.main_scanqr);
        setRbDrawbleSize(mRbCenter,R.drawable.main_center);
        setRbDrawbleSize(mRbResident,R.drawable.main_resident);
        setRbDrawbleSize(mRbFace,R.drawable.main_face);

        mFrameLayout = findView(R.id.fragment_container);
        mMainLayout = findView(R.id.main_content);
        mFuncLayout = findView(R.id.radiogrop);
    }

    /**
     * 判断是否显示人脸识别界面
     */
    private void showFaceRadio() {
        if (mFuncLayout != null) {
            if (!AppConfig.getInstance().isFaceEnabled()) {
                mFuncLayout.removeView(mRbFace);
            } else {
                if (mFuncLayout.indexOfChild(mRbFace) < 0) {
                    mFuncLayout.addView(mRbFace);
                }
            }
        }

        // 防止人体感应为人脸识别但是人脸又未启用情况
        if (AppConfig.getInstance().getBodyInduction() == 1 && !AppConfig.getInstance().isFaceEnabled()) {
            AppConfig.getInstance().setBodyInduction(0);
        }
    }

    /**
     * 判断是否显示扫码开门界面
     */
    private void showQcodeRadio() {
        boolean show = false;
        int openType = AppConfig.getInstance().getQrOpenType();
        if (openType == 0) {
            if (AppConfig.getInstance().getQrScanEnabled() == 1) {
                show = true;
            }
        } else {
            String registerId = AppConfig.getInstance().getBluetoothDevId();
            if (registerId != null && registerId.length() > 0) {
                show = true;
            }
        }

        if (mFuncLayout != null) {
            if (show) {
                if (mFuncLayout.indexOfChild(mRbQrcode) < 0) {
                    mFuncLayout.addView(mRbQrcode, 1);
                }
            } else {
                mFuncLayout.removeView(mRbQrcode);
            }
        }
    }

    //监视接口
    private void initCallClient() {
        CallManage.getInstance().initCallClient(this);
        CallManage.getInstance().setCallMonitorListener(this);
    }

    private void fragmentReplace(K4BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commitAllowingStateLoss();
        mFmCurrent = fragment;
    }

    public void setFragmentSize(boolean fullscreen) {
        LogUtils.d(Tag + " setFragmentSize: fullscreen is " + fullscreen);
        if (fullscreen) {
            mMainLayout.setVisibility(View.INVISIBLE);
            Point outSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(outSize);
            ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
            params.width = outSize.x;
            params.height = outSize.y;
            mFrameLayout.setLayoutParams(params);
        } else {
            mMainLayout.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
            params.width = getResources().getDimensionPixelOffset(R.dimen.dp_288);
            params.height = getResources().getDimensionPixelOffset(R.dimen.dp_280);
            mFrameLayout.setLayoutParams(params);
        }
        mIsFullScreen = fullscreen;
    }

    private void showMain() {
        if (!(mFmCurrent instanceof MainFragment)) {
            showFunction(FUNCTION_MAIN, true);
        }
        setMainEnable(true);
    }

    private void showFunction(int funcId, boolean checkRadio) {
        LogUtils.d(Tag + " showFunction: funcId is " + funcId + ", check is " + checkRadio);
        if (checkRadio) {
            radioButtonCheck(funcId);
        }
        // 人脸界面全屏未恢复时
        if (mIsFullScreen && funcId != FUNCTION_FACE) {
            LogUtils.e(Tag + " showFunction: reset the size of the fragment.");
            setFragmentSize(false);
        }
        switch (funcId) {
            case FUNCTION_MAIN:
                if (mFmMain == null) {
                    mFmMain = new MainFragment();
                }
                fragmentReplace(mFmMain);
                setFragmentSize(false);
                break;

            case FUNCTION_PASSWORD:
                if (mFmPassword == null) {
                    mFmPassword = new PasswordFragment();
                }
                fragmentReplace(mFmPassword);
                break;

            case FUNCTION_QRCODE:
                if (mFmQrcode == null) {
                    mFmQrcode = new QrcodeFragment();
                }
                fragmentReplace(mFmQrcode);
                break;

            case FUNCTION_CENTER:
                if (mFmCenter == null) {
                    mFmCenter = new CenterFragment();
                }
                mFmCenter.setArguments(null);
                if (mMonitorBundle != null) {
                    mFmCenter.setArguments(mMonitorBundle);
                    mMonitorBundle = null;
                }
                fragmentReplace(mFmCenter);
                break;

            case FUNCTION_RESIDENT:
                if (mFmResident == null) {
                    mFmResident = new ResidentFragment();
                }
                mFmResident.setArguments(null);
                if (mDefaultText != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ResidentFragment.KEY_TEXT, mDefaultText);
                    mFmResident.setArguments(bundle);
                    mDefaultText = null;
                }
                if (mMonitorBundle != null) {
                    mFmResident.setArguments(mMonitorBundle);
                    mMonitorBundle = null;
                }
                fragmentReplace(mFmResident);
                break;

            case FUNCTION_FACE:
                if (mFmHorizonFace == null) {
                    mFmHorizonFace = new HorizonScanFragment();
                }
                mFmHorizonFace.setFuncCodeListener(this);
                fragmentReplace(mFmHorizonFace);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 0) {
            mFaceResultCode = data.getIntExtra("funcIndex", -1);
            LogUtils.d(Tag + " onActivityResult: funcIndex is " + mFaceResultCode);
        }
    }

    /** 使能功能键和键盘控件 */
    public void setMainEnable(boolean enable) {
        setRadioButtonEnable(enable);
        mKeybordUtil.setClickable(enable);
    }

    /** 使能功能键 */
    public void setRadioButtonEnable(boolean enable) {
        mRbPassword.setEnabled(enable);
        mRbQrcode.setEnabled(enable);
        mRbCenter.setEnabled(enable);
        mRbResident.setEnabled(enable);
        mRbFace.setEnabled(enable);
    }

    private void radioButtonCheck(int funcId) {
        mRbPassword.setChecked(false);
        mRbQrcode.setChecked(false);
        mRbCenter.setChecked(false);
        mRbResident.setChecked(false);
        mRbFace.setChecked(false);

        switch (funcId) {
            case FUNCTION_PASSWORD:
                mRbPassword.setChecked(true);
                break;

            case FUNCTION_QRCODE:
                mRbQrcode.setChecked(true);
                break;

            case FUNCTION_CENTER:
                mRbCenter.setChecked(true);
                break;

            case FUNCTION_RESIDENT:
                mRbResident.setChecked(true);
                break;

            case FUNCTION_FACE:
                mRbFace.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
//        stopAlarmPlay();
        LogUtils.d(Tag + " onClick: id is " + view.getId());
        int funcId = 0;
        switch (view.getId()) {
            case R.id.main_password:
                funcId = FUNCTION_PASSWORD;
                break;
            case R.id.main_qrcode:
                funcId = FUNCTION_QRCODE;
                break;
            case R.id.main_center:
                funcId = FUNCTION_CENTER;
                break;
            case R.id.main_resident:
                funcId = FUNCTION_RESIDENT;
                break;
            case R.id.main_face:
                funcId = FUNCTION_FACE;
                break;
        }
        if (funcId > 0) {
            delayBodyInstruction(funcId);
            showFunction(funcId, true);
        }
    }

    private FragmentProxy.FragmentListener mFragmentListener = new FragmentProxy.FragmentListener() {
        @Override
        public void onExitFragment() {
            showMain();
        }

        @Override
        public void setClickable(boolean clickable) {
            setMainEnable(clickable);
        }
    };

    @Override
    public void onBackPressed() {
        showMain();
    }

    /**
     * 主界面刷卡、指纹、报警时的文字提示信息
     * @param textId    信息ID
     * @param colorId   信息颜色ID
     */
    private void showHint(int textId, int colorId) {
//        mHvHint.setHint(textId, colorId);
//        mHvHint.setVisibility(View.VISIBLE);
        if (mFmCurrent instanceof MainFragment) {
            mFmMain.showHint(this, textId, colorId);
        }
        //刷卡提示时不能控制键盘和功能键
        setMainEnable(false);
    }

    /**
     * 退出刷卡、指纹、报警时提示界面
     */
    private void exitHint() {
        if ( !K4Config.getInstance().getCallState() && !K4Config.getInstance().getMonitorTalk()) {
            showMain();
        }
        if (mFmMain != null) {
            mFmMain.hideHint();
        }
//        mHvHint.setVisibility(View.INVISIBLE);
        setMainEnable(true);
    }

    /**
     * 退出提示界面
     */
    private void exitHintEx() {
        mMainHandler.removeCallbacks(HintEndRunnable);
        if (mFmMain != null) {
            mFmMain.hideHint();
        }
//        mHvHint.setVisibility(View.INVISIBLE);
        setMainEnable(true);
    }

    /**
     * 刷卡、指纹提示结束时执行的动作
     */
    private Runnable HintEndRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.d(Tag + " ====  HintEndRunnable  ==== ");
            mDoorAlarming = false;
            exitHint();
        }
    };

    @Override
    public void onCardState(int state, String roomNo) {
        LogUtils.d(Tag + " ======= onCardState: state=" + state);
        if (mFmCurrent instanceof CenterFragment || mFmCurrent instanceof ResidentFragment) {
            if (K4Config.getInstance().getCallState() || K4Config.getInstance().getMonitorTalk()) {
                switch (state) {
                    case 0:
                        mFmCurrent.actionCallback(Const.CardAction.INVALID_CARD, roomNo);
                        break;
                    case 1:
                        mFmCurrent.actionCallback(Const.CardAction.OPNE_DOOR, roomNo);
                        break;
                    default:
                        break;
                }
                return;
            }
        }

        // 在人脸界面只播放提示音不做文字提示
        if (mFmCurrent instanceof HorizonScanFragment) {
            LogUtils.d(Tag + " HorizonScanFragment, play sound only.");
            if (state == 0) {
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1503_PATH, null);
            } else {
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
            }
            return;
        }

        // 不在呼叫通话界面则退回主界面
        if (!(mFmCurrent instanceof MainFragment)) {
            showFunction(FUNCTION_MAIN, true);
        }

        mMainHandler.removeCallbacks(HintEndRunnable);
        if (state == 0) {
            showHint(R.string.call_invalid_card, R.color.txt_red);
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1503_PATH, null);
        } else {
            showHint(R.string.call_open_door, R.color.txt_green);
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
        }
        mMainHandler.postDelayed(HintEndRunnable, Constant.MAIN_HINT_TIMEOUT);
    }

    @Override
    public void onFingerOpen(int code, String roomNo) {
        super.onFingerOpen(code, roomNo);
        LogUtils.d(Tag + " ========= onFingerOpen: code is " + code);

        // 在人脸界面只播放提示音不做文字提示
/*        if (mFmCurrent instanceof HorizonScanFragment) {
            LogUtils.d(Tag + " HorizonScanFragment, play sound only.");
            switch (code) {
                case 0:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1506_PATH, null);
                    break;
                case 1:
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, null);
                    break;
            }
            return;
        }*/

        // 不在呼叫通话界面则退回主界面
        if (!(mFmCurrent instanceof MainFragment)) {
            showFunction(FUNCTION_MAIN, true);
        }

        mMainHandler.removeCallbacks(HintEndRunnable);
        switch (code) {
            case 0:
                showHint(R.string.finger_invalid, R.color.txt_red);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1506_PATH, null);
                SinglechipClientProxy.getInstance().setFingerDevNo("");
                break;
            case 1:
                showHint(R.string.call_open_door, R.color.txt_green);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
                SinglechipClientProxy.getInstance().setFingerDevNo("");
                break;
            case 2:
                showHint(R.string.finger_keep_press, R.color.txt_green);
                break;
            case 3:
                showHint(R.string.finger_verifying, R.color.txt_green);
                break;
            case 4:
                showHint(R.string.finger_press_again, R.color.txt_red);
                break;
        }
        mMainHandler.postDelayed(HintEndRunnable, Constant.MAIN_HINT_TIMEOUT);
    }

    @Override
    public void onDoorAlarm(int doorAlarmType) {
        LogUtils.d(Tag + " ========= onDoorAlarm: doorAlarmType=" + doorAlarmType);
        FreeObservable.getInstance().resetFreeTime();

        //开屏，开背光灯
        if (!SystemSetUtils.isScreenOn()) {
            SystemSetUtils.screenOn();
        }
        if ( !SetDriverSinglechipClient.getInstance().getSystemSleep()) {
            SetDriverSinglechipClient.getInstance().setSystemSleep(1);
        }

        //退出屏保
        Activity topActivity = App.getInstance().getCurrentActivity();
        if (topActivity instanceof ScreenSaverActivity) {
            topActivity.finish();
        }

        mDoorAlarming = true;

        // 判断是否响报警声
        if (doorAlarmType != 2 || EntranceGuardDao.getAlarmOut() == 1) {
            PlaySoundUtils.playAlarmSound();
        }

        // 防拆报警不提示文字
        if (doorAlarmType != 1 && doorAlarmType != 2) {
            LogUtils.d(Tag + "destroy alarm, do not show hint.");
            mDoorAlarming = false;
            return;
        }

        // 呼叫或通话时提示
        if (K4Config.getInstance().getCallState() || K4Config.getInstance().getMonitorTalk()) {
            mFmCurrent.actionCallback(Constant.HintAction.ALARM_HINT, null);
            mDoorAlarming = false;
            return;
        }

        // 在人脸界面不做文字提示
        if (mFmCurrent instanceof HorizonScanFragment) {
            LogUtils.d(Tag + " HorizonScanFragment, play sound only.");
            mDoorAlarming = false;
            return;
        }

        // 不在呼叫通话界面则退回主界面
        if (!(mFmCurrent instanceof MainFragment)) {
            showFunction(FUNCTION_MAIN, true);
        }

        //文字提示：请关好门
        showHint(R.string.comm_text_d0, R.color.txt_red);
        mMainHandler.removeCallbacks(HintEndRunnable);
        mMainHandler.postDelayed(HintEndRunnable, Constant.MAIN_HINT_TIMEOUT);
    }

//    /**
//     * 停止响报警声，提示时不允许操作界面，故正常情况不会调用
//     */
//    private void stopAlarmPlay() {
//        if (mDoorAlarming) {
//            mDoorAlarming = false;
//            mMainHandler.removeCallbacks(HintEndRunnable);
//            exitHint();
//            PlaySoundUtils.stopPlayAssetsSound();
//        }
//    }

    @Override
    public void onBodyInduction() {
        super.onBodyInduction();
        int type = AppConfig.getInstance().getBodyInduction();
        LogUtils.d(Tag + " ===== onBodyInduction: type = " + type);
        if (mDoorAlarming) {
            LogUtils.d(Tag + " alarming, do not deal bodyinduction ");
            return;
        }
        if (mFmCurrent instanceof MainFragment) {
            switch (type) {
                case 1:
                    exitHintEx();
                    showFunction(FUNCTION_FACE, true);
//                    mHvHint.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                case 3:
                    exitHintEx();
                    showFunction(FUNCTION_QRCODE, true);
//                    mHvHint.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    /**
     * 监视通话时显示对应界面
     * @param callFrom  监视对象
     */
    private void showMonitorTalk(String callFrom) {
        switch (callFrom) {
            case Const.CallAction.CALL_FROM_RESIDENT:
                K4Config.getInstance().setMonitorTalk(true);
                mMonitorBundle = new Bundle();
                mMonitorBundle.putString(Const.CallAction.KEY_PARAM, Const.CallAction.CALL_FROM_RESIDENT);
                showFunction(FUNCTION_RESIDENT, true);
                break;

            case Const.CallAction.CALL_FROM_CENTER:
                K4Config.getInstance().setMonitorTalk(true);
                mMonitorBundle = new Bundle();
                mMonitorBundle.putString(Const.CallAction.KEY_PARAM, Const.CallAction.CALL_FROM_CENTER);
                showFunction(FUNCTION_CENTER, true);
                break;
        }
    }

    /**
     * 处理监视逻辑
     * @param callMonitorBean   监视参数
     */
    private void dealCallMonitor(CallMonitorBean callMonitorBean) {
        String callFrom = callMonitorBean.getCallFrom();
        boolean openDoor = callMonitorBean.isOpenDoor();
        LogUtils.d(Tag + " onCallMonitor: callFrom=" + callFrom + ", opendoor=" + openDoor);
        switch (callFrom) {
            //住户监视、对讲开锁
            case Const.CallAction.CALL_FROM_RESIDENT:
                if (mFmCurrent instanceof ResidentFragment) {
                    LogUtils.d(Tag + " in residentfragment.");
                    //如果在呼叫界面，回调监视数据
                    if (openDoor && !K4Config.getInstance().getMonitorTalk()
                            && !K4Config.getInstance().getCallState()) {
                        onCardState(1, null);
                    } else {
                        mFmCurrent.onMonitor(callMonitorBean);
                    }
                } else {
                    LogUtils.d(Tag + " not in residentfragment.");
                    //监视呼叫开锁提示
                    if (openDoor) {
                        onCardState(1, null);
                    } else {
                        //只有开始监视通话时才打开呼叫住户界面
                        if (!callMonitorBean.isCallTalk()) {
                            LogUtils.d(Tag + " onCallMonitor, not start talk, cannot show ResidentFragment.");
                            break;
                        }
                        if (mFmCurrent instanceof CenterFragment) {
                            showMain();
                            mMainHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showMonitorTalk(Const.CallAction.CALL_FROM_RESIDENT);
                                }
                            }, 100);
                        } else {
                            showMonitorTalk(Const.CallAction.CALL_FROM_RESIDENT);
                        }
                    }
                }
                break;
            //中心监视、对讲开锁
            case Const.CallAction.CALL_FROM_CENTER:
                if (mFmCurrent instanceof CenterFragment) {
                    if (openDoor && !K4Config.getInstance().getMonitorTalk()
                            && !K4Config.getInstance().getCallState()) {
                        onCardState(1, null);
                    } else {
                        mFmCurrent.onMonitor(callMonitorBean);
                    }
                } else {
                    //监视呼叫开锁提示
                    if (openDoor) {
                        onCardState(1, null);
                    } else {
                        //只有开始监视通话时才打开呼叫中心界面
                        if (!callMonitorBean.isCallTalk()) {
                            LogUtils.d(Tag + " onCallMonitor, not start talk, cannot show CenterFragment.");
                            break;
                        }
                        if (mFmCurrent instanceof ResidentFragment) {
                            showMain();
                            mMainHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showMonitorTalk(Const.CallAction.CALL_FROM_CENTER);
                                }
                            }, 100);
                        } else {
                            showMonitorTalk(Const.CallAction.CALL_FROM_CENTER);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onCallMonitor(final CallMonitorBean callMonitorBean) {
        boolean delay = false;
        //开屏，开背光灯
        if (!SystemSetUtils.isScreenOn()) {
            SystemSetUtils.screenOn();
        }
        if ( !SetDriverSinglechipClient.getInstance().getSystemSleep()) {
            SetDriverSinglechipClient.getInstance().setSystemSleep(1);
        }
        //退出屏保
        Activity topActivity = App.getInstance().getCurrentActivity();
        if (topActivity instanceof ScreenSaverActivity) {
            topActivity.finish();
            delay = true;
        }

        //退出设置
        if (topActivity instanceof SettingActivity && callMonitorBean.isCallTalk()) {
            topActivity.finish();
            delay = true;
        }

        // 退出设置和屏保界面时，等待mainActiviy显示后再显示被监视界面，否则导致监视通话时无法主动挂断
        if (delay) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dealCallMonitor(callMonitorBean);
                }
            }, 100);
        } else {
            dealCallMonitor(callMonitorBean);
        }
    }

    @Override
    public boolean onFreeReport(final long freeTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //LogUtils.d(Tag + " freeTime is " + freeTime);
                if (K4Config.getInstance().getCallState()
                        || K4Config.getInstance().getMonitorTalk()) {
                    FreeObservable.getInstance().resetFreeTime();
                    return;
                }
                if (freeTime > Constant.SCREEN_BACKMAIN_TIME) {
                    // 15秒无操作，返回默认界面
                    if (!(mFmCurrent instanceof MainFragment)) {
                        LogUtils.d(" ========= onFreeReport: showMain ========= ");
                        showMain();
                        SinglechipClientProxy.getInstance().setFingerDevNo("");
                    }
                }
            }
        });
        return true;
    }


    private void setRbDrawbleSize(RadioButton radioButton,int drawbleId) {
        Drawable drawable = getResources().getDrawable(drawbleId, null);
        drawable.setBounds(0,0,getResources().getDimensionPixelSize(R.dimen.dp_30),getResources().getDimensionPixelSize(R.dimen.dp_30));
        radioButton.setCompoundDrawables(null,drawable,null,null);
    }

    @Override
    public void onFuncCode(int funcCode, int param) {
        LogUtils.d(Tag + " face onFuncCode: funcCode is " + funcCode);
        switch (funcCode) {
            case FUNCTION_PASSWORD:
            case FUNCTION_QRCODE:
            case FUNCTION_CENTER:
            case FUNCTION_RESIDENT:
                delayBodyInstruction(funcCode);
                showFunction(funcCode, true);
                setFragmentSize(false);
                break;

            case FUNCTION_FACE:
                if (param == 1) {
                    setFragmentSize(true);
                } else {
                    setFragmentSize(false);
                }
                break;

            case FUNCTION_MAIN:
                setFragmentSize(false);
                break;
        }
    }

    /**
     * 人体感应触发人脸或二维码时，手动退出需间隔10秒才能触发下一次人体感应
     * @param funcId    切换后的功能ID
     */
    private void delayBodyInstruction(int funcId){
        if (funcId != FUNCTION_FACE && mFmCurrent instanceof HorizonScanFragment){
            SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(1);
        } else {
            if (funcId != FUNCTION_QRCODE && mFmCurrent instanceof QrcodeFragment) {
                if (AppConfig.getInstance().getQrOpenType() == 0) {
                    SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(2);
                } else {
                    SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(3);
                }
            }
        }
    }
}
