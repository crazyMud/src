package com.farsight.golf.adapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.util.SyncImageLoader;


public class SimpleAdapterExt extends SimpleAdapter {
	
	Context context;
	List<? extends Map<String, ?>> data;
	int resource;
	private AsyncImageLoader imageLoader;
	private ListView mListView;
	private int extType = 0;
	public SimpleAdapterExt(Context context,List<? extends Map<String, ?>> data, 
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);


	}
	
	public SimpleAdapterExt(Context context,List<? extends Map<String, ?>> data, 
			int resource, String[] from, int[] to, int extType) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);
		this.extType = extType;

	}
	
	public SimpleAdapterExt(Context context,List<? extends Map<String, ?>> data, 
			int resource, String[] from, int[] to, ListView mListView) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.resource = resource;
		this.mListView = mListView;
		imageLoader = new AsyncImageLoader(context);


	}
	
	public SimpleAdapterExt(Context context,List<? extends Map<String, ?>> data, 
			int resource, String[] from, int[] to, ListView mListView, int extType) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.resource = resource;
		this.mListView = mListView;
		imageLoader = new AsyncImageLoader(context);
		this.extType = extType;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Map<String,Object> item = (Map<String, Object>) data.get(position);
		if(convertView == null) {
			 holder = new ViewHolder();
			 convertView =  LayoutInflater.from(context).inflate(resource, null);
			 holder.portalImg = (ImageView) convertView.findViewById(R.id.hot_video);
			 convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			
		}
		
		final String imgPortalUrl = (String) item.get("cate_img");
		holder.imgPortalUrl = imgPortalUrl;
		holder.portalImg.setTag(imgPortalUrl);
		holder.portalImg.setOnClickListener((OnClickListener) context);
		
		if (imgPortalUrl != null && !imgPortalUrl.equals("")) {
			Bitmap bitmap = imageLoader.loadImage(holder.portalImg, imgPortalUrl,
					new ImageDownloadedCallBack() {

						@Override
						public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
	
							if (imageView.getTag() != null && imageView.getTag().toString().equalsIgnoreCase(imgPortalUrl)) {
								imageView.setImageBitmap(bitmap);
							}
						}
					});

			if (bitmap != null) {
				holder.portalImg.setImageBitmap(bitmap);
			}
		}

		return convertView;
		//return super.getView(position, convertView, parent);

	}
	private class ViewHolder {
           TextView name;
           ImageView portalImg;
           ImageView VideoImg;
           String imgPortalUrl;
           String imgVideoUrl;
           String videoUrl;
   }
	
}
