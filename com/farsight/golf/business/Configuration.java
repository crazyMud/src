package com.farsight.golf.business;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import android.os.Environment;
import android.util.Log;

public class Configuration {

	// 文件的根目录
	public static String RESOURCE_PATH;
	static {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			RESOURCE_PATH = Environment.getExternalStorageDirectory()
					+ "/golf/";

			Log.d("SD卡检测", " 存在SD卡 并且可使用");
		} else {
			RESOURCE_PATH = Environment.getRootDirectory() + "/golf/";

			Log.d("SD卡检测", " 不存在SD卡使用内部存储卡");
		}

		// 初始话文件目录
		File file = new File(RESOURCE_PATH + "files/");
		boolean mkdirs = false;
		if (!(mkdirs = file.exists())) {
			file.mkdirs();
			mkdirs = true;
		}
		File file2 = new File(RESOURCE_PATH + "temp/");
		boolean mkdirs2 = false;
		if (!(mkdirs2 = file2.exists())) {
			file2.mkdirs();
			mkdirs2 = true;
		}

		boolean mkdirs3 = false;
		File file3 = new File(RESOURCE_PATH + "op/");
		if (!(mkdirs3 = file3.exists())) {
			file3.mkdirs();
			mkdirs3 = true;
		}
		boolean mkdirs4 = false;

		File file4 = new File(RESOURCE_PATH + "portrait/");
		if (!(mkdirs4 = file4.exists())) {
			file4.mkdirs();
			mkdirs4 = true;
		}
		
		boolean mkdirs5 = false;

		File file5 = new File(RESOURCE_PATH + "video/");
		if (!(mkdirs5 = file5.exists())) {
			file5.mkdirs();
			mkdirs5 = true;
		}

		if (!mkdirs) {
			Log.d("初始话文件夹", "创建文件失败" + RESOURCE_PATH + "files/");
		} else {
			Log.d("初始话文件夹", "创建文件成功" + RESOURCE_PATH + "files/");
		}
		if (!mkdirs2) {
			Log.d("初始话文件夹", "创建文件失败" + RESOURCE_PATH + "temp/");
		} else {
			Log.d("初始话文件夹", "创建文件成功" + RESOURCE_PATH + "temp/");
		}
		if (!mkdirs3) {
			Log.d("初始话文件夹", "创建文件失败" + RESOURCE_PATH + "op/");
		} else {
			Log.d("初始话文件夹", "创建文件成功" + RESOURCE_PATH + "op/");
		}

		if (!mkdirs4) {
			Log.d("初始话文件夹", "创建文件失败" + RESOURCE_PATH + "portrait/");
		} else {
			Log.d("初始话文件夹", "创建文件成功" + RESOURCE_PATH + "portrait/");
		}
		
		if (!mkdirs5) {
			Log.d("初始话文件夹", "创建文件失败" + RESOURCE_PATH + "video/");
		} else {
			Log.d("初始话文件夹", "创建文件成功" + RESOURCE_PATH + "video/");
		}

	}
	public static final String RESOURCE_PATH_FILE = RESOURCE_PATH + "files/";

	public static final String RESOURCE_PATH_PORTRAIT = RESOURCE_PATH + "Portrait/";

	// 最好不要硬编码获得SD卡的路径
	// public static final String RESOURCE_PATH = "/sdcard/golf/";

	public static final String RESOURCE_TEMP_PATH = RESOURCE_PATH + "temp/";

	public static final String RESOURCE_OPERATION_PATH = RESOURCE_PATH + "op/";

	public static final String PORTRAIT_PATH = RESOURCE_PATH + "portrait/";
	
	public static final String VIDEO_PATH = RESOURCE_PATH + "video/";

	public static long HEART_BEAT_TIME = 2000;

	public static Timer timer = new Timer("", true);
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

}
