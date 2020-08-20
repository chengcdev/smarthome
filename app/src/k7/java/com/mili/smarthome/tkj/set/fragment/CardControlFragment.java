package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.main.entity.RoomNoHelper;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;

;

/**
 * 添加卡
 * 删除卡
 * 清空卡
 */

public class CardControlFragment extends BaseKeyBoardFragment implements ISetCallBackListener, ICardNoListener {
    private TextView tvTitle;
    private final int roomNoId = 1000;
    private final int cardNoId = 1001;
    private int currentId = cardNoId;
    private SetSuccessView successView;
    private String TAG = "CardControlFragment";
    private FullDeviceNo fullDeviceNo;
    private CustomInputView inputRoomNo;
    private CustomInputView inputCardNo;
    private int roomNoLen = 4;
    private int cardNoLen = 6;
    private String currentCardNo = "";
    private String currentRoomNo = "";
    private LinearLayout linClear;
    private LinearLayout linInput;
    private String extra;
    private RelativeLayout mRlRoomNo;
    //是否正在显示提示界面
    private boolean isShowTip = true;
    private UserInfoDao userInfoDao;
    private RoomNoHelper mRoomNoHelper;
    private boolean isStair; //是否梯口机


    @Override
    public int getLayout() {
        return R.layout.fragment_setting_card_control;
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {

    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        inputRoomNo = (CustomInputView) getContentView().findViewById(R.id.it_room_no);
        inputCardNo = (CustomInputView) getContentView().findViewById(R.id.it_card_no);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
        linClear = (LinearLayout) getContentView().findViewById(R.id.lin_clear);
        linInput = (LinearLayout) getContentView().findViewById(R.id.lin_input);
        mRlRoomNo = (RelativeLayout) getContentView().findViewById(R.id.rl_room_num);
    }

    @Override
    public void initAdapter() {
        tvTitle.setText(getString(R.string.setting_rule_set));
        fullDeviceNo = new FullDeviceNo(getContext());

        if (userInfoDao == null) {
            userInfoDao = new UserInfoDao();
        }

        isStair = fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR;

        //房号长度
        roomNoLen = fullDeviceNo.getRoomNoLen();
        //卡号长度
        cardNoLen = ParamDao.getCardNoLen();

        //获取住户列表
        if (roomNoLen == 4) {
            mRoomNoHelper = new RoomNoHelper();
            mRoomNoHelper.reset();
            currentRoomNo = mRoomNoHelper.getCurrentRoomNo();
        }


    }

    private void setCount() {
        if (mRoomNoHelper != null) {
            //设置列表计数
            if (!currentRoomNo.equals(mRoomNoHelper.getCurrentRoomNo())) {
                mRoomNoHelper.reset();
            }
            currentRoomNo = mRoomNoHelper.getNextRoomNo();
        }
    }


    @Override
    public void initListener() {
        //设置卡状态
        SinglechipClientProxy.getInstance().setCardState(1);
        //获取卡号监听
        SinglechipClientProxy.getInstance().setCardNoListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        successView.setSuccessListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            extra = bundle.getString(Constant.KEY_PARAM);
            switch (extra) {
                //添加卡
                case Constant.SetCardManageId.CARD_ADD:
                    initAddCard(currentRoomNo, currentCardNo);
                    //显示提示
                    successView.showSuccessView(getString(R.string.setting_card_tip), 4000, new ISetCallBackListener() {
                        @Override
                        public void success() {
                            isShowTip = false;
                        }

                        @Override
                        public void fail() {

                        }
                    });
                    break;
                //删除卡
                case Constant.SetCardManageId.CARD_DELETE:
                    isShowTip = false;
                    initDeleteCard(currentRoomNo, currentCardNo);
                    break;
                //清空卡
                case Constant.SetCardManageId.CARD_CLEAR:
                    initClearAll();
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //恢复刷卡进门状态
        SinglechipClientProxy.getInstance().setCardState(0);
    }

    void initClearAll() {
        tvTitle.setText(getString(R.string.setting_card_del_all));
        linClear.setVisibility(View.VISIBLE);
        linInput.setVisibility(View.GONE);
    }

    void initDeleteCard(String currentRoomNo, String currentCardNo) {
        if (isAdded()) {
            tvTitle.setText(getString(R.string.setting_card_delete));
        }
        editInputView(currentRoomNo, currentCardNo);
    }

    void initAddCard(String currentRoomNo, String currentCardNo) {
        if (isAdded()) {
            tvTitle.setText(getString(R.string.setting_card_add));
        }
        editInputView(currentRoomNo, currentCardNo);
    }

    private void editInputView(String currentRoomNo, String currentCardNo) {
        linClear.setVisibility(View.GONE);
        linInput.setVisibility(View.VISIBLE);

        if ((fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) && roomNoLen == 4) {
            mRlRoomNo.setVisibility(View.VISIBLE);
            inputRoomNo.setFirstFlash(false).init(currentRoomNo, roomNoLen, CustomInputAdapter.INPUT_TYPE_1);
            inputRoomNo.setCount(roomNoLen);
            inputCardNo.setFirstFlash(true).init(currentCardNo, cardNoLen, CustomInputAdapter.INPUT_TYPE_1);
        } else {
            //区口机
            mRlRoomNo.setVisibility(View.GONE);
            inputCardNo.setFirstFlash(true).init(currentCardNo, cardNoLen, CustomInputAdapter.INPUT_TYPE_1);
            currentId = cardNoId;
        }
    }


    @Override
    public void onKeyBoard(int viewId, String kid) {
        if (extra.equals(Constant.SetCardManageId.CARD_ADD) && isShowTip && !Constant.KEY_CANCLE.equals(kid)) {
            if (Constant.KEY_CONFIRM.equals(kid)) {
                successView.removeTipTextView();
                isShowTip = false;
            }
            return;
        }
        switch (kid) {
            case Constant.KEY_CANCLE:
                //退出界面
                exitFragment(this);
                break;
            case Constant.KEY_CONFIRM:
                isShowTip = false;
                switch (extra) {
                    //添加卡
                    case Constant.SetCardManageId.CARD_ADD:
                        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
                            if (currentRoomNo.length() < roomNoLen - 1 || currentCardNo.length() < cardNoLen) {
                                return;
                            }
                        } else {
                            if (currentCardNo.length() < cardNoLen) {
                                return;
                            }
                        }
                        //添加卡并保存
                        addCardToSave();
                        break;
                    //删除卡
                    case Constant.SetCardManageId.CARD_DELETE:
                        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
                            if (currentRoomNo.length() == roomNoLen && inputCardNo.getNum().equals("")) {
                                //删除某住户的所有卡
                                int code = delCard(currentRoomNo);
                                if (code == 1) {
                                    //删除成功
                                    successView.showSuccessView(getString(R.string.setting_success));
                                    saveDatas();
                                } else {
                                    //删除失败
                                    successView.showSuccessView(getString(R.string.setting_fail));
                                }
                                return;
                            } else {
                                if (currentRoomNo.length() < roomNoLen - 1 || currentCardNo.length() < cardNoLen) {
                                    return;
                                }
                            }
                        } else {
                            if (currentCardNo.length() < cardNoLen) {
                                return;
                            }
                        }
                        //删除卡并保存
                        deleteCardToSave();
                        break;
                    //清空卡
                    case Constant.SetCardManageId.CARD_CLEAR:
                        int code = CardClient.getInstance().ClearCard();
                        if (code == 1) {
                            userInfoDao.clearAllCards();
                            //成功
                            successView.showSuccessView(getString(R.string.setting_success));
                            saveDatas();
                        } else {
                            //失败
                            successView.showSuccessView(getString(R.string.setting_fail));
                        }
                        break;
                }
                break;
            case Constant.KEY_UP:
                //如果房号长度是4，获取住户列表
                changeRoomNo(Constant.KEY_UP);
                break;
            case Constant.KEY_DELETE:
                if (Constant.SetCardManageId.CARD_CLEAR.equals(extra)) {
                    return;
                }
                switch (currentId) {
                    case roomNoId:
                        if (inputRoomNo.getCount() != 0) {
                            inputRoomNo.deleteNum("");
                        }
                        currentRoomNo = inputRoomNo.getNum();
                        break;
                    case cardNoId:
                        if (inputCardNo.getCount() == 0 && mRlRoomNo.getVisibility() == View.VISIBLE) {
                            inputRoomNo.deleteNum("");
                            inputCardNo.setFirstFlash(false).setEndFlash(false).notifychange();
                            currentId = roomNoId;
                        }
                        inputCardNo.deleteNum("");
                        if (inputCardNo.getCount() == 0 && mRlRoomNo.getVisibility() == View.GONE) {
                            inputCardNo.setFirstFlash(true).setEndFlash(false).notifychange();
                        }
                        currentCardNo = inputCardNo.getNum();
                        break;
                    default:
                        break;
                }
                break;
            case Constant.KEY_NEXT:
                //如果房号长度是4，获取住户列表
                changeRoomNo(Constant.KEY_NEXT);
                break;
            default:
                if (Constant.SetCardManageId.CARD_CLEAR.equals(extra)) {
                    return;
                }
                switch (currentId) {
                    case roomNoId:
                        if (inputRoomNo.getCount() == roomNoLen - 1) {
                            inputCardNo.setCount(0);
                            inputCardNo.setFirstFlash(true).notifychange();
                            inputRoomNo.setEndFlash(false);
                            currentId = cardNoId;
                        }
                        inputRoomNo.addNum(kid);
                        currentRoomNo = inputRoomNo.getNum();
                        break;
                    case cardNoId:
                        inputCardNo.setEndFlash(false);
                        inputCardNo.addNum(kid);
                        currentCardNo = inputCardNo.getNum();
                        break;
                }
                break;
        }
    }

    private void changeRoomNo(String keyId) {
        if (Constant.SetCardManageId.CARD_CLEAR.equals(extra)) {
            return;
        }
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
            if (keyId.equals(Constant.KEY_UP)) {
                currentRoomNo = mRoomNoHelper.getPreviousRoomNo();
            }else {
                currentRoomNo = mRoomNoHelper.getNextRoomNo();
            }
            inputRoomNo.setText(currentRoomNo);
            if (inputCardNo.getNum().equals("")) {
                inputCardNo.setFirstFlash(true).setEndFlash(false).notifychange();
                inputCardNo.setCount(0);
                currentId = cardNoId;
            }
        }
    }

    private void deleteCardToSave() {
        currentRoomNo = inputRoomNo.getNum();
        currentCardNo = inputCardNo.getNum();
        //梯口号
        int code = delCard(currentRoomNo, currentCardNo);
        if (code == 1) {
            //成功
            successView.showSuccessView(getString(R.string.setting_success));
            saveDatas();
        } else {
            //失败
            successView.showSuccessView(getString(R.string.setting_fail));
        }
    }

    private void addCardToSave() {
        currentRoomNo = inputRoomNo.getNum();
        currentCardNo = inputCardNo.getNum();
        int code = SinglechipClientProxy.getInstance().addCard(currentRoomNo, currentCardNo,0x01, 0, "", 0, 0, -2);
        if (code == 1) {
            //成功
            successView.showSuccessView(getString(R.string.setting_success));
            saveDatas();
        } else {
            //失败
            successView.showSuccessView(getString(R.string.setting_fail));
        }
    }


    @Override
    public void success() {
    }

    private void saveDatas() {
        switch (extra) {
            //添加卡
            case Constant.SetCardManageId.CARD_ADD:
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR && roomNoLen == 4) {
                    //添加成功，保存数据
                    userInfoDao.addCard(currentCardNo, currentRoomNo, 0x01);
                    //设置列表计数
                    setCount();
                } else {
                    //添加成功，保存数据
                    userInfoDao.addCard(currentCardNo, "", 0x01);
                }
                initAddCard(currentRoomNo, "");
                break;
            //删除卡
            case Constant.SetCardManageId.CARD_DELETE:
                //设置列表计数
                setCount();
                initDeleteCard(currentRoomNo, "");
                break;
            //清空卡
            case Constant.SetCardManageId.CARD_CLEAR:
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFragment(CardControlFragment.this);
                    }
                }, 2000);
                break;
        }
    }

    @Override
    public void fail() {
        exitFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "当前房号：" + currentRoomNo + "当前卡号：" + currentCardNo);
    }


    @Override
    public void onCardNo(final String cardNo) {

        if (isShowTip) {
            return;
        }

        //获取到刷卡的卡号
        switch (extra) {
            //添加卡
            case Constant.SetCardManageId.CARD_ADD:
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    //梯口机
                    if (roomNoLen == 4 && inputRoomNo.getNum().length() != 4) {
                        return;
                    }
                }
                showCardNo(cardNo);
                //延时两秒添加保存
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            addCardToSave();
                        }
                    }
                }, 2000);
                break;
            //删除卡
            case Constant.SetCardManageId.CARD_DELETE:
                if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    //梯口机
                    if (roomNoLen == 4 && inputRoomNo.getNum().length() != 4) {
                        return;
                    }
                }
                showCardNo(cardNo);
                //延时两秒删除保存
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            deleteCardToSave();
                        }
                    }
                }, 2000);
                break;
        }
    }

    private void showCardNo(String cardNo) {
        inputCardNo.setText(cardNo);
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
