package com.pinpin.ui;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DemoHelper;
import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;
import com.pinpin.R;
import com.pinpin.app.PinApplication;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.view.BadgeView;

public class MainActivity extends BaseActivity implements EMEventListener {
	ViewPager mPager;
	PinpinFragment f0;
	PinPinPagerFragmentAdapter adapter;
	PagerTabStrip pagerTabStrip;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	public static String sendTo = "test2";
	// 账号在别处登录
	public boolean isConflict = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("text--------------", "进入main");

		// NewMessageBroadcastReceiver msgReceiver = new
		// NewMessageBroadcastReceiver();
		// IntentFilter intentFilter = new
		// IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// intentFilter.setPriority(3);
		// registerReceiver(msgReceiver, intentFilter);
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub try
		 * { // 调用sdk注册方法
		 * EMChatManager.getInstance().createAccountOnServer("test3", "111111");
		 * } catch (final EaseMobException e) { //注册失败 int
		 * errorCode=e.getErrorCode(); if(errorCode==EMError.NONETWORK_ERROR){
		 * Log.e("reg","网络异常，请检查网络！"); //Toast.makeText(getApplicationContext(),
		 * "网络异常，请检查网络！", Toast.LENGTH_SHORT).show(); }else
		 * if(errorCode==EMError.USER_ALREADY_EXISTS){
		 * //Toast.makeText(getApplicationContext(), "用户已存在！",
		 * Toast.LENGTH_SHORT).show(); Log.e("reg","用户已存在"); }else
		 * if(errorCode==EMError.UNAUTHORIZED){ Log.e("reg","注册失败，无权限！");
		 * //Toast.makeText(getApplicationContext(), "注册失败，无权限！",
		 * Toast.LENGTH_SHORT).show(); }else{ Log.e("reg","注册失败: " +
		 * e.getMessage()); //Toast.makeText(getApplicationContext(), "注册失败: " +
		 * e.getMessage(), Toast.LENGTH_SHORT).show(); } Log.e("reg","用户已注册成功");
		 * } } }).start();
		 */
		if (getIntent().getBooleanExtra(Constants.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constants.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			finish();
			startActivity(new Intent(MainActivity.this, LoginActivity.class));
			// showAccountRemovedDialog();
		} else {

		}
		if (Constants.USERNAME != null) {
			EMChatManager.getInstance().login(Constants.USERNAME, "pinA2015", new EMCallBack() {// 回调
				@Override
				public void onSuccess() {
					runOnUiThread(new Runnable() {
						public void run() {
							EMGroupManager.getInstance().loadAllGroups();
							EMChatManager.getInstance().loadAllConversations();
							Log.d("main", "登陆聊天服务器成功！");
						}
					});
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(int code, String message) {
					runOnUiThread(new Runnable() {
						public void run() {
							// Toast.makeText(getApplicationContext(),
							// "会话创建失败", Toast.LENGTH_LONG).show();
							Toast.makeText(getApplicationContext(), "账号删除成功", Toast.LENGTH_LONG).show();
						}
					});
					Log.e("main", "code:" + code + " msg:" + message);

				}
			});
		}
	}

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setBackBtnGone();
		// setSearchBtnGone();
		// noHeader();
		inflater.inflate(R.layout.activity_main, container);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(2);
		adapter = new PinPinPagerFragmentAdapter(getSupportFragmentManager());
		mPager.setAdapter(adapter);
		// adapter.notifyDataSetChanged()
		mPager.setPageMargin(16);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Log.e("onPageSelected", "" + position);

				changeState(position);
				if (position == 1) {
					noHeader();
				} else {
					showHeader();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.e("onPageScrolled", "" + arg0);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.e("onPageScrollStateChanged", "" + arg0);
			}
		});
		pinpin_btn.setBackgroundResource(R.drawable.btn_jihui_b);
		pinpin_btn_area.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeState(0);
				mPager.setCurrentItem(0);
			}
		});
		badgeChatBtn = new BadgeView(this, badge_container);
		badgeChatBtn.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		badgeChatBtn.setBadgeTBPadding(4);

		badgeChatBtn.setText("...");
		badgeChatBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

		chat_btn_area.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeState(1);
				mPager.setCurrentItem(1);

			}
		});
		me_btn_area.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeState(2);
				mPager.setCurrentItem(2);
			}
		});
		search_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// showAlertDialog("title", "message");
				Intent in = new Intent(MainActivity.this, SettingActivity.class);
				startActivity(in);
			}
		});

		// pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
		// pagerTabStrip.setTabIndicatorColorResource(R.color.tab_indicator_color);
		// pagerTabStrip.setTextColor(getResources().getColor(R.color.tab_text_color));
		EMChat.getInstance().setAppInited();
		mPager.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				shouldHandleNotified();
			}
		}, 300);
		/*
		 * EMChatManager.getInstance().registerEventListener(new
		 * EMEventListener() {
		 * 
		 * @Override public void onEvent(EMNotifierEvent event) {
		 * 
		 * switch (event.getEvent()) { case EventNewMessage: // 接收新消息 {
		 * EMMessage m = (EMMessage) event.getData(); if
		 * (!EasyUtils.isAppRunningForeground(getApplicationContext())) {
		 * EMLog.d("chat", "app is running in backgroud");
		 * PinApplication.getInstance().getNotifier() .viberateAndPlayTone(m);
		 * PinApplication.getInstance().getNotifier().sendNotification(m,
		 * false); } else {
		 * PinApplication.getInstance().getNotifier().sendNotification(m, true);
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * if( m.getBody() instanceof TextMessageBody){ TextMessageBody txtBody
		 * = (TextMessageBody) m.getBody();
		 * Log.e("新消息内容","---"+txtBody.getMessage()); } break; } case
		 * EventDeliveryAck:{//接收已发送回执
		 * 
		 * break; }
		 * 
		 * case EventNewCMDMessage:{//接收透传消息
		 * 
		 * break; }
		 * 
		 * case EventReadAck:{//接收已读回执
		 * 
		 * break; }
		 * 
		 * case EventOfflineMessage: {//接收离线消息 List<EMMessage> messages =
		 * (List<EMMessage>) event.getData();
		 * 
		 * Log.e("收到离线消息","---"+messages.size()); for(EMMessage m:messages){ if(
		 * m.getBody() instanceof TextMessageBody){ TextMessageBody txtBody =
		 * (TextMessageBody) m.getBody();
		 * Log.e("离线消息内容","---"+txtBody.getMessage()); }
		 * 
		 * 
		 * } break; }
		 * 
		 * case EventConversationListChanged:
		 * {//通知会话列表通知event注册（在某些特殊情况，SDK去删除会话的时候会收到回调监听）
		 * 
		 * break; }
		 * 
		 * default: break; } }
		 * 
		 * });
		 */

		/*
		 * EMChatManager.getInstance().registerEventListener(new
		 * EMEventListener() {
		 * 
		 * @Override public void onEvent(EMNotifierEvent event) { // TODO
		 * Auto-generated method stub switch (event.getEvent()) { case
		 * EventNewMessage: // 普通消息 { EMMessage message = (EMMessage)
		 * event.getData();
		 * 
		 * // 提示新消息
		 * PinApplication.getInstance().getNotifier().onNewMsg(message);
		 * 
		 * refreshUI(); break; }
		 * 
		 * case EventOfflineMessage: { refreshUI(); break; }
		 * 
		 * case EventConversationListChanged: { refreshUI(); break; }
		 * 
		 * default: break; } }});
		 */
	}

	private void refreshUI() {

		if (adapter.chatFragment != null) {
			((ChatFragment) adapter.chatFragment).refreshRecentBadge();
		}
	}

	private void changeState(int pos) {
		switch (pos) {
		case 0:
			setTitle("优聘");
			setSearchBtnVisible();
			// setSearchBtnGone();
			pinpin_btn.setBackgroundResource(R.drawable.btn_jihui_b);
			me_btn.setBackgroundResource(R.drawable.btn_me_a);
			chat_btn.setBackgroundResource(R.drawable.btn_xiaoxi_a);
			break;
		case 1:
			// setTitle("聊天");
			setSearchBtnGone();
			chat_btn.setBackgroundResource(R.drawable.btn_xiaoxi_b);
			me_btn.setBackgroundResource(R.drawable.btn_me_a);
			pinpin_btn.setBackgroundResource(R.drawable.btn_jihui_a);
			Fragment f = adapter.getItem(pos);
			Log.e("Fragment", f.getClass().getName());
			if (f instanceof ChatFragment) {
				((ChatFragment) f).refreshFriendList();
			}
			break;
		case 2:
			setTitle("我");
			setSearchBtnGone();
			me_btn.setBackgroundResource(R.drawable.btn_me_b);
			chat_btn.setBackgroundResource(R.drawable.btn_xiaoxi_a);
			pinpin_btn.setBackgroundResource(R.drawable.btn_jihui_a);
			break;
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

	}

	public class PinPinPagerFragmentAdapter extends FragmentStatePagerAdapter {
		// List<Fragment> fragmentList ;
		public Fragment pinpinFragment;
		public Fragment chatFragment;
		public Fragment meFragment;

		public PinPinPagerFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// return fragmentList.get(position);
			Fragment frag = null;
			switch (position) {
			case 0:
				if (pinpinFragment == null) {
					pinpinFragment = new PinpinFragment();
					Log.e("PP", "getItem");
				}
				return pinpinFragment;
			case 1:
				if (chatFragment == null) {
					chatFragment = new ChatFragment();
					Log.e("CF", "getItem");
				}
				return chatFragment;
			case 2:
				if (meFragment == null) {
					meFragment = new MeFragment();
					Log.e("ME", "getItem");
				}

				return meFragment;
			}

			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}

		@Override
		public int getCount() {
			// return fragmentList.size()
			return 3;
		}

		@Override
		public int getItemPosition(Object object) {
			return FragmentStatePagerAdapter.POSITION_NONE;
		}

	}

	/*
	 * public class PinPinFragmentAdapter extends FragmentPagerAdapter {
	 * 
	 * public PinPinFragmentAdapter(FragmentManager fm) { super(fm); }
	 * 
	 * @Override public Fragment getItem(int position) { Fragment frag = null;
	 * switch(position){ case 0 : frag = new PinpinFragment("");
	 * 
	 * break; case 1 : frag = new ChatFragment(""); break; case 2 : frag = new
	 * MeFragment(""); break; }
	 * 
	 * return frag; }
	 * 
	 * @Override public CharSequence getPageTitle(int position) { return "" ; }
	 * 
	 * @Override public int getCount() { return 3; }
	 * 
	 * @Override public int getItemPosition(Object object) { return
	 * FragmentStatePagerAdapter.POSITION_NONE; }
	 * 
	 * @Override public Object instantiateItem(ViewGroup container, int
	 * position) { Fragment f = (Fragment) super.instantiateItem(container,
	 * position);
	 * 
	 * // String title = mList.get(position); // f.setTitle(title); return f;
	 * 
	 * // Do we already have this fragment?
	 * 
	 * String name = makeFragmentName(container.getId(), position); Fragment
	 * fragment = mFragmentManager.findFragmentByTag(name); if (fragment !=
	 * null) { if (DEBUG) Log.v(TAG, "Attaching item #" + position + ": f=" +
	 * fragment); mCurTransaction.attach(fragment); } else { fragment =
	 * getItem(position); if (DEBUG) Log.v(TAG, "Adding item #" + position +
	 * ": f=" + fragment); mCurTransaction.add(container.getId(), fragment,
	 * makeFragmentName(container.getId(), position)); }
	 * 
	 * }
	 * 
	 * }
	 */

	public PinPinPagerFragmentAdapter getAdapter() {
		return adapter;
	}

	/*
	 * private class NewMessageBroadcastReceiver extends BroadcastReceiver {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { // 注销广播
	 * abortBroadcast();
	 * 
	 * // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成） String msgId =
	 * intent.getStringExtra("msgid"); //发送方 String username =
	 * intent.getStringExtra("from"); //
	 * 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象 EMMessage message =
	 * EMChatManager.getInstance().getMessage(msgId); EMMessage.Type type =
	 * message.getType(); switch (type) { case TXT: // 获取消息内容，发送消息 String
	 * content = ((TextMessageBody) message.getBody()).getMessage();
	 * Log.e("消息",content); Toast.makeText(getApplicationContext(), content,
	 * Toast.LENGTH_LONG).show(); break; case IMAGE: // 发送图片 String filePath =
	 * ((ImageMessageBody) message.getBody()).getLocalUrl(); if (filePath !=
	 * null) { File file = new File(filePath); if (!file.exists()) { //
	 * 不存在大图发送缩略图 //filePath = ImageUtils.getThumbnailImagePath(filePath); }
	 * 
	 * } break; default: break; } EMConversation conversation =
	 * EMChatManager.getInstance().getConversation(username);
	 * Log.e("消息",message.getFrom()); Log.e("消息",message.getTo());
	 * Log.e("消息",message.toString());
	 * 
	 * sendText("我是你的好友，收到消息了吧!","test1",conversation);
	 * 
	 * // 如果是群聊消息，获取到group id if (message.getChatType() == ChatType.GroupChat) {
	 * username = message.getTo(); } if (!username.equals(username)) { //
	 * 消息不是发给当前会话，return return; } } } private void sendText(String
	 * content,String to,EMConversation conversation) {
	 * 
	 * if (content.length() > 0) { EMMessage message =
	 * EMMessage.createSendMessage(EMMessage.Type.TXT); //
	 * 如果是群聊，设置chattype,默认是单聊
	 * 
	 * TextMessageBody txtBody = new TextMessageBody(content); // 设置消息body
	 * message.addBody(txtBody); // 设置要发给谁,用户username或者群聊groupid
	 * message.setReceipt(to); // 把messgage加到conversation中
	 * conversation.addMessage(message);
	 * EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
	 * 
	 * @Override public void onError(int arg0, String arg1) { // TODO
	 * Auto-generated method stub Log.e("消息发送","失败"); }
	 * 
	 * @Override public void onProgress(int arg0, String arg1) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onSuccess() { // TODO Auto-generated method stub
	 * Log.e("消息发送","成功"); }}); //
	 * 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
	 * //adapter.refreshSelectLast(); //mEditTextContent.setText("");
	 * 
	 * //setResult(RESULT_OK);
	 * 
	 * } }
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.e("pinpin收到了", requestCode + "--" + resultCode + "--" + data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PinpinFragment.START_OTHER_ACTIVITY) {
				List<Fragment> frags = getSupportFragmentManager().getFragments();
				f0 = null;
				for (Fragment f : frags) {
					if (f instanceof PinpinFragment) {
						Log.e("Fragment收到了", f + "--" + f.getId());
						f0 = (PinpinFragment) f;
						break;
					}
				}
				if (data.getBooleanExtra("islike", false)) {
					mPager.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							f0.like();
						}
					}, 1000);

				} else {
					mPager.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							f0.dislike();
						}
					}, 1000);

				}
			} else if (requestCode == MeFragment.RELOGIN) {
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				finish();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(Constants.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (intent.getBooleanExtra(Constants.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHelper.getInstance().logout(true, null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e("---------color userRemovedBuilder error" + e.getMessage(), null);
			}

		}

	}

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	private AlertDialog conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private BroadcastReceiver internalDebugReceiver;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
//		HashMap<String, String> data = new HashMap<String, String>() {
//			{
//				String phoneCode = getUsername();
//				put("phoneCode", phoneCode);
//				put("token", Constants.TOKEN);
//
//			}
//		};
//		RequestData request = HttpUtils.simplePostData(Address.HOST + Address.LOGOUT, data);
//		startHttpTask(new TaskResultListener() {
//			
//
//			@Override
//			public void result(ResposneBundle b) {
//				// TODO Auto-generated method stub
//				Log.e("Hello", b.getContent());
//				if (b.getContent() == null) {
//					showToast("出错了，服务器异常");
//					return;
//				}
//				try {
//					JSONObject job = new JSONObject(b.getContent());
//					if (job.getInt("code") == -1) {
//					} else {
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//
//			@Override
//			public void failed(final String message) {
//				// TODO Auto-generated method stub
//				runOnUiThread(new Runnable() {
//					public void run() {
//						showToast("asa"+message);
//					}
//				});
//			}
//		}, request);
		EMChatManager.getInstance().logout(true, new EMCallBack() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
		// DemoHelper.getInstance().logout(false,null);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new AlertDialog.Builder(this).create();
				conflictBuilder.show();
				Window window = conflictBuilder.getWindow();
				window.setContentView(R.layout.logoutdialog);
				conflictBuilder.setCanceledOnTouchOutside(false);
				Button bt_ok = (Button) window.findViewById(R.id.button_dialog_1);
				bt_ok.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						((DialogInterface) conflictBuilder).dismiss();
						conflictBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					}
				});
				// conflictBuilder.setMessage(R.string.connect_conflict);
				// conflictBuilder.setPositiveButton(R.string.ok, new
				// DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				// conflictBuilder = null;
				// finish();
				// startActivity(new Intent(MainActivity.this,
				// LoginActivity.class));
				// }
				// });
				conflictBuilder.setCancelable(false);
				;
				isConflict = true;
			} catch (Exception e) {
				EMLog.e("---------color conflictBuilder error" + e.getMessage(), null);
			}

		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public BadgeView getBadge() {
		return badgeChatBtn;
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });

	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			PinApplication.getInstance().getNotifier().onNewMsg(message);
			mPager.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					refreshUI();
				}
			});

			break;
		}

		case EventOfflineMessage: {
			refreshUI();
			break;
		}

		case EventConversationListChanged: {
			refreshUI();
			break;
		}

		default:
			break;
		}
	}

	boolean shouldHandleNotified() {
		Intent notifiedIntent = getIntent();
		String type = null;
		String who = null;
		if (notifiedIntent != null) {
			type = notifiedIntent.getStringExtra(PinApplication.NOTIFACATION);
			who = notifiedIntent.getStringExtra("offlineId");
		}

		if (!TextUtils.isEmpty(who) || !TextUtils.isEmpty(type)) {
			sendTo = who;
			if (adapter.chatFragment != null) {

				changeState(1);
				mPager.setCurrentItem(1);

				((ChatFragment) adapter.chatFragment).shouldDoClick();

			}

			return true;
		}
		return false;
	}
}
