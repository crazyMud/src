package com.farsight.golf.ui;


import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.business.LoginBusiness;
import com.farsight.golf.util.Callback;

public class RegisterActivity extends ActivityAbstract {
	TextView mobileNoTxt;
	DestoryReceiver receiver = new DestoryReceiver();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		mobileNoTxt = (TextView) this.findViewById(R.id.login_mobileExt);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.FinishActivity");
		registerReceiver(receiver, filter);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			Log.i(TAG, "unregisterReceiver registerActivity");
			unregisterReceiver(receiver);
		}catch(Exception e){}
	}
	public void onBack(View view) {
		this.finish();
	}
	public void nextStep(View view) {
		if(!verfiyMobileNo(mobileNoTxt.getText().toString())) {
			alertView("手机号码有误！");
			return;
		}
		getAuthCode();
		
	}
	public void clear(View view) {
		mobileNoTxt.setText("");
	}
	private boolean verfiyMobileNo(String mobiles) {
		Log.v("verfiyMobileNo", "verfiy the mobile number rules");
		String telRegex = "[1][358]\\d{9}";
		boolean isMobileNo = false;
		if (TextUtils.isEmpty(mobiles))
			isMobileNo = false;
		else
			isMobileNo = mobiles.matches(telRegex);
		return isMobileNo;

	}
	private void getAuthCode() {
		String uri = "users/verify/";
		JSONObject params = new JSONObject();
		try {
			params.put("phone", mobileNoTxt.getText().toString());
	        StringEntity entity = new StringEntity(params.toString());
			LoginBusiness.getAuthCode(uri, entity, this, new Callback() {

				@Override
				public void onCallBack(int callBackCode, Object object) {
					// TODO Auto-generated method stub
					switch(callBackCode) {
					case 0:
						confirmDialog(new Callback() {

							@Override
							public void onCallBack(int callBackCode, Object object) {

								switch(callBackCode) {
								case 0:
									Intent intent = new Intent(RegisterActivity.this, SendAuthCode.class); 
									Bundle bundle = new Bundle();
									bundle.putString("mobile", mobileNoTxt.getText().toString());
									intent.putExtras(bundle);
									startActivity(intent);
									finish();
									break;
								case 1:
									break;
								}
							}
							
						}, "确认手机号码", "确定", "取消",("我们将发送验证码到这个号码\\n" + mobileNoTxt.getText()).replace("\\n", "\n"));
						break;
					case 1:
						alertView(object.toString());
						break;
						
					}
				}
				
			});
		}catch(Exception e) {
			alertView(e.getMessage());
		}
	}

}
