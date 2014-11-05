package com.njut.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.changeSpitLikenumService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njut.R;
import com.njut.activity.CommentActivity;
import com.njut.data.Curriculum;
import com.njut.data.SpitElement;
import com.njut.utility.AppUtils;
import com.njut.utility.CalendarHelper;

public class SpitAdapter extends BaseAdapter {
	private List<SpitElement> list;
	private LayoutInflater inflater;
	private Context context;
	private Handler handler;
	private final int LIKENUM_ADD_FINISHED = 2;
	private final int LIKENUM_MINUS_FINISHED = 7;
	private final int LIKENUM_CHANGE_FAILED = 8;
	private final int NO_NETWORK = 4;
	private final int LIKENUN_ADD_BEGIN = 9;
	private final int LIKENUN_MINUS_BEGIN = 10;
	private String result;
	private String Like;
	private String Dislike;
	private String[] days = {"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
	private final int OPEN_SPIT_COMMENT = 1234;
	private int[] faces = {R.drawable.face_1,R.drawable.face_2,R.drawable.face_3,R.drawable.face_4,R.drawable.face_5};


	public SpitAdapter(Context context, List<SpitElement> list, Handler handler ) {
		this.list = list;
		this.inflater = LayoutInflater.from(context);
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// int type = getItemViewType(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.spit_item, null);
		}
		Like = context.getResources().getString(R.string.Like);
		Dislike = context.getResources().getString(R.string.Dislike);
		ImageView thumb = (ImageView) convertView.findViewById(R.id.thumb);
		
		TextView month_tv = (TextView) convertView.findViewById(R.id.month_tv);
		TextView day_tv = (TextView) convertView.findViewById(R.id.day_tv);
		
		
		TextView courseName = (TextView) convertView
				.findViewById(R.id.coursename_TextView);
		TextView content = (TextView) convertView
				.findViewById(R.id.content_TextView);
		TextView teacher = (TextView) convertView
				.findViewById(R.id.teacher_TextView);
		TextView commentcount = (TextView) convertView
				.findViewById(R.id.comment_TextView);
		TextView courseTime = (TextView) convertView
				.findViewById(R.id.coursetime_TextView);
		TextView time = (TextView) convertView.findViewById(R.id.spittime);
		final TextView thumb_like = (TextView) convertView
				.findViewById(R.id.thumb_TextView);
		SpitElement spitElement = list.get(position);
		courseName.setText(spitElement.getCoursename());
		commentcount.setText("评论 (" + spitElement.getCommmentlist().size()
				+ ")");
		if (list.get(position).getEverlike().equals("true"))
			thumb.setImageResource(R.drawable.thumb_pressed);
		else
			thumb.setImageResource(R.drawable.thumb);

		if (kczlApplication.IsStudent == 1) {
			for (Curriculum curriculum : kczlApplication.Curriculums) {
				if (curriculum.getCtid().equals(spitElement.getCtid())) {
					teacher.setText(curriculum.getTeacher());
					break;
				} else
					teacher.setText("老师姓名获取失败");
			}
		} else if (kczlApplication.IsStudent == 2) {
			teacher.setText(kczlApplication.PersonTeacher.getTeachername());
		}

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sf.parse(spitElement.getTimestamp());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sf1 = new SimpleDateFormat("MM-dd HH:mm");
		time.setText(sf1.format(date));
		month_tv.setText(new SimpleDateFormat("M月").format(date));
		day_tv.setText(new SimpleDateFormat("d日").format(date));

		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd");
		int day = cHelper.getDay(sf3.format(date),
				context.getResources().getString(R.string.begindate));
		int week = cHelper.getWeek(sf3.format(date),
				context.getResources().getString(R.string.begindate));
		if (day > 0 && week > 0)
			courseTime.setText("第" + week+"周 " + days[day - 1]);
		else
			courseTime.setText("获取失败");

		content.setText(spitElement.getContent());

		final String spitid = list.get(position).getSpitid();
		final String schoolnumberTo = list.get(position).getSchoolnumber();
		final ImageView faceImage = (ImageView) convertView
				.findViewById(R.id.face);
		final int likenum = list.get(position).getLikenum();
		thumb_like.setText("(" + likenum + ")");
		faceImage.setImageResource(faces[position%5]);

		final RelativeLayout changlikennum = (RelativeLayout) convertView
				.findViewById(R.id.spit_like_layout);
		if(kczlApplication.IsStudent ==2)
			changlikennum.setVisibility(View.GONE);
		changlikennum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				if (!AppUtils.isOpenNetwork(context))
					handler.sendEmptyMessage(NO_NETWORK);
				else {
					Message message = new Message();

					String tag = "";
					if (list.get(position).getEverlike().equals("true")) {
						tag = "0";
						message.what = LIKENUN_MINUS_BEGIN;
					} else {
						tag = "1";
						message.what = LIKENUN_ADD_BEGIN;
					}	
					handler.sendMessage(message);
					ChangeLikenumThread CLT = new ChangeLikenumThread(spitid,
							handler, position, tag, schoolnumberTo);
					CLT.start();
				}
			}
		});
		RelativeLayout comment = (RelativeLayout) convertView
				.findViewById(R.id.spit_comment_layout);
		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, CommentActivity.class);
				Bundle bundle = new Bundle();

				bundle.putSerializable("spitElement", list.get(position));
				bundle.putInt("position", position);
				intent.putExtras(bundle);
				((Activity) context).startActivityForResult(intent, OPEN_SPIT_COMMENT);
				((Activity) context).overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left ); 
			}
		});
		return convertView;
	}

	class ChangeLikenumThread extends Thread {
		public ChangeLikenumThread(String spitid, Handler handler,
				int position, String tag, String schoolnumberTo) {
			this.spitid = spitid;
			this.handler = handler;
			this.position = position;
			this.tag = tag;
			this.schoolnumberTo = schoolnumberTo;
		}

		String spitid;
		Handler handler;
		int position;
		String tag;
		String schoolnumberTo;

		public void run() {

			changeSpitLikenumService clns = new changeSpitLikenumService();

			String msg;
			String schoolnumberFrom = kczlApplication.Person.getSchoolnumber();
			msg = clns.upload(spitid, schoolnumberFrom, schoolnumberTo, tag);// infoelement还要增加成员变量edid(每条评教的标识id)
			// ctid(发送此条评教用户的学号)
			// from to都是发送评教人的学号 tag ChangeLikenumThread加个成员，初始化的传进来
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = context.getSharedPreferences(
					PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("IsLogined", kczlApplication.IsLogined);
			editor.commit();
			try {
				result = finishChangeLikenumOperation(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message message = new Message();
			message.what = LIKENUM_CHANGE_FAILED;
			message.getData().putInt("position", position);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(msg);
				if (jsonObject.getString("msg").equals("+1"))
				{
					message.what = LIKENUM_ADD_FINISHED;
				}
				if (jsonObject.getString("msg").equals("-1"))
				{
					message.what = LIKENUM_MINUS_FINISHED;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			handler.sendMessage(message);
		}
	}

	private String finishChangeLikenumOperation(String mStringReturnStr)// 不要写在UI线程上
			throws JSONException {
		JSONObject jsonObjs = new JSONObject(mStringReturnStr);
		String result = jsonObjs.getString("msg");
		return result;
	}

}
