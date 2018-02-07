package com.pinpin.ui;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.utils.AutoDownLoadManager;
import com.pinpin.utils.Log;
import com.pinpin.utils.SystemUtils;
import com.pinpin.utils.VersionCheckManager;

public class AboutActivity extends BaseActivity {

	private String version;
	private String downloadUrl;
    private WebView webView;  

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("关于我们");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
	//	setNextBtnVisibility();
	//	next_btn.setText("提交");

		inflater.inflate(R.layout.activity_about, container);
		RelativeLayout updateView = (RelativeLayout) findViewById(R.id.update_version);
		updateView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
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
										Toast.makeText(AboutActivity.this, "网络不给力！", Toast.LENGTH_SHORT).show();
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
												 
												
												
												//Log.e("version","客户端版本:"+versionName+"  服务器版本:"+version);
												if(!TextUtils.isEmpty(version)&&!TextUtils.equals(versionName, version)){
													showAlertDialogWithCancel("客户端有新版本","是否更新客户端？",new DialogInterface.OnClickListener(){

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															// TODO Auto-generated method stub
															
															 if(!TextUtils.isEmpty(downloadUrl)){
																 final AutoDownLoadManager adlm= new AutoDownLoadManager(AboutActivity.this);	
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
														}},"暂不更新");
												  
											    }else{
											    	Toast.makeText(AboutActivity.this, "已是最新版本，不需更新", Toast.LENGTH_SHORT).show();
											    }
											}
											
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace(); 
										}
									}
									 
								}
							});

					checkMng.checkVersion(Address.VERSION_FILE);
				  
			  }
		  });
        TextView softinfo=(TextView)findViewById(R.id.term_txt);
        //inflater.inflate(R.layout.soft_info, container);
        softinfo.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  //showToast("从服务器端加载使用条款和隐私政策！");
				  Intent intent = new Intent(AboutActivity.this, SoftInfoActivity.class);
				  startActivity(intent);
				  finish();
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
