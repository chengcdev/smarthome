package com.mili.smarthome.tkj.dao;

import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.entities.FaceWffrModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class WffrFaceDao {

    private static final long CAPACITY = 10000;

    /**
     * 获取人脸剩余个数
     */
    public long getSurplus() {
        long count = RealmUtils.count(FaceWffrModel.class);
        return Math.max(0, CAPACITY - count);
    }

    /**
     * 增加人脸信息
     * @param addModel 人脸信息
     */
    public void insert(final FaceWffrModel addModel) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean isDev = BaseFacePresenter.isDev(addModel.getFirstName());
                if (isDev) {
                    RealmResults<FaceWffrModel> results = realm.where(FaceWffrModel.class)
                            .like("firstName", "dev-*")
                            .equalTo("cardNo", addModel.getCardNo())
                            .findAllSorted("index");
                    for (int i = 0; i < results.size(); i++) {
                        results.get(i).setIndex(i);
                    }
                    addModel.setIndex(results.size());
                    realm.copyToRealm(addModel);
                } else {
                    FaceWffrModel results = realm.where(FaceWffrModel.class)
                            .equalTo("firstName", addModel.getFirstName())
                            .findFirst();
                    if (results == null) {
                        realm.copyToRealm(addModel);
                    }
                }
            }
        });
    }

    /**
     * 删除人脸信息
     */
    public void deleteByFirstName(String firstName) {
        RealmUtils.deleteFirst(FaceWffrModel.class, "firstName", firstName);
    }

    /**
     * 删除人脸信息
     */
    public void deleteByCardNo(String cardNo) {
        RealmUtils.deleteAll(FaceWffrModel.class, "cardNo", cardNo);
    }

    /**
     * 清空人脸信息
     */
    public void clear() {
        RealmUtils.deleteAll(FaceWffrModel.class);
    }

    /**
     * 查询早期的人脸记录（本地补录）
     */
    public List<FaceWffrModel> queryEarly(String cardNo) {
        List<FaceWffrModel> faceList = null;
        Realm realm = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            // 查询人脸信息
            RealmResults<FaceWffrModel> results = realm.where(FaceWffrModel.class)
                    .equalTo("cardNo", cardNo)
                    .like("firstName", "dev-*")
                    .findAllSorted("index", Sort.ASCENDING);
            // 如果大于等于3个，返回最早的FirstName
            if (results.size() > 3) {
                faceList = realm.copyFromRealm(results.subList(0, results.size() - 3));
            }
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);
            }
        }
        return faceList;
    }

    /**
     * 查询人脸信息
     * @return 人脸信息
     */
    public FaceWffrModel queryByFirstName(String firstName) {
        return RealmUtils.queryFirst(FaceWffrModel.class, "firstName", firstName);
    }

    /**
     * 查询人脸信息
     * @param cardNo 卡号
     * @return 人脸信息
     */
    public List<FaceWffrModel> queryByCardNo(String cardNo) {
        return RealmUtils.queryAll(FaceWffrModel.class, "cardNo", cardNo);
    }

    /**
     * 查询人脸信息
     * @return 人脸信息
     */
    public List<FaceWffrModel> queryAll() {
        return RealmUtils.queryAll(FaceWffrModel.class);
    }

    /**
     * 减去可用次数
     * @param firstName 人脸ID
     */
    public void subLifecycle(final String firstName) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                FaceWffrModel model = realm.where(FaceWffrModel.class).equalTo("firstName", firstName).findFirst();
                if (model != null){
                    int lifecycle = model.getLifecycle();
                    if (lifecycle > 0){
                        lifecycle--;
                        model.setLifecycle(lifecycle);
                    }
                }
            }
        });
    }
}
