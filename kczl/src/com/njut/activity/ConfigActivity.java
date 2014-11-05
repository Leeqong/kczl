package com.njut.activity;

import java.util.Calendar;

import org.nutlab.kczl.kczlApplication;
import org.nutlab.kczl.notification.MyNoticeReceiver;

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
import com.njut.database.SMessageDBTask;
import com.njut.utility.AppUtils;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class ConfigActivity extends Activity {
	private Button backButton;
	private Button changePwdButton;
	private Button feedbackButton;
	private Button aboutButton;
	private Button logoutButton;
	private Button updateButton;
	private TextView nameTextView;
	private TextView collegeTextView;
	private TextView majorTextView;
	private TextView classTextView;
	private ToggleButton notificationToggleButton;

	private void setReminder() {

		// get the AlarmManager instance
		AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// create a PendingIntent that will perform a broadcast
		Intent myintent = new Intent(ConfigActivity.this, MyNoticeReceiver.class);
		myintent.setAction("org.nutlab.kczl.notification.MyReceiver2");
		PendingIntent pi = PendingIntent.getBroadcast(ConfigActivity.this, 0,
				myintent, 0);
		// 7点响
		int hour = 7;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.config);

		/* 个人信息帮顶 */
		nameTextView = (TextView) findViewById(R.id.name_Text);
		nameTextView.setText(kczlApplication.Person.getRealname());
		collegeTextView = (TextView) findViewById(R.id.college_Text);
		collegeTextView.setText(kczlApplication.Person.getCollegename());
		majorTextView = (TextView) findViewById(R.id.specialty_Text);
		majorTextView.setText(kczlApplication.Person.getFieldName());
		classTextView = (TextView) findViewById(R.id.class_Text);
		classTextView.setText(kczlApplication.Person.getNatureclassname());

		backButton = (Button) findViewById(R.id.toolbar_nav_button);
		changePwdButton = (Button) findViewById(R.id.change_pwd_button);
		feedbackButton = (Button) findViewById(R.id.feedback_button);
		aboutButton = (Button) findViewById(R.id.about_button);
		logoutButton = (Button) findViewById(R.id.login_out_button);
		updateButton = (Button)findViewById(R.id.update_button);
		notificationToggleButton = (ToggleButton) findViewById(R.id.curriculum_remind_ToggleButton);
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
		notificationToggleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 当按钮第一次被点击时候响应的事件
				if (notificationToggleButton.isChecked()) {
					if(kczlApplication.isClocked == 0)
					{
						kczlApplication.isNotification = 1;
						setReminder();
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
						Intent myintent = new Intent(ConfigActivity.this,
								MyNoticeReceiver.class);
						myintent.setAction("org.nutlab.kczl.notification.MyReceiver2");
						PendingIntent pi = PendingIntent.getBroadcast(
								ConfigActivity.this, 0,
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
		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.update(ConfigActivity.this);
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.menu.showMenu();

			}
		});
		changePwdButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ConfigActivity.this,
						ChangePasswordActivity.class);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left );  
			}
		});
		feedbackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedbackAgent agent = new FeedbackAgent(ConfigActivity.this);
				agent.startFeedbackActivity();
				//				Intent intent = new Intent(ConfigActivity.this,
				//						FeedBackActivity.class);
				//				startActivity(intent);
				//				overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left );  
			}
		});
		aboutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ConfigActivity.this,
						AboutActivity.class);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left );  
			}
		});
		logoutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(new ContextThemeWrapper(ConfigActivity.this,android.R.style.Theme_Holo_Light_Dialog))
				.setTitle(null)
				.setMessage("注销")//亲，注销后您就再也收不到课程提醒了，请三思啊！
				.setPositiveButton("再见",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						//需要清除全局变量中的数据,回到最初的时候
						kczlApplication.IsLogined = 0;
						kczlApplication.UserName = "";
						kczlApplication.PassWord = "";
						kczlApplication.PersonString = "";
						kczlApplication.Curriculums.clear();
						kczlApplication.CourseElements.clear();
						kczlApplication.AchievementElements.clear();
						kczlApplication.AdviceElements.clear();
						kczlApplication.SpitElements.clear();

						String PREFS_NAME = "org.nutlab.kczl";
						SharedPreferences settings = getSharedPreferences(
								PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings
								.edit();
						editor.clear();  
						editor.commit(); 

						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						// create a PendingIntent that will perform a broadcast
						Intent myintent = new Intent(ConfigActivity.this,
								MyNoticeReceiver.class);
						myintent.setAction("org.nutlab.kczl.notification.MyReceiver2");
						PendingIntent pi = PendingIntent.getBroadcast(
								ConfigActivity.this, 0,myintent, 0);
						am.cancel(pi);
						SMessageDBTask.removeAll();
						XGPushManager.unregisterPush(ConfigActivity.this);
						// 返回登陆界面
						Intent intent = new Intent(
								ConfigActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("留下", null).show();
			}
		});
	}
}