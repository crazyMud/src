package com.farsight.golf.ui;


import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.farsight.golf.R;
import com.farsight.golf.business.LoginBusiness;
import com.farsight.golf.ui.personal.PersonalCenterActivity;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.ConstUtil;
import com.farsight.golf.util.DeviceUtils;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends ActivityAbstract {

	private static String TAG = "LoginActivity";
	private EditText mobileExt, pwdExt;
	private ProgressBar process;
	DestoryReceiver receiver = new DestoryReceiver();
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mobileExt = (EditText)this.findViewById(R.id.login_mobileExt);
		pwdExt = (EditText)this.findViewById(R.id.login_passwordExt);
		process = (ProgressBar)this.findViewById(R.id.login_process);
		RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstUtil.FINISH_ACTION);
		registerReceiver(receiver, filter);

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		} catch(Exception e) {
			
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
				return true;
	}
	public void onBack(View view) {
		this.finish();
	}
	public void login(View view) {
		if(!verfiyMobileNo(mobileExt.getText().toString())) {
			alertView("您输入的号码不正确！");
			return;
		} else if(TextUtils.isEmpty(pwdExt.getText())) {
			alertView("验证码不能为空！");
			return;
		}

		process.setVisibility(View.VISIBLE);
		String uri = "users/login";
		JSONObject params = new  JSONObject();
		try {
			params.put("phone", mobileExt.getText());
			params.put("password", pwdExt.getText());
			params.put("device_id", DeviceUtils.getDeviceID(this));
			StringEntity entity = new StringEntity(params.toString());
			dialog = ProgressDialog.show(this,"","正在登录......",true,true);
			LoginBusiness.login(uri, entity, this, new Callback() {
	
				@Override
				public void onCallBack(int callBackCode, Object object) {
					process.setVisibility(View.GONE);
					switch(callBackCode) {
					case 0:
						if(getIntent().getFlags() == ConstUtil.USER_LOGIN_FLAG) {
							actObser.addObserver(PersonalCenterActivity.perObserver);
							actObser.setDate(ConstUtil.PERSONAL_INIT);
						} else {
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, MainActivity.class);
							startActivity(intent);
							sendBroadcast(new Intent(ConstUtil.FINISH_ACTION));
						}
						dialog.dismiss();
						finish();
						break;
					case 1:
						dialog.dismiss();
						alertView(object.toString());
						break;
					}
					
				}
				
			});
		}catch(Exception e) {
			alertView("系统异常，请稍后再试");
			dialog.dismiss();
		}
	}
	
	public void forgetPassword(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ForgetPasswordActivity.class);
		startActivity(intent);
	}
	public void register(View view) {
		Intent intent = new Intent();
		intent.setClass(this, RegisterActivity.class);
		startActivity(intent);

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
	
	public void clear(View view) {
		mobileExt.setText("");
	}
	public void showPwd(View view) {
		System.out.println(pwdExt.getInputType());
		pwdExt.setInputType(pwdExt.getInputType() == 129?
				144:129);
	}
	
	
}
