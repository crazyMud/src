package com.farsight.golf.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.farsight.golf.R;

public class URLActivity extends ActivityAbstract {
	WebView urlwv;
	TextView titleTv;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.url_activity);
		urlwv = (WebView) findViewById(R.id.wv_url);
		titleTv = (TextView) findViewById(R.id.url_title);
		String url = getIntent().getStringExtra("url");
		String title = getIntent().getStringExtra("title");
		dialog = ProgressDialog.show(this,null,"页面加载中，请稍后.."); 
		if(url!=null) {
			
			titleTv.setText(title);
			urlwv.loadUrl(url);
			urlwv.setWebViewClient(new WebViewClient(){
				@Override
				public void onPageFinished(WebView view, String url) {
					dialog.dismiss();
					super.onPageFinished(view, url);
					
				}
			});
			dialog.show();
			urlwv.reload();
		}
	}
}
