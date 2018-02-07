package com.pinpin.utils;

/**
 * 
 */
 

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * 手机网络连接检测器
 * 
 * @author lixd186
 *
 */
public class ConnectionDetector {
	/**
	 * 
	 * 判断连接是否正常
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity == null)
			return false;
		else {
			NetworkInfo[] infos = connectivity.getAllNetworkInfo();
			
			for (NetworkInfo info : infos) {
				if (info.isConnected())
					return true;
			}
		}
		return false;
	}
	
	public static NetworkInfo getWhichNetworkAvailable(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity == null)
			return null;
		else {
			NetworkInfo[] infos = connectivity.getAllNetworkInfo();
			
			for (NetworkInfo info : infos) {
				if (info.isConnected()){
					
//					//Log.d("info.getDetailedState()",info.getDetailedState().name()+"");
//					//Log.d("info.getSubtypeName()",info.getSubtypeName()+"");
//					//Log.d("info.getExtraInfo()",info.getExtraInfo()+"");
//					//Log.d("info.getReason()",info.getReason()+"");
//					//Log.d("info.getSubtypeName()",info.getSubtypeName()+"");
//					//Log.d("info.getTypeName()",info.getTypeName()+"");
					
					return info;
				}
					
			}
		}
		
		return null;
	}
	
	public static boolean isWifi(Context context) {   
        ConnectivityManager connectivityManager = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();   
        if (activeNetInfo != null   
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {   
            return true;   
        }   
        return false;   
    }

	
}
