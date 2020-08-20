package com.mili.smarthome.tkj.face.megvii;

import com.mili.smarthome.tkj.face.FaceInfo;
import com.mili.smarthome.tkj.face.FaceInfoAdapter;

import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;

public class MegviiFaceInfoAdapter implements FaceInfoAdapter {

    private int type; // 0本地预览，1流媒体预览
    private byte[] data;
    private int width;
    private int height;
    private boolean mirror;
    private FacePassFace[] faceList;
    private FacePassDetectionResult detectionResult;

    public int getType() {
        return type;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isMirror() {
        return mirror;
    }

    @Override
    public int getFaceCount() {
        return faceList == null ? 0 : faceList.length;
    }

    @Override
    public FaceInfo getFaceInfo(int position) {
        FacePassFace face = faceList[position];
        return new FaceInfo()
                .setTrackId(face.trackId)
                .setLeft(face.rect.left)
                .setTop(face.rect.top)
                .setRight(face.rect.right)
                .setBottom(face.rect.bottom);
    }

    public FacePassDetectionResult getDetectionResult() {
        return detectionResult;
    }

    public MegviiFaceInfoAdapter setType(int type) {
        this.type = type;
        return this;
    }

    public MegviiFaceInfoAdapter setData(byte[] data) {
        this.data = data;
        return this;
    }

    public MegviiFaceInfoAdapter setWidth(int width) {
        this.width = width;
        return this;
    }

    public MegviiFaceInfoAdapter setHeight(int height) {
        this.height = height;
        return this;
    }

    public MegviiFaceInfoAdapter setMirror(boolean mirror) {
        this.mirror = mirror;
        return this;
    }

    public MegviiFaceInfoAdapter setFaceList(FacePassFace[] faceList) {
        this.faceList = faceList;
        return this;
    }

    public MegviiFaceInfoAdapter setDetectionResult(FacePassDetectionResult detectionResult) {
        this.detectionResult = detectionResult;
        if (detectionResult == null) {
            setFaceList(null);
        } else {
            setFaceList(detectionResult.faceList);
        }
        return this;
    }
}
