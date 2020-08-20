package com.mili.smarthome.tkj.face.horizon;

import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.face.horizon.realm.HorizonFaceDao;
import com.mili.smarthome.tkj.face.horizon.realm.HorizonFaceModel;
import com.mili.smarthome.tkj.face.horizon.util.SunriseSdkUtil;
import com.mili.smarthome.tkj.face.horizon.util.XWareHouseUtil;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.util.List;

import hobot.sunrise.sdk.jni.FeaResult;
import hobot.sunrise.sdk.jni.Rect;
import hobot.xwaremodule.sdk.jni.DetectResult;

public class HorizonFacePresenter extends BaseFacePresenter<HorizonFaceModel> {

    @Override
    public long getSurplus() {
        return new HorizonFaceDao().getSurplus();
    }

    @Override
    public boolean enrollFromImage(String imgPath, String faceId) {
        int result = -1;
        DetectResult detectResult = XWareHouseUtil.photoDetect(imgPath, 1280, 720);
        if (detectResult != null) {
            SunriseSdkUtil.changeMode(true);
            Rect rect = new Rect();
            rect.left = (int) detectResult.rect_y.x1_;
            rect.top = (int) detectResult.rect_y.y1_;
            rect.right = (int) detectResult.rect_y.x2_;
            rect.bottom = (int) detectResult.rect_y.y2_;
            FeaResult feaResult = SunriseSdkUtil.getFeature(detectResult.img_, detectResult.width, detectResult.height, rect);
            SunriseSdkUtil.changeMode(false);
            if (feaResult != null) {
                result = XWareHouseUtil.faceAdd(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION, faceId,
                        imgPath, null, feaResult.featureArray, feaResult.shift_l);
            }
        }
        if (result == 0) {
            HorizonFaceModel faceModel = new HorizonFaceModel()
                    .setFirstName(faceId)
                    .setCardNo(parseCardNo(faceId));
            addFaceInfo(faceModel);
        }
        LogUtils.d("-- FACE ENROLL: result=" + result + ", faceId=" + faceId + ", imgPath=" + imgPath);
        return (result == 0);
    }

    @Override
    public boolean enrollFromImage(String imgPath, FaceProtocolInfo faceInfo) {
        int result = -1;
        DetectResult detectResult = XWareHouseUtil.photoDetect(imgPath, 1280, 720);
        if (detectResult != null) {
            SunriseSdkUtil.changeMode(true);
            Rect rect = new Rect();
            rect.left = (int) detectResult.rect_y.x1_;
            rect.top = (int) detectResult.rect_y.y1_;
            rect.right = (int) detectResult.rect_y.x2_;
            rect.bottom = (int) detectResult.rect_y.y2_;
            FeaResult feaResult = SunriseSdkUtil.getFeature(detectResult.img_, detectResult.width, detectResult.height, rect);
            SunriseSdkUtil.changeMode(false);
            if (feaResult != null) {
                result = XWareHouseUtil.faceAdd(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION, faceInfo.getFaceFirstName(),
                        imgPath, null, feaResult.featureArray, feaResult.shift_l);
            }
        }
        if (result == 0) {
            HorizonFaceModel faceModel = new HorizonFaceModel()
                    .setFirstName(faceInfo.getFaceFirstName())
                    .setCardNo(parseCardNo(faceInfo.getFaceFirstName()));
            addFaceInfo(faceModel);
        }
        LogUtils.d("-- FACE ENROLL: result=" + result + ", faceId=" + faceInfo.getFaceFirstName() + ", imgPath=" + imgPath);
        return (result == 0);
    }

    @Override
    public boolean delFaceInfoById(String faceId) {
        int result = XWareHouseUtil.faceDelete(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION, faceId);
        LogUtils.d("-- FACE DEL: faceId=" + faceId + ", result=" + result);
        if (result == 0) {
            HorizonFaceDao horizonFaceDao = new HorizonFaceDao();
            horizonFaceDao.deleteByFirstName(faceId);
        }
        return (result == 0);
    }

    @Override
    public int addFaceInfo(HorizonFaceModel faceModel) {
        HorizonFaceDao faceDao = new HorizonFaceDao();
        faceDao.insert(faceModel);
        LogUtils.d("-- FACE ADD: faceId=" + faceModel.getFirstName());
        return 0;
    }

    @Override
    public int delFaceInfo(String cardNo) {
        HorizonFaceDao faceDao = new HorizonFaceDao();
        List<HorizonFaceModel> faceList = faceDao.queryByCardNo(cardNo);
        if (faceList != null && faceList.size() > 0) {
            for (HorizonFaceModel faceModel : faceList) {
                int result = XWareHouseUtil.faceDelete(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION, faceModel.getFirstName());
                LogUtils.d("-- FACE DEL: name=" + faceModel.getFirstName() + ", result=" + result);
            }
            faceDao.deleteByCardNo(cardNo);
        }
        return 0;
    }

    @Override
    public boolean clearFaceInfo() {
        int result = XWareHouseUtil.deleteFaceSet(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION);
        LogUtils.d("-- FACE CLEAR: result=" + result);
        if (result == 0) {
            HorizonFaceDao faceDao = new HorizonFaceDao();
            faceDao.clear();
        }
        return (result == 0);
    }

    @Override
    public HorizonFaceModel verifyFaceId(String faceId) {
        HorizonFaceDao faceDao = new HorizonFaceDao();
        HorizonFaceModel faceModel = faceDao.queryByFirstName(faceId);
        LogUtils.d("-- FACE VERIFY: faceId=" + faceId + ", result=" + (faceModel != null));
        if (faceModel == null) {
            int result = XWareHouseUtil.faceDelete(HorizonConst.LIBRARY_NAME, HorizonConst.MODEL_VERSION, faceId);
            LogUtils.d("-- FACE DEL: faceId=" + faceId + ", result=" + result);
        }
        return faceModel;
    }

    @Override
    public void subLifecycleInfo(String faceId) {

    }
}
