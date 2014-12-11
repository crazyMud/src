package com.farsight.golf.ui;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.farsight.golf.R;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.util.GlobalVar;





/**
 * @author Joephone
 * 
 */
public class LeaderPageActivity extends ActivityAbstract {

    final String TAG = this.getClass().getSimpleName();
    DestoryReceiver receiver = new DestoryReceiver();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_leader_page);
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalVar.FINISH_ACTIVITY);
		registerReceiver(receiver, filter);
	
	}
	
	public void login(View view) {
		enter(LoginActivity.class);
		
	}
	public void register(View view) {
		enter(RegisterActivity.class);
		
	}
	
	public void enterMain(View view) {
		enter(MainActivity.class);
	}
	
	private <T> void enter(Class<T> cls) {
		 Intent intent = new Intent();
		 intent.setClass(LeaderPageActivity.this, cls);
		 startActivity(intent);

	}

	@Override
	protected void onDestroy() {
		try {
			// if(receiver.isOrderedBroadcast()) {
			Log.i(TAG, "unregisterReceiver");
			unregisterReceiver(receiver);

		} catch (Exception e) {

		}

		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			System.out.println("exit program");
			MainApplication.getInstance().exit();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
	
	
	
}
