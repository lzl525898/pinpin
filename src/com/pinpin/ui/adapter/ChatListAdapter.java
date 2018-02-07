package com.pinpin.ui.adapter;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.model.ChatListModel;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpTask.TaskResultListener2;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.ChatFragment;
import com.pinpin.ui.MainActivity;
import com.pinpin.utils.ImageUtils;
import com.pinpin.utils.Log;
import com.pinpin.utils.SmileUtils;
import com.pinpin.view.BadgeView;
import com.pinpin.view.CircleImageView;

public class ChatListAdapter extends BaseAdapter {
	public static final int MATCH_LIST = 0;
	public static final int CONVERSION_LIST = 1;
	List<ChatListModel> list;
	LayoutInflater inflater;
	ChatFragment f;
	int listType = MATCH_LIST;
	public static final int NEED_MATCH = 0;
	public static final int FRIENDS = 1;

	public ChatListAdapter(Context ctx, ChatFragment f,
			List<ChatListModel> list, int listType) {
		super();
		this.f = f;
		this.listType = listType;
		this.list = list;
		this.inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public ChatListModel getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		if (convertView == null) {

			switch (type) {
			case NEED_MATCH:
				convertView = inflater.inflate(
						R.layout.item_in_chat_list_match, null);
				break;
			case FRIENDS:
				convertView = inflater
						.inflate(R.layout.item_in_chat_list, null);
				break;
			}
		}

		ImageView avatar = (ImageView) convertView
				.findViewById(R.id.img_avatar);
		avatar.setTag(position);
		TextView name = (TextView) convertView.findViewById(R.id.txt_name);
		TextView content = (TextView) convertView
				.findViewById(R.id.txt_content);
		name.setText(list.get(position).getName());
		content.setText(list.get(position).getContent());
		if (list.get(position).getDrawable() == -1) {
			MainActivity act = (MainActivity) f.getActivity();
			Log.w("path", list.get(position).path);
			File file = new File(list.get(position).path);
			if (file.exists() && file.length() > 0) {
				// avatar.setImageBitmap(ImageUtils.getBitmapFromFile(new
				// File(list.get(position).path), 160, 160));
				boolean r = ((CircleImageView) avatar).createCircle(ImageUtils
						.getBitmapFromFile(new File(list.get(position).path),
								600, 600));
				if (!r) {
					avatar.setImageResource(R.drawable.default_avatar);
				}
			} else {
				avatar.setImageResource(R.drawable.default_avatar);
				act.fileDownload(new TaskResultListener2() {
					@Override
					public void result(String b, View v) {
						// TODO Auto-generated method stub
						ImageView im = (ImageView) v;
						int index = (Integer) v.getTag();
						Bitmap bmp = ImageUtils.getBitmapFromFile(
								new File(list.get(index).path), 300, 300);
						if (bmp == null) {
							im.setImageResource(R.drawable.default_avatar);
						} else {
							im.setImageBitmap(bmp);
							bmp = null;
						}

					}

					@Override
					public void failed(String message) {
						// TODO Auto-generated method stub

					}
				}, list.get(position), avatar);
			}

		} else {
			avatar.setImageDrawable(avatar.getResources().getDrawable(
					list.get(position).getDrawable()));
		}

		switch (type) {
		case NEED_MATCH:
			Button btn_like = (Button) convertView.findViewById(R.id.btn_like);
			btn_like.setTag(position);
			btn_like.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final int position = (Integer) v.getTag();
					final MainActivity act = (MainActivity) v.getContext();
					HashMap<String, String> data = new HashMap<String, String>() {
						{
							put("token", Constants.TOKEN);
							put("objectId", list.get(position).getUid());
							put("isLike", "true");
						}
					};

					RequestData request = HttpUtils.simplePostData(Address.HOST
							+ Address.LIKE, data);
					act.startHttpTask(new TaskResultListener() {

						@Override
						public void result(ResposneBundle b) {
							// TODO Auto-generated method stub
							Log.e("result", b.getContent());

							try {
								JSONObject job = new JSONObject(b.getContent());
								if (job.getInt("code") == -1) {
									act.showToast(job.getString("msg"));
								} else {
									f.refreshFriendList();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void failed(final String message) {
							// TODO Auto-generated method stub
							act.runOnUiThread(new Runnable() {
								public void run() {
									act.showToast(message);
								}
							});

						}
					}, request);
				}
			});
			Button btn_dislike = (Button) convertView
					.findViewById(R.id.btn_dislike);
			btn_dislike.setTag(position);
			btn_dislike.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final int position = (Integer) v.getTag();
					final MainActivity act = (MainActivity) v.getContext();
					HashMap<String, String> data = new HashMap<String, String>() {
						{
							put("token", Constants.TOKEN);
							put("objectId", list.get(position).getUid());
							put("isLike", "false");
						}
					};

					RequestData request = HttpUtils.simplePostData(Address.HOST
							+ Address.LIKE, data);
					act.startHttpTask(new TaskResultListener() {

						@Override
						public void result(ResposneBundle b) {
							// TODO Auto-generated method stub
							Log.e("result", b.getContent());

							try {
								JSONObject job = new JSONObject(b.getContent());
								if (job.getInt("code") == -1) {
									act.showToast(job.getString("msg"));
								} else {

									list.remove(position);
									notifyDataSetChanged();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void failed(final String message) {
							// TODO Auto-generated method stub
							act.runOnUiThread(new Runnable() {
								public void run() {
									act.showToast(message);
								}
							});

						}
					}, request);
				}
			});
			break;
		case FRIENDS:
			TextView txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			TextView time = (TextView) convertView.findViewById(R.id.txt_time);
//			if (listType == MATCH_LIST) {
//				Log.e("时间now", "" + new Date().getTime());
//				Log.e("获取时间","" + list.get(position).getTime());
//				long diff = new Date().getTime() - list.get(position).getTime();
//				long minute = diff / (1000 * 60);
//				long hours = minute / 60;
//				long days = hours / 24;
//				if (minute == 0) {
//					time.setText("现在活跃");
//				} else if (minute < 60) {
//					time.setText(minute + "分钟前活跃");
//				} else if (hours < 24) {
//					time.setText(hours + "小时前活跃");
//				} else if (hours < 720) {
//					time.setText(days + "天前活跃");
//				} else{
//					time.setText("n天前活跃");
//				}
//
//			} 
			 if (listType == CONVERSION_LIST) {
				EMConversation conversation = EMChatManager.getInstance()
						.getConversation(list.get(position).getHid());
				List<EMMessage> messages = conversation.getAllMessages();
				int unReadCount = conversation.getUnreadMsgCount();

				time.setText(DateUtils.getTimestampString(new Date(list.get(
						position).getTime())));
				txt_content.setText("");
				if (messages.size() > 0) {
					EMMessage message = messages.get(messages.size() - 1);

					switch (message.getType()) {
					case TXT: // 文本
						TextMessageBody txtBody = (TextMessageBody) message
								.getBody();
						Spannable span = SmileUtils.getSmiledText(
								txt_content.getContext(), txtBody.getMessage());
						txt_content.setText(span, BufferType.SPANNABLE);
						time.setText(DateUtils.getTimestampString(new Date(
								message.getMsgTime())));
						break;
					case VOICE:
						txt_content.setText("[语音]");
						break;

					}
					/*
					 * for(EMMessage e:messages){ Log.e(new
					 * Date(e.getMsgTime()).
					 * toString(),((TextMessageBody)e.getBody()).getMessage());
					 * }
					 */
				}
				/*设置聊天列表的未读消息*/
				Log.e("未读消息------!!!!!!!!!!!!!", unReadCount + "条");
				View badge_left = convertView.findViewById(R.id.badge_left);
				BadgeView badge = (BadgeView) badge_left.getTag();
				if (badge == null) {
					badge = new BadgeView(badge_left.getContext(), badge_left);
					badge_left.setTag(badge);
					badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
				}
				if (unReadCount > 0) {
					// badge.setBadgeMargin(0);
					if (unReadCount > 99) {
						badge.setText("99+");
					} else {
						badge.setText("" + unReadCount);
					}
					badge.show();
				} else {
					badge.hide();
				}
			}

			break;
		}
		return convertView;
	}

	public int getViewTypeCount() {
		return 2;
	}

	public int getItemViewType(int position) {
		ChatListModel model = getItem(position);
		if (model == null) {
			return -1;
		}

		return model.isNeedAgree() ? NEED_MATCH : FRIENDS;// invalid
	}

}
