package com.mili.smarthome.tkj.dao;

import android.content.Intent;

import com.android.CommStorePathDef;
import com.android.Common;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.entities.BlueToothOpenModel;
import com.mili.smarthome.tkj.entities.DirectResidentsModel;
import com.mili.smarthome.tkj.entities.MessageModel;
import com.mili.smarthome.tkj.entities.ResidentSettingModel;
import com.mili.smarthome.tkj.entities.deviceno.DeviceNoModel;
import com.mili.smarthome.tkj.entities.deviceno.DeviceNoRuleSubsectionModel;
import com.mili.smarthome.tkj.entities.param.ParamModel;
import com.mili.smarthome.tkj.utils.FileUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.io.File;
import java.util.Objects;

import io.realm.Realm;

public class ResetFactoryDao {

    //恢复出厂操作
    public void resetDatas(){
        RealmUtils.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // 清除缓存
                FileUtils.clearAllCache(Objects.requireNonNull(ContextProxy.getContext()));
                //清除文件安夹
                File file_mnt = new File(CommStorePathDef.USERDATA_PATH);
                Common.RecursionDeleteFile(file_mnt);

                //删除数据库（不删除卡、密码、人脸、指纹）
                realm.delete(DeviceNoModel.class);
                realm.delete(DeviceNoRuleSubsectionModel.class);
                realm.delete(BlueToothOpenModel.class);
                realm.delete(MessageModel.class);
                realm.delete(ResidentSettingModel.class);
                realm.delete(ParamModel.class);
                realm.delete(DirectResidentsModel.class);
                //realm.deleteAll();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                AppPreferences.setReset(true);
                //删除成功
                if (BuildConfigHelper.isPad()) {
                    //pad
                    Intent intent = new Intent(Const.Action.RESET);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ContextProxy.getContext().startActivity(intent);
                }else {
                    //重启app
                    SystemSetUtils.rebootDevice();
                }

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //删除失败
                LogUtils.e("删除失败...");
            }
        });
    }
}
