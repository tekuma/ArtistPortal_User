package com.pintu.system.entity;


import com.pintu.pub.entity.BaseEntity;

@SuppressWarnings("serial")
public class User extends BaseEntity {
	
	private Integer id;//编号
	private String loginnmae;//登陆名称
	private String loginpwd;//登陆密码
	private String username;//用户姓名
	
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLoginnmae() {
		return loginnmae;
	}
	public void setLoginnmae(String loginnmae) {
		this.loginnmae = loginnmae;
	}
	public String getLoginpwd() {
		return loginpwd;
	}
	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}


	
}
