package com.mili.smarthome.tkj.main.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.client.MainClient;
import com.android.interf.ICardStateListener;
import com.android.interf.IDoorAlarmListener;
import com.android.interf.IFingerEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.CustomVersion;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.base.K3BaseActivity;
import com.mili.smarthome.tkj.base.K3Const;
import com.mili.smarthome.tkj.call.CallManage;
import com.mili.smarthome.tkj.call.CallMonitorBean;
import com.mili.smarthome.tkj.call.IActCallBackListener;
import com.mili.smarthome.tkj.call.ICallMonitorListener;
import com.mili.smarthome.tkj.call.fragment.CallCenterFragment;
import com.mili.smarthome.tkj.call.fragment.CallResidentFragment;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.face.BaseFaceFragment;
import com.mili.smarthome.tkj.face.FacePromptFragment;
import com.mili.smarthome.tkj.face.MegviiScanFragment;
import com.mili.smarthome.tkj.face.WffrScanFragment;
import com.mili.smarthome.tkj.main.fragment.AdminFragment;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.fragment.PasswordFragment;
import com.mili.smarthome.tkj.main.fragment.QrCodeDecoderFragment;
import com.mili.smarthome.tkj.main.fragment.QrCodeEncoderFragment;
import com.mili.smarthome.tkj.main.view.CheckableLayout;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.activity.SettingActivity;
import com.mili.smarthome.tkj.utils.FragmentUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;
import com.mili.smarthome.tkj.widget.UniformLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends K3BaseActivity implements ICardStateListener, IFingerEventListener, ICallMonitorListener, IDoorAlarmListener {

    public static final String ARGS_FUNC = "args_func_code";
    private static final String TAG = "lfl-MainActivity";

    public static final int FUNC_PASSWORD = 0;     // 密码开门
    public static final int FUNC_QRCODE = 1;       // 扫码开门
    public static final int FUNC_CENTER = 2;       // 呼叫中心
    public static final int FUNC_RESIDENT = 3;     // 呼叫住户
    public static final int FUNC_FACE = 4;         // 人脸开门
    public static final int FUNC_DEFAULT = -1;     // 默认界面
    public static final int FUNC_ADMIN = -2;       // 管理员密码界面
    public static final int FUNC_FACE_PROMPT = -4;// 人脸提示界面
    //public static final int FUNC_PROMPT = -9;      // 提示界面，显示并返回

    public Fragment fmCurrent;
    private Fragment fmMain, fmPassword, fmQrCode, fmCenter, fmResident, fmFace;
    private Fragment fmFacePrompt, fmAdmin;
    private MainTabAdapter mAdapter;

    private ClickCounter mClickCounter = new ClickCounter();

    private MainBroadcastReceiver mReceiver;
    private IActCallBackListener actCallBackListener;

    private boolean mPlayAlarmSound = false;// 门报警

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        RecyclerView rvTab = findView(R.id.rv_tab);
        rvTab.setLayoutManager(new UniformLayoutManager());
        rvTab.setAdapter(mAdapter = new MainTabAdapter());

        SinglechipClientProxy.getInstance().setDoorAlarmListener(this);

        fmMain = new MainFragment();

        mReceiver = new MainBroadcastReceiver();
        mReceiver.register(mContext);

        parseIntent(getIntent());
        Log.d(TAG, "onCreate:");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {
        if (intent == null) {
            fragmentReplace(fmMain);
        } else {
            int funcCode = intent.getIntExtra(ARGS_FUNC, FUNC_DEFAULT);
            mAdapter.setCheckFunc(funcCode, intent.getExtras());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemSetUtils.isScreenOn()) {
            FreeObservable.getInstance().observeFree();
        }
        //初始化对讲监听
        initCallClient();
//        MainClient.getInstance().Main_FaceLicense();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FreeObservable.getInstance().cancelObserveFree();
        mReceiver.unregister(mContext);
        //关闭对讲监听
        stopCallClient();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (freeTime > 15000) {
            // 15秒无操作，返回默认界面

            if (fmCurrent instanceof BaseFaceFragment) {
                SinglechipClientProxy.getInstance().disableBodyInduction(1, 3000);
            } else if (fmCurrent instanceof QrCodeDecoderFragment) {
                SinglechipClientProxy.getInstance().disableBodyInduction(2, 3000);
            } else if (fmCurrent instanceof QrCodeEncoderFragment) {
                SinglechipClientProxy.getInstance().disableBodyInduction(3, 3000);
            }

            if (!(fmCurrent instanceof MainFragment)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setCheckFunc(FUNC_DEFAULT);
                    }
                });
            }
        }
        return true;
    }

    private void initCallClient() {
        CallManage.getInstance().initCallClient(this);
        CallManage.getInstance().setCallMonitorListener(this);
    }


    //给呼叫中心和呼叫住户fragment的回调
    public void setActCallBackListener(IActCallBackListener actCallBackListener) {
        this.actCallBackListener = actCallBackListener;
    }

    private void stopCallClient() {
        CallManage.getInstance().stopCallCommClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == K3Const.REQUEST_SETTING) {
            mAdapter.notifyTabListChanged();
            mAdapter.setCheckFunc(FUNC_DEFAULT);
        }
    }

    @Override
    public void onDoorAlarm(int doorAlarmType) {
        if (doorAlarmType == 1 && AlarmParamDao.getForceOpen() != 1) {
            return;
        }
        if (doorAlarmType == 2 && EntranceGuardDao.getAlarmOut() != 1) {
            return;
        }
        if ((fmCurrent instanceof CallResidentFragment && CallResidentFragment.isTalking) ||
                fmCurrent instanceof CallCenterFragment && CallCenterFragment.isTalking) {
            if (doorAlarmType == 1 || doorAlarmType == 2) {
                if (actCallBackListener != null) {
                    actCallBackListener.callBackValue(Const.AlarmAction.ALARM_OPEN_DOOR, null);
                }
            }
            return;
        }
        FreeObservable.getInstance().resetFreeTime();
        // 亮屏
        if (!SystemSetUtils.isScreenOn()) {
            SystemSetUtils.screenOn();
            SinglechipClientProxy.getInstance().ctrlTouchKeyLampState(true);
        }
        // 关屏保
        Activity topActivity = App.getInstance().getCurrentActivity();
        if (topActivity instanceof ScreenSaverActivity) {
            topActivity.finish();
        }
        // 播报警声
        mPlayAlarmSound = true;
        PlaySoundUtils.playAlarmSound(new MediaPlayerUtils.OnMediaStatusCompletionListener() {
            @Override
            public void onMediaStatusCompletion(boolean flag) {
                mPlayAlarmSound = false;
            }
        });
        // 文字提示：请关好门
        switch (doorAlarmType) {
            case 1:
            case 2:
                showHint(R.string.comm_text_d0, R.color.txt_red);
                break;
        }
    }

    private void stopAlarmPlay() {
        if (mPlayAlarmSound) {
            mPlayAlarmSound = false;
            PlaySoundUtils.stopPlayAssetsSound();
        }
    }

    @Override
    public void onBodyInduction() {
        super.onBodyInduction();
        if (fmCurrent instanceof MainFragment) {
            switch (AppConfig.getInstance().getBodyInduction()) {
                case 1:
                    mAdapter.setCheckFunc(FUNC_FACE);
                    break;
                case 2:
                case 3:
                    mAdapter.setCheckFunc(FUNC_QRCODE);
                    break;
            }
        }
    }

    @Override
    public void onCardState(int state, String roomNo) {
        super.onCardState(state, roomNo);
        if (fmCurrent instanceof CallCenterFragment
                || fmCurrent instanceof CallResidentFragment && (CallResidentFragment.isCalling
                || CallResidentFragment.isTalking)) {
            switch (state) {
                case 0:
                    if (actCallBackListener != null) {
                        actCallBackListener.callBackValue(Const.CardAction.INVALID_CARD, roomNo);
                    }
                    break;
                case 1:
                    if (actCallBackListener != null) {
                        actCallBackListener.callBackValue(Const.CardAction.OPNE_DOOR, roomNo);
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (state) {
                case 0:
                    if (!(fmCurrent instanceof WffrScanFragment)) {
                        showHint(R.string.comm_text_f2, R.color.txt_red);
                    }
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1503_PATH);
                    break;
                case 1:
                    if (!(fmCurrent instanceof WffrScanFragment)) {
                        showHint(R.string.comm_text_1, R.color.txt_white);
                    }
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
                    break;
            }
        }
    }

    @Override
    public void onFingerOpen(int code, String roomNo) {
        super.onFingerOpen(code, roomNo);
        switch (code) {
            case 0:
                showHint(R.string.finger_invalid, R.color.txt_red);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1506_PATH);
                SinglechipClientProxy.getInstance().setFingerDevNo("");
                break;
            case 1:
                showHint(R.string.comm_text_1, R.color.txt_white);
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
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        super.onKeyEvent(keyCode, keyState);
        if (!mAdapter.isClickable())
            return false;
        if (keyState == KEYSTATE_DOWN)
            return false;
        stopAlarmPlay();
        switch (keyCode) {
            case KEYCODE_0:
            case KEYCODE_1:
            case KEYCODE_2:
            case KEYCODE_3:
            case KEYCODE_4:
            case KEYCODE_5:
            case KEYCODE_6:
            case KEYCODE_7:
            case KEYCODE_8:
            case KEYCODE_9:
                Bundle args = CallResidentFragment.createArguments(keyCode % 10);
                mAdapter.setCheckFunc(FUNC_RESIDENT, args);
                break;
            case KEYCODE_CALL:
                mAdapter.setCheckFunc(FUNC_CENTER);
                break;
            case KEYCODE_UNLOCK:
                if (fmCurrent instanceof PasswordFragment) {
                    int count = mClickCounter.increase();
                    if (count == 5) {
                        mAdapter.setCheckFunc(FUNC_ADMIN);
                    }
                } else {
                    mAdapter.setCheckFunc(FUNC_PASSWORD);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private void showHint(@StringRes int msgId, @ColorRes int colorId) {
        if (fmCurrent instanceof MainFragment) {
            ((MainFragment) fmCurrent).showHint(msgId, colorId);
        } else {
            Bundle args = new Bundle();
            args.putInt(MainFragment.TEXT_ID, msgId);
            args.putInt(MainFragment.COLOR_ID, colorId);
            mAdapter.setCheckFunc(FUNC_DEFAULT, args);
        }
    }

    protected Fragment createFragment(int mainFunc) {
        Fragment fragment = null;
        switch (mainFunc) {
            case FUNC_PASSWORD:
                if (fmPassword == null) {
                    fmPassword = new PasswordFragment();
                }
                fragment = fmPassword;
                break;
            case FUNC_QRCODE:
                int qrOpenDoorType = AppConfig.getInstance().getQrOpenType();
                if (qrOpenDoorType == 0) {
                    if (!(fmQrCode instanceof QrCodeDecoderFragment))
                        fmQrCode = new QrCodeDecoderFragment();
                } else {
                    if (!(fmQrCode instanceof QrCodeEncoderFragment))
                        fmQrCode = new QrCodeEncoderFragment();
                }
                fragment = fmQrCode;
                break;
            case FUNC_CENTER:
                if (fmCenter == null) {
                    fmCenter = new CallCenterFragment();
                }
                fragment = fmCenter;
                break;
            case FUNC_RESIDENT:
                if (fmResident == null) {
                    fmResident = new CallResidentFragment();
                }
                fragment = fmResident;
                break;
            case FUNC_FACE:
                if (fmFace == null) {
                    if (AppConfig.getInstance().getFaceManufacturer() == 0) {
                        fmFace = new WffrScanFragment();
                    } else if (AppConfig.getInstance().getFaceManufacturer() == 1) {
                        fmFace = new MegviiScanFragment();
                    }
                }
                fragment = fmFace;
                break;
            case FUNC_DEFAULT:
                fragment = fmMain;
                break;
            case FUNC_ADMIN:
                if (fmAdmin == null) {
                    fmAdmin = new AdminFragment();
                }
                fragment = fmAdmin;
                break;
            case FUNC_FACE_PROMPT:
                if (fmFacePrompt == null) {
                    fmFacePrompt = new FacePromptFragment();
                }
                fragment = fmFacePrompt;
                break;
        }
        return fragment;
    }

    private void fragmentReplace(Fragment fragment) {
        if (fmCurrent != fragment) {
            FragmentUtils.replace(this, R.id.fl_container, fragment);
            fmCurrent = fragment;
        }
    }

    public void setTabEnabled(boolean enabled) {
        mAdapter.setClickable(enabled || mPlayAlarmSound);//门报警时按键可用
    }

    private class MainTab {
        private int code;
        private int textId;
        private int drawableId;

        public MainTab(int code, @StringRes int textId, @DrawableRes int drawableId) {
            this.code = code;
            this.textId = textId;
            this.drawableId = drawableId;
        }

        public int getCode() {
            return code;
        }

        public int getTextId() {
            return textId;
        }

        public int getDrawableId() {
            return drawableId;
        }
    }

    private class TabVH extends RecyclerView.ViewHolder {

        private CheckableLayout mLayout;
        private ImageView mImageView;
        private TextView mTextView;

        private TabVH(View itemView) {
            super(itemView);
            mLayout = ViewUtils.findView(itemView, R.id.ly_root);
            mImageView = ViewUtils.findView(itemView, R.id.imageview);
            mTextView = ViewUtils.findView(itemView, R.id.textview);
        }

        public void setText(@StringRes int resid) {
            mTextView.setText(resid);
        }

        public void setIcon(@DrawableRes int resid) {
            mImageView.setImageResource(resid);
        }

        public void setChecked(boolean checked) {
            mLayout.setChecked(checked);
        }
    }

    private class MainTabAdapter extends RecyclerView.Adapter<TabVH> {

        private int mCheckFunc = FUNC_DEFAULT;
        private boolean mClickable = true;
        private List<MainTab> mTabList;

        private MainTabAdapter() {
            mTabList = createMainTabList();
        }

        @NonNull
        @Override
        public TabVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_tab, parent, false);
            return new TabVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final TabVH holder, int position) {
            final MainTab mainTab = mTabList.get(position);
            holder.setText(mainTab.getTextId());
            holder.setIcon(mainTab.getDrawableId());
            holder.setChecked(mainTab.getCode() == mCheckFunc);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fmCurrent instanceof BaseFaceFragment) {
                        SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(1);
                    } else if (fmCurrent instanceof QrCodeDecoderFragment) {
                        SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(2);
                    } else if (fmCurrent instanceof QrCodeEncoderFragment) {
                        SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(3);
                    }
                    stopAlarmPlay();
                    setCheckFunc(mainTab.getCode());
                }
            });
            holder.itemView.setClickable(mClickable);
        }

        @Override
        public int getItemCount() {
            return mTabList.size();
        }

        public void setCheckFunc(int checkFunc) {
            setCheckFunc(checkFunc, null);
        }

        public void setCheckFunc(int checkFunc, Bundle args) {
            if (mClickable) {
                mCheckFunc = checkFunc;
                notifyDataSetChanged();
                Fragment fragment = createFragment(checkFunc);
                LogUtils.d("checkFunc(%d, %s), fmCurrent=%s", checkFunc, fragment, fmCurrent);
                if (fragment != null) {
                    fragment.setArguments(args);
                    fragmentReplace(fragment);
                }
            }
        }

        public boolean isClickable() {
            return mClickable;
        }

        public void setClickable(boolean clickable) {
            if (mClickable != clickable) {
                mClickable = clickable;
                notifyDataSetChanged();
            }
        }

        public void notifyTabListChanged() {
            mTabList = createMainTabList();
            notifyDataSetChanged();
        }

        private List<MainTab> createMainTabList() {
            List<MainTab> tabList = new ArrayList<>();
            tabList.add(new MainTab(FUNC_PASSWORD, R.string.unlock_by_password, R.drawable.main_password));
            if (AppConfig.getInstance().isQrCodeEnabled()) {
                tabList.add(new MainTab(FUNC_QRCODE, R.string.unlock_by_qrcode, R.drawable.main_qr_scan));
            }
            tabList.add(new MainTab(FUNC_CENTER, R.string.call_center, R.drawable.main_call_center));
            tabList.add(new MainTab(FUNC_RESIDENT, R.string.call_resident, R.drawable.main_call_resident));
            if (AppConfig.getInstance().isFaceEnabled()) {
                tabList.add(new MainTab(FUNC_FACE, R.string.unlock_by_face, R.drawable.main_face_scan));
            }
            return tabList;
        }
    }

    private class ClickCounter {

        private int mCount = 0;
        private Timer mTimer;
        private TimerTask mResetTask;

        private ClickCounter() {
            mTimer = new Timer();
        }

        private int increase() {
            if (mResetTask != null)
                mResetTask.cancel();
            mCount++;
            mResetTask = new ResetTask();
            mTimer.schedule(mResetTask, 1000);
            return mCount;
        }

        private class ResetTask extends TimerTask {

            @Override
            public void run() {
                mCount = 0;
            }
        }
    }

    private class MainBroadcastReceiver extends BroadcastReceiver {

        public void register(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.Action.MAIN_DEFAULT);
            filter.addAction(Const.Action.MAIN_FACE_PROMPT);
            if (CustomVersion.VERSION_K3_SCREENOFF_FACE_RECOGNIZE) {
                filter.addAction(Const.Action.MAIN_FACE);
            }
            context.registerReceiver(this, filter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || action.length() == 0)
                return;
            switch (action) {
                case Const.Action.MAIN_DEFAULT:
//                    LogUtils.e("====MainActivity onReceive MAIN_DEFAULT");
                    setTabEnabled(true);
                    mAdapter.setCheckFunc(FUNC_DEFAULT, intent.getExtras());
                    //初始化对讲监听
                    initCallClient();
                    break;
                case Const.Action.MAIN_FACE_PROMPT:
                    mAdapter.setCheckFunc(FUNC_FACE_PROMPT);
                    setTabEnabled(false);
                    break;
                case Const.Action.MAIN_FACE:
                    if (CustomVersion.VERSION_K3_SCREENOFF_FACE_RECOGNIZE) {
                        if (!SystemSetUtils.isScreenOn()) {
                            setTabEnabled(true);
                            mAdapter.setCheckFunc(FUNC_FACE, intent.getExtras());
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onCallMonitor(CallMonitorBean callMonitorBean) {
        Activity activity = App.getInstance().getCurrentActivity();
        if (activity instanceof ScreenSaverActivity) {
            activity.finish();
        }
        String callFrom = callMonitorBean.getCallFrom();
        boolean openDoor = callMonitorBean.isOpenDoor();
        boolean isCallEnd = callMonitorBean.isCallEnd();
        boolean isCallTalk = callMonitorBean.isCallTalk();

        if ((fmCurrent instanceof CallResidentFragment && CallResidentFragment.isCalling || CallResidentFragment.isTalking)
                || fmCurrent instanceof CallCenterFragment) {
            if (isCallTalk || isCallEnd) {
                if (fmCurrent instanceof CallCenterFragment) {
                    if (callFrom.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                        toCallResidentFrag();
                    } else if (callFrom.equals(Const.CallAction.CALL_FROM_CENTER)) {
                        toCallStateBack(callMonitorBean);
                    }
                } else {
                    if (callFrom.equals(Const.CallAction.CALL_FROM_RESIDENT)) {
                        toCallStateBack(callMonitorBean);
                    } else if (callFrom.equals(Const.CallAction.CALL_FROM_CENTER)) {
                        toCallCenterFrag();
                    }
                }
            }else {
               toCallStateBack(callMonitorBean);
            }
        } else {
            if (openDoor) {
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH);
                //设置界面和人脸界面只播声音
                if (App.getInstance().getCurrentActivity() instanceof SettingActivity || fmCurrent instanceof BaseFaceFragment) {
                    return;
                }
                showHint(R.string.comm_text_1, R.color.txt_white);
            } else {
                if (fmCurrent instanceof MainFragment && callMonitorBean.isCallEnd()) {
                    return;
                }
                Activity act = App.getInstance().getCurrentActivity();
                if (act instanceof SettingActivity) {
                    activity.finish();
                }
                setTabEnabled(true);
                if (Const.CallAction.CALL_FROM_CENTER.equals(callFrom)) {
                    if (fmCurrent instanceof CallResidentFragment) {
                        toCallStateBack(callMonitorBean);
                    }else {
                        toCallCenterFrag();
                    }
                }else if (Const.CallAction.CALL_FROM_RESIDENT.equals(callFrom)) {
                    if (fmCurrent instanceof CallResidentFragment) {
                        toCallStateBack(callMonitorBean);
                    }else {
                        toCallResidentFrag();
                    }
                }
            }
        }
    }

    public void toCallResidentFrag() {
        setTabEnabled(true);
        //跳转到呼叫住户界面
        Bundle args = CallResidentFragment.createArguments(Const.CallAction.CALL_FROM_RESIDENT);
        mAdapter.setCheckFunc(FUNC_RESIDENT, args);
    }

    public void toCallCenterFrag() {
        setTabEnabled(true);
        //跳转到呼叫住户界面
        Bundle args = CallCenterFragment.createArguments(Const.CallAction.CALL_FROM_CENTER);
        mAdapter.setCheckFunc(FUNC_CENTER, args);
    }

    public void toCallStateBack(CallMonitorBean callMonitorBean) {
        if (actCallBackListener != null) {
            //如果在呼叫界面，回调监视数据
            actCallBackListener.callBack(callMonitorBean);
        }
    }

}
