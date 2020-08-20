package com.mili.smarthome.tkj.face;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.android.Common;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.entities.FaceWffrModel;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;

import org.linphone.LinphoneManager;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;

public abstract class BaseFaceFragment extends K3BaseFragment implements InterCommTypeDef.InterDefVideoDataListener {
    private static final String TAG = "lfl";
    private static int previewType = 0;
    private SurfaceView svReceive;
    private TextureView tvPreview;
    private String mRtspUrl;
    private InterCommClient mInterCommClient;
    private AndroidVideoWindowImpl mAndroidVideoWindowImpl;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRtspUrl = AppConfig.getInstance().getRtspUrl();
    }

    /**
     * @return 0忽略；1开锁成功；-1无效人脸
     */
    public int faceRecognizeSuccReport(String faceName, float similar, int previewType, byte[] previewData) {
        return SinglechipClientProxy.getInstance().faceRecognizeSucc(faceName, "",similar, previewType, previewData);
    }

    /**
     * @return 0忽略；1开锁成功；-1无效人脸
     */
    public int faceRecognizeSuccReport(FaceProtocolInfo faceInfo, float similar, int previewType, byte[] previewData) {
        return SinglechipClientProxy.getInstance().faceRecognizeSucc(faceInfo.getFaceFirstName(), faceInfo.getKeyID(), similar, previewType, previewData);
    }

    public boolean isEnabledRtsp() {
        return BuildConfigHelper.isEnabledIPC() && mRtspUrl != null && mRtspUrl.length() != 0;
    }

    public void togglePreviewType() {
        if (previewType == 0) {
            setPreviewType(1);
        } else {
            setPreviewType(0);
        }
    }

    public void setPreviewType(int previewType) {
        BaseFaceFragment.previewType = previewType;
        onPreviewTypeChanged();
    }

    public int getPreviewType() {
        return BaseFaceFragment.previewType;
    }

    private void onPreviewTypeChanged() {
        if (tvPreview == null || svReceive == null) {
            return;
        }
        if (previewType == 1) {
            tvPreview.setVisibility(View.INVISIBLE);
            svReceive.setVisibility(View.VISIBLE);
        } else {
            tvPreview.setVisibility(View.VISIBLE);
            svReceive.setVisibility(View.INVISIBLE);
        }
    }

    protected void startPreview(final SurfaceView svReceive, final TextureView tvPreview) {
        this.svReceive = svReceive;
        this.tvPreview = tvPreview;
        onPreviewTypeChanged();
        if (mInterCommClient == null) {
            mInterCommClient = new InterCommClient(mContext);
        }
        mInterCommClient.InterPreviewStart(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_FACE);
        if (isEnabledRtsp()) {
            mInterCommClient.InterRtspStart(mRtspUrl);
        }
        mInterCommClient.setInterVideoDataCallBKListener(this);

        if (mAndroidVideoWindowImpl == null) {
            mAndroidVideoWindowImpl = new AndroidVideoWindowImpl(svReceive, tvPreview, new AndroidVideoWindowImpl.VideoWindowListener() {

                public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
                    //svReceive = surface;
                    LinphoneManager.getLc().setVideoWindow(vw);
                }

                public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {

                }

                public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
                    //tvPreview = surface;
                    LinphoneManager.getLc().setPreviewWindow(tvPreview);

                }

                public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {

                }
            }, false);
        }
        Log.d(TAG, "BaseFaceFragment: startPreview");
    }

    protected void stopPreview() {
        if (mInterCommClient != null){
            mInterCommClient.setInterVideoDataCallBKListener(null);
            mInterCommClient.InterPreviewStop(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_FACE);
            if (isEnabledRtsp()) {
                mInterCommClient.InterRtspStop();
            }
            mInterCommClient.StopInterCommClient();
            mInterCommClient = null;
        }
        Log.d(TAG, "BaseFaceFragment: stopPreview");
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN || keyCode == KEYCODE_UNLOCK) {
            return false;
        }
        switch (keyCode) {
            case KEYCODE_BACK:
                GotoMainDefaultTask.getInstance().run();
                SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(1);
                break;
        }
        return true;
    }
}
