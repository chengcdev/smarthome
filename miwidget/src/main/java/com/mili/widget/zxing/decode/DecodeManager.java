package com.mili.widget.zxing.decode;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.google.zxing.Result;
import com.mili.widget.zxing.ZxingConst;

public final class DecodeManager implements Decoder {

    private Handler mResultHandler;
    private HandlerThread mThread;
    private Handler mDecodeHandler;

    public DecodeManager(Handler resultHandler) {
        mResultHandler = resultHandler;
        startDecodeThread();
    }

    @Override
    public void decode(byte[] data, int width, int height) {
        Message.obtain(mDecodeHandler, ZxingConst.DECODE, width, height, data).sendToTarget();
    }

    public void startDecodeThread() {
        mThread = new HandlerThread("ZxingDecodeThread");
        mThread.start();
        mDecodeHandler = new DecodeHandler(mThread.getLooper(), this);
    }

    public void quitSafely() {
        mThread.quitSafely();
    }

    void onDecodeSucceeded(Result result, byte[] bitmap, float scaled) {
        Bundle data = new Bundle();
        data.putByteArray(ZxingConst.BARCODE_BITMAP, bitmap);
        data.putFloat(ZxingConst.BARCODE_SCALED_FACTOR, scaled);

        Message msg = Message.obtain(mResultHandler, ZxingConst.DECODE_SUCCEEDED, result);
        msg.setData(data);
        msg.sendToTarget();
    }

    void onDecodeFailed() {
        Message.obtain(mResultHandler, ZxingConst.DECODE_FAILED).sendToTarget();
    }
}
