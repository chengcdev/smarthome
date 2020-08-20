package com.mili.smarthome.tkj.entities.param;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * 参数表，存储各类键值对参数
 */
public class ParamModel extends RealmObject {

    public static final String FIELD_KEY = "key";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_VALUE = "value";

    @PrimaryKey
    private String key;
    @Required
    private String type;
    private String value;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return Integer.valueOf(value);
    }

    public ParamModel setKey(String key) {
        this.key = key;
        return this;
    }

    public ParamModel setType(String type) {
        this.type = type;
        return this;
    }

    public ParamModel setValue(String value) {
        this.value = value;
        return this;
    }

    public ParamModel setValue(int value) {
        this.value = Integer.toString(value);
        return this;
    }
}
