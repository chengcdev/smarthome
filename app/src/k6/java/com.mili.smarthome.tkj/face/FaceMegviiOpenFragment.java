package com.mili.smarthome.tkj.face;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.Common;
import com.android.InterCommTypeDef;
import com.android.client.MainClient;
import com.android.interf.ICardReaderListener;
import com.android.provider.FullDeviceNo;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.app.CustomVersion;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.face.megvii.MegviiFaceInfoAdapter;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.face.megvii.online.FaceRecognize;
import com.mili.smarthome.tkj.face.megvii.utils.FacePassImageUtils;
import com.mili.smarthome.tkj.face.megvii.utils.SemaphoreUtils;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassAddFaceDetectionResult;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassRecognitionResultType;

import static com.android.CommTypeDef.FaceAddType.FACETYPE_DEV;
import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;

public class FaceMegviiOpenFragment extends BaseFaceFragment implements View.OnClickListener,
        ICardReaderListener, FreeObservable.FreeObserver {

    public static final String KEY_PREVIEW_TYPE = "key_preview_type";
    public static final String KEY_PREVIEW_DATA = "key_preview_data";

    private static final int FACE_TEMPERATURE_W_MIN = 150;		// 检测体温的人脸最小宽度
    private static final int FACE_TEMPERATURE_H_MIN = 150;		// 检测体温的人脸最小高度
    private static final int FACE_TEMPERATURE_X_OFFSET = 180;	// 检测体温的人脸X轴人脸起始偏移距离
    private static final int FACE_TEMPERATURE_Y_OFFSET = 80;	// 检测体温的人脸Y轴人脸起始偏移距离

    private SurfaceView svReceive;
    private TextureView tvPreview;
    private FaceDetectView faceDetectView;

    private TextView tvHint1;
    private TextView tvHint2;
    private TextView tvRight;
    private TextView tvEdit;
    private TextView tvAdd;
    private TextView tvDel;
    private ImageView ivToggle;

    private FacePresenter<FaceMegviiModel> mFacePresenter = new MegviiFacePresenterImpl();

    private boolean isLocalGroupExist = false;
    /** <p>1识别 <p>2注册 <p>3删除 <p>4请刷卡 <p>5请选择 */
    private int mState = 0;
    private String mCardNo;
    private String mEnrollName;
    private String mDeviceNo;
    private BackTask mBackTask = new BackTask();

    private boolean mDestroyView = false;
    /** 识别队列*/
    private ArrayBlockingQueue<MegviiFaceInfoAdapter> mRecogQueue;
    /** 识别线程*/
    private RecogThread mRecogThread;
    /** 注册队列 */
    private ArrayBlockingQueue<FacePassImage> mEnrollQueue;
    /** 注册线程 */
    private EnrollThread mEnrollThread;
    /* 网络请求队列*/
    private RequestQueue requestQueue;
    private RelativeLayout mImgBack;
    private TextView mTvBack;

    /* 陌生人脸抓拍定制 */
    private long mSnapTrackId = 0;
    private long mLastTrackId = 0;
    private int mRecognizeTimes = 0;
    private boolean mSnapThreadRun = false;
    private int mSnapStrangerUse = 0;

    /* 体温状态：-1 默认值 0 体温正常 1 体温偏高 2 体温偏低 */
    private int mFaceTempState = -1;

    @Override
    public int getLayout() {
        return R.layout.fragment_face_megvii_open;
    }

    private final int MSG_RECOGNIZING = 0x10;
    private final int MSG_RECOG_SUC = 0x11;
    private final int MSG_RECOG_TIMEOUT = 0x12;
    private final int MSG_ENROLL_SUC = 0x21;
    private final int MSG_ENROLL_TIMEOUT = 0x22;
    private final int MSG_EDIT_TIMEOUT = 0x3F;

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECOGNIZING:
                tvHint1.setText(R.string.face_scan_hint1);
                tvHint2.setText(R.string.face_scan_hint2);
                if (MegviiFace.getInstance().SDK_MODE == MegviiFace.FacePassSDKMode.MODE_ONLINE) {
                    tvEdit.setVisibility(View.GONE);// Face++在线版不支持本地录入
                } else {
                    tvEdit.setVisibility(View.VISIBLE);
                }
                break;
            case MSG_RECOG_SUC:
                FaceMegviiModel faceModel = (FaceMegviiModel) msg.obj;
                Bundle bundle = msg.getData();
                int previewType = 0;
                byte[] previewData = null;
                if (bundle != null) {
                    previewType = bundle.getInt(KEY_PREVIEW_TYPE);
                    previewData = bundle.getByteArray(KEY_PREVIEW_DATA);
                }
                onRecognizeSuc(faceModel, previewType, previewData);
                break;
            case MSG_RECOG_TIMEOUT:
                onRecognizeTimeout();
                break;
            case MSG_ENROLL_SUC:
                String token = (String)msg.obj;
                onEnrollSuc(token);
                break;
            case MSG_ENROLL_TIMEOUT:
                onEnrollFail();
                break;
            case MSG_EDIT_TIMEOUT:
                setRecognition();
                break;
        }
    }

    @Override
    public void initView(View view) {
        svReceive = ViewUtils.findView(view, R.id.sv_receive);
        tvPreview = ViewUtils.findView(view, R.id.tv_preview);
        faceDetectView = ViewUtils.findView(view, R.id.detectView);
        tvHint1 = ViewUtils.findView(view, R.id.tv_hint1);
        tvHint2 = ViewUtils.findView(view, R.id.tv_hint2);
        tvRight = ViewUtils.findView(view, R.id.tv_right);
        tvEdit = ViewUtils.findView(view, R.id.tv_edit);
        tvAdd = ViewUtils.findView(view, R.id.tv_add);
        tvDel = ViewUtils.findView(view, R.id.tv_del);
        ivToggle = ViewUtils.findView(view, R.id.iv_toggle);
        mImgBack = ViewUtils.findView(view, R.id.rl_back);
        mTvBack = ViewUtils.findView(view,R.id.tv_back);
        tvEdit.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        tvDel.setOnClickListener(this);
        ivToggle.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mTvBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDestroyView = false;
        AppUtils.getInstance().stopScreenService();
        FullDeviceNo fullDeviceNo = new FullDeviceNo(mContext);
        mDeviceNo = fullDeviceNo.getDeviceNo();
        mCardNo = "";
        /* 初始化网络请求库 */
        requestQueue = Volley.newRequestQueue(mContext);
        // 识别队列
        mRecogQueue = new ArrayBlockingQueue<>(1);
        // 识别线程
        mRecogThread = new RecogThread();
        mRecogThread.start();
        // 注册队列
        mEnrollQueue = new ArrayBlockingQueue<>(1);

        SinglechipClientProxy.getInstance().setFingerState(0x01);
        SinglechipClientProxy.getInstance().setCardReaderListener(this);
        //检查本地分组是否存在
        if(!FaceApi.checkGroup()){
            isLocalGroupExist = FaceApi.createGroup();
        }else{
            isLocalGroupExist = true;
        }
        setRecognition();
        startPreview(svReceive, tvPreview);
        ivToggle.setVisibility(isEnabledRtsp() ? View.VISIBLE : View.GONE);

        /* 人脸识别计数，10后若未识别则抓拍 */
        mSnapStrangerUse = SnapParamDao.getFaceStrangerSnap();
        if (mSnapStrangerUse == 1) {
            mSnapThreadRun = true;
            new SnapThread().start();
        }

        /* 人脸体温异常提示 */
        if (CustomVersion.VERSION_K6_MEGVII_TEMPERATURE) {
            MainClient.getInstance().setFaceTemperatureListener(new InterCommTypeDef.IFaceTemperatureListener() {
                @Override
                public void FaceTemperatureResult(int state) {
                    mFaceTempState = state;
                    LogUtils.d("[FaceMegviiOpenFragment] face temperature state is " + state);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FreeObservable.getInstance().observeFree();
        FreeObservable.getInstance().addObserver(this);
    }

    @Override
    public void onPause() {
        FreeObservable.getInstance().removeObserver(this);
        FreeObservable.getInstance().cancelObserveFree();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDestroyView = true;
        stopPreview();
        mMainHandler.removeCallbacksAndMessages(null);
        SinglechipClientProxy.getInstance().setFingerState(0x00);
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        AppUtils.getInstance().startScreenService();

        FaceApi.reset();
        if (mRecogThread != null && !mRecogThread.isInterrupted()) {
            mRecogThread.interrupt();
            mRecogThread = null;
        }
        if (mEnrollThread != null && !mEnrollThread.isInterrupted()) {
            mEnrollThread.interrupt();
            mEnrollThread = null;
        }
        if (requestQueue != null) {
            requestQueue.cancelAll("upload_detect_result_tag");
            requestQueue.stop();
        }
        SemaphoreUtils.getInstance().release();
        if (mSnapStrangerUse == 1) {
            mSnapThreadRun = false;
        }
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (mState == 1 && freeTime > Const.Config.FACE_RECOGNIZE_TIMEOUT) {
            mState = 0;
            mMainHandler.sendEmptyMessage(MSG_RECOG_TIMEOUT);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                setEdit();
                break;
            case R.id.tv_add:
                setEnrollment();
                break;
            case R.id.tv_del:
                mState = 3;
                Bundle data = new Bundle();
                data.putString(FaceDelFragment.ARGS_CARD_NO, mCardNo);
                ContextProxy.sendBroadcast(Constant.Action.BODY_FACE_DEL_ACTION, data);
                break;
            case R.id.rl_back:
                SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(1);
                backMainActivity();
                break;
            case R.id.iv_toggle:
                togglePreviewType();
                break;
        }
    }

    @Override
    public void onCardRead(int cardId, int result) {
        if (mState == 4) {
            if (result == 0) {
                mCardNo = MegviiFacePresenterImpl.cardIdToString(cardId);
                UserInfoDao userInfoDao = new UserInfoDao();
                UserCardInfoModels cardInfo = userInfoDao.queryByCardNo(mCardNo);
                if (cardInfo != null) {
                    mState = 5;
                    tvRight.setText(R.string.face_manage_hint2);
                    tvRight.setVisibility(View.VISIBLE);
                    tvAdd.setVisibility(View.VISIBLE);
                    tvDel.setVisibility(View.VISIBLE);
                    tvEdit.setVisibility(View.GONE);
                }
            }
            tvHint1.setText(R.string.face_enrollment_hint3);
        }
    }

    @Override
    public void InterVideoCallBK(byte[] data, int datalen, int width, int height, int type) {
//        LogUtils.d("InterVideoCallBK------>>>>>width=" + width + "   height=" + height);

        //如果人脸功能未启用，则人脸识别业务不往下走
        if (!AppConfig.getInstance().isFaceEnabled()) {
            return;
        }
        //如果旷视人脸初始化未完成，则人脸识别业务不往下走
        if(MegviiFace.getInstance().mFacePassHandler == null){
            return;
        }
        //如果识别状态切换到注册状态，则人脸识别业务不往下走且要取消人脸框，等待刷卡操作
        if(mState != 1 && mState != 2){
            faceDetectView.setFaceInfoAdapter(null);
            faceDetectView.postInvalidate();
            return;
        }
        MegviiFaceInfoAdapter faceInfoAdapter = null;
        try {
            final byte[] dataNV21 = Common.I420ToNV21(data, width, height);
            FacePassImage image = new FacePassImage(dataNV21, width, height, MegviiFace.IMAGE_ROTATION, FacePassImageType.NV21);
            if (mState == 1) {
                // 识别
                FacePassDetectionResult detectionResult = MegviiFace.getInstance().mFacePassHandler.feedFrame(image);
                if (detectionResult != null) {
                    faceInfoAdapter = new MegviiFaceInfoAdapter()
                            .setType(type)
                            .setData(data)
                            .setWidth(width)
                            .setHeight(height)
                            .setMirror(type == 0)
                            .setDetectionResult(detectionResult);
                    if (detectionResult.message.length > 0 && SemaphoreUtils.getInstance().tryAcquire()) {
                        mRecogQueue.offer(faceInfoAdapter);
                    }
                }
            } else {//if (mState == 2) {
                // 注册
                FacePassAddFaceDetectionResult addFaceDetectionResult = MegviiFace.getInstance().mFacePassHandler.addFaceDetect(image);
                if (addFaceDetectionResult != null) {
                    faceInfoAdapter = new MegviiFaceInfoAdapter()
                            .setType(type)
                            .setData(data)
                            .setWidth(width)
                            .setHeight(height)
                            .setMirror(type == 0)
                            .setFaceList(addFaceDetectionResult.faceList);
                    if (addFaceDetectionResult.image != null) {
                        mEnrollQueue.offer(addFaceDetectionResult.image);
                    }
                }
            }
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        if (!mDestroyView && faceInfoAdapter != null && faceInfoAdapter.getFaceCount() > 0) {
            SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
            FreeObservable.getInstance().resetFreeTime();
        }
        // 本地预览才画人脸框
        if (type != getPreviewType()) {
            return;
        }
        /* 将识别到的人脸在预览界面中圈出，并在上方显示人脸位置及角度信息 */
        faceDetectView.setFaceInfoAdapter(faceInfoAdapter);
        faceDetectView.postInvalidate();
    }

    private class RecogThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                MegviiFaceInfoAdapter faceInfoAdapter;
                try {
                    faceInfoAdapter = mRecogQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    SemaphoreUtils.getInstance().release();
                    continue;
                }
                LogUtils.d("FaceMegviiOpenFragment--->>>FeedFrameThread: SDK_MODE=%s, mState=%s", MegviiFace.getInstance().SDK_MODE, mState);
                final FacePassDetectionResult detectionResult = faceInfoAdapter.getDetectionResult();
                final int frameType = faceInfoAdapter.getType();
                final byte[] frameData = faceInfoAdapter.getData();
                if(MegviiFace.getInstance().SDK_MODE == MegviiFace.FacePassSDKMode.MODE_ONLINE) {
                    //在线模式
                    if (detectionResult == null || detectionResult.message.length == 0) {
                        SemaphoreUtils.getInstance().release();
                        continue;
                    }
                    //构建http请求
                    LogUtils.d("FacePass--->>>FaceRecognize: url=%s, deviceno=%s", MegviiFace.getInstance().recognizeUrl, mDeviceNo);
                    FaceRecognize request = new FaceRecognize(MegviiFace.getInstance().recognizeUrl, mDeviceNo, detectionResult, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsresponse = new JSONObject(response);
                                int code = jsresponse.getInt("code");
                                LogUtils.d("FacePass--->>>onResponse: code=%d", code);
                                if (code != 0) {
                                    SemaphoreUtils.getInstance().release();
                                    return;
                                }
                                /* 将服务器返回的结果交回SDK进行处理来获得识别结果 */
                                String data = jsresponse.getString("data");
                                FacePassRecognitionResult[] result = MegviiFace.getInstance().mFacePassHandler.decodeResponse(data.getBytes());
                                if (result == null || result.length == 0) {
                                    SemaphoreUtils.getInstance().release();
                                    return;
                                }
                                for (FacePassRecognitionResult res : result) {
                                    String faceToken = new String(res.faceToken);
                                    LogUtils.d("MegviiScanFragment--->>>trackId=%d, faceToken=%s, resultType=%d, searchScore=%f, searchThreshold=%f, livenessScore=%f, livenessThreshold=%f",
                                            res.trackId, faceToken, res.facePassRecognitionResultType, res.detail.searchScore, res.detail.searchThreshold, res.detail.livenessScore, res.detail.livenessThreshold);
                                    if ((res.facePassRecognitionResultType == FacePassRecognitionResultType.RECOG_OK) && (res.detail.searchScore >= res.detail.searchThreshold)) {
                                        LogUtils.d("FacePass--->>>recognitionResult:faceToken=%s, trackId=%d", faceToken, res.trackId);
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
                                        mMainHandler.sendMessage(msg);
                                        break;
                                    }
                                }
                                SemaphoreUtils.getInstance().release();
                            } catch (Exception e) {
                                e.printStackTrace();
                                SemaphoreUtils.getInstance().release();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            LogUtils.d("FacePass--->>>onErrorResponse: %s", error.toString());
                            SemaphoreUtils.getInstance().release();
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(500000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    request.setTag("upload_detect_result_tag");
                    requestQueue.add(request);
                }else{
                    if (mState == 1 && isLocalGroupExist) {
                        //识别
                        LogUtils.d("FaceMegviiOpenFragment--->>>FeedFrameThread: detectionResult.message.length=" + detectionResult.message.length);
                        if (detectionResult.message.length != 0) {
                            FacePassRecognitionResult[] recognizeResult = FaceApi.recognizeLocalFace(detectionResult.message);
                            if (recognizeResult != null && recognizeResult.length > 0) {
                                int index = 0;
                                for (FacePassRecognitionResult res : recognizeResult) {
                                    String faceToken = new String(res.faceToken);
                                    LogUtils.d("MegviiScanFragment--->>>trackId=%d, faceToken=%s, resultType=%d, searchScore=%f, searchThreshold=%f, livenessScore=%f, livenessThreshold=%f",
                                            res.trackId, faceToken, res.facePassRecognitionResultType, res.detail.searchScore, res.detail.searchThreshold, res.detail.livenessScore, res.detail.livenessThreshold);
                                    if ((res.facePassRecognitionResultType == FacePassRecognitionResultType.RECOG_OK) && (res.detail.searchScore >= res.detail.searchThreshold)) {
                                        LogUtils.d("FacePass--->>>recognitionResult:faceToken=%s, trackId=%d", faceToken, res.trackId);
                                        FaceMegviiModel faceModel = mFacePresenter.verifyFaceId(faceToken);
                                        if (faceModel != null) {

                                            /* 判断人脸位置及体温是否符合要求 */
                                            if (CustomVersion.VERSION_K6_MEGVII_TEMPERATURE) {
                                                int retCode = faceTempScanStart(detectionResult.faceList[index], faceInfoAdapter.getWidth(), faceInfoAdapter.getHeight());
                                                if (retCode == 1) {
                                                    index++;
                                                    continue;
                                                } else if (retCode == 2) {
                                                    break;
                                                }
                                            }

                                            faceModel.setSimilarity(res.detail.searchScore);

                                            Bundle bundle = new Bundle();
                                            bundle.putInt(KEY_PREVIEW_TYPE, frameType);
                                            bundle.putByteArray(KEY_PREVIEW_DATA, frameData);
                                            Message msg = Message.obtain();
                                            msg.what = MSG_RECOG_SUC;
                                            msg.obj = faceModel;
                                            msg.setData(bundle);
                                            mMainHandler.sendMessage(msg);
                                            break;
                                        }
                                    }
                                    /* 陌生人抓拍 */
                                    strangerSanp(res.trackId, res.facePassRecognitionResultType, frameData, faceInfoAdapter.getWidth(), faceInfoAdapter.getHeight());
                                }
                            } else {
                                /* 未注册过人脸情况 */
                                if (mSnapStrangerUse == 1 && detectionResult.faceList != null) {
                                    for (FacePassFace pass : detectionResult.faceList) {
                                        strangerSanp(pass.trackId, 4, frameData, faceInfoAdapter.getWidth(), faceInfoAdapter.getHeight());
                                    }
                                }
                            }
                        }
                    }
                    SemaphoreUtils.getInstance().release();
                }
            }
        }
    }

    /**
     * 离线模式注册线程
     */
    private class EnrollThread extends Thread {
        @Override
        public void run() {
            long startTime = SystemClock.elapsedRealtime();
            do {
                try {
                    if (isLocalGroupExist && mState == 2) {
                        FacePassImage image = mEnrollQueue.poll(1000, TimeUnit.MILLISECONDS);
                        if (image != null) {
                            final String token = FaceApi.addAndBindLocalFace(FacePassImageUtils.decode(image));
                            if (token != null && token.length() != 0) {
                                LogUtils.d("FacePass--->>>addAndBindLocalFace: faceToken=%s", token);
                                //注册成功
                                Message msg = Message.obtain();
                                msg.what = MSG_ENROLL_SUC;
                                msg.obj = token;
                                mMainHandler.sendMessageDelayed(msg, 1000);
                                break;
                            }
                        }
                    }
                    long spendTime = SystemClock.elapsedRealtime() - startTime;
                    if (spendTime > Const.Config.FACE_ENROLL_TIMEOUT) {
                        mMainHandler.sendEmptyMessage(MSG_ENROLL_TIMEOUT);
                        break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (!isInterrupted());
        }
    }

    private void setEdit() {
        mState = 4;
        SinglechipClientProxy.getInstance().setCardState(0x02);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        tvHint1.setText(R.string.face_enrollment_hint1);
        tvRight.setText(R.string.face_manage_hint1);
        tvRight.setVisibility(View.VISIBLE);
        tvAdd.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
        mMainHandler.removeMessages(MSG_RECOG_TIMEOUT);
        mMainHandler.sendEmptyMessageDelayed(MSG_EDIT_TIMEOUT, 20000);
    }

    private void setRecognition() {
        PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        tvHint1.setText(R.string.face_scan_hint1);
        tvHint2.setText(R.string.face_scan_hint2);
        if (MegviiFace.getInstance().SDK_MODE == MegviiFace.FacePassSDKMode.MODE_ONLINE) {
            tvEdit.setVisibility(View.GONE);// Face++在线版不支持本地录入
        } else {
            tvEdit.setVisibility(View.VISIBLE);
        }
        tvRight.setVisibility(View.GONE);
        tvAdd.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        mState = 1;
        mEnrollName = "";
        mMainHandler.removeMessages(MSG_ENROLL_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);

        /* 陌生人脸抓拍 */
        if (mSnapStrangerUse == 1) {
            mSnapTrackId = 0;
            mLastTrackId = 0;
            mRecognizeTimes = 0;
        }
    }

    private void setEnrollment() {
        PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        tvHint1.setText(R.string.face_scan_hint1);
        tvHint2.setText(R.string.face_enrollment_hint2);
        tvRight.setVisibility(View.GONE);
        tvAdd.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
        mState = 2;
        mEnrollName = BaseFacePresenter.genResidentFaceId(mCardNo);
        mMainHandler.removeMessages(MSG_EDIT_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);

        mEnrollThread = new EnrollThread();
        mEnrollThread.start();
    }

    private void onRecognizeSuc(FaceMegviiModel faceModel, int previewType, byte[] previewData) {
        FaceProtocolInfo faceInfo = MegviiFacePresenterImpl.convert(faceModel);
        int result = Common.validity(faceInfo);
        if (result == SUCCESS_STATE){
            result = faceRecognizeSuccReport(faceInfo, faceModel.getSimilarity(), previewType, previewData);
        }
        else if (result == FAIL_STATE){
            result = 0;
        }
        if (result == 1) {
            String roomNoStr = null;
            tvHint1.setText(R.string.face_recognition_suc1);
            tvHint2.setText(R.string.face_recognition_suc2);
            tvRight.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
            tvDel.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
            if (faceModel.getRoomNoState() == 0) {
                int type = Common.getFaceFirstNameType(faceModel.getFirstName());
                roomNoStr = Common.getFaceFirstNameStr(faceModel.getFirstName());
                if (roomNoStr != null){
                    if (type == FACETYPE_DEV){
                        UserInfoDao userInfoDao = new UserInfoDao();
                        roomNoStr  = userInfoDao.getRoomNoByCardNo(roomNoStr);
                    }
                }
            }
            LogUtils.e("roomStr...: "+roomNoStr);
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNoStr, 0);
            mMainHandler.removeMessages(MSG_RECOGNIZING);
            mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZING, 3000);
            if (BuildConfig.isEnabledFaceValid && (faceModel.getLifecycle() > VALID_LIFECYCLE_MODE)){
                mFacePresenter.subLifecycleInfo(faceModel.getFaceToken());
            }
        } else if (result == -1) {
            mFacePresenter.delFaceInfoById(faceModel.getFirstName());
        }
    }

    private void onRecognizeTimeout() {
        //tvHint2.setText(R.string.face_recognition_fail);
        tvRight.setVisibility(View.GONE);
        tvAdd.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
        //mMainHandler.postDelayed(mBackTask, 2000);
        if (AppConfig.getInstance().getScreenSaver() == 1 || AppConfig.getInstance().getPowerSaving() == 1) {
            AppUtils.getInstance().openScreenService();
            return;
        }
        backMainActivity();
    }

    private void onEnrollSuc(String token) {
        LogUtils.d("FacePass--->>>onEnrollSuc: token=" + token);
        // 添加人脸信息
        mState = 0;
        FaceMegviiModel faceInfoModel = new FaceMegviiModel();
        faceInfoModel.setFirstName(mEnrollName);
        faceInfoModel.setCardNo(mCardNo);
        faceInfoModel.setFaceToken(token);
        mFacePresenter.addFaceInfo(faceInfoModel);
        ContextProxy.sendBroadcast(Constant.Action.BODY_FACE_ENROLL_ACTION);
    }

    private void onEnrollFail() {
        mState = 0;
        PlaySoundUtils.playAssetsSound(CommStorePathDef.SET_ERR_PATH);
        mMainHandler.postDelayed(mBackTask, 2000);
    }

    private class BackTask implements Runnable {
        @Override
        public void run() {
            backMainActivity();
        }
    }

    /**
     * 陌生人抓拍
     * @param trackId   trackId
     * @param resultType 识别结果
     * @param frameData 一帧视频数据
     * @param width     宽
     * @param height    高
     */
    private void strangerSanp(long trackId, int resultType, byte[] frameData, int width, int height) {
        LogUtils.d("=======[MegviiScanFragment] [strangerSanp] trackId=%d, type=%d, photolen=%d mRecognizeTimes=%d mLastTrackId=%d",
                trackId, resultType, frameData.length, mRecognizeTimes, mLastTrackId);

        /* 不启用陌生人脸抓拍 */
        if (mSnapStrangerUse == 0) {
            return;
        }

        /* 人脸识别成功不抓拍 */
        if (resultType == 0) {
            mRecognizeTimes = 0;
            return;
        }

        /* 不同陌生人trackId不同 */
        if (trackId != mLastTrackId) {
            mLastTrackId = trackId;
            mRecognizeTimes = 0;
            return;
        }

        /* 同一个trackId不重复抓拍 */
        if (trackId == mSnapTrackId) {
            return;
        }

        /* 保存图片文件并进行上传服务器 */
        if (mRecognizeTimes > 10 || resultType == 2) {
            mSnapTrackId = trackId;
            mRecognizeTimes = 0;
            String photoPath = savePhoto(frameData, width, height);
            if (photoPath != null) {
                MainClient.getInstance().Main_FaceSnapStranger(photoPath);
            }
            LogUtils.d("=======[MegviiScanFragment] [strangerSanp] ok ");
        }
    }

    /**
     * 保存图片文件
     * @param data      图片数据
     * @param width     宽
     * @param height    高
     * @return          图片路径
     */
    private String savePhoto(byte[] data, int width, int height) {
        final byte[] dataNV21 = Common.I420ToNV21(data, width, height);
        String filename = CommStorePathDef.SNAP_DIR_PATH+ "/strangerFace.jpg";
        boolean ret = Common.WriteNV21ToJpg(dataNV21, width, height, filename);
        if (ret) {
            LogUtils.d("[MegviiScanFragment][savePhoto] photoName is " + filename);
            return filename;
        }
        return null;
    }

    /**
     * 陌生人脸抓拍定时器计数
     */
    private class SnapThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (mSnapThreadRun) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mRecognizeTimes++;
                LogUtils.d(" ======== mRecognizeTimes = %d ========", mRecognizeTimes);
            }
        }
    }

    /**
     * 判断人脸所处位置是否符合要求
     * @param face      人脸信息
     * @param width     视频宽
     * @param height    视频高
     * @return          true/false
     */
    private boolean faceLocation(FacePassFace face, int width, int height) {
        if (face == null) {
            LogUtils.d("[FaceMegviiOpenFragment -> faceLocation] face is null.");
            return true;
        }
        int faceWidth = face.rect.right = face.rect.left;
        int faceHeight = face.rect.bottom - face.rect.top;
        LogUtils.d(" [FaceMegviiOpenFragment -> faceLocation] videWH[%d, %d], faceWH[%d, %d] x=%d, y=%d",
                width, height, faceWidth, faceHeight, face.rect.left, face.rect.top);

        if (faceWidth < FACE_TEMPERATURE_W_MIN || faceHeight < FACE_TEMPERATURE_H_MIN) {
            LogUtils.d(" face width or height is out of range.");
            return false;
        }
        if (face.rect.left < FACE_TEMPERATURE_X_OFFSET || face.rect.right > width-FACE_TEMPERATURE_X_OFFSET) {
            LogUtils.d(" face left or right is out of range.");
            return false;
        }
        if (face.rect.top < FACE_TEMPERATURE_Y_OFFSET) {
            LogUtils.d(" face top is out of range.");
            return false;
        }
        return true;
    }

    /**
     * 开始检测体温
     * @param facePass  人脸区域信息
     * @param width     视频帧宽
     * @param height    视频帧高
     * @return    0 正常 1 位置不符 2 体温异常
     */
    private int faceTempScanStart(FacePassFace facePass, int width, int height) {
        boolean ret = faceLocation(facePass, width, height);
        if (!ret) {
            return 1;
        }

        /* 开始检测体温 */
        mFaceTempState = -1;
        MainClient.getInstance().Main_FaceTempScanStart();

        /* 等待体温检测结果上报，超时1秒失败 */
        int timeout = 10;
        while (mFaceTempState == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout--;
            if (timeout < 0) {
                break;
            }
        }
        LogUtils.d("[FaceMegviiOpenFragment] timeout=%d, mFaceTempState=%d.", timeout, mFaceTempState);

        if (mFaceTempState == 1) {
            return 2;
        }
        return 0;
    }
}
