package com.mili.smarthome.tkj.face.horizon;

import com.mili.smarthome.tkj.face.horizon.bean.FaceRecogResult;

import hobot.sunrise.sdk.jni.FaceModuleResult;

public interface IFaceRecogView {

    void updateOverlay(FaceModuleResult faceModuleResult, int boxSise);

    void faceRecogResult(FaceRecogResult recogResult);
}
