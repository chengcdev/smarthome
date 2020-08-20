package com.mili.smarthome.tkj.dao;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;

public abstract class BaseDao {

    protected final Realm getRealm() {
        return RealmHelper.getInstance().getRealm();
    }

    protected final void closeRealm(Realm realm) {
        RealmHelper.getInstance().closeRealm(realm);
    }

    protected final void executeTransaction(Realm.Transaction transaction) {
        RealmUtils.executeTransaction(transaction);
    }

    protected final <E extends RealmModel> long count(Class<E> clazz) {
        return RealmUtils.count(clazz);
    }

    protected final <E extends RealmModel> List<E> queryAll(Class<E> clazz) {
        return RealmUtils.queryAll(clazz);
    }

    protected final <E extends RealmModel> void clear(Class<E> clazz) {
        RealmUtils.deleteAll(clazz);
    }

    protected final void insert(RealmModel object) {
        RealmUtils.insert(object);
    }

    protected final void insertOrUpdate(RealmModel object) {
        RealmUtils.insertOrUpdate(object);
    }
}
