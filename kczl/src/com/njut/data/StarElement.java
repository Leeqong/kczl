package com.njut.data;

public class StarElement {
	public StarElement(int starName, int starValue) {
		super();
		this.starName = starName;
		this.starValue = starValue;
	}
	public int getStarName() {
		return starName;
	}
	public void setStarName(int starName) {
		this.starName = starName;
	}
	public int getStarValue() {
		return starValue;
	}
	public void setStarValue(int starValue) {
		this.starValue = starValue;
	}
	private int starName;
	private int starValue;

}
