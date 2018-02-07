package com.pinpin.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.pinpin.R;
import com.pinpin.app.PinApplication;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.adapter.ChatAdapter;
import com.pinpin.ui.adapter.ExpressionAdapter;
import com.pinpin.ui.adapter.ExpressionPagerAdapter;
import com.pinpin.utils.CommonUtils;
import com.pinpin.utils.ImageUtils;
import com.pinpin.utils.SmileUtils;
import com.pinpin.view.CircleImageView;
import com.pinpin.view.ExpandGridView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends BaseActivity implements EMEventListener, OnClickListener {
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	public static final String COPY_IMAGE = "EASEMOBIMG";
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	// 给谁发送消息
	private String toChatUsername = MainActivity.sendTo;
	// NewMessageBroadcastReceiver msgReceiver;
	private File cameraFile;
	private String avatarPath;
	private String json;
	Button btnSend;
	EditText editContent;
	ListView listviewChat;
	RelativeLayout chat_bg;
	ChatAdapter adapter;
	ViewPager expressionViewpager;
	private List<String> reslist;
	// ArrayList<PPMessage> chatDataList;
	RelativeLayout edittext_layout;
	private View buttonSetModeVoice;
	private View buttonSetModeKeyboard;
	private View buttonPressToSpeak;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	Button btn_more;
	private View more;
	private View recordingContainer;
	PowerManager.WakeLock wakeLock;
	private VoiceRecorder voiceRecorder;
	private ProgressBar loadmorePB;
	ImageView micImage;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;

	TextView recordingHint;
	private Drawable[] micImages;
	private EMConversation conversation;
	public String playMsgId;
	public static boolean isSpeaker = true;
	AudioManager audioManager = null;
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};

	private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                case 0:
                    //拔出耳机
                	isSpeaker = true;
                	audioManager.setSpeakerphoneOn(isSpeaker);
                    break;
                case 1:
                    //插耳机自动播放
                	isSpeaker = false;
                	audioManager.setSpeakerphoneOn(isSpeaker);
                    break;
                default:
                    break;
                }

            }
         //只监听拔出耳机
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
        	isSpeaker = true;
        	audioManager.setSpeakerphoneOn(isSpeaker);
        }
		}

	};

	private void registerHeadsetPlugReceiver() {
		IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
		registerReceiver(headsetPlugReceiver, intentFilter);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EMChatManager.getInstance().unregisterEventListener(this);

		// if (msgReceiver != null) {
		// unregisterReceiver(msgReceiver);
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// msgReceiver = new NewMessageBroadcastReceiver();
		// IntentFilter intentFilter = new IntentFilter(EMChatManager
		// .getInstance().getNewMessageBroadcastAction());
		// intentFilter.setPriority(3);
		// registerReceiver(msgReceiver, intentFilter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		registerHeadsetPlugReceiver();
	}

	public void personalClick(View v) {
		Intent in = new Intent(v.getContext(), OtherActivity.class);
		in.putExtra("info", json);
		in.putExtra("path", avatarPath);
		in.putExtra("url", "");
		in.putExtra("fromChat", true);
		Activity act = (Activity) v.getContext();
		act.startActivityForResult(in, PinpinFragment.START_OTHER_ACTIVITY);
		act.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		dismissPopupWindow();
	}

	public void clearClick(View v) {
		EMChatManager.getInstance().clearConversation(MainActivity.sendTo);
		if (adapter != null) {
			adapter.refresh();
		}
		dismissPopupWindow();
	}

	public void claimClick(View v) {
		showCustDialog0();
		dismissPopupWindow();
	}

	public void deleteClick(View v) {
		showAlertDialogWithCancel("提醒", "确定要删除该用户吗？", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
		}, "取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				HashMap<String, String> data = new HashMap<String, String>() {
					{
						put("phoneCode", MainActivity.sendTo);

					}
				};
				RequestData request = HttpUtils.simplePostData(Address.HOST + Address.CANCEL_MATCH, data);
				startHttpTask(new TaskResultListener() {

					@Override
					public void result(ResposneBundle b) {
						// TODO Auto-generated method stub
						if (b.getContent() == null) {
							showToast("出错了，服务器异常");
							return;
						}

						try {
							JSONObject job = new JSONObject(b.getContent());
							if (job.getInt("code") != -1) {
								showToast("删除用户成功");
								// Intent in = new Intent();
								// in.putExtra("refresh", true);
								// setResult(Activity.RESULT_OK, in);
								ChatFragment.shouldRefresh = true;
								ChatFragment.shouldDeleteWho = MainActivity.sendTo;
								finish();
							} else {
								showToast("删除用户失败");
							}
						} catch (JSONException e) {
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
		}, "删除");
		EMChatManager.getInstance().clearConversation(MainActivity.sendTo);
		dismissPopupWindow();
	}

	public void cancelClick(View v) {
		dismissPopupWindow();
	}

	@SuppressLint("NewApi")
	public void showCustDialog0() {

		
		if (ab == null) {
			ab = new AlertDialog.Builder(this).create();
		}
		ab.setCancelable(false);
		ab.show();

		ab.getWindow().setContentView(R.layout.own_dialog);
		TextView title_txt = (TextView) ab.getWindow().findViewById(R.id.title);
		TextView btn1 = (TextView) ab.getWindow().findViewById(R.id.content1);
		TextView btn2 = (TextView) ab.getWindow().findViewById(R.id.content2);
		TextView btn3 = (TextView) ab.getWindow().findViewById(R.id.content3);
		TextView btn4 = (TextView) ab.getWindow().findViewById(R.id.content4);
		TextView btn5 = (TextView) ab.getWindow().findViewById(R.id.content5);
		TextView btn6 = (TextView) ab.getWindow().findViewById(R.id.content6);
		TextView btn7 = (TextView) ab.getWindow().findViewById(R.id.content7);
		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				complain(((TextView) v).getText().toString(), "--");
			}
		});

		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				complain(((TextView) v).getText().toString(), "--");
			}
		});

		btn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				complain(((TextView) v).getText().toString(), "--");
			}
		});

		btn4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				complain(((TextView) v).getText().toString(), "--");
			}
		});

		btn5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				complain(((TextView) v).getText().toString(), "--");
			}
		});
		btn6.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ab.dismiss();
				showCustDialog1();

			}
		});
		btn7.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ab.dismiss();

			}
		});
	}

	@SuppressLint("NewApi")
	public void showCustDialog1() {

		if (ab == null) {
			ab = new AlertDialog.Builder(this).create();
		}
		ab.setCancelable(false);
		ab.show();
		
		
		ab.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		ab.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		ab.getWindow().setContentView(R.layout.own_edit_dialog);
		TextView title_txt = (TextView) ab.getWindow().findViewById(R.id.title);
		final EditText editText = (EditText) ab.getWindow().findViewById(R.id.content1);
		   
		/*
		 * editText.postDelayed( new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * //设置可获得焦点 editText.setFocusable(true);
		 * editText.setFocusableInTouchMode(true); //请求获得焦点
		 * editText.requestFocus(); //调用系统输入法 InputMethodManager inputManager =
		 * (InputMethodManager) editText
		 * .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		 * inputManager.showSoftInput(editText, 0); } },1000);
		 */

		Button btnCancel = (Button) ab.getWindow().findViewById(R.id.cancel);
		Button btnOK = (Button) ab.getWindow().findViewById(R.id.ok);

		btnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//ab.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				//ab.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				String value = editText.getText().toString();
				editText.setFocusable(true);
				if (TextUtils.isEmpty(value)) {
					showToast("请输入举报原因！");
				} else {
					complain("其他", value);
		////
					ab.dismiss();
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        
				ab.dismiss();
			}
		});

	}

	private void complain(final String type, final String content) {
		HashMap<String, String> data = new HashMap<String, String>() {
			{
				put("objectId", MainActivity.sendTo);
				put("reason", type);
				put("content", content);

			}
		};
		RequestData request = HttpUtils.simplePostData(Address.HOST + Address.COMPLAIN, data);
		startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				if (b.getContent() == null) {
					showToast("出错了，服务器异常");
					return;
				}

				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") != -1) {
						showToast("举报成功！");
						ab.dismiss();
					} else {
						showToast("举报失败！");
					}
				} catch (JSONException e) {
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

	@SuppressLint("NewApi")
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		footer.setVisibility(View.GONE);
		// setSearchBtnGone();
		setSearchButton(R.drawable.btn_gengduo, 100, 100);
		initPopuptWindow(R.layout.chat_menu);

		Intent data = getIntent();
		avatarPath = data.getStringExtra("path");
		json = data.getStringExtra("json");
		search_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopupWindow();

			}
		});
		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EMChatManager.getInstance().unregisterEventListener(ChatActivity.this);
				finish();
			}
		});
		setTitle(data.getStringExtra("username"));
		inflater.inflate(R.layout.chat_activity, container);
		chat_bg = (RelativeLayout) findViewById(R.id.chat_gsbg);
		if (avatarPath != null) {
			chat_bg.setBackground(CircleImageView.BoxBlurFilter(ImageUtils.getBitmapPath(avatarPath, 600, 600)));
		}
		recordingContainer = findViewById(R.id.recording_container);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		voiceRecorder = new VoiceRecorder(micImageHandler);
		micImage = (ImageView) findViewById(R.id.mic_image);
		btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(this);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		btn_more = (Button) findViewById(R.id.btn_more);
		more = findViewById(R.id.more);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);

		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		editContent = (EditText) findViewById(R.id.edit_content);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		iv_emoticons_checked.setOnClickListener(this);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };
		editContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		editContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		editContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btn_more.setVisibility(View.GONE);
					btnSend.setVisibility(View.VISIBLE);
				} else {
					btn_more.setVisibility(View.VISIBLE);
					btnSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		listviewChat = (ListView) findViewById(R.id.listview_chat);
		listviewChat.setDividerHeight(0);
		// chatDataList = new ArrayList<PPMessage>();
		/*
		 * for(int i = 0;i<10;i++){ chatDataList.add(new
		 * PPMessage(TYPE.SEND,R.drawable.a_liudi, "eee",
		 * DateUtils.getTimestampString(new Date())));
		 * 
		 * }
		 */
		adapter = new ChatAdapter(this, toChatUsername, avatarPath, json);
		listviewChat.setAdapter(adapter);
		listviewChat.setOnScrollListener(new ListScrollListener());
		adapter.refreshSelectLast();
		listviewChat.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();

				return false;
			}
		});

		// 未读消息数清零
		EMConversation conversation = EMChatManager.getInstance().getConversation(MainActivity.sendTo);
		conversation.markAllMessagesAsRead();

		// btnSend.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// String text = editContent.getText().toString();
		// if(TextUtils.isEmpty(text)){
		// Toast.makeText(getApplicationContext(), "不能发送空信息",
		// Toast.LENGTH_LONG).show();
		// return;
		// }
		// conversation = EMChatManager.getInstance()
		// .getConversation(MainActivity.sendTo);
		// sendText(text,MainActivity.sendTo,conversation);
		// }
		// });

	}

	public void onClick(View view) {
		// String st1 =
		// getResources().getString(R.string.not_connect_to_server);
		int id = view.getId();
		switch (view.getId()) {
		case R.id.btn_send:// 点击发送按钮(发文字和表情)
			String text = editContent.getText().toString();
			if (TextUtils.isEmpty(text)) {
				Toast.makeText(getApplicationContext(), "不能发送空信息", Toast.LENGTH_LONG).show();
				return;
			}
			conversation = EMChatManager.getInstance().getConversation(MainActivity.sendTo);
			sendText(text, MainActivity.sendTo, conversation);
			break;
		case R.id.btn_take_picture:// 点击照相图标
			selectPicFromCamera();
			break;
		case R.id.btn_picture:// 点击图片图标
			selectPicFromLocal();
			break;
		case R.id.iv_emoticons_normal:// 点击图片图标
			more.setVisibility(View.VISIBLE);
			iv_emoticons_normal.setVisibility(View.INVISIBLE);
			iv_emoticons_checked.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.GONE);
			emojiIconContainer.setVisibility(View.VISIBLE);
			hideKeyboard();
			break;
		case R.id.iv_emoticons_checked:// 点击图片图标
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			break;
		case R.id.btn_file://
			selectFileFromLocal();
			break;
		}

	}

	

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}
	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, 0).show();
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(),
				Constants.USERNAME + System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}
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
			Log.e("url", android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

	}

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			// case RESULT_CODE_COPY: // 复制消息
			// EMMessage copyMsg = ((EMMessage)
			// adapter.getItem(data.getIntExtra("position", -1)));
			// // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
			// // ((TextMessageBody) copyMsg.getBody()).getMessage()));
			// clipboard.setText(((TextMessageBody)
			// copyMsg.getBody()).getMessage());
			// break;
			// case RESULT_CODE_DELETE: // 删除消息
			// EMMessage deleteMsg = (EMMessage)
			// adapter.getItem(data.getIntExtra("position", -1));
			// conversation.removeMessage(deleteMsg.getMsgId());
			// adapter.refreshSeekTo(data.getIntExtra("position",
			// adapter.getCount()) - 1);
			// break;

			// case RESULT_CODE_FORWARD: // 转发消息
			// EMMessage forwardMsg = (EMMessage)
			// adapter.getItem(data.getIntExtra("position", 0));
			// Intent intent = new Intent(this, ForwardMessageActivity.class);
			// intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
			// startActivity(intent);
			//
			// break;

			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // 清空消息
			Log.e("--1", "--1");
			if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
				
			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
						Log.e("sendurl", selectedImage.toString());
						Log.e("--2", "--2");
					}
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			}
			/*
			 * else if (requestCode == REQUEST_CODE_TEXT || requestCode ==
			 * REQUEST_CODE_VOICE || requestCode == REQUEST_CODE_PICTURE ||
			 * requestCode == REQUEST_CODE_FILE) { resendMessage(); } else if
			 * (requestCode == REQUEST_CODE_COPY_AND_PASTE) { // 粘贴 if
			 * (!TextUtils.isEmpty(clipboard.getText())) { String pasteText =
			 * clipboard.getText().toString(); if
			 * (pasteText.startsWith(COPY_IMAGE)) { // 把图片前缀去掉，还原成正常的path
			 * sendPicture(pasteText.replace(COPY_IMAGE, "")); }
			 * 
			 * } }
			 */
		}
	}

	/**
	 * 发送文件
	 * 
	 * @param uri
	 */
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			String st7 = getResources().getString(R.string.file_does_not_exist);
			Toast.makeText(getApplicationContext(), st7, 0).show();
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			String st6 = getResources().getString(R.string.the_file_is_not_greater_than_10_m);
			Toast.makeText(getApplicationContext(), st6, 0).show();
			return;
		}

		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);

		message.setReceipt(toChatUsername);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
		message.addBody(body);
		conversation = EMChatManager.getInstance().getConversation(MainActivity.sendTo);
		conversation.addMessage(message);
		// listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		setResult(RESULT_OK);
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		conversation = EMChatManager.getInstance().getConversation(MainActivity.sendTo);
		conversation.addMessage(message);

		// listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		setResult(RESULT_OK);
		// more(more);
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
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
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	public void editClick(View v) {
		listviewChat.setSelection(listviewChat.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		btnSend.setVisibility(View.GONE);
		btn_more.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.GONE);

	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		editContent.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// editContent.setVisibility(View.VISIBLE);
		editContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(editContent.getText())) {
			btn_more.setVisibility(View.VISIBLE);
			btnSend.setVisibility(View.GONE);
		} else {
			btn_more.setVisibility(View.GONE);
			btnSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 点击清空聊天记录
	 * 
	 * @param view
	 */
	public void emptyHistory(View view) {
		// String st5 =
		// getResources().getString(R.string.Whether_to_empty_all_chats);
		// startActivityForResult(
		// new Intent(this, AlertDialog.class).putExtra("titleIsCancel",
		// true).putExtra("msg", st5).putExtra("cancel", true),
		// REQUEST_CODE_EMPTY_HISTORY);
	}

	/**
	 * 点击进入群组详情
	 * 
	 * @param view
	 */
	public void toGroupDetails(View view) {
		// if(group == null){
		// Toast.makeText(getApplicationContext(), R.string.gorup_not_found,
		// 0).show();
		// return;
		// }
		// startActivityForResult((new Intent(this,
		// GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
		// REQUEST_CODE_GROUP_DETAIL);
	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			System.out.println("more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
		} else {
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * 事件监听
	 * 
	 * see {@link EMNotifierEvent}
	 */
	@Override
	public void onEvent(final EMNotifierEvent event) {

		switch (event.getEvent()) {
		case EventNewMessage: {
			// 获取到message
			final EMMessage message = (EMMessage) event.getData();

			String username = null;
			// 群组消息
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
			} else {
				// 单聊消息
				username = message.getFrom();
			}
			/*
			 * runOnUiThread(new Runnable() {
			 * 
			 * @Override public void run() { // TODO Auto-generated method stub
			 * 
			 * Toast.makeText(getApplicationContext(), "来自：" + message.getFrom()
			 * + "的消息", Toast.LENGTH_LONG).show();
			 * 
			 * } });
			 */
			// 如果是当前会话的消息，刷新聊天页面
			if (username.equals(toChatUsername)) {
				refreshUIWithNewMessage();
				// 声音和震动提示有新消息
				PinApplication.getInstance().getNotifier().viberateAndPlayTone(message);
			} else {
				// 如果消息不是和当前聊天ID的消息
				PinApplication.getInstance().getNotifier().onNewMsg(message);
			}

			break;
		}
		case EventDeliveryAck: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			refreshUI();
			break;
		}
		case EventReadAck: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			refreshUI();
			break;
		}
		case EventOfflineMessage: {
			// a list of offline messages
			// List<EMMessage> offlineMessages = (List<EMMessage>)
			// event.getData();
			refreshUI();
			break;
		}
		default:
			break;
		}

	}

	private void refreshUIWithNewMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.refreshSelectLast();
			}
		});
	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.refresh();
			}
		});
	}

	/*
	 * private class NewMessageBroadcastReceiver extends BroadcastReceiver {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { // 注销广播
	 * abortBroadcast();
	 * 
	 * // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成） String msgId =
	 * intent.getStringExtra("msgid"); // 发送方 String username =
	 * intent.getStringExtra("from"); //
	 * 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象 EMMessage message =
	 * EMChatManager.getInstance().getMessage(msgId); EMMessage.Type type =
	 * message.getType(); switch (type) { case TXT: // 获取消息内容，发送消息 String
	 * content = ((TextMessageBody) message.getBody()) .getMessage();
	 * Log.e("消息", content); Toast.makeText(getApplicationContext(), content,
	 * Toast.LENGTH_LONG).show(); //chatDataList.add(new
	 * PPMessage(TYPE.RECEIVE,R.drawable.a_mingling, content,
	 * DateUtils.getTimestampString(new Date(message.getMsgTime()))));
	 * //adapter.notifyDataSetChanged();
	 * 
	 * //listviewChat.setSelection(chatDataList.size()-1); break; case IMAGE: //
	 * 发送图片 String filePath = ((ImageMessageBody) message.getBody())
	 * .getLocalUrl(); if (filePath != null) { File file = new File(filePath);
	 * if (!file.exists()) { // 不存在大图发送缩略图 // filePath = //
	 * ImageUtils.getThumbnailImagePath(filePath); }
	 * 
	 * } break; default: break; } conversation = EMChatManager.getInstance()
	 * .getConversation(username); Log.e("消息", message.getFrom()); Log.e("消息",
	 * message.getTo()); Log.e("消息", message.toString());
	 * 
	 * //sendText("我是你的好友，收到消息了吧!", "test1", conversation);
	 * 
	 * // 如果是群聊消息，获取到group id if (message.getChatType() == ChatType.GroupChat) {
	 * username = message.getTo(); } if (!username.equals(username)) { //
	 * 消息不是发给当前会话，return return; } } }
	 */

	private void sendText(String content, String to, EMConversation conversation) {

		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊

			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(to);
			// 把messgage加到conversation中
			conversation = EMChatManager.getInstance().getConversation(MainActivity.sendTo);
			conversation.addMessage(message);
			/*
			 * EMChatManager.getInstance().sendMessage(message, new EMCallBack()
			 * {
			 * 
			 * @Override public void onError(int arg0, String arg1) { // TODO
			 * Auto-generated method stub Log.e("消息发送", "失败"); }
			 * 
			 * @Override public void onProgress(int arg0, String arg1) { // TODO
			 * Auto-generated method stub
			 * 
			 * }
			 * 
			 * @Override public void onSuccess() { // TODO Auto-generated method
			 * stub Log.e("消息发送", "成功"); } });
			 */

			// chatDataList.add(new PPMessage(TYPE.SEND,R.drawable.a_liudi,
			// content, DateUtils.getTimestampString(new
			// Date(message.getMsgTime()))));
			// adapter.notifyDataSetChanged();
			// listviewChat.setSelection(chatDataList.size()-1);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refreshSelectLast();
			editContent.setText("");

			setResult(RESULT_OK);

		}
	}

	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
					Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying) {
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					}
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					File f = getExternalFilesDir(null);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(R.string.Recording_without_permission);
					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
									Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			// if (chatType == CHATTYPE_GROUP)
			// message.setChatType(ChatType.GroupChat);
			message.setReceipt(toChatUsername);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);
			conversation = EMChatManager.getInstance().getConversation(MainActivity.sendTo);
			conversation.addMessage(message);
			adapter.refreshSelectLast();
			setResult(RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
					isloading = true;
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					conversation = EMChatManager.getInstance().getConversation(MainActivity.sendTo);
					EMMessage firstMsg = conversation.getAllMessages().get(0);
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						//
						messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);

					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						if (messages.size() > 0) {
							adapter.refreshSeekTo(messages.size() - 1);
						}
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.pinpin.utils.SmileUtils");
							Field field = clz.getField(filename);
							editContent.append(SmileUtils.getSmiledText(ChatActivity.this, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(editContent.getText())) {

								int selectionStart = editContent.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = editContent.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											editContent.getEditableText().delete(i, selectionStart);
										else
											editContent.getEditableText().delete(selectionStart - 1, selectionStart);
									} else {
										editContent.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	public ListView getListView() {
		return listviewChat;
	}

	@Override
	protected void onResume() {
		Log.i("ChatActivity", "onResume");
		super.onResume();

		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck });
	}

}
