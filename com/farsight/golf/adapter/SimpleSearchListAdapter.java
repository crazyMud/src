package com.farsight.golf.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.ui.user.UserPageActivity;
import com.google.gson.Gson;
import com.umeng.socialize.utils.Log;

public class SimpleSearchListAdapter extends BaseAdapter {
	static String TAG = "SimpleSearchListAdapter";
	Context context;
	JSONArray data;
	int resource;
	AsyncImageLoader imageLoader;
	
	public SimpleSearchListAdapter(Context context,JSONArray data,
			int resource) {
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return data.get(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		JSONObject item = new JSONObject();
		try {
			item = (JSONObject) data.get(position);
			if(convertView == null) {
				holder = new ViewHolder();
				convertView =  LayoutInflater.from(context).inflate(resource, null);
				holder.name = (TextView) convertView.findViewById(R.id.hot_name);
				holder.videoCount = (TextView) convertView.findViewById(R.id.hot_video_count);
				holder.location = (TextView) convertView.findViewById(R.id.hot_location);
				holder.likes = (TextView) convertView.findViewById(R.id.hot_likes);
				holder.authorImg = (ImageView) convertView.findViewById(R.id.hot_portal_iden);
				holder.portalImg = (ImageView) convertView.findViewById(R.id.hot_portal);
				convertView.setOnClickListener(click);
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
				holder.name.setText(item.get("nickname").toString());
				holder.location.setText(item.get("area").toString());
				holder.videoCount.setText(String.format("%s个视频", item.get("videos").toString()));
				holder.likes.setText(String.format("%s个赞", item.get("likes").toString()));
				final String portalUrl = item.get("user_img").toString();
				
				holder.portalImg.setTag(item);
				holder.portalImg.setOnClickListener(click);
				holder.authorImg.setVisibility(item.get("level").toString().equalsIgnoreCase("0")?View.GONE:View.VISIBLE);
				if(!TextUtils.isEmpty(portalUrl)) {
					Bitmap bitmap = imageLoader.loadImage(holder.portalImg, portalUrl, 
							new ImageDownloadedCallBack() {
								
								@Override
								public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
									try {
										if (imageView.getTag() != null && ((JSONObject)imageView.getTag()).
												get("user_img").toString().equalsIgnoreCase(portalUrl)) {
											imageView.setImageBitmap(bitmap);
										}
									}catch(Exception e) {
										Log.e(TAG, e.getLocalizedMessage());
									}
									
								}
							});
					if(bitmap != null)
						holder.portalImg.setImageBitmap(bitmap);
				}
				
			
		}catch(Exception e) {
			Log.e(TAG, e.toString());
		}
		return convertView;
	}
	OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View view) {
			final JSONObject clickItem;
			Intent intent;
			switch(view.getId()) {
				case R.id.hot_portal:
					clickItem = (JSONObject) view.getTag();
					intent = new Intent();
					intent.putExtra("user", clickItem.toString());
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(context, UserPageActivity.class);
					((Activity)context).startActivity(intent);
					break;
				default:
					view.findViewById(R.id.hot_portal).performClick();
					break;
			}
			
		}
		
	};
	
	private class ViewHolder {
		  TextView name;
          TextView videoCount;
          TextView location;
          TextView likes;
          ImageView portalImg;
          ImageView authorImg;
         
	}
}
