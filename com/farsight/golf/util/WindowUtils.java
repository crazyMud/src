package com.farsight.golf.util;

import com.farsight.golf.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;



/**
 * 弹窗辅助类
 *
 * @ClassName WindowUtils
 *
 *
 */
public class WindowUtils extends PopupWindow {

    private static final String LOG_TAG = "WindowUtils";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    private static View eventView;
    public static Boolean isShown = false;
    static WindowUtils windowUtils = new WindowUtils();
    final static WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    public WindowUtils() {
    	
    }
    public WindowUtils(Activity context,View view) {
    	super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View mMenuView = view;
	
		//设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		//设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		//设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		//设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(true); 
		this.update();
		//mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		/*mMenuView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});*/

    }
    
    /**
     * 显示弹出框
     *
     * @param context
     * @param view
     */
    public static void showPopupWindow(final Context context, final View view) {
    	if (isShown) {
            Log.i(LOG_TAG, "return cause already shown");
            return;
        }
        isShown = true;
        Log.i(LOG_TAG, "showPopupWindow");
        LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	 mContext = context.getApplicationContext();
         // 获取WindowManager
         mWindowManager = (WindowManager) mContext
                 .getSystemService(Context.WINDOW_SERVICE);
         mView = view;
      // 类型
         params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

         // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

         // 设置flag

         int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
         // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
         // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
         params.flags = flags;
         // 不设置这个弹出框的透明遮罩显示为黑色
         params.format = PixelFormat.TRANSLUCENT;
         // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
         // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
         // 不设置这个flag的话，home页的划屏会有问题

         params.width = LayoutParams.MATCH_PARENT;
         params.height = LayoutParams.MATCH_PARENT;

         params.gravity = Gravity.CENTER;
        
         mWindowManager.addView(mView, params);

         Log.i(LOG_TAG, "add view");
    }
    

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        Log.i(LOG_TAG, "hide " + isShown + ", " + mView);
        if (isShown && null != mView) {
            Log.i(LOG_TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;
        }

    }
   
	
}

