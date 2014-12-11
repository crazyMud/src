package com.farsight.golf.ui.search;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleSearchListAdapter;
import com.farsight.golf.business.SearchBusiness;
import com.farsight.golf.impl.SimplOnGestureListenerImp;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.MainActivity;
import com.farsight.golf.ui.component.MyScrollLayout;
import com.farsight.golf.util.Callback;
import com.google.gson.JsonArray;
import com.loopj.android.http.RequestParams;

public class SearchActivity extends ActivityAbstract implements OnClickListener, OnTouchListener {
	static String TAG = "SearchActivity";
	private ImageView ivCursor;//下划线图片
	List<View> listViews = new ArrayList<View>();
	int ivCursorWidth, tabWidth, offsetX;
	TextView txtSearch, txtHot, txtNear, txtCurrent;
	List<TextView> listTabs = new ArrayList<TextView>();
	EditText searchEdit;
	RelativeLayout toolbar;
	GestureDetector gestureDetector;
	final int RIGHT = 0;  
	final int LEFT = 1; 
	final int TOP = 2;
	final int DOWN = 3;
	Handler handler;
	ListView listHot, listNear;
	BaseAdapter adapterHot,adapterNear;
	Button searchBtn;
	MyScrollLayout viewGroup;
	Runnable runnable;
	JSONArray dataHot = new JSONArray(),
			dataNear = new JSONArray();
	int resource;
	OnClickListener onClickListener;
	ProgressDialog dialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		// TODO Auto-generated method stub
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
		
		txtHot = (TextView) findViewById(R.id.search_hotTxt);
		txtNear = (TextView) findViewById(R.id.search_nearTxt);
		listTabs.add(txtHot);
		listTabs.add(txtNear);
		searchEdit = (EditText) findViewById(R.id.search_edit);
		
		gestureDetector = new GestureDetector(this,onGestureListener);
		
		listHot = (ListView) findViewById(R.id.list_hot);
		
		listNear = (ListView) findViewById(R.id.list_near);
		viewGroup = (MyScrollLayout) findViewById(R.id.view_group);
		viewGroup.setCallback(new Callback() {
			
			@Override
			public void onCallBack(int callBackCode, Object object) {
				startAnimation();
				
			}
		});
		
		listHot.setOnTouchListener(this);
		listNear.setOnTouchListener(this);
		
		toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		
		txtHot.setOnClickListener(this);
		txtNear.setOnClickListener(this);
		
		txtCurrent = txtHot;
		listViews.add(txtHot);
		listViews.add(txtNear);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int resource;

				switch(msg.what) {
				case 0:
					startAnimation();
					initPage();
					break;
				
				case 1:
					/*dataHot = (List<Map<String, Object>>) msg.obj;
					resource = R.layout.search_list;
					adapterHot = new SimpleSearchListAdapter(SearchActivity.this, dataHot, resource);
					listHot.setAdapter(adapterHot);
					listHot.post(new Runnable() {

						@Override
						public void run() {
							getListViewHeightBasedOnChildren(listHot);

						}
						
					});
*/
					break;
				case 2:
					/*dataNear = (List<Map<String, Object>>) msg.obj;
					resource = R.layout.search_list;
					adapterNear = new SimpleListAdapter(SearchActivity.this, dataNear, resource);
					listNear.setAdapter(adapterNear);
					listNear.post(new Runnable() {

						@Override
						public void run() {
							getListViewHeightBasedOnChildren(listNear);

							
						}
						
					});*/
				
					break;
					default:
						break;
				}
			}
		};
		
		
		searchEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
				startActivity(intent);
				
			}
		});
		
		//txtSearch.setOnClickListener(onClickListener);

		InitImageView();
	}
	
	
	private void initPage() {
		 
		if(dialog == null) {
			dialog = ProgressDialog.show(this, "", "数据加载中......", true, true);
		} else {
			dialog.show();
		}
		RequestParams params = new RequestParams();;
		String uri = "users/";
		
		switch(viewGroup.mCurScreen) {
			case 0:
				
				params.put("search_key", "");
				params.put("level", "0");
				SearchBusiness.getHotTeacher(uri, params, this, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						JSONArray jsonArray;
						JsonArray jArray;
						if(msg.what == 0) {
							jsonArray = (JSONArray) msg.obj;
							if(jsonArray.length() > 0) {
								jArray = jsonParser.parse(jsonArray.toString()).getAsJsonArray();
								dataHot = jsonArray;//gson.fromJson(gson.toJson(jArray), dataHot.getClass());
							}
							resource = R.layout.search_list;
							adapterHot = new SimpleSearchListAdapter(SearchActivity.this, dataHot, resource);
							listHot.setAdapter(adapterHot);
							
						} else if(msg.what ==1){
							alertView(msg.obj.toString());
						} else if(msg.what == 10) {
							ViewGroup.LayoutParams vparams = viewGroup.getLayoutParams();
							vparams.height = (Integer) msg.obj;
							viewGroup.setLayoutParams(vparams);

						}
						dialog.dismiss();
						
					}
				});
				break;
			case 1:
				params.put("search_key", "");
				params.put("level", "1");
				SearchBusiness.getNearTeacher(uri, params, this, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						JSONArray jsonArray;
						JsonArray jArray;
						if(msg.what == 0) {
							jsonArray = (JSONArray) msg.obj;
							if(jsonArray.length() > 0) {
								jArray = jsonParser.parse(jsonArray.toString()).getAsJsonArray();
								dataNear = jsonArray;//gson.fromJson(gson.toJson(jArray), dataNear.getClass());
							}
							resource = R.layout.search_list;
							adapterNear = new SimpleSearchListAdapter(SearchActivity.this, dataNear, resource);
							listNear.setAdapter(adapterNear);
							
							
						} else if(msg.what == 1) {
							alertView(msg.obj.toString());
							
						}
						dialog.dismiss();
					}
				});
				break;
		}
		
	}
	
	private OnGestureListener onGestureListener =  new SimplOnGestureListenerImp(new Callback() {

		@Override
		public void onCallBack(int callBackCode, Object object) {
			switch (callBackCode) {  
	        case RIGHT:  
	        	System.out.println("go right");
	        	if(viewGroup.mCurScreen == 1) {
	        		viewGroup.snapToScreen(0);
	        		
	        		if(listHot.getChildCount() == 0 && dataHot.length() == 0) {
	        			initPage();
	        		} 
	            }
	            break;  
	   
	        case LEFT:  
	            System.out.println("go left");  
	            if(viewGroup.mCurScreen == 0) {
	        		viewGroup.snapToScreen(1);
	        		
	        		
	        		if(listNear.getChildCount() == 0 &&  dataNear.length() == 0) {
	        			initPage();
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
			  
			 

	@Override
	public void onClick(View view) {
		if(txtCurrent.getId() != view.getId() && view instanceof TextView) {
			 ((TextView) view).setTextColor(Color.parseColor("#5fb336"));
        	 txtCurrent.setTextColor(Color.parseColor("#333333"));
        	 txtCurrent = (TextView) view;
		}
		switch(view.getId()) {
			case R.id.search_hotTxt:
				Log.v(TAG,"hot point");
				
				if(listHot.getChildCount() == 0 && dataHot.length() == 0) {
        			initPage();
        		} 
				viewGroup.snapToScreen(0);
        		
        	
				break;
				
			case R.id.search_nearTxt:
				Log.v(TAG,"the news");
				viewGroup.snapToScreen(1);
				if(listNear.getChildCount() == 0 &&  dataNear.length() == 0) {
        			initPage();
        		} 
				break;
				
			default:
				break;
		}
		startAnimation();
		
	}
	
	public void startAnimation() {
		
		 Animation animation = new TranslateAnimation(tabWidth * viewGroup.mCurScreen, tabWidth * viewGroup.mCurScreen, 0, 0);

		 animation.setFillAfter(true);// True:图片停在动画结束位置

		 animation.setDuration(200);

		 ivCursor.startAnimation(animation);
		 
		 if(txtCurrent.getId() != listTabs.get(viewGroup.mCurScreen).getId()) {
				listTabs.get(viewGroup.mCurScreen).setTextColor(Color.parseColor("#5fb336"));
	       	 	txtCurrent.setTextColor(Color.parseColor("#333333"));
	       	 	txtCurrent = (TextView) listTabs.get(viewGroup.mCurScreen);
		}
			
	}
	
	/**
     * 初始化动画
*/
    private void InitImageView() {
    	
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				ivCursor = (ImageView) findViewById(R.id.cursor);
				DisplayMetrics dm = getResources().getDisplayMetrics();
		        int screenW = dm.widthPixels;// 获取分辨率宽度

		        tabWidth = screenW /2;
	            ivCursor.getLayoutParams().width = tabWidth;
				handler.sendEmptyMessage(0);
			}
		}).start();
    	
        
    }
    
    
    public void clear(View view) {
    	searchEdit.setText("");
    }

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		

		 return gestureDetector.onTouchEvent(event);
		

	}
	

	
}
