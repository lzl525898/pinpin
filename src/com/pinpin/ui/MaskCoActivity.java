package com.pinpin.ui;

import java.util.ArrayList;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.adapter.MaskItemAdapter;
import com.pinpin.utils.Log;

public class MaskCoActivity extends BaseActivity {
	 public static final String[] CO =  {"","","",""};
     GridView mask_gridview ;
     List<String> sourceList ;
     String title;
     String type;
//     List<Boolean> checkedList ;
     MaskItemAdapter adapter;
     JSONArray tagArr;
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
	//	setTitle("屏蔽公司");
		Intent intent = getIntent();
		title = intent.getStringExtra("title").toString().trim();
		type = intent.getStringExtra("type").toString().trim();
		setTitle(title);
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
	    sourceList = new ArrayList<String>();	
		RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.activity_mask_co, container);
		mask_gridview = (GridView) rootView.findViewById(R.id.mask_gridview);
		adapter= new MaskItemAdapter(this,sourceList);
		mask_gridview.setAdapter(adapter);

    	//Collections.addAll(sourceList, CO);
		/*set --> get mask from service 
		 * init --> don't get mask from service
		 * */
		if(type.equals("set")){
			refreshCo();
		}else if(type.equals("init")){
			
		}
    	

//		RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.activity_mask_co, container);
//		mask_gridview = (GridView) rootView.findViewById(R.id.mask_gridview);
//		adapter= new MaskItemAdapter(this,sourceList);
//		mask_gridview.setAdapter(adapter);
    	mask_gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
	    	@Override
	    	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	// TODO Auto-generated method stub
	    		final int pos = position;
				showAlertDialogWithCancel("提醒", "要删除选定的标签吗？", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						final JSONArray arr =new JSONArray();
		                for (int i = 0; i < sourceList.size(); i++) {
		                	if(i!=pos)
		                	{
		                		arr.put(sourceList.get(i));
		                	}
						}
		                updateCoArray(arr);
					 }
				}, "确定");

		    	return true;
	    	}

	    });
		mask_gridview.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				final int pos = position;
//				showAlertDialogWithCancel("提醒", "要删除选定的标签吗？", new DialogInterface.OnClickListener() {
//					@SuppressLint("NewApi")
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						// TODO Auto-generated method stub
//						final JSONArray arr =new JSONArray();
//		                for (int i = 0; i < sourceList.size(); i++) {
//		                	if(i!=pos)
//		                	{
//		                		arr.put(sourceList.get(i));
//		                	}
//						}
//		                updateCoArray(arr);
//					 }
//				}, "确定");
				
                
//				boolean result = checkedList.get(position);
//				View v = view.findViewById(R.id.btn_mask);
//				if(result){
//					checkedList.set(position, false);
//					v.setBackgroundResource(R.drawable.blue);
//				}
//				else{
//					checkedList.set(position, true);
//					v.setBackgroundResource(R.drawable.red);
//				}
			}
		});
		back_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent data = new Intent();
//				ArrayList<String> results = new ArrayList<String>();
//				for(int i =0;i<checkedList.size();i++){
//					if(checkedList.get(i)){
//						results.add(sourceList.get(i));
//					}
//				}
//				data.putExtra("result", results);
//				setResult(RESULT_OK, data);
				
				finish();
			}
		});
		
		Button create_btn = (Button) findViewById(R.id.btn_chuangjiandi);
		create_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent in = new Intent(TradeActivity.this,
//						ChangeMessageActivity.class);
//				in.putExtra("title", title);
//				startActivityForResult(in, 1000);
				//showToast("功能尚在准备，敬请期待！");
				inputTitleDialog() ;
			}
		});

 		 
	}
	private void updateCoArray(final JSONArray arr) {
		 HashMap<String,String> data = new  HashMap<String,String>(){{
 			put("cos",arr.toString());
 			put("token",Constants.TOKEN);
 		 }};
 		  String url=Address.HOST+Address.MASK_CO;
 		  RequestData request = HttpUtils.simplePostData(url, data);
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
 						refreshCo();
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
	
	 private void inputTitleDialog() {

	        final EditText inputServer = new EditText(this);
	        inputServer.setFocusable(true);

	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("输入公司名称").setView(inputServer).setNegativeButton(
	                "取消", null);
	        builder.setPositiveButton("添加",
	                new DialogInterface.OnClickListener() {

	                    public void onClick(DialogInterface dialog, int which) {
	                    	
	                        String inputName = inputServer.getText().toString();
	                        if(containsEmoji(inputName)){
	    						showToast("不支持输入Emoji表情符号");
	    						return;
	    					}
	                        if(inputName.equals("")&&TextUtils.isEmpty(inputName)||inputName.trim().isEmpty()){
	                        	showToast("屏蔽标签不能为空");
	                        	return;
	                        }	           
	                        if(sourceList.contains(inputName)){
	                        	showToast("该标签已存在于列表中");
	                        	return;
	                        }
	                 
	                        
	                        if(inputName.length()>15){
	                        	showToast("标签过长");
	                        	return;
	                        }
	                        final JSONArray arr =new JSONArray();
	                        for(String co:sourceList){
	                        	 arr.put(co);
	                        }
	                        arr.put(inputName);
	                        updateCoArray(arr);
	                    }
	                });
	        builder.show();
	    }
	 
	 private void refreshCo(){
		 HashMap<String,String> data = new  HashMap<String,String>(){{
				 put("token",Constants.TOKEN);
			 }};
			  RequestData request = HttpUtils.simplePostData(Address.HOST+Address.GET_MASK_CO, data);
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
							JSONArray data = job.getJSONArray("data");
							 
								if(data == null) return;
								sourceList.clear();
								for (int i = 0; i < data.length(); i++) {
									sourceList.add(data.getString(i));
								}
								adapter.notifyDataSetChanged();
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
