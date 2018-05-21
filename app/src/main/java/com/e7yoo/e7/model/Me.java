package com.e7yoo.e7.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/11.
 */
@Deprecated
public class Me implements Serializable {

    private static final long serialVersionUID = -3333759175301198943L;
    private int _id;
    private String name;
    private String nick_name;
    private String sex;
    private long time;
    private long birth_time;
    private int score;
    private String icon;
    private String bg;
    private int level;
    private String label;
    private String city;
    private String address;

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

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getBirth_time() {
        return birth_time;
    }

    public void setBirth_time(long birth_time) {
        this.birth_time = birth_time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
