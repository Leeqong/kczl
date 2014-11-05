package com.njut.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.nutlab.kczl.kczlApplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.njut.R;
import com.njut.data.CourseElement;
import com.njut.utility.CalendarHelper;
import com.njut.utility.ClassCoverClass;
import com.njut.view.ChooseCurrAdapter;

public class ChooseCurrDialog{

	private Context mContext;
	private Dialog dialog;
	private List<CourseElement> list;
	private ListView course_lv;
	private String[] titles = {"小结","感受"};
	private int[] title_images = {R.drawable.xiaojie,R.drawable.ganshou};
	private int currIndex;

	private int[] colors = { R.color.light_green, R.color.dark_green,
			R.color.dark_blue, R.color.light_blue, R.color.light_orange,
			R.color.dark_orange, R.color.light_pink, R.color.dark_pink };


	public ChooseCurrDialog(final Context context,int index) {
		this.mContext = context;
		dialog = new Dialog(context,R.style.dialog);
		dialog.setContentView(R.layout.post);
		this.currIndex = index;
		TextView title = (TextView) dialog.getWindow().findViewById(R.id.title);
        title.setText(titles[currIndex]);
        ImageView title_iv = (ImageView) dialog.getWindow().findViewById(R.id.title_iv);
        title_iv.setImageResource(title_images[currIndex]);
		course_lv = (ListView) dialog.getWindow().findViewById(R.id.course_ListView);
		ClassCoverClass c2c = new ClassCoverClass();
		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		int day = cHelper.getDay(sf.format(date),
				kczlApplication.Person.getBegindate());
		int week = cHelper.getWeek(sf.format(date),
				kczlApplication.Person.getBegindate());
		list = c2c.curriculumsToCourseElements(kczlApplication.Curriculums,
				day, week);
		TextView emptyView = (TextView) dialog.getWindow().findViewById(R.id.emptyView);

		course_lv.setAdapter(new ChooseCurrAdapter(mContext, list));
		course_lv.setEmptyView(emptyView);
		course_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CourseElement mCourseElement = list.get(position);
				String starttime = list.get(position).getStarttime();
				if(currIndex == 0)
				{
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
						Intent intent = new Intent(context,
								AdviceActivity.class);
						intent.putExtras(bundle);
						((Activity) context).startActivityForResult(intent, 0);
						((Activity) context).overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						dismiss();
					} else {
						Toast.makeText(context, "现在还不能反馈哦~",
								Toast.LENGTH_SHORT).show();
					}
				}
				if(currIndex == 1)
				{
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
						Intent intent = new Intent(context,
								SpitActivity.class);
						intent.putExtras(bundle);
						((Activity) context).startActivityForResult(intent, 0);
						((Activity) context).overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						dismiss();
					} else {
						Toast.makeText(context, "现在还不能反馈哦~",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
			
		});


		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		//获得当前窗体
		Window dialogWindow = dialog.getWindow();

		//重新设置
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);  
		dialogWindow.setGravity(Gravity.TOP);
		LinearLayout outside = (LinearLayout) dialog.getWindow().findViewById(R.id.outside);
		outside.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				
			}
		});
		LinearLayout inner = (LinearLayout) dialog.getWindow().findViewById(R.id.inner);
		inner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public void show()
	{
		dialog.show();
	}

	public void dismiss()
	{
		dialog.dismiss();
	}
    
}
