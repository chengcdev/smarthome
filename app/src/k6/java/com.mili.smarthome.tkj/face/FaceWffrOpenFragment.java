package com.mili.smarthome.tkj.face;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.entities.FaceWffrModel;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.face.wffr.WffrFaceInfoAdapter;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;
import com.wf.wffrapp;
import com.wf.wffrjni;

import java.util.List;

import static com.android.CommTypeDef.FaceAddType.FACETYPE_DEV;
import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;

public class FaceWffrOpenFragment extends BaseFaceFragment implements View.OnClickListener,
        ICardReaderListener, FreeObservable.FreeObserver {

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

    private FacePresenter<FaceWffrModel> mFacePresenter = new WffrFacePresenterImpl();

    /** <p>1识别 <p>2注册 <p>3删除 <p>4请刷卡 <p>5请选择 <p>6注册成功 */
    private int mState = 0;
    private String mCardNo;
    private String mEnrollName;
    private int mResult;
    private String mRecogFaceId;

    private BackTask mBackTask = new BackTask();
    private RelativeLayout mImgBack;
    private TextView mTvBack;

    /* 陌生人脸抓拍定制 */
    private int mRecognizeTimes = 0;
    private boolean mSnapFlag = false;
    private boolean mSnapThreadRun = false;
    private int mNoFaceTimes = 0;
    private int mSnapStrangerUse = 0;

    @Override
    public int getLayout() {
        return R.layout.fragment_face_wffr_open;
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
        AppUtils.getInstance().stopScreenService();
        SinglechipClientProxy.getInstance().setFingerState(0x01);
        SinglechipClientProxy.getInstance().setCardReaderListener(this);

        faceDetectView.setRecognitionThreshold(wffrjni.GetRecognitionThreshold());
        ivToggle.setVisibility(isEnabledRtsp() ? View.VISIBLE : View.GONE);
        setRecognition();
        startPreview(svReceive, tvPreview);

        /* 人脸识别计数，10后若未识别则抓拍 */
        mSnapStrangerUse = SnapParamDao.getFaceStrangerSnap();
        if (mSnapStrangerUse == 1) {
            mSnapThreadRun = true;
            new SnapThread().start();
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
        stopPreview();
        wffrapp.stopExecution();
        mMainHandler.removeCallbacksAndMessages(null);
        SinglechipClientProxy.getInstance().setFingerState(0x00);
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        AppUtils.getInstance().startScreenService();
        mSnapThreadRun = false;
        super.onDestroyView();
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (mState == 1 && freeTime > Const.Config.FACE_RECOGNIZE_TIMEOUT) {
            mState = 0;
            mMainHandler.sendEmptyMessage(MSG_END_RECOGNIZE);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                mState = 4;
                wffrapp.stopExecution();
                SinglechipClientProxy.getInstance().setCardState(0x02);
                SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
                tvHint1.setText(R.string.face_enrollment_hint1);
                tvRight.setText(R.string.face_manage_hint1);
                tvRight.setVisibility(View.VISIBLE);
                tvAdd.setVisibility(View.GONE);
                tvDel.setVisibility(View.GONE);
                tvEdit.setVisibility(View.GONE);
                mMainHandler.sendEmptyMessageDelayed(MSG_EDIT_TIMEOUT, 20000);
                break;
            case R.id.tv_add:
                setEnrollment();
                break;
            case R.id.tv_del:
                mState = 3;
                Bundle data = new Bundle();
                data.putString(FaceDelFragment.ARGS_CARD_NO, mCardNo);
                Intent delfaceIntent = new Intent(Constant.Action.BODY_FACE_DEL_ACTION);
                delfaceIntent.putExtras(data);
                mContext.sendBroadcast(delfaceIntent);
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
                mCardNo = WffrFacePresenterImpl.cardIdToString(cardId);
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
                            LogUtils.d(" [FaceWffrOpenFragment >>> InterVideoCallBK] faceInfoAdapter is null.");
                        }
                    } else {
                        mNoFaceTimes = 0;
                    }
                }
            }
            if (type == getPreviewType()) {
                drawOutput(faceInfoAdapter);
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

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECOGNIZING:
                tvHint1.setText(R.string.face_scan_hint1);
                tvHint2.setText(R.string.face_scan_hint2);
                tvEdit.setVisibility(View.VISIBLE);
                break;
            case MSG_RECOG_FACEID_TIMEOUT:
                mRecogFaceId = "";
                break;
            case MSG_END_RECOGNIZE:
                onRecognizeTimeout();
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
        }
    }

    private void setRecognition() {
        if (AppConfig.getInstance().getFaceLiveCheck() == 0) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_PATH);
        }
        tvHint1.setText(R.string.face_scan_hint1);
        tvHint2.setText(R.string.face_scan_hint2);
        tvEdit.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.GONE);
        tvAdd.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        mResult = 0;
        mState = 1;
        mEnrollName = "";
        mRecogFaceId = "";
        wffrapp.setState(wffrapp.RECOGNITION);
        mMainHandler.removeMessages(MSG_END_ENROLL);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);

        /* 陌生人脸抓拍 */
        if (mSnapStrangerUse == 1) {
            mSnapFlag = false;
            mRecognizeTimes = 0;
            mNoFaceTimes = 0;
            LogUtils.d(" [FaceWffrOpenFragment >>> setRecognition] ");
        }
    }

    private void setEnrollment() {
        if (AppConfig.getInstance().getFaceLiveCheck() == 0) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_PATH);
        }
        tvHint1.setText(R.string.face_scan_hint1);
        tvHint2.setText(R.string.face_enrollment_hint2);
        tvRight.setVisibility(View.GONE);
        tvAdd.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
        mResult = 0;
        mState = 2;
        mEnrollName = BaseFacePresenter.genResidentFaceId(mCardNo);
        wffrapp.setState(wffrapp.ENROLLMENT);
        mMainHandler.removeMessages(MSG_EDIT_TIMEOUT);
        mMainHandler.sendEmptyMessageDelayed(MSG_END_ENROLL, Const.Config.FACE_ENROLL_TIMEOUT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
    }

    private void onRecognizeSuc(FaceWffrModel faceModel, int previewType, byte[] previewData) {
        if (faceModel.getFirstName().equals(mRecogFaceId))
            return;
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
        //tvHint2.setText(R.string.face_recognition_fail);
        tvRight.setVisibility(View.GONE);
        tvAdd.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
        if (AppConfig.getInstance().getScreenSaver() == 1 || AppConfig.getInstance().getPowerSaving() == 1) {
            AppUtils.getInstance().openScreenService();
            return;
        }
        backMainActivity();
    }

    private void onEnrollSuc() {
        // 添加人脸信息
        mState = 6;
        FaceWffrModel faceInfoModel = new FaceWffrModel();
        faceInfoModel.setFirstName(mEnrollName);
        faceInfoModel.setCardNo(mCardNo);
        mFacePresenter.addFaceInfo(faceInfoModel);
        mContext.sendBroadcast(new Intent(Constant.Action.BODY_FACE_ENROLL_ACTION));
    }

    private void onEnrollFail() {
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
     * @param frameData 一帧视频数据
     * @param width     宽
     * @param height    高
     */
    private void strangerSanp(byte[] frameData, int width, int height) {
        LogUtils.d("=======[FaceWffrOpenFragment] [strangerSanp] photolen=%d mRecognizeTimes=%d mSnapFlag=%b mNoFaceTimes=%d",
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
            LogUtils.d("=======[FaceWffrOpenFragment] [strangerSanp] ok ");
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
            LogUtils.d("[FaceWffrOpenFragment][savePhoto] photoName is " + filename);
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
