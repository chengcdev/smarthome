package com.mili.smarthome.tkj.setting.entities;

import com.google.gson.reflect.TypeToken;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.base.K3Const;
import com.mili.smarthome.tkj.utils.IOUtils;
import com.mili.smarthome.tkj.utils.JsonUtils;

import java.util.List;

public class SettingFuncManager {

    public static List<SettingFunc> getFuncList() {
        String jsonString = IOUtils.readFromAssets(ContextProxy.getContext(), "setting_func.json");
        return JsonUtils.fromJson(jsonString, new TypeToken<List<SettingFunc>>() {}.getType());
    }

    public static void notifyPwdModeChanged() {
        ContextProxy.sendBroadcast(K3Const.ACTION_PWD_MODE_CHANGED);
    }

    public static void notifyFaceRecogChanged() {
        ContextProxy.sendBroadcast(K3Const.ACTION_FACE_RECOG_CHANGED);
    }

    public static void notifyQrCodeChanged() {
        ContextProxy.sendBroadcast(K3Const.ACTION_QR_CODE_CHANGED);
    }
}
