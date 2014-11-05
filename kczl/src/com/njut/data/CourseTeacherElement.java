package com.njut.data;

public class CourseTeacherElement {
	

	public CourseTeacherElement(String coursename, String natureclass,
			String ctid, String courseCategory, String courseNature,
			String credit, int beginSection, int endSection, int beginWeek,
			int endWeek, int day, String oddorReven) {
		super();
		this.coursename = coursename;
		this.natureclass = natureclass;
		this.ctid = ctid;
		this.courseCategory = courseCategory;
		this.courseNature = courseNature;
		this.credit = credit;
		this.beginSection = beginSection;
		this.endSection = endSection;
		this.beginWeek = beginWeek;
		this.endWeek = endWeek;
		this.day = day;
		this.oddorReven = oddorReven;
	}
	private String coursename;
	private String natureclass;
	private String ctid;
	private String courseCategory;
	private String courseNature;
	private String credit;
	private int beginSection;//第几节课开始
	private int endSection;//上两节课还是几节
	private int beginWeek;
	private int endWeek;
	private int day;//周几上课
	private String oddorReven;
	private String place;
	

	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public int getBeginSection() {
		return beginSection;
	}
	public void setBeginSection(int beginSection) {
		this.beginSection = beginSection;
	}
	public int getEndSection() {
		return endSection;
	}
	public void setEndSection(int endSection) {
		this.endSection = endSection;
	}
	public int getBeginWeek() {
		return beginWeek;
	}
	public void setBeginWeek(int beginWeek) {
		this.beginWeek = beginWeek;
	}
	public int getEndWeek() {
		return endWeek;
	}
	public void setEndWeek(int endWeek) {
		this.endWeek = endWeek;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getOddorReven() {
		return oddorReven;
	}
	public void setOddorReven(String oddorReven) {
		this.oddorReven = oddorReven;
	}

	public CourseTeacherElement() {
		// TODO Auto-generated constructor stub
	}
	public String getCourseCategory() {
		return courseCategory;
	}
	public void setCourseCategory(String courseCategory) {
		this.courseCategory = courseCategory;
	}
	public String getCourseNature() {
		return courseNature;
	}
	public void setCourseNature(String courseNature) {
		this.courseNature = courseNature;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	
	public String getCtid() {
		return ctid;
	}
	public void setCtid(String ctid) {
		this.ctid = ctid;
	}

	public String getCoursename() {
		return coursename;
	}
	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}
	public String getNatureclass() {
		return natureclass;
	}
	public void setNatureclass(String natureclass) {
		this.natureclass = natureclass;
	}
	
}
