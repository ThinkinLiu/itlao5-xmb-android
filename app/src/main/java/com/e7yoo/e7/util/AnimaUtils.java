package com.e7yoo.e7.util;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class AnimaUtils {

	
	public static void startImageViewAnima(ImageView loading) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 120); 
		animation.setDuration(500);
		animation.setRepeatMode(Animation.REVERSE);
		animation.setRepeatCount(Integer.MAX_VALUE);
		loading.startAnimation(animation);
	}
	
	public static void removeImageViewAnima(ImageView loading) {
		loading.setAnimation(null);
	}
}
