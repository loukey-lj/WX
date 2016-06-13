/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.util.JiSuanJiFen.java <2016年6月8日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.biezhi.weixin.Jiang;
import me.biezhi.weixin.JinErHuiZong;
import me.biezhi.weixin.db.MoneyMap;
import me.biezhi.weixin.redis.RedisUtil;
import me.biezhi.weixin.task.EndTimer;
import me.biezhi.weixin.task.StartTimer;

/**
 *
 * @author liujie 
 * @Date 2016年6月8日 下午7:30:04
 * @version 1.0
 */
public class JiSuanJiFen {
	
	
	public static String xiaZhuJianCha(String liaoTianNeiRong,String userName){
		//第一步获取总金额
		/*if(!MoneyMap.mapAll.containsKey(userName)){
			return "您还没有充值记录,请联系财务";
		}*/
//		Integer allMoneny = MoneyMap.mapAll.get(userName);
		String allMonenyStr = RedisUtil.getValueBykey(userName);
		Integer allMoneny = 0;
		try{
			allMoneny = Integer.valueOf(allMonenyStr);
		}catch(Exception e){
			
		}
		String [] orderArr = liaoTianNeiRong.split(";");
		boolean needFirst = false;
		if(liaoTianNeiRong.contains("查") || liaoTianNeiRong.contains("取") || liaoTianNeiRong.contains("回")){
			needFirst = true;
		}
		if(needFirst){
			if(orderArr.length!=1){
				return "查/取/回 操作请单独输入";
			}
		}
		for(String str : orderArr){
			String[] meiYiZhu = str.split(":");
			String type = meiYiZhu[0];
			if("查".equals(type)){
				return "您的余额："+allMoneny + "";
			}
			
			if("回".equals(type)){
				//获取回额
				Integer amout = Integer.valueOf(meiYiZhu[1]);
				//第一步获取历史下注金额
				JinErHuiZong jine = MoneyMap.maptemp.get(userName);
				int leij = 0;
				if(jine != null){
					leij = jine.getLeiJi();
				}
				if(amout <= 0){
					return "回额不能小于0";
				}
				if(allMoneny - amout - leij < 0){
					return "回款["+amout+"]失败：总金额["+allMoneny+"] - 已下注金额[" + leij + "] = 可回金额[" + (allMoneny-leij) +"]";
				}else{
					return "-1 "+amout;
				}
				//第一步先判断
			}
			if(EndTimer.isEnd || !StartTimer.isStart){
				str = str.replace(":NDY", "");
				return "已封盘:"+ str +" 无效";
			}
			
			if("取".equals(type)){
				//清理 累计金额
				MoneyMap.maptemp.remove(userName);
//				MoneyMap.xiazhuMap.remove(userName);
				MoneyMap.yonghuMeizhujine.remove(userName);
				return "取消下注成功";
			}
			//判断聊天内容
			if(Jiang.map.containsKey(type)){
				try {
					Integer amout = 0;
					//如果是已改字开头
					if(type.startsWith("改")){
						amout = Integer.valueOf(meiYiZhu[1]);
						
						if(amout < 50){
							return "下注金额不能小于50";
						}
						String typeTem = type.replace("改", "");
						//第一步判断金额是否满足
						if(allMoneny < amout){
							return "您的余额：[" + allMoneny + "]不足,"+str+".请联系财务";
						}
						
						//判断是否操作下注范围
						if("小".equals(typeTem) || "大".equals(typeTem) || "单".equals(typeTem) || "双".equals(typeTem)){
							if(amout > 10000){
								return "[大 小 单 双]下注总和不能超过上线 10000";
							}
						}
						
						if("小单".equals(typeTem) || "大单".equals(typeTem) || "小双".equals(typeTem) || "大双".equals(typeTem)){
							if(amout > 5000){
								return "[小单 大单 小双 大双]下注总和不能超过上线 5000";
							}
						}
						
						if("顺".equals(typeTem)){
							if(amout > 2000){
								return "[顺子]下注总和不能超过上线 2000";
							}
						}
						
						if("豹".equals(typeTem)){
							if(amout > 2000){
								return "[豹子]下注总和不能超过上线 2000";
							}
						}
						
						if(typeTem.contains("特")){
							if(amout > 2000){
								return "[特]下注总和不能超过上线 2000";
							}
						}
						
						JinErHuiZong jine = new JinErHuiZong();
						jine.setDeail(type + " " + amout);
						jine.setLeiJi(amout);
						jine.setTotal(allMoneny);
						jine.setName(userName);
						MoneyMap.maptemp.put(userName, jine);
						
						/*List<JiangXiang> list = new ArrayList<JiangXiang>();
						JiangXiang j = new JiangXiang(type,amout);
						list.add(j);
						MoneyMap.xiazhuMap.put(userName,list);*/
						
						MoneyMap.yonghuMeizhujine.remove(userName);
						Map <String,Integer> typeValue = new HashMap<String, Integer>();
						typeValue.put(type, amout);
						MoneyMap.yonghuMeizhujine.put(userName,typeValue);
					}else if(type.startsWith("哈")){
						String typeTem = type.replace("哈", "");
						//第一步判断金额是否满足
						if(allMoneny <= 0 ){
							return "您的余额：[" + allMoneny + "]不足,"+str+".请联系财务";
						}
						amout = allMoneny;
						//判断是否操作下注范围
						if("小".equals(typeTem) || "大".equals(typeTem) || "单".equals(typeTem) || "双".equals(typeTem)){
							if(amout > 10000){
								return "[大 小 单 双]下注总和不能超过上线 10000";
							}
						}
						
						if("小单".equals(typeTem) || "大单".equals(typeTem) || "小双".equals(typeTem) || "大双".equals(typeTem)){
							if(amout > 5000){
								return "[小单 大单 小双 大双]下注总和不能超过上线 5000";
							}
						}
						
						if("顺".equals(typeTem)){
							if(amout > 2000){
								return "[顺子]下注总和不能超过上线 2000";
							}
						}
						
						if("豹".equals(typeTem)){
							if(amout > 2000){
								return "[豹子]下注总和不能超过上线 2000";
							}
						}
						
						if(typeTem.contains("特")){
							if(amout > 2000){
								return "[特]下注总和不能超过上线 2000";
							}
						}
						
						JinErHuiZong jine = new JinErHuiZong();
						jine.setDeail(type + " " + amout);
						jine.setLeiJi(amout);
						jine.setTotal(allMoneny);
						jine.setName(userName);
						MoneyMap.maptemp.put(userName, jine);
						
						/*List<JiangXiang> list = new ArrayList<JiangXiang>();
						JiangXiang j = new JiangXiang(type,amout);
						list.add(j);
						MoneyMap.xiazhuMap.put(userName,list);*/
						
						MoneyMap.yonghuMeizhujine.remove(userName);
						Map <String,Integer> typeValue = new HashMap<String, Integer>();
						typeValue.put(type, amout);
						MoneyMap.yonghuMeizhujine.put(userName,typeValue);
					}
					//正常下注
					else{
						amout = Integer.valueOf(meiYiZhu[1]);
						//第一步获取历史下注金额
						JinErHuiZong jine = MoneyMap.maptemp.get(userName);
						//如果没有下注情况
						if(jine == null){
							//如果金额不足
							if(allMoneny < amout){
								return "您的余额：[" + allMoneny + "]不足,"+str+".请联系财务";
							}
							
							//判断是否操作下注范围
							if("小".equals(type) || "大".equals(type) || "单".equals(type) || "双".equals(type)){
								if(amout > 10000){
									return "[大 小 单 双]下注总和不能超过上线 10000";
								}
							}
							
							if("小单".equals(type) || "大单".equals(type) || "小双".equals(type) || "大双".equals(type)){
								if(amout > 5000){
									return "[小单 大单 小双 大双]下注总和不能超过上线 5000";
								}
							}
							
							if("顺".equals(type)){
								if(amout > 2000){
									return "[顺子]下注总和不能超过上线 2000";
								}
							}
							
							if("豹".equals(type)){
								if(amout > 2000){
									return "[豹子]下注总和不能超过上线 2000";
								}
							}
							
							if(type.contains("特")){
								if(amout > 2000){
									return "[特]下注总和不能超过上线 2000";
								}
							}
							
							jine = new JinErHuiZong();
							jine.setDeail(type + " " + amout);
							jine.setLeiJi(amout);
							jine.setTotal(allMoneny);
							jine.setName(userName);
							MoneyMap.maptemp.put(userName, jine);
							
							/*List<JiangXiang> list = new ArrayList<JiangXiang>();
							JiangXiang j = new JiangXiang(type,amout);
							list.add(j);
							MoneyMap.xiazhuMap.put(userName,list);*/
							
							
							Map <String,Integer> typeValue = new HashMap<String, Integer>();
							typeValue.put(type, amout);
							MoneyMap.yonghuMeizhujine.put(userName,typeValue);
						}
						//如果有下注情况
						else{
							//获取累计金额
							Integer leji = jine.getLeiJi();
							if(allMoneny < leji + amout){
								return "您的余额：[" + allMoneny + "],不足当前下注["+str+"],已下注["+jine.getDeail()+"].请联系财务";
							}
							
						    
							//判断是否操作下注范围
							//获取历史下注情况
							Map <String,Integer> typeValue = MoneyMap.yonghuMeizhujine.get(userName);
							
							if("小".equals(type) || "大".equals(type) || "单".equals(type) || "双".equals(type)){
								Integer da = typeValue.get("大");
								Integer xiao = typeValue.get("小");
								Integer dan = typeValue.get("单");
								Integer shuan = typeValue.get("双");
								if(da == null){
									da = 0;
								}
								if(xiao == null){
									xiao = 0;
								}
								if(dan == null){
									dan = 0;
								}
								if(shuan == null){
									shuan = 0;
								}
								if(amout + da + xiao + dan + shuan > 10000){
									return "[大 小 单 双]下注总和不能超过上线 10000";
								}
							}
							
							if("小单".equals(type) || "大单".equals(type) || "小双".equals(type) || "大双".equals(type)){
								Integer xiaodan = typeValue.get("小单");
								Integer dadan = typeValue.get("大单");
								Integer xiaoshuan = typeValue.get("小双");
								Integer dashuan = typeValue.get("大双");
								if(xiaodan == null){
									xiaodan = 0;
								}
								if(dadan == null){
									dadan = 0;
								}
								if(xiaoshuan == null){
									xiaoshuan = 0;
								}
								if(dashuan == null){
									dashuan = 0;
								}
								
								if(amout + xiaodan + dadan + xiaoshuan + dashuan > 5000){
									return "[小单 大单 小双 大双]下注总和不能超过上线 5000";
								}
							}
							
							if("顺".equals(type)){
								Integer shun = typeValue.get("顺");
								if(shun == null){
									shun = 0;
								}
								
								if(amout + shun > 2000){
									return "[顺子]下注总和不能超过上线 2000";
								}
							}
							
							if("豹".equals(type)){
								Integer bao = typeValue.get("豹");
								if(bao == null){
									bao = 0;
								}
								
								if(amout + bao > 2000){
									return "[豹子]下注总和不能超过上线 2000";
								}
							}
							
							if(type.contains("特")){
								for(int i = 4; i<= 17; i ++){
									Integer te = typeValue.get("特"+i);
									if(te == null){
										te = 0;
									}
									amout = amout + te;
								}
								
								if(amout > 2000){
									return "[特]下注总和不能超过上线 2000";
								}
							}
							
							
							jine.setDeail(jine.getDeail() + "," + type + " " + amout);
							jine.setLeiJi(amout + leji);
							jine.setTotal(allMoneny);
							jine.setName(userName);
							MoneyMap.maptemp.put(userName, jine);
							
							/*JiangXiang j = new JiangXiang(type,amout);
							List<JiangXiang> list = MoneyMap.xiazhuMap.get(userName);
							list.add(j);
							MoneyMap.xiazhuMap.put(userName,list);*/
							
							Integer older  = typeValue.get(type);
							if(older == null){
								older = 0;
							}
							typeValue.put(type, amout + older);
							MoneyMap.yonghuMeizhujine.put(userName,typeValue);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				return "您输入了无效的内容:"+type;
			}
		}
		return null;
	}

	public static String  getHuiZong(){
		//循环 下单map
		Map<String,JinErHuiZong> map = MoneyMap.maptemp;
		Iterator<String> it = map.keySet().iterator();
		StringBuffer buf = new StringBuffer();
		while(it.hasNext()){
			buf.append(map.get(it.next()).toString()).append("\n");
		}
		return buf.toString();
	}
	
}
