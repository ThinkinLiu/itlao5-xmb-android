package com.e7yoo.e7.util;

import java.util.Random;

public class RandomUtil {
	public static final String M = "7f49c8efc9bd9c952fee068d57fff473";
	public static final String N = "@e7yoo.com";
	public static final String P = "e7yoo.com";
	private static Random random = new Random();

	public static int getRandomNum(int max) {
		int num = (int) Math.log10(max) + 1;
		int result = 0;
		for(int i = 0; i < num; i++) {
			result += random.nextInt(max);
		}
		if(result % num == 0) {
			result = result / num;
		} else {
			result = result / num + random.nextInt(num);
		}
		return result;
	}
}
