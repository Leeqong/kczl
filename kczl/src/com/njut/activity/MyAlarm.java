package com.njut.activity;

import java.text.SimpleDateFormat;
import java.util.List;

import org.nutlab.kczl.kczlApplication;

import com.njut.R;
import com.njut.data.CourseElement;
import com.njut.utility.CalendarHelper;
import com.njut.utility.ClassCoverClass;
import com.njut.utility.JsonParse;

import android.R.integer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore.Audio;
import android.view.View;
import android.widget.TextView;

/**
 * Display the alarm information
 */
public class MyAlarm extends Activity {

	/**
	 * An identifier for this notification unique within your application
	 */
	public static final int NOTIFICATION_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
//		setContentView(R.layout.customer_notitfication_layout);
//
//		// create the instance of NotificationManager
//		final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		// create the instance of Notification
//		Notification n = new Notification();
//		/* set the sound of the alarm. There are two way of setting the sound */
//		// n.sound=Uri.parse("file:///sdcard/alarm.mp3");
//		n.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "20");
//		// Post a notification to be shown in the status bar
//		nm.notify(NOTIFICATION_ID, n);
//
//		/* display some information */
//		try {
//			String PREFS_NAME = "org.nutlab.kczl";
//			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//			kczlApplication.PersonString = settings.getString("PersonString",
//					kczlApplication.PersonString);
//			JsonParse jp = new JsonParse();
//			kczlApplication.Person = jp.jsonToPersonElement(settings.getString(
//					"PersonString", kczlApplication.PersonString));
//			ClassCoverClass c2c = new ClassCoverClass();
//			CalendarHelper cHelper = new CalendarHelper();
//			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//			int day = cHelper.getDay(
//					sf.format(SystemClock.currentThreadTimeMillis()),
//					kczlApplication.Person.getBegindate());
//			int week = cHelper.getWeek(
//					sf.format(SystemClock.currentThreadTimeMillis()),
//					kczlApplication.Person.getBegindate());
//			List<CourseElement> list = c2c.curriculumsToCourseElements(
//					kczlApplication.Curriculums, day, week);
//			TextView tv = (TextView) findViewById(R.id.notification_text);
//			int i = list.size();
//			if (i == 0) {
//				tv.setText("�����Ȼû�пΣ��϶�����Ժʿ�ѽ������ݿ�Ū���ˣ�");
//			} else {
//				tv.setText("������" + i + "�ſ�Ҫ�ϣ�");
//			}
//			tv.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(MyAlarm.this, MainActivity.class);
//					startActivity(intent);
//
//				}
//			});
//		} catch (Exception e) {
//		}

		/** new method **/
		Notification notice = new Notification();
		notice.icon = R.drawable.icon;
		int i = 0;
		try {
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			kczlApplication.PersonString = settings.getString("PersonString",
					kczlApplication.PersonString);
			JsonParse jp = new JsonParse();
			kczlApplication.Person = jp.jsonToPersonElement(settings.getString(
					"PersonString", kczlApplication.PersonString));
			ClassCoverClass c2c = new ClassCoverClass();
			CalendarHelper cHelper = new CalendarHelper();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			int day = cHelper.getDay(
					sf.format(SystemClock.currentThreadTimeMillis()),
					kczlApplication.Person.getBegindate());
			int week = cHelper.getWeek(
					sf.format(SystemClock.currentThreadTimeMillis()),
					kczlApplication.Person.getBegindate());
			List<CourseElement> list = c2c.curriculumsToCourseElements(
					kczlApplication.Curriculums, day, week);
			i = list.size();
		} catch (Exception e) {
		}
		if (i == 0) {
			notice.tickerText = "����û�Σ���ҲҪ�ú�ѧϰ��";
		} else {
			notice.tickerText = "������" + i + "�ſ�Ҫ�ϣ�";
		}
		notice.defaults = Notification.DEFAULT_SOUND;
		notice.when = 10L;
		// 100 �����ӳٺ��� 250 ���룬��ͣ 100 ��������� 500 ����
		// notice.vibrate = new long[] { 100, 250, 100, 500 };����
		// notice.setLatestEventInfo(this, "֪ͨ", "������",
		// PendingIntent.getActivity(this, 0, null, 0));
		notice.setLatestEventInfo(this, "֪ͨ", notice.tickerText, PendingIntent
				.getActivity(this, 0, new Intent(this, MainActivity.class), 0));// ������תҳ�棬��û��ת
		NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		manager.notify(0, notice);
	}

}
