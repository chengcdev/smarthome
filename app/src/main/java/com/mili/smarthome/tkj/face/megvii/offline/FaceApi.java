package com.mili.smarthome.tkj.face.megvii.offline;

import android.graphics.Bitmap;

import com.mili.smarthome.tkj.face.megvii.MegviiFace;
import com.mili.smarthome.tkj.utils.LogUtils;

import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassConfig;
import mcv.facepass.types.FacePassRecognitionResult;

public class FaceApi {

    /* 分组名称 */
    public static final String GROUP_NAME = "facepass";

    /**
     * 检查分组是否存在
     */
    public static boolean checkGroup() {
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return false;
            }
            String[] localGroups = MegviiFace.getInstance().mFacePassHandler.getLocalGroups();
            if (localGroups == null || localGroups.length == 0) {
                return false;
            }
            for (String group : localGroups) {
                if (group.equals(GROUP_NAME)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>checkGroup=" + ex.toString());
        }
        return false;
    }

    /**
     * 创建分组
     */
    public static boolean createGroup() {
        boolean isLocalGroupCreate = false;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return isLocalGroupCreate;
            }
            isLocalGroupCreate = MegviiFace.getInstance().mFacePassHandler.createLocalGroup(GROUP_NAME);
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>createGroup=" + ex.toString());
        }
        return isLocalGroupCreate;
    }

    /**
     * 删除分组
     */
    public static boolean deleteGroup() {
        boolean isLocalGroupDelete = false;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return isLocalGroupDelete;
            }
            isLocalGroupDelete = !checkGroup() || MegviiFace.getInstance().mFacePassHandler.deleteLocalGroup(GROUP_NAME);
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>deleteGroup=" + ex.toString());
        }
        return isLocalGroupDelete;
    }

    /**
     * 查询分组列表
     */
    public static String[] getLocalGroup() {
        String[] groups = null;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return groups;
            }
            groups = MegviiFace.getInstance().mFacePassHandler.getLocalGroups();
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>getLocalGroup=" + ex.toString());
        }
        return groups;
    }

    /**
     * 查询分组人脸信息
     */
    public static String[] getLocalGroupInfo() {
        String[] faceToken = null;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return faceToken;
            }
            byte[][] faceTokens = MegviiFace.getInstance().mFacePassHandler.getLocalGroupInfo(GROUP_NAME);
            if (faceTokens != null && faceTokens.length > 0) {
                faceToken = new String[faceTokens.length];
                for (int i = 0, j = 0; j < faceTokens.length; j++) {
                    if (faceTokens[j].length > 0) {
                        faceToken[i] = new String(faceTokens[j]);
                        i++;
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>getLocalGroupInfo=" + ex.toString());
        }
        return faceToken;
    }

    /**
     * 添加人脸
     */
    public static boolean addLocalFace(Bitmap bitmap) {
        boolean isLocalFaceAdd = false;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return isLocalFaceAdd;
            }
            FacePassAddFaceResult result = MegviiFace.getInstance().mFacePassHandler.addFace(bitmap);
            if (result != null) {
                if (result.result == 0) {
                    isLocalFaceAdd = true;
                }
            }
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>addLocalFace=" + ex.toString());
        }
        return isLocalFaceAdd;
    }

    /**
     * 删除人脸
     */
    public static boolean delLocalFace(String faceTokenStr) {
        boolean isLocalFaceDel = false;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return isLocalFaceDel;
            }
            if (faceTokenStr == null || faceTokenStr.length() == 0) {
                return isLocalFaceDel;
            }
            byte[] faceToken = faceTokenStr.getBytes();
            isLocalFaceDel = MegviiFace.getInstance().mFacePassHandler.deleteFace(faceToken);
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>delLocalFace=" + ex.toString());
        }
        return isLocalFaceDel;
    }

    /**
     * 人脸绑定分组
     */
    public static boolean bindGroup(String faceTokenStr) {
        boolean isLocalFaceBind = false;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return isLocalFaceBind;
            }
            if (faceTokenStr == null || faceTokenStr.length() == 0) {
                return isLocalFaceBind;
            }
            byte[] faceToken = faceTokenStr.getBytes();
            isLocalFaceBind = MegviiFace.getInstance().mFacePassHandler.bindGroup(GROUP_NAME, faceToken);
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>bindGroup=" + ex.toString());
        }
        return isLocalFaceBind;
    }

    /**
     * 人脸解绑分组
     */
    public static boolean unBindGroup(String faceTokenStr) {
        boolean isLocalFaceUnbind = false;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return isLocalFaceUnbind;
            }
            if (faceTokenStr == null || faceTokenStr.length() == 0) {
                return isLocalFaceUnbind;
            }
            byte[] faceToken = faceTokenStr.getBytes();
            isLocalFaceUnbind = MegviiFace.getInstance().mFacePassHandler.unBindGroup(GROUP_NAME, faceToken);
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>unBindGroup=" + ex.toString());
        }
        return isLocalFaceUnbind;
    }

    /**
     * 获取注册人脸照片
     */
    public static Bitmap getFaceImage(String faceTokenStr) {
        Bitmap bitmap = null;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return bitmap;
            }
            if (faceTokenStr == null || faceTokenStr.length() == 0) {
                return bitmap;
            }
            byte[] faceToken = faceTokenStr.getBytes();
            bitmap = MegviiFace.getInstance().mFacePassHandler.getFaceImage(faceToken);
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>getFaceImage=" + ex.toString());
        }
        return bitmap;
    }

    /**
     * 添加并绑定人脸
     */
    public static String addAndBindLocalFace(Bitmap bitmap) {
        String faceToken = "";
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return "";
            }

//            FacePassConfig addConfig = MegviiFace.getInstance().mFacePassHandler.getAddFaceConfig();
//            LogUtils.e("brightnessSTDThreshold: "+addConfig.brightnessSTDThreshold);
//            LogUtils.e("roll: "+addConfig.poseThreshold.roll+"  yaw: "+addConfig.poseThreshold.yaw+"  pitch: "+addConfig.poseThreshold.pitch);
//            LogUtils.e("blur: "+addConfig.blurThreshold + " low brightness: "+addConfig.lowBrightnessThreshold+" high brightness: "+addConfig.highBrightnessThreshold);

            FacePassAddFaceResult result = MegviiFace.getInstance().mFacePassHandler.addFace(bitmap);
            if (result != null) {
//                LogUtils.d("blur=" + result.blur + ", brightness=" + result.brightness + ", deviation="
//                        + result.deviation + ", result=" + result.result);
//                LogUtils.d("facePassRect: top=%d botton=%d left=%d right=%d", result.facePassRect.top,
//                        result.facePassRect.bottom, result.facePassRect.left, result.facePassRect.right);
//                LogUtils.d("pitch=" + result.pose.pitch + ", roll=" + result.pose.roll + ", yaw=" + result.pose.yaw);
                if (result.result == 0) {
                    if (MegviiFace.getInstance().mFacePassHandler.bindGroup(GROUP_NAME, result.faceToken)) {
                        faceToken = new String(result.faceToken);
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>addAndBindLocalFace=" + ex.toString());
        }
        return faceToken;
    }

    /**
     * 识别本地人脸
     */
    public static FacePassRecognitionResult[] recognizeLocalFace(byte[] message) {
        FacePassRecognitionResult[] recognizeResult = null;
        try {
            if (MegviiFace.getInstance().mFacePassHandler == null) {
                return recognizeResult;
            }
            recognizeResult = MegviiFace.getInstance().mFacePassHandler.recognize(GROUP_NAME, message);
        } catch (Exception ex) {
            LogUtils.d("FaceApi------>>>>>recognizeLocalFace=" + ex.toString());
        }
        return recognizeResult;
    }

    public static void reset() {
        if (MegviiFace.getInstance().mFacePassHandler != null) {
            MegviiFace.getInstance().mFacePassHandler.reset();
        }
    }
}
