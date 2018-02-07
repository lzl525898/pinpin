package com.pinpin.ui;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinpin.R;
import com.pinpin.constants.Constants;
import com.pinpin.utils.CommonUtils;

public class AccountInitActivity extends BaseActivity {
	EditText password_content;
	EditText repassword_content;
	TextView content_birthday;
	EditText content_name;
	Button regBtn;
	RelativeLayout rootView;
	private  String[] genderResult =new String[1];
	private  String[] jobResult =new String[1];
	int[] gender_ids = {R.id.btn_gender_male,R.id.btn_gender_female};
	int[] job_ids = {R.id.btn_choose1,R.id.btn_choose2,R.id.btn_choose3};
    Bundle data;
	private class Datelistener implements OnDateSetListener{

		@Override
		public void onDateSet(DatePicker arg0, int y, int m, int d) {
		// TODO Auto-generated method stub
		//调用完日历控件点完成后干的事
			String birthday =String.format("%04d-%02d-%02d", y, m+1, d);
			content_birthday.setText(birthday);
			//content_birthday.setText(y+"-"+(m+1)+'-'+d);
			SharedPreferences birth=getSharedPreferences("Birthday",MODE_PRIVATE);
			SharedPreferences.Editor editor=birth.edit();
			editor.putString("Birthday", birthday);
		}
   }
   class GenderOnClickListener implements View.OnClickListener{
       @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
       	toggleButton((Button)v,genderResult,gender_ids);
		}
	}
	class JobOnClickListener implements View.OnClickListener{
       @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub 
       	toggleButton((Button)v,jobResult,job_ids);
       	showToast("角色信息一经选定注册，将无法修改。");
		}
	}
	void initGender(){
		 for(int i:gender_ids){
			   findViewById(i).setOnClickListener(new GenderOnClickListener());
	     }
 
	}
	void initJob(){
		for(int i:job_ids){
			   findViewById(i).setOnClickListener(new JobOnClickListener());
	     }
 
	}
	void toggleButton(Button v, String[] value,int[] ids){
		// showToast(jobResult+"--"+genderResult+"--"+value);
		   if(TextUtils.equals(value[0], v.getText().toString())){
			   v.setBackgroundResource(R.drawable.btn_sekuai1);
			   value[0] = null;
			}else{
			   
			   v.setBackgroundResource(R.drawable.btn_sekuai2);
			   value[0] =   v.getText().toString();
			   //showToast(jobResult[0]+"--"+genderResult[0]+"--"+value[0]);
			   
			   
		   }
		   for(int i:ids){
			   if(v.getId() == i){
				   continue;
			   }
			   rootView.findViewById(i).setBackgroundResource(R.drawable.btn_sekuai1);  
		   }
	}
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("账号信息");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		data =getIntent().getBundleExtra("data");
		//inflater.inflate(R.layout.activity_account, container);
	    rootView = (RelativeLayout) inflater.inflate(R.layout.activity_account_init, container);
		
	    content_birthday = (TextView) findViewById(R.id.content_birthday);
		password_content = (EditText) findViewById(R.id.password_content);
		repassword_content = (EditText) findViewById(R.id.repassword_content);
		content_name = (EditText) findViewById(R.id.content_name);
		 
		regBtn  = (Button) findViewById(R.id.btn_reg);
		initGender();
		initJob();
		content_birthday.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(AccountInitActivity.this, new Datelistener(), 1990,0,1).show();
				// TODO Auto-generated method stub
			}
		});
		//	LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.activity_setting, null);
	/*	RelativeLayout changePhoneView = (RelativeLayout) rootView.findViewById(R.id.phone_number_layout);	  
		changePhoneView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(AccountInitActivity.this, ChangePhoneActivity.class);
				startActivity(in);
			}
		});*/
		/*RelativeLayout changePasswordView = (RelativeLayout) rootView.findViewById(R.id.password_layout);	  
		changePasswordView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(AccountInitActivity.this, ChangePasswordActivity.class);
				startActivity(in);
			}
		});*/

		//phone_number_layout
		back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		
		regBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 try{
					 
					String password =  password_content.getText().toString();
					String repassword =  repassword_content.getText().toString();
					String birthday =  content_birthday.getText().toString();
					String username =  content_name.getText().toString();
					Constants.NICKNAME = content_name.getText().toString();

					 
					 if(TextUtils.equals("男", genderResult[0])){
						 data.putString("gender", "0");
					 }else  if(TextUtils.equals("女", genderResult[0])){
						 data.putString("gender", "1");
					 }else{
						 showToast("请选择性别");
						 return;
					 }
				     
					 if(TextUtils.equals("找工作", jobResult[0])){
						 data.putString("purpose", "0");
					 }else  if(TextUtils.equals("招牛人", jobResult[0])){
						 data.putString("purpose", "1");
					 }else if(TextUtils.equals("求合伙", jobResult[0])){
						 data.putString("purpose", "2");
					 }else{
						 
						 showToast("请选择我要做什么");
						 return;
					 }
					if(TextUtils.isEmpty(username)){
						 showToast("请您输入显示名");
						 return;
					 }
				     if(TextUtils.isEmpty(birthday)){
						 showToast("请您选择生日");
						 return;
					 }
				     if(containsEmoji(password)||containsEmoji(username)||containsEmoji(repassword)){
				    	 showToast("不支持输入Emoji表情符号");
						 return;
				     }
			     if(TextUtils.isEmpty(password)){
					 showToast("请您输入密码");
					 return;
				 }else if(password.length()<8){
					 showToast("密码长度至少为8位");
					 return;
				 }else if(!CommonUtils.isValidPassword(password)){
					 showToast("您输入密码格式不正确，请包含数字、大小写字母");
					 return;
				 }else if(!TextUtils.equals(password, repassword)){
					 showToast("两次密码输入不一致");
					 return;
				 }
			     data.putString(content_name.getTag().toString(), username);
			     data.putString(content_birthday.getTag().toString(), birthday);
				 data.putString(password_content.getTag().toString(), password);
				 
				 
			     Intent intent = new Intent(
							AccountInitActivity.this,
							PersonalInitActivity.class);
			     intent.putExtra("data", data);
			     startActivity(intent); 
			 }catch(Exception e){
				 e.printStackTrace();
			 }     
				
			 
			}
		});
	}

}