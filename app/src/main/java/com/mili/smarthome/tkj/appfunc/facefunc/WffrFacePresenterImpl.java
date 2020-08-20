package com.mili.smarthome.tkj.appfunc.facefunc;

import android.os.SystemClock;

import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.WffrFaceDao;
import com.mili.smarthome.tkj.entities.FaceWffrModel;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.face.wffr.WffrUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.wf.wffrapp;

import java.io.File;
import java.util.List;

public class WffrFacePresenterImpl extends BaseFacePresenter<FaceWffrModel> {

    @Override
    public long getSurplus() {
        return new WffrFaceDao().getSurplus();
    }

    @Override
    public boolean enrollFromImage(String imgPath, String faceId) {
        String cardNo = "";
        if (isDev(faceId)) {
            cardNo = parseCardNo(faceId);
        }
        // 通过图片注册人脸
        long beginTime = SystemClock.uptimeMillis();
        int result = wffrapp.runEnrollFromJpegFile(imgPath, faceId);
        long spendTime = SystemClock.uptimeMillis() - beginTime;
        LogUtils.d("-- FACE ENROLL: faceId=%s, imgPath=%s, result=%d(%dms)", faceId, imgPath, result, spendTime);
        if (result == 0) {
            // 保存人脸记录
            FaceWffrModel faceModel = new FaceWffrModel()
                    .setFirstName(faceId)
                    .setCardNo(cardNo);
            addFaceInfo(faceModel);
        }
        return (result == 0);
    }

    @Override
    public boolean enrollFromImage(String imgPath, FaceProtocolInfo faceProtocolInfo) {
        // 通过图片注册人脸
        long beginTime = SystemClock.uptimeMillis();
        int result = wffrapp.runEnrollFromJpegFile(imgPath, faceProtocolInfo.getFaceFirstName());
        long spendTime = SystemClock.uptimeMillis() - beginTime;
        LogUtils.d("-- FACE ENROLL: getFirstName=%s, imgPath=%s, result=%d(%dms)", faceProtocolInfo.getFaceFirstName(), imgPath, result, spendTime);
        if (result == 0) {
            WffrFaceDao faceDao = new WffrFaceDao();
            FaceWffrModel faceModel = new FaceWffrModel();
            faceModel.setFirstName(faceProtocolInfo.getFaceFirstName());
            faceModel.setRoomNoState(faceProtocolInfo.getRoomNoState());
            faceModel.setKeyID(faceProtocolInfo.getKeyID());
            faceModel.setExturl(faceProtocolInfo.getExturl());
            faceModel.setAttri(faceProtocolInfo.getAttri());
            faceModel.setStartTime(faceProtocolInfo.getStartTime());
            faceModel.setEndTime(faceProtocolInfo.getEndTime());
            faceModel.setLifecycle(faceProtocolInfo.getLifecycle());
            faceDao.insert(faceModel);
        }
        return (result == 0);
    }

    @Override
    public int addFaceInfo(FaceWffrModel faceModel) {
        WffrFaceDao faceDao = new WffrFaceDao();
        faceModel.setRoomNoState(0);
        faceModel.setKeyID("");
        faceModel.setExturl("");
        faceModel.setAttri(1);
        faceModel.setStartTime(0);
        faceModel.setEndTime(0);
        faceModel.setLifecycle(-2);
        faceDao.insert(faceModel);
        LogUtils.d("-- FACE ADD: faceId=" + faceModel.getFirstName());
        if (isDev(faceModel.getFirstName())) {
            // 本地补录，每个卡号限制最多3个，多于3个则覆盖最早的记录
            String cardNo = faceModel.getCardNo();
            List<FaceWffrModel> earlyList = faceDao.queryEarly(cardNo);
            if (earlyList != null) {
                for (FaceWffrModel earlyModel : earlyList) {
                    int result = wffrapp.deletePersonByName(earlyModel.getFirstName());
                    LogUtils.d("-- FACE DEL: faceId=" + earlyModel.getFirstName() + ", result=" + result);
                    if (result == 0) {
                        faceDao.deleteByFirstName(earlyModel.getFirstName());
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public boolean delFaceInfoById(String faceId) {
        int result = wffrapp.deletePersonByName(faceId);
        if (result == 0) {
            WffrFaceDao faceDao = new WffrFaceDao();
            faceDao.deleteByFirstName(faceId);
        }
        LogUtils.d("-- FACE DEL: faceId=" + faceId + ", result=" + result);
        return (result == 0);
    }

    @Override
    public int delFaceInfo(String cardNo) {
        WffrFaceDao faceDao = new WffrFaceDao();
        List<FaceWffrModel> faceList = faceDao.queryByCardNo(cardNo);
        for (FaceWffrModel faceModel : faceList) {
            int result = wffrapp.deletePersonByName(faceModel.getFirstName());
            LogUtils.d("-- FACE DEL: faceId=" + faceModel.getFirstName() + ", result=" + result);
            if (result == 0) {
                faceDao.deleteByFirstName(faceModel.getFirstName());
            }
        }
        return 0;
    }

    @Override
    public boolean clearFaceInfo() {
        String dbDirPath = WffrUtils.getDbDirPath(ContextProxy.getContext());
        String dbFileName = WffrUtils.DB_NAME;
        File dbFile = new File(dbDirPath, dbFileName);
        int result = dbFile.exists() ? wffrapp.deleteDatabase() : 0;
        LogUtils.d("-- FACE CLEAR: result=" + result);
        if (result == 0) {
            WffrFaceDao faceDao = new WffrFaceDao();
            faceDao.clear();
        }
        return (result == 0);
    }

    @Override
    public FaceWffrModel verifyFaceId(String faceId) {
        WffrFaceDao faceDao = new WffrFaceDao();
        FaceWffrModel faceModel = faceDao.queryByFirstName(faceId);
        LogUtils.d("-- FACE VERIFY: faceId=" + faceId + ", result=" + (faceModel != null));
        if (faceModel == null) {
            int result = wffrapp.deletePersonByName(faceId);
            LogUtils.d("-- FACE DEL: faceId=" + faceId + ", result=" + result);
        }
        return faceModel;
    }

    @Override
    public void subLifecycleInfo(String faceId) {
        WffrFaceDao faceDao = new WffrFaceDao();
        faceDao.subLifecycle(faceId);
    }

    public static FaceProtocolInfo convert(FaceWffrModel faceModel){
        FaceProtocolInfo faceProtocolInfo = new FaceProtocolInfo();
        faceProtocolInfo.setFaceFirstName(faceModel.getFirstName());
        faceProtocolInfo.setRoomNoState(faceModel.getRoomNoState());
        faceProtocolInfo.setKeyID(faceModel.getKeyID());
        faceProtocolInfo.setExturl(faceModel.getExturl());
        faceProtocolInfo.setAttri(faceModel.getAttri());
        faceProtocolInfo.setStartTime(faceModel.getStartTime());
        faceProtocolInfo.setEndTime(faceModel.getEndTime());
        faceProtocolInfo.setLifecycle(faceModel.getLifecycle());

        return faceProtocolInfo;
    }
}
