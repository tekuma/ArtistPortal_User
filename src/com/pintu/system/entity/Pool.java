package com.pintu.system.entity;


import java.util.Date;

import com.pintu.pub.entity.BaseEntity;

@SuppressWarnings("serial")
public class Pool extends BaseEntity {
	
	private Integer id;//编号
	private Integer memberid;//会员编号
	private String title;//作品标题
	private String createtime;//创作年份
	private String entrylabel;//作品标签
	private String description;//作品描叙
	private Date uploadtime;//上传时间
	private String storeaddress;//文件存放地址
	
	private Integer relevancyid;//对应关系ID
	private Integer collectionid;//收藏夹ID
	private String collectiontitle;//收藏夹标题
	private String printquantity;
	private String descriptionof;
	private String categories;
	private String styles;
	private String subject;
	private String color;
	
	private String clabel;//收藏标签
	private String thumbnailurlac;//单个作品查看时的压缩图地址
	private Integer psort;//作品在收藏夹里的排序
	
	
	public Integer getPsort() {
		return psort;
	}
	public void setPsort(Integer psort) {
		this.psort = psort;
	}
	public String getClabel() {
		return clabel;
	}
	public void setClabel(String clabel) {
		this.clabel = clabel;
	}
	public String getThumbnailurlac() {
		return thumbnailurlac;
	}
	public void setThumbnailurlac(String thumbnailurlac) {
		this.thumbnailurlac = thumbnailurlac;
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
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
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
	private String thumbnailurl;//所有作品压缩图路径
	private String isSuccess;
	
	public String getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getEntrylabel() {
		return entrylabel;
	}
	public void setEntrylabel(String entrylabel) {
		this.entrylabel = entrylabel;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getUploadtime() {
		return uploadtime;
	}
	public void setUploadtime(Date uploadtime) {
		this.uploadtime = uploadtime;
	}
	public String getStoreaddress() {
		return storeaddress;
	}
	public void setStoreaddress(String storeaddress) {
		this.storeaddress = storeaddress;
	}
	public Integer getRelevancyid() {
		return relevancyid;
	}
	public void setRelevancyid(Integer relevancyid) {
		this.relevancyid = relevancyid;
	}
	public Integer getCollectionid() {
		return collectionid;
	}
	public void setCollectionid(Integer collectionid) {
		this.collectionid = collectionid;
	}
	public String getCollectiontitle() {
		return collectiontitle;
	}
	public void setCollectiontitle(String collectiontitle) {
		this.collectiontitle = collectiontitle;
	}
	public String getThumbnailurl() {
		return thumbnailurl;
	}
	public void setThumbnailurl(String thumbnailurl) {
		this.thumbnailurl = thumbnailurl;
	}
	
	
	
	
	
	
	
	
}
