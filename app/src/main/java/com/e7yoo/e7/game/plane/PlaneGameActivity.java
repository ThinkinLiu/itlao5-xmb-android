package com.e7yoo.e7.game.plane;

import android.view.View;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.ShareDialogUtil;


public class PlaneGameActivity extends BaseActivity implements View.OnClickListener {

    private GameView gameView;
    private View shareView;


    @Override
	public void onPause() {
        super.onPause();
        if(gameView != null){
            gameView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(gameView != null){
            gameView.destroy();
        }
        gameView = null;
    }

    @Override
    protected String initTitle() {
        return null;
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_game_plane_game;
    }

    @Override
    protected void initView() {
        gameView = (GameView)findViewById(R.id.gameView);
        shareView = findViewById(R.id.actionbar_share);
        shareView.setVisibility(View.GONE);
    }

    @Override
    protected void initSettings() {
        int[] bitmapIds = {
                R.drawable.plane_plane,
                R.drawable.plane_explosion,
                R.drawable.plane_yellow_bullet,
                R.drawable.plane_blue_bullet,
                R.drawable.plane_small,
                R.drawable.plane_middle,
                R.drawable.plane_big,
                R.drawable.plane_bomb_award,
                R.drawable.plane_bullet_award,
                R.drawable.plane_pause1,
                R.drawable.plane_pause2,
                R.drawable.plane_bomb
        };
        gameView.start(bitmapIds);
    }

    @Override
    protected void initViewListener() {
        shareView.setOnClickListener(this);
        gameView.setOnStatusListener(new GameView.OnStatusListener() {
            @Override
            public void onStart() {
                if(shareView != null) {
                    shareView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPause() {
                if(shareView != null) {
                    shareView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd() {
                if(shareView != null) {
                    shareView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_share:
                if(gameView != null) {
                    toShare();
                }
                break;
        }
    }

    private void toShare() {
        ShareDialogUtil.show(this, null,
                getString(R.string.app_share_from),
                getString(R.string.game_share_text, getString(R.string.gamelist_plane), gameView.getScore() + ""),
                ShareDialogUtil.SHARE_IMAGE_PATH_TAKE_SCREENSHOT);
    }
}