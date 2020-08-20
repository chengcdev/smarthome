package com.mili.smarthome.tkj.main.face.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.Common;
import com.android.interf.ICardReaderListener;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.WffrFacePresenterImpl;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.FaceWffrModel;
import com.mili.smarthome.tkj.face.FaceDetectView;
import com.mili.smarthome.tkj.face.FaceInfo;
import com.mili.smarthome.tkj.face.FaceInfoAdapter;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.face.wffr.WffrFaceInfoAdapter;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.wf.wffrapp;
import com.wf.wffrjni;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.CommTypeDef.FaceAddType.FACETYPE_DEV;
import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;


/**
 * 人脸识别
 */
public class WffrFaceRecogActivity extends BaseFaceActivity implements KeyBoardItemView.IOnKeyClickListener,
        ICardReaderListener, FreeObservable.FreeObserver {

    private static final int MSG_RECOGNIZING = 0x10;
    private static final int MSG_RECOG_FACEID_TIMEOUT = 0x15;
    private static final int MSG_TIMEOUT = 0x1F;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_subtitle)
    TextView tvSubTitle;
    @BindView(R.id.tv_warning)
    TextView tvWarning;
    @BindView(R.id.key_cancle)
    KeyBoardItemView keyCancle;
    @BindView(R.id.key_face)
    KeyBoardItemView keyFace;
    @BindView(R.id.key_open_door_pwd)
    KeyBoardItemView keyOpenDoorPwd;
    @BindView(R.id.sv_receive)
    SurfaceView svReceive;
    @BindView(R.id.tv_preview)
    TextureView tvPreview;
    @BindView(R.id.detectView)
    FaceDetectView faceDetectView;

    private boolean mFaceLiveCheck;
    private boolean mHandlePreview;
    /**
     * 识别到人脸时记录开始时间，累计10秒未识别成功则播放识别不成功语音
     */
    private long mRecogStartTime;
    private String mRecogFaceId;
    private FacePresenter<FaceWffrModel> mFacePresenter = new WffrFacePresenterImpl();

    private boolean isHasFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        ButterKnife.bind(this);
        AppManage.getInstance().stopScreenService();
        KeyBoardItemView.setOnkeyClickListener(this);

        mFaceLiveCheck = (AppConfig.getInstance().getFaceLiveCheck() == 1);
        faceDetectView.setRecognitionThreshold(wffrjni.GetRecognitionThreshold());
        setRecognition();
        startPreview(svReceive, tvPreview);

        SinglechipClientProxy.getInstance().setCardReaderListener(this);
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
    protected void onDestroy() {
        stopPreview();
        wffrapp.stopExecution();
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        AppManage.getInstance().startScreenService();
        super.onDestroy();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECOGNIZING:
                tvSubTitle.setText("");
                if (mFaceLiveCheck) {
                    tvTitle.setText(R.string.face_scan_hint3);
                } else {
                    tvTitle.setText(R.string.face_scan_hint4);
                }
                tvTitle.setTextColor(Color.WHITE);
                tvWarning.setText(R.string.face_scan_hint1);
                tvWarning.setTextColor(Color.WHITE);
                break;
            case MSG_RECOG_FACEID_TIMEOUT:
                mRecogStartTime = 0;
                mRecogFaceId = "";
                break;
            case MSG_TIMEOUT:
                onRecognizeTimeout();
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isHasFocus = hasFocus;
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void OnViewDownClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
                AppManage.getInstance().keyBoardDown(keyCancle);
                break;
            case Constant.KeyNumId.KEY_NUM_13:
                //人脸识别
                AppManage.getInstance().keyBoardDown(keyFace);
                break;
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardDown(keyOpenDoorPwd);

                break;
        }
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
                AppManage.getInstance().keyBoardUp(keyCancle);
                if (isFastDoubleUpClick() && !isHasFocus) {
                    return;
                }
                //主动退出，禁用人体感应10秒
                SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(1);
                finish();
                break;
            case Constant.KeyNumId.KEY_NUM_13: {
                AppManage.getInstance().keyBoardUp(keyFace);
                if (isFastDoubleUpClick() && !isHasFocus) {
                    return;
                }
                SinglechipClientProxy.getInstance().setCardState(0x02);
                tvSubTitle.setText("");
                tvTitle.setText(R.string.face_manage_hint1);
                tvTitle.setTextColor(Color.WHITE);
                tvWarning.setText(R.string.face_enrollment_hint1);
                tvWarning.setTextColor(Color.GREEN);
                mHandlePreview = false;
                wffrapp.stopExecution();
                SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SinglechipClientProxy.getInstance().setCardState(0x00);
                        setRecognition();
                    }
                }, 5000);
                break;
            }
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardUp(keyOpenDoorPwd);
                if (isFastDoubleUpClick() && !isHasFocus) {
                    return;
                }
                //显示输入开门密码界面
                if (AppConfig.getInstance().getCallType() == 1) {
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1301_PATH);
                    AppManage.getInstance().toAct(this, MainActivity.class);
                } else {
                    AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_FACE_TO_OPEN);
                }
                finish();
                break;
        }
    }

    @Override
    public void onCardRead(int cardId, int result) {
        mMainHandler.removeCallbacksAndMessages(null);
        SinglechipClientProxy.getInstance().setCardState(0x00);
        if (result == 0) {
            String cardNo = BaseFacePresenter.cardIdToString(cardId);
            Intent intent = new Intent(this, FaceManageActivity.class);
            intent.putExtra(FaceManageActivity.EXTRA_CARDNO, cardNo);
            startActivity(intent);
            finish();
        } else {
            tvWarning.setTextColor(Color.RED);
            tvWarning.setText(R.string.comm_text_f2);
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRecognition();
                }
            }, 2000);
        }
    }

    @Override
    public void InterVideoCallBK(byte[] data, int datalen, int width, int height, final int type) {
        try {
//            YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
//            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/out.jpg");
//            FileOutputStream filecon = new FileOutputStream(file);
//            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, filecon);

            if (!mHandlePreview) {
                mRecogStartTime = 0;
                faceDetectView.setFaceInfoAdapter(null);
                faceDetectView.setEnrolling(false);
                faceDetectView.postInvalidate();
                return;
            }

            FaceInfoAdapter faceInfoAdapter = null;
            final byte[] previewData = new byte[datalen];
            System.arraycopy(data, 0, previewData, 0, datalen);
            wffrapp.startExecution(previewData, width, height, "");
            List<FaceInfo> faceList = wffrapp.getFaceParseResult();

            if (faceList != null && faceList.size() > 0) {
                SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
                FreeObservable.getInstance().resetFreeTime();
                if (mRecogStartTime == 0) {
                    mRecogStartTime = SystemClock.uptimeMillis();
                }
                boolean isSuc = false;
                for (FaceInfo faceInfo : faceList) {
                    if (faceInfo.getSimilar() > wffrjni.GetRecognitionThreshold()) {
                        final FaceWffrModel faceModel = mFacePresenter.verifyFaceId(faceInfo.getFaceId());
                        if (faceModel != null) {
                            isSuc = true;
                            faceModel.setConfidence(faceInfo.getSimilar());
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
                if (!isSuc && mRecogStartTime != 0) {
                    long spend = SystemClock.uptimeMillis() - mRecogStartTime;
                    if (spend > 10000) {
                        PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPER_ERROR_PATH);
                        mRecogStartTime = 0;
                    }
                }
                faceInfoAdapter = new WffrFaceInfoAdapter()
                        .setData(data)
                        .setWidth(width)
                        .setHeight(height)
                        .setMirror(type == 0)
                        .setFaceList(faceList);
            } else {
                mRecogStartTime = 0;
            }
            faceDetectView.setFaceInfoAdapter(faceInfoAdapter);
            faceDetectView.setEnrolling(false);
            faceDetectView.postInvalidate();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private void setRecognition() {
        if (mFaceLiveCheck) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_PATH);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        }
        mMainHandler.sendEmptyMessage(MSG_RECOGNIZING);
        mHandlePreview = true;
        mRecogStartTime = 0L;
        mRecogFaceId = "";
        wffrapp.setState(wffrapp.RECOGNITION);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
    }

    private void onRecognizeSuc(FaceWffrModel faceModel, int previewType, byte[] previewData) {
        if (faceModel.getFirstName().equals(mRecogFaceId))
            return;
        FaceProtocolInfo faceInfo = WffrFacePresenterImpl.convert(faceModel);
        int result = Common.validity(faceInfo);
        if (result == SUCCESS_STATE){
            result = SinglechipClientProxy.getInstance().faceRecognizeSucc(faceModel.getFirstName(), faceModel.getKeyID(), faceModel.getConfidence(), previewType, previewData);
        }
        else if (result == FAIL_STATE){
            result = 0;
        }

        if (result == 1) {
            String roomNoStr = null;
            mRecogStartTime = 0;
            mRecogFaceId = faceModel.getFirstName();
            tvTitle.setText(R.string.face_recognition_suc2);
            tvWarning.setText(R.string.face_recognition_suc1);
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
        mHandlePreview = false;
        tvTitle.setText(R.string.face_recognition_fail2);
        tvSubTitle.setText(R.string.face_recognition_fail1);
        //是否马上开启屏保或者关屏
        if (AppConfig.getInstance().getScreenSaver() == 1 || AppConfig.getInstance().getPowerSaving() == 1) {
            AppManage.getInstance().openScreenService();
            finish();
            return;
        }
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppManage.getInstance().restartLauncherAct();
            }
        }, 2000);
    }

    @Override
    public boolean onFreeReport(long freeTime) {
        if (freeTime > Const.Config.FACE_RECOGNIZE_TIMEOUT) {
            mMainHandler.sendEmptyMessage(MSG_TIMEOUT);
        }
        return true;
    }
}
