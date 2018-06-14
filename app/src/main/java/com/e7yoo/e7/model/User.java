package com.e7yoo.e7.model;

import android.text.TextUtils;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/5/9.
 */

public class User extends BmobUser {
    private static final long serialVersionUID = 1L;

    private Integer age; // 年龄
    private Integer sex; // 0 保密 1 男 2 女
    private String nickname; // 昵称
    private Integer score; // 积分
    private String label; // 备注
    private String extra; // 拓展字段 json
    private String bg; // 主页背景
    private String icon; // 头像
    private String name; // 姓名

    public Integer getAge() {
        if(age == null) {
            age = 0;
        }
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        if(sex == null) {
            sex = 0;
        }
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getNickname() {
        if(nickname == null) {
            if(TextUtils.isEmpty(getObjectId())) {
                nickname = "";
            } else {
                nickname = "m_" + getObjectId();
            }
        }
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getScore() {
        if(score == null) {
            score = 0;
        }
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getLabel() {
        if(label == null) {
            label = "这家伙好懒，什么都没留下！";
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
