package com.e7yoo.e7.util;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.e7yoo.e7.R;
import com.e7yoo.e7.model.TextSet;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 
 * @Title PopupWindowUtil
 * @Description popupwindow工具类，包含处理兼容性的方法
 * @author 刘生健
 * @createTime 2015-6-17 下午03:23:22
 * @modifyBy
 * @modifyTime
 * @modifyRemark
 */
public class PopupWindowUtil {

	/**
	 * 在new PopupWindow()后调用 兼容4.0(ICE_CREAM_SANDWICH)以下版本 <br>
	 * 防止错误<br>
	 * java.lang.NullPointerException<br>
	 * at android.widget.PopupWindow$1.onScrollChanged(PopupWindow.java:
	 * 
	 * @param window
	 */
	public static void fixPopupWindow(final PopupWindow window) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			try {
				final Field fAnchor = PopupWindow.class
						.getDeclaredField("mAnchor");
				fAnchor.setAccessible(true);
				Field listener = PopupWindow.class
						.getDeclaredField("mOnScrollChangedListener");
				listener.setAccessible(true);
				final ViewTreeObserver.OnScrollChangedListener originalListener = (ViewTreeObserver.OnScrollChangedListener) listener
						.get(window);
				ViewTreeObserver.OnScrollChangedListener newListener = new ViewTreeObserver.OnScrollChangedListener() {
					@Override
					public void onScrollChanged() {
						try {
							@SuppressWarnings("unchecked")
                            WeakReference<View> mAnchor = (WeakReference<View>) fAnchor
									.get(window);
							if (mAnchor == null || mAnchor.get() == null) {
								return;
							} else {
								originalListener.onScrollChanged();
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				};
				listener.set(window, newListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 底部功能按钮
	 * 
	 * @param context
	 * @param viewParent
	 * @param descTextResId
	 *            说明文字resId（如 选择背景图片），<=0时不可见，
	 * @param item1TextSet
	 *            功能1，为null时不可见
	 * @param item2TextSet
	 *            功能2，为null时不可见
	 * @param outsideTouchable
	 */
	public static void showPopWindow(Context context, View viewParent,
                                     int descTextResId, final TextSet item1TextSet,
                                     final TextSet item2TextSet, boolean outsideTouchable) {

		ArrayList<TextSet> list = new ArrayList<TextSet>();
		
		if(item1TextSet != null)
			list.add(item1TextSet);
		
		if(item2TextSet != null)
			list.add(item2TextSet);
		
		showPopWindow(context,viewParent,descTextResId,list,outsideTouchable);
	}

	public static void showPopWindow(Context context, View viewParent,
                                     int descTextResId, final ArrayList<TextSet> list, boolean outsideTouchable) {

		View view = LayoutInflater.from(context).inflate(
				R.layout.popwindow_bottom, null);
		TextView desc = (TextView) view.findViewById(R.id.pop_desc);
		View seperator1 = view.findViewById(R.id.pop_seperator1);
		TextView cancelView = (TextView) view.findViewById(R.id.pop_cancel);
		LinearLayout popMain = (LinearLayout)view.findViewById(R.id.pop_main);

		final PopupWindow popWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		PopupWindowUtil.fixPopupWindow(popWindow);
		if (descTextResId > 0) {
			desc.setVisibility(View.VISIBLE);
			seperator1.setVisibility(View.VISIBLE);
			desc.setText(descTextResId);
		} else {
			desc.setVisibility(View.GONE);
			seperator1.setVisibility(View.GONE);
		}
		
		if(list != null) {
			for(int i=0; i<list.size(); i++) {
				final TextSet textSet = list.get(i);
				View itemView = LayoutInflater.from(context).inflate(
						R.layout.popwindow_bottom_item, null);
				
				TextView popItem = (TextView) itemView.findViewById(R.id.pop_item);
				View popSeperator = itemView.findViewById(R.id.pop_seperator);
				
				if(i == 0)
					popSeperator.setVisibility(View.GONE);
				
				popItem.setText(textSet.textResId);
				popItem.setTextColor(context.getResources().getColor(
						textSet.important ? R.color.blue
								: R.color.text_h));
				
				popItem.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						textSet.onClickListener.onClick(v);
						popWindow.dismiss();
					}
				});
				
				popMain.addView(itemView);
			}
		}
		
		if (outsideTouchable) {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popWindow.dismiss();
				}
			});
		}
		cancelView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
		// 设置setFocusable(true)和backgroundDrawable才能使点击返回键popupwindow消失，否则，点击返回键Popupwindow为null
		popWindow.setFocusable(true);
		popWindow.setBackgroundDrawable(new PaintDrawable(0x88000000));
		popWindow.setOutsideTouchable(outsideTouchable);

		popWindow.showAtLocation(viewParent, Gravity.CENTER, 0, 0);
	}
}
