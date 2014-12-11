package com.farsight.golf.ui.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleListAdapter;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.business.HotPointerBusiness;
import com.farsight.golf.business.VideoBusiness;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.component.XListView;
import com.farsight.golf.ui.component.XListView.IXListViewListener;
import com.farsight.golf.ui.video.VideoPlayerActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;
import com.yixia.camera.util.StringUtils;

public class VideoTypeActivity extends ActivityAbstract  implements OnClickListener, IXListViewListener {
	static String TAG = "VideoTypeActivity"; 
	TextView titleTv, videoNumTv, typeDescTv;
	XListView lvType;
	ImageView hotPortalIv;
	List<Map<String,Object>> typeList = new ArrayList<Map<String,Object>>();
	BaseAdapter typeAdapter = null;
	JsonParser parser = new JsonParser();
	JSONArray jsonArray;
	JsonArray jArray;
	AsyncImageLoader imageLoader;
	ProgressDialog dialog = null;
	LayoutInflater inflater;
	View videoTypeHead;
	Map map;
	int pos = 0;
	String uri;
	RelativeLayout toolbar;
	RequestParams params = new RequestParams();
	String cateId;
	JSONObject  cateObject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_type_activity);
		init();
	}
	private void init() {
		
		inflater = getLayoutInflater();
		imageLoader = new AsyncImageLoader(this);
		lvType = (XListView) findViewById(R.id.lv_type);
		titleTv = (TextView) findViewById(R.id.type_title);
		toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		
		videoTypeHead = (LinearLayout)inflater.inflate(R.layout.video_type_head, null);
		
		videoNumTv = (TextView) videoTypeHead.findViewById(R.id.video_numtv);
		typeDescTv = (TextView) videoTypeHead.findViewById(R.id.video_desctv);
		hotPortalIv = (ImageView) videoTypeHead.findViewById(R.id.hot_portal);
		
		lvType.addHeaderView(videoTypeHead);
		lvType.setPullLoadEnable(true);
		lvType.setXListViewListener(this);
		
		
		try {
			String strObj = getIntent().getStringExtra("cate");
			if(strObj != null) {
				cateObject = new JSONObject(strObj);
				if(cateObject != null) {
					cateId = cateObject.getString("id");
					String title = cateObject.getString("title");
					String desc = cateObject.getString("description");
					String imgPortalUrl =cateObject.getString("cate_img");
					typeDescTv.setText(desc);
					titleTv.setText(title);
					
					if (imgPortalUrl != null && !imgPortalUrl.equals("")) {
						Bitmap bitmap = imageLoader.loadImage(hotPortalIv, imgPortalUrl,
								new ImageDownloadedCallBack() {
	
									@Override
									public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
										imageView.setImageBitmap(bitmap);
									
									}
								});
	
						if (bitmap != null) {
							hotPortalIv.setImageDrawable(new BitmapDrawable(bitmap));
						}
					}
					
					initData(IS_REFRESH);
					
				}
			} else {
				strObj = getIntent().getStringExtra("cateId");
				if(strObj != null) {
					cateId = strObj;
					uri = "categorys/";
					params = new RequestParams();
					params.put("type", "top");
					params.put("cate_id", cateId);
					params.put("tag", "");
					params.put("search_key", "");
					params.put("user_id", "");
					params.put("direction", "");
					params.put("last_post_id", "");
					params.setContentEncoding("utf-8");
					HotPointerBusiness.getTypePointer(uri, params, this, new Handler() {
						@Override
						public void handleMessage(Message msg) {
							JSONArray jsonArray;
							try {
								jsonArray = (JSONArray) msg.obj;
								if(jsonArray.length() == 0) return;
								cateObject = (JSONObject) jsonArray.get(0);
								String title = cateObject.getString("title");
								String desc = cateObject.getString("description");
								String imgPortalUrl =cateObject.getString("cate_img");
								typeDescTv.setText(desc);
								titleTv.setText(title);
								
								if (imgPortalUrl != null && !imgPortalUrl.equals("")) {
									Bitmap bitmap = imageLoader.loadImage(hotPortalIv, imgPortalUrl,
											new ImageDownloadedCallBack() {
				
												@Override
												public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
													imageView.setImageBitmap(bitmap);
												
												}
											});
				
									if (bitmap != null) {
										hotPortalIv.setImageDrawable(new BitmapDrawable(bitmap));
									}
								}
								
								initData(IS_REFRESH);
							}catch(Exception e) {
								Log.e(TAG, e.toString());
							}
						}
					}, 2);
				}
			}
		} catch(Exception e) {
			Log.e(TAG,e.toString());
		}
		
		
	}
	private void initData(final int opt) {
		if(dialog == null) {
			dialog = ProgressDialog.show(this, "", "数据加载中......", true, true);
		} else {
			dialog.show();
		}
		String uri = "posts/";
		params.put("type", "");
		params.put("cate_id", cateId);
		params.put("tag", "");
		params.put("search_key", "");
		params.put("user_id", "");
		params.put("direction", "");
		params.put("last_post_id", "");
		params.setContentEncoding("utf-8");
		
		HotPointerBusiness.getNewsPointer(uri, params, VideoTypeActivity.this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				jsonArray = (JSONArray) msg.obj;
				List<Map<String, Object>> list = typeList = new ArrayList<Map<String,Object>>();
				if( jsonArray.length() > 0) {
					list.clear();
					jArray = parser.parse(msg.obj.toString()).getAsJsonArray();
					list = (List<Map<String, Object>>) gson.fromJson(gson.toJson(jArray), typeList.getClass());
				}
				int resource = R.layout.hot_list;
				if(typeAdapter == null) {
					typeList.addAll(list);
					typeAdapter = new SimpleListAdapter(VideoTypeActivity.this, typeList, resource);
					lvType.setAdapter(typeAdapter);
				} else if(opt == IS_LOADING) {
					typeList.addAll(list);
					
				} else if(opt == IS_REFRESH) {
					typeList.clear();
					typeList.addAll(list);
					
				}
				typeAdapter.notifyDataSetChanged();
				videoNumTv.setText(String.format("共有%d个视频", typeList.size()));
				
				onLoad();
			}
		}, 0);
	}
	@Override
	public void onClick(View view) {
		
		pos = lvType.getPositionForView(view);
		map = typeList.get(pos);
		switch(view.getId()) {
			case R.id.hot_playBtn:
				String mVideoPath = map.get("video").toString();
				
				if(StringUtils.isNotEmpty(mVideoPath)) {
					uri = "posts/play/" + map.get("post_id").toString();
					VideoBusiness.playTimes(uri, this, new Handler() {
						@Override
						public void handleMessage(Message msg) {
							Log.i(TAG,msg.obj.toString());
						}
					});
					startActivity(new Intent(this, VideoPlayerActivity.class).putExtra("path", mVideoPath));
				}
				break;
			case R.id.hot_portal:
				break;
				
		}
		
		
	}
	@Override
	public void onRefresh() {
		initData(IS_REFRESH);
		
	}
	@Override
	public void onLoadMore() {
		initData(IS_LOADING);
		
	}
	private void onLoad() {
		dialog.dismiss();
		lvType.stopRefresh();
		lvType.stopLoadMore();
		lvType.setRefreshTime("刚刚");
	}
}
