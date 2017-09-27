package com.e7yoo.e7.app.news;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.NewsFragmentPagerAdapter;
import com.e7yoo.e7.fragment.NewsFragment;
import com.e7yoo.e7.model.ChannelItem;
import com.e7yoo.e7.model.News;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.view.ColumnHorizontalScrollView;

import java.util.ArrayList;
import java.util.Arrays;

public class NewsActivity extends BaseActivity {
	/** 自定义HorizontalScrollView */
	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	private ViewPager mViewPager;
	/** 用户选择的新闻分类列表 */
	private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
	/** 当前选中的栏目 */
	private int columnSelectIndex = 0;
	/** 左阴影部分 */
	public ImageView shade_left;
	/** 右阴影部分 */
	public ImageView shade_right;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** Item宽度 */
	private int mItemWidth = 0;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	LinearLayout mRadioGroup_content;
	RelativeLayout rl_column;
	private String text;

	@Override
	protected String initTitle() {
		if(getIntent() != null) {
			text = getIntent().getStringExtra("text");
			if(!TextUtils.isEmpty(text)) {
				return text;
			}
		}
		return getString(R.string.more_news);
	}

	@Override
	protected int initLayoutResId() {
		return R.layout.activity_news;
	}

	/** 初始化layout控件 */
	@Override
	protected void initView() {
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
		rl_column = (RelativeLayout) findViewById(R.id.rl_column);
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		shade_left = (ImageView) findViewById(R.id.shade_left);
		shade_right = (ImageView) findViewById(R.id.shade_right);
	}

	@Override
	protected void initSettings() {
		mScreenWidth = CommonUtil.getWindowsWidth(this);
		mItemWidth = mScreenWidth / 7;// 一个Item宽度为屏幕的1/7
		setChangelView();
	}

	@Override
	protected void initViewListener() {

	}

	/**
	 * 当栏目项发生变化时候调用
	 */
	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 延时，使scrollview滚动生效
				if(CommonUtil.isEmptyTrimNull(text)) {
					text = News.getTypes(NewsActivity.this)[0];
				}
				int tab = Arrays.asList(News.getTypes(NewsActivity.this)).indexOf(text) + 1;
				if(tab <= 0) {
					tab = 1;
				}
				final int currentTab = tab;
				mViewPager.setCurrentItem(currentTab);
				selectTab(currentTab);
			}
		}, 0);
	}

	/** 获取Column栏目 数据 */
	private void initColumnData() {
		userChannelList = new ArrayList<ChannelItem>();
		for (int i = 0; i < News.getTypes(this).length; i++) {
			userChannelList.add(new ChannelItem(i, News.getTypes(this)[i], i, 1));
		}
	}

	/**
	 * 初始化Column栏目项
	 */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count = userChannelList.size();
		mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right,
				rl_column);
		initView(0, News.type_weixin);
		for (int i = 0; i < count; i++) {
			initView(i + 1, userChannelList.get(i).getName());
		}
	}
	
	private void initView(int i, String name) {
		int width = mItemWidth;
		if(name.length() > 2) {
			width = (int)(mItemWidth * 1.5f);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		params.leftMargin = 5;
		params.rightMargin = 5;
		// TextView localTextView = (TextView)
		// mInflater.inflate(R.layout.column_radio_item, null);
		TextView columnTextView = new TextView(this);
		columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
		// localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
		columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
		columnTextView.setGravity(Gravity.CENTER);
		columnTextView.setPadding(5, 5, 5, 5);
		columnTextView.setId(i);
		columnTextView.setText(name);
		columnTextView.setSingleLine();
		columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
		if (columnSelectIndex == i) {
			columnTextView.setSelected(true);
		}
		columnTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
					View localView = mRadioGroup_content.getChildAt(i);
					if (localView != v)
						localView.setSelected(false);
					else {
						localView.setSelected(true);
						mViewPager.setCurrentItem(i);
					}
				}
				/*Toast.makeText(getApplicationContext(), userChannelList.get(v.getId()).getName(),
						Toast.LENGTH_SHORT).show();*/
			}
		});
		mRadioGroup_content.addView(columnTextView, i, params);
	}

	/**
	 * 初始化Fragment
	 */
	private void initFragment() {
		fragments.clear();// 清空
		Bundle data = new Bundle();
		data.putString("text", News.type_weixin);
		data.putInt("id", 0);
		NewsFragment newfragment = new NewsFragment();
		newfragment.setArguments(data);
		fragments.add(newfragment);
		int count = userChannelList.size();
		for (int i = 0; i < count; i++) {
			data = new Bundle();
			data.putString("text", userChannelList.get(i).getName());
			data.putInt("id", userChannelList.get(i).getId() + 1);
			newfragment = new NewsFragment();
			newfragment.setArguments(data);
			fragments.add(newfragment);
		}
		NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
		// mViewPager.setOffscreenPageLimit(0);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}

	/**
	 * ViewPager切换监听方法
	 */
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};

	/**
	 * 选择的Column里面的Tab
	 */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}

}
