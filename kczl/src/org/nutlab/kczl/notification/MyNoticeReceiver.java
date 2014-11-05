package org.nutlab.kczl.notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.njut.R;
import com.njut.activity.MainActivity;
import com.njut.activity.loadingActivity;
import com.njut.data.CourseElement;
import com.njut.data.Curriculum;
import com.njut.utility.CalendarHelper;
import com.njut.utility.ClassCoverClass;
import com.njut.utility.JsonParse;

public class MyNoticeReceiver extends BroadcastReceiver {

	/**
	 * called when the BroadcastReceiver is receiving an Intent broadcast.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setClass(context, MyAlarm.class);
//		context.startActivity(intent);
		Notification notice = new Notification();
		notice.icon = R.drawable.icon;
		notice.flags |= Notification.FLAG_AUTO_CANCEL;//在通知栏上点击此通知后自动清除此通知
		int i = 0;
		try {
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
			kczlApplication.PersonString = settings.getString("PersonString",
					kczlApplication.PersonString);
			JsonParse jp = new JsonParse();
			kczlApplication.Person = jp.jsonToPersonElement(settings.getString(
					"PersonString", kczlApplication.PersonString));
			kczlApplication.CurriculumsString = settings.getString("CurriculumsString", kczlApplication.CurriculumsString);
			if (!(kczlApplication.CurriculumsString.equals(""))) {
				JSONArray jsonObjs;

				jsonObjs = new JSONArray(kczlApplication.CurriculumsString);
				List<Curriculum> curriculums = new ArrayList<Curriculum>();
				for (int j = 0; j < jsonObjs.length(); j++) {
					curriculums.add(jp.jsonToCurriculum((JSONObject) jsonObjs
							.opt(j)));
				}
				kczlApplication.Curriculums.clear();
				kczlApplication.Curriculums.addAll(curriculums);
			}
			ClassCoverClass c2c = new ClassCoverClass();
			CalendarHelper cHelper = new CalendarHelper();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			long temp = System.currentTimeMillis();
			String temp2 = sf.format(temp);
			int day = cHelper.getDay(
					sf.format(System.currentTimeMillis()),
					kczlApplication.Person.getBegindate());
			int week = cHelper.getWeek(
					sf.format(System.currentTimeMillis()),
					kczlApplication.Person.getBegindate());
			Log.v("System.out", "Fromnotice day:"+day);
			Log.v("System.out", "Fromnotice week:"+week);
			List<CourseElement> list = c2c.curriculumsToCourseElements(
					kczlApplication.Curriculums, day, week);
			i = list.size();
			Log.v("System.out", "Fromnotice  list.size:"+ list.size());
			Log.v("System.out", "Fromnotice kczlApplication.Curriculums.size:"+kczlApplication.Curriculums.size());
		} catch (Exception e) {
		}
		if (i == 0) {
			notice.tickerText = "今天没课！但也要好好学习！";
		} else {
			notice.tickerText = "今天有" + i + "门课要上！";
		}
		notice.defaults = Notification.DEFAULT_SOUND;
		notice.when = System.currentTimeMillis()+10L;
		// 100 毫秒延迟后，震动 250 毫秒，暂停 100 毫秒后，再震动 500 毫秒
		// notice.vibrate = new long[] { 100, 250, 100, 500 };出错？
		// notice.setLatestEventInfo(this, "通知", "开会啦",
		// PendingIntent.getActivity(this, 0, null, 0));
		Intent target = new Intent(context,loadingActivity.class);
		target.putExtra("isFromNotice", "true");
		notice.setLatestEventInfo(context, "通知", notice.tickerText, PendingIntent
				.getActivity(context, 0, target, 0));// 即将跳转页面，还没跳转
		NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		manager.notify(0, notice);
	}

}
