package com.pinpin.ui;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.CommonUtils;
import com.pinpin.utils.Log;

public class RegisterActivity extends BaseActivity implements Callback{
	EditText mobile;
	EditText code;
    TextView area;
	Button regBtn;
	Button codeBtn;
	String title;
	 Bundle data0 = new Bundle();
	private boolean ready;
	private MyCountDownTimer mCountDownTimer; 
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		title=intent.getStringExtra("title");
		setTitle(title);
		
		setBackBtnGone();
		setSearchBtnGone();
		setFooterGone();
		initSDK();
		mCountDownTimer = new MyCountDownTimer(60000, 1000); 
		
		inflater.inflate(R.layout.activity_reg, container);
		area  = (TextView) findViewById(R.id.area);
		mobile  = (EditText) findViewById(R.id.mobile);
		code  = (EditText) findViewById(R.id.code);
		regBtn  = (Button) findViewById(R.id.btn_reg);
		if(!TextUtils.equals(title, "注册"))
		{
			regBtn.setText("下一步");
		}
		codeBtn  = (Button) findViewById(R.id.btn_code);
		codeBtn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(final View v) {
				// TODO Auto-generated method stub	
				  
				  Log.e("-----","212121212");
					   String usernameStr = mobile.getText().toString();
					   String areaStr = area.getText().toString().trim();
					 if(!CommonUtils.isMobile(usernameStr)){
						 showToast("您输入的手机号码格式不正确");
						 return;
					 }
					 else if(TextUtils.isEmpty(usernameStr)){
						 showToast( "请输入手机号");
						 return;
					 } 
					 v.setEnabled(false);
					 usernameStr = usernameStr.trim().replaceAll("\\s*", "");
						SMSSDK.getVerificationCode("86",
								usernameStr, new OnSendMessageHandler(){

									@Override
									public boolean onSendMessage(String arg0,
											String arg1) {
										Log.e("send1",arg0);
										Log.e("send2",arg1);
										v.post(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
												Toast.makeText(RegisterActivity.this, "短信验证码已发送，请稍候片刻", Toast.LENGTH_LONG).show();
												mCountDownTimer.start(); 
											}
										});
										
										Log.e("send1",arg0);
										Log.e("send2",arg1);
									
										//v.setEnabled(false);
										
										// TODO Auto-generated method stub
										return false;
									}});
					// 打开注册页面
					/*	RegisterPage registerPage = new RegisterPage();
						registerPage.setRegisterCallback(new EventHandler() {
							public void afterEvent(int event, int result, Object data) {
								// 解析注册结果
								if (result == SMSSDK.RESULT_COMPLETE) {
									@SuppressWarnings("unchecked")
									HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
									String country = (String) phoneMap.get("country");
									String phone = (String) phoneMap.get("phone");
									// 提交用户信息
									registerUser(country, phone);
								}
							}
						});
						registerPage.show(RegisterActivity.this);*/
					 
					  
			  }
		  });
		
		regBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//*****测试
				/*Intent intent = new Intent(
						  RegisterActivity.this,
						  PersonalInitActivity.class);
				          startActivity(intent);
				          finish();
				          int a = 1;
				          if(a==1){
				        	  return;
				          } */
				         
				
				//******测试end

				 final String usernameStr = mobile.getText().toString();
				 final String codeStr = code.getText().toString();
				 if(!CommonUtils.isMobile(usernameStr)){
					 showToast("您输入的手机号码格式不正确");
					 return;
				 }
				 else if(TextUtils.isEmpty(usernameStr)){
					 showToast( "请输入手机号");
					 return;
				 }else  if(TextUtils.isEmpty(codeStr)){
					 showToast(  "请输入验证码");
					 return;
				 }
				 HashMap<String,String> data = new  HashMap<String,String>(){{
					 put("phoneCode",usernameStr);
				     put("verificationCode",codeStr);
				 }};
				 
				 String url = Address.HOST+Address.CHECK_CODE;
				 if(!TextUtils.equals(title, "注册"))
				 {
					 url = Address.HOST+Address.CHECK_CODE_ONLY;
				 }
				  RequestData request = HttpUtils.simplePostData(url, data);
				  startHttpTask(new TaskResultListener(){

					@Override
					public void result(ResposneBundle b) {
						// TODO Auto-generated method stub
						Log.e("result",b.getContent());
						 if(b.getContent()==null){
							 showToast("出错了，服务器异常");
							 return;
						 }
						try {
							JSONObject job = new JSONObject(b.getContent());
							if(job.getInt("code")==-1 ){
								showToast(job.getString("msg"));
							}else{
								showToast(job.getString("msg"));
								data0.putString("phoneCode",usernameStr);
								Intent intent;
								if(!TextUtils.equals(title, "注册"))
								{
									intent = new Intent(
											  RegisterActivity.this,
											  ResetPasswordActivity.class);
								}
								else
								{
									intent = new Intent(
											  RegisterActivity.this,
											  AccountInitActivity.class);
								}
								intent.putExtra("data", data0);
						        startActivity(intent);
						        finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
	}
	
	
	class MyCountDownTimer extends CountDownTimer { 
	   
	    public MyCountDownTimer(long millisInFuture, long countDownInterval) { 
	      super(millisInFuture, countDownInterval); 
	    } 
	  
	    @Override
	    public void onFinish() { 
	    	codeBtn.setText("获取验证码"); 
	    	codeBtn.setEnabled(true);
	    } 
	  
	    @Override
	    public void onTick(long millisUntilFinished) { 
	      Log.i("MainActivity", millisUntilFinished + ""); 
	      codeBtn.setText("倒计时" + millisUntilFinished / 1000 + "秒"); 
	    } 
	}
	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, Constants.APPKEY, Constants.APPSECRET);
		final Handler handler = new Handler(this);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
		ready = true;
	}
	
	public boolean handleMessage(Message msg) {
		 

		int event = msg.arg1;
		int result = msg.arg2;
		Object data = msg.obj;
		if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
			// 短信注册成功后，返回MainActivity,然后提示新好友
			if (result == SMSSDK.RESULT_COMPLETE) {
				Toast.makeText(this, R.string.smssdk_user_info_submited, Toast.LENGTH_SHORT).show();
			} else {
				((Throwable) data).printStackTrace();
			}
		} 
		return false;
	}
	
	 
	protected void onDestroy() {
		if (ready) {
			// 销毁回调监听接口
			SMSSDK.unregisterAllEventHandler();
		}
		 mCountDownTimer.cancel();
		super.onDestroy();
	}
	
	/** 分割电话号码 */
	private String splitPhoneNum(String phone) {
		StringBuilder builder = new StringBuilder(phone);
		builder.reverse();
		for (int i = 4, len = builder.length(); i < len; i += 5) {
			builder.insert(i, ' ');
		}
		builder.reverse();
		return builder.toString();
	}

}
