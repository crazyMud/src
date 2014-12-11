package com.farsight.golf.main;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.farsight.golf.business.Configuration;
import com.farsight.golf.service.AssertService;
import com.loopj.android.http.PersistentCookieStore;
import com.yixia.camera.VCamera;

public class MainApplication extends Application  {
	public static JSONObject currentUser;
	final static String TAG = MainApplication.class.getSimpleName();
	private static PersistentCookieStore cookieStore;
	private List<Activity> activityList = new LinkedList<Activity>();
	private static MainApplication instance;
	private static ReentrantLock lock = new ReentrantLock();
	private static SharedPreferences setting;
	public static PersistentCookieStore getCookieStore() {
		lock.lock();
		if(cookieStore == null)
			cookieStore = new PersistentCookieStore(instance.getApplicationContext());
		lock.unlock();
		return cookieStore;
		 
	}
	public static SharedPreferences getSetting() {
		setting = instance.getSharedPreferences("setting", Activity.MODE_PRIVATE);
		return setting;
	}
	public static Editor getSettingEditor() {
		return getSetting().edit();
	}
	public static MainApplication getInstance() {
		return instance;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		/*File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		if (DeviceUtils.isZte()) {
			if (dcim.exists()) {
				VCamera.setVideoCachePath(dcim + "/Camera/VCameraDemo/");
			} else {
				VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/Camera/VCameraDemo/");
			}
		} else {
			VCamera.setVideoCachePath(dcim + "/Camera/VCameraDemo/");
		}*/
		VCamera.setVideoCachePath(Configuration.VIDEO_PATH);
		// 开启log输出,ffmpeg输出到logcat
		VCamera.setDebugMode(true);
		// 初始化拍摄SDK，必须
		VCamera.initialize(this);

		//解压assert里面的文件
		startService(new Intent(this, AssertService.class));
	}
	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish

	public void exit() {

		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);

	}
}
