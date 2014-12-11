package com.farsight.golf.ui;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.farsight.golf.R;
import com.farsight.golf.business.LoginBusiness;
import com.farsight.golf.ui.ActivityAbstract.DestoryReceiver;
import com.farsight.golf.ui.personal.PersonalInfoActivity;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.CountTimer;
import com.loopj.android.http.RequestParams;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class SendAuthCode extends ActivityAbstract {
	Button btnAuthCode;
	CountTimer ct;
	String mobileNo;
	EditText authCodeExt,passwordExt;
	DestoryReceiver receiver = new DestoryReceiver();
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth_code);
		Intent intent = this.getIntent();
		mobileNo = intent.getStringExtra("mobile");
		btnAuthCode = (Button) this.findViewById(R.id.login_loginBtn);
		authCodeExt = (EditText) this.findViewById(R.id.login_mobileExt);
		passwordExt = (EditText) this.findViewById(R.id.login_passwordExt);
		RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.FinishActivity");
		registerReceiver(receiver, filter);

		Callback callBack = new Callback() {
			@Override
			public void onCallBack(int callBackCode, Object object) {
			// TODO Auto-generated method stub
				switch (callBackCode) {
					case 0:
						long millisUntilFinished = (Long) object;
						btnAuthCode.setTextColor(0xff000000);
						btnAuthCode.setText("重新获得" + millisUntilFinished / 1000 + "s");
						break;
					case 1:
						btnAuthCode.setEnabled(true);
						btnAuthCode.setText("重发验证码");
						break;

				}

			}

		};
		ct = new CountTimer(60000, 1000, callBack);
		ct.start();
	}
	public void confirm(View view) {
		String uri = "users/";
		JSONObject params = new  JSONObject();
		try {
			params.put("phone", mobileNo);
			params.put("password", passwordExt.getText().toString());
			params.put("verify_code", authCodeExt.getText().toString());
			StringEntity entity = new StringEntity(params.toString());
			dialog = ProgressDialog.show(this,null,"用户注册中");
			LoginBusiness.register(uri, entity, this, new Callback() {
	
				@Override
				public void onCallBack(int callBackCode, Object object) {
					dialog.dismiss();
					switch(callBackCode) {
					case 0:
						 Intent intent = new Intent();
						 intent.setClass(SendAuthCode.this, PersonalInfoActivity.class);
						 startActivity(intent);
	
						break;
					case 1:
						alertView(object.toString());
						break;
					}
				}
				
			});
		}catch(Exception e) {
			dialog.dismiss();
		}
	}
	public void getAuth(View view) {
		btnAuthCode.setEnabled(false);
		ct.start();
		getAuthCode();
	}
	public void onBack(View view) {
		this.finish();
	}
	private void getAuthCode() {
		String uri = "users/verify/";
		JSONObject params = new JSONObject();
		try {
			params.put("phone", mobileNo);
	        StringEntity entity = new StringEntity(params.toString());
			LoginBusiness.getAuthCode(uri, entity, this);
		}catch(Exception e) {
			
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		}catch(Exception e){}
	}
}
