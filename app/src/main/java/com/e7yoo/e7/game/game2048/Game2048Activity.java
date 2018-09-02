package com.e7yoo.e7.game.game2048;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.sdsmdg.tastytoast.TastyToast;

public class Game2048Activity extends BaseActivity implements OnClickListener {
	
	public Game2048Activity() {
		mainActivity = this;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected String initTitle() {
		return getString(R.string.game2048);
	}

	@Override
	protected int initLayoutResId() {
		return R.layout.activity_game_game2048;
	}

	@Override
	protected void initView() {
		gameView = (GameView) findViewById(R.id.gameView);
		tvScore = (TextView) findViewById(R.id.tvScore);
		tvChangeColumn = (TextView) findViewById(R.id.change_column);
	}

	@Override
	protected void initSettings() {

	}

	@Override
	protected void initViewListener() {
		setLeftTvListener(this);
		setRightTv(View.VISIBLE, R.mipmap.ic_menu_white_24dp, 0, this);
		tvChangeColumn.setOnClickListener(this);
		gameView.setOnShareClickListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				toShare();
			}
		});
	}

	public void setChangeColumn() {
		int column = gameView.getColumnCount() + 1;
		if(column > GameView.MAX_COLUMN) {
			column = GameView.MIN_COLUMN;
		} else if(column < GameView.MIN_COLUMN) {
			column = GameView.MAX_COLUMN;
		}
		tvChangeColumn.setText(getString(R.string.game_2048_change_column, column, column));
	}

	// 清空分数
	public void clearScore() {
		score = 0;
		showScore();
	}

	// 在界面上显示分数
	public void showScore() {
		tvScore.setText(score + "");
	}

	// 增加分数
	public void addScore(int s) {
		score += s;
		showScore();
	}

	// 取得分数
	public int getScore() {
		return score;
	}

	private int score = 0; // 所得的分数
	// private int usertime = 0; // 时间

	private TextView tvScore; // 显示分数的控件
	// private TextView tvusertime; // 显示时间的控件
	private TextView tvChangeColumn; // 切换方格
	private GameView gameView;
	private static Game2048Activity mainActivity = null;

	public static Game2048Activity getMainActivity() {
		return mainActivity;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.titlebar_left_tv:
				onBackPressed();
				break;
			case R.id.change_column:
				gameView.setColumn(gameView.getColumnCount() + 1);
				break;
			case R.id.titlebar_right_tv:
				toShare();
				break;
			default:
				break;
		}
	}

	private void toShare() {
		shareTo(this, null,
				getString(R.string.app_share_from),
				getString(R.string.game_share_text, getString(R.string.gamelist_game2048), getScore() + ""),
				ShareDialogUtil.SHARE_IMAGE_PATH_TAKE_SCREENSHOT);
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