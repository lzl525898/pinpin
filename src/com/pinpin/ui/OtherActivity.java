package com.pinpin.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.model.UserInfo;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.IOUtil;
import com.pinpin.utils.ImageUtils;
import com.pinpin.utils.Log;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OtherActivity extends BaseActivity {
	private ViewPager viewPager;

	private ImageView[] tips;
	PagerAdapter adapter;
	private List<UserInfo> dataList;
	private List<UserInfo> tempList;
	private int currentPage = 0;
	LinearLayout tipsBox;

	private void updateViewPager() {
		tips = new ImageView[dataList.size()];
		tipsBox.removeAllViews();
		for (int i = 0; i < tips.length; i++) {

			ImageView img = new ImageView(this);
			tips[i] = img;
			if (i == 0) {
				img.setBackgroundResource(R.drawable.round_blue);
			} else {
				img.setBackgroundResource(R.drawable.round_gray);
			}

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
			params.leftMargin = 5;
			params.rightMargin = 5;
			tipsBox.addView(img, params); // 把点点添加到容器中
		}
	}

	private void initViewPager(String path, String url) {
		viewPager = (ViewPager) findViewById(R.id.gallery);

		tipsBox = (LinearLayout) findViewById(R.id.tips_box);
		dataList = new ArrayList<UserInfo>();
		UserInfo info = new UserInfo();
		if (!TextUtils.isEmpty(path)) {
			info.path = new File(path);
		}
		if (!TextUtils.isEmpty(url)) {
			info.url = url;
		}
		dataList.add(info);

		viewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {

				// TODO Auto-generated method stub
				currentPage = position;
				Log.e("图片", "第" + position + "张选中");
				if (tips == null)
					return;
				for (int i = 0; i < tips.length; i++) {

					if (i == currentPage) {
						tips[i].setBackgroundResource(R.drawable.round_blue);
						// Log.e("图片","第"+position+"张设置成蓝色");
					}

					else {
						tips[i].setBackgroundResource(R.drawable.round_gray);
						// Log.e("图片","第"+position+"张设置成灰色");
					}
				}

			}

		});
		adapter = new PagerAdapter() {

			@Override
			public int getCount() {

				// TODO Auto-generated method stub

				return dataList.size();

			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {

				// TODO Auto-generated method stub

				return arg0 == arg1;

			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object o) {

				// container.removeViewAt(position);

			}

			// 设置ViewPager指定位置要显示的view

			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				ImageView im = new ImageView(OtherActivity.this);
				im.setScaleType(ScaleType.CENTER_CROP);
				im.setImageBitmap(ImageUtils.getBitmapFromFile(dataList.get(position).path, 500, 300));
				container.addView(im);

				return im;

			}

		};

		viewPager.setAdapter(adapter);
	}

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();

		inflater.inflate(R.layout.activity_other, container);

		Intent in = getIntent();
		String json = in.getStringExtra("info");
		String path = in.getStringExtra("path");
		String url = in.getStringExtra("url");
		boolean fromChat = in.getBooleanExtra("fromChat", false);

		initViewPager(path, url);
		try {
			initViewWithData(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Button btn_like = (Button) findViewById(R.id.btn_like);
		Button btn_dislike = (Button) findViewById(R.id.btn_dislike);
		if (fromChat) {
			btn_like.setVisibility(View.INVISIBLE);
			btn_dislike.setVisibility(View.INVISIBLE);
		} else {
			btn_like.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent in = new Intent();
					in.putExtra("islike", true);
					setResult(RESULT_OK, in);
					finish();

				}
			});
			btn_dislike.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent in = new Intent();
					in.putExtra("islike", false);
					setResult(RESULT_OK, in);
					finish();

				}
			});
		}
		next_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(OtherActivity.this, ReportActivity.class);
				startActivity(in);
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

	private void initViewWithData(String data0) throws JSONException {
		JSONObject data = new JSONObject(data0);
		JSONArray pictures = data.getJSONArray("pictures");
		if (pictures.length() > 1) {
			tempList = new ArrayList<UserInfo>();
			for (int i = 1; i < pictures.length(); i++) {
				UserInfo info = new UserInfo();
				String relative = pictures.getJSONObject(i).getString("filePath");
				info.url = Address.HOST_PICTURE + relative;
				// info.path = new File(
				// getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),System.currentTimeMillis()+(int)Math.random()*1000+"");
				String ext = info.url.substring(info.url.lastIndexOf(".") + 1);
				info.path = new File(getExternalFilesDir(null), IOUtil.toMd5(info.url.getBytes()) + "." + ext);
				tempList.add(info);
			}
			fileDownload(new TaskResultListener() {

				@Override
				public void result(ResposneBundle b) {
					// TODO Auto-generated method stub
					if (adapter != null) {
						dataList.addAll(tempList);
						adapter.notifyDataSetChanged();
						updateViewPager();
					}
				}

				@Override
				public void failed(String message) {
					// TODO Auto-generated method stub

				}
			}, tempList);
		}

		String username = data.getString("username");
		setTitle(username);
		Log.e("DATA1234", data.toString());
		String genderResult;
		int gender = data.getInt("gender");
		if (gender == 0) {
			genderResult = "男";
		} else {
			genderResult = "女";
		}

		RelativeLayout salary_separator_layout = (RelativeLayout) findViewById(R.id.salary_separator_layout);
		RelativeLayout person_number_separator_layout = (RelativeLayout) findViewById(
				R.id.person_number_separator_layout);
		TextView title_position = (TextView) findViewById(R.id.title_position);
		TextView tag_title = (TextView) findViewById(R.id.tag_title);
		String birthdate = data.getString("birthdate");
		TextView perpose = (TextView) findViewById(R.id.purpose);
		TextView title_name = (TextView) findViewById(R.id.title_name);
		String title = username + ", " + genderResult + ", " + birthdate;
		TextView details_content = (TextView) findViewById(R.id.details_content);
		TextView description_title = (TextView) findViewById(R.id.description_title);
		title_name.setText(title);
		RelativeLayout person_number_layout2 = (RelativeLayout) findViewById(R.id.person_number_layout2);
		RelativeLayout service_year_layout = (RelativeLayout) findViewById(R.id.service_year_layout);
		JSONObject career = data.getJSONObject("career");
		int lookFor = career.getInt("purpose");
		String jobResult;
		if (lookFor == 0) {
			jobResult = "找工作";
			perpose.setText("个人信息");
			title_position.setText("期望工作地");
			tag_title.setText("个人标签");
			description_title.setText("个人描述");
			details_content.setHint("请输入个人描述");
			person_number_layout2.setVisibility(View.GONE);
			person_number_separator_layout.setVisibility(View.GONE);
		} else if (lookFor == 1) {
			jobResult = "招牛人";
			perpose.setText("公司信息");
			title_position.setText("公司地址");
			tag_title.setText("公司标签");
			description_title.setText("公司描述");
			details_content.setHint("请输入公司描述");
			service_year_layout.setVisibility(View.GONE);
			salary_separator_layout.setVisibility(View.GONE);
		} else {
			jobResult = "求合伙";
			title_position.setText("期望工作地");
			perpose.setText("个人信息");
			tag_title.setText("个人标签");
			description_title.setText("个人描述");
			details_content.setHint("请输入个人描述");
			service_year_layout.setVisibility(View.GONE);
			salary_separator_layout.setVisibility(View.GONE);
			person_number_layout2.setVisibility(View.GONE);
			person_number_separator_layout.setVisibility(View.GONE);
		}
		
		String details = career.getString("details");
		if (TextUtils.isEmpty(details) ||details.equals("null")) {
			details_content.setText("无");
		} else {
			details_content.setText(details);
		}

		TextView salary_name = (TextView) findViewById(R.id.salary_name2);
		String salary = career.getString("salary");
		if (salary.equals("null") || TextUtils.isEmpty(salary)) {
			salary_name.setText("面议");
		} else {
			salary_name.setText(salary);
		}
		
		TextView person_number = (TextView) findViewById(R.id.person_number2);
		String number = career.getString("personNumber");
		if (number.equals("null") || TextUtils.isEmpty(number)) {
			person_number.setText("0");
		} else {
			person_number.setText(number);
		}
		TextView constellation = (TextView) findViewById(R.id.title_constellation);
		constellation.setText(jobResult);
		TextView trade_content = (TextView) findViewById(R.id.trade_content);
		String tradeStr = career.getString(trade_content.getTag().toString());
		trade_content.setText(tradeStr);
		TextView position_content = (TextView) findViewById(R.id.position_content);
		String positionStr = career.getString(position_content.getTag().toString());
		position_content.setText(positionStr);
		TextView service_year_content = (TextView) findViewById(R.id.service_year_content);
		String yearStr = career.getString(service_year_content.getTag().toString());
		service_year_content.setText(yearStr);
		TextView location_name = (TextView) findViewById(R.id.location_name);
		String workPlaceStr = career.getString(location_name.getTag().toString());
		if (workPlaceStr.equals("null") || workPlaceStr.isEmpty()) {
			location_name.setText("无");
		} else {
			location_name.setText(workPlaceStr);
		}

		
		Button btn_personal_tag1 = (Button) findViewById(R.id.btn_personal_tag1);
		Button btn_personal_tag2 = (Button) findViewById(R.id.btn_personal_tag2);
		Button btn_personal_tag3 = (Button) findViewById(R.id.btn_personal_tag3);
		
		String tags = career.getString("tags");
		if(tags.isEmpty() || tags.equals("null")){
			btn_personal_tag1.setText("未添加");
			btn_personal_tag2.setText("未添加");
			btn_personal_tag3.setText("未添加");

		}else{
			JSONArray tagJson = new JSONArray(tags);
			String tag1 = tagJson.optString(0);
			if (tag1.isEmpty()|| tag1.equals("null")) {
				btn_personal_tag1.setText("未添加");
			} else {
				btn_personal_tag1.setText(tag1);
			}
			String tag2 = tagJson.optString(1);
			if (tag2.isEmpty()|| tag2.equals("null")) {
				btn_personal_tag2.setText("未添加");
			} else {
				btn_personal_tag2.setText(tag2);
			}
			String tag3 = tagJson.optString(2);
			if (tag3.isEmpty()|| tag3.equals("null")) {
				btn_personal_tag3.setText("未添加");
			} else {
				btn_personal_tag3.setText(tag3);
			}
		}

	}

}
