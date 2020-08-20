package com.mili.smarthome.tkj.present;

public interface IDriverSingerListener {
    /**
     * @param cardState 卡状态
     */
    void onCardState(int cardState, String roomNo);
}
