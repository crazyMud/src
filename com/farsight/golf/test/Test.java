package com.farsight.golf.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.farsight.golf.R;
import com.farsight.golf.util.DateDistance;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;

public class Test {
	public static void main(String[] args) {
		Test te = new Test();
		te.t();
	}
static class pe {
	public static int a;
	public int b;
}
	private void t() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		String url = "http://www.3gmfw.cn/qqtouxiang/UploadPic/2012-9/20129921285294.jpg";
		String url1 = "http://img.postwhy.com/2011/502-082646.jpg";
		//for (int i = 0; i < 5; i++) {
			item.put("hotPortal", R.drawable.portal);
		//	item.put("portalUrl", i % 2 == 0 ? url : url1);
			item.put("hotName", "布鲁斯++++++");
			item.put("create", 123482898);
			//data.add(item);
			//item = new HashMap<String, Object>();
		//}
Gson gson = new Gson();
		//String is = item.toString();
		
JsonParser jsonParser = new JsonParser();

		String str = gson.toJson(item).replaceAll("\\\"", "");
		
		String s = str;//"{created:1416396960,nickname:张三,user_img:http://static.lexianglai.com/app/stx/storage/avatars/thumbs5/1415433602454133.png}";
		System.out.println(str);
		JsonObject jo = (JsonObject) jsonParser.parse(s);
		
		System.out.println(jo.get("create"));
		
		System.out.println(jo.get("hotName"));
		
		Map<String,Object> map = new HashMap<String,Object>();
		map = gson.fromJson(jo, map.getClass());
				
				System.out.println(map.get("create"));
				
				
		long t1 =  System.currentTimeMillis()+10000;
		
		Date d1 = new Date(t1);
		
		
		long t2 =  System.currentTimeMillis() ;
		
		Date d2 = new Date(t2);
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		  
		String sd = sdf.format(new Date(Long.parseLong(t1+"")));
		
		String sd1 = sdf.format(new Date(Long.parseLong(t2+"")));  

		System.out.println(sd);
		
		System.out.println(sd1);
		
		String st = DateDistance.getDistance(t1);
		
		System.out.println(st);
		
		long [] l = DateDistance.getDistanceTimes(t2);
		
		for(int i=0;i<l.length;i++) {
			System.out.println(i + "->" + l[i]);
		}
		/*pe p = new pe();
		

		System.out.println(Integer.valueOf(pe.a));

		
		

		JsonArray ja = new JsonArray();

		String jsparse = gson.toJson(data);

		System.out.println(jsparse);

		// JsonElement je = gson.toJsonTree(jsparse.replace("\"", ""));

		// System.out.println(je);

		// System.out.println(jo);

		String tmp = jsparse.replaceAll("\"", "\\\\\"");
		System.out.println(tmp);

		String str = "[{\"hotName\":\"布鲁斯++++++0\"}]";// //,\"portalUrl\":\"http://www.3gmfw.cn/qqtouxiang/UploadPic/2012-9/20129921285294.jpg\",\"hotPortal\":2130837601}]";

		List<Map<String, Object>> dc = gson.fromJson(str, data.getClass());*/

		// ja = (JsonObject) gson.toJsonTree(jsparse,ja.getClass());
		// ja.add(gson.toJsonTree(data));

		// System.out.println(ja);

		// System.out.println(data.toString());
		// List<Map<String,Object>> dd = gson.fromJson(data.toString(),
		// data.getClass());

	}
}
