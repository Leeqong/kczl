package com.njut.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njut.R;
import com.njut.activity.CourseEveryWeekActivity;
import com.njut.utility.AppUtils;
import com.njut.utility.DisplayUtil;

/**
 * ����gridview�е�ÿһ��item��ʾ��textview
 * 
 * @author jack_peng
 * 
 */
public class CourseEveryWeekView extends BaseAdapter {

	private Context context;
	private Resources res;
	private Date[] datesOfWeek = new Date[7];
	private Date selectedDate;
	private int mCount;
	private float gridWidth;
	
	public CourseEveryWeekView(Context context, Resources rs, int jumpWeek,
			Date firstDateOfWeek, Date selectedDate) {
		this.selectedDate = selectedDate;
		for (int i = 0; i < datesOfWeek.length; i++) {
			GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
			gc.setTime(firstDateOfWeek);
			gc.add(Calendar.DATE, jumpWeek * 7 + i);
			datesOfWeek[i] = gc.getTime();
		}
		this.context = context;
		this.res = rs;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return datesOfWeek.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.calendar_item1, null);
		}
		if (position == 0) {
			mCount++;
		} else {
			mCount = 0;
		}

		if (mCount > 1) {
			return convertView;
		}

		float scale = context.getResources().getDisplayMetrics().density;
		//screenWidth = (int)(dm.widthPixels * density + 0.5f); // ��Ļ��px���磺480px��
		int screenWidth480 = (int) (320 * scale + 0.5f);
		DisplayMetrics dm = new DisplayMetrics();

		// ȡ�ô�������
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(
				dm);
		// ���ڵĿ��
		int screenWidth = dm.widthPixels;
		if (screenWidth < screenWidth480) {
			gridWidth = screenWidth / 7.0f;
		} else {
			gridWidth = screenWidth480 / 7.0f;
		}
		int gridHeightDip = DisplayUtil.dip2px(context, 40);
		RelativeLayout temp = (RelativeLayout)convertView;
		GridView.LayoutParams  params= new GridView.LayoutParams((int) (gridWidth+1), gridHeightDip);
		temp.setLayoutParams(params);//���ﵥλ��ʲô��dip?
		
		
		
		Log.v(this.getClass().getName(), Integer.toString(position)+""+selectedDate);
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		SimpleDateFormat fymd = new SimpleDateFormat("d");
		String d = fymd.format(datesOfWeek[position]);
		SpannableString sp = new SpannableString(d);

//		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
//				d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.6f), 0, d.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		textView.setText(sp);
		textView.setTextColor(Color.GRAY);
		if (AppUtils.areDatesSame(datesOfWeek[position], selectedDate)) {
			// textView.setBackgroundResource(R.drawable.calendar_list_overlay);
			textView.setTextColor(Color.BLACK);
			if (context instanceof CourseEveryWeekActivity) {
				((CourseEveryWeekActivity) context).darwOverlay(position);
			}

		}
		return convertView;
	}

	/**
	 * ���ÿһ��itemʱ����item�е�����
	 * 
	 * @param position
	 * @return
	 */
	public Date getDateByClickItem(int position) {
		return datesOfWeek[position];
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}

	public boolean contain(Date date) {
		for (int i = 0; i < datesOfWeek.length; i++) {
			if (AppUtils.areDatesSame(date, datesOfWeek[i]))
				return true;
		}
		return false;
	}
	public String getTopString() {
		SimpleDateFormat fymd = new SimpleDateFormat("yyyy ��M ��");
		return fymd.format(datesOfWeek[0]);
	}
}
