package com.pintu.server.comm;

import com.pintu.pub.dao.impl.BaseJdbcDao;


public class PublicJdbcDao extends BaseJdbcDao {
    //在此定义系统中各公共常量
	public static final String THERMALSYSADMIN = "system";//定义系统管理员名常量
	private String tempExcelExpPath;
	
	public String getTempExcelExpPath() {
		return tempExcelExpPath;
	}
	public void setTempExcelExpPath(String tempExcelExpPath) {
		this.tempExcelExpPath = tempExcelExpPath;
	}
}
