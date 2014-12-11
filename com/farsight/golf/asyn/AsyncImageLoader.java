package com.farsight.golf.asyn;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.farsight.golf.util.FileUtil;
import com.farsight.golf.util.ImageCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片异步加载类
 * 
 * @author Leslie.Fang
 * 
 */
public class AsyncImageLoader {
	// 最大线程数
	private static final int MAX_THREAD_NUM = 10;
	// 一级内存缓存基于 LruCache
	private ImageCache bitmapCache;
	// 二级文件缓存
	private FileUtil fileUtil;
	// 线程池
	private ExecutorService threadPools = null;
	String TAG = "AsyncImageLoader";

	public AsyncImageLoader(Context context) {
		bitmapCache = new ImageCache();
		fileUtil = new FileUtil(context);
		threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM);
	}

	public Bitmap loadImage(final ImageView imageView, final String imageUrl,
			final ImageDownloadedCallBack imageDownloadedCallBack) {
		final String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		final String filepath = fileUtil.getAbsolutePath() + "/" + filename;

		// 先从内存中拿
		Bitmap bitmap = bitmapCache.getBitmap(imageUrl);

		if (bitmap != null) {
			//Log.d(TAG, "image exists in memory");
			return bitmap;
		}

		// 从文件中找
		if (fileUtil.isBitmapExists(filename)) {
			//Log.i(TAG, "image exists in file" + filename);
			bitmap = BitmapFactory.decodeFile(filepath);

			// 重新缓存到内存中
			bitmapCache.putBitmap(imageUrl, bitmap);
			return bitmap;
		}

		// 内存和文件中都没有再从网络下载
		
		if (imageUrl != null && !imageUrl.equals("")) {
			final Handler handler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 111 && imageDownloadedCallBack != null) {
						Bitmap bitmap = (Bitmap) msg.obj;
						imageDownloadedCallBack.onImageDownloaded(imageView,
								bitmap);
					}
				}
			};

			Thread thread = new Thread() {
				@Override
				public void run() {
					Log.d(TAG, Thread.currentThread().getName()	+ " is running");
					/*InputStream inputStream = HTTPService.getInstance()
							.getStream(imageUrl);*/
					
					Drawable drawable = loadImageFromUrl(imageUrl);//Drawable.createFromStream(inputStream, "src");
					
					BitmapDrawable bd = (BitmapDrawable) drawable;

					Bitmap bitmap = bd !=null? bd.getBitmap():null;//BitmapFactory.decodeStream(inputStream);

					// 图片下载成功后缓存并执行回调刷新界面
					if (bitmap != null) {
						// 先缓存到内存
						bitmapCache.putBitmap(imageUrl, bitmap);
						bitmapCache.putDrawable(imageUrl, drawable);
						// 缓存到文件系统
						fileUtil.saveBitmap(filepath, bitmap);

						Message msg = new Message();
						msg.what = 111;
						msg.obj = bitmap;
						handler.sendMessage(msg);
					}
				}
			};

			threadPools.execute(thread);
		}

		return null;
	}

	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(i, "src");
		return d;
	}	
	
	public void shutDownThreadPool() {
		if (threadPools != null) {
			threadPools.shutdown();
			threadPools = null;
		}
	}

	/**
	 * 图片下载完成回调接口
	 * 
	 */
	public interface ImageDownloadedCallBack {
		void onImageDownloaded(ImageView imageView, Bitmap bitmap);
	}
}

