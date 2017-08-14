package com.delsart.bookdownload.bean;

/**
 * Created by Delsart on 2017/8/2.
 */

public class DownloadBean {
    String type;

    String url;

    public DownloadBean(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public DownloadBean() {
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
