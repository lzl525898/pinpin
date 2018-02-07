package com.pinpin.model;

import android.text.TextUtils;
import android.util.Log;

public class PPMessage {
	private String name;
	private String avatar_url;
	private String content;
	private String uid;
	private int drawable;
	private String time;
	public static enum TYPE {RECEIVE,SEND};
	public TYPE type;
	
	
	
	public PPMessage(TYPE type, int drawable, String content, String time) {
		super();
		this.type = type;
		this.drawable = drawable;
		this.content = content;
		this.time = time;
	}
	public PPMessage(int drawable, String content, String time) {
		super();
		this.drawable = drawable;
		 
		this.content = content;
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar_url() {
		return avatar_url;
	}
	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getDrawable() {
		return drawable;
	}
	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	 
	
	
}
