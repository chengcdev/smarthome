package com.android.interf;

/**
 * 读卡器回调
 */
public interface ICardReaderListener {

    /**
     * @param cardId 卡号
     * @param result 0-有效卡，1-无效卡
     */
    void onCardRead(int cardId, int result);
}
