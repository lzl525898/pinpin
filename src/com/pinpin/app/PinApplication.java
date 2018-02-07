/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinpin.app;

 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.android.volley.DemoHelper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pinpin.R;
import com.pinpin.constants.Constants;
import com.pinpin.model.ChatListModel;
import com.pinpin.ui.SplashActivity;
import com.pinpin.utils.HXNotifier;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;
 

public class PinApplication extends Application {
    protected HXNotifier notifier = null;
    private static int fromUsersNumber = 0;
    private static int messageNumber = 0;
    private String nickname = "";
	public static Context applicationContext;
	private static PinApplication instance;
	public final static String NOTIFACATION ="notify";
	EMEventListener eventListener;
	// login user name
	public final String PREF_USERNAME = "username";
	 /** 
     * Log or request TAG 
     */  
    public static final String TAG = "VolleyPatterns";  
  
    /** 
     * Global request queue for Volley 
     */  
    private RequestQueue mRequestQueue; 
    
    protected ImageLoader imageLoader ;
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
  private List<Activity> activityList = new ArrayList<Activity>();
	    
	    public void pushActivity(Activity activity){
	        if(!activityList.contains(activity)){
	            activityList.add(0,activity); 
	        }
	    }
	    
	    public boolean popActivity(Activity activity){
	        return activityList.remove(activity);
	    }
	@Override
	public void onCreate() {
		super.onCreate();
        applicationContext = this;
        instance = this;
        Log.e("error-2---", "全局application");
        DemoHelper.getInstance().init(applicationContext);
        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         * 
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         * for example:
         * 例子：
         * 
         * public class DemoHXSDKHelper extends HXSDKHelper
         * 
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
     //   hxSDKHelper.onInit(applicationContext);
        EMChat.getInstance().init(applicationContext);
       
           // EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
            // set debug mode in development process
            EMChat.getInstance().setDebugMode(true);  
            notifier = new HXNotifier();
            notifier.init(applicationContext);
            
            notifier.setNotificationInfoProvider( new HXNotifier.HXNotificationInfoProvider(){

				@Override
				public String getDisplayedText(EMMessage message) {
					// TODO Auto-generated method stub
					
					SharedPreferences pref = getSharedPreferences(
							Constants.RECENT_CHAT_HISTORY, Activity.MODE_PRIVATE);
					String json = pref.getString(Constants.USERNAME, null);
					Gson gson = new Gson();
					ArrayList<ChatListModel> recentlist = gson.fromJson(json, new TypeToken<List<ChatListModel>>() {
					}.getType());
					if (recentlist == null) {
						recentlist = new ArrayList<ChatListModel>();
					}
					
					for(Iterator iter = recentlist.iterator(); iter.hasNext();){
						ChatListModel model = (ChatListModel)iter.next();
						if(model.getHid().equals(message.getFrom())){
							nickname = model.getName();
							break;
						}
						nickname = "";
					}
					return "你的好友"+nickname+"发来了消息!";
				}

				@Override
				public String getLatestText(EMMessage message,
						int fromUsersNum, int messageNum) {
					// TODO Auto-generated method stub
					EMMessage.Type type = message.getType();
					if(type ==EMMessage.Type.TXT){
					      String content = ((TextMessageBody) message.getBody()).getMessage();
						   return content;
					}else{	   
						   return fromUsersNum + "个朋友，发来了" + messageNum + "条消息";
					}
//					return null;
					
				}

				@Override
				public String getTitle(EMMessage message) {
					// TODO Auto-generated method stub
					return nickname;
				}

				@Override
				public int getSmallIcon(EMMessage message) {
					// TODO Auto-generated method stub
					return R.drawable.ic_launcher;
				}

				@Override
				public Intent getLaunchIntent(EMMessage message) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(applicationContext, SplashActivity.class);
//					Intent intent = new Intent(applicationContext, ChatActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					ChatType chatType = message.getChatType();
					if(chatType == ChatType.Chat){ //单聊信息
						intent.putExtra("offlineId", message.getFrom());
						intent.putExtra(NOTIFACATION, NOTIFACATION);
						//intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
					}else{ //群聊信息
						//message.getTo()为群聊id 
						intent.putExtra("groupId", message.getTo());
						//intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					}
					
					return intent;
				}});
          //获取到配置options对象
         /*   EMChatOptions options = EMChatManager.getInstance().getChatOptions();
            options.setNotifyBySoundAndVibrate(true); //默认为true 开启新消息提醒
            options.setNoticeBySound(true); //默认为true 开启声音提醒
            options.setNoticedByVibrate(true); //默认为true 开启震动提醒
            options.setShowNotificationInBackgroud(true); //默认为true
*/            //设置自定义的文字提示
            initEventListener();
            imageLoader = new ImageLoader(PinApplication.getInstance().getRequestQueue(), new ImageCache() {  
    		    int maxSize = 10 * 1024 * 1024;  
    			private LruCache<String, Bitmap> mCache= new LruCache<String, Bitmap>(maxSize) {  
    	            @Override  
    	            protected int sizeOf(String key, Bitmap bitmap) {  
    	                return bitmap.getRowBytes() * bitmap.getHeight();  
    	            }  
    	        };   
    			  
    		    @Override  
    		    public Bitmap getBitmap(String url) {  
    		        return mCache.get(url);  
    		    }  
    		  
    		    @Override  
    		    public void putBitmap(String url, Bitmap bitmap) {  
    		        mCache.put(url, bitmap);  
    		    }  
    		}); 
            
            GlobalCrashHandler handler = GlobalCrashHandler.getInstance();
            handler.init(getApplicationContext());
            Thread.setDefaultUncaughtExceptionHandler(handler);
	}

	public static PinApplication getInstance() {
		return instance;
	}
	public   HXNotifier getNotifier() {
		return notifier;
	}
	
 
//	/**
//	 * 获取内存中好友user list
//	 *
//	 * @return
//	 */
//	public Map<String, User> getContactList() {
//	    return hxSDKHelper.getContactList();
//	}
//
//	/**
//	 * 设置好友user list到内存中
//	 *
//	 * @param contactList
//	 */
//	public void setContactList(Map<String, User> contactList) {
//	    hxSDKHelper.setContactList(contactList);
//	}
//
//	/**
//	 * 获取当前登陆用户名
//	 *
//	 * @return
//	 */
//	public String getUserName() {
//	    return hxSDKHelper.getHXId();
//	}
//
//	/**
//	 * 获取密码
//	 *
//	 * @return
//	 */
//	public String getPassword() {
//		return hxSDKHelper.getPassword();
//	}
//
//	/**
//	 * 设置用户名
//	 *
//	 * @param user
//	 */
//	public void setUserName(String username) {
//	    hxSDKHelper.setHXId(username);
//	}
//
//	/**
//	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
//	 * 内部的自动登录需要的密码，已经加密存储了
//	 *
//	 * @param pwd
//	 */
//	public void setPassword(String pwd) {
//	    hxSDKHelper.setPassword(pwd);
//	}	
//
//	/**
//	 * 退出登录,清空数据
//	 */
//	public void logout(final EMCallBack emCallBack) {
//		// 先调用sdk logout，在清理app中自己的数据
//	    hxSDKHelper.logout(emCallBack);
//	}
	
	/**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    public void initEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;
            
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                    EMLog.e("Application", "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }
                
                switch (event.getEvent()) {
                case EventNewMessage:
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                 
                    if(!EasyUtils.isAppRunningForeground(getApplicationContext())||activityList.size() <= 0){
                    	  Log.e("refreshA111","--------------");
                       getNotifier().onNewMsg(message);
                    }
                    break;
                case EventOfflineMessage:
                    if(activityList.size() <= 0){
                        EMLog.d("Application", "received offline messages");
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        getNotifier().onNewMesg(messages);
                    }
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage:
                {
                    
                     Log.d("Application", "收到透传消息");
                    //获取消息body
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//获取自定义action
                    
                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    EMLog.d("Application", String.format("透传消息：action:%s,message:%s", action,message.toString()));
                    final String str =  getString(R.string.receive_the_passthrough);
                    
                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
                    
                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Toast.makeText(getApplicationContext(), intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        
                      //注册广播接收者
                         registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str+action);
                     sendBroadcast(broadcastIntent, null);
                    
                    break;
                }
                case EventDeliveryAck:
                    message.setDelivered(true);
                    break;
                case EventReadAck:
                    message.setAcked(true);
                    break;
                // add other events in case you are interested in
                default:
                    break;
                }
                
            }
        };
        
        EMChatManager.getInstance().registerEventListener(eventListener);
        
        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener(){
            private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
            private final IntentFilter filter = new IntentFilter(ROOM_CHANGE_BROADCAST);
            private boolean registered = false;
            
            private void showToast(String value){
                if(!registered){
                  //注册广播接收者
                     registerReceiver(new BroadcastReceiver(){

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Toast.makeText(getApplicationContext(), intent.getStringExtra("value"), Toast.LENGTH_SHORT).show();
                        }
                        
                    }, filter);
                    
                    registered = true;
                }
                
                Intent broadcastIntent = new Intent(ROOM_CHANGE_BROADCAST);
                broadcastIntent.putExtra("value", value);
                sendBroadcast(broadcastIntent, null);
            }
            
            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                showToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                Log.i("info","onChatRoomDestroyed="+roomName);
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showToast("member : " + participant + " join the room : " + roomId);
                Log.i("info", "onmemberjoined="+participant);
                
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                    String participant) {
                showToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberExited="+participant);
                
            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                    String participant) {
                showToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberKicked="+participant);
                
            }

        });
    }
    
    
    public RequestQueue getRequestQueue() {  
        // lazy initialize the request queue, the queue instance will be  
        // created when it is accessed for the first time  
        if (mRequestQueue == null) {  
            // 1  
            // 2  
            synchronized (PinApplication.class) {  
                if (mRequestQueue == null) {  
                    mRequestQueue = Volley  
                            .newRequestQueue(getApplicationContext());  
                }  
            }  
        }  
        return mRequestQueue;  
    }  
  
    /** 
     * Adds the specified request to the global queue, if tag is specified then 
     * it is used else Default TAG is used. 
     *  
     * @param req 
     * @param tag 
     */  
    public <T> void addToRequestQueue(Request<T> req, String tag) {  
        // set the default tag if tag is empty  
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);  
  
        VolleyLog.d("Adding request to queue: %s", req.getUrl());  
  
        getRequestQueue().add(req);  
    }  
  
    /** 
     * Adds the specified request to the global queue using the Default TAG. 
     *  
     * @param req 
     * @param tag 
     */  
    public <T> void addToRequestQueue(Request<T> req) {  
        // set the default tag if tag is empty  
        req.setTag(TAG);  
  
        getRequestQueue().add(req);  
    }  
  
    /** 
     * Cancels all pending requests by the specified TAG, it is important to 
     * specify a TAG so that the pending/ongoing requests can be cancelled. 
     *  
     * @param tag 
     */  
    public void cancelPendingRequests(Object tag) {  
        if (mRequestQueue != null) {  
            mRequestQueue.cancelAll(tag);  
        }  
    }  
    
    public void loadImage(String url,ImageView view,int defaultImage,int failedImage,int w,int h){
		ImageListener listener = ImageLoader.getImageListener(url,view,  
				defaultImage, failedImage);  
		imageLoader.get(url, listener, w, h);  
	}
    
    
}

