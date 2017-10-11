package com.e7yoo.e7.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 时间处理工具类（包含是否当前，是否同一分钟等计算）,传入时间请使用UTC时间
 * 
 * @Title TimeUtil
 * @author www.e7yoo.com
 * @createTime 2016-7-11 下午04:36:53
 * @modifyBy
 * @modifyTime
 * @modifyRemark
 */
public class TimeUtil {

	/** 时间日期格式化到年月日时分秒. */
	public static final String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";
	/** 时间日期格式化到月日时分. */
	public static final String dateFormatMDHM = "MM-dd HH:mm";
	/** 时间日期格式化到年月日时分. */
	public static final String dateFormatYMDHM = "yyyy-MM-dd HH:mm";
	/** 时分. */
	public static final String dateFormatHM = "HH:mm";

	/**
	 * 将毫秒转换为显示的时间 TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @param millis
	 * @return
	 */
	public static String formatMsgTime(long millis) {

		long curMillis = System.currentTimeMillis();

		int offDay = getOffectDay(curMillis, millis);
		if (offDay == 0) {// 今天
			return formatTime(millis, dateFormatHM);
		} else if (offDay == 1) {// 昨天
			return "昨天" + " " + formatTime(millis, dateFormatHM);
		} else if (offDay <= 2) {// 前天
			return "前天" + " " + formatTime(millis, dateFormatHM);
		} else if (offDay <= 6) {// 七天前
			return format2WeekTime(millis);
		} else {
			return formatTime(millis, dateFormatYMDHM);
		}

	}

	/**
	 * 将毫秒转换为显示的时间 TODO(这里用一句话描述这个方法的作用)
	 *
	 * @param millisStr
	 * @return
	 */
	public static String formatFeedTime(String millisStr) {
		long millis = 0;
		try {
			millis = Long.parseLong(millisStr);
		} catch (Throwable e) {
		}
		if(millis <= 0) {
			return "";
		}
		long curMillis = System.currentTimeMillis();
		long diff = curMillis - millis;
		if(diff < 60) {
			return "刚刚";
		}/* else if(diff < 5 * 60) {
			return "5分钟内";
		} else if(diff < 60 * 60) {
			return "1小时内";
		}*/
		int offDay = getOffectDay(curMillis, millis);
		if (offDay == 0) {// 今天
			return formatTime(millis, dateFormatHM);
		} else if (offDay == 1) {// 昨天
			return "昨天" + " " + formatTime(millis, dateFormatHM);
		} else if (offDay <= 2) {// 前天
			return "前天" + " " + formatTime(millis, dateFormatHM);
		} else if (isSameYear(curMillis, millis)) {// 同一年
			return formatTime(millis, dateFormatMDHM);
		} else {
			return formatTime(millis, dateFormatYMDHM);
		}

	}

	/**
	 * 描述：计算两个日期所差的天数.
	 * 
	 * @param milliseconds1
	 *            the milliseconds1
	 * @param milliseconds2
	 *            the milliseconds2
	 * @return int 所差的天数
	 */
	public static int getOffectDay(long milliseconds1, long milliseconds2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(milliseconds1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(milliseconds2);
		// 先判断是否同年
		int y1 = calendar1.get(Calendar.YEAR);
		int y2 = calendar2.get(Calendar.YEAR);
		int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int maxDays = 0;
		int day = 0;
		if (y1 - y2 > 0) {
			maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 + maxDays;
		} else if (y1 - y2 < 0) {
			maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 - maxDays;
		} else {
			day = d1 - d2;
		}
		return day;
	}

	public static boolean isToday(long milliseconds1) {
		long milliseconds2 = System.currentTimeMillis();
		if (Math.abs(milliseconds1 - milliseconds2) >= 24 * 60 * 60 * 1000) {
			// 两者之差大于24小时则肯定不会是同一天
			return false;
		} else {
			// 否则，判断day值是否相同
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTimeInMillis(milliseconds1);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTimeInMillis(milliseconds2);
			int m1 = calendar1.get(Calendar.DAY_OF_MONTH);
			int m2 = calendar2.get(Calendar.DAY_OF_MONTH);
			if (m1 == m2) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 是否同一分钟内
	 * 
	 * @param milliseconds1
	 * @param milliseconds2
	 * @return
	 */
	public static boolean isSameMinute(long milliseconds1, long milliseconds2) {
		if (Math.abs(milliseconds1 - milliseconds2) >= 60 * 1000) {
			// 两者之差大于60秒小时则肯定不会是同一分钟
			return false;
		} else {
			// 否则，判断minute值是否相同
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTimeInMillis(milliseconds1);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTimeInMillis(milliseconds2);
			int m1 = calendar1.get(Calendar.MINUTE);
			int m2 = calendar2.get(Calendar.MINUTE);
			if (m1 == m2) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 是否同一年内
	 *
	 * @param milliseconds1
	 * @param milliseconds2
	 * @return
	 */
	public static boolean isSameYear(long milliseconds1, long milliseconds2) {
		if (Math.abs(milliseconds1 - milliseconds2) >= 366 * 24 * 60 * 60 * 1000) {
			// 两者之差大于366天则肯定不会是同一年
			return false;
		} else {
			// 否则，判断minute值是否相同
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTimeInMillis(milliseconds1);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTimeInMillis(milliseconds2);
			int m1 = calendar1.get(Calendar.YEAR);
			int m2 = calendar2.get(Calendar.YEAR);
			if (m1 == m2) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 将毫秒转化为pattern 格式的时间输出
	 * 
	 * @param millisStr
	 * @param pattern
	 * @return
	 */
	public static String formatTime(String millisStr, String pattern) {
		long millis = parse2Long(millisStr);
		if (millis <= 0) {
			return millisStr;
		}
		return formatTime(millis, pattern);
	}

	/**
	 * 将毫秒转化为 pattern 格式的时间输出
	 * 
	 * @param millis
	 * @param pattern
	 * @return
	 */
	public static String formatTime(long millis, String pattern) {
		// 北京时区GMT+8
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.setTimeInMillis(millis);
		DateFormat fmt = new SimpleDateFormat(pattern);
		String time = fmt.format(cal.getTime());
		return time;
	}

	/**
	 * 转为 周* （such as 周日 周一）
	 * @param millis
	 * @return
	 */
	public static String format2WeekTime(long millis) {
		String week = "周日";
		Calendar calendar = new GregorianCalendar();
		try {
			calendar.setTime(new Date(millis));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		switch (intTemp) {
		case 0:
			week = "周日";
			break;
		case 1:
			week = "周一";
			break;
		case 2:
			week = "周二";
			break;
		case 3:
			week = "周三";
			break;
		case 4:
			week = "周四";
			break;
		case 5:
			week = "周五";
			break;
		case 6:
			week = "周六";
			break;
		}
		return week + " " + formatTime(millis, dateFormatHM);
	}

	/**
	 * 获取 早上0/上午1/中午2/下午3/晚上4/午夜5 
	 * @return
	 */
	public static int getTime() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(System.currentTimeMillis());
		int hour = calendar1.get(Calendar.HOUR_OF_DAY);
		if(hour >= 6 && hour < 9) {
			return 0;
		} else if(hour >= 9 && hour < 12) {
			return 1;
		} else if(hour >= 12 && hour < 14) {
			return 2;
		} else if(hour >= 14 && hour < 18) {
			return 3;
		} else if(hour >= 18 && hour < 24) {
			return 4;
		} else if(hour >= 0 && hour < 6) {
			return 5;
		}
		return -1;
	}
	
	/**
	 * long型转换
	 * @param str
	 * 
	 * @return 不能转换时，返回Long.MIN_VALUE
	 */
	public static long parse2Long(String str) {
		long result = Long.MIN_VALUE;
		try {
			result = Long.valueOf(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getTextByTime() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(System.currentTimeMillis());
		int hour = calendar1.get(Calendar.HOUR_OF_DAY);
		if(hour >= 6 && hour < 9) {
			return "真开心，刚起床就来看我O(∩_∩)O~";
		} else if(hour >= 9 && hour < 12) {
			return "上午好！美好的一天开始了哦...";
		} else if(hour >= 12 && hour < 14) {
			return "中午好！";
		} else if(hour >= 14 && hour < 18) {
			return "想想，晚上吃什么呢...";
		} else if(hour >= 18 && hour < 24) {
			return "美好的一天又过完了，准备洗澡睡觉了哦";
		} else if(hour >= 0 && hour < 6) {
			return "现在是休息的时间哦，晚安！";
		}
		return "您好!";
	}
}
