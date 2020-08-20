package com.mili.smarthome.tkj.dao;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Realm工具类，提供查询、添加、更新、删除等操作接口
 */
public class RealmUtils {

    // --->>> 查询

    public static <E extends RealmModel> long count(Class<E> clazz) {
        Realm realm = null;
        long count;
        try {
            realm = RealmHelper.getInstance().getRealm();
            count = realm.where(clazz).count();
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);;
            }
        }
        return count;
    }

    public static <E extends RealmModel> long count(Class<E> clazz, String fieldName, String value) {
        Realm realm = null;
        long count;
        try {
            realm = RealmHelper.getInstance().getRealm();
            count = realm.where(clazz)
                    .equalTo(fieldName, value)
                    .count();
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);;
            }
        }
        return count;
    }

    public static <E extends RealmModel> List<E> queryAll(Class<E> clazz) {
        Realm realm = null;
        List<E> outList = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            RealmResults<E> results = realm.where(clazz).findAll();
            if (results != null) {
                outList = realm.copyFromRealm(results);
            }
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);;
            }
        }
        return outList;
    }

    public static <E extends RealmModel> List<E> queryAll(Class<E> clazz, String fieldName, String value) {
        Realm realm = null;
        List<E> outList = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            RealmResults<E> results = realm.where(clazz)
                    .equalTo(fieldName, value)
                    .findAll();
            if (results != null) {
                outList = realm.copyFromRealm(results);
            }
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);;
            }
        }
        return outList;
    }

    public static <E extends RealmModel> E queryFirst(Class<E> clazz, String fieldName, String value) {
        Realm realm = null;
        E outModel = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            E result = realm.where(clazz)
                    .equalTo(fieldName, value)
                    .findFirst();
            if (result != null) {
                outModel = realm.copyFromRealm(result);
            }
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);;
            }
        }
        return outModel;
    }

    public static <E extends RealmModel> E queryFirst(Class<E> clazz, String fieldName, String value, final String typeName, final int typeValue) {
        Realm realm = null;
        E outModel = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            E result = realm.where(clazz)
                    .equalTo(fieldName, value)
                    .equalTo(typeName, typeValue)
                    .findFirst();
            if (result != null) {
                outModel = realm.copyFromRealm(result);
            }
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);;
            }
        }
        return outModel;
    }

    // --->>> 执行事务

    public static void executeTransaction(Realm.Transaction transaction) {
        Realm realm = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            realm.executeTransaction(transaction);
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);;
            }
        }
    }

    public static void executeTransactionAsync(Realm.Transaction transaction,
                                               Realm.Transaction.OnSuccess onSuccess,
                                               Realm.Transaction.OnError onError) {
        Realm realm = null;
        try {
            realm = RealmHelper.getInstance().getRealm();
            realm.executeTransactionAsync(transaction, onSuccess, onError);
        } finally {
            if (realm != null) {
                RealmHelper.getInstance().closeRealm(realm);
            }
        }
    }

    // --->>> 添加

    public static void insert(final RealmModel model) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(model);
            }
        });
    }

    public static void insert(final List<? extends RealmModel> list) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(list);
            }
        });
    }

    public static void insert(final RealmModel... array) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(Arrays.asList(array));
            }
        });
    }


    // --->>> 更新

    public static void insertOrUpdate(final RealmModel model) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(model);
            }
        });
    }

    public static void insertOrUpdate(final List<? extends RealmModel> list) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        });
    }

    public static void insertOrUpdate(final RealmModel... array) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(Arrays.asList(array));
            }
        });
    }


    // --->>> 删除

    public static <E extends RealmModel> void deleteAll(final Class<E> clazz) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(clazz);
            }
        });
    }

    public static <E extends RealmModel> void deleteAll(final Class<E> clazz, final String fieldName, final String value, final String typeName, final int typeValue) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(clazz)
                        .equalTo(fieldName, value)
                        .equalTo(typeName, typeValue)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public static <E extends RealmModel> void deleteAll(final Class<E> clazz, final String fieldName, final String value) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(clazz)
                        .equalTo(fieldName, value)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public static <E extends RealmModel> void deleteFirst(final Class<E> clazz, final String fieldName, final String value) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(clazz)
                        .equalTo(fieldName, value)
                        .findAll()
                        .deleteFirstFromRealm();
            }
        });
    }
}
