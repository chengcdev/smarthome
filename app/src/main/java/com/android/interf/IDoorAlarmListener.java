package com.android.interf;

/**
 * 门磁报警
 */
public interface IDoorAlarmListener {
    /**
     * @param doorAlarmType 1:强行开门报警  2: 门未关超时报警  3:防拆破坏报警
     */
    void onDoorAlarm(int doorAlarmType);
}
