package com.e7yoo.e7.game.plane;

import android.os.Bundle;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;


public class PlaneGameActivity extends BaseActivity {

    private GameView gameView;


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

    }
}