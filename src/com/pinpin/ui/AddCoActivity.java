package com.pinpin.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.pinpin.R;

public class AddCoActivity extends BaseActivity {


	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("屏蔽公司");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		 
		inflater.inflate(R.layout.activity_add_co, container);

		back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		
	}

}