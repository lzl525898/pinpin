package com.pinpin.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinpin.R;

public class ReportActivity extends BaseActivity {

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("举报小王");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		setNextBtnVisibility();
		next_btn.setText("提交");

		inflater.inflate(R.layout.activity_report, container);

		next_btn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub	
				  showAlertDialog("提示", "您的提交举报已经收到，我们将核实后告知您结果。", new DialogInterface.OnClickListener() {
						
						@SuppressLint("NewApi")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							finish();
						 }
					}, "确定");
				  
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
