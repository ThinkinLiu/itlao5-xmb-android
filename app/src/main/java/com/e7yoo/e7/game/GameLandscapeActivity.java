package com.e7yoo.e7.game;

import android.view.View;
import android.view.WindowManager;

import com.e7yoo.e7.R;

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
    protected int initLayoutResId() {
        return R.layout.activity_game_webview_landscape;
    }

    @Override
    protected void initSettings() {
        super.initSettings();
        hintTitle();
        findViewById(R.id.ic_share_landscape).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_share_landscape:
                share();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
