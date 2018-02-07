package com.pinpin.network;

import java.io.File;

import android.os.AsyncTask;
import android.view.View;

import com.pinpin.core.http.impl.HttpInstance;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.ui.BaseActivity;
import com.pinpin.ui.LoginActivity;

 
/**s
 * Params --   task执行excute方法时传入的可变参数，同时被传入doInBackground
 * Progress -- 执行的进度相关的可变长参数，由publishProgress（在doInBackground中）方法传入�?
 *             在onProgressUpdate方法中获�?
 * Result --   doInBackground返回的结果，同时被传�?
 */
public class HttpTask extends AsyncTask<RequestData,Integer, ResposneBundle> {
	private BaseActivity mContext;
	 
	public interface TaskResultListener {
		void result(ResposneBundle b);
	 
	    void failed(String message); 
	}
	public interface TaskResultListener2 {
		 
		void result(String b,View v);
	    void failed(String message); 
	}
	TaskResultListener taskResult;
	boolean loading = true;
	public HttpTask(BaseActivity context,TaskResultListener taskResult){
		mContext = context;
		this.taskResult = taskResult; 
	}
	
	public HttpTask(BaseActivity context,TaskResultListener taskResult,boolean loading){
		mContext = context;
		this.taskResult = taskResult; 
		this.loading = loading;
	}
	
	public HttpTask(LoginActivity loginActivity, TaskResultListener l,
			boolean withLoading) {
		// TODO Auto-generated constructor stub
	}

	public HttpTask(LoginActivity loginActivity, TaskResultListener l) {
		// TODO Auto-generated constructor stub
	}

	protected void onPreExecute() {
		if(loading)
		mContext.showProgress("加载中...");
 
	}
	 
	protected ResposneBundle doInBackground(RequestData... params) {
		//publishProgress(1);
		
		//return HttpUtils.excuteRequest(params[0],taskResult);
		 switch(params[0].method){
		   case  GET:
			   byte[] result = HttpInstance.getInstance().get(params[0].uri, taskResult);
			   ResposneBundle r = new ResposneBundle("utf-8", result);
			   return r;
		   case  POST:
			    result = HttpInstance.getInstance().post(params[0].uri, params[0].body,taskResult);
			    r = new ResposneBundle("utf-8", result);
			   return r;
		   case  MULTIPART:
			    result = HttpInstance.getInstance().mpart(params[0].uri,params[0].body, taskResult,params[0].files);
			    r = new ResposneBundle("utf-8", result);
			   return r;
		   case  DOWNLOAD:
			    String path = HttpInstance.getInstance().download(mContext,params[0].uri,new File(params[0].target), taskResult);
			    r = new ResposneBundle(path);
			    return r;
		 } 
		 return null;
	}
	
	/**
	 * 后台线程的任务执行完毕，result即为doInBackground的执行结�?
	 * 
	 */
	protected void onPostExecute(ResposneBundle result) {
		if(loading)
		mContext.dismissProgress();
		
		
    	if(result != null&&result.result != null){
    		taskResult.result(result);
	    }else{
	    	 
	    }
	
	}
    protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
      //textView.setText("图片尺寸�?+values[2]+" \r\n已下载大小："+values[1]+" \r\n当前下载进度�?+values[0]+"%");
	}
}
