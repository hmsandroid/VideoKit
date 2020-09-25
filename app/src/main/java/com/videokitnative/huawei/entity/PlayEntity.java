package com.videokitnative.huawei.entity;

import java.io.Serializable;

public class PlayEntity implements Serializable {
    private static final long serialVersionUID = -5470245891598196978L;

    /**
     * The video name
     */
    private String name;

    /**
     * The url type:
     * 0: a single broadcast address
     * 1: multiple broadcast address
     */
    private int urlType = 0;

    /**
     * The video url
     */
    private String url;

    /**
     * Huawei video ID
     */
    private String appId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUrlType() {
        return urlType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}

