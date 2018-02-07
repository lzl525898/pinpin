package com.pinpin.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinpin.R;
import com.pinpin.model.ChatListModel;

public class MaskItemAdapter extends BaseAdapter{
     List<String> list;
     LayoutInflater inflater;

	public MaskItemAdapter( Context ctx,List<String> list) {
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
	public String getItem(int position) {
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
		if(convertView==null){
			convertView = inflater.inflate(R.layout.item_mask, null);
		}
	 
		TextView name = (TextView) convertView.findViewById(R.id.btn_mask);
		 
		name.setText(list.get(position));
		 
		return convertView;
	}

	 

}
