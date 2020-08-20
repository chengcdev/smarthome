package com.mili.smarthome.tkj.dao;

import com.mili.smarthome.tkj.entities.FingerModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class FingerDao {

    /**
     * 增加指纹信息
     * @param addModel 人脸信息
     */
    public void insert(FingerModel addModel) {
        RealmUtils.insert(addModel);
    }

    /**
     * 增加指纹信息
     * @param fingerId 指纹ID
     * @param valid 指纹有效标志
     * @param fingerInfo 指纹特征值
     * @param roomNo 房号
     */
    public void insert(final int fingerId, final int valid, final byte[] fingerInfo, final String roomNo) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int max = 10;
                if (Integer.valueOf(roomNo) == 0) {
                    max = 20;
                }
                RealmQuery<FingerModel> query = realm.where(FingerModel.class)
                        .equalTo("roomNo", roomNo);
                long count = query.count();
                if (count >= max) {
                    int replaceValid;
                    if (valid > max) {
                        replaceValid = valid - max;
                    } else {
                        replaceValid = valid + max;
                    }
                    FingerModel model = query.equalTo("valid", replaceValid).findFirst();
                    if (model != null) {
                        model.setFingerId(fingerId);
                        model.setValid(valid);
                        model.setFingerInfo(fingerInfo);
                        return; // 完成替换
                    }
                }
                FingerModel model = realm.createObject(FingerModel.class);
                model.setFingerId(fingerId);
                model.setValid(valid);
                model.setFingerInfo(fingerInfo);
                model.setRoomNo(roomNo);
            }
        });
    }

    /**
     * 根据指纹ID删除指纹
     */
    public void deleteByFingerId(final Integer[] fingerIds) {
        if (fingerIds == null || fingerIds.length == 0)
            return;
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<FingerModel> fingerList = realm.where(FingerModel.class)
                        .in("fingerId", fingerIds)
                        .findAll();
                if (fingerList.size() > 0) {
                    fingerList.deleteAllFromRealm();
                }
            }
        });
    }

    /**
     * 删除指纹
     */
    public void deleteByRoomNo(String roomNo) {
        RealmUtils.deleteAll(FingerModel.class, "roomNo", roomNo);
    }

    /**
     * 清空指纹
     */
    public void clear() {
        RealmUtils.deleteAll(FingerModel.class);
    }

    /**
     * 获取所有指纹
     */
    public List<FingerModel> queryAll() {
        return RealmUtils.queryAll(FingerModel.class);
    }
}
