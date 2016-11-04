package cn.springmvc.vo;

public class DhQueryVo {
	
	private String segIndex;
	//出发地
	private String depAirpCd;
	//目的地
	private String arrAirpCd;
	//出发时间
	private String depDate;
	//到达时间
	private String arrDate;
	private String retDate;
	private String kamno;
	private String adtCount;
	private String cabinLevel;
	private String flightOrder;
	
	private String jsessionid;
	
	public String getSegIndex() {
		return segIndex;
	}
	public void setSegIndex(String segIndex) {
		this.segIndex = segIndex;
	}
	public String getDepAirpCd() {
		return depAirpCd;
	}
	public void setDepAirpCd(String depAirpCd) {
		this.depAirpCd = depAirpCd;
	}
	public String getArrAirpCd() {
		return arrAirpCd;
	}
	public void setArrAirpCd(String arrAirpCd) {
		this.arrAirpCd = arrAirpCd;
	}
	public String getDepDate() {
		return depDate;
	}
	public void setDepDate(String depDate) {
		this.depDate = depDate;
	}
	public String getRetDate() {
		return retDate;
	}
	public void setRetDate(String retDate) {
		this.retDate = retDate;
	}
	public String getKamno() {
		return kamno;
	}
	public void setKamno(String kamno) {
		this.kamno = kamno;
	}
	public String getAdtCount() {
		return adtCount;
	}
	public void setAdtCount(String adtCount) {
		this.adtCount = adtCount;
	}
	public String getCabinLevel() {
		return cabinLevel;
	}
	public void setCabinLevel(String cabinLevel) {
		this.cabinLevel = cabinLevel;
	}
	public String getFlightOrder() {
		return flightOrder;
	}
	public void setFlightOrder(String flightOrder) {
		this.flightOrder = flightOrder;
	}
	public String getJsessionid() {
		return jsessionid;
	}
	public void setJsessionid(String jsessionid) {
		this.jsessionid = jsessionid;
	}
	public String getArrDate() {
		return arrDate;
	}
	public void setArrDate(String arrDate) {
		this.arrDate = arrDate;
	}
	
}
