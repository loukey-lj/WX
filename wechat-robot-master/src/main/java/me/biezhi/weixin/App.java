package me.biezhi.weixin;

import java.awt.EventQueue;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.UIManager;

import me.biezhi.weixin.db.MoneyMap;
import me.biezhi.weixin.redis.RedisPool;
import me.biezhi.weixin.redis.RedisUtil;
import me.biezhi.weixin.task.EndTimer;
import me.biezhi.weixin.task.StartTimer;
import me.biezhi.weixin.task.WaitTimer;
import me.biezhi.weixin.util.CookieUtil;
import me.biezhi.weixin.util.FenZhiChuLi;
import me.biezhi.weixin.util.JSUtil;
import me.biezhi.weixin.util.JiSuanJiFen;
import me.biezhi.weixin.util.Matchers;
import redis.clients.jedis.Jedis;
import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	private String uuid;
	private int tip = 0;
	private String base_uri, redirect_uri, webpush_url = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
	
	private String skey, synckey, wxsid, wxuin, pass_ticket, deviceId = "e" + DateKit.getCurrentUnixTime();
	
	private String cookie;
	private QRCodeFrame qrCodeFrame;
	
	private JSONObject SyncKey, User, BaseRequest;
	
	public String wxQun = null;
	
	/*// 微信联系人列表，可聊天的联系人列表
	private JSONArray MemberList, ContactList;*/
	
	// 微信特殊账号
	private List<String> SpecialUsers = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", "notification_messages");
	
	public App() {
		System.setProperty("jsse.enableSNIExtension", "false");
	}
	
	/**
	 * 获取UUID
	 * @return
	 */
	public String getUUID() {
		String url = "https://login.weixin.qq.com/jslogin";
		HttpRequest request = HttpRequest.get(url, true, 
				"appid", "wx782c26e4c19acffb", 
				"fun", "new",
				"lang", "zh_CN",
				"_" , DateKit.getCurrentUnixTime());
		
		LOGGER.info("[*] " + request);
		
		String res = request.body();
		request.disconnect();

		if(StringKit.isNotBlank(res)){
			String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
			if(null != code){
				if(code.equals("200")){
					this.uuid = Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
					return this.uuid;
				} else {
					LOGGER.info("[*] 错误的状态码: %s", code);
				}
			}
		}
		return null;
	}
	
	/**
	 * 显示二维码
	 * @return
	 */
	public void showQrCode() {
		
		String url = "https://login.weixin.qq.com/qrcode/" + this.uuid;
		
		final File output = new File("temp.jpg");
		
		HttpRequest.post(url, true, 
				"t", "webwx", 
				"_" , DateKit.getCurrentUnixTime())
				.receive(output);

		if(null != output && output.exists() && output.isFile()){
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						qrCodeFrame = new QRCodeFrame(output.getPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	/**
	 * 等待登录
	 */
	public String waitForLogin(){
		this.tip = 1;
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true, 
				"tip", this.tip, 
				"uuid", this.uuid,
				"_" , DateKit.getCurrentUnixTime());
		
		LOGGER.info("[*] " + request.toString());
		
		String res = request.body();
		request.disconnect();

		if(null == res){
			LOGGER.info("[*] 扫描二维码验证失败");
			return "";
		}
		
		String code = Matchers.match("window.code=(\\d+);", res);
		if(null == code){
			LOGGER.info("[*] 扫描二维码验证失败");
			return "";
		} else {
			if(code.equals("201")){
				LOGGER.info("[*] 成功扫描,请在手机上点击确认以登录");
				tip = 0;
			} else if(code.equals("200")){
				LOGGER.info("[*] 正在登录...");
				String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);

				String redirectHost = "wx.qq.com";
				try {
					URL pmURL = new URL(pm);
					redirectHost = pmURL.getHost();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				String pushServer = JSUtil.getPushServer(redirectHost);
				webpush_url = "https://" + pushServer + "/cgi-bin/mmwebwx-bin";

				this.redirect_uri = pm + "&fun=new";
				LOGGER.info("[*] redirect_uri=%s", this.redirect_uri);
				this.base_uri = this.redirect_uri.substring(0, this.redirect_uri.lastIndexOf("/"));
				LOGGER.info("[*] base_uri=%s", this.base_uri);
			} else if(code.equals("408")){
				LOGGER.info("[*] 登录超时");
			} else {
				LOGGER.info("[*] 扫描code=%s", code);
			}
		}
		return code;
	}
	
	private void closeQrWindow() {
		qrCodeFrame.dispose();
	}
	
	/**
	 * 登录
	 */
	public boolean login(){
		
		HttpRequest request = HttpRequest.get(this.redirect_uri);
		
		LOGGER.info("[*] " + request);
		
		String res = request.body();
		this.cookie = CookieUtil.getCookie(request);

		request.disconnect();
		
		if(StringKit.isBlank(res)){
			return false;
		}
		
		this.skey = Matchers.match("<skey>(\\S+)</skey>", res);
		this.wxsid = Matchers.match("<wxsid>(\\S+)</wxsid>", res);
		this.wxuin = Matchers.match("<wxuin>(\\S+)</wxuin>", res);
		this.pass_ticket = Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res);
		
		LOGGER.info("[*] skey[%s]", this.skey);
		LOGGER.info("[*] wxsid[%s]", this.wxsid);
		LOGGER.info("[*] wxuin[%s]", this.wxuin);
		LOGGER.info("[*] pass_ticket[%s]", this.pass_ticket);
		
		this.BaseRequest = new JSONObject();
		BaseRequest.put("Uin", this.wxuin);
		BaseRequest.put("Sid", this.wxsid);
		BaseRequest.put("Skey", this.skey);
		BaseRequest.put("DeviceID", this.deviceId);
		
		return true;
	}
	
	/**
	 * 微信初始化
	 */
	public boolean wxInit(){
		
		String url = this.base_uri + "/webwxinit?r=" + DateKit.getCurrentUnixTime() + "&pass_ticket=" + this.pass_ticket +
				"&skey=" + this.skey;
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", this.BaseRequest);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.info("[*] " + request);
		String res = request.body();
		request.disconnect();
		
		if(StringKit.isBlank(res)){
			return false;
		}
		
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			if(null != jsonObject){
				JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
				if(null != BaseResponse){
					int ret = BaseResponse.getInt("Ret", -1);
					if(ret == 0){
						this.SyncKey = jsonObject.getJSONObject("SyncKey");
						this.User = jsonObject.getJSONObject("User");
						
						StringBuffer synckey = new StringBuffer();
						
						JSONArray list = SyncKey.getJSONArray("List");
						for(int i=0, len=list.size(); i<len; i++){
							JSONObject item = list.getJSONObject(i);
							synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
						}
						
						this.synckey = synckey.substring(1);
						
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 微信状态通知
	 */
	public boolean wxStatusNotify (){
		
		String url = this.base_uri + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + this.pass_ticket;
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("Code", 3);
		body.put("FromUserName", this.User.getString("UserName"));
		body.put("ToUserName", this.User.getString("UserName"));
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.info("[*] " + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return false;
		}
		
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				return ret == 0;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 获取联系人
	 */
	public boolean getContact(){
		
		String url = this.base_uri + "/webwxgetcontact?pass_ticket=" + this.pass_ticket + "&skey=" + this.skey + "&r=" + DateKit.getCurrentUnixTime();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.info("[*] " + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return false;
		}
		
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				if(ret == 0){
					JSONArray MemberList = jsonObject.getJSONArray("MemberList");
					if(null != MemberList){
						//把联系人的uuid和备注放入内存
						//先清理内存
						MoneyMap.meberMap.clear();
						MoneyMap.beizhuUUid.clear();
						Map<String, String> tempMap = new HashMap<String, String>();
						Jedis jdis = RedisPool.getJedis();
						for(int i=0, len=MemberList.size(); i<len; i++){
							JSONObject contact = MemberList.getJSONObject(i);
							String  userName = contact.getString("UserName");
							//公众号/服务号
							if(contact.getInt("VerifyFlag", 0) == 8){
								continue;
							}
							//特殊联系人
							if(SpecialUsers.contains(userName)){
								continue;
							}
							//群聊
							if(userName.indexOf("@@") != -1){
								wxQun = userName;
								continue;
							}
							//自己
							if(userName.equals(this.User.getString("UserName"))){
								continue;
							}
							String  remarkName = contact.getString("RemarkName");
							//有备注
							if(remarkName != null && !"".equals(remarkName.trim())){
								
								if(tempMap.containsKey(remarkName)){
									throw new Exception("备注重复：" + contact.getString("remarkName") );
								}
								
								tempMap.put(remarkName,null);
								//把联系人放入内存中
								User u = new User(contact.getString("NickName"), remarkName, contact.getString("Alias"));
								MoneyMap.meberMap.put(userName, u);
								MoneyMap.beizhuUUid.put(remarkName, userName);
								
							}
							//没有备注
							else{
								throw new Exception("没有备注：" + contact.getString("NickName") );
							}
							
//							ContactList.add(contact);
						}
						tempMap.clear();
						tempMap = null;
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("获取联系人失败"+ e.getMessage());
		}
		return false;
	}
	
	/**
	 * 消息检查
	 */
	public int[] syncCheck(){
		
		int[] arr = new int[2];
		
		String url = this.webpush_url + "/synccheck";
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		
		HttpRequest request = HttpRequest.get(url, true,
				"r", DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5),
				"skey", this.skey,
				"uin", this.wxuin,
				"sid", this.wxsid,
				"deviceid", this.deviceId,
				"synckey", this.synckey,
				"_", System.currentTimeMillis())
				.header("Cookie", this.cookie);
		
		LOGGER.info("[*] " + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return arr;
		}
		
		String retcode = Matchers.match("retcode:\"(\\d+)\",", res);
		String selector = Matchers.match("selector:\"(\\d+)\"}", res);
		if(null != retcode && null != selector){
			arr[0] = Integer.parseInt(retcode);
			arr[1] = Integer.parseInt(selector);
			return arr;
		}
		return arr;
	}
	
	public void webwxsendmsg(String content, String to) {
		if(to == null){
			return;
		}
		String url = this.base_uri + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + this.pass_ticket;
		
		JSONObject body = new JSONObject();
		
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 1);
		Msg.put("Content", content);
		Msg.put("FromUserName", User.getString("UserName"));
		if(to.contains("@@")){
			to = to.substring(1);
			wxQun = to;
		}
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		
		body.put("BaseRequest", this.BaseRequest);
		body.put("Msg", Msg);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.info("[*] " + request);
		request.body();
		request.disconnect();
	}
	
	/**
	 * 获取最新消息
	 */
	public JSONObject webwxsync(){
		
		String url = this.base_uri + "/webwxsync?lang=zh_CN&pass_ticket=" + this.pass_ticket
				 + "&skey=" + this.skey + "&sid=" + this.wxsid + "&r=" + DateKit.getCurrentUnixTime();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("SyncKey", this.SyncKey);
		body.put("rr", DateKit.getCurrentUnixTime());
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.info("[*] " + request);
		String res = request.body();
		request.disconnect();
		
		if(StringKit.isBlank(res)){
			return null;
		}
		
		JSONObject jsonObject = JSON.parse(res).asObject();
		JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
		if(null != BaseResponse){
			int ret = BaseResponse.getInt("Ret", -1);
			if(ret == 0){
				this.SyncKey = jsonObject.getJSONObject("SyncKey");
				
				StringBuffer synckey = new StringBuffer();
				JSONArray list = SyncKey.getJSONArray("List");
				for(int i=0, len=list.size(); i<len; i++){
					JSONObject item = list.getJSONObject(i);
					synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
				}
				this.synckey = synckey.substring(1);
			}
		}
		return jsonObject;
	}
	
	/**
	 * 获取最新消息
	 */
	public void handleMsg(JSONObject data){
		if(null == data){
			return;
		}
		
		JSONArray AddMsgList = data.getJSONArray("AddMsgList");
		
		for(int i=0,len=AddMsgList.size(); i<len; i++){
			
			LOGGER.info("[*] 你有新的消息，请注意查收");
			JSONObject msg = AddMsgList.getJSONObject(i);
			String fromUserName = msg.getString("FromUserName");
			int msgType = msg.getInt("MsgType", 0);
			String name = "";
			//聊天内容
			String content = msg.getString("Content");
			//群聊
			String tempName = "";
			if(fromUserName.contains("@@")){
				String id = content.split(":<br/>")[0];
				if(MoneyMap.meberMap.containsKey(id)){
					name = MoneyMap.meberMap.get(id).getRemarkName();
				}else{
					//先重新获取一遍 联系人
					getContact();
					if(MoneyMap.meberMap.containsKey(id)){
						name = MoneyMap.meberMap.get(id).getRemarkName();
					}
					//重新获取一次之后还不成功
					else{
						webwxsendmsg("新手请加我好友", msg.getString("FromUserName"));
						return;
					}
				}
				
				tempName = MoneyMap.meberMap.get(id).getNickName();
			}
			//私聊
			else{
				String id = msg.getString("FromUserName");
				if(MoneyMap.meberMap.containsKey(id)){
					name = MoneyMap.meberMap.get(id).getRemarkName();
				}else{
					webwxsendmsg("新手请加我好友1", msg.getString("FromUserName"));
					return;
				}
			}
			
			if(msgType == 51){
				LOGGER.info("[*] 成功截获微信初始化消息");
			} else if(msgType == 1){
				if(SpecialUsers.contains(msg.getString("ToUserName"))){
					continue;
				} else if(msg.getString("FromUserName").equals(User.getString("UserName"))){
					continue;
				} else if (msg.getString("ToUserName").indexOf("@@") != -1) {
					String[] peopleContent = content.split(":<br/>");
					LOGGER.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
				} else {
					//根据
					//群聊丢弃用户标识的前一部分
					if(content.contains(":<br/>")){
						String mesg = makeMesg(content.split(":<br/>")[1]);
						if(mesg == null || mesg.trim().equals("")){
							return;
						}
						//积分计算判断积分是否有效
						mesg = JiSuanJiFen.xiaZhuJianCha(mesg, name);
						if(mesg != null){
							content = "@"+tempName+" " + mesg;
							
							LOGGER.info("自动回复:"+name + ": " + content);
							webwxsendmsg(content, msg.getString("FromUserName"));
						}
					}
					//私聊
					else{
						//财务只有财务允许私聊
						if(name.equals("000呃呃呃")){
							//加 备注 钱数
							content = content.trim();
							if(content.startsWith("加") || content.startsWith("减")){
								String []str = content.trim().split(" ");
								if(str == null || str.length !=3){
									webwxsendmsg("格式不符合请按照：加 备注 钱数 输入", msg.getString("FromUserName"));
								}else{
									try {
										Integer num = Integer.valueOf(str[2]);
										String  yonghu = str[1];
										String uuid = MoneyMap.beizhuUUid.get(yonghu);
										if(uuid == null){
											webwxsendmsg("用户不存在，请核对备注", msg.getString("FromUserName"));
											return;
										}
										String res = "";
										if(str[0].equals("加")){
											//修改内存
//											MoneyMap.yonghuMeizhujine
											res  = RedisUtil.addMoney(yonghu,num);
										}else if(str[0].equals("减")){
											res = RedisUtil.addMoney(yonghu,-num);
										}else{
											return;
										}
										//成功
										if(res.contains("OXOTRUE")){
											String res1 = res.replace("OXOTRUE", yonghu);
											//修改通知财务修改成功
											webwxsendmsg(res1, msg.getString("FromUserName"));
											
											//修改钱数成功@用户
											webwxsendmsg("@"+ MoneyMap.meberMap.get(uuid).getNickName() + " " + num + "到账当前余额:" +RedisUtil.getValueBykey(yonghu)  , wxQun);
										}else{
											//修改失败提示财务
											webwxsendmsg(res, msg.getString("FromUserName"));
										}
									} catch (Exception e) {
										webwxsendmsg("钱数格式不符合", msg.getString("FromUserName"));
									}
								}
							}
							else if(content.contains("开奖结果")){
								String []str = content.split(",");
								if(str == null || str.length != 4){
									webwxsendmsg("输入兑奖结果有误", msg.getString("FromUserName"));
								}
								try {
									Integer num1 = Integer.valueOf(str[1]);
									Integer num2 = Integer.valueOf(str[2]);
									Integer num3 = Integer.valueOf(str[3]);
									FenZhiChuLi.num1 = num1;
									FenZhiChuLi.num2 = num2;
									FenZhiChuLi.num3 = num3;
									
									FenZhiChuLi.kaijiang();
									FenZhiChuLi.jieguo();
									
									String temp1  = "欢迎来到鸿辉国际娱乐城\n"
											+ "下注一切以核对账单为准！！！\n"
											+ "下注一切以核对账单为准！！！\n"
											+ "下注一切以核对账单为准！！！\n"
											+ "●福利彩票官方开奖网站: http://caipiao.163.com/t/award/gxkuai3/";
									webwxsendmsg(temp1, wxQun);
									
									String temp2  = StartTimer.qihao +" 期"+new SimpleDateFormat("HH:mm").format(new Date())+"：\n"
											+ "0" + num1 + " 0" + num2 + " 0" + num3 + "=" + ((num1+ num2+ num3) > 9?(num1+ num2+ num3):"0"+(num1+ num2+ num3))
											+ "(xzxz)";
									webwxsendmsg(temp1, wxQun);
									
									//打印兑奖结果
									Jedis j = RedisPool.getJedis();
									Set s = j.keys("*");
									Iterator it = s.iterator();
							        int size = 0;
							        int total = 0;
							        StringBuffer buffer = new StringBuffer();
									while (it.hasNext()) {
										String key = (String) it.next();
										String value = j.get(key);
										
										size ++;
										total = total + Integer.valueOf(value);
										if(value.length() == 2){
											value = "000"+value;
										}else if(value.length() == 3){
											value = "00"+value;
										}else if(value.length() == 4){
											value = "0"+value;
										}else if(value.length() == 1){
											value = "0000"+value;
										}
										buffer.append(key).append(value);
										if(i%2 == 0){
											buffer.append("\n");
										}
									}
									
									
									String str1  = "在线人数"+size+"--总分"+ total + "\n"
											+ "◆◆◆NXMNMA◆◆◆\n"
											+ "==================\n"
											+ "账单只供参考，有错请私聊，安静娱乐少说话";
									webwxsendmsg(str1 + buffer.toString(), wxQun);
								} catch (Exception e) {
									webwxsendmsg("输入兑奖结果有误", msg.getString("FromUserName"));
								}
							}
						}
						
					}
//					LOGGER.info(name + ": " + content);
//					String ans = xiaodoubi(content);
				}
			} else if(msgType == 3){
				webwxsendmsg("二蛋还不支持图片呢", msg.getString("FromUserName"));
			} else if(msgType == 34){
				webwxsendmsg("二蛋还不支持语音呢", msg.getString("FromUserName"));
			} else if(msgType == 42){
				LOGGER.info(name + " 给你发送了一张名片:");
				LOGGER.info("=========================");
			}
		}
	}
	

	//根据聊天内容自动发送消息
	public String makeMesg(String string) {
		string = string.replace(" ", "");
		string = string.replace("加", "");
		string = string.replace("子", "");
		string = string.replace("消", "");
		int i = string.length();
	    List<Integer> list = new ArrayList<Integer>();
	    List<Integer> list1 = new ArrayList<Integer>();
	    List<String> list2 = new ArrayList<String>();
	    List<String> list3 = new ArrayList<String>();
	    Boolean isChinesStart =false;
 		for(int j = 0; j < i; j++){
			char ch = string.charAt(j);
			if(Character.toString(ch).matches("[\u4e00-\u9fa5]+")){
				if(j == 0){
					isChinesStart = true;
				}
				list.add(j);
			}else{
				list1.add(j);
			}
		}
 		
 		int length = 1;
 		for(int k = 0; k < list.size(); k ++ ){
 			int index = list.get(k);
 			if(k != list.size() -1){
 				if(index+1 == list.get(k+1)){
 					length ++;
 				}else{
 					list2.add(string.substring(index-length + 1,index + 1));
 					length = 1;
 				}
 			}else{
 				list2.add(string.substring(index-length + 1,index+ 1));
				length = 1;
 			}
 		}
 		
 		int lengthStr = 1;
 		for(int k = 0; k < list1.size(); k ++ ){
 			int index = list1.get(k);
 			if(k != list1.size() -1){
 				if(index+1 == list1.get(k+1)){
 					lengthStr ++;
 				}else{
 					list3.add(string.substring(index-lengthStr + 1,index + 1));
 					lengthStr = 1;
 				}
 			}else{
 				list3.add(string.substring(index-lengthStr + 1,index+ 1));
				lengthStr = 1;
 			}
 		}
 		
 		StringBuffer buf = new StringBuffer();
 		if(isChinesStart){
 			for(int k = 0; k < list2.size(); k ++ ){
 				String key = list2.get(k);
 				if(Jiang.map.containsKey(key)){
 					try {
 						try {
 							Integer amout = Integer.valueOf(list3.get(k));
 							buf.append(key).append(":").append(amout).append(";");
						} catch (Exception e) {
							buf.append(key).append(":").append("NDY").append(";");
						}
					} catch (Exception e) {
						return null;
					}
 				}
 			}
 		}
 		list.clear(); list=null;
 		list1.clear();list1=null;
 		list2.clear();list2 =null;
 		list3.clear();list3 = null;
 		return buf.toString();
	}

	private final String ITPK_API = "http://i.itpk.cn/api.php";
	
	// 这里的api_key和api_secret可以自己申请一个
	private final String KEY = "?api_key=你的api_key&api_secret=你的api_secret";
	
	private String xiaodoubi(String msg) {
		/*String url = ITPK_API + KEY + "&question=" + msg;
		String result = HttpRequest.get(url).body();
		return result;*/
		return msg;
	}

	/*private String getUserRemarkName(String id) {
		String name = "-1";
		for(int i=0, len=MemberList.size(); i<len; i++){
			JSONObject member = this.MemberList.getJSONObject(i);
			if(member.getString("UserName").equals(id)){
				if(StringKit.isNotBlank(member.getString("RemarkName"))){
					name = member.getString("RemarkName");
				} else {
					name = member.getString("NickName");
				}
				return name;
			}
		}
		return name;
	}*/
	
	public void listenMsgMode(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("[*] 进入消息监听模式 ...");
				int playWeChat = 0;
				while(true){
					
					int[] arr = syncCheck();
					
					LOGGER.info("[*] retcode=%s,selector=%s", arr[0], arr[1]);
					
					if(arr[0] == 1100){
//						LOGGER.info("[*] 你在手机上登出了微信，债见");
//						break;
						arr = syncCheck();
					}
					
					if(arr[0] == 0){
						if(arr[1] == 2){
							JSONObject data = webwxsync();
							handleMsg(data);
						} else if(arr[1] == 6){
							JSONObject data = webwxsync();
							handleMsg(data);
						} else if(arr[1] == 7){
							playWeChat += 1;
							LOGGER.info("[*] 你在手机上玩微信被我发现了 %d 次", playWeChat);
							webwxsync();
						} else if(arr[1] == 3){
						} else if(arr[1] == 0){
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, "listenMsgMode").start();
	}
	
	public static void main(String[] args) throws InterruptedException {
		MoneyMap.map.size();
		System.out.println(JSUtil.getPushServer("wx.qq.com"));

		App app = new App();
		String uuid = app.getUUID();
		if(null == uuid){
			LOGGER.info("[*] uuid获取失败");
		} else {
			LOGGER.info("[*] 获取到uuid为 [%s]", app.uuid);
			app.showQrCode();
			while(!app.waitForLogin().equals("200")){
				Thread.sleep(2000);
			}
			app.closeQrWindow();
			
			if(!app.login()){
				LOGGER.info("微信登录失败");
				return;
			}
			
			LOGGER.info("[*] 微信登录成功");
			
			if(!app.wxInit()){
				LOGGER.info("[*] 微信初始化失败");
				return;
			}
			
			LOGGER.info("[*] 微信初始化成功");
			
			if(!app.wxStatusNotify()){
				LOGGER.info("[*] 开启状态通知失败");
				return;
			}
			
			LOGGER.info("[*] 开启状态通知成功");
			
			if(!app.getContact()){
				LOGGER.info("[*] 获取联系人失败");
				return;
			}
			
			/*LOGGER.info("[*] 获取联系人成功");
			LOGGER.info("[*] 共有 %d 位联系人", app.ContactList.size());*/
			
			//初始化获取联系人
			
			// 监听消息
			app.listenMsgMode();
			
			//mvn exec:java -Dexec.mainClass="me.biezhi.weixin.App"
			
			//启动定时任务
			StartTimer start = new StartTimer(app);
			start.Start8();
			new WaitTimer(app).Start7();
			new EndTimer(app).Start7();
		}
	}
	
}