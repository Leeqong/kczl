package com.njut.data;

public class MessageElement {


	private String id = "-1";
	private String content;
	private String edid;
	private String type;//SPIT或者ADVICE
	private String userType;//S或者T
	private String viewed;//true false
	private String timestamp;

	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getViewed() {
		return viewed;
	}
	public void setViewed(String viewed) {
		this.viewed = viewed;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEdid() {
		return edid;
	}
	public void setEdid(String edid) {
		this.edid = edid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

	
}
