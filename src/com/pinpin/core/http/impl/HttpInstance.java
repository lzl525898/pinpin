package com.pinpin.core.http.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.pinpin.core.http.HttpClient;
import com.pinpin.core.http.MultipartFile;
import com.pinpin.core.http.ResponseWrapper;
import com.pinpin.core.net.InvalidSocketStateException;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.utils.Log;
 

public class HttpInstance {
	static HttpInstance utils =  new HttpInstance();
	public static HttpInstance getInstance(){
		return utils;
	}
	
	public String post0(String myUrl,Map<String, List<String>> postDatas ) {
	    Log.e("post=====", myUrl);
     	String result = "";
     	 
		HttpClient<String, String> client = new DefaultHttpClient<String, String>();
		/*暂时不考虑https,未来可以轻松支持https
		 * 
		 * SSLSocketFactory factory =  getCustomizedSSLSocketFactory();
		if (factory != null)
			client.setDefaultSSLSocketFactory(factory);*/
	 
		try {
			 
		    ResponseWrapper<String> wrapper = client.post(myUrl, null,
					postDatas, null);
			int code = wrapper.getResponseCode();
		    result = wrapper.getResponseBody();
            return result;
		}
		catch (InvalidSocketStateException e) {
			try {
				result = client.post(myUrl, null, postDatas, null).getResponseBody();
				return result;
			} 
			catch (IOException e1) {
				e1.printStackTrace();
				////Log.e("HttpUtils", "IOException e1: " + e1.getMessage());
				 JSONObject job = new JSONObject();
				 try {
					job.put("error", true);
				} 
				 catch (JSONException e2) {
					 ////Log.e("HttpUtils", "JSONException e2: " + e2.getMessage());
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				return job.toString();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			////Log.e("HttpUtils", "Exception : " + e.getMessage());
			 JSONObject job = new JSONObject();
			 try {
				job.put("error", true);
			} catch (JSONException e3) {
				////Log.e("HttpUtils", "JSONException3 : " + e3.getMessage());
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			return job.toString();
		}
	}
	
	public  byte[] post(String myUrl,Map<String, List<String>> postDatas,TaskResultListener listener ) {
	     return mpart(myUrl,postDatas,listener,null);
	}
	
	public byte[] mpart(String myUrl,Map<String, List<String>> postDatas, TaskResultListener listener,MultipartFile... files) {
	    Log.e("post=====", myUrl);
	    byte[] result = null;
     	 
		//HttpClient<String, String> client = new DefaultHttpClient<String, String>();
		HttpClient<byte[],byte[]> client = new DefaultHttpClient<byte[],byte[]>(new ByteGetHandler(),new ByteMultipartPostHandler());
		/*暂时不考虑https,未来可以轻松支持https
		 * 
		 * SSLSocketFactory factory =  getCustomizedSSLSocketFactory();
		if (factory != null)
			client.setDefaultSSLSocketFactory(factory);*/
	 
		try {
			 
		    ResponseWrapper<byte[]> wrapper = client.post(myUrl, null,postDatas, files);
			int code = wrapper.getResponseCode();
		    result = wrapper.getResponseBody();
            return result;
		}
		catch (InvalidSocketStateException e) {
			try {
				result = client.post(myUrl, null, postDatas, null).getResponseBody();
				return result;
			} 
			catch (IOException e1) {
				e1.printStackTrace();
				////Log.e("HttpUtils", "IOException e1: " + e1.getMessage());
				if(listener!=null){
					listener.failed("网络不给力！");
				}
			    
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			////Log.e("HttpUtils", "Exception : " + e.getMessage());
			if(listener!=null){
				listener.failed("网络不给力！");
			}
		}
		return null;
	}
	
	public byte[] get(String url,TaskResultListener listener) {
		 
		byte[] result = null;
		HttpClient<byte[],String> client = new DefaultHttpClient<byte[],String>(new ByteGetHandler());
/*
 * 
 *      暂时不考虑https，未来可以轻松支持https
		SSLSocketFactory factory =  getCustomizedSSLSocketFactory();
		if (factory != null)
			client.setDefaultSSLSocketFactory(factory);*/
	 
		try {
			URI uri = new URI(url);
			Log.w("发起get请求",uri.toString());
		    ResponseWrapper<byte[]> wrapper = client.get(uri.toString(), null);
			int code = wrapper.getResponseCode();
		    result = wrapper.getResponseBody();
            return result;
		}
		catch (InvalidSocketStateException e) {
			result =client.get(url, null).getResponseBody();
			return result;
		} 
		catch (Exception e) {
			e.printStackTrace();
			////Log.e("HttpUtils", "Exception : " + e.getMessage());
			if(listener!=null){
				listener.failed("网络不给力！");
			}
			return null;
		}
	}
	
	public String download(Context cxt,String url,File target,TaskResultListener listener) {
		 
		String result = null;
		 
 		HttpClient<String,String> client = new DefaultHttpClient<String,String>(new DefaultDownloadHandler(target));

		
//		暂时不考虑https，未来可以轻松支持https
//		SSLSocketFactory factory =  getCustomizedSSLSocketFactory();
//		if (factory != null)
//			client.setDefaultSSLSocketFactory(factory);
 		Log.e("获取文件MD5名",target.getName());
 		SharedPreferences pref = cxt.getSharedPreferences(target.getName(), Activity.MODE_PRIVATE );
 		String since = pref.getString("last", null);
		String etag = pref.getString("etag", null);
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		if(!TextUtils.isEmpty(since)){
			List<String> list = new ArrayList<String>();
			list.add(since);
			Log.e("since",since);
			headers.put("If-Modified-Since", list);
		}
        if(!TextUtils.isEmpty(etag)){
        	List<String> list = new ArrayList<String>();
        	list.add(etag);
        	Log.e("etag",etag);
        	headers.put("If-None-Match", list);
        }
		try {
			URI uri = new URI(url);
			Log.w("开始下载",uri.toString());
		    ResponseWrapper<String> wrapper = client.get(uri.toString(), headers);
			int code = wrapper.getResponseCode();
			Log.w("code","--"+code);
			if(code==304){
				 result = target.getAbsolutePath();
			}else{
				 if(wrapper.getResponseHeaders().containsKey("Last-Modified")){
			    	 Log.e("Last-Modified",wrapper.getResponseHeaders().get("Last-Modified").get(0));
			    	 pref.edit().putString("last", wrapper.getResponseHeaders().get("Last-Modified").get(0)).commit();
			     }
                 if(wrapper.getResponseHeaders().containsKey("ETag")){
               	    Log.e("ETag",wrapper.getResponseHeaders().get("ETag").get(0));
               	    pref.edit().putString("etag", wrapper.getResponseHeaders().get("ETag").get(0)).commit();
			     }
				 result = wrapper.getResponseBody();
			}
		   
            return result;
		}
		catch (InvalidSocketStateException e) {
			e.printStackTrace();
			result =client.get(url, null).getResponseBody();
			return result;
		} 
		catch (Exception e) {
			e.printStackTrace();
			////Log.e("HttpUtils", "Exception : " + e.getMessage());
			if(listener!=null){
				listener.failed(target.getAbsolutePath());
			}
			return null;
		} 
	}

}
