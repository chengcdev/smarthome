package com.mili.smarthome.tkj.face.horizon.realm;

import com.mili.smarthome.tkj.dao.RealmUtils;

import java.util.List;

import io.realm.Realm;

public class HorizonFaceDao {

    private static final long CAPACITY = 10000;

    /**
     * 获取人脸剩余个数
     */
    public long getSurplus() {
        long count = RealmUtils.count(HorizonFaceModel.class);
        return Math.max(0, CAPACITY - count);
    }

    /**
     * 增加人脸信息
     */
    public void insert(final HorizonFaceModel addModel) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(addModel);
            }
        });
    }

    /**
     * 删除人脸信息
     */
    public void deleteByFirstName(final String firstName) {
        RealmUtils.deleteFirst(HorizonFaceModel.class, "firstName", firstName);
    }

    /**
     * 删除人脸信息
     */
    public void deleteByCardNo(final String cardNo) {
        RealmUtils.deleteAll(HorizonFaceModel.class, "cardNo", cardNo);
    }

    /**
     * 清空人脸信息
     */
    public void clear() {
        RealmUtils.deleteAll(HorizonFaceModel.class);
    }

    /**
     * 查询人脸信息
     * @return 人脸信息
     */
    public HorizonFaceModel queryByFirstName(String firstName) {
        return RealmUtils.queryFirst(HorizonFaceModel.class, "firstName", firstName);
    }

    /**
     * 查询人脸信息
     * @param cardNo 卡号
     * @return 人脸信息
     */
    public List<HorizonFaceModel> queryByCardNo(String cardNo) {
        return RealmUtils.queryAll(HorizonFaceModel.class, "cardNo", cardNo);
    }

    /**
     * 查询人脸信息
     * @return 人脸信息
     */
    public List<HorizonFaceModel> queryAll() {
        return RealmUtils.queryAll(HorizonFaceModel.class);
    }


}
