package com.e7yoo.e7.game;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.e7yoo.e7.BaseWebviewActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.GameInfo;
import com.e7yoo.e7.util.AnimaUtils;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.UmengUtil;
import com.e7yoo.e7.util.ViewUtil;
import com.e7yoo.e7.webview.ReWebChomeClient;
import com.e7yoo.e7.webview.ReWebViewClient;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/9/28.
 */

public class GameActivity extends BaseWebviewActivity implements View.OnClickListener {
    private View closeView;
    private ImageView loadingIv;
    /** 来源 */
    public final static String INTENT_FROM = "from";
    public final static String INTENT_FROM_CHAT_CESHI = "from_chat_ceshi";
    public final static String INTENT_FROM_CHAT_GAME = "from_chat_game";
    /** Game信息 */
    public final static String INTENT_GAME_INFO = "game_info";
    /** URL */
    public final static String INTENT_URL = "url";
    private String intent_url;
    private String from;
    private GameInfo gameInfo;
    private String shareImagePath = null;

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
            initShareImagePath();
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

    private void initShareImagePath() {
        if(gameInfo != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FutureTarget<File> target = null;
                        if(TextUtils.isEmpty(gameInfo.getShare_image())) {
                            if(!TextUtils.isEmpty(gameInfo.getIcon())) {
                                target  = Glide.with(GameActivity.this).load(gameInfo.getIcon()).downloadOnly(124, 124);
                            }
                        } else {
                            target  = Glide.with(GameActivity.this).load(gameInfo.getShare_image()).downloadOnly(200, 200);
                        }
                        if(target != null) {
                            shareImagePath = target.get().getAbsolutePath();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
        String imageUrl = null;
        if(INTENT_FROM_CHAT_CESHI.equals(from)) {
            title = getString(R.string.share_title_ceshi);
            content = getString(R.string.share_content_ceshi);
            imageUrl = getImagePath();
        } else {
            if(gameInfo != null) {
                url = gameInfo.getShare_url();
                title = gameInfo.getShare_title() != null ? gameInfo.getShare_title() : getString(R.string.share_title_game);
                content = gameInfo.getShare_content() != null ? gameInfo.getShare_content() : getString(R.string.share_content_game);
                imageUrl = getImagePath();
            } else {
                title = getString(R.string.share_title_game);
                content = getString(R.string.share_content_game);
                imageUrl = getImagePath();
            }
        }
        ShareDialogUtil.show(this, url, title, content, imageUrl);

        UmengUtil.onEvent(UmengUtil.GAME_SHARE);
    }

    private String getImagePath() {
        if(!TextUtils.isEmpty(shareImagePath)) {
            return shareImagePath;
        }
        return ViewUtil.saveViewCapture(this, mWebView);
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

    @Override
    protected void onDestroy() {
        ShareDialogUtil.release();
        super.onDestroy();
    }
}
