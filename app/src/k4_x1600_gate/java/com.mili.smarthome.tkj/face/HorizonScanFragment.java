package com.mili.smarthome.tkj.face;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.client.SetDriverSinglechipClient;
import com.android.interf.IBodyInductionListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.face.horizon.FaceRecogResultCache;
import com.mili.smarthome.tkj.face.horizon.HorizonConst;
import com.mili.smarthome.tkj.face.horizon.HorizonFacePresenter;
import com.mili.smarthome.tkj.face.horizon.IFaceRecogView;
import com.mili.smarthome.tkj.face.horizon.ReceiveFaceResult;
import com.mili.smarthome.tkj.face.horizon.bean.FaceRecogResult;
import com.mili.smarthome.tkj.face.horizon.realm.HorizonFaceDao;
import com.mili.smarthome.tkj.face.horizon.realm.HorizonFaceModel;
import com.mili.smarthome.tkj.face.horizon.util.CameraDisplayUtil;
import com.mili.smarthome.tkj.face.horizon.util.HorizonPreferences;
import com.mili.smarthome.tkj.face.horizon.util.LiveNessUtil;
import com.mili.smarthome.tkj.face.horizon.util.SunriseSdkUtil;
import com.mili.smarthome.tkj.face.horizon.util.XWareHouseUtil;
import com.mili.smarthome.tkj.face.horizon.view.CameraOverlay;
import com.mili.smarthome.tkj.face.horizon.view.HRXMIPICam;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.view.HintView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.activity.SettingActivity;
import com.mili.smarthome.tkj.utils.FileUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hobot.sunrise.sdk.jni.FaceModuleResult;
import hobot.xwaremodule.sdk.jni.HobotXWMListSetResult;

public class HorizonScanFragment extends K4BaseFragment implements View.OnClickListener,
        IFaceRecogView, FreeObservable.FreeObserver, IBodyInductionListener {

    private HRXMIPICam mCameraView;
    private CameraOverlay mCameraOverlay;
    private View flFace;
    private View flAddSuc;
    private HintView hvHint;
    private TextView tvHint1;
    private TextView tvHint2;
    private TextView tvHint3;
    private View llOperHint;
    private TextView tvHint4;
    private TextView tvHint5;
    private TextView tvHint6;
    private ImageView mIvZoom;
    private ImageView ivAuth;

    /** <p>1识别 <p>2注册 <p>3删除 <p>4请输入管理密码 <p>5请选择 <p>6注册成功 */
    private int mState = 1;
    private String mCardNo;
    private String mEnrollName;
    private int mResult;
    private ReceiveFaceResult mReceiveFaceResult;
    private String mRecogFaceId;//识别到的人脸ID
    private StringBuilder mInputPwd = new StringBuilder();

    private ExecutorService mExecutor;
    private FacePresenter<HorizonFaceModel> mFacePresenter;
    private FuncCodeListener mListener;
    private boolean mFullScreen = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_face_horizon;
    }

    @Override
    protected void bindView() {
        super.bindView();
        ivAuth = findView(R.id.iv_auth);
        flFace = findView(R.id.fl_content);
        flAddSuc = findView(R.id.fl_add_suc);
        hvHint = findView(R.id.hv_hint);

        mCameraView = findView(R.id.hrx_camera_view);
        mCameraView.setOnClickListener(this);

        mCameraOverlay = findView(R.id.camera_overlay);
        mCameraOverlay.setDrawingCacheEnabled(true);
        mCameraOverlay.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        tvHint1 = findView(R.id.tv_hint1);
        tvHint2 = findView(R.id.tv_hint2);
        tvHint3 = findView(R.id.tv_hint3);
        llOperHint = findView(R.id.ll_oper_hint);
        tvHint4 = findView(R.id.tv_hint4);
        tvHint5 = findView(R.id.tv_hint5);
        tvHint6 = findView(R.id.tv_hint6);

        mIvZoom = findView(R.id.iv_zomm);
        assert mIvZoom != null;
        mIvZoom.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        super.bindData();
        //是否授权
        if (AuthManage.isAuth()) {
            ivAuth.setVisibility(View.GONE);
        }else {
            ivAuth.setVisibility(View.VISIBLE);
            ivAuth.setOnClickListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFacePresenter = new HorizonFacePresenter();
        mCardNo = "000000";
        mReceiveFaceResult = new ReceiveFaceResult(mCameraView);
        SunriseSdkUtil.setFaceResultListener(mReceiveFaceResult);
        SinglechipClientProxy.getInstance().setFingerState(0x01);

        setKeyboardMode(KeyboardCtrl.KEYMODE_EDIT);
        showView(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        createFaceSet();
        FreeObservable.getInstance().addObserver(this);
        SinglechipClientProxy.getInstance().setBodyInductionListener(this);
        startCamera();
        setRecognition();
    }

    @Override
    public void onPause() {
        FreeObservable.getInstance().removeObserver(this);
        stopCamera();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        SunriseSdkUtil.setFaceResultListener(null);
        mRecogFaceId = "";
        mReceiveFaceResult.stopReceiveFaceResult();
        mReceiveFaceResult.recyclingResources();
        mReceiveFaceResult = null;
        FaceRecogResultCache.clear();
        setKeyboardText("");
        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
        mMainHandler.removeCallbacksAndMessages(null);
        SinglechipClientProxy.getInstance().setFingerState(0x00);
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTryToOpen = false;
        if (Camerathread != null) {
            try {
                Camerathread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (CameraStarted) {
            mCameraView.stopCamera();
            boolean livenessCheck = AppConfig.getInstance().getFaceLiveCheck() == 1;
            if (livenessCheck) {
                LiveNessUtil.stop();
            }
            CameraStarted = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_auth:
                //授权
                AuthManage.startAuth(mContext);
                break;
            case R.id.iv_zomm:
                showView(false);
                break;
            case R.id.hrx_camera_view:
                if (mState == 1 && !mFullScreen) {  //人脸识别状态下方可全屏
                    showView(true);
                }
                break;
        }
    }

    private void showView(boolean isFull) {
        if (isFull) {
            mIvZoom.setVisibility(View.VISIBLE);

            if (mListener != null) {
                mListener.onFuncCode(MainActivity.FUNCTION_FACE, 1);
            }
        } else {
            mIvZoom.setVisibility(View.INVISIBLE);

            if (mListener != null) {
                mListener.onFuncCode(MainActivity.FUNCTION_FACE, 0);
            }
        }
        mFullScreen = isFull;
    }

    public void setFuncCodeListener(FuncCodeListener listener) {
        mListener = listener;
    }

    /** 退回主界面时需更改fragment尺寸 */
    private void backToMain() {
//        requestBack();
//        if (mListener != null) {
//            mListener.onFuncCode(MainActivity.FUNCTION_MAIN, 0);
//        }
        LogUtils.d("--->>>system sleep!!!");
        mState = 0;
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        FreeObservable.getInstance().systemSleep();
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (mState == 1 && freeTime >= Const.Config.FACE_RECOGNIZE_TIMEOUT) {
            backToMain();
        }
        return true;
    }

    @Override
    public void onBodyInduction() {
        if (mState == 0) {
            FreeObservable.getInstance().observeFree();
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setRecognition();
                }
            });
        }
    }

    private final int MSG_RECOGNIZING = 0x10;
    private final int MSG_RECOGNIZE_TIMEOUT = 0x11;
    private final int MSG_RECOG_FACEID_TIMEOUT = 0x15;
    private final int MSG_ENROLL_TIMEOUT = 0x2F;
    private final int MSG_EDIT_TIMEOUT = 0x3F;
    private final int MSG_EXIT = 0xFF;

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECOGNIZING:
                tvHint1.setText(R.string.face_scan_hint4);
                tvHint2.setText(R.string.face_scan_hint1);
                tvHint3.setText("");
                tvHint3.setVisibility(View.VISIBLE);
                llOperHint.setVisibility(View.GONE);
                break;
            case MSG_RECOG_FACEID_TIMEOUT:
                mRecogFaceId = "";
                break;
            case MSG_RECOGNIZE_TIMEOUT:
                if (mResult > 0) {
                    backToMain();
                } else {
                    onRecognizeTimeout();
                }
                break;
            case MSG_ENROLL_TIMEOUT:
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
    public boolean onKey(int code) {
        super.onKey(code);
        LogUtils.d("%s--->>>onKey: code=%d, mState=%d", HorizonScanFragment.class.getSimpleName(), code, mState);
        if (mState == 4) {
            mInputPwd.append(code);
            if (mInputPwd.length() == 1) {
                tvHint3.setText("*");
            } else {
                tvHint3.append("*");
            }
            if (mInputPwd.length() == 8) {
                if (mInputPwd.toString().equals(ParamDao.getAdminPwd())) {
                    setEdit();
                } else {
                    tvHint3.setText("");
                }
                mInputPwd.delete(0, mInputPwd.length());
            }
        } else if (mState == 5) {
            if (code == 1) {
                setEnrollment();
            } else if (code == 2) {
                mState = 3;
                tvHint1.setText(R.string.face_scan_hint4);
                tvHint2.setText(R.string.face_manage_del_confirm);
                tvHint3.setText(R.string.face_manage_hint2);
                tvHint3.setVisibility(View.GONE);
                llOperHint.setVisibility(View.VISIBLE);
                tvHint4.setText(R.string.face_back_hint);
                tvHint4.setCompoundDrawables(null, null, getDrawable(R.drawable.set_cancel_icon), null);
                tvHint5.setText("");
                tvHint5.setCompoundDrawables(null, null, null, null);
                tvHint6.setText(R.string.set_hint2);
                tvHint6.setCompoundDrawables(null, null, getDrawable(R.drawable.set_sure_icon), null);
            } else if (code == 3) {
                mMainHandler.removeCallbacksAndMessages(null);
                Intent intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
            }
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        LogUtils.d("%s--->>>onKeyConfirm: mState=%d", HorizonScanFragment.class.getSimpleName(), mState);
        if (mState == 1) {
            mState = 4;
            mReceiveFaceResult.stopReceiveFaceResult();
            updateOverlay(null, 0);
            mInputPwd.delete(0, mInputPwd.length());
            SinglechipClientProxy.getInstance().setCardState(0x02);
            SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
            tvHint1.setText(R.string.face_scan_hint4);
            tvHint2.setText(R.string.face_manage_hint11);
            tvHint3.setText("");
            mMainHandler.removeMessages(MSG_RECOGNIZING);
            mMainHandler.sendEmptyMessageDelayed(MSG_EDIT_TIMEOUT, 20000);
        } else if (mState == 3) {
            delFaceInfo(mCardNo);
        } else if (mState == 6 || mState == 0) {
//            backToMain();
            mMainHandler.removeCallbacksAndMessages(null);
            setRecognition();
        }
        return true;
    }

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        LogUtils.d("%s--->>>onKeyCancel: mState=%d", HorizonScanFragment.class.getSimpleName(), mState);
        if (mState == 1) {
            showView(true);
        } else if (mState == 3) {
            setEdit();
        } else if (mState == 4) {
            if (mInputPwd.length() > 0) {
                int start = mInputPwd.length() - 1;
                int end = mInputPwd.length();
                mInputPwd.delete(start, end);
                if (start >= 0) {
                    tvHint3.setText(tvHint3.getText().subSequence(0, start));
                }
            } else {
                //backToMain();
                mMainHandler.removeCallbacksAndMessages(null);
                setRecognition();
            }
        } else if (mState == 5 || mState == 6) {
            setRecognition();
        }
        return true;
    }

    /** 进入编辑状态 */
    private void setEdit() {
        mState = 5;
        mReceiveFaceResult.stopReceiveFaceResult();
        updateOverlay(null, 0);
        flFace.setVisibility(View.VISIBLE);
        flAddSuc.setVisibility(View.GONE);
        hvHint.setVisibility(View.GONE);
        tvHint1.setText(R.string.face_scan_hint4);
        tvHint2.setText(R.string.face_manage_hint2);
        tvHint3.setText("");
        tvHint3.setVisibility(View.GONE);
        llOperHint.setVisibility(View.VISIBLE);
        tvHint4.setText(R.string.face_add_hint);
        tvHint4.setCompoundDrawables(null, null, null, null);
        tvHint5.setText(R.string.face_del_hint);
        tvHint5.setCompoundDrawables(null, null, null, null);
        tvHint6.setText(R.string.face_set_hint);
        tvHint6.setCompoundDrawables(null, null, null, null);
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
        tvHint3.setVisibility(View.VISIBLE);
        llOperHint.setVisibility(View.GONE);
        mState = 1;
        mResult = 0;
        mEnrollName = "";
        mRecogFaceId = "";
        mReceiveFaceResult.startReceiveFaceResult(this);
        SunriseSdkUtil.changeMode(false);
        mMainHandler.removeMessages(MSG_ENROLL_TIMEOUT);
//        mMainHandler.removeMessages(MSG_RECOGNIZE_TIMEOUT);
//        mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZE_TIMEOUT, Const.Config.FACE_RECOGNIZE_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
        setKeyboardMode(KeyboardCtrl.KEYMODE_EDIT);
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
        tvHint3.setVisibility(View.VISIBLE);
        llOperHint.setVisibility(View.GONE);
        mState = 2;
        mResult = 0;
        mEnrollName = BaseFacePresenter.genResidentFaceId(mCardNo);
        startEnroll(mEnrollName);
        mMainHandler.removeMessages(MSG_EDIT_TIMEOUT);
        mMainHandler.sendEmptyMessageDelayed(MSG_ENROLL_TIMEOUT, Const.Config.FACE_ENROLL_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
    }

    public void onRecognizeSuc(@NonNull final HorizonFaceModel faceModel) {
        if (faceModel.getFirstName().equals(mRecogFaceId))
            return;
        mRecogFaceId = faceModel.getFirstName();
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                flFace.setVisibility(View.VISIBLE);
                flAddSuc.setVisibility(View.GONE);
                hvHint.setVisibility(View.GONE);
                tvHint1.setText(R.string.face_recognition_suc2);
                tvHint2.setText(R.string.face_recognition_suc1);
                tvHint3.setText("");
                tvHint3.setVisibility(View.VISIBLE);
                llOperHint.setVisibility(View.GONE);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH);
                SinglechipClientProxy.getInstance().faceRecognizeSuccReport(
                        faceModel.getFirstName(), faceModel.getSnapPath(), faceModel.getSimilar());
                SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_HALF);
            }
        });
        mMainHandler.removeMessages(MSG_RECOGNIZING);
        mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZING, 4000);
        mMainHandler.removeMessages(MSG_RECOG_FACEID_TIMEOUT);
        mMainHandler.sendEmptyMessageDelayed(MSG_RECOG_FACEID_TIMEOUT, 5000);
//        mMainHandler.removeMessages(MSG_RECOGNIZE_TIMEOUT);
//        mMainHandler.sendEmptyMessageDelayed(MSG_RECOGNIZE_TIMEOUT, Const.Config.FACE_RECOGNIZE_TIMEOUT);
    }

    private void onRecognizeTimeout() {
        tvHint1.setText(R.string.face_recognition_fail);
        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
    }

    private void onEnrollSuc() {
        mExecutor.shutdownNow();
        mState = 6;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                flFace.setVisibility(View.GONE);
                flAddSuc.setVisibility(View.VISIBLE);
                hvHint.setVisibility(View.GONE);
            }
        });
//        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 10000);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRecognition();
            }
        }, 10000);
    }

    private void onEnrollFail() {
        mExecutor.shutdownNow();
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                PlaySoundUtils.playAssetsSound(CommStorePathDef.SET_ERR_PATH);
                tvHint1.setText(R.string.face_enrollment_hint2);
                tvHint2.setText(R.string.face_scan_hint1);
                tvHint3.setText("");
            }
        });
//        mMainHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRecognition();
            }
        }, 2000);
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

    //==========================================================================

    public static boolean appFirstStart = true;
    private Boolean mTryToOpen;
    private boolean CameraStarted = false;
    private Thread Camerathread = null;
    private String appPath;

    private void startLiveNess() {
        int ret = 0;
        boolean livenessCheck = AppConfig.getInstance().getFaceLiveCheck() == 1;
        if (livenessCheck) {
            LiveNessUtil.setThreshold(HorizonPreferences.getJudgeLiveThr());
            if (LiveNessUtil.initErrCode != 0) {
                ret = LiveNessUtil.initErrCode;
            } else {
                ret = LiveNessUtil.start();
            }
        }
        if (ret != 0) {
            LogUtils.d("活体启动失败,错误码: %d", ret);
        }
    }

    private void startCamera() {
        mCameraView.onResume();
        mCameraView.setCameraFrameRate(HorizonPreferences.getCameraFrameRate());
        if (appFirstStart) {
            mTryToOpen = true;
            Camerathread = new Thread(new Runnable() {
                @Override
                public void run() {
                    appFirstStart = false;
                    while (mTryToOpen && mCameraView.openCamera(30) < 0) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                        }
                    }
                    mCameraView.startCamera(true);
                    appPath = mContext.getFilesDir().getAbsolutePath();
                    LiveNessUtil.init(mContext, appPath + "/liveModules", appPath);
                    startLiveNess();
                    CameraStarted = true;
                }
            });
            Camerathread.start();
        } else {
            mCameraView.openCamera(30);
            mCameraView.startCamera(true);
            CameraStarted = true;
            startLiveNess();
        }
    }

    private void stopCamera() {
        mCameraView.onPause();
        if (CameraStarted) {
            mCameraView.stopCamera();
            CameraStarted = false;
        }
        boolean livenessCheck = AppConfig.getInstance().getFaceLiveCheck() == 1;
        if (livenessCheck) {
            LiveNessUtil.stop();
        }
        mReceiveFaceResult.stopReceiveFaceResult();
    }

    @Override
    public void updateOverlay(final FaceModuleResult faceModuleResult, final int boxSise) {
        if (faceModuleResult != null) {
            if (!SetDriverSinglechipClient.getInstance().getSystemSleep()) {
                LogUtils.d("--->>>system wake up!!!");
                SetDriverSinglechipClient.getInstance().setSystemSleep(1);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setRecognition();
                    }
                });
                FreeObservable.getInstance().observeFree();
            } else {
                FreeObservable.getInstance().resetFreeTime();
            }
            if (TextUtils.isEmpty(mRecogFaceId)) {
                SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
            }
        }
        mCameraOverlay.post(new Runnable() {
            @Override
            public void run() {
                mCameraOverlay.updateShapes(faceModuleResult, boxSise);
            }
        });
    }

    @Override
    public void faceRecogResult(FaceRecogResult recogResult) {
        Log.d(HorizonConst.TAG, recogResult.toString());
        if (recogResult.getSimilar() < HorizonPreferences.getFaceThr()) {
            // TODO  陌生人（相识度小于阈值）
            return;
        }
        boolean livenessCheck = AppConfig.getInstance().getFaceLiveCheck() == 1;
        if (livenessCheck && recogResult.getLiveness() <= 0) {
            // TODO -99空间已满，-11或-42正在初始化，0为假体
            return;
        }
        HorizonFaceModel faceModel = null;
        if (recogResult.getFaceId() != null) {
            HorizonFaceDao faceDao = new HorizonFaceDao();
            faceModel = faceDao.queryByFirstName(recogResult.getFaceId());
        }
        if (faceModel == null) {
            // TODO 陌生人（没有人脸注册记录）
            return;
        }
        faceModel.setSimilar(recogResult.getSimilar());
        faceModel.setSnapPath(recogResult.getSnapPath());
        onRecognizeSuc(faceModel);
    }

    // 创建分库
    private void createFaceSet() {
        HobotXWMListSetResult hobotXWMListSetResult = XWareHouseUtil.faceSetList();
        if (hobotXWMListSetResult != null) {
            int found = 0;
            for (int i = 0; i < hobotXWMListSetResult.num_; i++) {
                if (hobotXWMListSetResult.sets_[i].set_name_.equals(HorizonConst.LIBRARY_NAME)) {
                    found = 1;
                    break;
                }
            }
            if (found == 0) {
                int ret = XWareHouseUtil.createFaceSet(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION);
                Log.d(HorizonConst.TAG, "[FaceEnroll] createFaceSet: ret=" + ret);
                ret = XWareHouseUtil.setFaceSetThreshold(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION);
                Log.d(HorizonConst.TAG, "[FaceEnroll] setFaceSetThreshold: ret=" + ret);
            }
        }
    }

    private void startEnroll(final String name) {
        Runnable enrollTask = new Runnable() {
            @Override
            public void run() {
                ThreadUtils.sleep(2000);// 延迟两秒，防止人脸注册过快
                String imagePath = Const.Directory.TEMP + "/enroll.jpg";
                boolean suc = false;
                while (!suc) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    int res = mCameraView.savePhoto((int) System.currentTimeMillis(), 0, 0,
                            CameraDisplayUtil.getCameraWidth(), CameraDisplayUtil.getCameraHeight(),
                            CameraDisplayUtil.getCameraWidth(), CameraDisplayUtil.getCameraHeight(), imagePath);
                    Log.d(HorizonConst.TAG, "[FaceEnroll] savePhoto: res=" + res);
                    if (res > 0) {
                        suc = mFacePresenter.enrollFromImage(imagePath, name);
                    }
                }
                FileUtils.deleteFile(imagePath);//删除临时文件
                mResult = 1;
                mMainHandler.removeMessages(MSG_ENROLL_TIMEOUT);
                mMainHandler.sendEmptyMessage(MSG_ENROLL_TIMEOUT);
            }
        };
        mExecutor = Executors.newCachedThreadPool();
        mExecutor.execute(enrollTask);
    }
}
