package cn.springmvc.vo;

public class ExcelVo {
	//航路	可售航班	舱位	价格	旅行日期	隔日中转
	private String hanglu;
	private String hangban;
	private String changwei;
	private String jiage;
	private String riqi;
	private String zhongzhuan;
	public String getHanglu() {
		return hanglu;
	}
	public void setHanglu(String hanglu) {
		this.hanglu = hanglu;
	}
	public String getHangban() {
		return hangban;
	}
	public void setHangban(String hangban) {
		this.hangban = hangban;
	}
	public String getChangwei() {
		return changwei;
	}
	public void setChangwei(String changwei) {
		this.changwei = changwei;
	}
	public String getJiage() {
		return jiage;
	}
	public void setJiage(String jiage) {
		this.jiage = jiage;
	}
	public String getRiqi() {
		return riqi;
	}
	public void setRiqi(String riqi) {
		this.riqi = riqi;
	}
	public String getZhongzhuan() {
		return zhongzhuan;
	}
	public void setZhongzhuan(String zhongzhuan) {
		this.zhongzhuan = zhongzhuan;
	}
	
}
