package com.pintu.pub.dao;

import com.pintu.pub.entity.BaseEntity;
import com.pintu.pub.page.PageResult;

import java.util.List;

@SuppressWarnings("rawtypes")
public abstract interface BaseDao{
  
	public abstract List getList(String paramString);
	  
	public abstract List getList(String paramString, Class paramClass);
	
	public abstract PageResult getList(String paramString, Class paramClass, int paramInt1, int paramInt2);
	
	public abstract boolean executeSQL(String paramString);
	
	//public abstract int executeProc(String paramString, BaseEntity paramBaseEntity);
	  
	public abstract int executeMYSQLDB(String executeType,String DBName,BaseEntity baseEntity,String keyStr);
  
}
