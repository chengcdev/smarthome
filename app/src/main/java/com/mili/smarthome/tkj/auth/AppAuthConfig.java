package com.mili.smarthome.tkj.auth;

import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;

public class AppAuthConfig {

    private static volatile AppAuthConfig authUtils;

    public static AppAuthConfig getInstance() {
        if (authUtils == null) {
            synchronized (AppAuthConfig.class) {
                if (authUtils == null) {
                    authUtils = new AppAuthConfig();
                }
            }
        }
        return authUtils;
    }

    /**
     * 初始化配置
     */
    public void initAuthConfig() {
        switch (BuildConfig.FLAVOR_MODEL) {
            case BuildConfigHelper.K3:
                build_K3_Version();
                break;
            case BuildConfigHelper.K4:
                build_K4_Version();
                break;
            case BuildConfigHelper.K6:
                build_K6_Version();
                break;
            case BuildConfigHelper.K7:
                build_K7_Version();
                break;
            case BuildConfigHelper.K4_X1600:
            case BuildConfigHelper.K4_X1600_GATE:
                build_K4_X1600_Version();
                break;
        }
    }

    /**
     * 冠林授权配置
     */
    private void AurineAuthConfig() {
        Const.AuthConfig.AUTH_APP_KEY = "";
        Const.AuthConfig.AUTH_APP_SECRET = "";
        Const.AuthConfig.AUTH_RSA = "";
    }

    /**
     * 地平线授权配置
     */
    private void K4_X1600_AuthConfig() {
        Const.AuthConfig.AUTH_APP_KEY = "d4QNz7kNvvcuXc0H";
        Const.AuthConfig.AUTH_APP_SECRET = "GIpKKqw8bHLR5csq";
        Const.AuthConfig.AUTH_RSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCuCyH/IvDjZ1iOVC+G7LEPa4It2ePl8z3yg/lTyL4/5OAJzPwXR8IVXikyJMObmjXKbJH/a9orjXqoFCxq/vtnCIowjbxqgL/xwjL5SyhA5+Tc20es3S38NbykR+XfsgV7ahA4wg3JFn0RTwDp2oj5bG5SQrjG5Bs/bKwcJbUczwIDAQAB";
    }

    /**
     * Rk3368
     */
    private void K3_K4_K6_AuthConfig() {
        Const.AuthConfig.AUTH_APP_KEY = "ZT6yith5GIBf472c";
        Const.AuthConfig.AUTH_APP_SECRET = "t4epT5l9svXTjsIK";
        Const.AuthConfig.AUTH_RSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCP4g1lpAw3KRoyIoKtuqIAIkg+2Cbet2iQC82lXAxmI8MQhJ624e24NKus3vI6OKZcohyYyqTOCEd7y9hvULeYolPw+5NKt4chT8cAMBY/LbIe+3Es6PN/jK7GeUZWeBwa+FD/v2E1R03gKlTQCUr7unpxY8/oZ3T+uzRuer/TnwIDAQAB";
    }


    private void build_K3_Version() {
        Const.CommonConfig.mIsAppAuth = false;
        K3_K4_K6_AuthConfig();
    }

    private void build_K4_Version() {
        Const.CommonConfig.mIsAppAuth = false;
        K3_K4_K6_AuthConfig();
    }

    private void build_K6_Version() {
        Const.CommonConfig.mIsAppAuth = false;
        K3_K4_K6_AuthConfig();
    }

    private void build_K7_Version() {
        AurineAuthConfig();
    }

    private void build_K4_X1600_Version() {
        Const.CommonConfig.mIsAppAuth = true;
        K4_X1600_AuthConfig();
    }

}
