package com.pinpin.ui;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinpin.R;
import com.pinpin.constants.Constants;
import com.pinpin.model.ChatListModel;
import com.pinpin.utils.ImageUtils;
import com.pinpin.view.CircleImageView;

@SuppressLint("ValidFragment")
public class MeFragment extends Fragment {
	public static final int RELOGIN = 888;
	ImageView person_image;
	ListView newsListView;
	List<ChatListModel> mNewsList;
	String column;
	TextView nickname;

	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final MainActivity parentActivity = (MainActivity ) getActivity();
//        LinearLayout rootView = new LinearLayout(getActivity());
//	     rootView.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
//	     rootView.setBackgroundColor(Color.BLUE);
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.me_fragment, null);
        person_image = (ImageView) rootView.findViewById(R.id.person_image);
        
          nickname = (TextView) rootView.findViewById(R.id.nickname);
          if(Constants.NICKNAME.isEmpty() || Constants.NICKNAME.equals("null")){
        	  nickname.setText("正在获取中...");
          }else{
        	  nickname.setText(Constants.NICKNAME);
          }
    
        RelativeLayout personalView = (RelativeLayout) rootView.findViewById(R.id.personal_layout);	  
        personalView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(),PersonalActivity.class);
				startActivity(in);
			  }
		  });
        RelativeLayout settingView = (RelativeLayout) rootView.findViewById(R.id.setting_layout);	  
		 settingView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(),SettingActivity.class);
				startActivity(in);
			  }
		  });
		RelativeLayout accountView = (RelativeLayout) rootView.findViewById(R.id.account_layout);	  
		accountView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(),AccountActivity.class);
				getActivity().startActivityForResult(in, RELOGIN);
			  }
		  });
		RelativeLayout suggestionView = (RelativeLayout) rootView.findViewById(R.id.suggestion_layout);	  
		suggestionView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(),SuggestionActivity.class);
				startActivity(in);
			  }
		  });
		RelativeLayout aboutView = (RelativeLayout) rootView.findViewById(R.id.about_layout);	  
		aboutView.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(),AboutActivity.class);
				startActivity(in);
			  }
		  });
		Log.e("Me!!!!!!!","onResume");
		// TODO Auto-generated method stub
		//MainActivity parentActivity = (MainActivity ) getActivity();
		//parentActivity.showHeader();
		if(!TextUtils.isEmpty(Constants.AVATAR_PATH)){
        	File path = new File(Constants.AVATAR_PATH);
        	if(path.exists())
        	{
        		 
        		 ((CircleImageView) person_image).createCircle(ImageUtils.getBitmapFromFile(path,
 						600, 600));
        	}else{
        		person_image.setImageResource(R.drawable.default_avatar);
			}
        }else{
        	person_image.setImageResource(R.drawable.default_avatar);
		}
		if(!TextUtils.isEmpty(Constants.NICKNAME)){
			nickname.setText(Constants.NICKNAME);
		}else{
			nickname.setText("获取中...");
		}
	     return rootView;
	  }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
			return;
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		Log.e("Me!!!!!!!", "onResume");
		// TODO Auto-generated method stub
		// MainActivity parentActivity = (MainActivity ) getActivity();
		// parentActivity.showHeader();
		if (!TextUtils.isEmpty(Constants.AVATAR_PATH)) {
			File path = new File(Constants.AVATAR_PATH);
			if (path.exists()) {

				((CircleImageView) person_image).createCircle(ImageUtils.getBitmapFromFile(path, 600, 600));
			} else {
				person_image.setImageResource(R.drawable.default_avatar);
			}
		} else {
			person_image.setImageResource(R.drawable.default_avatar);
		}
		if (!TextUtils.isEmpty(Constants.NICKNAME)) {
			nickname.setText(Constants.NICKNAME);
		} else {
			nickname.setText("获取中...");
		}

		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(Constants.ACCOUNT_REMOVED, true);
		}
	}
}
