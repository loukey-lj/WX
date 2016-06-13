/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.task.StartTimer.java <2016年6月7日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin.task;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import me.biezhi.weixin.App;
import me.biezhi.weixin.util.JiSuanJiFen;

/**
 * 机器人开始说话
 * @author liujie<2016年6月7日>
 */
public class EndTimer {
	
	public static boolean isEnd = false;
	private  App app;
	public EndTimer(App app) {
		super();
		this.app = app;
	}
	
	//第7分钟执行一次  每隔10 分钟开始执行一次
	public  void Start7() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				isEnd = true;
				StartTimer.isStart = false;
				StringBuffer buffer = new StringBuffer();
				buffer.append("--------------------\n");
				buffer.append("★★★ 封盘 ★★★ \n");
				buffer.append("下注信息别撤回，否则只吃不赔，超注无效！超注无效！请熟读群规！各位玩家提高警惕，有骗子冒充我们管理号！认准我们唯一宝号：\n");
				buffer.append("xtxw94@163.com(*少冬)\n");
				buffer.append("--------------------");
				app.webwxsendmsg(buffer.toString(), app.wxQun);
				
				StringBuffer buffer2 = new StringBuffer();
				buffer2.append("下注核对：\n");
				buffer2.append("══════════\n");
				buffer2.append(JiSuanJiFen.getHuiZong());
				buffer2.append("══════════\n");
				buffer2.append("如遇网络延迟！一切下注以核对账单为准！\n");
				buffer2.append("账单余分低于下注金额，上核对账单的，视为无效下注，不会扣也不会加！\n");
				buffer2.append("有误私聊管理！\n");
				app.webwxsendmsg(buffer2.toString(), app.wxQun);
				
				
				app.webwxsendmsg("回水组合规则如下:(组合是指除大、小、单、双以外的其他投注例如:小双、小单、大双、大单以及点数.顺子.豹子等等)--组合比例未超过总投注额百分之30的无回水，回水比例:输2000以下无回水,2000-10000，回水8% 。10001-20000，回水10%。20001-30000，回水12%。30000以上15%，2000以下无回水，回水时间:当晚22:30统算，次日9:30之前回水-鸿辉娱乐城祝大家玩的开心", app.wxQun);
				
			}
		}, time, 1000 * 60 * 10);// 这里设定将延时每天固定执行
	}
}