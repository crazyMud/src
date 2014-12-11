package com.farsight.golf.ui;

import java.io.File;

import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.business.LoginBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.CountTimer;



public class StartPageActivity extends ActivityAbstract {
	final String TAG = this.getClass().getSimpleName();
	static boolean hasLogin = false;
	int tryCount = 2;
	SharedPreferences setting = MainApplication.getSetting();
	CountTimer pt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.v(TAG, "enter the start ui");
		setContentView(R.layout.activity_start_page);
		
		/*String url = "http://f.hiphotos.baidu.com/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26%3Bt%3Dgif/sign=16bf878bfadcd100d991f07313e22c75/f31fbe096b63f624461814858544ebf81b4ca3ea.jpg";
		File file = new File(Configuration.RESOURCE_TEMP_PATH + "test.jpeg");
		// 指定文件类型
		String[] allowedContentTypes = new String[] { "image/png", "image/jpeg", "image/gif", "image/jpg", ".*" };
		try {
			HttpClientAsync.downloadFile(url, file, allowedContentTypes, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpClientAsync.getFileByUrl(url,file,new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				Log.v(TAG,"return ");
				super.handleMessage(msg);
			}
			
		});*/
	//	pt = new CountTimer(30000, 1000, protectCallback);
		if(!isNetworkAvailable(this)) 
			Toast.makeText(getApplicationContext(), "无可用网络", 5000).show();
		else 
			login();
		
		
	}
	@Override
	protected void onResume() {

		super.onResume();
		
	}
	/**
	 * 
	 * @param objClass
	 */
	private void splashy(final Class<?> objClass) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				enterMain(objClass);
			}
		}, 1000);
	}
	
	 /**
	  *@category 进入主界面
	 * @param objClass
	 */
	public  void enterMain(Class<?> objClass) {
		
		 Intent intent = new Intent();
		 intent.setClass(StartPageActivity.this, objClass);
		 startActivity(intent);
		 finish();
		
		
	 }

	/**
	 * @category 用户登录
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void login() {
		String uri = "users/me";
		try {
			
			LoginBusiness.login(uri, StartPageActivity.this ,new Callback() {
	
				@Override
				public void onCallBack(int callBackCode, Object object) {
					// TODO Auto-generated method stub
					switch(callBackCode) {
					case 0:
						splashy(MainActivity.class);
						break;
					case 1:
						int switchFlag = setting.getInt("switcher", 0);
						splashy(switchFlag==0?SwitcherActivity.class:MainActivity.class);
						break;
					
					default:
						alertView(object.toString());
					}
				}
				
			});
		}catch(Exception e) {
			
		}
	}
	/**
	 * 
	 * 网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		boolean ret = false;
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				//System.out.println(info[i].getTypeName() + "<->" + info[i].getState());
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					ret = true;
				}
			}
		}
		return ret;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		
	}


	
}
