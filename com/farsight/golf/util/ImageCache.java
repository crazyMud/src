package com.farsight.golf.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

public class ImageCache {
	private LruCache<String, Bitmap> mCache;
	private LruCache<String, Drawable> imgCachMap;
	
	public ImageCache() {
		int maxSize = 4 * 1024 * 1024;
		mCache = new LruCache<String, Bitmap>(maxSize) {
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		imgCachMap = new LruCache<String, Drawable>(maxSize);
	}

	public Bitmap getBitmap(String url) {
		return mCache.get(url);
	}

	public void putBitmap(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}
	public Drawable getDrawable(String url) {
		return imgCachMap.get(url);
	}

	public void putDrawable(String url, Drawable drawable) {
		imgCachMap.put(url, drawable);
	}
}

