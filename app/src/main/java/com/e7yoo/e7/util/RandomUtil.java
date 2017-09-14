package com.e7yoo.e7.util;

import java.util.Random;

public class RandomUtil {
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
