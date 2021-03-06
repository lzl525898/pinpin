package com.pinpin.ui;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.CommonUtils;
import com.pinpin.utils.Log;

public class ResetPasswordActivity extends BaseActivity {

	EditText old_password;
	EditText new_password;
	EditText confirm_password;
	Bundle data0;

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("密码设置");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		next_btn.setText("保存");
		setNextBtnVisibility();

		data0 =getIntent().getBundleExtra("data");
		inflater.inflate(R.layout.activity_reset_password, container);

		new_password = (EditText) findViewById(R.id.new_password_txt);
		confirm_password = (EditText) findViewById(R.id.confirm_password_txt);
		
		next_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String newPassword =  new_password.getText().toString();
				String confirmPassword =  confirm_password.getText().toString();
				 if(containsEmoji(newPassword)||containsEmoji(confirmPassword)){
						showToast("不支持输入Emoji表情符号");
						return;
					}
				if(TextUtils.isEmpty(newPassword)){
					 showToast("请您输入新密码");
					 return;
				 }else if(newPassword.length()<8){
					 showToast("新密码长度至少为8位");
					 return;
				 }else if(!CommonUtils.isValidPassword(newPassword)){
					 showToast("您输入新密码格式不正确，请包含数字、大小写字母");
					 return;
				 }else if(!TextUtils.equals(newPassword, confirmPassword)){
					 showToast("两次新密码输入不一致");
					 return;
				 }
				 HashMap<String,String> data = new  HashMap<String,String>(){{
					 
					 put("phoneCode",data0.getString("phoneCode"));
					 put("password",new_password.getText().toString());
				     
				 }};
				  RequestData request = HttpUtils.simplePostData(Address.HOST+Address.RESET_PASSWORD, data);
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
							showToast(job.getString("msg"));
							if(job.getInt("code")==-1 ){
							//	showToast(job.getString("msg"));
							}else{
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
		back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		
	}

}