package com.njut.data;

/*课程相关的数据抽象*/
public class CourseElement {
	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	private String courseName;
	private int state;
	private String teacherName;
	private String classroomName;
	private String time;
	private String ctid;
	private String courseNature;
	private String credit;
	private String endtime;
	private String starttime;
	
	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getCourseNature() {
		return courseNature;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public void setCourseNature(String courseNature) {
		this.courseNature = courseNature;
	}

	public CourseElement(String courseName, int state, String teacherName,
			String classroomName,String time) {
		this.courseName = courseName;
		this.state = state;
		this.teacherName = teacherName;
		this.classroomName = classroomName;
		this.time=time;
	}
	
	public CourseElement(String courseName, int state, String teacherName,
			String classroomName,String time,String ctid,String coursenature,String credit,String endtime,String starttime) {
		this.courseName = courseName;
		this.state = state;
		this.teacherName = teacherName;
		this.classroomName = classroomName;
		this.time=time;
		this.ctid=ctid;
		this.courseNature=coursenature;
		this.credit=credit;
		this.endtime=endtime;
		this.starttime =  starttime;
	}

	public String getCourseName() {
		return courseName;
	}

	public int getState() {
		return state;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public String getClassroomName() {
		return classroomName;
	}

	public String getTime() {
		return time;
	}

	public String getCtid() {
		return ctid;
	}
}