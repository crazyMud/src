package com.farsight.golf.ui.setting;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.farsight.golf.R;
import com.farsight.golf.business.LoginBusiness;
import com.farsight.golf.main.MainApplication;
import com.farsight.golf.ui.ActivityAbstract;
import com.farsight.golf.util.Callback;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SettingActivity extends ActivityAbstract {
	static String TAG = "SettingActivity";
	FeedbackAgent agent;
	Button exitBtn;
	RelativeLayout updateLayout, feedbackLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		exitBtn = (Button) findViewById(R.id.setting_exitBtn);
		updateLayout = (RelativeLayout) findViewById(R.id.setting_update);
		feedbackLayout = (RelativeLayout) findViewById(R.id.setting_feedback);
		
		exitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String uri = "users/logout";
				JSONObject params = new  JSONObject();
				try {
					JSONObject user = MainApplication.currentUser;
					params.put("phone", user.get("phone"));
					StringEntity entity  = new StringEntity(params.toString());
					LoginBusiness.logout(uri, entity, SettingActivity.this,new Callback() {
			
						@Override
						public void onCallBack(int callBackCode, Object object) {
							// TODO Auto-generated method stub
							Log.v(TAG, object.toString());
							if(callBackCode == 0) {
								//File file = new File(Configuration.PORTRAIT_PATH , "portal.png");
								//if(file.exists()) file.delete();
								//MainApplication.getInstance().exit();
								MainApplication.currentUser = null;
								finish();
							} else
								alertView(object.toString());
						}
						
					});
				}catch(Exception e) {
					Log.e(TAG,e.getMessage());
				}
				
			}
		});
		
		updateLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"update");
				
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				    @Override
				    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				        switch (updateStatus) {
				        case UpdateStatus.Yes: // has update
				            UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
				            break;
				        case UpdateStatus.No: // has no update
				            Toast.makeText(SettingActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
				            break;
				        case UpdateStatus.NoneWifi: // none wifi
				            Toast.makeText(SettingActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
				            break;
				        case UpdateStatus.Timeout: // time out
				            Toast.makeText(SettingActivity.this, "超时", Toast.LENGTH_SHORT).show();
				            break;
				        }
				    }

					
				});
				UmengUpdateAgent.update(SettingActivity.this);


			}
		});
		
		feedbackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FeedbackAgent agent = new FeedbackAgent(SettingActivity.this);
			    agent.startFeedbackActivity();

			}
		});
	}
}
