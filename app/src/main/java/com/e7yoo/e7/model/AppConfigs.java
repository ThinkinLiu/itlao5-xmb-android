package com.e7yoo.e7.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class AppConfigs extends BmobObject {
    private String shareUrl;
    private List robotIds;
    private String desc;
    private List descs; // 预留
    private Integer ad;


    public List getDescs() {
        return descs;
    }

    public void setDescs(List descs) {
        this.descs = descs;
    }

    public Integer getAd() {
        return ad;
    }

    public void setAd(Integer ad) {
        this.ad = ad;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List getRobotIds() {
        return robotIds;
    }

    public void setRobotIds(List robotIds) {
        this.robotIds = robotIds;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
