package com.pinpin.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.model.ChatListModel;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpTask.TaskResultListener2;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.CommonUtils;
import com.pinpin.utils.Log;

public class LoginActivity extends BaseActivity {
	EditText username;
	EditText password;
	Button loginBtn;
	TextView btn_forget;
	TextView btn_reg;
	protected void onNewIntent(Intent intent) {  
		// TODO Auto-generated method stub  
		super.onNewIntent(intent);  
		        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {  
		               
		                Intent in = new Intent(
		                		  LoginActivity.this,
								  MainActivity.class);
						          startActivity(in);
						          finish();
		        }  
		}  
/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	   if (keyCode == KeyEvent.KEYCODE_BACK)
	   {
	      return false;
	   }
	   
	   return super.onKeyDown(keyCode, event);
	}*/
	
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		noHeader();
		//setBackBtnVisibility();
		setBackBtnGone();
		setFooterGone();
		//setNextBtnVisibility();
		
	 
		inflater.inflate(R.layout.activity_sign, container);
		username  = (EditText) findViewById(R.id.username);
		password  = (EditText) findViewById(R.id.password);
		loginBtn  = (Button) findViewById(R.id.btn_login);
		btn_reg = (TextView) findViewById(R.id.btn_reg);
		btn_forget = (TextView) findViewById(R.id.btn_forget);
		btn_forget.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub	
				  Intent intent = new Intent(
						  LoginActivity.this,
							RegisterActivity.class);
				  intent.putExtra("title", "手机验证");
				  startActivity(intent);
				  
			  }
		  });
		btn_reg.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub	
				  Intent intent = new Intent(
						  LoginActivity.this,
							RegisterActivity.class);
				  intent.putExtra("title", "注册");
				  startActivity(intent);
				  
			  }
		  });
		loginBtn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub	
				 final String usernameStr = username.getText().toString();
				 final String passwordStr = password.getText().toString();
				 if(containsEmoji(usernameStr)||containsEmoji(passwordStr)){
						showToast("不支持输入Emoji表情符号");
						return;
					}
				 if(!CommonUtils.isMobile(usernameStr)){
					 showToast("您输入的手机号码格式不正确");
					 return;
				 }else
					
				 if(TextUtils.isEmpty(usernameStr)){
					 showToast( "请输入手机号");
					 return;
				 }else  if(TextUtils.isEmpty(passwordStr)){
					 showToast(  "请输入密码");
					 return;
				 }
				 HashMap<String,String> data = new  HashMap<String,String>(){{
					 put("phoneCode",usernameStr);
					 put("password",passwordStr);
				 }};
				  RequestData request = HttpUtils.simplePostData(Address.HOST+Address.LOGIN, data);
				  startHttpTask(new TaskResultListener(){

					@Override
					public void result(ResposneBundle b) {
						// TODO Auto-generated method stub
						Log.e("result",b.getContent());
						 
						try {
							JSONObject job = new JSONObject(b.getContent());
							if(job.getInt("code")==-1 ){
								/*登陆失败*/
								showToast(job.getString("msg"));
							}else{
								/*登陆成功*/
								JSONObject job2 = job.getJSONObject("data");
								if(job2.has("token")){
									Constants.TOKEN = job2.getString("token");
									Constants.USERNAME = usernameStr;
									Constants.PSW = passwordStr;
									if(job2.has("user")){
										Constants.NICKNAME =  job2.getJSONObject("user").getString("username");
										JSONObject career = job2.getJSONObject("user").getJSONObject("career");
										Constants.PURPOSE =  career.getInt("purpose");
									}
									JSONArray pictures = job2.getJSONObject("user").getJSONArray("pictures");
									 if(pictures.length()>0){
										 ChatListModel model = new ChatListModel();
										 JSONObject picture = pictures.getJSONObject(0);
										 if(picture!=null&&picture.has("filePath")){
											 String relative =picture.getString("filePath");
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
										 
											 
									 } 
									 else
									 {
										 intent2();
									 }
									//Constants.phoneCode = usernameStr;
								}
								
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							intent2();
						}
						
					}

					@Override
					public void failed(final String message) {
						// TODO Auto-generated method stub
						 runOnUiThread(new  Runnable() {
							public void run() {
								showToast(message) ;
							}
						});
						
					}}, request);
				 
				  
			  }
		  });
		
		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});
	}
	private void intent2(){
		SharedPreferences prefs = getSharedPreferences(Constants.SETTING, 0);
		boolean result = prefs.edit()
				.putString("token", Constants.TOKEN )
				.putLong("time", System.currentTimeMillis())
				.putString("USERNAME", Constants.USERNAME)
				//.putString("PSW", Constants.PSW)
				.commit();
		Log.e("result",result+"");
		Log.e("token",Constants.TOKEN);
		Intent intent = new Intent(
				  LoginActivity.this,
					MainActivity.class);
		           startActivity(intent);
		           overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		           finish();
   }
	@Override
	protected void onResume() {
		password.setSelection(password.getText().length(), password.getText().length());
		// TODO Auto-generated method stub
		super.onResume();
		
	}
}
