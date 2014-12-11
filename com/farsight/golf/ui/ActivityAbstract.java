package com.farsight.golf.ui;


import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.business.StatusManager;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.util.Callback;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

public abstract class ActivityAbstract extends Activity implements Observer {
	
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	protected ActivityObservable actObser = new ActivityObservable();
	final String TAG = this.getClass().getSimpleName();
	AlertDialog alert;
	protected int mYear;
	protected int mMonth;
	protected int mDay;
	protected int nativeStatusCode;
	protected static Gson gson =  new Gson();
	protected JsonParser jsonParser = new JsonParser();
	protected static final int IS_INIT = -1, IS_REFRESH = 10, IS_LOADING = 20;
	protected class DestoryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "DestoryReceiver" + intent.getAction());
			unregisterReceiver(this);
			finish();

		}
	}
	
	public void openShare() {
		mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
		// 设置分享图片, 参数2为图片的url地址
	

			mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
			mController.openShare(this, false);
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected static final int DATE_DIALOG_ID = 1;
	// public static String socketAddress;
	protected ProgressDialog progressDialog = null;
	@SuppressLint("SimpleDateFormat")
	protected static java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Log.i(TAG, "init the state instance");
		MainApplication.getInstance().addActivity(this);
		StatusManager instance = StatusManager.getInstance();
		instance.addObserver(this);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "destroy the state instance");
		try {
			super.onDestroy();
			StatusManager instance = StatusManager.getInstance();
			instance.deleteObserver(this);

		} catch(RuntimeException e) {
			Log.e(TAG,e.getMessage());
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Log.i(TAG,"UPDATE FROM OBSERVER");
		/*
		int login_STATE = 200;//StatusManager.getLOGIN_STATE();
		switch (login_STATE) {
		case 200:
			Log.d("dengluchengong ", "已用token在后台登陆成功登陆");
			break;
		case 401:// Token错误
		case 402:// Token超期
		case 500:// 服务器错误
		case 400:// 参数错误
		default:
			break;
		}*/
	}

	/**
	 * @param 提示信息显示
	 */
	public void alertView(String result) {
		Log.d(TAG,result);
		Context mContext = this;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.alert_no_btn, null);
		TextView text = (TextView) view.findViewById(R.id.alert_one_text);
		text.setText(result);
		Toast toast = new Toast(getApplicationContext());
		// toast.setGravity(Gravity.CENTER , 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
		toast.show();
		/*
		 alert = new AlertDialog.Builder(this).create(); alert.show();
		 alert.getWindow().setContentView(view);
		  
		 WindowManager.LayoutParams p = alert.getWindow().getAttributes();
		  
		 DisplayMetrics dm = new
		 DisplayMetrics();getWindowManager().getDefaultDisplay
		 ().getMetrics(dm);
		  
		  p.width = dm.widthPixels /2 ; alert.getWindow().setAttributes(p);
		  alert.setCanceledOnTouchOutside(true);*/
		 
	}

	/**
	 * 设置日期
	 */
	protected void setDateTime(Date date) {
		final Calendar c = Calendar.getInstance();
		if (date != null)
			c.setTime(date);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

	}

	/**
	 * 日期控件的事件
	 */
	protected DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateDisplay();
		}
	};

	protected void updateDateDisplay() {

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);

		}

		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;

		}
	}
	
	/**
	 * compute Sample Size
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	/**
	 * compute Initial Sample Size
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		// 上下限范围
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / 

minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	/**
	 * get Bitmap
	 * 
	 * @param imgFile
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static Bitmap getBitmapByFile(String imgFile, int minSideLength,
			int maxNumOfPixels) {
		if (imgFile == null || imgFile.length() == 0)
			return null;

		try {
			@SuppressWarnings("resource")
			FileDescriptor fd = new FileInputStream(imgFile).getFD();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// BitmapFactory.decodeFile(imgFile, options);
			BitmapFactory.decodeFileDescriptor(fd, null, options);

			options.inSampleSize = computeSampleSize(options, minSideLength,
					maxNumOfPixels);
			try {
				/* 这里一定要将其设置回false，因为之前我们将其设置成了true
				// 设置inJustDecodeBounds为true后，decodeFile并不分配空间，即，BitmapFactory解码出来的Bitmap为

Null,但可计算出原始图片的长度和宽度*/
				options.inJustDecodeBounds = false;

				Bitmap bmp = BitmapFactory.decodeFile(imgFile, options);
				return bmp == null ? null : bmp;
			} catch (OutOfMemoryError err) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @category 确认对话框
	 * @param callback
	 * @param title
	 * @param confirm text
	 * @param cancelText
	 */
	public void confirmDialog(final Callback callback,String title, String confirmText, String cancelText, String contentText) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.alert_yes_no, null);
		TextView text = (TextView) view.findViewById(R.id.txt_title);
		TextView textContent = (TextView) view.findViewById(R.id.txt_content);
		text.setText(title);
		textContent.setText(contentText==null?textContent.getText():contentText);
		final AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.show();
		alert.getWindow().setContentView(view);
		/*WindowManager.LayoutParams p = alert.getWindow().getAttributes();
		p.width = (int) (GlobalVar.screenWidth * 0.8);
		alert.getWindow().setAttributes(p);*/
		Button cancel = (Button) view.findViewById(R.id.btn_cancel);
		cancel.setText(cancelText==null?cancel.getText():cancelText);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
				callback.onCallBack(1, null);
			}
		});
		Button confirm = (Button) view.findViewById(R.id.btn_ok);
		confirm.setText(confirmText==null?confirm.getText():confirmText);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
				callback.onCallBack(0, null);
			}
		});

	}
	public void onBack(View view) {
		finish();
	}
	
	/**
	* 实现文本复制功能
	* add by wangqianzhou
	* @param content
	*/
	public static void copy(String content, Context context)
	{
	// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}
	/**
	* 实现粘贴功能
	* add by wangqianzhou
	* @param context
	* @return
	*/
	public static String paste(Context context)
	{
	// 得到剪贴板管理器
	ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
	return cmb.getText().toString().trim();
	}
}
