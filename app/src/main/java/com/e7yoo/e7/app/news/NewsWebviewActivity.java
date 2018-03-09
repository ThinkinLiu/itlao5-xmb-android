package com.e7yoo.e7.app.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;

import com.e7yoo.e7.BaseWebviewActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.NewsEntity;
import com.e7yoo.e7.model.TextSet;
import com.e7yoo.e7.util.AnimaUtils;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.FileUtil;
import com.e7yoo.e7.util.PopupWindowUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.TimeUtil;
import com.e7yoo.e7.webview.ReWebChomeClient;
import com.e7yoo.e7.webview.ReWebViewClient;
import com.sdsmdg.tastytoast.TastyToast;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

public class NewsWebviewActivity extends BaseWebviewActivity implements OnClickListener {
	private View closeView;
	private String intent_url;
	// private String intent_type;
	/** URL */
	public final static String INTENT_URL = "url";
	/** url类型（图片/文章/天气...） */
	public final static String INTENT_TYPE = "from_square";
	/** 每日一条  */
	public final static String INTENT_FROM_YITIAO = "from_yitiao";
	/** 消息详情  */
	public final static String INTENT_FROM_PUSH_MSG_DETAILS = "from_push_msg_details";
	/** 消息列表  */
	public final static String INTENT_FROM_PUSH_MSG = "from_push_msg";
	/** 聊天列表  */
	public final static String INTENT_FROM_CHAT_MSG = "from_chat_msg";
	/** 帖子详情  */
	public final static String INTENT_FROM_FEED_DETAILS = "from_feed_details";
	/** 来源 */
	public final static String INTENT_FROM = "from";
	private ImageView loadingIv;
	private String from;
	
	@Override
	public void onBackPressed() {
		if(closeView != null) {
			closeView.setVisibility(View.VISIBLE);
		} else {
			finishAct();
			return;
		}
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			try {
				setTitleTv(mWebView.copyBackForwardList().getCurrentItem().getTitle());
			} catch (Exception e) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							setTitleTv(mWebView.getTitle());
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}, 500);
			}
			return;
		}
		finishAct();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_left_tv:
			onBackPressed();
			break;
		case R.id.titlebar_left_tv_close:
			finishAct();
			break;
		case R.id.titlebar_right_tv:
			// saveFavorites();
			toMenu();
			break;
		default:
			break;
		}
	}

	private void toMenu() {
		ArrayList<TextSet> textSets = new ArrayList<>();
		textSets.add(new TextSet(R.string.news_webview_share, false, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toShare();
			}
		}));
		textSets.add(new TextSet(R.string.news_webview_open, false, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toOpen();
			}
		}));
		PopupWindowUtil.showPopWindow(this, rootView, 0, textSets, true);
	}

	private void toShare() {
		if(mWebView == null) {
			return;
		}
		String url = mWebView.getUrl();
		String title = mWebView.getTitle();
		if(CommonUtil.isEmptyTrimNull(url)) {
			TastyToast.makeText(this, getString(R.string.wangyefenxiang_no), TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
			CrashReport.postCatchedException(new Throwable("wangyefenxiang_url_is_null"));
			return;
		}
		ShareDialogUtil.show(this, url, null, getString(R.string.wangyefenxiang) + title, null);
	}

	private void toOpen() {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(mWebView.getUrl());
			intent.setData(content_url);
			startActivity(intent);
		} catch (Throwable e) {
			TastyToast.makeText(this, getString(R.string.url_open_failed), TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
		}
	}

	private void saveFavorites() {
		String url = mWebView.getUrl();
		if(CommonUtil.isEmptyTrimNull(url)) {
			TastyToast.makeText(this, getString(R.string.favorites_fai), TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
			return;
		}
		ArrayList<NewsEntity> news;
		try {
			news = (ArrayList<NewsEntity>) FileUtil.readFile(this, Constant.FILE_FAVORITES_NEWS_LIST);
			if(news.size() > 100) {
				news.remove(news.size() - 1);
			}
		} catch (Exception e) {
			news = new ArrayList<NewsEntity>();
		}
		NewsEntity newsEntity = new NewsEntity();
		newsEntity.setUrl(url);
		newsEntity.setTitle(mWebView.getTitle());
		newsEntity.setDate(getString(R.string.favorite_time_hint) + TimeUtil.formatMsgTime(System.currentTimeMillis()));
		for(int i = 0; i < news.size(); i++) {
			try {
				if(news.get(i).getUrl().equals(newsEntity.getUrl())) {
					news.remove(i);
					break;
				}
			} catch (Exception e) {
			}
			if(i > 30) {
				break;
			}
		}
		news.add(0, newsEntity);
		FileUtil.saveFile(this, Constant.FILE_FAVORITES_NEWS_LIST, news);
		TastyToast.makeText(this, getString(R.string.favorites_suc), TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected String initTitle() {
		return getString(R.string.more_news);
	}

	@Override
	protected int initLayoutResId() {
		return R.layout.activity_webview;
	}

	@Override
	protected void initView() {
		rootView = findViewById(R.id.root_layout);

		closeView = findViewById(R.id.titlebar_left_tv_close);
		closeView.setVisibility(View.GONE);
		closeView.setOnClickListener(this);

		mWebView = (WebView) findViewById(R.id.webview);

		loadingIv = (ImageView) findViewById(R.id.loading);
	}

	@Override
	protected void initSettings() {
		if (getIntent() != null) {
			// intent_type = getIntent().getStringExtra(INTENT_TYPE);
			intent_url = getIntent().getStringExtra(INTENT_URL);
			from = getIntent().getStringExtra(INTENT_FROM);
		}
		initWebView();

		AnimaUtils.startImageViewAnima(loadingIv);
		loadingIv.setVisibility(View.VISIBLE);

		//mWebView.addJavascriptInterface(new Js(), "js");
		mWebView.setWebViewClient(reWebViewClient);
		mWebView.setWebChromeClient(reWebChomeClient);

		if (!CommonUtil.isEmptyTrimNull(intent_url)) {
			if (!intent_url.startsWith("http")) {
				intent_url = "http://" + intent_url;
			}
			mWebView.loadUrl(intent_url);
		} else {
			finishAct();
		}

		setRightTv(View.VISIBLE, R.mipmap.ic_menu_white_24dp, 0, this);
	}

	@Override
	protected void initViewListener() {
		setLeftTvListener(this);
		closeView.setOnClickListener(this);
		addOnLongClickListener();
	}

	private void finishAct() {
		if(from != null && from.equals("push")) {
			startActivity(new Intent(this, MainActivity.class));
		}
		super.finish();
	}

	/*final class Js {
		@JavascriptInterface
		public void getUrl(final String html) {
			Toast.makeText(NewsWebviewActivity.this, html, 4000).show();
		}
	}*/

	ReWebViewClient reWebViewClient = new ReWebViewClient() {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			view.loadUrl(url);
			return true;
		}
	};

	ReWebChomeClient reWebChomeClient = new ReWebChomeClient(this, mProgressDialog) {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
			AnimaUtils.removeImageViewAnima(loadingIv);
			loadingIv.setVisibility(View.GONE);
			setTitleTv(title);
		}
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(newProgress >= 100) {
				AnimaUtils.removeImageViewAnima(loadingIv);
				loadingIv.setVisibility(View.GONE);
			}
			super.onProgressChanged(view, newProgress);
		}
	};
}
