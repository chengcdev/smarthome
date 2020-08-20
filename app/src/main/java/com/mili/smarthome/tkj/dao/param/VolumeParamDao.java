package com.mili.smarthome.tkj.dao.param;

import com.mili.smarthome.tkj.utils.SystemSetUtils;

/**
 * 声音参数访问类
 */
public class VolumeParamDao {

    public static final String VOLUME_PARAM = "volume_param";

    public static final String KEY_MEDIA_VOLUME = "media_volume";
    public static final String KEY_TIP_VOLUME = "tip_volume";
    public static final String KEY_KEY_VOLUME = "key_volume";
    public static final String KEY_CALL_VOLUME = "call_volume";

    /**
     * 媒体静音
     * @return 0关闭，1启用
     */
    public static int getMediaVolume() {
        return ParamDao.queryParamValue(KEY_MEDIA_VOLUME, 0);
    }

    /**
     * 媒体静音
     * @param value 0关闭，1启用
     */
    public static void setMediaVolume(int value) {
        ParamDao.saveParam(VOLUME_PARAM, KEY_MEDIA_VOLUME, value);
    }

    /**
     * 提示音
     * @return 0关闭，1启用
     */
    public static int getTipVolume() {
        return ParamDao.queryParamValue(KEY_TIP_VOLUME, 1);
    }

    /**
     * 提示音
     * @param value 0关闭，1启用
     */
    public static void setTipVolume(int value) {
        ParamDao.saveParam(VOLUME_PARAM, KEY_TIP_VOLUME, value);
    }

    /**
     * 按键音
     * @return 0关闭，1启用
     */
    public static int getKeyVolume() {
        return ParamDao.queryParamValue(KEY_KEY_VOLUME, 1);
    }

    /**
     * 按键音
     * @param value 0关闭，1启用
     */
    public static void setKeyVolume(int value) {
        SystemSetUtils.setEnableKeyVoice(value != 0);
        ParamDao.saveParam(VOLUME_PARAM, KEY_KEY_VOLUME, value);
    }

    /**
     * 通话音量
     */
    public static int getCallVolume() {
        return ParamDao.queryParamValue(KEY_CALL_VOLUME, 5);
    }

    /**
     * 通话音量
     */
    public static void setCallVolume(int value) {
        SystemSetUtils.setCallVolume(value);
        ParamDao.saveParam(VOLUME_PARAM, KEY_CALL_VOLUME, value);
    }
}
