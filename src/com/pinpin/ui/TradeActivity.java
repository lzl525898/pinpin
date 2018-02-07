package com.pinpin.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.adapter.TradeAdapter;
import com.pinpin.view.CustLinearLayout;
import com.pinpin.view.CustLinearLayout.OnItemClickListener;
import com.pinpin.view.CustLinearLayout.OnItemLongClickListener;

public class TradeActivity extends BaseActivity {

	public static final String[] TRADE =  {"IT","媒体/公关","金融","法律","咨询","文化/艺术","影视/娱乐","教育/科研","学生"};
	public static final String[] LABLES =  {"架构师","工作狂","软件工程师","硬件工程师","HTML5","IOS","Android","PHP","GROOVY","技术宅","闪客","攻城狮","程序猿","红客"};
	public static final String[] WORKPLACE =  {"北京","上海","天津","重庆","安徽","河南","湖南","湖北","河北","山东"};
	public static final String[] LIMITED =  {"无","1年","2年","3年","3-5年","5-8年","8-10年","10年以上"};
	public static final String[] SALARY =  {"2000元以下","2000-4000元","4000-6000元","6000-8000元","8000-10000元","10000-15000元","15000-20000元","20000-30000元","30000元以上","面议"};
	public static final String[] NUMBER =  {"0-20人","20-99人","100-499人","500-999人","1000-4999人","5000-9999人","10000人以上"};
	
	RelativeLayout rootView;
	CustLinearLayout listView;
	String title;
	List<String> sourceList;
	TradeAdapter adapter;
	JSONArray tagArr;
	
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
//		setTitle("行业");
		Intent intent = getIntent();
		title = intent.getStringExtra("title").toString().trim();
		setTitle(title);
		sourceList = new ArrayList<String>();
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		
		 
		rootView = (RelativeLayout) inflater.inflate(R.layout.activity_trade, container);
		listView =(CustLinearLayout) rootView.findViewById(R.id.listview);
		
		//final List list = Arrays.asList(TRADE);  
	//List list = new ArrayList();
	//	final List<String> list = new ArrayList<String>();		 
		if( title.equals("行业") )
		{
			Collections.addAll(sourceList, TRADE);
		}
		else if( title.equals("工作年限") )
		{
			Collections.addAll(sourceList, LIMITED);
		}
		else if( title.equals("薪酬") )
		{
			Collections.addAll(sourceList, SALARY);
		}
		else if( title.equals("期望工作地") )
		{
			Collections.addAll(sourceList, WORKPLACE);
		}
		else if( title.equals("公司人数") )
		{
			Collections.addAll(sourceList, NUMBER);
		}
		else //if( title.equals("个性标签") )
		{
			Collections.addAll(sourceList, LABLES);
		}
		
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				// TODO Auto-generated method stub
				String value  = sourceList.get(position);
				Intent data = new Intent();
				data.putExtra("result", value);
				setResult(RESULT_OK, data);
				finish();
			}
			
		});
        
        
        adapter= new TradeAdapter(this,sourceList);
		listView.setAdapter(adapter);
		if( title.equals("个性标签") )
		{
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	            @Override
	            public boolean onItemLongClick(int position) {
	            // TODO Auto-generated method stub
	            	final int pos = position;
	            	showAlertDialogWithCancel("提醒", "要删除个性标签吗？", new DialogInterface.OnClickListener() {
						@SuppressLint("NewApi")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

			                String value  = sourceList.get(pos);
			            	deleteTag(value);
						 }
					}, "确定");

		            return true;
	            }
	        });
			refreshTag();
		}
		
		//RelativeLayout maskView = (RelativeLayout) rootView.findViewById(R.id.trade_activity_layout);
		
//		final RelativeLayout layout = new RelativeLayout(this);
//		//setContentView(layout);
//		TextView text = new TextView(this);
//		text.setText("TTTTIIII");
//		layout.addView(text);
//		rootView.addView(layout);
		Button create_btn = (Button) findViewById(R.id.btn_chuangjiandi);
		if( title.equals("工作年限") || title.equals("公司人数") )
		{
			create_btn.setVisibility(View.GONE);
		}
		create_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent in = new Intent(TradeActivity.this,
//						ChangeMessageActivity.class);
//				in.putExtra("title", title);
//				startActivityForResult(in, 1000);
				if( title.equals("个性标签") )
				{
					if (Constants.TOKEN == null || Constants.TOKEN.equals(""))
					{
						showToast("功能尚在准备，敬请期待！");
					}
					else
					{
						
						inputTitleDialog();
					}
				}
				else
				{
					showToast("功能尚在准备，敬请期待！");
				}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1001)
         {
        	//View view = inflater.inflate(R.layout.activity_personal, null);
        	//TextView textView = (TextView)rootView.findViewById(R.id.position_content);
            //String result_value = data.getStringExtra("result");
            //textView.setText(result_value);
         }
    }
	
	private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入个性标签").setView(inputServer).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("添加",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                    	
                    	 String inputName = inputServer.getText().toString();
	                        String digits = " /\\:*?<>|\"\n\t";
	                        if(containsEmoji(inputName)){
	    						showToast("不支持输入Emoji表情符号");
	    						return;
	    					}
	                        for (int i = 0; i < inputName.length(); i++) {
	                        	  if (digits.indexOf(inputName.charAt(i)) > 0) {
	                        		  showToast("不能有特殊符号");
	                              	return;
	                        	  }
	                        	 }
	                        if(inputName.equals("")&&TextUtils.isEmpty(inputName)||inputName.trim().isEmpty()){
	                        	showToast("个性标签不能为空");
	                        	return;
	                        }else{
	                        	inputName=inputName.replace(" ","");
	                        }	                        
	                        if(sourceList.contains(inputName)){
	                        	showToast("该标签已存在于列表中");
	                        	return;
	                        }
	                        final JSONArray arr =new JSONArray();
	                        	arr.put(inputName);
	                            updateTagArray(arr);
	                    }
	                });
        builder.show();
    }

	private void deleteTag(final String tag) {
		 HashMap<String,String> data = new  HashMap<String,String>(){{
			put("tag",tag);
			put("token",Constants.TOKEN);
		 }};
		  RequestData request = HttpUtils.simplePostData(Address.HOST+Address.DELETE_TAG, data);
		  startHttpTask(new TaskResultListener(){

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result",b.getContent());
				 
				try {
					JSONObject job = new JSONObject(b.getContent());
					if(job.getInt("code")==-1 ){
						showToast(job.getString("msg"));
					}else{
						showToast(job.getString("msg"));
						refreshTag();
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
	
	private void updateTagArray(final JSONArray arr) {
		 HashMap<String,String> data = new  HashMap<String,String>(){{
			put("tags",arr.toString());
			put("token",Constants.TOKEN);
		 }};
		  RequestData request = HttpUtils.simplePostData(Address.HOST+Address.ADD_TAG, data);
		  startHttpTask(new TaskResultListener(){

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result",b.getContent());
				 
				try {
					JSONObject job = new JSONObject(b.getContent());
					if(job.getInt("code")==-1 ){
						showToast(job.getString("msg"));
					}else{
						showToast(job.getString("msg"));
						refreshTag();
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
	
	private void refreshTag(){
		 HashMap<String,String> data = new  HashMap<String,String>(){{
				 put("token",Constants.TOKEN);
			 }};
			  RequestData request = HttpUtils.simplePostData(Address.HOST+Address.GET_ALL_TAG, data);
			  startHttpTask(new TaskResultListener(){

				@Override
				public void result(ResposneBundle b) {
					// TODO Auto-generated method stub
					Log.e("result",b.getContent());
					 
					try {
						JSONObject job = new JSONObject(b.getContent());
						if(job.getInt("code")==-1 ){
							showToast(job.getString("msg"));
						}else{
							tagArr = job.getJSONArray("data");
							sourceList.clear();
							for (int i = 0; i < tagArr.length(); i++) {
								JSONObject tagJson = tagArr.getJSONObject(i);
				//				tradeStr = career.getString(trade_content.getTag().toString());
								String msg = tagJson.getString("name");
								sourceList.add(msg);
							}
							listView.refresh();
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
}
