package com.android.volley;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.pinpin.app.PinApplication;
import com.pinpin.constants.Constants;
import com.pinpin.ui.MainActivity;

public class DemoHelper {
    /**
     * 数据同步listener
     */
    static public interface DataSyncListener {
        /**
         * 同步完毕
         * @param success true：成功同步到数据，false失败
         */
        public void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";
    
	
    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

	private static DemoHelper instance = null;
	
	
	/**
     * HuanXin sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;
    /**
     * HuanXin sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;
    /**
     * HuanXin sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    
    private boolean alreadyNotified = false;
	
	public boolean isVoiceCalling;
    public boolean isVideoCalling;

	private String username;

    private Context appContext;


    private EMConnectionListener connectionListener;


    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;

	private DemoHelper() {
	}

	public synchronized static DemoHelper getInstance() {
		if (instance == null) {
			instance = new DemoHelper();
		}
		return instance;
	}
	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
		 appContext = context;
			//设置全局监听
			setGlobalListeners();
//		if (EaseUI.getInstance().init(context)) {
//		    appContext = context;
//		    
//		    //if your app is supposed to user Google Push, please set project number
//            String projectNumber = "562451699741";
//            //不使用GCM推送的注释掉这行
//            EMChatManager.getInstance().setGCMProjectNumber(projectNumber);
//            //在小米手机上当app被kill时使用小米推送进行消息提示，同GCM一样不是必须的
//            EMChatManager.getInstance().setMipushConfig("2882303761517370134", "5131737040134");
//		    
//		    //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
//		    EMChat.getInstance().setDebugMode(true);
//		    //get easeui instance
//		    easeUI = EaseUI.getInstance();
//		    //调用easeui的api设置providers
//		    setEaseUIProviders();
//		    demoModel = new DemoModel(context);
//		    //设置chat options
//		    setChatoptions();
//			//初始化PreferenceManager
//			PreferenceManager.init(context);
//			//初始化用户管理类
//			getUserProfileManager().init(context);
			
	
//			broadcastManager = LocalBroadcastManager.getInstance(appContext);
//	        initDbDao();
	        
//		}
	}
	/**
	    * 同步操作，从服务器获取群组列表
	    * 该方法会记录更新状态，可以通过isSyncingGroupsFromServer获取是否正在更新
	    * 和isGroupsSyncedWithServer获取是否更新已经完成
	    * @throws EaseMobException
	    */
	   public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback){
	       if(isSyncingGroupsWithServer){
	           return;
	       }
	       
	       isSyncingGroupsWithServer = true;
	       
	       new Thread(){
	           @Override
	           public void run(){
	               try {
	                   EMGroupManager.getInstance().getGroupsFromServer();
	                   
	                   // in case that logout already before server returns, we should return immediately
	                   if(!EMChat.getInstance().isLoggedIn()){
	                       return;
	                   }
	                   
	                  
	                   
	                   isGroupsSyncedWithServer = true;
	                   isSyncingGroupsWithServer = false;
	                   
	                   //通知listener同步群组完毕
	                   noitifyGroupSyncListeners(true);
	                   if(isContactsSyncedWithServer()){
	                       notifyForRecevingEvents();
	                   }
	                   if(callback != null){
	                       callback.onSuccess();
	                   }
	               } catch (EaseMobException e) {
	                   isGroupsSyncedWithServer = false;
	                   isSyncingGroupsWithServer = false;
	                   noitifyGroupSyncListeners(false);
	                   if(callback != null){
	                       callback.onError(e.getErrorCode(), e.toString());
	                   }
	               }
	           
	           }
	       }.start();
	   }


    /**
     * 设置全局事件监听
     */
    protected void setGlobalListeners(){
    	Log.e("error","进去全局监听" );
        syncGroupsListeners = new ArrayList<DataSyncListener>();
        syncContactsListeners = new ArrayList<DataSyncListener>();
        syncBlackListListeners = new ArrayList<DataSyncListener>();
        Log.e("error","准备进去匿名内部类" );
        // create the global connection listener
        connectionListener = new EMConnectionListener(){
        	  // create the global connection listener
                @Override
                public void onDisconnected(int error) {
                    if (error == EMError.USER_REMOVED) {
                        onCurrentAccountRemoved();
                    }else if (error == EMError.CONNECTION_CONFLICT) {
                        onConnectionConflict();
                    }
                }

                @Override
                public void onConnected() {
                    
                    // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                    if(isGroupsSyncedWithServer && isContactsSyncedWithServer){
                        new Thread(){
                            @Override
                            public void run(){
                                DemoHelper.getInstance().notifyForRecevingEvents();
                            }
                        }.start();
                    }
//                    else{
//                        if(!isGroupsSyncedWithServer){
//                            asyncFetchGroupsFromServer(null);
//                        }
//                        
//                        if(!isContactsSyncedWithServer){
//                            asyncFetchContactsFromServer(null);
//                        }
//                        
//                        if(!isBlackListSyncedWithServer){
//                            asyncFetchBlackListFromServer(null);
//                        }
//                    }
                }
            };
            
//            IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
//            if(callReceiver == null){
//                callReceiver = new CallReceiver();
//            }
//
//            //注册通话广播接收者
//            appContext.registerReceiver(callReceiver, callFilter);   
            
            //注册连接监听匿名内部类才能正常进行
            EMChatManager.getInstance().addConnectionListener(connectionListener);       
            //注册群组和联系人监听
//            registerGroupAndContactListener();
//            注册消息事件监听
    }
    
    /**
     * 账号在别的设备登录
     */
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);
    }
    /**
     * 账号被移除
     */
    protected void onCurrentAccountRemoved(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
	
	/**
	 * 是否登录成功过
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMChat.getInstance().isLoggedIn();
	}

	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken
	 *            是否解绑设备token(使用GCM才有)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		endCall();
		EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
			    reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}
	
	void endCall() {
		try {
			EMChatManager.getInstance().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  public void addSyncGroupListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncGroupsListeners.contains(listener)) {
	            syncGroupsListeners.add(listener);
	        }
	    }

	    public void removeSyncGroupListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncGroupsListeners.contains(listener)) {
	            syncGroupsListeners.remove(listener);
	        }
	    }

	    public void addSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncContactsListeners.contains(listener)) {
	            syncContactsListeners.add(listener);
	        }
	    }

	    public void removeSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncContactsListeners.contains(listener)) {
	            syncContactsListeners.remove(listener);
	        }
	    }

	    public void addSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.add(listener);
	        }
	    }

	    public void removeSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.remove(listener);
	        }
	    }
	
   public void noitifyGroupSyncListeners(boolean success){
       for (DataSyncListener listener : syncGroupsListeners) {
           listener.onSyncComplete(success);
       }
   }
   
   
   public void notifyContactsSyncListener(boolean success){
       for (DataSyncListener listener : syncContactsListeners) {
           listener.onSyncComplete(success);
       }
   }
   

	
	public void notifyBlackListSyncListener(boolean success){
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }
    
    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }
    
    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }
	
	public synchronized void notifyForRecevingEvents(){
        if(alreadyNotified){
            return;
        }
        
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
        alreadyNotified = true;
    }
	
    synchronized void reset(){
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;
        
        
        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;
        
        alreadyNotified = false;
        isGroupAndContactListenerRegisted = false;
        
      
    }

  

}
