package com.pinpin.ui;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.pinpin.R;
import com.pinpin.utils.Log;

public class SearchSettingActivity extends BaseActivity {

	protected int nFlag = 0;
	protected boolean bFlagZhaopin = false;
	protected boolean bFlagQiuzhi = false;
	protected boolean bFlagHehuo = false;
	protected boolean bFlagLianxiren = false;
	protected boolean bFlagGongsi = false;

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("设置");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		 
		RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.activity_setting, container);
		
	//	LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.activity_setting, null);
		RelativeLayout maskView = (RelativeLayout) rootView.findViewById(R.id.mask_co_layout);	  
        maskView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(SearchSettingActivity.this,
						MaskCoActivity.class);
				startActivity(in);
			  }
		  });

        final ToggleButton zhaopin_btn = (ToggleButton) findViewById(R.id.zhaopin_toggleButton);
        zhaopin_btn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  // TODO Auto-generated method stub
				  bFlagZhaopin = !bFlagZhaopin;
				  zhaopin_btn.setChecked(bFlagZhaopin);
			  }
		  });

        final Button btn_qiuzhi_switch = (Button) findViewById(R.id.btn_qiuzhi_switch);	  
        btn_qiuzhi_switch.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
				  if( bFlagQiuzhi == false )
				  {
					  btn_qiuzhi_switch.setBackgroundResource(R.drawable.btn_anniu_b);
				  }
				  else
				  {
					  btn_qiuzhi_switch.setBackgroundResource(R.drawable.btn_anniu_a);
				  }
				  bFlagQiuzhi = !bFlagQiuzhi;

			  }
		  });
        final ToggleButton hehuo_btn = (ToggleButton) findViewById(R.id.hehuo_toggleButton);
        hehuo_btn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  // TODO Auto-generated method stub
				  bFlagHehuo = !bFlagHehuo;
				  hehuo_btn.setChecked(bFlagHehuo);
			  }
		  });
        final ToggleButton lianxiren_btn = (ToggleButton) findViewById(R.id.lianxiren_toggleButton);
        lianxiren_btn.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  // TODO Auto-generated method stub
				  bFlagLianxiren = !bFlagLianxiren;
				  lianxiren_btn.setChecked(bFlagLianxiren);
			  }
		  });
		  final ToggleButton gongsi_btn = (ToggleButton) findViewById(R.id.gongsi_toggleButton);
		  gongsi_btn.setOnClickListener(new View.OnClickListener() {
				  @Override
				  public void onClick(View v) {
					  // TODO Auto-generated method stub
					  bFlagGongsi = !bFlagGongsi;
					  gongsi_btn.setChecked(bFlagGongsi);
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