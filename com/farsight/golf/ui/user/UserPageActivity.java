package com.farsight.golf.ui.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleListAdapter;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.business.PersonalInfoBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.LoginActivity;
import com.farsight.golf.ui.component.XListView;
import com.farsight.golf.ui.component.XListView.IXListViewListener;
import com.farsight.golf.ui.personal.PersonalCenterActivity;
import com.farsight.golf.ui.video.VideoPlayerActivity;
import com.farsight.golf.util.ConstUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;
import com.yixia.camera.util.StringUtils;

public class UserPageActivity extends ActivityAbstract implements
		OnClickListener,IXListViewListener {
	
	
	static final int WHAT_INIT_USER = 0,WHAT_START_UI=1,WHAT_NEW_LIST=2,
			WHAT_LIKE_LIST=3,WHAT_CARE_USER=4;
	
	static String TAG = "UserPageActivity";
	JSONObject videoItem;
	JSONObject person;
	
	int ivCursorWidth, tabWidth, offsetX;
	TextView careNumTv, funsTv, likeTv, userNameTv,
			introducTv;
	ImageView portalIv;
	Button careBtn;
	RelativeLayout idenLay;
	
	LayoutInflater inflater;
	
	LinearLayout userPageHead;
	
	EditText searchEdit;
	RelativeLayout toolbar;

	final int RIGHT = 0;
	final int LEFT = 1;
	final int TOP = 2;
	final int DOWN = 3;
	Handler handler;
	XListView lvNewPub;
	SimpleListAdapter adapterNewPub;
	ProgressDialog dialog = null;
	private AsyncImageLoader imageLoader;
	private String imgUrlStuff = "";
	String userId = "";
	
	List<Map<String, Object>> listNewPub = new ArrayList<Map<String, Object>>(),
			listAdmit = new ArrayList<Map<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_page);
		init();
		

	}
	
	

	private void init() {
		toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		userNameTv = (TextView) findViewById(R.id.user_name);
		
		try {
			videoItem  = new JSONObject(getIntent().getStringExtra("user"));
			userId = videoItem.getString("user_id");
		}catch(Exception e) {
			return;
		}
		inflater = getLayoutInflater();
		userPageHead = (LinearLayout) inflater.inflate(R.layout.user_page_head, null);
		careNumTv = (TextView) userPageHead.findViewById(R.id.care_txt_num);
		funsTv = (TextView) userPageHead.findViewById(R.id.funs_txt_num);
		likeTv = (TextView) userPageHead.findViewById(R.id.comm_txt_num);
		introducTv = (TextView) userPageHead.findViewById(R.id.hot_describe);
		careBtn = (Button) userPageHead.findViewById(R.id.iscare_btn);
		careBtn.setOnClickListener(this);
		portalIv = (ImageView) userPageHead.findViewById(R.id.hot_portal);
		idenLay = (RelativeLayout) userPageHead.findViewById(R.id.idenfi_teach_lay);


		imageLoader = new AsyncImageLoader(this);
		
		lvNewPub = (XListView) findViewById(R.id.lv_new_publish);
		lvNewPub.setPullLoadEnable(true);
		lvNewPub.setXListViewListener(this);
		
		lvNewPub.addHeaderView(userPageHead);
		initUser();
	}


	
	@Override
	public void onClick(View view) {
		
		final Map map;
		int pos = 0;
		switch (view.getId()) {
			case R.id.iscare_btn:
				careUser(view);
				break;
			case R.id.hot_playBtn:
				Log.v(TAG,"the hot_playBtn");
		
				pos = lvNewPub.getPositionForView(view);
				map = listNewPub.get(pos);
				
				String mVideoPath = map.get("video").toString();
				if(StringUtils.isNotEmpty(mVideoPath))
					startActivity(new Intent(this, VideoPlayerActivity.class).putExtra("path", mVideoPath));
				break;
			
		default:
			break;
		}
		

	}

	
	public void careUser(View view) {
		try {
			if(MainApplication.currentUser == null) {
				Intent intent = new Intent(this, LoginActivity.class);
				intent.setFlags(ConstUtil.USER_LOGIN_FLAG);
				startActivityForResult(intent, 200);
				return;
			} else if(MainApplication.currentUser.getString("id").equalsIgnoreCase(userId)) {
				alertView("不能关注自己");
				return;
			}
			JSONObject params = new JSONObject();
			
				String uri;
				if(view.getTag().toString().equals("0")) 
					uri = "follows/";
				else
					uri = "follows/undo";
				params.put("user_id", userId);
				StringEntity entity = new StringEntity(params.toString(),"utf-8");
				PersonalInfoBusiness.careUser(uri, entity, this, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						try {
							if(msg.what == WHAT_CARE_USER) {
								if(careBtn.getTag().toString().equalsIgnoreCase("0")) {
									Toast.makeText(UserPageActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
									careBtn.setText("取消关注");
									careBtn.setTag(1);
									person.put("is_following", 1);
									person.put("followings", person.getInt("followings") + 1);
									careBtn.setBackgroundResource(R.drawable.auth_code_btn_enable);
								} else {
									Toast.makeText(UserPageActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
									careBtn.setText("关注");
									careBtn.setTag(0);
									person.put("is_following", 0);
									person.put("followings", person.getInt("followings") - 1);
									careBtn.setBackgroundResource(R.drawable.auth_code_btn);
								}
								careNumTv.setText(person.getString("followings"));
							} else {
								alertView(msg.obj.toString());
							}
								
						}catch(Exception e) {
							Log.e(TAG,e.toString());
						}
					}
				}, WHAT_CARE_USER);
		}catch(Exception e) {
			Log.e(TAG,e.toString());
			
		}
	}
	private void updateItemDate(final int opt) {
		
		RequestParams params;
		String uri;
		uri = "posts/";
		params = new RequestParams();
		params.put("type", "latest");
		params.put("cate_id", "");
		params.put("tag", "");
		params.put("search_key", "");
		params.put("user_id", userId);
		params.put("direction", "");
		params.put("last_post_id", "");
		params.setContentEncoding("utf-8");
		PersonalInfoBusiness.getNewVideo(uri, params, UserPageActivity.this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int resource;
				JSONArray jsonArray;
				JsonParser parser = new JsonParser();
				JsonArray jArray;
				jsonArray = (JSONArray) msg.obj;
				List<Map<String, Object>> listRslt = new ArrayList<Map<String, Object>>();
				if (jsonArray.length() > 0) {
					jArray = parser.parse(msg.obj.toString()).getAsJsonArray();
					listRslt.clear();
					listRslt = (List<Map<String, Object>>) gson.fromJson(gson.toJson(jArray), listNewPub.getClass());
				}
				if(adapterNewPub == null){
					resource = R.layout.hot_list;
					listNewPub.addAll(listRslt);
					adapterNewPub = new SimpleListAdapter(UserPageActivity.this, listNewPub, resource);
					lvNewPub.setAdapter(adapterNewPub);
					
				} else if(IS_REFRESH == opt) {
					listNewPub.clear();
					listNewPub.addAll(listRslt);
					
				} else if(IS_LOADING == opt) {
					listNewPub.addAll(listRslt);
					
				}
				adapterNewPub.notifyDataSetChanged();
				onLoad();
				
			}
		}, WHAT_NEW_LIST);
	}

	private void initUser() {
		dialog = ProgressDialog.show(UserPageActivity.this, "", "数据加载中......", true, true);
		String uri = "users/" + userId;
		RequestParams params = new RequestParams();
		params.setContentEncoding("utf-8");
		PersonalInfoBusiness.executeGet(uri, params, UserPageActivity.this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					if(msg.what == 0) {
						Log.d(TAG, msg.obj.toString());
						person = (JSONObject) msg.obj;
						careNumTv.setText(person.get("followings").toString());
						funsTv.setText(person.get("followers").toString());
						likeTv.setText(person.get("likes").toString());
						userNameTv.setText(person.get("fullname").toString());
						introducTv.setText(person.get("introduce").toString());
						careBtn.setText(person.get("is_following").toString().
								equalsIgnoreCase("0")?"关注":"取消关注 ");
						careBtn.setBackgroundResource(person.get("is_following").toString().
								equalsIgnoreCase("0")?R.drawable.auth_code_btn:R.drawable.auth_code_btn_enable);
						careBtn.setTag(person.get("is_following"));
						
						idenLay.setVisibility(person.get("level").toString()
								.equalsIgnoreCase("0") ? View.GONE
								: View.VISIBLE);
						String imgPortalUrl = person.get("user_img").toString();
						if (imgPortalUrl != null && !imgPortalUrl.equals("")) {
							imgPortalUrl = imgUrlStuff + imgPortalUrl;

							Bitmap bitmap = imageLoader.loadImage(portalIv,	imgPortalUrl,
									new ImageDownloadedCallBack() {

										@Override
										public void onImageDownloaded(
												ImageView imageView,
												Bitmap bitmap) {
											imageView.setImageBitmap(bitmap);

										}
									});

							if (bitmap != null) {
								portalIv.setImageBitmap(bitmap);

							}
						}
						initPage();
					} else {
						alertView(msg.obj.toString());
					}
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				}
			}
		});
	}
	
	private void initPage() {
		
		updateItemDate(IS_REFRESH);
		
	}



	@Override
	public void onRefresh() {
		updateItemDate(IS_REFRESH);
		
	}



	@Override
	public void onLoadMore() {
		updateItemDate(IS_LOADING);
		
	}
	
	private void onLoad() {
		if(dialog != null)
			dialog.dismiss();
		lvNewPub.stopRefresh();
		lvNewPub.stopLoadMore();
		lvNewPub.setRefreshTime("刚刚");

		
	}
}
