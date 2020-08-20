package com.mili.smarthome.tkj.main.qrcode;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.TextView;

import com.android.CommStorePathDef;
import com.android.InterCommTypeDef;
import com.android.client.InterCommClient;
import com.android.client.ScanQrClient;
import com.google.zxing.Result;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.fragment.BaseMainFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MediaPlayerUtils;
import com.mili.smarthome.tkj.utils.PlaySoundUtils;
import com.mili.widget.zxing.ZxingConst;
import com.mili.widget.zxing.decode.DecodeManager;

import org.linphone.LinphoneManager;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * 扫码开门
 */

public class QrcodeDecoderFragment extends BaseMainFragment implements ISetCallBackListener, ScanQrClient.OnScanQrClientListener,
        InterCommTypeDef.InterDefVideoDataListener, MediaPlayerUtils.OnMediaStatusCompletionListener {


    private TextureView previewView;
    private TextView mTvTitle;
    private SurfaceView mSurfaceView;
    private ScanQrClient scanQrClient;
    private InterCommClient mInterCommClient;
    private AndroidVideoWindowImpl mAndroidVideoWindowImpl;
    private String TAG = "QrcodeOpenFragment";
    private Semaphore mSemaphore = new Semaphore(1);
    private boolean isDecodeSuccess = false;
    private DecodeResultHandler mResultHandler;
    private DecodeManager mDecodeManager;

    public int getLayout() {
        return R.layout.fragment_qrcode_decoder;
    }


    @Override
    public void initView() {
        previewView = (TextureView) getContentView().findViewById(R.id.preview_view);
        mTvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        mSurfaceView = (SurfaceView) getContentView().findViewById(R.id.recv_view);
    }

    @Override
    public void initAdapter() {

    }


    @Override
    public void initListener() {
        if (scanQrClient == null) {
            scanQrClient = new ScanQrClient(getContext());
        }
        scanQrClient.setOnScanQrClientListener(this);
        startPreview();
        startDecodeThread();
    }

    @Override
    public void success() {
        if (isAdded()) {
            exitFragment(this);
        }
    }

    @Override
    public void fail() {

    }

    @Override
    public void onDestroyView() {
        stopPreview();
        stopDecodeThread();
        mSemaphore.tryAcquire();
        mSemaphore.release();
        if (scanQrClient != null) {
            scanQrClient.stopScanQrClient();
            scanQrClient = null;
        }
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

    private void stopPreview() {
        if (mInterCommClient != null) {
            mInterCommClient.InterPreviewStop(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_QRCODE);
            mInterCommClient.setInterVideoDataCallBKListener(null);
            mInterCommClient.StopInterCommClient();
            mInterCommClient = null;
        }
    }



    private void startPreview() {
        if (mInterCommClient == null) {
            mInterCommClient = new InterCommClient(getContext());
        }
        mInterCommClient.InterPreviewStart(InterCommTypeDef.VideoCallBKState.SEND_VIDEO_STATE_QRCODE);
        mInterCommClient.setInterVideoDataCallBKListener(this);

        mSurfaceView.setZOrderOnTop(false);
        if (mAndroidVideoWindowImpl == null) {
            mAndroidVideoWindowImpl = new AndroidVideoWindowImpl(mSurfaceView, previewView, new AndroidVideoWindowImpl.VideoWindowListener() {

                public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
                    LinphoneManager.getLc().setVideoWindow(vw);
                }

                public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {

                }

                public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
                    LinphoneManager.getLc().setPreviewWindow(previewView);

                }

                public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {

                }
            }, false);
        } else {
            LogUtils.w("    mAndroidVideoWindowImpl is not null");
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

    @SuppressLint("HandlerLeak")
    class DecodeResultHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 解码成功
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
    /**
     * @param rawResult 返回的扫描结果
     */
    public void handleDecode(Result rawResult) {
        String result = rawResult.getText();
        //解析二维码
        if (!result.equals("")) {
            LogUtils.w(TAG + "   qr result: " + result);
            scanQrClient.ScanQrDreal(result, result.length());
        }

    }

    @Override
    public void OnClientStateChanged(int qrState, String roomNo) {
        //0:有效二维码  1:无效二维码  2:过期二维码
        LogUtils.w(TAG + "   qr qrState: " + qrState);
        switch (qrState) {
            case 0:
                if (mContext != null) {
                    mTvTitle.setText(mContext.getString(R.string.qr_tip_3));
                    //播放语音
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1501_PATH, roomNo, true, this);
                }
                break;
            case 1:
                if (mContext != null) {
                    mTvTitle.setText(mContext.getString(R.string.qr_tip_4));
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1504_PATH, this);
                }

                break;
            case 2:
                if (mContext != null) {
                    mTvTitle.setText(mContext.getString(R.string.qr_tip_5));
                    //播放语音
                    PlaySoundUtils.playAssetsSound(CommStorePathDef.VOICE_1505_PATH, this);
                }
                break;
        }
    }

    @Override
    public void onMediaStatusCompletion(boolean flag) {
        Objects.requireNonNull(getActivity()).finish();
    }

}
