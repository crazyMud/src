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

public class ListViewForVideoDetail extends ListView {
	static String TAG = "ListViewForVideoDetail";
	protected static String url = new String("");
	LayoutInflater inflater;
	LinearLayout headView;
	Activity context;
	TextView adviTv;
	AsyncImageLoader imageLoader;

    public ListViewForVideoDetail(Context context) {
        super(context);
    }

    public ListViewForVideoDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        
    }

    public ListViewForVideoDetail(Context context, AttributeSet attrs,
        int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        
        
    }
    private void init(Context context) {
    	this.context = (Activity) context;
    	/*imageLoader = new AsyncImageLoader(context);
    	inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.video_detail_head, null);
        addHeaderView(headView);*/

    }
   

}
