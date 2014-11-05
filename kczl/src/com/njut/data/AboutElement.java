package com.njut.data;

public class AboutElement {
	public AboutElement(String name, String job, String grade,
			String className, int imageID) {
		super();
		this.name = name;
		this.job = job;
		this.grade = grade;
		this.className = className;
		this.ImageID = imageID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getImageID() {
		return ImageID;
	}
	public void setImageID(int imageID) {
		ImageID = imageID;
	}
	private String name;
	private String job;
	private String grade;
	private String className;
	private int ImageID;

}
