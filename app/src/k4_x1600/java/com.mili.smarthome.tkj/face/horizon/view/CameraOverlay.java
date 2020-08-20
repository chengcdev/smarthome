package com.mili.smarthome.tkj.face.horizon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.face.horizon.FaceRecogResultCache;
import com.mili.smarthome.tkj.face.horizon.bean.FaceParseInfo;
import com.mili.smarthome.tkj.face.horizon.bean.FaceRecogResult;
import com.mili.smarthome.tkj.face.horizon.bean.Size;
import com.mili.smarthome.tkj.face.horizon.util.CameraDisplayUtil;
import com.mili.smarthome.tkj.face.horizon.util.HorizonPreferences;

import java.util.ArrayList;
import java.util.List;

import hobot.sunrise.sdk.jni.FaceModuleResult;

public class CameraOverlay extends View {

    private final String TAG = CameraOverlay.class.getName();
    private final int textSize;
    private final int lineWidth;
    private boolean isMirror; // 框位置是否镜像
    private boolean isConvertPicture = false;//是否颠倒显示图像
    private Paint eraser;
    private Paint mPaint = new Paint();
    private Size frameSize;
    private double xScale;
    private double yScale;
    private int width;
    private int height;
    private List<FaceParseInfo> faceParseInfos = new ArrayList<>();
    private boolean needCleanCanvas;
    private boolean CanvasCleaned = false;

    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.d(TAG, "screen_width = " + metrics.widthPixels + " | screen_height = " + metrics.heightPixels);

        // get attrConfiguration
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CameraOverlay);
        textSize = array.getDimensionPixelSize(R.styleable.CameraOverlay_textSize, 24);
        lineWidth = array.getDimensionPixelSize(R.styleable.CameraOverlay_lineWidth, 2);
        isMirror = array.getBoolean(R.styleable.CameraOverlay_mirror, false);
        array.recycle();

        eraser = new Paint();
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setColor(Color.WHITE);
        eraser.setStyle(Paint.Style.FILL);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setTextSize(textSize);
        mPaint.setTypeface(Typeface.SANS_SERIF);
        setWillNotDraw(false);

    }

    public void updateShapes(final FaceModuleResult result, final int boxSize) {
        //有的场景下,会出现一帧有shape,随后又为空的情况,ondraw来不及的话,shape一直显示不出来。
        //此处逻辑是,连续n(n暂定为3)帧为空的情况下,才draw空frame。
        boolean isCurrentFrameEmpty = (result == null);
        if (isCurrentFrameEmpty && !CanvasCleaned) {
            needCleanCanvas = true;
            CanvasCleaned = true;
            faceParseInfos.clear();
        } else if (!isCurrentFrameEmpty) {
            if (result.face_result != null) {
                if (result.face_result.track != null) {
                    int box_size;
                    for (int i = 0; i < result.face_result.track.count; i++) {
                        FaceParseInfo faceParseInfo = new FaceParseInfo();
                        box_size = (result.face_result.track.result[i].rect.right -
                                result.face_result.track.result[i].rect.left) * (result.face_result.track.result[i]
                                .rect.bottom - result.face_result.track.result[i].rect.top);
                        if (box_size > boxSize) {
                            faceParseInfo.top = result.face_result.track.result[i].rect.top;
                            faceParseInfo.left = result.face_result.track.result[i].rect.left;
                            faceParseInfo.right = result.face_result.track.result[i].rect.right;
                            faceParseInfo.bottom = result.face_result.track.result[i].rect.bottom;
                            faceParseInfo.trackId = result.face_result.track.result[i].id;
                            faceParseInfos.add(faceParseInfo);
                            CanvasCleaned = false;
                        }
                    }
                }
            }
        } else {
            return;
        }
        postInvalidate();
    }

    public void updateFrameInputSize(int width, int height) {
        Log.i(TAG, "updateFrameInputSize width =" + width + "height = " + height);
        this.frameSize = new Size(width, height);
        updateScaleValue();
    }

    private void updateScaleValue() {
        width = getWidth();
        height = getHeight();

        if (frameSize == null) {
            xScale = 1.0f;
            yScale = 1.0f;
            Log.d(TAG, "frameSize is null");
            frameSize = new Size(width, height);
        } else {
            xScale = width * 1.0f / frameSize.width;
            yScale = height * 1.0f / frameSize.height;
            //Log.d(TAG, "frameSize.width = " + frameSize.width + " | frameSize.height = " + frameSize.height);
        }
        //Log.d(TAG, "width = " + width + " | height = " + height + " | xScale = " + xScale + " | yScale = " + yScale);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        Log.d(TAG, "onLayout() called with: "
//                + "changed = [" + changed + "], left = ["
//                + left + "], top = [" + top + "], right = ["
//                + right + "], bottom = [" + bottom + "]");
        updateScaleValue();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(TAG, "onDraw in this.frameSize = " + this.frameSize);
        super.onDraw(canvas);
        if (!HorizonPreferences.getDrawRectOpen()) {
            return;
        }
        if (this.frameSize == null) {
            return;
        }
        if (needCleanCanvas) {
            cleanCanvas(canvas);
            needCleanCanvas = false;
        }
        //Log.i(TAG, "faceParseInfos =" + faceParseInfos);
        if (faceParseInfos != null && faceParseInfos.size() > 0) {
            boolean faceLiveCheck = AppConfig.getInstance().getFaceLiveCheck() == 1;
            int faceThr = HorizonPreferences.getFaceThr();
            for (FaceParseInfo faceParseInfo : faceParseInfos) {
                FaceRecogResult recogResult = FaceRecogResultCache.get(faceParseInfo.trackId);
                int color = Color.RED;//检测到人脸
                if (recogResult != null) {
                    if (faceLiveCheck && recogResult.getLiveness() > 0) {
                        color = Color.YELLOW;//活体
                    }
                    if (recogResult.getSimilar() >= faceThr) {
                        color = Color.GREEN;//识别成功
                    }
                }
                if (mPaint.getColor() != color) {
                    mPaint.setColor(color);
                }
                RectF rectF = getScaledRectMipi(faceParseInfo.left, faceParseInfo.top,
                        faceParseInfo.right, faceParseInfo.bottom);
                final int lineLen = (int) (rectF.right - rectF.left) / 8;
                //canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), rectF, mPaint);
                //canvas.drawRoundRect(rectF, 10, 10, mPaint);
                float[] lines = {rectF.left, rectF.top, rectF.left + lineLen, rectF.top,
                        rectF.left, rectF.top, rectF.left, rectF.top + lineLen,
                        rectF.right, rectF.top, rectF.right - lineLen, rectF.top,
                        rectF.right, rectF.top, rectF.right, rectF.top + lineLen,
                        rectF.left, rectF.bottom, rectF.left + lineLen, rectF.bottom,
                        rectF.left, rectF.bottom, rectF.left, rectF.bottom - lineLen,
                        rectF.right, rectF.bottom, rectF.right - lineLen, rectF.bottom,
                        rectF.right, rectF.bottom, rectF.right, rectF.bottom - lineLen
                };
                canvas.drawLines(lines, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAlpha(100);
                mPaint.setStyle(Paint.Style.STROKE);
                rectF = null;
            }
        }
        faceParseInfos.clear();
        //Log.d(TAG, "onDraw out this.frameSize = " + this.frameSize);
    }

    private double getScaledX(double x) {
        return (x * xScale);
    }

    private double getScaledY(double y) {
        return (y * yScale);
    }

    private RectF getScaledRect(RectF rect) {
        if (isMirror) {
            if (isConvertPicture) {
                return new RectF((float) getScaledX(frameSize.width - rect.right),
                        (float) getScaledY(frameSize.height - rect.bottom),
                        (float) getScaledX(frameSize.width - rect.left), // rect.right
                        (float) getScaledY(frameSize.height - rect.top));
            } else {
                return new RectF((float) getScaledX(frameSize.width - rect.right),
                        (float) getScaledY(rect.top),
                        (float) getScaledX(frameSize.width - rect.left), // rect.right
                        (float) getScaledY(rect.bottom));
            }
        } else {
            if (isConvertPicture) {
                return new RectF((int) getScaledX(rect.left),
                        (int) getScaledY(frameSize.height - rect.bottom),
                        (int) getScaledX(rect.right),
                        (int) getScaledY(frameSize.height - rect.top));
            } else {
                return new RectF((int) getScaledX(rect.left),
                        (int) getScaledY(rect.top),
                        (int) getScaledX(rect.right),
                        (int) getScaledY(rect.bottom));
            }

        }

    }

    private RectF getScaledRectMipi(float left, float top, float right, float bottom) {
        return new RectF(left * HRXMIPICam.mWidth / CameraDisplayUtil.getCameraWidth(),
                top * HRXMIPICam.mHeight / CameraDisplayUtil.getCameraHeight(),
                right * HRXMIPICam.mWidth / CameraDisplayUtil.getCameraWidth(),
                bottom * HRXMIPICam.mHeight / CameraDisplayUtil.getCameraHeight());
    }

    private RectF getScaledRect(float left, float top, float right, float bottom) {
        if (isMirror) {
            if (isConvertPicture) {
                return new RectF((float) getScaledX(frameSize.width - right),
                        (float) getScaledY(frameSize.height - bottom),
                        (float) getScaledX(frameSize.width - left), // rect.right
                        (float) getScaledY(frameSize.height - top));
            } else {
                return new RectF((float) getScaledX(frameSize.width - right),
                        (float) getScaledY(top),
                        (float) getScaledX(frameSize.width - left), // rect.right
                        (float) getScaledY(bottom));
            }
        } else {
            if (isConvertPicture) {
                return new RectF((int) getScaledX(left),
                        (int) getScaledY(frameSize.height - bottom),
                        (int) getScaledX(right),
                        (int) getScaledY(frameSize.height - top));
            } else {
                return new RectF((int) getScaledX(left),
                        (int) getScaledY(top),
                        (int) getScaledX(right),
                        (int) getScaledY(bottom));
            }

        }

    }

    private void cleanCanvas(Canvas canvas) {
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    public boolean isMirror() {
        return isMirror;
    }

    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    public boolean isConvertPicture() {
        return isConvertPicture;
    }

    public void setConvertPicture(boolean convertPicture) {
        isConvertPicture = convertPicture;
    }

}
