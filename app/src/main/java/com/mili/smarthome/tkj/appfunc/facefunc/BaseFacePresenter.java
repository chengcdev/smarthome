package com.mili.smarthome.tkj.appfunc.facefunc;

import com.mili.smarthome.tkj.dao.param.ParamDao;

import java.util.Locale;

public abstract class BaseFacePresenter<T> implements FacePresenter<T> {

    /**
     * 生成住户人脸ID
     */
    public static String genResidentFaceId(String cardNo) {
        return String.format(Locale.getDefault(), "dev-%s-%d", cardNo, System.currentTimeMillis());
    }

    /**
     * 生成访客人脸ID
     */
    public static String genVistorFaceId(String roomNo) {
        return String.format(Locale.getDefault(), "temp-%s-%d", roomNo, System.currentTimeMillis());
    }

    /**
     * 本地补录
     */
    public static boolean isDev(String faceId) {
        return faceId != null && faceId.startsWith("dev-");
    }

    /**
     * PC端下载（住户）
     */
    public static boolean isPC(String faceId) {
        return faceId != null && faceId.startsWith("pc-");
    }

    /**
     * PC端下载（访客）
     */
    public static boolean isTemp(String faceId) {
        return faceId != null && faceId.startsWith("temp-");
    }

    public static String parseCardNo(String faceId) {
        String[] temp = faceId.split("-");
        if (temp.length == 3) {
            return temp[1];
        }
        return "";
    }

    public static String cardIdToString(int cardId) {
        int cardLen = ParamDao.getCardNoLen();
        StringBuilder sb = new StringBuilder();
        sb.append(cardId);
        for (int i = sb.length(); i < cardLen; i++) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }
}
