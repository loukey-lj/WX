/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.redis.RedisUtil.java <2016年6月10日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin.redis;

import redis.clients.jedis.Jedis;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

/**
 *
 * @author liujie 
 * @Date 2016年6月10日 下午2:08:14
 * @version 1.0
 */
public class RedisUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

	public static String getValueBykey(String key){
		Jedis jdis = RedisPool.getJedis();
		return jdis.get(key);
	}
	
	public static void putkeyValue(String key,String value){
		Jedis jdis = RedisPool.getJedis();
		jdis.set(key, value);
	}
	
	public static boolean isexitKey(String key){
		Jedis jdis = RedisPool.getJedis();
		return jdis.exists(key);
	}
	
	public static void main(String[] args) {
		System.out.println(RedisUtil.isexitKey("网盘们"));
		RedisUtil.putkeyValue("网盘们", 100+"");
		System.out.println(RedisUtil.getValueBykey("网盘们"));
		System.out.println(RedisUtil.getValueBykey("一个陌生的人1111"));
	}

	public static String addMoney(String name, Integer num) {
		Integer money = 0;
		try {
			//如果用户没有冲过值
			if(!RedisUtil.isexitKey(name)){
				if(num > 0){
					putkeyValue(name, num + "");
					return "用户:OXOTRUE;本次操作积分 "+ (num>0?"+":"")+num+ "; 总积分："+ num;
				}else{
					return "初次充值金额不能小于=0";
				}
			}
			money = Integer.valueOf(RedisUtil.getValueBykey(name));
			money = money + num;
			if(money < 0){
				money = 0;
			}
			putkeyValue(name, money + "");
			LOGGER.info("用户:" + name +";本次操作积分 "+ num + "; 总积分："+ money);
			return "用户:OXOTRUE;本次操作积分 "+ (num>0?"+":"")+num+ "; 总积分："+ money;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("操作积分失败："+ e.getMessage());
	   }
		return "操作失败";
	}
}
