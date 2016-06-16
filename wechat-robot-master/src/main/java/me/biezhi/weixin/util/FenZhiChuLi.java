package me.biezhi.weixin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.biezhi.weixin.db.MoneyMap;
import me.biezhi.weixin.map.FenZhi;
import me.biezhi.weixin.redis.RedisUtil;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

public class FenZhiChuLi {
    private static final Logger LOGGER = LoggerFactory.getLogger(FenZhiChuLi.class);
    
    public static int num1;
    public static int num2;
    public static int num3;
    public static List<String> kaijiang = new ArrayList<String>();
    public static Map<String,Integer> jifenMap=new HashMap<String, Integer>(); 
    
    
    public static void clear(){
    	kaijiang.clear();
    	jifenMap.clear();
	}
    
    /**
     * 得到处理结果
     * @param numperiods 期数
     * @param num1  第一个数字
     * @param num2  第二个数字
     * @param num3 	第三个数字
     */
    public static  void kaijiang(){
    	List<String> list=new ArrayList<String>();
    	String type="";//单双
    	String type1="";//大小
    	String type2="";//大双、大单、小双、小单
    	String type3="";//顺子、豹子
    	int tNum=num1+num2+num3;//总和
    	if(tNum%2==1){
    		type="单";
    		if(3<=tNum&&tNum<=9){
    			type2="小单";
    		}else if(12<=tNum&&tNum<=18){
    			type2="大单";
    		}
    	}else if(tNum%2==0){
    		type="双";
    		if(3<=tNum&&tNum<=9){
    			type2="小双";
    		}else if(12<=tNum&&tNum<=18){
    			type2="大双";
    		}
    	}
    	if(3<=tNum&&tNum<=10){
    		type1="小";
    	}else if(11<=tNum&&tNum<=18){
    		type1="大";
    	}
    	if(num2==num1+1&&num3==num2+1){
    		type3="顺";
    	}else if(num1==num2&&num2==num3){
    		type3="豹";
    	}
    	if(type!=""){
    		list.add(type);
    	}
    	if(type1!=""){
    		list.add(type1);
    	}
    	if(type2!=""){
    		list.add(type2);
    	}
    	if(type3!=""){
    		list.add(type3);
    	}
    	list.add(String.valueOf(tNum)+"特");
    	kaijiang = list;
    }
    
    
    /**
     * 
     * @param list  根据开奖结果得到不同玩法的结果
     * @param map   根据用户下注结果得到所有用户下注账单
     * @return
     */
    public static  void jieguo(){
    	Map<String,Integer> fenzhiMap=FenZhi.map;   //玩法规则对应赔率map
   		Set<String> remarkSet=MoneyMap.yonghuMeizhujine.keySet();//获取下注账单的用户集合
		for(String remark:remarkSet){
			Map<String, Integer> map1=MoneyMap.yonghuMeizhujine.get(remark);
			Set<String> typeSet=map1.keySet();//获取用户下注类型集合
			int jifen=0;//用户所有下注获取的积分
			for(String type:typeSet){
				for (int i=0 ;i<kaijiang.size();i++) {
					if(type.contains("改")){
						type=type.split("改")[1];
					}
					if(type.equals(kaijiang.get(i))){//中奖则根据赔率计算得到积分
						jifen+=map1.get(type)*fenzhiMap.get(type);
						break;
					}else{//不中奖则减去押注本金
						if(i==kaijiang.size()-1){
							jifen-=map1.get(type);
						}
					}
				}
			}
			Integer mum = 0;
			try {
				String xx = RedisUtil.getValueBykey(remark);
				mum = Integer.valueOf(xx);
				mum = mum + jifen;
			} catch (Exception e) {
				
			}
			jifenMap.put(remark, mum);
			RedisUtil.putkeyValue(remark, mum + "");
    	}
    	
    }
    
    public static void main(String[] args) {
    	/*Map<String,Map<String,Integer>> map=new HashMap<String, Map<String,Integer>>();
    	Map<String,Integer> map1=new  HashMap<String, Integer>();
    	map1.put("大", 100);
    	map1.put("小", 300);
    	map1.put("大单", 200);
    	map1.put("顺", 100);
    	map1.put("11特", 500);
    	Map<String,Integer> map2=new  HashMap<String, Integer>();
    	map2.put("大", 200);
    	map2.put("小双", 300);
    	map2.put("大单", 200);
    	map2.put("豹", 100);
    	map.put("杨旋", map1);
    	map.put("刘杰", map2);
    	Map<String,Integer> jifenMap=new HashMap<String, Integer>(); //库的用户积分
    	jifenMap.put("杨旋", 1000);
    	jifenMap.put("刘杰", 1000);
    	FenZhiChuLi fenzhi=new FenZhiChuLi();
    	FenZhiChuLi.kaijiang();
    	//单, 大, 大单, 顺, 15特
    	new FenZhiChuLi().jieguo();*/
    	
    	
    	String s="改大100";
    	System.out.println(s.split("改")[1]);
    	
	}
}
