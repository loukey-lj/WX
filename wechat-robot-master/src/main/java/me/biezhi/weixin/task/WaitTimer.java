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

/**
 * 机器人开始说话
 * @author liujie<2016年6月7日>
 */
public class WaitTimer {
	
	private  App app;
	public WaitTimer(App app) {
		super();
		this.app = app;
	}
	
	//第7分钟执行一次  每隔10 分钟开始执行一次
	public  void Start7() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 4);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				StringBuffer buffer = new StringBuffer();
				buffer.append(new SimpleDateFormat("yyyyMMdd HH:mm").format(new Date())+"第"+ StartTimer.qihao +"期下注倒计时60秒" +"\n");
				buffer.append("---距离封盘还有60秒---\n");
				buffer.append("●如需上分请添加本群微信财务，转账后请一定在微信群内发 查 后面带数字，数字只能识别【阿拉伯数字】，如:查1000。其他形式的数字电脑识别不了，投注无效！\n");
				buffer.append("●如取消投注即微信发送【 取消 】，改注请在前面加【 改 】\n");
				buffer.append("●软件机器人更新！可支持【 哈注 】，但请各位注意哈注金额，必需在上限范围内！！");
				app.webwxsendmsg(buffer.toString(), app.wxQun);
			}
		}, time, 1000 * 60 * 10);// 这里设定将延时每天固定执行
	}
}