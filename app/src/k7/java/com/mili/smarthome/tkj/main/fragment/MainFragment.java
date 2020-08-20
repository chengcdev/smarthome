package com.mili.smarthome.tkj.main.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.CommStorePathDef;
import com.android.CommTypeDef;
import com.android.InterCommTypeDef;
import com.android.client.PassWordClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.userInfo.UserPwdModels;
import com.mili.smarthome.tkj.main.activity.direct.DirectPressLetterActivity;
import com.mili.smarthome.tkj.main.entity.HintBean;
import com.mili.smarthome.tkj.main.entity.InputBean;
import com.mili.smarthome.tkj.main.face.activity.WffrFaceRecogActivity;
import com.mili.smarthome.tkj.main.manage.HintEventManage;
import com.mili.smarthome.tkj.main.manage.KeyBoardEventManage;
import com.mili.smarthome.tkj.main.manage.MessageManage;
import com.mili.smarthome.tkj.main.qrcode.CaptureActivity;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.activity.CallCenterActivity;
import com.mili.smarthome.tkj.set.call.CallHelper;
import com.mili.smarthome.tkj.set.fragment.SetAreaInputCallFragment;
import com.mili.smarthome.tkj.set.fragment.SettingFragment;
import com.mili.smarthome.tkj.set.widget.NumView;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import java.util.Objects;

import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;
import static com.mili.smarthome.tkj.set.Constant.OPENDOOR_ROOMNO;


/**
 * 主界面
 */

public class MainFragment extends BaseKeyBoardFragment implements InterCommTypeDef.PassWordCmddListener {

    private ImageView imgTitle;
    private RelativeLayout rlNet;
    public NumView numview;
    private FullDeviceNo fullDeviceNo;
    public int adminCount; // 为5时进入管理员密码
    private String TAG = "MainFragment";
    private UserInfoDao userInfoDao;
    private PassWordClient passWordClient;
    private MainReceiver mainReceiver;
    private static final int TO_FACE_ACT = -1; //跳转到人脸
    private boolean isDirect;  //是否直按式
    private int mInputType;


    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void initView() {
        imgTitle = (ImageView) getContentView().findViewById(R.id.img_title);
        rlNet = (RelativeLayout) getContentView().findViewById(R.id.rl_net);
        numview = (NumView) getContentView().findViewById(R.id.numview);
        registerReceiver();
    }

    private void registerReceiver() {
        mainReceiver = new MainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ActionId.ACTION_FACE_TO_OPEN);
        filter.addAction(Constant.ActionId.ACTION_MAIN_FRAGMENT_NOTIFY);
        Objects.requireNonNull(getContext()).registerReceiver(mainReceiver, filter);
    }

    private void unRegisterReceiver() {
        if (mainReceiver != null) {
            Objects.requireNonNull(getContext()).unregisterReceiver(mainReceiver);
        }
    }


    @Override
    public void initAdapter() {
        if (fullDeviceNo == null) {
            fullDeviceNo = new FullDeviceNo(getContext());
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            InputBean inputBean = (InputBean) bundle.getSerializable(Constant.KEY_PARAM);
            if (inputBean != null) {
                mInputType = inputBean.getInputType();
                if (mInputType == KeyBoardEventManage.INPUT_TYPE_OPEN_PWD_DIRECT) {
                    isDirect = true;
                    setOpenPwd(false);
                } else if (mInputType == KeyBoardEventManage.INPUT_TYPE_OPEN_PWD) {
                    isDirect = false;
                    adminCount = 1;
                    setOpenPwd(false);
                } else if (mInputType == KeyBoardEventManage.INPUT_TYPE_ROOM_NO) {
                    isDirect = false;
                    setDevNo();
                } else if (mInputType == KeyBoardEventManage.INPUT_TYPE_ROOM_NO_SHOW) {
                    isDirect = false;
                    String inputKey = inputBean.getInputKey();
                    if (Constant.ACTION_KEY_CALL.equals(inputKey)) {
                        //呼叫中心
                        AppManage.getInstance().toAct(CallCenterActivity.class);
                    } else {
                        if (Constant.KEY_LOCK.equals(inputKey)) {
                            numview.notifyNumView(getOpenPwdLen(), true);
                            click(inputKey);
                        } else {
                            numview.notifyNumView(getRoomLen(), false);
                            adminCount = 0;
                            //按键
                            clickKeyNumEvent(inputKey);
                        }

                    }
                } else {
                    isDirect = false;
                    setDevNo();
                }
            } else {
                setDevNo();
            }
        } else {
            setDevNo();
        }
        userInfoDao = new UserInfoDao();
    }


    @Override
    public void initListener() {
        //密码回调监听
        if (passWordClient == null) {
            passWordClient = new PassWordClient(getContext());
        }
        passWordClient.setPassWordDataCallBKListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        AppManage.getInstance().frgCurrent = this;
        Constant.ScreenId.SCREEN_IS_SET = false;
        if (mInputType != KeyBoardEventManage.INPUT_TYPE_ROOM_NO_SHOW &&
                numview != null && !numview.getNum().equals("")) {
            //刷新主界面
            setDevNo();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unRegisterReceiver();
    }


    /**
     * 房号状态
     */
    private void setDevNo() {
        rlNet.setVisibility(View.VISIBLE);
        imgTitle.setVisibility(View.VISIBLE);
        numview.setVisibility(View.GONE);
        numview.notifyNumView(getRoomLen(), false);
        adminCount = 0;
        KeyBoardEventManage.getInstance().notifyKeyBoard(KeyBoardEventManage.INPUT_TYPE_MAIN);
        if (AppConfig.getInstance().getCallType() == 0 && (AppConfig.getInstance().getQrScanEnabled() == 1 || !AppConfig.getInstance().getBluetoothDevId().equals(""))) {
            //编码式启用扫码开门
            AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.main_num_qcn, R.drawable.main_num_qtw, R.drawable.main_num_qen);
            //信息初始化
            MessageManage.getInstance().initMessage();
        } else {
            //直按式
            AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.main_num_cn, R.drawable.main_num_tw, R.drawable.main_num_en);
        }
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            //是否跳转到人脸
            adminCount = TO_FACE_ACT;
        }
    }

    /**
     * 管理员密码状态
     */
    private void setAdminPwd() {
//        LogUtils.w(TAG + " setAdminPwd ");
        rlNet.setVisibility(View.GONE);
        imgTitle.setVisibility(View.VISIBLE);
        numview.setVisibility(View.GONE);
        AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.input_adminpwd_cn, R.drawable.input_adminpwd_tw, R.drawable.input_adminpwd_en);
        numview.notifyNumView(8, true);
        KeyBoardEventManage.getInstance().notifyKeyBoard(KeyBoardEventManage.INPUT_TYPE_ADMIN_PWD);
    }

    /**
     * 开门密码状态
     *
     * @param isClickCancle 是否点击了取消
     */
    private void setOpenPwd(boolean isClickCancle) {
//        LogUtils.w(TAG + " setOpenPwd ");
        rlNet.setVisibility(View.GONE);
        imgTitle.setVisibility(View.VISIBLE);
        numview.setVisibility(View.GONE);
        numview.notifyNumView(getOpenPwdLen(), true);
        if (isDirect) {
            adminCount = 1;
            AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.top_search_1_cn, R.drawable.top_search_1_tw, R.drawable.top_search_1_en);
            KeyBoardEventManage.getInstance().notifyKeyBoard(KeyBoardEventManage.INPUT_TYPE_OPEN_PWD_DIRECT);
        } else {
            AppManage.getInstance().setTopImgBg(imgTitle, R.drawable.input_pwd_cn, R.drawable.input_pwd_tw, R.drawable.input_pwd_en);
            if (!isClickCancle) {
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1301_PATH);
                //更新键盘按键，是否启用了动态键盘
                KeyBoardEventManage.getInstance().notifyKeyBoard(KeyBoardEventManage.INPUT_TYPE_OPEN_PWD);
            }
        }
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (viewId) {
            case Constant.VIEW_ID_KEY_CALL:
                clickCallEvent();
                break;
            case Constant.VIEW_ID_KEY_BOARD:
                click(keyId);
                break;
        }
    }

    public void click(String kid) {
        switch (kid) {
            case Constant.KEY_CANCLE:
                clickKeyCancelEvent();
                break;
            case Constant.KEY_LOCK:
                clickKeyLockEevent();
                break;
            case Constant.KEY_QR:
                AppManage.getInstance().toAct(CaptureActivity.class);
                break;
            case Constant.KEY_LIST:
                //跳转到输入字母键盘界面
                AppManage.getInstance().toActFinish(getActivity(), DirectPressLetterActivity.class);
                break;
            case Constant.KEY_UP:
                break;
            case Constant.KEY_NEXT:
                break;
            default:
                //按键
                clickKeyNumEvent(kid);
                break;
        }
    }

    private void clickCallEvent() {
        String num = numview.getNum();
        if (adminCount <= 0) {
            if (!num.equals("")) {
                if (num.length() > 2) {
                    //呼叫住户
                    CallHelper.getInstance().callResident(getContext(), numview.getNum());
                }
            } else {
                //呼叫中心
                AppManage.getInstance().toAct(CallCenterActivity.class);
            }
        } else if (adminCount < 6) {
            if (isSeniorPwdModule()) {
                //验证高级模式
                if (!num.equals("") && num.length() == getOpenPwdLen()) {
                    verifySeniorPwd();
                } else {
                    showErrorDialog(false);
                }
            } else {
                //验证简易模式
                verifyEasyPwd();
            }
        } else {
            //验证管理员密码
            verifyAdminPwd();
        }
    }

    private void clickKeyCancelEvent() {
        if (imgTitle.getVisibility() == View.VISIBLE) {
            if (isDirect) {
                getActivity().finish();
            } else {
                setDevNo();
            }
        } else {
            numview.backNum();
            if (numview.getNum().equals("")) {
                if (adminCount == 1) {
                    setOpenPwd(true);
                } else if (adminCount >= 6) {
                    setAdminPwd();
                } else {
                    if (isDirect && adminCount > 1) {
                        setOpenPwd(true);
                    } else {
                        setDevNo();
                    }
                }
            }
        }
    }

    private void clickKeyNumEvent(String kid) {
        imgTitle.setVisibility(View.GONE);
        numview.setVisibility(View.VISIBLE);
        if (adminCount <= 0 && numview.getNum().equals("")) {
            //请输入房号
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1201_PATH);
        }
        if (adminCount >= 1 && adminCount < 6) {
            if (isSeniorPwdModule()) {
                //高级模式
                seniorPwd(kid);
            } else {
                //简易模式
                easyPwd(kid);
            }
        } else if (adminCount >= 6) {
            //管理员密码
            adminPwd(kid);
        } else {
            KeyBoardEventManage.getInstance().notifyKeyBoard(KeyBoardEventManage.INPUT_TYPE_ROOM_NO);
            if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
                //区口机跳转到区口拨号界面
                toAreaCallFrag(kid);
            } else {
                numview.inputNum(kid, NumView.INPUT_TYP_NUM, false);
            }
        }
    }

    private void clickKeyLockEevent() {
        boolean isPlaySound = false;
        if (adminCount == TO_FACE_ACT) {
            AppManage.getInstance().toAct(WffrFaceRecogActivity.class);
        } else {
            if (!numview.getNum().equals("") && adminCount >= 1 && adminCount < 6) {
                isPlaySound = adminCount == 1;
                adminCount = 0;
            }
            adminCount++;
            if (adminCount == 1) {
                if (isDirect) {
                    //验证开门密码
                    if (isSeniorPwdModule()) {
                        //验证高级模式
                        if (!numview.getNum().equals("") && numview.getNum().length() == getOpenPwdLen()) {
                            verifySeniorPwd();
                        } else {
                            showErrorDialog(false);
                        }
                    } else {
                        //验证简易模式
                        verifyEasyPwd();
                    }
                } else {
                    //请输入开门密码
                    setOpenPwd(isPlaySound);
                }
            } else if (adminCount == 6) {
                //请输入管理密码
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1305_PATH);
                adminCount = 6;
                setAdminPwd();
            }
        }
    }

    private void adminPwd(String kid) {
        numview.inputNum(kid, NumView.INPUT_TYP_CHAR, false);
        if (numview.getNum().length() == 8) {
            verifyAdminPwd();
        }
    }

    private void verifyAdminPwd() {
        if (numview.getNum().equals(userInfoDao.getAdminPwd())) {
            KeyBoardEventManage.getInstance().notifyKeyBoard(KeyBoardEventManage.INPUT_TYPE_SET);
            //进入设置界面
            AppManage.getInstance().replaceFragment(getActivity(), new SettingFragment());
            Constant.ScreenId.SCREEN_IS_SET = true;
        } else {
            //错误提示界面
            showErrorDialog(true);
        }
    }

    private void easyPwd(String kid) {
        numview.inputNum(kid, NumView.INPUT_TYP_CHAR, false);
        if (numview.getNum().length() == 6) {
            verifyEasyPwd();
        }
    }

    private void verifyEasyPwd() {
        int result = userInfoDao.verifyPwd(numview.getNum(), false);
        if (result == SUCCESS_STATE) {
            UserPwdModels model = userInfoDao.getUserPwdModel(numview.getNum());
            if (model != null) {
                Constant.ERROR_PWD_COUNT = 0;
                passWordClient.DealPassWord(model.getKeyID(), model.getRoomNo(), 0);
                showSuccessDialog(model.getRoomNo());
                if (BuildConfig.isEnabledPwdValid && (model.getLifecycle() > VALID_LIFECYCLE_MODE)){
                    userInfoDao.subLifecycle(numview.getNum());
                }
            }
        } else {
            //错误提示界面
            Constant.ERROR_PWD_COUNT++;
            LogUtils.w(TAG + " verifyEasyPwd errorPwdCount: " + Constant.ERROR_PWD_COUNT);
            if (Constant.ERROR_PWD_COUNT > 3) {
                String roomNo;
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    roomNo = "0000"; // 梯口机
                } else {
                    roomNo = fullDeviceNo.getDeviceNo(); // 区口机
                }
                passWordClient.DealPassWord("", roomNo, 1);
                Constant.ERROR_PWD_COUNT = 0;
            }
            showErrorDialog(false);
        }
    }

    private void seniorPwd(String kid) {
        String num = numview.getNum();
        if (num.length() >= getRoomLen()) {
            numview.inputNum(kid, NumView.INPUT_TYP_CHAR, true);
        } else {
            numview.inputNum(kid, NumView.INPUT_TYP_NUM, true);
        }
        verifySeniorPwd();
    }

    private void verifySeniorPwd() {
        //密码长度 = 房号+简易密码
        String num = numview.getNum();
        if (!num.equals("") && num.length() == getOpenPwdLen()) {
            //截取开门密码房号
            final String roomNo = num.substring(0, getRoomLen());
            //截取开门密码后5位的密码
            int strLen = num.length();
            final String pwd = num.substring(getRoomLen(), strLen);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    passWordClient.DealAdvPassWord(roomNo, pwd);
                }
            }).start();
        }
    }

    /**
     * 是否高级密码模式
     */
    public boolean isSeniorPwdModule() {
        return AppConfig.getInstance().getOpenPwdMode() != 0;
    }

    /**
     * 开门密码长度
     */
    public int getOpenPwdLen() {
        if (isSeniorPwdModule()) {
            //高级模式
            return getRoomLen() + 5;
        } else {
            //简易模式
            return 6;
        }
    }

    /**
     * 房号长度
     */
    public int getRoomLen() {
        if (fullDeviceNo == null) {
            fullDeviceNo = new FullDeviceNo(getContext());
        }

        //密码长度 = 房号+简易密码
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            //区口机 房号长度 = 楼栋号+单元号+房号长度
            //梯号长度
            int stairNoLen = fullDeviceNo.getStairNoLen();
            //单元号长度
            int cellNoLen = fullDeviceNo.getCellNoLen();
            //楼栋号长度
            int buildNoLen = stairNoLen - cellNoLen;
            return stairNoLen + cellNoLen + buildNoLen;
        } else {
            return fullDeviceNo.getRoomNoLen();
        }
    }

    private void showErrorDialog(boolean isAdminPwd) {
        //显示密码错误
        HintBean hintBean = new HintBean(Constant.KEY_PWD_FAIL);
        hintBean.setAdminPwd(isAdminPwd);
        HintEventManage.getInstance().toHintAct(getContext(), hintBean);
    }

    private void showSuccessDialog(String roomNo) {
        //显示密码成功
        OPENDOOR_ROOMNO = roomNo;
        HintBean hintBean = new HintBean(Constant.OPEN_DOOR_REMIND);
        HintEventManage.getInstance().toHintAct(getContext(), hintBean);
    }

    @Override
    public void PassWordCmdListener(int param, int param2, String roomNo) {
        //param2 文字提示
        switch (param2) {
            //门开了，请进入
            case CommTypeDef.TextHit.OpenDoor_Open_OK:
                showSuccessDialog(roomNo);
                break;
            //密码错误
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd:
                showErrorDialog(false);
                break;
            //密码功能未启用
            case CommTypeDef.TextHit.OpenDoor_Err_Pwd_NoUse:
                showErrorDialog(false);
                break;
        }
    }

    class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case Constant.ActionId.ACTION_FACE_TO_OPEN:
                    //请输入开门密码
                    adminCount = 1;
                    setOpenPwd(false);
                    break;
                case Constant.ActionId.ACTION_MAIN_FRAGMENT_NOTIFY:
                    setDevNo();
                    break;
            }
        }
    }

    public void toAreaCallFrag(String kid) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_PARAM, kid);
        SetAreaInputCallFragment setAreaInputCallFragment = new SetAreaInputCallFragment();
        AppManage.getInstance().replaceFragment(getActivity(), setAreaInputCallFragment, bundle);
    }

}