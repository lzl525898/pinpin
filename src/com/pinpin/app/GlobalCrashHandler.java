package com.pinpin.app;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import com.pinpin.constants.Address;
import com.pinpin.core.http.impl.HttpInstance;
import com.pinpin.guide.Guide;
import com.pinpin.network.HttpTask;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.LoginActivity;
import com.pinpin.utils.Log;

 
/**
 * 自定义的 异常处理类 , 实现了 UncaughtExceptionHandler接口 
 * @author lixd186
 *
 */
public class GlobalCrashHandler implements UncaughtExceptionHandler {
 // 需求是 整个应用程序 只有一个 MyCrash-Handler 
 private static GlobalCrashHandler GlobalCrashHandler ;
 private Context context;
 private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

 //1.私有化构造方法
 private GlobalCrashHandler(){

 }

 public static synchronized GlobalCrashHandler getInstance(){
  if(GlobalCrashHandler!=null){
   return GlobalCrashHandler;
  }else {
   GlobalCrashHandler  = new GlobalCrashHandler();
   return GlobalCrashHandler;
  }
 }
 public void init(Context context){
  this.context = context;
  
 }
   
  public void uncaughtException(Thread arg0, Throwable arg1) {
	  //Toast.makeText(context, "程序发生意外错误，我们深表歉意！", Toast.LENGTH_LONG).show();
	  //context.startActivity(new Intent(context, LoginActivity.class));
  System.out.println("程序发生意外错误！ ");
  // 1.获取当前程序的版本号. 版本的id
  final String versioninfo = getVersionInfo();

  // 2.获取手机的硬件信息.
  final String mobileInfo  = getMobileInfo();

  // 3.把错误的堆栈信息 获取出来 
  final String errorinfo = getErrorInfo(arg1);

  // 4.把所有的信息 还有信息对应的时间 提交到服务器 
  try {
	  Log.e("Time",dataFormat.format(new Date()));
	  Log.e("osVersion",Build.VERSION.SDK_INT+"");
	  Log.e("osType",versioninfo);
	  Log.e("deviceMode",mobileInfo);
	  Log.e("content",errorinfo);
	  HashMap<String,String> data = new  HashMap<String,String>(){{
		 put("osVersion",Build.VERSION.SDK_INT+"");
		 put("osType",versioninfo);
		 put("deviceMode",mobileInfo);
		 put("content",errorinfo);
		     
	  }};
	  final RequestData request = HttpUtils.simplePostData(Address.HOST+Address.ACCESSLOG, data);
	  new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			  byte[] result = HttpInstance.getInstance().post(request.uri, request.body,null);
			  ResposneBundle r = new ResposneBundle("utf-8", result);
			  android.os.Process.killProcess(android.os.Process.myPid());
		}
	}).start();
	
	 
  } catch (Exception e) {
   e.printStackTrace();
  }
  Toast.makeText(context, "程序发生意外错误，我们深表歉意！", Toast.LENGTH_LONG).show();
  //context.startActivity(new Intent(context, Guide.class));
  //干掉当前的程序 
  //android.os.Process.killProcess(android.os.Process.myPid());
 } 
  /**
  * 获取错误的信息 
  * @param arg1
  * @return
  */
 private String getErrorInfo(Throwable arg1) {
  Writer writer = new StringWriter();
  PrintWriter pw = new PrintWriter(writer);
  arg1.printStackTrace(pw);
  pw.close();
  String error= writer.toString();
  return error;
 } 
  /**
  * 获取手机的硬件信息 
  * @return
  */
 private String getMobileInfo() {
  StringBuffer sb = new StringBuffer();
  //通过反射获取系统的硬件信息 
  try { 
    Field[] fields = Build.class.getDeclaredFields();
   for(Field field: fields){
    //暴力反射 ,获取私有的信息 
    field.setAccessible(true);
    String name = field.getName();
    String value = field.get(null).toString();
    sb.append(name+"="+value);
    sb.append("||");
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
  return sb.toString();
 } 
  /**
  * 获取手机的版本信息
  * @return
  */
 private String getVersionInfo(){
  try {
   PackageManager pm = context.getPackageManager();
    PackageInfo info =pm.getPackageInfo(context.getPackageName(), 0);
    return  info.versionName;
  } catch (Exception e) {
   e.printStackTrace();
   return "版本号未知";
  }
 }
}
 