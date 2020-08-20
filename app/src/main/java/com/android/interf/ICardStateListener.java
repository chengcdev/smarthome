package com.android.interf;

/**
 * 刷卡状态回调
 */
public interface ICardStateListener {
    /**
     * 按键触发
     * 0：无效卡刷卡  1: 有效卡刷卡进门  2、巡更卡刷卡  3、带事件的巡更卡刷卡 4: 管理员卡
     */
    void onCardState(int state, String roomNo);
}
