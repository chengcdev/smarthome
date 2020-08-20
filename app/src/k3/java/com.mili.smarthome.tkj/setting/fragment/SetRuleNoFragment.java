package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 编码规则设置
 */
public class SetRuleNoFragment extends BaseSetFragment {

    private NumInputView tvTikou;
    private NumInputView tvRoom;
    private NumInputView tvCell;

    private FullDeviceNo mFullDeviceNo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_ruleno;
    }

    @Override
    protected void bindView() {
        tvTikou = findView(R.id.tv_tk_no);
        tvRoom = findView(R.id.tv_room_no);
        tvCell = findView(R.id.tv_cell_no);
    }

    @Override
    protected void bindData() {
        mFullDeviceNo = new FullDeviceNo(mContext);
        tvTikou.setText(String.valueOf(mFullDeviceNo.getStairNoLen()));
        tvRoom.setText(String.valueOf(mFullDeviceNo.getRoomNoLen()));
        tvCell.setText(String.valueOf(mFullDeviceNo.getCellNoLen()));
        tvTikou.requestFocus();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
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
            case KEYCODE_CALL:
                save();
                break;
            case KEYCODE_BACK:
                backspace();
                break;
        }
        return true;
    }

    private void save() {
        byte stairNoLen = Byte.valueOf(tvTikou.getText().toString());
        byte roomNoLen = Byte.valueOf(tvRoom.getText().toString());
        byte cellNoLen = Byte.valueOf(tvCell.getText().toString());
        // 检查编号规则是否有效
        boolean isValid = mFullDeviceNo.isCheckDeviceNo(stairNoLen, roomNoLen, cellNoLen) == 0;
        if (isValid) {
            //
            int subsection = mFullDeviceNo.getCurrentSubsection(stairNoLen, roomNoLen, cellNoLen);
            mFullDeviceNo.setStairNoLen(stairNoLen);
            mFullDeviceNo.setRoomNoLen(roomNoLen);
            mFullDeviceNo.setCellNoLen(cellNoLen);
            mFullDeviceNo.setSubsection(subsection);

            /* 梯口机根据设置的梯口号长度调整设备号 */
            if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                String deviceNo = mFullDeviceNo.getDeviceNo();
                String str = deviceNo;
                if (stairNoLen < deviceNo.length()) {
                    str = deviceNo.substring(0, stairNoLen);
                }
                mFullDeviceNo.setCurrentDeviceNo(str);
                mFullDeviceNo.notifyDeviceNo();
            }

            //发送广播
            ContextProxy.sendBroadcast(CommSysDef.BROADCAST_DEVICENORULE);
            //
            showResultAndBack(R.string.setting_suc);
        } else {
            showResultAndBack(R.string.setting_fail);
        }
    }
}
