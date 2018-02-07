package com.pinpin.ui;

 
 
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.pinpin.R;
import com.pinpin.app.PinApplication;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.guide.Guide;
import com.pinpin.model.ChatListModel;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpTask.TaskResultListener2;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.AutoDownLoadManager;
import com.pinpin.utils.ConnectionDetector;
import com.pinpin.utils.Log;
import com.pinpin.utils.SystemUtils;
import com.pinpin.utils.VersionCheckManager;

 

public class SplashActivity extends BaseActivity {
	SharedPreferences pref ;
	private String version;
	private String downloadUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		noHeader();
		setFooterGone();
		if (!ConnectionDetector.isNetworkAvailable(this)) {
			AlertDialog alertDialog = new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("无法访问网络， 请检查网络设置")
					.setPositiveButton("退  出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									System.exit(0);
								}
							}).create();
			alertDialog.show();
			return;
		}
		CheckVersionThenDownLoad();
	 }
	void start(){
		long time = pref.getLong("time",0);
		final String token = pref.getString("token","");
		Log.e("token",token);
		Log.e("登录日",((System.currentTimeMillis()-time)/(1000*60*60*24))+"天");
		if(TextUtils.isEmpty(token)||time == 0|| ((System.currentTimeMillis()-time)/(1000*60*60*24)) > 7){
			Log.e("登录日",((System.currentTimeMillis()-time)/(1000*60*60*24))+"天");
			intent();
		} else{
			Constants.USERNAME =   pref.getString("USERNAME","");
			Constants.PSW =   pref.getString("PSW","");
			Log.e("USERNAME","||"+Constants.USERNAME);
			Log.e("PSW","||"+Constants.PSW);
			 HashMap<String,String> data = new  HashMap<String,String>(){{
				 put("token",token);
			     
			 }};
		    RequestData request = HttpUtils.simplePostData(Address.HOST+Address.CHECK_TOKEN, data);
			startHttpTask(new TaskResultListener() {
				
				@Override
				public void result(ResposneBundle b) {
					// TODO Auto-generated method stub
					if(b.getContent()==null){
						 showToast("出错了，服务器异常");
						 intent();
						 return;
					 }
					 
						try {
							Log.e("checkLogin",b.getContent());
							JSONObject job = new JSONObject(b.getContent());
							if(job.getInt("code")==-1000 ){
								intent();
								return;
							}
							
							JSONObject user = job.getJSONObject("data").getJSONObject("user");
							Constants.NICKNAME =  user.getString("username");
							Constants.TOKEN=  user.getString("token");
							Log.e("得到Constants.NICKNAME","--"+Constants.NICKNAME);
							Log.e("得到Constants.Token","--"+Constants.TOKEN);
							JSONArray pictures = user.getJSONArray("pictures");
							 if(pictures.length()>0){
								 ChatListModel model = new ChatListModel();
								 String relative =pictures.getJSONObject(0).getString(Constants.AVATAR);
								 if(TextUtils.isEmpty(relative)||relative.equals("null")){
									 model.avatar_url  = null;
									 intent2();
								 }else{
									 model.avatar_url = Address.HOST_PICTURE + relative;
									 String fileName =model.avatar_url.substring(model.avatar_url.lastIndexOf("/")+1);
									 File path =   getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
								     path = new File( path,fileName);
									 if(!path.exists()){
										try {
											path.createNewFile();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								    model.path = path.getAbsolutePath() ;
								    fileDownload(new  TaskResultListener2() {
										@Override
										public void result(String b, View v) {
											// TODO Auto-generated method stub
											Constants.AVATAR_PATH = b;
											Log.e("我的头像", Constants.AVATAR_PATH);
											intent2();
										}
										
										@Override
										public void failed(String message) {
											// TODO Auto-generated method stub
											intent2();
											showToast("获取个人头像失败");
										}
									}, model, null);
								 }
						
									return;
							 }
							 else
							 {
								 intent2();
							 }
							 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							intent2();
						}
				}
				
				@Override
				public void failed(String message) {
					// TODO Auto-generated method stub
					intent();
				}
			}, request);
		}
		 
	 }
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		pref = getSharedPreferences(Constants.SETTING, 0);
		
		inflater.inflate(R.layout.activity_splash, container);
		View view = findViewById(R.id.title);
		/*view.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				    
				       
						int value = pref.getInt(Constants.NEED_WELCOME_PAGE,-1);
						if(value == 1){
							start();
						}else{
							 Intent in = new Intent(SplashActivity.this,Guide.class);
							 startActivity(in);
					         overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					         finish();
						}
			        
			}
		}, 1500);*/
	}
	
	private void intent(){
		Intent intent = new Intent(
				SplashActivity.this,
				LoginActivity.class);
		SplashActivity.this.startActivity(intent);
	    
		SplashActivity.this.finish();
	}
	private void intent2(){
		SharedPreferences prefs = getSharedPreferences(Constants.SETTING, 0);
		boolean result = prefs.edit()
				.putString("token", Constants.TOKEN )
				.putLong("time", System.currentTimeMillis())
				.commit();
		Intent notifiedIntent = getIntent();
		String type=null;
		String who=null;
		if(notifiedIntent!=null){
			type = notifiedIntent.getStringExtra(PinApplication.NOTIFACATION);
			who= notifiedIntent.getStringExtra("offlineId");
		}
	    Intent intent = new Intent(
			SplashActivity.this,
			  MainActivity.class);
	          if(!TextUtils.isEmpty(who)||!TextUtils.isEmpty(type)){
	        	  Log.e("--------------offlineId",who);
	        	  Log.e("--------------NOTIFACATION",type);
	        		intent.putExtra("offlineId", who);
					intent.putExtra(PinApplication.NOTIFACATION, type);
	          }
	          startActivity(intent);
	          overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	          SplashActivity.this.finish();
	          }
	
	private void CheckVersionThenDownLoad() {
		  
		VersionCheckManager checkMng = new VersionCheckManager(
				new VersionCheckManager.VersionCheckListener() {

					@Override
					public void finish(String json) {
						// TODO Auto-generated method stub
       //                dismissProgress();
						Log.e("josn",json);
						JSONObject jo = null;
						try {
							jo = new JSONObject(json);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (jo.has("error_code")) {
							Log.e("错误", "检查版本更新文件出错");
							start();
							return;
						} else {
							try {
							 if(jo.has("androidClientApp")){
									final JSONObject child_job =  jo.getJSONObject("androidClientApp") ;
									//Log.e("androidClientApp", child_job.toString());
									
									String versionName = SystemUtils.getAppVersionName(getApplicationContext());
									////Log.e("包名"," "+getApplicationContext().getPackageName() );
									version ="";
									downloadUrl = "";
									   if(child_job.has("version")){
											version = child_job.getString("version");
										}
	                                    if(child_job.has("url")){
	                                  	  downloadUrl = child_job.getString("url");
										}
									 
	                                    start();
									
									//Log.e("version","客户端版本:"+versionName+"  服务器版本:"+version);
									/*if(!TextUtils.isEmpty(version)&&!TextUtils.equals(versionName, version)){
										showAlertDialogWithCancel("客户端有新版本","是否更新客户端？",new DialogInterface.OnClickListener(){

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method stub
												
												 if(!TextUtils.isEmpty(downloadUrl)){
													 final AutoDownLoadManager adlm= new AutoDownLoadManager(SplashActivity.this);	
													 //Log.e("url",downloadUrl);
													 File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
										                path = new File(path,"MoblieOA_"+version+".apk");
													    adlm.download(downloadUrl,path,"正在加载最新安装文件v"+version);
													 
										                return;
												  }
											}},"立即更新",new DialogInterface.OnClickListener(){

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method stub
												start();
											}},"暂不更新");
									  
								    }else{
								    	start();
								    }*/
								}else{
									start();
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								start();
								 
							}
						}
						 
					}
				});

		checkMng.checkVersion(Address.VERSION_FILE);

	}
}
