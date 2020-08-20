package com.mili.smarthome.tkj.face.horizon.util;

import android.util.Log;

import hobot.xwaremodule.sdk.jni.DetectResult;
import hobot.xwaremodule.sdk.jni.HobotXWMCompareResult;
import hobot.xwaremodule.sdk.jni.HobotXWMDataSourceInfo;
import hobot.xwaremodule.sdk.jni.HobotXWMFeature;
import hobot.xwaremodule.sdk.jni.HobotXWMListFeatureResult;
import hobot.xwaremodule.sdk.jni.HobotXWMListRecordResult;
import hobot.xwaremodule.sdk.jni.HobotXWMListSetResult;
import hobot.xwaremodule.sdk.jni.HobotXWMRecord;
import hobot.xwaremodule.sdk.jni.HobotXWMSearchParam;
import hobot.xwaremodule.sdk.jni.HobotXWMSearchResult;
import hobot.xwaremodule.sdk.jni.HobotXWareModuleJni;
import hobot.xwaremodule.sdk.jni.XWareModuleEnumClass;

public class XWareHouseUtil {
    private static final String TAG = XWareHouseUtil.class.getName();
    private static HobotXWareModuleJni hobotXWareModuleJni = null;
    private static HobotXWMDataSourceInfo hobotXWMDataSourceInfo = null;
    private static int xfaceWorkerHandle;
    private static float mDistance = 194.0F;
    private static float mSimilar = 194.0F;
    private final static int FEATURE_SIZE = 128;
    private static boolean canRun = false;
    private static int initRet;

    public static int init(String dbPath, String modelPath) {
        if (hobotXWMDataSourceInfo == null && hobotXWareModuleJni == null) {
            hobotXWareModuleJni = new HobotXWareModuleJni();
            hobotXWMDataSourceInfo = new HobotXWMDataSourceInfo();
            hobotXWMDataSourceInfo.type_ = XWareModuleEnumClass.HobotXWMDataSourceType.SQLITE;
            hobotXWMDataSourceInfo.flag = XWareModuleEnumClass.HobotXWMWareCheckFlag.WARE_CHECK_RACE;
            Log.d(TAG, "dbPath = " + dbPath);
            hobotXWMDataSourceInfo.db_file_ = new String(dbPath);
            //hobotXWMDataSourceInfo.zip_file_ = new HobotXWMLibraryFileInfo();
            //hobotXWMDataSourceInfo.zip_file_.serialize_type_ = XWareModuleEnumClass.HobotXWMSerializeType.PROTO;
            //hobotXWMDataSourceInfo.zip_file_.file_name_ = new String("./");
            initRet = hobotXWareModuleJni.hobot_xwm_init(hobotXWMDataSourceInfo);
            Log.d(TAG, "hobot_xwh_init initRet = " + initRet);
            xfaceWorkerHandle = hobotXWareModuleJni.hobot_xwm_create_face_worker(modelPath, (int) 0, (int) 0);
            Log.d(TAG, "xfaceWorkerHandle = " + xfaceWorkerHandle);
            if (initRet == 0) {
                canRun = true;
            }
        }
        return initRet;
    }

    public static int getInitStatus() {
        return initRet;
    }

    public static void setJniLogLevel(int level) {
        if (hobotXWareModuleJni != null) {
            hobotXWareModuleJni.hobot_jni_log_level(level);
        }
    }

    public static String getVersion() {
        String version = null;
        if (hobotXWareModuleJni != null) {
            version = hobotXWareModuleJni.hobot_xwm_get_version();
            Log.d(TAG, "hobot_xwh_get_version version = " + version);
        }
        return version;
    }

    public static DetectResult photoDetect(String photoPath, int width, int height) {
        DetectResult detectResult = null;
        if (photoPath != null || !canRun) {
            byte[] fileContent = FileUtil.getFileContent(photoPath);
            if (xfaceWorkerHandle != 0 && fileContent != null) {
                detectResult = hobotXWareModuleJni.hobot_xwm_face_worker_detect(xfaceWorkerHandle,
                        XWareModuleEnumClass.HobotImageType.None.ordinal(), width, height, fileContent);
                Log.d(TAG, "detectResult =" + detectResult + " photoPath = " + photoPath + " fileContent.length = "
                        + fileContent.length);

//                if (detectResult != null) {
//                    Log.d(TAG, "detectResult.img_length_ =" + detectResult.img_length_);
//                    Log.d(TAG, "detectResult.rect_y.x1_ = " + detectResult.rect_y.x1_);
//                    Log.d(TAG, "detectResult.rect_y.y1_ = " + detectResult.rect_y.y1_);
//                    Log.d(TAG, "detectResult.rect_y.x2_ = " + detectResult.rect_y.x2_);
//                    Log.d(TAG, "detectResult.rect_y.y2_ = " + detectResult.rect_y.y2_);
//                    Log.d(TAG, "detectResult.rect_src.x1_ = " + detectResult.rect_src.x1_);
//                    Log.d(TAG, "detectResult.rect_src.y1_ = " + detectResult.rect_src.y1_);
//                    Log.d(TAG, "detectResult.rect_src.x2_ = " + detectResult.rect_src.x2_);
//                    Log.d(TAG, "detectResult.rect_src.y2_ = " + detectResult.rect_src.y2_);
//                    Log.d(TAG, "detectResult.width = " + detectResult.width);
//                    Log.d(TAG, "detectResult.height = " + detectResult.height);
//
//                }
            }
        }
        return detectResult;
    }

    public static int createFaceSet(String libraryName, String modelVersion) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        Log.d(TAG, "hobotXWHDataSourceInfo.db_file_ = " + hobotXWMDataSourceInfo.db_file_);
        ret = hobotXWareModuleJni.hobot_xwm_create_set(libraryName, modelVersion, FEATURE_SIZE,
                hobotXWMDataSourceInfo);
        Log.d(TAG, "hobot_xwh_create_set = " + ret);
        return ret;
    }

    public static int setFaceSetThreshold(String libraryName, String modelVersion) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        ret = hobotXWareModuleJni.hobot_xwm_set_threshold(libraryName, modelVersion, mDistance, mSimilar);
        Log.d(TAG, "hobot_xwh_set_threshold = " + ret);
        return ret;
    }

    public static void setThreshold(float distance, float similar) {
        if (!canRun) {
            return;
        }
        mDistance = distance;
        mSimilar = similar;
    }

    public static int deleteFaceSet(String libraryName, String modelVersion) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        ret = hobotXWareModuleJni.hobot_xwm_drop_set(libraryName, modelVersion);
        return ret;
    }

    public static int faceAdd(String libraryName, String modelVersion, String idName, String
            imgPath, float[] featureFArray, int[] featureIArray, byte shift_l) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        if (libraryName != null && idName != null && modelVersion != null && imgPath != null) {
            Log.d(TAG, "FaceRegister mLibraryName = " + libraryName + " modelVersion=" + modelVersion);
            HobotXWMRecord hobotXWHRecord = new HobotXWMRecord();
            hobotXWHRecord.id_ = new String(idName);
            hobotXWHRecord.size_ = 1;
            hobotXWHRecord.features_ = new HobotXWMFeature[1];
            hobotXWHRecord.features_[0] = new HobotXWMFeature();
            hobotXWHRecord.features_[0].img_uri_ = new String(imgPath);
            hobotXWHRecord.features_[0].size_ = FEATURE_SIZE;
            //hobotXWHRecord.features_[0].feature_ = new float[FEATURE_SIZE];
            if (featureFArray != null) {
                hobotXWHRecord.features_[0].feature_ = featureFArray;
                hobotXWHRecord.features_[0].attr = 1;
            } else if (featureIArray != null) {
                hobotXWHRecord.features_[0].feature = featureIArray;
                hobotXWHRecord.features_[0].attr = shift_l << 1;
            }
            ret = hobotXWareModuleJni.hobot_xwm_add_record(libraryName, modelVersion, hobotXWHRecord);
            Log.d(TAG, "hobot_xwh_add_record ret = " + ret);
        }
        return ret;
    }

    public static int faceAddFeature(String libraryName, String modelVersion, String idName, String imgPath, float[]
            featureFArray, int[] featureIArray, byte shift_l) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        if (libraryName != null && modelVersion != null && imgPath != null) {
            HobotXWMFeature hobotXWMFeature = new HobotXWMFeature();
            hobotXWMFeature.img_uri_ = new String(imgPath);
            hobotXWMFeature.size_ = FEATURE_SIZE;
            if (featureFArray != null) {
                hobotXWMFeature.feature_ = featureFArray;
                hobotXWMFeature.attr = 1;
            } else if (featureIArray != null) {
                hobotXWMFeature.feature = featureIArray;
                hobotXWMFeature.attr = shift_l << 1;
            }
            ret = hobotXWareModuleJni.hobot_xwm_add_feature(libraryName, modelVersion, idName, hobotXWMFeature);
        }
        return ret;
    }

    public static int faceUpdateFeature(String libraryName, String modelVersion, String idName, String imgPath, float[]
            featureFArray, int[] featureIArray, byte shift_l) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        if (libraryName != null && modelVersion != null && imgPath != null) {
            HobotXWMFeature hobotXWMFeature = new HobotXWMFeature();
            hobotXWMFeature.img_uri_ = new String(imgPath);
            hobotXWMFeature.size_ = FEATURE_SIZE;
            if (featureFArray != null) {
                hobotXWMFeature.feature_ = featureFArray;
                hobotXWMFeature.attr = 1;
            } else if (featureIArray != null) {
                hobotXWMFeature.feature = featureIArray;
                hobotXWMFeature.attr = shift_l << 1;
            }

            ret = hobotXWareModuleJni.hobot_xwm_update_feature(libraryName, modelVersion, idName, hobotXWMFeature);
        }
        return ret;
    }

    public static int faceDeleteFeature(String libraryName, String modelVersion, String idName, String imgPath) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        if (libraryName != null && modelVersion != null && idName != null && imgPath != null) {
            ret = hobotXWareModuleJni.hobot_xwm_delete_feature(libraryName, modelVersion, idName, imgPath);
        }
        return ret;
    }

    public static int faceUpdate(String libraryName, String modelVersion, String idName, String imgPath, float[]
            featureFArray, int[] featureIArray, byte shift_l) {
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        if (libraryName != null && idName != null && modelVersion != null && imgPath != null) {
            HobotXWMRecord hobotXWMRecord = new HobotXWMRecord();
            hobotXWMRecord.id_ = new String(idName);
            hobotXWMRecord.size_ = 1;
            hobotXWMRecord.features_ = new HobotXWMFeature[1];
            hobotXWMRecord.features_[0] = new HobotXWMFeature();
            hobotXWMRecord.features_[0].img_uri_ = new String(imgPath);
            hobotXWMRecord.features_[0].size_ = FEATURE_SIZE;
            hobotXWMRecord.features_[0].feature_ = new float[FEATURE_SIZE];
            if (featureFArray != null) {
                hobotXWMRecord.features_[0].feature_ = featureFArray;
                hobotXWMRecord.features_[0].attr = 1;
            } else if (featureIArray != null) {
                hobotXWMRecord.features_[0].feature = featureIArray;
                hobotXWMRecord.features_[0].attr = shift_l << 1;
            }
            ret = hobotXWareModuleJni.hobot_xwm_update_record(libraryName, modelVersion, hobotXWMRecord);
        }

        return ret;
    }

    public static int faceDelete(String libraryName, String modelVersion, String idName) {
        Log.d(TAG, "faceDelete libraryName = " + libraryName + " idName = " + idName + " modelVersion =" +
                modelVersion);
        int ret = -1;
        if (!canRun) {
            return ret;
        }
        if (libraryName != null && modelVersion != null && idName != null) {
            return hobotXWareModuleJni.hobot_xwm_delete_record(libraryName, modelVersion, idName);
        }
        return ret;
    }

    public static HobotXWMListSetResult faceSetList() {
        if (!canRun) {
            return null;
        }
        HobotXWMListSetResult hobotXWMListSetResult = hobotXWareModuleJni.hobot_xwm_list_set();
        if (hobotXWMListSetResult != null) {
            Log.d(TAG, "hobotXWHListSetResult.num_ = " + hobotXWMListSetResult.num_);
            for (int i = 0; i < hobotXWMListSetResult.num_; i++) {
                Log.d(TAG, "hobotXWHListSetResult.sets_[i].set_name_ = " + hobotXWMListSetResult.sets_[i]
                        .set_name_);
                Log.d(TAG, "hobotXWHListSetResult.sets_[i].model_version_ = " + hobotXWMListSetResult.sets_[i]
                        .model_version_);
                Log.d(TAG, "hobotXWHListSetResult.sets_[i].feature_size_ = " + hobotXWMListSetResult.sets_[i]
                        .feature_size_);
                Log.d(TAG, "hobotXWHListSetResult.sets_[i].distance_threshold_ = " + hobotXWMListSetResult.sets_[i]
                        .distance_threshold_);
                Log.d(TAG, "hobotXWHListSetResult.sets_[i].similar_threshold_ = " + hobotXWMListSetResult.sets_[i]
                        .similar_threshold_);
            }
        }
        return hobotXWMListSetResult;
    }

    public static HobotXWMListRecordResult faceList(String libraryName, String modelVersion) {
        if (!canRun) {
            return null;
        }
        HobotXWMListRecordResult hobotXWMListRecordResult = hobotXWareModuleJni.hobot_xwm_list_record
                (libraryName, modelVersion);
//        if (hobotXWHListRecordResult != null) {
//            for (int i = 0; i < hobotXWHListRecordResult.num_; i++) {
//                Log.d(TAG, "hobotXWHListRecordResult.record_[i].id_ = " + hobotXWHListRecordResult.record_[i].id_);
//                for (int j = 0; j < hobotXWHListRecordResult.record_[i].size_; j++) {
//                    Log.d(TAG, "hobotXWHListRecordResult.record_[i].features_[j].img_uri_ = " +
//                            hobotXWHListRecordResult
//                                    .record_[i].features_[j].img_uri_);
//                    for (int k = 0; k < hobotXWHListRecordResult
//                            .record_[i].features_[j].size_; k++) {
//                        Log.d(TAG, "hobotXWHListRecordResult.record_[i].features_[j].feature_[k] = " +
//                                hobotXWHListRecordResult.record_[i].features_[j].feature_[k]);
//                    }
//                }
//            }
//        }
        return hobotXWMListRecordResult;
    }

    public static HobotXWMListFeatureResult faceFeatureList(String libraryName, String modelVersion, String id) {
        if (!canRun) {
            return null;
        }
        HobotXWMListFeatureResult hobotXWMListFeatureResult = hobotXWareModuleJni.hobot_xwm_list_feature
                (libraryName, modelVersion, id);
//        if (hobotXWHListFeatureResult != null) {
//            Log.d(TAG, "hobotXWHListFeatureResult.record_.id_ = " + hobotXWHListFeatureResult.record_.id_);
//            Log.d(TAG, "hobotXWHListFeatureResult.record_.size_ = " + hobotXWHListFeatureResult.record_.size_);
//            for (int i = 0; i < hobotXWHListFeatureResult.record_.size_; i++) {
//                Log.d(TAG, "hobotXWHListFeatureResult.record_.features_.img_uri_ = " + hobotXWHListFeatureResult
//                        .record_
//                        .features_[i].img_uri_);
//                for (int j = 0; j < hobotXWHListFeatureResult.record_.features_[i].size_; j++) {
//                    Log.d(TAG, "hobotXWHListFeatureResult.record_.features_.features_[i].feature_[j] = " +
//                            hobotXWHListFeatureResult.record_
//                                    .features_[i].feature_[j]);
//                }
//            }
//        }
        return hobotXWMListFeatureResult;
    }

    public static HobotXWMSearchResult faceSearch(String libraryName, String modelVersion, String imgPath, float[]
            featureFArray, int[] featureIArray, byte shift_l, int topN) {
        HobotXWMSearchResult hobotXWMSearchResult = null;
        synchronized (TAG) {
            if (!canRun) {
                return hobotXWMSearchResult;
            }
            if (libraryName != null && modelVersion != null && imgPath != null) {
                HobotXWMSearchParam hobotXWMSearchParam = new HobotXWMSearchParam();
                hobotXWMSearchParam.top_n_ = topN;
                hobotXWMSearchParam.distance_threshold_ = mDistance;
                hobotXWMSearchParam.similar_threshold_ = mSimilar;
                hobotXWMSearchParam.size_ = 1;
                hobotXWMSearchParam.features_ = new HobotXWMFeature[1];
                hobotXWMSearchParam.features_[0] = new HobotXWMFeature();
                hobotXWMSearchParam.features_[0].img_uri_ = new String(imgPath);
                hobotXWMSearchParam.features_[0].size_ = FEATURE_SIZE;
                if (featureFArray != null) {
                    hobotXWMSearchParam.features_[0].feature_ = featureFArray;
                    hobotXWMSearchParam.features_[0].attr = 1;
                } else if (featureIArray != null) {
                    hobotXWMSearchParam.features_[0].feature = featureIArray;
                    hobotXWMSearchParam.features_[0].attr = shift_l << 1;
                }
                hobotXWMSearchResult = hobotXWareModuleJni.hobot_xwm_search(libraryName, modelVersion,
                        hobotXWMSearchParam);

            }
        }
        return hobotXWMSearchResult;
    }

    public static HobotXWMCompareResult facefloatFeatureCompare(float[] features1, float[] features2,
                                                                float distance_threshold, float similar_threshold) {

        HobotXWMCompareResult hobotXWMCompareResult = null;
        HobotXWMRecord hobotXWMRecord1 = null;
        HobotXWMRecord hobotXWMRecord2 = null;
        if (!canRun) {
            return hobotXWMCompareResult;
        }
        if (features1 != null && features2 != null) {
            hobotXWMRecord1 = new HobotXWMRecord();
            hobotXWMRecord2 = new HobotXWMRecord();
            hobotXWMRecord1.features_ = new HobotXWMFeature[1];
            hobotXWMRecord1.size_ = 1;
            hobotXWMRecord1.features_[0] = new HobotXWMFeature();
            hobotXWMRecord1.features_[0].size_ = FEATURE_SIZE;
            hobotXWMRecord1.features_[0].feature_ = features1;
            hobotXWMRecord1.features_[0].attr = 1;
            hobotXWMRecord2.features_ = new HobotXWMFeature[1];
            hobotXWMRecord2.size_ = 1;
            hobotXWMRecord2.features_[0] = new HobotXWMFeature();
            hobotXWMRecord2.features_[0].size_ = FEATURE_SIZE;
            hobotXWMRecord2.features_[0].feature_ = features2;
            hobotXWMRecord2.features_[0].attr = 1;
            hobotXWMCompareResult = hobotXWareModuleJni.hobot_xwm_feature_compare1V1(hobotXWMRecord1, hobotXWMRecord2,
                    distance_threshold, similar_threshold);
        }
        return hobotXWMCompareResult;
    }

    public static HobotXWMCompareResult faceintFeatureCompare(int[] features1, byte shift1_l, int[] features2, byte
            shift2_l, float distance_threshold, float similar_threshold) {
        HobotXWMCompareResult hobotXWMCompareResult = null;
        HobotXWMRecord hobotXWMRecord1 = null;
        HobotXWMRecord hobotXWMRecord2 = null;
        if (!canRun) {
            return hobotXWMCompareResult;
        }
        if (features1 != null && features2 != null) {
            hobotXWMRecord1 = new HobotXWMRecord();
            hobotXWMRecord2 = new HobotXWMRecord();
            hobotXWMRecord1.features_ = new HobotXWMFeature[1];
            hobotXWMRecord1.size_ = 1;
            hobotXWMRecord1.features_[0] = new HobotXWMFeature();
            hobotXWMRecord1.features_[0].size_ = FEATURE_SIZE;
            hobotXWMRecord1.features_[0].feature = features1;
            hobotXWMRecord1.features_[0].attr = shift1_l << 1;
            hobotXWMRecord2.features_ = new HobotXWMFeature[1];
            hobotXWMRecord2.size_ = 1;
            hobotXWMRecord2.features_[0] = new HobotXWMFeature();
            hobotXWMRecord2.features_[0].size_ = FEATURE_SIZE;
            hobotXWMRecord2.features_[0].feature = features2;
            hobotXWMRecord2.features_[0].attr = shift2_l << 1;
            hobotXWMCompareResult = hobotXWareModuleJni.hobot_xwm_feature_compare1V1(hobotXWMRecord1, hobotXWMRecord2,
                    distance_threshold, similar_threshold);
        }
        return hobotXWMCompareResult;
    }

    public static int deinit() {
        int ret = -1;
        canRun = false;
        synchronized (TAG) {
            if (hobotXWareModuleJni != null && hobotXWMDataSourceInfo != null) {
                ret = hobotXWareModuleJni.hobot_xwm_close();
                Log.d(TAG, "hobot_xwh_close ret = " + ret);
                if (xfaceWorkerHandle != 0) {
                    ret |= hobotXWareModuleJni.hobot_xwm_delete_face_worker(xfaceWorkerHandle);
                    Log.d(TAG, "hobot_xwh_delete_face_worker ret = " + ret);
                }
                hobotXWareModuleJni = null;
                hobotXWMDataSourceInfo = null;
            }
        }
        return ret;
    }
}
