package com.njut.activity;

import static com.devspark.appmsg.AppMsg.LENGTH_STICKY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.kczl.notification.MyNoticeReceiver;
import org.nutlab.kczl.notification.TNoticeReceiver;
import org.nutlab.webService.curriculumService;
import org.nutlab.webService.curriculumteacherService;
import org.nutlab.webService.loginService;
import org.nutlab.webService.loginteacherService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.njut.R;
import com.njut.data.CourseTeacherElement;
import com.njut.data.Curriculum;
import com.njut.utility.AppUtils;
import com.njut.utility.JsonParse;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity {

	private String TAG = " LoginActivity ";
	private EditText mUserNameEt;
	private EditText mPwdEt;
	private Button mLoginBtn;
	protected final int LOGIN_FINISHED = 1;
	protected final int CURRICULUM_GET_FINISHED = 2;
	protected final int LOGIN_TEACHER_FINISHED = 3;
	protected final int CURRICULUM_TEACHER_GET_FINISHED = 4;
	private ProgressDialog progressDialog;
	private TextView origin_pwd_tv;
	private TextView forget_pwd_tv;
	private TextView bottom_tv;
	private AppMsg.Style style;
	private Button mCQC_SLoginBtn;
	private Button mCQC_TLoginBtn;

	protected final String RETURN_STRING = "returnString";

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case LOGIN_FINISHED: {
				Bundle bundle = msg.getData();
				Log.v("GPGPGP", "login_end:"+System.currentTimeMillis());
				try {
					finishLoginOperation(bundle.getString(RETURN_STRING));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			break;
			case CURRICULUM_GET_FINISHED: {
				Bundle bundle = msg.getData();
				try {
					finishCurriculumGetOperation(bundle
							.getString(RETURN_STRING));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			break;
			case LOGIN_TEACHER_FINISHED: {
				Bundle bundle = msg.getData();
				try {
					finishteacherLoginOperation(bundle.getString(RETURN_STRING));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			break;
			case CURRICULUM_TEACHER_GET_FINISHED: {
				Bundle bundle = msg.getData();
				try {
					finishCurriculumTeacherGetOperation(bundle
							.getString(RETURN_STRING));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			break;
			}
			super.handleMessage(msg);
		}
	};

	class CurriculumGetThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = CURRICULUM_GET_FINISHED;
			curriculumService cs = new curriculumService();
			String msg = cs.get();
			Bundle bundle = new Bundle();
			bundle.putString(RETURN_STRING, msg);
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	class CurriculumTeacherGetThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = CURRICULUM_TEACHER_GET_FINISHED;
			curriculumteacherService cts = new curriculumteacherService();
			String msg = cts.get();
			Bundle bundle = new Bundle();
			bundle.putString(RETURN_STRING, msg);
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	class LoginThread extends Thread {
		public void run() {
			String msg;
			Message message = new Message();
			loginService ls = new loginService();
			loginteacherService lts = new loginteacherService();
			kczlApplication.UserName = mUserNameEt.getText().toString();
			kczlApplication.PassWord = mPwdEt.getText().toString();
			if (kczlApplication.UserName.length() >= 10) {
				System.out.println(kczlApplication.UserName.length());
				message.what = LOGIN_FINISHED;
				msg = ls.login(kczlApplication.UserName,
						kczlApplication.PassWord);
			} else {
				message.what = LOGIN_TEACHER_FINISHED;
				msg = lts.login(kczlApplication.UserName,
						kczlApplication.PassWord);
			}
			Bundle bundle = new Bundle();
			bundle.putString(RETURN_STRING, msg);
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	protected void finishCurriculumGetOperation(String mStringReturnStr)
			throws JSONException {
		kczlApplication.CurriculumsString = mStringReturnStr;
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("CurriculumsString", mStringReturnStr);
		editor.commit();
		JSONArray jsonObjs = new JSONArray(mStringReturnStr);
		List<Curriculum> curriculums = new ArrayList<Curriculum>();
		for (int i = 0; i < jsonObjs.length(); i++) {
			JsonParse jp = new JsonParse();
			curriculums.add(jp.jsonToCurriculum((JSONObject) jsonObjs.opt(i)));
		}
		kczlApplication.Curriculums = curriculums;

		// 进入系统
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	protected void finishCurriculumTeacherGetOperation(String mStringReturnStr)
			throws JSONException {

		kczlApplication.CurriculumtString = mStringReturnStr;
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("CurriculumtString", mStringReturnStr);
		editor.commit();
		JSONArray jsonObjs = new JSONArray(mStringReturnStr);
		List<CourseTeacherElement> curriculumt = new ArrayList<CourseTeacherElement>();
		for (int i = 0; i < jsonObjs.length(); i++) {
			JsonParse jp = new JsonParse();
			curriculumt.add(jp.jsonToCourseTeacherElement((JSONObject) jsonObjs
					.opt(i)));
		}
		kczlApplication.CourseTeacherElements = curriculumt;

		// 进入系统
		Intent intent = new Intent(LoginActivity.this,
				CurriculumTabActivity.class);
		startActivity(intent);

	}

	protected void finishLoginOperation(String mStringReturnStr)
			throws JSONException {

		JSONObject jsonObj = new JSONObject(mStringReturnStr);
		if (((String) jsonObj.keys().next()).equals("msg")) {
			//			new AlertDialog.Builder(this).setTitle("登录状态")
			//					.setMessage(jsonObj.getString("msg"))
			//					.setPositiveButton("是", null).show();

			style = new AppMsg.Style(2000, R.color.alert);
			if(jsonObj.getString("msg").equals("login denied: wrong password"))	
			{
				AppMsg.makeText(LoginActivity.this, "密码错误", style).show();
				mPwdEt.setText("");
			}
			if(jsonObj.getString("msg").equals("login denied: no such user"))
			{
				AppMsg.makeText(LoginActivity.this, "该账户不存在", style).show();
			}

		} else {
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			kczlApplication.IsLogined = 1;
			kczlApplication.IsStudent = 1;
			editor.putInt("IsLogined", 1);
			editor.putString("JSON_VERSION","1.0");
			editor.putInt("IsStudent", 1);
			editor.putString("UserName", kczlApplication.UserName);
			editor.putString("PassWord", kczlApplication.PassWord);
			JsonParse jp = new JsonParse();
			kczlApplication.Person = jp.jsonToPersonElement(mStringReturnStr);
			kczlApplication.PersonString = mStringReturnStr;
			int year = Integer.parseInt(kczlApplication.Person.getBegindate()
					.split("-")[0]);
			int month = Integer.parseInt(kczlApplication.Person.getBegindate()
					.split("-")[1]);
			if (month > 7) {
				kczlApplication.Year = year + "-" + (year + 1);
				kczlApplication.Term = "第一学期";
			} else {
				kczlApplication.Year = (year - 1) + "-" + year;
				kczlApplication.Term = "第二学期";
			}
			setSReminder();
			kczlApplication.isClocked = 1;
			editor.putInt("isClocked",kczlApplication.isClocked);
			kczlApplication.isNotification = 1;
			editor.putInt("isNotification",kczlApplication.isNotification);
			editor.putString("Year", kczlApplication.Year);
			editor.putString("Term", kczlApplication.Term);
			editor.putString("PersonString", mStringReturnStr);
			editor.commit();

			// 登录成功后开始载入课程表信息
			CurriculumGetThread CGT = new CurriculumGetThread();
			progressDialog = ProgressDialog.show(
					new ContextThemeWrapper(LoginActivity.this,
							android.R.style.Theme_Holo_Light_Dialog),
							null, getString(R.string.loading),
							true);
			CGT.start();

		}
	}

	protected void finishteacherLoginOperation(String mStringReturnStr)
			throws JSONException {
		JSONObject jsonObj = new JSONObject(mStringReturnStr);
		if (((String) jsonObj.keys().next()).equals("msg")) {
			//			new AlertDialog.Builder(this).setTitle("登录状态")
			//			.setMessage(jsonObj.getString("msg"))
			//			.setPositiveButton("是", null).show();
			style = new AppMsg.Style(2000, R.color.alert);
			if(jsonObj.getString("msg").equals("login denied: wrong password"))	
			{
				AppMsg.makeText(LoginActivity.this, "密码错误", style).show();
				mPwdEt.setText("");
			}
			if(jsonObj.getString("msg").equals("login denied: no such teacher"))
			{
				AppMsg.makeText(LoginActivity.this, "该账户不存在", style).show();
			}

		} else {
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			kczlApplication.IsLogined = 1;
			kczlApplication.IsStudent = 2;
			editor.putInt("IsLogined", 1);
			editor.putInt("IsStudent", 2);
			editor.putString("JSON_VERSION","1.0");
			editor.putString("UserName", kczlApplication.UserName);
			editor.putString("PassWord", kczlApplication.PassWord);
			JsonParse jp = new JsonParse();
			kczlApplication.PersonTeacher = jp
					.jsonToPersonTeacherElement(mStringReturnStr);
			kczlApplication.PersonTeacherString = mStringReturnStr;
			editor.putString("PersonTeacherString",
					kczlApplication.PersonTeacherString);
			editor.commit();
            setTReminder();
            kczlApplication.isClocked = 1;
			editor.putInt("isClocked",kczlApplication.isClocked);
			kczlApplication.isNotification = 1;
			editor.putInt("isNotification",kczlApplication.isNotification);
			// 登录成功后开始载入课程表信息
			CurriculumTeacherGetThread CTGT = new CurriculumTeacherGetThread();
			progressDialog = ProgressDialog.show(
					new ContextThemeWrapper(LoginActivity.this,
							android.R.style.Theme_Holo_Light_Dialog),
							null, getString(R.string.loading),
							true);
			CTGT.start();
		}
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		kczlApplication.activitiesList.add(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_new);
		initView();
	}

	private void initView() {
		bottom_tv = (TextView) findViewById(R.id.bottom_tv);
		origin_pwd_tv = (TextView) findViewById(R.id.origin_pwd_tv);
		forget_pwd_tv = (TextView) findViewById(R.id.forget_pwd_tv);
		origin_pwd_tv.setText(Html.fromHtml("<u>" + "初始密码" + "</u>"));
		origin_pwd_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				style = new AppMsg.Style(LENGTH_STICKY, R.color.info);
				final AppMsg provided = AppMsg.makeText(LoginActivity.this, "初始密码：教师为4位工号，学生为8位出生日期", style,R.layout.sticky);
				provided.getView()
				.findViewById(R.id.remove_btn)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						provided.cancel();
					}
				});
				provided.show();
			}
		});
		forget_pwd_tv.setText(Html.fromHtml("<u>" + "忘记密码" + "</u>"));
		forget_pwd_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				style = new AppMsg.Style(LENGTH_STICKY, R.color.info);
				final AppMsg provided = AppMsg.makeText(LoginActivity.this, "请联系客服：njtechmlab@163.com", style,R.layout.sticky);
				provided.getView()
				.findViewById(R.id.remove_btn)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						provided.cancel();
					}
				});
				provided.show();
			}
		});
		bottom_tv
		.setText(Html
				.fromHtml("Powered by NanjingTech <a href=\"http://mlab.njtech.edu.cn\">M-Lab</a>"));
		bottom_tv.setMovementMethod(LinkMovementMethod.getInstance());
		mUserNameEt = (EditText) findViewById(R.id.username_et);
		mPwdEt = (EditText) findViewById(R.id.pwd_et);
		mLoginBtn = (Button) findViewById(R.id.login_btn);
		mPwdEt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// TODO Auto-generated method stub
					Drawable pressed = getResources().getDrawable(
							R.drawable.login_pwd_pressed);
					// / 这一步必须要做,否则不会显示.
					pressed.setBounds(0, 0, pressed.getMinimumWidth(),
							pressed.getMinimumHeight());
					mPwdEt.setCompoundDrawables(pressed, null, null, null);
				} else {
					Drawable normal = getResources().getDrawable(
							R.drawable.login_pwd);
					// / 这一步必须要做,否则不会显示.
					normal.setBounds(0, 0, normal.getMinimumWidth(),
							normal.getMinimumHeight());
					mPwdEt.setCompoundDrawables(normal, null, null, null);
				}
			}
		});
		mUserNameEt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// TODO Auto-generated method stub
					Drawable pressed = getResources().getDrawable(
							R.drawable.login_user_pressed);
					// / 这一步必须要做,否则不会显示.
					pressed.setBounds(0, 0, pressed.getMinimumWidth(),
							pressed.getMinimumHeight());
					mUserNameEt.setCompoundDrawables(pressed, null, null, null);
				} else {
					Drawable normal = getResources().getDrawable(
							R.drawable.login_user);
					// / 这一步必须要做,否则不会显示.
					normal.setBounds(0, 0, normal.getMinimumWidth(),
							normal.getMinimumHeight());
					mUserNameEt.setCompoundDrawables(normal, null, null, null);
				}
			}
		});
		mPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (AppUtils.isOpenNetwork(LoginActivity.this)) {
					if (mUserNameEt.getText().toString().equals("")
							|| mPwdEt.getText().toString().equals("")) {
						style = new AppMsg.Style(2000, R.color.confirm);
						AppMsg.makeText(LoginActivity.this, "不能为空", style).show();
						return false;
					}
					if (actionId == EditorInfo.IME_ACTION_GO) {
						LoginThread LT = new LoginThread();
						progressDialog = ProgressDialog
								.show(new ContextThemeWrapper(
										LoginActivity.this,
										android.R.style.Theme_Holo_Light_Dialog),
										getString(R.string.state),
										getString(R.string.logining), true);
						Log.v("GPGPGP", "login_start:"+System.currentTimeMillis());
						LT.start();
					}
				} else {
					style = new AppMsg.Style(2000, R.color.confirm);
					AppMsg.makeText(LoginActivity.this, "请检查网络设置", style).show();
				}
				return false;
			}
		});
		mLoginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AppUtils.isOpenNetwork(LoginActivity.this)) {
					if (mUserNameEt.getText().toString().equals("")
							|| mPwdEt.getText().toString().equals("")) {
						style = new AppMsg.Style(2000, R.color.confirm);
						AppMsg.makeText(LoginActivity.this, "不能为空", style).show();
						return;
					}
					LoginThread LT = new LoginThread();
					progressDialog = ProgressDialog.show(
							new ContextThemeWrapper(LoginActivity.this,
									android.R.style.Theme_Holo_Light_Dialog),
									null,
									getString(R.string.logining), true);
					LT.start();
				} else {
					style = new AppMsg.Style(2000, R.color.confirm);
					AppMsg.makeText(LoginActivity.this, "请检查网络设置", style).show();
				}
			}
		});
		mCQC_SLoginBtn = (Button) findViewById(R.id.cqc_slogin_btn);
		mCQC_TLoginBtn = (Button) findViewById(R.id.cqc_tlogin_btn);
		mCQC_SLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUserNameEt.setText("8888888888");
				mPwdEt.setText("8");
				LoginThread LT = new LoginThread();
				progressDialog = ProgressDialog.show(
						new ContextThemeWrapper(LoginActivity.this,
								android.R.style.Theme_Holo_Light_Dialog),
								getString(R.string.state),
								getString(R.string.logining), true);
				LT.start();
			}
		});
		mCQC_TLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUserNameEt.setText("8888");
				mPwdEt.setText("8");
				LoginThread LT = new LoginThread();
				progressDialog = ProgressDialog.show(
						new ContextThemeWrapper(LoginActivity.this,
								android.R.style.Theme_Holo_Light_Dialog),
								getString(R.string.state),
								getString(R.string.logining), true);
				LT.start();
			}
		});
	}
	
	void setTReminder()
	{
		// get the AlarmManager instance
				AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				// create a PendingIntent that will perform a broadcast

				Intent myintent = new Intent(LoginActivity.this,
						TNoticeReceiver.class);
				myintent.setAction("org.nutlab.kczl.notification.TReceiver");
				PendingIntent pi = PendingIntent.getBroadcast(LoginActivity.this, 0,
						myintent, 0);
				// 8点响
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

	private void setSReminder() {

		// get the AlarmManager instance
		AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// create a PendingIntent that will perform a broadcast

		Intent myintent = new Intent(LoginActivity.this,
				MyNoticeReceiver.class);
		myintent.setAction("org.nutlab.kczl.notification.MyReceiver2");
		PendingIntent pi = PendingIntent.getBroadcast(LoginActivity.this, 0,
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
	public boolean onTouchEvent(MotionEvent event) {  
		// TODO Auto-generated method stub  
		if(event.getAction() == MotionEvent.ACTION_DOWN){  
			InputMethodManager  manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){  
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
			} 
			//			if(mPwdEt != null)
			//				mPwdEt.setCursorVisible(false);//失去光标
			//			if(mUserNameEt != null)
			//				mUserNameEt.setCursorVisible(false);//失去光标
		}  
		return super.onTouchEvent(event);  
	}
	
	
}
