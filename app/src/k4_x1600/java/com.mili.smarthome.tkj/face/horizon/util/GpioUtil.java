package com.mili.smarthome.tkj.face.horizon.util;

import android.util.Log;

import com.hobot.hrxsys.HRXSys;

public class GpioUtil {
    private static final String TAG = GpioUtil.class.getName();
    private static long fd = -1;
    private static HRXSys hrxSys = null;

    private static boolean irLamp = false;//红外开关状态
    private static boolean wlLamp = false;//白光开关状态

    /*白光补光，光敏电阻，风扇，红外补光灯，继电器，蜂鸣器GPIO注册*/
    public static void init() {
        if (hrxSys == null) {
            hrxSys = HRXSys.getInstance();
            fd = hrxSys.Open();
            if (fd == 0) {
                fd = -1;
            }
            Log.d(TAG, "fd = " + fd);
        }
    }


    /*控制风扇开关*/
    public static int writeFan(boolean open) {
        Log.d(TAG, "writeFan in");
        int ret = -1;
        if (fd != -1) {
            ret = hrxSys.writeFan(open);
            Log.d(TAG, "ret = " + ret);
        }
        Log.d(TAG, "writeFan out");
        return ret;
    }

    /*获取光敏电阻状态*/
    public static int getPhotoresistance() {
        int ret = -1;
        if (fd != -1) {
            ret = hrxSys.getPhotoresistance();// 白天高电平3，晚上返回2
        }
        return ret;
    }

    /*控制白光灯开关*/
    public static int writeWhiteLightLamp(boolean open) {
        if (open == wlLamp) {
            return 0;
        }
        int ret = -1;
        if (fd != -1) {
            ret = hrxSys.writeWhiteLightLamp(open);
            if (ret >= 0) {
                wlLamp = open;
            }
        }
        return ret;
    }

    /*控制红外灯开关*/
    public static int writeInfraredLampGpio(boolean open) {
        if (open == irLamp) {
            return 0;
        }
        int ret = -1;
        if (fd != -1) {
            ret = hrxSys.writeInfraredLampGpio(open);
            if (ret >= 0) {
                irLamp = open;
            }
        }
        return ret;
    }

    /*测试屏幕背光开关*/
    public static int writeScreenGpio(boolean open) {
        int ret = -1;
        if (fd != -1) {
            try {
                if (open) {
                    //HRXTrans.WriteCtlStr(fd, "wpb8v1");
                } else {
                    //HRXTrans.WriteCtlStr(fd, "wpb8v0");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static void reboot() {
        if (fd != -1) {
            hrxSys.sysReboot();
        }
    }

    /*测试CPU*/
    public static int writeCpuGpio(boolean open) {
        int ret = -1;
        if (fd != -1) {
            ret = hrxSys.writeCpuGpio(open);
        }
        return ret;
    }

    /*gpio资源释放*/
    public static void close() {
        if (fd != -1) {
            try {
                Log.d(TAG, "close in");
                hrxSys.Close(fd);
                fd = -1;
                hrxSys = null;
                Log.d(TAG, "close out");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}