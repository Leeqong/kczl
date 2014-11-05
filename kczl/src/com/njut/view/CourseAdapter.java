package com.njut.view;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.njut.R;
import com.njut.activity.PopMenu;
import com.njut.data.CourseElement;

public class CourseAdapter extends BaseAdapter {
	private List<CourseElement> list;
	private LayoutInflater inflater;
	private Context context;
	private Handler handler;

	private static final int imageId[] = { R.drawable.blue_circle,
			R.drawable.green_circle, R.drawable.red_circle };

	public CourseAdapter(Context context, List<CourseElement> list,
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
			convertView = inflater.inflate(R.layout.course_list_item, null);
		}
		final View tempview = convertView;
		TextView courseName = (TextView) convertView
				.findViewById(R.id.course_name_TextView);

		TextView teacherName = (TextView) convertView
				.findViewById(R.id.teacher_name_Textview);

		TextView classroomName = (TextView) convertView
				.findViewById(R.id.classroom_name_Textview);

		TextView time = (TextView) convertView
				.findViewById(R.id.course_time_Textview);
		ImageView state = (ImageView) convertView
				.findViewById(R.id.state_Imageview);
		courseName.setText(list.get(position).getCourseName());

		teacherName.setText(list.get(position).getTeacherName());

		classroomName.setText(list.get(position).getClassroomName());

		time.setText(list.get(position).getTime());
		// state.setImageResource(imageId[list.get(position).getState()]);
		state.setImageResource(R.drawable.green_circle);
		
		return convertView;

	}

}
