package com.mili.smarthome.tkj.dao;

import com.android.CommTypeDef;
import com.android.Common;
import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.entities.userInfo.UserPwdModels;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.OTHER_FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;
import static com.android.CommTypeDef.LifecycleMode.VALID_NULL_MODE;
import static com.android.CommTypeDef.LifecycleMode.VALID_TIME_MODE;

public class UserInfoDao {

    public UserInfoDao() {
    }

    /**
     * 添加单张卡
     */
    public void addCard(String cardNo, String roomNo, int cardType) {
        UserCardInfoModels model = new UserCardInfoModels();
        model.setCardNo(cardNo);
        model.setRoomNo(roomNo);
        model.setCardType(cardType);
        model.setRoomNoState(0);
        model.setKeyID("");
        model.setStartTime(0);
        model.setEndTime(0);
        model.setLifecycle(-2);
        RealmUtils.insertOrUpdate(model);
    }

    /**
     * 添加单张卡
     */
    public void addCard(final UserCardInfoModels model) {
        RealmUtils.insertOrUpdate(model);
    }

    /**
     * 批量添加卡
     */
    public void addCards(final UserCardInfoModels[] cardList) {
        RealmUtils.insertOrUpdate(cardList);
    }

    /**
     * 批量添加卡
     */
    public void addCards(final List<UserCardInfoModels> cardList) {
        RealmUtils.insertOrUpdate(cardList);
    }

    /**
     * 删除单张卡
     */
    public void deleteCard(final String cardNo) {
        RealmUtils.deleteFirst(UserCardInfoModels.class, "cardNo", cardNo);
    }

    /**
     * 删除某住户的所有卡
     */
    public void deleteUserCards(final String roomNum) {
        if (Integer.parseInt(roomNum) == 0)
        {
            RealmUtils.deleteAll(UserCardInfoModels.class, "roomNo", roomNum, "roomNoState", 0);
        }
        else {
            RealmUtils.deleteAll(UserCardInfoModels.class, "roomNo", roomNum);
        }
    }


    /**
     * 清空所有卡
     */
    public void clearAllCards() {
        RealmUtils.deleteAll(UserCardInfoModels.class);
    }

    public UserCardInfoModels queryByCardNo(String cardNo) {
        return RealmUtils.queryFirst(UserCardInfoModels.class, "cardNo", cardNo);
    }

    public List<UserCardInfoModels> queryByRoomNo(String roomNo) {
        return RealmUtils.queryAll(UserCardInfoModels.class, "roomNo", roomNo);
    }

    public String getRoomNoByCardNo(String cardNo){
        UserCardInfoModels model = RealmUtils.queryFirst(UserCardInfoModels.class, "cardNo", cardNo, "roomNoState", 0);
        return model == null ? null : model.getRoomNo();
    }

    /**
     * 查询所有卡
     */
    public List<UserCardInfoModels> queryAllCards() {
        return RealmUtils.queryAll(UserCardInfoModels.class);
    }

    /**
     * 添加开门密码
     * 房号 -1 管理员密码
     * 房号是0，可以添加多个密码
     */
    public void addOpenPwd(final String roomNo, final String openPwd) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserPwdModels model = realm.where(UserPwdModels.class).equalTo("roomNo", roomNo).findFirst();
                if (model != null && Integer.valueOf(roomNo) != 0) {
                    model.setOpenDoorPwd(openPwd);
                    model.setRoomNoState(0);
                    model.setKeyID("");
                    model.setAttri(1);
                    model.setStartTime(0);
                    model.setEndTime(0);
                    model.setLifecycle(-2);
                }else {
                    //添加
                    UserPwdModels userPwdModels = realm.createObject(UserPwdModels.class);
                    userPwdModels.setRoomNo(roomNo);
                    userPwdModels.setOpenDoorPwd(openPwd);
                    userPwdModels.setRoomNoState(0);
                    userPwdModels.setKeyID("");
                    userPwdModels.setAttri(1);
                    userPwdModels.setStartTime(0);
                    userPwdModels.setEndTime(0);
                    userPwdModels.setLifecycle(-2);
                }
            }
        });

    }

    /**
     * 添加开门密码
     * 房号 -1 管理员密码
     * 房号是0，可以添加多个密码
     */
    public void addOpenPwd(final UserPwdModels pwdmodel) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserPwdModels model = realm.where(UserPwdModels.class).equalTo("roomNo", pwdmodel.getRoomNo()).findFirst();
                if (model != null && Integer.valueOf(pwdmodel.getRoomNo()) != 0) {
                    model.setOpenDoorPwd(pwdmodel.getOpenDoorPwd());
                    model.setRoomNoState(pwdmodel.getRoomNoState());
                    model.setKeyID(pwdmodel.getKeyID());
                    model.setAttri(pwdmodel.getAttri());
                    model.setStartTime(pwdmodel.getStartTime());
                    model.setEndTime(pwdmodel.getEndTime());
                    model.setLifecycle(pwdmodel.getLifecycle());
                }else {
                    //添加
                    UserPwdModels userPwdModels = realm.createObject(UserPwdModels.class);
                    userPwdModels.setOpenDoorPwd(pwdmodel.getOpenDoorPwd());
                    userPwdModels.setRoomNo(pwdmodel.getRoomNo());
                    userPwdModels.setRoomNoState(pwdmodel.getRoomNoState());
                    userPwdModels.setKeyID(pwdmodel.getKeyID());
                    userPwdModels.setAttri(pwdmodel.getAttri());
                    userPwdModels.setStartTime(pwdmodel.getStartTime());
                    userPwdModels.setEndTime(pwdmodel.getEndTime());
                    userPwdModels.setLifecycle(pwdmodel.getLifecycle());
                }
            }
        });
    }

    /**
     * 减去可用次数
     * @param openPwd 开门密码
     */
    public void subLifecycle(final String openPwd) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserPwdModels model = realm.where(UserPwdModels.class).equalTo("openDoorPwd", openPwd).findFirst();
                if (model != null){
                    int lifecycle = model.getLifecycle();
                    if (lifecycle > 0){
                        lifecycle--;
                        model.setLifecycle(lifecycle);
                    }
                }
            }
        });
    }

    /**
     * 删除开门密码
     */
    public void deleteOpenPwd(final String roomNo) {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (Double.valueOf(roomNo) == 0) {
                    RealmResults<UserPwdModels> userPwdModels = realm.where(UserPwdModels.class).equalTo("roomNo", roomNo).equalTo("roomNoState", 0).findAll();
                    userPwdModels.deleteAllFromRealm();
                } else {
                    UserPwdModels model = realm.where(UserPwdModels.class).equalTo("roomNo", roomNo).findFirst();
                    if (model != null) {
                        model.deleteFromRealm();
                    }
                }
            }
        });
    }

        /**
         * 删除开门密码
         * @param roomNo 房号状态
         * @param pwdNo  密码
         * @param roomNoState 0:有房号 1：无房号
         */
        public void deleteOpenPwd(final String roomNo, final String pwdNo,final int roomNoState) {
            RealmUtils.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (Double.valueOf(roomNo) == 0) {
                        if (roomNoState == 1){
                            UserPwdModels model = realm.where(UserPwdModels.class).equalTo("roomNo", roomNo)
                                    .equalTo("openDoorPwd",pwdNo) .equalTo("roomNoState", roomNoState).findFirst();
                            if (model != null) {
                                model.deleteFromRealm();
                            }
                        }
                        else {
                            RealmResults<UserPwdModels> userPwdModels = realm.where(UserPwdModels.class).equalTo("roomNo", roomNo).equalTo("roomNoState", roomNoState).findAll();
                            userPwdModels.deleteAllFromRealm();
                        }
                    } else {
                        UserPwdModels model = realm.where(UserPwdModels.class).equalTo("roomNo", roomNo).findFirst();
                        if (model != null) {
                            model.deleteFromRealm();
                        }
                    }
                }
            });
    }

    /**
     * 清空所有开门密码
     */
    public void clearAllOpenPwd() {
        RealmUtils.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UserPwdModels> userPwdModels = realm.where(UserPwdModels.class).findAll();
                if (userPwdModels != null) {
                    for (UserPwdModels models : userPwdModels) {
                        models.deleteFromRealm();
                    }
                }
            }
        });
    }

    /**
     * 通过开门密码数据
     * @param openPwd 密码
     */
    public UserPwdModels getUserPwdModel(String openPwd) {
        UserPwdModels models = RealmUtils.queryFirst(UserPwdModels.class, "openDoorPwd", openPwd);
        return models;
    }

    /**
     * 通过开门密码获取房号
     * @param openPwd 房号
     */
    public String getRoomNo(String openPwd) {
        UserPwdModels models = RealmUtils.queryFirst(UserPwdModels.class, "openDoorPwd", openPwd);
        return models == null ? "" : models.getRoomNo();
    }

    /**
     * 验证简易开门密码
     */
    public boolean verifyPwd(String openPwd){

        if (!AuthManage.isAuth()) {
            return false;
        }

        UserPwdModels models = RealmUtils.queryFirst(UserPwdModels.class, "openDoorPwd", openPwd);
        return models != null;
    }

    /**
     * 验证简易开门密码
     * @param openPwd  密码
     * @param otherParam 其他参数
     * @return
     */
    public int verifyPwd(String openPwd, boolean otherParam){
        int result = SUCCESS_STATE;
        if (!AuthManage.isAuth()) {
            return OTHER_FAIL_STATE;
        }

        UserPwdModels models = RealmUtils.queryFirst(UserPwdModels.class, "openDoorPwd", openPwd);
        if (models != null){
            if (BuildConfig.isEnabledPwdValid){
                if (models.getKeyID() == null && models.getLifecycle() == 0){
                    models.setLifecycle(-2);
                }
                result = Common.judge_validity(models.getLifecycle(), models.getStartTime(), models.getEndTime());
            }
        }
        else{
            result = FAIL_STATE;
        }
        return  result;
    }

    /**
     * 获取管理员密码
     */
    public String getAdminPwd() {
        return ParamDao.getAdminPwd();
    }

    /**
     * 设置管理员密码
     * @param adminPwd 管理员密码
     */
    public void setAdminPwd(String adminPwd) {
        ParamDao.setAdminPwd(adminPwd);
    }
}
