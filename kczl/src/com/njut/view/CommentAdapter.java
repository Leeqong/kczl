package com.njut.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.submitAdviceCommentService;
import org.nutlab.webService.submitCommentService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.njut.R;
import com.njut.data.CommentElement;
import com.njut.utility.AppUtils;

public class CommentAdapter extends BaseAdapter {

	private List<CommentElement> commentlist;
	private LayoutInflater inflater;
	private Context context;
	private String edid;

	private Handler myHandler;
	private final int NO_NETWORK = 0;
	private final int CANCEL_COMMENT_FINISHED = 2;
	private final int CANCEL_COMMENT_BEGIN = 13;
	private final int CANCEL_COMMENT_FAILED = 14;
	private String number;
	private String author_num;

	public CommentAdapter(List<CommentElement> commentlist, Context context,
			String edid, String author_num,Handler myHanlder) {
		super();
		this.commentlist = commentlist;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.edid = edid;
		this.myHandler = myHanlder;
		this.author_num = author_num;//发布这条感受的作者的学号
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commentlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return commentlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comment_item, null);
		}
		TextView name = (TextView) convertView.findViewById(R.id.fromname_tv);
		TextView time = (TextView) convertView.findViewById(R.id.time);
		TextView content = (TextView) convertView.findViewById(R.id.content);
		TextView to_tv = (TextView) convertView.findViewById(R.id.to_tv);
		TextView toname_tv = (TextView) convertView.findViewById(R.id.toname_tv);
		Button deletebtn = (Button) convertView
				.findViewById(R.id.delete_comment);
		final CommentElement commentElement = commentlist.get(position);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sf.parse(commentElement.getTimestap());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sf1 = new SimpleDateFormat("M月dd日 HH:mm");
		time.setText(sf1.format(date));
		if (commentElement.getContent().startsWith("[T]")) {
			content.setText(commentElement.getContent().substring(3));
			name.setText("任课教师");
		}
		else if(commentElement.getContent().startsWith("[S]"))
		{
			content.setText(commentElement.getContent().substring(3));
			name.setText("匿名用户");
		}

		else
			content.setText(commentElement.getContent());
		String toname="";
		if (kczlApplication.IsStudent == 1)
		{
			number = kczlApplication.Person.getSchoolnumber();

		}
		else if (kczlApplication.IsStudent == 2)
		{
			number = kczlApplication.PersonTeacher.getTeachernumber();

		}
		if (commentElement.getFromuser().equals(number))
			deletebtn.setVisibility(View.VISIBLE);
		else
			deletebtn.setVisibility(View.GONE);

		if (commentElement.getTouser().equals(author_num))
		{
			to_tv.setVisibility(View.GONE);
			toname_tv.setVisibility(View.GONE);
		}
		else
		{
			to_tv.setVisibility(View.VISIBLE);
			toname_tv.setVisibility(View.VISIBLE);
		}
		if(commentElement.getTouser().trim().length()>=10)
			toname = "匿名用户";
		else
			toname = "任课老师";
		toname_tv.setText(toname);
		deletebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(new ContextThemeWrapper(context,android.R.style.Theme_Holo_Light_Dialog))
				.setMessage("确认删除评论？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (commentElement.getFromuser().equals(number)) {
							submitCommentService scs = new submitCommentService();
							if (!AppUtils.isOpenNetwork(context))
								myHandler.sendEmptyMessage(NO_NETWORK);
							else {
								SubmitCommentThread SCT = new SubmitCommentThread(edid,
										commentElement.getContent(), "0",
										commentElement.getCommentid(), commentElement
										.getTouser(), commentElement.getType());

								myHandler.sendEmptyMessage(CANCEL_COMMENT_BEGIN);

								SCT.start();
							}
						}
					}
				})
				.setNegativeButton("取消", null).show();
			}
		});
		return convertView;
	}

	private class SubmitCommentThread extends Thread {

		private String edid;
		private String content;
		private String tag;
		private String commentid;
		private String targetUser;
		private String type;

		public SubmitCommentThread(String edid, String content, String tag,
				String commentid, String targetUser, String type) {
			super();
			this.edid = edid;
			this.content = content;
			this.tag = tag;
			this.commentid = commentid;
			this.targetUser = targetUser;
			this.type = type;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Message message = new Message();
			String msg = "";
			if (type.equals("spit")) {
				submitCommentService scs = new submitCommentService();
				msg = scs.upload(edid, number, targetUser, content, tag,
						commentid);
			}
			if (type.equals("advice")) {
				submitAdviceCommentService scs = new submitAdviceCommentService();
				msg = scs.upload(edid, number, targetUser, content, tag,
						commentid);
			}
			try {
				JSONObject jsonObject = new JSONObject(msg);
				String result = jsonObject.getString("msg");
				if (result.equals("deleting succeed")) {
					message.what = CANCEL_COMMENT_FINISHED;
				} else
					message.what = CANCEL_COMMENT_FAILED;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myHandler.sendMessage(message);
		}
	}
}
