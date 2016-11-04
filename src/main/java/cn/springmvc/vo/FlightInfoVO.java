package cn.springmvc.vo;

public class FlightInfoVO {
	//1:航班代码  2:燃油费机建费 3:起飞时间 4：起飞地点 5：到站时间 6：到站地点   
	//1:R N 航班  2:剩余座位 3:票面价格svg 4：代理返利 5：支付金额 
	private int id;
	private String flightCode;
	private String flightFuelPay;
	private String flightStartFlyTime;
	private String flightStartFlyAddress;
	private String flightArrivedTime;
	private String flightArrivedAddress;
	private String flightRN;
	private String flightLeftSeat;
	private String flightPrice;
	private String flightAgentPay;
	private String flightLastPay;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFlightCode() {
		return flightCode;
	}
	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}
	public String getFlightFuelPay() {
		return flightFuelPay;
	}
	public void setFlightFuelPay(String flightFuelPay) {
		this.flightFuelPay = flightFuelPay;
	}
	public String getFlightStartFlyTime() {
		return flightStartFlyTime;
	}
	public void setFlightStartFlyTime(String flightStartFlyTime) {
		this.flightStartFlyTime = flightStartFlyTime;
	}
	public String getFlightStartFlyAddress() {
		return flightStartFlyAddress;
	}
	public void setFlightStartFlyAddress(String flightStartFlyAddress) {
		this.flightStartFlyAddress = flightStartFlyAddress;
	}
	public String getFlightArrivedTime() {
		return flightArrivedTime;
	}
	public void setFlightArrivedTime(String flightArrivedTime) {
		this.flightArrivedTime = flightArrivedTime;
	}
	public String getFlightArrivedAddress() {
		return flightArrivedAddress;
	}
	public void setFlightArrivedAddress(String flightArrivedAddress) {
		this.flightArrivedAddress = flightArrivedAddress;
	}
	public String getFlightRN() {
		return flightRN;
	}
	public void setFlightRN(String flightRN) {
		this.flightRN = flightRN;
	}
	public String getFlightLeftSeat() {
		return flightLeftSeat;
	}
	public void setFlightLeftSeat(String flightLeftSeat) {
		this.flightLeftSeat = flightLeftSeat;
	}
	public String getFlightPrice() {
		return flightPrice;
	}
	public void setFlightPrice(String flightPrice) {
		this.flightPrice = flightPrice;
	}
	public String getFlightAgentPay() {
		return flightAgentPay;
	}
	public void setFlightAgentPay(String flightAgentPay) {
		this.flightAgentPay = flightAgentPay;
	}
	public String getFlightLastPay() {
		return flightLastPay;
	}
	public void setFlightLastPay(String flightLastPay) {
		this.flightLastPay = flightLastPay;
	}
	

}
