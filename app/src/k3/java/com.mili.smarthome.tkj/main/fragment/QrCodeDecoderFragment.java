package com.mili.smarthome.tkj.main.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.android.client.ScanQrClient;
import com.google.zxing.Result;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;
import com.mili.widget.zxing.ZxingConst;
import com.mili.widget.zxing.decode.DecodeManager;

import org.linphone.LinphoneManager;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import java.util.concurrent.Semaphore;

public class QrCodeDecoderFragment extends K3BaseFragment implements ScanQrClient.OnScanQrClientListener,
        InterCommTypeDef.InterDefVideoDataListener, MediaPlayerUtils.OnMediaStatusCompletionListener {

    private SurfaceView svReceive;
    private TextureView tvPreview;
    private ScanQrClient mScanQrClient;
    private InterCommClient mInterCommClient;
    private AndroidVideoWindowImpl mAndroidVideoWindowImpl;
    private TextView tvHint2;

    private DecodeManager mDecodeManager;
    private Handler mResultHandler;
    private Semaphore mSemaphore;
    private boolean isDecodeSuccess = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qrcode;
    }

    @Override
    protected void bindView() {
        svReceive = findView(R.id.sv_receive);
        tvPreview = findView(R.id.tv_preview);
        tvHint2 = findView(R.id.tv_hint2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 重新创建Semaphore，防止release多次导致的permits异常
        mSemaphore = new Semaphore(1);

        final View vwSurface = findView(R.id.fl_surface);
        ViewUtils.applyScale(vwSurface, 16.0 / 9.0);

        if (mScanQrClient == null) {
            mScanQrClient = new ScanQrClient(mContext);
        }
        mScanQrClient.setOnScanQrClientListener(this);

        tvHint2.setText(R.string.qr_scan_tip);
        startPreview();
        startDecodeThread();
    }

    @Override
    public void onDestroyView() {
        stopPreview();
        stopDecodeThread();
        mSemaphore.tryAcquire();
        mSemaphore.release();
        super.onDestroyView();
    }

    private void startDecodeThread() {
        isDecodeSuccess = false;
        if (mResultHandler == null)
            mResultHandler = new DecodeResultHandler();
        if (mDecodeManager == null)
            mDecodeManager = new DecodeManager(mResultHandler);
        mDecodeManager.startDecodeThread();
    }

    private void stopDecodeThread() {
        if (mResultHandler != null) {
            mResultHandler.removeCallbacksAndMessages(null);
        }
        if (mDecodeManager != null) {
            mDecodeManager.quitSafely();
            mDecodeManager = null;
        }
    }

    private void startPreview() {
        if (mInterCommClient == null) {
            mInterCommClient = new InterCommClient(mContext);
        }
        mInterCommClient.InterPreviewStart(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_QRCODE);
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
                    //svPreview = surface;
                    LinphoneManager.getLc().setPreviewWindow(tvPreview);

                }

                public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {

                }
            }, false);
        }
    }

    private void stopPreview() {
        if (mInterCommClient != null){
            mInterCommClient.InterPreviewStop(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_QRCODE);
            mInterCommClient.setInterVideoDataCallBKListener(null);
            mInterCommClient.StopInterCommClient();
            mInterCommClient = null;
        }
    }

    @Override
    public void InterVideoCallBK(byte[] data, int datalen, int width, int height, int type) {
        if (mDecodeManager == null || isDecodeSuccess)
            return;
        if (mSemaphore.tryAcquire()) {
            mDecodeManager.decode(data, width, height);
        }
    }

    private void handleDecode(Result rawResult) {
        String qrcode = rawResult.getText();
        LogUtils.d("QR Code: " + qrcode);
        if (!TextUtils.isEmpty(qrcode)) {
            mScanQrClient.ScanQrDreal(qrcode, qrcode.length());
        }
    }

    @Override
    public void OnClientStateChanged(int qrState, String roomNo) {
        switch (qrState) {
            case 0:
                tvHint2.setText(R.string.comm_text_1);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, true, this);
                break;
            case 1:
                tvHint2.setText(R.string.comm_text_2);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1504_PATH, this);
                break;
            case 2:
                tvHint2.setText(R.string.comm_text_3);
                PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1505_PATH, this);
                break;
        }
    }

    @Override
    public void onMediaStatusCompletion(boolean flag) {
        // mContext为空，说明已经执行onDetach()
        if (mContext != null) {
            GotoMainDefaultTask.getInstance().run();
        }
    }

    @SuppressLint("HandlerLeak")
    class DecodeResultHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ZxingConst.DECODE_SUCCEEDED:
                    isDecodeSuccess = true;
                    handleDecode((Result) msg.obj);
                    break;

                case ZxingConst.DECODE_FAILED:
                    break;
            }
            mSemaphore.release();
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN || keyCode == KEYCODE_UNLOCK)
            return false;
        switch (keyCode) {
            case KEYCODE_BACK:
                GotoMainDefaultTask.getInstance().run();
                SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(2);
                break;
        }
        return true;
    }

}
