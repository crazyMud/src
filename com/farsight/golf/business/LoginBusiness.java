package com.farsight.golf.business;



import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.farsight.golf.main.MainApplication;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.HttpClientAsync;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginBusiness extends HttpClientAsync {
	
	protected static final String TAG = "LoginBusiness";
	
	/*
	 * 发送手机验证码
	 */
	public static void getAuthCode(String uri, final StringEntity params, final Context mContext) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		params.setContentType(contentType);
		client.post(mContext, uri, header, params, contentType, new AsyncHttpResponseHandler (){
	
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] response,Throwable throwable) {
				Log.e(TAG,throwable.getMessage());
				
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				Log.v(TAG,statusCode + ":" + new String(response));
				
			}

				
		});
	
	}
	public static void getAuthCode(String uri, final StringEntity params, final Context mContext, final Callback callback) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		params.setContentType(contentType);
		client.post(mContext, uri, header, params, contentType, new AsyncHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] response,Throwable throwable) {
				String errorInfo = "",errorContent = "";
				int returnCode = 1;
				JSONObject jsonError;
				if(throwable instanceof SocketTimeoutException )
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				else {
					try {
					errorInfo = new String(response);
					jsonError = new JSONObject(errorInfo);
					errorContent = statusCode + ":" + errorInfo;
					errorInfo = (String) jsonError.get("error");
					Log.e(TAG,errorContent);
					}catch(Exception e) {
						errorInfo = "JSON对象错误";
					}
					callback.onCallBack(returnCode, errorInfo);
				}
				
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				callback.onCallBack(0, new String(response));
			}
			
		});
	}
	public static void resetPwd(String uri, final StringEntity params, final Context mContext, final Callback callback) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		params.setContentType(contentType);
		client.put(mContext, uri, params, contentType, new JsonHttpResponseHandler(){
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
	public static void logout(String uri, final StringEntity params, final Context mContext, final Callback callback) {
		
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		params.setContentType(contentType);
		List<Header> headers = getRequestHeaders();
		if(cookieStore != null && cookieStore.getCookies().size()>0) {
			cookieStore.clear();
			callback.onCallBack(0, "ok");
			
		}
		
	}
	/*
	 * @用户注册
	 */
	public static void register(String uri, final StringEntity params, final Context mContext, final Callback callback ) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		params.setContentEncoding("utf-8");
		params.setContentType(contentType);
		List<Header> headers = getRequestHeaders();

		client.post(mContext, uri, headers.toArray(new Header[headers.size()]), params, contentType, new JsonHttpResponseHandler() {

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
			public void onFailure(int statusCode, Header[] headers,	String responseString, Throwable error) {
				super.onFailure(statusCode, headers, responseString, error);
				if(error instanceof SocketTimeoutException )
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				else
					Log.e(TAG,error.toString());
				callback.onCallBack(0, error);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,	JSONObject response) {
				try {
					MainApplication.currentUser = response;
					BasicClientCookie cookie = new BasicClientCookie("session",response.getString("session"));
					cookie.setVersion(1);
					cookieStore.addCookie(cookie);
					Log.v(TAG, response.toString());
					callback.onCallBack(0, response);
				}catch(Exception e) {
					
				}
			}


			
		});
	}
	/*
	 * @catagator login
	 * 
	 */
	public static void login(String uri, final StringEntity params, final Context mContext, final Callback callback) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		params.setContentType(contentType);
		List<Header> headers = getRequestHeaders();
		client.setCookieStore(cookieStore);
		client.post(mContext, uri, headers.toArray(new Header[headers.size()]), params, contentType, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				
				if(throwable instanceof SocketTimeoutException )
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				else
					Log.e(TAG,responseString.toString());
				callback.onCallBack(1, throwable);
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
			public void onSuccess(int statusCode, Header[] headers,	JSONObject response) {

				try {
					Log.v(TAG,response.toString());
					MainApplication.currentUser = response;
					BasicClientCookie cookie = new BasicClientCookie("session",response.getString("session"));
					cookie.setVersion(1);
					cookieStore.addCookie(cookie);
					callback.onCallBack(0, response);
					
				}catch(Exception e) {
					callback.onCallBack(3, e.getMessage());
				}
			
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				Log.v(TAG,responseString);
				callback.onCallBack(0, responseString);
			}
			
			
		});
	}
	public static void login(String uri, final Context mContext, final Callback callback) {
		if(cookieStore != null && cookieStore.getCookies().size() > 0 ) {
			AsyncHttpClient client = getHttpClient();
			uri = url + uri;
			List<Header> headers = getRequestHeaders();
			client.get(mContext, uri,  headers.toArray(new Header[headers.size()]), new RequestParams(),new JsonHttpResponseHandler() {
				String errorInfo = "",errorContent = "";
				int returnCode = 1;
				@Override
				public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
					if(throwable instanceof SocketTimeoutException ) {
						Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
						returnCode = 4;
						errorInfo = "网络未连接";
					} else
						Log.e(TAG,responseString);
					callback.onCallBack(returnCode,errorInfo);
				}

				
	
				@Override
				public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse) {
					String errorInfo = "",errorContent = "";
					int returnCode = 1;
					try {
						if(throwable instanceof SocketTimeoutException ) {
							Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
							returnCode = 4;
							errorInfo = "网络未连接";
						} else {
							if(errorResponse == null) {
								errorInfo = "服务器故障，请稍后再试！";
								errorContent = throwable.getMessage();
								returnCode = 2;
							} else {
								errorInfo = errorResponse.get("error").toString();
								errorContent = statusCode + ":" + errorInfo;
								returnCode = 3;
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
					
					try {
						Log.v(TAG,response.toString());
						MainApplication.currentUser = response;
						BasicClientCookie cookie = new BasicClientCookie("session",response.getString("session"));
						cookie.setVersion(1);
						cookieStore.addCookie(cookie);
						callback.onCallBack(0, response);
						
					}catch(Exception e) {
						callback.onCallBack(3, e.getMessage());
					}
					
				}
				
			});
		} else {
			callback.onCallBack(1, null);
		
		}
	}
}
