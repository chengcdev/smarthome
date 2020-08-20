package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.interf.ICardNoListener;
import com.android.interf.IKeyEventListener;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenter;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenterImpl;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.RoomNoHelper;
import com.mili.smarthome.tkj.utils.StringUtils;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 卡管理（添加、删除）
 */
public class CardManageFragment extends BaseSetFragment implements ICardNoListener {

    private TextView tvTitle;
    private NumInputView tvRoomNo;
    private NumInputView tvCardNo;
    private String mFuncCode = SettingFunc.CARD_ADD;

    private int mRoomNoLen;
    private int mCardNoLen; // 卡号长度
    private RoomNoHelper mRoomNoHelper;
    private CardPresenter mCardPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_card;
    }

    @Override
    protected void bindView() {
        tvTitle = findView(R.id.tv_title);
        tvRoomNo = findView(R.id.tv_room_no);
        tvCardNo = findView(R.id.tv_card_no);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SinglechipClientProxy.getInstance().setCardState(0x01);
        SinglechipClientProxy.getInstance().setCardNoListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.CARD_ADD);
        }
        if (SettingFunc.CARD_ADD.equals(mFuncCode)) {
            tvTitle.setText(R.string.setting_0101);
            showResult(R.string.setting_card_sync_hint, 5000, true, null);
        } else {
            tvTitle.setText(R.string.setting_0102);
        }

        if (mCardPresenter == null) {
            mCardPresenter = new CardPresenterImpl();
        }

        FullDeviceNo fullDeviceNo = new FullDeviceNo(mContext);
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR
                && fullDeviceNo.getRoomNoLen() == 4) {
            findView(R.id.ll_room_no).setVisibility(View.VISIBLE);
            mRoomNoLen = fullDeviceNo.getRoomNoLen();
            mRoomNoHelper = new RoomNoHelper();
            tvRoomNo.setMaxLength(mRoomNoLen);
            tvRoomNo.setText(StringUtils.padLeft("0", mRoomNoLen, '0'));
        } else {
            findView(R.id.ll_room_no).setVisibility(View.GONE);
            mRoomNoLen = 0;
            tvRoomNo.setMaxLength(mRoomNoLen);
        }

        mCardNoLen = ParamDao.getCardNoLen();

        tvCardNo.setMaxLength(mCardNoLen);
        tvCardNo.requestFocus();
    }

    @Override
    public void onDestroyView() {
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().setCardNoListener(null);
        super.onDestroyView();
    }

    @Override
    public void onCardNo(String cardNo) {
        if (tvRoomNo.getText().length() != mRoomNoLen)
            return;
        tvCardNo.setText(cardNo);
        tvCardNo.postDelayed(new Runnable() {
            @Override
            public void run() {
                save();
            }
        }, 1500);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case IKeyEventListener.KEYCODE_UP:
                if (mRoomNoHelper != null) {
                    tvRoomNo.setText(mRoomNoHelper.getPreviousRoomNo());
                }
                break;
            case IKeyEventListener.KEYCODE_DOWN:
                if (mRoomNoHelper != null) {
                    tvRoomNo.setText(mRoomNoHelper.getNextRoomNo());
                }
                break;
            case IKeyEventListener.KEYCODE_0:
                inputNum(0);
                break;
            case IKeyEventListener.KEYCODE_1:
            case IKeyEventListener.KEYCODE_2:
            case IKeyEventListener.KEYCODE_3:
            case IKeyEventListener.KEYCODE_4:
            case IKeyEventListener.KEYCODE_5:
            case IKeyEventListener.KEYCODE_6:
            case IKeyEventListener.KEYCODE_7:
            case IKeyEventListener.KEYCODE_8:
            case IKeyEventListener.KEYCODE_9:
                inputNum(keyCode);
                break;
            case IKeyEventListener.KEYCODE_BACK:
                backspace();
                break;
            case IKeyEventListener.KEYCODE_CALL:
                save();
                break;
        }
        return true;
    }

    private void save() {
        if (tvRoomNo.getText().length() != mRoomNoLen)
            return;
        String roomNo = tvRoomNo.getText().toString();
        String cardNo = tvCardNo.getText().toString();
        boolean result;
        if (SettingFunc.CARD_ADD.equals(mFuncCode)) {
            if (cardNo.length() == mCardNoLen) {
                // 添加卡
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
                result = mCardPresenter.addCard(model);
            } else {
                return;
            }
        } else {
            if (cardNo.length() == mCardNoLen) {
                // 删除单张卡
                result = mCardPresenter.delCard(cardNo, roomNo);
            } else if (cardNo.length() == 0 && roomNo.length() != 0) {
                // 删除某住户所有卡
                result = mCardPresenter.deleteUserCards(roomNo);
            } else {
                return;
            }
        }
        if (result) {
            SinglechipClientProxy.getInstance().setCardNoListener(null);
            showResult(R.string.setting_suc, 1500, false, new Runnable() {
                @Override
                public void run() {
                    if (mRoomNoHelper != null) {
                        tvRoomNo.setText(mRoomNoHelper.getNextRoomNo());
                    }
                    tvCardNo.clearText();
                    tvCardNo.requestFocus();
                    SinglechipClientProxy.getInstance().setCardNoListener(CardManageFragment.this);
                }
            });
        } else {
            showResultAndBack(R.string.setting_fail);
        }
    }
}
