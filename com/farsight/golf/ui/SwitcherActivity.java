package com.farsight.golf.ui;
import java.util.List;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Scroller;
import android.widget.ViewFlipper;

import com.farsight.golf.R;
import com.farsight.golf.business.Configuration;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.util.BitmapUtil;





/**
 * @author Joephone
 * 
 */
public class SwitcherActivity extends ActivityAbstract {


	PopupWindow popWindow = null;
	ViewFlipper viewFlipper;
	LinearLayout linearLayout;
	View popviewLayout;
	LayoutInflater layoutInflater;
	Button enterBtn;
	int mViewCount;
	//ImageView[] mImageViews;
	private int mCurSel;
	Scroller mScroller;
	// 左右滑动时手指按下的X坐标 
    private float touchDownX; 
    // 左右滑动时手指松开的X坐标 
    private float touchUpX; 
    private static boolean hasEnter = false;
    int userid;
    String token = null;
    final String TAG = this.getClass().getSimpleName();
    DestoryReceiver receiver = new DestoryReceiver();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		this.setContentView(R.layout.activity_switcher);
		viewFlipper = (ViewFlipper)this.findViewById(R.id.viewFilper_switcher);
		linearLayout = (LinearLayout) findViewById(R.id.llayout);
		popviewLayout = this.getLayoutInflater().inflate(R.layout.pop_layout, null);
		init();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.FinishActivity");
		registerReceiver(receiver, filter);
		super.onCreate(savedInstanceState);
		
	}
	
	protected void loadImage() {
		try {
			Log.v(TAG, "load image......");
			List<String> listImageFiles = BitmapUtil.getImagePathFromSD(Configuration.RESOURCE_PATH_FILE);
			for(String fileName:listImageFiles) {
				Bitmap bm = BitmapUtil.GetBitmap(fileName, 30);
				ImageView imageView = new ImageView(this.getApplicationContext());
				imageView.setImageBitmap(bm);
				viewFlipper.addView(imageView,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				
			}
		}catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	private void init() {
		
		loadImage();
		
		mViewCount = viewFlipper.getChildCount();
/*    	mImageViews = new ImageView[mViewCount];
    	for(int i = 0; i < mViewCount; i++) {
    		ImageView imageDotView = new ImageView(this.getApplicationContext());
    		imageDotView.setImageResource(R.drawable.guide_round);
    		imageDotView.setPadding(15, 15, 15, 15);
    		LinearLayout.LayoutParams parms =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    		parms.gravity =Gravity.CENTER_VERTICAL;
    		linearLayout.addView(imageDotView, parms);
    		mImageViews[i] = imageDotView;//(ImageView) linearLayout.getChildAt(i);
    		mImageViews[i].setEnabled(true);
    		mImageViews[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int pos = (Integer)(v.getTag());
					setCurPoint(pos);
				}
			});
    		mImageViews[i].setTag(i);
    	}*/
    	mCurSel = 0;
    	//mImageViews[mCurSel].setEnabled(false);    	
    	viewFlipper.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) { 
		             // 取得左右滑动时手指按下的X坐标 
		             touchDownX = event.getX(); 
		             return true; 
		         } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	 touchUpX = event.getX();
		        	 if (touchUpX - touchDownX > 100) { // 设置View切换的动画 
		        		previous(v); 
		                
		             } else if(touchDownX-touchUpX >100) { // 从右往左，看后一个View 
		            	 next(v);
		             }
		        	 return true;
		         }
				return false;
			}
		});
	}


	private void showPopView() {
		Log.v("showPopView", "ShowPopView");
		try {
			if(popviewLayout != null) {
				popWindow = new PopupWindow(popviewLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
				popWindow.showAtLocation(findViewById(R.id.firstOpen_layout), Gravity.BOTTOM, 0, 150);
			}
		
		}catch(Exception e) {
			Log.v("error from showPopView",e.getMessage());
		}

	}

	 private void setCurPoint(int index) {
		
	    if(mCurSel-index>0) previous(null);
	    else next(null);

	    	
	}

	 public void previous(View v) {
		 int index = Integer.parseInt(viewFlipper.getCurrentView().getTag().toString());
		 if (index ==0){
	    		return ;
	    	}
		 Log.v("previous","touch previous " + index);
		 viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);  
		 viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
		 viewFlipper.showPrevious();
		 index = Integer.parseInt(viewFlipper.getCurrentView().getTag().toString());
		 //mImageViews[mCurSel].setEnabled(true);
		 //mImageViews[index].setEnabled(false);
	     mCurSel = index;
	 }
	 
	 public void next (View v) {
		 int index = Integer.parseInt(viewFlipper.getCurrentView().getTag().toString());
		 Log.v("next","touch next " + index);
		 if (index == mViewCount - 1 ){
			hasEnter = true;
			
	    	return ;
	    }
		 viewFlipper.setInAnimation(this, R.anim.in_right);
		 viewFlipper.setOutAnimation(this, R.anim.in_left);  
		 viewFlipper.showNext();
		 index = Integer.parseInt(viewFlipper.getCurrentView().getTag().toString());
		 if (index == mViewCount - 1 ){
			showPopView();//enterMain(); 
		 }
		 //mImageViews[mCurSel].setEnabled(true);
		 //mImageViews[index].setEnabled(false);
	     mCurSel = index;
	 }
	
	 
	 public  void enterMain(View view) {
		 Intent intent = new Intent();
		 intent.setClass(SwitcherActivity.this,MainActivity.class);
		 startActivity(intent);
		 SharedPreferences.Editor editor = MainApplication.getSettingEditor();
		 editor.putInt("switcher", 1);
		 editor.commit();
		 finish();
	 }
	 
	 @Override
	protected void onResume() {
		System.out.println("reopen switcher");
		super.onResume();
		if(hasEnter) {
			 hasEnter = false;
			 Intent intent = new Intent();
			 intent.setClass(SwitcherActivity.this,MainActivity.class);
			 startActivity(intent);
			 finish();
		}
		
	}
	 @Override
		protected void onDestroy() {
			try {
				// if(receiver.isOrderedBroadcast()) {
				Log.i(TAG, "unregisterReceiver");
				unregisterReceiver(receiver);
				popWindow.dismiss();
			} catch (Exception e) {

			}

			super.onDestroy();
		}
}
