package com.mili.smarthome.tkj.face;

public interface FaceInfoAdapter {

    /**
     * 视频帧数据
     */
    byte[] getData();

    /**
     * 视频帧宽度
     */
    int getWidth();

    /**
     * 视频帧高度
     */
    int getHeight();

    /**
     * 是否镜像
     */
    boolean isMirror();

    /**
     * 人脸个数
     */
    int getFaceCount();

    /**
     * 人脸信息（ID，坐标，相识度等）
     */
    FaceInfo getFaceInfo(int position);
}
