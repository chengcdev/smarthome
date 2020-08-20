package com.mili.smarthome.tkj.main.face.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
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
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.face.FaceDetectView;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.face.megvii.MegviiFaceInfoAdapter;
import com.mili.smarthome.tkj.face.megvii.MegviiRecogThread;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;

import static com.android.CommTypeDef.FaceAddType.FACETYPE_DEV;
import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;


/**
 * 人脸识别
 */
public class MegviiFaceRecogActivity extends BaseFaceActivity implements KeyBoardItemView.IOnKeyClickListener,
        ICardReaderListener, FreeObservable.FreeObserver {

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

    private boolean mHandlePreview;
    private String mRecogFaceId;
    private MegviiRecogThread mRecogThread;
    private FacePresenter<FaceMegviiModel> mFacePresenter = new MegviiFacePresenterImpl();

    private boolean isHasFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        ButterKnife.bind(this);
        AppManage.getInstance().stopScreenService();
        KeyBoardItemView.setOnkeyClickListener(this);

        // 识别线程
        mRecogThread = new MegviiRecogThread(mContext, mMainHandler);
        mRecogThread.start();

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
        super.onDestroy();
        if (mRecogThread != null && mRecogThread.isRunning()) {
            mRecogThread.interrupt();
        }
        FaceApi.reset();
        stopPreview();
        SinglechipClientProxy.getInstance().setCardState(0x00);
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        AppManage.getInstance().startScreenService();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MegviiRecogThread.MSG_RECOGNIZING:
                tvSubTitle.setText("");
                tvTitle.setText(R.string.face_scan_hint4);
                tvTitle.setTextColor(Color.WHITE);
                tvWarning.setText(R.string.face_scan_hint1);
                tvWarning.setTextColor(Color.WHITE);
                break;
            case MegviiRecogThread.MSG_RECOG_FACEID_TIMEOUT:
                mRecogFaceId = "";
                break;
            case MegviiRecogThread.MSG_RECOG_SUC:
                FaceMegviiModel faceModel = (FaceMegviiModel) msg.obj;
                Bundle bundle = msg.getData();
                int previewType = 0;
                byte[] previewData = null;
                if (bundle != null) {
                    previewType = bundle.getInt(MegviiRecogThread.KEY_PREVIEW_TYPE);
                    previewData = bundle.getByteArray(MegviiRecogThread.KEY_PREVIEW_DATA);
                }
                onRecognizeSuc(faceModel, previewType, previewData);
                break;
            case MegviiRecogThread.MSG_RECOG_TIMEOUT:
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
//        LogUtils.d("InterVideoCallBK------>>>>>width=" + width + "   height=" + height);

        //如果人脸功能未启用，则人脸识别业务不往下走
        if (!AppConfig.getInstance().isFaceEnabled()) {
            return;
        }
        //如果旷视人脸初始化未完成，则人脸识别业务不往下走
        if (MegviiFace.getInstance().mFacePassHandler == null) {
            return;
        }
        //如果识别状态切换到注册状态，则人脸识别业务不往下走且要取消人脸框，等待刷卡操作
        if (!mHandlePreview) {
            faceDetectView.setFaceInfoAdapter(null);
            faceDetectView.postInvalidate();
            return;
        }
        MegviiFaceInfoAdapter faceInfoAdapter = null;
        try {
            final byte[] dataNV21 = Common.I420ToNV21(data, width, height);
            FacePassImage image = new FacePassImage(dataNV21, width, height, MegviiFace.IMAGE_ROTATION, FacePassImageType.NV21);
            FacePassDetectionResult detectionResult = MegviiFace.getInstance().mFacePassHandler.feedFrame(image);
            if (detectionResult != null) {
                faceInfoAdapter = new MegviiFaceInfoAdapter()
                        .setType(type)
                        .setData(data)
                        .setWidth(width)
                        .setHeight(height)
                        .setMirror(type == 0)
                        .setDetectionResult(detectionResult);
                if (mRecogThread != null) {
                    mRecogThread.offer(faceInfoAdapter);
                }
            }
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        if (!isDestroyed() && faceInfoAdapter != null && faceInfoAdapter.getFaceCount() > 0) {
            SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
            FreeObservable.getInstance().resetFreeTime();
        }
        /* 将识别到的人脸在预览界面中圈出，并在上方显示人脸位置及角度信息 */
        faceDetectView.setFaceInfoAdapter(faceInfoAdapter);
        faceDetectView.postInvalidate();
    }

    private void setRecognition() {
        PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        mMainHandler.sendEmptyMessage(MegviiRecogThread.MSG_RECOGNIZING);
        mHandlePreview = true;
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
    }

    private void onRecognizeSuc(FaceMegviiModel faceModel, int previewType, byte[] previewData) {
        if (faceModel.getFirstName().equals(mRecogFaceId))
            return;
        FaceProtocolInfo faceInfo = MegviiFacePresenterImpl.convert(faceModel);
        int result = Common.validity(faceInfo);
        if (result == SUCCESS_STATE) {
            result = SinglechipClientProxy.getInstance().faceRecognizeSucc(faceModel.getFirstName(), faceModel.getKeyID(), faceModel.getSimilarity(), previewType, previewData);
        } else if (result == FAIL_STATE) {
            result = 0;
        }

        if (result == 1) {
            String roomNoStr = null;
            mRecogFaceId = faceModel.getFirstName();
            tvTitle.setText(R.string.face_recognition_suc2);
            tvWarning.setText(R.string.face_recognition_suc1);
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
            mMainHandler.removeMessages(MegviiRecogThread.MSG_RECOGNIZING);
            mMainHandler.sendEmptyMessageDelayed(MegviiRecogThread.MSG_RECOGNIZING, 4000);
            mMainHandler.removeMessages(MegviiRecogThread.MSG_RECOG_FACEID_TIMEOUT);
            mMainHandler.sendEmptyMessageDelayed(MegviiRecogThread.MSG_RECOG_FACEID_TIMEOUT, 5000);
            if (BuildConfig.isEnabledFaceValid && (faceModel.getLifecycle() > VALID_LIFECYCLE_MODE)) {
                mFacePresenter.subLifecycleInfo(faceModel.getFaceToken());
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
            mMainHandler.sendEmptyMessage(MegviiRecogThread.MSG_RECOG_TIMEOUT);
        }
        return true;
    }
}
