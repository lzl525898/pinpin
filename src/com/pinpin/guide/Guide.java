package com.pinpin.guide;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.model.ChatListModel;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpTask.TaskResultListener2;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.BaseActivity;
import com.pinpin.ui.LoginActivity;
import com.pinpin.ui.MainActivity;
import com.pinpin.utils.ImageUtils;
import com.pinpin.utils.Log;

public class Guide extends BaseActivity  implements OnViewChangeListener{

	private static final String TAG = "Guide";
	private ScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private Button startBtn;
	private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;
	SharedPreferences pref ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置无标题
		//Guide.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		noHeader();
		setFooterGone();
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
							Log.e("得到Constants.NICKNAME","--"+Constants.NICKNAME);
							JSONArray pictures = user.getJSONArray("pictures");
							 if(pictures.length()>0){
								 ChatListModel model = new ChatListModel();
								 String relative =pictures.getJSONObject(0).getString("filePath");
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
	private void intent(){
		Intent intent = new Intent(
				Guide.this,
				LoginActivity.class);
		Guide.this.startActivity(intent);
	    
		Guide.this.finish();
	}
	private void intent2(){
	Intent intent = new Intent(
			  Guide.this,
			  MainActivity.class);
	          startActivity(intent);
	          overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	  		  Guide.this.finish();
	          }
	/**
	 * 初始化
	 */
	private void init()
	{
		mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		//mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		startBtn = (Button) findViewById(R.id.startBtn);
		startBtn.setOnClickListener(onClick);
		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for (int i = 0; i < count; i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}
	
	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.startBtn:
				pref.edit().putInt(Constants.NEED_WELCOME_PAGE, 1).commit();
				mScrollLayout.setVisibility(View.GONE);
				pointLLayout.setVisibility(View.GONE);
				//mainRLayout.setBackgroundResource(R.drawable.whatsnew_bg);
				start();
			}
		}
	};

	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
	}

	private void setcurrentPoint(int position) {
		if (position < 0 || position > count - 1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.guide, menu);
		return true;
	}
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		 
		// TODO Auto-generated method stub
		pref = getSharedPreferences(Constants.SETTING, 0);
		int value = pref.getInt(Constants.NEED_WELCOME_PAGE,-1);
		if(value == 1){
			start();
		}else{
			inflater.inflate(R.layout.guide, container);
			init();
		}
	}

}
