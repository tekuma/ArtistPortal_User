package com.pintu.system.entity;


import java.util.Date;
import com.pintu.pub.entity.BaseEntity;

@SuppressWarnings("serial")
public class Collection extends BaseEntity {
	
	private Integer id;//编号
	private Integer memberid;//会员编号
	private String collectiontitle;//收藏标题
	private String printquantity;//限量打印数目
	private String descriptionof;//收藏描叙
	private Date createtime;//注册时间
	
	private String categories;
	private String styles;
	private String subject;
	private String color;
	private String clabel;//收藏标签
	
	
	
	
	public String getClabel() {
		return clabel;
	}
	public void setClabel(String clabel) {
		this.clabel = clabel;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	public String getStyles() {
		return styles;
	}
	public void setStyles(String styles) {
		this.styles = styles;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMemberid() {
		return memberid;
	}
	public void setMemberid(Integer memberid) {
		this.memberid = memberid;
	}
	public String getCollectiontitle() {
		return collectiontitle;
	}
	public void setCollectiontitle(String collectiontitle) {
		this.collectiontitle = collectiontitle;
	}
	public String getPrintquantity() {
		return printquantity;
	}
	public void setPrintquantity(String printquantity) {
		this.printquantity = printquantity;
	}
	
	public String getDescriptionof() {
		return descriptionof;
	}
	public void setDescriptionof(String descriptionof) {
		this.descriptionof = descriptionof;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	
	
	
	
}
