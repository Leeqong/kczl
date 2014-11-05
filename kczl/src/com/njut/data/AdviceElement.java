package com.njut.data;

import java.io.Serializable;
import java.util.List;

/*建议相关的数据的抽象*/
public class AdviceElement implements Serializable{

	public String getArrangement() {
		return arrangement;
	}

	public void setArrangement(String arrangement) {
		this.arrangement = arrangement;
	}

	private String schoolnumber;
	private String name;
	private int effect;
	private String date;
	private String content;
	private int likenum;
	private String everlike;
	private String caid;//此条评教的标识
	private String ctid;
	private List<CommentElement> commmentlist;
	private String attendance;
	private String speed;
	private String arrangement;
	
	public String getAttendance() {
		return attendance;
	}

	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	
	
	public String getSchoolnumber() {
		return schoolnumber;
	}

	public void setSchoolnumber(String schoolnumber) {
		this.schoolnumber = schoolnumber;
	}
	
	public String getCtid() {
		return ctid;
	}

	public void setCtid(String ctid) {
		this.ctid = ctid;
	}

	public List<CommentElement> getCommmentlist() {
		return commmentlist;
	}

	public void setCommmentlist(List<CommentElement> commmentlist) {
		this.commmentlist = commmentlist;
	}



	public AdviceElement(String name, int effect, String date,int likenum,String content,String everlike,String caid
		) {
		this.name = name;
		this.effect = effect;
		this.date = date;
		this.likenum = likenum;
		this.content = content;
		this.everlike = everlike;
		this.caid = caid;
	}
	
	public String getCaid() {
		return caid;
	}

	public void setCaid(String caid) {
		this.caid = caid;
	}
	
	public String getEverlike() {
		return everlike;
	}

	public void setEverlike(String everlike) {
		this.everlike = everlike;
	}

	public AdviceElement () {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLikenum() {
		return likenum;
	}

	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}
}

	