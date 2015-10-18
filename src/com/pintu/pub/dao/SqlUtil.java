package com.pintu.pub.dao;

import com.pintu.pub.page.PageResult;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

@SuppressWarnings({"rawtypes","unchecked"})
public class SqlUtil {
	/**
	 * 解析数据库查询出的数据集rsResult，将其转换为strResultType对应的类名的实体集
	 * 以List<[strResultType]类>形式返回 strPrefix为列名前缀
	 */
	public static List parseDataEntityBeans(ResultSet rsResult,
			String strPrefix, String strResultType) throws Exception {
		String[] strColumnNames = (String[]) null;
		String[] strAttributeNames = (String[]) null;
		int[] nTypes = (int[]) null;
		List listResult = new ArrayList();

		Class classResult = Class.forName(strResultType);
		Hashtable hs = new Hashtable(1024);
		while (rsResult.next()) {
			if (strColumnNames == null) {
				ResultSetMetaData rsMetaData = rsResult.getMetaData();
				int nColumnCount = rsMetaData.getColumnCount();
				strColumnNames = new String[nColumnCount];
				strAttributeNames = new String[nColumnCount];
				nTypes = new int[nColumnCount];
				for (int i = 0; i < nColumnCount; i++) {
					strAttributeNames[i] = strColumnNames[i] = rsMetaData
							.getColumnLabel(i + 1);
					if (strPrefix.length() > 0)
						strAttributeNames[i] = strColumnNames[i]
								.substring(strPrefix.length());
					nTypes[i] = rsMetaData.getColumnType(i + 1);
				}
				Method[] methods = classResult.getMethods();
				for (int ii = 0; ii < methods.length; ++ii) {
					hs.put(methods[ii].getName().toUpperCase(),
							methods[ii].getName());
				}
			}
			listResult.add(parseObjectFromResultSet(rsResult, strColumnNames,
					strAttributeNames, nTypes, strResultType, hs));
		}
		if (listResult.size() > 0) 
			return listResult;
		else
			return null;
	}

	public static List parseDataEntityBeansToList(ResultSet rsResult,
			String strResultType) throws Exception {
		String[] strColumnNames = (String[]) null;
		String[] strAttributeNames = (String[]) null;
		int[] nTypes = (int[]) null;
		List listResult = new ArrayList();

		Class classResult = Class.forName(strResultType);
		Hashtable hs = new Hashtable(1024);
		while (rsResult.next()) {
			if (strColumnNames == null) {
				ResultSetMetaData rsMetaData = rsResult.getMetaData();
				int nColumnCount = rsMetaData.getColumnCount();
				strColumnNames = new String[nColumnCount];
				strAttributeNames = new String[nColumnCount];
				nTypes = new int[nColumnCount];
				for (int i = 0; i < nColumnCount; i++) {
					strAttributeNames[i] = strColumnNames[i] = rsMetaData
							.getColumnName(i + 1);
					nTypes[i] = rsMetaData.getColumnType(i + 1);
				}
				Method[] methods = classResult.getMethods();
				for (int ii = 0; ii < methods.length; ++ii) {
					hs.put(methods[ii].getName().toUpperCase(),
							methods[ii].getName());
				}
			}
			try {
				listResult.add(parseObjectFromResultSet(rsResult,
						strColumnNames, strAttributeNames, nTypes,
						strResultType, hs));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listResult;
	}

	public static PageResult parseDataEntityBeans(ResultSet rsResult,
			String strPrefix, String strResultType, PageResult pageResult)
			throws Exception {
		String[] strColumnNames = (String[]) null;
		String[] strAttributeNames = (String[]) null;
		int[] nTypes = (int[]) null;
		List listResult = new ArrayList();

		Class classResult = Class.forName(strResultType);
		Hashtable hs = new Hashtable(1024);
		rsResult.absolute((pageResult.getPageNo() - 1)
				* pageResult.getPageSize() + 1);

		int f = 0;
		while ((f < pageResult.getPageSize()) && (!(rsResult.isAfterLast()))) {
			if (strColumnNames == null) {
				ResultSetMetaData rsMetaData = rsResult.getMetaData();
				int nColumnCount = rsMetaData.getColumnCount();
				strColumnNames = new String[nColumnCount];
				strAttributeNames = new String[nColumnCount];
				nTypes = new int[nColumnCount];
				for (int i = 0; i < nColumnCount; i++) {
					strAttributeNames[i] = strColumnNames[i] = rsMetaData
							.getColumnName(i + 1);
					if (strPrefix.length() > 0)
						strAttributeNames[i] = strColumnNames[i]
								.substring(strPrefix.length());
					nTypes[i] = rsMetaData.getColumnType(i + 1);
				}

				Method[] methods = classResult.getMethods();
				for (int ii = 0; ii < methods.length; ++ii) {
					hs.put(methods[ii].getName().toUpperCase(),
							methods[ii].getName());
				}
			}
			listResult.add(parseObjectFromResultSet(rsResult, strColumnNames,
					strAttributeNames, nTypes, strResultType, hs));
			++f;
			rsResult.next();
		}
		if (listResult.size() > 0) {
			pageResult.setList(listResult);
			return pageResult;
		}
		return null;
	}

	/**
	 * 解析数据集rsResult中的当前数据行，将该行的所有列值赋值给对应的strResultType类实体的属性（类的反射机制）
	 * 返回一个含值的strResultType类的实体
	 */
	public static Object parseObjectFromResultSet(ResultSet rsResult,
			String[] strColumnNames, String[] strAttributeNames, int[] nTypes,
			String strResultType, Hashtable hs) throws Exception {
		Class classResult = Class.forName(strResultType);
		Object objResult = classResult.newInstance();
		Object objVal = null;
		Method method = null;
		String strMethodName = null;
		for (int i = 0; i < strColumnNames.length; ++i) {
			objVal = rsResult.getObject(strColumnNames[i]);
			if (hs.get("SET" + strAttributeNames[i].toUpperCase()) != null) {
				strMethodName = hs.get(
						"SET" + strAttributeNames[i].toUpperCase()).toString();
			}
			if (objVal == null)
				continue;
			try {
				if (nTypes[i] == 3) {
					try {
						method = classResult.getMethod(strMethodName,
								new Class[] { Long.TYPE });
						objVal = new Long(((BigDecimal) objVal).longValue());
					} catch (NoSuchMethodException e) {
						try {
							method = classResult.getMethod(strMethodName,
									new Class[] { Double.TYPE });
							objVal = new Double(
									((BigDecimal) objVal).doubleValue());
						} catch (NoSuchMethodException exec) {
							exec.printStackTrace();
						}
					}
					method.invoke(objResult, new Object[] { objVal });
				} else if (nTypes[i] == 2) {
					try {
						method = classResult.getMethod(strMethodName,
								new Class[] { Double.class });
						objVal = new Double(((BigDecimal) objVal).doubleValue());
					} catch (NoSuchMethodException e) {
						try {
							method = classResult.getMethod(strMethodName,
									new Class[] { Long.class });
							objVal = new Long(((BigDecimal) objVal).longValue());
						} catch (NoSuchMethodException exec) {
							exec.printStackTrace();
						}
					}
					method.invoke(objResult, new Object[] { objVal });
				}else if(nTypes[i] == 8){
					method = classResult.getMethod(strMethodName,
							new Class[] { Double.class });
					method.invoke(objResult, new Object[] { objVal });
				}else if (nTypes[i] == -7) {
					method = classResult.getMethod(strMethodName,
							new Class[] { Boolean.class });
					method.invoke(objResult, new Object[] { objVal });
				} else if (nTypes[i] == 4) {
					method = classResult.getMethod(strMethodName,
							new Class[] { Integer.class });
					method.invoke(objResult, new Object[] { objVal });
				} else if (nTypes[i] == 7) {
					method = classResult.getMethod(strMethodName,
							new Class[] { Float.class });
					method.invoke(objResult, new Object[] { objVal });
				} else if (nTypes[i] == -5) {
					method = classResult.getMethod(strMethodName,
							new Class[] { Long.class });
					if (objVal instanceof BigInteger) {
						objVal = new Long(((BigInteger) objVal).longValue());
					}
					method.invoke(objResult, new Object[] { objVal });
				} else if ((nTypes[i] == 91) || (nTypes[i] == 92) ){
					method = classResult.getMethod(strMethodName,
							new Class[] { Date.class });
					method.invoke(objResult, new Object[] { objVal });
				} else if ((nTypes[i] == 93) ){ 
					SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					method = classResult.getMethod(strMethodName,
							new Class[] { Date.class });
					method.invoke(objResult, new Object[] { format.parse(objVal.toString()) });
				} /*else if (nTypes[i] == 93) {
					method = classResult.getMethod(strMethodName,
							new Class[] { Date.class });
					objVal = new Date(((Timestamp) objVal).getTime());
					method.invoke(objResult, new Object[] { objVal });
				} */else if ((nTypes[i] == 12) || (nTypes[i] == 1)
						|| (nTypes[i] == -1)) {
					method = classResult.getMethod(strMethodName,
							new Class[] { String.class });
					method.invoke(objResult, new Object[] { objVal });
				}
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				throw new Exception("method="
						+ method.getName()
						+ ",attribute="
						+ strAttributeNames[i]
						+ "value="
						+ Long.valueOf(new StringBuilder().append(nTypes[i])
								.toString()), e);
			}
		}
		return objResult;
	}
}
