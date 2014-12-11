package com.farsight.golf.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.farsight.golf.R;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.HttpClientAsync;
import com.loopj.android.http.RequestParams;

public class MessageBusiness extends HttpClientAsync {
	protected static final String TAG = "HotPointerBusiness";
	
	
	public static void getNotify(String uri, final RequestParams params,
			final Context mContext, final Handler handler, Integer what) {
		Message msg = handler.obtainMessage();
		msg.what = what;
		executeGet(uri, params, mContext, handler, msg);
		
	}
	
	public static void getNotify(String uri, final RequestParams params, final Context mContext, final Callback callback) {

		executeGet(uri, params, mContext,callback);
		
	}
	
	public static void getMessage(String uri, final StringEntity params, final Context mContext, final Handler handler) {
		
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		Map<String,Object> item = new HashMap<String, Object>();
		for(int i=0;i<8;i++) {
			item.put("hotPortal", R.drawable.portal);
			item.put("hotName", "布鲁斯" + i);
			data.add(item);
			item = new HashMap<String, Object>();
		}
		Message msg = handler.obtainMessage();
		msg.what = 2;
		msg.obj = data;
		handler.sendMessage(msg);
	
		
		
	}
}
