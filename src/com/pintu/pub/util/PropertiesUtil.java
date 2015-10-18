package com.pintu.pub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * priperty文件的操作类
 * */
public class PropertiesUtil {
	private static Logger _log = Logger.getLogger(PropertiesUtil.class);

	private static Hashtable<String, Properties> pptContainer = new Hashtable();

	public static final String getValue(String propertyFilePath, String key) {
		Properties ppts = getProperties(propertyFilePath);
		return ((ppts == null) ? null : ppts.getProperty(key));
	}

	public static final String getValue(String propertyFilePath, String key,
			boolean isAbsolutePath) {
		if (isAbsolutePath) {
			Properties ppts = getPropertiesByFs(propertyFilePath);
			return ((ppts == null) ? null : ppts.getProperty(key));
		}
		return getValue(propertyFilePath, key);
	}

	public static final Properties getProperties(String propertyFilePath) {
		if (propertyFilePath == null) {
			_log.error("propertyFilePath is null!");
			return null;
		}
		Properties ppts = (Properties) pptContainer.get(propertyFilePath);
		if (ppts == null) {
			ppts = loadPropertyFile(propertyFilePath);
			if (ppts != null) {
				pptContainer.put(propertyFilePath, ppts);
			}
		}
		return ppts;
	}

	public static final Properties getPropertiesByFs(String propertyFilePath) {
		if (propertyFilePath == null) {
			_log.error("propertyFilePath is null!");
			return null;
		}
		Properties ppts = (Properties) pptContainer.get(propertyFilePath);
		if (ppts == null) {
			ppts = loadPropertyFileByFileSystem(propertyFilePath);
			if (ppts != null) {
				pptContainer.put(propertyFilePath, ppts);
			}
		}
		return ppts;
	}

	private static Properties loadPropertyFile(String propertyFilePath) {
		InputStream is = PropertiesUtil.class
				.getResourceAsStream(propertyFilePath);
		if (is == null) {
			return loadPropertyFileByFileSystem(propertyFilePath);
		}
		Properties ppts = new Properties();
		try {
			ppts.load(is);
			return ppts;
		} catch (Exception e) {
			_log.debug("加载属性文件出错:" + propertyFilePath, e);
		}
		return null;
	}

	private static Properties loadPropertyFileByFileSystem(
			String propertyFilePath) {
		try {
			Properties ppts = new Properties();
			ppts.load(new FileInputStream(propertyFilePath));
			return ppts;
		} catch (FileNotFoundException e) {
			_log.error("FileInputStream(\"" + propertyFilePath
					+ "\")! FileNotFoundException: " + e);
			return null;
		} catch (IOException e) {
			_log.error("Properties.load(InputStream)! IOException: " + e);
		}
		return null;
	}

	public static final boolean setValueAndStore(String propertyFilePath,
			Hashtable<String, String> htKeyValue) {
		return setValueAndStore(propertyFilePath, htKeyValue, null);
	}

	public static final boolean setValueAndStore(String propertyFilePath,
			Hashtable<String, String> htKeyValue, String storeMsg) {
		Properties ppts = getProperties(propertyFilePath);

		if ((ppts == null) || (htKeyValue == null)) {
			return false;
		}
		ppts.putAll(htKeyValue);
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(propertyFilePath);
		} catch (FileNotFoundException e) {
			_log.debug("propertyFilePath = " + propertyFilePath);
			String path = PropertiesUtil.class.getResource(propertyFilePath)
					.getPath();
			_log.debug("~~~~~~~~path~~~XXX~~~~~" + path);
			try {
				stream = new FileOutputStream(path);
			} catch (FileNotFoundException e1) {
				_log.error("FileNotFoundException! path=" + propertyFilePath);
				return false;
			}
		}

		if (stream == null)
			;
		try {
			ppts.store(stream, (storeMsg != null) ? storeMsg
					: "set value and store.");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static final boolean createPropertiesFile(String propertyFilePath,
			Hashtable<String, String> htKeyValue) {
		File file = new File(propertyFilePath);
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return setValueAndStore(propertyFilePath, htKeyValue,
				"create properties file:" + file.getName());
	}

	public static final boolean setValue(String propertyFilePath, String key,
			String value) {
		Properties ppts = getProperties(propertyFilePath);
		if (ppts == null) {
			return false;
		}
		ppts.put(key, value);
		return true;
	}

	public static final void store(Properties properties,
			String propertyFilePath, String msg) {
		try {
			OutputStream stream = new FileOutputStream(propertyFilePath);
			properties.store(stream, msg);
		} catch (FileNotFoundException e) {
			_log.error("FileOutputStream(" + propertyFilePath
					+ ")! FileNotFoundException: " + e);
		} catch (IOException e) {
			_log.error("store(stream, msg)! IOException: " + e);
			e.printStackTrace();
		}
	}

	public static final String removeValue(String propertyFilePath, String key) {
		Properties ppts = getProperties(propertyFilePath);
		if (ppts == null) {
			return null;
		}
		return ((String) ppts.remove(key));
	}

	public static final Properties removeValue(String propertyFilePath,
			String[] key) {
		if (key == null) {
			_log.error("key[] is null!");
			return null;
		}
		Properties ppts = getProperties(propertyFilePath);
		if (ppts == null) {
			return null;
		}
		for (String strKey : key) {
			ppts.remove(strKey);
		}
		return ppts;
	}

	public static final boolean removeValueAndStore(String propertyFilePath,
			String[] key) {
		Properties ppts = removeValue(propertyFilePath, key);
		if (ppts == null) {
			return false;
		}
		store(ppts, propertyFilePath, "batch remove key value!");
		return true;
	}

	public static final boolean updateValue(String propertyFilePath,
			String key, String newValue) {
		if ((key == null) || (newValue == null)) {
			_log.error("key or newValue is null!");
			return false;
		}
		Hashtable ht = new Hashtable();
		ht.put(key, newValue);
		return setValueAndStore(propertyFilePath, ht, "update " + key
				+ "'s value!");
	}

	public static final boolean batchUpdateValue(String propertyFilePath,
			Hashtable<String, String> htKeyValue) {
		if ((propertyFilePath == null) || (htKeyValue == null)) {
			return false;
		}
		return setValueAndStore(propertyFilePath, htKeyValue,
				"batch update key value!");
	}

	public static final Properties removePropertyFile(String propertyFilePath) {
		return ((Properties) pptContainer.remove(propertyFilePath));
	}

	public static final void reloadPropertyFile(String propertyFilePath) {
		pptContainer.remove(propertyFilePath);
		loadPropertyFile(propertyFilePath);
	}

	public static final String getPpropertyFilePath(String pkg,
			String propertyFileName) {
		pkg = (pkg == null) ? "" : pkg.replaceAll("\\.", "/");
		propertyFileName = propertyFileName + ".properties";
		return "/" + pkg + "/" + propertyFileName;
	}

	/*public static void main(String[] args) {
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource("jdbc2.properties");
		String path = url.getPath();
		System.out.println("读取vlaue根据key");
		String v = getValue(path, "jdbc.driverClassName");
		System.out.println(v);

		System.out.println("添加key和value");
		Hashtable ht = new Hashtable();
		ht.put("name", "dengcd");
		setValueAndStore(path, ht);
		String v_ = getValue(path, "name");
		System.out.println(v_);
		System.out.println("重新加载文件，并读取key对应的value");
		reloadPropertyFile(path);
		String v2_ = getValue(path, "name");
		System.out.println(v2_);
	}*/
}
