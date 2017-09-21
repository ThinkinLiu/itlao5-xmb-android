package com.e7yoo.e7.model;

import android.content.Context;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/31.
 */

public class Robot implements Serializable {
    private static final long serialVersionUID = 1485523020887627445L;

    private int _id;
    private String name;
    private String icon;
    private long time;
    private long birthTime;
    private String welcome;
    private int sex; // 0，保密 1，男 2，女
    private int voice; // 0 (普通女声), 1 (普通男声), 2 (特别男声), 3 (情感男声), 4 (童声)
    private String bg;
    private int score;
    private String desc;
    private int level;

    public Robot() {

    }

    public Robot(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.time = System.currentTimeMillis();
        this.birthTime = this.time;
        this.sex = 2;
        this.score = 0;
        this.level = 0;
        this.voice = 0;
    }

    public Robot(Context context) {
        this.name = context.getString(R.string.mengmeng);
        this.icon = null;
        this.time = System.currentTimeMillis();
        this.birthTime = this.time;
        this.welcome = context.getString(R.string.mengmeng_welcome);
        this.sex = 2;
        this.bg = null;
        this.score = 0;
        this.level = 0;
        this.voice = 4;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public int getVoice() {
        return voice;
    }

    @Override
    public String toString() {
        return "Robot{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", time='" + time + '\'' +
                ", welcome='" + welcome + '\'' +
                ", birthTime='" + birthTime + '\'' +
                ", sex='" + sex + '\'' +
                ", bg='" + bg + '\'' +
                ", score='" + score + '\'' +
                ", level='" + level + '\'' +
                ", voice='" + voice + '\'' +
                '}';
    }
}
