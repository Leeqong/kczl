package com.njut.data;

import java.io.Serializable;


public class CommentElement implements Serializable{
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public CommentElement() {
		super();
	}
	public CommentElement(String edid, String commentid, String content,
			String fromuser, String touser, String timestap,String type) {
		super();
		this.edid = edid;
		this.commentid = commentid;
		this.content = content;
		this.fromuser = fromuser;
		this.touser = touser;
		this.timestap = timestap;
		this.type = type;
	}
	String edid;//caid与spitid共用
	String commentid;
    String content;
    String fromuser;
    String touser;
    String timestap;
    String type;//为了删除评论时区分是评一评还是说一说  spit  advice
    
      public String getEdid() {
		return edid;
	}
	public void setEdid(String edid) {
		this.edid = edid;
	}
	public String getCommentid() {
		return commentid;
	}
	public void setCommentid(String commentid) {
		this.commentid = commentid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFromuser() {
		return fromuser;
	}
	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getTimestap() {
		return timestap;
	}
	public void setTimestap(String timestap) {
		this.timestap = timestap;
	}

      
	
}
