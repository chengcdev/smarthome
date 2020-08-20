package com.mili.smarthome.tkj.face;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.Common;
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
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.face.megvii.MegviiFaceInfoAdapter;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.face.megvii.online.FaceRecognize;
import com.mili.smarthome.tkj.face.megvii.utils.FacePassImageUtils;
import com.mili.smarthome.tkj.face.megvii.utils.SemaphoreUtils;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.view.HintView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

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
import static com.android.CommTypeDef.FaceAddType.FACETYPE_PC;
import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;

public class MegviiScanFragment extends BaseFaceFragment implements View.OnClickListener, ICardReaderListener, FreeObservable.FreeObserver {

    public static final String KEY_PREVIEW_TYPE = "key_preview_type";
    public static final String KEY_PREVIEW_DATA = "key_preview_data";

    private SurfaceView svReceive;
    private TextureView tvPreview;
    private FaceDetectView faceDetectView;

    private View flFace;
    private View flAddSuc;
    private HintView hvHint;
//    private TextView tvHint0;
    private TextView tvHint1;
    private TextView tvHint2;
    private TextView tvHint3;
    private View llOperHint;
    private TextView tvHint4;
    private TextView tvHint5;
    private LinearLayout mLlBtnGroup;
    private LinearLayout mLlHint;
    private ImageView mIvZoom, mIvToggle;
    private RadioButton mRbPwd, mRbQrcode, mRbCenter, mRbResident, mRbFace;

    private FacePresenter<FaceMegviiModel> mFacePresenter = new MegviiFacePresenterImpl();

    private boolean isLocalGroupExist = false;
    private String mCardNo;
    private String mEnrollName;
    private String mDeviceNo;

    /** <p>1识别 <p>2注册 <p>3删除 <p>4请刷卡 <p>5请选择 <p>6注册成功 */
    private int mState = 1;
    private boolean mFullScreen = false;
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

    /* 陌生人脸抓拍定制 */
    private long mSnapTrackId = 0;
    private long mLastTrackId = 0;
    private int mRecognizeTimes = 0;
    private boolean mSnapThreadRun = false;
    private int mSnapStrangerUse = 0;

    private FuncCodeListener mListener;

    public void setFuncCodeListener(FuncCodeListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_face_megvii;
    }

    @Override
    protected void bindView() {
        super.bindView();
        flFace = findView(R.id.fl_content);
        flAddSuc = findView(R.id.fl_add_suc);
        hvHint = findView(R.id.hv_hint);
        svReceive = findView(R.id.sv_receive);
        assert svReceive != null;
        svReceive.setOnClickListener(this);
        tvPreview = findView(R.id.tv_preview);
        assert tvPreview != null;
        tvPreview.setOnClickListener(this);
        faceDetectView = findView(R.id.detectView);
//        tvHint0 = findView(R.id.tv_hint0);
        tvHint1 = findView(R.id.tv_hint1);
        tvHint2 = findView(R.id.tv_hint2);
        tvHint3 = findView(R.id.tv_hint3);
        llOperHint = findView(R.id.ll_oper_hint);
        tvHint4 = findView(R.id.tv_hint4);
        tvHint5 = findView(R.id.tv_hint5);

        mLlBtnGroup = findView(R.id.radiogrop);
        assert mLlBtnGroup != null;
        mLlBtnGroup.setVisibility(View.GONE);
        mLlHint = findView(R.id.fl_hint);
        mIvZoom = findView(R.id.iv_zomm);
        assert mIvZoom != null;
        mIvZoom.setOnClickListener(this);
        mIvToggle = findView(R.id.iv_toggle);
        assert mIvToggle != null;
        mIvToggle.setOnClickListener(this);

        mRbPwd = findView(R.id.rb_password);
        mRbQrcode = findView(R.id.rb_qrcode);
        mRbCenter = findView(R.id.rb_center);
        mRbResident = findView(R.id.rb_resident);
        mRbFace = findView(R.id.rb_face);
        mRbPwd.setOnClickListener(this);
        mRbQrcode.setOnClickListener(this);
        mRbCenter.setOnClickListener(this);
        mRbResident.setOnClickListener(this);
        mRbFace.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDestroyView = false;
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

        setKeyboardMode(KeyboardCtrl.KEYMODE_EDIT);
        showView(true);
        showRadioChecked(MainActivity.FUNCTION_FACE);

        /* 人脸识别计数，10后若未识别则抓拍 */
        mSnapStrangerUse = SnapParamDao.getFaceStrangerSnap();
        if (mSnapStrangerUse == 1) {
            mSnapThreadRun = true;
            new SnapThread().start();
        }
        LogUtils.d("MegviiScanFragment--->>>onViewCreated()");
    }

    @Override
    public void onResume() {
        super.onResume();
        // 解决在onViewCreated中设置有时候无效问题
        showRadioChecked(MainActivity.FUNCTION_FACE);
        FreeObservable.getInstance().addObserver(this);
    }

    @Override
    public void onPause() {
        FreeObservable.getInstance().removeObserver(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDestroyView = true;
        LogUtils.d("MegviiScanFragment--->>>onDestroyView()");
        stopPreview();
        SinglechipClientProxy.getInstance().setFingerState(0x00);
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);

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

    private final int MSG_RECOGNIZING = 0x10;
    private final int MSG_RECOG_SUC = 0x11;
    private final int MSG_RECOG_TIMEOUT = 0x12;
    private final int MSG_ENROLL_SUC = 0x21;
    private final int MSG_ENROLL_TIMEOUT = 0x22;
    private final int MSG_EDIT_TIMEOUT = 0x3F;
    private final int MSG_EXIT = 0x10FF;

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECOGNIZING:
                tvHint1.setText(R.string.face_scan_hint4);
                tvHint2.setText(R.string.face_scan_hint1);
                tvHint3.setText("");
                llOperHint.setVisibility(View.INVISIBLE);
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
            case MSG_EXIT:
                backToMain();
                break;
        }
    }

    private void showView(boolean isFull) {
        if (isFull) {
            mLlBtnGroup.setVisibility(View.VISIBLE);
            mIvZoom.setVisibility(View.VISIBLE);
            mIvToggle.setVisibility(isEnabledRtsp() ? View.VISIBLE : View.INVISIBLE);
            mLlHint.setVisibility(View.INVISIBLE);
//            tvHint0.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = mLlBtnGroup.getLayoutParams();
            params.height = getResources().getDimensionPixelOffset(R.dimen.dp_90);
//            params.height = getResources().getDimensionPixelOffset(R.dimen.dp_120);
            mLlBtnGroup.setLayoutParams(params);

            if (mListener != null) {
                mListener.onFuncCode(MainActivity.FUNCTION_FACE, 1);
            }
        } else {
            mLlBtnGroup.setVisibility(View.GONE);
            mIvZoom.setVisibility(View.INVISIBLE);
            mIvToggle.setVisibility(View.INVISIBLE);
            mLlHint.setVisibility(View.VISIBLE);
//            tvHint0.setVisibility(View.GONE);

            if (mListener != null) {
                mListener.onFuncCode(MainActivity.FUNCTION_FACE, 0);
            }
        }
        mFullScreen = isFull;
    }

    private void showRadioChecked(int funcCode) {
        LogUtils.d("MegviiScanFragment--->>>showRadioChecked: funcCode is %d", funcCode);
        mRbPwd.setChecked(false);
        mRbQrcode.setChecked(false);
        mRbCenter.setChecked(false);
        mRbResident.setChecked(false);
        mRbFace.setChecked(false);
        switch (funcCode) {
            case MainActivity.FUNCTION_PASSWORD:
                mRbPwd.setChecked(true);
                break;
            case MainActivity.FUNCTION_QRCODE:
                mRbQrcode.setChecked(true);
                break;
            case MainActivity.FUNCTION_CENTER:
                mRbCenter.setChecked(true);
                break;
            case MainActivity.FUNCTION_RESIDENT:
                mRbResident.setChecked(true);
                break;
            case MainActivity.FUNCTION_FACE:
                mRbFace.setChecked(true);
                break;
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
                LogUtils.d("MegviiScanFragment--->>>FeedFrameThread: SDK_MODE=%s, mState=%d", MegviiFace.getInstance().SDK_MODE, mState);
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
                        LogUtils.d("=================FeedFrameThread  detectionResult.message.length=" + detectionResult.message.length);
                        if (detectionResult.message.length != 0) {
                            FacePassRecognitionResult[] recognizeResult = FaceApi.recognizeLocalFace(detectionResult.message);
                            if (recognizeResult != null && recognizeResult.length > 0) {
                                for (FacePassRecognitionResult res : recognizeResult) {
                                    String faceToken = new String(res.faceToken);
                                    LogUtils.d("MegviiScanFragment--->>>trackId=%d, faceToken=%s, resultType=%d, searchScore=%f, searchThreshold=%f, livenessScore=%f, livenessThreshold=%f",
                                            res.trackId, faceToken, res.facePassRecognitionResultType, res.detail.searchScore, res.detail.searchThreshold, res.detail.livenessScore, res.detail.livenessThreshold);
                                    if ((res.facePassRecognitionResultType == FacePassRecognitionResultType.RECOG_OK) && (res.detail.searchScore >= res.detail.searchThreshold)) {
                                        LogUtils.d("FacePass--->>>recognitionResult:faceToken=%s, trackId=%d", faceToken, res.trackId);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_password:
                gotoFuction(MainActivity.FUNCTION_PASSWORD);
                break;
            case R.id.rb_qrcode:
                gotoFuction(MainActivity.FUNCTION_QRCODE);
                break;
            case R.id.rb_center:
                gotoFuction(MainActivity.FUNCTION_CENTER);
                break;
            case R.id.rb_resident:
                gotoFuction(MainActivity.FUNCTION_RESIDENT);
                break;
            case R.id.iv_zomm:
                showView(false);
                break;
            case R.id.iv_toggle:
                togglePreviewType();
                break;
            case R.id.sv_receive:
            case R.id.tv_preview:
                LogUtils.d(" MegviiScanFragment: mState is " + mState + ", mFullScreen is " + mFullScreen);
                if (mState == 1 && !mFullScreen) {  //人脸识别状态下方可全屏
                    showView(true);
                    showRadioChecked(MainActivity.FUNCTION_FACE);
                }
                break;
        }
    }

    private void gotoFuction(int funcCode) {
        stopPreview();

        showRadioChecked(funcCode);
        if (mListener != null) {
            mListener.onFuncCode(funcCode, 0);
        }
        mLlBtnGroup.setVisibility(View.GONE);
    }

    /** 退回主界面时需更改fragment尺寸 */
    private void backToMain() {
        requestBack();
        if (mListener != null) {
            mListener.onFuncCode(MainActivity.FUNCTION_MAIN, 0);
        }
        mLlBtnGroup.setVisibility(View.GONE);
    }

    private Drawable getDrawable(@DrawableRes int resid) {
        Drawable drawable = ContextCompat.getDrawable(mContext, resid);
        if (drawable == null)
            return null;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    @Override
    public void onCardRead(int cardId, int result) {
        if (mState == 4) {
            if (result == 0) {
                mCardNo = MegviiFacePresenterImpl.cardIdToString(cardId);
                setEdit();
            } else {
                tvHint2.setText(R.string.comm_text_f2);
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRecognition();
                    }
                }, 1000);
            }
        }
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        if (mState == 5) {
            if (code == 1) {
                setEnrollment();
            } else if (code == 2) {
                mState = 3;
                tvHint1.setText(R.string.face_scan_hint4);
                tvHint2.setText(R.string.face_manage_del_confirm);
                tvHint3.setText(R.string.face_manage_hint2);
                llOperHint.setVisibility(View.VISIBLE);
                tvHint4.setText(R.string.face_back_hint);
                tvHint4.setCompoundDrawables(null, null, getDrawable(R.drawable.set_cancel_icon), null);
                tvHint5.setText(R.string.set_hint2);
                tvHint5.setCompoundDrawables(null, null, getDrawable(R.drawable.set_sure_icon), null);
            }
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (mState == 1) {
            if (MegviiFace.getInstance().SDK_MODE == MegviiFace.FacePassSDKMode.MODE_ONLINE) {
                return false;// Face++在线版不支持本地录入
            }
            mState = 4;
            SinglechipClientProxy.getInstance().setCardState(0x02);
            SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
            tvHint1.setText(R.string.face_scan_hint4);
            tvHint2.setText(R.string.face_scan_hint1);
            tvHint3.setText(R.string.face_manage_hint1);
            mMainHandler.removeMessages(MSG_RECOG_TIMEOUT);
            mMainHandler.sendEmptyMessageDelayed(MSG_EDIT_TIMEOUT, Const.Config.FACE_ENROLL_TIMEOUT);
        } else if (mState == 3) {
            delFaceInfo(mCardNo);
        } else if (mState == 6) {
            backToMain();
        }
        return true;
    }

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        if (mState == 3) {
            setEdit();
        } else {
            SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(1);
            backToMain();
        }
        return true;
    }

    /** 进入编辑状态 */
    private void setEdit() {
        mState = 5;
        flFace.setVisibility(View.VISIBLE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.GONE);
        tvHint1.setText(R.string.face_scan_hint4);
        tvHint2.setText(R.string.face_scan_hint1);
        tvHint3.setText(R.string.face_manage_hint2);
        llOperHint.setVisibility(View.VISIBLE);
        tvHint4.setText(R.string.face_add_hint);
        tvHint4.setCompoundDrawables(null, null, null, null);
        tvHint5.setText(R.string.face_del_hint);
        tvHint5.setCompoundDrawables(null, null, null, null);
        mMainHandler.removeMessages(MSG_EDIT_TIMEOUT);
        mMainHandler.sendEmptyMessageDelayed(MSG_EDIT_TIMEOUT, 20000);
        setKeyboardMode(KeyboardCtrl.KEYMODE_SET);
    }

    /** 进入识别状态 */
    private void setRecognition() {
        PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        flFace.setVisibility(View.VISIBLE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.GONE);
        tvHint1.setText(R.string.face_scan_hint4);
        tvHint2.setText(R.string.face_scan_hint1);
        tvHint3.setText("");
        llOperHint.setVisibility(View.INVISIBLE);
        mEnrollName = "";
        mState = 1;
        mMainHandler.removeMessages(MSG_ENROLL_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
        setKeyboardMode(KeyboardCtrl.KEYMODE_EDIT);

        /* 陌生人脸抓拍 */
        if (mSnapStrangerUse == 1) {
            mSnapTrackId = 0;
            mLastTrackId = 0;
            mRecognizeTimes = 0;
        }
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
            flFace.setVisibility(View.VISIBLE);
            flAddSuc.setVisibility(View.GONE);
            hvHint.setVisibility(View.GONE);
            tvHint1.setText(R.string.face_recognition_suc2);
            tvHint2.setText(R.string.face_recognition_suc1);
            tvHint3.setText("");
            llOperHint.setVisibility(View.INVISIBLE);
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
//        tvHint1.setText(R.string.face_recognition_fail);
//        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
        if (AppConfig.getInstance().getScreenSaver() == 1) {
            FreeObservable.getInstance().startScreenSaver();
        } else if (AppConfig.getInstance().getPowerSaving() == 1) {
            FreeObservable.getInstance().systemSleep();
        }
        backToMain();
    }

    /** 进入录入状态 */
    private void setEnrollment() {
        PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        flFace.setVisibility(View.VISIBLE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.GONE);
        tvHint1.setText(R.string.face_enrollment_hint2);
        tvHint2.setText(R.string.face_scan_hint1);
        tvHint3.setText("");
        llOperHint.setVisibility(View.INVISIBLE);
        mState = 2;
        mEnrollName = BaseFacePresenter.genResidentFaceId(mCardNo);
        mMainHandler.removeMessages(MSG_EDIT_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);

        mEnrollThread = new EnrollThread();
        mEnrollThread.start();
    }

    private void onEnrollSuc(String token) {
        LogUtils.d("FacePass--->>>onEnrollSuc: token=" + token);
        mState = 6;
        // 添加人脸信息
        FaceMegviiModel faceInfoModel = new FaceMegviiModel();
        faceInfoModel.setFirstName(mEnrollName);
        faceInfoModel.setCardNo(mCardNo);
        faceInfoModel.setFaceToken(token);
        mFacePresenter.addFaceInfo(faceInfoModel);
        flFace.setVisibility(View.GONE);
        flAddSuc.setVisibility(View.VISIBLE);
        hvHint.setVisibility(View.GONE);
        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 10000);
    }

    private void onEnrollFail() {
        mState = 0;
        PlaySoundUtils.playAssetsSound(CommStorePathDef.SET_ERR_PATH);
        tvHint1.setText(R.string.face_enrollment_hint2);
        tvHint2.setText(R.string.face_scan_hint1);
        tvHint3.setText("");
        llOperHint.setVisibility(View.GONE);
        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
    }

    private void delFaceInfo(String cardNo) {
        flFace.setVisibility(View.GONE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.VISIBLE);
        hvHint.setHint(R.string.set_success, R.color.txt_white);
        mFacePresenter.delFaceInfo(cardNo);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setEdit();
            }
        }, 2000);
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
}