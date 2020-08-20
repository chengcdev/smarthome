package com.mili.smarthome.tkj.appfunc.facefunc;

import com.android.CommStorePathDef;
import com.android.Common;
import com.android.client.MainClient;
import com.android.interf.FaceListenerAdapter;
import com.mili.smarthome.tkj.app.AppExecutors;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.NetworkParamDao;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.wf.wffrapp;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import mcv.facepass.FacePassHandler;

/**
 * 人脸代理类（负责实现人脸授权申请、人脸图片注册，人脸删除）
 */
public class FacePresenterProxy {

    private static FacePresenter mFacePresenter;
    private static Semaphore mSemaphore = new Semaphore(1, false);

    public static void init() {
        MainClient.getInstance().setFaceListener(new FaceListenerImpl<FaceProtocolInfo>());
    }

    public static void setFacePresenter(FacePresenter facePresenter) {
        mFacePresenter = facePresenter;
    }

    public static void registerFaceType() {
        int faceManufacturer = AppConfig.getInstance().getFaceManufacturer();
        int faceType = Const.FaceType.NONE;
        if (faceManufacturer == 0) {
            // EI
            faceType = Const.FaceType.EI_3_1;
        } else if (faceManufacturer == 1) {
            // Face++
            String faceIp = NetworkParamDao.getFaceIp();
            faceType = (Common.ipToint(faceIp) == 0) ? Const.FaceType.FACEPASS_OFFLINE : Const.FaceType.FACEPASS_ONLINE;
        } else if (faceManufacturer == 2) {
            // Horizon
            faceType = Const.FaceType.EI_3_1;
        }
        MainClient.getInstance().Main_SetFaceType(faceType);
        LogUtils.d("-- FACE TYPE: %d", faceType);
    }

    public static long getSurplus() {
        if (mFacePresenter != null) {
            return mFacePresenter.getSurplus();
        }
        return 0;
    }

    public static boolean enrollFromImage(String imgPath, String faceId) {
        if (mFacePresenter != null) {
            return mFacePresenter.enrollFromImage(imgPath, faceId);
        }
        return false;
    }

    public static boolean enrollFromImage(String imgPath, FaceProtocolInfo faceProtocolInfo) {
        if (mFacePresenter != null) {
            return mFacePresenter.enrollFromImage(imgPath, faceProtocolInfo);
        }
        return false;
    }

    public static boolean delFaceInfoById(String faceId) {
        if (mFacePresenter != null) {
            return mFacePresenter.delFaceInfoById(faceId);
        }
        return false;
    }

    public static int delFaceInfo(String cardNo) {
        if (mFacePresenter != null) {
            return mFacePresenter.delFaceInfo(cardNo);
        }
        return 0;
    }

    public static boolean clearFaceInfo() {
        if (mFacePresenter != null) {
            return mFacePresenter.clearFaceInfo();
        }
        return false;
    }

    /**
     * 获取EI人脸授权
     */
    private static void getWffrFaceLicense() {
        int result = wffrapp.verifyLicense();
        LogUtils.d("wffr verify license result: " + result);
        if (result == 0) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_LICENSE_OK_PATH);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_LICENSE_FAIL_PATH);
        }
    }

    /**
     * 获取FACE++人脸授权
     */
    private static void getMegviiFaceLicense() {
        FacePassHandler.initSDK(ContextProxy.getContext());
        if (FacePassHandler.isAuthorized()) {
            LogUtils.d(">>>>>onFaceLicense: 人脸已授权！");
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_LICENSE_YES_PATH);

            /* 当前不是face++旷视人脸，又提示授权成功 */
            if (AppPreferences.getFaceManufacturer() != 1) {
                AppPreferences.setFaceManufacturer(1);
                AppExecutors.scheduler().schedule(new Runnable() {
                    @Override
                    public void run() {
                        SystemSetUtils.rebootDevice();
                    }
                }, 3, TimeUnit.SECONDS);
            }
            return;
        }
        if (!mSemaphore.tryAcquire()) {
            return;
        }
        AppExecutors.newThread().execute(new Runnable() {
            @Override
            public void run() {
                FacePassHandler.getAuth(MegviiFace.AUTH_IP, MegviiFace.API_KEY, MegviiFace.API_SECRET);
                boolean result = false;
                for (int i = 0; i < 50; i++) {
                    try {
                        Thread.sleep(300);
                        if (FacePassHandler.isAuthorized()) {
                            result = true;
                            break;
                        }
                    } catch (Exception e) {
                        LogUtils.printThrowable(e);
                    }
                }
                if (result) {
                    LogUtils.d(">>>>>onFaceLicense: 人脸授权成功！");
                    AppPreferences.setFaceManufacturer(1);
                    AppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_LICENSE_OK_PATH);
                        }
                    });
                    AppExecutors.scheduler().schedule(new Runnable() {
                        @Override
                        public void run() {
                            SystemSetUtils.rebootDevice();
                        }
                    }, 3, TimeUnit.SECONDS);
                } else {
                    LogUtils.d(">>>>>onFaceLicense: 人脸授权失败！");
                    AppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_LICENSE_FAIL_PATH);
                        }
                    });
                }
                mSemaphore.release();
            }
        });
    }

    private static class FaceListenerImpl<T> extends FaceListenerAdapter<T> {

        @Override
        public void onFaceLicense(int faceLicense, int faceType) {
            LogUtils.d(">>>>>onFaceLicense: faceLicense=%d, faceType=%d", faceLicense, faceType);
//            if (faceLicense == 1) {
//                switch (faceType) {
//                    case 1:
//                        //getWffrFaceLicense();
//                        break;
//                    case 2:
//                        //getMegviiFaceLicense();
//                        break;
//                }
//            }
            getMegviiFaceLicense();
        }

        @Override
        public boolean onFaceEnroll(String imgPath, String faceId) {
            return enrollFromImage(imgPath, faceId);
        }

        @Override
        public boolean onFaceEnroll(String imgPath, FaceProtocolInfo faceProtocolInfo) {
            return enrollFromImage(imgPath, faceProtocolInfo);
        }

        @Override
        public boolean onFaceDelete(String faceId) {
            return delFaceInfoById(faceId);
        }
    }

    /**
     * 人脸注册自测
     */
    public static void testFaceEnroll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ret;
                String faceDir = "/sdcard/megvii/photo/";
                File dir = new File(faceDir);
                if (!dir.exists()) {
                    return;
                }
                File[] list = dir.listFiles();
                LogUtils.d(" === file count is %d", list.length);
                int count = 0;
                for (int i=0; i<list.length; i++) {
                    String faceId = String.format("pc-010102030-%d", i);
                    ret = FacePresenterProxy.enrollFromImage(list[i].getPath(), faceId);
                    LogUtils.d("[testFaceEnroll] dir=" + faceDir + ", faceId=" + faceId + ", ret=" + ret);
                    if (!ret) {
                        count++;
                    }
                }
                LogUtils.d(" === file count is %d, failed is %d", list.length, count);
            }
        }).start();
    }
}
