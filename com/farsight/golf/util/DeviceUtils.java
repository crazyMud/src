package com.farsight.golf.util;

import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;


public class DeviceUtils {
	
	private static Context context;
	public static String getDeviceUUID(final Context context) {
		
		final TelephonyManager tm = (TelephonyManager)context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;

		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}
	public static String getAndroidID(final Context context) {
		
		final String androidId;
		androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		return androidId;
	}
	public static String getDeviceID(final Context context) {
		final TelephonyManager tm = (TelephonyManager)context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice;
		tmDevice = "" + tm.getDeviceId();
		return tmDevice;
	}
	
	public static String getVersion(final Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					context.getPackageName(), 0);
			String version = "V" + info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取当前程序版本code
	 * 
	 * @return
	 */
	public static String getPackageCode(final Context context) {
		String code = "";
		try {
			// PackageManager pm = this.getPackageManager();
			// PackageInfo pi = null;
			// pi = pm.getPackageInfo(this.getPackageName(), 0);
			// code = pi.versionCode;
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			code = info.versionName;
			return code;
		} catch (Exception e) {
			code = ""; // failed, ignored
		}
		return code;
	}
	public static String getVersion() {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					context.getPackageName(), 0);
			String version = "V" + info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取当前程序版本code
	 * 
	 * @return
	 */
	public static String getPackageCode() {
		String code = "";
		try {
			// PackageManager pm = this.getPackageManager();
			// PackageInfo pi = null;
			// pi = pm.getPackageInfo(this.getPackageName(), 0);
			// code = pi.versionCode;
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			code = info.versionName;
			return code;
		} catch (Exception e) {
			code = ""; // failed, ignored
		}
		return code;
	}
	public static void setContext(Context context) {
		DeviceUtils.context = context;
	}
}
