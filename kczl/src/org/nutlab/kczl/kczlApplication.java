package org.nutlab.kczl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.nutlab.kczl.notification.MyNoticeReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.njut.data.AchievementElement;
import com.njut.data.AdviceElement;
import com.njut.data.CookieElement;
import com.njut.data.CookieTeacherElement;
import com.njut.data.CourseElement;
import com.njut.data.CourseTeacherElement;
import com.njut.data.Curriculum;
import com.njut.data.PersonElement;
import com.njut.data.PersonTeacherElement;
import com.njut.data.SpitElement;
import com.njut.data.TeacherListElement;
import com.njut.utility.JsonParse;

public class kczlApplication extends android.app.Application {
	
	public static  String JSON_VERSION = "";
	//singleton
    private static kczlApplication globalContext = null;
	/** �û��� **/
	public static String UserName="";
	/** ���� **/
	public static String PassWord="";
	/** ��������ַ **/
	public static String ServerUri = "app.njut.org.cn";
	/** �Ƿ��¼ **/
	public static int IsLogined = 0;
	/** �Ƿ�ѧ�� **/
	public static int IsStudent = 0;//��֪Ϊ0��ѧ��Ϊ1����ʦΪ2
	/** ѧ����� **/
	public static String PersonString="";
	public static PersonElement Person;
	/** ��ʦ��� **/
	public static String PersonTeacherString="";
	public static PersonTeacherElement PersonTeacher;
	/** Cookie��Ϣ **/
	public static CookieElement sCookie;
	public static CookieTeacherElement tCookie;
	public static CookieStore Cookies;
	/** �γ���Ϣ **/
	public static List<Curriculum> Curriculums = new ArrayList<Curriculum>();
	public static String CurriculumsString;
	public static List<CourseElement> CourseElements = new ArrayList<CourseElement>();
	/** ��ʦ�γ���Ϣ **/	
	public static String CurriculumtString;
	public static List<CourseTeacherElement> CourseTeacherElements = new ArrayList<CourseTeacherElement>();
	public static List<TeacherListElement> teacherlist = new ArrayList<TeacherListElement>();
	/** �ɼ���Ϣ **/
	public static List<AchievementElement> AchievementElements = new ArrayList<AchievementElement>();
	public static String AchievementString;
	/** ѧ����Ϣ����Ϣ **/
	public static String AdviceString="";
	public static List<AdviceElement> AdviceElements = new ArrayList<AdviceElement>();
	public static String SpitString="";
	public static List<SpitElement> SpitElements = new ArrayList<SpitElement>();
	public static String lastUpdate="";
//	/** ��ʦ��Ϣ����Ϣ **/
//	public static String TeacherAdviceString="";
//	public static List<AdviceElement> TeacherAdviceElements = new ArrayList<AdviceElement>();
//	public static String TeacherSpitString="";
//	public static List<SpitElement> TeacherSpitElements = new ArrayList<SpitElement>();
	/** �Ƿ����� **/
	public static int IsOffLine = 0;
	/** �Ƿ����������� **/
	public static int isClocked = 0;
	/** �������ӿ��� **/
	public static int isNotification = 1;
	/**����֪ͨ���� **/
	public static int isReplyNotification = 1;
	/** ѧ����Ϣ **/
	public static String Year="";
	public static String Term="";
	
	public static kczlApplication getInstance() {
        return globalContext;
    }

	public void onCreate() {
		// getParms();
		super.onCreate();
		globalContext = this;
		getParms();
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		kczlApplication.PersonString = settings.getString("PersonString","");
		if (!kczlApplication.PersonString.equals("")) {
			JsonParse jp = new JsonParse();
			kczlApplication.Person = jp.jsonToPersonElement(settings.getString(
					"PersonString", kczlApplication.PersonString));
		}

		/*
		 * String PREFS_NAME = "org.nutlab.kczl"; SharedPreferences settings =
		 * getSharedPreferences(PREFS_NAME, 0);
		 * isClocked=settings.getInt("isClocked", isClocked);
		 * isNotification=settings.getInt("isNotification", isNotification);
		 * if(isNotification==1){ if(isClocked==0){ setReminder(); isClocked=1;
		 * String PREFS_NAME2 = "org.nutlab.kczl"; SharedPreferences settings2 =
		 * getSharedPreferences(PREFS_NAME2, 0); SharedPreferences.Editor editor
		 * = settings2.edit(); editor.putInt("isClocked", isClocked);
		 * editor.commit(); }}
		 */
	}

	public void onTerminate() {
		// saveParms();
		super.onTerminate();
		Log.v("System.out", "onTerminate");
	}

	public void onLowMemory() {
		// saveParms();
		super.onLowMemory();
		Log.v("System.out", "onLowMemory");
	}

	public void getParms() {
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		PersonString = settings.getString("PersonString", "");
		UserName = settings.getString("UserName", "");
		PassWord = settings.getString("PassWord", "");
		ServerUri = settings.getString("ServerUri", ServerUri);
		IsLogined = settings.getInt("IsLogined", IsLogined);
		AchievementString = settings.getString("AchievementString", "");
		// if(sCookie == null) sCookie = new CookieElement();
		// sCookie.setEmail(settings.getString("email", sCookie.getEmail()));
		// sCookie.setSessioncode(settings.getString("sessioncode",
		// sCookie.getSessioncode()));
		JsonParse jp = new JsonParse();
		String persontmp = settings.getString("PersonString", "");
		if (!persontmp.equals(""))
			Person = jp.jsonToPersonElement(persontmp);
		CurriculumsString = settings.getString("CurriculumsString", "");

	}

	public void saveParms() {
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("PersonString", PersonString);
		editor.putString("UserName", UserName);
		editor.putString("PassWord", PassWord);
		editor.putInt("IsLogined", IsLogined);
		editor.putString("ServerUri", ServerUri);
		editor.putString("email", sCookie.getEmail());
		editor.putString("sessioncode", sCookie.getSessioncode());
		editor.putString("CurriculumsString", CurriculumsString);
		editor.putString("AchievementString", AchievementString);
		editor.commit();
	}

	private void setReminder() {

		// get the AlarmManager instance
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		// create a PendingIntent that will perform a broadcast
		PendingIntent pi = PendingIntent.getBroadcast(kczlApplication.this, 0,
				new Intent(kczlApplication.this, MyNoticeReceiver.class), 0);
		// 7����
		am.setRepeating(AlarmManager.RTC_WAKEUP, 25200000, 86400000, pi);
		// Calendar c=Calendar.getInstance();
		// am.set(AlarmManager.RTC_WAKEUP,SystemClock.elapsedRealtime() +1000,
		// pi);
		// am.cancel(pi);
	}

	public static List<Activity> activitiesList = new ArrayList<Activity>();
}
