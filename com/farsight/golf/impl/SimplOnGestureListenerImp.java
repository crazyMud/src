package com.farsight.golf.impl;


import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.farsight.golf.util.Callback;

public class SimplOnGestureListenerImp extends SimpleOnGestureListener {
	final int RIGHT = 0;  
	final int LEFT = 1; 
	final int TOP = 2;
	final int DOWN = 3;
	Callback callback;
	public SimplOnGestureListenerImp(final Callback callback) {
		this.callback = callback;
	}
	 @Override  
     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
		 try {
	         float x = e2.getX() - e1.getX();  
	         float y = e2.getY() - e1.getY();  
	
	      if (x > 0) {  //上右
	   	   if(Math.abs(x) > 200) {
	             doResult(RIGHT); 
	   	   } else
	   		   	doResult(TOP); 
	       } else if (x < 0) {  //下左
	       	if(Math.abs(x) > 200) {
	       		doResult(LEFT);
	       		
	       	} else
	       		doResult(DOWN);
	        }  
	           return true;  
		 }catch(Exception e) {
			 
		 }
		 return true;  
     }  
	 private void doResult(int action) {
		 callback.onCallBack(action, null);
	 }

}
