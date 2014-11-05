package com.njut.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.nutlab.kczl.kczlApplication;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.njut.R;
import com.njut.data.CourseElement;
import com.njut.utility.CalendarHelper;
import com.njut.utility.ClassCoverClass;

public class ChooseCurrPopupWindow {

	private Context context;
	private ListView course_lv;
	private final PopupWindow popupWindow;
	private List<CourseElement> list;
	private int[] colors = { R.color.light_green, R.color.dark_green,
			R.color.dark_blue, R.color.light_blue, R.color.light_orange,
			R.color.dark_orange, R.color.light_pink, R.color.dark_pink };

	public ChooseCurrPopupWindow(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(
				R.layout.choose_curr_popup, null);
		course_lv = (ListView) root.findViewById(R.id.curr_list);
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
		course_lv.setAdapter(new MyAdapter());

		popupWindow = new PopupWindow(root, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null)
				convertView = LayoutInflater.from(context).inflate(
						R.layout.choose_popup_item, null);
			LinearLayout banner = (LinearLayout) convertView
					.findViewById(R.id.banner);
			banner.setBackgroundColor(context.getResources().getColor(colors[position%8]));
			TextView course_name_TextView = (TextView) convertView
					.findViewById(R.id.course_name_TextView);
			course_name_TextView.setText(list.get(position).getCourseName());
			TextView course_time_Textview = (TextView) convertView
					.findViewById(R.id.course_time_Textview);
			course_time_Textview.setText(list.get(position).getTime());
			return convertView;
		}

	}
	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent, 0, 18);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}


	// 隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}
}
