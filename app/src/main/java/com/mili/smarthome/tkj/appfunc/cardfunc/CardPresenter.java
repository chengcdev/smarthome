package com.mili.smarthome.tkj.appfunc.cardfunc;

import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;

public interface CardPresenter {

    /**
     * 添加单张卡
     */
    boolean addCard(String cardNo, String roomNo, int cardType);

    /**
     * 添加单张卡
     */
    boolean addCard(String roomNo, String cardNo, int cardType, int roomNoState, String keyID, int startTime, int endTime, int lifecycle);

    /**
     * 添加单张卡
     */
    boolean addCard(final UserCardInfoModels model);

    /**
     * 删除单张卡
     */
    boolean delCard(String cardNo, String roomNo);

    /**
     * 删除某住户的所有卡
     */
    boolean deleteUserCards(final String roomNo);

    /**
     * 清空所有卡
     */
    boolean clearCards();

    /**
     * 上位机批量添加卡
     */
    void notifyCardAdd(final UserCardInfoModels[] cardList);

    /**
     * 上位机批量删除卡
     */
    void notifyCardDel(String[] cardNos);

    /**
     * 上位机清空所有卡
     */
    void notifyCardClear();
}
