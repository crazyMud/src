package com.farsight.golf.ui.personal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleListAdapter;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.business.Configuration;
import com.farsight.golf.business.PersonalInfoBusiness;
import com.farsight.golf.business.VideoBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.LoginActivity;
import com.farsight.golf.ui.MainActivity;
import com.farsight.golf.ui.home.IndexActivity;
import com.farsight.golf.ui.setting.SettingActivity;
import com.farsight.golf.ui.video.VideoPlayerActivity;
import com.farsight.golf.util.ConstUtil;
import com.farsight.golf.util.GlobalVar;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;
import com.yixia.camera.util.StringUtils;



public class PersonalCenterActivity extends ActivityAbstract implements OnClickListener {
	static String TAG = "PersonalCenterActivity";
	protected static String url = new String("");
	Button editBtn;
	View currentView, loadView;
	TextView typeTxt, nameTxt, careTv, funsTv, likeTv, aboutTv, authTv;
	ImageView imgPortral;
	AsyncImageLoader imageLoader;
	public static PersonalCenterActivity perObserver;
	RelativeLayout toolbar, innerToolbar;
	LinearLayout [] lays;
	int[] resource;
	ListView listView;
	SimpleListAdapter adapter;
	List<Map<String,Object>> dataVideo = new ArrayList<Map<String,Object>>();
	int listResource;
	DestoryReceiver receiver;
	JSONObject cuser;
	String uri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		perObserver = this;
		setContentView(R.layout.activity_personal_me);
		receiver = new DestoryReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				initActivityPage();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalVar.UPDATE_ACTIVITY);
		registerReceiver(receiver, filter);
		imageLoader = new AsyncImageLoader(this);
		editBtn = (Button) findViewById(R.id.edit_btn);
		if(MainApplication.currentUser == null)
			editBtn.setText("点击登录");
		toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		nameTxt = (TextView) findViewById(R.id.name_txt);
		imgPortral = (ImageView) findViewById(R.id.hot_portal);
		careTv = (TextView) findViewById(R.id.care_txt_num);
		funsTv = (TextView) findViewById(R.id.funs_txt_num);
		likeTv = (TextView) findViewById(R.id.comm_txt_num);
		aboutTv = (TextView) findViewById(R.id.hot_describe);
		authTv = (TextView) findViewById(R.id.auth_txt);
		
		initActivityPage();
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
		/*
	 * 加载用户信息
	 */
	public void initActivityPage() {
		
		cuser = MainApplication.currentUser;
		if(cuser != null) {
			
			try {
				String name = cuser.getString("fullname");
				String portalUrl = url + cuser.getString("user_img");
				nameTxt.setText(name);
				careTv.setText(cuser.getString("followings"));
				funsTv.setText(cuser.getString("followers"));
				likeTv.setText(cuser.getString("likes"));
				aboutTv.setText(cuser.getString("introduce"));
				authTv.setVisibility(cuser.getString("level").equalsIgnoreCase("0")?View.VISIBLE:View.GONE);
				if(cuser.getString("level").equalsIgnoreCase("0")) {
					aboutTv.setVisibility(View.VISIBLE);
					aboutTv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				if(!TextUtils.isEmpty(portalUrl)) {
					Bitmap cachedImage = imageLoader.loadImage(imgPortral, portalUrl, new ImageDownloadedCallBack(){

						@Override
						public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
							if (imageView != null) {
								imgPortral.setScaleType(ImageView.ScaleType.CENTER_CROP);
								imgPortral.setImageBitmap(bitmap);

							}
							
						}
						
					});
					if(cachedImage != null) {
						imgPortral.setImageBitmap(cachedImage);
					}
				}
				
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			nameTxt.setText("点击登录");
			nameTxt.setOnClickListener(this);
		}
		
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		resource = new int[]{R.id.video_send, R.id.video_friend, R.id.video_comm, R.id.video_comment,
				/* R.id.video_share,*/
					/*R.id.video_down/*, R.id.video_question, R.id.video_ask*/};
		lays = new LinearLayout[resource.length];
		for(int i=0;i<resource.length;i++) {
			lays[i] = (LinearLayout) findViewById(resource[i]);
			lays[i].setOnClickListener(this);
			
		}
		editBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(MainApplication.currentUser == null) {
					Intent intent = new Intent(PersonalCenterActivity.this, LoginActivity.class);
					intent.setFlags(ConstUtil.USER_LOGIN_FLAG);
					startActivityForResult(intent, 200);
					
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("edit", true);
				intent.setClass(PersonalCenterActivity.this, PersonalInfoActivity.class);
				startActivity(intent);
				
			}
		});
		
		currentView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG,"RETURN");
	}
	
	public void Setting(View view) {
		if(MainApplication.currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(ConstUtil.USER_LOGIN_FLAG);
			startActivity(intent);
			return;
		}
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View view) {
		
		if(MainApplication.currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(ConstUtil.USER_LOGIN_FLAG);
			startActivity(intent);
			return;
		}
		
	
		try {
		
			Intent intent = new Intent(this,PersonalTypeVideoActivity.class);
			switch(view.getId()) {
				case R.id.video_send:
					intent.putExtra("title","我发布的视频");
					intent.putExtra("metype", "me");
					break;
					
				case R.id.video_friend:
					intent.putExtra("title","我好友的视频");
					intent.putExtra("metype", "friend");
					break;
					
				case R.id.video_comm:
					intent.putExtra("title","我点赞的视频");
					intent.putExtra("metype", "like");
					break;
					
				case R.id.video_comment:
					intent.putExtra("title","我评论的视频");
					intent.putExtra("metype", "comment");
					break;

			/*	case R.id.video_down:
					intent.putExtra("title","我下载的视频");
					intent.putExtra("metype", "download");
					break;*/
					

			}
			startActivity(intent);
			
			
			
		}catch(Exception e) {
			Log.e(TAG,e.getMessage());
		}
		
		
	}
	

	@Override
	public void update(Observable observable, Object object) {
		Log.i(TAG,"update from observer by personal center");
		super.update(observable, object);
		initActivityPage();
	}
	
}
