package com.mili.smarthome.tkj.face.horizon.realm;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class HorizonFaceModel extends RealmObject {

    private String firstName;
    private String lastName;
    private String cardNo;

    @Ignore
    private float similar;//相似度
    @Ignore
    private String snapPath;//抓拍路径

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public float getSimilar() {
        return similar;
    }

    public String getSnapPath() {
        return snapPath;
    }

    public long getTimestamp() {
        long timestamp = 0;
        if (!TextUtils.isEmpty(firstName)) {
            String[] seq = firstName.split("-");
            if (seq.length == 3)
                timestamp = Long.valueOf(seq[2]);
        }
        return timestamp;
    }

    public HorizonFaceModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public HorizonFaceModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public HorizonFaceModel setCardNo(String cardNo) {
        this.cardNo = cardNo;
        return this;
    }

    public HorizonFaceModel setSimilar(float similar) {
        this.similar = similar;
        return this;
    }

    public HorizonFaceModel setSnapPath(String snapPath) {
        this.snapPath = snapPath;
        return this;
    }
}
