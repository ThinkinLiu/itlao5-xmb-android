package com.e7yoo.e7.model;

import android.text.TextUtils;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2018/5/9.
 */

public class Feed extends BmobObject {
    private static final long serialVersionUID = 1L;

    public static final int FeedType_TOPIC = 0;
    public static final int FeedType_JOKE = 1;
    public static final int FeedType_PIC = 2;
    private Integer type; // 类型 0 普通帖子，1 笑话， 2 趣图，其他待拓展
    private String title; // 标题
    private BmobPointer srcFeed; // 原贴
    private BmobRelation likes; // 喜欢
    private List images; // 图片
    private BmobRelation favs; // 收藏
    private String extra; // 拓展字段 json
    private String content; // 内容
    private User author; // 作者
    private BmobGeoPoint addr; // 经纬度（发帖时）
    private String addrName; // 发帖地址名称
    private String addrDetails; // 发帖详细地址
    private String time; // 发帖时间
    private String img; // 单张图片

    public Integer getType() {
        if(type == null) {
            type = 0;
        }
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BmobPointer getSrcFeed() {
        return srcFeed;
    }

    public void setSrcFeed(BmobPointer srcFeed) {
        this.srcFeed = srcFeed;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public List getImages() {
        return images;
    }

    public void setImages(List images) {
        this.images = images;
    }

    public BmobRelation getFavs() {
        return favs;
    }

    public void setFavs(BmobRelation favs) {
        this.favs = favs;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public BmobGeoPoint getAddr() {
        return addr;
    }

    public void setAddr(BmobGeoPoint addr) {
        this.addr = addr;
    }

    public String getAddrName() {
        return addrName;
    }

    public void setAddrName(String addrName) {
        this.addrName = addrName;
    }

    public String getAddrDetails() {
        return addrDetails;
    }

    public void setAddrDetails(String addrDetails) {
        this.addrDetails = addrDetails;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
