package com.farsight.golf.business;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.farsight.golf.util.Callback;
import com.farsight.golf.util.HttpClientAsync;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HotPointerBusiness extends HttpClientAsync {
	protected static final String TAG = "HotPointerBusiness";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void getHotPointer(String uri, final RequestParams params, final Context mContext, 
			final Handler handler, final Integer direction) {
		
		Message msg = handler.obtainMessage();
		msg.arg1 = direction==null?0:direction;
		executeGet(uri, params, mContext, handler, msg);
		
		
	}
	
	public static void getNav(String uri, final Context mContext, final Callback callback) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		List<Header> headers = getRequestHeaders();
		client.get(mContext, uri, headers.toArray(new Header[headers.size()]), null, new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int statusCode, Header[] header, byte[] responseByte,	Throwable throwable) {
				String errorInfo = "",errorContent = "";
				int returnCode = 1;
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 4;
					errorInfo = "网络未连接";
				} else
					Log.e(TAG,throwable.getMessage());
				
				callback.onCallBack(statusCode, new String(responseByte));
			
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseByte) {
				Log.v(TAG,new String(responseByte));
				
				callback.onCallBack(statusCode, new String(responseByte));
				
			}
			
		});
	}
	

	public static void getNewsPointer(String uri, final RequestParams params, final Context mContext,
			final Handler handler, final Integer direction) {
		
		Message msg = handler.obtainMessage();
		msg.arg1 = direction==null?0:direction;
		executeGet(uri, params, mContext, handler, msg);
		
	
		
		
	}
	
	
	public static void getTypePointer(String uri, final RequestParams params, final Context mContext, 
			final Handler handler, final Integer direction) {
		
		Message msg = handler.obtainMessage();
		msg.arg1 = direction==null?0:direction;
		executeGet(uri, params, mContext, handler, msg);
		
		
	}
	
	public static void like(String uri, final StringEntity entity, final Context mContext, final Handler handler) {
		Message msg = handler.obtainMessage();
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		entity.setContentEncoding("utf-8");
		entity.setContentType(contentType);
		List<Header> headers = getRequestHeaders();
		client.post(mContext, uri, headers.toArray(new Header[headers.size()]), entity, contentType, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] header, byte[] responseString,	Throwable throwable) {
				String errorInfo = "",errorContent = new String(responseString);
				int returnCode = 1;
				Message msg = handler.obtainMessage();
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 4;
					errorInfo = "网络未连接";
				} else
					Log.e(TAG,errorContent);
				try {
					JSONObject erObj = new JSONObject(errorContent);
					errorInfo = erObj.get("error").toString();
					msg.what = returnCode;
					msg.obj = errorInfo;
					handler.sendMessage(msg);
				}catch(Exception e) {
					
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseString) {
				Log.v(TAG,new String(responseString));
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = responseString;
				handler.sendMessage(msg);
				
			}

			
			
		});
		
	}
	
	public static void publish(String uri, final StringEntity entity, final Context mContext, final Callback callback) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		entity.setContentType(contentType);
		List<Header> headers = getRequestHeaders();
		client.put(mContext, uri, entity, contentType, new JsonHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				if(throwable instanceof SocketTimeoutException )
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				else
					Log.e(TAG,responseString);
				callback.onCallBack(0, throwable);
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
						}
						Log.e(TAG,errorContent);
					}
					callback.onCallBack(returnCode, errorInfo);
					
				} catch (JSONException e) {
					callback.onCallBack(3,e.getMessage());
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Log.v(TAG,response.toString());
				callback.onCallBack(0, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String responseString) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, responseString);
				Log.v(TAG,responseString);
				callback.onCallBack(0, responseString);
			}
			
		});
	}

	public static void delete(String uri, final RequestParams params, final Context mContext, 
			final Handler handler, final Integer direction) {
		final Message msg = handler.obtainMessage();
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		msg.arg1 = direction==null?0:direction;
		List<Header> headers = getRequestHeaders();
		client.delete(mContext, uri, headers.toArray(new Header[headers.size()]), params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int stateCode, Header[] header, byte[] responseByte) {
				
				msg.what = 0;
				handler.sendMessage(msg);
				
			}
			
			@Override
			public void onFailure(int stateCode, Header[] header, byte[] responseByte, Throwable throwable) {
				Log.d(TAG,new String(responseByte));
				msg.what = 900;
				msg.obj = new String(responseByte);
				handler.sendMessage(msg);
				
			}
		});
//		
		
	}
}
