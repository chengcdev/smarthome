package com.mili.smarthome.tkj.dao;

import com.android.CommTypeDef;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.entities.deviceno.DeviceNoModel;

import io.realm.Realm;

public class DeviceNoDao extends BaseDao{

    public DeviceNoDao() {
        DeviceNoModel deviceNoModel = queryDeviceNoModel();
        if (deviceNoModel == null) {
            addDeviceNoModel();
        }
    }

    /**
     * 查询设备编号信息
     */
    public DeviceNoModel queryDeviceNoModel() {
        Realm realm = getRealm();
        DeviceNoModel noModel = realm.where(DeviceNoModel.class).equalTo("Id", Const.SetDeviceNoId.DEVICE_NO_ID).findFirst();
        if (noModel != null) {
            noModel = realm.copyFromRealm(noModel);
        }
        closeRealm(realm);
        return noModel;
    }

    /**
     * 更新设置设备编号信息
     */
    private void insertOrUpdate(final DeviceNoModel noModel) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(noModel);
            }
        });
    }

    /**
     * 添加设备编号信息
     */
    private void addDeviceNoModel(){
        //添加数据
        DeviceNoModel model = new DeviceNoModel();
        model.setId(Const.SetDeviceNoId.DEVICE_NO_ID);
        model.setStairNo("01");
        model.setDeviceNo("010100001");
        model.setUseCellNo(1);
        model.setStairNoLen(4);
        model.setRoomNoLen(4);
        model.setCellNoLen(2);
        model.setAreaNo("01");
        model.setCurrentDeviceNo("0101");
        model.setSubSection(224);
        model.setDeviceType(CommTypeDef.DeviceType.DEVICE_TYPE_STAIR);
        insertOrUpdate(model);
    }


    private DeviceNoModel getModel(Realm realm) {
        return realm.where(DeviceNoModel.class).equalTo("Id", Const.SetDeviceNoId.DEVICE_NO_ID).findFirst();
    }


    /**
     * 设置区口号
     */
    public void setAreaNo(final int areaNo) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setAreaNo(String.valueOf(areaNo));
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置设备编号
     */
    public void setDeviceNo(final String deviceNo) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setDeviceNo(deviceNo);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置梯口号长度
     */
    public void setStairNoLen(final int stairNoLen) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setStairNoLen(stairNoLen);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置房号长度
     */
    public void setRoomNoLen(final int roomNoLen) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setRoomNoLen(roomNoLen);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置房号长度
     */
    public void setCellNoLen(final int cellNoLen) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setCellNoLen(cellNoLen);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置是否启用单元号
     *
     */
    public void setUseCellNo(final byte useCellNo) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setUseCellNo(useCellNo);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置分段参数
     */
    public void setSubsection(final int subsection) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setSubSection(subsection);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置设备类型
     */
    public void setDeviceType(final byte deviceType) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setDeviceType(deviceType);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置梯口号
     */
    public void setStairNo(final String stairNo) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setStairNo(stairNo);
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 设置当前界面显示的设备号
     */
    public void setCurrentDeviceNo(final String currentDeviceNo) {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceNoModel model = getModel(realm);
                if (model != null) {
                    model.setCurrentDeviceNo(currentDeviceNo);
                }
            }
        });
        closeRealm(realm);
    }
}
