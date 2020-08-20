package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.android.CommTypeDef;
import com.android.interf.ICardNoListener;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenter;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenterImpl;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.entity.RoomNoHelper;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 卡管理（添加、删除）
 */
public class CardManageFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener,
        SetOperateView.IOperateListener, ICardNoListener {

    private NumInputView mRoomNo;
    private NumInputView mCardNo;
    private SetOperateView mOprateView;
    private KeyBoardView keyBoardView;
    //默认房号长度
    private final int defaaultRoomNoLen = 4;
    //房号长度
    private int roomNoLen = 4;
    //卡号长度
    private int cardNumLen = 6;
    //是否显示提示框
    private boolean isShowTip;
    private FullDeviceNo fullDeviceNo;
    private String currentCardNo = "";
    private String currentRoomNo = "";
    private final int roomNoId = 1000;
    private final int cardNoId = 1001;
    private int currentViewId = cardNoId;
    private LinearLayout mLinRoomNo;
    private String mFuncCode;
    private UserInfoDao userInfoDao;
    //是否点击取消退出
    private boolean isClickCancel;
    //是否点击了确定
    private boolean isClickConfirm;
    private RoomNoHelper mRoomNoHelper;
    //设备类型
    private int mDeviceType;
    private String TAG = "CardManageFragment";


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_card;
    }

    @Override
    protected void bindView() {
        mRoomNo = findView(R.id.tv_room_no);
        mCardNo = findView(R.id.tv_card_no);
        mOprateView = findView(R.id.rootview);
        keyBoardView = findView(R.id.keyboardview);
        mLinRoomNo = findView(R.id.lin_room_no);
        setBackVisibility(View.VISIBLE);
    }

    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mOprateView.setSuccessListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.CARD_ADD);
        }

        //若是添加卡，显示提示
        if (mFuncCode.equals(SettingFunc.CARD_ADD)) {
            setBackVisibility(View.GONE);
            mOprateView.operateBackState(getString(R.string.setting_card_tip), 5000, new SetOperateView.IOperateListener() {
                @Override
                public void success() {
                    setBackVisibility(View.VISIBLE);
                }

                @Override
                public void fail() {

                }
            });

        }

        initData();
        //卡号回调监听
        initCardListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        //设置卡状态
        SinglechipClientProxy.getInstance().setCardState(0);
    }

    private void initCardListener() {
        //设置卡状态
        SinglechipClientProxy.getInstance().setCardState(1);
        //获取卡号监听
        SinglechipClientProxy.getInstance().setCardNoListener(this);
    }

    private void initData() {
        if (userInfoDao == null) {
            userInfoDao = new UserInfoDao();
        }
        //获取房号长度
        fullDeviceNo = new FullDeviceNo(getContext());
        roomNoLen = fullDeviceNo.getRoomNoLen();
        mDeviceType = fullDeviceNo.getDeviceType();
        //获取卡号长度
        cardNumLen = ParamDao.getCardNoLen();
        mRoomNo.setMaxLength(roomNoLen);
        mCardNo.setMaxLength(cardNumLen);

        //获取住户列表
        if (roomNoLen == defaaultRoomNoLen) {
            mRoomNoHelper = new RoomNoHelper();
            mRoomNoHelper.reset();
            currentRoomNo = mRoomNoHelper.getCurrentRoomNo();
            //梯口机
            mLinRoomNo.setVisibility(View.VISIBLE);
        } else {
            mLinRoomNo.setVisibility(View.GONE);
        }

        editInputView(currentRoomNo, "");
    }

    private void editInputView(String currentRoomNo, String currentCardNo) {
        isClickCancel = false;
        isShowTip = false;
        mCardNo.setMaxLength(cardNumLen);

        if ((mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) && roomNoLen == defaaultRoomNoLen) {
            mRoomNo.setMaxLength(roomNoLen);
            mLinRoomNo.setVisibility(View.VISIBLE);
            mRoomNo.setText(currentRoomNo);
        } else {
            mLinRoomNo.setVisibility(View.GONE);
            roomNoLen = 0;
            mRoomNo.setMaxLength(roomNoLen);
            mRoomNo.setText("");
        }
        mCardNo.setText(currentCardNo);
        mCardNo.setFocusable(true);
        mCardNo.setFocusableInTouchMode(true);
        mCardNo.setClickable(true);
        mCardNo.requestFocus();
    }

    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                isClickCancel = true;
                if (mLinRoomNo.getVisibility() == View.GONE && mCardNo.getCursorIndex() == 0) {
                    requestBack();
                } else {
                    boolean focused = mCardNo.isFocused();
                    LogUtils.w(TAG + "  onKeyBoardListener mCardNo.isFocused: " + focused);
                    if (mRoomNo.getCursorIndex() == 0 && !focused) {
                        requestBack();
                    } else {
                        backspace();
                    }
                }
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                isClickConfirm = true;
                if (SettingFunc.CARD_ADD.equals(mFuncCode)) {
                    //添加卡操作
                    addCard();
                } else {
                    //删除卡操作
                    deletCard();
                }
                break;
            default:
                if ((mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) && roomNoLen == defaaultRoomNoLen) {
                    if (mRoomNo.getCursorIndex() == 0 && mRoomNo.getText().toString().equals("")) {
                        mRoomNo.requestFocus();
                    }
                } else {
                    if (mCardNo.getCursorIndex() == 0 && mCardNo.getText().toString().equals("")) {
                        mCardNo.requestFocus();
                    }
                }
                String id = keyBoardBean.getkId();
                inputNum(Integer.valueOf(id));
                break;
        }
    }


    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
        isShowTip = false;
    }

    private void successSaveDatas(String cardNo) {
        switch (mFuncCode) {
            case SettingFunc.CARD_ADD:
                LogUtils.w(TAG + " successSaveDatas saveDao" + "  cardNo" + cardNo);
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
                    //添加成功，保存卡信息
                    userInfoDao.addCard(cardNo, currentRoomNo, 0x01);
                    //设置列表计数
                    setCount();
                } else {
                    //添加成功，保存卡信息
                    userInfoDao.addCard(cardNo, "", 0x01);
                }
                LogUtils.w(TAG + " successSaveDatas init" + "  cardNo" + cardNo);
                editInputView(currentRoomNo, "");
                break;
            case SettingFunc.CARD_DEL:
                if (currentRoomNo.length() == roomNoLen && cardNo.equals("")) {
                    //删除某住户的所有卡
                    userInfoDao.deleteUserCards(currentRoomNo);
                } else {
                    //删除某张卡
                    userInfoDao.deleteCard(cardNo);
                }
                //设置列表计数
                setCount();
                editInputView(currentRoomNo, "");
                break;
        }
    }

    private void setCount() {

        if (mRoomNoHelper != null) {
            //设置列表计数
            if (!currentRoomNo.equals(mRoomNoHelper.getCurrentRoomNo())) {
                if (!currentRoomNo.equals(mRoomNoHelper.getPreviousRoomNo())) {
                    mRoomNoHelper.reset();
                }
            }
            currentRoomNo = mRoomNoHelper.getNextRoomNo();
        }

    }

    @Override
    public void fail() {
        setBackVisibility(View.VISIBLE);
        isShowTip = false;
    }

    private void addCard() {
        currentRoomNo = mRoomNo.getText().toString();
        currentCardNo = mCardNo.getText().toString();
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
            if (currentRoomNo.length() < roomNoLen - 1 || currentCardNo.length() < cardNumLen) {
                return;
            }
        } else {
            if (currentCardNo.length() < cardNumLen) {
                return;
            }
        }
        //保存
        saveDatas(currentRoomNo, currentCardNo);
    }

    private void deletCard() {
        currentRoomNo = mRoomNo.getText().toString();
        currentCardNo = mCardNo.getText().toString();
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
            if (currentRoomNo.length() == roomNoLen && currentCardNo.equals("")) {
                //删除某住户的所有卡
                int code = delCard(currentRoomNo);
                if (code == 0x01) {
                    //删除成功
                    mOprateView.operateBackState(getString(R.string.set_success));
                    //保存到数据库
                    successSaveDatas(currentCardNo);
                } else {
                    //删除失败
                    mOprateView.operateBackState(getString(R.string.set_fail));
                }
                setBackVisibility(View.GONE);
                return;
            } else {
                if (currentRoomNo.length() < roomNoLen - 1 || currentCardNo.length() < cardNumLen) {
                    return;
                }
            }
        } else {
            if (currentCardNo.length() < cardNumLen) {
                return;
            }
        }
        //保存
        saveDatas(currentRoomNo, currentCardNo);
    }

    private void saveDatas(String roomNo, String cardNo) {
        int code = 0;
        switch (mFuncCode) {
            //添加卡
            case SettingFunc.CARD_ADD:
                LogUtils.w(TAG + " saveDatas roomNo: " + roomNo + "  cardNo" + cardNo);
                code = SinglechipClientProxy.getInstance().addCard(roomNo, cardNo, 0x01, 0, "", 0, 0,-2);
                break;
            //删除卡
            case SettingFunc.CARD_DEL:
                code = delCard(roomNo, cardNo);
                break;
        }
        if (code == 0x01) {
            //成功
            mOprateView.operateBackState(getString(R.string.set_success));
            //保存到数据库
            successSaveDatas(cardNo);
        } else {
            //失败
            mOprateView.operateBackState(getString(R.string.set_fail));
        }
        setBackVisibility(View.GONE);
    }


    @Override
    public void onCardNo(final String cardNo) {

        AppUtils.getInstance().startScreenService();

        isClickConfirm = false;

        if (isShowTip) {
            return;
        }
        //获取到刷卡的卡号
        switch (mFuncCode) {
            //添加卡
            case SettingFunc.CARD_ADD:
                if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    currentRoomNo = mRoomNo.getText().toString();
                    //梯口机
                    if (roomNoLen == 4 && currentRoomNo.length() != 4) {
                        return;
                    }
                }
                isShowTip = true;
                showCardNo(cardNo);
                //延时两秒添加保存
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded() && !isClickConfirm && currentCardNo.length() >= cardNumLen) {
                            saveDatas(currentRoomNo, currentCardNo);
                        }
                    }
                }, 2000);
                break;
            //删除卡
            case SettingFunc.CARD_DEL:
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    currentRoomNo = mRoomNo.getText().toString();
                    //梯口机
                    if (roomNoLen == 4 && currentRoomNo.length() != 4) {
                        return;
                    }
                }
                isShowTip = true;
                showCardNo(cardNo);
                //延时两秒删除保存
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded() && !isClickConfirm && currentCardNo.length() >= cardNumLen) {
                            saveDatas(currentRoomNo, currentCardNo);
                        }
                    }
                }, 2000);
                break;
        }
    }

    private void showCardNo(String cardNo) {
        LogUtils.w(TAG + "showCardNo cardNo: " + cardNo);
        mCardNo.setFocusable(false);
        mCardNo.clearText();
        mCardNo.setText(cardNo);

        currentRoomNo = mRoomNo.getText().toString();
        currentCardNo = mCardNo.getText().toString();
//        mCardNo.setCursorIndex(0);
    }

    /**
     * 删除单张卡
     */
    private int delCard(String roomNo, String cardNo) {
        CardPresenter cardPresenter = new CardPresenterImpl();
        boolean result = cardPresenter.delCard(cardNo, roomNo);
        return result ? 1 : 0;
    }

    /**
     * 删除住户所有卡
     */
    private int delCard(String roomNo) {
        CardPresenter cardPresenter = new CardPresenterImpl();
        boolean result = cardPresenter.deleteUserCards(roomNo);
        return result ? 1 : 0;
    }
}
