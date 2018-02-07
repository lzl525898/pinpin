package com.pinpin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class CustLinearLayout extends ScrollView  {
	public interface OnItemClickListener{
		void onItemClick(int position);
	}
	public interface OnItemLongClickListener{
		boolean onItemLongClick(int position);
	}
	LinearLayout ll;
	OnItemClickListener mOnItemClickListener;
	OnItemLongClickListener mOnItemLongClickListener;
	 
	BaseAdapter adapter ;
	public CustLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public CustLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
		  
		// TODO Auto-generated constructor stub
	}

	public CustLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		// TODO Auto-generated constructor stub
	}
	private void initView(){
		ll = new LinearLayout(getContext());
		ll.setOrientation(LinearLayout.VERTICAL);
		addView(ll,new ViewGroup.LayoutParams(-1,-1));
	}
	public void setAdapter(BaseAdapter adapter){
		this.adapter = adapter;
		bindView();
	}
	
	public void setOnItemClickListener(OnItemClickListener l){
		this.mOnItemClickListener = l;
		
	}

	public void setOnItemLongClickListener( OnItemLongClickListener l){
		this.mOnItemLongClickListener = l;
	}
	public void refresh(){
		ll.removeAllViews();
		bindView();
	}
	private void bindView(){
		
		for(int i = 0 ; i < adapter.getCount();i++){
			View v = adapter.getView(i, null, null);
			if(mOnItemClickListener!=null){
				v.setTag(i);
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mOnItemClickListener.onItemClick((Integer) v.getTag());
					}
				});
			}
			if(mOnItemLongClickListener!=null){
				v.setTag(i);
				v.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						mOnItemLongClickListener.onItemLongClick((Integer) v.getTag());
						return true;
					}
				});
			}
			ll.addView(v);
		}
	}
}
