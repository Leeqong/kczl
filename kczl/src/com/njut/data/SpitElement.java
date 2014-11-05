package com.njut.data;

import java.io.Serializable;
import java.util.List;

/*吐槽相关的数据的抽象*/
public class SpitElement implements Serializable{

	public List<CommentElement> getCommmentlist() {
		return commmentlist;
	}

	public void setCommmentlist(List<CommentElement> commmentlist) {
		this.commmentlist = commmentlist;
	}

	public String getCoursename() {
		return coursename;
	}

	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}

	public String getSpitid() {
		return spitid;
	}

	public void setSpitid(String spitid) {
		this.spitid = spitid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public SpitElement(String schoolnumber, String content, String ctid,
			int likenum, String everlike, String coursename, String spitid,
			String timestamp) {
		super();
		this.schoolnumber = schoolnumber;
		this.content = content;
		this.ctid = ctid;
		this.likenum = likenum;
		this.everlike = everlike;
		this.coursename = coursename;
		this.spitid = spitid;
		this.timestamp = timestamp;
	}

	public SpitElement() {
		// TODO Auto-generated constructor stub
	}

	private String schoolnumber;
	private String content;
	private String ctid;
	private int likenum;
	private String everlike;
	private String coursename;
	private String spitid;
	private String timestamp;
	private List<CommentElement> commmentlist;

	public int getLikenum() {
		return likenum;
	}

	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}

	public String getEverlike() {
		return everlike;
	}

	public void setEverlike(String everlike) {
		this.everlike = everlike;
	}

	public String getSchoolnumber() {
		return schoolnumber;
	}

	public void setSchoolnumber(String schoolnumber) {
		this.schoolnumber = schoolnumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCtid() {
		return ctid;
	}

	public void setCtid(String ctid) {
		this.ctid = ctid;
	}

}
