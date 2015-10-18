package com.pintu.pub.dao.impl;

import com.google.gson.Gson;
import com.mysql.jdbc.Statement;
import com.pintu.pub.dao.BaseDao;
import com.pintu.pub.dao.SqlUtil;
import com.pintu.pub.entity.BaseEntity;
import com.pintu.pub.entity.ProcEntity;
import com.pintu.pub.page.PageResult;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

@SuppressWarnings({"rawtypes","unused","unchecked","null"})
public class BaseJdbcDao implements BaseDao {
	protected final Log log = LogFactory.getLog(super.getClass());
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 根据sql语句将查询结果的第1列值以字符串形式存入list，返回list
	 */
	public List getList(String sql) {
		PreparedStatement ps = null;
		List resultLis = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next())
				resultLis.add(rs.getString(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ((ps != null) || (rs != null)) {
				try {
					ps.close();
					rs.close();
					ps = null;
					rs = null;
					DataSourceUtils.releaseConnection(conn,
							this.jdbcTemplate.getDataSource());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return resultLis;
	}

	/**
	 * 根据sql语句和实体类destClass，将查询结果以实体类格式存入list，返回list
	 */
	public List getList(String sql, Class destClass) {
		PreparedStatement ps = null;
		String resultType = "";
		List objs = null;
		ResultSet rs = null;
		Connection conn = null;

		resultType = destClass.getName();
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs != null)
				objs = SqlUtil.parseDataEntityBeans(rs, "", resultType);
		} catch (Exception e) {
			this.log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if ((ps != null) || (rs != null)) {
				try {
					ps.close();
					rs.close();
					ps = null;
					rs = null;
					DataSourceUtils.releaseConnection(conn,
							this.jdbcTemplate.getDataSource());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return objs;
	}

	
	
	
	
	/**
	 * 根据sql语句、实体类destClass、当前页数iPageNo、每页数据量iPageSize，
	 * 将查询结果以实体类格式存入PageResult对象的list属性，返回PageResult
	 */
	public PageResult getList(String sql, Class destClass, int iPageNo,
			int iPageSize) {
		PreparedStatement ps = null;
		String resultType = destClass.getName();
		PageResult pageResult = new PageResult();
		ResultSet rs = null;
		int rowCount = 0;
		Connection conn = null;
		try {
			rowCount = getRsCount(sql);
			if (iPageNo <= 0) {
				iPageNo = 1;
			}
			if (iPageNo > (rowCount + iPageSize - 1) / iPageSize) {
				iPageNo = (rowCount + iPageSize - 1) / iPageSize;
			}
			pageResult.setPageNo(iPageNo);
			pageResult.setPageSize(iPageSize);
			pageResult.setRecTotal(rowCount);
			pageResult.setPageTotal(rowCount % iPageSize == 0 ? (rowCount)
					/ iPageSize : (rowCount / iPageSize + 1));

			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			StringBuffer sqlbs = new StringBuffer(
					"select * from ( select rownum myrownum,t.* from (");
			sqlbs.append(sql);
			sqlbs.append(") t where rownum<=");
			sqlbs.append(pageResult.getEndRec());
			sqlbs.append(") where myrownum>=");
			sqlbs.append(pageResult.getBeginRec());
			ps = conn.prepareStatement(sqlbs.toString());
			rs = ps.executeQuery();
			if (rs != null)
				pageResult.setList(SqlUtil.parseDataEntityBeans(rs, "",
						resultType));
		} catch (Exception e) {
			this.log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if ((ps != null) || (rs != null)) {
				try {
					rs.close();
					ps.close();
					rs = null;
					ps = null;

					DataSourceUtils.releaseConnection(conn,
							this.jdbcTemplate.getDataSource());
				} catch (SQLException e) {
					this.log.error(e.getMessage());
				}
			}
		}
		return pageResult;
	}

	/**
	 * 根据sql语句返回记录条数，返回int
	 */
	public int getRsCount(String sql) {
		int i = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			StringBuffer sqlbs = new StringBuffer(
					"select count(1) count from (");
			//为提高sql效率，要去掉语句中所有的排序语句 order by
			String tempSql = sql.toLowerCase();
			int pos = tempSql.indexOf("order");
			while (pos > 0) {
				if (tempSql.substring(pos, tempSql.length() - 1).indexOf(")") > 0)
					tempSql = tempSql.substring(0, pos - 1)
							+ tempSql.substring(pos, tempSql.length() - 1)
									.substring(
											tempSql.substring(pos,
													tempSql.length() - 1)
													.indexOf(")"),
											tempSql.substring(pos,
													tempSql.length() - 1)
													.length() - 1);
				else
					tempSql = tempSql.substring(0, pos - 1);

				pos = tempSql.indexOf("order");
			}
			sqlbs.append(tempSql);
			sqlbs.append(")");
			ps = conn.prepareStatement(sqlbs.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				i = rs.getInt("count");
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return i;
	}

	/**
	 * 根据sql查询语句，返回某个字符串，sDefault为查不到数据时的返回值
	 */
	public String getTempStr(String sql, String sDefault) {
		String str = sDefault;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				str = rs.getString(1);
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return str;
	}

	/**
	 * 根据sql查询语句，返回某个整数值，iDefault为查不到数据时的返回值
	 */
	public int getTempInt(String sql, int iDefault) {
		int i = iDefault;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				i = rs.getInt(1);
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return i;
	}

	/**
	 * 根据sql查询语句，返回某个浮点数，dDefault为查不到数据时的返回值
	 */
	public Double getTempDouble(String sql, Double dDefault) {
		Double dTemp = dDefault;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				dTemp = rs.getDouble(1);
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return dTemp;
	}

	/**
	 * 将某浮点数转换为系统规定格式的（调用数据库sf_je_f 函数）
	 */
	public double getFormatMoney(double sourceMoney) {
		double money = sourceMoney;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			StringBuffer sql = new StringBuffer("select sf_je_f(");
			sql.append(String.valueOf(sourceMoney));
			sql.append(") from dual");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				money = rs.getDouble(1);
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return money;
	}

	/**
	 * 根据sql查询语句，返回某个日期，iDefault为查不到数据时的返回值
	 */
	public Date getTempDate(String sql) {
		Date dt = new Date();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				dt = rs.getDate(1);
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return dt;
	}

	/**
	 * 获取服务器日期，返回字符串类型
	 */
	public String getServerDateTimeStr(String dtFormat) {
		String dtStr = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			StringBuffer sql = new StringBuffer("select to_char(sysdate,'");
			sql.append(dtFormat);
			sql.append("') from dual");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				dtStr = rs.getString(1);
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return dtStr;
	}

	/**
	 * 获取服务器日期，返回日期类型，hasTime决定返回值是否含有时分秒
	 */
	public Date getServerDateTime() {
		Date dt = new Date();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement("select trunc(sysdate) from dual");
			rs = ps.executeQuery();
			if (rs.next()) {
				dt = rs.getDate(1);
			}
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					rs.close();
					rs = null;
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return dt;
	}

	/**
	 * 执行DDL sql语句返回记录条数，返回true或false
	 */
	public boolean executeSQL(String sql) {
		boolean rs = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			ps = conn.prepareStatement(sql);
			ps.execute(sql);
			if (ps.getResultSet() == null)
				rs = true;
		} catch (Exception localException) {
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DataSourceUtils.releaseConnection(conn,
					this.jdbcTemplate.getDataSource());
		}
		return rs;
	}

	/**
	 * 执行procName存储过程，返回v_result参数值,返回-99代表无此存过名称 注意：存过中v_result 一定在参数的最后定义
	 **
	public int executeProc(String procName, BaseEntity baseEntity) {
		PreparedStatement ps = null;
		String resultType = ProcEntity.class.getName();
		List objs = null;
		ResultSet rs = null;
		Connection conn = null;
		CallableStatement proc = null;
		int result = -99;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			StringBuffer sql = new StringBuffer(
					"select object_name,argument_name,data_type,in_out,to_char(data_scale) data_scale from user_arguments where object_name='");
			sql.append(procName);
			sql.append("' order by position");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if (rs != null) {
				objs = SqlUtil.parseDataEntityBeans(rs, "", resultType);
				StringBuffer sb = new StringBuffer();
				sb.append("{ call ");
				sb.append(procName);
				sb.append("(");
				for (int i = 0; i < objs.size(); ++i) {
					if (i == objs.size() - 1)
						sb.append("?");
					else {
						sb.append("?,");
					}
				}
				sb.append(") }");
				proc = conn.prepareCall(sb.toString());
				Map propertyMap = new HashMap();
				parseEntityToHashasMap(baseEntity, propertyMap);
				setProcParamValue(proc, objs, propertyMap);
				proc.execute();
				result = proc.getInt(objs.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ((ps != null) || (rs != null)) {
				try {
					ps.close();
					rs.close();
					ps = null;
					rs = null;
					DataSourceUtils.releaseConnection(conn,
							this.jdbcTemplate.getDataSource());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	} */

	/**
	 * 遍历实体类的所有属性，将属性和属性值放入实体基类(BaseEntity)中的
	 * */
	public static void parseEntityToHashasMap(Object obj, Map hasMap) {
		Field[] field = obj.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		int iLenth = field.length;
		Field[] baseField = BaseEntity.class.getDeclaredFields(); // 获取实体类基类的所有属性，返回Field数组
		field = Arrays.copyOf(field, field.length + baseField.length);// 扩充实体类属性数组空间
		for (int j = 0; j < baseField.length; j++)
			// 将基类的所有属性复制到实体类属性数组的后面
			field[iLenth + j] = baseField[j];
		for (int i = 0; i < field.length; i++) { // 遍历所有属性
			String fieldName = field[i].getName(); // 获取属性的名字
			String methodStr = fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			String type = field[i].getGenericType().toString(); // 获取属性的类型
			Method m;
			try {
				m = obj.getClass().getMethod("get" + methodStr);
				if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
					String fieldValue = (String) m.invoke(obj); // 调用getter方法获取属性值
					if (fieldValue == null) {
						fieldValue = "";
					}
					hasMap.put(fieldName, fieldValue);
				} else if (type.equals("class java.lang.Integer")) {
					Integer fieldValue = (Integer) m.invoke(obj);
					/*
					 * if (fieldValue == null) { fieldValue = 0; }
					 */
					hasMap.put(fieldName, fieldValue);
				} else if (type.equals("class java.lang.Float")) {
					Float fieldValue = (Float) m.invoke(obj);
					/*
					 * if (fieldValue == null) { fieldValue = 0; }
					 */
					hasMap.put(fieldName, fieldValue);
				} else if (type.equals("class java.lang.Short")) {
					Short fieldValue = (Short) m.invoke(obj);
					/*
					 * if (fieldValue == null) { fieldValue = 0; }
					 */
					hasMap.put(fieldName, fieldValue);
				} else if (type.equals("class java.lang.Long")) {
					Long fieldValue = (Long) m.invoke(obj);
					/*
					 * if (fieldValue == null) { fieldValue = 0; }
					 */
					hasMap.put(fieldName, fieldValue);
				} else if (type.equals("class java.lang.Double")) {
					Double fieldValue = (Double) m.invoke(obj);
					/*
					 * if (fieldValue == null) { fieldValue = 0.0; }
					 */
					hasMap.put(fieldName, fieldValue);
				} else if (type.equals("class java.lang.Boolean")) {
					Boolean fieldValue = (Boolean) m.invoke(obj);
					hasMap.put(fieldName, fieldValue);
				} else if (type.equals("class java.util.Date")) {
					Date fieldValue = (Date) m.invoke(obj);
					hasMap.put(fieldName, fieldValue);
				} else {
					Object fieldValue = null;
					hasMap.put(fieldName, fieldValue);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
				// hasMap.put(fieldName,null);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				// hasMap.put(fieldName,null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				// hasMap.put(fieldName,null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				// hasMap.put(fieldName,null);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				// hasMap.put(fieldName,null);
			}
		}
		// System.out.println(hasMap);
	}

	/**
	 * 生成mysql insert语句
	 **/
	public static String saveProcParamValue(String DBName,List<ProcEntity> pelist,Map hasMap) {
		
		String cloumn_name="";
		String valueStr="";
		String cloStr="";
		for (int i = 0; i <pelist.size(); i++) {
			ProcEntity pe=pelist.get(i);
			cloumn_name=pe.getColumn_name().toLowerCase();
			//if(hasMap.get(cloumn_name)!=null&&!hasMap.get(cloumn_name).equals("")){
				if(pe.getData_type().equals("varchar")||pe.getData_type().equals("text")){
					if(hasMap.get(cloumn_name)!=null){
						cloStr+=cloumn_name+",";
						valueStr+="'"+hasMap.get(cloumn_name)+"',";
					}
				}else if(pe.getData_type().equals("double")||pe.getData_type().equals("float")){
					if(hasMap.get(cloumn_name)!=null){
						cloStr+=cloumn_name+",";
						valueStr+=hasMap.get(cloumn_name)+",";
					}
				}else if(pe.getData_type().equals("int")){
					if(hasMap.get(cloumn_name)!=null){
						cloStr+=cloumn_name+",";
						valueStr+=hasMap.get(cloumn_name)+",";
					}
				}else if(pe.getData_type().equals("date")||pe.getData_type().equals("datetime")){
					if(hasMap.get(cloumn_name)!=null){
						cloStr+=cloumn_name+",";
						SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						valueStr+="str_to_date('"+format.format((Date)hasMap.get(cloumn_name))+"','%Y-%m-%d %H:%i:%s'),";
					}else{
						cloStr+=cloumn_name+",";
						SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						valueStr+="str_to_date('"+format.format(new Date())+"','%Y-%m-%d %H:%i:%s'),";
					}
				}
			}
			
		//}
		if(cloStr!=null&&!cloStr.equals("")){
			cloStr=cloStr.substring(0,cloStr.lastIndexOf(","));
			if(valueStr!=null&&!valueStr.equals("")){
				valueStr=valueStr.substring(0,valueStr.lastIndexOf(","));
				StringBuffer sql=new StringBuffer("insert into "+DBName+"("+cloStr+") values("+valueStr+")");
				return sql.toString();
			}else{
				return "";
			}
			
		}else{
			return "";
		}
		
	} 
	/**
	 * 生成mysql update语句
	 **/
	public static String updateProcParamValue(String DBName,List<ProcEntity> pelist,Map hasMap,String keyStr) {
		
		if(keyStr!=null&&!keyStr.equals("")){
			String cloumn_name="";
			String valueStr="";
			for (int i = 0; i <pelist.size(); i++) {
				ProcEntity pe=pelist.get(i);
				cloumn_name=pe.getColumn_name().toLowerCase();
				//if(hasMap.get(cloumn_name)!=null&&!cloumn_name.equals(keyStr)){
					if(pe.getData_type().equals("varchar")||pe.getData_type().equals("text")){
						if(hasMap.get(cloumn_name)!=null){
							valueStr+=cloumn_name+"='"+hasMap.get(cloumn_name)+"',";
						}
					}else if(pe.getData_type().equals("double")||pe.getData_type().equals("float")){
						if(hasMap.get(cloumn_name)!=null){
							valueStr+=cloumn_name+"="+hasMap.get(cloumn_name)+",";
						}
					}else if(pe.getData_type().equals("int")){
						if(hasMap.get(cloumn_name)!=null){
							valueStr+=cloumn_name+"="+hasMap.get(cloumn_name)+",";
						}
					}else if(pe.getData_type().equals("date")||pe.getData_type().equals("datetime")){
						if(hasMap.get(cloumn_name)!=null){
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							valueStr+=cloumn_name+"=str_to_date('"+format.format((Date)hasMap.get(cloumn_name))+"','%Y-%m-%d %H:%i:%s'),";
						}
					}
				//}
				
			}
			if(valueStr!=null&&!valueStr.equals("")){
				valueStr=valueStr.substring(0,valueStr.lastIndexOf(","));
				StringBuffer sql=new StringBuffer("update "+DBName+" set "+valueStr+" where "+keyStr+"='"+hasMap.get(keyStr)+"'");
				return sql.toString();
			}else{
				return "";
			}
			
		}else{
			return "";
		}
		
	} 
	/**
	 * 生成mysql delete语句
	 **/
	public static String deleteProcParamValue(String DBName,Map hasMap,String keyStr) {
		
		if(keyStr!=null&&!keyStr.equals("")){
			if(hasMap.get(keyStr)!=null&&!hasMap.get(keyStr).equals("")){
				StringBuffer sql=new StringBuffer("delete from "+DBName+" where "+keyStr+"='"+hasMap.get(keyStr)+"'");
				return sql.toString();
			}else{
				return "";
			}
			
		}else{
			return "";
		}
		
	} 
	/**
	 * 实体类的所有属性赋空值
	 * */
	public static void setEntityToNull(Object obj) {
		if (null != obj) {
			Field[] field = obj.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
			int iLenth = field.length;
			Field[] baseField = BaseEntity.class.getDeclaredFields(); // 获取实体类基类的所有属性，返回Field数组
			field = Arrays.copyOf(field, field.length + baseField.length);
			for (int j = 0; j < baseField.length; j++)
				field[iLenth + j] = baseField[j];
			for (int i = 0; i < field.length; i++) { // 遍历所有属性
				String fieldName = field[i].getName(); // 获取属性的名字                                                 
				// System.out.println("fieldName is "+fieldName);
				String methodStr = fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				String type = field[i].getGenericType().toString(); // 获取属性的类型
				Method m;
				try {
					if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
						m = obj.getClass().getMethod("set" + methodStr,
								String.class);
						m.invoke(obj, (Object) ""); // 调用setter方法为属性赋值
					} else if (type.equals("class java.lang.Integer")) {
						m = obj.getClass().getMethod("set" + methodStr,
								Integer.class);
						m.invoke(obj, (Object) null);
					} else if (type.equals("class java.lang.Short")) {
						m = obj.getClass().getMethod("set" + methodStr,
								Short.class);
						m.invoke(obj, (Object) null);
					} else if (type.equals("class java.lang.Double")) {
						m = obj.getClass().getMethod("set" + methodStr,
								Double.class);
						m.invoke(obj, (Object) null);
					} else if (type.equals("class java.lang.Boolean")) {
						m = obj.getClass().getMethod("set" + methodStr,
								Boolean.class);
						m.invoke(obj, (Object) null);
					} else if (type.equals("class java.util.Date")) {
						m = obj.getClass().getMethod("set" + methodStr,
								Date.class);
						m.invoke(obj, (Object) null);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 增加日志信息
	 * */
	public boolean addLogInfo(String oprType, String oprUser, String oprContent) {
		boolean rs = false;
		StringBuffer sql = new StringBuffer(
				"insert into sf_czrz_t(czsj,czlb,czr,cznr) values(sysdate,'");
		sql.append(oprType);
		sql.append("','");
		sql.append(oprUser);
		sql.append("','");
		sql.append(oprContent + "')");
		rs = executeSQL(sql.toString());
		return rs;
	}
	/**
	 * sql保存到数据库
	 * */
	public int executeMYSQLDB(String executeType,String DBName,BaseEntity baseEntity,String keyStr){
		PreparedStatement ps = null;
		String resultType = ProcEntity.class.getName();
		List<ProcEntity> objs = null;
		ResultSet rs = null;
		Connection conn = null;
		CallableStatement proc = null;
		String saveSql="";
		int result = -99;
		try {
			conn = DataSourceUtils.getConnection(this.jdbcTemplate
					.getDataSource());
			StringBuffer sql = new StringBuffer(
					"select table_name,column_name,data_type from information_schema.COLUMNS where table_name='");
			sql.append(DBName);
			sql.append("' ");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if (rs != null) {
				objs = SqlUtil.parseDataEntityBeans(rs, "", resultType);
				//proc = conn.prepareCall(sb.toString());
				Map propertyMap = new HashMap();
				parseEntityToHashasMap(baseEntity, propertyMap);
				if(executeType.equals("save")){
					saveSql=saveProcParamValue(DBName,objs,propertyMap);
				}else if(executeType.equals("update")){
					saveSql=updateProcParamValue(DBName,objs,propertyMap,keyStr);
				}else if(executeType.equals("delete")){
					saveSql=deleteProcParamValue(DBName,propertyMap,keyStr);
				}else{
					saveSql="";
				}
				if(saveSql!=null&&!saveSql.equals("")){
					ps = conn.prepareStatement(saveSql);
					//rs = ps.execute(saveSql);
					if(ps.executeUpdate()>=0){
						result =0;// proc.getInt(objs.size());
					}else{
						result =1;
					}
				}else{
					result =2;//生成语句错误
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ((ps != null) || (rs != null)) {
				try {
					ps.close();
					rs.close();
					ps = null;
					rs = null;
					DataSourceUtils.releaseConnection(conn,
							this.jdbcTemplate.getDataSource());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	/**
	 * mysql分页查询
	 * */
	public PageResult findPageResult(String sql,PageResult pageResult,Class destClass){
		String sql1="select count(1) from ("+sql+") t";
		String sql2="select page.* from ("+sql+") page limit "+
				((pageResult.getPageNo()-1)*pageResult.getPageSize())+","+pageResult.getPageSize();
		pageResult.setList(this.getList(sql2,destClass));
		pageResult.setRecTotal(this.getTempInt(sql1, 0));
		pageResult.setPageTotal(((int)pageResult.getRecTotal()/pageResult.getPageSize())+
				(pageResult.getRecTotal()%pageResult.getPageSize()>0?1:0));
		return pageResult;
	}
	/*
	 * public static String getProcSql(String procName) { return
	 * "select object_name,argument_name,data_type,in_out,to_char(data_scale) data_scale from user_arguments where object_name='"
	 * + procName + "'"; }
	 * 
	 * public static String[] getColumnNames(ResultSet rs) throws SQLException {
	 * ResultSetMetaData metaData = rs.getMetaData(); int columsTotal =
	 * metaData.getColumnCount(); String[] columnNames = new
	 * String[columsTotal]; for (int i = 1; i <= columsTotal; ++i) {
	 * columnNames[(i - 1)] = metaData.getColumnName(i); } return columnNames; }
	 * 
	 * public static int[] getColumnTypes(ResultSet rs) throws SQLException {
	 * ResultSetMetaData metaData = rs.getMetaData(); int columsTotal =
	 * metaData.getColumnCount(); int[] columnTypes = new int[columsTotal]; for
	 * (int i = 1; i <= columsTotal; ++i) { columnTypes[(i - 1)] =
	 * metaData.getColumnType(i); } return columnTypes; }
	 * 
	 * private class MapRowMapper implements RowMapper { public Object
	 * mapRow(ResultSet rs, int rowNum) throws SQLException { String[]
	 * columnNames = BaseJdbcDao.getColumnNames(rs); Map map = new
	 * LinkedHashMap(); int[] columnTypes = BaseJdbcDao.getColumnTypes(rs); for
	 * (int j = 0; j < columnNames.length; ++j) {
	 * map.put(columnNames[j].toLowerCase() + "|" + columnTypes[j],
	 * rs.getObject(columnNames[j])); } return map; } }
	 */
}
