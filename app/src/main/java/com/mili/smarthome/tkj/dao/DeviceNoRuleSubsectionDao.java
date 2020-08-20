package com.mili.smarthome.tkj.dao;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.entities.deviceno.DeviceNoRuleSubsectionModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 上位机下发设备编号规则描述符
 */

public class DeviceNoRuleSubsectionDao extends BaseDao{

    public DeviceNoRuleSubsectionDao() {
        for (int i = 0; i < 4; i++) {
            DeviceNoRuleSubsectionModel deviceNoRuleSubsectionModel = queryModel(i);
            if (deviceNoRuleSubsectionModel == null) {
                addDeviceSub();
            }
        }

    }

    private void addDeviceSub(){
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    addModel(0, 3, "");
                    break;
                case 1:
                    addModel(1, 3, ContextProxy.getString(R.string.intercalldest_1));
                    break;
                case 2:
                    addModel(2, 3, ContextProxy.getString(R.string.intercalldest_2));
                    break;
                case 3:
                    addModel(3, 3, ContextProxy.getString(R.string.intercalldest_3));
                    break;
            }
        }
    }

    public void addModel(int Id, int subContent, String subsection) {
        DeviceNoRuleSubsectionModel model = new DeviceNoRuleSubsectionModel();
        model.setId(Id);
        model.setSubCount(subContent);
        model.setSubsection(subsection);
        insertOrUpdate(model);
    }

    /**
     * 更新设置设备编号信息
     */
    public void insertOrUpdate(final DeviceNoRuleSubsectionModel model) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(model);
            }
        });
    }

    public DeviceNoRuleSubsectionModel queryModel(int Id){
        Realm realm = getRealm();
        DeviceNoRuleSubsectionModel model = realm.where(DeviceNoRuleSubsectionModel.class).equalTo("Id",Id).findFirst();
        if (model != null) {
            model = realm.copyFromRealm(model);
        }
        closeRealm(realm);
        return model;
    }

    public List<DeviceNoRuleSubsectionModel> queryModel(){
        List<DeviceNoRuleSubsectionModel> list = new ArrayList<>();
        Realm realm = getRealm();
        RealmResults<DeviceNoRuleSubsectionModel> all = realm.where(DeviceNoRuleSubsectionModel.class).findAll();
        if (all != null) {
            list = realm.copyFromRealm(all);
        }
        closeRealm(realm);
        return list;
    }


    public void clearModel() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<DeviceNoRuleSubsectionModel> subsectionModels = realm.where(DeviceNoRuleSubsectionModel.class).findAll();
                subsectionModels.deleteAllFromRealm();
            }
        });
        closeRealm(realm);
    }

    /**
     * 获取表的长度
     */
    public long queryAllCount(){
        Realm realm = getRealm();
        long count = realm.where(DeviceNoRuleSubsectionModel.class).count();
        closeRealm(realm);
        return count;
    }

}
