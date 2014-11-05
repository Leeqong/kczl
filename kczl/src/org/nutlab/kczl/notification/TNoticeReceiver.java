package org.nutlab.kczl.notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.njut.R;
import com.njut.activity.loadingActivity;
import com.njut.data.CourseTeacherElement;
import com.njut.utility.CalendarHelper;
import com.njut.utility.JsonParse;

/**
 * 
 * Ŀǰ���������֮ǰһ�����ʦ�пΣ��ڶ���˵��notice���������µı��棬����鿴��
 * ��ʵÿ�����Ӷ������������ж�һ�£�֮ǰ�����пβ�notification�����򣬳�Ĭ  by gpgp
 */
public class TNoticeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//		intent.setClass(context, MyAlarm.class);
		//		context.startActivity(intent);

		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		kczlApplication.PersonTeacherString = settings.getString("PersonTeacherString",
				kczlApplication.PersonTeacherString);
		JsonParse jp = new JsonParse();
		kczlApplication.PersonTeacher = jp
				.jsonToPersonTeacherElement(kczlApplication.PersonTeacherString);
		kczlApplication.CurriculumtString = settings.getString(
				"CurriculumtString", kczlApplication.CurriculumtString);
		JSONArray jsonObjs = null;
		try {
			jsonObjs = new JSONArray(kczlApplication.CurriculumtString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<CourseTeacherElement> curriculumt = new ArrayList<CourseTeacherElement>();
		for (int i = 0; i < jsonObjs.length(); i++) {
			curriculumt.add(jp.jsonToCourseTeacherElement((JSONObject) jsonObjs
					.opt(i)));
		}

		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		long temp = System.currentTimeMillis();
		String temp2 = sf.format(temp);
		int day = cHelper.getDay(
				sf.format(System.currentTimeMillis()),
				kczlApplication.PersonTeacher.getBegindate());
		int yesterday = (day-1+7)%7 ;
		Log.v("GPGPGP", "yesterday " + yesterday);
		Boolean toNotice = false;
		Log.v("GPGPGP", "curriculumt.size():"+curriculumt.size());
		String targetCtid= "";
		String courseName = "";
		for (int j = 0; j < curriculumt.size(); j++) {
			Log.v("GPGPGP", "curriculumt:day "+j +" = "+curriculumt.get(j).getDay());
			if(curriculumt.get(j).getDay() == yesterday)
			{
				courseName = curriculumt.get(j).getCoursename();
				targetCtid = curriculumt.get(j).getCtid();
				toNotice =true;
				break;
			}
		}
		
		if(toNotice)
		{
			Notification notice = new Notification();
			notice.icon = R.drawable.icon;
			notice.flags |= Notification.FLAG_AUTO_CANCEL;//��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
			notice.tickerText = "���µı������ɣ�����鿴";
			notice.defaults = Notification.DEFAULT_SOUND;
			notice.when = System.currentTimeMillis()+10L;
			// 100 �����ӳٺ��� 250 ���룬��ͣ 100 ��������� 500 ����
			// notice.vibrate = new long[] { 100, 250, 100, 500 };����
			// notice.setLatestEventInfo(this, "֪ͨ", "������",
			// PendingIntent.getActivity(this, 0, null, 0));
			Intent target = new Intent(context,loadingActivity.class);
			target.putExtra("ctid", targetCtid);
			target.putExtra("coursename", courseName);
			target.putExtra("isFromTNotice", "true");
			notice.setLatestEventInfo(context, "֪ͨ", notice.tickerText, PendingIntent
					.getActivity(context, 0, target, 0));// ������תҳ�棬��û��ת
			NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
			manager.notify(0, notice);
		}
	}

}
