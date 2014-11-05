package com.njut.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njut.R;
import com.njut.data.TcardElement;
import com.njut.utility.CalendarHelper;
import com.njut.utility.DisplayUtil;

public class TcardAdapter extends BaseAdapter{

	private int fullwidth = -1;

	public TcardAdapter(List<TcardElement> list,
			Context context) {
		super();
		this.list = list;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	private List<TcardElement> list;
	private LayoutInflater inflater;
	private Context context;
	private int[] ratingbarimages = { R.drawable.stream_star_1,
			R.drawable.stream_star_2, R.drawable.stream_star_3,
			R.drawable.stream_star_4, R.drawable.stream_star_5 };

	private String[] days = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };

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

	public int Getmax(TcardElement tcardelement){
		int max = tcardelement.getFivestar();
		if(tcardelement.getFourstar()>max)
			max = tcardelement.getFourstar();
		if(tcardelement.getThreestar()>max)
			max = tcardelement.getThreestar();
		if(tcardelement.getTwostar()>max)
			max = tcardelement.getTwostar();
		if(tcardelement.getOnestar()>max)
			max = tcardelement.getOnestar();
		return max;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TcardElement tcardElement = list.get(position);
		ViewHolder holder;
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.teacher_push_card, null);
			holder = new ViewHolder();
			holder.course_digittime_tv = (TextView) convertView.findViewById(R.id.course_digittime_tv);
			holder.tcard_time_tv = (TextView) convertView.findViewById(R.id.coursetime_tv);
			holder.total_tv = (TextView) convertView.findViewById(R.id.total);
			holder.onestar_tv = (TextView) convertView.findViewById(R.id.onestar_tv);
			holder.twostar_tv = (TextView) convertView.findViewById(R.id.twostar_tv);
			holder.threestar_tv = (TextView) convertView.findViewById(R.id.threestar_tv);
			holder.fourstar_tv = (TextView) convertView.findViewById(R.id.fourstar_tv);
			holder.fivestar_tv = (TextView) convertView.findViewById(R.id.fivestar_tv);
			holder.onestarLL = (LinearLayout) convertView.findViewById(R.id.onestar);
			holder.twostarLL = (LinearLayout) convertView.findViewById(R.id.twostar);
			holder.threestarLL = (LinearLayout) convertView.findViewById(R.id.threestar);
			holder.fourstarLL = (LinearLayout) convertView.findViewById(R.id.fourstar);
			holder.fivestarLL = (LinearLayout) convertView.findViewById(R.id.fivestar);
			holder.average_tv = (TextView) convertView.findViewById(R.id.tcard_average);
			holder.ratingbar_iv = (ImageView) convertView.findViewById(R.id.ratingbar_iv);
			holder.participate = (TextView) convertView.findViewById(R.id.participate);
			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.onestar_tv.setText(String.valueOf(tcardElement.getOnestar()));
		holder.twostar_tv.setText(String.valueOf(tcardElement.getTwostar()));
		holder.threestar_tv.setText(String.valueOf(tcardElement.getThreestar()));
		holder.fourstar_tv.setText(String.valueOf(tcardElement.getFourstar()));
		holder.fivestar_tv.setText(String.valueOf(tcardElement.getFivestar()));
		holder.total_tv.setText(String.valueOf(tcardElement.getTotal()));

		double sum = (tcardElement.getOnestar()+tcardElement.getTwostar()*2+tcardElement.getThreestar()*3+tcardElement.getFourstar()*4+tcardElement.getFivestar()*5) / (double)tcardElement.getNum();
		holder.average_tv.setText(String.format("%.1f",sum));
		holder.participate.setText(String.valueOf(tcardElement.getNum()));
		if(tcardElement.getLearningEffect() <= 0||tcardElement.getLearningEffect() >5)
			holder.ratingbar_iv.setVisibility(View.INVISIBLE);
		else
			holder.ratingbar_iv.setBackgroundResource(ratingbarimages[tcardElement.getLearningEffect()-1]);

		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.AT_MOST); 
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.AT_MOST);	
		holder.fivestarLL.measure(w, h);
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int other = DisplayUtil.dip2px(context, 194);
		fullwidth = dm.widthPixels - other;
		//int max = Getmax(tcardElement);
		int width;

		width = (int)((tcardElement.getOnestar()/(double)tcardElement.getNum())*fullwidth);
		RelativeLayout.LayoutParams layoutParams1= (RelativeLayout.LayoutParams) holder.onestarLL.getLayoutParams();
		layoutParams1.width = width;
		holder.onestarLL.setLayoutParams(layoutParams1);
		width = (int)((tcardElement.getTwostar()/(double)tcardElement.getNum())*fullwidth);
		RelativeLayout.LayoutParams layoutParams2= (RelativeLayout.LayoutParams) holder.twostarLL.getLayoutParams();
		layoutParams2.width = width;
		holder.twostarLL.setLayoutParams(layoutParams2);
		width = (int)((tcardElement.getThreestar()/(double)tcardElement.getNum())*fullwidth);
		RelativeLayout.LayoutParams layoutParams3= (RelativeLayout.LayoutParams) holder.threestarLL.getLayoutParams();
		layoutParams3.width = width;
		holder.threestarLL.setLayoutParams(layoutParams3);
		width = (int)((tcardElement.getFourstar()/(double)tcardElement.getNum())*fullwidth);
		RelativeLayout.LayoutParams layoutParams4= (RelativeLayout.LayoutParams) holder.fourstarLL.getLayoutParams();
		layoutParams4.width = width;
		holder.fourstarLL.setLayoutParams(layoutParams4);
		width = (int)((tcardElement.getFivestar()/(double)tcardElement.getNum())*fullwidth);
		RelativeLayout.LayoutParams layoutParams5= (RelativeLayout.LayoutParams) holder.fivestarLL.getLayoutParams();
		layoutParams5.width = width;
		holder.fivestarLL.setLayoutParams(layoutParams5);
		holder.course_digittime_tv.setText(tcardElement.getDatestamp());
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sf.parse(tcardElement.getDatestamp());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd");
		int day = cHelper.getDay(sf3.format(date), context.getResources()
				.getString(R.string.begindate));
		int week = cHelper.getWeek(sf3.format(date), context.getResources()
				.getString(R.string.begindate));
		if (day > 0 && week > 0)
			holder.tcard_time_tv.setText("第" + week+"周 " + days[day - 1]);

		return convertView;
	}

	private static class ViewHolder
	{
		TextView course_digittime_tv;
		TextView tcard_time_tv;
		//		RatingBar ratingbarGreen;
		ImageView ratingbar_iv;
		TextView total_tv;
		TextView onestar_tv;
		TextView twostar_tv;
		TextView threestar_tv;
		TextView fourstar_tv;
		TextView fivestar_tv;
		TextView average_tv;
		LinearLayout onestarLL;
		LinearLayout twostarLL;
		LinearLayout threestarLL;
		LinearLayout fourstarLL;
		LinearLayout fivestarLL;
		TextView participate;		
	}
}
