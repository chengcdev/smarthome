package com.mili.smarthome.tkj.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.PassWordClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppExecutors;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.userInfo.UserPwdModels;
import com.mili.smarthome.tkj.main.adapter.NumBitmapAdapter;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.widget.MultiImageView;

import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;

public class PasswordFragment extends K3BaseFragment implements RadioGroup.OnCheckedChangeListener, InterCommTypeDef.PassWordCmddListener {

    private static final int PWD_TYPE_RESIDENT = 0x00;
    private static final int PWD_TYPE_EXPRESS = 0x01;
    private static final int PWD_TYPE_TAKE_OUT = 0x02;
    private static final int PWD_TYPE_OTHER = 0x03;

    private static int ErrCount = 0;

    private TextView tvHint;
    private RadioGroup rgPwdType;
    private NumBitmapAdapter mAdapter;

    private FullDeviceNo mFullDeviceNo;
    private PassWordClient mPassWordClient;
    private boolean mIsSeniorPwd; // 是否是高级密码
    private int mRoomNoLen = 0; // 房号长度
    private int mPwdLen = 6;
    private int mPwdType = PWD_TYPE_RESIDENT;
    private boolean mPwdVerify = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_password;
    }

    @Override
    protected void bindView() {
        tvHint = findView(R.id.tv_hint);

        MultiImageView ivNums = findView(R.id.iv_nums);
        ivNums.setAdapter(mAdapter = new NumBitmapAdapter(mContext) {
            @Override
            public boolean isMask(int position) {
                if (mIsSeniorPwd && mPwdType == PWD_TYPE_RESIDENT) {
                    return position >= mRoomNoLen;
                }
                return true;
            }
        });

        rgPwdType = findView(R.id.rg_pwd_type);
        rgPwdType.setOnCheckedChangeListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1301_PATH);

        tvHint.setVisibility(View.VISIBLE);
        mAdapter.clear();
        mPwdVerify = false;

        mPassWordClient = new PassWordClient(mContext);
        mPassWordClient.setPassWordDataCallBKListener(this);

        mFullDeviceNo = new FullDeviceNo(mContext);
        if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomNoLen = mFullDeviceNo.getRoomNoLen();
        } else {
            mRoomNoLen = mFullDeviceNo.getStairNoLen() + mFullDeviceNo.getRoomNoLen();
        }

        mIsSeniorPwd = (AppConfig.getInstance().getOpenPwdMode() == 1);
        setPwdLength();
    }

    private void setPwdLength() {
        if (mIsSeniorPwd && mPwdType == PWD_TYPE_RESIDENT) {
            mPwdLen = mRoomNoLen + 5;
        } else {
            mPwdLen = 6;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rgPwdType.check(R.id.rb_resident);
    }

    @Override
    public void onDestroyView() {
        mPassWordClient.setPassWordDataCallBKListener(null);
        super.onDestroyView();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_resident:
                mPwdType = PWD_TYPE_RESIDENT;
                break;
            case R.id.rb_express:
                mPwdType = PWD_TYPE_EXPRESS;
                break;
            case R.id.rb_take_out:
                mPwdType = PWD_TYPE_TAKE_OUT;
                break;
            case R.id.rb_other:
                mPwdType = PWD_TYPE_OTHER;
                break;
        }
        mAdapter.clear();
        setPwdLength();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN || keyCode == KEYCODE_UNLOCK)
            return false;
        switch (keyCode) {
            case KEYCODE_0:
                inputNum(0);
                break;
            case KEYCODE_1:
            case KEYCODE_2:
            case KEYCODE_3:
            case KEYCODE_4:
            case KEYCODE_5:
            case KEYCODE_6:
            case KEYCODE_7:
            case KEYCODE_8:
            case KEYCODE_9:
                inputNum(keyCode);
                break;
            case KEYCODE_BACK:
                backspace();
                break;
            case KEYCODE_CALL:
                if (mPwdVerify) {
                    break;
                }
                if (mAdapter.getCount() > 0 && mAdapter.getCount() < mPwdLen) {
                    mPwdVerify = true;
                    AppExecutors.newThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mPwdType == PWD_TYPE_RESIDENT) {
                                if (mIsSeniorPwd) {
                                    seniorPwd(mAdapter.getText());
                                } else {
                                    easyPwd(mAdapter.getText());
                                }
                            } else {
                                otherPwd(mAdapter.getText(), mPwdType);
                            }
                        }
                    });
                }
                break;
        }
        return true;
    }

    @Override
    protected void inputNum(int num) {
        if (mPwdVerify) {
            return;
        }
        if (mAdapter.getCount() >= mPwdLen) {
            return;
        }
        mAdapter.input(num);
        tvHint.setVisibility(View.INVISIBLE);
        if (mAdapter.getCount() == mPwdLen) {
            AppExecutors.newThread().execute(new Runnable() {
                @Override
                public void run() {
                    if (mPwdType == PWD_TYPE_RESIDENT) {
                        if (mIsSeniorPwd) {
                            seniorPwd(mAdapter.getText());
                        } else {
                            easyPwd(mAdapter.getText());
                        }
                    } else {
                        otherPwd(mAdapter.getText(), mPwdType);
                    }
                }
            });
        }
    }

    @Override
    protected void backspace() {
        if (mAdapter.getCount() == 0) {
            GotoMainDefaultTask.getInstance().run();
            return;
        }
        mAdapter.backspace();
        if (mAdapter.getCount() == 0) {
            tvHint.setVisibility(View.VISIBLE);
        }
    }

    private void easyPwd(String openPwd) {
        LogUtils.d("PasswordFragment--->>>easyPwd: %s", openPwd);
        UserInfoDao userInfoDao = new UserInfoDao();
        int result = userInfoDao.verifyPwd(openPwd, false);
        Bundle args = new Bundle();
        if (result == SUCCESS_STATE) {
            UserPwdModels model = userInfoDao.getUserPwdModel(openPwd);
            if (model != null)
            {
                mPassWordClient.DealPassWord(model.getKeyID(), model.getRoomNo(), 0);
                ErrCount = 0;
                //
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, model.getRoomNo(), 0);
                args.putInt(MainFragment.TEXT_ID, R.string.comm_text_1);
                if (BuildConfig.isEnabledPwdValid && (model.getLifecycle() > VALID_LIFECYCLE_MODE)){
                    userInfoDao.subLifecycle(openPwd);
                }
            }
        } else {
            //
            ErrCount++;
            if (ErrCount == 4) {
                String roomNo;
                if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    roomNo = "0000"; // 梯口机
                } else {
                    roomNo = mFullDeviceNo.getDeviceNo(); // 区口机
                }
                mPassWordClient.DealPassWord("", roomNo, 1);
                ErrCount = 0;
            }
            //
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1302_PATH);
            args.putInt(MainFragment.TEXT_ID, R.string.comm_text_f1);
            args.putInt(MainFragment.COLOR_ID, R.color.txt_red);
        }
        ContextProxy.sendBroadcast(Const.Action.MAIN_DEFAULT, args);
    }

    private void seniorPwd(String openPwd) {
        LogUtils.d("PasswordFragment--->>>seniorPwd: %s", openPwd);
        //截取开门密码房号
        String roomNo = openPwd.substring(0, mRoomNoLen);
        //截取开门密码后六位的密码
        String pwd = openPwd.substring(mRoomNoLen);
        mPassWordClient.DealAdvPassWord(roomNo, pwd);
    }

    private void otherPwd(String openPwd, int pwdType) {
        LogUtils.d("PasswordFragment--->>>otherPwd(%d): %s", pwdType, openPwd);
        String devNo;
        if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            devNo = "0000"; // 梯口机
        } else {
            devNo = mFullDeviceNo.getDeviceNo(); // 区口机
        }
        mPassWordClient.DealOtherPassWord(devNo, openPwd, pwdType);
    }

    @Override
    public void PassWordCmdListener(int param, int param2, String roomNo) {
        LogUtils.d("PasswordFragment--->>>PassWordCmdListener: %X, %X", param, param2);
        Bundle args = new Bundle();
        switch (param2) {
            case CommTypeDef.TextHit.OpenDoor_Open_OK:
                // 门开了，请进入
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, 0);
                args.putInt(MainFragment.TEXT_ID, R.string.comm_text_1);
                break;
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd:
                // 密码错误
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1302_PATH);
                args.putInt(MainFragment.TEXT_ID, R.string.comm_text_f1);
                args.putInt(MainFragment.COLOR_ID, R.color.txt_red);
                break;
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd_NoUse:
                // 密码功能未启用
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1303PATH);
                args.putInt(MainFragment.TEXT_ID, R.string.comm_text_f3);
                args.putInt(MainFragment.COLOR_ID, R.color.txt_red);
                break;
            default:
                return;
        }
        ContextProxy.sendBroadcast(Const.Action.MAIN_DEFAULT, args);
    }
}
