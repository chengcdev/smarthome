package com.mili.widget.zxing.decode;

public interface Decoder {

    void decode(byte[] data, int width, int height);
}
