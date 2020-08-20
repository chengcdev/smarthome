package com.mili.smarthome.tkj.main.face.activity;

import android.view.SurfaceView;
import android.view.TextureView;

import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;

import org.linphone.LinphoneManager;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

/**
 * <p>2020-01-17 09:37  create by admin
 */
public abstract class BaseFaceActivity extends BaseK7Activity implements InterCommTypeDef.InterDefVideoDataListener {

    private SurfaceView svReceive;
    private TextureView tvPreview;
    private InterCommClient mInterCommClient;
    private AndroidVideoWindowImpl mAndroidVideoWindowImpl;

    protected void startPreview(final SurfaceView svReceive, final TextureView tvPreview) {
        this.svReceive = svReceive;
        this.tvPreview = tvPreview;
        if (mInterCommClient == null) {
            mInterCommClient = new InterCommClient(this);
        }
        mInterCommClient.InterPreviewStart(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_FACE);
        mInterCommClient.setInterVideoDataCallBKListener(this);

//        svReceive.setZOrderOnTop(false);
//        svPreview.setZOrderOnTop(true);
//        svPreview.setZOrderMediaOverlay(true);

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
    }

    protected void stopPreview() {
        if (mInterCommClient != null) {
            mInterCommClient.InterPreviewStop(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_FACE);
            mInterCommClient.setInterVideoDataCallBKListener(null);
            mInterCommClient.StopInterCommClient();
            mInterCommClient = null;
        }
    }

}
