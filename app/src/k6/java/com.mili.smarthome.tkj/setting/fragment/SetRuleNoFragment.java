package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 编码规则设置
 */
public class SetRuleNoFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener, View.OnTouchListener {

    private NumInputView mTvTikouLen;
    private NumInputView mTvRoomLen;
    private NumInputView mTvCellLen;
    private KeyBoardView keyBoardView;
    private SetOperateView mOperateView;
    //梯口号长度
    private int stairNoLen;
    //房号长度
    private int roomLen;
    //单元号长度
    private int cellLen;;
    private FullDeviceNo fullDeviceNo;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_ruleno;
    }

    @Override
    protected void bindView() {
        mTvTikouLen = findView(R.id.tv_tk_len);
        mTvRoomLen = findView(R.id.tv_room_len);
        mTvCellLen = findView(R.id.tv_cell_len);
        keyBoardView = findView(R.id.keyboardview);
        mOperateView = findView(R.id.rootview);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mOperateView.setSuccessListener(this);
        mTvCellLen.setOnTouchListener(this);
        mTvRoomLen.setOnTouchListener(this);
        mTvCellLen.setOnTouchListener(this);

        initDeviceNo();
    }


    private void initDeviceNo() {
        fullDeviceNo = new FullDeviceNo(getContext());
        stairNoLen = fullDeviceNo.getStairNoLen();
        roomLen = fullDeviceNo.getRoomNoLen();
        cellLen = fullDeviceNo.getCellNoLen();

        mTvTikouLen.setText(stairNoLen + "");
        mTvRoomLen.setText(roomLen + "");
        mTvCellLen.setText(cellLen + "");
        mTvTikouLen.requestFocus();
    }


    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (mTvTikouLen.getCursorIndex() == 0) {
                    requestBack();
                }
                backspace();
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                stairNoLen = Integer.parseInt(mTvTikouLen.getText().toString());
                roomLen = Integer.parseInt(mTvRoomLen.getText().toString());
                cellLen = Integer.parseInt(mTvCellLen.getText().toString());
                int checkDeviceNo = fullDeviceNo.isCheckDeviceNo(stairNoLen, roomLen, cellLen);
                if (checkDeviceNo == 0) {
                    //保存
                    toSave();
                } else {
                    //设置失败
                    mOperateView.operateBackState(getString(R.string.set_fail));
                }
                break;
            default:
                int id = Integer.valueOf(keyBoardBean.getkId());
                inputNum(id);
                if (mTvCellLen.getCursorIndex() == mTvCellLen.getText().toString().length()) {
                    mTvCellLen.setCursorIndex(0);
                    mTvCellLen.requestFocus();
                }
                break;
        }
    }

    private void toSave() {
        int subsection = fullDeviceNo.getCurrentSubsection(stairNoLen, roomLen, cellLen);
        fullDeviceNo.setStairNoLen((byte) stairNoLen);
        fullDeviceNo.setRoomNoLen((byte) roomLen);
        fullDeviceNo.setCellNoLen((byte) cellLen);
        fullDeviceNo.setSubsection(subsection);

        //梯口机做当前显示的设备号保存
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            String deviceNo = fullDeviceNo.getDeviceNo();
            String str = deviceNo.substring(0, stairNoLen);
            fullDeviceNo.setCurrentDeviceNo(str);
            fullDeviceNo.notifyDeviceNo();
        }

        //发送广播
        Intent intent = new Intent(CommSysDef.BROADCAST_DEVICENORULE);
        getActivity().sendBroadcast(intent);

        //设置成功
        mOperateView.operateBackState(getString(R.string.set_success));
        setBackVisibility(View.GONE);
    }

    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    @Override
    public void fail() {
        //设置失败
        initDeviceNo();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.tv_tk_len:
                mTvTikouLen.setCursorIndex(0);
                mTvCellLen.setCursorIndex(0);
                break;
            case R.id.tv_room_len:
                mTvTikouLen.setCursorIndex(mTvTikouLen.getText().toString().length());
                mTvCellLen.setCursorIndex(0);
                break;
            case R.id.tv_cell_len:
                mTvTikouLen.setCursorIndex(mTvTikouLen.getText().toString().length());
                mTvCellLen.setCursorIndex(0);
                break;
            default:
                break;
        }
        return false;
    }
}
