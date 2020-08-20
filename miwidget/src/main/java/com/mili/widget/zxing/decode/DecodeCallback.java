package com.mili.widget.zxing.decode;

import com.google.zxing.Result;

public interface DecodeCallback {

    //void onDecode(Result result, byte[] bitmap, float scaled);
    void onDecode(Result result);

    void onTimeout();
}
