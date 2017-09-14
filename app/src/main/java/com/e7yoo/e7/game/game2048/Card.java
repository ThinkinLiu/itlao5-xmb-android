package com.e7yoo.e7.game.game2048;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 卡片类
 * @author Administrator
 *
 */
public class Card extends FrameLayout {
	private int[] colors = {/*0x33ffffff, */0xbbf2b179, 0xbbf47c5f, 0xbbea593a, 0xbbf1d04b, 0xbbe4b1a0, 0xbbff5500};
	public Card(Context context) {
		super(context);
		
		label = new TextView(getContext());
		label.setTextSize(32);
		label.setTextColor(0xff000000);
		label.setBackgroundColor(0x33ffffff);
		label.setGravity(Gravity.CENTER);
		
		LayoutParams lp = new LayoutParams(-1,-1);
		lp.setMargins(10, 10, 0, 0);	// 设置文字卡片离其它卡片的距离，设置了左边和上边相差10
		addView(label, lp);
		
		setNum(0);
	}
	
	private int num = 0;	// 卡片上的数字
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
		// 如果添加的是0，那么就代表该卡片是空的
		if(num <= 0){
			label.setText("");
		}else{
			label.setText(num+"");
		}
		if(num > 9999) {
			label.setTextSize(26);
		} else if(num > 999) {
			label.setTextSize(28);
		} else if(num > 99) {
			label.setTextSize(30);
		} else {
			label.setTextSize(32);
		}
		if(num <= 4) {
			label.setTextColor(0xff000000);
			label.setBackgroundColor(0x33ffffff);
		} else {
			try {
				label.setTextColor(0xffffffff);
				int log2n = (int) (Math.log(num) / Math.log(2));
				label.setBackgroundColor(colors[(log2n - 2) % colors.length]);
			} catch (Exception e) {
				label.setTextColor(0xff000000);
				label.setBackgroundColor(0x33ffffff);
			}
		}
	}
	
	// 重写equals方法，使它能够判断两个卡片的数是否相等
	public boolean equals(Card o) {
		return getNum() == o.getNum();
	}

	private TextView label;
	

}