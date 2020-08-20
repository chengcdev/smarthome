package com.mili.smarthome.tkj.fragment;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.PassWordClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.userInfo.UserPwdModels;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.setting.activity.SettingActivity;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.NumberView;

import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;
import static com.mili.smarthome.tkj.constant.Constant.OPENDOOR_ROOMNO;

public class PwdOpenFragment extends BaseMainFragment implements KeyBoardAdapter.IKeyBoardListener, NumberView.INumViewListener, RadioGroup.OnCheckedChangeListener, InterCommTypeDef.PassWordCmddListener {


    private KeyBoardView keyBoardView;
    private NumberView mNumView;
    private RadioGroup rgPwdType;
    //是否输入的是管理员密码
    private boolean isIuputAdmin;
    //是否播放过声音
    private boolean isPlayed;
    private UserInfoDao userInfoDao;
    private PassWordClient passWordClient;
    private FullDeviceNo fullDeviceNo;
    //是否高级密码
    private boolean isSeniorPwd;
    //房号长度
    private int roomNoLen;
    //默认密码长度
    private int defaultPwdLen = 6;
    private HintDialogFragment hintDialogFragment;
    private String mNum = "";
    //简易密码 类型
    // 住户密码:0x00 快递:0x01 外卖:0x02 其他:0x03
    private int pwdType = 0x00;
    private static final int PWD_USER = 0x00;
    private static final int PWD_EXPRESS = 0x01;
    private static final int PWD_WAIMAI = 0x02;
    private static final int PWD_OTHER = 0x03;

    @Override
    public void initView(View view) {
        keyBoardView = (KeyBoardView) view.findViewById(R.id.keyboardview);
        mNumView = (NumberView) view.findViewById(R.id.num_view);
        rgPwdType = (RadioGroup) view.findViewById(R.id.rg_pwd_type);

        RadioButton mRbResident = (RadioButton) view.findViewById(R.id.rb_resident);
        RadioButton mRbExpress = (RadioButton) view.findViewById(R.id.rb_express);
        RadioButton mRbTakeOut = (RadioButton) view.findViewById(R.id.rb_take_out);
        RadioButton mRbOther = (RadioButton) view.findViewById(R.id.rb_other);

        setRbDrawbleSize(mRbResident, R.drawable.ctrl_resident_icon);
        setRbDrawbleSize(mRbExpress, R.drawable.ctrl_experss_icon);
        setRbDrawbleSize(mRbTakeOut, R.drawable.ctrl_take_out_icon);
        setRbDrawbleSize(mRbOther, R.drawable.ctrl_other_icon);


        rgPwdType.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_pwd_open;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNum = "";
        isIuputAdmin = false;
        isPlayed = false;
        if (AppConfig.getInstance().getPwdDynamic() == 1) {
            //启用动态密码
            keyBoardView.setRandomKeyBoard(true);
        }
        keyBoardView.init(KeyBoardView.KEY_BOARD_PWD);
        keyBoardView.setKeyBoardListener(this);
        mNumView.setNumListener(this);
        rgPwdType.setOnCheckedChangeListener(this);
        rgPwdType.check(R.id.rb_resident);
        if (hintDialogFragment == null) {
            hintDialogFragment = new HintDialogFragment();
        }
        //密码回调监听
        if (passWordClient == null) {
            passWordClient = new PassWordClient(getContext());
        }
        passWordClient.setPassWordDataCallBKListener(this);
        if (fullDeviceNo == null) {
            fullDeviceNo = new FullDeviceNo(getContext());
        }
        if (AppConfig.getInstance().getOpenPwdMode() == 0) {
            isSeniorPwd = false;
        } else {
            isSeniorPwd = true;
        }
        if (isSeniorPwd) {
            //获取房号长度
            if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
                //区口机 房号长度 = 楼栋号+单元号+房号长度
                //梯号长度
                int stairNoLen = fullDeviceNo.getStairNoLen();
                //单元号长度
                int cellNoLen = fullDeviceNo.getCellNoLen();
                //楼栋号长度
                int buildNoLen = stairNoLen - cellNoLen;

                roomNoLen = stairNoLen + cellNoLen + buildNoLen;
            } else {
                roomNoLen = fullDeviceNo.getRoomNoLen();
            }
            //设置高级密码时，开门密码的长度为房号+密码长度
            mNumView.init(getString(R.string.set_input_door_pwd), roomNoLen + 5);
        } else {
            //设置简易密码时，开门密码长度为密码长度
            mNumView.init(getString(R.string.set_input_door_pwd), defaultPwdLen);
        }

    }


    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        if (keyBoardBean.getkId().equals(Const.KeyBoardId.KEY_CANCEL)) {
            boolean last = mNumView.removeNum();
            if (last) {
                backMainActivity();
            }
        } else if (keyBoardBean.getkId().equals(Const.KeyBoardId.KEY_LOCK)) {
            if (isIuputAdmin) {
                return;
            }
            if (mNum.length() == 0) {
                isIuputAdmin = mNumView.inputAdminNum();
                if (isIuputAdmin && !isPlayed) {
                    //不使用随机键盘
                    keyBoardView.init(false);
                    keyBoardView.setKeyBoardListener(this);
                    //播放声音
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1305_PATH);
                    isPlayed = true;
                    //隐藏外卖导航
                    rgPwdType.setVisibility(View.INVISIBLE);
                }
            }

            /* 密码长度未满时，按钥匙键需验证密码 */
            if (mNum.length() > 0) {
                if (pwdType == PWD_USER && isSeniorPwd) {
                    //高级密码 房号+密码
                    if (mNum.length() < roomNoLen + 5) {
                        seniorPwd(mNum);
                    }
                } else {
                    //简易密码
                    if (mNum.length() < defaultPwdLen) {
                        easyPwd(mNum);
                    }
                }
            }
        } else {
            if (pwdType == PWD_USER && isSeniorPwd && !isIuputAdmin) {
                //高级模式
                mNumView.addSeniorNum(keyBoardBean.getName(), roomNoLen);
            } else {
                //简易模式
                mNumView.addNum(keyBoardBean.getkId(), NumberView.NUM_TYPE_RESIDENT_PWD_1);
            }
        }
    }

    @Override
    public void getNum(String num) {

        mNum = num;

        LogUtils.e("   PwdOpenFragment getNum ： " + num + " PwdOpenFragment isIuputAdmin ： " + isIuputAdmin);

        if (userInfoDao == null) {
            userInfoDao = new UserInfoDao();
        }
        if (isIuputAdmin) {
            String adminPwd = userInfoDao.getAdminPwd();
            if (num.equals(adminPwd)) {
                //跳转到设置界面
                AppUtils.getInstance().toAct(getContext(), SettingActivity.class);
            } else if (num.length() >= 8) {
                //密码错误
                AppUtils.getInstance().replaceFragment(getActivity(), hintDialogFragment, R.id.fl,
                        "HintDialogFragment", Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_PWD_ERROR_NO_SOUND);
            }
        } else {
            if (pwdType == PWD_USER && isSeniorPwd) {
                //高级密码 房号+密码
                if (num.length() == 5 + roomNoLen) {
                    seniorPwd(num);
                }
            } else {
                //简易密码
                if (num.length() == 6) {
                    easyPwd(num);
                }
            }

        }
    }

    private void easyPwd(String num) {
        String roomNo = null;
        int isOK = SUCCESS_STATE;
        if (num != null && !num.equals("")) {
            roomNo = userInfoDao.getRoomNo(num);
            //不是住户密码时
            if (pwdType != PWD_USER) {
                //根据不同类型设置密码
                passWordClient.DealOtherPassWord(roomNo, num, pwdType);
                return;
            } else {
                //验证密码
                isOK = userInfoDao.verifyPwd(num, false);
            }
        }

        if (isOK == SUCCESS_STATE) {
            UserPwdModels model = userInfoDao.getUserPwdModel(num);
            if (model != null) {
                Constant.ERROR_COUNT = 0;
                passWordClient.DealPassWord(model.getKeyID(), model.getRoomNo(), 0);
               //开门
                OPENDOOR_ROOMNO = model.getRoomNo();
                AppUtils.getInstance().replaceFragment(getActivity(), hintDialogFragment, R.id.fl, "HintDialogFragment",
                        Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_OPNE_DOOR_REMIND);
                if (BuildConfig.isEnabledPwdValid && (model.getLifecycle() > VALID_LIFECYCLE_MODE)){
                    userInfoDao.subLifecycle(num);
                }
            }
        } else {
            if (Constant.ERROR_COUNT == 3) {
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    //梯口机
                    roomNo = "0000";
                } else {
                    //区口机
                    roomNo = fullDeviceNo.getDeviceNo();
                }
//                    LogUtils.e("PwdOpenFragment esay password error===========3次");
                passWordClient.DealPassWord("",roomNo, 1);
                Constant.ERROR_COUNT = -1;
            }
            Constant.ERROR_COUNT++;
            //密码错误
            AppUtils.getInstance().replaceFragment(getActivity(), hintDialogFragment, R.id.fl, "HintDialogFragment",
                    Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_PWD_ERROR);
        }
    }

    private void seniorPwd(String num) {
        //密码长度 = 房号+简易密码
        if (num != null && !num.equals("") && num.length() > roomNoLen && num.length() <= (5 + roomNoLen)) {
            //截取开门密码房号
            final String roomNo = num.substring(0, roomNoLen);
            //截取开门密码后六位的密码
            int strLen = num.length();
            final String pwd = num.substring(roomNoLen, strLen);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    passWordClient.DealAdvPassWord(roomNo, pwd);
                }
            }).start();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rb_resident:
                mNum = "";
                pwdType = PWD_USER;
                if (isSeniorPwd) {
                    //设置高级密码时，开门密码的长度为房号+密码长度
                    mNumView.init(getString(R.string.set_input_door_pwd), roomNoLen + 5);
                } else {
                    //设置简易密码时，开门密码长度为密码长度
                    mNumView.init(getString(R.string.set_input_door_pwd), defaultPwdLen);
                }
                break;
            case R.id.rb_express:
                mNum = "";
                pwdType = PWD_EXPRESS;
                //设置简易密码时，开门密码长度为密码长度
                mNumView.init(getString(R.string.set_input_door_pwd), defaultPwdLen);
                break;
            case R.id.rb_take_out:
                mNum = "";
                pwdType = PWD_WAIMAI;
                //设置简易密码时，开门密码长度为密码长度
                mNumView.init(getString(R.string.set_input_door_pwd), defaultPwdLen);
                break;
            case R.id.rb_other:
                mNum = "";
                pwdType = PWD_OTHER;
                //设置简易密码时，开门密码长度为密码长度
                mNumView.init(getString(R.string.set_input_door_pwd), defaultPwdLen);
                break;
        }
        isIuputAdmin = false;
    }

    @Override
    public void PassWordCmdListener(int param, int param2, String roomNo) {
        LogUtils.w(" PwdOpenFragment param: " + param + " param2: " + param2);
        //param2 文字提示
        switch (param2) {
            //门开了，请进入
            case CommTypeDef.TextHit.OpenDoor_Open_OK:
                OPENDOOR_ROOMNO = roomNo;
                AppUtils.getInstance().replaceFragment(getActivity(), hintDialogFragment, R.id.fl, "HintDialogFragment",
                        Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_OPNE_DOOR_REMIND);
                break;
            //密码错误
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd:
                AppUtils.getInstance().replaceFragment(getActivity(), hintDialogFragment, R.id.fl, "HintDialogFragment",
                        Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_PWD_ERROR);
                break;
            //密码功能未启用
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd_NoUse:
                AppUtils.getInstance().replaceFragment(getActivity(), hintDialogFragment, R.id.fl, "HintDialogFragment",
                        Constant.IntentId.INTENT_KEY, Constant.IntentId.INTENT_PWD_NOT_USER);
                break;
        }
    }

    private void setRbDrawbleSize(RadioButton radioButton, int drawbleId) {
        Drawable drawable = getContext().getResources().getDrawable(drawbleId, null);
        drawable.setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.dp_25), getResources().getDimensionPixelSize(R.dimen.dp_25));
        radioButton.setCompoundDrawables(drawable, null, null, null);
    }
}
