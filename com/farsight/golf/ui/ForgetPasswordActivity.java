package com.farsight.golf.ui;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsight.golf.R;
import com.farsight.golf.business.LoginBusiness;
import com.farsight.golf.util.Callback;
import com.farsight.golf.util.CountTimer;

public class ForgetPasswordActivity extends ActivityAbstract {
	TextView authCodeTxt,newPwdTxt,descTxt,mobilTxt,nextTxt;
	EditText authCodeExt,newPwdExt,mobilExt;
	Button sendAuthBtn;
	CountTimer ct;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_forget_password);
		authCodeTxt = (TextView) this.findViewById(R.id.personal_sex);
		newPwdTxt = (TextView) this.findViewById(R.id.personal_name);
		descTxt =  (TextView) this.findViewById(R.id.txt_desc); 
		mobilTxt = (TextView) this.findViewById(R.id.personal_info);
		sendAuthBtn = (Button) this.findViewById(R.id.login_loginBtn);
		nextTxt =  (TextView) this.findViewById(R.id.login_register);
		RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		authCodeExt = (EditText) this.findViewById(R.id.personal_auth_code);
		newPwdExt = (EditText) this.findViewById(R.id.personal_password);
		mobilExt = (EditText) this.findViewById(R.id.personal_alias);
		init();
	}
	
	private void init() {
		authCodeTxt.setVisibility(View.GONE);
		newPwdTxt.setVisibility(View.GONE);
		authCodeExt.setVisibility(View.GONE);
		newPwdExt.setVisibility(View.GONE);
		sendAuthBtn.setVisibility(View.GONE);
	}
	
	public void nextStep(View view) {
		if(view.getTag().toString().equalsIgnoreCase("1"))
			resetPwd();
		else
			confirmDialog(new Callback() {
	
				@Override
				public void onCallBack(int callBackCode, Object object) {
	
					switch(callBackCode) {
					case 0:
						sendAuthCode();
						break;
					case 1:
						break;
					}
				}
				
			}, "确认手机号码", "确定", "取消",("我们将发送验证码到这个号码\\n" + mobilExt.getText()).replace("\\n", "\n"));
	}
	
	private void sendAuthCode() {
		
		getAuthCode();
	}
	public void getAuth(View view) {
		sendAuthBtn.setEnabled(false);
		ct.start();
		getAuthCode();
	}
	public void getAuthCode() {
		String uri = "verifies/";
		JSONObject params = new  JSONObject();
		try {
			params.put("phone", mobilExt.getText().toString());
			StringEntity entity  = new StringEntity(params.toString());
			LoginBusiness.getAuthCode(uri, entity, this, new Callback() {
	
				@Override
				public void onCallBack(int callBackCode, Object object) {
					// TODO Auto-generated method stub
					switch(callBackCode) {
					case 0:
						authCodeTxt.setVisibility(View.VISIBLE);
						newPwdTxt.setVisibility(View.VISIBLE);
						authCodeExt.setVisibility(View.VISIBLE);
						newPwdExt.setVisibility(View.VISIBLE);
						sendAuthBtn.setVisibility(View.VISIBLE);
						mobilTxt.setVisibility(View.GONE);
						mobilExt.setVisibility(View.GONE);
						descTxt.setVisibility(View.GONE);
						nextTxt.setText("确定");
						nextTxt.setTag(1);
						Callback callBack = new Callback() {
							@Override
							public void onCallBack(int callBackCode, Object object) {
							// TODO Auto-generated method stub
								switch (callBackCode) {
									case 0:
										long millisUntilFinished = (Long) object;
										sendAuthBtn.setTextColor(0xff000000);
										sendAuthBtn.setText("重新获得" + millisUntilFinished / 1000 + "s");
										break;
									case 1:
										sendAuthBtn.setEnabled(true);
										sendAuthBtn.setText("重发验证码");
										break;
	
								}
	
							}
	
						};
						ct = new CountTimer(60000, 1000, callBack);
						ct.start();
						break;
					case 1:
						alertView("网络错误！");
						break;
					case 2:
						alertView("您输入的手机号码尚未注册!");
						break;
					}
				}
				
			});
		}catch(Exception e) {
			
		}
	}
	private void resetPwd() {
		String uri = "users/editpassword/";
		JSONObject params = new  JSONObject();
		try {
			params.put("phone", mobilExt.getText().toString());
			params.put("password", mobilExt.getText().toString());
			params.put("verify_code", mobilExt.getText().toString());
			StringEntity entity = new StringEntity(params.toString());
			LoginBusiness.resetPwd(uri, entity, this, new Callback(){
	
				@Override
				public void onCallBack(int callBackCode, Object object) {
					// TODO Auto-generated method stub
					switch(callBackCode) {
					case 0:
						finish();
						break;
					case 1:
						alertView("网络错误");
						break;
					}
				}
				
			});
		}catch(Exception e) {
			
		}
	}
	@Override
	protected void onDestroy() {
		if(ct !=null)
			ct.cancel();
		
		super.onDestroy();

	}
}
