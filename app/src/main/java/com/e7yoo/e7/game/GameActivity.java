package com.e7yoo.e7.game;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.e7yoo.e7.BaseWebviewActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.GameInfo;
import com.e7yoo.e7.util.AnimaUtils;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.ViewUtil;
import com.e7yoo.e7.webview.ReWebChomeClient;
import com.e7yoo.e7.webview.ReWebViewClient;

/**
 * Created by Administrator on 2017/9/28.
 */

public class GameActivity extends BaseWebviewActivity implements View.OnClickListener {
    private View closeView;
    private ImageView loadingIv;
    /** 来源 */
    public final static String INTENT_FROM = "from";
    public final static String INTENT_FROM_CHAT_CESHI = "from_chat_ceshi";
    /** Game信息 */
    public final static String INTENT_GAME_INFO = "game_info";
    /** URL */
    public final static String INTENT_URL = "url";
    private String intent_url;
    private String from;
    private GameInfo gameInfo;

    @Override
    protected String initTitle() {
        return getString(R.string.more_game_center);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_game_webview;
    }

    @Override
    protected void initView() {
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
            gameInfo = (GameInfo) getIntent().getSerializableExtra(INTENT_GAME_INFO);
        }
        initWebView();
        setRightTv(View.VISIBLE, R.mipmap.ic_share, 0, this);

        AnimaUtils.startImageViewAnima(loadingIv);
        loadingIv.setVisibility(View.VISIBLE);
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
    }

    @Override
    protected void initViewListener() {
        closeView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_tv_close:
                finishAct();
                break;
            case R.id.titlebar_right_tv:
                share();
                break;
            default:
                break;
        }
    }

    protected void share() {
        String url = null;
        String title = null;
        String content = null;
        String imageUrl = ViewUtil.saveViewCapture(this, mWebView);
        if(INTENT_FROM_CHAT_CESHI.equals(from)) {
            title = getString(R.string.share_title_ceshi);
            content = getString(R.string.share_content_ceshi);
        } else {
            if(gameInfo != null) {
                url = gameInfo.getShare_url();
                title = gameInfo.getShare_title() != null ? gameInfo.getShare_title() : getString(R.string.share_title_game);
                content = gameInfo.getShare_content() != null ? gameInfo.getShare_content() : getString(R.string.share_content_game);
                imageUrl = gameInfo.getShare_image();
            } else {
                title = getString(R.string.share_title_game);
                content = getString(R.string.share_content_game);
            }
        }
        ShareDialogUtil.show(this, url, title, content, imageUrl);
    }

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

    private void finishAct() {
        if(from != null && from.equals("push")) {
            startActivity(new Intent(this, MainActivity.class));
        }
        super.finish();
    }

    ReWebViewClient reWebViewClient = new ReWebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            /*if("http://www.4399.com/".equals(url)) {
                return true;
            }*/
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
