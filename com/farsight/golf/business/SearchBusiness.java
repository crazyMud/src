package com.farsight.golf.business;

import android.content.Context;
import android.os.Handler;
import com.farsight.golf.util.HttpClientAsync;
import com.loopj.android.http.RequestParams;

public class SearchBusiness extends HttpClientAsync {
	protected static final String TAG = "HotPointerBusiness";
	
	public static void getHotTeacher(String uri, final RequestParams params, final Context mContext, final Handler handler) {
		
		executeGet(uri, params, mContext, handler);
		
		
	}
	
	public static void getNearTeacher(String uri, final RequestParams params, final Context mContext, final Handler handler) {
		
		executeGet(uri, params, mContext, handler);
	
		
	}
	
	public static void searchVideo(String uri, final RequestParams params, final Context mContext, final Handler handler) {
		executeGet(uri, params, mContext, handler);
	}
	
	public static void searchUser(String uri, final RequestParams params, final Context mContext, final Handler handler) {
		executeGet(uri, params, mContext, handler);
	}
}
