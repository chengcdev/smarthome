package com.mili.smarthome.tkj.dao;


import com.mili.smarthome.tkj.app.CustomVersion;
import com.mili.smarthome.tkj.appfunc.facefunc.BaseFacePresenter;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MegviiFaceDao {

    private static final long CAPACITY = 50000;//10000;
    private static final long CAPACITY_KAIWEI = 30000;  /*楷唯物业*/

    /**
     * 获取人脸剩余个数
     */
    public long getSurplus() {
        long count = RealmUtils.count(FaceMegviiModel.class);
        if (CustomVersion.VERSION_KAIWEI_WUYE) {
            return Math.max(0, CAPACITY_KAIWEI - count);
        }
        return Math.max(0, CAPACITY - count);
    }

    /**
     * 增加人脸信息
     * @param addModel 人脸信息
     */
    public void insert(final FaceMegviiModel addModel) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean isDev = BaseFacePresenter.isDev(addModel.getFirstName());
                if (isDev) {
                    RealmResults<FaceMegviiModel> results = realm.where(FaceMegviiModel.class)
                            .equalTo("cardNo", addModel.getCardNo())
                            .like("firstName", "dev-*")
                            .findAllSorted("index");
                    for (int i = 0; i < results.size(); i++) {
                        results.get(i).setIndex(i);
                    }
                    addModel.setIndex(results.size());
                    realm.copyToRealm(addModel);
                } else {
                    FaceMegviiModel results = realm.where(FaceMegviiModel.class)
                            .equalTo("firstName", addModel.getFirstName())
                            .findFirst();
                    if (results == null) {
                        realm.copyToRealm(addModel);
                    } else {
                        results.setFaceToken(addModel.getFaceToken());
                    }
                }
            }
        });
    }

    /**
     * 删除人脸信息
     */
    public void deleteByCardNo(String cardNo) {
        RealmUtils.deleteFirst(FaceMegviiModel.class, "cardNo", cardNo);
    }

    /**
     * 删除人脸信息
     */
    public void deleteByFaceToken(String faceToken) {
        RealmUtils.deleteFirst(FaceMegviiModel.class, "faceToken", faceToken);
    }

    /**
     * 删除人脸信息
     */
    public void deleteByFirstName(String firstName) {
        RealmUtils.deleteFirst(FaceMegviiModel.class, "firstName", firstName);
    }

    /**
     * 清空人脸信息
     */
    public void clear() {
        RealmUtils.deleteAll(FaceMegviiModel.class);
    }

    /**
     * 查询早期的人脸记录
     */
    public List<FaceMegviiModel> queryEarly(String cardNo) {
        List<FaceMegviiModel> faceList = null;
        Realm realm = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            // 查询人脸信息
            RealmResults<FaceMegviiModel> results = realm.where(FaceMegviiModel.class)
                    .equalTo("cardNo", cardNo)
                    .like("firstName", "dev-*")
                    .findAllSorted("index", Sort.ASCENDING);
            // 如果大于等于3个，返回最早一个的FirstName
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
    public FaceMegviiModel queryByFaceToken(String faceToken) {
        return RealmUtils.queryFirst(FaceMegviiModel.class, "faceToken", faceToken);
    }

    /**
     * 查询人脸信息
     * @return 人脸信息
     */
    public FaceMegviiModel queryByFirstName(String firstName) {
        return RealmUtils.queryFirst(FaceMegviiModel.class, "firstName", firstName);
    }

    /**
     * 查询人脸信息
     * @param cardNo 卡号
     * @return 人脸信息
     */
    public List<FaceMegviiModel> queryByCardNo(final String cardNo) {
        return RealmUtils.queryAll(FaceMegviiModel.class, "cardNo", cardNo);
    }

    /**
     * 查询人脸信息
     * @return 人脸信息
     */
    public List<FaceMegviiModel> queryAll() {
        return RealmUtils.queryAll(FaceMegviiModel.class);
    }

    /**
     * 减去可用次数
     * @param faceToken 人脸ID
     */
    public void subLifecycle(final String faceToken) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                FaceMegviiModel model = realm.where(FaceMegviiModel.class).equalTo("faceToken", faceToken).findFirst();
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
