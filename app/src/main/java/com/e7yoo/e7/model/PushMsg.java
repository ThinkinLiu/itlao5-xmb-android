package com.e7yoo.e7.model;

/**
 * Created by Administrator on 2017/9/25.
 */

public class PushMsg {
    private int _id;
    private long time;
    private int action; // 是跳转详情页0还是网页1（拓展字段extras）
    private String url; // 列表页跳转url（拓展字段extras）
    private String pic_url; // 列表页图片（拓展字段extras）
    private long msg_time; // 消息时间（拓展字段extras）
    private String content_pic_url; // 内容中的图片url（拓展字段extras）
    private String content_url; // 内容中的跳转url（拓展字段extras）
    private String title;
    private String content;
    private String extras;
    private String msgId;
    private String desc; // 预留
    private int unread; // 0已读，1未读

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public long getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(long msg_time) {
        this.msg_time = msg_time;
    }

    public String getContent_pic_url() {
        return content_pic_url;
    }

    public void setContent_pic_url(String content_pic_url) {
        this.content_pic_url = content_pic_url;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
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

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}
