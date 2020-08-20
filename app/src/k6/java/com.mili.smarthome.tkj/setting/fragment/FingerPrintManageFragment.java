package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.interf.FingerEventListenerAdapter;
import com.android.interf.IFingerEventListener;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.entity.RoomNoHelper;
import com.mili.smarthome.tkj.fragment.BaseMainFragment;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.ResUtils;
import com.mili.smarthome.tkj.utils.StringUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

import static com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_FINGER_PRINT_ADD;

/**
 * 指纹（添加、删除）
 */
public class FingerPrintManageFragment extends BaseMainFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener, View.OnClickListener {

    private NumInputView mRoomNo;
    private SetOperateView mOprateView;
    private KeyBoardView keyBoardView;
    //默认房号长度
    private final int defaaultRoomNoLen = 4;
    //房号长度
    private int roomNoLen = 4;
    //是否显示提示框
    private boolean isShowTip;
    private FullDeviceNo fullDeviceNo;
    private final int roomNoId = 1000;
    private int currentViewId = roomNoId;
    private String mFuncCode;
    //列表住户添加，删除计数
    private int count = 0;
    private TextView mTvTip1;
    private TextView mTvTip2;
    private TextView mTvTitle;
    private LinearLayout mLinRecordTip;
    private RoomNoHelper mRoomNoHelper;
    private String roomNo;
    private ImageView mImaBack;
    private int stairNoLen;
    private String currentRoomNo = "";
    private IFingerEventListener mFingerEventListener = new FingerEventListener();


    @Override
    public void initView(View view) {
        mRoomNo = (NumInputView) view.findViewById(R.id.tv_room_no);
        mOprateView = (SetOperateView) view.findViewById(R.id.rootview);
        keyBoardView = (KeyBoardView) view.findViewById(R.id.keyboardview);
        mTvTip1 = (TextView) view.findViewById(R.id.tv_record_tip1);
        mTvTip2 = (TextView) view.findViewById(R.id.tv_record_tip2);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mLinRecordTip = (LinearLayout) view.findViewById(R.id.lin_record_tip);
        mImaBack = (ImageView) view.findViewById(R.id.iv_back);
        setBackVisibility(View.GONE);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_finger_print_manager;
    }


    @Override
    public void onResume() {
        super.onResume();
        mImaBack.setVisibility(View.VISIBLE);
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mOprateView.setSuccessListener(this);
        mImaBack.setOnClickListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SET_FINGER_PRINT_ADD);
        }

        //若是添加指纹，显示提示
        if (mFuncCode.equals(SET_FINGER_PRINT_ADD)) {
            mImaBack.setVisibility(View.GONE);
            mOprateView.operateBackState(getStr(R.string.setting_finger_tip), 5000, new SetOperateView.IOperateListener() {
                @Override
                public void success() {
                    mTvTitle.setText(getStr(R.string.setting_030501));
                    mLinRecordTip.setVisibility(View.VISIBLE);
                    mImaBack.setVisibility(View.VISIBLE);
                }

                @Override
                public void fail() {

                }
            });
        } else {
            mTvTitle.setText(getStr(R.string.setting_030502));
            mLinRecordTip.setVisibility(View.GONE);
        }

        initData();

    }

    @Override
    public void onPause() {
        super.onPause();
        setBackVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SinglechipClientProxy.getInstance().stopAddFinger();
        SinglechipClientProxy.getInstance().setCardState(0);
        SinglechipClientProxy.getInstance().removeFingerEventListener(mFingerEventListener);
    }

    private void initData() {
        fullDeviceNo = new FullDeviceNo(getContext());
        //获取房号长度
        roomNoLen = fullDeviceNo.getRoomNoLen();
        //梯号长度
        stairNoLen = fullDeviceNo.getStairNoLen();


        //获取住户列表
        if (roomNoLen == defaaultRoomNoLen) {
            mRoomNoHelper = new RoomNoHelper();
        }
        initInputView();

        if (mFuncCode.equals(SET_FINGER_PRINT_ADD)) {
            //添加指纹
            SinglechipClientProxy.getInstance().stopAddFinger();
            SinglechipClientProxy.getInstance().setCardState(0);
        }

        //指纹操作
        mTvTip1.setText(getStr(R.string.finger_collect_format, 1));
        mTvTip2.setText(R.string.finger_press);
        SinglechipClientProxy.getInstance().addFingerEventListener(mFingerEventListener);
        SinglechipClientProxy.getInstance().setCardState(3);
        SinglechipClientProxy.getInstance().addFinger(mRoomNo.getText().toString());
    }

    private void initInputView() {
        isShowTip = false;
        roomNoLen = fullDeviceNo.getRoomNoLen();
        if ((fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA)) {
            //区口机
            roomNoLen = roomNoLen + stairNoLen;
        }
        mRoomNo.setMaxLength(roomNoLen);
        mRoomNo.requestFocus();
        if (mFuncCode.equals(SET_FINGER_PRINT_ADD)) {
            if (roomNoLen == defaaultRoomNoLen) {
                currentRoomNo = mRoomNoHelper.getCurrentRoomNo();
                mRoomNo.setText(currentRoomNo);
            }else {
                currentRoomNo = StringUtils.padLeft("0", roomNoLen, '0');
            }
            beginAddFinger(currentRoomNo);
            mRoomNo.setCursorIndex(roomNoLen);
        } else {
            mRoomNo.setText("");
        }
        mImaBack.setVisibility(View.VISIBLE);
    }


    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                backspace();
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                if (SET_FINGER_PRINT_ADD.equals(mFuncCode)) {
                    //添加指纹操作

                } else {
                    //删除指纹操作
                    String currentRoomNo = mRoomNo.getText().toString();
                    if (currentRoomNo.length() >= roomNoLen) {
                        fingerDel(currentRoomNo);
                    }
                }
                break;
            default:
                inputNum(Integer.parseInt(keyBoardBean.getName()));
                if (mRoomNo.getText().length() == roomNoLen) {
                    beginAddFinger(mRoomNo.getText().toString());
                }
                //梯口机四位房号，是否显示默认房号
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
                    String roomNo = mRoomNoHelper.getCurrentRoomNo();
                    String curRoomNo = mRoomNo.getText().toString();
                    if (!roomNo.equals(curRoomNo)) {
                        mRoomNoHelper.reset();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                exitFragment(this);
                break;
        }
    }

    @Override
    protected void backspace() {
        if (mRoomNo.isFocused()) {
            SinglechipClientProxy.getInstance().stopAddFinger();
        }
        super.backspace();
    }

    private void beginAddFinger(String roomNo) {
        int state = SinglechipClientProxy.getInstance().addFinger(roomNo);
        if (state == 0) {
            //失败
            mTvTip1.setVisibility(View.GONE);
            mTvTip2.setText(R.string.set_input_room_no_error);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initInputView();
                }
            }, 3000);
        }else {
            //成功
            mRoomNo.setText(roomNo);
            mTvTip1.setVisibility(View.VISIBLE);
            mTvTip1.setText(getStr(R.string.finger_collect_format, 1));
            mTvTip2.setText(R.string.finger_press);
        }

    }

    class FingerEventListener extends FingerEventListenerAdapter {

        @Override
        public void onFingerCollect(int code, int press, int count) {
            if (mRoomNo.getText().toString().length() != roomNoLen) {
                mTvTip1.setVisibility(View.GONE);
                mTvTip2.setText(R.string.set_input_room_no);
                return;
            }else {
                mTvTip1.setVisibility(View.VISIBLE);
            }

            AppUtils.getInstance().startScreenService();

            if (press == 0) {
                if (count < 4) {
                    //记录一次当前的房号
                    currentRoomNo = mRoomNo.getText().toString();
                    mTvTip1.setText(getStr(R.string.finger_collect_format, count));
                } else {
                    mTvTip1.setText(R.string.finger_collect_begin);
                }
                mTvTip2.setText(R.string.finger_press);
            } else {
                if (code == 0) {
                    mTvTip1.setText(R.string.finger_collect_0);
                    mTvTip2.setText(R.string.finger_raise);
                } else {
                    int resid = ResUtils.getStringId(mContext, "finger_collect_" + code);
                    if (resid != 0) {
                        mTvTip2.setText(resid);
                    } else {
                        mTvTip2.setText(R.string.finger_collect_255);
                    }
                }
            }
        }

        @Override
        public void onFingerAdd(int code, int fingerId, int valid, byte[] fingerData) {
            FingerDao fingerDao = new FingerDao();
            if (code == 1) {
                LogUtils.e("onFingerAdd===== code :" + code);

                if (currentRoomNo.equals("")) {
                    currentRoomNo = "0";
                }
                currentRoomNo = mRoomNo.getText().toString();
                LogUtils.w(" FingerPrintManageFragment currentRoomNo: "+currentRoomNo);
                fingerDao.insert(fingerId, valid, fingerData, currentRoomNo);
                // TODO 设置成功
                mOprateView.operateBackState(getStr(R.string.set_success));
                mImaBack.setVisibility(View.GONE);
            } else if (code == 3) {
                // TODO 指纹库满
            } else {
                // TODO 设置失败
                mOprateView.operateBackState(getStr(R.string.set_fail));
                mImaBack.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void success() {
        //保存到数据库
        switch (mFuncCode) {
            case SET_FINGER_PRINT_ADD:
                showRoomNo(false);
                initInputView();
                break;
            case SettingFunc.SET_FINGER_PRINT_DELETE:
                //设置列表计数
                initInputView();
                break;
        }
    }

    private void showRoomNo(boolean isFail) {
        String roomNo = mRoomNo.getText().toString();
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4 && !isFail) {
            //设置列表计数
            roomNo = mRoomNoHelper.getNextRoomNo();
        }
        beginAddFinger(roomNo);
    }


    @Override
    public void fail() {
        showRoomNo(true);
        initInputView();
    }

    /**
     * 删除指纹
     */
    private void fingerDel(String roomNo) {
        SinglechipClientProxy.getInstance().delFinger(roomNo);
        FingerDao fingerDao = new FingerDao();
        fingerDao.deleteByRoomNo(roomNo);
        mOprateView.operateBackState(getStr(R.string.set_success));
        mImaBack.setVisibility(View.GONE);
    }


}
