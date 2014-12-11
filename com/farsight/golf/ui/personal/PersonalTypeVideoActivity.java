package com.farsight.golf.ui.personal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleListAdapter;
import com.farsight.golf.business.HotPointerBusiness;
import com.farsight.golf.business.PersonalInfoBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.component.XListView;
import com.farsight.golf.ui.component.XListView.IXListViewListener;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.WindowUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;

public class PersonalTypeVideoActivity extends ActivityAbstract implements OnClickListener, IXListViewListener {
	static String TAG = "PersonalTypeVideoActivity";
	TextView typeTxt;
	RelativeLayout innerToolbar;
	XListView listView;
	SimpleListAdapter adapter;
	int listResource;
	List<Map<String,Object>> dataVideo = new ArrayList<Map<String,Object>>();
	Map<String,Object> map_g;
	RequestParams params;
	String uri;
	JSONObject cuser = MainApplication.currentUser;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_type);
		
		typeTxt = (TextView) this.findViewById(R.id.video_type);
		innerToolbar = (RelativeLayout) this.findViewById(R.id.toolBarLayout);
		innerToolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		listView = (XListView) this.findViewById(R.id.listView_video);
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);
		init();
		
	}
	private void init() {

		try {
			if(dialog == null)
				dialog = ProgressDialog.show(this, "","视频正在加载中");
			else 
				dialog.show();
			String metype = getIntent().getStringExtra("metype");
			String title = getIntent().getStringExtra("title");
			if(metype == null) return;
			uri = "posts/";
			params = new RequestParams();
			params.put("type", "");
			params.put("cate_id", "");
			params.put("tag", "");
			params.put("search_key", "");
			params.put("user_id", "");
			params.put("direction", "");
			params.put("last_post_id", "");
			params.put("metype", metype);
			params.setContentEncoding("utf-8");
			typeTxt.setText(title);
			
			PersonalInfoBusiness.getVideoByType(uri, params, this, new Handler() {
				public void handleMessage(Message msg) {
					dialog.dismiss();
					JSONArray jsonArray;
					JsonParser parser = new JsonParser();
					JsonArray jArray;				
					jsonArray = (JSONArray) msg.obj;
					if( jsonArray.length() > 0) {
						jArray = parser.parse(msg.obj.toString()).getAsJsonArray();
						dataVideo = (List<Map<String, Object>>) gson.fromJson(gson.toJson(jArray), dataVideo.getClass());
					}
					listResource = R.layout.hot_list;
					adapter = new SimpleListAdapter(PersonalTypeVideoActivity.this, dataVideo, listResource);
					listView.setAdapter(adapter);
					
				};
			});
				
		}catch(Exception e) {
			dialog.dismiss();
				Log.e(TAG,e.getMessage());
			}
	}
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.hot_line_more) {//更多
			Log.v(TAG,"the hot_line_more");
			int position = listView.getPositionForView(view);
			map_g = dataVideo.get(position);
			final View moreView = getLayoutInflater().inflate(R.layout.pop_window, null);
			final WindowUtils popWin = new WindowUtils(this,moreView);
			
			OnClickListener click = new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					switch(view.getId()) {
						case R.id.cancel:
							popWin.dismiss();
							break;
						case R.id.copy_url:
							copy(map_g.get("shareurl").toString(),PersonalTypeVideoActivity.this);
							Toast.makeText(PersonalTypeVideoActivity.this, "复制URL成功", Toast.LENGTH_LONG).show();
							popWin.dismiss();
							break;
						case R.id.delete:
							Log.e(TAG,"delete post");
							popWin.dismiss();
							confirmDialog(new Callback(){

								@Override
								public void onCallBack(int callBackCode, Object object) {
									if(callBackCode == 0) {
										String deleteuri = "posts/" + map_g.get("post_id").toString();
										
										try {
											HotPointerBusiness.delete(deleteuri,null,PersonalTypeVideoActivity.this,new Handler() {
												@Override
												public void handleMessage(Message msg) {
													// TODO Auto-generated method stub
													super.handleMessage(msg);
												
													if(msg.what == 0) {
														
														
													} else {
														alertView(msg.obj.toString());
														
													}
													
													
												}
											},40);
										}catch(Exception e) {
											
											alertView(e.getMessage());
										}
									}
								}
								}, "确定删除吗？", "确定 ", "取消", "");
							
							break;
					}
					
				}
			};
			moreView.findViewById(R.id.cancel).setOnClickListener(click);
			moreView.findViewById(R.id.copy_url).setOnClickListener(click);
			View delView = moreView.findViewById(R.id.delete);
			delView.setOnClickListener(click);
			moreView.setOnTouchListener(new OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent event) {
					 Log.i(TAG, "onTouch");
		                int x = (int) event.getX();
		                int y = (int) event.getY();
		                Rect rect = new Rect();
		                moreView.findViewById(R.id.popup_window).getGlobalVisibleRect(rect);
		                if (!rect.contains(x, y)) {
		                	popWin.dismiss();
		                }
		                return false;
					
				}
			});
			popWin.showAtLocation(findViewById(R.id.main_index), Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
		}
		
	}
	private void updateItemInfoList(final int opt/*refresh or loading*/) {
		try {
			if(dialog == null)
				dialog = ProgressDialog.show(this, "","视频正在加载中");
			else 
				dialog.show();
			PersonalInfoBusiness.getVideoByType(uri, params, this, new Handler() {
				public void handleMessage(Message msg) {
					dialog.dismiss();
					JSONArray jsonArray;
					JsonParser parser = new JsonParser();
					JsonArray jArray;				
					jsonArray = (JSONArray) msg.obj;
					if(jsonArray.length() == 0) return;
					jArray = parser.parse(msg.obj.toString()).getAsJsonArray();
					List<Map<String, Object>> ls = (List<Map<String, Object>>) gson.fromJson(gson.toJson(jArray), dataVideo.getClass());
					if(opt == IS_REFRESH) {
						dataVideo.clear();
						dataVideo.addAll(ls);
					} else if(opt == IS_LOADING) {
						dataVideo.addAll(ls);
					}
					onLoad();
					adapter.notifyDataSetChanged();
					
				};
			});
		}catch(Exception e) {
			dialog.dismiss();
			alertView(e.toString());
		}
	}
	@Override
	public void onRefresh() {
		updateItemInfoList(IS_REFRESH);
		
	}
	@Override
	public void onLoadMore() {
		updateItemInfoList(IS_LOADING);
		
	}
	private void onLoad() {

			listView.stopRefresh();
			listView.stopLoadMore();
			listView.setRefreshTime("刚刚");

		
	}
}
