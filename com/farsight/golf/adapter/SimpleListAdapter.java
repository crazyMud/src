package com.farsight.golf.adapter;



import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.business.HotPointerBusiness;
import com.farsight.golf.business.VideoBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.LoginActivity;
import com.farsight.golf.ui.home.VideoDetailActivity;
import com.farsight.golf.ui.user.UserPageActivity;
import com.farsight.golf.ui.video.VideoPlayerActivity;
import com.farsight.golf.util.DateDistance;
import com.google.gson.Gson;
import com.yixia.camera.util.StringUtils;


public class SimpleListAdapter extends SimpleAdapter {
	static String TAG = "SimpleListAdapter";
	Context context;
	List<? extends Map<String, ?>> data;
	int resource;
	private AsyncImageLoader imageLoader;
	
	public SimpleListAdapter(Context context,List<? extends Map<String, ?>> data, 
			int resource) {
		super(context, data, resource, null, null);
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);


	}
	public SimpleListAdapter(Context context,List<? extends Map<String, ?>> data, 
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);


	}
	public SimpleListAdapter(Context context,List<? extends Map<String, ?>> data, 
			int resource, String[] from, int[] to, ListView mListView) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.resource = resource;
		imageLoader = new AsyncImageLoader(context);


	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Map<String,Object> item = (Map<String, Object>) data.get(position);
		try {
			if(convertView == null) {
				 holder = new ViewHolder();
				 convertView =  LayoutInflater.from(context).inflate(resource, null);
				 holder.portalImg = (ImageView) convertView.findViewById(R.id.hot_portal);
				 holder.VideoImg =  (ImageView) convertView.findViewById(R.id.hot_video);
				 holder.playImg = (ImageView) convertView.findViewById(R.id.hot_playBtn);
				 holder.authImg = (ImageView) convertView.findViewById(R.id.hot_portal_iden);
				 holder.commBtn = (Button) convertView.findViewById(R.id.hot_commend_btn);
				 holder.commLay = (LinearLayout) convertView.findViewById(R.id.hot_line_comm);
				 holder.dissLay = (LinearLayout) convertView.findViewById(R.id.hot_line_diss);
				 holder.shareLay = (LinearLayout) convertView.findViewById(R.id.hot_line_share);
				 holder.moreLay = (LinearLayout) convertView.findViewById(R.id.hot_line_more);
				 holder.times = (TextView) convertView.findViewById(R.id.hot_time);
				 holder.likes = (TextView) convertView.findViewById(R.id.hot_comm);
				 holder.name = (TextView)  convertView.findViewById(R.id.hot_name);
				 holder.plays = (TextView)  convertView.findViewById(R.id.hot_play);
				 holder.describ = (TextView)  convertView.findViewById(R.id.hot_describe);
				 holder.item = item;
				 
				 convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				
			}
			holder.name.setText(item.get("nickname").toString());
			holder.plays.setText(item.get("plays").toString());
			holder.describ.setText(item.get("post_content").toString());
			holder.likes.setText(item.get("likes") + "个赞");
			holder.authImg.setVisibility(item.get("user_level").toString().equalsIgnoreCase("0")?View.GONE:View.VISIBLE);
			try {
				
				String times = item.get("created").toString();
				Double doTime = Double.parseDouble(times);
				long time = doTime.longValue();
				String stiff = DateDistance.getDistance(time);
				holder.times.setText(stiff);
				
			} catch(Exception e) {
				Log.d(TAG,e.toString());
			}
			
			holder.commLay.setTag(item);
			holder.commLay.setOnClickListener(click);
			
			holder.dissLay.setTag(item);
			holder.dissLay.setOnClickListener(click);
			
			holder.shareLay.setTag(item);
			holder.shareLay.setOnClickListener(click);
			
			holder.moreLay.setTag(item);
			holder.moreLay.setOnClickListener((OnClickListener) context);
			
			holder.playImg.setTag(item);
			holder.playImg.setOnClickListener(click);
			
			holder.portalImg.setTag(item);
			holder.portalImg.setOnClickListener(click);
			
			holder.name.setTag(item);
			holder.name.setOnClickListener(click);
			
			holder.commBtn.setBackgroundResource(item.get("commend")==null?R.drawable.comm:R.drawable.comm_pressed);
			final String imgPortalUrl = (String) item.get("user_img");
			
		
			
			if (imgPortalUrl != null && !imgPortalUrl.equals("")) {
				Bitmap bitmap = imageLoader.loadImage(holder.portalImg, imgPortalUrl,
						new ImageDownloadedCallBack() {
	
							@Override
							public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
		
								if (imageView.getTag() != null && ((Map)imageView.getTag()).
										get("user_img").toString().equalsIgnoreCase(imgPortalUrl)) {
									imageView.setImageBitmap(bitmap);
								}
							}
						});
	
				if (bitmap != null) {
					holder.portalImg.setImageBitmap(bitmap);
				}
			}
			
			final String imgVideoUrl = (String) item.get("video_img");
			holder.imgVideoUrl = imgVideoUrl;
			holder.VideoImg.setTag(imgVideoUrl);
			
			if (imgVideoUrl != null && !imgVideoUrl.equals("")) {
				Bitmap bitmap = imageLoader.loadImage(holder.VideoImg, imgVideoUrl,
						new ImageDownloadedCallBack() {
	
							@Override
							public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
		
								if (imageView.getTag() != null && imageView.getTag().toString().equalsIgnoreCase(imgVideoUrl)) {
									imageView.setImageBitmap(bitmap);
								}
							}
						});
	
				if (bitmap != null) {
					holder.VideoImg.setImageBitmap(bitmap);
				}
			}
			return convertView;
		}catch(Exception e) {
			e.printStackTrace();
			Log.e(TAG,e.toString());
		}
		return super.getView(position, convertView, parent);

	}
	
	OnClickListener click = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			final Map<String,Object> clickItem;
			String uri;
			Intent intent;
			try {
				clickItem = (Map<String,Object>)view.getTag();
				switch(view.getId()) {
					case R.id.hot_line_share://分享
						((ActivityAbstract)context).openShare();
						break;
					
					case R.id.hot_line_comm://点赞
						Log.v(TAG,"the hot_comm_lay");
						if(MainApplication.currentUser ==null) {
							login();
							return;
						}
						LinearLayout commLay = (LinearLayout) view;
						final Button commBtn = (Button) commLay.findViewById(R.id.hot_commend_btn);
						final TextView commTxt = (TextView) commLay.findViewById(R.id.hot_commend_txt);
						JSONObject params = new JSONObject();
					
						params.put("post_id", clickItem.get("post_id"));
						if(clickItem.get("commend")==null) {
							uri = "likes/";
						} else {
							uri = "likes/undo";
							
						}
						StringEntity entity = new StringEntity(params.toString(),"utf-8");
						HotPointerBusiness.like(uri, entity, context, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								switch(msg.what) {
								case 0:
									if(clickItem.get("commend")==null) clickItem.put("commend", true);
									else clickItem.remove("commend");
									commBtn.setBackgroundResource(clickItem.get("commend")==null?
											R.drawable.comm:R.drawable.comm_pressed);
									commTxt.setText(clickItem.get("commend")==null?
											"点赞":"取消");
									Toast.makeText(context, clickItem.get("commend")==null?
											"取消点赞":"点赞成功", 1000).show();
									break;
								case 1:
									Toast.makeText(context, msg.obj.toString(), 1000).show();;
									break;
									
									default:break;
								}
								super.handleMessage(msg);
							}
						});
						break;
					
					case R.id.hot_line_diss://评论
						Log.v(TAG,"the hot_comm_lay");
						if(MainApplication.currentUser ==null) {
							login();
							return;
						}
						intent = new Intent(context, VideoDetailActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("mapItem", new Gson().toJson(clickItem));
						((Activity)context).startActivity(intent);
						break;
						
					case R.id.hot_playBtn:
						Log.v(TAG,"the hot_playBtn");
						String mVideoPath = clickItem.get("video").toString();
						
						if(StringUtils.isNotEmpty(mVideoPath)) {
							uri = "posts/play/" + clickItem.get("post_id").toString();
							VideoBusiness.playTimes(uri, context, new Handler() {
								@Override
								public void handleMessage(Message msg) {
									Log.i(TAG,msg.obj.toString());
								}
							});
							intent = new Intent(context, VideoPlayerActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("path", mVideoPath);
							((Activity)context).startActivity(intent);
						}
						break;
						
					case R.id.hot_portal:
						intent = new Intent();
						intent.putExtra("user", new Gson().toJson(clickItem));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(context, UserPageActivity.class);
						((Activity)context).startActivity(intent);
						break;
					
					case R.id.hot_name:
						intent = new Intent();
						intent.putExtra("user",  new Gson().toJson(clickItem));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(context, UserPageActivity.class);
						((Activity)context).startActivity(intent);
						break;
						
					
					
				}
			}catch(Exception e) {
				Log.e(TAG, e.toString());
			}
					
		}
		
	};
	private class ViewHolder {
		
           TextView name;
           TextView plays;
           TextView times;
           TextView describ;
           TextView likes;
           ImageView portalImg;
           ImageView VideoImg;
           ImageView playImg;
           ImageView authImg;
           Button commBtn;
           LinearLayout commLay;
           LinearLayout dissLay;
           LinearLayout shareLay;
           LinearLayout moreLay; 
           String imgVideoUrl;
           String videoUrl;
           Map<String, Object> item;
           
   }
	private void login() {
		Intent intent = new Intent(context,LoginActivity.class);
		intent.setFlags(100);
		((Activity)context).startActivity(intent);
	}
	
}
