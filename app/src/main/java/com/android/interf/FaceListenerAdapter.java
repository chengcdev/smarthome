package com.android.interf;

import com.mili.smarthome.tkj.face.FaceProtocolInfo;

public abstract class FaceListenerAdapter<T> implements IFaceListener<T> {

    @Override
    public void onFaceLicense(int faceLicense, int faceType) {

    }

    @Override
    public boolean onFaceEnroll(String imgPath, String faceId) {
        return false;
    }

    @Override
    public boolean onFaceEnroll(String imgPath, FaceProtocolInfo faceProtocolInfo) {
        return false;
    }

    @Override
    public boolean onFaceDelete(String faceId) {
        return false;
    }
}
