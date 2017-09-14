package com.e7yoo.e7.game.killbird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.e7yoo.e7.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Himi
 *
 */
public class GameView extends SurfaceView implements Callback, Runnable {

	//用于控制SurfaceView
	private SurfaceHolder sfh;
	//声明一个画笔
	private Paint paint;
	//文本的坐标
	@SuppressWarnings("unused")
	private int textX = 10, textY = 10;
	//声明一条线程
	private Thread th;
	//线程消亡的标识位
	private boolean flag;
	//声明一个画布
	private Canvas canvas;
	//声明屏幕的宽高
	private int screenW, screenH;
	//随机
	Random random=new Random();
	//背景
	private Bitmap bg_1,bg_2,bg_3;
	private float bg_scale;
	//水管
	private Bitmap pipe_up,pipe_down;
	private int pipeW;
	private int pipeD;
	private int fpipeD;
	private int pipeDx,pipeDy;
	private int pipeclose,pipeopen;
	private boolean pipeRunning=false;
	private int pipecloset,pipeopent;
	//鸟
	private ArrayList<Bird> birdlist=new ArrayList<Bird>();
	private Bitmap birdpic[][]=new Bitmap[5][4];
	private Bitmap abirdpic[]=new Bitmap[4];
	private int birdpics=3;
	private int birdH,birdW;
	private int deltatime=10;
	//血
	private Bitmap bloodpic;
	private int bloodW,bloodH;
	private int bloodtime;
	//得分
	public SafeInt score=new SafeInt(0);
	private Typeface mFace;
	private int fontsize=100;
	//模式
	private int mode;
	private boolean isStart;
	private boolean isEnd;
	//开始资源
	private Bitmap tap;
	private Bitmap ready;
	private int tapDx=0;
	private int ftapDx=0;
	private int tapWay;
	//GameUI
	private int scoreW,scoreH;
	private int gameoverW,gameoverH;
	private Bitmap scoreb[]=new Bitmap[6];
	private Bitmap gameover;
	private Bitmap game1,game2;
	private int gameH,gameW;
	private Bitmap logo;
	private int logoH,logoW;
	private Bitmap help1,help2;
	private int helpH,helpW;
	//黑屏
	private int lockS;
	//程序
	private KillBirdActivity main;
	@SuppressWarnings("unused")
	private Handler handler;
	//Draw
	private boolean isDraw=true;
	//暂停
	private Bitmap stop;
	public boolean isStop=false;
	//Temp
	private ArrayList<Bird> tbirdlist=new ArrayList<Bird>();
	/**
	 * SurfaceView初始化函数
	 */
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//实例化程序
		main=(KillBirdActivity) context;
		//实例SurfaceHolder
		sfh = this.getHolder();
		//为SurfaceView添加状态监听
		sfh.addCallback(this);
		//实例一个画笔
		paint = new Paint();
		//设置画笔颜色为白色
		paint.setColor(Color.WHITE);
		//设置焦点
		setFocusable(true);
		//初始化图片
		initPic();
		//InitVal
		isStart=true;
		isEnd=true;
	}

	public void recycler() {
		recycler(bg_1);
		recycler(bg_2);
		recycler(bg_3);

		recycler(pipe_up);
		recycler(pipe_down);
		recycler(birdpic[1][1]);
		recycler(birdpic[1][2]);
		recycler(birdpic[1][3]);
		recycler(birdpic[2][1]);
		recycler(birdpic[2][2]);
		recycler(birdpic[2][3]);
		recycler(birdpic[3][1]);
		recycler(birdpic[3][2]);
		recycler(birdpic[3][3]);
		recycler(abirdpic[1]);
		recycler(abirdpic[2]);
		recycler(abirdpic[3]);
		recycler(bloodpic);
		recycler(tap);
		recycler(ready);
		recycler(scoreb[1]);
		recycler(scoreb[2]);
		recycler(scoreb[3]);
		recycler(scoreb[4]);
		recycler(scoreb[5]);
		recycler(gameover);
		recycler(game1);
		recycler(game2);
		recycler(logo);
		recycler(help1);
		recycler(help2);
		recycler(stop);
	}

	private void recycler(Bitmap bmp) {
		if(bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
		}
		bmp = null;
	}

	
	/**
	 * 游戏逻辑
	 */
	private void logic() {
		if (isStart==false){
			
		}else if (isEnd==true){
			
		}else{
			actBird();
			addBird(); 
			isGameEnd();
		}
		runPipe();
	}
	
	/**
	 * 游戏绘图
	 */
	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			if (canvas != null) {
				drawBackBG(canvas);
				drawPipe(canvas);
				drawBlood(canvas);
				drawFrontBG(canvas);
				drawBird(canvas);
				drawStop(canvas);
				drawScore(canvas);
				drawTap(canvas);
				drawGameBoard(canvas);
				drawButton(canvas);
				drawLogo(canvas);
				drawLockScreen(canvas);
				//-----------利用填充矩形的方式，刷屏
				////绘制矩形
				//canvas.drawRect(0,0,this.getWidth(),
				//this.getHeight(), paint);
				//-----------利用填充画布，刷屏
				//canvas.drawColor(Color.WHITE);
				//-----------利用填充画布指定的颜色分量，刷屏
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}
	
	/**
	 * 游戏结束
	 */
	public void isGameEnd(){
		ArrayList<Bird> temp=birdlist;
		int counts=temp.size();
		for (int i=0;i<counts;i++){
			if (screenW<=(int)temp.get(i).y) {
				main.refresh(mode, score.get());
				isEnd=true;
				tbirdlist=new ArrayList<Bird>();
				lockScreen();
			}
			
		}
	}
	
	/**
	 * 开始按钮
	 */
	public void drawButton(Canvas canvas){
		if (isEnd==false) return;
		canvas.drawBitmap(game1,new Rect(0,0,game1.getWidth(),game1.getHeight()),new Rect(screenW*3/4-gameW/2,screenH/3-gameH/2,screenW*3/4+gameW/2,screenH/3+gameH/2), paint);
		canvas.drawBitmap(game2,new Rect(0,0,game2.getWidth(),game2.getHeight()),new Rect(screenW*3/4-gameW/2,screenH*2/3-gameH/2,screenW*3/4+gameW/2,screenH*2/3+gameH/2), paint);
	}
	
	/**
	 * 游戏得分扳
	 * @param canvas
	 */
	public void drawGameBoard(Canvas canvas){
		if (isEnd==false) return;
		if (mode==0) return;
		int s=score.get();
		int bs=main.get(mode);
		int index=1;
		if (score.get()>=30) index=2;
		if (score.get()>=100) index=3;
		if (score.get()>=150) index=4;
		if (score.get()>250) index=5;
		canvas.drawBitmap(gameover,new Rect(0,0,gameover.getWidth(),gameover.getHeight()),new Rect(screenW/4-gameoverW/2,screenH/4-gameoverH/2,screenW/4+gameoverW/2,screenH/4+gameoverH/2), paint);
		canvas.drawBitmap(scoreb[index],new Rect(0,0,scoreb[index].getWidth(),scoreb[index].getHeight()),new Rect(screenW/4-scoreW/2,screenH*3/4-scoreH/2,screenW/4+scoreW/2,screenH*3/4+scoreH/2), paint);
		drawText(String.valueOf(s), canvas,screenW/4-scoreW/2+(int)((float)0.707*scoreW), screenH*3/4-scoreH/2+(int)((float)0.307*scoreH),screenW/4-scoreW/2+(int)((float)0.885*scoreW), screenH*3/4-scoreH/2+(int)((float)0.438*scoreH), Color.WHITE);
		drawText(String.valueOf(bs), canvas,screenW/4-scoreW/2+(int)((float)0.707*scoreW), screenH*3/4-scoreH/2+(int)((float)0.658*scoreH),screenW/4-scoreW/2+(int)((float)0.885*scoreW), screenH*3/4-scoreH/2+(int)((float)0.789*scoreH), Color.WHITE);
	}
	
	/**
	 * 过度
	 */
	public void lockScreen(){
		lockS=30;
	}
	
	/**
	 * 黑屏过度
	 */
	public void drawLockScreen(Canvas canvas){
		if (lockS<=0) {
			lockS=0;
			return;
		}else{
			if (lockS>=27) 
				canvas.drawRGB(0, 0, 0);
			else
				canvas.drawARGB(100*lockS/30, 0, 0, 0);
			lockS--;
		}
	}
	
	/**
	 * 展现得分
	 */
	public void drawScore(Canvas canvas){
		int s=score.get();
		if (isStart==true && isEnd==false) drawScoreText("Score:"+ String.valueOf(s),canvas,screenW/4*3,0,fontsize, Color.WHITE);
	}
	
	/**
	 * 添加小鸟
	 */
	public void addBird(){
		if (deltatime<=0){
			Bird temp=null;
			if (mode==1){
				int level=1;
				int aspeed=0;
				if (score.get()>=0) {
					level=1;
					aspeed=screenW/96;
					if (score.get()<500) aspeed=score.get()*aspeed/500;
					temp=new Bird(screenW/96+aspeed+getRandom(-screenW/288,screenW/288),(float)screenH/720,level,screenH/2,getRandom(-birdW*2,-birdW/2),screenH/2+fpipeD-screenH*2/720,screenH/2-fpipeD,birdH,birdW,birdpic[getRandom(1,birdpics)],abirdpic);
					birdlist.add(temp);
				}
				if (score.get()>=10) {
					level=1;
					if (score.get()>50) level=2;
					aspeed=screenW/96;
					if (score.get()<500) aspeed=score.get()*aspeed/500;
					temp=new Bird(screenW/96+aspeed+getRandom(-screenW/288,screenW/288),(float)screenH/720,level,screenH/2,getRandom(-birdW*2,-birdW/2),screenH/2+fpipeD-screenH*2/720,screenH/2-fpipeD,birdH,birdW,birdpic[getRandom(1,birdpics)],abirdpic);
					birdlist.add(temp);
				}
				if (score.get()>=100) {
					level=1;
					if (score.get()>180) level=2;
					if (score.get()>300) level=3;
					aspeed=screenW/96;
					if (score.get()<500) aspeed=score.get()*aspeed/500;
					temp=new Bird(screenW/96+aspeed+getRandom(-screenW/288,screenW/288),(float)screenH/720,level,screenH/2,getRandom(-birdW*2,-birdW/2),screenH/2+fpipeD-screenH*2/720,screenH/2-fpipeD,birdH,birdW,birdpic[getRandom(1,birdpics)],abirdpic);
					birdlist.add(temp);
				}
			}
			if (mode==2){
				boolean flag=true;
				if (birdlist.size()+1>=3 && score.get()<=50) flag=false;
				if (birdlist.size()+1>=8 && score.get()<=100) flag=false;
				if (birdlist.size()+1>=15 && score.get()<=200) flag=false;
				if (flag==true) {
					temp=new Bird(screenW/144,(float)screenH/720,1,screenH/2,getRandom(-birdW*2,-birdW/2),screenH/2+fpipeD-screenH*2/720,screenH/2-fpipeD,birdH,birdW,birdpic[getRandom(1,birdpics)],abirdpic);
					birdlist.add(temp);
				}
			}
			deltatime=50;
		}else{
			deltatime--;
		}
		
	}
	/**
	 * 小鸟动画
	 */
	private void actBird(){
		ArrayList<Bird> temp=birdlist;
		int counts=temp.size();
		for (int i=0;i<counts;i++){
			if (temp.get(i).act(50,getRandom(10*screenH/720,15*screenH/720))) main.playMusic("flap");;
		}
	}
	/**
	 * 绘制小鸟
	 */
	public void drawBird(Canvas canvas){
		ArrayList<Bird> temp=birdlist;
		int counts=temp.size();
		for (int i=0;i<counts;i++){
			temp.get(i).draw(canvas);;
		}
	}
	/**
	 * 杀死小鸟
	 */
	public void killBird(int up,int down){
		ArrayList<Bird> temp=birdlist;
		ArrayList<Bird> finish=new ArrayList<Bird>();
		int counts=temp.size();
		for (int i=0;i<counts;i++){
			int x=temp.get(i).x;
			int y=temp.get(i).y;
			if (screenW/2-pipeW/2<=y && screenW/2+pipeW/2>=y && (x-birdH/2<=up||x+birdH/2>=down)){
				if (bloodtime<=0) bloodtime=1;
				main.playMusic("s"+ String.valueOf(getRandom(1,2)));
				if (mode==1) score.add(1);
				if (mode==2) {
					isEnd=true;
					main.refresh(mode, score.get());
				}
			}else{
				finish.add(temp.get(i));
			}
		}
		birdlist=finish;
	}
	/**
	 * 弹出小鸟
	 */
	public void rapBird(int up,int down){
		ArrayList<Bird> temp=birdlist;
		int counts=temp.size();
		for (int i=0;i<counts;i++){
			int x=temp.get(i).x;
			int y=temp.get(i).y;
			if (screenW/2-pipeW/2>=y && screenW/2-pipeW/2<=y+birdW/2 && (x-birdH/2<=up||x+birdH/2>=down)){
				//temp.get(i).levelUp(-1,screenW/20*(pipeW-screenW/2+y)/pipeW*2);
				if (mode==1)temp.get(i).levelUp(-1,temp.get(i).level*temp.get(i).v*2*(pipeW-screenW/2+y)/pipeW*2,screenH/40);
				if (mode==2)temp.get(i).levelUp(-1,temp.get(i).level*temp.get(i).v*3*(pipeW-screenW/2+y)/pipeW*2,screenH/40);
				main.playMusic("k"+ String.valueOf(getRandom(1,2)));
				if (mode==2) score.add(1);
			}else if (screenW/2+pipeW/2>=y-birdW/2 && screenW/2+pipeW/2<=y && (x-birdH/2<=up||x+birdH/2>=down)){
				if (mode==2) score.add(1);
				temp.get(i).levelUp(1,temp.get(i).level*temp.get(i).v,screenH/40);
				main.playMusic("k"+ String.valueOf(getRandom(1,2)));
			}else if (screenW/2-pipeW/2<=y && screenW/2+pipeW/2>=y && (x-birdH/2<=up||x+birdH/2>=down)){
				if (mode==1)temp.get(i).levelUp(-1,temp.get(i).level*temp.get(i).v*2*(pipeW-screenW/2+y)/pipeW*2,screenH/40);
				if (mode==2)temp.get(i).levelUp(-1,temp.get(i).level*temp.get(i).v*3*(pipeW-screenW/2+y)/pipeW*2,screenH/40);
				main.playMusic("k"+ String.valueOf(getRandom(1,2)));
				if (mode==2) score.add(1);
			}
		}
	}
	
	/**
	 * 管子动画
	 */
	public void runPipe(){
		if(pipeopen!=0 || pipeclose!=0){
			pipeDx=getRandom(-5,5);
			pipeDy=getRandom(-5,5);
			if (pipeopen>pipeopent) pipeDx=pipeDy=0;
		}
		if (pipeopen!=0){
			if (pipeopen>pipeopent){
				pipeopen--;
			}else{
				pipeD+=fpipeD/pipeopent;
				pipeopen--;
				if (pipeopen<=0) {
					pipeopen=0;
					pipeD=fpipeD;
					pipeDx=pipeDy=0;
					pipeRunning=false;
				}
			}
			
		}
		if (pipeclose!=0){
			pipeD-=fpipeD/pipecloset;
			pipeclose--;
			if (pipeclose<=0) {
				pipeopen=pipeopent+5;
				pipeclose=0;
				pipeD=0;
				pipeDx=pipeDy=0;
			}
			killBird(screenH/2-pipeD,screenH/2+pipeD);
		}
		rapBird(screenH/2-pipeD,screenH/2+pipeD);
	}
	
	/**
	 * 开始水管动画
	 */
	public void startPipe(){
		pipeRunning=true;
		pipeclose=pipecloset;
		main.playMusic("slide");
	}
	
	/**
	 * 血液绘制
	 */
	public void drawBlood(Canvas canvas){
		if (pipeopen<=0&&bloodtime==1) return;
		if (bloodtime<=0) return;
		int w=bloodpic.getWidth()/5;
		int h=bloodpic.getHeight()/6;
		int x=(bloodtime+1)%5-1-1;
		int y=(bloodtime-1)/5;
		canvas.drawBitmap(bloodpic, new Rect(x*w,y*h,x*w+w,y*h+h), new Rect(screenW/2-bloodW/2,screenH/2,screenW/2+bloodW/2,screenH/2+bloodH), paint);
		canvas.drawBitmap(bloodpic, new Rect(x*w,y*h,x*w+w,y*h+h), new Rect(screenW/2-bloodW/2,screenH/2,screenW/2+bloodW/2,screenH/2+bloodH), paint);
		bloodtime++;
		bloodtime++;
		bloodtime++;
		if (bloodtime>=30) bloodtime=0;
	}
	
	/**
	 * 开始动画
	 */
	public void drawTap(Canvas canvas){
		if (isStart==false){
			canvas.drawBitmap(ready, new Rect(0,0,ready.getWidth(),ready.getHeight()),new Rect(screenW/2-ready.getWidth()/2,screenH/4-ready.getHeight()/2+tapDx,screenW/2+ready.getWidth()/2,screenH/4+ready.getHeight()/2+tapDx),paint);
			canvas.drawBitmap(tap, new Rect(0,0,tap.getWidth(),tap.getHeight()),new Rect(screenW/2-tap.getWidth()/2,screenH/4*3-tap.getHeight()/2+tapDx,screenW/2+tap.getWidth()/2,screenH/4*3+tap.getHeight()/2+tapDx),paint);
			if (mode==1)canvas.drawBitmap(help1, new Rect(0,0,help1.getWidth(),help1.getHeight()), new Rect(screenW/2-helpW/2,screenH/2-helpH/2,screenW/2+helpW/2,screenH/2+helpH/2),paint);
			if (mode==2)canvas.drawBitmap(help2, new Rect(0,0,help1.getWidth(),help1.getHeight()), new Rect(screenW/2-helpW/2,screenH/2-helpH/2,screenW/2+helpW/2,screenH/2+helpH/2),paint);
			if (tapDx*tapDx>=ftapDx*ftapDx) tapWay*=-1;
			tapDx+=tapWay;
		}
	}
	
	/**
	 * 背景的绘制
	 */
	public void drawBackBG(Canvas canvas){
		//bg_scale=(float)screenH/3/bg_3.getHeight();
		canvas.drawColor(Color.rgb(36, 191, 242));
		for (int i=1;i<=screenW;i+=bg_3.getWidth()*bg_scale){
			canvas.drawBitmap(bg_3, new Rect(0,0,bg_3.getWidth(),bg_3.getHeight()), new Rect(i,(int)(screenH-bg_3.getHeight()*bg_scale-bg_1.getHeight()*bg_scale),(int)(i+bg_3.getWidth()*bg_scale),(int)(screenH-bg_1.getHeight()*bg_scale)), paint);
		}
		for (int i=1;i<=screenW;i+=bg_2.getWidth()*bg_scale){
			canvas.drawBitmap(bg_2, new Rect(0,0,bg_2.getWidth(),bg_2.getHeight()), new Rect(i,(int)(screenH-bg_2.getHeight()*bg_scale-bg_1.getHeight()*bg_scale),(int)(i+bg_2.getWidth()*bg_scale),(int)(screenH-bg_1.getHeight()*bg_scale)), paint);
		}
	}
	public void drawFrontBG(Canvas canvas){
		//bg_scale=(float)screenH/3/bg_3.getHeight();
		for (int i=1;i<=screenW;i+=bg_1.getWidth()*bg_scale){
			canvas.drawBitmap(bg_1, new Rect(0,0,bg_1.getWidth(),bg_1.getHeight()), new Rect(i,(int)(screenH-bg_1.getHeight()*bg_scale),(int)(i+bg_1.getWidth()*bg_scale),screenH), paint);
		}
	}
	
	
	/**
	 * LOGO
	 */
	public void drawLogo(Canvas canvas){
		if (mode!=0) return ;
		canvas.drawBitmap(logo, new Rect(0,0,logo.getWidth(),logo.getHeight()),new Rect(screenW/4-logoW/2,screenH/2-logoH/2,screenW/4+logoW/2,screenH/2+logoH/2),paint);
	}
	/**
	 * 绘制水管
	 */
	public void drawPipe(Canvas canvsa){
		//pipeW=screenW/10;
		canvas.drawBitmap(pipe_up, new Rect(0,pipe_up.getHeight()-screenH/2,pipe_up.getWidth(),pipe_up.getHeight()), new Rect(screenW/2-pipeW/2+pipeDy,0,screenW/2+pipeW/2+pipeDy,screenH/2-pipeD+pipeDx),paint);
		canvas.drawBitmap(pipe_down, new Rect(0,0,pipe_down.getWidth(),screenH/2), new Rect(screenW/2-pipeW/2-pipeDy,screenH/2+pipeD-pipeDx,screenW/2+pipeW/2-pipeDy,screenH),paint);
	}
	
	/**
	 * 绘制暂停
	 */
	public void drawStop(Canvas canvas){
		if (isEnd==true || isStart==false || mode==0) return;
		if (isStop==false)canvas.drawBitmap(stop, new Rect(0,0,stop.getWidth(),stop.getHeight()),new Rect(screenW-50,0,screenW,50), paint);
		if (isStop==true){
			canvas.drawARGB(50, 0, 0, 0);
			drawText("点击屏幕返回游戏",canvas,screenW/2,screenH/2,fontsize, Color.WHITE);
		}
	
	}
	/**
	 * 绘制文字
	 * @param str
	 * @param canvas
	 */
	private void drawText(String str, Canvas canvas, int x1, int y1, int x2, int y2, int color){

		Paint countPaint = new Paint();
		countPaint.setColor(color);
		countPaint.setTextSize(100);
		countPaint.setTypeface(mFace);
		countPaint.setTextAlign(Paint.Align.CENTER);
		Rect textBounds = new Rect();
		countPaint.getTextBounds(str, 0, str.length(), textBounds);//get text bounds, that can get the text width and height
		int textWidth =textBounds.right-textBounds.left;
		int textHeight=textBounds.bottom-textBounds.top;
		while (textWidth>x2-x1 || textHeight>y2-y1){
			countPaint.setTextSize(countPaint.getTextSize()-1);
			countPaint.getTextBounds(str, 0, str.length(), textBounds);
			textWidth =textBounds.right-textBounds.left;
			textHeight=textBounds.bottom-textBounds.top;
		}
		canvas.drawText(str, (x2-x1)/2+x1-textWidth/2, (y2-y1)/2+y1+textHeight/2,countPaint);
	}
	private void drawText(String str, Canvas canvas, int x, int y, int size, int color){
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(color);
		countPaint.setTextSize(size);
		//countPaint.setTextAlign(Paint.Align.CENTER);
		Rect textBounds = new Rect();
		countPaint.getTextBounds(str, 0, str.length(), textBounds);//get text bounds, that can get the text width and height
		int textWidth =textBounds.right-textBounds.left;
		while (x+textWidth/2>screenW||x-textWidth/2<0){
			countPaint.setTextSize(countPaint.getTextSize()-1);
			countPaint.getTextBounds(str, 0, str.length(), textBounds);
			textWidth =textBounds.right-textBounds.left;
		}
		int textHeight = textBounds.bottom - textBounds.top;
		canvas.drawText(str, x-textWidth/2, y + textHeight/2,countPaint);
	}
	private void drawScoreText(String str, Canvas canvas, int x, int y, int size, int color){
		Paint countPaint = new Paint();
		countPaint.setColor(color);
		countPaint.setTextSize(size);
		countPaint.setTypeface(mFace);
		//countPaint.setTextAlign(Paint.Align.CENTER);
		Rect textBounds = new Rect();
		countPaint.getTextBounds(str, 0, str.length(), textBounds);//get text bounds, that can get the text width and height
		int textWidth =textBounds.right-textBounds.left;
		int textHeight = textBounds.bottom - textBounds.top;
		canvas.drawText(str, x-textWidth/2, y+textHeight*2,countPaint);
	}
	
	
	/**
	 * 获得图片资源
	 */
	public void initPic(){
		bg_1=getRes(R.drawable.bird_bg_ground);
		bg_2=getRes(R.drawable.bird_bg_trees);
		bg_3=getRes(R.drawable.bird_bg_city);
		pipe_up=getRes(R.drawable.bird_pipe_up);
		pipe_down=getRes(R.drawable.bird_pipe_down);
		birdpic[1][1]=getRes(R.drawable.bird1_1);
		birdpic[1][2]=getRes(R.drawable.bird1_2);
		birdpic[1][3]=getRes(R.drawable.bird1_3);
		birdpic[2][1]=getRes(R.drawable.bird2_1);
		birdpic[2][2]=getRes(R.drawable.bird2_2);
		birdpic[2][3]=getRes(R.drawable.bird2_3);
		birdpic[3][1]=getRes(R.drawable.bird3_1);
		birdpic[3][2]=getRes(R.drawable.bird3_2);
		birdpic[3][3]=getRes(R.drawable.bird3_3);
		abirdpic[1]=getRes(R.drawable.bird_angrybird1);
		abirdpic[2]=getRes(R.drawable.bird_angrybird2);
		abirdpic[3]=getRes(R.drawable.bird_angrybird3);
		bloodpic=getRes(R.drawable.bird_blood);
		tap=getRes(R.drawable.bird_tap);
		ready=getRes(R.drawable.bird_ready);
		scoreb[1]=getRes(R.drawable.bird_score);
		scoreb[2]=getRes(R.drawable.bird_score1);
		scoreb[3]=getRes(R.drawable.bird_score2);
		scoreb[4]=getRes(R.drawable.bird_score3);
		scoreb[5]=getRes(R.drawable.bird_score4);
		gameover=getRes(R.drawable.bird_gameover);
		game1=getRes(R.drawable.bird_game1);
		game2=getRes(R.drawable.bird_game2);
		logo=getRes(R.drawable.bird_logo);
		help1=getRes(R.drawable.bird_help1);
		help2=getRes(R.drawable.bird_help2);
		stop=getRes(R.drawable.bird_stop);
	}
	
	/**
	 * 初始化一些数值
	 */
	public void initVal(){
		bg_scale=(float)screenH/3/bg_3.getHeight();
		pipeW=screenW/8;
		fpipeD=pipeD=screenH/8;
		pipeDx=pipeDy=0;
		pipeclose=pipeopen=0;
		pipecloset=2;
		pipeopent=10;
		pipeRunning=false;
		birdH=screenH/16;
		birdW=birdpic[1][1].getWidth()*birdH/birdpic[1][1].getHeight();
		bloodW=pipeW*2;
		bloodH=screenH/4;
		bloodtime=0;
		score=new SafeInt(0);
		tapDx=0;
		tapWay=screenH/320;
		ftapDx=screenH/80;
		scoreW=screenW*8/2/10;
		scoreH=scoreW*scoreb[1].getHeight()/scoreb[1].getWidth();
		gameoverW=screenW*6/2/10;
		gameoverH=gameoverW*gameover.getHeight()/gameover.getWidth();;
		gameW=screenW/4;
		gameH=game1.getHeight()*gameW/game1.getWidth();
		logoW=screenW/2/10*9;
		logoH=logo.getHeight()*logoW/logo.getWidth();
		helpH=fpipeD*2;
		helpW=helpH*help1.getWidth()/help1.getHeight();
		lockS=0;
		birdlist=tbirdlist;
	}
	
	/**
	 * 初始化游戏信息
	 */
	public void initGame(){
		birdlist=new ArrayList<Bird>();
		bloodtime=0;
		score=new SafeInt(0);
		pipeDx=pipeDy=0;
		pipeclose=pipeopen=0;
		fpipeD=pipeD=screenH/8;
		pipeRunning=false;
		isStart=false;
		isEnd=false;
	}
	/**
	 * 计算字体大小
	 */
	private void initFontSize(){
		mFace = Typeface.createFromAsset(getContext().getAssets(),"233.ttf");
		String str="1000";
		Paint countPaint=new Paint();
		countPaint.setTypeface(mFace);
		countPaint.setTextSize(fontsize);
		Rect textBounds = new Rect();
		countPaint.getTextBounds(str, 0, str.length(), textBounds);//get text bounds, that can get the text width and height
		while (textBounds.height()>screenH/4){
			fontsize--;
			countPaint.setTextSize(fontsize);
			countPaint.getTextBounds(str, 0, str.length(), textBounds);
		}
	}
	
	/**
	 * 获得随机数
	 */
	private int getRandom(int min,int max){  
		int ran= Math.abs(random.nextInt());
		int returnRan=ran%(max-min+1)+min;  
		return returnRan;
	}
	
	/**
	 * 触屏事件监听
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x=textX = (int) event.getX();
		int y=textY = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isStop==true){
				isStop=false;
				return true;
			}
			if (!isEnd && isStart && screenW-50<=x && x<=screenW && y<=50){
				isStop=true;
				myDraw();
				//main.showAD();
				return true;
			}
			if (!isStop && !pipeRunning && isStart && !isEnd) startPipe();
			if (!isStop && !isStart) isStart=true;
			if (isEnd){
				if (screenW*3/4-gameW/2<=x && screenH/3-gameH/2<=y && screenW*3/4+gameW/2 >=x && screenH/3+gameH/2>=y){
					mode=1;
					initGame();
					lockScreen();
				}
				if (screenW*3/4-gameW/2<=x && screenH*2/3-gameH/2<=y && screenW*3/4+gameW/2 >=x && screenH*2/3+gameH/2>=y){
					mode=2;
					initGame();
					lockScreen();
				}
			}
		}
		return true;
	}
	
	/**
	 * 按键事件监听
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 返回图片资源
	 */
	public Bitmap getRes(int resID) {
		return BitmapFactory.decodeResource(getResources(),resID);
	}
	
	/**
	 * SurfaceView视图创建，响应此函数
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		screenW = this.getWidth();
		screenH = this.getHeight();
		//初始化数值
		initVal();
		//字体初始化
		initFontSize();
		//线程开关
		flag = true;
		//实例线程
		th = new Thread(this);
		//启动线程
		th.start();
	}
	
	/**
	 * SurfaceView视图状态发生改变，响应此函数
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}
	
	/**
	 * SurfaceView视图消亡时，响应此函数
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		tbirdlist=birdlist;
		flag = false;
	}
	
	/**
	 * 游戏线程
	 */
	public void run() {
		while (flag) {
			isDraw=!isDraw;
			if (isDraw)myDraw();
			if (isStop==false) {
				long start = System.currentTimeMillis();
				logic();
				long end = System.currentTimeMillis();
				try {
					if (end - start < 30) {
						Thread.sleep(30 - (end - start));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}