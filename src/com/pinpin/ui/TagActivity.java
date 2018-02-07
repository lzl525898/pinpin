package com.pinpin.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
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
import com.pinpin.model.UserInfo;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.adapter.TagItemAdapter;
import com.pinpin.utils.IOUtil;
import com.pinpin.utils.Log;
import com.pinpin.utils.MyGridView;

public class TagActivity extends BaseActivity {
	public static final String[] TRADE = { "IT", "媒体/公关", "金融", "法律", "咨询", "文化/艺术", "影视/娱乐", "教育/科研", "学生" };
	public static final String[] LABLES = { "架构师", "工作狂", "软件工程师", "硬件工程师", "HTML5", "IOS", "Android", "PHP", "GROOVY",
			"技术宅", "闪客", "攻城狮", "程序猿", "红客" };
	public static final String[] WORKPLACE = { "北京", "上海", "天津", "重庆", "安徽", "河南", "湖南", "湖北", "河北", "山东" };
	MyGridView tag_gridview;
	List<String> sourceList;
	String title;
	String params;
	String phoneCode;
	List<Boolean> checkedList;
	TagItemAdapter adapter;
	JSONArray tagArr;

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		title = intent.getStringExtra("title").toString().trim();
		phoneCode = intent.getStringExtra("phoneCode").toString().trim();
		setTitle(title);
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		sourceList = new ArrayList<String>();
		checkedList = new ArrayList<Boolean>();
		RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.activity_tag, container);
		tag_gridview = (MyGridView) rootView.findViewById(R.id.tag_gridview);
		adapter = new TagItemAdapter(this, sourceList);
		tag_gridview.setAdapter(adapter);
		/*
		 * if( title.equals("期望工作地") ) { //Collections.addAll(sourceList, CO);
		 * refreshPlace(); } else if( title.equals("行业") ) {
		 * //Collections.addAll(sourceList, CO); refreshTrade(); } else //if(
		 * title.equals("个性标签") ) { //Collections.addAll(sourceList, LABLES);
		 * refreshTag(); }
		 */
		refreshTag();

		tag_gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				final int pos = position;
				showAlertDialogWithCancel("提醒", "要删除选定的标签吗？", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						/*
						 * if( title.equals("期望工作地") ) { final JSONArray arr
						 * =new JSONArray(); for (int i = 0; i <
						 * sourceList.size(); i++) { if(i!=pos) {
						 * arr.put(sourceList.get(i)); } }
						 * updatePlaceArray(arr); }
						 */
						String value = sourceList.get(pos);
						deleteTag(value);
					}
				}, "确定");

				return true;
			}

		});

		tag_gridview.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				/*
				 * int count = 0; for(int i =0;i<checkedList.size();i++){
				 * if(checkedList.get(i)){ count++; } } if(3<=count) {
				 * showToast("选择的标签不能超过3个"); return; } boolean result =
				 * checkedList.get(position); View v =
				 * view.findViewById(R.id.btn_tag); if(result){
				 * checkedList.set(position, false);
				 * v.setBackgroundResource(R.drawable.btn_sekuai1); } else{
				 * checkedList.set(position, true);
				 * v.setBackgroundResource(R.drawable.btn_sekuai2); }
				 */
				final int pos = position;
				String value = sourceList.get(position);
				Intent data = new Intent();
				data.putExtra("result", value);
				setResult(RESULT_OK, data);
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

		Button create_btn = (Button) findViewById(R.id.btn_chuangjiandi);
		create_btn.setText("创建" + title);
		create_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent in = new Intent(TradeActivity.this,
				// ChangeMessageActivity.class);
				// in.putExtra("title", title);
				// startActivityForResult(in, 1000);
				// showToast("功能尚在准备，敬请期待！");
				inputTitleDialog();
			}
		});

	}

	private void inputTitleDialog() {

		final EditText inputServer = new EditText(this);
		inputServer.setFocusable(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("输入标签名称").setView(inputServer).setNegativeButton("取消", null);
		builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String inputName = inputServer.getText().toString();
				String digits = " /\\:*?<>|\"\n\t";

				for (int i = 0; i < inputName.length(); i++) {
					if (digits.indexOf(inputName.charAt(i)) > 0) {
						showToast("不能有特殊符号");
						return;
					}
				}
				if (TextUtils.isEmpty(inputName) || inputName.trim().isEmpty()) {
					showToast("个性标签不能为空");
					return;
				} else {
					inputName = inputName.replace(" ", "");
				}
				if (inputName.toString().length() > 10) {
					showToast("标签不能超过10个字");
					return;
				}
				if (sourceList.contains(inputName)) {
					showToast("该标签已存在于列表中");
					return;
				}
				final JSONArray arr = new JSONArray();
				arr.put(inputName);
				updateTagArray(arr);
			}
		});
		builder.show();
	}

	private void deleteTag(final String tag) {
		String url;
		if (title.equals("期望工作地")) {
			url = Address.HOST + Address.DELETE_PLACE;
			params = "workPlace";
		} else if (title.equals("行业")) {
			url = Address.HOST + Address.DELETE_TRADE;
			params = "trade";
		} else // if( title.equals("个性标签") )
		{
			url = Address.HOST + Address.DELETE_TAG;
			params = "tag";
		}

		HashMap<String, String> data = new HashMap<String, String>() {
			{
				put(params, tag);
				put("token", Constants.TOKEN);
				put("phoneCode", phoneCode);
			}
		};
		RequestData request = HttpUtils.simplePostData(url, data);
		startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result", b.getContent());

				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") == -1) {
						showToast(job.getString("msg"));
					} else {
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
				runOnUiThread(new Runnable() {
					public void run() {
						showToast(message);
					}
				});

			}
		}, request);
	}

	private void refreshTag() {
		HashMap<String, String> data = new HashMap<String, String>() {
			{
				put("token", Constants.TOKEN);
				put("phoneCode", phoneCode);
				if (title.equals("公司标签")) {
					put("role", "1");
				} else if (title.equals("个性标签")) {
					put("role", "0");
				} else if (title.equals("我的标签")) {
					put("role", "2");
				}
			}
		};
		String url;
		if (title.equals("期望工作地")) {
			url = Address.HOST + Address.GET_ALL_PLACE;
		} else if (title.equals("行业")) {
			url = Address.HOST + Address.GET_ALL_TRADE;
		} else // if( title.equals("个性标签") )
		{
			url = Address.HOST + Address.GET_ALL_TAG;
		}
		RequestData request = HttpUtils.simplePostData(url, data);
		startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result", b.getContent());

				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") == -1) {
						showToast(job.getString("msg"));
					} else {
						tagArr = job.getJSONArray("data");
						sourceList.clear();
						checkedList.clear();
						for (int i = 0; i < tagArr.length(); i++) {
							JSONObject tagJson = tagArr.getJSONObject(i);
							// tradeStr =
							// career.getString(trade_content.getTag().toString());
							String msg = tagJson.getString("name");
							sourceList.add(msg);
							checkedList.add(false);
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
				runOnUiThread(new Runnable() {
					public void run() {
						showToast(message);
					}
				});

			}
		}, request);
	}

	private void updateTagArray(final JSONArray arr) {
		String url;
		if (title.equals("期望工作地")) {
			url = Address.HOST + Address.ADD_PLACE;
			params = "workPlaces";
		} else if (title.equals("行业")) {
			url = Address.HOST + Address.ADD_TRADE;
			params = "trades";
		} else // if( title.equals("个性标签") )
		{
			url = Address.HOST + Address.ADD_TAG;
			params = "tags";
		}
		HashMap<String, String> data = new HashMap<String, String>() {
			{
				if (title.equals("公司标签")) {
					put("role", "1");
				} else if (title.equals("个性标签")) {
					put("role", "0");
				} else if (title.equals("我的标签")) {
					put("role", "2");
				}
				put(params, arr.toString());
				put("token", Constants.TOKEN);
				put("phoneCode", phoneCode);
			}
		};

		RequestData request = HttpUtils.simplePostData(url, data);
		startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result", b.getContent());
				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") == -1) {
						showToast(job.getString("msg"));
					} else {
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
				runOnUiThread(new Runnable() {
					public void run() {
						showToast(message);
					}
				});

			}
		}, request);
	}

}
