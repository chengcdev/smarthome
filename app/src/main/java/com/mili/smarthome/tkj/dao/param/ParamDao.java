package com.mili.smarthome.tkj.dao.param;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.RealmUtils;
import com.mili.smarthome.tkj.entities.param.ParamModel;

import java.util.List;

public class ParamDao {

    public static String queryParamValue(String key, String defaultValue) {
        ParamModel model = queryParamModelByKey(key);
        if (model == null) {
            return defaultValue;
        } else {
            return model.getValue();
        }
    }

    public static int queryParamValue(String key, int defaultValue) {
        ParamModel model = queryParamModelByKey(key);
        if (model == null) {
            return defaultValue;
        } else {
            return model.getIntValue();
        }
    }

    public static ParamModel queryParamModelByKey(String key) {
        return RealmUtils.queryFirst(ParamModel.class, ParamModel.FIELD_KEY, key);
    }

    public static List<ParamModel> queryParamListByType(String type) {
        return RealmUtils.queryAll(ParamModel.class, ParamModel.FIELD_TYPE, type);
    }

    public static void saveParam(String paramType, String paramName, String paramValue) {
        saveParam(new ParamModel()
                .setType(paramType)
                .setKey(paramName)
                .setValue(paramValue));
    }

    public static void saveParam(String paramType, String paramName, int paramValue) {
        saveParam(new ParamModel()
                .setType(paramType)
                .setKey(paramName)
                .setValue(paramValue));
    }

    public static void saveParam(ParamModel paramModel) {
        RealmUtils.insertOrUpdate(paramModel);
    }

    public static void saveParamList(List<ParamModel> paramList) {
        RealmUtils.insertOrUpdate(paramList);
    }

    public static void saveParamArray(ParamModel... paramArray) {
        RealmUtils.insertOrUpdate(paramArray);
    }



    // ================================================================== //

    public static final String COMMON_PARAM = "common_param";

    public static final String KEY_CARD_NO_LENGTH = "card_no_length";
    public static final String KEY_ADMIN_PWD = "admin_pwd";
    public static final String KEY_AREA_NAME = "area_name";
    public static final String KEY_PWD_OPEN_MODE = "pwd_open_mode";
    public static final String KEY_POWER_SAVE = "power_save";
    public static final String KEY_SCREEN_PRO = "screen_pro";
    public static final String KEY_PWD_DYNAMIC = "pwd_dynamic";
    public static final String KEY_TOUCH_SENSITIVITY = "touch_sensitivity";
    public static final String KEY_CALL_TYPE = "call_type";
    public static final String KEY_ENABLE_CENTER = "enable_center";
    public static final String KEY_EVENT_PLATFORM = "event_platform";

    /**
     * 获取卡号位数
     */
    public static int getCardNoLen() {
        return ParamDao.queryParamValue(KEY_CARD_NO_LENGTH, 8);
    }

    /**
     * 设置卡号位数
     */
    public static void setCardNoLen(int value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_CARD_NO_LENGTH, value);
    }

    /**
     * 获取管理员密码
     */
    public static String getAdminPwd() {
        return ParamDao.queryParamValue(KEY_ADMIN_PWD, "13572468");
    }

    /**
     * 设置管理员密码
     */
    public static void setAdminPwd(String value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_ADMIN_PWD, value);
    }

    /**
     * 获取小区名称
     */
    public static String getAreaName() {
        ParamModel model = ParamDao.queryParamModelByKey(KEY_AREA_NAME);
        if (model == null) {
            return ContextProxy.getString(R.string.mian_title);
        } else {
            return model.getValue();
        }
    }

    /**
     * 设置小区名称
     */
    public static void setAreaName(String value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_AREA_NAME, value);
    }

    /**
     * 密码进门模式
     * @return 0简易模式，1高级模式
     */
    public static int getPwdDoorMode() {
        return ParamDao.queryParamValue(KEY_PWD_OPEN_MODE, 0);
    }

    /**
     * 密码进门模式
     * @param value 0简易模式，1高级模式
     */
    public static void setPwdDoorMode(int value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_PWD_OPEN_MODE, value);
    }

    /**
     * 省电模式
     * @return 0不启用，1启用
     */
    public static int getPowerSave() {
        return ParamDao.queryParamValue(KEY_POWER_SAVE, 1);
    }

    /**
     * 省电模式
     * @param value 0不启用，1启用
     */
    public static void setPowerSave(int value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_POWER_SAVE, value);
    }

    /**
     * 屏保设置
     * @return 0不启用，1启用
     */
    public static int getScreenPro() {
        return ParamDao.queryParamValue(KEY_SCREEN_PRO, 0);
    }

    /**
     * 屏保设置
     * @param value 0不启用，1启用
     */
    public static void setScreenPro(int value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_SCREEN_PRO, value);
    }

    /**
     * 是否使用动态密码
     * @return 0否，1是
     */
    public static int getPwdDynamic() {
        return ParamDao.queryParamValue(KEY_PWD_DYNAMIC, 0);
    }

    /**
     * 是否使用动态密码
     * @param value 0否，1是
     */
    public static void setPwdDynamic(int value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_PWD_DYNAMIC, value);
    }

    /**
     * 灵敏度设置
     * @return 0高，1中，2低
     */
    public static int getTouchSensitivity() {
        return ParamDao.queryParamValue(KEY_TOUCH_SENSITIVITY, 1);
    }

    /**
     * 灵敏度设置
     * @param value 0高，1中，2低
     */
    public static void setTouchSensitivity(int value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_TOUCH_SENSITIVITY, value);
    }

    /**
     * k7 呼叫方式
     * @return 0 编码式 1 直按式
     */
    public static int getCallType() {
        return ParamDao.queryParamValue(KEY_CALL_TYPE,0);
    }

    /**
     * k7 呼叫方式
     * @param value 0 编码式 1 直接式
     */
    public static void setCallType(int value) {
        ParamDao.saveParam(COMMON_PARAM,KEY_CALL_TYPE,value);
    }

    /**
     * k7 启用中心机
     * @return 0 否 1 是
     */
    public static int getEnableCenter() {
        return ParamDao.queryParamValue(KEY_ENABLE_CENTER,0);
    }

    /**
     * k7 启用中心机
     * @param value 0 否 1 是
     */
    public static void setEnableCenter(int value) {
        ParamDao.saveParam(COMMON_PARAM,KEY_ENABLE_CENTER,value);
    }

    /**
     * 获取事件上报平台
     * @return  0 管理中心 1 智慧云平台
     */
    public static int getEventPlatform() {
        return ParamDao.queryParamValue(KEY_EVENT_PLATFORM, 0);
    }

    /**
     * 设置事件上报平台
     * @param value 0 管理中心 1 智慧云平台
     */
    public static void setEventPlatform(int value) {
        ParamDao.saveParam(COMMON_PARAM, KEY_EVENT_PLATFORM, value);
    }
}
