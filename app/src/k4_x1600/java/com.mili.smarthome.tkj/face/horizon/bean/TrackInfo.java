package com.mili.smarthome.tkj.face.horizon.bean;

import hobot.sunrise.sdk.jni.LmkXy;
import hobot.sunrise.sdk.jni.Quality;
import hobot.sunrise.sdk.jni.Rect;

public class TrackInfo {
    public boolean onePer;
    public Rect rect;
    public LmkXy[] xy;
    public int lmk_len;
    public Quality quality_detail;
    public long ts;
    public int overall_quality;
    public TrackInfo info[];
}
