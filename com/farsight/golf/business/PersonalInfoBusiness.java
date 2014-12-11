package com.farsight.golf.business;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.HttpClientAsync;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PersonalInfoBusiness extends HttpClientAsync {
	protected static final String TAG = "PersonalInfoBusiness";

	public static void savePersonalInfo(String uri, final StringEntity params,
			final Context mContext, final Callback callback) {
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		List<Header> headers = getRequestHeaders();
		client.setURLEncodingEnabled(true);
		client.put(mContext, uri, headers.toArray(new Header[headers.size()]),
				params, contentType, new JsonHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {

						String errorInfo = "", errorContent = "";
						int returnCode = 1;
						try {
							if (throwable instanceof SocketTimeoutException)
								Toast.makeText(mContext, "网络未连接",
										Toast.LENGTH_LONG).show();
							else {
								if (errorResponse == null) {
									errorInfo = "服务器故障，请稍后再试！";
									errorContent = throwable.getMessage();
									returnCode = 2;
								} else {
									errorInfo = errorResponse.get("error")
											.toString();
									errorContent = statusCode + ":" + errorInfo;
								}
								Log.e(TAG, errorContent);
							}
							callback.onCallBack(returnCode, errorInfo);

						} catch (JSONException e) {
							callback.onCallBack(3, e.getMessage());
						}

					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {

						Log.v(TAG, response.toString());
						callback.onCallBack(0, response);
					}

				});

	}

	
	public static void getVideoByType(String uri, final RequestParams params, final Context mContext,
			final Handler handler) {

		Message msg = handler.obtainMessage();
		executeGet(uri, params, mContext, handler, msg);

	}

	public static void getNewVideo(String uri, final RequestParams params,
			final Context mContext, final Handler handler, Integer what) {
		Message msg = handler.obtainMessage();
		msg.what = what;
		executeGet(uri, params, mContext, handler, msg);

	}
	
	public static void careUser(String uri, final StringEntity entity,
			final Context mContext, final Handler handler, Integer what) {
		final Message message = handler.obtainMessage();
		message.what = what;
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		entity.setContentEncoding("utf-8");
		entity.setContentType(contentType);
		List<Header> headers = getRequestHeaders();
		client.post(mContext, uri, headers.toArray(new Header[headers.size()]), entity, contentType, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] header, byte[] responseString,	Throwable throwable) {
				String errorInfo = "",errorContent = "";
				int returnCode = 1;
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 4;
					errorInfo = "网络未连接";
				} else {
					try {
						errorInfo = new String(responseString);
						JSONObject jsonError = new JSONObject(errorInfo);
						errorContent = statusCode + ":" + errorInfo;
						errorInfo = (String) jsonError.get("error");

						Log.e(TAG,errorContent);
						}catch(Exception e) {
							errorInfo = "JSON对象错误";
						}
					
				}
				Message msg = message;
				msg.what = returnCode;
				msg.obj = errorInfo;
				handler.sendMessage(msg);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseString) {
				Log.v(TAG,new String(responseString));
				Message msg = message;
				msg.what = Integer.valueOf(message.what) == 0?0:message.what;
				msg.obj = responseString;
				handler.sendMessage(msg);
				
			}

			
			
		});

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void executeGet(String uri, final RequestParams params,
			final Context mContext, final Handler handler) {

		Message msg = handler.obtainMessage();
		executeGet(uri, params, mContext, handler, msg);

	}
}
