package com.farsight.golf.adapter;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.asyn.AsyncImageLoader;
import com.farsight.golf.asyn.AsyncImageLoader.ImageDownloadedCallBack;
import com.farsight.golf.util.DateDistance;
import com.google.gson.JsonObject;
import com.yixia.camera.util.StringUtils;

public class SimpleDiscussAdapter extends BaseAdapter {

	static String TAG = "SimpleDiscussAdapter";
	Context context;
	List<? extends Map<String, Object>> data;
	int resource;
	private AsyncImageLoader imageLoader;
	
	public SimpleDiscussAdapter(Context context,int resource, List<Map<String, Object>> data) {
		this.context = context;
		this.resource = resource;
		this.data = data;
		imageLoader = new AsyncImageLoader(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		Map item =  data.get(position);
		if(view == null && item != null) {
			holder = new ViewHolder();
			view =  LayoutInflater.from(context).inflate(resource, null);
			holder.portalImg = (ImageView) view.findViewById(R.id.hot_portal);
			holder.name = (TextView) view.findViewById(R.id.dis_name);
			holder.describ = (TextView) view.findViewById(R.id.dis_content);
			holder.times = (TextView) view.findViewById(R.id.dis_time);
			holder.disLay = (RelativeLayout) view.findViewById(R.id.dis_lay);
			holder.authImg = (ImageView) view.findViewById(R.id.hot_portal_iden);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		try {
			holder.name.setText(item.get("nickname").toString());
			holder.describ.setText(Html.fromHtml(item.get("content").toString()));
			try {
				
				String times = item.get("created").toString();
				Double doTime = Double.parseDouble(times);
				long time = doTime.longValue();
				String stiff = DateDistance.getDistance(time);
				holder.times.setText(stiff);
				
			} catch(Exception e) {
				Log.d(TAG,e.toString());
			}
			//holder.disLay.setOnClickListener((OnClickListener) context);
			final String imgUrl = item.get("user_img").toString();
			holder.portalImg.setTag(imgUrl);
			//holder.authImg.setVisibility(item.get("user_level").toString().equalsIgnoreCase("0")?View.GONE:View.VISIBLE);
			if(imgUrl != null && StringUtils.isNotEmpty(imgUrl)) {
				Bitmap bitmap = imageLoader.loadImage(holder.portalImg, imgUrl, new ImageDownloadedCallBack() {
					
					@Override
					public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
						if(imageView.getTag().toString().equalsIgnoreCase(imgUrl)) 
							imageView.setImageBitmap(bitmap);
						
					}
				});
				if(bitmap != null) {
					holder.portalImg.setImageBitmap(bitmap);
				}
			}
		}catch(Exception e) {
			Log.e(TAG, e.toString());
		}
		return view;
	}
	private class ViewHolder {
		
        TextView name;
        TextView plays;
        TextView times;
        TextView describ;
        RelativeLayout disLay;
        ImageView portalImg;
        ImageView VideoImg;
        ImageView playImg;
        ImageView authImg;
        Button commBtn;
        String imgPortalUrl;
        String imgVideoUrl;
        String videoUrl;
        
}

}
