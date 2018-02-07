package com.pinpin.ui;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.model.ChatListModel;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.adapter.ChatListAdapter;
import com.pinpin.utils.IOUtil;
import com.pinpin.utils.Log;
import com.pinpin.view.BadgeView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ChatFragment extends Fragment {

	public static boolean shouldRefresh = false;
	public int unReadCount = 0;
	static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd");
	public static String shouldDeleteWho;
	boolean isFriendsList = true;

	boolean shouldDoClick = false;
	ArrayList<ChatListModel> friendList;
	// ListView newsListView;
	List<ChatListModel> mNewsList;

	String column;
	Button btnChat;
	Button btnMatch;
	BadgeView badgeLeft;
	BadgeView badgeRight;
	View badge_match;
	View badge_match2;
	LinearLayout btn_wrapper;
	LinearLayout init_wrapper;
	Button btnFind;
	ListView listviewChat;
	ChatListAdapter chatListAdapterA = null;
	ChatListAdapter chatListAdapterB = null;
	MainActivity parentActivity;
	SharedPreferences pref;
	ArrayList<ChatListModel> recentlist;
	Gson gson;
	TextView hint;

	public void refreshRecentBadge() {
		int unReadCount = 0;
		if (badgeRight == null) {
			badgeRight = new BadgeView(getActivity(), badge_match2);
			badgeRight.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			badgeRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		}
		boolean hasUnRead = false;
		if (recentlist != null) {
			for (ChatListModel c : recentlist) {
				EMConversation conversation = EMChatManager.getInstance().getConversation(c.getHid());
				if (conversation != null) {

					unReadCount = conversation.getUnreadMsgCount();
					Log.e("未读消息", unReadCount + "条");
					if (unReadCount > 0) {
						hasUnRead = true;
						if (hasUnRead) {
							if (unReadCount < 99) {
								badgeRight.setText("" + unReadCount);
								badgeRight.show();
							} else {
								badgeRight.setText("99+");
								badgeRight.show();
							}
						}
						break;
					}
				}
			}

			BadgeView badgeChat = ((MainActivity) getActivity()).getBadge();
			if (hasUnRead) {
				if (unReadCount > 100) {
					badgeRight.setText("99+");
					badgeRight.show();
				} else {
					badgeRight.setText("" + unReadCount);
					badgeRight.show();
				}
				badgeChat.setText("" + unReadCount);
				badgeChat.show();
			} else {
				badgeRight.hide();
				badgeChat.hide();
			}

		}
		if (chatListAdapterB != null) {
			chatListAdapterB.notifyDataSetChanged();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parentActivity = (MainActivity) getActivity();
		pref = parentActivity.getSharedPreferences(Constants.RECENT_CHAT_HISTORY, Activity.MODE_PRIVATE);
		String json = pref.getString(Constants.USERNAME, null);
		Log.e("还原Json", json);
		gson = new Gson();
		recentlist = gson.fromJson(json, new TypeToken<List<ChatListModel>>() {
		}.getType());
		if (recentlist == null) {
			recentlist = new ArrayList<ChatListModel>();
		}

		// for(Iterator iter = recentlist.iterator(); iter.hasNext();){
		// ChatListModel model = (ChatListModel)iter.next();
		// }
		/*
		 * for (Iterator iter = recentlist.iterator(); iter.hasNext();) {
		 * ChatListModel model = (ChatListModel)iter.next(); EMConversation
		 * conversation =
		 * EMChatManager.getInstance().getConversation(model.getHid());
		 * List<EMMessage> messages = conversation.getAllMessages();
		 * if(messages.size()==0){ iter.remove(); }else{ EMMessage message =
		 * messages.get(0); model.setTime(message.getMsgTime()); switch
		 * (message.getType()) { case TXT: // 文本 TextMessageBody txtBody =
		 * (TextMessageBody) message.getBody();
		 * model.setContent(txtBody.getMessage()); break; } } }
		 */

		LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.chat_fragment, null);
		rootView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				refreshRecentBadge();
			}
		}, 2);
		btnFind = (Button) rootView.findViewById(R.id.btn_find);
		init_wrapper = (LinearLayout) rootView.findViewById(R.id.init_wrapper);
		btn_wrapper = (LinearLayout) rootView.findViewById(R.id.btn_wrapper);
		btnChat = (Button) rootView.findViewById(R.id.btn_chat);
		btnMatch = (Button) rootView.findViewById(R.id.btn_match);
		badge_match = rootView.findViewById(R.id.badge_match);
		badge_match2 = rootView.findViewById(R.id.badge_match2);
		hint = (TextView) rootView.findViewById(R.id.text_hint);
		btnMatch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isFriendsList = true;
				btn_wrapper.setBackgroundResource(R.drawable.daohanglan);
				listviewChat.setAdapter(chatListAdapterA);
			}
		});
		btnChat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isFriendsList = false;
				// TODO Auto-generated method stub
				if (chatListAdapterB == null) {

					// ArrayList<ChatListModel> list = new
					// ArrayList<ChatListModel>();
					// list.add(new
					// ChatListModel(R.drawable.a_liudi,"刘迪","你好吗？","8:30"));
//					chatListAdapterB = new ChatListAdapter(getActivity(), ChatFragment.this, recentlist,
//							ChatListAdapter.CONVERSION_LIST);
				} else {
					chatListAdapterB.notifyDataSetChanged();
				}
				btn_wrapper.setBackgroundResource(R.drawable.liaotian2);
				listviewChat.setAdapter(chatListAdapterB);
			}
		});
		listviewChat = (ListView) rootView.findViewById(R.id.listview_chat);
		friendList = new ArrayList<ChatListModel>();
		chatListAdapterB = new ChatListAdapter(getActivity(), ChatFragment.this, recentlist,
				ChatListAdapter.CONVERSION_LIST);
		chatListAdapterA = new ChatListAdapter(getActivity(), ChatFragment.this, friendList,
				ChatListAdapter.MATCH_LIST);
		listviewChat.setAdapter(chatListAdapterA);
		listviewChat.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (isFriendsList) {
					String hid = chatListAdapterA.getItem(position).getHid();
					String name = chatListAdapterA.getItem(position).getName();
					if (chatListAdapterA.getItemViewType(position) == ChatListAdapter.NEED_MATCH) {
						Intent data = new Intent(getActivity(), OtherActivity.class);
						String jsonString = "";
						try {
							jsonString = getJsonStringByPosition(chatListAdapterA.getItem(position).json, hid,
									"likeme");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						data.putExtra("fromChat", true);
						data.putExtra("info", jsonString);
						ChatListModel model = chatListAdapterA.getItem(position);

						if (model.path != null) {
							data.putExtra("path", chatListAdapterA.getItem(position).path);

						}

						data.putExtra("url", "");

						// recentlist.remove(model);
						// /* 设置了当前listition time */
						// model.setTime(System.currentTimeMillis());
						// recentlist.add(0, model);
						// String json = gson.toJson(recentlist, new
						// TypeToken<List<ChatListModel>>() {
						// }.getType());
						// Log.e("持久化JSON", json);
						// pref.edit().putString(Constants.USERNAME,
						// json).commit();

						parentActivity.startActivityForResult(data, PinpinFragment.START_OTHER_ACTIVITY);
						parentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					} else {
						if (!TextUtils.isEmpty(hid)) {
							MainActivity.sendTo = hid;
							Intent data = new Intent();
							data.putExtra("username", name);
							ChatListModel model = chatListAdapterA.getItem(position);
							String jsonString = "";
							try {
								jsonString = getJsonStringByPosition(chatListAdapterA.getItem(position).json, hid,
										"friends");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							data.putExtra("username", name);

							data.putExtra("json", jsonString);
							if (model.path != null) {
								data.putExtra("path", model.path);
							}
							/* 消息列表 好友所有信息送到好友列表（头像等） */
							recentlist.remove(model);
							model.setTime(System.currentTimeMillis());
							recentlist.add(0, model);
							String json = gson.toJson(recentlist, new TypeToken<List<ChatListModel>>() {
							}.getType());
							Log.e("持久化JSON", json);
							pref.edit().putString(Constants.USERNAME, json).commit();
							data.setClass(getActivity(), ChatActivity.class);
							startActivity(data);
						} else {
							parentActivity.showToast("未注册聊天账号");
						}
					}
				} else {
					ChatListModel model = chatListAdapterB.getItem(position);
					String hid = model.getHid();
					String name = model.getName();
					if (!TextUtils.isEmpty(hid)) {
						MainActivity.sendTo = hid;
						Intent data = new Intent();
						String jsonString = "";
						try {
							jsonString = getJsonStringByPosition(chatListAdapterB.getItem(position).json, hid,
									"friends");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						data.putExtra("username", name);

						data.putExtra("json", jsonString);
						if (model.path != null) {
							data.putExtra("path", model.path);
						}
						model.setTime(System.currentTimeMillis());
						recentlist.remove(model);
						recentlist.add(0, model);
						String json = gson.toJson(recentlist, new TypeToken<List<ChatListModel>>() {
						}.getType());
						Log.e("持久化JSON", json);
						pref.edit().putString(Constants.USERNAME, json).commit();
						listviewChat.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								chatListAdapterB.notifyDataSetChanged();
								refreshRecentBadge();
							}
						}, 500);

						data.setClass(getActivity(), ChatActivity.class);
						startActivity(data);
					} else {

						parentActivity.showToast("未注册聊天账号");
					}
				}

			}

			private Button findViewById(int btnLike) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		refreshFriendList();

		return rootView;
	}

	protected String getJsonStringByPosition(String json, String number, String flags) throws JSONException {
		// TODO Auto-generated method stub
		JSONObject all = new JSONObject(json);
		JSONObject data = all.getJSONObject("data");
		JSONArray friendsArray = null;
		if (flags.equals("likeme")) {
			friendsArray = data.getJSONArray("likeme");
		} else if (flags.equals("friends")) {
			friendsArray = data.getJSONArray("friends");
		}
		for (int i = 0; i < friendsArray.length(); i++) {
			JSONObject friends = friendsArray.getJSONObject(i);
			if (friends.getString("easeMobId").equals(number)) {
				return friendsArray.getJSONObject(i).toString();
			}
		}
		return null;
	}

	public void refreshFriendList() {
		if (parentActivity == null) {
			parentActivity = (MainActivity) getActivity();
		}
		if (parentActivity == null) {
			return;
		}
		HashMap<String, String> data = new HashMap<String, String>() {
			{
				put("token", Constants.TOKEN);

			}
		};

		RequestData request = HttpUtils.simplePostData(Address.HOST + Address.GET_ALL_RELATION, data);
		parentActivity.startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result", b.getContent());
				friendList.clear();
				recentlist.clear();
				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") == -1) {
						parentActivity.showToast(job.getString("msg"));
					} else {
						init_wrapper.setVisibility(View.GONE);
						listviewChat.setVisibility(View.VISIBLE);
						JSONObject job2 = job.getJSONObject("data");
						JSONArray likemeArr = job2.getJSONArray("likeme");
						JSONArray friendsArr = job2.getJSONArray("friends");
						Log.e("token", Constants.TOKEN);

						if (likemeArr != null) {

							int count = likemeArr.length();
							int total = unReadCount + count;
							BadgeView badgeChat = ((MainActivity) getActivity()).getBadge();
							if (badgeLeft == null) {

								badgeLeft = new BadgeView(getActivity(), badge_match);
								badgeLeft.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
								badgeLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
							}
							if (count > 99) {
								badgeLeft.setText("99+");
								badgeLeft.show();
								badgeChat.setText("" + total);
								badgeChat.show();

							} else if (count == 0) {
								badgeLeft.hide();
								badgeChat.hide();
							} else {
								badgeLeft.setText("" + count);
								badgeLeft.show();
								badgeChat.setText("" + total);
								badgeChat.show();

							}

							// Date tempDate = SDF2.parse("2049-12-31");

							Date tempDate = new Date();
							for (int i = 0; i < likemeArr.length(); i++) {
								JSONArray pictures = likemeArr.getJSONObject(i).getJSONArray("pictures");
								if (pictures.length() > 0) {
									ChatListModel model = new ChatListModel(-1,
											likemeArr.getJSONObject(i).getString("username"), "对你感兴趣", 0,
											likemeArr.getJSONObject(i).getString("id"),
											likemeArr.getJSONObject(i).getString("easeMobId"), true);
									model.json = b.getContent();
									String relative = pictures.getJSONObject(0).getString("filePath");
									model.avatar_url = Address.HOST_PICTURE + relative;
									// String fileName
									// =model.avatar_url.substring(model.avatar_url.lastIndexOf("/")+1);
									File path = getActivity()
											.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
									// path = new File(
									// path,System.currentTimeMillis()+(int)Math.random()*1000+"");
									/*
									 * path = new File( path,fileName);
									 * if(!path.exists()){ try {
									 * path.createNewFile(); } catch
									 * (IOException e) { // TODO Auto-generated
									 * catch block e.printStackTrace(); } }
									 */

									String ext = model.avatar_url.substring(model.avatar_url.lastIndexOf(".") + 1);
									path = new File(path, IOUtil.toMd5(model.avatar_url.getBytes()) + "." + ext);
									model.path = path.getAbsolutePath();
									model.setTime(tempDate.getTime());
									friendList.add(model);

								} else {
									ChatListModel model = new ChatListModel(R.drawable.default_avatar,
											likemeArr.getJSONObject(i).getString("username"), "对你感兴趣", 0,
											likemeArr.getJSONObject(i).getString("id"),
											likemeArr.getJSONObject(i).getString("easeMobId"), true);
									model.setTime(tempDate.getTime());
									model.json = b.getContent();
									friendList.add(model);
								}
							}
						}
						if (friendsArr != null) {

							for (int i = 0; i < friendsArr.length(); i++) {
								JSONArray pictures2 = friendsArr.getJSONObject(i).getJSONArray("pictures");
								String lastLoginTime = friendsArr.getJSONObject(i).getString("lastLoginTime");
								String lastAccessTime = friendsArr.getJSONObject(i).getString("lastAccessTime");

								Date date;
								Date date2;
								try {
									long time = Long.valueOf(lastLoginTime);
									date = new Date(time);

								} catch (Exception e) {
									e.printStackTrace();
									try {
										date = SDF.parse(lastLoginTime);
									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
										date = new Date();
									}
								}
								try {
									date2 = SDF2.parse(lastAccessTime);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									date2 = new Date();
								}

								if (pictures2.length() > 0) {
									String matchDate = SDF2.format(date2);
									ChatListModel model = new ChatListModel(-1,
											friendsArr.getJSONObject(i).getString("username"), "配对于" + matchDate,
											date.getTime(), friendsArr.getJSONObject(i).getString("id"),
											friendsArr.getJSONObject(i).getString("easeMobId"), false);

									model.json = b.getContent();
									String relative = pictures2.getJSONObject(0).getString("filePath");
									model.avatar_url = Address.HOST_PICTURE + relative;
									File path = getActivity()
											.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
									// path = new File(
									// path,System.currentTimeMillis()+(int)Math.random()*1000+"");
									String ext = model.avatar_url.substring(model.avatar_url.lastIndexOf(".") + 1);
									path = new File(path, IOUtil.toMd5(model.avatar_url.getBytes()) + "." + ext);
									/*
									 * if(!path.exists()){ try {
									 * path.createNewFile(); } catch
									 * (IOException e) { // TODO Auto-generated
									 * catch block e.printStackTrace(); } }
									 */
									model.path = path.getAbsolutePath();
									model.setTime(date.getTime());
									friendList.add(model);
									recentlist.add(model);
								} else {
									String matchDate = SDF2.format(date2);
									ChatListModel model = new ChatListModel(R.drawable.default_avatar,
											friendsArr.getJSONObject(i).getString("username"), "配对于" + matchDate,
											date.getTime(), friendsArr.getJSONObject(i).getString("id"),
											friendsArr.getJSONObject(i).getString("easeMobId"), false);
									model.json = b.getContent();
									model.setTime(date.getTime());
									friendList.add(model);
									recentlist.add(model);
								}
							}

							Comparator<ChatListModel> comparator = new Comparator<ChatListModel>() {
								public int compare(ChatListModel s1, ChatListModel s2) {
									// 先排年龄

									return (int) (s2.getTime() - s1.getTime());

								}
							};
							Collections.sort(friendList, comparator);
						}
						chatListAdapterA.notifyDataSetChanged();
						chatListAdapterB.notifyDataSetChanged();
						if (shouldDoClick) {
							doClick();
							shouldDoClick = false;
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (chatListAdapterA.getCount() == 0) {
					listviewChat.setVisibility(View.GONE);
					init_wrapper.setVisibility(View.VISIBLE);
					btnFind.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							BaseActivity act = (BaseActivity) getActivity();
							if (act != null) {
								act.pinpin_btn_area.performClick();
							}
						}
					});
				}
			}

			@Override
			public void failed(final String message) {
				// TODO Auto-generated method stub
				// listviewChat.setVisibility(View.GONE);

				parentActivity.runOnUiThread(new Runnable() {
					public void run() {
						parentActivity.showToast(message);
						listviewChat.setVisibility(View.GONE);
						init_wrapper.setVisibility(View.VISIBLE);
						btnFind.setVisibility(View.GONE);
						hint.setText(message);
					}
				});

			}
		}, request);
	}

	@Override
	public void onResume() {
		Log.e("Chat!!!!!!!", "onResume");
		refreshRecentBadge();
//		LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.chat_fragment, null);
//		rootView.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				refreshRecentBadge();
//			}
//		}, 2);
		if (chatListAdapterB != null) {
			chatListAdapterB.notifyDataSetChanged();
		}
		// TODO Auto-generated method stub
		// MainActivity parentActivity = (MainActivity ) getActivity();
		// parentActivity.noHeader();
		if (shouldRefresh) {
			refreshFriendList();
			if (recentlist != null && chatListAdapterB != null) {
				ChatListModel temp = null;
				for (ChatListModel c : recentlist) {
					if (TextUtils.equals(c.getHid(), shouldDeleteWho)) {
						temp = c;
						break;
					}
				}
				if (temp != null) {
					recentlist.remove(temp);
					chatListAdapterB.notifyDataSetChanged();
				}
			}

			shouldRefresh = false;
		}

		super.onResume();
	}

	public void doClick() {
		int position = -1;
		int index = 0;
		for (ChatListModel c : friendList) {
			if (TextUtils.equals(c.getHid(), MainActivity.sendTo)) {
				position = index;
				break;
			}
			index++;
		}
		if (position > -1) {
			listviewChat.performItemClick(null, position, position);
		}

	}

	public void shouldDoClick() {
		// TODO Auto-generated method stub
		shouldDoClick = true;
	}

	/*
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { // TODO Auto-generated method stub
	 * super.onActivityResult(requestCode, resultCode, data);
	 * Log.e("ChatFragment!!!", "onActivityResult"); }
	 */

}
