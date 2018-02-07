package com.pinpin.ui;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.app.PinApplication;
import com.pinpin.constants.Constants;
import com.pinpin.core.http.impl.HttpInstance;
import com.pinpin.model.ChatListModel;
import com.pinpin.model.UserInfo;
import com.pinpin.network.HttpTask;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpTask.TaskResultListener2;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.utils.Log;
import com.pinpin.view.BadgeView;
import com.pinpin.view.CustomProgressDialog;

 
@SuppressLint("NewApi")
public abstract class BaseActivity extends FragmentActivity {
	protected PopupWindow popupWindow;
	AlertDialog ab;
	protected HttpTask task;
	protected ViewGroup container;
	protected ViewGroup header;
	protected ViewGroup footer;
	protected Button return_btn;
	protected Button back_btn;
	protected Button search_btn;
	protected Button next_btn;
	protected Button left_btn;
	protected Button right_btn;
	protected ImageView badge_container;
	protected  BadgeView badgeChatBtn;
	protected ViewGroup btn_parent;
	protected TextView title_txt;
	protected LayoutInflater inflater;
	private CustomProgressDialog progressDialog;
	 
	 
	protected Button me_btn;
	protected Button pinpin_btn;     
	protected Button chat_btn;
	public ViewGroup pinpin_btn_area;
	protected ViewGroup me_btn_area;
	protected ViewGroup  chat_btn_area;
	private InputMethodManager manager;
	private int finishCount;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PinApplication.getInstance().pushActivity(this);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//隐藏软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//打开输入法窗口:
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 
		setContentView(R.layout.base_activity);
		
		 DisplayMetrics metric = new DisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(metric);
	     Constants.screen_width = metric.widthPixels;     // 屏幕宽度（像素）
	     Constants.screen_height = metric.heightPixels;   // 屏幕高度（像素）
	     float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
	     int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
		 
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		container = (ViewGroup) findViewById(R.id.container);
		header = (ViewGroup) findViewById(R.id.top);
		footer = (ViewGroup) findViewById(R.id.bottom);
		search_btn = (Button) findViewById(R.id.search_btn);
		next_btn = (Button) findViewById(R.id.next_btn);
		back_btn = (Button) findViewById(R.id.back_btn);
 
		title_txt = (TextView) findViewById(R.id.title_txt);
		
		pinpin_btn_area = (ViewGroup) findViewById(R.id.btn_jihui_container);
		me_btn_area = (ViewGroup) findViewById(R.id.btn_me_container);
		chat_btn_area = (ViewGroup) findViewById(R.id.btn_xiaoxi_container);
		pinpin_btn = (Button) findViewById(R.id.btn_jihui);
	    me_btn = (Button) findViewById(R.id.btn_me);
	    chat_btn = (Button) findViewById(R.id.btn_xiaoxi);
	    badge_container= (ImageView) findViewById(R.id.badge_container);
		onConentViewLoad(container);
//        back_btn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//				finish(); 
//			}
//		});
		 
	}

	/**
	 * 加载显示主内容到container，通常可考虑调用多次initLayoutMode来初始化页面布局
	 * 
	 * @param container
	 */
	protected abstract void onConentViewLoad(ViewGroup container);

	protected void onContentViewReLoad(ViewGroup container) {
		
	}

	protected void onContentViewChange(ViewGroup container, View newView) {
		container.removeAllViews();
		container.addView(newView);
	}

	public void setTitle(String title) {
		title_txt.setText(title);
	}
	public void noHeader() {
		header.setVisibility(View.GONE);
	}
	public void setSearchButton(int drawable,int w,int h){
		search_btn.setBackgroundResource(R.drawable.btn_gengduo);
		 RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w,h);
	     params.addRule(RelativeLayout.CENTER_VERTICAL);
		 params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		 
		 params.setMargins(0, 0, 30, 0);
		 search_btn.setLayoutParams(params );
	}
	public void showHeader() {
		header.setVisibility(View.VISIBLE);
	}
	public void setSearchBtnGone() {
		search_btn.setVisibility(View.GONE);
	}
	public void setSearchBtnVisible() {
		search_btn.setVisibility(View.VISIBLE);
	}
	
	public void setNextBtnVisibility() {
		next_btn.setVisibility(View.VISIBLE);
	}
	
	public void setBackBtnVisibility() {
		back_btn.setVisibility(View.VISIBLE);
	}
	public void setBackBtnGone() {
		back_btn.setVisibility(View.GONE);
	}
	public void setFooterGone() {
		footer.setVisibility(View.GONE);
	}

	public void showProgress(String msg) {

		if (null == progressDialog) {
			progressDialog = CustomProgressDialog.createDialog(this);
			//progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			//progressDialog.setTitle(title);
			progressDialog.setMessage(msg);
			progressDialog.setCancelable(false);
//			progressDialog.setCancelable(true);
		}
		dismissProgress();
		 
		if(android.os.Build.VERSION.SDK_INT>=17&&isDestroyed()){
			return;
		}
		if(isFinishing()){return;}
		progressDialog.show();
	}

	public void dismissProgress() {
		if (null != progressDialog) {
			progressDialog.dismiss();
		}
	}

	public void showToast(  String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	public void showAlertDialog(String title, String message) {
		if(isFinishing()){return;}
  
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(title)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("确  定",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialogInterface, int i) {

							}
						});

		ab.create().show();
	}

	public void showAlertDialog(String title, String message,
			DialogInterface.OnClickListener event, String confirm) {

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(title).setMessage(message).setCancelable(false)
				.setPositiveButton(confirm, event);

		ab.create().show();

	}

	public void showAlertDialogWithCancel(String title, String message,
			DialogInterface.OnClickListener event, String confirm,DialogInterface.OnClickListener eventCancel,String cancel) {

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(title)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(confirm, event)
				.setNegativeButton(cancel, eventCancel);

		ab.create().show();

	}

	public void showAlertDialogWithCancel(String title, String message,
			DialogInterface.OnClickListener event, String confirm) {

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(title)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(confirm, event)
				.setNegativeButton("取 消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}});

		ab.create().show();

	}
	public void startHttpTask(TaskResultListener l,RequestData data,boolean withLoading) {
		task = new HttpTask(this, l,withLoading);
		task.execute(data);
    }
	public void startHttpTask(TaskResultListener l,RequestData data) {
		task = new HttpTask(this, l);
		task.execute(data);
    }
	public void fileDownload(final TaskResultListener l,  final ConcurrentLinkedQueue<UserInfo>   infos) {
		 
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(UserInfo info:infos){
					  Log.e("开始下载图片...",info.url);
					  String path = HttpInstance.getInstance().download(BaseActivity.this,info.url,info.path, null);
					  Log.e("图片下载完成...",info.path.getAbsolutePath());
		        }
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(l!=null){
							l.result(null);
						}
					}
				});
			}
		}).start();
		 
	 }
	public void fileDownload(final TaskResultListener l,  final List<UserInfo>   infos) {
		 
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(UserInfo info:infos){
					  if(info==null||info.path==null){
						  continue;
					  }
					  if(info.path.exists()&&info.path.length()>0){
						  continue;
					  }
					  Log.e("开始下载图片...",info.url);
					  String path = HttpInstance.getInstance().download(BaseActivity.this,info.url,info.path, null);
					  Log.e("图片下载完成...",info.path.getAbsolutePath());
		        }
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(l!=null){
							l.result(null);
						}
					}
				});
			}
		}).start();
		 
	 }
	public void fileDownload(final TaskResultListener2 l, final  ChatListModel info,final View view) {
		 
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			   final String path = HttpInstance.getInstance().download(BaseActivity.this,info.avatar_url,new File(info.path), null);
			   runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(l!=null){
							if(!TextUtils.isEmpty(path)){
								l.result(path,view);
							}else{
								l.failed("文件下载失败");
							}
							
						}
					}
				});
			}
		}).start();
		 
	 }
	protected void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}



	public  String getUsername(){
		
		return Constants.USERNAME;
	 }
	protected void showPopupWindow() {  
	        if (null == popupWindow||popupWindow.isShowing()) {  
	            return;  
	        } 
	         popupWindow.showAtLocation(container, Gravity.BOTTOM, 0, 0); 
	      
	    }  
	protected void dismissPopupWindow() {  
        if (null != popupWindow||popupWindow.isShowing()) {  
        	popupWindow.dismiss();
        	 
        } 
        
    }  
	  /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }
	protected void initPopuptWindow(int layout) {  
        // TODO Auto-generated method stub  
        // 获取自定义布局文件activity_popupwindow_left.xml的视图  
        View popupWindow_view =inflater.inflate(R.layout.popwindow_layout, null,  
                false);  
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度  
        popupWindow = new PopupWindow(popupWindow_view,  LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);  
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(0, 200, 200, 200)));
        inflater.inflate(layout, (ViewGroup)popupWindow_view.findViewById(R.id.pop_container));
        // 设置动画效果  
        popupWindow.setAnimationStyle(R.style.AnimationFade);  
        // 点击其他地方消失  
      /*  popupWindow_view.setOnTouchListener(new OnTouchListener() {  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // TODO Auto-generated method stub  
                if (popupWindow != null && popupWindow.isShowing()) {  
                    popupWindow.dismiss();  
                   // popupWindow = null;  
                }  
                return false;  
            }  
        });  */
    }



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PinApplication.getInstance().popActivity(this);
	}  
	
}
