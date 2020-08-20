package com.mili.smarthome.tkj.face.megvii.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;

import mcv.facepass.types.FacePassImage;

public class FacePassImageUtils {

    public static Bitmap decode(FacePassImage image) {
        Rect rect = new Rect(0, 0, image.width, image.height);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(image.image, ImageFormat.NV21, image.width, image.height, null);
        yuvImage.compressToJpeg(rect, 95, outputStream);
        byte[] jpgBytes = outputStream.toByteArray();
        return BitmapFactory.decodeByteArray(jpgBytes, 0, jpgBytes.length);
    }
}
