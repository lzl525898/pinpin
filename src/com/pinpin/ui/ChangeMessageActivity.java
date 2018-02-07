package com.pinpin.ui;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pinpin.R;

public class ChangeMessageActivity extends BaseActivity {


	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		String title = intent.getStringExtra("title").toString().trim();
		setTitle(title);
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		setNextBtnVisibility();
		next_btn.setText("保存");
		//EditText textView = (EditText) findViewById(R.id.message_txt);
		//String message = "输入" + title + "名称";
		//textView.setHint(message);
		 
		inflater.inflate(R.layout.activity_change_message, container);
		//RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.activity_change_phone, container);

		next_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText editText = (EditText)findViewById(R.id.message_txt);
				String result = editText.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("result", result);
                /*
                 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
                 */
                setResult(1001, intent);
				finish();
				
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