package com.pinpin.utils;

 

import java.util.List;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Log;

public class SystemUtils {
	
	public static String getSign(Context c){
		StringBuilder sb = new StringBuilder();
		String result = null;;
		try {
			PackageInfo info = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				sb.append(signature.toCharsString());
            }
            /************** 得到应用签名 **************/
			result = sb.toString();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return result;
	}
	
	public static boolean isCUMobileNumber(String mobiles) {
		//return Pattern.compile("^((13[0-4])|(15[5-6])|(176)|(18[5-6]))\\d{8}").matcher(mobiles).matches();
		return Pattern.compile("^186466[1-3]\\d{4}").matcher(mobiles).matches()||Pattern.compile("^185461[0-1]\\d{4}").matcher(mobiles).matches();
		
	}
	public static boolean isProcessStarted(Context context,String packageName)
    {
        boolean isStarted =false;
        try
        {
            ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
           // int intGetTastCounter = 1000;
            List<RunningAppProcessInfo> mRunningProcesses = mActivityManager.getRunningAppProcesses() ;
            for (RunningAppProcessInfo process :  mRunningProcesses )
            {
            	Log.d("packName", "process:"+process.processName);
                    if(TextUtils.equals(packageName, process.processName))
                    {                
                            isStarted = true;
                            break;
                    }
            }
        }
        catch(SecurityException e)
        {
                e.printStackTrace();
        }            
        return isStarted;                
    }
 
public static boolean isServiceStarted(Context context,String serviceClassName)
        {
            boolean isStarted =false;
            try
            {
                ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                int intGetTastCounter = 1000;
                List<ActivityManager.RunningServiceInfo> mRunningService = 
                          mActivityManager.getRunningServices(intGetTastCounter );
                for (ActivityManager.RunningServiceInfo amService : mRunningService)
                {
                	Log.d("ServiceClassName", serviceClassName);
                	Log.d("amService", amService.service.getClassName());
                        if(0 == amService.service.getClassName().compareTo(serviceClassName))
                        {                
                                isStarted = true;
                                break;
                        }
                }
            }
            catch(SecurityException e)
            {
                    e.printStackTrace();
            }            
            return isStarted;                
        }

/**
 * 返回当前程序版本名称
 */
public static String getAppVersionName(Context context) {
	String versionName = "未知版本";
	try {
		// Get the package info
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
		versionName = pi.versionName;
		if (TextUtils.isEmpty(versionName)) {
			return "";
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return versionName;
}
}


