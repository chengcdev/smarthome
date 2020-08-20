package com.mili.smarthome.tkj.face.horizon;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.face.horizon.bean.FaceRecogResult;
import com.mili.smarthome.tkj.face.horizon.bean.RecognitionInfo;
import com.mili.smarthome.tkj.face.horizon.bean.TrackInfo;
import com.mili.smarthome.tkj.face.horizon.util.CameraDisplayUtil;
import com.mili.smarthome.tkj.face.horizon.util.GpioUtil;
import com.mili.smarthome.tkj.face.horizon.util.HorizonPreferences;
import com.mili.smarthome.tkj.face.horizon.util.LiveNessUtil;
import com.mili.smarthome.tkj.face.horizon.util.XWareHouseUtil;
import com.mili.smarthome.tkj.face.horizon.view.HRXMIPICam;

import java.util.concurrent.Semaphore;

import hobot.sunrise.sdk.jni.FaceModuleResult;
import hobot.sunrise.sdk.jni.FaceResultListener;
import hobot.sunrise.sdk.jni.Rect;
import hobot.xwaremodule.sdk.jni.HobotXWMSearchResult;
import hobot.xwaremodule.sdk.jni.XWareModuleEnumClass;

public class ReceiveFaceResult implements FaceResultListener {
    public static final String TAG = ReceiveFaceResult.class.getName();
    private final int MESSAGE_RECOGNITION = 1;
    //private final int MESSAGE_CLOSE_NO_PEOPLE = 2;
    private final int MESSAGE_CAPTURE_PICTURE = 3;
    private final int MESSAGE_THREAD_QUIT = 4;
    private int mTrackid = 0;
    private Rect mRect = new Rect();
    public Handler handler;
    public Handler handlerCapture;
    private int capture_min_size = 80;
    private int box_size = 0;
    private long timeSpace = 1000;
    private long LogPrintIntervalTime = 3000;
    private long curLogPrintTime = 0;
    private long lastLogPrintTime = 0;
    private boolean mLivenessCheck = true;
    private boolean faceOpenSnap = true;
//    private boolean screenOn = false;
//    private boolean autoScreenShut = false;
//    private boolean messageCloseSend = false;
    private boolean receiveFaceResult = false;
    private boolean drop_feature = false;
    private SparseArray<TrackInfo> mInfo = null;
    private SparseArray<RecognitionInfo> mRecognitionInfo = null;

    private HRXMIPICam mHRXMIPICam;
    private IFaceRecogView mIFaceRecogView;

    private Semaphore mSemaphore = new Semaphore(1);
    private Thread threadRecog;
    private Thread threadCapture;

    public ReceiveFaceResult(HRXMIPICam hrxCam) {
        mHRXMIPICam = hrxCam;
        threadRecog = new RecogThread();
        threadRecog.start();
        threadCapture = new CaptureThread();
        threadCapture.start();
        GpioUtil.writeFan(true);
    }

    void removeRecognitionMsg() {
        handler.removeMessages(MESSAGE_RECOGNITION);
    }

    public void startReceiveFaceResult(IFaceRecogView iFaceRecogView) {
        mLivenessCheck = AppConfig.getInstance().getFaceLiveCheck() == 1;
        faceOpenSnap = SnapParamDao.getFaceOpenSnap() == 1;
//        autoScreenShut = HorizonPreferences.getAutoScreenShut();
        timeSpace = HorizonPreferences.getSamePeopleRecognitionInterval();
        drop_feature = false;
        receiveFaceResult = true;
//        messageCloseSend = false;
        curLogPrintTime = lastLogPrintTime = System.currentTimeMillis();
        mIFaceRecogView = iFaceRecogView;
        if (mLivenessCheck) {
            GpioUtil.writeInfraredLampGpio(true);
            //GpioUtil.writeCpuGpio(true);
        }
    }

    public void stopReceiveFaceResult() {
        mIFaceRecogView = null;
        receiveFaceResult = false;
        removeRecognitionMsg();
//        handler.removeMessages(MESSAGE_CLOSE_NO_PEOPLE);
        handlerCapture.removeMessages(MESSAGE_CAPTURE_PICTURE);
        if (mLivenessCheck) {
            GpioUtil.writeInfraredLampGpio(false);
            //GpioUtil.writeCpuGpio(false);
        }
//        if (!screenOn) {
//            GpioUtil.writeScreenGpio(true);
//            screenOn = true;
//        }
    }

    public void recyclingResources() {
        try {
            handler.sendEmptyMessage(MESSAGE_THREAD_QUIT);
            handlerCapture.sendEmptyMessage(MESSAGE_THREAD_QUIT);
            threadCapture.join();
            threadRecog.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void faceRecogResult(FaceRecogResult recogResult) {
        FaceRecogResultCache.put(recogResult);
        if (mIFaceRecogView != null) {
            mIFaceRecogView.faceRecogResult(recogResult);
        }
    }

    public void updateOverlay(final FaceModuleResult faceModuleResult, final int boxSise) {
        if (mIFaceRecogView != null) {
            mIFaceRecogView.updateOverlay(faceModuleResult, boxSise);
        }
    }

    @Override
    public void FaceResultCallBack(final FaceModuleResult faceModuleResult) {
        if (!receiveFaceResult) {
            return;
        }
        if (XWareHouseUtil.getInitStatus() == XWareModuleEnumClass.HobotXWMErrCode.Errcode_GET_LIMIT_FAIL.getValue()) {
            // TODO 获取库容上限失败
            return;
        }
        curLogPrintTime = System.currentTimeMillis();
        if (faceModuleResult != null) {
            if (faceModuleResult.face_result != null) {
                int maxRect = -1;
                if (mInfo == null) {
                    mInfo = new SparseArray<>();
                }
                if (faceModuleResult.face_result.snap != null) {
                    for (int i = 0; i < faceModuleResult.face_result.snap.lost_cnt; i++) {
                        Log.d(TAG, "faceModuleResult.face_result.snap.lost_face[i].id = " + faceModuleResult
                                .face_result.snap.lost_face[i].id);
                        if (mInfo.get(faceModuleResult.face_result.snap.lost_face[i].id) != null) {
                            mInfo.remove(faceModuleResult.face_result.snap.lost_face[i].id);
                        }
                    }
                }
                if (faceModuleResult.face_result.track != null) {
                    if (faceModuleResult.face_result.track.ts > 255 || faceModuleResult.face_result.track.ts < 0) {
                        Log.d(TAG, "FaceResultCallBack faceModuleResult.face_result.track.ts = " + faceModuleResult.face_result.track.ts);
                        return;
                    }
                    if (faceModuleResult.face_result.track.count != 0) {
//                        if (messageCloseSend) {
//                            handler.removeMessages(MESSAGE_CLOSE_NO_PEOPLE);
//                            messageCloseSend = false;
//                        }
//                        if (!screenOn) {
//                            GpioUtil.writeScreenGpio(true);
//                            screenOn = true;
//                        }
                        capture_min_size = HorizonPreferences.getCaptureSize();
                        updateOverlay(faceModuleResult, capture_min_size * capture_min_size);
                        mRect.left = 0;
                        mRect.top = 0;
                        mRect.right = 0;
                        mRect.bottom = 0;
                        for (int i = 0; i < faceModuleResult.face_result.track.count; i++) {
                            box_size = (faceModuleResult.face_result.track.result[i].rect.right - faceModuleResult.face_result.track.result[i].rect.left)
                                    * (faceModuleResult.face_result.track.result[i].rect.bottom - faceModuleResult.face_result.track.result[i].rect.top);
                            if (box_size > capture_min_size * capture_min_size) {
                                if (mRect.left == 0 && mRect.top == 0 && mRect.right == 0 && mRect.bottom == 0) {
                                    mRect.left = faceModuleResult.face_result.track.result[i].rect.left;
                                    mRect.top = faceModuleResult.face_result.track.result[i].rect.top;
                                    mRect.right = faceModuleResult.face_result.track.result[i].rect.right;
                                    mRect.bottom = faceModuleResult.face_result.track.result[i].rect.bottom;
                                    mTrackid = faceModuleResult.face_result.track.result[i].id;
                                    maxRect = i;
                                    TrackInfo trackInfo = mInfo.get(mTrackid);
                                    if (trackInfo == null) {
                                        trackInfo = new TrackInfo();
                                        trackInfo.info = new TrackInfo[256];
                                    }
                                    trackInfo.ts = faceModuleResult.face_result.track.ts;
                                    if (trackInfo.info[(int) trackInfo.ts] == null) {
                                        trackInfo.info[(int) trackInfo.ts] = new TrackInfo();
                                    }
                                    trackInfo.info[(int) trackInfo.ts].onePer = faceModuleResult.face_result.track.count == 1;
                                    trackInfo.info[(int) trackInfo.ts].rect = faceModuleResult.face_result.track.result[i].rect;
                                    trackInfo.info[(int) trackInfo.ts].quality_detail = faceModuleResult.face_result.track.result[i].quality_detail;
                                    if (faceModuleResult.face_result.track.result[maxRect].lmk_len != 0) {
                                        trackInfo.info[(int) trackInfo.ts].lmk_len = faceModuleResult.face_result.track.result[maxRect].lmk_len;
                                        trackInfo.info[(int) trackInfo.ts].xy = faceModuleResult.face_result.track.result[maxRect].xy;

                                    }
                                    mInfo.put(faceModuleResult.face_result.track.result[i].id, trackInfo);
                                } else if (box_size > (mRect.right - mRect.left) * (mRect.bottom - mRect.top)) {
                                    mRect.left = faceModuleResult.face_result.track.result[i].rect.left;
                                    mRect.top = faceModuleResult.face_result.track.result[i].rect.top;
                                    mRect.right = faceModuleResult.face_result.track.result[i].rect.right;
                                    mRect.bottom = faceModuleResult.face_result.track.result[i].rect.bottom;
                                    mTrackid = faceModuleResult.face_result.track.result[i].id;
                                    maxRect = i;
                                    TrackInfo trackInfo = mInfo.get(mTrackid);
                                    if (trackInfo == null) {
                                        trackInfo = new TrackInfo();
                                        trackInfo.info = new TrackInfo[256];
                                    }
                                    trackInfo.ts = faceModuleResult.face_result.track.ts;
                                    if (trackInfo.info[(int) trackInfo.ts] == null) {
                                        trackInfo.info[(int) trackInfo.ts] = new TrackInfo();
                                    }
                                    trackInfo.info[(int) trackInfo.ts].onePer = faceModuleResult.face_result.track.count == 1;
                                    trackInfo.info[(int) trackInfo.ts].rect = faceModuleResult.face_result.track.result[i].rect;
                                    trackInfo.info[(int) trackInfo.ts].quality_detail = faceModuleResult.face_result.track.result[i].quality_detail;
                                    if (faceModuleResult.face_result.track.result[maxRect].lmk_len != 0) {
                                        trackInfo.info[(int) trackInfo.ts].lmk_len = faceModuleResult.face_result.track.result[maxRect].lmk_len;
                                        trackInfo.info[(int) trackInfo.ts].xy = faceModuleResult.face_result.track.result[maxRect].xy;
                                    }
                                    mInfo.put(faceModuleResult.face_result.track.result[i].id, trackInfo);
                                }
                            }
                        }
                        if (maxRect == -1) {
                            removeRecognitionMsg();
                            Log.d(TAG, "FaceResultCallBack out 111");
                            return;
                        }
                    } else {
                        removeRecognitionMsg();
                        updateOverlay(null, 0);
//                        if (!messageCloseSend) {
//                            handler.sendEmptyMessageDelayed(MESSAGE_CLOSE_NO_PEOPLE, 10000);
//                            messageCloseSend = true;
//                        }
                        //Log.d(TAG, "FaceResultCallBack out 222");
                        return;
                    }
                }
                if (faceModuleResult.face_result.feature != null) {
//                    Log.d(TAG, "faceModuleResult.face_result.feature mTrackid = " + mTrackid);
                    for (int i = 0; i < faceModuleResult.face_result.feature.result_num; i++) {
                        if (curLogPrintTime - lastLogPrintTime > LogPrintIntervalTime) {
                            Log.d(TAG, "faceModuleResult.face_result.feature.fea_info[i].track_id = " + faceModuleResult
                                    .face_result.feature.fea_info[i].track_id + " mTrackid = " + mTrackid);
                        }
                        if (faceModuleResult.face_result.feature.fea_info[i].track_id == mTrackid) {
                            if (curLogPrintTime - lastLogPrintTime > LogPrintIntervalTime) {
                                Log.d(TAG, "faceModuleResult.face_result.feature.fea_info[i].track_id = " +
                                        faceModuleResult.face_result.feature.fea_info[i].track_id + " " +
                                        "faceModuleResult" + ".time_stamp = " + faceModuleResult.time_stamp + " " +
                                        "mTrackid = " + mTrackid);
                            }
                            if (drop_feature && mLivenessCheck) {
                                break;
                            }
                            Message message = Message.obtain();
                            if (curLogPrintTime - lastLogPrintTime > LogPrintIntervalTime) {
                                Log.d(TAG, "FaceResultCallBack before new message out message =" + message);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", mTrackid);
                            bundle.putInt("ts", (int) faceModuleResult.time_stamp);
                            bundle.putIntArray("feature", faceModuleResult.face_result.feature.fea_info[i].featureArray);
                            bundle.putByte("shift_l", faceModuleResult.face_result.feature.fea_info[i].shift_l);
                            message.what = MESSAGE_RECOGNITION;
                            message.setData(bundle);
                            handler.sendMessage(message);
                            if (curLogPrintTime - lastLogPrintTime > LogPrintIntervalTime) {
                                Log.d(TAG, "FaceResultCallBack after sendMessage ");
                            }
                        }
                    }
                }
            }
        }
    }

    public void FaceRecognition(int trackid, int ts, int[] featurearray, byte shift_l) {
        Log.d(HorizonConst.TAG, "[FaceRecog] trackid=" + trackid + ", trackid=" + trackid + ", ts=" + ts);
        TrackInfo tempTrack = mInfo.get(trackid);
        RecognitionInfo recognitionInfo = mRecognitionInfo.get(trackid);
        if (tempTrack == null) {
            //Log.d(TAG, "FaceRecognition trackid lost");
            if (recognitionInfo != null) {
                mRecognitionInfo.remove(trackid);
            }
            Log.d(HorizonConst.TAG, "[FaceRecog] trackid = " + trackid + " can't find track info");
            return;
        } else {
            if (tempTrack.info[ts] == null) {
                Log.d(HorizonConst.TAG, "[FaceRecog] ts = " + ts + " can't find track info");
                return;
            }
            if (recognitionInfo == null) {
                recognitionInfo = new RecognitionInfo();
                mRecognitionInfo.put(trackid, recognitionInfo);
            } else if (recognitionInfo.recognition) {
                if (System.currentTimeMillis() - recognitionInfo.curTime < timeSpace) {
                    Log.d(HorizonConst.TAG, "[FaceRecog] same person 1 second recognition");
                    return;
                }
            }
        }
        HobotXWMSearchResult hobotXWHSearchResult = XWareHouseUtil.faceSearch(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION,
                "img_uri", null, featurearray, shift_l, 5);
        FaceRecogResult recogResult = new FaceRecogResult();
        recogResult.setTrackId(trackid);
        if (hobotXWHSearchResult != null && hobotXWHSearchResult.num_ != 0) {
            String faceId = hobotXWHSearchResult.id_score_[0].id_;
            int similar = (int) (hobotXWHSearchResult.id_score_[0].similar_ * 100);
            recogResult.setFaceId(faceId).setSimilar(similar);
            if (similar >= HorizonPreferences.getFaceThr()) {
                boolean isLive = true;
                int[] id = new int[5];
                if (mLivenessCheck) {
                    int[] pos = LiveNessUtil.getEyePos(tempTrack.info[ts]);
                    boolean crop = (tempTrack.info[ts].rect.right - tempTrack.info[ts].rect.left) <= 140;
                    drop_feature = true;
                    int retUpdate = LiveNessUtil.updateParam(true, trackid, faceId, pos[0], pos[1], pos[2], pos[3],
                            ts, false, tempTrack.info[ts]
                                    .onePer, crop, tempTrack.info[ts].quality_detail, tempTrack.info[ts].rect);
                    if (0 != retUpdate) {
                        faceRecogResult(recogResult.setLiveness(retUpdate));
                        drop_feature = false;
                        return;
                    }

                    // id[1]是活体值，id[2]是校准步骤标志，id[3]和id[4]是活检出来的坐标
                    isLive = LiveNessUtil.getResult(id);
                    drop_feature = false;
                    if (id[0] < 0) {
                        faceRecogResult(recogResult.setLiveness(id[0]));
                        return;
                    } else if (id[0] != trackid) {
                        Log.d(HorizonConst.TAG, "[FaceRecog] id[0] != trackid");
                        return;
                    }
                }
                Log.d(HorizonConst.TAG, "[FaceRecog] isLive=" + isLive + ", id[0]=" + id[0] + ", id[1]=" + id[1]);
                if (isLive) {
                    recogResult.setLiveness(id[1]);
                    if (HorizonPreferences.getAutoFixCoordinate() && 15 == id[2]) {
                        HorizonPreferences.saveAutoFixCoordinate(false);
                    }
                    recognitionInfo.recognition = true;
                    recognitionInfo.curTime = System.currentTimeMillis();

                    if (faceOpenSnap && mHRXMIPICam != null) {
                        String snapPath = Const.Directory.TEMP + "/recogSucc.jpg";
                        Message message = Message.obtain();
                        message.what = MESSAGE_CAPTURE_PICTURE;
                        message.arg1 = ts;
                        message.obj = snapPath;
                        handlerCapture.sendMessage(message);
                        recogResult.setSnapPath(snapPath);
                    }
                    faceRecogResult(recogResult);

                } else {
                    recognitionInfo.notLiveCount++;
                    if (recognitionInfo.notLiveCount >= 5) {
                        faceRecogResult(recogResult);
                        recognitionInfo.notLiveCount = 0;
                    }
                }
            } else {
                recognitionInfo.recogFalseCount++;
                if (recognitionInfo.recogFalseCount >= 5) {
                    faceRecogResult(recogResult);
                    recognitionInfo.recogFalseCount = 0;
                }
            }
        } else {
            recognitionInfo.recogFalseCount++;
            if (recognitionInfo.recogFalseCount >= 5) {
                faceRecogResult(recogResult);
                recognitionInfo.recogFalseCount = 0;
            }
        }
    }

    class RecogThread extends Thread {
        public RecogThread() {
            super("HorizonRecogThread");
        }
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            if (mRecognitionInfo == null) {
                mRecognitionInfo = new SparseArray<>();
            }
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (curLogPrintTime - lastLogPrintTime > LogPrintIntervalTime) {
                        Log.d(TAG, "handleMessage msg.what = " + msg.what);
                        lastLogPrintTime = curLogPrintTime;
                    }
                    switch (msg.what) {
                        case MESSAGE_RECOGNITION:
                            if (mSemaphore.tryAcquire()) {
                                Bundle bundle = msg.getData();
                                FaceRecognition(bundle.getInt("id"),
                                        bundle.getInt("ts"),
                                        bundle.getIntArray("feature"),
                                        bundle.getByte("shift_l"));
                                mSemaphore.release();
                            }
                            break;
//                        case MESSAGE_CLOSE_NO_PEOPLE:
//                            if (screenOn && autoScreenShut) {
//                                GpioUtil.writeScreenGpio(false);
//                                screenOn = false;
//                            }
//                            break;
                        case MESSAGE_THREAD_QUIT:
                            handler.getLooper().quit();
                            Log.d(TAG, "RecogThread quit");
                    }
                    super.handleMessage(msg);
                }
            };
//            Log.d(TAG, "Thread messageCloseSend = " + messageCloseSend);
//            if (!messageCloseSend) {
//                handler.sendEmptyMessageDelayed(MESSAGE_CLOSE_NO_PEOPLE, 10000);
//                messageCloseSend = true;
//            }
            Looper.loop();
        }
    }


    class CaptureThread extends Thread {
        public CaptureThread() {
            super("HorizonCaptureThread");
        }
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            handlerCapture = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.d(TAG, "threadCapture msg = " + msg);
                    switch (msg.what) {
                        case MESSAGE_CAPTURE_PICTURE:
                            //HRXTrans.pictureCapture(msg.arg1, (String) msg.obj);
                            if (mHRXMIPICam != null) {
                                int res = mHRXMIPICam.savePhoto(msg.arg1, 0, 0,
                                        CameraDisplayUtil.getCameraWidth(), CameraDisplayUtil.getCameraHeight(),
                                        CameraDisplayUtil.getCameraWidth(), CameraDisplayUtil.getCameraHeight(), (String) msg.obj);
                                Log.d(HorizonConst.TAG, "[FaceCapture] savePhoto: res=" + res);
                            }
                            break;
                        case MESSAGE_THREAD_QUIT:
                            handlerCapture.getLooper().quit();
                            Log.d(TAG, "CaptureThread quit");
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }
}
