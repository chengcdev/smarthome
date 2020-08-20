package com.mili.smarthome.tkj.face.horizon.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.hobot.hrxcam.HRXCamera;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.face.horizon.HorizonConst;
import com.mili.smarthome.tkj.face.horizon.util.CameraDisplayUtil;
import com.mili.smarthome.tkj.face.horizon.util.FlashSpaceCheck;
import com.mili.smarthome.tkj.face.horizon.util.SunriseSdkUtil;

import java.io.FileOutputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class HRXMIPICam extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = HRXMIPICam.class.getSimpleName();

    protected volatile boolean mFrameAvailable = false;

    private static final int SDCARD_WRITE_THR = 300;
    private static int pictureStoreCount = 0;
    private HRXCamera mCamera;
    private boolean mShouldRender = false;
    static int mWidth = 608;
    static int mHeight = 1080;
    private int mGetVideoData = 0;
    private boolean flashFull = false;
    private int CpResetCount = -1;

    public HRXMIPICam(Context context) {
        this(context, null);
    }

    public HRXMIPICam(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mCamera = new HRXCamera();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    protected int OnEvent(int event, int val) {
        //Log.d(TAG, "OnEvent in");
        mFrameAvailable = true;
        // 如果你需要每一帧都回调
        if (mGetVideoData == 1) {
            HRXCamera.GetFrame(HRXCamera.GetMIPIContext(), -1);
        }
        if (event == HRXCamera.CAM_EVENT_TS_ERROR && (CpResetCount == -1) && ((val & 0x03) == 0x03)) {
            CpResetCount = 20;
            SunriseSdkUtil.resetCp();
            Log.d(TAG, "Camera frame is repeat continue 3 times");
        }
        if (event == HRXCamera.CAM_EVENT_NEW_FRAME) {
            if (CpResetCount != -1) {
                Log.d(TAG, "CpResetCount = " + CpResetCount);
                CpResetCount--;
                if (CpResetCount == -1) {
                    Log.d(TAG, "need set cp param");
                    SunriseSdkUtil.config();
                }
            }
        }
        if (mShouldRender) requestRender();
        //Log.d(TAG, "OnEvent out");
        return 0;
    }

    protected int OnData(int cmd, int index, byte[] data, int len) {
        if (cmd == HRXCamera.CAM_CMD_TAKE_PHOTO) {
            // save the jpeg file
            // 请注意在系统设置里面手动打开访问存储设备的权限
            try {
                String path = Const.Directory.TEMP + "/takephoto.jpg";
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                fileOutputStream.write(data, 0, len);
                fileOutputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "OnData: " + e.getLocalizedMessage());
            }
        }
        return 0;
    }

    int getCameraCount() {
        return HRXCamera.GetCameraCount();
    }

    public int openCamera(int bufferCount) {
        Log.d(TAG, "openCamera in mCamera = " + mCamera);
        int ret = 0;
        synchronized (TAG) {
            mCamera.Init(HRXCamera.GetMIPIContext()); // 初始化，一般只需要调一次，不需要每次开camera都调用
            HRXCamera.CamSetMaxFrameBufferSize(HRXCamera.GetMIPIContext(),
                    CameraDisplayUtil.getCameraWidth() * CameraDisplayUtil.getCameraHeight() * 3 / 2);
            ret = HRXCamera.Open(HRXCamera.GetMIPIContext(), CameraDisplayUtil.getCameraWidth(),
                    CameraDisplayUtil.getCameraHeight(), bufferCount);
            //打开摄像头，要抓拍老的图片bufferCount就要设置为比如30. 建议最大30
            Log.d(HorizonConst.TAG, "openCamera ret = " + ret);
            if (ret <= 0) {
                return -1;
            }
            HRXCamera.CamSetJpegParam(HRXCamera.GetMIPIContext(), 95, 0); // 设置抓拍的压缩质量和exif中的旋转方向
            HRXCamera.CamSetOnEvent(HRXCamera.GetMIPIContext(), this, "OnEvent"); // 设置事件回调，比如要显示画面，作为新视频帧的通知
            HRXCamera.CamSetOnDataOut(HRXCamera.GetMIPIContext(), this, "OnData"); // 设置数据回调， 内存抓图，视频帧数据回调都是通过这个接口返回数据
        }
        Log.d(TAG, "openCamera out mCamera = " + mCamera);
        return 0;
    }

    public int startCamera(boolean highPriority) {
        Log.d(TAG, "startCamera in mCamera = " + mCamera);
        int ret = 0;
        synchronized (TAG) {
            ret = HRXCamera.Start(HRXCamera.GetMIPIContext()); // 开始工作
            Log.d(TAG, "startCamera ret = " + ret);
            if (ret <= 0) {
                return -2;
            }
            HRXCamera.SetHighPriority(HRXCamera.GetMIPIContext(), highPriority); // 设置高优先级
            HRXCamera.CamSetOption(HRXCamera.GetMIPIContext(), HRXCamera.CAMERA_OPTION_TAKE_NEAREST, 1);
            mShouldRender = true;
            //mGetVideoData = 1;
        }
        Log.d(TAG, "startCamera out mCamera = " + mCamera);
        return 0;
    }

    int takePhoto(int index, int left, int top, int width, int height, int targetWidth, int targetHeight) {
        return HRXCamera.CamTakePhoto(HRXCamera.GetMIPIContext(), index,
                left, top, width, height,
                targetWidth, targetHeight);
    }

    public int savePhoto(int index, int left, int top, int width, int height, int targetWidth, int targetHeight, String fnJpeg) {
        int ret = -1;
        synchronized (TAG) {
            if (!mShouldRender) {
                return ret;
            }
            pictureStoreCount++;
            if (pictureStoreCount == 10) {
                pictureStoreCount = 0;
                if (FlashSpaceCheck.getAvailableSize(Const.Directory.SD) / (1024 * 1024) <= SDCARD_WRITE_THR) {
                    flashFull = true;
                    Log.d(HorizonConst.TAG, "savePhoto: flash is full");
                    return -1000;
                } else {
                    flashFull = false;
                }
            }
            if (flashFull) {
                ret = -1000;
            } else {
                ret = HRXCamera.CamSavePhoto(HRXCamera.GetMIPIContext(), index,
                        left, top, width, height, targetWidth, targetHeight, fnJpeg);
            }
        }
        Log.d(HorizonConst.TAG, "savePhoto: mShouldRender=" + mShouldRender + ", index=" + index + ", ret=" + ret);
        return ret;
    }

    public int stopCamera() {
        Log.d(TAG, "stopCamera in");
        synchronized (TAG) {
            Log.d(TAG, "stopCamera in mCamera = " + mCamera);
            //mGetVideoData = 0;
            mShouldRender = false;
            HRXCamera.Close(HRXCamera.GetMIPIContext()); // 关闭摄像头
            HRXCamera.Release(HRXCamera.GetMIPIContext()); // 销毁，一般只需要调一次，和Init配对。不需要每次关闭都调用
            Log.d(TAG, "stopCamera out mCamera = " + mCamera);
        }
        return 0;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("ogl", "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Log.d(TAG, "onSurfaceChanged w" + width + " h " + height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.d(TAG, "onDrawFrame in");
        if (mFrameAvailable) {
            mFrameAvailable = false;
            HRXCamera.DrawFrame(HRXCamera.GetMIPIContext());
        }
        //Log.d(TAG, "onDrawFrame out");
    }

    @Override
    public void onPause() {
        mShouldRender = false;
        Log.d(TAG, "on pause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "on resume in mCamera = " + mCamera);
        super.onResume();
        HRXCamera.CamSetOnEvent(HRXCamera.GetMIPIContext(), this, "OnEvent"); // 设置事件回调，比如要显示画面，作为新视频帧的通知
        HRXCamera.CamSetOnDataOut(HRXCamera.GetMIPIContext(), this, "OnData"); // 设置数据回调， 内存抓图，视频帧数据回调都是通过这个接口返回数据
        HRXCamera.UpdateView(HRXCamera.GetMIPIContext(), mWidth, mHeight);
        mShouldRender = true;
        Log.d(TAG, "on resume out mCamera = " + mCamera + " verson = " + HRXCamera.GetVersionName());
    }

    public void setCameraFrameRate(int rate) {
        Log.d(TAG, "setCameraFrameRate rate = " + rate);
        HRXCamera.CamSetFPS(HRXCamera.GetMIPIContext(), rate);
    }
}
