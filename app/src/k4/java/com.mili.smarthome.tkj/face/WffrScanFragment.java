package com.mili.smarthome.tkj.face;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.android.InterCommTypeDef;
import com.android.client.MainClient;
import com.android.interf.ICardReaderListener;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.WffrFacePresenterImpl;
import com.mili.smarthome.tkj.base.K4Config;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.entities.FaceWffrModel;
import com.mili.smarthome.tkj.face.wffr.WffrFaceInfoAdapter;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.view.HintView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.wf.wffrapp;
import com.wf.wffrjni;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.android.CommTypeDef.FaceAddType.FACETYPE_DEV;
import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;


public class WffrScanFragment extends BaseFaceFragment implements View.OnClickListener, ICardReaderListener,
        FreeObservable.FreeObserver, InterCommTypeDef.IFaceRecognizeListener {

    private SurfaceView svReceive;
    private TextureView tvPreview;
    private FaceDetectView faceDetectView;

    private View flFace;
    private View flAddSuc;
    private HintView hvHint;
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

    private FacePresenter<FaceWffrModel> mFacePresenter = new WffrFacePresenterImpl();

    /** <p>1识别 <p>2注册 <p>3删除 <p>4请刷卡 <p>5请选择 <p>6注册成功 */
    private int mState = 1;
    private String mCardNo;
    private String mEnrollName;
    private int mResult;
    private String mRecogFaceId;//识别到的人脸ID

    private boolean mFullScreen = false;
    private FuncCodeListener mListener;

    /* 陌生人脸抓拍定制 */
    private int mRecognizeTimes = 0;
    private boolean mSnapFlag = false;
    private boolean mSnapThreadRun = false;
    private int mNoFaceTimes = 0;
    private int mSnapStrangerUse = 0;

    /* 是否正在进行人脸识别 */
    private boolean mFaceRecognize = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_face_wffr;
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
        tvHint1 = findView(R.id.tv_hint1);
        tvHint2 = findView(R.id.tv_hint2);
        tvHint3 = findView(R.id.tv_hint3);
        llOperHint = findView(R.id.ll_oper_hint);
        tvHint4 = findView(R.id.tv_hint4);
        tvHint5 = findView(R.id.tv_hint5);

//        tvPreview.setOnClickListener(this);
//        tvPreview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                LogUtils.d("AAA: width=" + tvPreview.getWidth() + ", height=" + tvPreview.getHeight());
//                LogUtils.d("BBB: width=" + svReceive.getWidth() + ", height=" + svReceive.getHeight());
//            }
//        });

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
        mCardNo = "";
        SinglechipClientProxy.getInstance().setFingerState(0x01);
        SinglechipClientProxy.getInstance().setCardReaderListener(this);

        faceDetectView.setRecognitionThreshold(wffrjni.GetRecognitionThreshold());
        setRecognition();
        startPreview(svReceive, tvPreview);

        if (K4Config.getInstance().getAliyunEdge()) {
            mFaceRecognize = false;
            MainClient.getInstance().setFaceRecognizeListener(this);
        }

        setKeyboardMode(KeyboardCtrl.KEYMODE_EDIT);
        showView(true);
        showRadioChecked(MainActivity.FUNCTION_FACE);

        /* 人脸识别计数，10后若未识别则抓拍 */
        mSnapStrangerUse = SnapParamDao.getFaceStrangerSnap();
        if (mSnapStrangerUse == 1) {
            mSnapThreadRun = true;
            new SnapThread().start();
        }
        Log.d("WffrScanFragment", " ===== onViewCreated ===== ");
    }

    @Override
    public void onResume() {
        super.onResume();
        FreeObservable.getInstance().addObserver(this);
        // 解决在onViewCreated中设置有时候无效问题
        showRadioChecked(MainActivity.FUNCTION_FACE);
    }

    @Override
    public void onPause() {
        FreeObservable.getInstance().removeObserver(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        LogUtils.d(" onDestroyView ");
        setKeyboardText("");
        stopPreview();
        wffrapp.stopExecution();
        mRecogFaceId = "";
        mMainHandler.removeCallbacksAndMessages(null);
        SinglechipClientProxy.getInstance().setFingerState(0x00);
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        if (K4Config.getInstance().getAliyunEdge()) {
            MainClient.getInstance().setFaceRecognizeListener(null);
        }
        mSnapThreadRun = false;
        super.onDestroyView();
    }

    private void showView(boolean isFull) {
        if (isFull) {
            mLlBtnGroup.setVisibility(View.VISIBLE);
            mIvZoom.setVisibility(View.VISIBLE);
            mIvToggle.setVisibility(isEnabledRtsp() ? View.VISIBLE : View.INVISIBLE);
            mLlHint.setVisibility(View.INVISIBLE);

            ViewGroup.LayoutParams params = mLlBtnGroup.getLayoutParams();
            params.height = getResources().getDimensionPixelOffset(R.dimen.dp_90);
            mLlBtnGroup.setLayoutParams(params);

            if (mListener != null) {
                mListener.onFuncCode(MainActivity.FUNCTION_FACE, 1);
            }
        } else {
            mLlBtnGroup.setVisibility(View.GONE);
            mIvZoom.setVisibility(View.INVISIBLE);
            mIvToggle.setVisibility(View.INVISIBLE);
            mLlHint.setVisibility(View.VISIBLE);

            if (mListener != null) {
                mListener.onFuncCode(MainActivity.FUNCTION_FACE, 0);
            }
        }
        mFullScreen = isFull;
    }

    private void showRadioChecked(int funcCode) {
        LogUtils.d("WffrScanFragment showRadioChecked: funcCode is " + funcCode);
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

    public void setFuncCodeListener(FuncCodeListener listener) {
        mListener = listener;
    }

    @Override
    public void InterVideoCallBK(byte[] data, int datalen, int width, int height, final int type) {
        try {
//            YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
//            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/out.jpg");
//            FileOutputStream filecon = new FileOutputStream(file);
//            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, filecon);

            FaceInfoAdapter faceInfoAdapter = null;
            if (mState == 1 || mState == 2) {
                final byte[] previewData = new byte[datalen];
                System.arraycopy(data, 0, previewData, 0, datalen);
                wffrapp.startExecution(previewData, width, height, mEnrollName);
                List<FaceInfo> faceList = wffrapp.getFaceParseResult();

                if (faceList != null && faceList.size() > 0) {
                    SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
                    FreeObservable.getInstance().resetFreeTime();

                    for (FaceInfo faceInfo : faceList) {
                        if (mState == 2) {
                            if (faceInfo.getSimilar() == -1) {
                                continue;
                            }
                            mResult++;
                        } else if (faceInfo.getSimilar() > wffrjni.GetRecognitionThreshold()) {
                            final FaceWffrModel faceModel = mFacePresenter.verifyFaceId(faceInfo.getFaceId());
                            if (faceModel != null) {
                                mRecognizeTimes = 0;
                                mSnapFlag = false;
                                faceModel.setConfidence(faceInfo.getSimilar());
                                mResult++;
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onRecognizeSuc(faceModel, type, previewData);
                                    }
                                });
                            } else {
                                faceInfo.setSimilar(0f);
                            }
                        }
                    }
                    faceInfoAdapter = new WffrFaceInfoAdapter()
                            .setData(previewData)
                            .setWidth(width)
                            .setHeight(height)
                            .setMirror(type == 0)
                            .setFaceList(faceList);

                    /* 陌生人脸抓拍 */
                    if (mSnapStrangerUse == 1 && mState == 1) {
                        strangerSanp(previewData, width, height);
                    }
                }

                /* 陌生人脸抓拍 */
                if (mSnapStrangerUse == 1 && mState == 1) {
                    if (faceInfoAdapter == null) {
                        mNoFaceTimes++;
                        if (mNoFaceTimes > 20) {
                            mNoFaceTimes = 0;
                            mRecognizeTimes = 0;
                            mSnapFlag = false;
                            LogUtils.d(" [WffrScanFragment >>> InterVideoCallBK] faceInfoAdapter is null.");
                        }
                    } else {
                        mNoFaceTimes = 0;
                    }
                }
            }
            if (type == getPreviewType()) {
                drawOutput(faceInfoAdapter);

                /* 与边缘网关对接实现人脸识别功能 */
                if (K4Config.getInstance().getAliyunEdge()) {
                    if (faceInfoAdapter != null) {
                        detectFace(data, width, height);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private void drawOutput(final FaceInfoAdapter faceInfoAdapter) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                faceDetectView.setFaceInfoAdapter(faceInfoAdapter);
                faceDetectView.setEnrolling(mState == 2);
                faceDetectView.invalidate();
            }
        });
    }

    private final int MSG_RECOGNIZING = 0x10;
    private final int MSG_RECOG_FACEID_TIMEOUT = 0x15;
    private final int MSG_END_RECOGNIZE = 0x1F;
    private final int MSG_END_ENROLL = 0x2F;
    private final int MSG_EDIT_TIMEOUT = 0x3F;
    private final int MSG_EXIT = 0xFF;
    private final int MSG_RECOGNIZE_TIMEOUT = 0x5F;

    @Override
    protected void handleMessage(Message msg) {
        LogUtils.d(" handleMessage: what is " + msg.what + ", result is " + mResult);
        switch (msg.what) {
            case MSG_RECOGNIZING:
                tvHint1.setText(R.string.face_scan_hint4);
                tvHint2.setText(R.string.face_scan_hint1);
                tvHint3.setText("");
                llOperHint.setVisibility(View.INVISIBLE);
                break;
            case MSG_RECOG_FACEID_TIMEOUT:
                mRecogFaceId = "";
                break;
            case MSG_END_RECOGNIZE:
                onRecognizeTimeout();
                if (mListener != null) {
                    mListener.onFuncCode(0xFF, 0);
                }
                break;
            case MSG_END_ENROLL:
                if (mResult > 0) {
                    onEnrollSuc();
                } else {
                    onEnrollFail();
                }
                break;
            case MSG_EDIT_TIMEOUT:
                setRecognition();
                break;
            case MSG_EXIT:
                backToMain();
                break;

            case MSG_RECOGNIZE_TIMEOUT:
                mFaceRecognize = false;
                Log.d("WffrScanFragment", " face recognize timeout ");
                break;
        }
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
                mCardNo = WffrFacePresenterImpl.cardIdToString(cardId);
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
            mState = 4;
            wffrapp.stopExecution();
            SinglechipClientProxy.getInstance().setCardState(0x02);
            SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
            tvHint1.setText(R.string.face_scan_hint4);
            tvHint2.setText(R.string.face_scan_hint1);
            tvHint3.setText(R.string.face_manage_hint1);
            mMainHandler.sendEmptyMessageDelayed(MSG_EDIT_TIMEOUT, 20000);
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
        LogUtils.d(" ===========  setEdit  ===========");
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
        LogUtils.d(" ===========  setRecognition  ===========");
        if (AppConfig.getInstance().getFaceLiveCheck() == 0) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_PATH);
        }
        flFace.setVisibility(View.VISIBLE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.GONE);
        tvHint1.setText(R.string.face_scan_hint4);
        tvHint2.setText(R.string.face_scan_hint1);
        tvHint3.setText("");
        llOperHint.setVisibility(View.INVISIBLE);
        mResult = 0;
        mEnrollName = "";
        mRecogFaceId = "";
        mState = 1;
        wffrapp.setState(wffrapp.RECOGNITION);
        mMainHandler.removeMessages(MSG_END_ENROLL);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
        setKeyboardMode(KeyboardCtrl.KEYMODE_EDIT);

        /* 陌生人脸抓拍 */
        if (mSnapStrangerUse == 1) {
            mSnapFlag = false;
            mRecognizeTimes = 0;
            mNoFaceTimes = 0;
            LogUtils.d(" [WffrScanFragment >>> setRecognition] ");
        }
    }

    /** 进入录入状态 */
    private void setEnrollment() {
        LogUtils.d(" ===========  setEnrollment  ===========");
        if (AppConfig.getInstance().getFaceLiveCheck() == 0) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_PATH);
        }
        flFace.setVisibility(View.VISIBLE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.GONE);
        tvHint1.setText(R.string.face_enrollment_hint2);
        tvHint2.setText(R.string.face_scan_hint1);
        tvHint3.setText("");
        llOperHint.setVisibility(View.INVISIBLE);
        mState = 2;
        mResult = 0;
        mEnrollName = BaseFacePresenter.genResidentFaceId(mCardNo);
        wffrapp.setState(wffrapp.ENROLLMENT);
        mMainHandler.removeMessages(MSG_EDIT_TIMEOUT);
        mMainHandler.sendEmptyMessageDelayed(MSG_END_ENROLL, Const.Config.FACE_ENROLL_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
    }

    /**
     * 人脸识别成功
     * @param faceModel 人脸信息
     */
    private void onRecognizeSuc(FaceWffrModel faceModel, int previewType, byte[] previewData) {
        LogUtils.d(" ===========  onRecognizeSuc  ===========");
        if (faceModel.getFirstName().equals(mRecogFaceId)) {
            LogUtils.d(" the same face ID: " + mRecogFaceId);
            return;
        }
        FaceProtocolInfo faceInfo = WffrFacePresenterImpl.convert(faceModel);
        int result = Common.validity(faceInfo);
        if (result == SUCCESS_STATE){
            result = faceRecognizeSuccReport(faceInfo, faceModel.getConfidence(), previewType, previewData);
        }
        else if (result == FAIL_STATE){
            result = 0;
        }
        if (result == 1) {
            String roomNoStr = null;
            mRecogFaceId = faceModel.getFirstName();
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
            mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZING, 4000);
            mMainHandler.removeMessages(MSG_RECOG_FACEID_TIMEOUT);
            mMainHandler.sendEmptyMessageDelayed(MSG_RECOG_FACEID_TIMEOUT, 5000);
            if (BuildConfig.isEnabledFaceValid && (faceModel.getLifecycle() > VALID_LIFECYCLE_MODE)){
                mFacePresenter.subLifecycleInfo(faceModel.getFirstName());
            }
        } else if (result == -1) {
            mFacePresenter.delFaceInfoById(faceModel.getFirstName());
        }
    }

    private void onRecognizeTimeout() {
        LogUtils.d(" ===========  onRecognizeTimeout  ===========");
//        tvHint1.setText(R.string.face_recognition_fail);
        backToMain();
//        if (AppConfig.getInstance().getScreenSaver() == 1) {
//            FreeObservable.getInstance().startScreenSaver();
//        } else if (AppConfig.getInstance().getPowerSaving() == 1) {
//            FreeObservable.getInstance().systemSleep();
//        }
    }

    private void onEnrollSuc() {
        LogUtils.d(" ===========  onEnrollSuc  ===========");
        mState = 6;
        // 添加人脸信息
        FaceWffrModel faceInfoModel = new FaceWffrModel();
        faceInfoModel.setFirstName(mEnrollName);
        faceInfoModel.setCardNo(mCardNo);
        mFacePresenter.addFaceInfo(faceInfoModel);
        flFace.setVisibility(View.GONE);
        flAddSuc.setVisibility(View.VISIBLE);
        hvHint.setVisibility(View.GONE);
        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 10000);
    }

    private void onEnrollFail() {
        LogUtils.d(" ===========  onEnrollFail  ===========");
        PlaySoundUtils.playAssetsSound(CommStorePathDef.SET_ERR_PATH);
        tvHint1.setText(R.string.face_enrollment_hint2);
        tvHint2.setText(R.string.face_scan_hint1);
        tvHint3.setText("");
        llOperHint.setVisibility(View.GONE);
        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
    }

    private void delFaceInfo(String cardNo) {
        mFacePresenter.delFaceInfo(cardNo);
        flFace.setVisibility(View.GONE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.VISIBLE);
        hvHint.setHint(R.string.set_success, R.color.txt_white);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setEdit();
            }
        }, 2000);
    }

    private void gotoFuction(int funcCode) {
        LogUtils.d(" gotoFuction: funcCode is " + funcCode);
//        stopPreview();
//        wffrapp.stopExecution();

        showRadioChecked(funcCode);
        if (mListener != null) {
            mListener.onFuncCode(funcCode, 0);
        }
        mLlBtnGroup.setVisibility(View.GONE);
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
                LogUtils.d(" WffrScanFragment: mState is " + mState + ", mFullScreen is " + mFullScreen);
                if (mState == 1 && !mFullScreen) {  //人脸识别状态下方可全屏
                    showView(true);
                    showRadioChecked(MainActivity.FUNCTION_FACE);
                }
                break;
        }
    }

    /** 退回主界面时需更改fragment尺寸 */
    private void backToMain() {
        LogUtils.d(" =============  backToMain  ============== ");
        SinglechipClientProxy.getInstance().disableBodyInduction(1, 3000);
        requestBack();
        if (mListener != null) {
            mListener.onFuncCode(MainActivity.FUNCTION_MAIN, 0);
        }
        mLlBtnGroup.setVisibility(View.GONE);
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (mState == 1 && freeTime > Const.Config.FACE_RECOGNIZE_TIMEOUT) {
            mState = 0;
            mMainHandler.sendEmptyMessage(MSG_END_RECOGNIZE);
        }
        return true;
    }

    /**
     * 人脸识别
     * @param data      人脸yuv数据
     * @param width     宽
     * @param height    高
     */
    private void detectFace(byte[] data, int width, int height) {

        /* 正在人脸识别时不处理 */
        if (mFaceRecognize) {
            return;
        }
        mFaceRecognize = true;

        Log.d("WffrScanFragment", "[saveYuvData] datalen is " + data.length + ", width is " + width + ", height is " + height);

        /* 将yuv数据转为nv21数据，然后存储为jpg图片 */
        byte[] nv21Data = Common.I420ToNV21(data, width, height);
        YuvImage image = new YuvImage(nv21Data, ImageFormat.NV21, width, height, null);
        File file = new File(CommStorePathDef.USERDATA_PATH+ "/facePhoto/faceRecognize.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String photoName = file.getPath();
        Log.d("WffrScanFragment", "[saveYuvData] photoName is " + photoName);

        /* 识别人脸 */
        if (photoName.length() > 0) {
            MainClient.getInstance().Main_FaceRecognize(photoName);
            mMainHandler.removeMessages(MSG_RECOGNIZE_TIMEOUT);
            mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZE_TIMEOUT, 5000);
        }
    }

    /*人脸识别结果反馈*/
    @Override
    public void FaceRecognizeResult(int state) {
        Log.d("WffrScanFragment", " FaceRecognizeResult state is " + state + ", mFaceRecognize is " + mFaceRecognize);
        if (mFaceRecognize) {
            if (state == 0) {
                flFace.setVisibility(View.VISIBLE);
                flAddSuc.setVisibility(View.GONE);
                hvHint.setVisibility(View.GONE);
                tvHint1.setText(R.string.face_recognition_suc2);
                tvHint2.setText(R.string.face_recognition_suc1);
                tvHint3.setText("");
                llOperHint.setVisibility(View.INVISIBLE);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH);
                mMainHandler.removeMessages(MSG_RECOGNIZING);
                mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZING, 4000);
            }
            else
            {
                /* 人脸识别失败时，延迟一秒再重新识别 */
                mMainHandler.removeMessages(MSG_RECOGNIZE_TIMEOUT);
                mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZE_TIMEOUT, 1000);
            }
        }
    }

    /**
     * 陌生人抓拍
     * @param frameData 一帧视频数据
     * @param width     宽
     * @param height    高
     */
    private void strangerSanp(byte[] frameData, int width, int height) {
        LogUtils.d("=======[WffrScanFragment] [strangerSanp] photolen=%d mRecognizeTimes=%d mSnapFlag=%b mNoFaceTimes=%d",
                frameData.length, mRecognizeTimes, mSnapFlag, mNoFaceTimes);

        //不启用陌生人脸抓拍
        if (mSnapStrangerUse == 0) {
            return;
        }

        if (mSnapFlag) {
            return;
        }

        /* 保存图片文件并进行上传服务器 */
        if (mRecognizeTimes > 10) {
            mSnapFlag = true;
            mRecognizeTimes = 0;
            String photoPath = savePhoto(frameData, width, height);
            if (photoPath != null) {
                MainClient.getInstance().Main_FaceSnapStranger(photoPath);
            }
            LogUtils.d("=======[WffrScanFragment] [strangerSanp] ok ");
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
            LogUtils.d("[WffrScanFragment][savePhoto] photoName is " + filename);
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
