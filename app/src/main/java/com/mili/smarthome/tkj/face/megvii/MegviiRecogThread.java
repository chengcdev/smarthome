package com.mili.smarthome.tkj.face.megvii;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.provider.FullDeviceNo;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.face.megvii.online.FaceRecognize;
import com.mili.smarthome.tkj.utils.LogUtils;

import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassRecognitionResultType;

/**
 * <p>2020-01-17 14:52  create by zenghm
 */
public class MegviiRecogThread extends Thread {

    public static final int MSG_RECOGNIZING = 0x10;
    public static final int MSG_RECOG_SUC = 0x11;
    public static final int MSG_RECOG_TIMEOUT = 0x12;
    public static final int MSG_RECOG_FACEID_TIMEOUT = 0x15;

    public static final String KEY_PREVIEW_TYPE = "key_preview_type";
    public static final String KEY_PREVIEW_DATA = "key_preview_data";

    private volatile boolean running = false;
    private Semaphore mSemaphore = new Semaphore(0);
    private ArrayBlockingQueue<MegviiFaceInfoAdapter> mRecogQueue = new ArrayBlockingQueue<>(1);
    private RequestQueue requestQueue;
    private String mDeviceNo;
    private Handler mHandler;
    private MegviiFacePresenterImpl mFacePresenter = new MegviiFacePresenterImpl();

    public MegviiRecogThread(Context context, Handler handler) {
        mHandler = handler;

        FullDeviceNo fullDeviceNo = new FullDeviceNo(context);
        mDeviceNo = fullDeviceNo.getDeviceNo();

        /* 初始化网络请求库 */
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    @Override
    public void interrupt() {
        running = false;
        super.interrupt();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean offer(MegviiFaceInfoAdapter faceInfoAdapter) {
        if (running && mSemaphore.tryAcquire()) {
            return mRecogQueue.offer(faceInfoAdapter);
        }
        return false;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            MegviiFaceInfoAdapter faceInfoAdapter;
            try {
                mSemaphore.release();
                faceInfoAdapter = mRecogQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            final FacePassDetectionResult detectionResult = faceInfoAdapter.getDetectionResult();
            final int frameType = faceInfoAdapter.getType();
            final byte[] frameData = faceInfoAdapter.getData();
            LogUtils.v("FacePass--->>>SDK_MODE=%s", MegviiFace.getInstance().SDK_MODE);
            if (MegviiFace.getInstance().SDK_MODE == MegviiFace.FacePassSDKMode.MODE_ONLINE) {
                //在线模式
                if (detectionResult == null || detectionResult.message.length == 0) {
                    continue;
                }
                //构建http请求
                LogUtils.v("FacePass--->>>RECOGNIZE: url=%s, deviceno=%s", MegviiFace.getInstance().recognizeUrl, mDeviceNo);
                FaceRecognize request = new FaceRecognize(MegviiFace.getInstance().recognizeUrl, mDeviceNo, detectionResult, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsresponse = new JSONObject(response);
                            int code = jsresponse.getInt("code");
                            LogUtils.v("FacePass--->>>RECOGNIZE: response code %d", code);
                            if (code != 0) {
                                return;
                            }
                            /* 将服务器返回的结果交回SDK进行处理来获得识别结果 */
                            String data = jsresponse.getString("data");
                            FacePassRecognitionResult[] result = MegviiFace.getInstance().mFacePassHandler.decodeResponse(data.getBytes());
                            if (result == null || result.length == 0) {
                                return;
                            }
                            for (FacePassRecognitionResult res : result) {
                                String faceToken = new String(res.faceToken);
                                LogUtils.d("FacePass--->>>RECOGNIZE: trackId=%d, faceToken=%s, resultType=%d, searchScore=%f, searchThreshold=%f, livenessScore=%f, livenessThreshold=%f",
                                        res.trackId, faceToken, res.facePassRecognitionResultType, res.detail.searchScore, res.detail.searchThreshold, res.detail.livenessScore, res.detail.livenessThreshold);
                                if ((res.facePassRecognitionResultType == FacePassRecognitionResultType.RECOG_OK) && (res.detail.searchScore >= res.detail.searchThreshold)) {
                                    FaceMegviiModel faceModel =  new FaceMegviiModel()
                                            .setFaceToken(faceToken)
                                            .setFirstName(faceToken)
                                            .setSimilarity(res.detail.searchScore);

                                    Bundle bundle = new Bundle();
                                    bundle.putInt(KEY_PREVIEW_TYPE, frameType);
                                    bundle.putByteArray(KEY_PREVIEW_DATA, frameData);
                                    Message msg = Message.obtain();
                                    msg.what = MSG_RECOG_SUC;
                                    msg.obj = faceModel;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        LogUtils.d("FacePass--->>>RECOGNIZE: onErrorResponse: %s", error.toString());
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(500000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setTag("upload_detect_result_tag");
                requestQueue.add(request);
            } else {
                LogUtils.v("FacePass--->>>RECOGNIZE: detectionResult.message.length=" + detectionResult.message.length);
                if (detectionResult.message.length != 0) {
                    FacePassRecognitionResult[] recognizeResult = FaceApi.recognizeLocalFace(detectionResult.message);
                    if (recognizeResult != null && recognizeResult.length > 0) {
                        for (FacePassRecognitionResult res : recognizeResult) {
                            String faceToken = new String(res.faceToken);
                            LogUtils.d("FacePass--->>>RECOGNIZE: trackId=%d, faceToken=%s, resultType=%d, searchScore=%f, searchThreshold=%f, livenessScore=%f, livenessThreshold=%f",
                                    res.trackId, faceToken, res.facePassRecognitionResultType, res.detail.searchScore, res.detail.searchThreshold, res.detail.livenessScore, res.detail.livenessThreshold);
                            if ((res.facePassRecognitionResultType == FacePassRecognitionResultType.RECOG_OK) && (res.detail.searchScore >= res.detail.searchThreshold)) {
                                FaceMegviiModel faceModel = mFacePresenter.verifyFaceId(faceToken);
                                if (faceModel != null) {
                                    faceModel.setSimilarity(res.detail.searchScore);

                                    Bundle bundle = new Bundle();
                                    bundle.putInt(KEY_PREVIEW_TYPE, frameType);
                                    bundle.putByteArray(KEY_PREVIEW_DATA, frameData);
                                    Message msg = Message.obtain();
                                    msg.what = MSG_RECOG_SUC;
                                    msg.obj = faceModel;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (requestQueue != null) {
            requestQueue.cancelAll("upload_detect_result_tag");
            requestQueue.stop();
        }
        running = false;
        LogUtils.d("FacePass--->>>RECOGNIZE: END!");
    }
}
