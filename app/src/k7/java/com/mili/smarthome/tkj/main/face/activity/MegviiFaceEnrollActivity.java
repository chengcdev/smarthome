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
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.MegviiFacePresenterImpl;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.face.FaceDetectView;
import com.mili.smarthome.tkj.face.megvii.MegviiEnrollThread;
import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.face.megvii.MegviiFaceInfoAdapter;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassAddFaceDetectionResult;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;


/**
 * 人脸注册
 */
public class MegviiFaceEnrollActivity extends BaseFaceActivity implements KeyBoardItemView.IOnKeyClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_subtitle)
    TextView tvSubTitle;
    @BindView(R.id.tv_warning)
    TextView tvWarning;
    @BindView(R.id.key_cancle)
    KeyBoardItemView keyCancle;

    @BindView(R.id.sv_receive)
    SurfaceView svReceive;
    @BindView(R.id.tv_preview)
    TextureView tvPreview;
    @BindView(R.id.detectView)
    FaceDetectView faceDetectView;

    private String mCardNo;
    private String mEnrollName;
    private MegviiEnrollThread mEnrollThread;
    private FacePresenter<FaceMegviiModel> mFacePresenter = new MegviiFacePresenterImpl();

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MegviiEnrollThread.MSG_ENROLL_SUC:
                String token = (String) msg.obj;
                onEnrollSuc(token);
                break;
            case MegviiEnrollThread.MSG_ENROLL_TIMEOUT:
                onEnrollFail();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_enroll);
        ButterKnife.bind(this);
        KeyBoardItemView.setOnkeyClickListener(this);

        if (!FaceApi.checkGroup()) {
            FaceApi.createGroup();
        }

        Intent intent = getIntent();
        mCardNo = intent.getStringExtra(FaceManageActivity.EXTRA_CARDNO);

        setEnrollment();
        startPreview(svReceive, tvPreview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPreview();
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        if (mEnrollThread != null && mEnrollThread.isRunning()) {
            mEnrollThread.interrupt();
            mEnrollThread = null;
        }
    }

    @Override
    public void OnViewDownClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardDown(keyCancle);
                break;
        }
    }

    @Override
    public void OnViewUpClick(int code, View view) {
        int position = AppManage.getInstance().getPosition(code);
        switch (position) {
            case Constant.KeyNumId.KEY_NUM_12:
            case Constant.KeyNumId.KEY_NUM_13:
            case Constant.KeyNumId.KEY_NUM_14:
                AppManage.getInstance().keyBoardUp(keyCancle);
                AppManage.getInstance().restartLauncherAct();
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
        if (MegviiFace.getInstance().mFacePassHandler == null) {
            return;
        }
        MegviiFaceInfoAdapter faceInfoAdapter = null;
        try {
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
                if (addFaceDetectionResult.image != null && mEnrollThread != null) {
                    mEnrollThread.offer(addFaceDetectionResult.image);
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

    private void setEnrollment() {
        if (AppConfig.getInstance().getFaceLiveCheck() == 0) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_PATH);
        }
        tvSubTitle.setText(R.string.face_enrollment_hint2);
        tvTitle.setText(R.string.face_scan_hint4);
        tvTitle.setTextColor(Color.GREEN);
        tvWarning.setText(R.string.face_scan_hint1);
        tvWarning.setTextColor(Color.WHITE);
        mEnrollName = BaseFacePresenter.genResidentFaceId(mCardNo);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);

        mEnrollThread = new MegviiEnrollThread(mMainHandler);
        mEnrollThread.start();
    }

    private void onEnrollSuc(String token) {
        FaceMegviiModel faceInfoModel = new FaceMegviiModel();
        faceInfoModel.setFirstName(mEnrollName);
        faceInfoModel.setCardNo(mCardNo);
        faceInfoModel.setFaceToken(token);
        mFacePresenter.addFaceInfo(faceInfoModel);
        AppManage.getInstance().toActFinish(this, FacePromptActivity.class);
    }

    private void onEnrollFail() {
        PlaySoundUtils.playAssetsSound(CommStorePathDef.SET_ERR_PATH, new MediaPlayerUtils.OnMediaStatusCompletionListener() {
            @Override
            public void onMediaStatusCompletion(boolean flag) {
                AppManage.getInstance().restartLauncherAct();
            }
        });
    }

}
