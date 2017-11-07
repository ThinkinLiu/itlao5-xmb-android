package com.e7yoo.e7.game.killbird;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;


public class KillBirdActivity extends BaseActivity implements View.OnClickListener {

    private SharedPreferences sp;
    private String game1="G1",game2="G2";
    public SafeInt games1,games2;
    private SoundPool soundp;
    private HashMap<String,Integer> soundm;
    private GameView gm;
	private View shareView;
	
    /**
     * 读取数据
     */
    public void LoadData(){
    	games1=new SafeInt(sp.getInt(game1, 0));
    	games2=new SafeInt(sp.getInt(game2, 0));
    }
    
    /**
     * 保存数据
     */
    public void SaveData(){
    	sp.edit().putInt(game1, games1.get()).commit();
		sp.edit().putInt(game2,games2.get()).commit();
    }
    
    /**
     * 刷新最高分
     * @param mode
     * @param score
     */
    public void refresh(int mode,int score){
    	if (mode==1){
    		if (score>=games1.get()) games1=new SafeInt(score);
    		SaveData();
    	}
    	if (mode==2){
    		if (score>=games2.get()) games2=new SafeInt(score);
    		SaveData();
    	}
    }
    
    /**
     * 获得最高分
     * @param mode
     * @return
     */
    public int get(int mode){
    	if (mode==1){
    		return games1.get();
    	}
    	if (mode==2){
    		return games2.get();
    	}
    	return 12345;
    }
    
    /**
     * 初始化音效
     */
    public void initMusic(){
    	soundp=new SoundPool(50, AudioManager.STREAM_MUSIC,100);
    	soundm=new HashMap<String,Integer>();
    	soundm.put("slide", soundp.load(this, R.raw.slide, 1));
    	soundm.put("flap", soundp.load(this, R.raw.flap, 1));
    	soundm.put("s1", soundp.load(this, R.raw.squish1, 1));
    	soundm.put("s2", soundp.load(this, R.raw.squish2, 1));
    	soundm.put("k1", soundp.load(this, R.raw.kick1, 1));
    	soundm.put("k2", soundp.load(this, R.raw.kick2, 1));
    }
    
    /**
     * 播放音效
     */
    public void playMusic(String str){
    	soundp.play(soundm.get(str), 1, 1, 0, 0, 1f);
    }
    
    public void onDestroy(){
    	soundp.release();
		gm.recycler();
		gm = null;
    	super.onDestroy();   
    }

	@Override
	protected String initTitle() {
		return null;
	}

	@Override
	protected boolean initTheme() {
		super.initTheme();
		//隐去状态栏部分(电池等图标和一切修饰部分)
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return true;
	}

	@Override
	protected int initLayoutResId() {
		sp = this.getSharedPreferences("SaveBirdGameData", this.MODE_PRIVATE);
		LoadData();
		initMusic();
		return R.layout.activity_game_killbird;
	}

	@Override
	protected void initView() {
		gm = (GameView) findViewById(R.id.gameView);
		shareView = findViewById(R.id.actionbar_share);
		shareView.setVisibility(View.GONE);
	}

	@Override
	protected void initSettings() {
	}

	@Override
	protected void initViewListener() {
		gm.setOnStopListener(new GameView.OnStopListener() {
			@Override
			public void onStart() {
				if(shareView != null) {
					shareView.setVisibility(View.GONE);
				}
			}
			@Override
			public void onStop() {
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
		shareView.setOnClickListener(this);

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
	
    public void onPause(){  
    	gm.isStop=true;
    	super.onPause();  
    }  
    
    public void onStop(){  
    	gm.isStop=true;
    	super.onStop();  
    }  
    
    public void onResume(){
    	gm.myDraw();
    	super.onResume();  
    }  
    
    public void onRestart(){
    	gm.myDraw();
    	super.onRestart();  
    }


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.actionbar_share:
				if(gm != null && gm.score != null) {
					toShare();
				}
				break;
		}
	}

	private void toShare() {
		ShareDialogUtil.show(this, null,
				getString(R.string.app_share_from),
				getString(R.string.game_share_text, getString(R.string.gamelist_killbird), gm.score.get() + ""),
				ShareDialogUtil.SHARE_IMAGE_PATH_TAKE_SCREENSHOT);
	}
}