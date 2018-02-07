package com.pinpin.ui;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.model.UserInfo;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.adapter.PinpinItemAdapter;
import com.pinpin.utils.IOUtil;
import com.pinpin.utils.ImageUtils;
import com.pinpin.view.CircleImageView;
import com.pinpin.view.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.pinpin.view.lorentzos.flingswipe.SwipeFlingAdapterView.OnItemClickListener;

@SuppressLint("ValidFragment")
public class PinpinFragment extends Fragment implements OnClickListener {
	MainActivity parentActivity;
	public final static int START_OTHER_ACTIVITY = 1000;
	public int offset = 0;
	public int max = 5;
	public static boolean isButtonClick = false;
	Button dislike;
	Button like;
	private SwipeFlingAdapterView flingContainer;
	String column;
	Animation operatingAnim;
	ImageView roundImg;
	ImageView roundImgpoint;
	LinearLayout loadingView;
	PinpinItemAdapter adapter;
	ImageView im_tx;
	CopyOnWriteArrayList<UserInfo> dataList;
	Button tishiTxt;
	CircleImageView my_avatar;
	File path;
	static boolean search=false;
	void initAnim() {
		if (operatingAnim == null) {
			operatingAnim = AnimationUtils.loadAnimation(getActivity(),
					R.anim.round);
			LinearInterpolator lin = new LinearInterpolator();
			operatingAnim.setInterpolator(lin);

		}
	}

	public void startAnim() {
		loadingView.setVisibility(View.VISIBLE);
		roundImg.startAnimation(operatingAnim);
		roundImgpoint.startAnimation(operatingAnim);
		tishiTxt.setText("正在查找附近的人...");
		tishiTxt.setBackgroundResource(R.drawable.bg_zikuang);
		flingContainer.setVisibility(View.GONE);
		like.setVisibility(View.GONE);
		dislike.setVisibility(View.GONE);
		my_avatar.setOnClickListener(null);
	}

	public void dismissAnim(int secend) {
		loadingView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				loadingView.setVisibility(View.GONE);
				roundImg.clearAnimation();
				my_avatar.createCircle(ImageUtils.getBitmapFromFile(path, 600,
						600));
				flingContainer.setVisibility(View.VISIBLE);
				like.setVisibility(View.VISIBLE);
				dislike.setVisibility(View.VISIBLE);
			}
		}, secend * 1250);
	}

	public void stopAnim() {
		loadingView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				roundImg.clearAnimation();
				roundImgpoint.clearAnimation();
				tishiTxt.setText("暂时没有您想找的人了");
				my_avatar.createCircle(my_avatar.creategrayCircle(ImageUtils
						.getBitmapFromFile(path, 600, 600)));
				tishiTxt.setBackgroundResource(R.drawable.bg_zikuang2);
				tishiTxt.setOnClickListener(new MyOnclick());
				my_avatar.setOnClickListener(new MyOnclick());
			}
		}, 2500);

	}

	// 点击事件
	public class MyOnclick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			requestData();
			my_avatar
					.createCircle(ImageUtils.getBitmapFromFile(path, 600, 600));
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentActivity = (MainActivity) getActivity();
		RelativeLayout rootView = (RelativeLayout) inflater.inflate(
				R.layout.pinpin_fragment, null);
		Log.e("Create", "PinPinFragment");
		flingContainer = (SwipeFlingAdapterView) rootView
				.findViewById(R.id.frame);
		loadingView = (LinearLayout) rootView.findViewById(R.id.loading);
		roundImg = (ImageView) rootView.findViewById(R.id.xuanzhuan);
		roundImgpoint = (ImageView) rootView.findViewById(R.id.xuanzhuan_point);
		tishiTxt = (Button) rootView.findViewById(R.id.tishi);
		my_avatar = (CircleImageView) rootView.findViewById(R.id.avatar);
		initAnim();

		dataList = new CopyOnWriteArrayList<UserInfo>();
		adapter = new PinpinItemAdapter(getActivity(), dataList);

		dislike = (Button) rootView.findViewById(R.id.left);
		like = (Button) rootView.findViewById(R.id.right);
		dislike.setOnClickListener(this);
		like.setOnClickListener(this);
		flingContainer.setAdapter(adapter);
		flingContainer.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClicked(int itemPosition, Object dataObject) {
				// TODO Auto-generated method stub
				UserInfo info = (UserInfo) dataObject;
				Intent in = new Intent(getActivity(), OtherActivity.class);
				in.putExtra("info", info.json);
				if (info.path != null) {
					in.putExtra("path", info.path.getAbsolutePath());
				}

				in.putExtra("url", info.url);
				parentActivity.startActivityForResult(in,
						PinpinFragment.START_OTHER_ACTIVITY);
				parentActivity.overridePendingTransition(R.anim.fade_in,
						R.anim.fade_out);
			}
		});
		flingContainer
				.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
					@Override
					public void removeFirstObjectInAdapter() {
						// this is the simplest way to delete an object from the
						// Adapter (/AdapterView)
						// makeToast(getActivity(), "干掉了一个对象!");
						if (dataList.size() == 0) {
							return;
						}
						dataList.remove(0);
						adapter.notifyDataSetChanged();
					}

					@Override
					public void onLeftCardExit(Object dataObject) {
						UserInfo info = (UserInfo) dataObject;
						choose(false, info.id);
						// makeToast(getActivity(), "不喜欢!");
					}

					@Override
					public void onRightCardExit(Object dataObject) {
						// makeToast(getActivity(), "喜欢!");
						UserInfo info = (UserInfo) dataObject;
						choose(true, info.id);
					}

					@Override
					public void onAdapterAboutToEmpty(int itemsInAdapter) {
						// Ask for more data here
						// dataList.add("XML ".concat(String.valueOf(i)));
						// arrayAdapter.notifyDataSetChanged();
						// makeToast(getActivity(), itemsInAdapter+"加载更多!");
						requestData();
					}

					@Override
					public void onScroll(float scrollProgressPercent) {
						View view = flingContainer.getSelectedView();
						if (view == null)
							return;
						view.findViewById(R.id.item_swipe_right_indicator)
								.setAlpha(
										scrollProgressPercent < 0 ? -scrollProgressPercent
												: 0);
						view.findViewById(R.id.item_swipe_left_indicator)
								.setAlpha(
										scrollProgressPercent > 0 ? scrollProgressPercent
												: 0);
					}
				});

//		requestData();

		return rootView;
	}

	private void requestData() {
		startAnim();
		dataList.clear();
		HashMap<String, String> data = new HashMap<String, String>() {
			{
				put("token", Constants.TOKEN);
				put("max", max + "");
				// Log.e("offset",offset+"");
				put("offset", offset + "");

			}
		};
		RequestData request = HttpUtils.simplePostData(Address.HOST
				+ Address.GET_FRIENDS, data);
		((MainActivity) getActivity()).startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {

				// TODO Auto-generated method stub
				if (b.getContent() == null) {
					((MainActivity) getActivity()).showToast("抱歉哦， 服务器出错了");
					return;
				}
				Log.w("获取推荐朋友的结果", b.getContent());
				try {
					JSONObject job = new JSONObject(b.getContent());
					// showToast(job.getString("msg"));
					if (job.getInt("code") == -1000) {
						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
						getActivity().finish();
						;
					} else if (job.getInt("code") == 1) {
						JSONArray arr = job.getJSONArray("data");
						if (arr.length() > 0) {
							Log.e("新推荐朋友", arr.length() + "人");
							for (int i = 0; i < arr.length(); i++) {
								UserInfo info = new UserInfo();
								JSONObject dataJob = arr.getJSONObject(i);
								info.nickname = dataJob.getString("username");
								info.id = dataJob.getString("id");
								JSONObject carrer = dataJob
										.getJSONObject("career");
								info.salary = carrer.getString("position");
								info.position = carrer.getString("trade");
								if (carrer.has("tags")) {
									/*
									 * JSONArray ja =
									 * carrer.getJSONArray("tags");
									 * if(ja!=null&&ja.length()>0){ info.tags =
									 * new String[ja.length()]; for(int
									 * ii=0;ii<ja.length();ii++){ info.tags[ii]
									 * = ja.getString(ii); } }
									 */
									String temp = carrer.getString("tags");
									if (!TextUtils.isEmpty(temp)
											&& !TextUtils.equals("null", temp)) {
										JSONArray ja = new JSONArray(temp);
										if (ja != null && ja.length() > 0) {
											info.tags = new String[ja.length()];
											for (int ii = 0; ii < ja.length(); ii++) {
												info.tags[ii] = ja
														.getString(ii);
											}
										}
									}

								}
								info.json = dataJob.toString();

								JSONArray pictures = dataJob
										.getJSONArray("pictures");
								if (pictures.length() > 0) {
									String relative = pictures.getJSONObject(0)
											.getString("filePath");
									info.url = Address.HOST_PICTURE + relative;

									// info.path = new
									// File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+(int)Math.random()*1000+".img");
									File path = getActivity()
											.getExternalFilesDir(
													android.os.Environment.DIRECTORY_PICTURES);
									String ext = info.url.substring(info.url
											.lastIndexOf(".") + 1);
									path = new File(path, IOUtil.toMd5(info.url
											.getBytes()) + "." + ext);
									/*
									 * if (!path.exists()) { try { //
									 * path.mkdirs(); path.createNewFile(); }
									 * catch (IOException e) { // TODO
									 * Auto-generated catch block
									 * e.printStackTrace(); } }
									 */
									info.path = path;

									Log.e("创建图片下载地址",
											info.path.getAbsolutePath());
								}
								dataList.add(info);
								Log.e("新增一个朋友", "图片地址" + info.url + "---总朋友数量"
										+ dataList.size());
							}

							((MainActivity) getActivity()).fileDownload(
									new TaskResultListener() {

										@Override
										public void result(ResposneBundle b) {
											// TODO Auto-generated method stub

											adapter.notifyDataSetChanged();
											Log.e("刷新", "刷新列表");
											dismissAnim(2);
										}

										@Override
										public void failed(String message) {
											// TODO Auto-generated method stub
											adapter.notifyDataSetChanged();
										}
									}, dataList);

						} else {
							if (getActivity() == null) {
								return;
							}
							/*
							 * ((MainActivity) getActivity())
							 * .showToast("抱歉哦， 暂时没有您想找的人了");
							 */
							stopAnim();
						}

					} else {
						((MainActivity) getActivity()).showToast(job
								.getString("msg"));
						stopAnim();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					stopAnim();
				}

			}

			@Override
			public void failed(final String message) {
				// TODO Auto-generated method stub
				parentActivity.runOnUiThread(new Runnable() {
					public void run() {
						parentActivity.showToast(message);
						tishiTxt.setText("无网络，点击重试");
											}
				});

				
				stopAnim();
				
			}
		}, request, false);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.e("Pinpin!!!!!!!", "onResume");
		// TODO Auto-generated method stub
		// MainActivity parentActivity = (MainActivity ) getActivity();
		// parentActivity.showHeader();
//		if(search){
//			requestData();
//		}
//		if(search){
//			requestData();
//		}
		requestData();
		if (!TextUtils.isEmpty(Constants.AVATAR_PATH)) { 
			path = new File(Constants.AVATAR_PATH);
			if (path.exists()) {
				// my_avatar.setImageBitmap(ImageUtils.getBitmapFromFile(path,
				// 160, 160));
				if (tishiTxt.getText().toString() == "正在查找附近的人...") {
					my_avatar.createCircle(ImageUtils.getBitmapFromFile(path,
							600, 600));
				} else {
					my_avatar.createCircle(my_avatar
							.creategrayCircle(ImageUtils.getBitmapFromFile(
									path, 600, 600)));
				}
			} else {
				my_avatar.setImageResource(R.drawable.default_avatar);
			}
		} else {
			path = null;
			my_avatar.setImageResource(R.drawable.default_avatar);
		}

		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	void choose(final boolean islike, final String userId) {
		HashMap<String, String> data = new HashMap<String, String>() {
			{
				put("token", Constants.TOKEN);
				put("objectId", userId);
				if (islike) {
					put("isLike", "true");
				} else {
					put("isLike", "false");
				}

			}
		};

		RequestData request = HttpUtils.simplePostData(Address.HOST
				+ Address.LIKE, data);
		parentActivity.startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result", b.getContent());

				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") == -1) {
						parentActivity.showToast(job.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void failed(final String message) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						parentActivity.showToast(message);
					}
				});

			}
		}, request, false);
	}

	static void makeToast(Context ctx, String s) {
		Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
	}

	public void like() {
		flingContainer.getTopCardListener().selectRight();
	}

	public void dislike() {
		flingContainer.getTopCardListener().selectLeft();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left:
			dislike();
			break;
		case R.id.right:
			like();
			break;
		default:
			break;
		}
	}
}
