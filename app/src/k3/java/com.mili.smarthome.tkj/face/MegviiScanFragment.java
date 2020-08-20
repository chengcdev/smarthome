package com.mili.smarthome.tkj.face;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
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
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.app.CustomVersion;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.face.camera.CameraManager;
import com.mili.smarthome.tkj.face.camera.CameraPreview;
import com.mili.smarthome.tkj.face.camera.CameraPreviewData;
import com.mili.smarthome.tkj.face.camera.ComplexFrameHelper;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.face.megvii.MegviiFaceInfoAdapter;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.face.megvii.online.FaceRecognize;
import com.mili.smarthome.tkj.face.megvii.utils.FacePassImageUtils;
import com.mili.smarthome.tkj.face.megvii.utils.SemaphoreUtils;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassAddFaceDetectionResult;
import mcv.facepass.types.FacePassAgeGenderResult;
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
import static com.mili.smarthome.tkj.face.megvii.offline.FaceApi.GROUP_NAME;

public class MegviiScanFragment extends BaseFaceFragment implements View.OnClickListener, ICardReaderListener {
    private static final String TAG = "lfl";
    public static final String KEY_PREVIEW_TYPE = "key_preview_type";
    public static final String KEY_PREVIEW_DATA = "key_preview_data";

    private SurfaceView svReceive;
    private TextureView tvPreview;
    /* 在预览界面圈出人脸 */
    private FaceDetectView faceDetectView;
    private View flFace;
    private ViewStub stubDel;
    private TextView tvDelHint;
    private TextView tvHint1;
    private TextView tvHint2;
    private TextView tvRight;
    private TextView tvEdit;
    private TextView tvAdd;
    private TextView tvDel;
    private TextView tvToggle;

    private FacePresenter<FaceMegviiModel> mFacePresenter = new MegviiFacePresenterImpl();

    private boolean isLocalGroupExist = false;
    /**
     * <p>1识别 <p>2注册 <p>3删除 <p>4请刷卡 <p>5请选择
     */
    private int mState = 0;
    private String mCardNo;
    private String mEnrollName;
    private String mDeviceNo;

    private boolean mDestroyView = false;
    /**
     * 识别队列
     */
    private ArrayBlockingQueue<MegviiFaceInfoAdapter> mRecogQueue;
    /*DetectResult queue*/
    private ArrayBlockingQueue<MegviiFaceInfoAdapter> mDetectResultQueue;
    /**
     * 识别线程
     */
//    private RecogThread mRecogThread;
    private FeedFrameThread mFeedFrameThread;
    private RecognizeThread mRecognizeThread;
    private long mRecoLastTrackId = 0;
    /**
     * 注册队列
     */
    private ArrayBlockingQueue<FacePassImage> mEnrollQueue;
    /**
     * 注册线程
     */
    private EnrollThread mEnrollThread;
    /* 网络请求队列*/
    private RequestQueue requestQueue;

    /* 陌生人脸抓拍定制 */
    private long mSnapTrackId = 0;
    private long mLastTrackId = 0;
    private int mRecognizeTimes = 0;
    private boolean mSnapThreadRun = false;
    private int mSnapStrangerUse = 0;

    /* 是否关屏后开人脸界面 */
    private boolean mScreenOff = false;

    /* 相机预览界面 */
    private CameraPreview mIRCameraView;
    private CameraManager mIRCameraManager;
    private static final int cameraWidth = 1280;
    private static final int cameraHeight = 720;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_face_megvii_scan;
    }

    @Override
    protected void bindView() {
        flFace = findView(R.id.fl_face);
        tvDelHint = findView(R.id.tv_del_hint);
        svReceive = findView(R.id.sv_receive);
        tvPreview = findView(R.id.tv_preview);
        faceDetectView = findView(R.id.detectView);
        tvHint1 = findView(R.id.tv_hint1);
        tvHint2 = findView(R.id.tv_hint2);
        tvRight = findView(R.id.tv_right);
        tvEdit = findView(R.id.tv_edit);
        tvAdd = findView(R.id.tv_add);
        tvDel = findView(R.id.tv_del);
        tvToggle = findView(R.id.tv_toggle);

        tvEdit.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        tvDel.setOnClickListener(this);
        tvToggle.setOnClickListener(this);

        stubDel = findView(R.id.stub_del);
        stubDel.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                ViewUtils.findView(view, R.id.btn_cancel).setOnClickListener(MegviiScanFragment.this);
                ViewUtils.findView(view, R.id.btn_confirm).setOnClickListener(MegviiScanFragment.this);
            }
        });
        mIRCameraView = findView(R.id.preview2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDestroyView = false;

        final View vwSurface = findView(R.id.fl_surface);
        ViewUtils.applyScale(vwSurface, 16.0 / 9.0);

        FullDeviceNo fullDeviceNo = new FullDeviceNo(mContext);
        mDeviceNo = fullDeviceNo.getDeviceNo();

        /* 初始化网络请求库 */
        requestQueue = Volley.newRequestQueue(mContext);
        // 识别队列
        mRecogQueue = new ArrayBlockingQueue<>(1);
        mDetectResultQueue = new ArrayBlockingQueue<>(5);
        // 识别线程
//        mRecogThread = new RecogThread();
//        mRecogThread.start();
        // 识别线程
        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();

        // 检测线程
        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();

        // 注册队列
        mEnrollQueue = new ArrayBlockingQueue<>(1);

        SinglechipClientProxy.getInstance().setFingerState(0x01);
        SinglechipClientProxy.getInstance().setCardReaderListener(this);
        //检查本地分组是否存在
        if (!FaceApi.checkGroup()) {
            isLocalGroupExist = FaceApi.createGroup();
        } else {
            isLocalGroupExist = true;
        }
        setRecognition();
        startPreview(svReceive, tvPreview);
        tvToggle.setVisibility(isEnabledRtsp() ? View.VISIBLE : View.GONE);

        /* 人脸识别计数，10后若未识别则抓拍 */
        mSnapStrangerUse = SnapParamDao.getFaceStrangerSnap();
        if (mSnapStrangerUse == 1) {
            mSnapThreadRun = true;
            new SnapThread().start();
        }

        mIRCameraManager = new CameraManager();
        mIRCameraManager.setPreviewDisplay(mIRCameraView);
        mIRCameraManager.setListener(new CameraManager.CameraListener() {
            @Override
            public void onPictureTaken(CameraPreviewData cameraPreviewData) {
                ComplexFrameHelper.addIRFrame(cameraPreviewData);
//                Log.d(TAG, " IR "+"heigh: " + cameraPreviewData.height + "  width  " + cameraPreviewData.width
//                        + "  mirror  " + cameraPreviewData.mirror + "  rotation  " + cameraPreviewData.rotation);
            }
        });
        Log.d(TAG, "onViewCreated: ");
    }

    @Override
    public void onResume() {
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
        SinglechipClientProxy.getInstance().IRLedCtrl(true);
        mIRCameraManager.open(getActivity().getWindowManager(), true, cameraWidth, cameraHeight);
        super.onResume();
        PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        Log.d(TAG, "onResume: open Camera");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDestroyView = true;
        stopPreview();

        FaceApi.reset();
//        mRecogThread.interrupt();
        if (mRecognizeThread != null && !mRecognizeThread.isInterrupt) {
            mRecognizeThread.interrupt();
            mRecognizeThread = null;
        }
        if (mFeedFrameThread != null && !mFeedFrameThread.isInterrupt) {
            mFeedFrameThread.interrupt();
            mFeedFrameThread = null;
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
        if (mIRCameraManager != null) {
            mIRCameraManager.release();
        }
        SinglechipClientProxy.getInstance().setFingerState(0x00);
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        SinglechipClientProxy.getInstance().IRLedCtrl(false);
        mRecoLastTrackId = 0;
        Log.d(TAG, "onDestroyView: ");
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
                String token = (String) msg.obj;
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
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (CustomVersion.VERSION_K3_SCREENOFF_FACE_RECOGNIZE) {
            mScreenOff = false;
            if (args != null) {
                int type = args.getInt("screen_off_type");
                if (type == 1) {
                    mScreenOff = true;
                }
            }
            LogUtils.d("========= screemOff=" + mScreenOff + "========");
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
        if (!CustomVersion.VERSION_K3_SCREENOFF_FACE_RECOGNIZE) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        } else {
            if (!mScreenOff) {
                PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
            }
        }

        flFace.setVisibility(View.VISIBLE);
        stubDel.setVisibility(View.GONE);
        tvDelHint.setVisibility(View.GONE);
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
        if (!CustomVersion.VERSION_K3_SCREENOFF_FACE_RECOGNIZE) {
            SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
        } else {
            if (!mScreenOff) {
                SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
            }
        }

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
        if (result == SUCCESS_STATE) {
            result = faceRecognizeSuccReport(faceInfo, faceModel.getSimilarity(), previewType, previewData);
        } else if (result == FAIL_STATE) {
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
                if (roomNoStr != null) {
                    if (type == FACETYPE_DEV) {
                        UserInfoDao userInfoDao = new UserInfoDao();
                        roomNoStr = userInfoDao.getRoomNoByCardNo(roomNoStr);
                    }
                }
            }
            LogUtils.e("roomStr...: " + roomNoStr);
            PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNoStr, 0);
            mMainHandler.removeMessages(MSG_RECOGNIZING);
            mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZING, 3000);
            if (BuildConfig.isEnabledFaceValid && (faceModel.getLifecycle() > VALID_LIFECYCLE_MODE)) {
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
        //mMainHandler.postDelayed(GotoMainDefaultTask.getInstance(), 2000);
        if (AppConfig.getInstance().getScreenSaver() == 1) {
            FreeObservable.getInstance().startScreenSaver();
        } else if (AppConfig.getInstance().getPowerSaving() == 1) {
            FreeObservable.getInstance().systemSleep();
        }
        ContextProxy.sendBroadcast(Const.Action.MAIN_DEFAULT);
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
        ContextProxy.sendBroadcast(Const.Action.MAIN_FACE_PROMPT);
    }

    private void onEnrollFail() {
        mState = 0;
        PlaySoundUtils.playAssetsSound(CommStorePathDef.SET_ERR_PATH);
        mMainHandler.postDelayed(GotoMainDefaultTask.getInstance(), 2000);
    }

    private void delFaceInfo(String cardNo) {
        stubDel.setVisibility(View.GONE);
        tvDelHint.setVisibility(View.VISIBLE);
        tvDelHint.setText(R.string.face_manage_del_ing);
        mFacePresenter.delFaceInfo(cardNo);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvDelHint.setText(R.string.face_manage_del_suc);
            }
        });
        mMainHandler.postDelayed(GotoMainDefaultTask.getInstance(), 2000);
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
                flFace.setVisibility(View.INVISIBLE);
                stubDel.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_cancel:
                flFace.setVisibility(View.VISIBLE);
                stubDel.setVisibility(View.GONE);
                break;
            case R.id.btn_confirm:
                delFaceInfo(mCardNo);
                break;
            case R.id.tv_toggle:
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
                    return;
                }
            }
            tvHint1.setText(R.string.face_enrollment_hint3);
        }
    }

    @Override
    public void InterVideoCallBK(byte[] data, int datalen, int width, int height, int type) {
        //如果人脸功能未启用，则人脸识别业务不往下走
        if (!AppConfig.getInstance().isFaceEnabled()) {
            return;
        }

        //如果旷视人脸初始化未完成，则人脸识别业务不往下走
        if (MegviiFace.getInstance().mFacePassHandler == null) {
            return;
        }

        //如果识别状态切换到注册状态，则人脸识别业务不往下走且要取消人脸框，等待刷卡操作
        if (mState != 1 && mState != 2) {
            faceDetectView.setFaceInfoAdapter(null);
            faceDetectView.postInvalidate();
            return;
        }

        MegviiFaceInfoAdapter faceInfoAdapter = null;
        try {
            if (mState == 1) {
                // 识别
                if (SystemSetUtils.isScreenOn()) {
                    CameraPreviewData cameraPreviewData = new CameraPreviewData(data, width, height, MegviiFace.IMAGE_ROTATION, false);
                    ComplexFrameHelper.addRgbFrame(cameraPreviewData);
//                    Log.d(TAG, "---RGB--- " + "heigh: " + cameraPreviewData.height + "  width  " + cameraPreviewData.width
//                            + "  mirror  " + cameraPreviewData.mirror + "  rotation  " + cameraPreviewData.rotation);
                }
            } else {//if (mState == 2) {
                // 注册
                final byte[] dataNV21 = Common.I420ToNV21(data, width, height);
                FacePassImage image = new FacePassImage(dataNV21, width, height, MegviiFace.IMAGE_ROTATION, FacePassImageType.NV21);
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

        if (CustomVersion.VERSION_K3_SCREENOFF_FACE_RECOGNIZE) {
            if (mScreenOff && !SystemSetUtils.isScreenOn()) {
                final byte[] dataNV21 = Common.I420ToNV21(data, width, height);
                try {
                    FacePassImage image = new FacePassImage(dataNV21, width, height, MegviiFace.IMAGE_ROTATION, FacePassImageType.NV21);
                    FacePassDetectionResult detectionResult = MegviiFace.getInstance().mFacePassHandler.feedFrame(image);
                    if (detectionResult != null && detectionResult.message.length > 0) {
                        SystemSetUtils.screenOn();
                        SinglechipClientProxy.getInstance().ctrlTouchKeyLampState(true);
                        mScreenOff = false;
                    }
                } catch (FacePassException e) {
                    e.printStackTrace();
                }
            }
        }
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
                LogUtils.d("MegviiScanFragment--->>>RecogThread: SDK_MODE=%s, mState=%s", MegviiFace.getInstance().SDK_MODE, mState);
                final FacePassDetectionResult detectionResult = faceInfoAdapter.getDetectionResult();
                final int frameType = faceInfoAdapter.getType();
                final byte[] frameData = faceInfoAdapter.getData();
                if (MegviiFace.getInstance().SDK_MODE == MegviiFace.FacePassSDKMode.MODE_ONLINE) {
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
                                        FaceMegviiModel faceModel = new FaceMegviiModel()
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
                } else {
                    if (mState == 1 && isLocalGroupExist) {
                        //识别
                        LogUtils.d("MegviiScanFragment--->>>RecogThread  detectionResult.message.length=" + detectionResult.message.length);
                        if (detectionResult.message.length != 0) {
                            FacePassRecognitionResult[] recognizeResult = FaceApi.recognizeLocalFace(detectionResult.message);
                            if (recognizeResult != null && recognizeResult.length > 0) {
                                for (FacePassRecognitionResult res : recognizeResult) {
                                    String faceToken = new String(res.faceToken);
                                    LogUtils.d("MegviiScanFragment--->>>trackId=%d, faceToken=%s, resultType=%d, searchScore=%f, searchThreshold=%f, livenessScore=%f, livenessThreshold=%f",
                                            res.trackId, faceToken, res.facePassRecognitionResultType, res.detail.searchScore, res.detail.searchThreshold, res.detail.livenessScore, res.detail.livenessThreshold);
                                    boolean isLiveness = (AppConfig.getInstance().getFaceLiveCheck() == 0)
                                            || (res.detail.livenessScore >= res.detail.livenessThreshold);
                                    if (res.detail.searchScore >= res.detail.searchThreshold && isLiveness) {
                                        //LogUtils.d("FacePass--->>>recognitionResult:faceToken=%s, trackId=%d", faceToken, res.trackId);
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
                                    strangerSanp(res.trackId, res.facePassRecognitionResultType,
                                            frameData, faceInfoAdapter.getWidth(), faceInfoAdapter.getHeight());
                                }
                            } else {
                                /* 未注册过人脸情况 */
                                if (mSnapStrangerUse == 1 && detectionResult.faceList != null) {
                                    for (FacePassFace pass : detectionResult.faceList) {
                                        strangerSanp(pass.trackId, 4, frameData,
                                                faceInfoAdapter.getWidth(), faceInfoAdapter.getHeight());
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
     * 检测线程
     */
    private class FeedFrameThread extends Thread {
        boolean isInterrupt = false;
        MegviiFaceInfoAdapter faceInfoAdapter = null;

        @Override
        public void run() {
            while (!isInterrupt) {
                Pair<CameraPreviewData, CameraPreviewData> framePair;
                try {
                    framePair = ComplexFrameHelper.takeComplexFrame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(TAG, "takeComplexFrame null ");
                    continue;
                }
                /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
                FacePassImage image;
                try {
                    final byte[] dataNV21 = Common.I420ToNV21(framePair.first.nv21Data,
                            framePair.first.width, framePair.first.height);
                    image = new FacePassImage(dataNV21, framePair.first.width,
                            framePair.first.height, framePair.first.rotation, FacePassImageType.NV21);
                } catch (FacePassException e) {
                    e.printStackTrace();
                    continue;
                }
                /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                FacePassDetectionResult detectionResult = null;
                try {
                    detectionResult = MegviiFace.getInstance().mFacePassHandler.feedFrame(image);
                } catch (FacePassException e) {
                    e.printStackTrace();
                }
//                Log.d(TAG, "run: mFacePassHandler.feedFrame");
                /* 将识别到的人脸在预览界面中圈出，并在上方显示人脸位置及角度信息 */
                faceInfoAdapter = new MegviiFaceInfoAdapter()
                        .setType(0)
                        .setData(framePair.first.nv21Data)
                        .setWidth(framePair.first.width)
                        .setHeight(framePair.first.height)
                        .setMirror(true)
                        .setDetectionResult(detectionResult);
                faceDetectView.setFaceInfoAdapter(faceInfoAdapter);
                faceDetectView.postInvalidate();

                if (detectionResult != null && detectionResult.message.length != 0) {
                    Log.d(TAG, "detectionResult != null");
                    FreeObservable.getInstance().resetFreeTime();
                    try {
                        FacePassImage irImage = new FacePassImage(framePair.second.nv21Data,
                                framePair.second.width, framePair.second.height, 0, FacePassImageType.NV21);
                        detectionResult = MegviiFace.getInstance().mFacePassHandler.IRfilter(irImage, detectionResult);
                        if (detectionResult.message.length == 0) {
                            for (FacePassFace face : detectionResult.faceList) {
                                MegviiFace.getInstance().mFacePassHandler.decodeResponseVirtual(face.trackId);
                                MegviiFace.getInstance().mFacePassHandler.resetMessage(face.trackId);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                if (detectionResult != null && detectionResult.message.length != 0) {
                    faceInfoAdapter = new MegviiFaceInfoAdapter()
                            .setType(0)
                            .setData(framePair.first.nv21Data)
                            .setWidth(framePair.first.width)
                            .setHeight(framePair.first.height)
                            .setMirror(true)
                            .setDetectionResult(detectionResult);
                    mDetectResultQueue.offer(faceInfoAdapter);
                    Log.d(TAG, "mDetectResultQueue.offer");
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
        }
    }

    private class RecognizeThread extends Thread {
        boolean isInterrupt;
        MegviiFaceInfoAdapter faceInfoAdapter;

        @Override
        public void run() {
            while (!isInterrupt) {
                try {
                    faceInfoAdapter = mDetectResultQueue.take();
                    final FacePassDetectionResult detectionResult = faceInfoAdapter.getDetectionResult();
                    final int frameType = faceInfoAdapter.getType();
                    final byte[] frameData = faceInfoAdapter.getData();

                    Log.d(TAG, "RecognizeThread  mState:  " + mState + " isLocalGroupExist: " + isLocalGroupExist);
                    if (mState == 1 && isLocalGroupExist) {
                        FacePassRecognitionResult[] recognitionResults;
                        recognitionResults = MegviiFace.getInstance().
                                mFacePassHandler.recognize(GROUP_NAME, detectionResult.message);
                        Log.d(TAG, "recognitionResults!");
                        if (recognitionResults != null && recognitionResults.length > 0) {
                            for (FacePassRecognitionResult resut : recognitionResults) {
                                String faceToken = new String(resut.faceToken);
                                Log.d("lfl", "ID: " + resut.trackId + "  识别分：" +
                                        resut.detail.searchScore + "   活体分： " + resut.detail.livenessScore + "  识别阈值： " + resut.detail.searchThreshold
                                        + " 活体阈值" + resut.detail.livenessThreshold);
                                boolean isLiveness = (AppConfig.getInstance().getFaceLiveCheck() == 0)
                                        || (resut.detail.livenessScore >= resut.detail.livenessThreshold);
                                if (resut.detail.searchScore >= resut.detail.searchThreshold && isLiveness) {
                                    //LogUtils.d("FacePass--->>>recognitionResult:faceToken=%s, trackId=%d", faceToken, res.trackId);
                                    FaceMegviiModel faceModel = mFacePresenter.verifyFaceId(faceToken);
                                    if (faceModel != null && resut.trackId != mRecoLastTrackId) {
                                        mRecoLastTrackId = resut.trackId;
                                        faceModel.setSimilarity(resut.detail.searchScore);
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
                                SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
                                /* 陌生人抓拍 */
                                strangerSanp(resut.trackId, resut.facePassRecognitionResultType,
                                        frameData, faceInfoAdapter.getWidth(), faceInfoAdapter.getHeight());
                            }
                        } else {
                            Log.d(TAG, "人脸未注册!");
                            if (detectionResult.faceList != null) {
                                SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
                                /* 陌生人抓拍 */
                                if (mSnapStrangerUse == 1) {
                                    for (FacePassFace pass : detectionResult.faceList) {
                                        strangerSanp(pass.trackId, 4, frameData,
                                                faceInfoAdapter.getWidth(), faceInfoAdapter.getHeight());
                                    }
                                }
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException");
                    e.printStackTrace();
                } catch (FacePassException e) {
                    Log.e(TAG, "FacePassException!");
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
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
    public boolean onFreeReport(long freeTime) {
        if (mState == 1 && freeTime > Const.Config.FACE_RECOGNIZE_TIMEOUT) {
            mState = 0;
            mMainHandler.sendEmptyMessage(MSG_RECOG_TIMEOUT);
        }
        return true;
    }

    /**
     * 陌生人抓拍
     *
     * @param trackId    trackId
     * @param resultType 识别结果
     * @param frameData  一帧视频数据
     * @param width      宽
     * @param height     高
     */
    private void strangerSanp(long trackId, int resultType, byte[] frameData, int width, int height) {
//        LogUtils.d("=======[MegviiScanFragment] [strangerSanp] trackId=%d, type=%d, photolen=%d mRecognizeTimes=%d mLastTrackId=%d",
//                trackId, resultType, frameData.length, mRecognizeTimes, mLastTrackId);

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
//            LogUtils.d("=======[MegviiScanFragment] [strangerSanp] ok ");
        }
    }

    /**
     * 保存图片文件
     *
     * @param data   图片数据
     * @param width  宽
     * @param height 高
     * @return 图片路径
     */
    private String savePhoto(byte[] data, int width, int height) {
        final byte[] dataNV21 = Common.I420ToNV21(data, width, height);
        String filename = CommStorePathDef.SNAP_DIR_PATH + "/strangerFace.jpg";
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
