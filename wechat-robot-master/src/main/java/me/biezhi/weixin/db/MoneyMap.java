/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.db.MoneyMap.java <2016年6月8日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin.db;

import java.util.HashMap;
import java.util.Map;

import me.biezhi.weixin.JinErHuiZong;
import me.biezhi.weixin.User;

/**
 *
 * @author liujie 
 * @Date 2016年6月8日 下午7:24:55
 * @version 1.0
 */
public class MoneyMap {
	
	//key:用户  value:钱数
//	public static Map<String,Integer> mapAll = new HashMap<String, Integer>();
	//key:用户 value:汇总信息打印
	public static Map<String,JinErHuiZong> maptemp = new HashMap<String, JinErHuiZong>();
	
	//key:用户 value:汇总信息打印
//	public static Map<String,List<JiangXiang>> xiazhuMap = new HashMap<String, List<JiangXiang>>();
	
	//key用户: value：Map<类型，钱数>
	public static Map<String,Map<String,Integer>>  yonghuMeizhujine = new HashMap<String, Map<String,Integer>>();
	
	//key:uuid,value 备注
	public static Map<String,User> meberMap = new HashMap<String, User>();
	
	//备注:uuid
	public static Map<String,String> beizhuUUid = new HashMap<String, String>();
	
	//key:备注：钱数
//	public static Map<String,Integer> money = new HashMap<String, Integer>();
	
	public static void clear(){
		yonghuMeizhujine.clear();
		maptemp.clear();
	}
   
	public static Map<String, String> map = new HashMap<String, String>();
	static {
		int indx = 1;
		for(int i = 9; i<= 22 ; i ++){
			for(int j = 0; j < 6 ;j ++){
				if(indx == 1){
					j = 2;
				}
				if(0<indx && indx<10){
					if(i <10){
						map.put("0"+i+""+j+"8", "00"+ indx);
					}else{
						map.put(i+""+j+"8", "0"+ indx);
					}
				}else if(10<= indx && indx<100){
					if(i <10){
						map.put("0"+i+""+j+"8", "0"+ indx);
					}else{
						map.put(i+""+j+"8", "0"+ indx);
					}
				}
				indx ++;
			}
		}
		
		map.put("0838", "0828");
		map.put("0848", "0828");
		map.put("0858", "0828");
		map.put("0908", "0828");
		map.put("0918", "0828");
		map.put("0928", "0828");
		map.put("0938", "0828");
		map.put("0948", "0828");
		map.put("0928", "0828");
		map.put("0828", "0828");
	}
}
