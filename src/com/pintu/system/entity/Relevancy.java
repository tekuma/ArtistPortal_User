package com.pintu.system.entity;


import com.pintu.pub.entity.BaseEntity;

@SuppressWarnings("serial")
public class Relevancy extends BaseEntity {
	
	private Integer id;//编号
	private Integer poolid;//作品池编号
	private Integer collectionid;//	收藏表编号
	
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPoolid() {
		return poolid;
	}
	public void setPoolid(Integer poolid) {
		this.poolid = poolid;
	}
	public Integer getCollectionid() {
		return collectionid;
	}
	public void setCollectionid(Integer collectionid) {
		this.collectionid = collectionid;
	}
	
	
	
	
	
}
