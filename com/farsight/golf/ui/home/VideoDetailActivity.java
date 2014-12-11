package com.farsight.golf.ui.home;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleDiscussAdapter;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.business.HotPointerBusiness;
import com.farsight.golf.business.VideoBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.user.UserPageActivity;
import com.farsight.golf.ui.video.VideoPlayerActivity;
import com.farsight.golf.util.WindowUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.loopj.android.http.RequestParams;
import com.yixia.camera.util.StringUtils;

public class VideoDetailActivity extends ActivityAbstract implements OnClickListener {
	static String TAG = "VideoDetailActivity";
	TextView userNameTv, playsNumTv, describTv, likesTv;
	Button replyBtn;
	ImageView videoIv, playIv, portalIv;
	EditText replyContentEt;
	ListView commLv;
	List<Map<String, Object>> listComm = new ArrayList<Map<String, Object>>();
	BaseAdapter commAdapter;
	LayoutInflater inflater;
	View headView;
	InputMethodManager imm;
	JSONObject jsonMap;
	Map curDelMap;
	String uri;
	View replyView, deleteLine;
	int pos_g;
	PopupWindow popupWindow, deletPop;
	String strMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_detail_activity);
		try {
			strMap  = getIntent().getStringExtra("mapItem");
			
			if(strMap != null && strMap.length() > 0) {
				jsonMap =  new JSONObject(strMap);
				
			}
		}catch(Exception e) {
			Log.e(TAG,e.toString());
		}
		init();
		
		
	}
	
	private void init() {
		
		inflater = this.getLayoutInflater();
		commLv = (ListView) findViewById(R.id.video_des_lv);
		
		if(jsonMap!=null) {
			
			initVideoInfo();
		}
		
	}
	
	private void initComment() {
		uri = "comments/";
		RequestParams params = new RequestParams();
		try {
			params.put("post_id", jsonMap.get("post_id"));
			VideoBusiness.getComments(uri, params, this, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					int resource = R.layout.video_discuss_list;
					JSONArray jsonArray;
					JsonArray jArray;
					if(msg.what == 0) {
						jsonArray = (JSONArray) msg.obj;
						if( jsonArray.length() > 0) {
							jArray = jsonParser.parse(msg.obj.toString()).getAsJsonArray();
							listComm = (List<Map<String, Object>>) gson.fromJson(gson.toJson(jArray), listComm.getClass());
						}
						commAdapter = new SimpleDiscussAdapter(VideoDetailActivity.this,resource,listComm);
						commLv.setAdapter(commAdapter);
					} else {
						alertView(msg.obj.toString());
					}
				}
			});
		}catch(Exception e) {
			
		}
	}
	
	OnClickListener click = new OnClickListener() {
		
		@Override
		public void onClick(View view) {

			String uri;
			Intent intent;
			try {
				
				switch(view.getId()) {
					case R.id.hot_line_share://分享
						((ActivityAbstract)VideoDetailActivity.this).openShare();
						break;
					
					case R.id.hot_line_comm://点赞
						Log.v(TAG,"the hot_comm_lay");
					
						LinearLayout commLay = (LinearLayout) view;
						final Button commBtn = (Button) commLay.findViewById(R.id.hot_commend_btn);
						final TextView commTxt = (TextView) commLay.findViewById(R.id.hot_commend_txt);
						JSONObject params = new JSONObject();
					
						params.put("post_id", jsonMap.get("post_id"));
						if(jsonMap.get("commend")==null) {
							uri = "likes/";
						} else {
							uri = "likes/undo";
							
						}
						StringEntity entity = new StringEntity(params.toString(),"utf-8");
						HotPointerBusiness.like(uri, entity, VideoDetailActivity.this, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								try {
									switch(msg.what) {
									case 0:
										
										if(jsonMap.isNull("commend")) jsonMap.put("commend", true);
										else jsonMap.remove("commend");
										commBtn.setBackgroundResource(jsonMap.isNull("commend")?
												R.drawable.comm:R.drawable.comm_pressed);
										commTxt.setText(jsonMap.isNull("commend")?
												"点赞":"取消");
										Toast.makeText(VideoDetailActivity.this, jsonMap.isNull("commend")?
												"取消点赞":"点赞成功", 1000).show();
										break;
									case 1:
										Toast.makeText(VideoDetailActivity.this, msg.obj.toString(), 1000).show();;
										break;
										
										default:break;
									}
								}catch(Exception e) {
									Log.e(TAG, e.toString());
								}
								super.handleMessage(msg);
							}
						});
						break;
					
					
					case R.id.hot_playBtn:
						Log.v(TAG,"the hot_playBtn");
						String mVideoPath = jsonMap.get("video").toString();
						
						if(StringUtils.isNotEmpty(mVideoPath)) {
							uri = "posts/play/" + jsonMap.get("post_id").toString();
							VideoBusiness.playTimes(uri, VideoDetailActivity.this, new Handler() {
								@Override
								public void handleMessage(Message msg) {
									Log.i(TAG,msg.obj.toString());
								}
							});
							intent = new Intent(VideoDetailActivity.this, VideoPlayerActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("path", mVideoPath);
							startActivity(intent);
						}
						break;
						
					case R.id.hot_portal:
						intent = new Intent();
						intent.putExtra("user", strMap);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(VideoDetailActivity.this, UserPageActivity.class);
						startActivity(intent);
						break;
					
					case R.id.hot_name:
						intent = new Intent();
						intent.putExtra("user", jsonMap.get("post_id").toString());
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(VideoDetailActivity.this, UserPageActivity.class);
						startActivity(intent);
						break;
						
					case R.id.hot_line_more://更多
						Log.v(TAG,"the hot_line_more");
						
						final View moreView = getLayoutInflater().inflate(R.layout.pop_window, null);
						final WindowUtils popWin = new WindowUtils(VideoDetailActivity.this,moreView);
						
						OnClickListener click = new OnClickListener() {
							
							@Override
							public void onClick(View view) {
								try {
									switch(view.getId()) {
									case R.id.cancel:
										popWin.dismiss();
										break;
									case R.id.copy_url:
										copy(jsonMap.get("shareurl").toString(),VideoDetailActivity.this);
										Toast.makeText(VideoDetailActivity.this, "复制URL成功", Toast.LENGTH_LONG).show();
										popWin.dismiss();
										break;
									case R.id.delete:
										/*Log.e(TAG,"delete post");
										popWin.dismiss();
										String deleteuri = "posts/" + jsonMap.getString("post_id").toString();
										
										try {
											HotPointerBusiness.delete(deleteuri,null,VideoDetailActivity.this,new Handler() {
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
										break;*/
									}
								}catch(Exception e) {
									Log.d(TAG,e.toString());
								}
							}
						};
						
						moreView.findViewById(R.id.cancel).setOnClickListener(click);
						moreView.findViewById(R.id.copy_url).setOnClickListener(click);
						View delView = moreView.findViewById(R.id.delete);
						/*if(MainApplication.currentUser != null && MainApplication.currentUser.get("id").toString().
								equalsIgnoreCase(jsonMap.get("user_id").toString()))
							delView.setVisibility(View.VISIBLE);
						else */
							delView.setVisibility(View.GONE);
						//delView.setOnClickListener(click);
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
						break;	
					
					
				}
			}catch(Exception e) {
				Log.e(TAG, e.toString());
			}
					
		}
		
	};
	private void initVideoInfo() {
		try {
			
			headView = (LinearLayout) inflater.inflate(R.layout.hot_list, null);
			headView.findViewById(R.id.hot_line_diss).setOnClickListener(this);
			headView.findViewById(R.id.hot_line_share).setOnClickListener(click);
			headView.findViewById(R.id.hot_line_comm).setOnClickListener(click);
			headView.findViewById(R.id.hot_playBtn).setOnClickListener(click);
			headView.findViewById(R.id.hot_line_more).setOnClickListener(click);
			
			commLv.addHeaderView(headView);
			userNameTv = (TextView) headView.findViewById(R.id.hot_name);
			playsNumTv = (TextView) headView.findViewById(R.id.hot_play);
			describTv = (TextView) headView.findViewById(R.id.hot_describe);
			likesTv = (TextView) headView.findViewById(R.id.hot_comm);
			videoIv = (ImageView) headView.findViewById(R.id.hot_video);
			playIv = (ImageView) headView.findViewById(R.id.hot_playBtn);
			portalIv = (ImageView) headView.findViewById(R.id.hot_portal);
			
			userNameTv.setText(jsonMap.get("nickname").toString());
			playsNumTv.setText(jsonMap.get("plays").toString());
			describTv.setText(jsonMap.get("post_content").toString());
			
			likesTv.setText(jsonMap.get("likes") + "个赞");
			
			commLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
					try {
						
						pos_g = adapter.getPositionForView(view);
						if(pos_g == 0) return;
						pos_g--;
						Map curMap = listComm.get(pos_g);//include head view
						if(!MainApplication.currentUser.get("id").toString().equalsIgnoreCase(curMap.get("user_id").toString()))
							showReplyWin(curMap);
						else 
							showDeleteWin(curMap);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		   
			try {
				if( jsonMap.get("video_img")!= null && StringUtils.isNotEmpty( jsonMap.get("video_img").toString())) {
					AsyncImageLoader imageLoader = new AsyncImageLoader(this);
					Bitmap bitMap = imageLoader.loadImage(videoIv, jsonMap.get("video_img").toString(), new ImageDownloadedCallBack() {
			
						@Override
						public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
							imageView.setImageBitmap(bitmap);
							
						}
						
					});
					if(bitMap != null) {
						videoIv.setImageBitmap(bitMap);
					}
				
				}
				if( jsonMap.get("user_img")!= null && StringUtils.isNotEmpty( jsonMap.get("user_img").toString())) {
					AsyncImageLoader imageLoader = new AsyncImageLoader(this);
					Bitmap bitMap = imageLoader.loadImage(portalIv, jsonMap.get("user_img").toString(), new ImageDownloadedCallBack() {
			
						@Override
						public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
							imageView.setImageBitmap(bitmap);
							
						}
						
					});
					if(bitMap != null) {
						portalIv.setImageBitmap(bitMap);
					}
				
				}
				commLv.post(new Runnable() {
					
					@Override
					public void run() {
						showReplyWin(null);
						
					}
				});
			}catch(Exception e) {
				Log.e(TAG,e.getMessage());
			}
			
			initComment();
		
		}catch(Exception e) {
				System.out.println(e.toString());
		}
	}
	
	private void popupInputMethodWindow() {  
		commLv.postDelayed(new Runnable() {  
	        @Override  
	        public void run() {  
	            imm = (InputMethodManager) VideoDetailActivity.this.getSystemService(Service.INPUT_METHOD_SERVICE);  
	            
	            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	        }  
	    }, 0);  
	}
	
	private void showDeleteWin(Map curMap) {
		if(deletPop == null) {
			deleteLine = inflater.inflate(R.layout.delete_reply_dialog, null);//new LinearLayout(this);
			deletPop = new PopupWindow(deleteLine,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);  
			deletPop.setFocusable(true);  
			deletPop.setOutsideTouchable(true);  
			deletPop.setAnimationStyle(R.style.AnimBottom);
			ColorDrawable dw = new ColorDrawable(0xffffffff);
			deletPop.setBackgroundDrawable(dw);
			deleteLine.findViewById(R.id.btn_delete).setOnClickListener(this);
			deleteLine.findViewById(R.id.btn_cancel).setOnClickListener(this);
			
		}
			deletPop.showAtLocation(this.findViewById(R.id.reply_main), Gravity.BOTTOM, 0, 0);
			curDelMap = curMap;
		
		
	}
	@SuppressLint("NewApi")
	private void showReplyWin(Map curMap) {
		if(popupWindow == null) {
			replyView = inflater.inflate(R.layout.commend_dialog, null);
			replyBtn = (Button) replyView.findViewById(R.id.reply_sendBt);
			replyBtn.setOnClickListener(this);
			replyContentEt = (EditText) replyView.findViewById(R.id.reply_content);
			
			popupWindow = new PopupWindow(replyView);  
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setFocusable(true);  
			popupWindow.setOutsideTouchable(false);  
			ColorDrawable dw = new ColorDrawable(0xffffffff);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);  
			popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  
			
			
		} 
			popupWindow.showAtLocation(headView, Gravity.BOTTOM, 0, 0);
			popupInputMethodWindow();
			if(curMap != null) {
				try {
				String replyCnt = String.format("回复：%s", curMap.get("nickname").toString());
				replyContentEt.setHint(replyCnt);
				replyContentEt.setText(replyCnt);
				replyContentEt.setSelection(replyCnt.length());
				replyContentEt.setFocusable(true);
				}catch(Exception e) {
					Log.e(TAG, e.toString());
				}
			}
	}
	
	@Override
	public void onClick(View view) {
		try {
			switch(view.getId()) {
			case R.id.reply_sendBt:
				popupWindow.dismiss();
				uri = "comments/";
				String replyContent = "",replyFor,replyFors[];
				replyContent = replyContentEt.getText().toString();
				replyFor = replyContentEt.getHint().toString();
				replyFors = replyFor.split(":");
				if(replyFors.length == 2)
					replyContent = "回复 :<font color='green'>" + replyFors[1] + "</font>";
				JSONObject params = new JSONObject();
				params.put("post_id", jsonMap.get("post_id"));
				params.put("content", replyContent);
				params.put("video", "");
				params.put("video_img", "");
				StringEntity entity = new StringEntity(params.toString(),"utf-8");
				VideoBusiness.sendComments(uri, entity, this, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						replyContentEt.setText("");
						if(msg.what == 0) {
							listComm.clear();
							initComment();
						} else {
							alertView(msg.obj.toString());
						}
					}
				});
				break;
			case R.id.dis_lay:
				showReplyWin(null);
				break;
				
			case R.id.hot_line_diss:
				showReplyWin(null);
				break;
				
			case R.id.btn_cancel:
				deletPop.dismiss();
				break;
			
				
			case R.id.btn_delete:
				uri = "comments/" + curDelMap.get("comment_id");
				VideoBusiness.deleteComments(uri, this, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if(msg.what == 0) {
							listComm.remove(pos_g);
							commAdapter.notifyDataSetChanged();
							commLv.invalidate();
						} else {
							alertView(msg.obj.toString());
						}
						deletPop.dismiss();
					}
				});
				break;
				
				default:
					break;
			}
		}catch(Exception e) {
			alertView(e.toString());
		}
		
	}
	
}
