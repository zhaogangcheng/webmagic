package cn.springmvc.vo;

import java.util.Date;

public class HQQueryVo {
	//出发地点
	private String from;
	
	//到达地点]
	private String arrive;
	
	//出发时间 起始
	private Date starttime;
	
	//出发时间 终止
	private Date endtime;
	
	//sessionid
	private String jsessionid;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getArrive() {
		return arrive;
	}

	public void setArrive(String arrive) {
		this.arrive = arrive;
	}

	

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public String getJsessionid() {
		return jsessionid;
	}

	public void setJsessionid(String jsessionid) {
		this.jsessionid = jsessionid;
	}
	
	
	

}
