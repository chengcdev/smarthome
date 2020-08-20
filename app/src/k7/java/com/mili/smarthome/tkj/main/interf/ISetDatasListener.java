package com.mili.smarthome.tkj.main.interf;

public interface ISetDatasListener {

    void getContent(int viewId, String content);

    void lastCotent(int viewId, boolean isLast);

    void firstCotent(int viewId, boolean isFirst);
}
