package com.pinpin.utils;




 
//import android.util.Log;

public class Log {
	private static final DEBUG_LEVEL SETTING_DEBUG_MODE = DEBUG_LEVEL.DEBUG;//Logģʽ������ʱDEBUG_LEVELӦ����ΪRELEASE
	public enum DEBUG_LEVEL {  
		RELEASE, INFO, DEBUG, WARNING, ERROR,
	}
	 public static int getDebugLevel(DEBUG_LEVEL level){
		  
		 switch (level){
		
		 case INFO:
			 return 0;
		 case DEBUG:
			 return 1;
		 case WARNING:
			 return 2;
		 case ERROR:
			 return 3;
		 case RELEASE:
			 return 4;
		 }
		 return 5;
	 }
	
  public  static void d(String tag,String msg){
	  if(getDebugLevel(SETTING_DEBUG_MODE)<=1){
	  if(tag==null){
		  tag = "empty tag";
	  }
	  if(msg==null){
		  msg = "empty message";
	  }
	  android.util.Log.d(tag,msg);
	  }
  }
  
  public  static void e(String tag,String msg){
	  if(getDebugLevel(SETTING_DEBUG_MODE)<=3){
	  if(tag==null){
		  tag = "empty tag";
	  }
	  if(msg==null){
		  msg = "empty message";
	  }
	  android.util.Log.e(tag,msg);
	  }
  }
  public  static void e(String tag,String msg,Exception e){
	  if(getDebugLevel(SETTING_DEBUG_MODE)<=3){
	  if(tag==null){
		  tag = "empty tag";
	  }
	  if(msg==null){
		  msg = "empty message";
	  }
	  android.util.Log.e(tag,msg,e);
	  }
  }
  
  public  static void i(String tag,String msg){
	  if(getDebugLevel(SETTING_DEBUG_MODE)<=0){
	  if(tag==null){
		  tag = "empty tag";
	  }
	  if(msg==null){
		  msg = "empty message";
	  }
	  android.util.Log.i(tag,msg);
	  }
  }
  public  static void w(String tag,String msg){
	  if(getDebugLevel(SETTING_DEBUG_MODE)<=2)
	  if(tag==null){
		  tag = "empty tag";
	  }
	  if(msg==null){
		  msg = "empty message";
	  }
	  android.util.Log.w(tag,msg);
	  }
  
}
