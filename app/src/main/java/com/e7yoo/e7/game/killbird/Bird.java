package com.e7yoo.e7.game.killbird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bird {
	int v;
	Bitmap pic[]=new Bitmap[4];
	Bitmap apic[]=new Bitmap[4];
	int us=1;
	int upic=1;
	int x,y;
	float mx;
	float vx;
	int height,width;
	float g=5;
	int level=1;
	int isFly=0;
	float fg=0;
	float fv=0;
	int mh;
	public Bird(int v, float g, int level, int x, int y, int mx, int mh, int height, int width, Bitmap pic[], Bitmap apic[]){
		this.v=v;
		this.pic=pic;
		this.apic=apic;
		this.x=x;
		this.y=y;
		this.mx=mx;
		this.height=height;
		this.width=width;
		this.level=level;
		this.g=g;
		this.mh=mh;
		isFly=0;
	}
	
	public boolean act(int delta,int up){
		boolean flag=false;
		if (isFly!=0){
			y+=isFly*fv;
			fv-=fg;
			if (fv<=0) isFly=0;
			if (x>=mx-height/2-5) vx=up;
			x=x-(int)vx;
			vx-=g;
			return false;
		}
		if (x>=mx-height/2-5) {
			vx=up;
			flag=true;
		}
		if (x<mh) vx=0;
		x=x-(int)vx;
		y=y+v*level/4;
		vx-=g;
		us++;
		if (us>=12) us=3;
		upic=us/3;
		return flag;
	}
	
	public void levelUp(int is,int v,int vx){
		isFly=is;
		fv=v;
		fg=v/10;
		if (fg<=0) fg=1;
		if (level<4) level++; 
		this.vx=-vx;
	}
	
	public void draw(Canvas canvas){
		// Paint paint=new Paint();
		if (level<=upic){
			canvas.drawBitmap(pic[upic], new Rect(0,0,pic[upic].getWidth(),pic[upic].getHeight()),  new Rect(y-width/2,x-height/2,y+width/2,x+height/2), null);
		}else{
			canvas.drawBitmap(apic[upic], new Rect(0,0,apic[upic].getWidth(),apic[upic].getHeight()),  new Rect(y-width/2,x-height/2,y+width/2,x+height/2), null);
		}
	}
	
	
	
	
}
