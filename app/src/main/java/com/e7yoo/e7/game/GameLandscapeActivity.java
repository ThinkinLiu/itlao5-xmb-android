package com.e7yoo.e7.game;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.e7yoo.e7.BaseWebviewActivity;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.AnimaUtils;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.e7yoo.e7.util.ViewUtil;
import com.e7yoo.e7.webview.ReWebChomeClient;
import com.e7yoo.e7.webview.ReWebViewClient;

/**
 * Created by Administrator on 2017/9/28.
 */

public class GameLandscapeActivity extends GameActivity {
    @Override
    protected boolean initTheme() {
        super.initTheme();
        //隐去状态栏部分(电池等图标和一切修饰部分)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return true;
    }

    @Override
    protected void initSettings() {
        super.initSettings();
        hintTitle();
    }
}
