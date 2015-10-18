package com.pintu.server.comm;

import java.util.ArrayList;

import java.util.List;

@SuppressWarnings("unchecked")
public class PublicUilt {
	/**
	 * 查询系统功能
	 */
	/*public static List<Functionlist> findPublicXtgn(String gnlb,User user,PublicJdbcDao psychologyJdbcDao){
		String sql="";
		if (user==null){
			sql="select * from sys_functionlist_t where gnfl in("+gnlb+") order by gnfl,gnxh ";
		}else{
			if(user.getDlmc()!=null&&!user.getDlmc().equals("")&&user.getDlmc().equals(psychologyJdbcDao.THERMALSYSADMIN)){
				sql="select * from sys_functionlist_t where gnfl in("+gnlb+") order by gnfl,gnxh ";
			}else{
				if(user.getGnqx()!=null&&!user.getGnqx().equals("")){
					sql="select * from sys_functionlist_t where gnfl in("+gnlb+") and bh in("+user.getGnqx()+") order by gnlb,gnxh ";
				}else{
					sql="select * from sys_functionlist_t where 1=0 ";
				}
				
			}
		}
		List<Functionlist> functionlist=new ArrayList<Functionlist>();
		functionlist=psychologyJdbcDao.getList(sql,Functionlist.class);
		return functionlist;
	}
*/
	/**
	 *根据sql查询一个实体 
	 *
	@SuppressWarnings("rawtypes")
	public static BaseEntity findOneEntity(String sql,PatrolJdbcDao sysDao,Class entityClass){
		if (sql!=null && !sql.equals("")){
			List list = sysDao.getList(sql, entityClass);
			if (list != null && list.size() > 0) {
				return (BaseEntity) list.get(0);
			} else {
				return null;
			}
		}else{
			return null;
		}
	}*/
	 
}
