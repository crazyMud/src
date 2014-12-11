package com.farsight.golf.adapter;


import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.ui.home.VideoTypeActivity;
import com.google.gson.Gson;



public class SimpleTypeListAdapter extends BaseAdapter {
	static String TAG = "SimpleTypeListAdapter";
	Context context;
	List<? extends Map<String, ?>> data;
	int resource;
	private AsyncImageLoader imageLoader;
	private ListView mListView;
	private String imgUrlStuff = "";
	public SimpleTypeListAdapter(Context context,List<? extends Map<String, ?>> data, 
			int resource) {
		super();
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);


	}
	public SimpleTypeListAdapter(Context context,List<? extends Map<String, ?>> data, 
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
		try {
			Map<String,Object> item = (Map<String, Object>) data.get(position);
			if(convertView == null) {
				 holder = new ViewHolder();
				 convertView =  LayoutInflater.from(context).inflate(resource, null);
				 holder.portalImg = (ImageView) convertView.findViewById(R.id.hot_video);
				 holder.name = (TextView) convertView.findViewById(R.id.hot_describe);
				 holder.cateNum = (TextView) convertView.findViewById(R.id.hot_comm);
				 convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				
			}
			
			final String imgPortalUrl = imgUrlStuff + (String) item.get("cate_img");
			holder.imgPortalUrl = imgPortalUrl;
			holder.name.setText(item.get("title").toString());
			holder.cateNum.setText(item.get("cate_num").toString() + "个视频");
			holder.portalImg.setTag(item);
			holder.portalImg.setOnClickListener(click);
			
			if (imgPortalUrl != null && !imgPortalUrl.equals("")) {
				Bitmap bitmap = imageLoader.loadImage(holder.portalImg, imgPortalUrl,
						new ImageDownloadedCallBack() {
	
							@Override
							public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
		
								if (imageView.getTag() != null && ((Map)imageView.getTag()).get("cate_img").toString().equalsIgnoreCase(imgPortalUrl)) {
									imageView.setImageBitmap(bitmap);
								}
							}
						});
	
				if (bitmap != null) {
					holder.portalImg.setImageBitmap(bitmap);
				}
			}
			return convertView;
		}catch(Exception e) {
			Log.e(TAG, e.toString());
		}
		return convertView;
		

	}
	private class ViewHolder {
           TextView name;
           TextView cateNum;
           ImageView portalImg;
           ImageView VideoImg;
           String imgPortalUrl;
           String imgVideoUrl;
           String videoUrl;
   }
	OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent;
			final Map<String,Object> clickItem;
			try {
				clickItem = (Map<String,Object>)view.getTag();
				switch(view.getId()) {
				case R.id.hot_video:
					intent = new Intent(context, VideoTypeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					((Activity)context).startActivity(intent.putExtra("cate",  new Gson().toJson(clickItem)));
					
					break;
				}
			}catch(Exception e) {
				
			}
		}
	};
	
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
