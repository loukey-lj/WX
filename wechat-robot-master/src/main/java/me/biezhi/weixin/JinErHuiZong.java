/*
 *Project: wechat-robot
 *File: me.biezhi.weixin.JinErHuiZong.java <2016年6月8日>
 ****************************************************************
 * 版权所有@2016 国裕网络科技  保留所有权利.
 ***************************************************************/
package me.biezhi.weixin;

/**
 *
 * @author liujie 
 * @Date 2016年6月8日 下午7:56:55
 * @version 1.0
 */
public class JinErHuiZong {
	
	private String name;
	private String deail;
	private Integer total;
	private Integer leiJi;
	
	public Integer getLeiJi() {
		return leiJi;
	}
	public void setLeiJi(Integer leiJi) {
		this.leiJi = leiJi;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDeail() {
		return deail;
	}
	public void setDeail(String deail) {
		this.deail = deail;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	@Override
	public String toString() {
		return "[" + name + "]积分:" + total + "[" + deail + "]";
	}
	
	
	
}
