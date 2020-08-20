package com.mili.smarthome.tkj.dao;

import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.entities.ResidentSettingModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ResidentSettingDao extends BaseDao{

    public ResidentSettingDao() {
        ResidentSettingModel model = queryModel();
        if (model == null) {
            addWorkModel();
        }
    }


    /**
     * 查询住户设置参数信息
     */
    public ResidentSettingModel queryModel() {
        Realm realm = getRealm();
        ResidentSettingModel model = realm.where(ResidentSettingModel.class).equalTo("Id", Const.SetDeviceNoId.RESIDENT_SETTING_ID).findFirst();
        if (model != null) {
            model = realm.copyFromRealm(model);
        }
        closeRealm(realm);
        return model;
    }

    /**
     * 添加设置参数信息
     */
    private void addWorkModel() {
        ResidentSettingModel model = new ResidentSettingModel();
        model.setId(Const.SetDeviceNoId.RESIDENT_SETTING_ID);
        model.setRoomNoStart("01");
        model.setFloorHouseNum("02");
        model.setFloorCount("10");
        insertOrUpdate(model);
    }

    /**
     * 更新设置设备编号信息
     */
    public void insertOrUpdate(final ResidentSettingModel model) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(model);
            }
        });
    }

    private ResidentSettingModel getModel(Realm realm) {
        return realm.where(ResidentSettingModel.class).equalTo("Id", Const.SetDeviceNoId.RESIDENT_SETTING_ID).findFirst();
    }

    /**
     * 获取起始房号
     */
    public String getRoomStart(){
        String roomNumStart = "";
        ResidentSettingModel model = queryModel();
        if (model != null) {
            roomNumStart = model.getRoomNoStart();
        }
        return roomNumStart;
    }

    /**
     * 设置起始房号
     */
    public void setRoomStart(final String roomStart){
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ResidentSettingModel model = getModel(realm);
                if (model != null) {
                    model.setRoomNoStart(roomStart);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 获取楼层数
     */
    public String getFloorCount(){
        String floorNum = "";
        ResidentSettingModel model = queryModel();
        if (model != null) {
            floorNum = model.getFloorCount();
        }
        return floorNum;
    }

    /**
     * 设置楼层数
     */
    public void setFloorCount(final String floorCount){
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ResidentSettingModel model = getModel(realm);
                if (model != null) {
                    model.setFloorCount(floorCount);
                }
            }
        });
        closeRealm(realm);
    }


    /**
     * 获取每层户数
     */
    public String getFloorHouseNum(){
        String floorHouseNum = "";
        ResidentSettingModel model = queryModel();
        if (model != null) {
            floorHouseNum = model.getFloorHouseNum();
        }
        return floorHouseNum;
    }

    /**
     * 设置每层户数
     */
    public void setFloorHouseNum(final String floorHouseNum){
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ResidentSettingModel model = getModel(realm);
                if (model != null) {
                    model.setFloorHouseNum(floorHouseNum);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 获取房号列表
     */
    public List<String> getResidentRoomList(){
        List<String> list = new ArrayList<>();
        list.clear();
        int roomStart = Integer.parseInt(getRoomStart());
        int floorHouse = Integer.parseInt(getFloorHouseNum());
        List<String> floorList = getFloorList();
        for (int i = 0; i < floorList.size(); i++) {
            int rStart;
            for (int j = 0; j < floorHouse; j++) {
                rStart = roomStart + j;
                final int finalRStart = rStart;
                list.add(floorList.get(i) + addZero(finalRStart));
            }
        }
        return list;
    }

    /**
     * 层数集合
     */
    private List<String> getFloorList() {
        int fNum = Integer.parseInt(getFloorCount());
        List<String> list = new ArrayList<>();
        list.clear();
        for (int i = 0; i < fNum; i++) {
            if (i < 9) {
                list.add("0" + (i + 1));
            } else {
                list.add((i + 1) + "");
            }
        }
        return list;
    }

    /**
     * 房号小于10补零
     */
    private String addZero(int room) {
        String reult;
        if (room <= 9) {
            reult = "0" + room;
        } else {
            reult = room + "";
        }
        return reult;
    }

}
