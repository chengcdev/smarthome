package com.example.authrolibrary.helper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.authrolibrary.constants.CommonDefind;
import com.example.authrolibrary.entity.AuthCodeEntity;
import com.example.authrolibrary.entity.AutuParamEntity;
import com.example.authrolibrary.entity.DeviceParamEntity;
import com.example.authrolibrary.entity.RequestEntity;
import com.example.authrolibrary.http.RequsetServer;
import com.example.authrolibrary.http.RetrofitManage;
import com.example.authrolibrary.interf.IAuthDeviceListener;
import com.example.authrolibrary.utils.AuthUtils;
import com.example.authrolibrary.utils.DialogUtils;
import com.example.authrolibrary.utils.FileUtils;
import com.example.authrolibrary.utils.MD5Utils;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthDeviceHelper {

    private static String appKey = CommonDefind.AuthConfig.AUTH_APP_KEY;
    private static String appSecret = CommonDefind.AuthConfig.AUTH_APP_SECRET;
    private static String rsa = CommonDefind.AuthConfig.AUTH_RSA;
    //设备类型
    private static String deviceType = CommonDefind.AuthConfig.DEVICE_TYPE;
    //协议版本号
    private static String agreementVer = CommonDefind.AuthConfig.AGREEMENT_VERSION;
    //设备版本号
    private static String deviceVersion = "";
    private static AlertDialog mDialog;
    /**
     * 开始授权
     * @param context context
     * @param authDeviceListener 授权回调
     */
   public static void startAuth(final Context context, AutuParamEntity paramEntity, final IAuthDeviceListener authDeviceListener) {

        appKey = paramEntity.getAppKey();
        appSecret = paramEntity.getAppSecret();
        rsa = paramEntity.getRsa();
        deviceType = paramEntity.getDeviceType();
        agreementVer = paramEntity.getAgreementVer();
        deviceVersion = paramEntity.getDeviceVersion();

        DialogUtils dialogUtils = new DialogUtils();
        mDialog = dialogUtils.loadingDialog(context);

        RequestEntity requestBean = new RequestEntity();
        requestBean.setAppkey(appKey);
        requestBean.setDevice(getDeviceJson());
        requestBean.setVer(agreementVer);
        requestBean.setSign(getSign());
//        Gson gson = new Gson();
//        String json = gson.toJson(requestBean);
        //请求
        RequsetServer requsetServer = RetrofitManage.getRetrofit().create(RequsetServer.class);
        Call<AuthCodeEntity> activationDeviceRequset = requsetServer.getAuthCode(requestBean);
        activationDeviceRequset.enqueue(new Callback<AuthCodeEntity>() {
            @Override
            public void onResponse(@NonNull Call<AuthCodeEntity> call, @NonNull Response<AuthCodeEntity> response) {
                //请求成功
//                Log.e(" AuthDeviceHelper", "  startAuth onResponse" + response.toString());
                AuthCodeEntity activationDeviceBean = response.body();
                if (activationDeviceBean != null) {
                    int returnCode = activationDeviceBean.getErrorCode();
                    String errorMsg = activationDeviceBean.getErrorMsg();
//                    Log.e("  AuthDeviceHelper", "  errorMsg : " + errorMsg);

                    switch (returnCode) {
                        case CommonDefind.RetrunCode.CODE_SUCCESS:
                        case CommonDefind.RetrunCode.CODE_ACTIVATION:
                            AuthCodeEntity.BodyBean authBean = activationDeviceBean.getBody();
                            if (authBean != null) {
                                String authCode = authBean.getAuthCode();
//                                Log.e(" AuthDeviceHelper", "  authCode : " + authCode);
                                //保存文件
                                FileUtils.writeAuthFile(authCode);
                                authDeviceListener.onSucces();
                            }
                            break;
                        case CommonDefind.RetrunCode.CODE_NO_DEVICE:
                        case CommonDefind.RetrunCode.CODE_SIGN_ERROR:
                        case CommonDefind.RetrunCode.CODE_NO_ORDER:
                        case CommonDefind.RetrunCode.CODE_ACTIVATION_OVERDUE:
                        case CommonDefind.RetrunCode.CODE_UNKNOWN:
                            Toast.makeText(context,errorMsg,Toast.LENGTH_LONG).show();
                            break;
                    }

                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthCodeEntity> call, @NonNull Throwable t) {
//                Log.e(" AuthDeviceHelper", " onFailure : " + t.getMessage());
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                //请求失败
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
                authDeviceListener.onFail();
            }
        });


    }

    private static String getSign() {
        DeviceParamEntity deviceParamBean = new DeviceParamEntity();
        deviceParamBean.setId(AuthUtils.getDeviceId());
        deviceParamBean.setMac(AuthUtils.getMacAddr());
        deviceParamBean.setSn(AuthUtils.getSerialNumber());
        deviceParamBean.setType(deviceType);
        deviceParamBean.setVersion(deviceVersion);

        Gson gson = new Gson();
        String result = gson.toJson(deviceParamBean);

        String sign = MD5Utils.toMD5String(appKey + result + appSecret).toLowerCase();
        return sign;
    }

    private static DeviceParamEntity getDeviceJson() {
        DeviceParamEntity deviceParamBean = new DeviceParamEntity();
        deviceParamBean.setId(AuthUtils.getDeviceId());
        deviceParamBean.setSn(AuthUtils.getSerialNumber());
        deviceParamBean.setMac(AuthUtils.getMacAddr());
        deviceParamBean.setType(deviceType);
        deviceParamBean.setVersion(deviceVersion);

        return deviceParamBean;
    }

    /**
     * 是否授权
     * @return true 授权过 false 未授权
     */
    public static boolean isAuth(Context context, AutuParamEntity paramEntity) {
        appKey = paramEntity.getAppKey();
        appSecret = paramEntity.getAppSecret();
        rsa = paramEntity.getRsa();

        boolean isAuth = false;
        //检查是否有读写权限
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String authCode = FileUtils.readAuthFile();
            if (authCode == null || authCode.equals("")) {
                return false;
            }

            // TODO: 2019/9/10 若授权文件存在且获取不到本机mac，可以不需要授权，返回true
            if (AuthUtils.getMacAddr() == null || AuthUtils.getMacAddr().equals("")) {
                return true;
            }

            isAuth = AuthUtils.getIsAuth(authCode, rsa, appKey, appSecret);
        }
        return isAuth;
    }

}
