package com.farsight.golf.util;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewTools implements Runnable {
	
	ListView listView;
	Handler handler;
	public ListViewTools(final ListView listView, final Handler handler) {
		this.listView = listView;
		this.handler = handler;
	}
	@Override
	public void run() {
		int totalHeight = 0;
		
		try {
			ListAdapter listAdapter = listView.getAdapter(); 
	        Message msg = handler.obtainMessage();
	        msg.what = 10;
	        
	        if (listAdapter == null) {
	   
	        	msg.obj = 0;
	        }
	        
	        for (int i = 0; i < listAdapter.getCount(); i++) {
	            View listItem = listAdapter.getView(i, null, listView);
	            listItem.measure(0, 0);
	            totalHeight += listItem.getMeasuredHeight();
	        }
	        
	        totalHeight += (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	        msg.obj = totalHeight;
	        handler.sendMessage(msg);
	        return;
	        
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
        
	}
		
}
