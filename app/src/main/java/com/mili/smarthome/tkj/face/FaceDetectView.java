package com.mili.smarthome.tkj.face;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.utils.LogUtils;

public class FaceDetectView extends View {

    private int faceManufacturer;
    private float recognitionThreshold;
    private boolean enroll;
    private FaceInfoAdapter faceInfoAdapter;
    private Paint mPaint;
    private Matrix mMatrix = new Matrix();
    private RectF dstRect = new RectF();
    private RectF srcRect = new RectF();

    public FaceDetectView(Context context) {
        this(context, null, 0);
    }

    public FaceDetectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceDetectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        faceManufacturer = AppConfig.getInstance().getFaceManufacturer();
        mPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    public void setRecognitionThreshold(float recognitionThreshold) {
        this.recognitionThreshold = recognitionThreshold;
    }

    public void setEnrolling(boolean enroll) {
        this.enroll = enroll;
    }

    public void setFaceInfoAdapter(FaceInfoAdapter faceInfoAdapter) {
        this.faceInfoAdapter = faceInfoAdapter;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long startTime = System.currentTimeMillis();
        if (faceInfoAdapter == null || faceInfoAdapter.getFaceCount() == 0)
            return;

        canvas.save();

        float scaleX = (float) getWidth() / (float) faceInfoAdapter.getWidth();
        float scaleY = (float) getHeight() / (float) faceInfoAdapter.getHeight();
        mMatrix.reset();
        if (faceInfoAdapter.isMirror()) {
            mMatrix.setScale(-1, 1);
            mMatrix.postTranslate(faceInfoAdapter.getWidth(), 0f);
        }
        mMatrix.postScale(scaleX, scaleY);

        int faceCount = faceInfoAdapter.getFaceCount();
        for (int position = 0; position < faceCount; position++) {
            FaceInfo faceInfo = faceInfoAdapter.getFaceInfo(position);
            if (faceManufacturer == 0) {
                if (enroll) {
                    mPaint.setColor(Color.BLUE);
                } else if (faceInfo.getSimilar() > recognitionThreshold) {
                    mPaint.setColor(Color.GREEN);
                } else {
                    mPaint.setColor(Color.RED);
                }
            } else {
                mPaint.setColor(Color.WHITE);
            }

            srcRect.set(faceInfo.getLeft(), faceInfo.getTop(), faceInfo.getRight(), faceInfo.getBottom());
            mMatrix.mapRect(dstRect, srcRect);
            int left = (int) dstRect.left;
            int top = (int) dstRect.top;
            int right = (int) dstRect.right;
            int bottom = (int) dstRect.bottom;
            drawDetect(left, top, right, bottom, canvas);
        }
        canvas.restore();
        LogUtils.d("FaceDetectView--->>>onDraw: spend %dms", (System.currentTimeMillis() - startTime));
    }

    private void drawDetect(int left, int top, int right, int bottom, Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3.0f);
        mPaint.setAlpha(255);
        //
        int lineEnd = (right - left) / 5;
        //Left
        canvas.drawLine(left, top, left + lineEnd, top, mPaint);
        canvas.drawLine(left, top, left, top + lineEnd, mPaint);
        //Right
        canvas.drawLine(right - lineEnd, top, right, top, mPaint);
        canvas.drawLine(right, top, right, top + lineEnd, mPaint);
        //BottomLeft
        canvas.drawLine(left, bottom, left + lineEnd, bottom, mPaint);
        canvas.drawLine(left, bottom - lineEnd, left, bottom, mPaint);
        //BottomRight
        canvas.drawLine(right - lineEnd, bottom, right, bottom, mPaint);
        canvas.drawLine(right, bottom - lineEnd, right, bottom, mPaint);
    }

}

