package com.pinpin.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.pinpin.R;
import com.pinpin.model.UserInfo;
import com.pinpin.utils.ImageUtils;
import com.pinpin.utils.Log;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PinpinItemAdapter extends BaseAdapter {
	CopyOnWriteArrayList<UserInfo> list;
	LayoutInflater inflater;

	public PinpinItemAdapter(Context ctx, CopyOnWriteArrayList<UserInfo> list) {
		super();
		this.list = list;
		this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public UserInfo getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item, null);
			holder.user_imageview = (ImageView) convertView.findViewById(R.id.user_imageview);
			holder.user_info_textview = (TextView) convertView.findViewById(R.id.user_info_textview);
			holder.user_name_textview = (TextView) convertView.findViewById(R.id.user_name_textview);
			holder.user_year_textview = (TextView) convertView.findViewById(R.id.user_year_textview);
			holder.user_age_textview = (TextView) convertView.findViewById(R.id.user_age_textview);
			holder.user_position_textview = (TextView) convertView.findViewById(R.id.user_position_textview);
			holder.tag1 = (TextView) convertView.findViewById(R.id.pinpin_tag_1);
			holder.tag2 = (TextView) convertView.findViewById(R.id.pinpin_tag_2);
			holder.tag3 = (TextView) convertView.findViewById(R.id.pinpin_tag_3);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserInfo info = list.get(position);

		if (info.path != null && info.path.exists()) {
			holder.user_imageview.setImageBitmap(ImageUtils.getBitmapFromFile(info.path, 400, 600));
		} else {
			holder.user_imageview.setImageResource(R.drawable.default_avatar);
		}
		try {
			JSONObject all = new JSONObject(info.json);
			String birthDateString = all.getString("birthdate");
			String age = getPersonAgeByBirthdate(birthDateString);
			JSONObject data = all.getJSONObject("career");
			String year = data.getString("years");
			String salary = data.getString("salary");

			if (!(salary.equals("null") || salary.isEmpty())) {
				holder.user_position_textview.setText("期望薪资：" + salary);
			} else {
				holder.user_position_textview.setText("期望薪资：无");
			}
			if (year == "无" || year == null || year.isEmpty() || year.equals("null")) {
				holder.user_year_textview.setText("");
			} else {
				holder.user_year_textview.setText("" + year + "工作经验");
			}

			if (!age.equals("null") && !age.isEmpty()) {
				holder.user_age_textview.setText(age + "岁");
			} else {
				holder.user_age_textview.setText("");
			}
			// if(all.getInt("age")==0){
			// holder.user_age_textview.setText("");
			// }else {
			// holder.user_age_textview.setText(""+all.getInt("age")+"岁");
			//// holder.user_age_textview.setText(""+age+"岁");
			// }
		} catch (JSONException e) {
			Log.e("lalalalalalal", "lalalalalalala");
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		holder.user_name_textview.setText(info.nickname);
		String positionName = "";
		if (info.salary.length() >= 4) {
			positionName = info.salary.substring(0, 3) + "...";
		} else {
			positionName = info.salary;
		}
		holder.user_info_textview.setText(positionName);

		for (int i = 0; i < 3; i++) {
			switch (i) {
			case 0:
				if (info.tags != null && info.tags.length > 0 && !TextUtils.isEmpty(info.tags[0])) {
					holder.tag1.setText(info.tags[0]);
					holder.tag1.setVisibility(View.VISIBLE);
				} else {
					holder.tag1.setVisibility(View.INVISIBLE);
				}
				break;
			case 1:
				if (info.tags != null && info.tags.length > 1 && !TextUtils.isEmpty(info.tags[1])) {
					holder.tag2.setText(info.tags[1]);
					holder.tag2.setVisibility(View.VISIBLE);
				} else {
					holder.tag2.setVisibility(View.INVISIBLE);
				}
				break;
			case 2:
				if (info.tags != null && info.tags.length > 2 && !TextUtils.isEmpty(info.tags[2])) {
					holder.tag3.setText(info.tags[2]);
					holder.tag3.setVisibility(View.VISIBLE);
				} else {
					holder.tag3.setVisibility(View.INVISIBLE);
				}
				break;
			}
		}

		return convertView;
	}

	private String getPersonAgeByBirthdate(String birthDateString) {
		// TODO Auto-generated method stub
		if (birthDateString.equals("null")) {
			return null;
		} else if (birthDateString.isEmpty()) {
			return null;
		}

		Date birthDate = new Date(birthDateString.replace('-', '/'));
		Date currentDate = new Date();
		if (currentDate.getTime() >= birthDate.getTime()) {
			long days = (currentDate.getTime() - birthDate.getTime()) / (1000 * 60 * 60 * 24);
			String age = Long.toString(days / 365);
			return age;
		} else {
			/* 年龄不能为负 */
			return null;
		}
	}

	public static class ViewHolder {
		ImageView user_imageview;
		TextView user_name_textview;
		TextView user_position_textview;
		TextView user_info_textview;
		TextView user_year_textview;
		TextView user_age_textview;
		TextView tag1;
		TextView tag2;
		TextView tag3;
	}
}
