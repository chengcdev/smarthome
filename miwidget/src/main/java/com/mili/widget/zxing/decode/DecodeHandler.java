package com.mili.widget.zxing.decode;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.mili.widget.zxing.ZxingConst;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private DecodeManager mDecodeManager;
    private final MultiFormatReader mMultiFormatReader;

    DecodeHandler(Looper looper, DecodeManager decodeManager) {
        super(looper);
        mDecodeManager = decodeManager;

        Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ZxingConst.DECODE:
                decode((byte[]) msg.obj, msg.arg1, msg.arg2);
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        // 解码区域，对应扫码框
        int frameSide = Math.min(width, height) * 6 / 8;
        int left = (width - frameSide) / 2;
        int top = (height - frameSide) / 2;

        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                data, width, height, left, top, frameSide, frameSide, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = mMultiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            mMultiFormatReader.reset();
        }

        if (rawResult != null) {
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");

            int[] pixels = source.renderThumbnail();
            Bitmap resultBitmap = Bitmap.createBitmap(pixels, 0, source.getThumbnailWidth(), source.getThumbnailWidth(), source.getThumbnailHeight(), Bitmap.Config.ARGB_8888);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            float scaled = (float) source.getThumbnailWidth() / source.getWidth();

            mDecodeManager.onDecodeSucceeded(rawResult, outputStream.toByteArray(), scaled);
        } else {
            mDecodeManager.onDecodeFailed();
        }
    }
}
