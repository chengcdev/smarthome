package com.mili.smarthome.tkj.appfunc.facefunc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.dao.MegviiFaceDao;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.io.DataOutputStream;
import java.util.List;

public class MegviiFacePresenterImpl extends BaseFacePresenter<FaceMegviiModel> {

    @Override
    public long getSurplus() {
        return new MegviiFaceDao().getSurplus();
    }

    @Override
    public boolean enrollFromImage(String imgPath, String faceId) {
        String cardNo = "";
        if (isDev(faceId)) {
            cardNo = parseCardNo(faceId);
        }
        // 通过图片注册人脸
        long beginTime = SystemClock.uptimeMillis();
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        final String faceToken = FaceApi.addAndBindLocalFace(bitmap);
        long spendTime = SystemClock.uptimeMillis() - beginTime;
        LogUtils.d("-- FACE ENROLL: faceId=%s, imgPath=%s, faceToken=%s(%dms)", faceId, imgPath, faceToken, spendTime);
        boolean result = (faceToken != null && faceToken.length() > 0);
        if (result) {
            // 保存人脸记录
            FaceMegviiModel faceModel = new FaceMegviiModel()
                    .setFirstName(faceId)
                    .setCardNo(cardNo)
                    .setFaceToken(faceToken);
            addFaceInfo(faceModel);
        }
        return result;
    }

    @Override
    public boolean enrollFromImage(String imgPath, FaceProtocolInfo faceProtocolInfo) {
        // 通过图片注册人脸
        long beginTime = SystemClock.uptimeMillis();
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        final String faceToken = FaceApi.addAndBindLocalFace(bitmap);
        long spendTime = SystemClock.uptimeMillis() - beginTime;
        LogUtils.d("-- FACE ENROLL: getFirstName=%s, imgPath=%s, faceToken=%s(%dms)", faceProtocolInfo.getFaceFirstName(), imgPath, faceToken, spendTime);
        boolean result = (faceToken != null && faceToken.length() > 0);
        if (result) {
            MegviiFaceDao faceDao = new MegviiFaceDao();
            FaceMegviiModel faceModel = new FaceMegviiModel();
            faceModel.setFaceToken(faceToken);
            faceModel.setFirstName(faceProtocolInfo.getFaceFirstName());
            faceModel.setRoomNoState(faceProtocolInfo.getRoomNoState());
            faceModel.setKeyID(faceProtocolInfo.getKeyID());
            faceModel.setExturl(faceProtocolInfo.getExturl());
            faceModel.setAttri(faceProtocolInfo.getAttri());
            faceModel.setStartTime(faceProtocolInfo.getStartTime());
            faceModel.setEndTime(faceProtocolInfo.getEndTime());
            faceModel.setLifecycle(faceProtocolInfo.getLifecycle());
            faceModel.setFaceToken(faceToken);
            faceDao.insert(faceModel);
        }
        return result;
    }

    @Override
    public int addFaceInfo(FaceMegviiModel faceModel) {
        MegviiFaceDao faceDao = new MegviiFaceDao();
        faceModel.setRoomNoState(0);
        faceModel.setKeyID("");
        faceModel.setExturl("");
        faceModel.setAttri(1);
        faceModel.setStartTime(0);
        faceModel.setEndTime(0);
        faceModel.setLifecycle(-2);
        faceDao.insert(faceModel);
        LogUtils.d("-- FACE ADD: faceId=%s", faceModel.getFirstName());
        // 删除早期的人脸信息
        String cardNo = faceModel.getCardNo();
        List<FaceMegviiModel> earlyList = faceDao.queryEarly(cardNo);
        if (earlyList != null) {
            for (FaceMegviiModel earlyModel : earlyList) {
                boolean result = FaceApi.delLocalFace(earlyModel.getFaceToken());
                LogUtils.d("-- FACE DEL: faceId=%s, result=%s", earlyModel.getFirstName(), result);
//                if (!result) {
                    faceDao.deleteByFirstName(earlyModel.getFirstName());
//                }
            }
        }
        return 0;
    }

    @Override
    public boolean delFaceInfoById(String faceId) {
        MegviiFaceDao faceDao = new MegviiFaceDao();
        FaceMegviiModel faceModel = faceDao.queryByFirstName(faceId);
        boolean result = true;
        if (faceModel != null) {
            result = FaceApi.delLocalFace(faceModel.getFaceToken());
            if (result) {
                faceDao.deleteByFirstName(faceId);
            }
        }
        LogUtils.d("-- FACE DEL: faceId=%s, result=%s", faceId, result);
        return result;
    }

    @Override
    public int delFaceInfo(String cardNo) {
        MegviiFaceDao faceDao = new MegviiFaceDao();
        List<FaceMegviiModel> faceList = faceDao.queryByCardNo(cardNo);
        for (FaceMegviiModel faceModel : faceList) {
            boolean result = FaceApi.delLocalFace(faceModel.getFaceToken());
            LogUtils.d("-- FACE DEL: faceId=%s, faceToken=%s, result=%s", faceModel.getFirstName(), faceModel.getFaceToken(), result);
//            if (result) {
                faceDao.deleteByFaceToken(faceModel.getFaceToken());
//            }
        }
        return 0;
    }

    @Override
    public boolean clearFaceInfo() {
        LogUtils.d("-- FACE CLEAR");
        try {
            Process p = Runtime.getRuntime().exec("system/bin/sh");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("rm -rf " + Const.Directory.MEGVII + "/*\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        MegviiFaceDao faceDao = new MegviiFaceDao();
        faceDao.clear();
        App.getInstance().initMegviiAssets();
        return true;
    }

    @Override
    public FaceMegviiModel verifyFaceId(String faceToken) {
        MegviiFaceDao faceDao = new MegviiFaceDao();
        FaceMegviiModel faceModel = faceDao.queryByFaceToken(faceToken);
        LogUtils.d("-- FACE VERIFY: faceToken=%s, result=%s", faceToken, (faceModel != null));
        if (faceModel == null) {
            boolean result = FaceApi.delLocalFace(faceToken);
            LogUtils.d("-- FACE DEL: faceToken=%s, result=%s", faceToken, result);
        }
        return faceModel;
    }

    @Override
    public void subLifecycleInfo(String faceToken) {
        MegviiFaceDao faceDao = new MegviiFaceDao();
        faceDao.subLifecycle(faceToken);
    }

    public static FaceProtocolInfo convert(FaceMegviiModel faceModel){
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
