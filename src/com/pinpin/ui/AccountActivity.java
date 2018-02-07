package com.pinpin.ui;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.pinpin.R;
import com.pinpin.constants.Address;
import com.pinpin.constants.Constants;
import com.pinpin.network.HttpTask.TaskResultListener;
import com.pinpin.network.HttpUtils;
import com.pinpin.network.HttpUtils.RequestData;
import com.pinpin.network.HttpUtils.ResposneBundle;
import com.pinpin.utils.Log;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends BaseActivity {
	TextView content_birthday;
	EditText content_name;
	RelativeLayout rootView;
	String genderResult;
	String jobResult;
	Button btn_quit;
	Button btn_deleteUser;
	protected int gender = 0;
	protected int lookFor = 0;
	String birthday = null;
	String birthdate;
	int[] gender_ids = { R.id.btn_gender_male, R.id.btn_gender_female };
	int[] job_ids = { R.id.btn_choose1, R.id.btn_choose2, R.id.btn_choose3 };
	Bundle data;

	private class Datelistener implements OnDateSetListener {

		@Override
		public void onDateSet(DatePicker arg0, int y, int m, int d) {
			// TODO Auto-generated method stub
			// 调用完日历控件点完成后干的事
			String birthday = String.format("%04d-%02d-%02d", y, m + 1, d);
			content_birthday.setText(birthday);
			// content_birthday.setText(y+"-"+(m+1)+'-'+d);
		}
	}

	class GenderOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			toggleButton((Button) v, genderResult, gender_ids, 0);
		}
	}

	class JobOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			toggleButton((Button) v, jobResult, job_ids, 1);
		}
	}

	void initGender() {
		for (int i : gender_ids) {
			findViewById(i).setOnClickListener(new GenderOnClickListener());
		}

	}

	void initJob() {
		// for (int i : job_ids) {
		// findViewById(i).setOnClickListener(new JobOnClickListener());
		// }

	}

	void toggleButton(Button v, String value, int[] ids, int type) {
		if (TextUtils.equals(value, v.getText().toString())) {
//			v.setBackgroundResource(R.drawable.btn_sekuai1);
			genderResult = null;
			jobResult = null;
		} else {
			v.setBackgroundResource(R.drawable.btn_sekuai2);
			if (0 == type) {
				genderResult = v.getText().toString();
			} else {
				jobResult = v.getText().toString();
			}
		}
		for (int i : ids) {
			if (v.getId() == i) {
				continue;
			}
			rootView.findViewById(i).setBackgroundResource(R.drawable.btn_sekuai1);
		}
	}

	@Override
	protected void onConentViewLoad(ViewGroup container) {
		// TODO Auto-generated method stub
		setTitle("账号信息");

		setBackBtnVisibility();
		setSearchBtnGone();
		setFooterGone();
		setNextBtnVisibility();
		next_btn.setText("确定");

		data = getIntent().getBundleExtra("data");
		// inflater.inflate(R.layout.activity_account, container);
		rootView = (RelativeLayout) inflater.inflate(R.layout.activity_account, container);
		content_birthday = (TextView) findViewById(R.id.content_birthday);

		content_birthday = (TextView) findViewById(R.id.content_birthday);

		content_name = (EditText) findViewById(R.id.content_name);
		content_name.addTextChangedListener(new EditChangedListener());
		content_name.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				content_name.setCursorVisible(true);
			}

		});

		btn_quit = (Button) rootView.findViewById(R.id.btn_quit);
		btn_deleteUser = (Button) rootView.findViewById(R.id.btn_deleteUser);
		initGender();
		initJob();
		content_birthday.setOnClickListener(new View.OnClickListener() {
			int year;
			int month;
			int day;

			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(birthdate)) {
					String[] birth = birthdate.split("-");
					year = Integer.parseInt(birth[0]);
					month = Integer.parseInt(birth[1]) - 1;
					day = Integer.parseInt(birth[2]);
				} else {
					year = 1990;
					month = 1;
					day = 1;
				}
				new DatePickerDialog(AccountActivity.this, new Datelistener(), year, month, day).show();
			}
		});
		// LinearLayout rootView = (LinearLayout)
		// inflater.inflate(R.layout.activity_setting, null);
		/*
		 * RelativeLayout changePhoneView = (RelativeLayout)
		 * rootView.findViewById(R.id.phone_number_layout);
		 * changePhoneView.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent in = new Intent(AccountInitActivity.this,
		 * ChangePhoneActivity.class); startActivity(in); } });
		 */
		/*
		 * RelativeLayout changePasswordView = (RelativeLayout)
		 * rootView.findViewById(R.id.password_layout);
		 * changePasswordView.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent in = new Intent(AccountInitActivity.this,
		 * ChangePasswordActivity.class); startActivity(in); } });
		 */

		HashMap<String, String> data = new HashMap<String, String>() {
			{
				String phoneCode = getUsername();
				put("phoneCode", phoneCode);
				put("token", Constants.TOKEN);
			}
		};
		RequestData request = HttpUtils.simplePostData(Address.HOST + Address.GET_USER, data);
		startHttpTask(new TaskResultListener() {

			@Override
			public void result(ResposneBundle b) {
				// TODO Auto-generated method stub
				Log.e("result", b.getContent());

				try {
					JSONObject job = new JSONObject(b.getContent());
					if (job.getInt("code") == -1) {
						showToast(job.getString("msg"));
					} else {
						JSONObject data = job.getJSONObject("data");
						gender = data.getInt("gender");
						Button btn_Select;
						if (gender == 0) {
							btn_Select = (Button) findViewById(R.id.btn_gender_male);
							btn_Select.setBackgroundResource(R.drawable.btn_sekuai2);
							genderResult = "男";
						} else {
							btn_Select = (Button) findViewById(R.id.btn_gender_female);
							btn_Select.setBackgroundResource(R.drawable.btn_sekuai2);
							genderResult = "女";
						}

						JSONObject career = data.getJSONObject("career");
						lookFor = career.getInt("purpose");
						if (lookFor == 0) {
							btn_Select = (Button) findViewById(R.id.btn_choose1);
							btn_Select.setBackgroundResource(R.drawable.btn_sekuai2);
							jobResult = "找工作";
						} else if (lookFor == 1) {
							btn_Select = (Button) findViewById(R.id.btn_choose2);
							btn_Select.setBackgroundResource(R.drawable.btn_sekuai2);
							jobResult = "招牛人";
						} else {
							btn_Select = (Button) findViewById(R.id.btn_choose3);
							btn_Select.setBackgroundResource(R.drawable.btn_sekuai2);
							jobResult = "求合伙";
						}

						birthdate = data.getString("birthdate");
						content_birthday.setText(birthdate);
						SharedPreferences birth = getSharedPreferences("Birthday", MODE_PRIVATE);
						SharedPreferences.Editor editor = birth.edit();
						editor.putString("Birthday", birthday);
						String username = data.getString("username");
						content_name.setText(username);
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

		RelativeLayout passwordView = (RelativeLayout) rootView.findViewById(R.id.password_layout);
		passwordView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(AccountActivity.this, ChangePasswordActivity.class);
				startActivity(in);
			}
		});
		// phone_number_layout
		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});

		btn_deleteUser.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Constants.AVATAR_PATH = null;
				if (Constants.AVATAR_PATH != null) {
					File file = new File(Constants.AVATAR_PATH);
					if (file.isFile() && file.exists()) {
						file.delete();
						Constants.AVATAR_PATH = null;
					}
				}
				showAlertDialogWithCancel("提醒", "您要删除当前账号吗？删除后您账号下的所有信息都将被清除，无法找回！！！",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

						HashMap<String, String> data = new HashMap<String, String>() {
							{
								String phoneCode = getUsername();
								put("phoneCode", phoneCode);
								put("token", Constants.TOKEN);

							}
						};
						RequestData request = HttpUtils.simplePostData(Address.HOST + Address.DELETE_USER, data);
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
										setResult(Activity.RESULT_OK);
										finish();
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
				}, "确定");
			}
		});

		/*
		 * btn_deleteUser.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 * 
		 * HashMap<String,String> data = new HashMap<String,String>(){{ String
		 * phoneCode = getUsername(); put("phoneCode",phoneCode);
		 * put("token",Constants.TOKEN);
		 * 
		 * }}; RequestData request =
		 * HttpUtils.simplePostData(Address.HOST+Address.DELETE_USER, data);
		 * startHttpTask(new TaskResultListener(){
		 * 
		 * @Override public void result(ResposneBundle b) { // TODO
		 * Auto-generated method stub Log.e("result",b.getContent());
		 * if(b.getContent()==null){ showToast("出错了，服务器异常"); return; } try {
		 * JSONObject job = new JSONObject(b.getContent());
		 * if(job.getInt("code")==-1 ){ showToast(job.getString("msg")); }else{
		 * setResult(Activity.RESULT_OK);
		 * 
		 * finish(); } } catch (JSONException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 * 
		 * }
		 * 
		 * @Override public void failed(final String message) { // TODO
		 * Auto-generated method stub runOnUiThread(new Runnable() { public void
		 * run() { showToast(message) ; } }); }}, request);
		 * 
		 * } });
		 */
		btn_quit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Constants.AVATAR_PATH = null;
				HashMap<String, String> data = new HashMap<String, String>() {
					{
						String phoneCode = getUsername();
						put("phoneCode", phoneCode);
						put("token", Constants.TOKEN);

					}
				};
				RequestData request = HttpUtils.simplePostData(Address.HOST + Address.LOGOUT, data);
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
								// setResult(Activity.RESULT_OK);
								/*
								 * Intent intent = new Intent(
								 * AccountActivity.this, LoginActivity.class);
								 * startActivity(intent);
								 * overridePendingTransition
								 * (android.R.anim.slide_in_left,
								 * android.R.anim.slide_out_right);
								 */
								// finish();
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

				EMChatManager.getInstance().logout(true, new EMCallBack() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						setResult(Activity.RESULT_OK);
						finish();
					}

					@Override
					public void onProgress(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});

			}
		});
		next_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			/*
			 * public void onClick(View v) { // TODO Auto-generated method stub
			 * try{
			 * 
			 * String birthday = content_birthday.getText().toString(); String
			 * username = content_name.getText().toString();
			 * if(TextUtils.isEmpty(username)){ showToast("请您输入显示名"); return; }
			 * if(TextUtils.isEmpty(birthday)){ showToast("请您选择生日"); return; }
			 * if(TextUtils.isEmpty(password)){ showToast("请您输入密码"); return;
			 * }else if(password.length()<8){ showToast("密码长度至少为8位"); return;
			 * }else if(!CommonUtils.isValidPassword(password)){
			 * showToast("您输入密码格式不正确，请包涵数字、大小写字母"); return; }else
			 * if(!TextUtils.equals(password, repassword)){
			 * showToast("两次密码输入不一致"); return; }
			 * data.putString(content_name.getTag().toString(), username);
			 * data.putString(content_birthday.getTag().toString(), birthday);
			 * 
			 * if(TextUtils.equals("男", genderResult)){ data.putString("gender",
			 * "0"); }else if(TextUtils.equals("女", genderResult)){
			 * data.putString("gender", "1"); }else{ showToast("请选择性别"); return;
			 * } if(TextUtils.equals("找工作", jobResult)){
			 * data.putString("purpose", "0"); }else if(TextUtils.equals("招牛人",
			 * jobResult)){ data.putString("purpose", "1"); }else
			 * if(TextUtils.equals("求合伙", jobResult)){ data.putString("purpose",
			 * "2"); }else{ showToast("请选择我要做什么"); return; }
			 * 
			 * Intent intent = new Intent( AccountActivity.this,
			 * SettingInitActivity.class); intent.putExtra("data", data);
			 * startActivity(intent); }catch(Exception e){ e.printStackTrace();
			 * }
			 * 
			 * 
			 * }
			 */
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// *****测试
				// Intent intent = new Intent(
				// SettingActivity.this,
				// MainActivity.class);
				// startActivity(intent);
				// finish();
				// int a = 1;
				// if(a==1){
				// return;
				// }
				//

				// ******测试end
				if (TextUtils.equals("男", genderResult)) {
					gender = 0;
				} else if (TextUtils.equals("女", genderResult)) {
					gender = 1;
				} else {
					showToast("请选择性别");
					return;
				}

				if (TextUtils.equals("找工作", jobResult)) {
					lookFor = 0;
				} else if (TextUtils.equals("招牛人", jobResult)) {
					lookFor = 1;
				} else if (TextUtils.equals("求合伙", jobResult)) {
					lookFor = 2;
				} else {
					showToast("请选择我要做什么");
					return;
				}
				String birthday = content_birthday.getText().toString();
				String username = content_name.getText().toString();
				String digits = " /\\:*?<>|\"\n\t";

				for (int i = 0; i < username.length(); i++) {
					if (digits.indexOf(username.charAt(i)) > 0) {
						showToast("不能有特殊符号");
						return;
					}
				}
				if (TextUtils.isEmpty(username) || username.trim().isEmpty()) {
					showToast("请您输入显示名");
					return;
				} else {
					username = username.replace(" ", "");
				}
				if (TextUtils.isEmpty(birthday)) {
					showToast("请您选择生日");
					return;
				}

				HashMap<String, String> data = new HashMap<String, String>() {
					{
						String phoneCode = getUsername();
						put("phoneCode", phoneCode);
						// put("phoneCode",Constants.phoneCode);
						put("token", Constants.TOKEN);
						put("gender", Integer.toString(gender));
						put("purpose", Integer.toString(lookFor));
						put("username", content_name.getText().toString());
						put("birthdate", content_birthday.getText().toString());

					}
				};
				RequestData request = HttpUtils.simplePostData(Address.HOST + Address.UPDATE_USER, data);
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
							showToast(job.getString("msg"));
							if (job.getInt("code") == -1) {
								showToast(job.getString("msg"));
							} else {
								finish();
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
				Constants.NICKNAME = content_name.getText().toString();
			}
		});
	}

	class EditChangedListener implements TextWatcher {
		private CharSequence temp;// 监听前的文本
		private int editStart;// 光标开始位置
		private int editEnd;// 光标结束位置
		private final int charMaxNum = 10;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			String content = content_name.getText().toString();

		}

		@Override
		public void afterTextChanged(Editable arg0) {

			/** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
			editStart = content_name.getSelectionStart();
			editEnd = content_name.getSelectionEnd();
			if (temp.length() > charMaxNum) {
				Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制,最多输入10个字", Toast.LENGTH_LONG).show();
				arg0.delete(editStart - 1, editEnd);
				int tempSelection = editStart;
				content_name.setText(arg0);
				content_name.setSelection(tempSelection);
			}

		}
	};
}
