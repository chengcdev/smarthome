package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.main.entity.RoomNoHelper;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;

/**
 * 添加密码
 * 删除密码
 * 清空密码
 * 修改管理密码
 */

public class PwdontrolFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private final int roomNoId = 1000;
    private final int pwdNoId = 1001;
    private int currentId = pwdNoId;
    private SetSuccessView successView;
    private String TAG = "PwdontrolFragment";
    private FullDeviceNo fullDeviceNo;
    private CustomInputView itRoomNo;
    private CustomInputView itPwdNo;
    private int roomNoLen = 4;
    private int pwdNoLen = 6;
    private String currentPwdNo = "";
    private String currentRoomNo = "";
    private LinearLayout linClear;
    private RelativeLayout linInput;
    private RelativeLayout rlPwd;
    private TextView tvRoomNo;
    private TextView tvPwd;
    private String extra;
    private int stairNoLen;
    private UserInfoDao userInfoDao;
    private RoomNoHelper mRoomNoHelper;


    @Override
    public int getLayout() {
        return R.layout.fragment_setting_pwd_control;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        itRoomNo = (CustomInputView) getContentView().findViewById(R.id.it_room_no);
        itPwdNo = (CustomInputView) getContentView().findViewById(R.id.it_card_no);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
        linClear = (LinearLayout) getContentView().findViewById(R.id.lin_clear);
        linInput = (RelativeLayout) getContentView().findViewById(R.id.lin_input);
        rlPwd = (RelativeLayout) getContentView().findViewById(R.id.rl_pwd);
        tvRoomNo = (TextView) getContentView().findViewById(R.id.tv_room_no);
        tvPwd = (TextView) getContentView().findViewById(R.id.tv_pwd);
    }

    @Override
    public void initAdapter() {
        tvTitle.setText(getString(R.string.setting_rule_set));
        fullDeviceNo = new FullDeviceNo(getContext());

        if (userInfoDao == null) {
            userInfoDao = new UserInfoDao();
        }

        //房号长度
        roomNoLen = fullDeviceNo.getRoomNoLen();
        //梯号长度
        stairNoLen = fullDeviceNo.getStairNoLen();

        if (CommTypeDef.DeviceType.DEVICE_TYPE_AREA == fullDeviceNo.getDeviceType()) {
            //如果是区口，编号长度=房号长度+梯口号长度
            tvRoomNo.setText(getString(R.string.setting_number));
            roomNoLen = roomNoLen + stairNoLen;
        } else {
            tvRoomNo.setText(getString(R.string.setting_card_room_no));
        }
    }


    @Override
    public void initListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        successView.setSuccessListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            extra = bundle.getString(Constant.KEY_PARAM);
            //设置房号
            initRoomNo(true);
            showList();
        }
    }

    void showList() {
        switch (extra) {
            //添加密码
            case Constant.SetPwdManageId.PWD_ADD:
                initPwdAdd(currentRoomNo, currentPwdNo);
                break;
            //删除密码
            case Constant.SetPwdManageId.PWD_DELETE:
                initPwdDeleteView();
                //删除密码
                itRoomNo.setFirstFlash(true).init("", roomNoLen, CustomInputAdapter.INPUT_TYPE_1);
                currentId = roomNoId;
                break;
            //清空密码
            case Constant.SetPwdManageId.PWD_CLEAR:
                initPwdClear();
                break;
            //修改管理密码
            case Constant.SetPwdManageId.PWD_UPDATE:
                initPwdUpdate();
                break;
        }
    }

    public void initPwdUpdate() {
        tvTitle.setText(getString(R.string.setting_pwd_update));
        linClear.setVisibility(View.GONE);
        linInput.setVisibility(View.VISIBLE);
        tvRoomNo.setGravity(Gravity.LEFT);
        tvPwd.setGravity(Gravity.LEFT);
        tvRoomNo.setText(getString(R.string.setting_pwd_new));
        tvPwd.setText(getString(R.string.setting_pwd_new_again));

        itRoomNo.setFirstFlash(true).init("", 8, CustomInputAdapter.INPUT_TYPE_1);
        itPwdNo.setFirstFlash(false).init("", 8, CustomInputAdapter.INPUT_TYPE_1);
        currentId = roomNoId;
    }

    public void initPwdClear() {
        tvTitle.setText(getString(R.string.setting_pwd_del_all));
        linClear.setVisibility(View.VISIBLE);
        linInput.setVisibility(View.GONE);
    }

    public void initPwdDeleteView() {
        tvTitle.setText(getString(R.string.setting_pwd_delete));
        linClear.setVisibility(View.GONE);
        linInput.setVisibility(View.VISIBLE);
        rlPwd.setVisibility(View.GONE);
    }

    public void initPwdAdd(String currentRoomNo, String currentPwdNo) {
        tvTitle.setText(getString(R.string.setting_pwd_add));
        linClear.setVisibility(View.GONE);
        linInput.setVisibility(View.VISIBLE);
        editInputView(0, currentRoomNo, currentPwdNo);
    }

    private void editInputView(int type, String currentRoomNo, String currentPwdNo) {
        if (type == 0) {
            //添加密码
            itRoomNo.setFirstFlash(false).setEndFlash(false).init(currentRoomNo, roomNoLen, CustomInputAdapter.INPUT_TYPE_1);
            itRoomNo.setCount(roomNoLen);
            itPwdNo.setFirstFlash(true).setEndFlash(false).init(currentPwdNo, pwdNoLen, CustomInputAdapter.INPUT_TYPE_1);
            currentId = pwdNoId;
        } else {
            if (currentRoomNo.equals("")) {
                //删除密码
                itRoomNo.setCount(0);
                itRoomNo.setFirstFlash(true).setEndFlash(false).init("", roomNoLen, CustomInputAdapter.INPUT_TYPE_1);
                currentId = roomNoId;
            } else {
                itRoomNo.setText(currentRoomNo);
                if (rlPwd.getVisibility() == View.VISIBLE && currentPwdNo.equals("")) {
                    itPwdNo.setCount(0);
                    itPwdNo.setFirstFlash(true).setEndFlash(false).notifychange();
                }
            }
        }

    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //退出界面
                exitFragment(this);
                break;
            case Constant.KEY_CONFIRM:
                switch (extra) {
                    case Constant.SetPwdManageId.PWD_UPDATE:
                        currentRoomNo = itRoomNo.getNum();
                        currentPwdNo = itPwdNo.getNum();
                        if (currentRoomNo.length() < 7 || currentPwdNo.length() < 7) {
                            return;
                        }
                        if (currentRoomNo.equals(currentPwdNo)) {
                            successView.showSuccessView(getString(R.string.setting_success));
                        } else {
                            successView.showSuccessView(getString(R.string.setting_pwd_error));
                        }
                        break;
                    case Constant.SetPwdManageId.PWD_CLEAR:
                        successView.showSuccessView(getString(R.string.setting_success));
                        break;
                    //添加密码
                    case Constant.SetPwdManageId.PWD_ADD:
                        currentRoomNo = itRoomNo.getNum();
                        currentPwdNo = itPwdNo.getNum();
                        if (currentRoomNo.length() < roomNoLen - 1 || currentPwdNo.length() < 6) {
                            return;
                        }
                        successView.showSuccessView(getString(R.string.setting_success));
                        break;
                    //删除密码
                    case Constant.SetPwdManageId.PWD_DELETE:
                        currentRoomNo = itRoomNo.getNum();
                        if (currentRoomNo.length() < roomNoLen - 1) {
                            return;
                        }
                        successView.showSuccessView(getString(R.string.setting_success));
                        break;
                }
                break;
            case Constant.KEY_UP:
                //如果房号长度是4，获取住户列表
                changeRoomNo(Constant.KEY_UP);
                break;
            case Constant.KEY_DELETE:
                backNum();
                break;
            case Constant.KEY_NEXT:
                //如果房号长度是4，获取住户列表
                changeRoomNo(Constant.KEY_NEXT);
                break;
            default:
                inputNum(keyId);
                break;
        }
    }

    private void changeRoomNo(String keyId) {
        //如果是修改管理密码和清空所有密码，不做操作
        if (Constant.SetPwdManageId.PWD_UPDATE.equals(extra) || Constant.SetPwdManageId.PWD_CLEAR.equals(extra)) {
            return;
        }
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
            if (keyId.equals(Constant.KEY_UP)) {
                currentRoomNo = mRoomNoHelper.getPreviousRoomNo();
            }else {
                currentRoomNo = mRoomNoHelper.getNextRoomNo();
            }
            itRoomNo.setText(currentRoomNo);
            if (rlPwd.getVisibility() == View.VISIBLE && itPwdNo.getNum().equals("")) {
                itPwdNo.setFirstFlash(true).setEndFlash(false).notifychange();
                itPwdNo.setCount(0);
                currentId = pwdNoId;
            }
        }
    }

    private void backNum() {
        if (Constant.SetPwdManageId.PWD_CLEAR.equals(extra)) {
            return;
        }
        if (Constant.SetPwdManageId.PWD_UPDATE.equals(extra)) {
            switch (currentId) {
                case roomNoId:
                    if (itRoomNo.getCount() != 0) {
                        itRoomNo.deleteNum("");
                    }
                    currentRoomNo = itRoomNo.getNum();
                    break;
                case pwdNoId:
                    if (itPwdNo.getCount() == 0) {
                        itRoomNo.deleteNum("");
                        itPwdNo.setEndFlash(false);
                        currentId = roomNoId;
                    }
                    itPwdNo.deleteNum("");
                    currentPwdNo = itPwdNo.getNum();
                    break;
            }
        } else {
            switch (currentId) {
                case roomNoId:
                    if (itRoomNo.getCount() != 0) {
                        itRoomNo.deleteNum("");
                    }
                    currentRoomNo = itRoomNo.getNum();
                    break;
                case pwdNoId:
                    if (itPwdNo.getCount() == 0 && itRoomNo.getNum().length() == roomNoLen) {
                        itRoomNo.deleteNum("");
                        itPwdNo.setEndFlash(false);
                        currentId = roomNoId;
                    }
                    itPwdNo.deleteNum("");
                    currentPwdNo = itPwdNo.getNum();
                    break;
            }
        }
    }

    void inputNum(String kid) {
        if (Constant.SetPwdManageId.PWD_CLEAR.equals(extra)) {
            return;
        }
        //修改密码显示*号
        if (extra.equals(Constant.SetPwdManageId.PWD_UPDATE)) {
            switch (currentId) {
                case roomNoId:
                    if (itRoomNo.getCount() == 7) {
                        itPwdNo.setFirstFlash(true);
                        itRoomNo.setEndFlash(false);
                        itPwdNo.setCount(0);
                        itPwdNo.notifychange();
                        currentId = pwdNoId;
                    }
                    itRoomNo.addNum(kid, true);
                    currentRoomNo = itRoomNo.getNum();
                    break;
                case pwdNoId:
                    itPwdNo.setEndFlash(false);
                    itPwdNo.addNum(kid, true);
                    currentPwdNo = itPwdNo.getNum();
                    break;
            }
        } else {
            switch (currentId) {
                case roomNoId:
                    if (!extra.equals(Constant.SetPwdManageId.PWD_DELETE) && itRoomNo.getCount() == roomNoLen - 1) {
                        itPwdNo.setFirstFlash(true);
                        itRoomNo.setEndFlash(false);
                        itPwdNo.setCount(0);
                        itPwdNo.notifychange();
                        currentId = pwdNoId;
                    }
                    if (rlPwd.getVisibility() == View.GONE) {
                        itRoomNo.setEndFlash(false);
                        currentId = roomNoId;
                    }
                    itRoomNo.addNum(kid);
                    currentRoomNo = itRoomNo.getNum();
                    break;
                case pwdNoId:
                    itPwdNo.addNum(kid);
                    itPwdNo.setEndFlash(false);
                    currentPwdNo = itPwdNo.getNum();
                    break;
            }
        }

    }

    @Override
    public void success() {
        //保存数据
        switch (extra) {
            //添加密码
            case Constant.SetPwdManageId.PWD_ADD:
                currentRoomNo = itRoomNo.getNum();
                currentPwdNo = itPwdNo.getNum();
                userInfoDao.addOpenPwd(currentRoomNo, currentPwdNo);
                initRoomNo(false);
                initPwdAdd(currentRoomNo, "");
                break;
            //删除密码
            case Constant.SetPwdManageId.PWD_DELETE:
                currentRoomNo = itRoomNo.getNum();
                userInfoDao.deleteOpenPwd(currentRoomNo);
                initRoomNo(false);
                initPwdDelete(currentRoomNo);
                break;
            //清空密码
            case Constant.SetPwdManageId.PWD_CLEAR:
                userInfoDao.clearAllOpenPwd();
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFragment(PwdontrolFragment.this);
                    }
                }, 2000);
                break;
            //修改管理密码
            case Constant.SetPwdManageId.PWD_UPDATE:
                currentPwdNo = itPwdNo.getNum();
                userInfoDao.setAdminPwd(currentPwdNo);
                initPwdUpdate();
                break;
        }
    }

    private void initPwdDelete(String currentRoomNo) {
        editInputView(1, currentRoomNo, "");
    }

    @Override
    public void fail() {
        if (Constant.SetPwdManageId.PWD_UPDATE.equals(extra)) {
            initPwdUpdate();
        }
    }

    private void initRoomNo(boolean isInit) {
        if (CommTypeDef.DeviceType.DEVICE_TYPE_STAIR == fullDeviceNo.getDeviceType() && roomNoLen == 4) {
            if (mRoomNoHelper == null) {
                mRoomNoHelper = new RoomNoHelper();
            }
            //设置列表计数
            if (!currentRoomNo.equals(mRoomNoHelper.getCurrentRoomNo())) {
                mRoomNoHelper.reset();
            }
            if (!isInit) {
                currentRoomNo = mRoomNoHelper.getNextRoomNo();
            } else {
                currentRoomNo = mRoomNoHelper.getCurrentRoomNo();
            }
        } else {
            if (Constant.SetPwdManageId.PWD_ADD.equals(extra)) {
                currentRoomNo = "";
                for (int i = 0; i < roomNoLen; i++) {
                    currentRoomNo = "0" + currentRoomNo;
                }
            } else {
                currentRoomNo = "";
            }
        }

    }

}
