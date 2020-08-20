package com.mili.smarthome.tkj.appfunc;

import com.android.client.ScanQrClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.dao.param.FaceParamDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.EthernetUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.wf.wffrapp;

import java.io.File;

import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassConfig;

public class AppConfig {

    private static AppConfig mInstance;
    public static AppConfig getInstance() {
        if (mInstance == null) {
            synchronized (AppConfig.class) {
                if (mInstance == null) {
                    mInstance = new AppConfig();
                }
            }
        }
        return mInstance;
    }

    private byte mDevType;
    private String mAreaName;

    /** 人脸厂商信息：0印度，1旷视，2地平线 */
    private int mFaceManufacturer = 1;

    /** 人脸模块：0禁用 1启用 */
    private int mFaceModule;
    /** 人脸识别：0禁用 1启用 */
    private int mFaceRecognition;
    /** 安全级别：0高 1正常 2普通 */
    private int mFaceSafeLevel;
    /** 人脸活体检测：0禁用 1启用 */
    private int mFaceLiveCheck;
    /** IPC地址 */
    private String mRtspUrl;

    /** 二维码开门：0扫码开门 1蓝牙开门器*/
    private int mQrOpenType;
    /** 扫码开门：0禁用 1启用 */
    private int mQrScanEnabled;
    /** 蓝牙开门器设备Id */
    private String mBluetoothDevId;

    /** 指纹识别：0禁用 1启用*/
    private int mFingerprint;

    /** 人体感应：0触发开屏 1人脸识别 2扫码开门 3蓝牙开门器 */
    private int mBodyInduction;

    /** 密码进门模式：0简易模式 1高级模式 */
    private int mOpenPwdMode;

    /** 屏保：0关闭 1启用 */
    private int mScreenSaver;
    /** 节电：0关闭 1启用 */
    private int mPowerSaving;

    /** 媒体静音：0关闭 1启用 **/
    private int mMediaVolume;
    /** 按键音：0关闭 1启用 **/
    private int mKeyVolume;
    /** 提示音：0关闭 1启用 **/
    private int mTipVolume;

    /** 是否使用动态密码: 0否 1是 **/
    private int mPwdDynamic;
    /** 密码进门模式: 0简易模式 1高级模式 **/
    private int mPwdDoorMode;

    /** k7 呼叫方式: 0编码方式 1直按方式 **/
    private int mCallType;

    /** 事件上报平台: 0管理中心 1智慧云平台 **/
    private int mEventPlatform;

    private AppConfig() {
        init();
    }

    private void init() {
        FullDeviceNo fullDeviceNo = new FullDeviceNo(ContextProxy.getContext());
        mDevType = fullDeviceNo.getDeviceType();
        mAreaName = ParamDao.getAreaName();

        mFaceManufacturer = initFaceManufacturer();
        mFaceModule = FaceParamDao.getFaceModule();
        mFaceRecognition = FaceParamDao.getFaceRecognition();
        mFaceSafeLevel = FaceParamDao.getFaceSafeLevel();
        mFaceLiveCheck = FaceParamDao.getFaceLiveCheck();
        mRtspUrl = FaceParamDao.getRtspUrl();

        mQrOpenType = EntranceGuardDao.getQrOpenDoorType();
        mQrScanEnabled = EntranceGuardDao.getSweepCodeOpen();
        mBluetoothDevId = EntranceGuardDao.getBluetoothDevId();

        mFingerprint = EntranceGuardDao.getFingerprint();

        mBodyInduction = EntranceGuardDao.getBodyFeeling();

        mOpenPwdMode = ParamDao.getPwdDoorMode();
        mScreenSaver = ParamDao.getScreenPro();
        mPowerSaving = ParamDao.getPowerSave();

        mMediaVolume = VolumeParamDao.getMediaVolume();
        mKeyVolume = VolumeParamDao.getKeyVolume();
        mTipVolume = VolumeParamDao.getTipVolume();

        mPwdDynamic = ParamDao.getPwdDynamic();
        mPwdDoorMode = ParamDao.getPwdDoorMode();

        mCallType = ParamDao.getCallType();

        mEventPlatform = ParamDao.getEventPlatform();
    }

    public byte getDevType() {
        return mDevType;
    }

    public void setDevType(byte devType) {
        FullDeviceNo fullDeviceNo = new FullDeviceNo(ContextProxy.getContext());
        fullDeviceNo.setDeviceType(devType);
        this.mDevType = devType;
    }

    public String getAreaName() {
        return mAreaName;
    }

    public void setAreaName(String areaName) {
        ParamDao.setAreaName(areaName);
        this.mAreaName = areaName;
    }

    public boolean isFaceEnabled() {
        return (mFaceModule == 1 && mFaceRecognition == 1);
    }

    public boolean isFingerEnabled() {
        if (SinglechipClientProxy.getInstance().isFingerWork()) {
            return mFingerprint == 1;
        } else {
            return false;
        }
    }

    public boolean isQrCodeEnabled() {
        return (mQrOpenType == 0 && mQrScanEnabled == 1)
                || (mQrOpenType == 1 && mBluetoothDevId != null && mBluetoothDevId.length() > 0);
    }

    public int getFaceManufacturer() {
        return mFaceManufacturer;
    }

    public int getFaceManufacturerByInit() {
        mFaceManufacturer = initFaceManufacturer();
        return mFaceManufacturer;
    }

    public int getFaceModule() {
        return mFaceModule;
    }

    public int getFaceRecognition() {
        return mFaceRecognition;
    }

    public int getFaceSafeLevel() {
        return mFaceSafeLevel;
    }

    public int getFaceLiveCheck() {
        return mFaceLiveCheck;
    }

    public String getRtspUrl() {
        return mRtspUrl;
    }

    public int getQrOpenType() {
        return mQrOpenType;
    }

    public int getQrScanEnabled() {
        return mQrScanEnabled;
    }

    public String getBluetoothDevId() {
        return mBluetoothDevId;
    }

    public String getBluetoothQrCode() {
        if (!AuthManage.isAuth()) {
            return BuildConfigHelper.getSoftWareVer()
                    + "_" + BuildConfigHelper.getHardWareVer()
                    + "_" + EthernetUtils.getMacAddress();
        }
        return ScanQrClient.getInstance().GetQRencode(mBluetoothDevId);
    }

    public int getFingerprint() {
        return mFingerprint;
    }

    public int getBodyInduction() {
        return mBodyInduction;
    }

    public int getOpenPwdMode() {
        return mOpenPwdMode;
    }

    public int getScreenSaver() {
        return mScreenSaver;
    }

    public int getPowerSaving() {
        return mPowerSaving;
    }

    public int getMediaVolume() {
        return mMediaVolume;
    }

    public int getKeyVolume() {
        return mKeyVolume;
    }

    public int getTipVolume() {
        return mTipVolume;
    }

    public int getPwdDynamic() {
        return mPwdDynamic;
    }

    public int getPwdDoorMode() {
        return mPwdDoorMode;
    }

    public int getCallType() {
        return mCallType;
    }

    public int getEventPlatform() {
        return mEventPlatform;
    }

    public void setFaceModule(int faceModule) {
        FaceParamDao.setFaceModule(faceModule);
        if (faceModule == 0) {
            if (mBodyInduction == 1) {
                setBodyInduction(0);
            }
        } else if (faceModule == 1) {
            if (mFaceRecognition == 1) {
                setBodyInduction(1);
            }
        }
        this.mFaceModule = faceModule;
    }

    public void setFaceRecognition(int faceRecognition) {
        FaceParamDao.setFaceRecognition(faceRecognition);
        if (faceRecognition == 1) {
            setBodyInduction(1);
        } else {
            if (mBodyInduction == 1) {
                setBodyInduction(0);
            }
        }
        this.mFaceRecognition = faceRecognition;
    }

    public void setFaceSafeLevel(int faceSafeLevel) {
        FaceParamDao.setFaceSafeLevel(faceSafeLevel);
        this.mFaceSafeLevel = faceSafeLevel;
        switch (mFaceManufacturer) {
            case 0:
                wffrapp.setSafeLevel(faceSafeLevel);
                break;
            case 1:
                try {
                    FacePassConfig facePassConfig = MegviiFace.getInstance().getFacePassConfig(ContextProxy.getContext(), mFaceSafeLevel, mFaceLiveCheck);
                    MegviiFace.getInstance().mFacePassHandler.setConfig(facePassConfig);
                } catch (FacePassException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setFaceLiveCheck(int faceLiveCheck) {
        FaceParamDao.setFaceLiveCheck(faceLiveCheck);
        this.mFaceLiveCheck = faceLiveCheck;
        switch (mFaceManufacturer) {
            case 0:
                wffrapp.setSpoofing(faceLiveCheck);
                break;
            case 1:
                try {
                    FacePassConfig facePassConfig = MegviiFace.getInstance().getFacePassConfig(ContextProxy.getContext(), mFaceSafeLevel, mFaceLiveCheck);
                    MegviiFace.getInstance().mFacePassHandler.setConfig(facePassConfig);
                } catch (FacePassException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setRtspUrl(String rtspUrl) {
        FaceParamDao.setRtspUrl(rtspUrl);
        this.mRtspUrl = rtspUrl;
    }

    public void setQrOpenType(int qrOpenType) {
        EntranceGuardDao.setQrOpenDoorType(qrOpenType);
        this.mQrOpenType = qrOpenType;
    }

    public void setQrScanEnabled(int qrScanEnabled) {
        EntranceGuardDao.setSweepCodeOpen(qrScanEnabled);
        if (qrScanEnabled == 0) {
            if (mBodyInduction == 2 || mBodyInduction == 3) {
                setBodyInduction(0);
            }
        } else {
            if (mBodyInduction == 3) {
                setBodyInduction(2);
            }
        }
        this.mQrScanEnabled = qrScanEnabled;
    }

    public void setBluetoothDevId(String bluetoothDevId) {
        EntranceGuardDao.setBluetoothDevId(bluetoothDevId);
        if (bluetoothDevId == null || bluetoothDevId.length() == 0) {
            if (mBodyInduction == 2 || mBodyInduction == 3) {
                setBodyInduction(0);
            }
        } else {
            if (mBodyInduction == 2) {
                setBodyInduction(3);
            }
        }
        this.mBluetoothDevId = bluetoothDevId;
    }

    public void setFingerprint(int fingerprint) {
        EntranceGuardDao.setFingerprint(fingerprint);
        this.mFingerprint = fingerprint;
    }

    public void setBodyInduction(int bodyInduction) {
        EntranceGuardDao.setBodyFeeling(bodyInduction);
        this.mBodyInduction = bodyInduction;
    }

    public void setOpenPwdMode(int openPwdMode) {
        ParamDao.setPwdDoorMode(openPwdMode);
        this.mOpenPwdMode = openPwdMode;
    }

    public void setScreenSaver(int screenSaver) {
        ParamDao.setScreenPro(screenSaver);
        this.mScreenSaver = screenSaver;
    }

    public void setPowerSaving(int powerSaving) {
        ParamDao.setPowerSave(powerSaving);
        this.mPowerSaving = powerSaving;
    }

    public void setMediaVolume(int value) {
        VolumeParamDao.setMediaVolume(value);
        this.mMediaVolume = value;
    }

    public void setKeyVolume(int value) {
        VolumeParamDao.setKeyVolume(value);
        this.mKeyVolume = value;
    }

    public void setTipVolume(int value) {
        VolumeParamDao.setTipVolume(value);
        this.mTipVolume = value;
    }

    public void setPwdDynamic(int value) {
        ParamDao.setPwdDynamic(value);
        mPwdDynamic = value;
    }

    public void setPwdDoorMode(int value) {
        ParamDao.setPwdDoorMode(value);
        mPwdDoorMode = value;
    }

    public void setCallType(int callType) {
        ParamDao.setCallType(callType);
        mCallType = callType;
    }

    public void setEventPlatform(int platform) {
        ParamDao.setEventPlatform(platform);
        mEventPlatform = platform;
    }

    private int initFaceManufacturer() {
        int faceManufacturer = 0;
        if (BuildConfigHelper.isHorizon()) {
            faceManufacturer = 2;
        } else {
            faceManufacturer = AppPreferences.getFaceManufacturer();
            /* 若sp中显示非face++人脸，但是授权文件存在，则也认为是face++人脸 */
            if (faceManufacturer != 1) {
                String megviiLicDir = Const.Directory.SD + "/megvii/data";
                File dataFile = new File(megviiLicDir, "data");
                File ridFile = new File(megviiLicDir, "rid");
                if (dataFile.exists() && ridFile.exists()) {
                    faceManufacturer = 1;
                }
            }
        }
        LogUtils.d("[initFaceManufacturer] faceManufacturer = %d", faceManufacturer);
        return faceManufacturer;
    }
}
