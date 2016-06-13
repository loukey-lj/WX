/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.Jiang.java <2016年6月6日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author liujie 
 * @Date 2016年6月6日 下午8:24:01
 * @version 1.0
 */
public class Jiang {
	
	/*Da("1","大"),
	Xiao("2","小"),
	Dan("3","单"),
	XiaoDan("4","小单"),
	DaDan("5","大单"),
	SHUANG("6","双"),
	XiaoShuang("7","小双"),
	DaShuang("8","大双"),
	DianShu("9","点数"),
	ShunZi("10","顺子"),
	BaoZi("11","豹子"),
	G_Da("01","改大"),
	G_XiaO("02","改小"),
	G_Dan("03","改单"),
	G_XiaoDan("04","改小单"),
	G_DaDan("05","改大单"),
	G_SHUANG("06","改双"),
	G_XiaoShuang("07","改小双"),
	G_DaShuang("08","改大双"),
	G_DianShu("09","改点数"),
	G_ShunZi("010","改顺子"),
	G_BaoZi("011","改豹子"),
	QuXiao("-1","取"),
	Cha("0","查")
	;*/
	private String index;
	private String name;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private Jiang(String index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public static Map<String, Integer> map = new HashMap<String, Integer>();
	public static Map<String, String> map1 = new HashMap<String, String>();
	public static Map<String, String> map2 = new HashMap<String, String>();
	public static Map<Integer, String> map3 = new HashMap<Integer, String>();
	static{
		map.put("大", 1);
		map.put("小", 1);
		map.put("单", 1);
		map.put("小单", 1);
		map.put("大单", 1);
		map.put("双", 1);
		map.put("小双", 1);
		map.put("大双", 1);
		map.put("顺", 1);
		map.put("豹", 1);
		map.put("改大", 1);
		map.put("改小", 1);
		map.put("改单", 1);
		map.put("改小单", 1);
		map.put("改大单", 1);
		map.put("改双", 1);
		map.put("改小双", 1);
		map.put("改大双", 1);
		map.put("改顺", 1);
		map.put("改豹", 1);
		map.put("取", 0);
		map.put("查", 0);
		map.put("回", 1);
		
		map.put("四特", 1);
		map.put("五特", 1);
		map.put("六特", 1);
		map.put("七特", 1);
		map.put("八特", 1);
		map.put("九特", 1);
		map.put("十特", 1);
		map.put("十一特", 1);
		map.put("十二特", 1);
		map.put("十三特", 1);
		map.put("十四特", 1);
		map.put("十五特", 1);
		map.put("十六特", 1);
		map.put("十七特", 1);
		
		map.put("4特", 1);
		map.put("5特", 1);
		map.put("6特", 1);
		map.put("7特", 1);
		map.put("8特", 1);
		map.put("9特", 1);
		map.put("10特", 1);
		map.put("11特", 1);
		map.put("12特", 1);
		map.put("13特", 1);
		map.put("14特", 1);
		map.put("15特", 1);
		map.put("16特", 1);
		map.put("17特", 1);
		
		//哈大，哈小，哈大单，哈小单，哈单，哈双，哈豹
		map.put("哈大", 0);
		map.put("哈小", 0);
		map.put("哈单", 0);
		map.put("哈大单", 0);
		map.put("哈小单", 0);
		map.put("哈双", 0);
		map.put("哈大双", 0);
		map.put("哈小双", 0);
		map.put("哈豹", 0);
		map.put("哈顺", 0);
		
		
		map1.put("4特", "四特");
		map1.put("5特", "五特");
		map1.put("6特", "六特");
		map1.put("7特", "七特");
		map1.put("8特", "八特");
		map1.put("9特", "九特");
		map1.put("10特", "十特");
		map1.put("11特", "十一特");
		map1.put("12特", "十二特");
		map1.put("13特", "十三特");
		map1.put("14特", "十四特");
		map1.put("15特", "十五特");
		map1.put("16特", "十六特");
		map1.put("17特", "十七特");
		
		
		map3.put(4, "四特");
		map3.put(5, "五特");
		map3.put(6, "六特");
		map3.put(7, "七特");
		map3.put(8, "八特");
		map3.put(9, "九特");
		map3.put(10, "十特");
		map3.put(11, "十一特");
		map3.put(12, "十二特");
		map3.put(13, "十三特");
		map3.put(14, "十四特");
		map3.put(15, "十五特");
		map3.put(16, "十六特");
		map3.put(17, "十七特");
		
		
		map2.put("四特", "4特");
		map2.put("五特", "5特");
		map2.put("六特", "6特");
		map2.put("七特", "7特");
		map2.put("八特", "8特");
		map2.put("九特", "9特");
		map2.put("十特", "10特");
		map2.put("十一特", "11特");
		map2.put("十二特", "12特");
		map2.put("十三特", "13特");
		map2.put("十四特", "14特");
		map2.put("十五特", "15特");
		map2.put("十六特", "16特");
		map2.put("十七特", "17特");
	}
}
