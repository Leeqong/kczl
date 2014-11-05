package com.njut.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.changeSpitLikenumService;
import org.nutlab.webService.fetchInfoDetailService;
import org.nutlab.webService.submitCommentService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.njut.R;
import com.njut.data.AdviceElement;
import com.njut.data.CommentElement;
import com.njut.data.CourseTeacherElement;
import com.njut.data.Curriculum;
import com.njut.data.SpitElement;
import com.njut.pullrefresh.ui.PullToRefreshBase;
import com.njut.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.njut.pullrefresh.ui.PullToRefreshListView;
import com.njut.utility.AppUtils;
import com.njut.utility.CalendarHelper;
import com.njut.utility.JsonParse;
import com.njut.view.CommentAdapter;
import com.umeng.analytics.MobclickAgent;

public class CommentActivity extends Activity {

	private Button sendBtn;
	private Button backButton;
	private EditText comment_content_box;
	private final int SUBMIT_COMMENT_FINISHED = 1;
	private final int CANCEL_COMMENT_FINISHED = 2;
	private final int NO_NETWORK = 0;
	private final int SUBMIT_FAILED = 3;
	private final int FETCH_INFO_DETAIL_REFRESHED = 4;
	private final int SERVER_ERROR = 5;
	private final int FETCH_INFO_DETAIL_FAILED = 6;
	private final int NO_CONTENT = 7;
	private final int LIKENUM_ADD_FINISHED = 11;
	private final int LIKENUM_CHANGE_FAILED = 8;
	private final int LIKENUN_ADD_BEGIN = 9;
	private final int LIKENUN_MINUS_BEGIN = 10;
	private final int LIKENUM_MINUS_FINISHED = 12;
	private final int CANCEL_COMMENT_BEGIN = 13;
	private final int CANCEL_COMMENT_FAILED = 14;
	private final int OPEN_SPIT_COMMENT = 1234;

	private ProgressDialog progressDialog;

	private SpitElement spitElement;
	private List<CommentElement> commentlist;
	private int targetPosition = -1;//回复的时候的位置
	private String targetTouser;
	private ListView commentListView;
	private CommentAdapter commentAdapter;
	private TextView comment_count;
	private int position;
	private String edid;

	private TextView coursename_TextView;
	private TextView content_TextView;
	private TextView teacher_TextView;
	private TextView courseTime;
	private TextView thumb_like;
	private ImageView likeface;
	private LinearLayout commentHeaderView;
	private LinearLayout emptyHeaderView;
	private ImageView thumb;

	private PullToRefreshListView commentPullListView;
	private boolean mSpitIsStart = true;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	private String Like;
	private String Dislike;
	private String[] days = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
	private int[] faces = {R.drawable.face_1,R.drawable.face_2,R.drawable.face_3,R.drawable.face_4,R.drawable.face_5};

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case NO_NETWORK:
				Toast.makeText(getApplicationContext(), "请检查网络设置",
						Toast.LENGTH_SHORT).show();
				commentPullListView.onPullDownRefreshComplete();
				break;
			case SUBMIT_FAILED:
				Toast.makeText(getApplicationContext(), "评论提交失败",
						Toast.LENGTH_SHORT).show();
				break;
			case CANCEL_COMMENT_BEGIN:

				progressDialog = ProgressDialog.show(new ContextThemeWrapper(CommentActivity.this,android.R.style.Theme_Holo_Light_Dialog),
						null, "正在删除评论", true);
				break;
			case CANCEL_COMMENT_FAILED:
				Toast.makeText(getApplicationContext(), "评论删除失败",
						Toast.LENGTH_SHORT).show();
				break;

			case LIKENUM_ADD_FINISHED: {
				int position = msg.getData().getInt("position");
				int templike = spitElement.getLikenum();

				spitElement.setLikenum(templike + 1);
				spitElement.setEverlike("true");
				kczlApplication.SpitElements.set(position, spitElement);
				setLikeUI();
			}
			break;
			case LIKENUM_MINUS_FINISHED: {
				int position = msg.getData().getInt("position");
				int templike = spitElement.getLikenum();
				spitElement.setLikenum(templike - 1);
				spitElement.setEverlike("false");
				kczlApplication.SpitElements.set(position, spitElement);
				setLikeUI();
			}
			break;
			case SUBMIT_COMMENT_FINISHED:
				Toast.makeText(getApplicationContext(), "评论提交成功！",
						Toast.LENGTH_SHORT).show();
				if (!AppUtils.isOpenNetwork(CommentActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else
					commentPullListView.doPullRefreshing(true, 0);
				comment_content_box.setText("");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						comment_content_box.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				break;
			case CANCEL_COMMENT_FINISHED:
				Toast.makeText(getApplicationContext(), "评论删除成功！",
						Toast.LENGTH_SHORT).show();
				if (!AppUtils.isOpenNetwork(CommentActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else
					commentPullListView.doPullRefreshing(true, 0);
				break;
			case NO_CONTENT:
				Toast.makeText(getApplicationContext(), "评论不能为空！",
						Toast.LENGTH_SHORT).show();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器内部错误，请稍后重试",
						Toast.LENGTH_SHORT).show();
				commentPullListView.onPullDownRefreshComplete();
				break;
			case FETCH_INFO_DETAIL_REFRESHED:
				if (commentlist.size() == 0) {
					emptyHeaderView.setVisibility(View.VISIBLE);
				} else {
					emptyHeaderView.setVisibility(View.GONE);
				}
				if (kczlApplication.IsStudent == 1) {
					kczlApplication.SpitElements.set(position, spitElement);
					kczlApplication.SpitString = JsonParse.SpitListToJson(kczlApplication.SpitElements);
				} else if (kczlApplication.IsStudent == 2) {
					for (int i = 0; i < kczlApplication.teacherlist.size(); i++) {
						if (kczlApplication.teacherlist.get(i).getCtid()
								.equals(spitElement.getCtid()))
							kczlApplication.teacherlist.get(i).getSpitlist()
							.set(position, spitElement);
					}
				}
				setLikeUI();
				commentAdapter.notifyDataSetChanged();
				commentPullListView.onPullDownRefreshComplete();
				commentPullListView.setHasMoreData(true);
				setCommentLastUpdateTime();
				comment_count.setText(commentlist.size() + "条评论");
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (progressDialog != null)
			progressDialog.dismiss();
		if (kczlApplication.IsStudent == 1)
			saveStudentParams();
		else if (kczlApplication.IsStudent == 2)
			saveTeacherParams();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("UserName", kczlApplication.UserName);
		outState.putString("PassWord", kczlApplication.PassWord);
		outState.putInt("IsLogined", kczlApplication.IsLogined);
		outState.putInt("isClocked", kczlApplication.isClocked);
		outState.putInt("isNotification", kczlApplication.isNotification);
		outState.putInt("IsOffLine", kczlApplication.IsOffLine);
		outState.putString("ServerUri", kczlApplication.ServerUri);
		outState.putInt("IsStudent", kczlApplication.IsStudent);

		if (kczlApplication.IsStudent == 1) {
			outState.putString("PersonString", kczlApplication.PersonString);
			outState.putString("CurriculumsString",
					kczlApplication.CurriculumsString);
			outState.putString("AchievementString",
					kczlApplication.AchievementString);
			outState.putString("SpitString", kczlApplication.SpitString);
			outState.putString("AdviceString", kczlApplication.AdviceString);
		} else if (kczlApplication.IsStudent == 2) {
			outState.putString("CurriculumtString",
					kczlApplication.CurriculumtString);
			Gson gson = new Gson();
			outState.putString("TeacherListString",
					gson.toJson(kczlApplication.teacherlist));
			outState.putString("PersonTeacherString",
					kczlApplication.PersonTeacherString);
		}
	}

	private void setCommentLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		commentPullListView.setLastUpdatedLabel(text);
	}

	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}

		return mDateFormat.format(new Date(time));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (progressDialog != null)
			progressDialog.dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (null != savedInstanceState) {
			JsonParse jp = new JsonParse();
			kczlApplication.UserName = savedInstanceState.getString("UserName");
			kczlApplication.PassWord = savedInstanceState.getString("PassWord");
			kczlApplication.ServerUri = savedInstanceState
					.getString("ServerUri");
			kczlApplication.IsLogined = savedInstanceState.getInt("IsLogined");
			kczlApplication.isClocked = savedInstanceState.getInt("isClocked");
			kczlApplication.isNotification = savedInstanceState
					.getInt("isNotification");
			kczlApplication.IsOffLine = savedInstanceState.getInt("IsOffLine");
			kczlApplication.IsStudent = savedInstanceState.getInt("IsStudent");
			if (kczlApplication.IsStudent == 1) {
				kczlApplication.PersonString = savedInstanceState
						.getString("PersonString");
				kczlApplication.AchievementString = savedInstanceState
						.getString("AchievementString");
				kczlApplication.Person = jp
						.jsonToPersonElement(savedInstanceState
								.getString("PersonString"));
				kczlApplication.CurriculumsString = savedInstanceState
						.getString("CurriculumsString");
				kczlApplication.AdviceString = savedInstanceState
						.getString("AdviceString");
				kczlApplication.SpitString = savedInstanceState
						.getString("SpitString");
				try {
					if (!(kczlApplication.CurriculumsString.equals(""))) {
						JSONArray jsonObjs;

						jsonObjs = new JSONArray(
								kczlApplication.CurriculumsString);
						List<Curriculum> curriculums = new ArrayList<Curriculum>();
						for (int i = 0; i < jsonObjs.length(); i++) {
							curriculums.add(jp
									.jsonToCurriculum((JSONObject) jsonObjs
											.opt(i)));
						}
						kczlApplication.Curriculums.clear();
						kczlApplication.Curriculums.addAll(curriculums);
					}
					if (!(kczlApplication.AdviceString.equals(""))) {
						JSONArray jsonAdvice = new JSONArray(
								kczlApplication.AdviceString);
						List<AdviceElement> adviceElements = new ArrayList<AdviceElement>();
						for (int i = 0; i < jsonAdvice.length(); i++) {
							adviceElements
							.add(jp.jsonToAdviceElement((JSONObject) jsonAdvice
									.opt(i)));
							kczlApplication.AdviceElements.clear();
							kczlApplication.AdviceElements
							.addAll(adviceElements);
						}
					}
					if (!(kczlApplication.SpitString.equals(""))) {
						JSONArray jsonSpit = new JSONArray(
								kczlApplication.SpitString);
						List<SpitElement> spitElements = new ArrayList<SpitElement>();
						for (int i = 0; i < jsonSpit.length(); i++) {
							spitElements.add(jp
									.jsonToSpitElement((JSONObject) jsonSpit
											.opt(i)));
						}
						kczlApplication.SpitElements.clear();
						kczlApplication.SpitElements.addAll(spitElements);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (kczlApplication.IsStudent == 2) {
				kczlApplication.CurriculumtString = savedInstanceState
						.getString("CurriculumtString");
				String teacherliststring = savedInstanceState
						.getString("TeacherListString");
				kczlApplication.PersonTeacherString = savedInstanceState
						.getString("PersonTeacherString");
				JSONArray teacherArray;
				try {
					teacherArray = new JSONArray(teacherliststring);
					kczlApplication.teacherlist.clear();
					for (int i = 0; i < teacherArray.length(); i++) {
						kczlApplication.teacherlist
						.add(jp.jsonToTeacherListElemet((JSONObject) teacherArray
								.opt(i)));
					}
					kczlApplication.PersonTeacher = jp
							.jsonToPersonTeacherElement(kczlApplication.PersonTeacherString);
					JSONArray jsonObjs = new JSONArray(
							kczlApplication.CurriculumtString);
					List<CourseTeacherElement> curriculumt = new ArrayList<CourseTeacherElement>();
					for (int i = 0; i < jsonObjs.length(); i++) {
						curriculumt
						.add(jp.jsonToCourseTeacherElement((JSONObject) jsonObjs
								.opt(i)));
					}
					kczlApplication.CourseTeacherElements.clear();
					kczlApplication.CourseTeacherElements.addAll(curriculumt);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment);

		spitElement = (SpitElement) getIntent().getSerializableExtra(
				"spitElement");
		commentlist = spitElement.getCommmentlist();
		Like = getResources().getString(R.string.Like);
		Dislike = getResources().getString(R.string.Dislike);

		sendBtn = (Button) findViewById(R.id.send_comment);
		backButton = (Button) findViewById(R.id.toolbar_nav_button);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						comment_content_box.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				setResult(OPEN_SPIT_COMMENT, null);
				CommentActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
			}
		});
		commentHeaderView = (LinearLayout) getLayoutInflater().inflate(
				R.layout.comment_header, null);
		emptyHeaderView = (LinearLayout) commentHeaderView
				.findViewById(R.id.emptyView);
		if (commentlist.size() == 0) {
			emptyHeaderView.setVisibility(View.VISIBLE);
		} else {
			emptyHeaderView.setVisibility(View.GONE);
		}

		comment_content_box = (EditText) findViewById(R.id.comment_content);

		comment_count = (TextView) commentHeaderView
				.findViewById(R.id.comment_count);
		coursename_TextView = (TextView) commentHeaderView
				.findViewById(R.id.coursename_TextView1);
		content_TextView = (TextView) commentHeaderView
				.findViewById(R.id.content_TextView);
		teacher_TextView = (TextView) commentHeaderView
				.findViewById(R.id.teacher_TextView);
		TextView spittime = (TextView) commentHeaderView
				.findViewById(R.id.spittime);
		courseTime = (TextView) commentHeaderView
				.findViewById(R.id.time_TextView);
		thumb_like = (TextView) commentHeaderView
				.findViewById(R.id.thumb_TextView);
		thumb = (ImageView) commentHeaderView.findViewById(R.id.thumb);
		likeface = (ImageView) commentHeaderView.findViewById(R.id.face);

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sf.parse(spitElement.getTimestamp());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sf1 = new SimpleDateFormat("MM-dd HH:mm");
		spittime.setText(sf1.format(date));
		TextView month_tv = (TextView) commentHeaderView.findViewById(R.id.month_tv);
		TextView day_tv = (TextView) commentHeaderView.findViewById(R.id.day_tv);
		month_tv.setText(new SimpleDateFormat("M月").format(date));
		day_tv.setText(new SimpleDateFormat("d日").format(date));

		CalendarHelper cHelper = new CalendarHelper();
		SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd");
		int day = cHelper.getDay(sf3.format(date),
				getResources().getString(R.string.begindate));
		int week = cHelper.getWeek(sf3.format(date),
				getResources().getString(R.string.begindate));
		if (day > 0 && week > 0)
			courseTime.setText("第" + week + "周 " + days[day - 1]);
		else
			courseTime.setText("获取失败");

		if (kczlApplication.IsStudent == 1) {
			for (Curriculum curriculum : kczlApplication.Curriculums) {
				if (curriculum.getCtid().equals(spitElement.getCtid())) {
					teacher_TextView.setText(curriculum.getTeacher());
					break;
				}
			}
		} else if (kczlApplication.IsStudent == 2) {
			teacher_TextView.setText(kczlApplication.PersonTeacher
					.getTeachername());
		}
		position = getIntent().getExtras().getInt("position");
		edid = spitElement.getSpitid();
		String author_num  = spitElement.getSchoolnumber();
		commentAdapter = new CommentAdapter(commentlist, this, edid,author_num, myHandler);
		commentPullListView = (PullToRefreshListView) findViewById(R.id.commmentListView);
		// 上拉加载不可用
		commentPullListView.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		commentPullListView.setScrollLoadEnabled(false);
		// xml的PullToRefreshListView不是listview，所以设的属性也没用，要在代码再设

		LinearLayout emptyView = (LinearLayout) findViewById(R.id.emptyView);
		commentListView = commentPullListView.getRefreshableView();
		commentListView.setCacheColorHint(Color.TRANSPARENT);
		commentListView.setDivider(this.getResources().getDrawable(
				R.drawable.commentlist_divider));
		commentListView.setDividerHeight(1);
		commentListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// commentListView
		// .setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		commentListView.addHeaderView(commentHeaderView);
		commentListView.setAdapter(commentAdapter);

		commentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position == 0)
					return;
				if(commentlist.size() >0)
				{
					if (commentlist.get(position-1).getContent().startsWith("[T]")) {
						comment_content_box.setHint("回复任课教师:");
					}
					else
					{
						comment_content_box.setHint("回复匿名用户:");
					}
					targetPosition = position -1 ;
					comment_content_box.setFocusable(true);
					comment_content_box.setFocusableInTouchMode(true);
					comment_content_box.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(comment_content_box,
							InputMethodManager.SHOW_IMPLICIT);

				}
			}
		});

		// commentListView.setEmptyView(emptyView);
		//		if (commentlist.size() != 0)
		//			commentListView.setSelection(1);
		// Button refresh = (Button) this.findViewById(R.id.refresh);
		// refresh.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// commentPullListView.doPullRefreshing(true, 0);
		// }
		// });

		// if (!AppUtils.isOpenNetwork(CommentActivity.this))
		// myHandler.sendEmptyMessage(NO_NETWORK);
		// else
		// commentPullListView.doPullRefreshing(true, 0);
		if (!AppUtils.isOpenNetwork(CommentActivity.this))
			myHandler.sendEmptyMessage(NO_NETWORK);
		else
		{
			if (getIntent().getBooleanExtra("toRefresh",false))
				commentPullListView.doPullRefreshing(true, 0);
		}
		commentPullListView
		.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils.isOpenNetwork(CommentActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mSpitIsStart = true;
					FetchInfoDetailThread FIDT = new FetchInfoDetailThread();
					FIDT.start();
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils.isOpenNetwork(CommentActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mSpitIsStart = false;
					FetchInfoDetailThread FIDT = new FetchInfoDetailThread();
					FIDT.start();
				}
			}
		});

		coursename_TextView.setText(spitElement.getCoursename());
		content_TextView.setText(spitElement.getContent());
		comment_count.setText(commentlist.size() + "条评论");
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String content = comment_content_box.getText().toString()
						.trim();

				if (!AppUtils.isOpenNetwork(CommentActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					if (!content.equals("")) {
						SubmitCommentThread SCT;
						if(targetPosition == -1)
						{
							SCT = new SubmitCommentThread(
									content, "1", "",spitElement.getSchoolnumber());
						}
						else
						{
							SCT = new SubmitCommentThread(
									content, "1", commentlist.get(targetPosition).getCommentid(), commentlist.get(targetPosition).getFromuser());
						}
						progressDialog = ProgressDialog.show(
								new ContextThemeWrapper(CommentActivity.this,android.R.style.Theme_Holo_Light_Dialog),
								null, "正在提交评论", true);
						SCT.start();
					} else
						myHandler.sendEmptyMessage(NO_CONTENT);
				}
			}
		});


		setLikeUI();

		RelativeLayout tospit = (RelativeLayout) commentHeaderView
				.findViewById(R.id.spit_comment_layout);

		tospit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				comment_content_box.setFocusable(true);
				comment_content_box.setFocusableInTouchMode(true);
				comment_content_box.requestFocus();
				comment_content_box.setHint("写评论...(200字内)");
				targetPosition = -1;//不是回复评论
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(comment_content_box,
						InputMethodManager.SHOW_IMPLICIT);
			}
		});
		RelativeLayout changlikennum = (RelativeLayout) commentHeaderView
				.findViewById(R.id.spit_like_layout);
		if (kczlApplication.IsStudent == 2)
			changlikennum.setVisibility(View.GONE);
		changlikennum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!AppUtils.isOpenNetwork(CommentActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					String tag = "";
					if (spitElement.getEverlike().equals("true")) {
						tag = "0";

						progressDialog = ProgressDialog.show(
								new ContextThemeWrapper(CommentActivity.this,android.R.style.Theme_Holo_Light_Dialog),
								null,
								getString(R.string.disliking), true);
					} else {
						tag = "1";
						progressDialog = ProgressDialog.show(
								new ContextThemeWrapper(CommentActivity.this,android.R.style.Theme_Holo_Light_Dialog),
								null,
								getString(R.string.liking), true);
					}
					ChangeLikenumThread CLT = new ChangeLikenumThread(
							spitElement.getSpitid(), myHandler, position, tag,
							spitElement.getSchoolnumber());

					CLT.start();
				}
			}
		});

	}

	private void setLikeUI() {
		int likenum = spitElement.getLikenum();
		likeface
		.setImageResource(faces[position%5]);
		thumb_like.setText("("+likenum+")");
		if (spitElement.getEverlike().equals("true"))
			thumb.setImageResource(R.drawable.thumb_pressed);
		else
			thumb.setImageResource(R.drawable.thumb);
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
			msg = clns.upload(spitid, kczlApplication.Person.getSchoolnumber(),
					schoolnumberTo, tag);// infoelement还要增加成员变量edid(每条评教的标识id)
			// ctid(发送此条评教用户的学号)
			// from to都是发送评教人的学号 tag ChangeLikenumThread加个成员，初始化的传进来
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = CommentActivity.this
					.getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("IsLogined", kczlApplication.IsLogined);
			editor.commit();
			try {
				finishChangeLikenumOperation(msg);
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
				if (jsonObject.getString("msg").equals("+1")) {
					message.what = LIKENUM_ADD_FINISHED;
				}
				if (jsonObject.getString("msg").equals("-1")) {
					message.what = LIKENUM_MINUS_FINISHED;
				}
				myHandler.sendMessage(message);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String finishChangeLikenumOperation(String mStringReturnStr)// 不要写在UI线程上
			throws JSONException {
		JSONObject jsonObjs = new JSONObject(mStringReturnStr);
		String result = jsonObjs.getString("msg");
		return result;
	}

	class FetchInfoDetailThread extends Thread {
		public void run() {

			fetchInfoDetailService fs = new fetchInfoDetailService();
			String msg = fs.get(spitElement.getSpitid());
			if (msg.startsWith("Error Response")) {
				myHandler.sendEmptyMessage(SERVER_ERROR);
				return;
			}
			finishFetchInfoDetailOperation(msg);

		}
	}

	private void finishFetchInfoDetailOperation(String mStringReturnStr) {
		Message message = new Message();
		message.what = FETCH_INFO_DETAIL_REFRESHED;
		try {
			JSONObject jsonObjs = new JSONObject(mStringReturnStr);
			JsonParse jp = new JsonParse();
			spitElement = jp.jsonToSpitElement(jsonObjs);
			commentlist.clear();
			commentlist.addAll(spitElement.getCommmentlist());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myHandler.sendMessage(message);
	}

	private class SubmitCommentThread extends Thread {

		private String content;
		private String tag;
		private String commentid;
		private String targetUser;

		public SubmitCommentThread(String content, String tag, String commentid,String targetUser) {
			super();
			this.content = content;
			this.tag = tag;
			this.commentid = commentid;
			this.targetUser = targetUser;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Message message = new Message();
			submitCommentService scs = new submitCommentService();
			String from = "";
			if (kczlApplication.IsStudent == 1)
				from = kczlApplication.Person.getSchoolnumber();
			else if (kczlApplication.IsStudent == 2)
				from = kczlApplication.PersonTeacher.getTeachernumber();
			String msg = scs.upload(spitElement.getSpitid(), from,
					targetUser, content, tag, commentid);
			try {
				JSONObject jsonObject = new JSONObject(msg);
				String result = jsonObject.getString("msg");
				if (result.equals("succeed")) {
					message.what = SUBMIT_COMMENT_FINISHED;
				} else
					message.what = SUBMIT_FAILED;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			myHandler.sendMessage(message);
		}
		// private void finishCommentSubmitOperation(String mStringReturnStr)
		// {
		//
		// }

	}

	void saveStudentParams() {
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("adviceString", kczlApplication.AdviceString);
		editor.putString("spitString", kczlApplication.SpitString);
		editor.commit();
	}

	void saveTeacherParams() {
		JsonParse jp = new JsonParse();
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("TeacherListString",
				jp.teacherListToJSON(kczlApplication.teacherlist));
		editor.commit();
	}

}
