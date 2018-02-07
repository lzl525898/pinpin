package com.pinpin.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.Log;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingInitActivity extends BaseActivity {
	Button regBtn;
	int fromAge = 18;
	int toAge = 70;
	RelativeLayout rootView;
	protected int nFlag = 0;
	protected boolean bFlagZhaopin = false;
	protected boolean bFlagQiuzhi = false;
	protected boolean bFlagHehuo = false;
	protected boolean bFlagLianxiren = false;
	protected boolean bFlagGongsi = false;
	ToggleButton button_mask_co;
	ToggleButton button_mask_contact;
	Bundle data;
	boolean maskContact;
	protected boolean maskCo = false;
	Button btn_gender_male;
	Button btn_gender_female;
	Button btn_gender_unlimited;
	// Button btn_job_1;
	// Button btn_job_2;
	// Button btn_job_3;
	// Button btn_job_4;

	String genderResult;
	String jobResult;
	// private String[] genderResult =new String[1];
	// private String[] jobResult =new String[1];
	int[] job_ids = { R.id.btn_choose1, R.id.btn_choose2, R.id.btn_choose3 };

	class GenderOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_gender_male:
				btn_gender_male.setBackgroundResource(R.drawable.icon_nan_a);
				btn_gender_female.setBackgroundResource(R.drawable.icon_nv_a);
				btn_gender_unlimited.setBackgroundResource(R.drawable.btn_sekuai1);
				genderResult = "男";
				break;
			case R.id.btn_gender_female:
				btn_gender_female.setBackgroundResource(R.drawable.icon_nv_b);
				btn_gender_male.setBackgroundResource(R.drawable.icon_nan_b);
				btn_gender_unlimited.setBackgroundResource(R.drawable.btn_sekuai1);
				genderResult = "女";
				break;
			case R.id.btn_gender_unlimited:
				btn_gender_unlimited.setBackgroundResource(R.drawable.buxian);
				btn_gender_unlimited.setTextColor(Color.WHITE);
				btn_gender_female.setBackgroundResource(R.drawable.icon_nv_a);
				btn_gender_male.setBackgroundResource(R.drawable.icon_nan_b);
				genderResult = "不限";
				break;

			default:
				break;
			}
		}
	}

	class JobOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			toggleButton((Button) v, jobResult, job_ids, 1);
		}
	}

	void initGender() {

		btn_gender_male = (Button) findViewById(R.id.btn_gender_male);
		btn_gender_male.setOnClickListener(new GenderOnClickListener());
		btn_gender_female = (Button) findViewById(R.id.btn_gender_female);
		btn_gender_female.setOnClickListener(new GenderOnClickListener());
		btn_gender_unlimited = (Button) findViewById(R.id.btn_gender_unlimited);
		btn_gender_unlimited.setOnClickListener(new GenderOnClickListener());
	}

	void initJob() {
		for (int i : job_ids) {
			findViewById(i).setOnClickListener(new JobOnClickListener());
		}
		// btn_job_1 = (Button) findViewById(R.id.btn_choose1);
		// btn_job_1.setOnClickListener(new JobOnClickListener());
		// btn_job_2 = (Button) findViewById(R.id.btn_choose2);
		// btn_job_2.setOnClickListener(new JobOnClickListener());
		// btn_job_3 = (Button) findViewById(R.id.btn_choose3);
		// btn_job_3.setOnClickListener(new JobOnClickListener());
		// btn_job_4 = (Button) findViewById(R.id.btn_choose4);
		// btn_job_4.setOnClickListener(new JobOnClickListener());
	}

	void toggleButton(Button v, String value, int[] ids, int type) {
		if (TextUtils.equals(value, v.getText().toString())) {
//			v.setBackgroundResource(R.drawable.btn_sekuai1);
//			v.setTextColor(getResources().getColor(R.color.set_text));
			value = null;
		} else {

			v.setBackgroundResource(R.drawable.buxian);
			v.setTextColor(Color.WHITE);
			// value[0] = v.getText().toString();
			if (0 == type) {
				genderResult = v.getText().toString();
			} else {
				jobResult = v.getText().toString();
			}
		}
		for (int i : ids) {
			if (v.getId() == i) {
				continue;
			}
			rootView.findViewById(i).setBackgroundResource(R.drawable.btn_sekuai1);
			((Button) rootView.findViewById(i)).setTextColor(getResources().getColor(R.color.set_text));

		}
	}

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		data = getIntent().getBundleExtra("data");
		setTitle("搜索设置");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		rootView = (RelativeLayout) inflater.inflate(R.layout.activity_setting_init, container);
		initGender();
		initJob();
		regBtn = (Button) findViewById(R.id.btn_reg);
		// 处理seekbar
		RelativeLayout seekbar_container = (RelativeLayout) rootView.findViewById(R.id.seekbar_container);
		ImageView boy_image = (ImageView) rootView.findViewById(R.id.boy_image);
		ImageView old_image = (ImageView) rootView.findViewById(R.id.old_image);
		final TextView title_range = (TextView) rootView.findViewById(R.id.title_range);
		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(18,70,this);
		//seekBar.setRangeValues(18, 70);
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				// handle changed range values
				// Log.i("Seekbar", "User selected new range values: MIN=" +
				// minValue + ", MAX=" + maxValue);
				title_range.setText(minValue + "-" + maxValue + "岁");
				fromAge = minValue;
				toAge = maxValue;
			}
		});

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
		params.addRule(RelativeLayout.RIGHT_OF, boy_image.getId());
		params.addRule(RelativeLayout.LEFT_OF, old_image.getId());
		seekbar_container.addView(seekBar, params);
		// LinearLayout rootView = (LinearLayout)
		// inflater.inflate(R.layout.activity_setting, null);
		RelativeLayout maskView = (RelativeLayout) rootView.findViewById(R.id.mask_co_layout);
		button_mask_co = (ToggleButton) rootView.findViewById(R.id.button_mask_co);
		button_mask_contact = (ToggleButton) rootView.findViewById(R.id.button_mask_contact);
		button_mask_co.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				maskCo = isChecked;
			}
		});

		button_mask_contact.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

//				maskCo = isChecked;
			}
		});

		RelativeLayout maskCoView = (RelativeLayout) rootView.findViewById(R.id.mask_co_layout);
//		maskCoView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent in = new Intent(SettingInitActivity.this, MaskCoActivity.class);
//				/*type --> setting init activity*/
//				in.putExtra("type", "init");
//				in.putExtra("title", "屏蔽公司");
//				startActivityForResult(in, Constants.REQUEST_RECALL_MASK_CO);
//			}
//		});

		/*
		 * ((ToggleButton) rootView.findViewById(R.id.button_mask_contact)).
		 * setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener()
		 * {
		 * 
		 * @Override public void onCheckedChanged(CompoundButton buttonView,
		 * boolean isChecked) { // TODO Auto-generated method stub
		 * 
		 * maskContact = isChecked; } });
		 */

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
				// *****测试
				/*
				 * Intent intent = new Intent( SettingInitActivity.this,
				 * MainActivity.class); startActivity(intent); finish(); int a =
				 * 1; if(a==1){ return; }
				 */

				// ******测试end

				if (TextUtils.equals("男", genderResult)) {
					data.putString("showGender", "0");
				} else if (TextUtils.equals("女", genderResult)) {
					data.putString("showGender", "1");
				} else if (TextUtils.equals("不限", genderResult)) {
					data.putString("showGender", "2");
				} else {
					showToast("请选择性别");
					return;
				}

				if (TextUtils.equals("找工作", jobResult)) {
					data.putString("lookFor", "0");
				} else if (TextUtils.equals("招牛人", jobResult)) {
					data.putString("lookFor", "1");
				} else if (TextUtils.equals("求合伙", jobResult)) {
					data.putString("lookFor", "2");
				} else {
					showToast("请选择向我展示什么");
					return;
				}
				data.putString("ageMin", "" + fromAge);
				data.putString("ageMax", "" + toAge);
				data.putString("maskContact", "" + maskContact);
				data.putString("maskCo", "" + maskCo);

				HashMap<String, String> data0 = new HashMap<String, String>() {
					{
						for (String key : data.keySet()) {
							put(key, data.getString(key));
						}
					}
				};
				RequestData request = HttpUtils.simplePostData(Address.HOST + Address.REGISTER, data0);
				startHttpTask(new TaskResultListener() {

					@Override
					public void result(ResposneBundle b) {
						// TODO Auto-generated method stub
						Log.e("result", b.getContent());
						if (b.getContent() == null) {
							showToast("出错了，服务器异常");
							return;
						}
						try {
							JSONObject job = new JSONObject(b.getContent());
							showToast(job.getString("msg"));
							if (job.getInt("code") == -1) {
								showToast(job.getString("msg"));
							} else {
								JSONObject job2 = job.getJSONObject("data");
								if (job2.has("token")) {
									Constants.TOKEN = job2.getString("token");

									Constants.USERNAME = data.getString("phoneCode");
									Constants.PSW = data.getString("password");

									SharedPreferences prefs = getSharedPreferences(Constants.SETTING, 0);
									boolean result = prefs.edit().putString("token", Constants.TOKEN)
											.putLong("time", System.currentTimeMillis())
											.putString("USERNAME", Constants.USERNAME).putString("PSW", Constants.PSW)
											.commit();
									Log.e("token", Constants.TOKEN);
									Log.e("USERNAME", Constants.USERNAME);
									Log.e("PSW", Constants.PSW);

								}

								Intent intent = new Intent(SettingInitActivity.this, LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
						runOnUiThread(new Runnable() {
							public void run() {
								showToast(message);
							}
						});

					}
				}, request);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == Constants.REQUEST_RECALL_MASK_CO) {
				ArrayList<String> results = data.getStringArrayListExtra("result");
				Log.e("REQUEST_RECALL_MASK_CO", results.toString());
			}

		}

	}
}
