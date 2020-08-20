package com.mili.widget.zxing;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;

final class CaptureHandler extends Handler {

    private CaptureView mCaptureView;

    CaptureHandler(CaptureView captureView) {
        mCaptureView = captureView;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ZxingConst.DECODE_SUCCEEDED:
                mCaptureView.onDecode((Result) msg.obj);
                break;
            case ZxingConst.DECODE_FAILED:
                mCaptureView.onFail();
                break;
            case ZxingConst.DECODE_TIMEOUT:
                mCaptureView.onTimeout();
                break;
        }
    }
}
