package com.e7yoo.e7.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2018/5/9.
 */

public class comment extends BmobObject {
    private static final long serialVersionUID = 101L;

    private feed srcFeed; // 原贴
    private BmobRelation likes; // 喜欢
    private List images; // 图片
    private String extra; // 拓展字段 json
    private String content; // 内容
    private User author; // 作者

    public feed getSrcFeed() {
        return srcFeed;
    }

    public void setSrcFeed(feed srcFeed) {
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
}
