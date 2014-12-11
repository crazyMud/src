package com.farsight.golf.util;

import android.os.CountDownTimer;
import android.util.Log;


public class CountTimer extends CountDownTimer {
	Callback callBack;
	final static int ON_TICK = 0;
	final static int ON_FINISHED = 1;
	Object object;
	public CountTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
	}
	public CountTimer(long millisInFuture, long countDownInterval, Callback callBack) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		this.callBack = callBack;
	}
	public CountTimer(long millisInFuture, long countDownInterval, Object object, Callback callBack) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		this.object = object;
		this.callBack = callBack;
	}
	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		//Log.i("CountTimer","onTick");
		callBack.onCallBack(ON_TICK,millisUntilFinished);

	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		Log.i("CountTimer","onFinish");
		callBack.onCallBack(ON_FINISHED,null);
		
	}

}
