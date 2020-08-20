package com.android.interf;

import com.mili.smarthome.tkj.face.FaceProtocolInfo;

public interface IFaceListener<T> {

    /**
     * 人脸授权命令的回调
     * @param faceLicense 1-请求人脸授权
     * @param faceType 1EI; 2Face++
     */
    void onFaceLicense(int faceLicense, int faceType);

    /**
     * 通过图片注册人脸信息的回调
     * @param imgPath 图片文件路径
     * @param faceId 注册ID
     * @return true-成功，false失败
     */
    boolean onFaceEnroll(String imgPath, String faceId);

    /**
     * 通过图片注册人脸信息的回调
     * @param imgPath 图片文件路径
     * @param faceProtocolInfo 人脸模型
     * @return true-成功，false失败
     */
    boolean onFaceEnroll(String imgPath, FaceProtocolInfo faceProtocolInfo);

    /**
     * 删除faceId对应的人脸信息的回调
     * @param faceId 人脸ID
     * @return true-成功，false失败
     */
    boolean onFaceDelete(String faceId);
}
