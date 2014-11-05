package com.njut.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.njut.R;
import com.njut.data.CourseElement;

public class ChooseCurrAdapter extends BaseAdapter{
	private List<CourseElement> list;
	private LayoutInflater inflater;
	private Context context;
	private Handler handler;

	private int[] colors = { R.color.light_green, R.color.dark_green,
			R.color.dark_blue, R.color.light_blue, R.color.light_orange,
			R.color.dark_orange, R.color.light_pink, R.color.dark_pink };

	private static final int imageId[] = { R.drawable.blue_circle,
		R.drawable.green_circle, R.drawable.red_circle };
	public ChooseCurrAdapter(Context context, List<CourseElement> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}
	public ChooseCurrAdapter(Context context, List<CourseElement> list,
			Handler handler) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		this.handler = handler;
	}


	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.choose_course_list_item, null);
		}

		LinearLayout banner = (LinearLayout) convertView.findViewById(R.id.banner);

		String starttime = list.get(position).getStarttime();
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
			banner.setBackgroundColor(context.getResources().getColor(R.color.light_green));
		}else{
			banner.setBackgroundColor(context.getResources().getColor(R.color.red));
		}

//		banner.setBackgroundColor(context.getResources().getColor(colors[position%8]));
		TextView courseName = (TextView) convertView
				.findViewById(R.id.course_name_TextView);

		TextView teacherName = (TextView) convertView
				.findViewById(R.id.teacher_name_Textview);

		TextView classroomName = (TextView) convertView
				.findViewById(R.id.classroom_name_Textview);

		TextView time = (TextView) convertView
				.findViewById(R.id.course_time_Textview);
		courseName.setText(list.get(position).getCourseName());

		teacherName.setText(list.get(position).getTeacherName());

		classroomName.setText(list.get(position).getClassroomName());

		time.setText(list.get(position).getTime());

		return convertView;

	}

}
