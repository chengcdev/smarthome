package com.mili.widget.zxing.encode;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class QRCodeEncoder {

    /**
     * 生成二维码图片
     * @param contents 二维码字符串
     * @param dimension 二维码图片尺寸
     * @return 二维码图片
     */
    public static Bitmap encodeAsBitmap(String contents, int dimension, int margin) {
        if (contents == null) {
            return null;
        }
        if (margin < 0) {
            margin = 0;
        }
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, margin);
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, dimension, dimension, hints);
        } catch (Exception ex) {
            // Unsupported format
            ex.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
