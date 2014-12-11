package com.farsight.golf.ui.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.adapter.SimpleListAdapter;
import com.farsight.golf.adapter.SimplePagerAdapter;
import com.farsight.golf.adapter.SimpleTypeListAdapter;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.business.HotPointerBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.MainActivity;
import com.farsight.golf.ui.component.HotHeadView;
import com.farsight.golf.ui.component.XListView;
import com.farsight.golf.ui.component.XListView.IXListViewListener;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.WindowUtils;
import com.loopj.android.http.RequestParams;


public class IndexActivity extends ActivityAbstract implements OnClickListener,OnPageChangeListener,IXListViewListener  {
	static String TAG = "IndexActivity";

	LinearLayout linearLayout;
	View popviewLayout;
	LayoutInflater layoutInflater;
	Button enterBtn;
	TextView adviseTxt;
	int mViewCount;
	ImageView[] mImageViews;
	XListView listHot,listNews,listType;
	Scroller mScroller;
	int heightHot,heightNews,heightType;
	BaseAdapter adapterHot,adapterNews,adapterType;
	
	int CUR_OPT;
    List<Map<String,Object>> dataHot = new ArrayList<Map<String,Object>>(),
    		dataNews = new ArrayList<Map<String,Object>>(),
    		dataType = new ArrayList<Map<String,Object>>();
    
    List<String> adviseList = new ArrayList<String>();
    SimplePagerAdapter pagerAdapter;
    ProgressDialog progressDialog = null;
    
    Button refreshBtn;
    ViewGroup.LayoutParams vparams;
    HotHeadView hotHeadView;
    LinearLayout noneViewHot,noneViewNews,noneViewType,viewHot,viewNews,viewType;
    /*
     * 
     */
    AsyncImageLoader ImageLoader;
    RelativeLayout toolbar;
	private ViewPager mPager;//页卡内容
    private List<View> listViews = new ArrayList<View>(); // Tab页面列表
    List<TextView> listTabs = new ArrayList<TextView>(); // 文本
    
    
    private TextView txtHot, txtNew, txtType, txtCurrent;// 页卡头标
   
    private int currIndex = 0/* 当前页卡编号*/,ivCursorWidth/* 动画图片宽度*/,tabWidth/* 每个tab头的宽度*/,offsetX;// tab头的宽度减去动画图片的宽度再除以2（保证动画图片相对tab头居中）

    private ImageView ivCursor;//下划线图片
    
    private boolean loading = true;
    
    Map map_g;
	int pos_g = 0;
	
	int lastX,lastY;
    
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_index);
		init();
		
		
	}

	@SuppressLint("NewApi")
	private void init() {
		
		txtHot = (TextView)IndexActivity.this.findViewById(R.id.main_hotTxt);
		txtCurrent = txtHot;
		txtNew = (TextView)IndexActivity.this.findViewById(R.id.main_newTxt);
		txtType = (TextView)IndexActivity.this.findViewById(R.id.main_typeTxt);
		ivCursor = (ImageView)IndexActivity.this.findViewById(R.id.cursor);
		toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		
		
		listTabs.add(txtHot);
		listTabs.add(txtNew);
		listTabs.add(txtType);
		
		LayoutInflater mInflater = getLayoutInflater();
		viewHot = (LinearLayout)mInflater.inflate(R.layout.tab_content_hot, null);
		viewNews = (LinearLayout)mInflater.inflate(R.layout.tab_content_news, null);
		viewType = (LinearLayout)mInflater.inflate(R.layout.tab_content_type, null);
		listViews.add(viewHot);
		listViews.add(viewNews);
		listViews.add(viewType);
    
		mPager = (ViewPager) findViewById(R.id.vPager);
		mPager.setOffscreenPageLimit(3);
		pagerAdapter = new SimplePagerAdapter(listViews);
		mPager.setAdapter(pagerAdapter);
        mPager.setOnPageChangeListener(this);
		
		noneViewHot = (LinearLayout)mInflater.inflate(R.layout.none_content, null);
		noneViewNews = (LinearLayout)mInflater.inflate(R.layout.none_content, null);
		noneViewType = (LinearLayout)mInflater.inflate(R.layout.none_content, null);
		
		listHot = (XListView)viewHot.findViewById(R.id.hot_list);
		listNews = (XListView)viewNews.findViewById(R.id.hot_list);
		listType = (XListView)viewType.findViewById(R.id.hot_list);
		
		hotHeadView = new HotHeadView(this);
		listHot.addHeaderView(hotHeadView);
		
		listHot.setPullLoadEnable(true);
		listHot.setXListViewListener(this);
	
		listNews.setPullLoadEnable(true);
		listNews.setXListViewListener(this);
		
		
		listType.setPullLoadEnable(true);
		listType.setXListViewListener(this);
		
		InitViewPager();
	}
	/**
     * 初始化动画
*/
    private void InitImageView() {
    	
    	DisplayMetrics dm = getResources().getDisplayMetrics();

        int screenW = dm.widthPixels;// 获取分辨率宽度

        ivCursorWidth = BitmapFactory.decodeResource(getResources(), R.drawable.cursor).getWidth();// 获取图片宽度

        tabWidth = screenW /listViews.size();
        ivCursor.getLayoutParams().width = tabWidth;

        ivCursorWidth = tabWidth;

        
        
    }

	/**
     * 初始化ViewPager
*/
    private void InitViewPager() {
	
		txtHot.setOnClickListener(this);
		txtNew.setOnClickListener(this);
		txtType.setOnClickListener(this);
		mPager.post(new Runnable() {
			
			@Override
			public void run() {
				InitImageView();
				
			}
		});
		
        initItem();
		  
	
    }
    
	@Override
	public void onClick(View view) {
		try {
			
			String uri;
			if(view.getId() != R.id.main_hotTxt && 
					view.getId() != R.id.main_newTxt && 
					view.getId() != R.id.main_typeTxt)
				if(currIndex ==0) {
				
					pos_g = listHot.getPositionForView(view) - 1;//has headview
					map_g = dataHot.get(pos_g);
					
				} else {
					pos_g = currIndex==1?listNews.getPositionForView(view):listType.getPositionForView(view);
					map_g = currIndex==1?dataNews.get(pos_g):dataType.get(pos_g);
					
				}
			
				switch(view.getId()) {
				case R.id.main_hotTxt:
					Log.v(TAG,"hot point");
					mPager.setCurrentItem(0);
					onPageSelected(0);
					break;
					
				case R.id.main_newTxt:
					Log.v(TAG,"the news");
					mPager.setCurrentItem(1);
					onPageSelected(1);
					break;
					
				case R.id.main_typeTxt:
					Log.v(TAG,"the type");
					mPager.setCurrentItem(2);
					onPageSelected(2);
					break;
					

				case R.id.hot_line_more://更多
					Log.v(TAG,"the hot_line_more");
					
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
									copy(map_g.get("shareurl").toString(),IndexActivity.this);
									Toast.makeText(IndexActivity.this, "复制URL成功", Toast.LENGTH_LONG).show();
									popWin.dismiss();
									break;
								case R.id.delete:
									Log.e(TAG,"delete post");
									popWin.dismiss();
									confirmDialog(new Callback(){

										@Override
										public void onCallBack(
												int callBackCode, Object object) {
											switch(callBackCode) {
											case 0:
												String deleteuri = "posts/" + map_g.get("post_id").toString();
												
												try {
													HotPointerBusiness.delete(deleteuri,null,IndexActivity.this,new Handler() {
														@Override
														public void handleMessage(Message msg) {
															// TODO Auto-generated method stub
															super.handleMessage(msg);
														
															if(msg.what == 0) {
																switch(currIndex) {
																case 0:
																	dataHot.remove(pos_g);
																	adapterHot.notifyDataSetChanged();
																	listHot.invalidate();

																	break;
																case 1:
																	dataNews.remove(pos_g);
																	adapterNews.notifyDataSetChanged();
																	listNews.invalidate();

																	break;
																	
																case 2:
																	dataType.remove(pos_g);
																	adapterType.notifyDataSetChanged();
																	listType.invalidate();

																	break;
																}
																
															} else {
																alertView(msg.obj.toString());
																
															}
															
															
														}
													},40);
												}catch(Exception e) {
													
													alertView(e.getMessage());
												}
												break;
											case 1:
												break;
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
					if(MainApplication.currentUser != null && MainApplication.currentUser.get("id").toString().equalsIgnoreCase(map_g.get("user_id").toString()))
						delView.setVisibility(View.VISIBLE);
					else 
						delView.setVisibility(View.GONE);
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
					break;
					
				}
	
			
		}catch(Exception e) {
			e.printStackTrace();
			Log.e(TAG,e.toString());
			
		}
		
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
		
	}
	public void startAnimation() {
		if(txtCurrent.getId() != listTabs.get(currIndex).getId()) {
			listTabs.get(currIndex).setTextColor(Color.parseColor("#5fb336"));
       	 	txtCurrent.setTextColor(Color.parseColor("#333333"));
       	 	txtCurrent = (TextView) listTabs.get(currIndex);
		}
		
		 Animation animation = new TranslateAnimation(tabWidth * currIndex, tabWidth *currIndex, 0, 0);

		 animation.setFillAfter(true);// True:图片停在动画结束位置

		 animation.setDuration(200);

		ivCursor.startAnimation(animation);
	}
	
	@Override
	public void onPageSelected(int index) {
		
		System.out.println("selected index" + index);
		currIndex = index;
		startAnimation();
		onChangePage();
		
		
	}
	public void onChangePage() {
		
		switch(currIndex) {
		case 0:
			if(dataHot.size() == 0) initItem();
			break;
		case 1:
			if(dataNews.size() == 0) initItem();
			break;
		case 2:
			if(dataType.size() == 0) initItem();
			break;
		}
		
	}
	private void updateItemInfoList(final int opt/*refresh or loading*/) {
		String uri;
		Map map;
		RequestParams params;
				 Map<String,Object> item;
				 switch(currIndex) {
					case 0:
						uri = "posts/";
						map = dataHot.size()==0?null:dataHot.get(opt == IS_LOADING?dataHot.size()-1:0);
						params = new RequestParams();
						params.put("type", "top");
						params.put("cate_id", "");
						params.put("tag", "");
						params.put("search_key", "");
						params.put("user_id", "");
						params.put("direction", opt == IS_INIT?"": opt == IS_LOADING?"next":"refresh");
						params.put("last_post_id", map!=null && opt != IS_INIT?map.get("post_id"):"");
						params.setContentEncoding("utf-8");
						HotPointerBusiness.getHotPointer(uri, params, IndexActivity.this, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if(msg.what != 0) {
									//alertView(msg.obj.toString());
									onLoad();
									return;
								}
								JSONArray jsonArray = (JSONArray) msg.obj;
								List<Map<String, Object>> ls =  new ArrayList<Map<String,Object>>();
								
								if(jsonArray.length() == 0 && viewHot.getChildAt(0) instanceof XListView) {
									if(dataHot.size() > 0) {
										onLoad();
										return;
									}
									viewHot.removeView(listHot);
									viewHot.addView(noneViewHot);
									
								} else if(jsonArray.length() > 0 && !(viewHot.getChildAt(0) instanceof XListView)) {
									viewHot.removeView(noneViewHot);
									viewHot.addView(listHot);
								}
								if(jsonArray.length() > 0) {
									ls = (List<Map<String, Object>>) gson.fromJson(jsonArray.toString(), dataHot.getClass());

								};
								
								if(adapterHot == null) {
									dataHot.addAll(ls);
									adapterHot = new SimpleListAdapter(IndexActivity.this, dataHot,  R.layout.hot_list);
									listHot.setAdapter(adapterHot);
									
								} else if(IS_REFRESH == opt) {
									dataHot.clear();
									dataHot.addAll(ls);
									
								} else if(IS_LOADING == opt) {
									dataHot.addAll(ls);
								}
								
								adapterHot.notifyDataSetChanged();
								onLoad();
							}
						},0);
						break;
						
					case 1:
						
						uri = "posts/";
						map = dataNews.size()==0?null:dataNews.get(opt == IS_LOADING?dataNews.size()-1:0);
						params = new RequestParams();
						params.put("type", "latest");
						params.put("cate_id", "");
						params.put("tag", "");
						params.put("search_key", "");
						params.put("user_id", "");
						params.put("direction", opt == IS_INIT?"":opt == IS_LOADING?"next":"refresh");
						params.put("last_post_id",  map != null&& opt != IS_INIT?map.get("post_id"):"");
						params.setContentEncoding("utf-8");
						HotPointerBusiness.getNewsPointer(uri, params, this, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if(msg.what != 0) {
									//alertView(msg.obj.toString());
									onLoad();
									return;
								}
								JSONArray jsonArray = (JSONArray) msg.obj;
								List<Map<String, Object>> ls =  new ArrayList<Map<String,Object>>(); 
								if(jsonArray.length() == 0 && viewNews.getChildAt(0) instanceof XListView) {
									if(dataNews.size() > 0) {
										onLoad();
										return;
									}
									viewNews.removeView(listNews);
									viewNews.addView(noneViewNews);
									
								} else if(jsonArray.length() > 0 && !(viewNews.getChildAt(0) instanceof XListView)) {
									viewNews.removeView(noneViewNews);
									viewNews.addView(listNews);
								}
								if( jsonArray.length() > 0) {
									ls = (List<Map<String, Object>>) gson.fromJson(jsonArray.toString(), dataNews.getClass());
									
								}
								
								if(adapterNews == null) {
									dataNews.addAll(ls);
									adapterNews = new SimpleListAdapter(IndexActivity.this, dataNews, R.layout.hot_list);
									listNews.setAdapter(adapterNews);
									
								} else if(IS_REFRESH == opt) {
									dataNews.clear();
									dataNews.addAll(ls);
									
								} else if(IS_LOADING == opt) {
									dataNews.addAll(ls);
								} 
								adapterNews.notifyDataSetChanged();
								onLoad();
							}
						}, 20);
						 	 
						break;
						
					case 2:
						uri = "categorys/";
						params = new RequestParams();
						params.put("type", "top");
						params.put("cate_id", "");
						params.put("tag", "");
						params.put("search_key", "");
						params.put("user_id", "");
						params.put("direction", "");
						params.put("last_post_id", "");
						params.setContentEncoding("utf-8");
						map = dataType.size()==0?null:dataType.get(opt == IS_LOADING?dataType.size()-1:0);
						params = new RequestParams();
						HotPointerBusiness.getTypePointer(uri, params, IndexActivity.this, new Handler() {
							@Override
							public void handleMessage(Message msg) {
								if(msg.what != 0) {
									//alertView(msg.obj.toString());
									onLoad();
									return;
								}
								JSONArray jsonArray = (JSONArray) msg.obj;
								List<Map<String, Object>> ls = new ArrayList<Map<String,Object>>();
								if(jsonArray.length() == 0 && viewType.getChildAt(0) instanceof XListView) {
									if(dataType.size() > 0) {
										onLoad();
										return;
									}
									viewType.removeView(listType);
									viewType.addView(noneViewType);
									
								} else if(jsonArray.length() > 0 && !(viewType.getChildAt(0) instanceof XListView)) {
									viewType.removeView(noneViewType);
									viewType.addView(listType);
								}
								if(jsonArray.length() > 0) {
									ls = (List<Map<String, Object>>) gson.fromJson(jsonArray.toString(), dataHot.getClass());
								
								}
								if(adapterType == null) {
									dataType.addAll(ls);
									adapterType = new SimpleTypeListAdapter(IndexActivity.this, dataType, R.layout.type_list);
									listType.setAdapter(adapterType);
								}
								if(opt == IS_LOADING) {
									dataType.addAll(ls);
									loading = true;
								} else {
									dataType.clear();
									dataType.addAll(ls);
								}
								adapterType.notifyDataSetChanged();
								onLoad();
							}
						}, 30);
						 	
						break;
						
						default:
							break;
					}
	}

	private void initItem() {
		if(progressDialog == null)
			progressDialog = ProgressDialog.show(this, "", "数据加载中......", true, true);  
		else
			progressDialog.show(); 
		updateItemInfoList(IS_INIT);
		   
	}

	
	
	public void refresh(View view) {
		
		updateItemInfoList(IS_REFRESH);
	}


	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event){

			if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
				WindowUtils.hidePopupWindow();
				if(getParent() instanceof MainActivity) {
					MainActivity mainActivity = (MainActivity) getParent();
					mainActivity.onKeyDown(keyCode, event);
					return true;
				}
			}
			return super.onKeyDown(keyCode, event);
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
		if(progressDialog != null)
			progressDialog.dismiss();
		switch(currIndex) {
			case 0:
				listHot.stopRefresh();
				listHot.stopLoadMore();
				listHot.setRefreshTime("刚刚");
				break;
			case 1:
				listNews.stopRefresh();
				listNews.stopLoadMore();
				listNews.setRefreshTime("刚刚");
				break;
			case 2:
				listType.stopRefresh();
				listType.stopLoadMore();
				listType.setRefreshTime("刚刚");
				break;
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		try {
			int actionId = ev.getAction();
			String actionName = "Unknow:id=" + actionId;
	
	        switch (actionId) {
	
	        case MotionEvent.ACTION_DOWN:
	        	
	            actionName = "ACTION_DOWN";
	            lastX = (int)ev.getX();
	            lastY= (int)ev.getY();
	            break;
	
	        case MotionEvent.ACTION_MOVE:
	
	            actionName = "ACTION_MOVE";
	            
	            break;
	
	        case MotionEvent.ACTION_UP:
	
	            actionName = "ACTION_UP";
	           
	            break;
	
	        case MotionEvent.ACTION_CANCEL:
	
	            actionName = "ACTION_CANCEL";
	
	            break;
	
	        case MotionEvent.ACTION_OUTSIDE:
	
	            actionName = "ACTION_OUTSIDE";
	
	            break;
	
	        }
	        //Log.e(TAG, actionName);
	        int x = (int)ev.getX();
			int y = (int)ev.getY();
			Rect rect = new Rect();
			hotHeadView.getGlobalVisibleRect(rect);
			if(rect.contains(x, y)) {
				mPager.requestDisallowInterceptTouchEvent(true);
				if(Math.abs(x-lastX) > 120) {
					hotHeadView.onTouchEvent(ev);
					return true;
					
				} else if(Math.abs(y - lastY) > 120) {
					listHot.onTouchEvent(ev);
					return true;
				}
				
			}
		}catch(Exception e) {
			
		}
		return super.dispatchTouchEvent(ev);
	}
	
}
