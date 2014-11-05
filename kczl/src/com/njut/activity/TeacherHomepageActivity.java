package com.njut.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.TcardService;
import org.nutlab.webService.teacherAdviceService;
import org.nutlab.webService.teacherSpitService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.njut.R;
import com.njut.data.AdviceElement;
import com.njut.data.CourseTeacherElement;
import com.njut.data.SpitElement;
import com.njut.data.TcardElement;
import com.njut.data.TeacherListElement;
import com.njut.pullrefresh.ui.PullToRefreshBase;
import com.njut.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.njut.pullrefresh.ui.PullToRefreshListView;
import com.njut.utility.AppUtils;
import com.njut.utility.JsonParse;
import com.njut.view.AdviceAdapter;
import com.njut.view.SpitAdapter;
import com.njut.view.TcardAdapter;
import com.umeng.analytics.MobclickAgent;

public class TeacherHomepageActivity extends Activity {

	private String targetCtid;
	private int targetLocation = -1;
	private String courseName;
	// 下拉刷新
	private PullToRefreshListView advicePullListView;
	private PullToRefreshListView spitPullListView;
	private PullToRefreshListView tcardPullListView;
	// 为ViewPager准备
	private View spitView;
	private View adviceView;
	private View tcardView;
	private ViewPager viewPager;// 页卡内容
	private ImageView strip;// 动画长条（strip）
	private TextView AdviceTextView;
	private TextView SpitTextView;
	private TextView TcardTextView;

	private AdviceAdapter adviceAdapter;
	private SpitAdapter spitAdapter;
	private TcardAdapter tcardAdapter;
	private ListView adviceListView;
	private ListView spitListView;
	private ListView tcardListView;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private List<AdviceElement> advicelist;
	private List<SpitElement> spitlist;
	private List<TcardElement> tcardlist;
	private String lastAdviceUpdate;// 保存listview中最后一个item的edid
	private String lastSpitUpdate;// 保存listview中最后一个item的edid
	private String lastTcardUpdate;// 保存listview中最后一个item的edid
	// 标示滑动的方向
	private boolean mAdviceIsStart = true;
	private boolean mSpitIsStart = true;
	private boolean mTcardIsStart = true;
	private ProgressDialog progressDialog;
	private int tripleScreenWidth;
	private int index = 1;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	private final int ADVICE_GET_FINISHED = 1;
	private final int LIKENUM_ADD_FINISHED = 2;
	private final int NO_MESSAGE = 3;
	private final int NO_NETWORK = 4;
	private final int SERVER_ERROR = 5;
	private final int SPIT_GET_FINISHED = 6;
	private final int LIKENUM_MINUS_FINISHED = 7;
	private final int LIKENUM_CHANGE_FAILED = 8;
	private final int LIKENUN_ADD_BEGIN = 9;
	private final int LIKENUN_MINUS_BEGIN = 10;
	private final int OPEN_SPIT_COMMENT = 1234;
	private final int OPEN_ADVICE_COMMENT = 4321;
	private final int TCARD_GET_FINISHED = 11;

	/** Called when the activity is first created. */
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case ADVICE_GET_FINISHED: {
				adviceAdapter.notifyDataSetChanged();
				advicePullListView.onPullDownRefreshComplete();
				advicePullListView.onPullUpRefreshComplete();
				advicePullListView.setHasMoreData(true);
				setAdviceLastUpdateTime();
			}
			break;
			case LIKENUN_ADD_BEGIN:
				progressDialog = ProgressDialog.show(
						TeacherHomepageActivity.this,
						getString(R.string.state), getString(R.string.liking),
						true);
				break;
			case LIKENUN_MINUS_BEGIN:
				progressDialog = ProgressDialog.show(
						TeacherHomepageActivity.this,
						getString(R.string.state),
						getString(R.string.disliking), true);
				break;
			case SPIT_GET_FINISHED:
				spitAdapter.notifyDataSetChanged();
				spitPullListView.onPullDownRefreshComplete();
				spitPullListView.onPullUpRefreshComplete();
				spitPullListView.setHasMoreData(true);
				setSpitLastUpdateTime();
				break;
			case LIKENUM_ADD_FINISHED: {
				int position = msg.getData().getInt("position");
				int templike = spitlist.get(position).getLikenum();
				spitlist.get(position).setLikenum(templike + 1);
				spitlist.get(position).setEverlike("true");
				spitAdapter.notifyDataSetChanged();
			}
			break;
			case LIKENUM_MINUS_FINISHED: {
				int position = msg.getData().getInt("position");
				int templike = spitlist.get(position).getLikenum();
				spitlist.get(position).setLikenum(templike - 1);
				spitlist.get(position).setEverlike("false");
				spitAdapter.notifyDataSetChanged();
			}
			break;
			case NO_MESSAGE: {
				Toast.makeText(getApplicationContext(), "亲，没有更多消息了",
						Toast.LENGTH_SHORT).show();
			}
			break;
			case NO_NETWORK: {
				Toast.makeText(getApplicationContext(), "请检查网络设置",
						Toast.LENGTH_SHORT).show();
				advicePullListView.onPullDownRefreshComplete();
				advicePullListView.onPullUpRefreshComplete();
				spitPullListView.onPullDownRefreshComplete();
				spitPullListView.onPullUpRefreshComplete();
				tcardPullListView.onPullDownRefreshComplete();
				tcardPullListView.onPullUpRefreshComplete();
			}
			break;
			case SERVER_ERROR: {
				Toast.makeText(getApplicationContext(), "服务器内部错误，请稍后重试",
						Toast.LENGTH_SHORT).show();
				advicePullListView.onPullDownRefreshComplete();
				advicePullListView.onPullUpRefreshComplete();
				spitPullListView.onPullDownRefreshComplete();
				spitPullListView.onPullUpRefreshComplete();
				tcardPullListView.onPullDownRefreshComplete();
				tcardPullListView.onPullUpRefreshComplete();
			}
			break;
			case TCARD_GET_FINISHED: {
				tcardAdapter.notifyDataSetChanged();
				tcardPullListView.onPullDownRefreshComplete();
				tcardPullListView.onPullUpRefreshComplete();
				tcardPullListView.setHasMoreData(true);
				setTcardLastUpdateTime();
			}
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
		saveParams();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == OPEN_SPIT_COMMENT) {
			spitlist.clear();
			spitlist.addAll(kczlApplication.teacherlist.get(targetLocation)
					.getSpitlist());
			spitAdapter.notifyDataSetChanged();
		}
		if (requestCode == OPEN_ADVICE_COMMENT) {
			advicelist.clear();
			advicelist.addAll(kczlApplication.teacherlist.get(targetLocation)
					.getAdvicelist());
			adviceAdapter.notifyDataSetChanged();
		}
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
		outState.putString("CurriculumtString",
				kczlApplication.CurriculumtString);
		Gson gson = new Gson();
		outState.putString("TeacherListString",
				gson.toJson(kczlApplication.teacherlist));
		outState.putString("PersonTeacherString",
				kczlApplication.PersonTeacherString);
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
					kczlApplication.teacherlist.add(jp
							.jsonToTeacherListElemet((JSONObject) teacherArray
									.opt(i)));
				}
				kczlApplication.PersonTeacher = jp
						.jsonToPersonTeacherElement(kczlApplication.PersonTeacherString);
				JSONArray jsonObjs = new JSONArray(
						kczlApplication.CurriculumtString);
				List<CourseTeacherElement> curriculumt = new ArrayList<CourseTeacherElement>();
				for (int i = 0; i < jsonObjs.length(); i++) {
					curriculumt.add(jp
							.jsonToCourseTeacherElement((JSONObject) jsonObjs
									.opt(i)));
				}
				kczlApplication.CourseTeacherElements.clear();
				kczlApplication.CourseTeacherElements.addAll(curriculumt);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.teacher_homepage);
		targetCtid = getIntent().getExtras().getString("ctid");
		courseName = getIntent().getExtras().getString("coursename");
		TextView courseNameTxt = (TextView) this
				.findViewById(R.id.courseNameTxt);
		courseNameTxt.setText(courseName);
		InitImageView();// 长条动画
		InitPullListView();
		InitViewPager();

		Button back = (Button) this.findViewById(R.id.toolbar_nav_button);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Boolean PullUDIntroKnown = settings.getBoolean("PullUDIntroKnown", false);
		if(!PullUDIntroKnown)
		{
			Intent intent = new Intent(TeacherHomepageActivity.this, PullUDIntroActivity.class);
			startActivity(intent);
		}
	}

	private void InitPullListView() {


		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		lastTcardUpdate = settings.getString("lastTcardUpdate", "");
		advicelist = new ArrayList<AdviceElement>();
		lastAdviceUpdate = "";
		spitlist = new ArrayList<SpitElement>();
		lastSpitUpdate = "";
		tcardlist = new ArrayList<TcardElement>();
		for (int i = 0; i < kczlApplication.teacherlist.size(); i++) {
			if (kczlApplication.teacherlist.get(i).getCtid().equals(targetCtid)) {
				targetLocation = i;
				advicelist.addAll(kczlApplication.teacherlist.get(i)
						.getAdvicelist());
				spitlist.addAll(kczlApplication.teacherlist.get(i)
						.getSpitlist());
				tcardlist.addAll(kczlApplication.teacherlist.get(i)
						.getTcardlist());

			}
		}
		//json:[{"arrangement":1,"attendance":5,"datestamp":"2014-5-13","fivestar":0,"fourstar":0,"learningEffect":3,"num":1,"onestar":0,"speed":3,"threestar":1,"total":63,"twostar":0}] 

//		TcardElement temp = new TcardElement();
//
//		temp.setOnestar(1);
//		temp.setTwostar(1);
//		temp.setThreestar(4);
//		temp.setFourstar(32);
//		temp.setFivestar(23);
//		temp.setTotal(63);
//		temp.setNum(61);
//		temp.setLearningEffect(4);
//		temp.setDatestamp("2014-5-27");
//		tcardlist.add(temp);
//		TcardElement temp1 = new TcardElement();
//		temp1.setOnestar(2);
//		temp1.setTwostar(2);
//		temp1.setThreestar(3);
//		temp1.setFourstar(32);
//		temp1.setFivestar(20);
//		temp1.setTotal(63);
//		temp1.setNum(59);
//		temp1.setLearningEffect(4);
//		temp1.setDatestamp("2014-5-23");
//		tcardlist.add(temp1);
//		TcardElement temp2 = new TcardElement();
//		temp2.setOnestar(2);
//		temp2.setTwostar(3);
//		temp2.setThreestar(32);
//		temp2.setFourstar(15);
//		temp2.setFivestar(8);
//		temp2.setTotal(63);
//		temp2.setNum(60);
//		temp2.setLearningEffect(4);
//		temp2.setDatestamp("2014-5-20");
//		tcardlist.add(temp2);
		if (targetLocation == -1) {// 之前没有存储过这门课的数据，新建一个条目
			targetLocation = kczlApplication.teacherlist.size();
			TeacherListElement teacherListElement = new TeacherListElement();
			teacherListElement.setCtid(targetCtid);
			teacherListElement.setAdvicelist(advicelist);
			teacherListElement.setSpitlist(spitlist);
			teacherListElement.setTcardlist(tcardlist);
			kczlApplication.teacherlist.add(teacherListElement);
		}
		if (advicelist.size() != 0)
			lastAdviceUpdate = advicelist.get(advicelist.size() - 1).getCaid();
		if (spitlist.size() != 0)
			lastSpitUpdate = spitlist.get(spitlist.size() - 1).getSpitid();
		adviceAdapter = new AdviceAdapter(this, advicelist, myHandler);

		LayoutInflater inflater = getLayoutInflater();
		spitView = inflater.inflate(R.layout.teacher_spitlist, null);
		adviceView = inflater.inflate(R.layout.teacher_advicelist, null);
		tcardView = inflater.inflate(R.layout.tcardlist, null);
		spitPullListView = (PullToRefreshListView) spitView
				.findViewById(R.id.spit_ListView);
		advicePullListView = (PullToRefreshListView) adviceView
				.findViewById(R.id.advice_ListView);
		tcardPullListView = (PullToRefreshListView) tcardView
				.findViewById(R.id.tcard_ListView);
		// 上拉加载不可用
		advicePullListView.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		advicePullListView.setScrollLoadEnabled(true);
		adviceListView = advicePullListView.getRefreshableView();
		// xml的PullToRefreshListView不是listview，所以设的属性也没用，要在代码再设
		adviceListView.setCacheColorHint(Color.TRANSPARENT);
		adviceListView.setDivider(null);
		adviceListView.setDividerHeight(0);
		adviceListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adviceListView.setAdapter(adviceAdapter);
		LinearLayout emptyView = (LinearLayout) adviceView
				.findViewById(R.id.emptyView);
		adviceListView.setEmptyView(emptyView);
		Button refresh = (Button) adviceView.findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				advicePullListView.doPullRefreshing(true, 0);
			}
		});

		spitAdapter = new SpitAdapter(this, spitlist, myHandler);

		// 上拉加载不可用
		spitPullListView.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		spitPullListView.setScrollLoadEnabled(true);
		spitListView = spitPullListView.getRefreshableView();
		// xml的PullToRefreshListView不是listview，所以设的属性也没用，要在代码再设
		spitListView.setCacheColorHint(Color.TRANSPARENT);
		spitListView.setDivider(null);
		spitListView.setDividerHeight(0);
		spitListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		spitListView.setAdapter(spitAdapter);

		LinearLayout emptyView1 = (LinearLayout) spitView
				.findViewById(R.id.emptyView);
		spitListView.setEmptyView(emptyView1);
		Button refresh1 = (Button) spitView.findViewById(R.id.refresh);
		refresh1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				spitPullListView.doPullRefreshing(true, 0);
			}
		});

		tcardAdapter = new TcardAdapter(tcardlist,
				TeacherHomepageActivity.this);
		// 上拉加载不可用
		tcardPullListView.setPullLoadEnabled(false);
		tcardPullListView.setScrollLoadEnabled(true);
		tcardListView = tcardPullListView.getRefreshableView();

		tcardListView.setCacheColorHint(Color.TRANSPARENT);
		tcardListView.setDivider(null);
		tcardListView.setDividerHeight(0);
		tcardListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		tcardListView.setAdapter(tcardAdapter);

		if (spitlist.size() == 0)
			spitPullListView.doPullRefreshing(true, 0);
		if (advicelist.size() == 0)
			advicePullListView.doPullRefreshing(true, 0);
		if (tcardlist.size() == 0)
			tcardPullListView.doPullRefreshing(true, 0);
		if (getIntent().getStringExtra("isFromTNotice") != null
				&& getIntent().getStringExtra("isFromTNotice").equals(
						"true"))
		{
			//点通知进来的一定自动下来刷新
			tcardPullListView.doPullRefreshing(true, 0);
		}
		advicePullListView
		.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils
						.isOpenNetwork(TeacherHomepageActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mAdviceIsStart = true;
					AdviceGetThread AGT = new AdviceGetThread();
					AGT.start();
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils
						.isOpenNetwork(TeacherHomepageActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mAdviceIsStart = false;
					AdviceGetThread AGT = new AdviceGetThread();
					AGT.start();
				}
			}
		});

		spitPullListView
		.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils
						.isOpenNetwork(TeacherHomepageActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mSpitIsStart = true;
					SpitGetThread SGT = new SpitGetThread();
					SGT.start();
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils
						.isOpenNetwork(TeacherHomepageActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mSpitIsStart = false;
					SpitGetThread SGT = new SpitGetThread();
					SGT.start();
				}
			}
		});
		tcardPullListView
		.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils
						.isOpenNetwork(TeacherHomepageActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mTcardIsStart = true;
					TcardGetThread TGT = new TcardGetThread();
					TGT.start();
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils
						.isOpenNetwork(TeacherHomepageActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mTcardIsStart = false;
					TcardGetThread TGT = new TcardGetThread();
					TGT.start();
				}
			}
		});
	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.teacher_vPager);
		views = new ArrayList<View>();
		views.add(tcardView);
		views.add(adviceView);
		views.add(spitView);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		AdviceTextView = (TextView) findViewById(R.id.advice_TextView);
		SpitTextView = (TextView) findViewById(R.id.spit_TextView);
		TcardTextView = (TextView) findViewById(R.id.tcard_TextView);
		// AdviceTextView在左边，SpitTextView在右边
		AdviceTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				index = 1;
				viewPager.setCurrentItem(index);
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				SpitTextView
				.setTextColor(getResources().getColor(R.color.gray));
				TcardTextView.setTextColor(getResources()
						.getColor(R.color.gray));
			}
		});
		SpitTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				index = 2;
				viewPager.setCurrentItem(index);
				SpitTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.gray));
				TcardTextView.setTextColor(getResources()
						.getColor(R.color.gray));
			}
		});
		TcardTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				index = 0;
				viewPager.setCurrentItem(index);
				TcardTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.gray));
				SpitTextView
				.setTextColor(getResources().getColor(R.color.gray));
			}
		});
	}

	// 初始化长条动画
	private void InitImageView() {
		strip = (ImageView) findViewById(R.id.strip);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.strip)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		tripleScreenWidth = screenW / 3;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				tripleScreenWidth, LayoutParams.WRAP_CONTENT);
		strip.setLayoutParams(params);
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, 0);
		strip.setImageMatrix(matrix);// 设置动画初始位置
	}

	private void setAdviceLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		advicePullListView.setLastUpdatedLabel(text);
	}

	private void setSpitLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		spitPullListView.setLastUpdatedLabel(text);
	}
	private void setTcardLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		tcardPullListView.setLastUpdatedLabel(text);
	}

	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}

		return mDateFormat.format(new Date(time));
	}

	/** Called when the activity is first created. */

	class AdviceGetThread extends Thread {
		public void run() {

			Message message = new Message();
			message.what = ADVICE_GET_FINISHED;
			teacherAdviceService tas = new teacherAdviceService();

			String msg;
			if (mAdviceIsStart)
				msg = tas.get(targetCtid, "null");
			else
				msg = tas.get(targetCtid, lastAdviceUpdate);

			// 如果msg没错误
			// 当上拉加载更多没有更多的时候，json:[] lastupdate:XXX
			// if (msg == "Error") {
			// myHandler.sendEmptyMessage(NO_NETWORK);
			// return;
			// }
			if (msg.startsWith("Error Response")) {
				myHandler.sendEmptyMessage(SERVER_ERROR);
				return;
			}

			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("IsLogined", kczlApplication.IsLogined);
			editor.commit();
			try {
				finishAdviceGetOperation(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myHandler.sendMessage(message);
		}
	}

	private void finishAdviceGetOperation(String mStringReturnStr)// 不要写在UI线程上
			throws JSONException {

		if (!mStringReturnStr.startsWith("json"))
			return;
		mStringReturnStr = mStringReturnStr.substring(5);// 删掉json:
		int lastindex = mStringReturnStr.lastIndexOf("lastupdate");
		lastAdviceUpdate = mStringReturnStr.substring(lastindex + 11);// 取到lastuodate的值

		mStringReturnStr = mStringReturnStr.substring(0, lastindex);

		// mStringReturnStr =
		// "[{\"commentlist\":{\"list\":[]},\"coursename\":\"土木工程概论\",\"everlike\":\"false\",\"evldaily\":{\"advice\":\"测试用\",\"attendance\":1,\"ctid\":695627,\"date\":\"2013-10-24\",\"discipline\":1,\"edid\":793,\"effect\":5,\"schoolnumber\":\"1405110111\",\"speed\":3},\"likenum\":0}]";s

		JSONArray jsonObjs = new JSONArray(mStringReturnStr);
		JsonParse jp = new JsonParse();

		if (mAdviceIsStart)// 下拉刷新 重新加载
		{
			advicelist.clear();
			for (int m = 0; m < jsonObjs.length(); m++) {
				advicelist.add(jp.jsonToAdviceElement((JSONObject) jsonObjs
						.opt(m)));
			}
			kczlApplication.teacherlist.get(targetLocation).getAdvicelist()
			.clear();
			kczlApplication.teacherlist.get(targetLocation).getAdvicelist()
			.addAll(advicelist);
		} else {// 上拉加载更多
			if (jsonObjs.length() == 0) {
				myHandler.sendEmptyMessage(NO_MESSAGE);
			}
			for (int i = 0; i < jsonObjs.length(); i++) {
				advicelist.add(jp.jsonToAdviceElement((JSONObject) jsonObjs
						.opt(i)));
				kczlApplication.teacherlist.get(targetLocation).getAdvicelist()
				.clear();
				kczlApplication.teacherlist.get(targetLocation).getAdvicelist()
				.addAll(advicelist);
			}
		}
		if (advicelist.size() != 0)
			lastAdviceUpdate = advicelist.get(advicelist.size() - 1).getCaid();
		saveParams();
	}

	class SpitGetThread extends Thread {
		public void run() {

			Message message = new Message();
			message.what = SPIT_GET_FINISHED;
			teacherSpitService tss = new teacherSpitService();

			String msg;
			if (mSpitIsStart)
				msg = tss.get(targetCtid, "null");
			else
				msg = tss.get(targetCtid, lastSpitUpdate);
			// 如果msg没错误
			// 当上拉加载更多没有更多的时候，json:[] lastupdate:XXX
			// if (msg == "Error") {
			// myHandler.sendEmptyMessage(NO_NETWORK);
			// return;
			// }
			if (msg.startsWith("Error Response")) {
				myHandler.sendEmptyMessage(SERVER_ERROR);
				return;
			}

			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("IsLogined", kczlApplication.IsLogined);
			editor.commit();
			try {
				finishSpitGetOperation(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myHandler.sendMessage(message);
		}
	}

	private void finishSpitGetOperation(String mStringReturnStr)// 不要写在UI线程上
			throws JSONException {
		if (!mStringReturnStr.startsWith("json"))
			return;
		mStringReturnStr = mStringReturnStr.substring(5);// 删掉json:
		int lastindex = mStringReturnStr.lastIndexOf("lastupdate");
		lastSpitUpdate = mStringReturnStr.substring(lastindex + 11);// 取到lastuodate的值
		mStringReturnStr = mStringReturnStr.substring(0, lastindex);

		// mStringReturnStr =
		// "[{\"commentlist\":{\"list\":[]},\"coursename\":\"土木工程概论\",\"everlike\":\"false\",\"evldaily\":{\"advice\":\"测试用\",\"attendance\":1,\"ctid\":695627,\"date\":\"2013-10-24\",\"discipline\":1,\"edid\":793,\"effect\":5,\"schoolnumber\":\"1405110111\",\"speed\":3},\"likenum\":0}]";
		JSONArray jsonObjs = new JSONArray(mStringReturnStr);
		JsonParse jp = new JsonParse();

		if (mSpitIsStart)// 下拉刷新 重新加载
		{
			spitlist.clear();

			for (int m = 0; m < jsonObjs.length(); m++) {
				spitlist.add(jp.jsonToSpitElement((JSONObject) jsonObjs.opt(m)));
			}
			kczlApplication.teacherlist.get(targetLocation).getSpitlist()
			.clear();
			kczlApplication.teacherlist.get(targetLocation).getSpitlist()
			.addAll(spitlist);
		} else {// 上拉加载更多
			if (jsonObjs.length() == 0) {
				myHandler.sendEmptyMessage(NO_MESSAGE);
			}
			for (int i = 0; i < jsonObjs.length(); i++) {
				spitlist.add(jp.jsonToSpitElement((JSONObject) jsonObjs.opt(i)));
			}
			kczlApplication.teacherlist.get(targetLocation).getSpitlist()
			.clear();
			kczlApplication.teacherlist.get(targetLocation).getSpitlist()
			.addAll(spitlist);
		}
		if (spitlist.size() != 0)
			lastSpitUpdate = spitlist.get(spitlist.size() - 1).getSpitid();
		saveParams();
	}

	public class MyViewPagerAdapter extends PagerAdapter {

		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {

			Animation animation = new TranslateAnimation(tripleScreenWidth
					* currIndex, tripleScreenWidth * arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			strip.startAnimation(animation);

			switch (arg0) {
			case 0:
				SpitTextView
				.setTextColor(getResources().getColor(R.color.gray));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.gray));
				TcardTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				break;
			case 1:
				SpitTextView
				.setTextColor(getResources().getColor(R.color.gray));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				TcardTextView.setTextColor(getResources()
						.getColor(R.color.gray));
				break;
			case 2:
				SpitTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.gray));
				TcardTextView.setTextColor(getResources()
						.getColor(R.color.gray));
				break;

			default:
				break;
			}

		}
	}

	void saveParams() {
		JsonParse jp = new JsonParse();
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("TeacherListString",
				jp.teacherListToJSON(kczlApplication.teacherlist));
		editor.putString("lastTcardUpdate", lastTcardUpdate);
		editor.commit();
	}

	class TcardGetThread extends Thread {
		public void run() {
			String msg;
			Message message = new Message();
			TcardService ts = new TcardService();
			message.what = TCARD_GET_FINISHED;
			if (mTcardIsStart)
				msg = ts.get(targetCtid, "null");
			else
				msg = ts.get(targetCtid, lastTcardUpdate);
			try {
				finishTcardGetOperation(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myHandler.sendMessage(message);
		}
	}

	protected void finishTcardGetOperation(String mStringReturnStr)
			throws JSONException {

		if (!mStringReturnStr.startsWith("json"))
			return;
		mStringReturnStr = mStringReturnStr.substring(5);// 删掉json:
		int lastindex = mStringReturnStr.lastIndexOf("lastupdate");
		lastTcardUpdate = mStringReturnStr.substring(lastindex + 11);// 取到lastuodate的值

		mStringReturnStr = mStringReturnStr.substring(0, lastindex);

		JSONArray jsonObjs = new JSONArray(mStringReturnStr);
		JsonParse jp = new JsonParse();

		if (mTcardIsStart)// 下拉刷新 重新加载
		{
			tcardlist.clear();

			for (int m = 0; m < jsonObjs.length(); m++) {
				tcardlist.add(jp.jsonToTcardElement((JSONObject) jsonObjs.opt(m)));
			}
			kczlApplication.teacherlist.get(targetLocation).getTcardlist().clear();
			kczlApplication.teacherlist.get(targetLocation).getTcardlist().addAll(tcardlist);
		} else {// 上拉加载更多
			if (jsonObjs.length() == 0) {
				myHandler.sendEmptyMessage(NO_MESSAGE);
			}
			for (int i = 0; i < jsonObjs.length(); i++) {
				tcardlist.add(jp.jsonToTcardElement((JSONObject) jsonObjs.opt(i)));
			}
			kczlApplication.teacherlist.get(targetLocation).getTcardlist().clear();
			kczlApplication.teacherlist.get(targetLocation).getTcardlist().addAll(tcardlist);
		}
		TcardComparator tcardComparator = new TcardComparator();
		Collections.sort(tcardlist,tcardComparator);
		saveParams();
	}
	
	private class TcardComparator implements Comparator<TcardElement>{

		@Override
		public int compare(TcardElement lhs, TcardElement rhs) {
			// TODO Auto-generated method stub
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date datelhs = null;
			Date daterhs = null;
			try {
				datelhs = sf.parse(lhs.getDatestamp());
				daterhs = sf.parse(rhs.getDatestamp());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(datelhs.before(daterhs)) return 1;
			if(datelhs.after(daterhs)) return -1;
			return 0;
		}
	}
}
