package com.njut.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.changeLikenumService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njut.R;
import com.njut.activity.CommentActivity;
import com.njut.activity.CommentAdviceActivity;
import com.njut.data.AdviceElement;
import com.njut.data.Curriculum;
import com.njut.data.SpitElement;
import com.njut.utility.AppUtils;
import com.njut.utility.CalendarHelper;
import com.njut.view.SpitAdapter.ChangeLikenumThread;

public class AdviceAdapter extends BaseAdapter {
	private List<AdviceElement> list;
	private LayoutInflater inflater;
	private Context context;
	private Handler handler;
	private final int LIKENUM_CHANGE_FINISHED = 2;
	private final int NO_NETWORK = 4;
	private String result;// 发送请求后返回的结果 +1 -1
	private int[] ratingbarimages = { R.drawable.stream_star_1,
			R.drawable.stream_star_2, R.drawable.stream_star_3,
			R.drawable.stream_star_4, R.drawable.stream_star_5 };
	private String[] learning_effect_array;
	private int[] attendanceid = { R.string.attendance_value1,
			R.string.attendance_value2, R.string.attendance_value3 };
	private int[] speedid = { R.string.teaching_schedule_value1,
			R.string.teaching_schedule_value2,
			R.string.teaching_schedule_value3 };
	private String Like;
	private String Dislike;
	private final int OPEN_ADVICE_COMMENT = 4321;

	private String[] days = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };

	public AdviceAdapter(Context context, List<AdviceElement> list,
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

	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.advice_item, null);
		}
		Like = context.getResources().getString(R.string.Like);
		Dislike = context.getResources().getString(R.string.Dislike);
		AdviceElement adviceElement = list.get(position);
		TextView courseName = (TextView) convertView
				.findViewById(R.id.coursename_TextView);
		TextView teacherName = (TextView) convertView
				.findViewById(R.id.teacher_TextView);
		TextView courseTime = (TextView) convertView
				.findViewById(R.id.coursetime_TextView);
		TextView adviceTime = (TextView) convertView
				.findViewById(R.id.advicetime);
		TextView content = (TextView) convertView
				.findViewById(R.id.content_TextView);
		TextView status = (TextView) convertView.findViewById(R.id.status);
		LinearLayout ratingLayout = (LinearLayout) convertView
				.findViewById(R.id.ratingLayout);
		if (adviceElement.getArrangement().equals("3")
				|| adviceElement.getArrangement().equals("5")) {
			if (adviceElement.getArrangement().equals("3"))
				status.setText("停课");
			if (adviceElement.getArrangement().equals("5"))
				status.setText("调课");
			status.setVisibility(View.VISIBLE);
			//ratingLayout.setVisibility(View.GONE);
			content.setVisibility(View.GONE);
		}
		if (adviceElement.getArrangement().equals("1")) {
			status.setVisibility(View.GONE);
			//ratingLayout.setVisibility(View.VISIBLE);
			content.setVisibility(View.VISIBLE);
			TextView effectTextView = (TextView) convertView
					.findViewById(R.id.learning_effect_textView);
			ImageView likeeffectimage = (ImageView) convertView
					.findViewById(R.id.rating_bar);
			int effect = list.get(position).getEffect();
			if (effect == 0)
				effect = 1;// 其实正常上课提交时候确保effect != 0.但是之前提交了一些错误的上去
			likeeffectimage.setImageResource(ratingbarimages[effect - 1]);
			Resources res = context.getResources();
			learning_effect_array = res
					.getStringArray(R.array.learning_effect_array);
			TextView learning_effect_textView = (TextView) convertView
					.findViewById(R.id.learning_effect_textView);
			learning_effect_textView.setText(learning_effect_array[effect - 1]);
		}

		TextView commentcount = (TextView) convertView
				.findViewById(R.id.comment_TextView);
		courseName.setText(adviceElement.getName());

		commentcount.setText("评论(" + adviceElement.getCommmentlist().size()+")");

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sf.parse(adviceElement.getDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sf1 = new SimpleDateFormat("M月dd日 HH:mm");
		adviceTime.setText(sf1.format(date));
		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd");
		int day = cHelper.getDay(sf3.format(date), context.getResources()
				.getString(R.string.begindate));
		int week = cHelper.getWeek(sf3.format(date), context.getResources()
				.getString(R.string.begindate));
		if (day > 0 && week > 0)
		    courseTime.setText("第" + week+"周 " + days[day - 1]);
		else
			courseTime.setText("获取失败");

		content.setText(adviceElement.getContent());

		if (kczlApplication.IsStudent == 1) {
			for (Curriculum curriculum : kczlApplication.Curriculums) {
				if (curriculum.getCtid().equals(adviceElement.getCtid())) {
					teacherName.setText(curriculum.getTeacher());
					break;
				} else
					teacherName.setText("老师姓名获取失败");
			}
		} else if (kczlApplication.IsStudent == 2) {
			teacherName.setText(kczlApplication.PersonTeacher.getTeachername());
		}

		final String adviceid = list.get(position).getCaid();
		final String schoolnumberTo = list.get(position).getSchoolnumber();

		RelativeLayout comment = (RelativeLayout) convertView
				.findViewById(R.id.advice_comment_layout);
		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, CommentAdviceActivity.class);
				Bundle bundle = new Bundle();

				bundle.putSerializable("adviceElement", list.get(position));
				bundle.putInt("position", position);
				intent.putExtras(bundle);
				((Activity)context).startActivityForResult(intent, OPEN_ADVICE_COMMENT);
				((Activity) context).overridePendingTransition(
						R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		return convertView;

	}
}
