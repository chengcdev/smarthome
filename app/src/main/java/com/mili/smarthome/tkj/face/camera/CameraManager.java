package com.mili.smarthome.tkj.face.camera;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mili.smarthome.tkj.face.camera.SettingVar;

public class CameraManager implements CameraPreview.CameraPreviewListener {
    protected boolean front = false;

    protected Camera camera = null;

    protected int cameraId = -1;

    protected SurfaceHolder surfaceHolder = null;

    private CameraListener listener = null;

    private CameraPreview cameraPreview;

    private CameraState state = CameraState.IDEL;

    private int previewDegreen = 0;

    private int manualWidth, manualHeight;

    private Camera.Size previewSize = null;

    private byte[] mPicBuffer;

    public CameraManager() {
        super();
    }


    private boolean isSupportedPreviewSize(int width, int height, Camera mCamera) {
        Camera.Parameters camPara = mCamera.getParameters();
        List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
        for (Camera.Size tmpSize : allSupportedSize) {
            Log.i("metrics", "support height" + tmpSize.height + "width " + tmpSize.width);
            if (tmpSize.height == height && tmpSize.width == width)
                return true;
        }
        return false;
    }

    private static Camera.Size getBestPreviewSize(Camera mCamera) {
        Camera.Parameters camPara = mCamera.getParameters();
        List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
        ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
        int max = Integer.MIN_VALUE;
        Camera.Size maxSize = null;
        for (Camera.Size tmpSize : allSupportedSize) {
            int multi = tmpSize.height * tmpSize.width;
            if (multi > max) {
                max = multi;
                maxSize = tmpSize;
            }
            //选分辨率比较高的
            if (tmpSize.width > tmpSize.height && (tmpSize.width > SettingVar.mHeight / 2 || tmpSize.height > SettingVar.mWidth / 2)) {
                widthLargerSize.add(tmpSize);
            }
        }
        if (widthLargerSize.isEmpty()) {
            widthLargerSize.add(maxSize);
        }

        final float propotion = SettingVar.mWidth >= SettingVar.mHeight ? (float) SettingVar.mWidth / (float) SettingVar.mHeight : (float) SettingVar.mHeight / (float) SettingVar.mWidth;

        Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                //                                int off_one = Math.abs(lhs.width * lhs.height - Screen.mWidth * Screen.mHeight);
                //                                int off_two = Math.abs(rhs.width * rhs.height - Screen.mWidth * Screen.mHeight);
                //                                return off_one - off_two;
                //选预览比例跟屏幕比例比较接近的
                float a = getPropotionDiff(lhs, propotion);
                float b = getPropotionDiff(rhs, propotion);
                return (int) ((a - b) * 10000);
            }
        });

        float minPropotionDiff = getPropotionDiff(widthLargerSize.get(0), propotion);
        ArrayList<Camera.Size> validSizes = new ArrayList<>();
        for (int i = 0; i < widthLargerSize.size(); i++) {
            Camera.Size size = widthLargerSize.get(i);
            float propotionDiff = getPropotionDiff(size, propotion);
            if (propotionDiff > minPropotionDiff) {
                break;
            }
            validSizes.add(size);
        }

        Collections.sort(validSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return rhs.width * rhs.height - lhs.width * lhs.height;
            }
        });
        return widthLargerSize.get(0);
    }

    public static float getPropotionDiff(Camera.Size size, float standardPropotion) {
        return Math.abs((float) size.width / (float) size.height - standardPropotion);
    }

    public int getCameraWidth() {
        return manualWidth;
    }

    public int getCameraheight() {
        return manualHeight;
    }

    @SuppressLint("StaticFieldLeak")
    public boolean open(final WindowManager windowManager) {
        if (state != CameraState.OPENING) {
            state = CameraState.OPENING;
            release();
            new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... params) {
                    cameraId = front ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
                    try {
                        camera = Camera.open(cameraId);
                    } catch (Exception e) {
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        int count = Camera.getNumberOfCameras();
                        if (count > 0) {
                            cameraId = 0;
                            camera = Camera.open(cameraId);
                        } else {
                            cameraId = -1;
                            camera = null;
                        }
                    }
                    if (camera != null) {
                        Camera.CameraInfo info = new Camera.CameraInfo();
                        Camera.getCameraInfo(cameraId, info);
                        int rotation = windowManager.getDefaultDisplay().getRotation();
                        int degrees = 0;
                        switch (rotation) {
                            case Surface.ROTATION_0:
                                degrees = 0;
                                break;
                            case Surface.ROTATION_90:
                                degrees = 90;
                                break;
                            case Surface.ROTATION_180:
                                degrees = 180;
                                break;
                            case Surface.ROTATION_270:
                                degrees = 270;
                                break;
                        }
                        int previewRotation;
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            previewRotation = (info.orientation + degrees) % 360;
                            previewRotation = (360 - previewRotation) % 360;  // compensate the mirror
                        } else {  // back-facing
                            previewRotation = (info.orientation - degrees + 360) % 360;
                        }
                        previewRotation = 90;
                        if (SettingVar.isSettingAvailable) {
                            previewRotation = SettingVar.cameraPreviewRotation;
                        }

                        Log.i("CameraManager", String.format("camera rotation: %d %d %d", degrees, info.orientation, previewRotation));
                        camera.setDisplayOrientation(previewRotation);
                        Camera.Parameters param = camera.getParameters();
                        if (manualHeight > 0 && manualWidth > 0 && isSupportedPreviewSize(manualWidth, manualHeight, camera)) {
                            param.setPreviewSize(manualWidth, manualHeight);
                        } else {
                            Camera.Size bestPreviewSize = getBestPreviewSize(camera);
                            Log.i("metrics", "best height is" + bestPreviewSize.height + "width is " + bestPreviewSize.width);
                            manualWidth = bestPreviewSize.width;
                            manualHeight = bestPreviewSize.height;
                            param.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
                            SettingVar.iscameraNeedConfig = true;
                            Log.i("cameraManager", "camerawidth : " + bestPreviewSize.width + "  height  : " + bestPreviewSize.height);
                        }
                        SettingVar.cameraSettingOk = true;
                        param.setPreviewFormat(ImageFormat.NV21);
                        camera.setParameters(param);
                        PixelFormat pixelinfo = new PixelFormat();
                        int pixelformat = camera.getParameters().getPreviewFormat();
                        PixelFormat.getPixelFormatInfo(pixelformat, pixelinfo);
                        Camera.Parameters parameters = camera.getParameters();
                        Camera.Size sz = parameters.getPreviewSize();
                        Log.i("cameraManager", "camerawidth : " + sz.width + "  height  : " + sz.height);
                        int bufSize = sz.width * sz.height * pixelinfo.bitsPerPixel / 8;
                        if (mPicBuffer == null || mPicBuffer.length != bufSize) {
                            mPicBuffer = new byte[bufSize];
                        }
                        camera.addCallbackBuffer(mPicBuffer);
                        previewSize = sz;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    cameraPreview.setCamera(camera);
                    state = CameraState.OPENED;
                }
            }.execute();
            return true;
        } else {
            return false;
        }
    }

    public boolean open(WindowManager windowManager, boolean front) {
        if (state == CameraState.OPENING) {
            return false;
        }
        this.front = front;
        return open(windowManager);
    }

    public boolean open(WindowManager windowManager, boolean front, int width, int height) {
        if (state == CameraState.OPENING) {
            return false;
        }
        this.manualHeight = height;
        this.manualWidth = width;
        this.front = front;
        return open(windowManager);
    }

    public void release() {
        if (camera != null) {
            this.cameraPreview.setCamera(null);
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    public void finalRelease() {
        this.listener = null;
        this.cameraPreview = null;
        this.surfaceHolder = null;
    }

    public void setPreviewDisplay(CameraPreview preview) {
        this.cameraPreview = preview;
        this.surfaceHolder = preview.getHolder();
        preview.setListener(this);
    }

    public void setListener(CameraListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStartPreview() {
        camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (listener != null) {
                    listener.onPictureTaken(
                            new CameraPreviewData(data, previewSize.width, previewSize.height,
                                    previewDegreen, front));
                }
                camera.addCallbackBuffer(data);
            }
        });
    }

    public enum CameraState {
        IDEL,
        OPENING,
        OPENED
    }

    public interface CameraListener {
        void onPictureTaken(CameraPreviewData cameraPreviewData);
    }
}
