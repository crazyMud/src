package com.farsight.golf.service;



import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.farsight.golf.business.MessageBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityObservable;
import com.farsight.golf.ui.MainActivity;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.ConstUtil;
import com.loopj.android.http.RequestParams;

public class MessageService extends Service {
	public static final String ACTION = "com.farsight.golf.service.MessageService";
	static String TAG = "MessageService";
	protected ActivityObservable actObser;
	RequestParams params = new RequestParams();;
	String uri = "messages/";
	boolean hasReturn = true;
	public static Map<String,Object> message = new HashMap<String,Object>(); 
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return new CallBackBinder();
	}
	@Override
	public void onDestroy() {
		try {
			hasReturn = false;
		}catch(Exception e) {
			
		}
	}	
	private void getMessage() {
		Log.d(TAG,"get the message......");
			if(MainApplication.currentUser != null)
			MessageBusiness.getNotify(uri, params, MessageService.this, new Callback() {
		
					@Override
					public void onCallBack(int callBackCode, Object object) {
						Log.d(TAG,object.toString());
						try {
							Thread.sleep(10000);
							if(callBackCode == 0) {
								
								JSONArray jsonArray = (JSONArray) object;
								
								actObser.setDate(ConstUtil.MESSAGE_FLAG);
							}
							if(hasReturn)
								getMessage();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
			});
	
	}
	
	
	@Override
	public void onCreate() {
		Log.d(TAG,"start the service......");
		if(MainActivity.mainObserver == null) {
			stopSelf();
			return;
		}
		
		if(actObser == null) actObser = new ActivityObservable();
		actObser.addObserver(MainActivity.mainObserver);
		//getMessage();

		
	}
	 public class CallBackBinder extends Binder {
	    	public MessageService getMessageService() {
	    		return MessageService.this;
	    	}
	}
	 

	
}
