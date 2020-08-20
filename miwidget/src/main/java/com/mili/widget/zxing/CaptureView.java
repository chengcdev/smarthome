package com.mili.widget.zxing;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.Result;
import com.mili.widget.zxing.camera.CameraManager;
import com.mili.widget.zxing.decode.DecodeCallback;
import com.mili.widget.zxing.decode.DecodeManager;

public final class CaptureView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CaptureView.class.getSimpleName();

    private CaptureHandler mHandler;
    private CameraManager cameraManager;
    private DecodeCallback decodeCallback;
    private boolean hasSurface;
    private BeepManager beepManager;
    //private AmbientLightManager ambientLightManager;

    private DecodeManager decodeManager ;

    public CaptureView(Context context) {
        super(context);
        init(context);
    }

    public CaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mHandler = new CaptureHandler(this);
        hasSurface = false;
        beepManager = new BeepManager(context);
        //ambientLightManager = new AmbientLightManager(context);
        cameraManager = new CameraManager(getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        beepManager.init();
        //ambientLightManager.start(cameraManager);

        SurfaceHolder surfaceHolder = getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
        decodeManager = new DecodeManager(mHandler);
        decodeManager.startDecodeThread();
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeMessages(ZxingConst.DECODE_TIMEOUT);
        decodeManager.quitSafely();
        decodeManager = null;

        //ambientLightManager.stop();
        beepManager.close();
        cameraManager.stopPreview();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onDetachedFromWindow();
    }

    public void setDecodeCallback(DecodeCallback callback) {
        decodeCallback = callback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            cameraManager.startPreview();
            cameraManager.requestPreviewFrame(decodeManager);
            mHandler.sendEmptyMessageDelayed(ZxingConst.DECODE_TIMEOUT, 60 * 1000);

        } catch (Exception e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            onError();
        }
    }

    void onDecode(Result result) {
        mHandler.removeMessages(ZxingConst.DECODE_TIMEOUT);
        beepManager.playBeepSoundAndVibrate();
        if (decodeCallback != null) {
            decodeCallback.onDecode(result);
        }
    }

    void onTimeout() {
        if (decodeCallback != null) {
            decodeCallback.onTimeout();
        }
    }

    void onFail() {
        cameraManager.requestPreviewFrame(decodeManager);
    }

    void onError() {

    }
}
