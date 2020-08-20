package com.mili.smarthome.tkj.dao;

import com.mili.smarthome.tkj.entities.DoorReminderModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class DoorReminderDao {
    public void addDoorReminder(final DoorReminderModel model){
        if (model.getFlagID().length() == 0){
            return;
        }
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DoorReminderModel doorReminderModel = realm.where(DoorReminderModel.class).equalTo("flagID", model.getFlagID()).findFirst();
                if (doorReminderModel != null){
                    doorReminderModel.setFlagType(model.getFlagType());
                    doorReminderModel.setVoiceText(model.getVoiceText());
                    doorReminderModel.setStartTime(model.getStartTime());
                    doorReminderModel.setEndTime(model.getEndTime());
                }
                else{
                    DoorReminderModel mDoorReminderModel = realm.createObject(DoorReminderModel.class);
                    mDoorReminderModel.setFlagType(model.getFlagType());
                    mDoorReminderModel.setFlagID(model.getFlagID());
                    mDoorReminderModel.setVoiceText(model.getVoiceText());
                    mDoorReminderModel.setStartTime(model.getStartTime());
                    mDoorReminderModel.setEndTime(model.getEndTime());
                }
            }
        });
    }

    /**
     * 删除某房号所有数据
     */
    public void deleteDoorReminder(final String flagID) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<DoorReminderModel> doorReminderModels = realm.where(DoorReminderModel.class).equalTo("flagID", flagID).findAll();
                doorReminderModels.deleteAllFromRealm();
            }
        });
    }


    /**
     * 清空所有提示消息
     */
    public void clearAllDoorReminders() {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<DoorReminderModel> doorReminderModels = realm.where(DoorReminderModel.class).findAll();
                if (doorReminderModels != null) {
                    for (DoorReminderModel models : doorReminderModels) {
                        models.deleteFromRealm();
                    }
                }
            }
        });
    }

    public DoorReminderModel queryByFlagID (String flagID){
        return RealmUtils.queryFirst(DoorReminderModel.class, "flagID", flagID);
    }
}
