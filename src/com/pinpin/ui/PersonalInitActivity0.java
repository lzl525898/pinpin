package com.pinpin.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.constants.Constants;
import com.pinpin.utils.ImageUtils;
/**
 * 
 * @author lee
 *  个人信息初始化
 */
public class PersonalInitActivity0 extends BaseActivity {
	RelativeLayout rootView;
    ImageButton[] photos;
    List <String>drawables;
 
	private View.OnClickListener mClickDeleteListener = new OnClickListener() {
		
		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
			showAlertDialog("提醒", "确定要删除图片吗？", new DialogInterface.OnClickListener() {
				
				@SuppressLint("NewApi")
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					drawables.remove(v.getTag());
					refreshPhoto();
				 }
			}, "确定");
		}
	};
    private View.OnClickListener mGetPhotoListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			selectPicFromLocal() ;
		}
	};
	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCAL);
	}
	/**
	 * onActivityResult
	 */
	 private void refreshPhoto(){
		 if(drawables.size()>0){
			 Constants.AVATAR_PATH=drawables.get(0);
			 Constants.USER_AVATAR_PATH=drawables.get(0);
		 }else{
			 Constants.AVATAR_PATH=null;
		 }
		 for(int i=0;i<photos.length;i++){
				
				if(i<=drawables.size()-1){
					Bitmap bmp =ImageUtils.getBitmapFromFile(new File(drawables.get(i)),200,200);
					photos[i].setImageBitmap(bmp);
					bmp = null;
					 
					//photos[i].setImageURI(Uri.fromFile(new File(drawables.get(i))));
				    photos[i].setOnClickListener(mClickDeleteListener );
				    photos[i].setTag(drawables.get(i));
				}else{
					photos[i].setTag(null);
					photos[i].setImageBitmap(null);
					photos[i].setOnClickListener(mGetPhotoListener);
				} 
			}
	 }
	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("设置个人信息");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		
		//inflater.inflate(R.layout.activity_personal, container);
		rootView = (RelativeLayout) inflater.inflate(R.layout.activity_personal_init0, container);
		drawables = new  ArrayList<String>();
	 
		photos = new ImageButton[6];
		photos[0] = (ImageButton) rootView.findViewById(R.id.btn_personal_a);
		photos[1] = (ImageButton) rootView.findViewById(R.id.btn_personal_b);
		photos[2] = (ImageButton) rootView.findViewById(R.id.btn_personal_c);
		photos[3] = (ImageButton) rootView.findViewById(R.id.btn_personal_d);
		photos[4] = (ImageButton) rootView.findViewById(R.id.btn_personal_e);
		photos[5] = (ImageButton) rootView.findViewById(R.id.btn_personal_f);
		refreshPhoto();
	//	LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.activity_setting, null);
		RelativeLayout tradeView = (RelativeLayout) rootView.findViewById(R.id.trade_layout);	  
		tradeView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub	
				Intent in = new Intent(PersonalInitActivity0.this,
						TradeActivity.class);
				in.putExtra("title", "行业");
				//startActivity(in);
				
                startActivityForResult(in, 1000);
			  }
		  });
		
		RelativeLayout positionView = (RelativeLayout) rootView.findViewById(R.id.position_layout);	  
		positionView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub	
				Intent in = new Intent(PersonalInitActivity0.this,
						ChangeMessageActivity.class);
				//in.putExtra("title", "职位");
				//startActivity(in);
				
				/*
                 * 如果希望启动另一个Activity，并且希望有返回值，则需要使用startActivityForResult这个方法，
                 * 第一个参数是Intent对象，第二个参数是一个requestCode值，如果有多个按钮都要启动Activity，则requestCode标志着每个按钮所启动的Activity
                 */
               // startActivityForResult(in, 1000);
			  }
		  });
		RelativeLayout serviceYearView = (RelativeLayout) rootView
				.findViewById(R.id.service_year_layout);
		serviceYearView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PersonalInitActivity0.this,
						ServiceYearActivity.class);
				in.putExtra("title", "工作年限");

				startActivityForResult(in, 1000);
			}
		});

		RelativeLayout placeView = (RelativeLayout) rootView.findViewById(R.id.come_from_layout);	  
		placeView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub	
				Intent in = new Intent(PersonalInitActivity0.this,
						TradeActivity.class);
				in.putExtra("title", "期望工作地");
				//startActivity(in);
				
                startActivityForResult(in, 1000);
			  }
		  });
		
//		Button tag_btn = (Button) findViewById(R.id.btn_personal_tag2);
//		tag_btn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//				Intent in = new Intent(PersonalInitActivity.this,
//						TradeActivity.class);
//				in.putExtra("title", "个性标签");
//				//startActivity(in);
//				
//                startActivityForResult(in, 1000);
//			}
//		});
//		
		

	}
	/**
     * 所有的Activity对象的返回值都是由这个方法来接收
     * requestCode:    表示的是启动一个Activity时传过去的requestCode值
     * resultCode：表示的是启动后的Activity回传值时的resultCode值
     * data：表示的是启动后的Activity回传过来的Intent对象  position_name
     */
    @SuppressLint("NewApi")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 清空消息
			 if (requestCode == ChatActivity.REQUEST_CODE_LOCAL) { // 发送本地图片
			 
				if (data != null) {
					Uri selectedImage = data.getData();
					String path = null;
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
						String st8 = getResources().getString(R.string.cant_find_pictures);
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							String picturePath = cursor.getString(columnIndex);
							cursor.close();
							cursor = null;

							if (picturePath == null || picturePath.equals("null")) {
								Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;
							}
							path = picturePath;
							
							 
						} else {
							File file = new File(selectedImage.getPath());
							if (!file.exists()) {
								Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;

							}
							path = file.getAbsolutePath();
					  }
						drawables.add(path);
						refreshPhoto();
						//photos[index].setImageBitmap(BitmapFactory.decodeFile(path));
						 
					}
				}
			} 
       }
        if(requestCode == 1000 && resultCode == 1001)
         {
        	//View view = inflater.inflate(R.layout.activity_personal, null);
        	TextView textView = (TextView)rootView.findViewById(R.id.position_content);
            String result_value = data.getStringExtra("result");
            textView.setText(result_value);
         }
    }

}
