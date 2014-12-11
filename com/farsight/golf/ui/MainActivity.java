package com.farsight.golf.ui;




import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.service.MessageService;
import com.farsight.golf.ui.component.MyScrollLayout;
import com.farsight.golf.ui.home.IndexActivity;
import com.farsight.golf.ui.message.MessageActivity;
import com.farsight.golf.ui.personal.PersonalCenterActivity;
import com.farsight.golf.ui.search.SearchActivity;
import com.farsight.golf.ui.video.LocalVideoActivity;
import com.farsight.golf.ui.video.MediaRecorderActivity;
import com.farsight.golf.ui.video.VideoPublishActivity;
import com.farsight.golf.util.ConstUtil;
import com.farsight.golf.util.PollingUtils;
import com.farsight.golf.util.WindowUtils;

public class MainActivity extends ActivityAbstract implements OnClickListener, Observer  {
	static String TAG = "MainActivity";
    private Map<String,View> mapViews = new HashMap<String,View>(); // Tab页面列表
    private Map<String,Window> mapWins = new HashMap<String,Window>(); // Tab页面列表
    ImageView redPointer;
    WindowUtils popVideoChoiceWin;
    public static MainActivity mainObserver;
    LocalActivityManager manager = null;
    LinearLayout container;
    RelativeLayout layHome, laySearch, layVideo, layMessage, layMe, curView;
    Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
    	
    };
    boolean KEYCODE_BACK_FIRST = false;
    long temptime;
    Intent serviceIntent;
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event){

		if ((keyCode == KeyEvent.KEYCODE_BACK)
				&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
			if(KEYCODE_BACK_FIRST && curView.equals(layHome)){
				temptime = System.currentTimeMillis();
				KEYCODE_BACK_FIRST = false;
				Toast.makeText(this, "请再按一次返回退出", Toast.LENGTH_SHORT).show();
				return true;
			}
			if (System.currentTimeMillis() - temptime < 2000 && curView.equals(layHome)){
				finish();
				System.exit(0); 
			}else{
				KEYCODE_BACK_FIRST = true;
				layHome.performClick();
				
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
    protected void onDestroy() {
    	super.onDestroy();
    	try {
    		stopService(serviceIntent);
    		if(popVideoChoiceWin != null) popVideoChoiceWin.dismiss();
	    	if(mapViews !=null)
	    		for(String key:mapViews.keySet())
	    			manager.destroyActivity(key, true);
    	}catch(Exception e) {
    		
    	}
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		mainObserver = this;
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		//PollingUtils.startPollingService(this, 5, MessageService.class, MessageService.ACTION);
		
		
		layHome = (RelativeLayout)this.findViewById(R.id.lay_home);
		laySearch = (RelativeLayout)this.findViewById(R.id.lay_search);
		layVideo = (RelativeLayout)this.findViewById(R.id.lay_video);
		layMessage = (RelativeLayout)this.findViewById(R.id.lay_message);
		layMe = (RelativeLayout)this.findViewById(R.id.lay_me);
		container = (LinearLayout)findViewById(R.id.main_container);
		redPointer = (ImageView) findViewById(R.id.img_tippoint);
		
		
		layHome.setOnClickListener(this);
		laySearch.setOnClickListener(this);
		layVideo.setOnClickListener(this);
		layMessage.setOnClickListener(this);
		layMe.setOnClickListener(this);
		
		manager = new LocalActivityManager(this , true);
        manager.dispatchCreate(savedInstanceState);
        curView = layHome;
        curView.setBackgroundColor(Color.parseColor("#5fb336"));
		layHome.performClick();
		serviceIntent = new Intent(this, MessageService.class);
		startService(serviceIntent);
		
	}

    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	try {
    		manager.dispatchResume();
    	} catch(Exception e) {
    		
    	}
    }
    
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.e(TAG,"RETURN ->" +resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View view) {
		if(view.getId() != curView.getId() && view.getId() != R.id.lay_video) {
			view.setBackgroundColor(Color.parseColor("#5fb336"));
			curView.setBackgroundColor(Color.parseColor("#f2f2f2"));
			curView = (RelativeLayout) view;
			
		}
		Window win;
		
		switch(view.getId()) {
		
			case R.id.lay_home:
				Log.v(TAG,"home page");
				container.removeAllViews();
				if(mapViews.containsKey("IndexActivity")) {
					View vw = mapViews.get("IndexActivity");
					((IndexActivity)mapWins.get("IndexActivity").getContext()).startAnimation();
					container.addView(vw);
				} else {
					win = manager.startActivity("IndexActivity",new Intent(MainActivity.this, IndexActivity.class));
					View vw = win.getDecorView();
					mapViews.put("IndexActivity",vw);
					mapWins.put("IndexActivity", win);
					container.addView(vw);
				}
				break;
				
			case R.id.lay_search:
				container.removeAllViews();
				
				Log.v(TAG,"search page");
				if(mapViews.containsKey("SearchActivity")) {
					View vw = mapViews.get("SearchActivity");
					((SearchActivity)mapWins.get("SearchActivity").getContext()).startAnimation();
					container.addView(vw);
					
					
				} else {
					win = manager.startActivity("SearchActivity",new Intent(MainActivity.this, SearchActivity.class));
					View vw = win.getDecorView();
					mapViews.put("SearchActivity",vw);
					mapWins.put("SearchActivity",win);
					container.addView(vw);
				}
				//progress.setVisibility(View.GONE);
				break;
				
			case R.id.lay_video:
				Log.v(TAG,"video page");
				if(MainApplication.currentUser == null) {
					Intent intent = new Intent(this, LoginActivity.class);
					startActivityForResult(intent, 200);
					
					return;
				}
				final View choiceView = getLayoutInflater().inflate(R.layout.pop_choice_video, null);
				if(popVideoChoiceWin !=null && popVideoChoiceWin.isShowing()) return;
				popVideoChoiceWin = new WindowUtils(this,choiceView);
				choiceView.setOnTouchListener(new OnTouchListener() {
	
		            @Override
		            public boolean onTouch(View v, MotionEvent event) {
	
		                Log.i(TAG, "onTouch");
		                int x = (int) event.getX();
		                int y = (int) event.getY();
		                Rect rect = new Rect();
		                choiceView.findViewById(R.id.popup_window).getGlobalVisibleRect(rect);
		                if (!rect.contains(x, y)) {
		                	popVideoChoiceWin.dismiss();
		                }
	
		                return false;
		            }
		        });
				OnClickListener click = new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent;
						switch(view.getId()) {
						case R.id.choice_operation:
							popVideoChoiceWin.dismiss();
							intent = new Intent();
							intent.setClass(MainActivity.this, LocalVideoActivity.class);
							startActivity(intent);
							break;
						case R.id.choice_record:
							popVideoChoiceWin.dismiss();
							intent = new Intent();
							intent.setClass(MainActivity.this, MediaRecorderActivity.class);
							startActivity(intent);
							break;
						
						}
						
					}
				};
				popVideoChoiceWin.showAtLocation(findViewById(R.id.main_index), Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
				choiceView.findViewById(R.id.choice_operation).setOnClickListener(click);
				choiceView.findViewById(R.id.choice_record).setOnClickListener(click);
				break;
				
			case R.id.lay_message:
				container.removeAllViews();
				Log.v(TAG,"message page");
				if(mapViews.containsKey("MessageActivity")) {

					View vw = mapViews.get("MessageActivity");
					((MessageActivity)mapWins.get("MessageActivity").getContext()).startAnimation();
					container.addView(vw);
					
				} else {
					win = manager.startActivity("MessageActivity",new Intent(MainActivity.this, MessageActivity.class));
					View vw = win.getDecorView();
					mapViews.put("MessageActivity",vw);
					mapWins.put("MessageActivity", win);
					container.addView(vw);
				}
				redPointer.setVisibility(View.GONE);
				break;
				
			case R.id.lay_me:

				container.removeAllViews();
				Log.v(TAG,"me page");
				if(mapViews.containsKey("PersonalCenterActivity"))
					container.addView(mapViews.get("PersonalCenterActivity"));
				else {
					win = manager.startActivity("PersonalCenterActivity",new Intent(MainActivity.this, PersonalCenterActivity.class));
					View vw = win.getDecorView();
					mapViews.put("PersonalCenterActivity",vw);
					mapWins.put("PersonalCenterActivity", win);
					container.addView(vw);
				}
				break;
				
			default:
				break;
		}
		
		
	}

	@Override
	public void update(Observable observable, Object object) {
		Log.i(TAG,"update from observer by main activity");
		super.update(observable, object);
		switch((Integer)object) {
			case ConstUtil.VIDEO_PUBLISHED:
				layMe.performClick();
				break;
			case ConstUtil.MESSAGE_FLAG:
				if(curView.getId() != R.id.lay_message && redPointer.getVisibility() == View.GONE)
					redPointer.setVisibility(View.VISIBLE);
				break;
		}
	}

	
	
}
