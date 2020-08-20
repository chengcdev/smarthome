package com.mili.smarthome.tkj.face.wffr;

import com.mili.smarthome.tkj.face.FaceInfo;
import com.mili.smarthome.tkj.face.FaceInfoAdapter;

import java.util.List;

public class WffrFaceInfoAdapter implements FaceInfoAdapter {

    private byte[] data;
    private int width;
    private int height;
    private boolean mirror;
    private List<FaceInfo> faceList;

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
        return faceList == null ? 0 : faceList.size();
    }

    @Override
    public FaceInfo getFaceInfo(int position) {
        return faceList.get(position);
    }

    public WffrFaceInfoAdapter setData(byte[] data) {
        this.data = data;
        return this;
    }

    public WffrFaceInfoAdapter setWidth(int width) {
        this.width = width;
        return this;
    }

    public WffrFaceInfoAdapter setHeight(int height) {
        this.height = height;
        return this;
    }

    public WffrFaceInfoAdapter setMirror(boolean mirror) {
        this.mirror = mirror;
        return this;
    }

    public WffrFaceInfoAdapter setFaceList(List<FaceInfo> faceList) {
        this.faceList = faceList;
        return this;
    }
}
