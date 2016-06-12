/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.map.CacheManager.java <2016年6月7日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 **************************************************************
package me.biezhi.weixin.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.biezhi.weixin.Jiang;

*//**
 *
 * @author liujie 
 * @Date 2016年6月7日 上午9:06:53
 * @version 1.0
 *//*
public class CacheManager {
	
	//某个时间段 里面有多少key 然后根据这些key可以查的这段时间的所有用户下注信息
	public static Map<String, Map<String,Object>> timeMap = new HashMap<String, Map<String,Object>>();
	
	//某个用户一天有多少key,然后根据这些key可以查的用户当天的下注信息
	public static Map<String, Map<String,Object>> userMap = new HashMap<String, Map<String,Object>>();
	
	//每个轮回每个用户的下单明细，循环之后 再汇总
	public static Map<String,Map<String,Integer>> orderMap = new HashMap<String, Map<String,Integer>>();
	
	public static Map<String,Integer> moneyMap = new ConcurrentHashMap<String, Integer>();
	
	//维护当前事前的key
    private static String timeKey  = null;
	
	public static void put2OrderMap(String finalKey,String type,Integer amout){
		if(orderMap.containsKey(finalKey)){
			Map<String,Integer> map = orderMap.get(finalKey);
			if(map.containsKey(type)){
				type = type + "_" + System.currentTimeMillis();
			}
			map.put(type, amout);
		}else{
			Map<String,Integer> map = new HashMap<String,Integer>();
			map.put(type, amout);
			orderMap.put(finalKey, map);
		}
	}
	
	public static void put2TimeMap(String timeKey, String finalKey){
		if(timeMap.containsKey(timeKey)){
			Map<String,Object> map = timeMap.get(timeKey);
			if(!map.containsKey(finalKey)){
				map.put(finalKey, null);
			}
		}else{
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(finalKey,null);
			timeMap.put(timeKey, map);
		}
	}
	
	public static void put2UserMap(String userKey, String finalKey){
		if(userMap.containsKey(userKey)){
			Map<String,Object> map = userMap.get(userKey);
			if(!map.containsKey(finalKey)){
				map.put(finalKey, null);
			}
		}else{
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(finalKey,null);
			userMap.put(userKey, map);
		}
	}
  
	public static void put2MoneyMap(String userKey, Integer amout){
		if(moneyMap.containsKey(userKey)){
			moneyMap.put(userKey, moneyMap.get(userKey) + amout);
		}else{
			moneyMap.put(userKey,amout);
		}
	}
	
	public static String getTimeKey() {
		return timeKey;
	}

	public static void setTimeKey(String timeKey) {
		//获取当前的
		CacheManager.timeKey = timeKey;
	}

	public static void main(String[] args) {
		String time = "2016060711";
		CacheManager.put2TimeMap(time,time+"_刘杰");
		CacheManager.put2TimeMap(time,time+"_刘杰");
		CacheManager.put2TimeMap(time,time+"_刘杰2");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰3");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰36");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰35");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰34");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰33");
		CacheManager.put2TimeMap(time+"2",time+"2_刘杰32");
		Iterator<String> iter = timeMap.keySet().iterator();
		while(iter.hasNext()){
			String timeKey = iter.next();
			Iterator<String> finalKeys = timeMap.get(timeKey).keySet().iterator();
			while(finalKeys.hasNext()){
				String finalKey = finalKeys.next();
				System.out.println(timeKey + ":"+ finalKey);
			}
		}
		
		String time = "2016060711";
		CacheManager.put2UserMap(time,time+"_刘杰");
		CacheManager.put2UserMap(time,time+"_刘杰");
		CacheManager.put2UserMap(time,time+"_刘杰2");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰3");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰36");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰35");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰34");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰33");
		CacheManager.put2UserMap(time+"2",time+"2_刘杰32");
		Iterator<String> iter = userMap.keySet().iterator();
		while(iter.hasNext()){
			String timeKey = iter.next();
			Iterator<String> finalKeys = userMap.get(timeKey).keySet().iterator();
			while(finalKeys.hasNext()){
				String finalKey = finalKeys.next();
				System.out.println(timeKey + ":"+ finalKey);
			}
		}
		
		String time = "2016060711";
		String time1 = "2016060712";
		CacheManager.put2OrderMap(time,"大",100);
		CacheManager.put2OrderMap(time,"小",100);
		CacheManager.put2OrderMap(time,"单",299);
		CacheManager.put2OrderMap(time,"小",100);
		CacheManager.put2OrderMap(time,"单",299);
		CacheManager.put2OrderMap(time,"大",100);
		CacheManager.put2OrderMap(time1,"小",100);
		CacheManager.put2OrderMap(time1,"大",100);
		CacheManager.put2OrderMap(time1,"小",100);
		CacheManager.put2OrderMap(time1,"大",100);
		Iterator<String> iter = orderMap.keySet().iterator();
		while(iter.hasNext()){
			String finalkey = iter.next();
			Map<String, Integer> map = orderMap.get(finalkey);
			Iterator<String> types = map.keySet().iterator();
			System.out.println(finalkey);
			while(types.hasNext()){
				String type = types.next();
				Integer value = map.get(type);
				System.out.println(type+":" + value);
			}
		}
	}
	
	*//**
	 * 查询每个人一轮的汇总
	 * @author liujie<2016年6月8日>
	 * @return
	 *//*
	public static String  getTotalOrder(){
		Iterator<String> iter = orderMap.keySet().iterator();
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		StringBuffer buf = new StringBuffer();
		while(iter.hasNext()){
			String finalkey = iter.next();
			String userName = finalkey.split("_")[1];
			buf.append(userName);
			Map<String, Integer> map = orderMap.get(finalkey);
			Iterator<String> types = map.keySet().iterator();
			while(types.hasNext()){
				String type = types.next();
				Integer value = map.get(type);
				if(Jiang.map.containsKey(type)){
					
				}
				totalMap.put(type, value);
				System.out.println(type+":" + value);
			}
		}
	}
}
*/