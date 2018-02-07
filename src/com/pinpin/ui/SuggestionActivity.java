package com.pinpin.ui;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.Log;

public class SuggestionActivity extends BaseActivity {

	// Button confirmBtn;
	EditText content_text;
	TextView maxtext;

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("意见反馈");
		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		setNextBtnVisibility();
		next_btn.setText("提交");
		inflater.inflate(R.layout.activity_suggestion, container);
maxtext=(TextView)findViewById(R.id.maxtext);
		// confirmBtn = (Button) findViewById(R.id.btn_shanchubeijing);
		content_text = (EditText) findViewById(R.id.message_txt);
		content_text.addTextChangedListener(new EditChangedListener());
		next_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 String inputName = content_text.getText().toString();
                 String digits = " /\\:*?<>|\"\n\t";
                 if(containsEmoji(inputName)){
						showToast("不支持输入Emoji表情符号");
						return;
					}
                 for (int i = 0; i < inputName.length(); i++) {
                 	  if (digits.indexOf(inputName.charAt(i)) > 0) {
                 		  showToast("不能有特殊符号");
                       	return;
                 	  }
                 	 }
                 if(TextUtils.isEmpty(inputName)||inputName.trim().isEmpty()){
                 	showToast("个性标签不能为空");
                 	return;
                 }else {
					HashMap<String, String> data = new HashMap<String, String>() {
						{
							String phoneCode = getUsername();
							put("phoneCode", phoneCode);
							put("token", Constants.TOKEN);
							put("content", content_text.getText().toString());

						}
					};
					RequestData request = HttpUtils.simplePostData(Address.HOST
							+ Address.SUGGESTION, data);
					startHttpTask(new TaskResultListener() {

						@Override
						public void result(ResposneBundle b) {
							// TODO Auto-generated method stub
							Log.e("result", b.getContent());
							if (b.getContent() == null) {
								showToast("出错了，服务器异常");
								return;
							}
							try {
								JSONObject job = new JSONObject(b.getContent());
								if (job.getInt("code") == -1) {
									showToast(job.getString("msg"));
								} else {
									showAlertDialog(
											"提示",
											"您提交的意见已经收到，感谢您对优聘的支持！",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													finish();
												}
											}, "确定");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void failed(final String message) {
							// TODO Auto-generated method stub
							runOnUiThread(new Runnable() {
								public void run() {
									showToast(message);
								}
							});
						}
					}, request);
				}
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

	class EditChangedListener implements TextWatcher {
		private CharSequence temp;// 监听前的文本
		private int editStart;// 光标开始位置
		private int editEnd;// 光标结束位置
		private final int charMaxNum = 100;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String content = content_text.getText().toString();  
	        maxtext.setText(content.length() + "/"  
	                + charMaxNum); 
		}

		@Override
		public void afterTextChanged(Editable arg0) {

			/** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
			editStart = content_text.getSelectionStart();
			editEnd = content_text.getSelectionEnd();
			if (temp.length() > charMaxNum) {
				Toast.makeText(getApplicationContext(),
						"你输入的字数已经超过了限制,最多输入100个字", Toast.LENGTH_LONG).show();
				arg0.delete(editStart - 1, editEnd);
				int tempSelection = editStart;
				content_text.setText(arg0);
				content_text.setSelection(tempSelection);
			}

		}
	};

}
