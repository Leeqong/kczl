package com.njut.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.curriculumService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.njut.R;
import com.njut.data.CourseElement;
import com.njut.data.Curriculum;
import com.njut.utility.AppUtils;
import com.njut.utility.CalendarHelper;
import com.njut.utility.ClassCoverClass;
import com.njut.utility.JsonParse;
import com.njut.utility.SpecialCalendar;
import com.njut.view.CourseAdapter;
import com.njut.view.CourseEveryMonthView;

/**
 * ����������ʾ�Լ��γ̵���ʾ
 * 
 * 
 * 
 */
public class CourseEveryMonthActivity extends Activity implements
		OnGestureListener {
	private String TAG = "CourseEveryMonthActivity";

	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private CourseEveryMonthView calV = null;
	private GridView gridView = null;
	private TextView topText = null;
	private TextView dayText;
	private TextView weekText;
	private static int jumpMonth = 0; // ÿ�λ��������ӻ��ȥһ����,Ĭ��Ϊ0������ʾ��ǰ�£�
	private Date selectedDate;
	private Resources res = null;
	private Button backButton;
	private Button updateButton;
	private ListView courseListView;
	private ProgressDialog progressDialog;
	private List<CourseElement> list;
	private ReturnToTodayReceiver receiver;
	private PopWindow popWindow;
	private CourseAdapter courseAdapter;

	protected final int CURRICULUM_GET_FINISHED = 2;
	protected final int SPIT_ACTIVITY = 3;
	protected final int ADVICE_ACTIVITY = 4;
	protected final String RETURN_STRING = "returnString";

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = new Bundle();
			if (msg.what == SPIT_ACTIVITY || msg.what == ADVICE_ACTIVITY) {
				Bundle bundle1 = msg.getData();
				CourseElement mCourseElement = list.get(bundle1
						.getInt("position"));
				bundle.putString("coursename", mCourseElement.getCourseName());
				bundle.putString("courseNature",
						mCourseElement.getCourseNature());
				bundle.putString("credit", mCourseElement.getCredit());
				bundle.putString("ctid", mCourseElement.getCtid());
				bundle.putString("starttime", mCourseElement.getStarttime());
				bundle.putString("endtime", mCourseElement.getEndtime());
			}
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case CURRICULUM_GET_FINISHED: {
				Bundle bundlecurr = msg.getData();
				try {
					finishCurriculumGetOperation(bundlecurr
							.getString(RETURN_STRING));
					// ˢ��courseListView
					refreshAchievement();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
				break;
			case SPIT_ACTIVITY:

				Intent intent = new Intent(CourseEveryMonthActivity.this,
						SpitActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;
			case ADVICE_ACTIVITY:
				Intent intent1 = new Intent(CourseEveryMonthActivity.this,
						AdviceActivity.class);
				intent1.putExtras(bundle);
				startActivityForResult(intent1, 0);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void finishCurriculumGetOperation(String mStringReturnStr)
			throws JSONException {
		/*
		 * new AlertDialog.Builder(this).setTitle("����")
		 * .setMessage(mStringReturnStr).setPositiveButton("��", null)
		 * .setNegativeButton("��", null).show();
		 */
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
			curriculums.add(jp.jsonToCurriculum(jsonObjs.opt(i).toString()));
		}
		kczlApplication.Curriculums.clear();
		kczlApplication.Curriculums.addAll(curriculums);
	}

	class CurriculumGetThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = CURRICULUM_GET_FINISHED;
			curriculumService cs = new curriculumService();
			String msg = cs.get();
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("IsLogined", kczlApplication.IsLogined);
			editor.commit();
			Bundle bundle = new Bundle();
			bundle.putString(RETURN_STRING, msg);
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	public CourseEveryMonthActivity() {
		Date date = new Date();
		selectedDate = date;

	}

	private void refreshAchievement() {
		courseListView = (ListView) findViewById(R.id.course_ListView);
		ClassCoverClass c2c = new ClassCoverClass();
		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		int day = cHelper.getDay(sf.format(selectedDate),
				kczlApplication.Person.getBegindate());
		int week = cHelper.getWeek(sf.format(selectedDate),
				kczlApplication.Person.getBegindate());
		list.clear();
		list.addAll(c2c.curriculumsToCourseElements(
				kczlApplication.Curriculums, day, week));
		courseAdapter.notifyDataSetChanged();

		courseListView.setOnItemClickListener(new MyOnItemClickListener(
				myHandler, popWindow));
	}

	private void reloadData() {
		CurriculumGetThread CGT = new CurriculumGetThread();
		progressDialog = ProgressDialog.show(
				new ContextThemeWrapper(CourseEveryMonthActivity.this.getParent(),
						android.R.style.Theme_Holo_Light_Dialog),
				null, getString(R.string.loading), true);
		CGT.start();

	}

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
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.course_month);
		res = getResources();
		list = new ArrayList<CourseElement>();
		gestureDetector = new GestureDetector(this);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calV = new CourseEveryMonthView(this, getResources(), jumpMonth,
				selectedDate);

		addGridView();
		gridView.setAdapter(calV);

		flipper.addView(gridView, 0);

		topText = (TextView) findViewById(R.id.date_textView);
		dayText = (TextView) findViewById(R.id.day_textView);
		setDayText(selectedDate);
		setWeekText(selectedDate);
		addTextToTopTextView(topText);

		backButton = (Button) findViewById(R.id.toolbar_nav_button2);
		updateButton = (Button) findViewById(R.id.toolbar_update_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.menu.showMenu();
			}
		});
		updateButton.setOnClickListener(new View.OnClickListener() {// ���°�ť����¼�
					@Override
					public void onClick(View v) {
						reloadData();
					}
				});
		courseListView = (ListView) findViewById(R.id.course_ListView);
		ClassCoverClass c2c = new ClassCoverClass();
		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		int day = cHelper.getDay(sf.format(selectedDate),
				kczlApplication.Person.getBegindate());
		int week = cHelper.getWeek(sf.format(selectedDate),
				kczlApplication.Person.getBegindate());

		list.addAll(c2c.curriculumsToCourseElements(
				kczlApplication.Curriculums, day, week));
		/*
		 * list = new ArrayList<CourseElement>(); list.add(new
		 * CourseElement("���ݽṹ", 0, "����", "��ѧ317", "15:40-17:20"));//��������ʾ���б���
		 * list.add(new CourseElement("���ݽṹ", 0, "����", "��ѧ317", "15:40-17:20"));
		 * list.add(new CourseElement("���ݽṹ", 0, "����", "��ѧ317", "15:40-17:20"));
		 * list.add(new CourseElement("���ݽṹ", 0, "����", "��ѧ317", "15:40-17:20"));
		 * list.add(new CourseElement("���ݽṹ", 0, "����", "��ѧ317", "15:40-17:20"));
		 */
		courseAdapter = new CourseAdapter(this, list, myHandler);
		courseListView.setAdapter(courseAdapter);
		TextView emptyView = (TextView) findViewById(R.id.emptyView);
		courseListView.setEmptyView(emptyView);
		popWindow = new PopWindow(getApplicationContext());
		courseListView.setOnItemClickListener(new MyOnItemClickListener(
				myHandler, popWindow));

		receiver = new ReturnToTodayReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CourseEveryMonthActivity.class.getName());
		registerReceiver(receiver, filter);
	}

	private class MyOnItemClickListener implements OnItemClickListener {
		private Handler handler;
		private PopWindow popWindow;

		public MyOnItemClickListener(Handler handler, PopWindow popWindow) {
			this.handler = handler;
			this.popWindow = popWindow;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View view,
				final int position, long arg3) {
			// TODO Auto-generated method stub
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(date);
			String selected = sdf.format(selectedDate);

			if (today.equals(selected)) {
				final CourseElement mCourseElement = list.get(position);

				View popupView = popWindow.getView();
				Button totalk = (Button) popupView.findViewById(R.id.talk);
				Button toevaluate = (Button) popupView
						.findViewById(R.id.evaluate);
				popWindow.showAsDropDown(view);
				final String starttime = list.get(position).getStarttime();
				totalk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Date date = new Date();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						int hour = calendar.get(Calendar.HOUR_OF_DAY);
						int minute = calendar.get(Calendar.MINUTE);

						Date start = null;
						SimpleDateFormat sdftmp = new SimpleDateFormat("hh:mm");
						try {
							start = sdftmp.parse(starttime);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						calendar.setTime(start);
						int start_hour = calendar.get(Calendar.HOUR_OF_DAY);
						int start_minute = calendar.get(Calendar.MINUTE);

						if ((hour > start_hour) || (hour ==start_hour&&minute >= start_minute)) {
							Message msg = new Message();
							msg.what = SPIT_ACTIVITY;
							Bundle bundle = new Bundle();
							bundle.putInt("position", position);
							bundle.putString("coursename",
									mCourseElement.getCourseName());
							bundle.putString("courseNature",
									mCourseElement.getCourseNature());
							bundle.putString("credit",
									mCourseElement.getCredit());
							bundle.putString("ctid", mCourseElement.getCtid());
							bundle.putString("endtime",
									mCourseElement.getEndtime());
							bundle.putString("starttime",
									mCourseElement.getStarttime());
							msg.setData(bundle);
							handler.sendMessage(msg);
							popWindow.dismiss();
						} else {
							Toast.makeText(getApplicationContext(),
									"���ڻ����ܷ���Ŷ~", Toast.LENGTH_SHORT).show();
						}
					}
				});
				toevaluate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Date date = new Date();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						int hour = calendar.get(Calendar.HOUR_OF_DAY);
						int minute = calendar.get(Calendar.MINUTE);

						Date start = null;
						SimpleDateFormat sdftmp = new SimpleDateFormat("hh:mm");
						try {
							start = sdftmp.parse(starttime);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						calendar.setTime(start);
						int start_hour = calendar.get(Calendar.HOUR_OF_DAY);
						int start_minute = calendar.get(Calendar.MINUTE);
						if ((hour > start_hour) || (hour ==start_hour&&minute >= start_minute)) {
							Message msg = new Message();
							msg.what = ADVICE_ACTIVITY;
							Bundle bundle = new Bundle();
							bundle.putInt("position", position);
							bundle.putString("coursename",
									mCourseElement.getCourseName());
							bundle.putString("courseNature",
									mCourseElement.getCourseNature());
							bundle.putString("credit",
									mCourseElement.getCredit());
							bundle.putString("ctid", mCourseElement.getCtid());
							bundle.putString("endtime",
									mCourseElement.getEndtime());
							bundle.putString("starttime",
									mCourseElement.getStarttime());
							msg.setData(bundle);
							handler.sendMessage(msg);
							popWindow.dismiss();
						} else {
							Toast.makeText(getApplicationContext(),
									"���ڻ����ܷ���Ŷ~", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			else
				Toast.makeText(CourseEveryMonthActivity.this, "ֻ�е���γ̲ſ��Է���~", Toast.LENGTH_SHORT).show();
		}
	}

	// ���gridview
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// ȡ����Ļ�Ŀ�Ⱥ͸߶�
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		// gridView.setColumnWidth(46);
		// if (Width == 480 && Height == 800) {
		gridView.setColumnWidth(Width / 7 + 1);
		// }
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // ȥ��gridView�߿�
		gridView.setOnTouchListener(new OnTouchListener() {
			// ��gridview�еĴ����¼��ش���gestureDetector

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return CourseEveryMonthActivity.this.gestureDetector
						.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {// gridView�е�ÿһ��item�ĵ���¼�
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position && position <= endPosition) {
					String scheduleDay = calV.getDateByClickItem(position)
							.split("\\.")[0];
					int scheduleYear = calV.getShowYear();
					int scheduleMonth = calV.getShowMonth();
					Calendar cal1 = Calendar.getInstance();
					cal1.set(scheduleYear, scheduleMonth - 1,
							Integer.parseInt(scheduleDay));
					Date date = cal1.getTime();
					setNewBg(selectedDate, date);
					selectedDate = date;
					setDayText(selectedDate);
					setWeekText(selectedDate);
					refreshAchievement();

				}
			}
		});
		gridView.setLayoutParams(params);
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,// �����¼�
			float velocityY) {
		int gvFlag = 0; // ÿ�����gridview��viewflipper��ʱ���ı��
		if (e1.getX() - e2.getX() > 50) {
			// ���󻬶�
			addGridView(); // ���һ��gridView
			jumpMonth++; // ��һ����

			calV = new CourseEveryMonthView(this, getResources(), jumpMonth,
					selectedDate);
			gridView.setAdapter(calV);
			// flipper.addView(gridView);
			addTextToTopTextView(topText);
			gvFlag++;
			flipper.addView(gridView, gvFlag);
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.flipper.showNext();
			flipper.removeViewAt(0);
			return true;
		} else if (e1.getX() - e2.getX() < -50) {
			// ���һ���
			addGridView(); // ���һ��gridView
			jumpMonth--; // ��һ����

			calV = new CourseEveryMonthView(this, getResources(), jumpMonth,
					selectedDate);
			gridView.setAdapter(calV);
			gvFlag++;
			addTextToTopTextView(topText);
			// flipper.addView(gridView);
			flipper.addView(gridView, gvFlag);

			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.flipper.showPrevious();
			flipper.removeViewAt(0);
			return true;
		}
		return false;
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	// ���ͷ������� ,�µ���Ϣ
	private void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		textDate.append(calV.getShowYear()).append(" ").append("��")
				.append(calV.getShowMonth()).append(" ").append("��");
		view.setText(textDate.toString());

	}

	private class ReturnToTodayReceiver extends BroadcastReceiver {// �ص�����

		@Override
		public void onReceive(Context context, Intent intent) {
			Date date = new Date();
			setDayText(date);
			setWeekText(date);
			refreshAchievement();
			if (jumpMonth == 0) {
				if (!AppUtils.areDatesSame(date, selectedDate)) {
					setNewBg(selectedDate, date);
					selectedDate = date;
				}
				return;
			}
			selectedDate = date;
			addGridView(); // ���һ��gridView
			int temp = jumpMonth;
			int gvFlag = 0;
			jumpMonth = 0; // ��һ����

			calV = new CourseEveryMonthView(CourseEveryMonthActivity.this,
					CourseEveryMonthActivity.this.getResources(), jumpMonth,
					selectedDate);
			gridView.setAdapter(calV);
			addTextToTopTextView(topText);
			flipper.addView(gridView, ++gvFlag);

			if (temp < 0) {
				CourseEveryMonthActivity.this.flipper
						.setInAnimation(AnimationUtils.loadAnimation(
								CourseEveryMonthActivity.this,
								R.anim.push_left_in));
				CourseEveryMonthActivity.this.flipper
						.setOutAnimation(AnimationUtils.loadAnimation(
								CourseEveryMonthActivity.this,
								R.anim.push_left_out));
				CourseEveryMonthActivity.this.flipper.showNext();
			} else {

				CourseEveryMonthActivity.this.flipper
						.setInAnimation(AnimationUtils.loadAnimation(
								CourseEveryMonthActivity.this,
								R.anim.push_right_in));
				CourseEveryMonthActivity.this.flipper
						.setOutAnimation(AnimationUtils.loadAnimation(
								CourseEveryMonthActivity.this,
								R.anim.push_right_out));
				CourseEveryMonthActivity.this.flipper.showPrevious();
			}
			CourseEveryMonthActivity.this.flipper.removeViewAt(0);
		}
	}

	private void setNewBg(Date oldDate, Date newDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(oldDate);
		if (cal.get(Calendar.MONTH) + 1 == calV.getShowMonth()) {
			int last = SpecialCalendar.getPositionInMonth(oldDate);
			TextView lastText = (TextView) ((gridView.getChildAt(last))
					.findViewById(R.id.tvtext));
			lastText.setBackgroundColor(Color.WHITE);
			lastText.setTextColor(Color.GRAY);
		}
		int current = SpecialCalendar.getPositionInMonth(newDate);
		TextView CurrentText = (TextView) ((gridView.getChildAt(current))
				.findViewById(R.id.tvtext));
		Drawable drawable = res.getDrawable(R.drawable.calendar_item_bg);
		CurrentText.setBackgroundDrawable(drawable);
		CurrentText.setTextColor(Color.BLACK);
	}

	private void setDayText(Date date) {
		if (AppUtils.areDatesSame(new Date(), date)) {
			dayText.setText(R.string.today);
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
		dayText.setText(sdf.format(date));
	}

	private void setWeekText(Date date) {
		weekText = (TextView) findViewById(R.id.week_textView);
		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (sf.parse(kczlApplication.Person.getBegindate()).getTime() > date
					.getTime()) {
				weekText.setText("");
			} else {
				int week = cHelper.getWeek(sf.format(date),
						kczlApplication.Person.getBegindate());
				weekText.setText("��" + week + "��");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}