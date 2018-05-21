package com.e7yoo.e7.model;

import java.io.Serializable;

public class TodayHisEntity implements Serializable {

	/** @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) */
	private static final long serialVersionUID = -5509736977716127846L;

	private String _id;
	private String day;
	private String des;
	private String lunar;
	private String month;
	private String pic;
	private String title;
	private String year;
	private String content;

	private String img;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getDes() {
		if(des == null) {
			des = "";
		}
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getLunar() {
		return lunar;
	}

	public void setLunar(String lunar) {
		this.lunar = lunar;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getPic() {
		if(pic == null) {
			pic = getImg();
		}
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getContent() {
		if(content == null) {
			content = "";
		}
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
