package com.mili.smarthome.tkj.face.horizon.bean;

public class FaceRecogResult {

    private int trackId;        //标识值
    private String faceId;      //人脸ID
    private int similar = 0;    //相似度
    private int liveness = 0;   //活体值，-99空间已满，-11或-42正在初始化，0为假体
    private String snapPath = "";

    public int getTrackId() {
        return trackId;
    }

    public String getFaceId() {
        return faceId;
    }

    public int getSimilar() {
        return similar;
    }

    public int getLiveness() {
        return liveness;
    }

    public String getSnapPath() {
        return snapPath;
    }

    public FaceRecogResult setTrackId(int trackId) {
        this.trackId = trackId;
        return this;
    }

    public FaceRecogResult setFaceId(String faceId) {
        this.faceId = faceId;
        return this;
    }

    public FaceRecogResult setSimilar(int similar) {
        this.similar = similar;
        return this;
    }

    public FaceRecogResult setLiveness(int liveness) {
        this.liveness = liveness;
        return this;
    }

    public FaceRecogResult setSnapPath(String snapPath) {
        this.snapPath = snapPath;
        return this;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("FaceRecogResult@").append(Integer.toHexString(hashCode()))
                .append(": trackId=").append(trackId)
                .append(", faceId=").append(faceId)
                .append(", similar=").append(similar)
                .append(", liveness=").append(liveness)
                .append(", snapPath=").append(snapPath)
                .toString();
    }
}
