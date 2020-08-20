package com.mili.smarthome.tkj.dao;


import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.entities.MessageModel;
import com.mili.smarthome.tkj.message.MessageBean;
import com.mili.smarthome.tkj.utils.FileUtils;
import com.mili.smarthome.tkj.utils.SysTimeSetUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MessageDao extends BaseDao {
    private static final int MAX_NUM_INFO = 5;

    /**
     * 增加数据
     */
    public void  addModel(final String title, final String textPath, final String time, final int saveDay) {
        //超过保留天数，删除最后一条信息
        deleteSaveDayDatas();

        //超过五条信息删除最有一条
        if (queryAllCount() >= MAX_NUM_INFO) {
            deleteLastDatas();
        }

        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MessageModel messageModel = realm.createObject(MessageModel.class);
                //存储文本
                messageModel.setTextPath(textPath);
                //存储时间
                messageModel.setDateTime(time);
                //存储时间
                messageModel.setTitle(title);
                //保留天数
                messageModel.setSaveDay(saveDay);
            }
        });
        closeRealm(realm);
    }


    /**
     * 查询信息总条数
     */
    public int queryAllCount() {
        int count;
        Realm realm = getRealm();
        count = (int) realm.where(MessageModel.class).count();
        closeRealm(realm);
        return count;
    }

    /**
     * 获取所有信息
     */
    public List<MessageBean> getAllMessage() {
        Realm realm = getRealm();
        List<MessageBean> list = new ArrayList<>();
        list.clear();

        //超过保留天数，删除最后一条信息
        deleteSaveDayDatas();
        //按时间递增排序显示
        RealmResults<MessageModel> allMessage = realm.where(MessageModel.class).findAll().sort("datetime", Sort.ASCENDING);
        for (int i = 0; i < allMessage.size(); i++) {
            try {
                String content = FileUtils.readSDFile(allMessage.get(i).getTextPath());
                list.add(new MessageBean(allMessage.get(i).getTitle(), content));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        closeRealm(realm);
        return list;
    }


    /**
     * 清空所有信息
     */
    public void clearAllMessage() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MessageModel> messageModels = realm.where(MessageModel.class).findAll();
                //清空文件夹
                File[] fileList = FileUtils.getFileList(CommStorePathDef.INFO_DIR_PATH);
                for (File aFileList : fileList) {
                    FileUtils.deleteFile(aFileList.getPath());
                }

                //清空信息表
                messageModels.deleteAllFromRealm();

            }
        });
        closeRealm(realm);
    }


    /**
     * 删除最后一条数据
     */

    private void deleteLastDatas() {
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //按时间降序排列删除时间最早的一条
                RealmResults<MessageModel> messageModels = realm.where(MessageModel.class).findAll().sort("datetime", Sort.DESCENDING);
                if (messageModels.get(messageModels.size() - 1) != null) {
                    deleteTextFile(messageModels.get(messageModels.size() - 1).getTextPath());
                    //删除数据库最后一条数据
                    messageModels.deleteLastFromRealm();
                }
            }
        });
        closeRealm(realm);
    }

    /**
     * 删除文件
     */
    private void deleteTextFile(String path) {
        if (path != null) {
            FileUtils.deleteFile(path);
        }
    }

    /**
     * 删除超过保留天数的数据
     */

    private void deleteSaveDayDatas() {

        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MessageModel> messageModels = realm.where(MessageModel.class).findAll();

                for (int i = 0; i < messageModels.size(); i++) {
                    long saveTime = Long.parseLong(messageModels.get(i).getDateTime());
                    int saveDay = messageModels.get(i).getSaveDay();

                    if (SysTimeSetUtils.caculateTimeDiff(saveTime,saveDay)) {
                        deleteTextFile(messageModels.get(i).getTextPath());
                        //删除数据
                        messageModels.deleteFromRealm(i);
                    }
                }
            }
        });
        closeRealm(realm);
    }


}
