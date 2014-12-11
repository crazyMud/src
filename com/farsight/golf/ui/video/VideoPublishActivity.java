package com.farsight.golf.ui.video;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.business.VideoBusiness;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.ui.MainActivity;
import com.farsight.golf.util.BitmapUtil;
import com.farsight.golf.util.ConstUtil;
import com.farsight.golf.util.HttpClientAsync;
import com.yixia.camera.util.StringUtils;

public class VideoPublishActivity extends ActivityAbstract {
	static String TAG = "VideoPublishActivity";
	Button publishBtn;
	EditText describ;
	String strDesc;
	TextView insertFlag;
	private String mPath;
	ProgressDialog dialog;
	private int maxLen = 300;  
	private EditText editText = null;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_video);
		RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		insertFlag = (TextView) findViewById(R.id.text_number);
		publishBtn = (Button) findViewById(R.id.publish_btn);
		describ = (EditText) findViewById(R.id.video_desc);
		describ.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable edit) {
				insertFlag.setText(String.format("%d字", maxLen - edit.length()));
				
			}

			@Override
			public void beforeTextChanged(CharSequence text, int start,
					int count, int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence text, int start, int before,	int count) {
				
				
			}
			
		});
		actObser.addObserver(MainActivity.mainObserver);
		//insertFlag = (TextView) findViewById(R.id.insert_flag);
		mPath = getIntent().getStringExtra("path");
		
		if (StringUtils.isEmpty(mPath)) {
			finish();
			return;
		}
		publishBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				hideKeyboard(view);
				uploadVideo();
				
			}
		});
	}
	public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
	
	private void uploadVideo() {
		strDesc = describ.getText().toString();
		if(strDesc.length() > maxLen) {
			alertView("说明文字不能超过300个汉字");
			return;
		}
		String uri = "files/";
		try {
			File videoFile = new File(mPath);
			String bimMapFileName = videoFile.getName().replace(".", "_") + ".png";
			bimMapFileName = videoFile.getPath() + bimMapFileName;
			File bitMapFile = new File(bimMapFileName);
			if(bitMapFile.exists()) bitMapFile.delete();
			Bitmap bitMap = BitmapUtil.getVideoThumbnail(mPath, 96, 96,  MediaStore.Images.Thumbnails.MICRO_KIND);
			FileOutputStream out = new FileOutputStream(bitMapFile);
			bitMap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			File[] files = new File[]{videoFile,bitMapFile};
			dialog = ProgressDialog.show(this,null,"文件上传中，请稍后.."); 
			publishBtn.setEnabled(false);
			HttpClientAsync.upLoadByHttpClient4(files, uri, this, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch(msg.what) {
					case 100:
						//Toast.makeText(VideoPublishActivity.this, "文件上传成功", Toast.LENGTH_LONG).show();
						String uri = "posts/";
						try {
							JSONObject jsonResult = (JSONObject) msg.obj;
							JSONObject jsonFile1 = (JSONObject) jsonResult.get("uploadFile1");
							JSONObject jsonFile2 = (JSONObject) jsonResult.get("uploadFile2");
							JSONObject params = new JSONObject();
							params.put("video", jsonFile1.get("file_url"));//"video":"xxx", 			# 视频地址
							params.put("video_img", jsonFile2.get("file_url"));//"video_img":"xxx",		# 视频缩略图地址
							params.put("post_content", strDesc);//"post_content":"???",	# 描述
							params.put("gps_lng", "");//"gps_lng":"???",		# 经度
							params.put("gps_lat", "");//"gps_lat":"???",		# 纬度
							String strEntity = params.toString();
							StringEntity entity = new StringEntity(strEntity,"utf-8");
							entity.setContentType(new BasicHeader("Content-Type", "application/json"));
							VideoBusiness.publishVideo(uri, entity, VideoPublishActivity.this,this);
							
						}catch(Exception e) {
							Log.e(TAG, e.toString());
							dialog.dismiss();
						}
						break;
					case 0:
						dialog.dismiss();
						Toast.makeText(VideoPublishActivity.this, "保存成功", Toast.LENGTH_LONG).show();
						actObser.setDate(ConstUtil.VIDEO_PUBLISHED);
						finish();
						break;
					case 1:
						Toast.makeText(VideoPublishActivity.this, "视频发布失败" + msg.obj, Toast.LENGTH_LONG).show();
						dialog.dismiss();
						break;
					}
				}
				
			});
		} catch (Exception e) {
			dialog.dismiss();
			e.printStackTrace();
		}
	}
}
