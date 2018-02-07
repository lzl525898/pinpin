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
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingActivity extends BaseActivity {
	protected int fromAge = 0;
	protected int toAge = 0;
	protected int showGender = 0;
	protected int lookFor = 0;
	TextView title_range;
	RelativeLayout rootView;
	protected int nFlag = 0;
	protected boolean bFlagZhaopin = false;
	protected boolean bFlagQiuzhi = false;
	protected boolean bFlagHehuo = false;
	protected boolean bFlagLianxiren = false;
	protected boolean bFlagGongsi = false;
	protected boolean maskContact = false;
	protected boolean maskCo = false;
	Boolean flag = true;
	ToggleButton button_mask_co;
	ToggleButton button_mask_contact;
	RangeSeekBar<Integer> seekBar;
	Button btn_gender_male;
	Button btn_gender_female;
	Button btn_gender_unlimited;
	// Button btn_job_1;
	// Button btn_job_2;
	// Button btn_job_3;
	// Button btn_job_4;

	String genderResult;
	String jobResult;
	int[] job_ids = { R.id.btn_choose1, R.id.btn_choose2, R.id.btn_choose3 };

	class GenderOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_gender_male:
				btn_gender_male.setBackgroundResource(R.drawable.icon_nan_a);
				btn_gender_female.setBackgroundResource(R.drawable.icon_nv_a);
				btn_gender_unlimited.setBackgroundResource(R.drawable.btn_sekuai1);
				btn_gender_unlimited.setTextColor(getResources().getColor(R.color.set_text));
				genderResult = "男";
				break;
			case R.id.btn_gender_female:
				btn_gender_female.setBackgroundResource(R.drawable.icon_nv_b);
				btn_gender_male.setBackgroundResource(R.drawable.icon_nan_b);
				btn_gender_unlimited.setBackgroundResource(R.drawable.btn_sekuai1);
				btn_gender_unlimited.setTextColor(getResources().getColor(R.color.set_text));
				genderResult = "女";
				break;
			case R.id.btn_gender_unlimited:
				btn_gender_unlimited.setBackgroundResource(R.drawable.buxian);
				btn_gender_female.setBackgroundResource(R.drawable.icon_nv_a);
				btn_gender_male.setBackgroundResource(R.drawable.icon_nan_b);
				btn_gender_unlimited.setTextColor(Color.WHITE);
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
			value = null;
		} else {
			v.setBackgroundResource(R.drawable.buxian);
			v.setTextColor(Color.WHITE);
			// value = v.getText().toString();
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
		setTitle("搜索设置");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		rootView = (RelativeLayout) inflater.inflate(R.layout.activity_setting, container);
		initGender();
		initJob();
		setBackBtnVisibility();
		// next_btn.setText("确定");

		// 处理seekbar
		RelativeLayout seekbar_container = (RelativeLayout) rootView.findViewById(R.id.seekbar_container);
		ImageView boy_image = (ImageView) rootView.findViewById(R.id.boy_image);
		ImageView old_image = (ImageView) rootView.findViewById(R.id.old_image);
		title_range = (TextView) rootView.findViewById(R.id.title_range);
		seekBar = new RangeSeekBar<Integer>(18,70,this);
		//seekBar.setRangeValues(18, 70);
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				// handle changed range values
				// Log.i("Seekbar", "User selected new range values: MIN=" +
				// minValue + ", MAX=" + maxValue);
				fromAge = minValue;
				toAge = maxValue;
			
				title_range.setText(minValue.toString() + "-" + maxValue.toString());
			
			}
		});
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
		params.addRule(RelativeLayout.RIGHT_OF, boy_image.getId());
		params.addRule(RelativeLayout.LEFT_OF, old_image.getId());
		seekbar_container.addView(seekBar, params);

		RelativeLayout maskView = (RelativeLayout) rootView.findViewById(R.id.mask_co_layout);
		button_mask_co = (ToggleButton) rootView.findViewById(R.id.button_mask_co);

		button_mask_co.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				maskCo = isChecked;
				/*
				 * if(isChecked){
				 * 
				 * Intent in = new Intent(SettingActivity.this,
				 * MaskCoActivity.class); startActivityForResult(in,
				 * Constants.REQUEST_RECALL_MASK_CO); }
				 */
			}
		});
		RelativeLayout maskCoView = (RelativeLayout) rootView.findViewById(R.id.mask_co_layout);
		maskCoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(SettingActivity.this, MaskCoActivity.class);
				in.putExtra("title", "屏蔽公司");
				in.putExtra("type", "set");
				startActivityForResult(in, Constants.REQUEST_RECALL_MASK_CO);
			}
		});

		RelativeLayout maskCoPhoneView = (RelativeLayout) rootView.findViewById(R.id.mask_co_phone);

		maskCoPhoneView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		button_mask_contact = (ToggleButton) rootView.findViewById(R.id.button_mask_phone);
		button_mask_contact.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // TODO
																							// Auto-generated
																							// method
																							// stub
				maskContact = isChecked;
			}
		});

		HashMap<String, String> data = new HashMap<String, String>() {
			{
				put("phoneCode", Constants.USERNAME);
				put("token", Constants.TOKEN);
			}
		};
        
		RequestData request = HttpUtils.simplePostData(Address.HOST + Address.GET_SETTING, data);
		final ArrayList SaveValueList = new ArrayList();
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
						JSONObject data = job.getJSONObject("data");
						showGender = data.getInt("showGender");
						SaveValueList.add(showGender);
						Button btn_Select;
						if (showGender == 0) {
							btn_Select = (Button) findViewById(R.id.btn_gender_male);
							btn_Select.setBackgroundResource(R.drawable.icon_nan_a);
							genderResult = "男";
						} else if (showGender == 1) {
							btn_Select = (Button) findViewById(R.id.btn_gender_female);
							btn_Select.setBackgroundResource(R.drawable.icon_nv_b);
							genderResult = "女";
						} else {
							btn_Select = (Button) findViewById(R.id.btn_gender_unlimited);
							btn_Select.setBackgroundResource(R.drawable.buxian);
							btn_Select.setTextColor(Color.WHITE);
							genderResult = "不限";
						}

						lookFor = data.getInt("lookFor");
						//settingValue[1]=Integer.toString(lookFor);
						SaveValueList.add(lookFor);
						if (lookFor == 0) {
							btn_Select = (Button) findViewById(R.id.btn_choose1);
							btn_Select.setBackgroundResource(R.drawable.buxian);
							jobResult = "找工作";
							btn_Select.setTextColor(Color.WHITE);
						} else if (lookFor == 1) {
							btn_Select = (Button) findViewById(R.id.btn_choose2);
							btn_Select.setBackgroundResource(R.drawable.buxian);
							jobResult = "招牛人";
							btn_Select.setTextColor(Color.WHITE);
						} else {
							btn_Select = (Button) findViewById(R.id.btn_choose3);
							btn_Select.setBackgroundResource(R.drawable.buxian);
							jobResult = "求合伙";
							btn_Select.setTextColor(Color.WHITE);
						}

						// btn_job_1 = (Button) findViewById(R.id.btn_choose1);
						// btn_job_1.setOnClickListener(new
						// JobOnClickListener());
						// btn_job_2 = (Button) findViewById(R.id.btn_choose2);
						// btn_job_2.setOnClickListener(new
						// JobOnClickListener());
						// btn_job_3 = (Button) findViewById(R.id.btn_choose3);
						// btn_job_3.setOnClickListener(new
						// JobOnClickListener());
						// btn_job_4 = (Button) findViewById(R.id.btn_choose4);
						// btn_job_4.setOnClickListener(new
						// JobOnClickListener());
						fromAge = data.getInt("ageMin");
						//settingValue[2]=Integer.toString(fromAge);
						SaveValueList.add(fromAge);
						toAge = data.getInt("ageMax");
						//settingValue[3]=Integer.toString(toAge);
						SaveValueList.add(toAge);
						if (fromAge < 18) {
							fromAge = 18;
						}
						if (70 < fromAge) {
							fromAge = 70;
						}
						if (toAge < 18) {
							toAge = 18;
						}
						if (70 < toAge) {
							toAge = 70;
						}

						seekBar.setSelectedMinValue(fromAge);
						seekBar.setSelectedMaxValue(toAge);
						title_range.setText(fromAge + "-" + toAge);
						maskContact = data.getBoolean("maskContact");
						//settingValue[4]=maskContact;
						SaveValueList.add(maskContact);
						button_mask_contact.setChecked(maskContact);
						maskCo = data.getBoolean("maskCo");
						//settingValue[5]=button_mask_contact;
						SaveValueList.add(maskCo);
						button_mask_co.setChecked(maskCo);
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
						flag = false;
					}
				});

			}
		}, request);

		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String, String> data = new HashMap<String, String>() {
					{
						if (genderResult.equals("男")) {
							showGender = 0;
						} else if (genderResult.equals("女")) {
							showGender = 1;
						} else {
							showGender = 2;
						}
						if (jobResult.equals("找工作")) {
							lookFor = 0;
						} else if (jobResult.equals("招牛人")) {
							lookFor = 1;
						} else {
							lookFor = 2;
						}
						put("token", Constants.TOKEN);
						put("showGender", Integer.toString(showGender));
						put("lookFor", Integer.toString(lookFor));
						put("ageMin", Integer.toString(fromAge));
						put("ageMax", Integer.toString(toAge));
						put("maskContact", Boolean.toString(maskContact));
						put("maskCo", Boolean.toString(maskCo));
					}
				};
				if(SaveValueList.get(0).equals(showGender)&&SaveValueList.get(1).equals(lookFor)&&SaveValueList.get(2).equals(fromAge)&&SaveValueList.get(3).equals(toAge)&&SaveValueList.get(4).equals(maskContact)&&SaveValueList.get(5).equals(maskCo))
				{
					flag=false;
				}else{
					flag=true;
				}
				if (flag) {
					RequestData request = HttpUtils.simplePostData(Address.HOST + Address.UPDATE_SETTING, data);
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
								System.out.println(job.getString("msg"));
								showToast(job.getString("msg"));
								if (job.getInt("code") == -1) {
									showToast(job.getString("msg"));
								} else {
									// PinpinFragment.search=true;
									finish();
									PinpinFragment.search = true;
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
					// finish();
				} else {
					finish();
				}
			}
		});
		/*
		 * next_btn.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * HashMap<String, String> data = new HashMap<String, String>() { { if
		 * (genderResult.equals("男")) { showGender = 0; } else if
		 * (genderResult.equals("女")) { showGender = 1; } else { showGender = 2;
		 * } if (jobResult.equals("找工作")) { lookFor = 0; } else if
		 * (jobResult.equals("招牛人")) { lookFor = 1; } else { lookFor = 2; }
		 * put("token", Constants.TOKEN); put("showGender",
		 * Integer.toString(showGender)); put("lookFor",
		 * Integer.toString(lookFor)); put("ageMin", Integer.toString(fromAge));
		 * put("ageMax", Integer.toString(toAge)); put("maskContact",
		 * Boolean.toString(maskContact)); put("maskCo",
		 * Boolean.toString(maskCo)); } }; RequestData request =
		 * HttpUtils.simplePostData(Address.HOST + Address.UPDATE_SETTING,
		 * data); startHttpTask(new TaskResultListener() {
		 * 
		 * @Override public void result(ResposneBundle b) { // TODO
		 * Auto-generated method stub Log.e("result", b.getContent()); if
		 * (b.getContent() == null) { showToast("出错了，服务器异常"); return; } try {
		 * JSONObject job = new JSONObject(b.getContent());
		 * showToast(job.getString("msg")); if (job.getInt("code") == -1) {
		 * showToast(job.getString("msg")); } else { //
		 * PinpinFragment.search=true; finish(); PinpinFragment.search=true; } }
		 * catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * }
		 * 
		 * @Override public void failed(final String message) { // TODO
		 * Auto-generated method stub runOnUiThread(new Runnable() { public void
		 * run() { showToast(message); } }); } }, request); } });
		 */

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
