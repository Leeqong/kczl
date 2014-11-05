package com.njut.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.kczl.notification.MyNoticeReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.njut.data.CourseTeacherElement;
import com.njut.data.Curriculum;
import com.njut.database.SMessageDBTask;
import com.njut.utility.JsonParse;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

/*
 * 欢迎界面模块
 */

public class loadingActivity extends Activity {

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.loading);
		// UmengUpdateAgent.update(this);
		// setReminder();
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		kczlApplication.activitiesList.add(this);
		//String versionName = AppUtils.getVersionName(loadingActivity.this);
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		kczlApplication.JSON_VERSION = settings.getString("JSON_VERSION",
				"");
		//以后可以改成对应的发布版本，json存储有变化，如果还是旧的数据，清除掉重新登录，防止出错
		if(!kczlApplication.JSON_VERSION.equals("1.0"))
		{
			SharedPreferences.Editor editor = settings
					.edit();
			editor.clear();  
			editor.commit(); 
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			// create a PendingIntent that will perform a broadcast
			PendingIntent pi = PendingIntent.getBroadcast(
					loadingActivity.this, 0,
					new Intent(loadingActivity.this,
							MyNoticeReceiver.class), 0);
			am.cancel(pi);
			SMessageDBTask.removeAll();
			XGPushManager.unregisterPush(loadingActivity.this);
		}

		kczlApplication.IsLogined = settings.getInt("IsLogined",
				kczlApplication.IsLogined);
		kczlApplication.isClocked = settings.getInt("isClocked",
				kczlApplication.isClocked);
		kczlApplication.IsStudent = settings.getInt("IsStudent",
				kczlApplication.IsStudent);
		kczlApplication.isNotification = settings.getInt("isNotification",
				kczlApplication.isNotification);
		kczlApplication.isReplyNotification = settings.getInt("isReplyNotification",
				kczlApplication.isReplyNotification);

		//		if(kczlApplication.Person!=null&&kczlApplication.Person.getSchoolnumber()!=null&&!kczlApplication.Person.getSchoolnumber().equals(""))
		//			kczlApplication.IsStudent = 1;
		if (kczlApplication.IsLogined == 1) {

			FeedbackAgent agent = new FeedbackAgent(loadingActivity.this);
			agent.sync();
			// 开启logcat输出，方便debug，发布时请关闭
			kczlApplication.IsOffLine = 1;
			try {
				if (kczlApplication.IsStudent == 1)
					getParms();
				else
					getTeacherParms();
			} catch (Exception e) {
			} finally {
			}


			if (kczlApplication.IsStudent == 1) {
				Intent intent = new Intent(loadingActivity.this,
						MainActivity.class);

				if (getIntent().getStringExtra("isFromNotice") != null
						&& getIntent().getStringExtra("isFromNotice").equals(
								"true"))
					intent.putExtra("isFromNotice", "true");// 为了点击通知栏的时候到MainActivity转到课表
				if (getIntent().getStringExtra("isFromXgMessage") != null
						&& getIntent().getStringExtra("isFromXgMessage").equals(
								"true"))
					intent.putExtra("isFromXgMessage", "true");// 为了点击通知栏的时候到MainActivity转到课表

				startActivity(intent);
				finish();

			} else if (kczlApplication.IsStudent == 2) {
				Intent intent;
				if (getIntent().getStringExtra("isFromTNotice") != null
						&& getIntent().getStringExtra("isFromTNotice").equals(
								"true"))
				{
					intent= new Intent(loadingActivity.this,
							TeacherHomepageActivity.class);
					intent.putExtra("isFromTNotice", "true");// 为了点击通知栏的时候到MainActivity转到课表
					intent.putExtra("coursename", getIntent().getStringExtra("coursename"));
					intent.putExtra("ctid", getIntent().getStringExtra("ctid"));
					intent.putExtra("isFromTNotice", "true"); 
				}
				else
				{
					intent= new Intent(loadingActivity.this,
							CurriculumTabActivity.class);
					if (getIntent().getStringExtra("isFromXgMessage") != null
							&& getIntent().getStringExtra("isFromXgMessage").equals(
									"true"))
						intent.putExtra("isFromXgMessage", "true");// 为了点击通知栏的时候到MainActivity转到课表
				}
				startActivity(intent);
				finish();
			} else if (kczlApplication.IsStudent == 0) {
				Intent intent = new Intent(loadingActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		} else {
			Intent intent = new Intent(loadingActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}

	}

	private void getParms() throws JSONException {
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		kczlApplication.PersonString = settings.getString("PersonString", "");
		kczlApplication.UserName = settings.getString("UserName", "");
		kczlApplication.PassWord = settings.getString("PassWord", "");
		// kczlApplication.IsLogined = settings.getInt("IsLogined",
		// kczlApplication.IsLogined);
		// kczlApplication.sCookie.setEmail(settings.getString("email",
		// kczlApplication.sCookie.getEmail()));
		// kczlApplication.sCookie.setSessioncode(settings.getString("sessioncode",
		// kczlApplication.sCookie.getSessioncode()));
		JsonParse jp = new JsonParse();
		kczlApplication.Person = jp.jsonToPersonElement(settings.getString(
				"PersonString", ""));
		kczlApplication.CurriculumsString = settings.getString(
				"CurriculumsString", "");
		if (!(kczlApplication.CurriculumsString.equals(""))) {
			JSONArray jsonObjs = new JSONArray(
					kczlApplication.CurriculumsString);
			List<Curriculum> curriculums = new ArrayList<Curriculum>();
			for (int i = 0; i < jsonObjs.length(); i++) {
				curriculums.add(jp.jsonToCurriculum((JSONObject) jsonObjs
						.opt(i)));
			}
			kczlApplication.Curriculums.clear();
			kczlApplication.Curriculums.addAll(curriculums);
		}
		// 2013 10 25

		kczlApplication.AdviceString = settings.getString("adviceString", "");
		if (!(kczlApplication.AdviceString.equals(""))) {
			kczlApplication.AdviceElements.clear();
			kczlApplication.AdviceElements.addAll(JsonParse.JsonToAdviceList(kczlApplication.AdviceString));
		}
		kczlApplication.SpitString = settings.getString("spitString", "");
		// 2013 11 20
		if (!(kczlApplication.SpitString.equals(""))) {
			kczlApplication.SpitElements.clear();
			kczlApplication.SpitElements.addAll(JsonParse.JsonToSpitList(kczlApplication.SpitString));
		}
	}

	private void getTeacherParms() throws JSONException {
		JsonParse jp = new JsonParse();
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		kczlApplication.PersonTeacherString = settings.getString(
				"PersonTeacherString", kczlApplication.PersonTeacherString);
		kczlApplication.UserName = settings.getString("UserName",
				kczlApplication.UserName);
		kczlApplication.PassWord = settings.getString("PassWord",
				kczlApplication.PassWord);
		kczlApplication.CurriculumtString = settings.getString(
				"CurriculumtString", kczlApplication.CurriculumtString);
		String teacherliststring = "";
		teacherliststring = settings.getString("TeacherListString", "[]");
		JSONArray teacherArray = new JSONArray(teacherliststring);
		for (int i = 0; i < teacherArray.length(); i++) {
			kczlApplication.teacherlist.add(jp
					.jsonToTeacherListElemet((JSONObject) teacherArray.opt(i)));
		}
		kczlApplication.PersonTeacher = jp
				.jsonToPersonTeacherElement(kczlApplication.PersonTeacherString);
		JSONArray jsonObjs = new JSONArray(kczlApplication.CurriculumtString);
		List<CourseTeacherElement> curriculumt = new ArrayList<CourseTeacherElement>();
		for (int i = 0; i < jsonObjs.length(); i++) {
			curriculumt.add(jp.jsonToCourseTeacherElement((JSONObject) jsonObjs
					.opt(i)));
		}
		kczlApplication.CourseTeacherElements.clear();
		kczlApplication.CourseTeacherElements.addAll(curriculumt);
	}
}
