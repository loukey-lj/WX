/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.User.java <2016年6月10日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin;

/**
 *
 * @author liujie 
 * @Date 2016年6月10日 下午3:56:43
 * @version 1.0
 */
public class User {
	
	private String NickName;
	private String RemarkName;
	private String wxh;
	
	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	public String getRemarkName() {
		return RemarkName;
	}
	public void setRemarkName(String remarkName) {
		RemarkName = remarkName;
	}
	public String getWxh() {
		return wxh;
	}
	public void setWxh(String wxh) {
		this.wxh = wxh;
	}
	public User(String nickName, String remarkName, String wxh) {
		super();
		NickName = nickName;
		RemarkName = remarkName;
		this.wxh = wxh;
	}
	
	

}
