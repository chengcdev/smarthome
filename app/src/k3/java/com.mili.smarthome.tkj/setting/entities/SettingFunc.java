package com.mili.smarthome.tkj.setting.entities;

import com.android.CommTypeDef;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

import java.util.ArrayList;
import java.util.List;

public class SettingFunc implements SettingCode {

    private String code;
    //private String name;
    private List<SettingFunc> children;

    public String getCode() {
        return code;
    }

    public String getName() {
        return getName(code);
    }

    public boolean isEnabled() {
        switch (code) {
            case PASSWORD_ADD:
            case PASSWORD_DEL:
            case PASSWORD_CLEAR:
                return AppConfig.getInstance().getOpenPwdMode() == 0;

            case SET_FACE_RECOGNITION:
                return AuthManage.isAuth() && AppConfig.getInstance().getFaceModule() == 1;

            case SET_FINGER:
                return SinglechipClientProxy.getInstance().isFingerWork();

            case SET_BODY_DETECTION:
                return AppConfig.getInstance().isFaceEnabled() || AppConfig.getInstance().isQrCodeEnabled();

            case SET_STAIR_NO:
                return AppConfig.getInstance().getDevType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR;

            case SET_AREA_NO:
                return AppConfig.getInstance().getDevType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA;

            case SET_APN:
                return BuildConfigHelper.isEnabledAPN();
        }
        return true;
    }

    public boolean hasChild() {
        if (children != null && children.size() > 0) {
            for (SettingFunc child : children) {
                if (child.isEnabled())
                    return true;
            }
        }
        return false;
    }

    public List<SettingFunc> getChildren() {
        ArrayList<SettingFunc> enableList = new ArrayList<>();
        if (children != null && children.size() > 0) {
            for (SettingFunc child : children) {
                if (child.isEnabled()) {
                    enableList.add(child);
                }
            }
        }
        return enableList;
    }

    public SettingFunc setCode(String code) {
        this.code = code;
        return this;
    }

    public SettingFunc setChildren(List<SettingFunc> children) {
        this.children = children;
        return this;
    }

    public static String getName(String funcCode) {
        String resName = "setting_" + funcCode;
        int resId = ContextProxy.getStringId(resName);
        if (resId == 0)
            return funcCode;
        else
            return ContextProxy.getString(resId);
    }
}
