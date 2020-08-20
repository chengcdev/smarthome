package com.mili.smarthome.tkj.appfunc.cardfunc;

import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

import java.util.List;

public class CardPresenterImpl implements CardPresenter {

    private UserInfoDao mUserInfoDao;

    public CardPresenterImpl() {
        mUserInfoDao = new UserInfoDao();
    }

    @Override
    public boolean addCard(String roomNo, String cardNo, int cardType, int roomNoState, String keyID, int startTime, int endTime, int lifecycle) {
        int cardFreeCount = SinglechipClientProxy.getInstance().getCardFreeCount();
        if (cardFreeCount <= 0) {
            return false;
        }
        int result = SinglechipClientProxy.getInstance().addCard(roomNo, cardNo, cardType, roomNoState, keyID, startTime, endTime, lifecycle);
        if (result == 0x01) {
            mUserInfoDao.addCard(cardNo, roomNo, cardType);
            return true;
        }
        return false;
    }

    @Override
    public boolean addCard(String cardNo, String roomNo, int cardType) {
        return false;
    }

    @Override
    public boolean addCard(UserCardInfoModels model) {
        int cardFreeCount = SinglechipClientProxy.getInstance().getCardFreeCount();
        if (cardFreeCount <= 0) {
            return false;
        }
        int result = SinglechipClientProxy.getInstance().addCard(model.getRoomNo(), model.getCardNo(), model.getCardType(),
                model.getRoomNoState(), model.getKeyID(), model.getStartTime(), model.getEndTime(), model.getLifecycle());
        if (result == 0x01) {
            mUserInfoDao.addCard(model);
            return true;
        }
        return false;
    }

    @Override
    public boolean delCard(String cardNo, String roomNo) {
        // 删除卡对应的人脸
        FacePresenterProxy.delFaceInfo(cardNo);
        // 删除单张卡
        int result = SinglechipClientProxy.getInstance().delCard(roomNo, cardNo);
        if (result == 0x01) {
            mUserInfoDao.deleteCard(cardNo);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUserCards(String roomNo) {
        List<UserCardInfoModels> cardList = mUserInfoDao.queryByRoomNo(roomNo);
        for (UserCardInfoModels cardInfo : cardList) {
            // 删除卡对应的人脸
            FacePresenterProxy.delFaceInfo(cardInfo.getCardNo());
        }
        // 删除住户所有卡
        int result = SinglechipClientProxy.getInstance().delUserCard(roomNo);
        if (result == 0x01) {
            mUserInfoDao.deleteUserCards(roomNo);
            return true;
        }
        return false;
    }

    @Override
    public boolean clearCards() {
//        // 清空人脸数据库
//        FacePresenterProxy.clearFaceInfo();
        // 清空卡
        int result = SinglechipClientProxy.getInstance().clearCard();
        if (result == 0x01) {
            mUserInfoDao.clearAllCards();
            return true;
        }
        return false;
    }

    @Override
    public void notifyCardAdd(UserCardInfoModels[] cardList) {
        mUserInfoDao.addCards(cardList);
    }

    @Override
    public void notifyCardDel(String[] cardNos) {
        if (cardNos != null) {
            for (String cardNo : cardNos) {
                // 删除卡对应的人脸
                FacePresenterProxy.delFaceInfo(cardNo);
                // 删除单张卡
                mUserInfoDao.deleteCard(cardNo);
            }
        }
    }

    @Override
    public void notifyCardClear() {
//        // 清空人脸数据库
//        FacePresenterProxy.clearFaceInfo();
        // 清空卡
        mUserInfoDao.clearAllCards();
    }
}
