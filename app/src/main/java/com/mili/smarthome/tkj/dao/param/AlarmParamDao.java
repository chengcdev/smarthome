package com.mili.smarthome.tkj.dao.param;

/**
 * 报警参数访问类
 */
public class AlarmParamDao {

    public static final String ALARM_PARAM = "alarm_param";

    public static final String KEY_FORCE_OPEN = "alarm_force_open";

    /**
     * 强行开门报警
     * @return 0不启用，1启用
     */
    public static int getForceOpen() {
        return ParamDao.queryParamValue(KEY_FORCE_OPEN, 0);
    }

    /**
     * 强行开门报警
     * @param value 0不启用，1启用
     */
    public static void setForceOpen(int value) {
        ParamDao.saveParam(ALARM_PARAM, KEY_FORCE_OPEN, value);
    }

}
