package com.mili.smarthome.tkj.message;


import java.io.Serializable;

public class MessageBean implements Serializable {

    //标题
    private String title;
    //内容
    private String content;

    public MessageBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
