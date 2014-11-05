package com.njut.data;

public class TcardElement {
	
	private int arrangement;
	private int attendance;
	private int fivestar;
	private int fourstar;
	private int threestar;
	private int twostar;
	private int onestar;
	private int total;
	private int learningEffect;//服务器返回的总是整数
	private int speed;
	private int num;
	private String datestamp;
	
	public String getDatestamp() {
		return datestamp;
	}
	public void setDatestamp(String datestamp) {
		this.datestamp = datestamp;
	}
	public int getArrangement() {
		return arrangement;
	}
	public void setArrangement(int arrangement) {
		this.arrangement = arrangement;
	}
	public int getAttendance() {
		return attendance;
	}
	public void setAttendance(int attendance) {
		this.attendance = attendance;
	}
	public int getFivestar() {
		return fivestar;
	}
	public void setFivestar(int fivestar) {
		this.fivestar = fivestar;
	}
	public int getFourstar() {
		return fourstar;
	}
	public void setFourstar(int fourstar) {
		this.fourstar = fourstar;
	}
	public int getThreestar() {
		return threestar;
	}
	public void setThreestar(int threestar) {
		this.threestar = threestar;
	}
	public int getTwostar() {
		return twostar;
	}
	public void setTwostar(int twostar) {
		this.twostar = twostar;
	}
	public int getOnestar() {
		return onestar;
	}
	public void setOnestar(int onestar) {
		this.onestar = onestar;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getLearningEffect() {
		return learningEffect;
	}
	public void setLearningEffect(int learningEffect) {
		this.learningEffect = learningEffect;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
