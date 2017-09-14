package com.e7yoo.e7.model;

import android.view.View.OnClickListener;

/**
 * 保存popupwindow中item（TextView）的属性
 * PopupWindowUtil.showPopWindow使用
 * @author goome
 *
 */
public class TextSet {
	public int textResId;
	public boolean important;
	public OnClickListener onClickListener;

	/**
	 * 
	 * @param textResId
	 * @param important 重要/敏感功能
	 * @param onClickListener
	 */
	public TextSet(int textResId, boolean important,
                   OnClickListener onClickListener) {
		this.textResId = textResId;
		this.important = important;
		this.onClickListener = onClickListener;
	}
}