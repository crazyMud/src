package com.farsight.golf.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.farsight.golf.business.Configuration;
import com.farsight.golf.main.MainApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;



public class HttpClientAsync {

	protected static PersistentCookieStore cookieStore = MainApplication.getCookieStore();//new PersistentCookieStore(MainApplication.getInstance().getApplicationContext());
	private static String TAG = "HttpClientAsync";
	protected static String url = new String("http://api.lexianglai.com/app/api/");
	protected static AsyncHttpClient client =  getHttpClient();
	protected static Gson gson =  new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING)
			 .enableComplexMapKeySerialization()
			.create();
	static String privateKey = "yj1ad3@9^U1_3d9f";
	protected static List<Header> headers = getRequestHeaders();
	protected static Header[] header = headers.toArray(new Header[headers.size()]);
	protected static String contentType = "application/json";
	public static AsyncHttpClient getHttpClient() {
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.setMaxRetriesAndTimeout(3,4000);
		return client;

		
	}
	public static void getFileByUrl(String url,final File file, final Handler handler) {
		client.setTimeout(30000);
		client.get(url, new FileAsyncHttpResponseHandler(file,true) {

			@Override
			public void onFailure(int stateCode, Header[] header, Throwable throwable,File file) {
				// TODO Auto-generated method stub
				Log.e("HttpClientAsync", throwable.getMessage());
			}

			@Override
			public void onSuccess(int stateCode, Header[] header, File file) {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.arg1 = 1;
				handler.sendMessage(msg);
			}
			
		});
	}

    /**
 * @param url
 *            要下载的文件URL
 * @throws Exception
 */
 public static void downloadFile(String url,final File file, final String[] allowedContentTypes, final Context mContext) throws Exception {

	// 获取二进制数据如图片和其他文件
	client.get(url, new BinaryHttpResponseHandler(allowedContentTypes) {

		@Override
		public void onSuccess(int statusCode, Header[] headers,	byte[] binaryData) {
			
			// 下载成功后需要做的工作
			//progress.setProgress(0);
			//
			Log.e("binaryData:", "共下载了：" + binaryData.length);
			//
			Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);

			//File file = new File(tempPath);
			// 压缩格式
			CompressFormat format = Bitmap.CompressFormat.JPEG;
			// 压缩比例
			int quality = 100;
			try {
				// 若存在则删除
				if (file.exists())
					file.delete();
				// 创建文件
				file.createNewFile();
				//
				OutputStream stream = new FileOutputStream(file);
				if(stream != null) {
					bmp.compress(format, quality, stream);// 压缩输出
					stream.close();// 关闭
					
				}
				//Toast.makeText(mContext, "下载成功\n" + file.getPath(),	Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				byte[] binaryData, Throwable error) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, "下载失败", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProgress(int bytesWritten, int totalSize) {
			// TODO Auto-generated method stub
			super.onProgress(bytesWritten, totalSize);
			int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
			// 下载进度显示
			//progress.setProgress(count);
			Log.e("下载 Progress>>>>>", bytesWritten + " / " + totalSize);

		}

		@Override
		public void onRetry(int retryNo) {
			// TODO Auto-generated method stub
			super.onRetry(retryNo);
			// 返回重试次数
		}

	});
}	
 /**
* @param path
*            要上传的文件路径
* @param url
*            服务端接收URL
* @throws Exception
*/
public static void uploadFile(String path, String uri, final Context mContext, final Handler handler) throws Exception {
	uri = url + uri;
	//uri = "http://api.lexianglai.com/upload.php";
	if(path == null) {
		handler.sendEmptyMessage(0);
	} else {
		final File file = new File(path);
	/*	upLoadByHttpClient4(file,uri,mContext,handler);
	}*/

	if (file.exists() && file.length() > 0) {
		List<Header> headers = getRequestHeaders();
		String BOUNDARY = UUID.randomUUID().toString(); 
		headers.add(new BasicHeader("HTTP_ACCEPT","*.*"));
		headers.add(new BasicHeader("HTTP_EXPECT","100-continue"));
		headers.add(new BasicHeader("Content-Type","multipart/form-data"));
		AsyncHttpClient client = new AsyncHttpClient();
		
		RequestParams requestParams = new RequestParams();
		
		requestParams.put("Content-Type","multipart/form-data");
		
		byte[] myByteArray = FileTools.File2byte(file);
		ByteArrayInputStream bstream = new ByteArrayInputStream(myByteArray);
		
		InputStream is = new FileInputStream(file);
		InputStreamBody isb = new InputStreamBody(is,file.getName());  
		
		requestParams.put("uploadFile", new RequestParams.FileWrapper(file, "application/octet-stream", file.getName()));
		requestParams.put("uploadFile", new RequestParams.StreamWrapper(is, file.getName(),"application/octet-stream",true));
		SimpleMultipartEntity sim = new SimpleMultipartEntity();
		sim.addPart("uploadFile", file.getName(), is);
		
		MultipartEntity multipartEntity = new MultipartEntity();  
		// multipartEntity.addPart("uploadFile", isb);  
		   //  multipartEntity.addPart("desc", new StringBody("this is description.")); 
		   //  ContentBody contentBody = new FileBody(file);  
		   //  FormBodyPart formBodyPart = new FormBodyPart("uploadFile", contentBody);  
	//  multipartEntity.addPart(formBodyPart);  
		     FileBody fileBody = new FileBody(file,"application/octet-stream"); 
		     FormBodyPart fbp= new FormBodyPart("uploadFile", fileBody);  
		    
		      multipartEntity.addPart(fbp);  
		      
		    
		      requestParams.put("uploadFile", bstream, file.getName(),"multipart/form-data" );
	

		      MimeTypeMap map = MimeTypeMap.getSingleton();
		   
		      String mime_type = map.getMimeTypeFromExtension("png");

		      MultipartEntity form = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		      form.addPart("uploadFile", new FileBody(file, mime_type, "UTF-8"));

		
		client.post(mContext, uri, headers.toArray(new Header[headers.size()]), sim, "multipart/form-data; boundary=----------------------------90ea168ad37b" , new AsyncHttpResponseHandler() {
		//client.post(mContext, uri, headers.toArray(new Header[headers.size()]), form, "multipart/form-data; boundary=----------------------------90ea168ad37b", new AsyncHttpResponseHandler() {
			
		
			@Override
			public void onSuccess(int statusCode, Header[] headers,	byte[] responseBody) {
				// 上传成功后要做的工作
				Log.v(TAG, new String(responseBody));
				Toast.makeText(mContext, "上传成功", Toast.LENGTH_LONG).show();
				File portalFile = new File(Configuration.PORTRAIT_PATH,"portal.png");
				file.renameTo(portalFile);
				handler.sendEmptyMessage(0);
				//progress.setProgress(0);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// 上传失败后要做到工作
				Log.e(TAG,new String(responseBody));
				Toast.makeText(mContext, "上传失败", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				super.onProgress(bytesWritten, totalSize);
				int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				// 上传进度显示
				//progress.setProgress(count);
				Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);
			}

			@Override
			public void onRetry(int retryNo) {
				// TODO Auto-generated method stub
				super.onRetry(retryNo);
				// 返回重试次数
			}

		});
	} else {
		Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();
	}
	}
}



/**
 * upLoadByAsyncHttpClient:由HttpClient4上传
 * 
 * @return void
 * @throws IOException
 * @throws ClientProtocolException
 * @throws
 * @since CodingExample　Ver 1.1
 */
public static void upLoadByHttpClient4(final File file, final String uri, final Context mContext, final Handler handler) throws ClientProtocolException, IOException {
	
	final String finalUri = url + uri;
	if(file == null) {
		handler.sendEmptyMessage(0);
		return;
	}
		
	Runnable runnable = new Runnable() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			Looper.getMainLooper().prepare();
			if (file.exists() && file.length() > 0) {
				List<Header> headers = getRequestHeaders();
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				HttpPost httppost = new HttpPost(finalUri);
				httppost.setHeaders(headers.toArray(new Header[headers.size()]));
				try {
					MultipartEntity entity = new MultipartEntity();
					
					FileBody fileBody = new FileBody(file);
					entity.addPart("uploadFile", fileBody);
					entity.addPart("test", new StringBody("测试内容"));
					httppost.setEntity(entity);
					
					HttpResponse response;
					response = httpclient.execute(httppost);
					HttpEntity resEntity = response.getEntity();
					if (response.getStatusLine().getStatusCode() == 201) { 
						if (resEntity != null) {
							//Toast.makeText(mContext, "文件上传成功", Toast.LENGTH_LONG).show();
							String result = EntityUtils.toString(resEntity);
							Log.i(TAG, result);
							JSONTokener jsonParser = new JSONTokener(result);  
							JSONObject jsonResult = (JSONObject) jsonParser.nextValue();  
							resEntity.consumeContent();
							Message msg = handler.obtainMessage();
							msg.what = 0;
							msg.obj = jsonResult.get("file_url");
							handler.sendMessage(msg);
						}
						
					} else {
						handler.sendEmptyMessage(1);
					}
					httpclient.getConnectionManager().shutdown();
				} catch (Exception e) {
					Log.e(TAG,e.getMessage());
				}
				
			}
			Looper.getMainLooper().loop();
		}
	};
	new Thread(runnable).start();
	
}

/**
 * upLoadByAsyncHttpClient:由HttpClient4上传
 * 
 * @return void
 * @throws IOException
 * @throws ClientProtocolException
 * @throws
 * @since CodingExample　Ver 1.1
 */
public static void upLoadByHttpClient4(final File[] files, final String uri, final Context mContext, final Handler handler) throws ClientProtocolException, IOException {
	
	final String finalUri = url + uri;
	if(files == null || files.length == 0) {
		handler.sendEmptyMessage(0);
		return;
	}
		
	Runnable runnable = new Runnable() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			Looper.getMainLooper().prepare();
				
					List<Header> headers = getRequestHeaders();
					//headers.add(new BasicHeader("Content-Type", "multipart/form-data"));
					HttpClient httpclient = new DefaultHttpClient();
					httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
					HttpPost httppost = new HttpPost(finalUri);
					httppost.setHeaders(headers.toArray(new Header[headers.size()]));
					try {
						MultipartEntity entity = new MultipartEntity();
						for(int i=0;i<files.length;i++) {
							if(files[i] == null || !files[i].exists()) continue;
							FileBody fileBody = new FileBody(files[i]);
							entity.addPart("uploadFile" + (i+1), fileBody);
						}
						httppost.setEntity(entity);
						
						HttpResponse response;
						response = httpclient.execute(httppost);
						HttpEntity resEntity = response.getEntity();
						if (response.getStatusLine().getStatusCode() == 201) { 
							if (resEntity != null) {
								//Toast.makeText(mContext, "文件上传成功", Toast.LENGTH_LONG).show();
								String result = EntityUtils.toString(resEntity);
								Log.i(TAG, result);
								JSONTokener jsonParser = new JSONTokener(result);  
								JSONObject jsonResult = (JSONObject) jsonParser.nextValue();  
								resEntity.consumeContent();
								Message msg = handler.obtainMessage();
								msg.what = 100;
								msg.obj = jsonResult;
								handler.sendMessage(msg);
							}
							
						} else {
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = response;
							handler.sendMessage(msg);
						}
						httpclient.getConnectionManager().shutdown();
					} catch (Exception e) {
						Log.e(TAG,e.getMessage());
					}
					
				
				
			Looper.getMainLooper().loop();
		}
	};
	new Thread(runnable).start();
	
}
/*
 * get http headers
 * 
 * 
 */
protected static List<Header> getRequestHeaders() {
	client.setCookieStore(cookieStore);
	List<Header> listHeader = new ArrayList<Header>();
	String timestamp =  System.currentTimeMillis()/1000 + "";
	listHeader.add(new BasicHeader("X-Yj-Created",timestamp));//
	listHeader.add(new BasicHeader("X-Yj-Sign", md5(privateKey + timestamp)));//
	listHeader.add(new BasicHeader("charset", HTTP.UTF_8));
//	listHeader.add(new BasicHeader("Content-Type", "application/json;charset=utf-8"));//
	
	if(cookieStore.getCookies().size()>0) {
		Cookie cookie = cookieStore.getCookies().get(0);
		listHeader.add(new BasicHeader("X-Yj-Session",cookie.getValue()));
		
	}
	return listHeader;
}

private static String md5(String string) {

    byte[] hash;

    try {

        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

    } catch (NoSuchAlgorithmException e) {

        throw new RuntimeException("Huh, MD5 should be supported?", e);

    } catch (UnsupportedEncodingException e) {

        throw new RuntimeException("Huh, UTF-8 should be supported?", e);

    }

    StringBuilder hex = new StringBuilder(hash.length * 2);

    for (byte b : hash) {

        if ((b & 0xFF) < 0x10) hex.append("0");

        hex.append(Integer.toHexString(b & 0xFF));

    }

    return hex.toString();

}

protected static void executePost(String uri, final StringEntity entity, final Context mContext, final Callback callback) {
	
	AsyncHttpClient client = getHttpClient();
	uri = url + uri;
	entity.setContentEncoding("utf-8");
	entity.setContentType(contentType);
	List<Header> headers = getRequestHeaders();
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().prepare();
	
	client.post(mContext, uri, headers.toArray(new Header[headers.size()]), entity, contentType, new JsonHttpResponseHandler() {

		@Override
		public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			if(throwable instanceof SocketTimeoutException ) {
				Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				returnCode = 904;
				errorInfo = "网络未连接";
			} else
				Log.e(TAG,responseString.toString());
			callback.onCallBack(returnCode, errorInfo);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			try {
				if(throwable instanceof SocketTimeoutException )
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				else {
					if(errorResponse==null) {
						errorInfo = "服务器故障，请稍后再试！";
						errorContent = throwable.getMessage();
						returnCode = 902;
					} else {
						errorInfo = errorResponse.get("error").toString();
						errorContent = statusCode + ":" + errorInfo;
						returnCode = 904;
						
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
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().loop();
}

protected static void executePost(String uri, final StringEntity entity, final Context mContext, final Handler handler) {
	
	AsyncHttpClient client = getHttpClient();
	uri = url + uri;
	entity.setContentEncoding("utf-8");
	entity.setContentType(contentType);
	List<Header> headers = getRequestHeaders();
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().prepare();

	client.post(mContext, uri, headers.toArray(new Header[headers.size()]), entity, contentType, new JsonHttpResponseHandler() {

		@Override
		public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			if(throwable instanceof SocketTimeoutException ) {
				Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				returnCode = 904;
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
			int returnCode = 901;
			try {
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					errorInfo = "网络未连接";
					returnCode = 904;
				} else {
					if(errorResponse==null) {
						errorInfo = "服务器故障，请稍后再试！";
						errorContent = throwable.getMessage();
						returnCode = 902;
					} else {
						errorInfo = errorResponse.get("error").toString();
						errorContent = statusCode + ":" + errorInfo;
						returnCode = 904;
						
					}
					Log.e(TAG,errorContent);
				}
				Message msg = handler.obtainMessage();
				msg.what = returnCode;
				msg.obj = errorInfo;
				handler.sendMessage(msg);
				
			} catch (JSONException e) {
				Message msg = handler.obtainMessage();
				msg.what = 903;
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
				msg.what = 903;
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
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().loop();
}

protected static void executePost(String uri, final StringEntity entity, final Context mContext, final Handler handler, final Message message) {
	
	AsyncHttpClient client = getHttpClient();
	uri = url + uri;
	entity.setContentEncoding("utf-8");
	entity.setContentType(contentType);
	List<Header> headers = getRequestHeaders();
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().prepare();

	client.post(mContext, uri, headers.toArray(new Header[headers.size()]), entity, contentType, new JsonHttpResponseHandler() {

		@Override
		public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			if(throwable instanceof SocketTimeoutException ) {
				Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				returnCode = 904;
				errorInfo = "网络未连接";
			} else
				Log.e(TAG,responseString.toString());
			Message msg = message;
			msg.what = returnCode;
			msg.obj = errorInfo;
			handler.sendMessage(msg);
			
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			try {
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					errorInfo = "网络未连接";
					returnCode = 904;
				} else {
					if(errorResponse==null) {
						errorInfo = "服务器故障，请稍后再试！";
						errorContent = throwable.getMessage();
						returnCode = 902;
					} else {
						errorInfo = errorResponse.get("error").toString();
						errorContent = statusCode + ":" + errorInfo;
						returnCode = 904;
						
					}
					Log.e(TAG,errorContent);
				}
				Message msg = message;
				msg.what = returnCode;
				msg.obj = errorInfo;
				handler.sendMessage(msg);
				
			} catch (JSONException e) {
				Message msg = handler.obtainMessage();
				msg.what = 903;
				msg.obj = e.getMessage();
				handler.sendMessage(msg);
				
			}
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,	JSONObject response) {

			try {
				Log.v(TAG,response.toString());
				Message msg = message;
				msg.what = 0;
				msg.obj = response;
				handler.sendMessage(msg);
				
			}catch(Exception e) {
				Message msg = message;
				msg.what = 903;
				msg.obj = e.getMessage();
				handler.sendMessage(msg);
			}
		
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,String responseString) {
			Log.v(TAG,responseString);
			Message msg = message;
			msg.what = Integer.valueOf(message.what) == 0?0:message.what;
			msg.obj = responseString;
			handler.sendMessage(msg);
			
		}
		
		
	});
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().loop();
}

public static void executeGet(String uri, final RequestParams params, final Context mContext, final Callback callback) {
	
		AsyncHttpClient client = getHttpClient();
		uri = url + uri;
		List<Header> headers = getRequestHeaders();
		
		client.get(mContext, uri,  headers.toArray(new Header[headers.size()]),params ,new JsonHttpResponseHandler() {
			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 904;
					errorInfo = "网络未连接";
				} else
					Log.e(TAG,responseString);
				callback.onCallBack(returnCode,errorInfo);
			}

			

			@Override
			public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse) {
				String errorInfo = "",errorContent = "";
				int returnCode = 901;
				try {
					if(throwable instanceof SocketTimeoutException ) {
						Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
						returnCode = 904;
						errorInfo = "网络未连接";
					} else {
						if(errorResponse == null) {
							errorInfo = "服务器故障，请稍后再试！";
							errorContent = throwable.getMessage();
							returnCode = 902;
						} else {
							errorInfo = errorResponse.get("error").toString();
							errorContent = statusCode + ":" + errorInfo;
							returnCode = 903;
						}
						Log.e(TAG,errorContent);
						
					}
					callback.onCallBack(returnCode, errorInfo);
					
				} catch (JSONException e) {
					callback.onCallBack(903,e.getMessage());
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				try {
					Log.v(TAG,response.toString());
					callback.onCallBack(0, response);
					
				}catch(Exception e) {
					callback.onCallBack(903, e.getMessage());
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				
				try {
					Log.v(TAG,response.toString());
					callback.onCallBack(0, response);
					
				}catch(Exception e) {
					callback.onCallBack(903, e.getMessage());
				}
				
			}
			
		});
		
	
}
protected static void executeGet(String uri, final RequestParams params, final Context mContext, final Handler handler, final Message message) {
	
	AsyncHttpClient client = getHttpClient();
	uri = url + uri;
	List<Header> headers = getRequestHeaders();
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().prepare();
	client.get(mContext, uri,  headers.toArray(new Header[headers.size()]),params ,new JsonHttpResponseHandler() {
		String errorInfo = "",errorContent = "";
		int returnCode = 901;
		@Override
		public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
			if(throwable instanceof SocketTimeoutException ) {
				Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				returnCode = 904;
				errorInfo = "网络未连接";
			} else
				Log.e(TAG,responseString);
			//Message message = handler.obtainMessage();
			message.what = returnCode;
			message.obj = errorInfo;
			handler.sendMessage(message);
		}

		

		@Override
		public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse) {
			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			try {
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 904;
					errorInfo = "网络未连接";
				} else {
					if(errorResponse == null) {
						errorInfo = "服务器故障，请稍后再试！";
						errorContent = throwable.getMessage();
						returnCode = 902;
					} else {
						errorInfo = errorResponse.get("error").toString();
						errorContent = statusCode + ":" + errorInfo;
						returnCode = 903;
					}
					Log.e(TAG,errorContent);
					
				}
				//Message msg = handler.obtainMessage();
				message.what = returnCode;
				message.obj = errorInfo;
				handler.sendMessage(message);
				
			} catch (JSONException e) {
				//Message msg = handler.obtainMessage();
				message.what = 903;
				message.obj = e.getMessage();
				handler.sendMessage(message);
				
			}
		}
		
		
		
		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONArray errorResponse) {
			Log.v(TAG,errorResponse.toString());
			//Message msg = handler.obtainMessage();
			message.what = 903;
			message.obj = errorResponse;
			handler.sendMessage(message);
		}



		@Override
		public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
			Log.v(TAG,response.toString());
			//Message msg = handler.obtainMessage();
			message.what = Integer.valueOf(message.what) == 0?0:message.what;
			message.obj = response;
			handler.sendMessage(message);
		}



		@Override
		public void onSuccess(int statusCode, Header[] headers,	JSONObject response) {

			try {
				Log.v(TAG,response.toString());
				//Message msg = handler.obtainMessage();
				message.what = 0;
				message.obj = response;
				handler.sendMessage(message);
				
			}catch(Exception e) {
				//Message msg = handler.obtainMessage();
				message.what = 903;
				message.obj = e.getMessage();
				handler.sendMessage(message);
			}
		
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,String responseString) {
			Log.v(TAG,responseString);
			//Message msg = handler.obtainMessage();
			message.what = 0;
			message.obj = responseString;
			handler.sendMessage(message);
			
		}
		
	});
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().loop();
}

protected static void executeGet(String uri, final RequestParams params, final Context mContext, final Handler handler) {
	
	AsyncHttpClient client = getHttpClient();
	uri = url + uri;
	List<Header> headers = getRequestHeaders();
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().prepare();
	client.get(mContext, uri,  headers.toArray(new Header[headers.size()]),params ,new JsonHttpResponseHandler() {
		String errorInfo = "",errorContent = "";
		int returnCode = 901;
		@Override
		public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
			if(throwable instanceof SocketTimeoutException ) {
				Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
				returnCode = 904;
				errorInfo = "网络未连接";
			} else
				Log.e(TAG,responseString);
			Message message = handler.obtainMessage();
			message.what = returnCode;
			message.obj = errorInfo;
			handler.sendMessage(message);
		}

		

		@Override
		public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse) {
			String errorInfo = "",errorContent = "";
			int returnCode = 901;
			try {
				if(throwable instanceof SocketTimeoutException ) {
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
					returnCode = 904;
					errorInfo = "网络未连接";
				} else {
					if(errorResponse == null) {
						errorInfo = "服务器故障，请稍后再试！";
						errorContent = throwable.getMessage();
						returnCode = 902;
					} else {
						errorInfo = errorResponse.get("error").toString();
						errorContent = statusCode + ":" + errorInfo;
						returnCode = 903;
					}
					Log.e(TAG,errorContent);
					
				}
				Message message = handler.obtainMessage();
				message.what = returnCode;
				message.obj = errorInfo;
				handler.sendMessage(message);
				
			} catch (JSONException e) {
				Message message = handler.obtainMessage();
				message.what = 903;
				message.obj = e.getMessage();
				handler.sendMessage(message);
				
			}
		}
		
		
		
		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONArray errorResponse) {
			Log.v(TAG,errorResponse.toString());
			Message message = handler.obtainMessage();
			message.what = 903;
			message.obj = errorResponse;
			handler.sendMessage(message);
		}



		@Override
		public void onSuccess(int statusCode, Header[] headers,	JSONArray response) {
			Log.v(TAG,response.toString());
			Message message = handler.obtainMessage();
			message.what = Integer.valueOf(message.what) == 0?0:message.what;
			message.obj = response;
			handler.sendMessage(message);
		}



		@Override
		public void onSuccess(int statusCode, Header[] headers,	JSONObject response) {

			try {
				Log.v(TAG,response.toString());
				Message message = handler.obtainMessage();
				message.what = 0;
				message.obj = response;
				handler.sendMessage(message);
				
			}catch(Exception e) {
				Message message = handler.obtainMessage();
				message.what = 903;
				message.obj = e.getMessage();
				handler.sendMessage(message);
			}
		
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,String responseString) {
			Log.v(TAG,responseString);
			Message message = handler.obtainMessage();
			message.what = 0;
			message.obj = responseString;
			handler.sendMessage(message);
			
		}
		
	});
	if(Looper.myLooper() != Looper.getMainLooper())
		Looper.getMainLooper().loop();
	}

	public static void executeDelete(String uri, final RequestParams params, final Context mContext, final Handler handler) {
		final Message msg = handler.obtainMessage();
		AsyncHttpClient client = getHttpClient();
		List<Header> headers = getRequestHeaders();
		uri = url + uri;
		client.delete(mContext, uri, headers.toArray(new Header[headers.size()]), params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int stateCode, Header[] header, byte[] responseByte) {
				
				msg.what = 0;
				msg.obj = new String(responseByte);
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
	}
 
}
