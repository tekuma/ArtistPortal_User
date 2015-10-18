package com.pintu.pub.util;

import java.util.HashSet;
import java.util.Set;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Description : 处理汉字和对应拼音转换的工具类
 */
public class PinYinUtil {

	/**
	 * Description ： 传入汉字字符串，拼接成对应的拼音,返回拼音的集合
	 * 注：如果是两个汉字以内则为汉字的全拼，若超过两汉字则为首汉字全拼+后面汉字的首字母
	 */
	public static Set<String> getPinYinSet(String src) {
		Set<String> lstResult = new HashSet<String>();
		char[] t1 = null; // 字符串转换成char数组
		t1 = src.toCharArray();

		if (t1.length <= 2) {
			// ①迭代汉字
			for (char ch : t1) {
				String s[] = getPinYin(ch);
				Set<String> lstNew = new HashSet<String>();
				// ②迭代每个汉字的拼音数组
				for (String str : s) {
					if (lstResult.size() == 0) {
						lstNew.add(str);
					} else {
						for (String ss : lstResult) {
							ss += str;
							lstNew.add(ss);
						}
					}
				}
				lstResult.clear();
				lstResult = lstNew;
			}
		} else {
			int index = 0;
			// ①迭代汉字
			for (char ch : t1) {
				index = index + 1;
				String s[] = getPinYin(ch);
				if (index > 1) {
					for (int i = 0; i < s.length; i++) {
						s[i] = s[i].substring(0, 1);
					}
				}
				Set<String> lstNew = new HashSet<String>();
				// ②迭代每个汉字的拼音数组
				for (String str : s) {
					if (lstResult.size() == 0) {
						lstNew.add(str);
					} else {
						for (String ss : lstResult) {
							ss += str;
							lstNew.add(ss);
						}
					}
				}
				lstResult.clear();
				lstResult = lstNew;
			}
		}
		return lstResult;
	}

	/**
	 * Description ： 传入汉字字符串，拼接成对应的拼音,返回拼音的集合
	 */
	public static Set<String> getPinYins(String src) {
		Set<String> lstResult = new HashSet<String>();
		char[] t1 = null; // 字符串转换成char数组
		t1 = src.toCharArray();

		// ①迭代汉字
		for (char ch : t1) {
			String s[] = getPinYin(ch);
			Set<String> lstNew = new HashSet<String>();
			// ②迭代每个汉字的拼音数组
			for (String str : s) {
				if (lstResult.size() == 0) {
					lstNew.add(str);
				} else {
					for (String ss : lstResult) {
						ss += str;
						lstNew.add(ss);
					}
				}
			}
			lstResult.clear();
			lstResult = lstNew;
		}

		return lstResult;
	}

	/**
	 * Description ： 传入中文汉字，转换出对应拼音 注：出现同音字，默认选择汉字全拼的第一种读音
	 */
	public static String getPinYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];

		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断能否为汉字字符
				// System.out.println(t1[i]);
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
					t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
				} else {
					// 如果不是汉字字符，间接取出字符并连接到字符串t4后
					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return t4;
	}

	/**
	 * Description ： 将单个汉字转换成汉语拼音，考虑到同音字问题，返回字符串数组的形式
	 */
	public static String[] getPinYin(char src) {
		char[] t1 = { src };
		String[] t2 = new String[t1.length];

		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);

		// 判断能否为汉字字符
		if (Character.toString(t1[0]).matches("[\\u4E00-\\u9FA5]+")) {
			try {
				// 将汉字的几种全拼都存到t2数组中
				t2 = PinyinHelper.toHanyuPinyinStringArray(t1[0], t3);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			// 如果不是汉字字符，则把字符直接放入t2数组中
			t2[0] = String.valueOf(src);
		}
		return t2;
	}

	/**
	 * Description ： 传入没有多音字的中文汉字，转换出对应拼音 注：如果传入的中文中有任一同音字都会返回字符串信息：false
	 */
	public static String getNoPolyphone(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];

		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断能否为汉字字符
				// System.out.println(t1[i]);
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
					if (t2.length > 1) {
						return "false";
					} else {
						t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
					}
				} else {
					// 如果不是汉字字符，间接取出字符并连接到字符串t4后
					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return t4;
	}

	/**
	 * 提取每个汉字的首字母
	 * 
	 * @param str
	 * @return String
	 */
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
	}

	/**
	 * 将字符串转换成ASCII码
	 * 
	 * @param cnStr
	 * @return String
	 */
	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			// System.out.println(Integer.toHexString(bGBK[i] & 0xff));
			// 将每个字符转换成ASCII码
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}
}
