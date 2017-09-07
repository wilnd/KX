package mocent.Monitor.Entity;

import java.util.Date;

/**
 * 用户呼叫后的对象
 * @author Administrator
 *
 */
public class CallService {

	private int car_id;
	
	private int longitude;
	
	private int latitude;
	
	private int speed;
	
	private int direction;
	
	private int from;
	
	//1-我在处理，2-待处理，3-别人在处理
	private int State=2;
	
	private Date deal_time;
	
	private Date call_time;
	
	private int deal_id;

	public int getCar_id() {
		return car_id;
	}

	public void setCar_id(int car_id) {
		this.car_id = car_id;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	public Date getCall_time() {
		return call_time;
	}

	public void setCall_time(Date call_time) {
		this.call_time = call_time;
	}

	public Date getDeal_time() {
		return deal_time;
	}

	public void setDeal_time(Date deal_time) {
		this.deal_time = deal_time;
	}

	public int getDeal_id() {
		return deal_id;
	}

	public void setDeal_id(int deal_id) {
		this.deal_id = deal_id;
	}
}
