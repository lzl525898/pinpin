/*package com.pinpin.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.model.ChatListModel;
import com.pinpin.model.UserInfo;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.ImageUtils;
import com.pinpin.view.DiscoverContainerView;

@SuppressLint("ValidFragment")
public class CopyOfPinpinFragment extends Fragment {
	public final static int START_OTHER_ACTIVITY = 1000;
	public int offset = 0;
	public int max = 10;
	public static boolean isButtonClick = false;
	Button dislike;
	Button like;
	private DiscoverContainerView contentView;
	ListView newsListView;
	List<ChatListModel> mNewsList;
	String column;
	Animation operatingAnim;
	ImageView roundImg;
	RelativeLayout loadingView;
	ConcurrentLinkedQueue<UserInfo> dataList;
    TextView tishiTxt ;
	public CopyOfPinpinFragment(String column) {
		super();

		this.column = column;
	}

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
		tishiTxt.setText("正在查找附近的人...");
	}

	public void dismissAnim(int secend) {
		loadingView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				loadingView.setVisibility(View.GONE);
				roundImg.clearAnimation();
			}
		}, secend * 1500);
    }
	
	public void stopAnim() {
		roundImg.clearAnimation();
		tishiTxt.setText("抱歉哦， 暂时没有您想找的人了");
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final MainActivity parentActivity = (MainActivity) getActivity();
		RelativeLayout rootView = (RelativeLayout) inflater.inflate(
				R.layout.pinpin_fragment, null);
		contentView = (DiscoverContainerView) rootView
				.findViewById(R.id.contentview);
		Log.e("Create", "PinPinFragment");
		loadingView = (RelativeLayout) rootView.findViewById(R.id.loading);
		roundImg = (ImageView) rootView.findViewById(R.id.xuanzhuan);
		tishiTxt = (TextView) rootView.findViewById(R.id.tishi);
		ImageView my_avatar = (ImageView) rootView.findViewById(R.id.avatar);
		if (!TextUtils.isEmpty(Constants.AVATAR_PATH)) {
			File path = new File(Constants.AVATAR_PATH);
			if (path.exists()) {
				my_avatar.setImageBitmap(ImageUtils.getBitmapFromFile(path,
						160, 160));
			}
		}
		initAnim();
		// startAnim();
		// parentActivity.showProgress("Test");
		
		 * search_btn.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 * 
		 * } });
		 
		dataList = contentView.getDataList();
		dislike = (Button) rootView.findViewById(R.id.dislike);
		like = (Button) rootView.findViewById(R.id.like);
		requestData(true);
		// ConcurrentLinkedQueue<UserInfo> dataList = contentView.getDataList();
		// for(int i = 1 ; i< 6;i++){
		// UserInfo user = new UserInfo();
		// user.drawable= getResources().getIdentifier("a"+i, "drawable",
		// "com.pinpin");
		// user.nickname = "测试姓名"+i;
		// user.position = "软件工程师";
		// user.salary = "月薪"+ i * 2000;
		// dataList.add(user);
		// }
		contentView.setSildeControlButton(dislike, like);
		contentView
				.setContainerInterface(new DiscoverContainerView.ContainerInterface() {

					@Override
					public void onFeelOperat(UserInfo userVo, int feelType) {
						if (CopyOfPinpinFragment.isButtonClick) {
							CopyOfPinpinFragment.isButtonClick = false;
							return;
						}
						// TODO Auto-generated method stub
						// if(userVo!=null)
						// Toast.makeText(getActivity(),feelType+
						// "hahaha-"+userVo.nickname, Toast.LENGTH_LONG).show();
						if (DiscoverContainerView.TYPE_HEARTBEAT == feelType) {
							like();
						} else if (DiscoverContainerView.TYPE_NOFEEL == feelType) {
							dislike();
						}
					}

					@Override
					public void loadMore() {
						// TODO Auto-generated method stub
						// ConcurrentLinkedQueue<UserInfo> dataList =
						// contentView.getDataList();
						// for(int i = 1 ; i< 10;i++){
						// UserInfo user = new UserInfo();
						// user.drawable= getResources().getIdentifier("a"+i,
						// "drawable", "com.pinpin");
						// user.nickname = "测试姓名"+i;
						// user.position = "软件工程师";
						// user.salary = "月薪"+ i * 2000;
						// dataList.add(user);
						// }
						requestData(false);

					}
				});
		// contentView.initCardView(getActivity());
		// LinearLayout rootView = new LinearLayout(getActivity());
		// rootView.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
		// rootView.setBackgroundColor(Color.GREEN);
		return rootView;
	}

	private void requestData(final boolean isFirst) {

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
									path = new File(path, System
											.currentTimeMillis()
											+ (int) Math.random()
											* 1000
											+ ".img");
									if (!path.exists()) {
										try {
											// path.mkdirs();
											path.createNewFile();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									info.path = path;

									Log.e("创建图片下载地址",
											info.path.getAbsolutePath());
								}
								dataList.add(info);
							}
							// Log.e("offset-before",offset+"");
							// offset = offset + arr.length();
							// Log.e("offset-after",offset+"");
							((MainActivity) getActivity()).fileDownload(
									new TaskResultListener() {

										@Override
										public void result(ResposneBundle b) {
											// TODO Auto-generated method stub
											// if(isFirst){
											contentView
													.initCardView(getActivity());
											// }
											dislike.setVisibility(View.VISIBLE);
											like.setVisibility(View.VISIBLE);
											dismissAnim(2);
										}

										@Override
										public void failed(String message) {
											// TODO Auto-generated method stub

										}
									}, dataList);

						} else {
							((MainActivity) getActivity())
									.showToast("抱歉哦， 暂时没有您想找的人了");
							stopAnim();
						}

					} else {
						((MainActivity) getActivity()).showToast(job
								.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void failed(String message) {
				// TODO Auto-generated method stub

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

	public void like() {
		// TODO Auto-generated method stub
		if (like != null) {
			like.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					like.performClick();
				}
			}, 550);

		}

	}

	public void dislike() {
		// TODO Auto-generated method stub
		if (dislike != null) {
			dislike.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					dislike.performClick();
				}
			}, 550);

		}
	}

}
*/