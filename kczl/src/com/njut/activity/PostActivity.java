package com.njut.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.nutlab.kczl.kczlApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.njut.R;
import com.njut.data.CourseElement;
import com.njut.utility.CalendarHelper;
import com.njut.utility.ClassCoverClass;
import com.njut.view.ChooseCurrAdapter;
import com.njut.view.CourseAdapter;

public class PostActivity extends Activity {

	private ChooseCurrAdapter adapter;
	private Date selectedDate;
	private ListView courseListView;
	private List<CourseElement> list;
	private PopWindow popWindow;

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {

		}
	};

	public void onBackPressed() {
		finish();
		overridePendingTransition(0, 0);
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.post);
		courseListView = (ListView) findViewById(R.id.course_ListView);
		ClassCoverClass c2c = new ClassCoverClass();
		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		selectedDate = date;
		int day = cHelper.getDay(sf.format(selectedDate),
				kczlApplication.Person.getBegindate());
		int week = cHelper.getWeek(sf.format(selectedDate),
				kczlApplication.Person.getBegindate());
		list = c2c.curriculumsToCourseElements(kczlApplication.Curriculums,
				day, week);
		adapter = new ChooseCurrAdapter(this, list, myHandler);

		TextView emptyView = (TextView) findViewById(R.id.emptyView);
		courseListView.setAdapter(adapter);
		courseListView.setEmptyView(emptyView);
		popWindow = new PopWindow(getApplicationContext());
		courseListView.setOnItemClickListener(new MyOnItemClickListener(
				myHandler, popWindow));
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
			final CourseElement mCourseElement = list.get(position);
			View popupView = popWindow.getView();
			Button totalk = (Button) popupView.findViewById(R.id.talk);
			Button toevaluate = (Button) popupView.findViewById(R.id.evaluate);

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
						Bundle bundle = new Bundle();
						bundle.putInt("position", position);
						bundle.putString("coursename",
								mCourseElement.getCourseName());
						bundle.putString("courseNature",
								mCourseElement.getCourseNature());
						bundle.putString("credit", mCourseElement.getCredit());
						bundle.putString("ctid", mCourseElement.getCtid());
						bundle.putString("endtime", mCourseElement.getEndtime());
						bundle.putString("starttime",
								mCourseElement.getStarttime());
						Intent intent = new Intent(PostActivity.this,
								SpitActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 0);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						popWindow.dismiss();
					} else {
						Toast.makeText(getApplicationContext(), "现在还不能反馈哦~",
								Toast.LENGTH_SHORT).show();
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

					if ((hour > start_hour) || (hour ==start_hour&&minute >= start_minute)){
						Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putInt("position", position);
						bundle.putString("coursename",
								mCourseElement.getCourseName());
						bundle.putString("courseNature",
								mCourseElement.getCourseNature());
						bundle.putString("credit", mCourseElement.getCredit());
						bundle.putString("ctid", mCourseElement.getCtid());
						bundle.putString("endtime", mCourseElement.getEndtime());
						bundle.putString("starttime",
								mCourseElement.getStarttime());
						msg.setData(bundle);
						handler.sendMessage(msg);
						popWindow.dismiss();
						Intent intent = new Intent(PostActivity.this,
								AdviceActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 0);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
					} else {
						Toast.makeText(getApplicationContext(), "现在还不能反馈哦~",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			popWindow.showAsDropDown(view);
		}
	}
}
