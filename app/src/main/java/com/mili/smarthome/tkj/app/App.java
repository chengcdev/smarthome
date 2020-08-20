package com.mili.smarthome.tkj.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.MscTts;
import com.android.CommStorePathDef;
import com.android.Common;
import com.facebook.stetho.Stetho;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.appfunc.facefunc.WffrFaceDbManager;
import com.mili.smarthome.tkj.appfunc.facefunc.WffrFacePresenterImpl;
import com.mili.smarthome.tkj.auth.AppAuthConfig;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.dao.RealmHelper;
import com.mili.smarthome.tkj.dao.param.FaceParamDao;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.face.wffr.WffrUtils;
import com.mili.smarthome.tkj.ttsvoice.TtsBlockingQueue;
import com.mili.smarthome.tkj.ttsvoice.TtsComDefine;
import com.mili.smarthome.tkj.ttsvoice.TtsParamData;
import com.mili.smarthome.tkj.utils.FileUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.ThreadUtils;
//import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.wf.wffrapp;
import com.wf.wffrjni;

import java.io.File;

import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassConfig;

public class App extends Application {

    private static App mInstance;
    private ActivityLifecycleObserver mLifecycleObserver;
    private MscTts mTts;
    private TtsBlockingQueue mTtsBlockingQueue;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ContextProxy.setContext(this);
        inittts();
    }

    /**
     * 初始化
     */
    public boolean initialize() {
        //初始化授权
        AppAuthConfig.getInstance().initAuthConfig();
        printScreenInfo();
        initFiles();
        initCrashHandler();
        boolean result = initRealm();
        if (AuthManage.isAuth() && AppConfig.getInstance().getFaceModule() == 1) {
            int faceManufacturer = AppConfig.getInstance().getFaceManufacturer();
            LogUtils.d(" App >> faceManufacturer " + faceManufacturer);
            if (faceManufacturer == 0) {
                checkSdcardState();
                initWffrAssets();
            } else if (faceManufacturer == 1) {
                initMegviiAssets();
            }
        }
        registerLifecycleObserver();
        return result;
    }


    /**
     * 重启判断face++人脸的授权文件是否存在，若存在则进行初始化人脸
     * 解决梯口机上电启动后，由于sdcard目录未mounted挂载而导致无法读取授权文件，从而使face++人脸变成EI人脸的问题
     */
    private void checkSdcardState() {
        String state = Environment.getExternalStorageState();
        File sdFile = Environment.getExternalStorageDirectory();
        LogUtils.d("======== state=%s[%s]  sdcardDir=%s=========", state, Environment.MEDIA_MOUNTED, sdFile.getPath());

        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int timeout = 30;
                    while(timeout > 0) {
                        try {
                            Thread.sleep(1000);
                            timeout--;

                            //sdcard 挂载成功后退出
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                Thread.sleep(1000);
                                break;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtils.d(" ===== recheck faceManufacturer [%d]======= ", timeout);
                    if (AppConfig.getInstance().getFaceManufacturerByInit() == 1) {
                        App.getInstance().initMegviiAssets();
                    }
                }
            }).start();
        }
    }

    /**
     * 创建文件夹
     */
    public void initFiles() {
        FileUtils.createDirectory(Const.Directory.ROOT);
        FileUtils.createDirectory(Const.Directory.LOG);
        FileUtils.createDirectory(Const.Directory.TEMP);
        FileUtils.createDirectory(Const.Directory.WFFR);
        FileUtils.createDirectory(Const.Directory.MEGVII);
        FileUtils.createDirectory(Const.Directory.HORIZON);

        FileUtils.createDirectory(CommStorePathDef.USERDATA_PATH);
        FileUtils.createDirectory(CommStorePathDef.PARAM_PATH);
        FileUtils.createDirectory(CommStorePathDef.INFO_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.LOGO_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.SNAP_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.FACE_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.FACEPASS_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.FACEPASS_PERSON_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.MULTIMEDIA_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.SCANQR_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.MULTIMEDIA_VIDEO_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.MULTIMEDIA_PHOTO_DIR_PATH);
        FileUtils.createDirectory(CommStorePathDef.RECORD_DIR_PATH);

        if (Common.hasExternalSdCard()) {
            FileUtils.createDirectory(CommStorePathDef.EX_MULTIMEDIA_DIR_PATH);
            FileUtils.createDirectory(CommStorePathDef.EX_MULTIMEDIA_VIDEO_DIR_PATH);
        }
        // 删除抓拍文件夹下所有文件
        FileUtils.deleteDirPathAllFile(CommStorePathDef.SNAP_DIR_PATH);
        FileUtils.deleteDirPathAllFile(CommStorePathDef.FACE_DIR_PATH);
        FileUtils.deleteDirPathAllFile(CommStorePathDef.RECORD_DIR_PATH);
    }

    /**
     * 初始化全局异常捕捉
     */
    public void initCrashHandler() {
        CrashHandler.getInstance().init(this, Const.Directory.LOG);
    }

    /**
     * 初始化Realm数据库框架
     */
    public boolean initRealm() {
        try {
            RealmHelper.getInstance().compactRealm();

            //Stetho初始化 用于查看realm数据库数据的配置
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                    .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                    .build()
            );
            return true;
        } catch (Exception e) {
            LogUtils.printThrowable(e);
            return false;
        }
    }

    /**
     * 初始化wffr
     */
    public void initWffrAssets() {
        final Context context = App.this;
        if (!AppPreferences.isWffrInitialized()) {
            boolean result = WffrUtils.copyAssets(context);
            AppPreferences.setWffrInitialized(result);
        }
        String assetPath = WffrUtils.getBasePath(context);

        // 删除授权成功后生成的文件，防止重新烧写MAC不能使用
        File baseDir = new File(assetPath, "perm.lic");
        if (baseDir.exists()) {
            baseDir.delete();
        }

        int spoofing = FaceParamDao.getFaceLiveCheck();
        wffrapp.setAssetPath(assetPath);
        wffrapp.setSpoofing(spoofing);
        int result = wffrjni.initialize(assetPath, 0, 0, 0, 0, spoofing);
        if (result != 0) {
            if (WffrFaceDbManager.restore(context)) {
                result = wffrjni.initialize(assetPath, 0, 0, 0, 0, spoofing);
            }
        }
        LogUtils.d("wffrjni initialize result: " + result);
        if (result == 0) {
            // 安全级别
            int safeLevel = FaceParamDao.getFaceSafeLevel();
            wffrapp.setSafeLevel(safeLevel);
            // 不保存注册图片
            wffrjni.saveEnrollImages(0);
            wffrjni.Release();
        }
        FacePresenterProxy.init();
        FacePresenterProxy.setFacePresenter(new WffrFacePresenterImpl());
    }

    /**
     * 初始化megvii
     */
    public void initMegviiAssets() {
        FacePresenterProxy.init();
        FacePassHandler.initSDK(this);
        LogUtils.d("-- FACEPASS: Megvii Version=" + FacePassHandler.getVersion());
//        FacePassHandler.getAuth(MegviiFace.getInstance().authIP, MegviiFace.getInstance().apiKey, MegviiFace.getInstance().apiSecret);
        // 初始化人脸句柄

        AppExecutors.newThread().execute(new Runnable() {
            @Override
            public void run() {
                while (!FacePassHandler.isAvailable()) {
                    /* 如果SDK初始化未完成则需等待 */
                    ThreadUtils.sleep(300);
                }
                LogUtils.d("-- FACEPASS: start to build FacePassHandler");
                /* 初始化FacePass SDK 所需模型 */
                try {
                    /* 填入所需要的配置 */
                    int liveCheck = FaceParamDao.getFaceLiveCheck();
                    int safeLevel = FaceParamDao.getFaceSafeLevel();
                    FacePassConfig config = MegviiFace.getInstance().getFacePassConfig(getApplicationContext(), safeLevel, 1);

//                    File file = new File(config.fileRootPath);
//                    boolean fileValidFlag = file.isDirectory() && file.canRead() && file.canWrite();
//
//                    SparseBooleanArray boolMap = new SparseBooleanArray();
//                    boolMap.put(1, config.faceMinThreshold < 0 || config.faceMinThreshold > 512);
//                    boolMap.put(2, config.blurThreshold > 1.0F || config.blurThreshold < 0.0F);
//                    boolMap.put(3, config.searchThreshold > 100.0F || config.searchThreshold < 0.0F);
//                    boolMap.put(4, config.livenessEnabled && (config.livenessThreshold > 100.0F || config.livenessThreshold < 0.0F));
//                    boolMap.put(5, config.rgbIrLivenessEnabled && (config.livenessThreshold > 100.0F || config.livenessThreshold < 0.0F));
//                    boolMap.put(6, config.poseThreshold == null || config.poseThreshold.yaw < 0.0F || config.poseThreshold.yaw > 90.0F || config.poseThreshold.pitch < 0.0F || config.poseThreshold.pitch > 90.0F || config.poseThreshold.roll < 0.0F || config.poseThreshold.roll > 90.0F);
//                    boolMap.put(7, config.highBrightnessThreshold < config.lowBrightnessThreshold);
//                    boolMap.put(8, config.lowBrightnessThreshold > 255.0F || config.lowBrightnessThreshold < 0.0F);
//                    boolMap.put(9, config.highBrightnessThreshold > 255.0F || config.highBrightnessThreshold < 0.0F);
//                    boolMap.put(10, config.brightnessSTDThreshold > 255.0F || config.brightnessSTDThreshold < 0.0F);
//                    boolMap.put(11, config.rotation != 0 && config.rotation != 90 && config.rotation != 180 && config.rotation != 270);
//                    boolMap.put(12, config.retryCount < 0);
//                    boolMap.put(21, config.poseBlurModel == null);
//                    boolMap.put(22, config.searchModel == null);
//                    boolMap.put(23, config.detectModel == null);
//                    boolMap.put(24, config.detectRectModel == null);
//                    boolMap.put(25, config.landmarkModel == null);
//                    boolMap.put(26, config.smileEnabled && config.smileModel == null);
//                    boolMap.put(27, config.livenessEnabled && config.livenessModel == null);
//                    boolMap.put(28, config.rgbIrLivenessEnabled && config.rgbIrLivenessModel == null);
//                    boolMap.put(29, config.occlusionFilterEnabled && config.occlusionFilterModel == null);
//                    boolMap.put(30, config.mouthOccAttributeEnabled && config.mouthOccAttributeModel == null);
//                    boolMap.put(99, !fileValidFlag);
//                    StringBuilder log = new StringBuilder("-- FACEPASS: config isInvalidParams-->>");
//                    int len = boolMap.size();
//                    for (int i = 0; i < len; i++) {
//                        if (boolMap.valueAt(i)) {
//                            log.append("  ").append(boolMap.keyAt(i));
//                        }
//                    }
//                    LogUtils.d(log.toString());


                    /* 创建SDK实例 */
                    MegviiFace.getInstance().mFacePassHandler = new FacePassHandler(config);
                    MegviiFace.getInstance().mFacePassHandler.setIRConfig(0.9919034, -10.145447,
                            0.9881963, 37.772705, 0.4);

                    // 先启用活体，创建FacePassHandler，然后再根据实际活体开关设置config
//                    config.livenessEnabled = (liveCheck != 0);
                    config.livenessEnabled = false;
                    config.rgbIrLivenessEnabled = (liveCheck != 0);
                    MegviiFace.getInstance().mFacePassHandler.setConfig(config);

                    FacePassConfig addConfig = MegviiFace.getInstance().mFacePassHandler.getAddFaceConfig();
                    addConfig.blurThreshold = 0.4f;
                    addConfig.poseThreshold.yaw =30f;
                    addConfig.poseThreshold.roll = 30f;
                    addConfig.poseThreshold.pitch = 30f;
                    if (CustomVersion.VERSION_KAIWEI_WUYE) {
                        addConfig.faceMinThreshold = 80; //100
                        addConfig.lowBrightnessThreshold = 30f; //70
                        addConfig.highBrightnessThreshold = 230f; //210
                        addConfig.brightnessSTDThreshold = 160f; //80
                    }

                    LogUtils.d("-- FACEPASS: searchThreshold=" + addConfig.searchThreshold + ", brightnessSTDThreshold="
                            + addConfig.brightnessSTDThreshold + ", highBrightnessThreshold=" + addConfig.highBrightnessThreshold
                            + "lowBrightnessThreshold=" + addConfig.lowBrightnessThreshold + ", faceMinThreshold="
                            + addConfig.faceMinThreshold + ", blurThreshold=" + addConfig.blurThreshold
                            + ", livenessThreshold=" + addConfig.livenessThreshold + ", retryCount="
                            + addConfig.retryCount + ", rotation=" + addConfig.rotation + ", pose.pitch="
                            + addConfig.poseThreshold.pitch + ", pose.roll=" + addConfig.poseThreshold.roll
                            + ", pose.yaw=" + addConfig.poseThreshold.yaw);

                    MegviiFace.getInstance().mFacePassHandler.setAddFaceConfig(addConfig);

                    FacePresenterProxy.setFacePresenter(new MegviiFacePresenterImpl());
                } catch (FacePassException e) {
                    LogUtils.printThrowable(e);
                    LogUtils.d("-- FACEPASS: FacePassHandler is null");
                }
            }
        });
    }

    /**
     * 打印屏幕信息
     */
    private void printScreenInfo() {
        DisplayMetrics outMetrics = getResources().getDisplayMetrics();
        LogUtils.d("screen=" + outMetrics.widthPixels + "*" + outMetrics.heightPixels);
        LogUtils.d("density=" + outMetrics.density + ", densityDpi=" + outMetrics.densityDpi);
    }

    /**
     * 注册Activity生命周期观察者
     */
    private void registerLifecycleObserver() {
        mLifecycleObserver = new ActivityLifecycleObserver();
        registerActivityLifecycleCallbacks(mLifecycleObserver);
    }

    /**
     * getCurrentActivity() 获取到当前最上层的activity
     */
    public Activity getCurrentActivity() {
        return mLifecycleObserver.getTopActivity();
    }

    private boolean inittts() {
        mTts = new MscTts(this);
        if (mTts == null)
            LogUtils.e("mTts is null !!!");
        else
            LogUtils.e("mTts is start !!!");
        mTtsBlockingQueue = new TtsBlockingQueue();
        return true;
    }

    public void ttsStartQuene() {
        mTtsBlockingQueue.SetMscttsApp(this);
        mTtsBlockingQueue.startSpeakThread();
    }

    public void setMscTtsInitIntf(MscTts.MscTtsInitIntf mMscttsIntf) {
        mTts.setMscTtsInitIntf(mMscttsIntf);
    }

    public void setMscTtsSpeakIntf(String subModule, MscTts.MscTtsSpeakIntf SpeakIntf) {
        mTts.setMscTtsSpeakIntf(subModule, SpeakIntf);
    }

    public void defaultStartSpeaking(String subModule, int isQueue, String speakStr, String speed, String pitch, String volume, String stream_type) {
        if (isQueue == 1) {
            TtsParamData ttsData = new TtsParamData();
            ttsData.speakStr = speakStr;
            ttsData.speed = speed;
            ttsData.pitch = pitch;
            ttsData.volume = volume;
            ttsData.stream_type = stream_type;
            ttsData.speakUrl = "";
            ttsData.postfix = "";
            ttsData.Mode = -1;
            ttsData.times = -1;
            ttsData.isQueue = isQueue;
            ttsData.SpeakMode = TtsComDefine.SpeakMode.defaultStartSpeaking;
            ttsData.subModuleStr = subModule;
            TtsBlockingQueue.produceVoice(ttsData);
        } else {
            mTts.defaultStartSpeaking(subModule, speakStr, speed, pitch, volume, stream_type);
        }
    }

    public void StartSpeakingUrl(String subModule, int isQueue, String speakUrl, String postfix, String speed, String pitch, String volume, String stream_type, int Mode, int times) {
        if (isQueue == 1) {
            TtsParamData ttsData = new TtsParamData();
            ttsData.speakStr = "";
            ttsData.speed = speed;
            ttsData.pitch = pitch;
            ttsData.volume = volume;
            ttsData.stream_type = stream_type;
            ttsData.speakUrl = speakUrl;
            ttsData.postfix = postfix;
            ttsData.Mode = Mode;
            ttsData.times = times;
            ttsData.isQueue = isQueue;
            ttsData.SpeakMode = TtsComDefine.SpeakMode.StartSpeakingurl;
            ttsData.subModuleStr = subModule;
            TtsBlockingQueue.produceVoice(ttsData);
        } else {
            mTts.StartSpeakingUrl(subModule, speakUrl, postfix, speed, pitch, volume, stream_type, Mode, times);
        }
    }

    public void StartSpeaking(String subModule, int isQueue, String speakStr, String speed, String pitch, String volume, String stream_type, int Mode, int times) {
        if (isQueue == 1) {
            TtsParamData ttsData = new TtsParamData();
            ttsData.speakStr = speakStr;
            ttsData.speed = speed;
            ttsData.pitch = pitch;
            ttsData.volume = volume;
            ttsData.stream_type = stream_type;
            ttsData.speakUrl = "";
            ttsData.postfix = "";
            ttsData.Mode = Mode;
            ttsData.times = times;
            ttsData.isQueue = isQueue;
            ttsData.SpeakMode = TtsComDefine.SpeakMode.StartSpeaking;
            ttsData.subModuleStr = subModule;
            TtsBlockingQueue.produceVoice(ttsData);
        } else {
            mTts.StartSpeaking(subModule, speakStr, speed, pitch, volume, stream_type, Mode, times);
        }
    }

    public boolean IsSpeaking() {
        return mTts.IsSpeaking();
    }

    public void MscTtsPause() {
        mTts.MscTtsPause();
    }

    public void MscTtsResume() {
        mTts.MscTtsResume();
    }

    public void MscTtsStop() {
        mTts.MscTtsStop();
    }
}
