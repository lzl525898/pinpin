package com.pinpin.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinpin.R;

public class ReportDetailActivity extends BaseActivity {

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("举报小王");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		setNextBtnVisibility();
		next_btn.setText("提交");

		inflater.inflate(R.layout.activity_report_detail, container);
		
		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});
	}

}
