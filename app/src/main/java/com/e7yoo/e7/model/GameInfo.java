package com.e7yoo.e7.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/25.
 */

public class GameInfo implements Serializable {
    private static final long serialVersionUID = -1L;
    private int _id;
    private String name;//游戏名
    private String content;// 描述
    private String type;// 游戏类型，射击/竞技/消除/单机/网络等
    private String icon;//游戏截图
    private String big_icon;//游戏大图
    private int game_type; // 0 本地集成游戏， 1 h5竖屏游戏， 2 h5横屏游戏 3，打开本地游戏app
    private String h5_url;// h5地址
    private String game_path;// game_type为0时表示本地集成游戏名（用于判断是哪个游戏），为3时表示app包名
    private String share_url;
    private String share_title;
    private String share_content;
    private String share_image;
    private String desc; // 备用字段

    public GameInfo(String name, String content, int game_type, String h5_url, String game_path) {
        this.name = name;
        this.content = content;
        this.game_type = game_type;
        this.h5_url = h5_url;
        this.game_path = game_path;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBig_icon() {
        return big_icon;
    }

    public void setBig_icon(String big_icon) {
        this.big_icon = big_icon;
    }

    public int getGame_type() {
        return game_type;
    }

    public void setGame_type(int game_type) {
        this.game_type = game_type;
    }

    public String getH5_url() {
        return h5_url;
    }

    public void setH5_url(String h5_url) {
        this.h5_url = h5_url;
    }

    public String getGame_path() {
        return game_path;
    }

    public void setGame_path(String game_path) {
        this.game_path = game_path;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    public String getShare_image() {
        return share_image;
    }

    public void setShare_image(String share_image) {
        this.share_image = share_image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
