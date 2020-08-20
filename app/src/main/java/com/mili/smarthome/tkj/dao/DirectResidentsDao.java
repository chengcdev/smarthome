package com.mili.smarthome.tkj.dao;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.entities.DirectResidentsModel;

import java.util.List;

import io.realm.Realm;

public class DirectResidentsDao extends BaseDao {

    public static final String ROOM_NO_MANAGE = "0";

    /**
     * 添加住户列表
     *
     * @param residentRoomList 住户列表
     */
    public static void addResidentList(final List<String> residentRoomList) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                for (String roomNo : residentRoomList) {
                    DirectResidentsModel residentsModel = realm.where(DirectResidentsModel.class).equalTo("roomNo", roomNo).findFirst();
                    if (residentsModel == null) {
                        //添加住户
                        DirectResidentsModel directResidentsModel = new DirectResidentsModel();
                        directResidentsModel.setRoomNo(roomNo);
                        //管理员机
                        if (roomNo.equals(ROOM_NO_MANAGE)) {
                            directResidentsModel.setRoomName(ContextProxy.getString(R.string.manage_center));
                        }else {
                            directResidentsModel.setRoomName(roomNo);
                        }
                        realm.copyToRealmOrUpdate(directResidentsModel);
                    }
                }

//                for (int i = 0; i < residentRoomList.size(); i++) {
//                    String roomNo = residentRoomList.get(i);
//                    DirectResidentsModel residentsModel = realm.where(DirectResidentsModel.class).equalTo("roomNo", roomNo).findFirst();
//                    if (residentsModel == null) {
//                        //添加住户
//                        DirectResidentsModel directResidentsModel = new DirectResidentsModel();
//                        directResidentsModel.setRoomNo(roomNo);
//                        //管理员机
//                        if (roomNo.equals(ROOM_NO_MANAGE)) {
//                            directResidentsModel.setRoomName(ContextProxy.getString(R.string.manage_center));
//                        }else {
//                            directResidentsModel.setRoomName(roomNo);
//                        }
//                        realm.copyToRealmOrUpdate(directResidentsModel);
//                    }
//                }
            }
        });
    }

    public static List<DirectResidentsModel> queryResidentList() {
      return RealmUtils.queryAll(DirectResidentsModel.class);
    }

    /**
     * 查询住户
     * @param roomNo 查询的房号
     */
    public static DirectResidentsModel queryResidentFirst(String roomNo) {
        return RealmUtils.queryFirst(DirectResidentsModel.class,"roomNo",roomNo);
    }

    /**
     * 设置住户列表
     * @param roomNo 房号
     * @param roomName 房名
     */
    public static void setResidentRoomName(final String roomNo, final String roomName) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DirectResidentsModel directResidentsModel = realm.where(DirectResidentsModel.class)
                        .equalTo("roomNo", roomNo).findFirst();
                if (directResidentsModel != null) {
                    directResidentsModel.setRoomName(roomName);
                }
            }
        });
    }
}
