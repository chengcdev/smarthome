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
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenter;
import com.mili.smarthome.tkj.appfunc.facefunc.WffrFacePresenterImpl;
import com.mili.smarthome.tkj.entities.FaceWffrModel;
import com.mili.smarthome.tkj.face.FaceDetectView;
import com.mili.smarthome.tkj.face.FaceInfo;
import com.mili.smarthome.tkj.face.FaceInfoAdapter;
import com.mili.smarthome.tkj.face.wffr.WffrFaceInfoAdapter;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.wf.wffrapp;
import com.wf.wffrjni;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 人脸注册
 */
public class WffrFaceEnrollActivity extends BaseFaceActivity implements KeyBoardItemView.IOnKeyClickListener {

    private static final int MSG_TIMEOUT = 0x30;

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

    private boolean mFaceLiveCheck;
    private String mCardNo;
    private String mEnrollName;
    private int mResult;
    private FacePresenter<FaceWffrModel> mFacePresenter = new WffrFacePresenterImpl();

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TIMEOUT:
                if (mResult > 0) {
                    onEnrollSuc();
                } else {
                    onEnrollFail();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_enroll);
        ButterKnife.bind(this);
        KeyBoardItemView.setOnkeyClickListener(this);

        Intent intent = getIntent();
        mCardNo = intent.getStringExtra(FaceManageActivity.EXTRA_CARDNO);
        mFaceLiveCheck = (AppConfig.getInstance().getFaceLiveCheck() == 1);
        faceDetectView.setRecognitionThreshold(wffrjni.GetRecognitionThreshold());
        setEnrollment();
        startPreview(svReceive, tvPreview);
    }

    @Override
    protected void onDestroy() {
        stopPreview();
        wffrapp.stopExecution();
        SinglechipClientProxy.getInstance().ctrlCamLamp(SinglechipClientProxy.TURN_OFF);
        super.onDestroy();
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
        try {
//            YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
//            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/out.jpg");
//            FileOutputStream filecon = new FileOutputStream(file);
//            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, filecon);

            wffrapp.startExecution(data, width, height, mEnrollName);
            List<FaceInfo> faceList = wffrapp.getFaceParseResult();

            if (faceList != null && faceList.size() > 0) {
                SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_ON_FOR_FACE);
                for (FaceInfo faceInfo : faceList) {
                    if (faceInfo.getSimilar() == -1) {
                        continue;
                    }
                    mResult++;
                }
            }
            FaceInfoAdapter faceInfoAdapter = new WffrFaceInfoAdapter()
                    .setData(data)
                    .setWidth(width)
                    .setHeight(height)
                    .setMirror(type == 0)
                    .setFaceList(faceList);
            faceDetectView.setFaceInfoAdapter(faceInfoAdapter);
            faceDetectView.setEnrolling(true);
            faceDetectView.postInvalidate();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private void setEnrollment() {
        if (mFaceLiveCheck) {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_PATH);
            tvTitle.setText(R.string.face_scan_hint3);
        } else {
            PlaySoundUtils.playAssetsSound(CommStorePathDef.FACE_OPERATE_SHORT_PATH);
            tvTitle.setText(R.string.face_scan_hint4);
        }
        tvSubTitle.setText(R.string.face_enrollment_hint2);
        tvTitle.setTextColor(Color.GREEN);
        tvWarning.setText(R.string.face_scan_hint1);
        tvWarning.setTextColor(Color.WHITE);
        mResult = 0;
        mEnrollName = BaseFacePresenter.genResidentFaceId(mCardNo);
        wffrapp.setState(wffrapp.ENROLLMENT);
        SinglechipClientProxy.getInstance().ctrlCamLampChange(SinglechipClientProxy.TURN_HALF);
        mMainHandler.removeMessages(MSG_TIMEOUT);
        mMainHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, Const.Config.FACE_ENROLL_TIMEOUT);
    }

    private void onEnrollSuc() {
        FaceWffrModel faceInfoModel = new FaceWffrModel();
        faceInfoModel.setFirstName(mEnrollName);
        faceInfoModel.setCardNo(mCardNo);
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
