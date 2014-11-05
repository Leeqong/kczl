package org.nutlab.kczl.notification;

import java.util.Calendar;

import com.njut.activity.ConfigActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            //重新计算闹铃时间，并调第一步的方法设置闹铃时间及闹铃间隔时间
        	// get the AlarmManager instance
    		AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    		// create a PendingIntent that will perform a broadcast
    		Intent myintent = new Intent(context, MyNoticeReceiver.class);
    		myintent.setAction("org.nutlab.kczl.notification.MyReceiver2");
    		PendingIntent pi = PendingIntent.getBroadcast(context, 0,
    				myintent, 0);
    		
    		// 7点响
    		int hour = 7;
    		Time time = new Time("GMT+8");
    		time.setToNow();
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTimeInMillis(System.currentTimeMillis());
    		calendar.set(Calendar.YEAR, time.year);
    		calendar.set(Calendar.MONTH, time.month);
    		calendar.set(Calendar.DAY_OF_MONTH, time.monthDay);
    		calendar.set(Calendar.HOUR_OF_DAY, hour);
    		calendar.set(Calendar.MINUTE, 0);
    		calendar.set(Calendar.SECOND, 0);// we set second zero.
    		calendar.set(Calendar.MILLISECOND, 0);
    		long settedTime;
    		long circleTime = 24 * 60 * 60 * 1000;
    		if (time.hour < hour) {
    			settedTime = calendar.getTimeInMillis();
    		} else {
    			settedTime = calendar.getTimeInMillis() + circleTime;
    		}

    		aManager.setRepeating(AlarmManager.RTC_WAKEUP, settedTime, circleTime,
    				pi);
        }
    }
}