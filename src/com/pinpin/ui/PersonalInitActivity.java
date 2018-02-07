package com.pinpin.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.ImageUtils;
import com.pinpin.utils.Log;

public class PersonalInitActivity extends BaseActivity {
	public static final int REQUEST_CODE_CROP_IMAGE = 101;
	private Uri cropImageUri;
	int whichLabel = -1;
	RelativeLayout rootView;
	ImageButton[] photos;
	List<String> drawables;
	Bundle data;
	Button btn_personal_tag1;// 标签1
	Button btn_personal_tag2;// 标签2
	Button btn_personal_tag3;// 标签3
	Button regBtn;
	RelativeLayout pic_containner;
	TextView trade_content; // 行业
	EditText position_content;// 职位
	TextView service_year_content; // 工作年限
	TextView location_name; // 工作地点
	TextView salary_name; // 薪酬
	TextView person_number_content;
	TextView msg_title;
	TextView tag_title;
	TextView title_name;
	TextView description_title;
	EditText details_content;
	int base_w = 0;
	String purpose;

	private void initPhotos() {
		base_w = (int) (Constants.screen_width * 0.32);
		int margin = (int) (Constants.screen_width * 0.01);
		int big_pic_w = 2 * base_w + margin;
		photos = new ImageButton[6];

		for (int i = 0; i < photos.length; i++) {
			photos[i] = new ImageButton(this);
			photos[i].setPadding(0, 0, 0, 0);
			photos[i].setBackgroundResource(R.drawable.personal_bg);
			photos[i].setId(i);
			photos[i].setScaleType(ScaleType.CENTER_CROP);
			RelativeLayout.LayoutParams params = null;
			switch (i) {
			case 0:
				params = new RelativeLayout.LayoutParams(big_pic_w, big_pic_w);
				params.setMargins(margin, margin, margin, 0);
				break;
			case 1:
				params = new RelativeLayout.LayoutParams(base_w, base_w);
				params.setMargins(0, margin, margin, margin);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.RIGHT_OF, 0);
				break;
			case 2:
				params = new RelativeLayout.LayoutParams(base_w, base_w);
				params.setMargins(0, 0, margin, margin);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.RIGHT_OF, 0);
				params.addRule(RelativeLayout.BELOW, 1);
				break;
			case 3:
				params = new RelativeLayout.LayoutParams(base_w, base_w);
				params.setMargins(margin, 0, margin, margin);
				params.addRule(RelativeLayout.BELOW, 2);
				break;
			case 4:
				params = new RelativeLayout.LayoutParams(base_w, base_w);
				params.setMargins(0, 0, margin, margin);
				params.addRule(RelativeLayout.BELOW, 2);
				params.addRule(RelativeLayout.RIGHT_OF, 3);
				break;
			case 5:
				params = new RelativeLayout.LayoutParams(base_w, base_w);
				params.setMargins(0, 0, margin, margin);
				params.addRule(RelativeLayout.BELOW, 2);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				// params.addRule(RelativeLayout.RIGHT_OF,4);
				break;
			}
			pic_containner.addView(photos[i], params);
		}
	}

	private View.OnClickListener mClickDeleteListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
			showAlertDialogWithCancel("提醒", "确定要删除图片吗？", new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					int i = Integer.parseInt(v.getTag().toString());
					drawables.remove(drawables.get(i));

					HashMap<String, String> data = new HashMap<String, String>() {
						{
							put("token", Constants.TOKEN);
							put("index", v.getTag().toString());
						}
					};
					RequestData request = HttpUtils.simplePostData(Address.HOST + Address.DELETE_PICTURE, data);
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
								if (job.getInt("code") == -1) {
									showToast(job.getString("msg"));
								} else {
									refreshPhoto();
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

			}, "确定");
		}
	};
	private View.OnClickListener mGetPhotoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			selectPicFromLocal();
		}
	};

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCAL);
	}

	/**
	 * onActivityResult
	 */
	private void refreshPhoto() {
		if (photos.length == 0) {
			Constants.AVATAR_PATH = null;
			return;
		}
		for (int i = 0; i < photos.length; i++) {

			if (i <= drawables.size() - 1) {
				Bitmap bmp = null;
				if (i == 0) {
					Constants.AVATAR_PATH = drawables.get(i);
					bmp = ImageUtils.getBitmapFromFile(new File(drawables.get(i)), 2 * base_w, 2 * base_w);
				} else {
					bmp = ImageUtils.getBitmapFromFile(new File(drawables.get(i)), base_w, base_w);
				}
				photos[i].setImageBitmap(bmp);
				bmp = null;

				// photos[i].setImageURI(Uri.fromFile(new
				// File(drawables.get(i))));
				photos[i].setOnClickListener(mClickDeleteListener);
				photos[i].setTag(drawables.get(i));
			} else {
				photos[i].setTag(null);
				photos[i].setImageBitmap(null);
				photos[i].setOnClickListener(mGetPhotoListener);

			}
		}
	}

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("个人信息");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		data = getIntent().getBundleExtra("data");
		// inflater.inflate(R.layout.activity_personal, container);
		rootView = (RelativeLayout) inflater.inflate(R.layout.activity_personal_init, container);
		pic_containner = (RelativeLayout) rootView.findViewById(R.id.pic_containner);
		drawables = new ArrayList<String>();

		regBtn = (Button) findViewById(R.id.btn_reg);
		msg_title = (TextView) rootView.findViewById(R.id.msg_title);
		tag_title = (TextView) rootView.findViewById(R.id.tag_title);
		title_name = (TextView) rootView.findViewById(R.id.title_name);
		location_name = (TextView) rootView.findViewById(R.id.location_name);
		description_title = (TextView) rootView.findViewById(R.id.description_title);
		details_content = (EditText) rootView.findViewById(R.id.details_content);
		TextView position_name = (TextView) rootView.findViewById(R.id.position_name);
		RelativeLayout serviceYearView = (RelativeLayout) rootView.findViewById(R.id.service_year_layout);
		RelativeLayout serviceYearSeparatorView = (RelativeLayout) rootView
				.findViewById(R.id.service_year_separator_layout);
		RelativeLayout salaryView = (RelativeLayout) rootView.findViewById(R.id.salary_layout);
		RelativeLayout salarySeparatorView = (RelativeLayout) rootView.findViewById(R.id.salary_separator_layout);
		RelativeLayout personNumberView = (RelativeLayout) rootView.findViewById(R.id.person_number_layout);
		RelativeLayout personNumberSeparatorView = (RelativeLayout) rootView
				.findViewById(R.id.person_number_separator_layout);
		person_number_content = (TextView) rootView.findViewById(R.id.person_number);
		personNumberView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalInitActivity.this, TradeActivity.class);
				in.putExtra("title", "公司人数");
				startActivityForResult(in, Constants.REQUEST_RECALL_NUMBER);
			}
		});
		purpose = data.getString("purpose");
		if (TextUtils.equals("1", purpose)) {
			setTitle("公司信息");
			msg_title.setText("公司信息");
			tag_title.setText("公司标签");
			description_title.setText("公司描述");
			position_name.setText("招聘岗位");
			details_content.setHint("请输入公司描述");
			title_name.setText("公司地址");
			location_name.setHint("请选择公司地址");
			serviceYearView.setVisibility(View.GONE);
			serviceYearSeparatorView.setVisibility(View.GONE);
		} else if (TextUtils.equals("2", purpose)) {
			setTitle("合伙信息");
			msg_title.setText("合伙信息");
			tag_title.setText("我的标签");
			description_title.setText("合伙描述");
			description_title.setText("合伙描述");
			details_content.setHint("请输入合伙描述");
			salaryView.setVisibility(View.GONE);
			personNumberView.setVisibility(View.GONE);
			personNumberSeparatorView.setVisibility(View.GONE);
			salarySeparatorView.setVisibility(View.GONE);
		} else {
			setTitle("个人信息");
			msg_title.setText("我的信息");
			tag_title.setText("个性标签");
			description_title.setText("个人描述");
			title_name.setText("期望工作地");
			details_content.setHint("请输入个人描述");
			personNumberView.setVisibility(View.GONE);
			personNumberSeparatorView.setVisibility(View.GONE);
		}
		// photos[0] = (ImageButton) rootView.findViewById(R.id.btn_personal_a);
		// photos[1] = (ImageButton) rootView.findViewById(R.id.btn_personal_b);
		// photos[2] = (ImageButton) rootView.findViewById(R.id.btn_personal_c);
		// photos[3] = (ImageButton) rootView.findViewById(R.id.btn_personal_d);
		// photos[4] = (ImageButton) rootView.findViewById(R.id.btn_personal_e);
		// photos[5] = (ImageButton) rootView.findViewById(R.id.btn_personal_f);
		initPhotos();

		refreshPhoto();
		// LinearLayout rootView = (LinearLayout)
		// inflater.inflate(R.layout.activity_setting, null);
		RelativeLayout tradeView = (RelativeLayout) rootView.findViewById(R.id.trade_layout);
		trade_content = (TextView) rootView.findViewById(R.id.trade_content);
		tradeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent in = new
				// Intent(PersonalInitActivity.this,TradeActivity.class);
				Intent in = new Intent(PersonalInitActivity.this, TagActivity.class);
				in.putExtra("title", "行业");
				in.putExtra("phoneCode", data.getString("phoneCode"));
				startActivityForResult(in, Constants.REQUEST_RECALL_TRADE);
			}
		});

		// RelativeLayout positionView = (RelativeLayout)
		// rootView.findViewById(R.id.position_layout);
		// positionView.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent in = new Intent(PersonalInitActivity.this,
		// ChangeMessageActivity.class);
		// in.putExtra("title", "职位");
		// startActivityForResult(in, 1000);
		// }
		// });
		position_content = (EditText) rootView.findViewById(R.id.position_content);
		position_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				position_content.setCursorVisible(true);

			}

		});
		// RelativeLayout serviceYearView = (RelativeLayout) rootView
		// .findViewById(R.id.service_year_layout);
		service_year_content = (TextView) rootView.findViewById(R.id.service_year_content);
		serviceYearView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalInitActivity.this, TradeActivity.class);
				in.putExtra("title", "工作年限");
				startActivityForResult(in, Constants.REQUEST_RECALL_LIMITED);
			}
		});

		RelativeLayout placeView = (RelativeLayout) rootView.findViewById(R.id.come_from_layout);
		placeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalInitActivity.this, TagActivity.class);
				in.putExtra("title", "期望工作地");
				in.putExtra("phoneCode", data.getString("phoneCode"));
				startActivityForResult(in, Constants.REQUEST_RECALL_LOCATION);
			}
		});

		// RelativeLayout salaryView = (RelativeLayout)
		// rootView.findViewById(R.id.salary_layout);
		salary_name = (TextView) rootView.findViewById(R.id.salary_name);
		salaryView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalInitActivity.this, TradeActivity.class);
				in.putExtra("title", "薪酬");
				startActivityForResult(in, Constants.REQUEST_RECALL_SALARY);
			}
		});
		btn_personal_tag1 = (Button) rootView.findViewById(R.id.btn_personal_tag1);
		btn_personal_tag1.setOnClickListener(new LabelOnClickListener());
		btn_personal_tag1.setOnLongClickListener(new LabelLongOnClickListener());
		btn_personal_tag2 = (Button) rootView.findViewById(R.id.btn_personal_tag2);
		btn_personal_tag2.setOnClickListener(new LabelOnClickListener());
		btn_personal_tag2.setOnLongClickListener(new LabelLongOnClickListener());
		btn_personal_tag3 = (Button) rootView.findViewById(R.id.btn_personal_tag3);
		btn_personal_tag3.setOnClickListener(new LabelOnClickListener());
		btn_personal_tag3.setOnLongClickListener(new LabelLongOnClickListener());

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

				// ******测试end
				try {

					String tradeStr = trade_content.getText().toString();
					String positionStr = position_content.getText().toString();
					String yearStr = service_year_content.getText().toString();
					String workPlaceStr = location_name.getText().toString();
					String numberstr = person_number_content.getText().toString();
					String salaryStr = salary_name.getText().toString();
					String tag1 = btn_personal_tag1.getText().toString();
					String tag2 = btn_personal_tag2.getText().toString();
					String tag3 = btn_personal_tag3.getText().toString();
					String details = details_content.getText().toString();
					if (containsEmoji(details) || containsEmoji(positionStr)) {
						showToast("不支持输入Emoji表情符号");
						return;
					}
					if (TextUtils.isEmpty(tradeStr)) {
						showToast("请选择行业");
						return;
					} else if (TextUtils.isEmpty(positionStr)) {
						showToast("请输入职位");
						return;
					} else if (TextUtils.equals("0", purpose)) {
						if (TextUtils.isEmpty(yearStr)) {
							showToast("请输入工作年限");
							return;
						} else if (TextUtils.isEmpty(workPlaceStr)) {
							showToast("请输入期望工作地");
							return;
						} else if (TextUtils.isEmpty(salaryStr)) {
							showToast("请输入薪酬");
							return;
						}

					} else if (TextUtils.equals("1", purpose)) {
						if (TextUtils.isEmpty(workPlaceStr)) {
							showToast("请输入公司地址");
							return;
						} else if (TextUtils.isEmpty(salaryStr)) {
							showToast("请输入薪酬");
							return;
						} else if (TextUtils.isEmpty(numberstr)) {
							showToast("请输入公司人数");
							return;
						}
					} else if (TextUtils.equals("2", purpose)) {
						if (TextUtils.isEmpty(yearStr)) {
							showToast("请输入工作年限");
							return;
						} else if (TextUtils.isEmpty(workPlaceStr)) {
							showToast("请输入期望工作地");
							return;
						}
					}

					data.putString(trade_content.getTag().toString(), tradeStr);
					data.putString(position_content.getTag().toString(), positionStr);

					if (!TextUtils.isEmpty(yearStr)) {
						data.putString(service_year_content.getTag().toString(), yearStr);
					}
					if (!TextUtils.isEmpty(workPlaceStr)) {
						data.putString(location_name.getTag().toString(), workPlaceStr);
					}
					if (!TextUtils.isEmpty(salaryStr)) {
						data.putString(salary_name.getTag().toString(), salaryStr);
					}
					data.putString(salary_name.getTag().toString(), salaryStr);
					data.putString(person_number_content.getTag().toString(), numberstr);
					JSONArray array = new JSONArray();

					if (!TextUtils.equals("+", tag1)) {
						array.put(tag1);
					}
					if (!TextUtils.equals("+", tag2)) {
						array.put(tag2);
					}
					if (!TextUtils.equals("+", tag3)) {
						array.put(tag3);
					}
					if (array.length() > 0) {
						data.putString("tags", array.toString());
					}

					if (!TextUtils.isEmpty(details)) {
						data.putString(details_content.getTag().toString(), details);
					}
					Log.e("data------------", data.toString());
					Intent intent = new Intent(PersonalInitActivity.this, SettingInitActivity.class);
					intent.putExtra("data", data);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	class LabelLongOnClickListener implements View.OnLongClickListener {

		@Override
		public boolean onLongClick(View arg0) {

			switch (arg0.getId()) {
			case R.id.btn_personal_tag1:
				if (btn_personal_tag1.getText().toString().equals("+")) {
					Toast.makeText(PersonalInitActivity.this, "标签为空", Toast.LENGTH_SHORT).show();
				} else {
					showAlertDialogWithCancel("删除", "是否要删除这个标签", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {

							btn_personal_tag1.setText(btn_personal_tag2.getText().toString());
							btn_personal_tag2.setText(btn_personal_tag3.getText().toString());
							btn_personal_tag3.setText("+");

						}
					}, "确定");
					return false;
				}
				break;
			case R.id.btn_personal_tag2:
				if (btn_personal_tag2.getText().toString().equals("+")) {
					Toast.makeText(PersonalInitActivity.this, "标签为空", Toast.LENGTH_SHORT).show();
				} else {
					showAlertDialogWithCancel("删除", "是否要删除这个标签", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							btn_personal_tag2.setText(btn_personal_tag3.getText().toString());
							btn_personal_tag3.setText("+");

						}
					}, "确定");
					return false;
				}
				break;
			case R.id.btn_personal_tag3:
				if (btn_personal_tag3.getText().toString().equals("+")) {
					Toast.makeText(PersonalInitActivity.this, "标签为空", Toast.LENGTH_SHORT).show();
				} else {
					showAlertDialogWithCancel("删除", "是否要删除这个标签", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							btn_personal_tag3.setText("+");

						}
					}, "确定");
					return false;
				}
				break;
			default:
				break;
			}
			return false;

		}

	}

	class LabelOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			whichLabel = v.getId();
			// Intent in = new Intent(PersonalInitActivity.this,
			// TradeActivity.class);
			Intent in = new Intent(PersonalInitActivity.this, TagActivity.class);
			in.putExtra("title", tag_title.getText().toString());
			in.putExtra("phoneCode", data.getString("phoneCode"));
			startActivityForResult(in, Constants.REQUEST_RECALL_LABLE);
		}

	}

	/**
	 * 所有的Activity对象的返回值都是由这个方法来接收 requestCode:
	 * 表示的是启动一个Activity时传过去的requestCode值
	 * resultCode：表示的是启动后的Activity回传值时的resultCode值
	 * data：表示的是启动后的Activity回传过来的Intent对象 position_name
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_CROP_IMAGE) {
				if (cropImageUri != null) {
					File file = new File(cropImageUri.getPath());
					if (file.exists() && file.length() > 0) {
						RequestData request = HttpUtils.simpleFileData(Address.HOST + Address.ADD_PICTURE, "packname",
								file);
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
										drawables.add(cropImageUri.getPath());
										refreshPhoto();
										cropImageUri = null;
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
								cropImageUri = null;
							}
						}, request);
					} else {

					}

				}
			} else if (requestCode == ChatActivity.REQUEST_CODE_LOCAL) { // 发送本地图片

				if (data != null) {
					Uri selectedImage = data.getData();
					String path = null;
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
						String st8 = getResources().getString(R.string.cant_find_pictures);
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							String picturePath = cursor.getString(columnIndex);
							cursor.close();
							cursor = null;

							if (picturePath == null || picturePath.equals("null")) {
								Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;
							}
							path = picturePath;

						} else {
							File file = new File(selectedImage.getPath());
							if (!file.exists()) {
								Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;
							}

							path = file.getAbsolutePath();
						}
						cropImageUri = Uri
								.fromFile(new File(getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
										System.currentTimeMillis() + ".jpg"));
						ImageUtils.startCropImage(this, Uri.fromFile(new File(path)), cropImageUri, 300, 300,
								REQUEST_CODE_CROP_IMAGE);

						/*
						 * try { path = ImageUtils.getCompressImageFile(new
						 * File(path),600,800); } catch (FileNotFoundException
						 * e) { // TODO Auto-generated catch block
						 * e.printStackTrace(); } drawables.add(path);
						 * RequestData request =
						 * HttpUtils.simpleFileData(Address
						 * .HOST+Address.ADD_PICTURE, "packname", new
						 * File(path)); startHttpTask(new TaskResultListener(){
						 * 
						 * @Override public void result(ResposneBundle b) { //
						 * TODO Auto-generated method stub
						 * Log.e("result",b.getContent());
						 * if(b.getContent()==null){ showToast("出错了，服务器异常");
						 * return; } try { JSONObject job = new
						 * JSONObject(b.getContent());
						 * showToast(job.getString("msg"));
						 * if(job.getInt("code")==-1 ){
						 * showToast(job.getString("msg")); }else{
						 * refreshPhoto(); } } catch (JSONException e) { // TODO
						 * Auto-generated catch block e.printStackTrace(); }
						 * 
						 * }
						 * 
						 * @Override public void failed(final String message) {
						 * // TODO Auto-generated method stub runOnUiThread(new
						 * Runnable() { public void run() { showToast(message) ;
						 * } });
						 * 
						 * }}, request);
						 */

						// photos[index].setImageBitmap(BitmapFactory.decodeFile(path));

					}
				}
			} else if (requestCode == Constants.REQUEST_RECALL_LABLE) {
				String result = data.getStringExtra("result");
				if (whichLabel > 0) {
					switch (whichLabel) {
					case R.id.btn_personal_tag1:
						((Button) rootView.findViewById(whichLabel)).setText(result);
						break;
					case R.id.btn_personal_tag2:
						if (btn_personal_tag1.getText().toString().equals("+")) {
							btn_personal_tag1.setText(result);
						} else {
							((Button) rootView.findViewById(whichLabel)).setText(result);
						}
						break;
					case R.id.btn_personal_tag3:
						if (btn_personal_tag1.getText().toString().equals("+")) {
							btn_personal_tag1.setText(result);
						} else if (btn_personal_tag2.getText().toString().equals("+")) {
							btn_personal_tag2.setText(result);
						} else {
							((Button) rootView.findViewById(whichLabel)).setText(result);
						}
						break;

					default:
						break;
					}

				}
			} else if (requestCode == Constants.REQUEST_RECALL_TRADE) {
				String result = data.getStringExtra("result");
				trade_content.setText(result);
				;
			} else if (requestCode == Constants.REQUEST_RECALL_LOCATION) {
				String result = data.getStringExtra("result");
				location_name.setText(result);
				;
			} else if (requestCode == Constants.REQUEST_RECALL_LIMITED) {
				String result = data.getStringExtra("result");
				service_year_content.setText(result);
				;
			} else if (requestCode == Constants.REQUEST_RECALL_SALARY) {
				String result = data.getStringExtra("result");
				salary_name.setText(result);
				;
			} else if (requestCode == Constants.REQUEST_RECALL_NUMBER) {
				String result = data.getStringExtra("result");
				person_number_content.setText(result);
				;
			}

		}

	}

}
