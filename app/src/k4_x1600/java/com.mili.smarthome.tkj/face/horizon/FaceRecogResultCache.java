package com.mili.smarthome.tkj.face.horizon;

import android.util.SparseArray;

import com.mili.smarthome.tkj.face.horizon.bean.FaceRecogResult;

/**
 * 缓存最近的人脸识别结果
 */
public class FaceRecogResultCache {

    private static SparseArray<FaceRecogResult> mCache;

    public static FaceRecogResult get(int trackId) {
        if (mCache == null)
            return null;
        return mCache.get(trackId);
    }

    public static void put(FaceRecogResult result) {
        if (mCache == null) {
            mCache = new SparseArray<>();
        }
        mCache.put(result.getTrackId(), result);
        if (mCache.size() > 5) {
            mCache.removeAt(0);
        }
    }

    public static void clear() {
        if (mCache != null) {
            mCache.clear();
        }
    }
}
