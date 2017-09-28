package com.e7yoo.e7;

import android.content.Intent;
import android.view.View;

import com.e7yoo.e7.game.GameActivity;
import com.e7yoo.e7.game.game2048.Game2048Activity;
import com.e7yoo.e7.game.killbird.KillBirdActivity;
import com.e7yoo.e7.game.plane.PlaneMainActivity;
import com.e7yoo.e7.util.ActivityUtil;

public class GameListActivity extends BaseActivity implements View.OnClickListener {
    private View m2048Layout, mKillBirdLayout, mPlaneLayout, mMoreLayout;

    @Override
    protected String initTitle() {
        return getString(R.string.more_game);
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_game_list;
    }

    @Override
    protected void initView() {
        m2048Layout = findViewById(R.id.gamelist_2048_layout);
        mKillBirdLayout = findViewById(R.id.gamelist_killbird_layout);
        mPlaneLayout = findViewById(R.id.gamelist_plane_layout);
        mMoreLayout = findViewById(R.id.gamelist_more_layout);
    }

    @Override
    protected void initSettings() {
    }

    @Override
    protected void initViewListener() {
        m2048Layout.setOnClickListener(this);
        mKillBirdLayout.setOnClickListener(this);
        mPlaneLayout.setOnClickListener(this);
        mMoreLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gamelist_2048_layout:
                ActivityUtil.toActivity(this, Game2048Activity.class);
                break;
            case R.id.gamelist_killbird_layout:
                ActivityUtil.toActivity(this, KillBirdActivity.class);
                break;
            case R.id.gamelist_plane_layout:
                ActivityUtil.toActivity(this, PlaneMainActivity.class);
                break;
            case R.id.gamelist_more_layout:
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra(GameActivity.INTENT_URL, "http://sda.4399.com/4399swf/upload_swf/ftp14/yzg/20141021/3a/game.htm");
                ActivityUtil.toActivity(this, intent);
                break;
        }
    }

}
