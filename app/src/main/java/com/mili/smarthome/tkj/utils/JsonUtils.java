package com.mili.smarthome.tkj.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 提供json的生成和解析接口
 * @author
 *      2017-08-01: Created by zenghm.
 */
public class JsonUtils {

    private static Gson miGson;

    public static Gson miGson() {
        if (miGson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            builder.serializeSpecialFloatingPointValues();
            builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                    return expose == null || !expose.serialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            });
            miGson = builder.create();
        }
        return miGson;
    }

    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String jsonString, Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, classOfT);
    }

    public static <T> T fromJson(String jsonString, Type typeOfT) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, typeOfT);
    }

    public static Map<String, Object> toMap(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keys.hasNext()) {
                key = keys.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (JSONException e) {
            LogUtils.printThrowable(e);
        }
        return null;
    }
}
