package com.njut.view;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.njut.R;
import com.njut.data.CourseTeacherElement;

public class CurriculumAdapter extends BaseAdapter{

	public CurriculumAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	private List<CourseTeacherElement> list;
	private LayoutInflater inflater;
	private Context context;
	private Handler handler;

	public CurriculumAdapter(Context context, List<CourseTeacherElement> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
//		this.handler = handler;
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
			convertView = inflater.inflate(R.layout.curriculum_list_item, null);
		}

		TextView courseName = (TextView) convertView
				.findViewById(R.id.course_teacher_Textview);

		TextView natureclass = (TextView) convertView
				.findViewById(R.id.class_teacher_Textview);

		courseName.setText(list.get(position).getCoursename());

		natureclass.setText(list.get(position).getNatureclass());
		
		return convertView;

	}
}
