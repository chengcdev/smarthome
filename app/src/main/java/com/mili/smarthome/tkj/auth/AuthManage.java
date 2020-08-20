package com.mili.smarthome.tkj.auth;


import android.content.Context;

import com.example.authrolibrary.entity.AutuParamEntity;
import com.example.authrolibrary.helper.AuthDeviceHelper;
import com.example.authrolibrary.interf.IAuthDeviceListener;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.utils.SystemSetUtils;


/**
 * 授权类
 */

public class AuthManage {

    /**
     * 开始授权
     * @param context activity
     */
    public static void startAuth(final Context context) {

        AutuParamEntity autuParamEntity = new AutuParamEntity();
        autuParamEntity.setAppKey(Const.AuthConfig.AUTH_APP_KEY);
        autuParamEntity.setAppSecret(Const.AuthConfig.AUTH_APP_SECRET);
        autuParamEntity.setRsa(Const.AuthConfig.AUTH_RSA);
        autuParamEntity.setDeviceType(BuildConfig.FLAVOR_MODEL);
        autuParamEntity.setAgreementVer(Const.AuthConfig.AGREEMENT_VER);
        autuParamEntity.setDeviceVersion(BuildConfig.softVersionType + "_" + BuildConfig.buildVersionTime);

        AuthDeviceHelper.startAuth(context, autuParamEntity, new IAuthDeviceListener() {
            @Override
            public void onSucces() {
                //重启设备
                SystemSetUtils.rebootDevice();
            }

            @Override
            public void onFail() {

            }
        });
    }

    /**
     * @return 设备是否授权合法
     */
    public static boolean isAuth() {
        if (!Const.CommonConfig.mIsAppAuth){
            return true;
        }
        AutuParamEntity autuParamEntity = new AutuParamEntity();
        autuParamEntity.setAppKey(Const.AuthConfig.AUTH_APP_KEY);
        autuParamEntity.setAppSecret(Const.AuthConfig.AUTH_APP_SECRET);
        autuParamEntity.setRsa(Const.AuthConfig.AUTH_RSA);
        return AuthDeviceHelper.isAuth(App.getInstance(),autuParamEntity);
    }

    /**
     * @return 1 授权 0 未授权
     */
    public static int getAuthState() {
        return isAuth() ? 1 : 0;
    }
}
