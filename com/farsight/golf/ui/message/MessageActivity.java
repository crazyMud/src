package com.farsight.golf.ui.message;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleNotifyAdapter;
import com.farsight.golf.business.MessageBusiness;
import com.farsight.golf.impl.SimplOnGestureListenerImp;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.LoginActivity;
import com.farsight.golf.ui.MainActivity;
import com.farsight.golf.ui.component.MyScrollLayout;
import com.farsight.golf.ui.component.ScrollViewExtend;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.ListViewTools;
import com.google.gson.JsonArray;
import com.loopj.android.http.RequestParams;

public class MessageActivity extends ActivityAbstract implements OnClickListener, OnTouchListener {
	static String TAG = "MessageActivity";
	int currIndex = 0,ivCursorWidth, tabWidth, offsetX;// tab头的宽度减去动画图片的宽度再除以2（保证动画图片相对tab头居中）
	private ImageView ivCursor;//下划线图片
	List<TextView> listViews = new ArrayList<TextView>();
	TextView txtNotify, txtMsg, txtCurrent;
	GestureDetector gestureDetector;
	ScrollViewExtend scrollView;
	ProgressBar progressBar;
	final int RIGHT = 0;  
	final int LEFT = 1; 
	final int TOP = 2;
	final int DOWN = 3;
	Handler handler;
	ListView listViewNotify, listViewMsg;
	BaseAdapter adapterNotify,adapterMsg;
	MyScrollLayout viewGroup;
	List<Map<String,Object>> dataNotify = new ArrayList<Map<String,Object>>(),
			dataMsg = new ArrayList<Map<String,Object>>();
	static final int WHAT_INIT = 0,WHAT_NOTIFY=1,WHAT_MESSAGE=2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressBar.setVisibility(View.GONE);
				int resource;
				String[] from;
				int[] to;
				Runnable runnable;
				JSONArray jsonArray;
				JsonArray jArray;
				if(msg.what >= 900) {
					progressBar.setVisibility(View.GONE);
					if(msg.what == 903) {
						Intent intent = new Intent(MessageActivity.this,LoginActivity.class);
						startActivity(intent);
					} else {
						alertView(msg.obj.toString());
					}
					return;
				}
				switch(msg.what) {
				case WHAT_INIT:
					initPage(0);
					break;
					
				case WHAT_NOTIFY:
					jsonArray = (JSONArray) msg.obj;
					if(jsonArray.length() > 0) {
						jArray = jsonParser.parse(jsonArray.toString()).getAsJsonArray();
						dataNotify = gson.fromJson(gson.toJson(jArray), dataNotify.getClass());
					}
					resource = R.layout.notify_list;
					adapterNotify = new SimpleNotifyAdapter(MessageActivity.this, dataNotify, resource);
					listViewNotify.setAdapter(adapterNotify);
				/*	runnable = new ListViewTools(listViewNotify,handler);
					listViewNotify.post(runnable);*/
					break;
					
				case WHAT_MESSAGE:
					
					dataMsg = (List<Map<String, Object>>) msg.obj;
					resource = R.layout.message_list;
					from = new String[]{"hotPortal","hotName"};
					to = new int[]{R.id.hot_portal,R.id.hot_name};
					adapterMsg = new SimpleAdapter(MessageActivity.this, dataMsg, resource, from, to);
					listViewMsg.setAdapter(adapterMsg);
					/*runnable = new ListViewTools(listViewMsg,handler);
					listViewMsg.post(runnable);*/
					break;
					
				default:
						ViewGroup.LayoutParams vparams = viewGroup.getLayoutParams();
						vparams.height = (Integer) msg.obj;
						viewGroup.setLayoutParams(vparams);
						scrollView.smoothScrollTo(0, 0);
						progressBar.setVisibility(View.GONE);
						break;
				}

			}
		};
		init();
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){

		if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
			if(getParent() instanceof MainActivity) {
				MainActivity mainActivity = (MainActivity) getParent();
				mainActivity.onKeyDown(keyCode, event);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private void init() {
		ivCursor = (ImageView) findViewById(R.id.cursor);
		txtNotify = (TextView) findViewById(R.id.message_notifyTxt);
		txtMsg = (TextView) findViewById(R.id.message_msgTxt);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		scrollView = (ScrollViewExtend) findViewById(R.id.index_scroll);
		listViewNotify = (ListView) findViewById(R.id.listView_notify);
		listViewNotify.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long arg3) {
				/*
				 * post_id=0 关注，通过USERID打开个人主页
				 * post_id!= 0 评论，打开postid对应的视频详细页
				 */
				
			}
			
		});
		listViewMsg = (ListView) findViewById(R.id.listView_msg);
		viewGroup = (MyScrollLayout) findViewById(R.id.view_group);
		gestureDetector = new GestureDetector(MessageActivity.this,onGestureListener);
		listViewNotify.setOnTouchListener(MessageActivity.this);
		listViewMsg.setOnTouchListener(MessageActivity.this);
		
		txtNotify.setOnClickListener(MessageActivity.this);
		txtMsg.setOnClickListener(MessageActivity.this);
		txtCurrent = txtNotify;
		
		listViews.add(txtNotify);
		listViews.add(txtMsg);
		InitImageView();
		
	}
	private OnGestureListener onGestureListener =  new SimplOnGestureListenerImp(new Callback() {

		@Override
		public void onCallBack(int callBackCode, Object object) {
			Runnable runnable;
			switch (callBackCode) {  
	        case RIGHT:  
	        	System.out.println("go right");
	        	if(viewGroup.mCurScreen == 1) {
	        		currIndex = 0;
	        		viewGroup.snapToScreen(0);
	        		startAnimation();
	        		if(dataNotify.size() > 0) {
	        			runnable = new ListViewTools(listViewNotify,handler);
	        			viewGroup.post(runnable);
	        		} else {
	        			initPage(0);
	        		}
	            }
	            break;  
	   
	        case LEFT:  
	            System.out.println("go left");  
	            if(viewGroup.mCurScreen < 1) {
	        		currIndex = 1;
	        		viewGroup.snapToScreen(1);
	        		startAnimation();
	        		if(dataMsg.size() > 0) {
	        			runnable = new ListViewTools(listViewMsg,handler);
	        			viewGroup.post(runnable);
	        		} else {
	        			initPage(1);
	        		}
	        		
	            }
	            break;  
	        case TOP:  
		        System.out.println("go top");  
		        break;  
	        case DOWN:  
		        System.out.println("go down");  
		        break;  
	    
	          }  
			
		}
		
	});
	public void startAnimation() {
		
		 Animation animation = new TranslateAnimation(tabWidth * currIndex , tabWidth * currIndex , 0, 0);

		 animation.setFillAfter(true);// True:图片停在动画结束位置

		 animation.setDuration(200);

		ivCursor.startAnimation(animation);
		if(txtCurrent.getId() != listViews.get(viewGroup.mCurScreen).getId()) {
			listViews.get(viewGroup.mCurScreen).setTextColor(Color.parseColor("#5fb336"));
       	 	txtCurrent.setTextColor(Color.parseColor("#333333"));
       	 	txtCurrent = (TextView) listViews.get(viewGroup.mCurScreen);
	}
	}
	
	/**
    * 初始化动画
*/
   private void InitImageView() {
   	
   	new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				DisplayMetrics dm = getResources().getDisplayMetrics();

		        int screenW = dm.widthPixels;// 获取分辨率宽度
		        
		        ivCursorWidth = BitmapFactory.decodeResource(getResources(),R.drawable.cursor).getWidth();// 获取图片宽度

		        tabWidth = screenW /2;

		        ivCursor.getLayoutParams().width = tabWidth;

		        handler.sendEmptyMessage(0);
			}
		}).start();
   	
       
   }
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	@Override
	public void onClick(View view) {
		if(txtCurrent.getId() != view.getId() && view instanceof TextView) {
			 ((TextView) view).setTextColor(Color.parseColor("#5fb336"));
	       	 txtCurrent.setTextColor(Color.parseColor("#333333"));
	       	 txtCurrent = (TextView) view;
		}
		switch(view.getId()) {
			case R.id.message_notifyTxt:
				Log.v(TAG,"the notify");
				currIndex = 0;
				viewGroup.snapToScreen(0);
       		
       		if(dataNotify.size() > 0) {
       			/*viewGroup.post(new Runnable() {

						@Override
						public void run() {
							progressBar.setVisibility(View.VISIBLE);
							Runnable runnable = new ListViewTools(listViewNotify,handler);
							new Thread(runnable).start();

						}
						
					});*/
       		} else {
       			initPage(0);
       		}
				break;
				
			case R.id.message_msgTxt:
				Log.v(TAG,"the message");
				currIndex = 1;
				if(dataMsg.size() > 0) {
       			/*viewGroup.post(new Runnable() {

						@Override
						public void run() {
							progressBar.setVisibility(View.VISIBLE);
							Runnable runnable = new ListViewTools(listViewMsg,handler);
							new Thread(runnable).start();

						}
						
					});*/
       		} else {
       			initPage(1);
       		}
				break;
				
			default:
				break;
		}
		startAnimation();
	}
	
	private void initPage(final int pageIndex) {
		progressBar.setVisibility(View.VISIBLE);
		RequestParams params;
		String uri;
		uri = "messages/";
		if(pageIndex == 0) {
			params = new RequestParams();
			MessageBusiness.getNotify(uri, params, MessageActivity.this, handler, 1);
			
		} else if(pageIndex == 1) 
			MessageBusiness.getMessage("", null, null, handler);
		
	}
}
