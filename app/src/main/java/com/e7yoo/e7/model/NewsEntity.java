package com.e7yoo.e7.model;

import java.io.Serializable;

public class NewsEntity implements Serializable {
	/** @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) */
	private static final long serialVersionUID = -4956213966398586329L;
	private String title;
	private String date;
	private String author_name;
	private String thumbnail_pic_s;
	private String thumbnail_pic_s02; // 头条，显示三张缩略图，经分析与thumbnail_pic_s03一致
	private String thumbnail_pic_s03;
	private String url;
	private String uniquekey; // 头条唯一码，（与url一致，即 url为**/uniquekey.html）
	private String type; // 头条
	private String realtype; // 头条，真实分类
	private String category; // 头条以外，分类

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthor_name() {
		return author_name;
	}

	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	public String getThumbnail_pic_s() {
		return thumbnail_pic_s;
	}

	public void setThumbnail_pic_s(String thumbnail_pic_s) {
		this.thumbnail_pic_s = thumbnail_pic_s;
	}

	public String getThumbnail_pic_s02() {
		return thumbnail_pic_s02;
	}

	public void setThumbnail_pic_s02(String thumbnail_pic_s02) {
		this.thumbnail_pic_s02 = thumbnail_pic_s02;
	}

	public String getThumbnail_pic_s03() {
		return thumbnail_pic_s03;
	}

	public void setThumbnail_pic_s03(String thumbnail_pic_s03) {
		this.thumbnail_pic_s03 = thumbnail_pic_s03;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUniquekey() {
		return uniquekey;
	}

	public void setUniquekey(String uniquekey) {
		this.uniquekey = uniquekey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRealtype() {
		return realtype;
	}

	public void setRealtype(String realtype) {
		this.realtype = realtype;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
