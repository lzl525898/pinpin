package com.pinpin.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.app.PinApplication;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.model.UserInfo;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.IOUtil;
import com.pinpin.utils.ImageUtils;
import com.pinpin.utils.Log;

public class PersonalActivity extends BaseActivity {
	public static final int REQUEST_CODE_CROP_IMAGE = 101;
	public static Context thjs;
	private Uri cropImageUri;
	int whichLabel = -1;
	RelativeLayout rootView;
	ImageButton[] photos;
	List<String> drawables;
	Button btn_personal_tag1;// 标签1
	Button btn_personal_tag2;// 标签2
	Button btn_personal_tag3;// 标签3
	RelativeLayout pic_containner;
	TextView trade_content; // 行业
	EditText position_content;// 职位
	TextView service_year_content; // 工作年限
	TextView location_name; // 工作地点
	TextView salary_name; // 薪酬
	TextView msg_title;
	TextView tag_title;
	TextView title_name;
	TextView description_title;
	EditText details_content;
	TextView position_name;
	TextView person_number_content;
	RelativeLayout serviceYearView;
	RelativeLayout serviceYearSeparatorView;
	RelativeLayout salaryView;
	RelativeLayout salarySeparatorView;
	RelativeLayout personNumberView;
	RelativeLayout personNumberSeparatorView;
	int base_w = 0;
	String tradeStr;
	String positionStr;
	String yearStr;
	String workPlaceStr;
	String salaryStr;
	String personNumberStr;
	String tag1;
	String tag2;
	String tag3;
	String details;
	protected int lookFor = 0;

	// ConcurrentLinkedQueue<UserInfo> dataList = new
	// ConcurrentLinkedQueue<UserInfo>();
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
			// photos[i].setScaleType(ScaleType.CENTER_CROP);
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

	/*
	 * 长按删除照片
	 */
	private OnLongClickListener mClickDeleteListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(final View v) {

			showAlertDialogWithCancel("提醒", "确定要删除图片吗？",
					new DialogInterface.OnClickListener() {
						@SuppressLint("NewApi")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							int i = Integer.parseInt(v.getTag().toString());
							drawables.remove(drawables.get(i));
							if (drawables.size() == 0) {
								Constants.AVATAR_PATH = null;
							}
							HashMap<String, String> data = new HashMap<String, String>() {
								{
									put("token", Constants.TOKEN);
									put("index", v.getTag().toString());
								}
							};
							RequestData request = HttpUtils
									.simplePostData(Address.HOST
											+ Address.DELETE_PICTURE, data);
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
										JSONObject job = new JSONObject(b
												.getContent());
										if (job.getInt("code") == -1) {
											showToast(job.getString("msg"));
										} else {
											refreshPhoto(false);

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
			return false;

		}
	};
	// private View.OnClickListener mClickDeleteListener = new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(final View v) {
	// // TODO Auto-generated method stub
	// showAlertDialogWithCancel("提醒", "确定要删除图片吗？", new
	// DialogInterface.OnClickListener() {
	// @SuppressLint("NewApi")
	// @Override
	// public void onClick(DialogInterface arg0, int arg1) {
	// // TODO Auto-generated method stub
	// int i = Integer.parseInt(v.getTag().toString());
	// drawables.remove(drawables.get(i));
	//
	// HashMap<String,String> data = new HashMap<String,String>(){{
	// put("token",Constants.TOKEN);
	// put("index", v.getTag().toString());
	// }};
	// RequestData request =
	// HttpUtils.simplePostData(Address.HOST+Address.DELETE_PICTURE, data);
	// startHttpTask(new TaskResultListener(){
	//
	// @Override
	// public void result(ResposneBundle b) {
	// // TODO Auto-generated method stub
	// Log.e("result",b.getContent());
	// if(b.getContent()==null){
	// showToast("出错了，服务器异常");
	// return;
	// }
	// try {
	// JSONObject job = new JSONObject(b.getContent());
	// if(job.getInt("code")==-1 ){
	// showToast(job.getString("msg"));
	// }else{
	// refreshPhoto(false);
	//
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// @Override
	// public void failed(final String message) {
	// // TODO Auto-generated method stub
	// runOnUiThread(new Runnable() {
	// public void run() {
	// showToast(message) ;
	// }
	// });
	// }}, request);
	// }
	// }, "确定");
	// }
	// };
	private View.OnClickListener mGetPhotoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getTag() == null) {
				selectPicFromLocal();
			} else {

			}
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
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCAL);
	}

	public void downPic(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					String path = getCacheDir() + File.separator + "Image";
					File fileDir = new File(path);
					if (!fileDir.exists()) {
						fileDir.mkdir();
					}
					File file = new File(path, "head.jpg");

					// File file = new
					// File(Environment.getExternalStorageDirectory(),"head.jpg");
					URL uri = null;
					try {
						uri = new URL(url);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						Log.d("TEST", "11111-->" + e.getMessage());
						e.printStackTrace();
					}
					HttpURLConnection conn = null;
					try {
						conn = (HttpURLConnection) uri.openConnection();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					conn.setConnectTimeout(5000);
					// 获取到文件的大小
					InputStream is = null;
					try {
						is = conn.getInputStream();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Log.d("TEST", "download file->" + file.getPath());
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BufferedInputStream bis = new BufferedInputStream(is);
					byte[] buffer = new byte[1024];
					int len;
					int total = 0;
					try {
						while ((len = bis.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
							total += len;
							// 获取当前下载量
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						bis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// return file;
				} else {
					Log.i("TEST", "没有SD卡......");
					// return null;
				}
			}
		}).start();
	}

	/**
	 * onActivityResult将头像一张设置放大300 后面为180 第一次进入 都有点击事件
	 */
	private void refreshPhoto(boolean isFirst) {

		if (photos.length == 0) {
			Constants.AVATAR_PATH = null;
			return;
		}
		for (int i = 0; i < photos.length; i++) {

			if (i <= drawables.size() - 1) {
				if (i == 0) {
					PinApplication.getInstance().loadImage(drawables.get(i),
							photos[i], R.drawable.default_avatar,
							R.drawable.default_image, 300, 300);
					String addrStr = drawables.get(0).split("[/]")[0];
					if (addrStr.equals("http:")) {
						downPic(drawables.get(0));
						// Constants.AVATAR_PATH=Environment.getExternalStorageDirectory()+"/head.jpg";
						Constants.AVATAR_PATH = getCacheDir() + File.separator
								+ "Image/head.jpg";
					} else {
						Constants.AVATAR_PATH = drawables.get(0);
					}
				} else {
					PinApplication.getInstance().loadImage(drawables.get(i),
							photos[i], R.drawable.default_avatar,
							R.drawable.default_image, 180, 180);
				}

				if (!isFirst)
					photos[i].setOnLongClickListener(mClickDeleteListener);
				photos[i].setTag(i);
			} else {
				photos[i].setTag(null);
				photos[i].setImageBitmap(null);
				photos[i].setOnClickListener(mGetPhotoListener);

			}

			/*
			 * Bitmap bmp = null; if(i == 0){ Constants.AVATAR_PATH =
			 * drawables.get(i); bmp =ImageUtils.getBitmapFromFile(new
			 * File(drawables.get(i)),2*base_w,2*base_w); }else{ bmp
			 * =ImageUtils.getBitmapFromFile(new
			 * File(drawables.get(i)),base_w,base_w); } //Drawable[] arr = new
			 * Drawable[2]; //arr[0] =
			 * getResources().getDrawable(R.drawable.personal_bg); //arr[1] =
			 * new
			 * BitmapDrawable(getResources(),ImageUtils.centerSquareScaleBitmap
			 * (bmp, 0));
			 * 
			 * TransitionDrawable td = new TransitionDrawable(new Drawable[]{new
			 * ColorDrawable(Color.LTGRAY),new BitmapDrawable(bmp)});
			 * td.setCrossFadeEnabled(true);
			 * photos[i].setScaleType(ScaleType.CENTER_CROP);
			 * photos[i].setImageDrawable(td);
			 * 
			 * td.startTransition(1000);
			 * 
			 * //photos[i].setImageBitmap(bmp); //td = null; //bmp = null;
			 * //photos[i].setImageURI(Uri.fromFile(new
			 * File(drawables.get(i)))); if(!isFirst)
			 * photos[i].setOnClickListener(mClickDeleteListener ); //
			 * photos[i].setTag(drawables.get(i)); photos[i].setTag(i); }else{
			 * photos[i].setTag(null); photos[i].setImageBitmap(null);
			 * photos[i].setOnClickListener(mGetPhotoListener);
			 * 
			 * }
			 */
		}
	}

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub

		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();

		// inflater.inflate(R.layout.activity_personal, container);
		rootView = (RelativeLayout) inflater.inflate(
				R.layout.activity_personal, container);
		pic_containner = (RelativeLayout) rootView
				.findViewById(R.id.pic_containner);
		drawables = new ArrayList<String>();
		setNextBtnVisibility();
		next_btn.setText("确定");
		setTitle("");

		msg_title = (TextView) rootView.findViewById(R.id.msg_title);
		tag_title = (TextView) rootView.findViewById(R.id.tag_title);
		title_name = (TextView) rootView.findViewById(R.id.title_name);
		description_title = (TextView) rootView
				.findViewById(R.id.description_title);
		position_name = (TextView) rootView.findViewById(R.id.position_name);
		serviceYearView = (RelativeLayout) rootView
				.findViewById(R.id.service_year_layout);
		serviceYearSeparatorView = (RelativeLayout) rootView
				.findViewById(R.id.service_year_separator_layout);
		salaryView = (RelativeLayout) rootView.findViewById(R.id.salary_layout);
		salarySeparatorView = (RelativeLayout) rootView
				.findViewById(R.id.salary_separator_layout);
		personNumberView = (RelativeLayout) rootView
				.findViewById(R.id.person_number_layout);
		personNumberSeparatorView = (RelativeLayout) rootView
				.findViewById(R.id.person_number_separator_layout);

		// photos[0] = (ImageButton) rootView.findViewById(R.id.btn_personal_a);
		// photos[1] = (ImageButton) rootView.findViewById(R.id.btn_personal_b);
		// photos[2] = (ImageButton) rootView.findViewById(R.id.btn_personal_c);
		// photos[3] = (ImageButton) rootView.findViewById(R.id.btn_personal_d);
		// photos[4] = (ImageButton) rootView.findViewById(R.id.btn_personal_e);
		// photos[5] = (ImageButton) rootView.findViewById(R.id.btn_personal_f);
		// 头像集初始化
		initPhotos();
		//
		refreshPhoto(true);
		// LinearLayout rootView = (LinearLayout)
		// inflater.inflate(R.layout.activity_setting, null);
		RelativeLayout tradeView = (RelativeLayout) rootView
				.findViewById(R.id.trade_layout);
		trade_content = (TextView) rootView.findViewById(R.id.trade_content);
		tradeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent in = new
				// Intent(PersonalActivity.this,TradeActivity.class);
				Intent in = new Intent(PersonalActivity.this, TagActivity.class);
				in.putExtra("title", "行业");
				in.putExtra("phoneCode", getUsername());
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
		position_content = (EditText) rootView
				.findViewById(R.id.position_content);
		position_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				position_content.setCursorVisible(true);

			}

		});
		service_year_content = (TextView) rootView
				.findViewById(R.id.service_year_content);
		serviceYearView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalActivity.this,
						TradeActivity.class);
				in.putExtra("title", "工作年限");
				startActivityForResult(in, Constants.REQUEST_RECALL_LIMITED);
			}
		});

		person_number_content = (TextView) rootView
				.findViewById(R.id.person_number);
		personNumberView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalActivity.this,
						TradeActivity.class);
				in.putExtra("title", "公司人数");
				startActivityForResult(in, Constants.REQUEST_RECALL_NUMBER);
			}
		});

		RelativeLayout placeView = (RelativeLayout) rootView
				.findViewById(R.id.come_from_layout);
		location_name = (TextView) rootView.findViewById(R.id.location_name);
		placeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalActivity.this, TagActivity.class);
				in.putExtra("title", "期望工作地");
				in.putExtra("phoneCode", getUsername());
				startActivityForResult(in, Constants.REQUEST_RECALL_LOCATION);
			}
		});

		salary_name = (TextView) rootView.findViewById(R.id.salary_name);
		salaryView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalActivity.this,
						TradeActivity.class);
				in.putExtra("title", "薪酬");
				startActivityForResult(in, Constants.REQUEST_RECALL_SALARY);
			}
		});

		btn_personal_tag1 = (Button) rootView
				.findViewById(R.id.btn_personal_tag1);
		btn_personal_tag1.setOnClickListener(new LabelOnClickListener());
		btn_personal_tag1
				.setOnLongClickListener(new LabelLongOnClickListener());
		btn_personal_tag2 = (Button) rootView
				.findViewById(R.id.btn_personal_tag2);
		btn_personal_tag2.setOnClickListener(new LabelOnClickListener());
		btn_personal_tag2
				.setOnLongClickListener(new LabelLongOnClickListener());
		btn_personal_tag3 = (Button) rootView
				.findViewById(R.id.btn_personal_tag3);
		btn_personal_tag3.setOnClickListener(new LabelOnClickListener());
		btn_personal_tag3
				.setOnLongClickListener(new LabelLongOnClickListener());
		details_content = (EditText) rootView
				.findViewById(R.id.details_content);
		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();

			}
		});

		HashMap<String, String> data = new HashMap<String, String>() {
			{
				String phoneCode = getUsername();
				put("phoneCode", phoneCode);
				put("token", Constants.TOKEN);
			}
		};
		// 获取数据
		RequestData request = HttpUtils.simplePostData(Address.HOST
				+ Address.GET_USER, data);
		startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") == -1) {
						showToast(job.getString("msg"));
					} else {
						JSONObject data = job.getJSONObject("data");
						JSONObject career = data.getJSONObject("career");
						lookFor = career.getInt("purpose");
						if (1 == lookFor) {
							setTitle("公司信息");
							msg_title.setText("公司信息");
							tag_title.setText("公司标签");
							description_title.setText("公司描述");
							details_content.setHint("请输入公司描述");
							title_name.setText("公司地址");
							position_name.setText("招聘岗位");
							serviceYearView.setVisibility(View.GONE);
							serviceYearSeparatorView.setVisibility(View.GONE);
						} else if (2 == lookFor) {
							setTitle("合伙信息");
							msg_title.setText("合伙信息");
							tag_title.setText("我的标签");
							description_title.setText("合伙描述");
							details_content.setHint("请输入合伙描述");
							salaryView.setVisibility(View.GONE);
							title_name.setText("期望工作地");
							salarySeparatorView.setVisibility(View.GONE);
							personNumberView.setVisibility(View.GONE);
							personNumberSeparatorView.setVisibility(View.GONE);
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

						tradeStr = career.getString(trade_content.getTag()
								.toString());
						if (trade_content.equals("null")) {
							trade_content.setText("无");
						} else {
							trade_content.setText(tradeStr);
						}
						positionStr = career.getString(position_content
								.getTag().toString());
						position_content.setText(positionStr);
						yearStr = career.getString(service_year_content
								.getTag().toString());
						if (yearStr.equals("null")) {
							service_year_content.setText("无");
						} else {
							service_year_content.setText(yearStr);
						}

						workPlaceStr = career.getString(location_name.getTag()
								.toString());
						 if(containsEmoji(workPlaceStr)||containsEmoji(positionStr)){
								showToast("不支持输入Emoji表情符号");
								return;
							}
						if (workPlaceStr.equals("null")) {
							location_name.setText("无");
						} else {
							location_name.setText(workPlaceStr);
						}

						salaryStr = career.getString(salary_name.getTag()
								.toString());
						salary_name.setText(salaryStr);
						personNumberStr = career.getString(person_number_content
								.getTag().toString());
						if (personNumberStr.equals("null")) {
							person_number_content.setText("");
						} else {

							person_number_content.setText(personNumberStr);
						}
						if (career.has("tags")) {

							String tags = career.getString("tags");
							if (tags != null && !TextUtils.equals("null", tags)) {
								JSONArray tagJson = new JSONArray(tags);
								if (tagJson != null) {
									for (int i = 0; i < tagJson.length(); i++) {
										if (0 == i) {
											tag1 = tagJson.optString(i);
											btn_personal_tag1.setText(tag1);
										} else if (1 == i) {
											tag2 = tagJson.optString(i);
											btn_personal_tag2.setText(tag2);
										} else {
											tag3 = tagJson.optString(2);
											btn_personal_tag3.setText(tag3);
										}
									}
								}
							}
						}

						details = career.getString("details");
						if (TextUtils.isEmpty(details)
								|| TextUtils.equals("null", details)) {
							details_content.setText("");
						} else {
							details_content.setText(details);
						}

						JSONArray pictureJson = data.getJSONArray("pictures");
						for (int i = 0; i < pictureJson.length(); i++) {
							UserInfo info = new UserInfo();
							JSONObject picture = pictureJson.getJSONObject(i);
							info.url = Address.HOST_PICTURE
									+ picture.getString("filePath");

							// info.path = new
							// File(getExternalFilesDir(null),System.currentTimeMillis()-i*1000+"");
							String ext = info.url.substring(info.url
									.lastIndexOf(".") + 1);
							info.path = new File(getExternalFilesDir(null),
									IOUtil.toMd5(info.url.getBytes()) + "."
											+ ext);

							String path = info.path.getPath();
							drawables.add(info.url);
							// dataList.add(info);

							/*
							 * if(info.path.exists()){ Bitmap bmp = null;
							 * if(i==0) { int margin = (int)
							 * (Constants.screen_width *0.01); int big_pic_w =
							 * 2*base_w + margin; bmp =
							 * ImageUtils.getBitmapFromFile
							 * (info.path,big_pic_w,big_pic_w); } else { bmp =
							 * ImageUtils
							 * .getBitmapFromFile(info.path,base_w,base_w); }
							 * photos[i].setImageBitmap(bmp); }
							 */

							if (5 <= i) {
								break;
							}
						}
						refreshPhoto(false);

						/*
						 * fileDownload(new TaskResultListener() {
						 * 
						 * @Override public void result(ResposneBundle b) { //
						 * TODO Auto-generated method stub int i=0; for(UserInfo
						 * info:dataList){
						 * Log.w("pos"+i,info.path.getAbsolutePath());
						 * 
						 * } refreshPhoto(false); }
						 * 
						 * @Override public void failed(String message) { //
						 * TODO Auto-generated method stub
						 * 
						 * } },dataList);
						 */

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

		next_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// *****测试

				tradeStr = trade_content.getText().toString();
				positionStr = position_content.getText().toString();
				yearStr = service_year_content.getText().toString();
				workPlaceStr = location_name.getText().toString();
				salaryStr = salary_name.getText().toString();
				personNumberStr = person_number_content.getText().toString();
				tag1 = btn_personal_tag1.getText().toString();
				tag2 = btn_personal_tag2.getText().toString();
				tag3 = btn_personal_tag3.getText().toString();
				details = details_content.getText().toString();
				String digits = " /\\:*?<>|\"\n\t";
				for (int i = 0; i < positionStr.length(); i++) {
					if (digits.indexOf(positionStr.charAt(i)) > 0) {
						showToast("不能有特殊符号");
						return;
					}
				}
				 if(containsEmoji(details)||containsEmoji(positionStr)){
						showToast("不支持输入Emoji表情符号");
						return;
					}
				if (TextUtils.isEmpty(tradeStr)) {
					showToast("请选择行业");
					return;
				} else if (TextUtils.isEmpty(positionStr)
						|| positionStr.trim().isEmpty()) {
					showToast("请输入职位");
					return;
				} else if (TextUtils.isEmpty(yearStr)) {
					showToast("请输入工作年限");
					return;
				} else if (TextUtils.isEmpty(salaryStr) && salaryStr == null) {
					showToast("请输入薪酬");
					return;
				}
				positionStr = positionStr.replace(" ", "");
				HashMap<String, String> data = new HashMap<String, String>() {
					{

						put("token", Constants.TOKEN);
						put(trade_content.getTag().toString(), tradeStr);
						put(position_content.getTag().toString(), positionStr);

						if (!TextUtils.isEmpty(yearStr)) {
							put(service_year_content.getTag().toString(),
									yearStr);
						}
						if (!TextUtils.isEmpty(workPlaceStr)) {
							put(location_name.getTag().toString(), workPlaceStr);
						}
						if (!TextUtils.isEmpty(salaryStr)) {
							put(salary_name.getTag().toString(), salaryStr);
						}
						if (!TextUtils.isEmpty(personNumberStr)) {
							put(person_number_content.getTag().toString(),
									personNumberStr);
						}
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
							put("tags", array.toString());
						}

						if (!TextUtils.isEmpty(details)) {
							put(details_content.getTag().toString(), details);
						}

					}
				};
				RequestData request = HttpUtils.simplePostData(Address.HOST
						+ Address.UPDATE_CAREER, data);
				Log.e("perpose", "" + data);
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
							/* msg 信息更新成功 */
							 
							if (job.getInt("code") == -1) {
								showToast(job.getString("msg"));
							} else {
								showToast(job.getString("msg"));
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

	class LabelLongOnClickListener implements View.OnLongClickListener {

		@Override
		public boolean onLongClick(View arg0) {

			switch (arg0.getId()) {
			case R.id.btn_personal_tag1:
				if (btn_personal_tag1.getText().toString().equals("+")) {
					Toast.makeText(PersonalActivity.this, "标签为空",
							Toast.LENGTH_SHORT).show();
				} else {
					showAlertDialogWithCancel("删除", "是否要删除这个标签",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {

									btn_personal_tag1.setText(btn_personal_tag2
											.getText().toString());
									btn_personal_tag2.setText(btn_personal_tag3
											.getText().toString());
									btn_personal_tag3.setText("+");

								}
							}, "确定");
					return false;
				}
				break;
			case R.id.btn_personal_tag2:
				if (btn_personal_tag2.getText().toString().equals("+")) {
					Toast.makeText(PersonalActivity.this, "标签为空",
							Toast.LENGTH_SHORT).show();
				} else {
					showAlertDialogWithCancel("删除", "是否要删除这个标签",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									btn_personal_tag2.setText(btn_personal_tag3
											.getText().toString());
									btn_personal_tag3.setText("+");

								}
							}, "确定");
					return false;
				}
				break;
			case R.id.btn_personal_tag3:
				if (btn_personal_tag3.getText().toString().equals("+")) {
					Toast.makeText(PersonalActivity.this, "标签为空",
							Toast.LENGTH_SHORT).show();
				} else {
					showAlertDialogWithCancel("删除", "是否要删除这个标签",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
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
			// Intent in = new Intent(PersonalActivity.this,
			// TradeActivity.class);
			Intent in = new Intent(PersonalActivity.this, TagActivity.class);
			in.putExtra("title", tag_title.getText().toString());
			in.putExtra("phoneCode", getUsername());
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
						RequestData request = HttpUtils.simpleFileData(
								Address.HOST + Address.ADD_PICTURE, "packname",
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
									JSONObject job = new JSONObject(
											b.getContent());
									if (job.getInt("code") == -1) {
										showToast(job.getString("msg"));
									} else {
										drawables.add(cropImageUri.getPath());
										showToast("图片添加"+job.getString("msg"));
										refreshPhoto(false);
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
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						String st8 = getResources().getString(
								R.string.cant_find_pictures);
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							String picturePath = cursor.getString(columnIndex);
							cursor.close();
							cursor = null;

							if (picturePath == null
									|| picturePath.equals("null")) {
								Toast toast = Toast.makeText(this, st8,
										Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;
							}
							path = picturePath;

						} else {
							File file = new File(selectedImage.getPath());
							if (!file.exists()) {
								Toast toast = Toast.makeText(this, st8,
										Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;

							}
							path = file.getAbsolutePath();
						}
						cropImageUri = Uri
								.fromFile(new File(
										getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
										System.currentTimeMillis() + ".jpg"));
						ImageUtils.startCropImage(this,
								Uri.fromFile(new File(path)), cropImageUri,
								300, 300, REQUEST_CODE_CROP_IMAGE);

						/*
						 * try { path = ImageUtils.getCompressImageFile(new
						 * File(path),800,800); } catch (FileNotFoundException
						 * e) { // TODO Auto-generated catch block
						 * e.printStackTrace(); }
						 * 
						 * drawables.add(path); RequestData request =
						 * HttpUtils.simpleFileData
						 * (Address.HOST+Address.ADD_PICTURE, "packname", new
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
						 * refreshPhoto(false); } } catch (JSONException e) { //
						 * TODO Auto-generated catch block e.printStackTrace();
						 * }
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
						((Button) rootView.findViewById(whichLabel))
						.setText(result);
						break;
					case R.id.btn_personal_tag2:
						if(btn_personal_tag1.getText().toString().equals("+")){
							btn_personal_tag1.setText(result);
						}else {
							((Button) rootView.findViewById(whichLabel))
							.setText(result);
						}
						break;
					case R.id.btn_personal_tag3:
						if(btn_personal_tag1.getText().toString().equals("+")){
							btn_personal_tag1.setText(result);
						}else if(btn_personal_tag2.getText().toString().equals("+")){
							btn_personal_tag2.setText(result);
						}else {
							((Button) rootView.findViewById(whichLabel))
							.setText(result);
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
