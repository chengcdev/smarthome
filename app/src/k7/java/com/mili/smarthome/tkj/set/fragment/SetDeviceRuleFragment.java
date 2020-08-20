package com.mili.smarthome.tkj.set.fragment;

import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;
import com.mili.smarthome.tkj.utils.AppManage;

/**
 * 梯口编号规则设置
 */

public class SetDeviceRuleFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private final int tkhLenId = 1000;
    private final int roomLenId = 1001;
    private final int unitLenId = 1002;
    private int currentId = tkhLenId;
    private SetSuccessView successView;
    private String TAG = "SetDeviceRuleFragment";
    private byte tkhLenContent = 4;
    private byte roomLenContent = 4;
    private byte unitLenContent = 2;
    private CustomInputView inputTkhLen;
    private CustomInputView inputRoomLen;
    private CustomInputView inputUnitLen;
    private FullDeviceNo fullDeviceNo;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting_device_rule;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        inputTkhLen = (CustomInputView) getContentView().findViewById(R.id.it_tkh_len);
        inputRoomLen = (CustomInputView) getContentView().findViewById(R.id.it_device_len);
        inputUnitLen = (CustomInputView) getContentView().findViewById(R.id.it_unit_len);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }


    @Override
    public void initAdapter() {

        fullDeviceNo = new FullDeviceNo(getContext());
        tvTitle.setText(getString(R.string.setting_rule_set));

        tkhLenContent = fullDeviceNo.getStairNoLen();
        roomLenContent = fullDeviceNo.getRoomNoLen();
        unitLenContent = fullDeviceNo.getCellNoLen();

        inputTkhLen.setFirstFlash(true).init(String.valueOf(tkhLenContent), 1, CustomInputAdapter.INPUT_TYPE_OTHER);
        inputRoomLen.setFirstFlash(false).init(String.valueOf(roomLenContent), 1, CustomInputAdapter.INPUT_TYPE_OTHER);
        inputUnitLen.setFirstFlash(false).init(String.valueOf(unitLenContent), 1, CustomInputAdapter.INPUT_TYPE_OTHER);
    }


    @Override
    public void initListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        successView.setSuccessListener(this);
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //退出界面
                exitFragment(this);
                break;
            case Constant.KEY_CONFIRM:

                tkhLenContent = Byte.parseByte(inputTkhLen.getNum());
                roomLenContent = Byte.parseByte(inputRoomLen.getNum());
                unitLenContent = Byte.parseByte(inputUnitLen.getNum());

                if (isCheckDeviceNo() == 0) {
                    //保存
                    saveDatas();
                    //设置成功
                    successView.showSuccessView(getString(R.string.setting_success), 1000, this);
                } else {
                    //设置失败
                    successView.showSuccessView(getString(R.string.setting_fail), 1000, this);
                }
                break;
            case Constant.KEY_UP:

                break;
            case Constant.KEY_DELETE:
                switch (currentId) {
                    case tkhLenId:
                        inputTkhLen.deleteNum("0");
                        inputTkhLen.setFirstFlash(true).notifychange();
                        break;
                    case roomLenId:
                        inputTkhLen.setFirstFlash(true).notifychange();
                        inputRoomLen.setFirstFlash(false);
                        inputRoomLen.deleteNum("0");
                        currentId = tkhLenId;
                        break;
                    case unitLenId:
                        inputRoomLen.setFirstFlash(true).notifychange();
                        inputUnitLen.setFirstFlash(false);
                        inputUnitLen.deleteNum("0");
                        currentId = roomLenId;
                        break;
                }
                break;
            case Constant.KEY_NEXT:

                break;
            default:
                switch (currentId) {
                    case tkhLenId:
                        inputTkhLen.setCount(0);
                        inputTkhLen.setEndFlash(false);
                        inputRoomLen.setFirstFlash(true).notifychange();
                        currentId = roomLenId;
                        inputTkhLen.addNum(keyId);
                        break;
                    case roomLenId:
                        inputRoomLen.setCount(0);
                        inputRoomLen.setEndFlash(false);
                        inputUnitLen.setFirstFlash(true).notifychange();
                        currentId = unitLenId;
                        inputRoomLen.addNum(keyId);
                        break;
                    case unitLenId:
                        inputUnitLen.setCount(0);
                        inputUnitLen.setEndFlash(true);
                        inputUnitLen.addNum(keyId);
                        break;
                }

                break;
        }
    }


    private void saveDatas() {
        fullDeviceNo.setStairNoLen(tkhLenContent);
        fullDeviceNo.setRoomNoLen(roomLenContent);
        fullDeviceNo.setCellNoLen(unitLenContent);
        fullDeviceNo.setSubsection(getSubsection());

        //梯口机做当前显示的设备号保存
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            String deviceNo = fullDeviceNo.getDeviceNo();
            String str = deviceNo.substring(0, tkhLenContent);
            fullDeviceNo.setCurrentDeviceNo(str);
            fullDeviceNo.notifyDeviceNo();
        }
    }


    @Override
    public void success() {
        exitFragment(this);
    }

    @Override
    public void fail() {

    }

    @Override
    public void onDestroyView() {
        AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_DEVICENORULE);
        super.onDestroyView();
    }

    public int isCheckDeviceNo() {
        if (unitLenContent > 2) {
            return 1;
        }
        if (tkhLenContent < unitLenContent || tkhLenContent > 9) {
            return 2;
        }
        if (roomLenContent < 3 || roomLenContent > 9) {
            return 3;
        }
        if ((tkhLenContent + roomLenContent) > 17) {
            return 4;
        }
        return 0;
    }

    private int getSubsection() {
        return ((tkhLenContent - unitLenContent) * 100) + (unitLenContent * 10) + roomLenContent;
    }

}
