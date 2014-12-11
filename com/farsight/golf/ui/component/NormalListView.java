package com.farsight.golf.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;


public class NormalListView extends ListView  {
	static String TAG = "NormalListView";
	LayoutInflater inflater;
	LinearLayout headView;
	float touchDownX; 
	float touchUpX;
	
	Context context;

    public NormalListView(Context context) {
        super(context);
    }

    public NormalListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public NormalListView(Context context, AttributeSet attrs,
        int defStyle) {
        super(context, attrs, defStyle);
        
        
    }
    
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
    }  
  
}
