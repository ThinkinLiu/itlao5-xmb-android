package com.e7yoo.e7.game.plane;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.sdsmdg.tastytoast.TastyToast;

public class PlaneMainActivity extends BaseActivity implements Button.OnClickListener {
	ImageView ivBg;

	@Override
	protected String initTitle() {
		return null;
	}

	@Override
	protected int initLayoutResId() {
		return R.layout.activity_game_plane_main;
	}

	@Override
	protected void initView() {
		ivBg = (ImageView) findViewById(R.id.iv_bg);
	}

	@Override
	protected void initSettings() {
		Glide.with(this).load(R.drawable.plane_mainbg).into(ivBg);
	}

	@Override
	protected void initViewListener() {

	}

	@Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btnGame){
            startGame();
        }
    }

    public void startGame(){
        Intent intent = new Intent(this, PlaneGameActivity.class);
        startActivity(intent);
    }
    
	@Override
	public void onBackPressed() {
		long nowTime = System.currentTimeMillis();
		if (nowTime - onBackPressedTime > 3000) {
			TastyToast.makeText(this, getString(R.string.game_exit), TastyToast.LENGTH_SHORT, TastyToast.WARNING);
			onBackPressedTime = nowTime;
			return;
		}
		super.onBackPressed();
	}
	long onBackPressedTime = 0;
}