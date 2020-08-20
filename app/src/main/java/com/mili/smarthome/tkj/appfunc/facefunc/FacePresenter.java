package com.mili.smarthome.tkj.appfunc.facefunc;

import com.mili.smarthome.tkj.face.FaceProtocolInfo;

public interface FacePresenter<T> {

    /**
     * 获取人脸剩余个数
     */
    long getSurplus();

    /**
     * 通过图片注册人脸信息
     * @param imgPath 图片文件路径
     * @param faceId 注册ID
     * @return true-成功，false失败
     */
    boolean enrollFromImage(String imgPath, String faceId);

    /**
     * 通过图片注册人脸信息
     * @param imgPath 图片文件路径
     * @param faceModel 人脸模型
     * @return true-成功，false失败
     */
    boolean enrollFromImage(String imgPath, FaceProtocolInfo faceModel);

    /**
     * 删除faceId对应的人脸信息
     * @param faceId 人脸ID
     * @return true-成功，false失败
     */
    boolean delFaceInfoById(String faceId);

    /**
     * 添加人脸信息
     */
    int addFaceInfo(T faceModel);

    /**
     * 删除卡号关联的人脸信息
     */
    int delFaceInfo(String cardNo);

    /**
     * 清空人脸信息
     */
    boolean clearFaceInfo();

    /**
     * 验证本地数据库firstName是否已注册
     * @return 已注册返回数据库实体，否则返回null
     */
    T verifyFaceId(String faceId);

    /**
     * 减去人脸可用次数
     */
    void subLifecycleInfo(String faceId);
}
