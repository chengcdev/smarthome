package com.mili.smarthome.tkj.dao;

import com.android.client.ScanQrClient;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.entities.BlueToothOpenModel;
import com.mili.smarthome.tkj.utils.EthernetUtils;

import io.realm.Realm;

public class BluetoothOpenDoorDao extends BaseDao{

    public static final String SETTING_DOOR_BLUE = "setting_door_blue";

    /**
     * 查询
     */
    public BlueToothOpenModel queryModel() {
        Realm realm = getRealm();
        BlueToothOpenModel model = realm.where(BlueToothOpenModel.class).equalTo("Id", SETTING_DOOR_BLUE).findFirst();
        if (model != null) {
            model = realm.copyFromRealm(model);
        }
        closeRealm(realm);
        return model;
    }

    /**
     * 添加设备注册Id
     * @param registerId 设备注册Id
     * @param codeString ScanQrClient.getInstance().GetQRencode(currentRegisterId);
     */
    public void addModel(String registerId,String codeString) {
        BlueToothOpenModel model = new BlueToothOpenModel();
        model.setId(SETTING_DOOR_BLUE);
        model.setRegisterId(registerId);
        model.setQrCodeString(codeString);
        insertOrUpdate(model);
    }

    /**
     * 添加设备注册Id
     * @param registerId 设备注册Id
     */
    public void addModel(String registerId) {
        if (registerId == null || registerId.equals("")) {
             return;
        }
        String codeString = ScanQrClient.getInstance().GetQRencode(registerId);
        BlueToothOpenModel model = new BlueToothOpenModel();
        model.setId(SETTING_DOOR_BLUE);
        model.setRegisterId(registerId);
        model.setQrCodeString(codeString);
        insertOrUpdate(model);
    }


    /**
     * 更新设置设备编号信息
     */
    public void insertOrUpdate(final BlueToothOpenModel model) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(model);
            }
        });
    }


    /**
     * 获取设备注册Id
     */
    public String getRegisterId(){
        String registerId = "";
        BlueToothOpenModel model = queryModel();
        if (model != null) {
            registerId = model.getRegisterId();
        }
        return registerId;
    }


    /**
     * 获取设备注册IDs加密的二维码字符串
     */
    public String getQrCodeString(){

        if (!AuthManage.isAuth()) {
            return BuildConfigHelper.getSoftWareVer()
                    + "_" + BuildConfigHelper.getHardWareVer()
                    + "_" + EthernetUtils.getMacAddress();
        }

        String code = "";
        BlueToothOpenModel model = queryModel();
        if (model != null) {
            code = model.getQrCodeString();
        }
        return code;
    }

}
