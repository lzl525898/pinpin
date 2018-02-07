package com.pinpin.model;

import java.io.File;

import com.pinpin.utils.Log;

public class ChatListModel {
	private String name;
	public String avatar_url;
	public String path;
	private String content;
	private String uid;
	private String hid;
	private int drawable;
	private long time;
	public String json;
	public String matchDate;
	private boolean needAgree;
	
	
	public ChatListModel() {
		super();
	}
	public ChatListModel(int drawable, String name, String content, long time) {
		super();
		this.drawable = drawable;
		this.name = name;
		this.content = content;
		this.time = time;
		 
	}
	public ChatListModel(int drawable, String name, String content, long time,String uid,String hid,boolean needAgree) {
		super();
		this.drawable = drawable;
		this.name = name;
		this.content = content;
		this.time = time;
		this.uid = uid;
		this.hid = hid;
		this.needAgree =needAgree;
	}
	public boolean isNeedAgree() {
		return needAgree;
	}

	public void setNeedAgree(boolean needAgree) {
		this.needAgree = needAgree;
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
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatListModel other = (ChatListModel) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
	 
	
	
}
