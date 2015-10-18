package com.pintu.system.entity;


import java.util.Date;

import com.pintu.pub.entity.BaseEntity;

@SuppressWarnings("serial")
public class Code extends BaseEntity {
	
	private Integer id;
	private String number;
	private String email;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
	
	
}
