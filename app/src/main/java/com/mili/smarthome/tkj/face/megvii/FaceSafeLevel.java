package com.mili.smarthome.tkj.face.megvii;

/**
 * 旷视Megvii人脸参数定义(低、中、高)
 * 2018-07-01: Created by chenrh.
 */
public class FaceSafeLevel {

    /* 人脸参数-识别要求低 */
    public static final class Low {
        // 人脸识别阈值
        public static float _search = 70f;
        // 最小人脸尺寸
        public static int _faceMin = 100;
        // 人脸角度阈值-旋转角度
        public static float _pose1 = 30f;
        // 人脸角度阈值-垂直角度
        public static float _pose2 = 30f;
        // 人脸角度阈值-水平角度
        public static float _pose3 = 30f;
        // 模糊度阈值
        public static float _blur = 0.4f;
        // 最小人脸照度阈值
        public static float _lowBrightness = 30f;
        // 最大人脸照度阈值
        public static float _highBrightness = 230f;
        // 人脸照度标准差
        public static float _brightnessSTD = 160f;
        // 人脸识别次数
        public static int _retryTime = 20;
        // 活体阈值
        public static float _liveness = 60f;
        // 活体检测
        public static boolean _isLiveness = false;
        // 笑脸检测
        public static boolean _isSmile = false;
        // 年龄性别检测
        public static boolean _isAgeGender = false;
        // 检测识别最大的人脸
        public static boolean _isMaxFaceEnabled = true;
    }

    /* 人脸参数-识别要求中 */
    public static final class Medium {
        // 人脸识别阈值
        public static float _search = 75f;
        // 最小人脸尺寸
        public static int _faceMin = 100;
        // 人脸角度阈值-旋转角度
        public static float _pose1 = 25f;
        // 人脸角度阈值-垂直角度
        public static float _pose2 = 25f;
        // 人脸角度阈值-水平角度
        public static float _pose3 = 25f;
        // 模糊度阈值
        public static float _blur = 0.3f;
        // 最小人脸照度阈值
        public static float _lowBrightness = 50f;
        // 最大人脸照度阈值
        public static float _highBrightness = 220f;
        // 人脸照度标准差
        public static float _brightnessSTD = 120f;
        // 人脸识别次数
        public static int _retryTime = 20;
        // 活体阈值
        public static float _liveness = 60f;
        // 活体检测
        public static boolean _isLiveness = false;
        // 笑脸检测
        public static boolean _isSmile = false;
        // 年龄性别检测
        public static boolean _isAgeGender = false;
        // 检测识别最大的人脸
        public static boolean _isMaxFaceEnabled = true;
    }

    /* 人脸参数-识别要求高 */
    public static final class High {
        // 人脸识别阈值
        public static float _search = 80f;
        // 最小人脸尺寸
        public static int _faceMin = 100;
        // 人脸角度阈值-旋转角度
        public static float _pose1 = 25f;
        // 人脸角度阈值-垂直角度
        public static float _pose2 = 25f;
        // 人脸角度阈值-水平角度
        public static float _pose3 = 25f;
        // 模糊度阈值
        public static float _blur = 0.2f;
        // 最小人脸照度阈值
        public static float _lowBrightness = 70f;
        // 最大人脸照度阈值
        public static float _highBrightness = 210f;
        // 人脸照度标准差
        public static float _brightnessSTD = 80f;
        // 人脸识别次数
        public static int _retryTime = 20;
        // 活体阈值
        public static float _liveness = 60f;
        // 活体检测
        public static boolean _isLiveness = false;
        // 笑脸检测
        public static boolean _isSmile = false;
        // 年龄性别检测
        public static boolean _isAgeGender = false;
        // 检测识别最大的人脸
        public static boolean _isMaxFaceEnabled = true;
    }
}