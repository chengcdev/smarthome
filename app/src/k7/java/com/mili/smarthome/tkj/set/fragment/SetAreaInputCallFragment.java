package com.mili.smarthome.tkj.set.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.call.CallHelper;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;

/**
 * 区口号拨号输入界面
 */

public class SetAreaInputCallFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private CustomInputView itBuild;
    private CustomInputView itUnit;
    private CustomInputView itRoomNo;
    private final int BuildId = 1000;
    private final int UnitId = 1001;
    private final int RoomNoId = 1002;
    private int currentId = BuildId;
    private SetSuccessView successView;
    private String TAG = "SetAreaInputCallFragment";
    private FullDeviceNo fullDeviceNo;
    private RelativeLayout Rlbuild;
    private RelativeLayout RlUnitNo;
    //梯口号长度
    private int stairNoLen;
    //是否启用单元号
    private int useCellNo;
    //单元号长度
    private int cellNoLen;
    //房号长度
    private int roomNoLen;
    //楼栋号长度
    private int buildLen;
    //当前楼栋号
    private String currentBuild = "";
    //当前单元号
    private String currentUnit = "";
    //当前房号
    private String currentRoomNo = "";
    private String mNum = "";

    @Override
    public int getLayout() {
        return R.layout.fragment_area_input_calll;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        itBuild = (CustomInputView) getContentView().findViewById(R.id.it_ldh);
        itUnit = (CustomInputView) getContentView().findViewById(R.id.it_unit);
        itRoomNo = (CustomInputView) getContentView().findViewById(R.id.it_room_no);
        RlUnitNo = (RelativeLayout) getContentView().findViewById(R.id.rl_dyh);
        Rlbuild = (RelativeLayout) getContentView().findViewById(R.id.rl_build);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }


    @Override
    public void initAdapter() {

        Constant.ScreenId.SCREEN_IS_SET = true;

        //显示第一次点击数字
        Bundle bundle = getArguments();
        if (bundle != null) {
            mNum = bundle.getString(Constant.KEY_PARAM);
        }

        fullDeviceNo = new FullDeviceNo(getContext());
        tvTitle.setText(getString(R.string.setting_input_room_no));
        stairNoLen = fullDeviceNo.getStairNoLen();
        useCellNo = fullDeviceNo.getUseCellNo();
        cellNoLen = fullDeviceNo.getCellNoLen();
        roomNoLen = fullDeviceNo.getRoomNoLen();
        buildLen = stairNoLen - cellNoLen;
        if (useCellNo == 1 && cellNoLen != 0) {
            //启用单元号
            RlUnitNo.setVisibility(View.VISIBLE);
        } else {
            //不启用单元号
            RlUnitNo.setVisibility(View.GONE);
        }
        if (buildLen == 0) {
            Rlbuild.setVisibility(View.GONE);
        } else {
            Rlbuild.setVisibility(View.VISIBLE);
        }


        if (Rlbuild.getVisibility() == View.VISIBLE) {
            currentId = BuildId;
            itBuild.setFirstFlash(false).init(mNum, buildLen, CustomInputAdapter.INPUT_TYPE_2);
            if (RlUnitNo.getVisibility() == View.VISIBLE) {
                itUnit.setFirstFlash(false).init("", cellNoLen, CustomInputAdapter.INPUT_TYPE_1);
            }
            itRoomNo.setFirstFlash(false).init("", roomNoLen, CustomInputAdapter.INPUT_TYPE_1);
        } else {
            if (RlUnitNo.getVisibility() == View.VISIBLE) {
                currentId = UnitId;
                itUnit.setFirstFlash(false).init(mNum, cellNoLen, CustomInputAdapter.INPUT_TYPE_2);
                itRoomNo.setFirstFlash(false).init("", roomNoLen, CustomInputAdapter.INPUT_TYPE_1);
            } else {
                currentId = RoomNoId;
                itRoomNo.setFirstFlash(false).init(mNum, roomNoLen, CustomInputAdapter.INPUT_TYPE_2);
            }
        }
    }


    @Override
    public void initListener() {
        successView.setSuccessListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Constant.ScreenId.SCREEN_IS_SET = false;
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        if (viewId == Constant.VIEW_ID_KEY_CALL) {
            currentBuild = itBuild.getNum();
            currentUnit = itUnit.getNum();
            currentRoomNo = itRoomNo.getNum();
            String callNum = currentBuild + currentUnit + currentRoomNo;
            if (buildLen + cellNoLen + roomNoLen == callNum.length()) {
                //呼叫住户
                CallHelper.getInstance().callResident(getContext(), callNum);
            }
        } else {
            setKeyBoardState(keyId);
        }
    }


    private void setKeyBoardState(String kid) {
        switch (kid) {
            case Constant.KEY_CANCLE:
                switch (currentId) {
                    case BuildId:
                        if (itBuild.getCount() <= 0) {
                            //跳转到开门密码界面
                            Intent intent = new Intent(Constant.ActionId.ACTION_AREA_TO_ROOM_NO);
                            mContext.sendBroadcast(intent);
                            return;
                        }
                        itBuild.deleteNum("");
                        break;
                    case UnitId:
                        if (itUnit.getCount() <= 0) {
                            if (Rlbuild.getVisibility() == View.VISIBLE) {
                                currentId = BuildId;
                                itBuild.deleteNum("");
                            }
                        }
                        itUnit.deleteNum("");
                        break;
                    case RoomNoId:
                        if (itRoomNo.getCount() <= 0) {
                            if (RlUnitNo.getVisibility() == View.VISIBLE) {
                                currentId = UnitId;
                                itUnit.deleteNum("");
                            } else if (Rlbuild.getVisibility() == View.VISIBLE) {
                                currentId = BuildId;
                                itBuild.deleteNum("");
                            }
                        }
                        itRoomNo.deleteNum("");
                        break;
                }
                break;
            //确定
            case Constant.KEY_LOCK:
                //跳转到开门密码界面
                Intent intent = new Intent(Constant.ActionId.ACTION_AREA_TO_OPEN_PWD);
                mContext.sendBroadcast(intent);
                break;
            default:
                switch (currentId) {
                    case BuildId:
                        if (itBuild.getCount() == buildLen - 1) {
                            if (RlUnitNo.getVisibility() == View.VISIBLE) {
                                currentId = UnitId;
                                itUnit.setFirstFlash(true);
                                itBuild.setEndFlash(false);
                                itUnit.notifychange();
                            } else {
                                currentId = RoomNoId;
                                itRoomNo.setFirstFlash(true);
                                itBuild.setEndFlash(false);
                                itRoomNo.notifychange();
                            }

                        }
                        itBuild.addNum(kid);
                        break;
                    case UnitId:
                        if (itUnit.getCount() == cellNoLen - 1) {
                            currentId = RoomNoId;
                            itRoomNo.setFirstFlash(true);
                            itUnit.setEndFlash(false);
                            itRoomNo.notifychange();
                        }
                        itUnit.addNum(kid);
                        break;
                    case RoomNoId:
                        itRoomNo.setEndFlash(false);
                        itRoomNo.addNum(kid);
                        break;
                }
                break;
        }
    }


    @Override
    public void success() {
    }

    @Override
    public void fail() {

    }

}
