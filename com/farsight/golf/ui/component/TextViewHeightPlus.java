package com.farsight.golf.ui.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextViewHeightPlus extends TextView {

	 private static final String TAG = "TextView";
	 
	 Context context;
	    private int actualHeight=0;


	    public int getActualHeight() {
	        return actualHeight;
	    }

	    public TextViewHeightPlus(Context context) {
	        super(context);
	        this.context = context;
	    }

	    public TextViewHeightPlus(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        this.context = context;
	        
	    }

	    public TextViewHeightPlus(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        this.context = context;

	    }

	   @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	        actualHeight=0;

	        actualHeight=(int) ((getLineCount()-1)*getTextSize());

	    }

	@SuppressLint("NewApi")
	@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			Layout layout = getLayout();  
			        if (layout != null) {  
			           int height = (int)FloatMath.ceil(getMaxLineHeight(this.getText().toString()))  
			                   + getCompoundPaddingTop() + getCompoundPaddingBottom();  
			          // System.out.println(getLineCount());
			           int cnt = (getLineCount()-1) * (int)FloatMath.ceil(getLineSpacingExtra());
			           height += cnt;
			           int width = getMeasuredWidth();              
			           setMeasuredDimension(width, height);  
			      }  

		}
	   @SuppressLint("NewApi")
	private float getMaxLineHeight(String str) {  
		    float height = 0.0f;  
		    try {
		          float screenW = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();  
		          float paddingLeft = ((LinearLayout)this.getParent()).getPaddingLeft();  
		          float paddingReft = ((LinearLayout)this.getParent()).getPaddingRight();  
		   //这里具体this.getPaint()要注意使用，要看你的TextView在什么位置，这个是拿TextView父控件的Padding的，为了更准确的算出换行  
		    int line = (int) Math.ceil( (this.getPaint().measureText(str)/(screenW-paddingLeft-paddingReft))); 
		    height = (this.getPaint().getFontMetrics().descent-this.getPaint().getFontMetrics().ascent 
		    		+ (int)FloatMath.ceil(getLineSpacingExtra()))*line; 
		   
		    }catch(Exception e) {
		    	
		    }
		    return height;
		 }  


}
