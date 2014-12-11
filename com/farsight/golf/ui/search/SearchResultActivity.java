package com.farsight.golf.ui.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleJSONAdapter;
import com.farsight.golf.adapter.SimpleSearchListAdapter;
import com.farsight.golf.business.SearchBusiness;
import com.farsight.golf.impl.SimplOnGestureListenerImp;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.component.MyScrollLayout;
import com.farsight.golf.util.Callback;
import com.loopj.android.http.RequestParams;

public class SearchResultActivity extends ActivityAbstract implements OnClickListener, OnTouchListener {
	static String TAG = "SearchResultActivity";
	RelativeLayout toolbar;
	EditText searchEdit;
	Button searchBtn;
	TextView txtVideo, txtUser, txtSearch, txtCurrent, txtVideoKey, txtUserKey;
	ImageView ivSearchBtn;
	private ImageView ivCursor;
	int ivCursorWidth, tabWidth, offsetX;
	MyScrollLayout viewGroup;
	List<TextView> listTabs = new ArrayList<TextView>();
	ListView lvVideo, lvUser;
	BaseAdapter adapterVideo, adapterUser;
	JSONArray dataVideo = new JSONArray(),
			dataUser = new JSONArray();
	String uri;
	int resource;
	GestureDetector gestureDetector;
	LayoutInflater inflate;
	View videoHead, userHead;
	int headHeight;
	RelativeLayout vhContent, uhContent;
	final int RIGHT = 0, LEFT = 1, TOP = 2, DOWN = 3;
	ProgressDialog dialog = null;
	OnGestureListener onGestureListener =  new SimplOnGestureListenerImp(new Callback() {

		@Override
		public void onCallBack(int callBackCode, Object object) {
			switch (callBackCode) {  
	        case RIGHT:  
	        	System.out.println("go right");
	        	if(viewGroup.mCurScreen == 1) {
	        		viewGroup.snapToScreen(0);
	        		
	        		if(lvVideo.getChildCount() == 0 && dataVideo.length() == 0) {
	        			initPage();
	        		} 
	            }
	            break;  
	   
	        case LEFT:  
	            System.out.println("go left");  
	            if(viewGroup.mCurScreen == 0) {
	        		viewGroup.snapToScreen(1);
	        		
	        		
	        		if(lvUser.getChildCount() == 0 &&  dataUser.length() == 0) {
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
		init();
	}
	
	
	
	OnKeyListener onkey = new OnKeyListener() {
		
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_ENTER) {
				InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(imm.isActive())
					imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0 );
				initSearchHead();
				return true;
			}
			return false;
		}
	};
	private void init() {
		inflate = this.getLayoutInflater();
		gestureDetector = new GestureDetector(this,onGestureListener);
		toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		lvVideo = (ListView) findViewById(R.id.list_video);
		lvUser = (ListView) findViewById(R.id.list_user);
		
		lvVideo.setOnTouchListener(this);
		lvUser.setOnTouchListener(this);
		
		searchEdit = (EditText) findViewById(R.id.search_edit);
		//searchEdit.setOnKeyListener(onkey);
		
		videoHead = inflate.inflate(R.layout.search_head, null);
		vhContent = (RelativeLayout) videoHead.findViewById(R.id.head_content);
		vhContent.setVisibility(View.GONE);
		txtVideoKey = (TextView) videoHead.findViewById(R.id.search_key);
		ivSearchBtn = (ImageView) videoHead.findViewById(R.id.search_ivBtn);
		ivSearchBtn.setOnClickListener(this);
		adapterVideo = new SimpleJSONAdapter(SearchResultActivity.this, dataVideo, resource);
		lvVideo.addHeaderView(videoHead,null,false);
		lvVideo.setAdapter(adapterVideo);
		
		
		userHead = inflate.inflate(R.layout.search_head, null);
		uhContent = (RelativeLayout) userHead.findViewById(R.id.head_content);
		uhContent.setVisibility(View.GONE);
		txtUserKey = (TextView) userHead.findViewById(R.id.search_key);
		ivSearchBtn = (ImageView) userHead.findViewById(R.id.search_ivBtn);
		ivSearchBtn.setOnClickListener(this);
		adapterUser = new SimpleSearchListAdapter(SearchResultActivity.this, dataUser, resource);
		lvUser.addHeaderView(userHead,null,false);
		lvUser.setAdapter(adapterUser);
		
		searchEdit.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable edit) {
				initSearchHead();
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if(imm.isActive())
						imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0 );
					initSearchHead();
                    return true;
				}
				return false;
			}
		});

		searchBtn = (Button) findViewById(R.id.search_clear);
		txtSearch = (TextView) findViewById(R.id.search_txt);
		txtSearch.setOnClickListener(this);
		txtVideo = (TextView) findViewById(R.id.search_VideoTxt);
		txtUser = (TextView) findViewById(R.id.search_UserTxt);
		
		listTabs.add(txtVideo);
		listTabs.add(txtUser);
		txtCurrent = txtVideo;
		
		viewGroup = (MyScrollLayout) findViewById(R.id.view_group);
		viewGroup.setCallback(new Callback() {

			@Override
			public void onCallBack(int callBackCode, Object object) {
				startAnimation();
				
			}
			
		});
		
		txtVideo.setOnClickListener(this);
		txtUser.setOnClickListener(this);
		txtSearch.setOnClickListener(this);
		searchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				searchEdit.setText("");
				
			}
		});
		InitImageView();
		//initPage();
	}
	
	private void initPage() {
		if(dialog == null) {
			dialog = ProgressDialog.show(this, "", "数据加载中......", true, true);
		} else
			dialog.show();
		RequestParams params = new RequestParams();
		switch(viewGroup.mCurScreen) {
		case 0:
			vhContent.setVisibility(View.GONE);
			uri = "posts/";
			
			params.put("type", "");
			params.put("cate_id", "");
			params.put("tag", "");
			params.put("search_key", searchEdit.getText().toString());
			params.put("user_id", "");
			params.put("direction", "");
			params.put("last_post_id", "");
			params.put("metype", "");
			params.setContentEncoding("utf-8");
			SearchBusiness.searchVideo(uri, params, this, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if(msg.what == 0) {
						dataVideo = (JSONArray) msg.obj;
						resource = R.layout.hot_list;
						adapterVideo = new SimpleJSONAdapter(SearchResultActivity.this, dataVideo, resource);
						lvVideo.setAdapter(adapterVideo);
						
					} else {
						alertView(msg.obj.toString());
					}
					dialog.dismiss();
				}
			});
			break;
		case 1:
			uhContent.setVisibility(View.GONE);
			String uri = "users/";
			params.put("search_key", searchEdit.getText().toString());
			params.put("level", "0");
			SearchBusiness.getHotTeacher(uri, params, this, new Handler() {
				@Override
				public void handleMessage(Message msg) {

					if(msg.what == 0) {
						dataUser = (JSONArray) msg.obj;
						resource = R.layout.search_list;
						adapterUser = new SimpleSearchListAdapter(SearchResultActivity.this, dataUser, resource);
						lvUser.setAdapter(adapterUser);
						
					} else if(msg.what ==1){
						alertView(msg.obj.toString());
					}
					dialog.dismiss();
				}
			});
			break;
			
		}
	}
	
	@SuppressLint("NewApi")
	public void deleteItem(int position) {
		if(viewGroup.mCurScreen == 0) {
			dataVideo.remove(position);
			adapterVideo.notify();
			lvVideo.invalidate();
		} else{
			dataUser.remove(position);
			adapterUser.notify();
			lvUser.invalidate();
		}
	}
	private void initSearchHead() {
		
		if(viewGroup.mCurScreen == 0) {
			lvVideo.post(new Runnable() {
				
				@Override
				public void run() {
					txtVideoKey.setText(String.format("搜索\"%s\"相关的视频", searchEdit.getText().toString()));
					vhContent.setVisibility(View.VISIBLE);
					
				}
			});
		} else {
			lvUser.post(new Runnable() {
				
				@Override
				public void run() {
					txtUserKey.setText(String.format("搜索\"%s\"相关的用户", searchEdit.getText().toString()));
					uhContent.setVisibility(View.VISIBLE);
					
				}
			});
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

		        tabWidth = screenW /viewGroup.getChildCount();
	            ivCursor.getLayoutParams().width = tabWidth;
		       	
			}
		}).start();
    	
        
    }

	@Override
	public void onClick(View view) {
		if(txtCurrent.getId() != view.getId() && view instanceof TextView) {
			 ((TextView) view).setTextColor(Color.parseColor("#5fb336"));
       	 txtCurrent.setTextColor(Color.parseColor("#333333"));
       	 txtCurrent = (TextView) view;
		}
		
		switch(view.getId()) {
			case R.id.search_ivBtn:
				
				initPage();
				break;
				
			case R.id.search_VideoTxt:
				Log.v(TAG,"hot point");
				
				if(lvVideo.getChildCount() == 0 && dataVideo.length() == 0) {
	    			initPage();
	    		} 
				viewGroup.snapToScreen(0);
	    		
	    	
				break;
				
			case R.id.search_UserTxt:
				Log.v(TAG,"the news");
				viewGroup.snapToScreen(1);
				if(lvUser.getChildCount() == 0 &&  dataUser.length() == 0) {
	    			initPage();
	    		} 
				break;
			case R.id.search_txt:
				finish();
				break;
		default:
			startAnimation();
			break;
		}
		
	}
	
	private void startAnimation() {
		
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

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		 return gestureDetector.onTouchEvent(event);
	}
	
	
}
