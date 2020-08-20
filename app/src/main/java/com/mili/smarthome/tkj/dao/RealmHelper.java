package com.mili.smarthome.tkj.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.entities.DirectResidentsModel;
import com.mili.smarthome.tkj.utils.LogUtils;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Realm帮助类，实现创建Realm数据库，版本升级，以及Realm实例的管理
 */
public final class RealmHelper {

    /** 枚举当前已知的数据库版本号 */
    public class VERSION {
        /**
         * 数据库初始版本
         */
        public static final long BASE_VERSION = 0L;
        /**
         * k7 添加一个直按式住户列表数据表(DirectResidentsModel)
         */
        public static final long V20200102 = 1L;

        /**
         * 卡数据库增加用户ID、时间等字段
         */
        public static final long V20200303 = 2L;
        /**
         * 人脸数据库增加用户ID、时间等字段
         */
        public static final long V20200315 = 3L;
        /**
         * 增加开门提醒数据库
         */
        public static final long V20200327 = 5L;
    }

    private static RealmHelper mInstance;
    public static RealmHelper getInstance() {
        if (mInstance == null) {
            synchronized (RealmHelper.class) {
                if (mInstance == null) {
                    mInstance = new RealmHelper(ContextProxy.getContext());
                }
            }
        }
        return mInstance;
    }

    /** 数据库名称 */
    private static final String REALM_NAME = "ml8_android_tkj_" + BuildConfig.FLAVOR_MODEL + ".realm";
    /** 数据库当前版本 */
    private static final long REALM_VERSION = RealmHelper.VERSION.V20200327;

    private RealmConfiguration realmConfig;

    private RealmHelper(Context context) {
        Realm.init(context);
        realmConfig = new RealmConfiguration.Builder()
                .name(REALM_NAME)
                .schemaVersion(REALM_VERSION)
                .migration(new RealmMigrationImpl())
                .build();
    }

    public Realm getRealm() {
        return Realm.getInstance(realmConfig);
    }

    public void closeRealm(@NonNull Realm realm) {
        realm.close();
    }

    public void compactRealm() {
        Realm.compactRealm(realmConfig);
    }

    private class RealmMigrationImpl implements RealmMigration {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            LogUtils.d("REALM MIGRATE: from " + oldVersion + " to " + newVersion);
            RealmSchema realmSchema = realm.getSchema();
            for (long ver = oldVersion; ver < newVersion; ver++) {
                if (ver == VERSION.BASE_VERSION) { // --> VERSION.V20200102
                    //k7 添加一个直按式住户列表数据表
                    RealmObjectSchema interSetingmodel = realmSchema.create(DirectResidentsModel.class.getSimpleName());
                    interSetingmodel.addField("roomNo", String.class, FieldAttribute.PRIMARY_KEY);
                    interSetingmodel.addField("roomName", String.class);

                } else if (ver == VERSION.V20200102) {
                    RealmObjectSchema CardModel = realmSchema.get("UserCardInfoModels");
                    CardModel.addField("roomNoState", int.class);
                    CardModel.addField("keyID", String.class);
                    CardModel.addField("startTime", int.class);
                    CardModel.addField("endTime", int.class);
                    CardModel.addField("lifecycle", int.class);

                    RealmObjectSchema pwdModel = realmSchema.get("UserPwdModels");
                    pwdModel.addField("roomNoState", int.class);
                    pwdModel.addField("keyID", String.class);
                    pwdModel.addField("attri", int.class);
                    pwdModel.addField("startTime", int.class);
                    pwdModel.addField("endTime", int.class);
                    pwdModel.addField("lifecycle", int.class);
                } else if (ver == VERSION.V20200303) {
                    RealmObjectSchema megviiModel = realmSchema.get("FaceMegviiModel");
                    megviiModel.addField("roomNoState", int.class);
                    megviiModel.addField("keyID", String.class);
                    megviiModel.addField("exturl", String.class);
                    megviiModel.addField("attri", int.class);
                    megviiModel.addField("startTime", int.class);
                    megviiModel.addField("endTime", int.class);
                    megviiModel.addField("lifecycle", int.class);

                    RealmObjectSchema wffrModel = realmSchema.get("FaceWffrModel");
                    wffrModel.addField("roomNoState", int.class);
                    wffrModel.addField("keyID", String.class);
                    wffrModel.addField("exturl", String.class);
                    wffrModel.addField("attri", int.class);
                    wffrModel.addField("startTime", int.class);
                    wffrModel.addField("endTime", int.class);
                    wffrModel.addField("lifecycle", int.class);
                }else if (ver == VERSION.V20200315) {
                    RealmObjectSchema doorReminderModel=realmSchema.create("DoorReminderModel");
                    doorReminderModel.addField("flagType",int.class);
                    doorReminderModel.addField("flagID",String.class);
                    doorReminderModel.addField("voiceText",String.class);
                    doorReminderModel.addField("startTime", int.class);
                    doorReminderModel.addField("endTime", int.class);
                }
                else if (ver == VERSION.V20200327){

                }
            }
        }
    }
}
