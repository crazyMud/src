package com.farsight.golf.ui.component;
import java.util.Date;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.farsight.golf.R;

/**
 * 能够兼容ViewPager的ScrollView
 * 
 * @Description: 解决了ViewPager在ScrollView中的滑动反弹问题

 * @File: ScrollViewExtend.java

 * 

 * @Author LEON


 * @Version V1.0
 */
public class ScrollViewExtend extends ScrollView  {
    // 滑动距离及坐标
	String TAG = "ScrollViewExtend";
    private float xDistance, yDistance, xLast, yLast;
    LinearLayout headView;
    LinearLayout innerLine;
    private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private int headContentWidth;
	private int headContentHeight;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private OnRefreshListener refreshListener;
	private OnScrollListener onScrollListener;
	private boolean isRefreshable;
	private int state;
	private boolean isBack;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private boolean canReturn;
	private boolean isRecored;
	private int startY;
	private static final long DELAY = 100;
	private int currentScroll;
	private Runnable scrollCheckTask;
	
	public interface OnScrollListener {
	        public void onScrollChanged(int x, int y, int oldX, int oldY);

	        public void onScrollStopped();

	        public void onScrolling();
	    }


	public interface OnRefreshListener {
		public void onRefresh();
	}
	/**
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(x, y, oldX, oldY);
        }
    }

    /**
     * @param child
     * @return
     */
    public boolean isChildVisible(View child) {
        if (child == null) {
            return false;
        }
        Rect scrollBounds = new Rect();
        getHitRect(scrollBounds);
        return child.getLocalVisibleRect(scrollBounds);
    }

    /**
     * @return
     */
    public boolean isAtTop() {
        return getScrollY() <= 0;
    }

    /**
     * @return
     */
    public boolean isAtBottom() {
        return getChildAt(getChildCount() - 1).getBottom() + getPaddingBottom() == getHeight() + getScrollY();
    }

	
    public ScrollViewExtend(Context context) {
    	super(context);
    	init(context);
    }
    
    public ScrollViewExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
     
     return 0;
    } 


    @Override
	protected void onFinishInflate() {
    	try {
			innerLine = (LinearLayout) this.findViewById(R.id.scroll_line);
			arrowImageView = (ImageView) headView
					.findViewById(R.id.head_arrowImageView);
			progressBar = (ProgressBar) headView
					.findViewById(R.id.head_progressBar);
			tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
			lastUpdatedTextView = (TextView) headView
					.findViewById(R.id.head_lastUpdatedTextView);
			measureView(headView);
	
			headContentHeight = headView.getMeasuredHeight();
			headContentWidth = headView.getMeasuredWidth();
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			headView.invalidate();
			innerLine.addView(headView,0);
    	}catch(Exception e) {
    		
    	}
		super.onFinishInflate();
	}
    
    private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
	private void init(Context context) {
    	LayoutInflater inflater = LayoutInflater.from(context);
    	headView = (LinearLayout) inflater.inflate(R.layout.mylistview_head, null);
    	animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		canReturn = false;
		scrollCheckTask = new Runnable() {
	            @Override
	            public void run() {
	                int newScroll = getScrollY();
	                if (currentScroll == newScroll) {
	                    if (onScrollListener != null) {
	                        onScrollListener.onScrollStopped();
	                    }
	                } else {
	                    if (onScrollListener != null) {
	                        onScrollListener.onScrolling();
	                    }
	                    currentScroll = getScrollY();
	                    postDelayed(scrollCheckTask, DELAY);
	                }
	            }
	        };

		
    	 
    }
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (getScrollY() == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.i(TAG, "在down时候记录当前位置‘");
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						Log.i(TAG, "在UP时什么都不作");
						currentScroll = getScrollY();
	                    postDelayed(scrollCheckTask, DELAY);

					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
						Log.i(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						Log.i(TAG, "由松开刷新状态，到done状态");
					}
				}
				isRecored = false;
				isBack = false;

				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && getScrollY() == 0) {
					Log.i(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {
						canReturn = true;

						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.i(TAG, "由松开刷新状态转变到done状态");
						} else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {
						canReturn = true;

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
							Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
					if (canReturn) {
						canReturn = false;
						return true;
					}
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}
	// 当状态改变时候，调用该方法，以更新界面
		private void changeHeaderViewByState() {
			switch (state) {
			case RELEASE_To_REFRESH:
				arrowImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);

				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(animation);

				tipsTextview.setText("松开刷新");

				Log.i(TAG, "当前状态，松开刷新");
				break;
			case PULL_To_REFRESH:
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.VISIBLE);
				// 是由RELEASE_To_REFRESH状态转变来的
				if (isBack) {
					isBack = false;
					arrowImageView.clearAnimation();
					arrowImageView.startAnimation(reverseAnimation);

					tipsTextview.setText("下拉刷新");
				} else {
					tipsTextview.setText("下拉刷新");
				}
				Log.i(TAG, "当前状态，下拉刷新");
				break;

			case REFRESHING:

				headView.setPadding(0, 0, 0, 0);

				progressBar.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.GONE);
				tipsTextview.setText("正在刷新...");
				lastUpdatedTextView.setVisibility(View.VISIBLE);

				Log.i(TAG, "当前状态,正在刷新...");
				break;
			case DONE:
				headView.setPadding(0, -1 * headContentHeight, 0, 0);

				progressBar.setVisibility(View.GONE);
				arrowImageView.clearAnimation();
				arrowImageView.setImageResource(R.drawable.goicon);
				tipsTextview.setText("下拉刷新");
				lastUpdatedTextView.setVisibility(View.VISIBLE);

				Log.i(TAG, "当前状态，done");
				break;
			}
		}
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
              
                if(xDistance > yDistance){
                	
                    return false;
                }  
        }

        return super.onInterceptTouchEvent(ev);
    }
    
    public void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
		invalidate();
		//scrollTo(0, 0);
	}
    
	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	
	
	

}