package com.pinpin.model;

import java.io.File;

import com.pinpin.R;


 
public class UserInfo {
	public String id  ;
	public String salary = "11";
	
	public String position = "11";
	 
	public String nickname ="小猪";
	
	public String json  ;
	
	public int drawable = R.drawable.splash;
	
	public File path  ;
	
	public String url  ;
	
	public String[] tags  ;

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", salary=" + salary + ", position="
				+ position + ", nickname=" + nickname + ", json=" + json
				+ ", drawable=" + drawable + ", path=" + path + ", url=" + url
				+ "]";
	}
	
	

}
