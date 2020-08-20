package com.android.interf;

public interface IFingerEventListener {

    /** 正常 */
    int FINGER_NORMAL = 0;

    /** 未放手指：请稍用力按手指 */
    int FINGER_NONE = 1;

    /** 湿 */
    int FINGER_WET = 10;

    /** 干 */
    int FINGER_DRY = 11;

    /** 偏上：请下移手指 */
    int FINGER_TOP = 20;

    /** 偏下：请上移手指 */
    int FINGER_BOTTOM = 21;

    /** 偏左：请右移手指 */
    int FINGER_LEFT = 22;

    /** 偏右：请左移手指 */
    int FINGER_RIGHT = 23;

    /** 偏左上：请向右下方平移手指 */
    int FINGER_LEFT_TOP = 24;

    /** 偏左下：请向右上方平移手指 */
    int FINGER_LEFT_BOTTOM = 25;

    /** 偏右上：请向左下方平移手指 */
    int FINGER_RIGHT_TOP = 26;

    /** 偏右下：请向左上方平移手指 */
    int FINGER_RIGHT_BOTTOM = 27;

    /** 面积偏小：请平放手指，与传感器充分接触 */
    int FINGER_SMALL = 30;

    /** 未知 */
    int FINGER_UNKNOWN = 255;

    /**
     * 指纹采集过程回调
     * @param code 指纹错误码
     * @param press 0:抬起  1:按下
     * @param count 次数
     */
    void onFingerCollect(int code, int press, int count);

    /**
     * 指纹采集成功回调
     * @param code 0-成功；3-指纹库满；else-失败
     * @param fingerId 指纹ID
     * @param valid 指纹有效标志：大于零有效，其他的无效
     * @param fingerData 指纹特征值
     */
    void onFingerAdd(int code, int fingerId, int valid, byte[] fingerData);

    /**
     * 指纹开门回调
     * @param code 1-指纹正确开锁；0-指纹错误；2-保持手指按下；3-请稍候；4-请重按手指
     */
    void onFingerOpen(int code, String roomNo);
}
