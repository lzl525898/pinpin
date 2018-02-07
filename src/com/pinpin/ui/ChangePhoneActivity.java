package com.pinpin.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.pinpin.R;

public class ChangePhoneActivity extends BaseActivity {


	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("修改手机号");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		setNextBtnVisibility();
		 
		inflater.inflate(R.layout.activity_change_phone, container);

		next_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(ChangePhoneActivity.this,
						PhoneVerificationActivity.class);
				startActivity(in);
			//	showAlertDialog("提示", "对不起，输入错误");
			/*	showAlertDialogWithCancel("title", "message",
						new DialogInterface.OnClickListener() {
			           @Override
			           public void onClick(DialogInterface dialog, int which) {
			                
			           }
			           }, "confirm");*/
				
			}
		});

		back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		
	}

}