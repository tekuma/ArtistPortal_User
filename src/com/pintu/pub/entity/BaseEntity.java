package com.pintu.pub.entity;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
	private int result;
	private Long myrownum;

	public int getResult() {
		return result;
	}

	public void setResult(int v_result) {
		result = v_result;
	}

	public Long getMyrownum() {
		return myrownum;
	}

	public void setMyrownum(Long myrownum) {
		this.myrownum = myrownum;
	}
	
}
