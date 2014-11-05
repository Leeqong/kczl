package com.njut.activity;

import java.util.Calendar;

import org.nutlab.kczl.kczlApplication;
import org.nutlab.kczl.notification.MyNoticeReceiver;
import org.nutlab.kczl.notification.TNoticeReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.njut.R;
import com.njut.database.TMessageDBTask;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class TeacherConfigActivity extends Activity{

	private String TAG = "TeacherConfigActivity";

	private Button feedbackBtn;
	private Button aboutBtn;
	private Button loginoutBtn;
	private TextView teacherName;
	private TextView teacherNumber;
	private Button changePwdBtn;
	private Button updateBtn;
	private ToggleButton notificationToggleButton;
	private ToggleButton commentRemindToggleButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.teacher_config);	
		kczlApplication.activitiesList.add(this);
		teacherName = (TextView) this.findViewById(R.id.teacher_name);
		teacherNumber = (TextView) this.findViewById(R.id.teacher_number);
		notificationToggleButton = (ToggleButton) findViewById(R.id.system_remind_ToggleButton);
		commentRemindToggleButton  = (ToggleButton) findViewById(R.id.comment_remind_ToggleButton);
		teacherName.setText(kczlApplication.PersonTeacher.getTeachername());
		String teacherNum = kczlApplication.PersonTeacher.getPersonnelnumber();
		int length = teacherNum.length();
		teacherNumber.setText(teacherNum.substring(length-4,length));

		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		kczlApplication.isClocked = settings.getInt("isClocked",
				kczlApplication.isClocked);
		kczlApplication.isNotification = settings.getInt("isNotification",
				kczlApplication.isNotification);
		if (kczlApplication.isNotification == 1) {
			notificationToggleButton.setChecked(true);
		} else {
			notificationToggleButton.setChecked(false);
		}

		if (kczlApplication.isReplyNotification == 1) {
			commentRemindToggleButton.setChecked(true);
		} else {
			commentRemindToggleButton.setChecked(false);
		}
		notificationToggleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 当按钮第一次被点击时候响应的事件
				if (notificationToggleButton.isChecked()) {
					if(kczlApplication.isClocked == 0)
					{
						kczlApplication.isNotification = 1;
						setTReminder();
						kczlApplication.isClocked = 1;
						String PREFS_NAME2 = "org.nutlab.kczl";
						SharedPreferences settings2 = getSharedPreferences(
								PREFS_NAME2, 0);
						SharedPreferences.Editor editor = settings2.edit();
						editor.putInt("isClocked", kczlApplication.isClocked);
						editor.putInt("isNotification",
								kczlApplication.isNotification);
						editor.commit();
					}
				}
				// 当按钮再次被点击时候响应的事件
				else {
					if(kczlApplication.isClocked == 1)
					{
						kczlApplication.isNotification = 0;
						// get the AlarmManager instance
						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						// create a PendingIntent that will perform a broadcast
						Intent myintent = new Intent(TeacherConfigActivity.this, TNoticeReceiver.class);
						myintent.setAction("org.nutlab.kczl.notification.TReceiver");
						PendingIntent pi = PendingIntent.getBroadcast(
								TeacherConfigActivity.this, 0,
								myintent, 0);
						am.cancel(pi);
						kczlApplication.isClocked = 0;
						String PREFS_NAME2 = "org.nutlab.kczl";
						SharedPreferences settings2 = getSharedPreferences(
								PREFS_NAME2, 0);
						SharedPreferences.Editor editor = settings2.edit();
						editor.putInt("isClocked", kczlApplication.isClocked);
						editor.putInt("isNotification",
								kczlApplication.isNotification);
						editor.commit();
					}
				}
			}
		});

		commentRemindToggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(commentRemindToggleButton.isChecked())
				{
					kczlApplication.isReplyNotification = 1;
					String PREFS_NAME2 = "org.nutlab.kczl";
					SharedPreferences settings2 = getSharedPreferences(
							PREFS_NAME2, 0);
					SharedPreferences.Editor editor = settings2.edit();
					editor.putInt("isReplyNotification", kczlApplication.isReplyNotification);
					editor.commit();
				}
				else
				{
					kczlApplication.isReplyNotification = 0;
					String PREFS_NAME2 = "org.nutlab.kczl";
					SharedPreferences settings2 = getSharedPreferences(
							PREFS_NAME2, 0);
					SharedPreferences.Editor editor = settings2.edit();
					editor.putInt("isReplyNotification", kczlApplication.isReplyNotification);
					editor.commit();
				}
			}
		});



		changePwdBtn = (Button)findViewById(R.id.changePwd_btn);

		changePwdBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TeacherConfigActivity.this,
						ChangePasswordActivity.class);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left );  
			}
		});

		feedbackBtn = (Button) findViewById(R.id.feedbcak_btn);

		feedbackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FeedbackAgent agent = new FeedbackAgent(TeacherConfigActivity.this);
				agent.startFeedbackActivity();
				//				Intent intent = new Intent(TeacherConfigActivity.this,
				//						FeedBackActivity.class);
				//				startActivity(intent);
				//				overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left );  
			}
		});

		aboutBtn = (Button) findViewById(R.id.about_btn);

		aboutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(TeacherConfigActivity.this,
						AboutActivity.class);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left );  
			}
		});

		loginoutBtn = (Button) findViewById(R.id.log_out_btn);

		loginoutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				new AlertDialog.Builder(new ContextThemeWrapper(TeacherConfigActivity.this,android.R.style.Theme_Holo_Light_Dialog))
				.setTitle(null)
				.setMessage("亲，注销后您就再也收不到反馈信息了,请三思啊！")
				.setPositiveButton("再见",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						//需要清除全局变量中的数据,回到最初的时候
						kczlApplication.IsLogined = 0;
						kczlApplication.UserName = "";
						kczlApplication.PassWord = "";
						kczlApplication.PersonTeacherString = "";
						kczlApplication.CourseTeacherElements.clear();
						kczlApplication.teacherlist.clear();

						String PREFS_NAME = "org.nutlab.kczl";
						SharedPreferences settings = getSharedPreferences(
								PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings
								.edit();
						editor.clear();  
						editor.commit(); 
						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						// create a PendingIntent that will perform a broadcast
						Intent myintent = new Intent(TeacherConfigActivity.this, TNoticeReceiver.class);
						myintent.setAction("org.nutlab.kczl.notification.TReceiver");
						PendingIntent pi = PendingIntent.getBroadcast(
								TeacherConfigActivity.this, 0,
								myintent, 0);
						am.cancel(pi);
						TMessageDBTask.removeAll();
						XGPushManager.unregisterPush(TeacherConfigActivity.this);
						// 返回登陆界面
						Intent intent = new Intent(
								TeacherConfigActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("留下", null).show();
			}
		});
		updateBtn = (Button) findViewById(R.id.update_btn);
		updateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.update(TeacherConfigActivity.this);
			}
		});

	}

	private void setTReminder() {

		// get the AlarmManager instance
		AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// create a PendingIntent that will perform a broadcast
		Intent myintent = new Intent(TeacherConfigActivity.this, TNoticeReceiver.class);
		myintent.setAction("org.nutlab.kczl.notification.TReceiver");
		PendingIntent pi = PendingIntent.getBroadcast(TeacherConfigActivity.this, 0,
				myintent, 0);
		// 7点响
		int hour = 8;
		Time time = new Time();
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
		// Calendar c=Calendar.getInstance();
		// aManager.setRepeating(AlarmManager.RTC_WAKEUP,SystemClock.elapsedRealtime()
		// +5000,5000,pi);
	}


}
