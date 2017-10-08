package com.e7yoo.e7.model;

import java.io.Serializable;

public class PrivateMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 92167621L;
	private int _id;
	private String user;
	private int code;
	private long time;// 发送时间
	private String content;
	private Type type;// 方向,0:我主动发，1:我被动收，2，提示（网络异常等）
	private String url;
	private String id;
	private String time2;//
	private String title;//
	private String news_type;//
	private int robot_id;

	public String getNews_type() {
		return news_type;
	}

	public void setNews_type(String news_type) {
		this.news_type = news_type;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime2() {
		return time2;
	}

	public void setTime2(String time2) {
		this.time2 = time2;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public PrivateMsg(int code, long time, String content, String url, Type type, String id, String time2, String title, int robotId) {
		super();
		this.code = code;
		this.time = time;
		this.content = content;
		this.type = type;
		this.url = url;
		this.id = id;
		this.time2 = time2;
		this.title = title;
		this.robot_id = robotId;
	}

	public PrivateMsg(int code, long time, String content, String url, Type type, String id, String time2, int robotId) {
		super();
		this.code = code;
		this.time = time;
		this.content = content;
		this.type = type;
		this.url = url;
		this.id = id;
		this.time2 = time2;
		this.robot_id = robotId;
	}

	public PrivateMsg(int code, long time, String content, String url, Type type, String news_type, int robotId) {
		super();
		this.code = code;
		this.time = time;
		this.content = content;
		this.type = type;
		this.url = url;
		this.news_type = news_type;
		this.robot_id = robotId;
	}

	public PrivateMsg(int code, long time, String content, String url, Type type, int robotId) {
		super();
		this.code = code;
		this.time = time;
		this.content = content;
		this.type = type;
		this.url = url;
		this.robot_id = robotId;
	}
	
	public PrivateMsg() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getRobotId() {
		return robot_id;
	}

	public void setRobotId(int robotId) {
		this.robot_id = robotId;
	}

	public enum Type implements Serializable {
		SEND, REPLY, HINT, /*ADVER, */NEWS_HINT, NEWS, ALL_NEWS, SHARE, TODAY_HISTORY, TODAY_HOSTORY_DETAILS, TUPIAN
	}
}
