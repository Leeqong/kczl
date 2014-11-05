package com.njut.data;

import java.util.ArrayList;
import java.util.List;

//为了存储整个老师列表的信息
public class TeacherListElement {
	public TeacherListElement() {
		super();
		advicelist = new ArrayList<AdviceElement>();
		spitlist = new ArrayList<SpitElement>();
		tcardlist = new ArrayList<TcardElement>();
	}
	private String ctid;
	private List<AdviceElement> advicelist;
	private List<SpitElement> spitlist;
	private List<TcardElement> tcardlist;
	
	public String getCtid() {
		return ctid;
	}
	public void setCtid(String ctid) {
		this.ctid = ctid;
	}
	public List<AdviceElement> getAdvicelist() {
		return advicelist;
	}
	public void setAdvicelist(List<AdviceElement> advicelist) {
		this.advicelist.clear();
		this.advicelist.addAll(advicelist);
	}
	public List<SpitElement> getSpitlist() {
		return spitlist;
	}
	public void setSpitlist(List<SpitElement> spitlist) {
		this.spitlist.clear();
		this.spitlist.addAll(spitlist);
	}

//-------------------------------------------------------	
	public List<TcardElement> getTcardlist() {
		return tcardlist;
	}
	public void setTcardlist(List<TcardElement> tcardlist) {
		this.tcardlist.clear();
		this.tcardlist.addAll(tcardlist);
	}
//-------------------------------------------------------
	
}
