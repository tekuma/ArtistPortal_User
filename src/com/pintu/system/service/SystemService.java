package com.pintu.system.service;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pintu.server.comm.PublicJdbcDao;
import com.pintu.system.entity.Code;
import com.pintu.system.entity.Collection;
import com.pintu.system.entity.Member;
import com.pintu.system.entity.Pool;

@SuppressWarnings("unchecked")
@Service(value = "loginService")
public class SystemService{

	@Resource
	private PublicJdbcDao publicJdbcDao;
	public PublicJdbcDao getPublicJdbcDao() {
		return publicJdbcDao;
	}
	public void setPublicJdbcDao(PublicJdbcDao publicJdbcDao) {
		this.publicJdbcDao = publicJdbcDao;
	}
	
	/**
	 * 查询文件服务器地址
	 * @return
	 */
	public String findFileUrl(){
		String sql="select csz from sys_param_t where bh=3";
		List<String> list=this.getPublicJdbcDao().getList(sql);
		return list.get(0);
	}
	/**
	 * 根据用户id查询pool作品集
	 * @return气昂昂
	 */
	public List<Pool> findPoolByUserId(Integer userId,Integer page){
		return (List<Pool>) this.publicJdbcDao
				.getList(
						"select ID,MemberID,Title,createTime,EntryLabel,Description,UploadTime,storeaddress from tk_pool_t where MemberID="
								+ userId+" order by UploadTime desc limit "+page*9+",9", Pool.class);
	}
	
	/**
	 * 查询添加收藏作品时的作品列表
	 * @param userId
	 * @return
	 */
	public List<Pool> findAddCollectionList(Integer userId){
		String sql="select ID,MemberID,Title,CreateTime,EntryLabel,Description,UploadTime,storeaddress from tk_pool_t t1 where t1.memberid="+userId+" and t1.id not in (select poolid from tk_relevancy_t)";
		List<Pool> pools=new ArrayList<Pool>();
		pools=this.getPublicJdbcDao().getList(sql, Pool.class);
		return pools;
	}
	
	/**
	 * 查询修改收藏作品时的作品列表
	 * @param userId
	 * @return
	 */
	public List<Pool> findUpdateCollectionList(Integer userId,Integer collectionId){
		String sql="select ID,MemberID,Title,CreateTime,EntryLabel,Description,UploadTime,storeaddress from tk_pool_t t1 where t1.memberid="+userId+" and t1.id not in (select poolid from tk_relevancy_t where collectionid not in("+collectionId+"))";
		List<Pool> pools=new ArrayList<Pool>();
		pools=this.getPublicJdbcDao().getList(sql, Pool.class);
		return pools;
	}
	
	/**
	 * 根据用户id查询collection作品集
	 * @return
	 */
	public List<Pool> findCollectionByUserId(Integer userId){
		return this.publicJdbcDao
				.getList("select t1.id,t1.title,t1.storeaddress,t1.createTime,t1.entryLabel,t1.description , t1.psort psort,"
						+ "t2.id relevancyid,t3.id collectionid,t3.CollectionTitle collectionTitle,t3.PrintQuantity printquantity,t3.DescriptionOf descriptionof,t3.Categories Categories,t3.Styles Styles,t3.Subject Subject,t3.color color,t3.clabel clabel from"
						+ " tk_pool_t t1 , tk_relevancy_t t2 , tk_collection_t t3 where t1.MemberID="+userId
								+ " and t1.id=t2.POOLID and t2.COLLECTIONID=t3.id order by t3.createtime desc,t3.id,t1.psort",Pool.class);
		
	}
	
	/**
	 * 根据用户id查询添加collection时的作品集
	 * @return
	 */
	public List<Pool> findPoolsForAddCollection(Integer userId){
		return this.publicJdbcDao
				.getList("select t1.id id,t1.storeaddress storeaddress,t2.id relevancyid,t3.id collectionid,t3.CollectionTitle collectionTitle,t3.PrintQuantity printquantity,t3.DescriptionOf descriptionof,t3.Categories Categories,t3.Styles Styles,t3.Subject Subject,t3.color color from"
						+ " tk_pool_t t1 left join tk_relevancy_t t2 on t1.id=t2.POOLID left join tk_collection_t t3 on t2.COLLECTIONID=t3.id"
						+ " where t1.MemberID="+userId+" order by t3.id",Pool.class);
		
	}
	
	/**
	 * 删除并建立关系
	 * @param collectionId
	 * @return
	 */
	public void createRelevancy(Integer collectionId,String[] poolIds,boolean isDel){
		this.getPublicJdbcDao().executeSQL("delete from tk_relevancy_t where COLLECTIONID="+collectionId);
		for(String poolId : poolIds){
			if(poolId!=null&&poolId!=""){
				String sql="insert into tk_relevancy_t (POOLID,COLLECTIONID) values ("+Integer.valueOf(poolId)+","+collectionId+");";
				this.getPublicJdbcDao().executeSQL(sql);
			}
		}
	}
	
	/**
	 * 修改或添加收藏信息
	 * @param collectionId
	 */
	public void collectioncreateCollection(Collection collection,boolean isSave){
		if(isSave){//修改
			this.getPublicJdbcDao().executeMYSQLDB("update", "tk_collection_t", collection, "id");
		}else{//保存
			this.getPublicJdbcDao().executeMYSQLDB("save", "tk_collection_t", collection, "id");
		}
	}
	
	/**
	 * 查找用户信息根据用户名
	 * @param id
	 * @return
	 */
	public Member findMemberByLoginName(String loginName){
		String sql="select id,loginname,loginpwd,lastname,firstname,birthday,telephone,email,avatarpath,introduction,membertype,createtime,bank,bankaccount,payee,location,nationality,bio,website,gender from tk_member_t where LoginName='"+loginName+"'";
		List<Member> members=this.getPublicJdbcDao().getList(sql,Member.class);
		Member member=new Member();
		if(members!=null&&members.size()>0){
			member = members.get(0);
		}
		return member;
	}
	
	/**
	 * 该用户名是否存在
	 * @param loginName
	 * @return
	 */
	public boolean isExistMemberById(String loginName){
		Integer index=this.getPublicJdbcDao().getTempInt("select count(id) from tk_member_t where loginname='"+loginName+"'", 0);
		boolean flag=true;
		if(index==0){
			flag=false;
		}
		return flag;
		
	}
	
	/**
	 * 邀请码是否存在
	 * @param registCode
	 * @return
	 */
	public boolean verifyRgistCode(String registCode){
		String sql="select count(number) from tk_code_t where number='"+registCode+"'";
		Integer index=this.getPublicJdbcDao().getTempInt(sql, 0);
		boolean flag=true;
		if(index==0){
			flag=false;
		}
		return flag;
	}
	
	//邀请码是否存在
	public Code verifyRgistCodeconfig(String registCode){
		String sql="select EMAIL from tk_code_t where number='"+registCode+"'";
		List<Code> count=this.getPublicJdbcDao().getList(sql);
		if(count!=null&&count.size()>0){
			return count.get(0);
		}else{
			return null;
		}
		
	}
	
	//修改头像路径
	public void saveUserHead(Integer id,String url){
		this.getPublicJdbcDao().executeSQL("update tk_member_t set AvatarPath="+url+" where id="+id);
	}
	
	
	//保存支付宝账号
	public boolean saveAccount(Integer memberid,String account){
		String sql="select count(1) from tk_member_t where id<>'"+memberid+"' and bankaccount='"+account+"'";
		int count=this.publicJdbcDao.getTempInt(sql, 0);
		if(count>0){
			return false;
		}else{
			String SQL="update tk_member_t set bankaccount='"+account+"' where id="+memberid;
			return this.publicJdbcDao.executeSQL(SQL);
		}
	}
	

	
	
	
	
}
