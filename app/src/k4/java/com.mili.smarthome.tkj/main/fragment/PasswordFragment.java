package com.mili.smarthome.tkj.main.fragment;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.PassWordClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.userInfo.UserPwdModels;
import com.mili.smarthome.tkj.main.view.HintView;
import com.mili.smarthome.tkj.main.view.NumberView;
import com.mili.smarthome.tkj.setting.activity.SettingActivity;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;

public class PasswordFragment extends K4BaseFragment implements InterCommTypeDef.PassWordCmddListener {

    private static final String Tag = "PasswordFragment";
    private RadioGroup mRadioGroup;
    private HintView mHvHint;
    private NumberView mNvPassword, mNvPwdAdmin;
    private TextView mTvHint, mTvHintAdmin;

    private FrameLayout mLlcontent, mLlAdmin;
    private LinearLayout mLlHint;

    private int mKeyCount = 0;
    private int mRoomLen = 4;

    private FullDeviceNo mFullDeviceNo;
    private PassWordClient mPassWordClient;
    private boolean mIsSeniorPwd;   // 是否是高级密码
    private boolean mIsAdminPwd;    // 是否管理密码
    private static int mErrCount = 0;
    private int mEasyPwdType = 0;   // 简易密码类型 住户密码:0x00 快递:0x01 外卖:0x02 其他:0x03


    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (mNvPassword == null) {
            return true;
        }

        String password = mNvPassword.getText();
        if (password.length() > 0) {
            if (mIsSeniorPwd) {
                seniorPwd(password);
            } else {
                if (mEasyPwdType == 0) {
                    easyPwd(password);
                } else {
                    easyPwdOther(password);
                }
            }
            mKeyCount = 0;
        } else if (!mIsAdminPwd){
            mKeyCount++;
            if (mKeyCount >= 5) {
                mIsAdminPwd = true;
                showAdminView();
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1305_PATH);
            }
        } else {
            mKeyCount = 0;
        }
        return true;
    }

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        String text;
        if (mIsAdminPwd) {
            text = mNvPwdAdmin.getText();
            if (text.length() == 1) {
                showAdminView();
            } else if (text.length() == 0) {
                requestBack();
            }
        } else {
            text = mNvPassword.getText();
            if (text.length() == 1) {
                showPwdView();
            } else if (text.length() == 0) {
                requestBack();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyText(String text) {
        super.onKeyText(text);
        if (mNvPassword == null || mNvPwdAdmin == null) {
            return true;
        }
        if (text.length() == 0) {
            return true;
        }

        if (mIsAdminPwd) {
            mNvPwdAdmin.setText(text);
            mTvHintAdmin.setVisibility(View.INVISIBLE);
            adminPwd(text);
        } else {
            mNvPassword.setText(text);
            mTvHint.setVisibility(View.INVISIBLE);
            mNvPassword.setVisibility(View.VISIBLE);
            if (mIsSeniorPwd) {
                if (text.length() >= Constant.PASSWORD_SENIOR_LEN + mRoomLen) {
                    seniorPwd(text);
                }
            } else {
                if (text.length() >= Constant.PASSWORD_LEN) {
                    if (mEasyPwdType == 0) {
                        easyPwd(text);
                    } else {
                        easyPwdOther(text);
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_MAIN_HINT:
                int resId = msg.arg1;
                int colorId = msg.arg2;
                mHvHint.setHint(resId, colorId);
                showHintView(true);
                mMainHandler.sendEmptyMessageDelayed(MSG_REQUEST_EXIT, Constant.MAIN_HINT_TIMEOUT);
                break;

            case MSG_REQUEST_EXIT:
                requestBack();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_password;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mRadioGroup = findView(R.id.rg_password);
        if (mRadioGroup != null) {
            mRadioGroup.check(R.id.pwd_resident);
            mRadioGroup.setOnCheckedChangeListener(mRGListener);
        }

        mTvHint = findView(R.id.tv_hint);
        mTvHintAdmin = findView(R.id.tv_hint_admin);
        mHvHint = findView(R.id.hv_hint);

        mNvPassword = findView(R.id.gv_password);
        mNvPwdAdmin = findView(R.id.gv_password_admin);

        mLlcontent = findView(R.id.ll_content);
        mLlAdmin = findView(R.id.ll_admin);
        mLlHint = findView(R.id.ll_hint);
    }

    @Override
    protected void bindData() {
        super.bindData();
        mPassWordClient = new PassWordClient(mContext);
        mPassWordClient.setPassWordDataCallBKListener(this);
        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1301_PATH);
    }

    @Override
    protected void unbindView() {
        super.unbindView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFullDeviceNo == null) {
            mFullDeviceNo = new FullDeviceNo(mContext);
        }
        if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomLen = mFullDeviceNo.getRoomNoLen();
        } else {
            mRoomLen = mFullDeviceNo.getStairNoLen() + mFullDeviceNo.getRoomNoLen();
        }

        if (mNvPwdAdmin != null) {
            mNvPwdAdmin.setMode(NumberView.MODE_PASSWORD);
            mNvPwdAdmin.setLen(0, Constant.PASSWORD_ADMIN_LEN);
        }

        //动态密码
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            shuffleKeyboard();
        }

        mEasyPwdType = 0;
        mRadioGroup.check(R.id.pwd_resident);
        showPwdMode();
        showPwdView();
        mIsAdminPwd = false;
        mKeyCount = 0;
        LogUtils.d(Tag + " ======= onResume: mIsSeniorPwd is " + mIsSeniorPwd);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            resetKeyboard();
        }
    }

    /**
     * 只有住户才有高级密码模式
     */
    private void showPwdMode() {
        if (AppConfig.getInstance().getPwdDoorMode() == 1 && mEasyPwdType == 0) {
            mIsSeniorPwd = true;
            if (mNvPassword != null) {
                mNvPassword.setMode(NumberView.MODE_PASSWORD_SENIOR);
                mNvPassword.setLen(mRoomLen, Constant.PASSWORD_SENIOR_LEN);
                mNvPassword.clear();
            }
        } else {
            mIsSeniorPwd = false;
            if (mNvPassword != null) {
                mNvPassword.setMode(NumberView.MODE_PASSWORD);
                mNvPassword.setLen(0, Constant.PASSWORD_LEN);
                mNvPassword.clear();
            }
        }
    }

//    private void checkPwdType() {
//        if (mRadioGroup != null) {
//            int viewId = R.id.pwd_resident;
//            switch (mEasyPwdType) {
//                case 0:
//                    viewId = R.id.pwd_resident;
//                    break;
//                case 1:
//                    viewId = R.id.pwd_express;
//                    break;
//                case 2:
//                    viewId = R.id.pwd_takeout;
//                    break;
//                case 3:
//                    viewId = R.id.pwd_other;
//                    break;
//
//            }
//            mRadioGroup.check(viewId);
//        }
//    }

    private void showPwdView() {
        mRadioGroup.setVisibility(View.VISIBLE);
        mTvHint.setVisibility(View.VISIBLE);
        mTvHint.setText(R.string.set_input_door_pwd);

        if (mNvPassword != null) {
            mNvPassword.clear();
            mNvPassword.setVisibility(View.INVISIBLE);
        }
        showHintView(false);

        if (mTvHintAdmin != null) {
            mTvHintAdmin.setVisibility(View.INVISIBLE);
            mNvPwdAdmin.setVisibility(View.INVISIBLE);
        }

        if (mIsSeniorPwd) {
            setKeyboardMaxlen(Constant.PASSWORD_SENIOR_LEN + mRoomLen);
        } else {
            setKeyboardMaxlen(Constant.PASSWORD_LEN);
        }
        setKeyboardMode(0);
        setKeyboardText("");
    }

    private void showAdminView() {
        mTvHintAdmin.setVisibility(View.VISIBLE);
        mNvPwdAdmin.setVisibility(View.VISIBLE);
        mNvPwdAdmin.clear();

        mRadioGroup.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.INVISIBLE);
        mNvPassword.setVisibility(View.INVISIBLE);
        showHintView(false);

        setKeyboardMaxlen(Constant.PASSWORD_ADMIN_LEN);
        setKeyboardMode(0);
        setKeyboardText("");
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            resetKeyboard();
        }
    }

    /**
     * 显示密码正确性提示
     */
    private void showHintView(boolean show) {
        if (show) {
            mLlcontent.setVisibility(View.INVISIBLE);
            mLlAdmin.setVisibility(View.INVISIBLE);
            mLlHint.setVisibility(View.INVISIBLE);
            mHvHint.setVisibility(View.VISIBLE);
        } else {
            mLlcontent.setVisibility(View.VISIBLE);
            mLlAdmin.setVisibility(View.VISIBLE);
            mLlHint.setVisibility(View.VISIBLE);
            mHvHint.setVisibility(View.INVISIBLE);
        }
    }

    private void showHint(int resId, int colorId) {
        Message message = Message.obtain();
        message.what = MSG_MAIN_HINT;
        message.arg1 = resId;
        message.arg2 = colorId;
        mMainHandler.sendMessageDelayed(message, 200);
    }

    private void adminPwd(String adminPwd) {
        if (adminPwd.length() < Constant.PASSWORD_ADMIN_LEN) {
            return;
        }
        UserInfoDao userInfoDao = new UserInfoDao();
        String password = userInfoDao.getAdminPwd();
        if (adminPwd.equals(password)) {
            Intent intent = new Intent(mContext, SettingActivity.class);
            startActivity(intent);
            ContextProxy.sendBroadcast(Const.Action.MAIN_DEFAULT);
        } else {
            setFragmentClickable(false);
            showHint(R.string.pwd_err, R.color.txt_red);
        }
    }

    /**
     * 简易密码
     * @param openPwd   开门密码
     */
    private void easyPwd(String openPwd) {
        LogUtils.d(Tag + " easyPwd: openPwd is " + openPwd);

        setFragmentClickable(false);
        UserInfoDao userInfoDao = new UserInfoDao();
        int result = userInfoDao.verifyPwd(openPwd, false);
        if (result == SUCCESS_STATE) {
            UserPwdModels model = userInfoDao.getUserPwdModel(openPwd);
            if (model != null) {
                mPassWordClient.DealPassWord(model.getKeyID(), model.getRoomNo(), 0);
                mErrCount = 0;
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, model.getRoomNo(), 0);
                showHint(R.string.comm_text_1, R.color.txt_green);
                if (BuildConfig.isEnabledPwdValid && (model.getLifecycle() > VALID_LIFECYCLE_MODE)){
                    userInfoDao.subLifecycle(openPwd);
                }
            }
        } else {
            mErrCount++;
            if (mErrCount == 4) {
                String roomNo;
                if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    roomNo = "0000"; // 梯口机
                } else {
                    roomNo = mFullDeviceNo.getDeviceNo(); // 区扣机
                }
                mPassWordClient.DealPassWord("",roomNo, 1);
                mErrCount = 0;
            }
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1302_PATH);
            showHint(R.string.comm_text_f1, R.color.txt_red);
        }
    }

    /**
     * 非住户类型的简易密码
     * @param openPwd 开门密码
     */
    private void easyPwdOther(String openPwd) {
        setFragmentClickable(false);
        if (mPassWordClient != null) {
            mPassWordClient.DealOtherPassWord("0000", openPwd, mEasyPwdType);
        }
    }

    /**
     * 高级密码
     * @param openPwd 开门密码
     */
    private void seniorPwd(String openPwd) {
        if (openPwd.length() > mRoomLen) {
            setFragmentClickable(false);
            new Thread(new SeniorPwdTask(openPwd)).start();
        }
    }

    /**
     * 高级密码任务
     */
    private class SeniorPwdTask implements Runnable {
        private String mSeniorPwd;
        private SeniorPwdTask(String pwd) {
            mSeniorPwd = pwd;
        }

        @Override
        public void run() {
            //截取开门密码房号
            String roomNo = mSeniorPwd.substring(0, mRoomLen);
            //截取开门密码后六位的密码
            String pwd = mSeniorPwd.substring(mRoomLen);
            mPassWordClient.DealAdvPassWord(roomNo, pwd);
        }
    }

    @Override
    public void PassWordCmdListener(int param, int param2, String roomNo) {
        switch (param2) {
            case CommTypeDef.TextHit.OpenDoor_Open_OK:
                // 门开了，请进入
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
                showHint(R.string.comm_text_1, R.color.txt_green);
                break;
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd:
                // 密码错误
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1302_PATH);
                showHint(R.string.comm_text_f1, R.color.txt_red);
                break;
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd_NoUse:
                // 密码功能未启用
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1303PATH);
                showHint(R.string.comm_text_f3, R.color.txt_red);
                break;
        }
    }

    private RadioGroup.OnCheckedChangeListener mRGListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            int pwdType = 0;
            switch (checkedId) {
                case R.id.pwd_resident:
                    pwdType = 0;
                    break;
                case R.id.pwd_express:
                    pwdType = 1;
                    break;
                case R.id.pwd_takeout:
                    pwdType = 2;
                    break;
                case R.id.pwd_other:
                    pwdType = 3;
                    break;
            }
            mEasyPwdType = pwdType;
            showPwdMode();
            showPwdView();
            LogUtils.d(Tag + " pwdType = " + pwdType);
        }
    };
}
