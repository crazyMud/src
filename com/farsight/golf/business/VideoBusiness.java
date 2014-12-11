package com.farsight.golf.business;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.farsight.golf.util.HttpClientAsync;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

public class VideoBusiness extends HttpClientAsync {
	protected static final String TAG = "VideoBusiness";

	
	public static void publishVideo(String uri, final StringEntity entity, final Context mContext, final Handler handler) {
		
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		entity.setContentEncoding("utf-8");
		entity.setContentType(contentType);
		List<Header> headers = getRequestHeaders();

		client.post(mContext, uri, headers.toArray(new Header[headers.size()]), entity, contentType, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				String errorInfo = "",errorContent = "";
				int returnCode = 1;
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 4;
					errorInfo = "网络未连接";
				} else
					Log.e(TAG,responseString.toString());
				Message msg = handler.obtainMessage();
				msg.what = returnCode;
				msg.obj = errorInfo;
				handler.sendMessage(msg);
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

				String errorInfo = "",errorContent = "";
				int returnCode = 1;
				try {
					if(throwable instanceof SocketTimeoutException )
						Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					else {
						if(errorResponse==null) {
							errorInfo = "服务器故障，请稍后再试！";
							errorContent = throwable.getMessage();
							returnCode = 2;
						} else {
							errorInfo = errorResponse.get("error").toString();
							errorContent = statusCode + ":" + errorInfo;
							returnCode = 409;
							
						}
						Log.e(TAG,errorContent);
					}
					Message msg = handler.obtainMessage();
					msg.what = returnCode;
					msg.obj = errorInfo;
					handler.sendMessage(msg);
					
				} catch (JSONException e) {
					Message msg = handler.obtainMessage();
					msg.what = 3;
					msg.obj = e.getMessage();
					handler.sendMessage(msg);
					
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,	JSONObject response) {

				try {
					Log.v(TAG,response.toString());
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = response;
					handler.sendMessage(msg);
					
				}catch(Exception e) {
					Message msg = handler.obtainMessage();
					msg.what = 3;
					msg.obj = e.getMessage();
					handler.sendMessage(msg);
				}
			
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				Log.v(TAG,responseString);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = responseString;
				handler.sendMessage(msg);
				
			}
			
			
		});
				
		
	}
	public static void playTimes(String uri, final Context mContext, final Handler handler)  {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		List<Header> headers = getRequestHeaders();
		
		client.put(mContext, uri, headers.toArray(new Header[headers.size()]), null, "", new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int stateCode, Header[] headers, byte[] responseByte,
					Throwable throwable) {
				String errorInfo = "",errorContent = "";
				int returnCode = 1;
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 4;
					errorInfo = "网络未连接";
				} else
					Log.e(TAG,new String(responseByte));
				Message msg = handler.obtainMessage();
				msg.what = returnCode;
				msg.obj = errorInfo;
				handler.sendMessage(msg);
				
			}

			@Override
			public void onSuccess(int arstateCodeg, Header[] headers, byte[] responseByte) {
				Log.v(TAG,new String(responseByte));
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = responseByte;
				handler.sendMessage(msg);
				
			}
		});
	}
	
	public static void sendComments(String uri, final StringEntity entity, final Context mContext, final Handler handler) {
		executePost(uri, entity, mContext, handler);
	}
	
	public static void getComments(String uri, final RequestParams params, final Context mContext, final Handler handler) {
		executeGet(uri, params, mContext, handler);
	}
	
	public static void deleteComments(String uri, final Context mContext, final Handler handler) {
		executeDelete(uri, null, mContext, handler);
	}
}
