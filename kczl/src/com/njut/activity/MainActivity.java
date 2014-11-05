package com.njut.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.njut.R;
import com.njut.data.AdviceElement;
import com.njut.data.Curriculum;
import com.njut.data.SpitElement;
import com.njut.database.SMessageDBTask;
import com.njut.utility.AppUtils;
import com.njut.utility.JsonParse;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/*主界面，侧边栏菜单的实现*/
public class MainActivity extends ActivityGroup{
	/** Called when the activity is first created. */

	private SlidingMenu menu;
	private RelativeLayout timetable_Layout;
	private RelativeLayout achievement_layout;
	private RelativeLayout message_layout;
	private RelativeLayout config_layout;

	private ImageView imageview_timetable;
	private ImageView imageview_achievement;
	private ImageView imageview_config;
	private ImageView imageview_message;

	private LinearLayout block_timetable;
	private LinearLayout block_grade;
	private LinearLayout block_msg;
	private LinearLayout block_setting;

	private LinearLayout linear;

	private Integer unViewCount;
	private TextView unViewedMsgCount_tv;
	private ImageView badge_iv;
	
	private final int READ_DB_FINISHED = 123;

	private Boolean isExit = false;
	private Boolean hasTask = false;
	Timer tExit = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isExit = false;
			hasTask = true;
		}
	};
    
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READ_DB_FINISHED:
				if(unViewCount > 0)
				{
					badge_iv.setVisibility(View.VISIBLE);
					unViewedMsgCount_tv.setText(String.valueOf(unViewCount));
				}
				else
				{
					badge_iv.setVisibility(View.INVISIBLE);
					unViewedMsgCount_tv.setText("");
				}
				break;
			}
		};
	};


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				if (!hasTask) {
					tExit.schedule(task, 2000);//
				}
			} else {
				// Intent intent = new Intent(
				// this,
				// WelcomeActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				for (Iterator<Activity> it = kczlApplication.activitiesList.iterator(); it.hasNext();) {
					Activity mActivity = (Activity) it.next();
					if (mActivity != null) {
						mActivity.finish();
					}

				}
			}

			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	// @Override
	// public boolean dispatchKeyEvent(KeyEvent event) {
	//
	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	// return true;
	// }
	// return super.dispatchKeyEvent(event);
	// }

	@SuppressWarnings("deprecation")
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.v("System.out", "onSaveInstanceState called!");
		outState.putString("PersonString", kczlApplication.PersonString);
		outState.putString("UserName", kczlApplication.UserName);
		outState.putString("PassWord", kczlApplication.PassWord);
		outState.putInt("IsLogined", kczlApplication.IsLogined);
		outState.putInt("isClocked", kczlApplication.isClocked);
		outState.putInt("isNotification", kczlApplication.isNotification);
		outState.putInt("IsOffLine", kczlApplication.IsOffLine);
		outState.putString("ServerUri", kczlApplication.ServerUri);
		// outState.putString("email", kczlApplication.sCookie.getEmail());
		// outState.putString("sessioncode",kczlApplication.sCookie.getSessioncode());
		outState.putString("CurriculumsString",
				kczlApplication.CurriculumsString);
		outState.putString("AchievementString",
				kczlApplication.AchievementString);
		outState.putString("SpitString",
				kczlApplication.SpitString);
		outState.putString("AdviceString",
				kczlApplication.AdviceString);
	}
	/*
	 * 注意如果您的Activity之间有继承或者控制关系请不要同时在父和子Activity中重复添加onPause和onResume方法，
	 * 否则会造成重复统计(eg.使用TabHost、TabActivity、ActivityGroup时)。*/
	//	@Override
	//	public void onResume() {
	//		super.onResume();
	//		MobclickAgent.onResume(this);
	//	}
	//
	//	@Override
	//	public void onPause() {
	//		super.onPause();
	//		MobclickAgent.onPause(this);
	//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(null != savedInstanceState)
		{
			kczlApplication.PersonString = savedInstanceState
					.getString("PersonString");
			kczlApplication.UserName = savedInstanceState.getString("UserName");
			kczlApplication.PassWord = savedInstanceState.getString("PassWord");
			kczlApplication.ServerUri = savedInstanceState.getString("ServerUri");
			kczlApplication.IsLogined = savedInstanceState.getInt("IsLogined");
			kczlApplication.AchievementString = savedInstanceState
					.getString("AchievementString");
			kczlApplication.isClocked = savedInstanceState.getInt("isClocked");
			kczlApplication.isNotification = savedInstanceState
					.getInt("isNotification");
			kczlApplication.IsOffLine = savedInstanceState.getInt("IsOffLine");
			// kczlApplication.sCookie.setEmail(savedInstanceState.getString("email"));
			// kczlApplication.sCookie.setSessioncode(savedInstanceState.getString(
			// "sessioncode"));
			JsonParse jp = new JsonParse();
			kczlApplication.Person = jp.jsonToPersonElement(savedInstanceState
					.getString("PersonString"));
			kczlApplication.CurriculumsString = savedInstanceState
					.getString("CurriculumsString");
			kczlApplication.AdviceString = savedInstanceState
					.getString("AdviceString");
			kczlApplication.SpitString = savedInstanceState
					.getString("SpitString");
			Log.v("System.out","begin");
			Log.v("System.out", "onCreate:kczlApplication.SpitString:"+kczlApplication.SpitString);
			Log.v("System.out", "onCreate:kczlApplication.CurriculumsString:"+kczlApplication.CurriculumsString);
			Log.v("System.out", "onCreate:kczlApplication.AdviceString:"+kczlApplication.AdviceString);
			Log.v("System.out","end");
			try {
				if (!(kczlApplication.CurriculumsString.equals(""))) {
					JSONArray jsonObjs;

					jsonObjs = new JSONArray(kczlApplication.CurriculumsString);
					List<Curriculum> curriculums = new ArrayList<Curriculum>();
					for (int i = 0; i < jsonObjs.length(); i++) {
						curriculums.add(jp.jsonToCurriculum((JSONObject) jsonObjs
								.opt(i)));
					}
					kczlApplication.Curriculums.clear();
					kczlApplication.Curriculums.addAll(curriculums);
				}

				if (!(kczlApplication.AdviceString.equals(""))) {
					JSONArray jsonAdvice = new JSONArray(
							kczlApplication.AdviceString);
					List<AdviceElement> adviceElements = new ArrayList<AdviceElement>();
					for (int i = 0; i < jsonAdvice.length(); i++) {
						adviceElements
						.add(jp.jsonToAdviceElement((JSONObject) jsonAdvice
								.opt(i)));
						kczlApplication.AdviceElements.clear();
						kczlApplication.AdviceElements.addAll(adviceElements);
					}
				}
				if (!(kczlApplication.SpitString.equals(""))) {
					JSONArray jsonSpit = new JSONArray(kczlApplication.SpitString);
					List<SpitElement> spitElements = new ArrayList<SpitElement>();
					for (int i = 0; i < jsonSpit.length(); i++) {
						spitElements.add(jp.jsonToSpitElement((JSONObject) jsonSpit
								.opt(i)));
					}
					kczlApplication.SpitElements.clear();
					kczlApplication.SpitElements.addAll(spitElements);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("System.out", "onCreate:kczlApplication.SpitElements.size():"+kczlApplication.SpitElements.size());
			Log.v("System.out", "onCreate:kczlApplication.AdviceElements.size():"+kczlApplication.AdviceElements.size());
			Log.v("System.out", "onCreate:kczlApplication.Curriculums.size():"+kczlApplication.Curriculums.size());
		}
		XGPushConfig.enableDebug(this, true);

		XGPushManager.registerPush(this,kczlApplication.Person.getSchoolnumber());
		XGPushManager.setTag(MainActivity.this, "student");
		String token = XGPushConfig.getToken(MainActivity.this);
		Log.v("System.out", "MainActivity  onCreate");
		kczlApplication.activitiesList.add(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		linear = (LinearLayout) findViewById(R.id.mian);
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int offset = dm.widthPixels / 3;
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchmodeMarginThreshold(100);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffset(offset);
		menu.setFadeDegree(0.1f);
		menu.setBehindScrollScale(0);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slide_menu);
		findViews();
		setListener();
		linear.removeAllViews();

		String isFromNotice = getIntent().getStringExtra("isFromNotice");
		String isFromXgMessage = getIntent().getStringExtra("isFromXgMessage");
		if (isFromNotice != null && isFromNotice.equals("true")) {
			Intent intent_notice = new Intent(MainActivity.this,
					WeekAndMonthTabActivity.class);
			View view = MainActivity.this.getLocalActivityManager()
					.startActivity("suibian", intent_notice).getDecorView();
			restore();
			imageview_timetable.setImageResource(R.drawable.menu_classtable_p);
			timetable_Layout.setBackgroundResource(R.color.menu_bg_pressed);
			block_timetable.setVisibility(View.VISIBLE);
			linear.addView(view);
		} 
		else if(isFromXgMessage!=null && isFromXgMessage.equals("true")){
			Intent intent = new Intent(MainActivity.this, MessageActivity.class);
			View view = MainActivity.this.getLocalActivityManager()
					.startActivity("suibian3", intent).getDecorView(); // 两个不能一样的
			linear.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			restore();
			imageview_message
			.setImageResource(R.drawable.menu_msg_p);
			block_msg.setVisibility(View.VISIBLE);
			message_layout.setBackgroundResource(R.color.menu_bg_pressed);
		}
		else
		{
			Intent intent = new Intent(MainActivity.this,WeekAndMonthTabActivity.class);
			View view = MainActivity.this.getLocalActivityManager()
					.startActivity("suibian", intent).getDecorView();
			// 不加这句话，首页被挤的很小。
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageview_timetable
			.setImageResource(R.drawable.menu_home_p);
			timetable_Layout.setBackgroundResource(R.color.menu_bg_pressed);
			block_timetable.setVisibility(View.VISIBLE);
			linear.addView(view);
		}

		//		if (kczlApplication.Person != null) {
		//			String firstname = String.valueOf(kczlApplication.Person
		//					.getRealname().charAt(0));
		//			bigname.setText(firstname);
		//		}

		AppUtils.menu = menu;
//		getLoaderManager().initLoader(LOADER_ID, null, this);
		new SMessageDBThread().start();
	}

	public void findViews() {
		badge_iv = (ImageView)menu.findViewById(R.id.badge_iv);
		timetable_Layout = (RelativeLayout) menu.findViewById(R.id.course);
		achievement_layout = (RelativeLayout) menu.findViewById(R.id.grade);
		message_layout = (RelativeLayout) menu.findViewById(R.id.msg);
		config_layout = (RelativeLayout) menu.findViewById(R.id.config);

		imageview_timetable = (ImageView) menu.findViewById(R.id.menu_timetable_iv);
		imageview_achievement = (ImageView) menu.findViewById(R.id.menu_grade_iv);
		imageview_config = (ImageView) menu.findViewById(R.id.menu_setting_iv);
		imageview_message = (ImageView) menu.findViewById(R.id.menu_msg_iv);

		block_timetable = (LinearLayout) menu.findViewById(R.id.block_timetable);
		block_grade = (LinearLayout) menu.findViewById(R.id.block_grade);
		block_msg = (LinearLayout) menu.findViewById(R.id.block_msg);
		block_setting = (LinearLayout) menu.findViewById(R.id.block_setting);
		unViewedMsgCount_tv = (TextView) menu.findViewById(R.id.msg_count_tv);
	}

	public void setListener() {
		timetable_Layout.setOnClickListener(clickListener_home);
		achievement_layout.setOnClickListener(clickListener_style);
		config_layout.setOnClickListener(clickListener_config);
		message_layout.setOnClickListener(clickListener_message);
	}

	
	private OnClickListener clickListener_home = new OnClickListener() {
		@Override
		public void onClick(View v) {

			linear.removeAllViews();
			// timetableText.setTextColor(Color.BLACK);
			// achievementtableText.setTextColor(Color.WHITE);
			// configText.setTextColor(Color.WHITE);
			Intent intent = new Intent(MainActivity.this,
					WeekAndMonthTabActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			View view = MainActivity.this.getLocalActivityManager()
					.startActivity("suibian", intent).getDecorView(); // 两个不能一样的
			linear.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			menu.showContent();
			restore();
			imageview_timetable
			.setImageResource(R.drawable.menu_classtable_p);
			block_timetable.setVisibility(View.VISIBLE);
			timetable_Layout.setBackgroundResource(R.color.menu_bg_pressed);
		}
	};

	private OnClickListener clickListener_style = new OnClickListener() {
		@Override
		public void onClick(View v) {
			linear.removeAllViews();
			// timetableText.setTextColor(Color.WHITE);
			// achievementtableText.setTextColor(Color.BLACK);
			// configText.setTextColor(Color.WHITE);
			Intent intent = new Intent(MainActivity.this,
					QueryAchievementActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			View view = MainActivity.this.getLocalActivityManager()
					.startActivity("suibian1", intent).getDecorView(); // 两个不能一样的
			linear.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			menu.showContent();
			restore();
			imageview_achievement
			.setImageResource(R.drawable.menu_grade_p);
			block_grade.setVisibility(View.VISIBLE);
			achievement_layout.setBackgroundResource(R.color.menu_bg_pressed);
		}
	};

	private OnClickListener clickListener_config = new OnClickListener() {
		@Override
		public void onClick(View v) {
			linear.removeAllViews();
			// timetableText.setTextColor(Color.WHITE);
			// achievementtableText.setTextColor(Color.WHITE);
			// configText.setTextColor(Color.BLACK);
			Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			View view = MainActivity.this.getLocalActivityManager()
					.startActivity("suibian2", intent).getDecorView(); // 两个不能一样的
			linear.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			menu.showContent();
			restore();
			imageview_config
			.setImageResource(R.drawable.menu_setting_p);
			block_setting.setVisibility(View.VISIBLE);
			config_layout.setBackgroundResource(R.color.menu_bg);
		}
	};

	private OnClickListener clickListener_message = new OnClickListener() {
		@Override
		public void onClick(View v) {
			linear.removeAllViews();
			// timetableText.setTextColor(Color.WHITE);
			// achievementtableText.setTextColor(Color.WHITE);
			// configText.setTextColor(Color.BLACK);
			Intent intent = new Intent(MainActivity.this, MessageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			View view = MainActivity.this.getLocalActivityManager()
					.startActivity("suibian3", intent).getDecorView(); // 两个不能一样的
			linear.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			menu.showContent();
			restore();
			imageview_message
			.setImageResource(R.drawable.menu_msg_p);
			block_msg.setVisibility(View.VISIBLE);
			message_layout.setBackgroundResource(R.color.menu_bg_pressed);
		}
	};

	public void restore()// 就是点击menu的按钮时把按钮背景都改成没有selected
	{
		imageview_timetable.setImageResource(R.drawable.menu_classtable);
		imageview_achievement.setImageResource(R.drawable.menu_grade);
		imageview_config.setImageResource(R.drawable.menu_setting);
		imageview_message.setImageResource(R.drawable.menu_msg);
	
		block_timetable.setVisibility(View.INVISIBLE);
		block_grade.setVisibility(View.INVISIBLE);
		block_msg.setVisibility(View.INVISIBLE);
		block_setting.setVisibility(View.INVISIBLE);

		timetable_Layout.setBackgroundResource(R.color.menu_bg);
		achievement_layout.setBackgroundResource(R.color.menu_bg);
		message_layout.setBackgroundResource(R.color.menu_bg);
		config_layout.setBackgroundResource(R.color.menu_bg);
	}
	
	public void updateBadge()
	{
		new SMessageDBThread().start();
	}

//	@Override
//	public Loader<Integer> onCreateLoader(int id, Bundle args) {
//		// TODO Auto-generated method stub
//		return new SMessageUnViewedCountDBLoader(MainActivity.this, args);
//	}
//
//	@Override
//	public void onLoadFinished(Loader<Integer> loader, Integer data) {
//		// TODO Auto-generated method stub
//		unViewCount = data;
//		if(unViewCount > 0)
//		{
//			badge_iv.setVisibility(View.VISIBLE);
//			unViewedMsgCount_tv.setText(String.valueOf(unViewCount));
//		}
//		else
//		{
//			badge_iv.setVisibility(View.INVISIBLE);
//			unViewedMsgCount_tv.setText("");
//		}
//	}
//
//	@Override
//	public void onLoaderReset(Loader<Integer> loader) {
//		// TODO Auto-generated method stub
//		unViewCount = 0;
//		unViewedMsgCount_tv.setText("");
//		badge_iv.setVisibility(View.INVISIBLE);
//	}
	
//	private static class SMessageUnViewedCountDBLoader extends AsyncTaskLoader<Integer>
//	{
//
//		public SMessageUnViewedCountDBLoader(Context context, Bundle args) {
//			super(context);
//		}
//
//		@Override
//		protected void onStartLoading() {
//			super.onStartLoading();
//			forceLoad();
//		}
//
//		public Integer loadInBackground() {
//			return SMessageDBTask.getUnViewedCount();
//		}
//
//	}
	
	
	class SMessageDBThread extends Thread
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			unViewCount = SMessageDBTask.getUnViewedCount();
			myHandler.sendEmptyMessage(READ_DB_FINISHED);
		}
	}
}