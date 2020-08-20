package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.client.CardClient;
import com.android.interf.ICardNoListener;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenter;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenterImpl;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.utils.RoomNoHelper;
import com.mili.smarthome.tkj.widget.NumInputView;


/**
 * 卡管理（添加卡、删除卡、清空卡）
 */
public class CardManageFragment extends K4BaseFragment implements View.OnClickListener, ICardNoListener, View.OnTouchListener {

    private static final int CARD_HINT_TIME = 3*1000;

    private static final String Tag = "CardManageFragment";
    private TextView mHeadDesc;
    private NumInputView mRoomNo, mCardNo;
    private LinearLayout mLlContent;
    private RelativeLayout mLlButton;
    private TextView mTvHint;

    private int mFocusIndex = 0;
    private String mFuncCode = SettingFunc.CARD_ADD;
    private boolean mHintState = false;
    private int mRoomNoLen = 4;
    private int mCardMaxlen = 8;

    private FullDeviceNo mFullDeviceNo;
    private int mDeviceType;

    private RoomNoHelper mRoomNoHelper;
    private CardPresenter mCardPresenter;

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        if (mFocusIndex == 0) {
            mRoomNo.input(code);
            if (mRoomNo.getCursorIndex() >= mRoomNoLen) {
                mFocusIndex = 1;
                mCardNo.requestFocus();
                mCardNo.setCursorIndex(0);
            }
        } else {
            mCardNo.input(code);
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        if (mFuncCode.equals(SettingFunc.CARD_CLEAR)) {
            boolean ret = clearCard();
            if (ret) {
                mMainHandler.sendEmptyMessage(MSG_SET_OK);
            } else {
                mMainHandler.sendEmptyMessage(MSG_SET_ERROR);
            }
            return true;
        }

        if (mHintState) {
            mMainHandler.removeMessages(MSG_SET_SHOW);
            showView(true);
            focusCardNo();
            mHintState = false;
            return true;
        }

        if (mRoomNo == null || mCardNo == null) {
            Log.d(Tag, "onKeyConfirm: inHint or inputtextview is null.");
            return true;
        }

        String roomNo;
        String cardNo;
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            roomNo = "";
            cardNo = mCardNo.getText().toString();
            if (cardNo.length() < mCardMaxlen) {
                return true;
            }
        } else {
            roomNo = mRoomNo.getText().toString();
            cardNo = mCardNo.getText().toString();
            if (roomNo.length() < mRoomNoLen) {
                return true;
            }
            if (cardNo.length() < mCardMaxlen) {
                if (mFuncCode.equals(SettingFunc.CARD_DEL) && cardNo.length() == 0) {
                    //删除卡是可按住户删除
                } else {
                    return true;
                }
            }
        }
        manageCard(roomNo, cardNo);
        return true;
    }

    @Override
    public boolean onKeyCancel() {
        if (mFuncCode.equals(SettingFunc.CARD_CLEAR)) {
            exitFragment();
            return true;
        }

        if (mHintState) {
            mMainHandler.removeMessages(MSG_SET_SHOW);
            showView(true);
            focusCardNo();
            mHintState = false;
            return true;
        }

        //区口机时，无房户信息
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            mCardNo.backspace();
            if (mCardNo != null && mCardNo.getText().length() == 0) {
                exitFragment();
            }
            return true;
        } else {
            if (mFocusIndex == 1) {
                mCardNo.backspace();
                if (mCardNo != null && mCardNo.getCursorIndex() == 0) {
                    mFocusIndex = 0;
                    mRoomNo.requestFocus();
                    mRoomNo.setCursorIndex(mRoomNoLen-1);
                }
            } else {
                if (mRoomNo != null) {
                    if (mRoomNo.getCursorIndex() == 0) {
                        exitFragment();
                    } else {
                        mRoomNo.backspace();
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_SET_SHOW:
                showView(true);
                mHintState = false;
                focusCardNo();
                break;

            case MSG_SET_OK:
            case MSG_SET_ERROR:
                showView(false);
                if (mTvHint != null) {
                    mTvHint.setLines(1);
                    if (msg.what == MSG_SET_OK) {
                        mTvHint.setText(R.string.set_ok);
                    } else {
                        mTvHint.setText(R.string.set_error);
                    }
                }
                mMainHandler.sendEmptyMessageDelayed(MSG_REQUEST_EXIT, 2000);
                break;

            case MSG_REQUEST_EXIT:
                if (mFuncCode.equals(SettingFunc.CARD_CLEAR)) {
                    exitFragment();
                } else {
                    SinglechipClientProxy.getInstance().setCardNoListener(this);
                    nextRoomNo();
                    showView(true);
                    focusCardNo();
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_card;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView() {
        super.bindView();

        if (mFullDeviceNo == null) {
            mFullDeviceNo = new FullDeviceNo(getContext());
        }
        mDeviceType = mFullDeviceNo.getDeviceType();

        mHeadDesc = findView(R.id.tv_head);
        mRoomNo = findView(R.id.tv_roomno);
        mTvHint = findView(R.id.tv_hint);
        mLlButton = findView(R.id.ll_button);

        ImageButton up = findView(R.id.ib_up);
        assert up != null;
        up.setOnClickListener(this);
        ImageButton down = findView(R.id.ib_down);
        assert down != null;
        down.setOnClickListener(this);

        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            mCardNo = findView(R.id.tv_cardno_area);
            mLlContent = findView(R.id.ll_area);
            LinearLayout content = findView(R.id.ll_content);
            assert content != null;
            content.setVisibility(View.INVISIBLE);
        } else {
            mCardNo = findView(R.id.tv_cardno);
            mLlContent = findView(R.id.ll_content);
            LinearLayout area = findView(R.id.ll_area);
            assert area != null;
            area.setVisibility(View.INVISIBLE);
        }

        mRoomNo.setOnTouchListener(this);
        mCardNo.setOnTouchListener(this);
    }

    @Override
    protected void bindData() {
        super.bindData();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mFuncCode = bundle.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.CARD_ADD);
            String funcName = SettingFunc.getNameByCode(mFuncCode);
            if (mHeadDesc != null) {
                mHeadDesc.setText(funcName);
            }
        }

        initData();
        initCardListener();

        focusCardNo();
        mHintState = false;
        switch (mFuncCode) {
            case SettingFunc.CARD_ADD:
                showView(false);
                mHintState = true;
                mTvHint.setText(R.string.set_card_hint);
                mMainHandler.sendEmptyMessageDelayed(MSG_SET_SHOW, CARD_HINT_TIME);
                break;

            case SettingFunc.CARD_CLEAR:
                showView(false);
                mTvHint.setText(R.string.set_card_delHint);
                break;
        }
    }

    @Override
    protected void unbindView() {
        super.unbindView();
        mMainHandler.removeCallbacksAndMessages(0);
        mHintState = false;

        //恢复刷卡进门状态
        CardClient.getInstance().SetCardState(0);
        SinglechipClientProxy.getInstance().setCardNoListener(null);
    }


    private void initData() {
        if (mCardPresenter == null) {
            mCardPresenter = new CardPresenterImpl();
        }

        //获取卡号长度
        mCardMaxlen = ParamDao.getCardNoLen();
        mCardNo.setMaxLength(mCardMaxlen);

        //设置默认房号
        if (mFullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mRoomNoLen = mFullDeviceNo.getRoomNoLen();
            StringBuilder roomNo = new StringBuilder();
            for (int i = 0; i < mRoomNoLen; i++) {
                roomNo.append('0');
            }
            mRoomNo.setMaxLength(mRoomNoLen);
            mRoomNo.setText(roomNo);
        }

        // 默认房号长度为4时显示房号列表
        if (mRoomNoLen == 4) {
            mRoomNoHelper = new RoomNoHelper();
            mRoomNoHelper.reset();
        }
        Log.d(Tag, "mRoomNoLen=" + mRoomNoLen + ", mCardMaxlen=" + mCardMaxlen);
    }

    private void initCardListener() {
        //设置卡状态
        CardClient.getInstance().SetCardState(1);
        //获取卡号监听
        SinglechipClientProxy.getInstance().setCardNoListener(this);
    }

    private void showView(boolean visible) {
        if (visible) {
            mLlContent.setVisibility(View.VISIBLE);
            mLlButton.setVisibility(View.VISIBLE);
            mTvHint.setVisibility(View.INVISIBLE);
        } else {
            mLlContent.setVisibility(View.INVISIBLE);
            mLlButton.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            return;
        }
        switch (view.getId()) {
            case R.id.ib_down:
                nextRoomNo();
                break;

            case R.id.ib_up:
                preRoomNo();
                break;
        }
    }


    private void focusCardNo() {
        mFocusIndex = 1;
        if (mCardNo != null) {
            mCardNo.requestFocus();
            mCardNo.setText("");
            mCardNo.setCursorIndex(0);
        }
    }

    private void preRoomNo() {
        //不是默认长度，不支持房号切换
        if (mRoomNoLen != 4) {
            return;
        }
        if (mRoomNoHelper != null) {
            String roomNo = mRoomNoHelper.getPreviousRoomNo();
            if (mRoomNo != null) {
                mRoomNo.setText(roomNo);
            }
        }
    }

    private void nextRoomNo() {
        //不是默认长度，不支持房号切换
        if (mRoomNoLen != 4) {
            return;
        }
        if (mRoomNoHelper != null) {
            String roomNo = mRoomNoHelper.getNextRoomNo();
            if (mRoomNo != null) {
                mRoomNo.setText(roomNo);
            }
        }
    }

    private boolean addCard(String roomNo, String cardNo) {
        int cardType = 0x01;
        UserCardInfoModels model = new UserCardInfoModels();
        model.setCardNo(cardNo);
        model.setRoomNo(roomNo);
        model.setCardType(cardType);
        model.setRoomNoState(0);
        model.setKeyID("");
        model.setStartTime(0);
        model.setEndTime(0);
        model.setLifecycle(-2);
        return mCardPresenter.addCard(model);
    }

    private boolean delCard(String roomNo, String cardNo) {
        if (roomNo.length() == mRoomNoLen && cardNo.length() == 0) {
            //按住户删除
            return mCardPresenter.deleteUserCards(roomNo);
        } else {
            //按卡号删除
            return mCardPresenter.delCard(cardNo, roomNo);
        }
    }

    private boolean clearCard() {
        return mCardPresenter.clearCards();
    }

    private void manageCard(String roomNo, String cardNo) {
        Log.d(Tag, "manageCard: roomNo=" + roomNo + ", cardno=" + cardNo);

        boolean ret;
        switch (mFuncCode) {
            case SettingFunc.CARD_ADD:
                if (cardNo.length() != mCardMaxlen) {
                    Log.d(Tag, "[manageCard] the length of cardno is " + cardNo.length() + " != " + mCardMaxlen);
                    return;
                }
                ret = addCard(roomNo, cardNo);
                break;
            case SettingFunc.CARD_DEL:
                ret = delCard(roomNo, cardNo);
                break;
            default:
                return;
        }
        if (ret) {
            SinglechipClientProxy.getInstance().setCardNoListener(null);
            mMainHandler.sendEmptyMessage(MSG_SET_OK);
        } else {
            mMainHandler.sendEmptyMessage(MSG_SET_ERROR);
        }
    }

    @Override
    public void onCardNo(String cardNo) {
        Log.d(Tag, "onCardNo: cardno = " + cardNo);

        //梯口机时不判断房号
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            String roomNo = mRoomNo.getText().toString();
            if (roomNo.length() != mRoomNoLen) {
                return;
            }
        }

        mCardNo.setText(cardNo);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String roomNo = mRoomNo.getText().toString();
                String cardNo = mCardNo.getText().toString();
                if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
                    roomNo = "";
                }
                manageCard(roomNo, cardNo);
            }
        }, 1000);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        switch(view.getId()) {
            case R.id.tv_roomno:
                mRoomNo.requestFocus();
                mRoomNo.setCursorIndex(0);
                mFocusIndex = 0;
                break;
            case R.id.tv_cardno_area:
            case R.id.tv_cardno:
                mCardNo.requestFocus();
                mCardNo.setCursorIndex(0);
                mFocusIndex = 1;
                break;
        }
        return false;
    }
}
