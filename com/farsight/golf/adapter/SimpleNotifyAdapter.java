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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.util.DateDistance;
import com.farsight.golf.util.SyncImageLoader;


public class SimpleNotifyAdapter extends BaseAdapter {
	static String TAG = "SimpleNotifyAdapter";
	Context context;
	List<? extends Map<String, ?>> data;
	int resource;
	private AsyncImageLoader imageLoader;
	private ListView mListView;
	public SimpleNotifyAdapter(Context context,List<? extends Map<String, ?>> data, 
			int resource) {
		super();
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);


	}
	public SimpleNotifyAdapter(Context context,List<? extends Map<String, ?>> data, 
			int resource, ListView mListView) {
		super();
		this.context = context;
		this.data = data;
		this.resource = resource;
		this.mListView = mListView;
		imageLoader = new AsyncImageLoader(context);


	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Map<String,Object> item = (Map<String, Object>) data.get(position);
		if(convertView == null) {
			 holder = new ViewHolder();
			 convertView =  LayoutInflater.from(context).inflate(resource, null);
			 holder.portalImg = (ImageView) convertView.findViewById(R.id.hot_portal);
			 holder.notifyTv = (TextView) convertView.findViewById(R.id.notifiy_commom);
			 holder.times = (TextView) convertView.findViewById(R.id.notify_time);
			
			 convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			
		}
		
		holder.notifyTv.setText(item.get("content").toString());
		final String imgPortalUrl = (String) item.get("user_img");
		holder.imgPortalUrl = imgPortalUrl;
		holder.portalImg.setTag(imgPortalUrl);
		try {
			
			String times = item.get("created").toString();
			Double doTime = Double.parseDouble(times);
			long time = doTime.longValue();
			String stiff = DateDistance.getTimes(time);
			holder.times.setText(stiff);
			
		} catch(Exception e) {
			Log.d(TAG,e.toString());
		}
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


	}
	private class ViewHolder {
           ImageView portalImg;
           ImageView VideoImg;
           TextView notifyTv;
           TextView times;
           String imgPortalUrl;
           String imgVideoUrl;
           String videoUrl;
   }
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	} 
}
