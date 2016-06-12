/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.Jiang.java <2016年6月6日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin.map;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author liujie 
 * @Date 2016年6月6日 下午8:24:01
 * @version 1.0
 */
public class FenZhi {
	
	/*Da("1","大"),
	Xiao("2","小"),
	Dan("3","单"),
	XiaoDan("4","小单"),
	DaDan("5","大单"),
	SHUANG("6","双"),
	XiaoShuang("7","小双"),
	DaShuang("8","大双"),
	DianShu("9","数"),
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
	G_DianShu("09","改数"),
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
	private FenZhi(String index, String name) {
		this.index = index;
		this.name = name;
	}
	
	/**
	 * 玩法规则对应赔率
	 */
	public static Map<String, Integer> map = new HashMap<String, Integer>();
	static{
		map.put("大", 1);
		map.put("小", 1);
		map.put("单", 1);
		map.put("小单", 3);
		map.put("大单", 3);
		map.put("双", 1);
		map.put("小双", 3);
		map.put("大双", 3);
		map.put("顺", 4);
		map.put("豹", 19);
		map.put("4特", 29);//点数4的倍数
		map.put("5特", 14);
		map.put("6特", 9);
		map.put("7特", 7);
		map.put("8特", 5);
		map.put("9特", 4);
		map.put("10特", 3);
		map.put("11特", 3);
		map.put("12特", 4);
		map.put("13特", 5);
		map.put("14特", 7);
		map.put("15特", 9);
		map.put("16特", 14);
		map.put("17特", 29);

		
	}
}
