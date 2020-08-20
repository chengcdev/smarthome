/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mili.widget.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
public final class ViewfinderView extends View {

    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    /**
     * 四个绿色边角对应的长度
     */
    private int ScreenRate;

    /**
     * 四个绿色边角对应的宽度
     */
    private static final int CORNER_WIDTH = 5;
    /**
     * 扫描框中的中间线的宽度
     */
    private static final int MIDDLE_LINE_WIDTH = 5;

    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    private static final int MIDDLE_LINE_PADDING = 5;

    /**
     * 中间那条线每次刷新移动的距离
     */
    private static final int SPEEN_DISTANCE = 5;

    /**
     * 画笔对象的引用
     */
    private Paint paint = new Paint();;

    /**
     * 中间滑动线的最顶端位置
     */
    private int slideTop;

    /**
     * 中间滑动线的最底端位置
     */
    private int slideBottom;

    /**
     * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
     */
    private Bitmap resultBitmap;

    /**
     * 扫描框
     */
    private Rect scanFrame = new Rect();

    private final int maskColor = 0x80000000;
    private final int cornerColor = 0xFF00FF00;
    private final int resultColor = 0xB0000000;

    private boolean isFirst;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        float density = context.getResources().getDisplayMetrics().density;
        //将像素转换成dp
        ScreenRate = (int) (36 * density);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //获取屏幕的宽和高
        int width = getWidth();
        int height = getHeight();

        //扫码框
        int frameSide = Math.min(width, height) * 6 / 8;
        int left = (width - frameSide) / 2;
        int top = (height - frameSide) / 2;
        int right = left + frameSide;
        int bottom = top + frameSide;
        scanFrame.set(left, top, right, bottom);

        //初始化中间线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            slideTop = scanFrame.top;
            slideBottom = scanFrame.bottom;
        }

        paint.setColor(resultBitmap != null ? resultColor : maskColor);

        //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        canvas.drawRect(0, 0, width, scanFrame.top, paint);
        canvas.drawRect(0, scanFrame.top, scanFrame.left, scanFrame.bottom + 1, paint);
        canvas.drawRect(scanFrame.right + 1, scanFrame.top, width, scanFrame.bottom + 1,
                paint);
        canvas.drawRect(0, scanFrame.bottom + 1, width, height, paint);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, scanFrame.left, scanFrame.top, paint);
        } else {

            scanFrame.set(left, top, right + 1, bottom + 1);
            //画扫描框边上的角，总共8个部分
            paint.setColor(cornerColor);
            canvas.drawRect(scanFrame.left, scanFrame.top, scanFrame.left + ScreenRate,
                    scanFrame.top + CORNER_WIDTH, paint);
            canvas.drawRect(scanFrame.left, scanFrame.top, scanFrame.left + CORNER_WIDTH, scanFrame.top
                    + ScreenRate, paint);
            canvas.drawRect(scanFrame.right - ScreenRate, scanFrame.top, scanFrame.right,
                    scanFrame.top + CORNER_WIDTH, paint);
            canvas.drawRect(scanFrame.right - CORNER_WIDTH, scanFrame.top, scanFrame.right, scanFrame.top
                    + ScreenRate, paint);
            canvas.drawRect(scanFrame.left, scanFrame.bottom - CORNER_WIDTH, scanFrame.left
                    + ScreenRate, scanFrame.bottom, paint);
            canvas.drawRect(scanFrame.left, scanFrame.bottom - ScreenRate,
                    scanFrame.left + CORNER_WIDTH, scanFrame.bottom, paint);
            canvas.drawRect(scanFrame.right - ScreenRate, scanFrame.bottom - CORNER_WIDTH,
                    scanFrame.right, scanFrame.bottom, paint);
            canvas.drawRect(scanFrame.right - CORNER_WIDTH, scanFrame.bottom - ScreenRate,
                    scanFrame.right, scanFrame.bottom, paint);
            scanFrame.set(left, top, right, bottom);


            //绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
            slideTop += SPEEN_DISTANCE;
            if (slideTop >= scanFrame.bottom) {
                slideTop = scanFrame.top;
            }
            canvas.drawRect(
                    scanFrame.left + MIDDLE_LINE_PADDING,
                    slideTop - MIDDLE_LINE_WIDTH / 2,
                    scanFrame.right - MIDDLE_LINE_PADDING,
                    slideTop + MIDDLE_LINE_WIDTH / 2, paint);

            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, scanFrame.left, scanFrame.top,
                    scanFrame.right, scanFrame.bottom);

        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

}
