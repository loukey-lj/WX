/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.task.StartTimer.java <2016年6月7日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import me.biezhi.weixin.App;
import me.biezhi.weixin.db.MoneyMap;
import me.biezhi.weixin.util.FenZhiChuLi;

/**
 * 机器人开始说话
 * @author liujie<2016年6月7日>
 */
public class StartTimer {
	
	private  App app;
	public StartTimer(App app) {
		super();
		this.app = app;
	}
	public static String qihao;
	public static Integer num = 0;
	public static boolean isStart = false;
	//第8分钟执行一次  每隔10 分钟开始执行一次
	public  void Start8() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 8);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();       
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//产生期号
				// 判断时间范围
				Date date = new Date();
				//开始之前要吧之前的缓存都清理掉
				FenZhiChuLi.clear();
				MoneyMap.clear();
				EndTimer.isEnd = false;
				StartTimer.isStart  = true;
				if(date.getHours() == 22 && date.getMinutes() > 28){
					app.webwxsendmsg(new SimpleDateFormat("yyyyMMdd HH:mm").format(date)+"游戏结束期待明天", app.wxQun);
					EndTimer.isEnd = true;
				}
				
				if(date.getHours() < 9 || date.getHours() > 22){
					app.webwxsendmsg(new SimpleDateFormat("yyyyMMdd HH:mm").format(date)+"游戏结束期待明天", app.wxQun);
					EndTimer.isEnd = true;
				}else{
					String []mat = new SimpleDateFormat("yyyyMMdd HHmm").format(date).split(" ");
					qihao = mat[0] + MoneyMap.map.get(mat[1]);
					app.webwxsendmsg(new SimpleDateFormat("yyyyMMdd HH:mm").format(date)+"新一轮游戏开始了:期号为" + qihao, app.wxQun);
				}
			}
		}, time, 1000 * 60 * 10);// 这里设定将延时每天固定执行
	}
}