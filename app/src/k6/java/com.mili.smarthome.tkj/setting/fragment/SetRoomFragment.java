package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 住户设置
 */
public class SetRoomFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener, View.OnTouchListener {

    private NumInputView mRoomStart;
    private NumInputView mFloorCount;
    private NumInputView mFloorHouseNum;
    private KeyBoardView keyBoardView;
    //起始房号
    private String roomStart;
    //楼层数
    private String flooCount;
    //每层户数
    private String floorHouseNum;
    private SetOperateView mOperateView;
    private ResidentSettingDao residentSettingDao;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_room;
    }

    @Override
    protected void bindView() {
        mRoomStart = findView(R.id.tv_room_start);
        mFloorCount = findView(R.id.tv_floor_count);
        mFloorHouseNum = findView(R.id.tv_room_count);
        keyBoardView = findView(R.id.keyboardview);
        mOperateView = findView(R.id.rootview);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mRoomStart.setOnTouchListener(this);
        mFloorCount.setOnTouchListener(this);
        mFloorHouseNum.setOnTouchListener(this);
        mOperateView.setSuccessListener(this);
        //获取数据显示
        initData();
    }

    private void initData() {
        residentSettingDao = new ResidentSettingDao();
        roomStart = residentSettingDao.getRoomStart();
        flooCount = residentSettingDao.getFloorCount();
        floorHouseNum = residentSettingDao.getFloorHouseNum();

        mRoomStart.setText(roomStart);
        mFloorCount.setText(flooCount);
        mFloorHouseNum.setText(floorHouseNum);
        mRoomStart.requestFocus();
    }


    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (mRoomStart.getCursorIndex() == 0) {
                    requestBack();
                }else {
                    backspace();
                }
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                if (roomStart.equals("00") || flooCount.equals("00") || floorHouseNum.equals("00")) {
                    //设置失败
                    mOperateView.operateBackState(getString(R.string.set_fail));
                } else {
                    //设置成功
                    mOperateView.operateBackState(getString(R.string.set_success));
                }
                setBackVisibility(View.GONE);
                break;
            default:
                int id = Integer.valueOf(keyBoardBean.getkId());
                inputNum(id);
                if (mFloorHouseNum.getCursorIndex() == mFloorHouseNum.getText().toString().length()) {
                    mFloorHouseNum.setCursorIndex(mFloorHouseNum.getText().toString().length()-1);
                    mFloorHouseNum.requestFocus();
                }
                break;
        }
    }


    @Override
    public void success() {
        //保存数据
        if (residentSettingDao != null) {
            roomStart = mRoomStart.getText().toString();
            floorHouseNum = mFloorHouseNum.getText().toString();
            flooCount = mFloorCount.getText().toString();
            residentSettingDao.setFloorCount(flooCount);
            residentSettingDao.setRoomStart(roomStart);
            residentSettingDao.setFloorHouseNum(floorHouseNum);
        }
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    @Override
    public void fail() {
        //保存失败
        setBackVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.tv_room_start:
                mRoomStart.setCursorIndex(0);
                break;
            case R.id.tv_floor_count:
                mRoomStart.setCursorIndex(mRoomStart.getText().toString().length());
                break;
            case R.id.tv_room_count:
                mRoomStart.setCursorIndex(mRoomStart.getText().toString().length());
                break;
        }
        return false;
    }
}
