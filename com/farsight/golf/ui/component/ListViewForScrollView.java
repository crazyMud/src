package com.farsight.golf.ui.component;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.farsight.golf.R;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.business.HotPointerBusiness;
import com.farsight.golf.ui.URLActivity;
import com.farsight.golf.ui.home.VideoTypeActivity;
import com.farsight.golf.util.Callback;
import com.yixia.camera.util.StringUtils;

public class ListViewForScrollView extends ListView implements OnTouchListener,OnGestureListener {
	static String TAG = "ListViewForScrollView";
	protected static String url = new String("");
	LayoutInflater inflater;
	LinearLayout headView;
	float touchDownX; 
	float touchUpX;
	ViewFlipper viewFlipper;
	Activity context;
	ImageView[] mImageViews;
	LinearLayout lineImgDot;
	TextView adviTv;
	AsyncImageLoader imageLoader;
	int mViewCount;
	int mCurSel;
	GestureDetector detector;
	
    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        
    }

    public ListViewForScrollView(Context context, AttributeSet attrs,
        int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        
        
    }
    private void init(Context context) {
    	this.context = (Activity) context;
    	detector = new GestureDetector(this);
    	imageLoader = new AsyncImageLoader(context);
    	inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.tab_content_head, null);
        
        
        adviTv = (TextView) headView.findViewById(R.id.hot_adviseTxt);
        viewFlipper = (ViewFlipper) headView.findViewById(R.id.viewFilper_switcher);
        
        viewFlipper.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent ev) {
				Log.i("TAG", "on touch viewFilipper");
				requestDisallowInterceptTouchEvent(true);
				return detector.onTouchEvent(ev);
				
			}
		});
        
        
        
        lineImgDot = (LinearLayout) headView.findViewById(R.id.llayout);
        
       
        
        initImage();
        
        addHeaderView(headView,null,false);

    }
    private void initImage() {
    	String uri = "ops/nav/";
    	try {
	    	HotPointerBusiness.getNav(uri, context, new Callback() {
	
				@Override
				public void onCallBack(int callBackCode, Object object) {
					try {
						JSONTokener parse = new JSONTokener(object.toString());
						JSONArray jsonArray = (JSONArray) parse.nextValue();
						JSONObject jo;
						ImageView iv;
						mViewCount = jsonArray.length();
				    	mImageViews = new ImageView[mViewCount];
				    	LinearLayout.LayoutParams parms =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						for(int i=0;i<jsonArray.length();i++) {
							jo = jsonArray.getJSONObject(i);
							jo.put("index", i);
							iv = new ImageView(context);
							iv.setScaleType(ScaleType.FIT_XY);
							iv.setTag(jo);
							Bitmap bitmap = imageLoader.loadImage(iv, url + jo.getString("img"), new ImageDownloadedCallBack() {
	
								@Override
								public void onImageDownloaded(ImageView imageView,
										Bitmap bitmap) {
									imageView.setImageBitmap(bitmap);
									viewFlipper.addView(imageView,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
									
								}
								
							});
							if(bitmap != null) {
								iv.setImageBitmap(bitmap);
								viewFlipper.addView(iv,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
							}
							ImageView imageDotView = new ImageView(context);
				    		imageDotView.setImageResource(R.drawable.guide_round);
				    		imageDotView.setPadding(15, 15, 15, 15);
				    		parms.gravity =Gravity.CENTER_VERTICAL;
				    		lineImgDot.addView(imageDotView, parms);
				    		
				    		mImageViews[i] = imageDotView;
				    		mImageViews[i].setEnabled(true);
				    		mImageViews[i].setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									int pos = (Integer)(v.getTag());
									setCurPoint(pos);
								}
							});
				    		mImageViews[i].setTag(i);
						}
						mCurSel = 0;
			        	mImageViews[mCurSel].setEnabled(false);
			        	jo = (JSONObject) viewFlipper.getCurrentView().getTag();
			   			adviTv.setText(jo.getString("title").toString());
				   		
					}catch(Exception e) {
						Log.e(TAG, e.toString());
					}
				}
				
			});
    	}catch(Exception e) {
    		
    	}
    	
    }
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
    
    @Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i("TAG", "on touch ListView");
		return false;
		
	}
	public void previous(View v) {
		 try {
			 View currentView = viewFlipper.getCurrentView();
			 JSONObject jo = (JSONObject) currentView.getTag();
			 int index = Integer.parseInt(jo.getString("index"));
			 if (index ==0){
		    	return ;
		    }
			 viewFlipper.setInAnimation(context, android.R.anim.slide_in_left);  
			 viewFlipper.setOutAnimation(context, android.R.anim.slide_out_right);
			 viewFlipper.showPrevious();
			 currentView = viewFlipper.getCurrentView();
			 jo = (JSONObject) currentView.getTag();
			 index = Integer.parseInt(jo.getString("index"));
			 mImageViews[mCurSel].setEnabled(true);
			 mImageViews[index].setEnabled(false);
		     mCurSel = index;
		     adviTv.setText(jo.getString("title").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public void next (View v) {
		try {
			 View currentView = viewFlipper.getCurrentView();
			 JSONObject jo = (JSONObject) currentView.getTag();
			 int index = Integer.parseInt(jo.getString("index"));
			 Log.v("next","touch next " + index);
			 if (index == viewFlipper.getChildCount() - 1 ){
	
		    	return ;
		    }	
			 viewFlipper.setInAnimation(context, R.anim.in_right);
			 viewFlipper.setOutAnimation(context, R.anim.in_left);  
			 viewFlipper.showNext();
			 currentView = viewFlipper.getCurrentView();
			 jo = (JSONObject) currentView.getTag();
			 index = Integer.parseInt(jo.getString("index"));
			 mImageViews[mCurSel].setEnabled(true);
			 mImageViews[index].setEnabled(false);
		     mCurSel = index;
		     adviTv.setText(jo.getString("title").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	 }
	
	private void setCurPoint(int index) {
	
	    if(mCurSel-index>0) previous(null);
	    else next(null);

    	
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean ret = super.onInterceptTouchEvent(ev);
		return ret;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		return true;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		 if (e1.getX() - e2.getX() > 120) { // flip from right to left
				 viewFlipper.setInAnimation(context, R.anim.in_right);
				 viewFlipper.setOutAnimation(context, R.anim.in_left);  
				 next(null);
	            return true;
	        } else if (e1.getX() - e2.getX() < -120) { // flip from left to right
	        	viewFlipper.setInAnimation(context, android.R.anim.slide_in_left);  
				viewFlipper.setOutAnimation(context, android.R.anim.slide_out_right);
				previous(null);
	            return true;
	        }
	        return false;

	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		try {
			JSONObject jo =  (JSONObject)viewFlipper.getCurrentView().getTag();
			Intent intent = new Intent();
			try {
				if(jo.getString("type").equalsIgnoreCase("url") && !StringUtils.isEmpty(jo.getString("params"))) {
					intent.setClass(context, URLActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("url", jo.getString("params"));
					intent.putExtra("title", jo.getString("title"));
					context.startActivity(intent);
				} else if(jo.getString("type").equalsIgnoreCase("category") && !StringUtils.isEmpty(jo.getString("params"))) {
					intent.setClass(context, VideoTypeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("cateId", jo.getString("params"));
					intent.putExtra("title", jo.getString("title"));
					context.startActivity(intent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}catch(Exception e) {
			
		}
		return false;
	}
	


	

}
