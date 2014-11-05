package com.njut.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.njut.data.AchievementElement;
import com.njut.data.AdviceElement;
import com.njut.data.CommentElement;
import com.njut.data.CourseTeacherElement;
import com.njut.data.Curriculum;
import com.njut.data.PersonElement;
import com.njut.data.PersonTeacherElement;
import com.njut.data.SpitElement;
import com.njut.data.TcardElement;
import com.njut.data.TeacherListElement;

public class JsonParse {

	public PersonElement jsonToPersonElement(String jsonStr) {
		PersonElement person = new PersonElement();
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			person.setBirthday(jsonObj.getString("birthday"));
			person.setSex(jsonObj.getString("sex"));
			person.setSchoolnumber(jsonObj.getString("schoolnumber"));
			person.setTel(jsonObj.getString("tel"));
			person.setCampusname(jsonObj.getString("campusname"));
			person.setUniversityname(jsonObj.getString("universityname"));
			person.setNatureclassname(jsonObj.getString("natureclassname"));
			person.setEmail(jsonObj.getString("email"));
			person.setSchoolCalendar(jsonObj.getString("schoolCalendar"));
			person.setGrade(jsonObj.getString("grade"));
			person.setCollegename(jsonObj.getString("collegename"));
			person.setRealname(jsonObj.getString("realname"));
			person.setBegindate(jsonObj.getString("begindate"));
			person.setSessioncode(jsonObj.getString("sessioncode"));
			person.setFieldName(jsonObj.getString("fieldname"));
		} catch (JSONException e) {
			Log.e("jsonToPersonElement", e.toString());
		}
		return person;
	}

	public PersonTeacherElement jsonToPersonTeacherElement(String jsonStr) {
		PersonTeacherElement personteacher = new PersonTeacherElement();
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			personteacher.setPersonnelnumber(jsonObj
					.getString("personnelnumber"));
			personteacher.setTeachername(jsonObj.getString("teachername"));
			personteacher.setTeachernumber(jsonObj.getString("teachernumber"));
			personteacher.setBegindate(jsonObj.getString("begindate"));
		} catch (JSONException e) {
			Log.e("jsonToPersonTeacherElement", e.toString());
		}
		return personteacher;
	}

	public Curriculum jsonToCurriculum(String jsonStr) {
		Curriculum curriculum = new Curriculum();
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			curriculum.setEndTime(jsonObj.getString("endtime"));
			curriculum.setCourseBelonging(jsonObj.getString("courseBelonging"));
			curriculum.setStartTime(jsonObj.getString("starttime"));
			curriculum.setCourseNature(jsonObj.getString("courseNature"));
			curriculum.setExamMethod(jsonObj.getString("examMethod"));
			curriculum.setBeginSection(jsonObj.getInt("beginsection"));
			curriculum.setCourseName(jsonObj.getString("coursename"));
			curriculum.setCourseCategory(jsonObj.getString("courseCategory"));
			curriculum.setEndWeek(jsonObj.getInt("endweek"));
			curriculum.setEndSection(jsonObj.getInt("endsection"));
			curriculum.setOddorReven(jsonObj.getString("oddoreven"));
			curriculum.setBeginWeek(jsonObj.getInt("beginweek"));
			curriculum.setDay(jsonObj.getInt("day"));
			curriculum.setCredit(jsonObj.getString("credit"));
			curriculum.setPlace(jsonObj.getString("place"));
			curriculum.setChooseNumber(jsonObj.getString("choosenumber"));
			curriculum.setCtid(jsonObj.getString("ctid"));
			curriculum.setTeacher(jsonObj.getString("teachername"));
		} catch (JSONException e) {
			Log.e("jsonToCurriculum", e.toString());
		}
		return curriculum;
	}

	public Curriculum jsonToCurriculum(JSONObject jsonObj) {
		Curriculum curriculum = new Curriculum();
		try {
			curriculum.setEndTime(jsonObj.getString("endtime"));
			curriculum.setCourseBelonging(jsonObj.getString("courseBelonging"));
			curriculum.setStartTime(jsonObj.getString("starttime"));
			curriculum.setCourseNature(jsonObj.getString("courseNature"));
			curriculum.setExamMethod(jsonObj.getString("examMethod"));
			curriculum.setBeginSection(jsonObj.getInt("beginsection"));
			curriculum.setCourseName(jsonObj.getString("coursename"));
			curriculum.setCourseCategory(jsonObj.getString("courseCategory"));
			curriculum.setEndWeek(jsonObj.getInt("endweek"));
			curriculum.setEndSection(jsonObj.getInt("endsection"));
			curriculum.setOddorReven(jsonObj.getString("oddoreven"));
			curriculum.setBeginWeek(jsonObj.getInt("beginweek"));
			curriculum.setDay(jsonObj.getInt("day"));
			curriculum.setCredit(jsonObj.getString("credit"));
			curriculum.setPlace(jsonObj.getString("place"));
			curriculum.setChooseNumber(jsonObj.getString("choosenumber"));
			curriculum.setCtid(jsonObj.getString("ctid"));
			curriculum.setTeacher(jsonObj.getString("teachername"));
		} catch (JSONException e) {
			Log.e("jsonToCurriculum", e.toString());
		}
		return curriculum;
	}

	public AchievementElement jsonToAchievementElement(JSONObject jsonObj) {
		AchievementElement achievementElement = new AchievementElement();
		try {
			achievementElement.setCourseName(jsonObj.getString("coursename"));
			// achievementElement.setPoint(jsonObj.getDouble("point"));
			achievementElement.setCredit(jsonObj.getDouble("credit"));
			achievementElement.setType(jsonObj.getString("courseNature"));
			if (jsonObj.getString("examMethod").equals("学位课")) {
				achievementElement.setType("学位课");
			}
			String score = jsonObj.getString("score");
			Pattern pattern = Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,2}$");
			Matcher isNum = pattern.matcher(score);
			if (isNum.matches()) {
				achievementElement.setScore(Double.parseDouble(score));
			} else {
				achievementElement.setGrade(score);
			}
			/*
			 * 五级制
			 */
			/*
			 * if (achievementElement.getScore() >= 90) {
			 * achievementElement.setPoint(4.5); } else { if
			 * (achievementElement.getScore() >= 80) {
			 * achievementElement.setPoint(3.5); } else { if
			 * (achievementElement.getScore() >= 70) {
			 * achievementElement.setPoint(2.5); } else { if
			 * (achievementElement.getScore() >= 60) {
			 * achievementElement.setPoint(1.5); } else {
			 * achievementElement.setPoint(0.0); } } }
			 * 
			 * 
			 * }
			 */
			/*
			 * 常规制
			 */

			if (achievementElement.getScore() >= 50) {
				achievementElement
				.setPoint((achievementElement.getScore() - 50) / 10.0);
			} else
				achievementElement.setPoint(0.0);

			achievementElement.setNewestMark(jsonObj.getDouble("makeupScore"));
		} catch (JSONException e) {
			achievementElement.setNewestMark(0.0);
			Log.e("jsonToAchievementElement", e.toString());

		}
		return achievementElement;
	}

	// {"commentlist":{"list":[]},"courseAdvice":{"arrangement":1,"attendance":5,"caid":724,"content":"。。。。，，，","ctid":704381,"learningEffect":5,"schoolnumber":"1405110127","speed":3,"timestamp":"2014-2-22 14:00:24"},"coursename":"Java分布式系统开发","everlike":"false","likenum":4}
	public AdviceElement jsonToAdviceElement(JSONObject jsonObj) {
		AdviceElement advice = new AdviceElement();
		List<CommentElement> commentlist = new ArrayList<CommentElement>();
		try {
			JSONObject commentlistObject = jsonObj.getJSONObject("commentlist");
			JSONArray commentlistArray = commentlistObject.getJSONArray("list");
			for (int m = 0; m < commentlistArray.length(); m++) {
				commentlist.add(jsonToCommentElement(
						(JSONObject) commentlistArray.opt(m), "advice"));
			}
			CommentComparator commentComparator = new CommentComparator();
			Collections.sort(commentlist, commentComparator);
			advice.setCommmentlist(commentlist);
			JSONObject coursedaily = jsonObj.getJSONObject("courseAdvice");
			advice.setCaid(coursedaily.getString("caid"));
			advice.setCtid(coursedaily.getString("ctid"));
			advice.setName(jsonObj.getString("coursename"));
			advice.setDate(coursedaily.getString("timestamp"));
			advice.setEffect(coursedaily.getInt("learningEffect"));
			advice.setContent(coursedaily.getString("content"));
			advice.setLikenum(jsonObj.getInt("likenum"));
			advice.setSchoolnumber(coursedaily.getString("schoolnumber"));
			advice.setEverlike(jsonObj.getString("everlike"));
			advice.setAttendance(coursedaily.getString("attendance"));
			advice.setArrangement(coursedaily.getString("arrangement"));
			advice.setSpeed(coursedaily.getString("speed"));
		} catch (JSONException e) {
			Log.e("jsonToAdviceElement", e.toString());
		}
		return advice;
	}
	//按服务器的json结构解析出列表以后Gson存在本地，之后的存取都用Gson的便利方法
	public static List<AdviceElement> JsonToAdviceList(String json)
	{
		List<AdviceElement> advicelist = new ArrayList<AdviceElement>();
		Gson gson = new Gson();
		try {
			JSONArray adviceArray = new JSONArray(json);
			for (int i = 0; i < adviceArray.length(); i++) {
				JSONObject advice = (JSONObject) adviceArray.opt(i);
				advicelist.add(gson.fromJson(advice.toString(),AdviceElement.class));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return advicelist;	
	}

	public static String AdviceListToJson(List<AdviceElement> advicelist)
	{
		Gson gson = new Gson();
		return gson.toJson(advicelist);
	}

	public SpitElement jsonToSpitElement(JSONObject jsonObj) {
		SpitElement spit = new SpitElement();
		List<CommentElement> commentlist = new ArrayList<CommentElement>();
		try {
			JSONObject spitdaily = jsonObj.getJSONObject("spit");
			JSONObject commentlistObject = jsonObj.getJSONObject("commentlist");
			JSONArray commentlistArray = commentlistObject.getJSONArray("list");
			for (int m = 0; m < commentlistArray.length(); m++) {
				commentlist.add(jsonToCommentElement(
						(JSONObject) commentlistArray.opt(m), "spit"));
			}
			CommentComparator commentComparator = new CommentComparator();
			Collections.sort(commentlist, commentComparator);
			spit.setCommmentlist(commentlist);
			spit.setCoursename(jsonObj.getString("coursename"));
			spit.setTimestamp(spitdaily.getString("timestamp"));
			spit.setContent(spitdaily.getString("content"));
			spit.setLikenum(jsonObj.getInt("likenum"));
			spit.setEverlike(jsonObj.getString("everlike"));
			spit.setSpitid(spitdaily.getString("spitid"));
			spit.setCtid(spitdaily.getString("ctid"));
			spit.setSchoolnumber(spitdaily.getString("schoolnumber"));
		} catch (JSONException e) {
			Log.e("jsonToSpitElement", e.toString());
		}
		return spit;
	}

	public static List<SpitElement> JsonToSpitList(String json)
	{
		List<SpitElement> spitlist = new ArrayList<SpitElement>();
		Gson gson = new Gson();
		try {
			JSONArray spitArray = new JSONArray(json);
			for (int i = 0; i < spitArray.length(); i++) {
				JSONObject spit = (JSONObject) spitArray.opt(i);
				spitlist.add(gson.fromJson(spit.toString(),SpitElement.class));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return spitlist;	
	}

	public static String SpitListToJson(List<SpitElement> spitlist)
	{
		Gson gson = new Gson();
		return gson.toJson(spitlist);
	}


	// {"commentlist":{"list":[{"commentid":54,"c":"testtesttestadvice","fromuser":"1405110111","infoid":716,"timestamp":"2014-2-19 22:54:52","touser":"1405110109"}]}

	public CommentElement jsonToCommentElement(JSONObject jsonObj, String type) {
		CommentElement comment = new CommentElement();
		try {
			comment.setCommentid(jsonObj.getString("commentid"));
			comment.setContent(jsonObj.getString("content"));
			comment.setFromuser(jsonObj.getString("fromuser"));
			comment.setEdid(jsonObj.getString("infoid"));
			comment.setTouser(jsonObj.getString("touser"));
			comment.setTimestap(jsonObj.getString("timestamp"));
			comment.setType(type);
		} catch (JSONException e) {
			Log.e("jsonToCommentElement", e.toString());
		}
		return comment;
	}

	public CourseTeacherElement jsonToCourseTeacherElement(JSONObject jsonObj) {
		CourseTeacherElement teacherElement = new CourseTeacherElement();
		try {
			teacherElement.setCourseCategory(jsonObj
					.getString("courseCategory"));
			teacherElement.setCourseNature(jsonObj.getString("courseNature"));
			teacherElement.setCoursename(jsonObj.getString("coursename"));
			teacherElement.setCredit(jsonObj.getString("credit"));
			teacherElement.setCtid(jsonObj.getString("ctid"));
			teacherElement.setNatureclass(jsonObj.getString("natureclass"));

			JSONArray schedule = jsonObj.getJSONArray("schedule");
			if(schedule.length() > 0)
			{
				JSONObject scheduleJson = (JSONObject) schedule.opt(0);
				teacherElement.setBeginSection(scheduleJson.getInt("beginsection"));
				teacherElement.setBeginWeek(scheduleJson.getInt("beginweek"));
				teacherElement.setDay(scheduleJson.getInt("day"));
				teacherElement.setEndSection(scheduleJson.getInt("endsection"));
				teacherElement.setEndWeek(scheduleJson.getInt("endweek"));
				teacherElement.setOddorReven(scheduleJson.getString("oddoreven"));
				teacherElement.setPlace(scheduleJson.getString("place"));
			}
		} catch (JSONException e) {
			Log.e("jsonToCourseTeacherElement", e.toString());
		}
		return teacherElement;
	}

	public TeacherListElement jsonToTeacherListElemet(JSONObject jsonObj)// 一个形如[{ctid:xxx,spitlist:[],advicelist:[] },.....]
	{
		Gson gson = new Gson();
		TeacherListElement teacherListElement = new TeacherListElement();
		teacherListElement = gson.fromJson(jsonObj.toString(), TeacherListElement.class);
		return teacherListElement;
	}

	public String teacherListToJSON(List<TeacherListElement> teacherlist) {
		String result = "";
		Gson gson = new Gson();
		result = gson.toJson(teacherlist);
		return result;
	}
	// json:[{"arrangement":1,"attendance":5,"fivestar":1,"fourstar":0,"learningEffect":5,"num":1,"onestar":0,"speed":3,"threestar":0,"total":65,"twostar":0}] 
	public TcardElement jsonToTcardElement(JSONObject jsonObj) {
		TcardElement tcard = new TcardElement();
		List<TcardElement> tcardlist = new ArrayList<TcardElement>();
		try {
			tcard.setArrangement(jsonObj.getInt("arrangement"));
			tcard.setAttendance(jsonObj.getInt("attendance"));
			tcard.setDatestamp(jsonObj.getString("datestamp"));
			tcard.setFivestar(jsonObj.getInt("fivestar"));
			tcard.setFourstar(jsonObj.getInt("fourstar"));
			tcard.setLearningEffect(jsonObj.getInt("learningEffect"));
			tcard.setNum(jsonObj.getInt("num"));
			tcard.setOnestar(jsonObj.getInt("onestar"));
			tcard.setSpeed(jsonObj.getInt("speed"));
			tcard.setThreestar(jsonObj.getInt("threestar"));
			tcard.setTotal(jsonObj.getInt("total"));
			tcard.setTwostar(jsonObj.getInt("twostar"));
		} catch (JSONException e) {
			Log.e("jsonToTcardElement", e.toString());
		}
		return tcard;
	}

}
