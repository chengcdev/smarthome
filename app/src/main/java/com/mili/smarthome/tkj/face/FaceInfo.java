package com.mili.smarthome.tkj.face;

/**
 * 人脸信息（ID，坐标，相识度等）
 */
public class FaceInfo {

    private long trackId;
    private String faceId;

    private int left;
    private int top;
    private int right;
    private int bottom;

    private float similar;//相似度
    private float liveness = 0;//活体值

    public long getTrackId() {
        return trackId;
    }

    public String getFaceId() {
        return faceId;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public float getSimilar() {
        return similar;
    }

    public float getLiveness() {
        return liveness;
    }

    public FaceInfo setTrackId(long trackId) {
        this.trackId = trackId;
        return this;
    }

    public FaceInfo setFaceId(String faceId) {
        this.faceId = faceId;
        return this;
    }

    public FaceInfo setLeft(int left) {
        this.left = left;
        return this;
    }

    public FaceInfo setTop(int top) {
        this.top = top;
        return this;
    }

    public FaceInfo setRight(int right) {
        this.right = right;
        return this;
    }

    public FaceInfo setBottom(int bottom) {
        this.bottom = bottom;
        return this;
    }

    public FaceInfo setSimilar(float similar) {
        this.similar = similar;
        return this;
    }

    public FaceInfo setLiveness(float liveness) {
        this.liveness = liveness;
        return this;
    }
}
