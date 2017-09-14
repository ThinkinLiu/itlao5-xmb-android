package com.e7yoo.e7.game.killbird;

public class SafeInt {
	private int num[]=new int[10];
	private int safe=-11;
	
	public SafeInt(int x){
		for (int i=1;i<=8;i++) num[i]=0+safe;
		add(x);
	}
	
	public void add(int x){
		num[1]+=x;
		for (int i=1;i<=8;i++){
			num[i+1]+=(num[i]-safe)/10;
			num[i]=(num[i]-safe)%10+safe;
		}
	}
	
	public void minus(int x){
		if (x>get()) {
			minus(get());
			return;
		}
		num[1]-=x;
		for (int i=1;i<=8;i++){
			while(num[i]-safe<0){
				num[i]+=10;
				num[i+1]--;
			}
		}
	}
	public int get(){
		int ans=0;
		for (int i=8;i>=1;i--){
			ans=ans*10+num[i]-safe;
		}
		return ans;
	}
}
