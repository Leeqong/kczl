package com.njut.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.adviceService;
import org.nutlab.webService.spitService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.njut.R;
import com.njut.data.AdviceElement;
import com.njut.data.SpitElement;
import com.njut.pullrefresh.ui.PullToRefreshBase;
import com.njut.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.njut.pullrefresh.ui.PullToRefreshListView;
import com.njut.utility.AppUtils;
import com.njut.utility.JsonParse;
import com.njut.view.AdviceAdapter;
import com.njut.view.SpitAdapter;
import com.umeng.analytics.MobclickAgent;

public class HomepageActivity extends Activity {

	private Button backButton;
	private Button postButton;
	private ListView adviceListView;
	private ListView spitListView;
	private List<AdviceElement> advicelist;
	private List<SpitElement> spitlist;

	private AdviceAdapter adviceAdapter;
	private SpitAdapter spitAdapter;

	private LinearLayout adviceLine;
	private LinearLayout spitLine;
	private int index = 1;

	private int halfScreenWidth;

	// 下拉刷新
	private PullToRefreshListView advicePullListView;
	private PullToRefreshListView spitPullListView;
	// 标示滑动的方向，数据插在头部还是尾部
	private boolean mAdviceIsStart = true;
	private boolean mSpitIsStart = true;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	private String lastAdviceUpdate;// 保存listview中最后一个item的edid
	private String lastSpitUpdate;// 保存listview中最后一个item的edid
	// 为ViewPager准备
	private View spitView;
	private View adviceView;
	private ViewPager viewPager;// 页卡内容
	private ImageView stripLeft;// 动画长条（strip）
	private ImageView stripRight;// 动画长条（strip）
	private TextView AdviceTextView;
	private TextView SpitTextView;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

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
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
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
				progressDialog = ProgressDialog.show(new ContextThemeWrapper(
						HomepageActivity.this,
						android.R.style.Theme_Holo_Light_Dialog),
						getString(R.string.state), getString(R.string.liking),
						true);
				break;
			case LIKENUN_MINUS_BEGIN:
				progressDialog = ProgressDialog.show(new ContextThemeWrapper(
						HomepageActivity.this,
						android.R.style.Theme_Holo_Light_Dialog),
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
				if (progressDialog != null)
					progressDialog.dismiss();
				int position = msg.getData().getInt("position");
				int templike = spitlist.get(position).getLikenum();
				spitlist.get(position).setLikenum(templike + 1);
				spitlist.get(position).setEverlike("true");
				spitAdapter.notifyDataSetChanged();
			}
			break;
			case LIKENUM_MINUS_FINISHED: {
				if (progressDialog != null)
					progressDialog.dismiss();
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
				if (progressDialog != null)
					progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "请检查网络设置",
						Toast.LENGTH_SHORT).show();
				advicePullListView.onPullDownRefreshComplete();
				advicePullListView.onPullUpRefreshComplete();
				spitPullListView.onPullDownRefreshComplete();
				spitPullListView.onPullUpRefreshComplete();
			}
			break;
			case SERVER_ERROR: {
				Toast.makeText(getApplicationContext(), "服务器内部错误，请稍后重试",
						Toast.LENGTH_SHORT).show();
				advicePullListView.onPullDownRefreshComplete();
				advicePullListView.onPullUpRefreshComplete();
				spitPullListView.onPullDownRefreshComplete();
				spitPullListView.onPullUpRefreshComplete();
			}
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
		saveParams();
	}

	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.v("System.out", "HomePageActivity  onSaveInstanceState called");
	};

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.v("System.out", "HomePageActivity  onRestoreInstanceState called");
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == OPEN_SPIT_COMMENT) {
			spitlist.clear();
			spitlist.addAll(kczlApplication.SpitElements);
			spitAdapter.notifyDataSetChanged();
		}
		if (requestCode == OPEN_ADVICE_COMMENT) {
			advicelist.clear();
			advicelist.addAll(kczlApplication.AdviceElements);
			adviceAdapter.notifyDataSetChanged();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.homepage);

		InitImageView();// 长条动画
		InitPullListView();
		InitViewPager();

		backButton = (Button) findViewById(R.id.toolbar_nav_button);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.menu.showMenu();
			}
		});

		postButton = (Button) findViewById(R.id.toolbar_post_button);
		postButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(HomepageActivity.this,
//						PostActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//
//				startActivityForResult(intent, 123);// 如果发布的话回来要刷新
				//				ChooseCurrPopupWindow chooseCurrPopupWindow = new ChooseCurrPopupWindow(HomepageActivity.this);
				//				chooseCurrPopupWindow.showAsDropDown(v);
				
				ChooseCurrDialog dialog = new ChooseCurrDialog(HomepageActivity.this,currIndex);
				dialog.show();
			}
		});
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Boolean PullUDIntroKnown = settings.getBoolean("PullUDIntroKnown", false);
		if(!PullUDIntroKnown)
		{
			Intent intent = new Intent(HomepageActivity.this, PullUDIntroActivity.class);
			startActivity(intent);
		}
	}

	private void InitPullListView() {

		advicelist = new ArrayList<AdviceElement>();
		if (kczlApplication.AdviceElements.size() != 0) {
			for (int i = 0; i < kczlApplication.AdviceElements.size(); i++)
				advicelist.add(kczlApplication.AdviceElements.get(i));
			lastAdviceUpdate = advicelist.get(advicelist.size() - 1).getCaid();
		} else
			lastAdviceUpdate = "null";

		spitlist = new ArrayList<SpitElement>();
		if (kczlApplication.SpitElements.size() != 0) {
			for (int i = 0; i < kczlApplication.SpitElements.size(); i++)
				spitlist.add(kczlApplication.SpitElements.get(i));
			lastSpitUpdate = spitlist.get(spitlist.size() - 1).getSpitid();
		} else
			lastSpitUpdate = "null";
		Log.v("System.out", "MainActivit-spitlist.size:" + spitlist.size());

		adviceAdapter = new AdviceAdapter(this, advicelist, myHandler);

		LayoutInflater inflater = getLayoutInflater();
		spitView = inflater.inflate(R.layout.spitlist, null);
		adviceView = inflater.inflate(R.layout.advicelist, null);
		spitPullListView = (PullToRefreshListView) spitView
				.findViewById(R.id.spit_ListView);
		advicePullListView = (PullToRefreshListView) adviceView
				.findViewById(R.id.advice_ListView);
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

		if (spitlist.size() == 0) {
			spitPullListView.doPullRefreshing(true, 0);
		}
		if (advicelist.size() == 0) {
			advicePullListView.doPullRefreshing(true, 0);
		}

		advicePullListView
		.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!AppUtils.isOpenNetwork(HomepageActivity.this))
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
				if (!AppUtils.isOpenNetwork(HomepageActivity.this))
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
				if (!AppUtils.isOpenNetwork(HomepageActivity.this))
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
				if (!AppUtils.isOpenNetwork(HomepageActivity.this))
					myHandler.sendEmptyMessage(NO_NETWORK);
				else {
					mSpitIsStart = false;
					SpitGetThread SGT = new SpitGetThread();
					SGT.start();
				}
			}
		});
	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		views.add(adviceView);
		views.add(spitView);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		AdviceTextView = (TextView) findViewById(R.id.advice_TextView);
		SpitTextView = (TextView) findViewById(R.id.spit_TextView);
		// AdviceTextView在左边，SpitTextView在右边
		AdviceTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				currIndex = 0;
				viewPager.setCurrentItem(currIndex);
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				SpitTextView
				.setTextColor(getResources().getColor(R.color.gray));
			}
		});
		SpitTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				currIndex = 1;
				viewPager.setCurrentItem(currIndex);
				SpitTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.gray));
			}
		});
	}

	// 初始化长条动画
	private void InitImageView() {
		stripLeft = (ImageView) findViewById(R.id.stripLeft);
		stripRight= (ImageView) findViewById(R.id.stripRight);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.strip)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		halfScreenWidth = screenW / 2;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				halfScreenWidth, LayoutParams.WRAP_CONTENT);
		stripLeft.setLayoutParams(params);
		LinearLayout.LayoutParams paramsRight = new LinearLayout.LayoutParams(
				halfScreenWidth, LayoutParams.WRAP_CONTENT);
		paramsRight.setMargins(halfScreenWidth, 0, 0, 0);
		stripRight.setLayoutParams(paramsRight);
		stripRight.setVisibility(View.GONE);
		// Matrix matrix = new Matrix();
		// matrix.postTranslate(halfScreenWidth, 0);
		// stripLeft.setImageMatrix(matrix);// 设置动画初始位置
	}

	private void setAdviceLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		advicePullListView.setLastUpdatedLabel(text);
	}

	private void setSpitLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		spitPullListView.setLastUpdatedLabel(text);
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
			adviceService as = new adviceService();

			String msg;
			if (mAdviceIsStart)
				msg = as.get("null");
			else
				msg = as.get(lastAdviceUpdate);
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



		JSONArray jsonObjs = new JSONArray(mStringReturnStr);
		JsonParse jp = new JsonParse();

		if (mAdviceIsStart)// 下拉刷新 重新加载
		{
			advicelist.clear();

			for (int m = 0; m < jsonObjs.length(); m++) {
				advicelist.add(jp.jsonToAdviceElement((JSONObject) jsonObjs
						.opt(m)));
				kczlApplication.AdviceElements.clear();
				kczlApplication.AdviceElements.addAll(advicelist);

			}
		} else {// 上拉加载更多
			if (jsonObjs.length() == 0) {
				myHandler.sendEmptyMessage(NO_MESSAGE);
			}
			for (int i = 0; i < jsonObjs.length(); i++) {
				advicelist.add(jp.jsonToAdviceElement((JSONObject) jsonObjs
						.opt(i)));
			}
		}
		if (advicelist.size() != 0)
			lastAdviceUpdate = advicelist.get(advicelist.size() - 1).getCaid();
		
		// mStringReturnStr =
		// "[{\"commentlist\":{\"list\":[]},\"coursename\":\"土木工程概论\",\"everlike\":\"false\",\"evldaily\":{\"advice\":\"测试用\",\"attendance\":1,\"ctid\":695627,\"date\":\"2013-10-24\",\"discipline\":1,\"edid\":793,\"effect\":5,\"schoolnumber\":\"1405110111\",\"speed\":3},\"likenum\":0}]";
		if (mAdviceIsStart) {// sharedPreference里保存的是目前listview中最上面的20条，也就是最近下拉刷新返回的那个json
			kczlApplication.AdviceString = JsonParse.AdviceListToJson(kczlApplication.AdviceElements);
			saveParams();
		}
	}

	class SpitGetThread extends Thread {
		public void run() {

			Message message = new Message();
			message.what = SPIT_GET_FINISHED;
			spitService ss = new spitService();

			String msg;
			if (mSpitIsStart)
				msg = ss.get("null");
			else
				msg = ss.get(lastSpitUpdate);
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
				kczlApplication.SpitElements.clear();
				kczlApplication.SpitElements.addAll(spitlist);
			}
		} else {// 上拉加载更多
			if (jsonObjs.length() == 0) {
				myHandler.sendEmptyMessage(NO_MESSAGE);
			}
			for (int i = 0; i < jsonObjs.length(); i++) {
				spitlist.add(jp.jsonToSpitElement((JSONObject) jsonObjs.opt(i)));
			}
		}
		if (spitlist.size() != 0)
			lastSpitUpdate = spitlist.get(spitlist.size() - 1).getSpitid();
		
		if (mSpitIsStart) {// sharedPreference里保存的是目前listview中最上面的20条，也就是最近下拉刷新返回的那个json
			kczlApplication.SpitString = JsonParse.SpitListToJson(kczlApplication.SpitElements);
			saveParams();
		}
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

			currIndex = arg0;
			if (currIndex == 0)//from right to left
			{
				Animation animation = new TranslateAnimation(0,-halfScreenWidth, 0, 0);
				animation.setAnimationListener(new MyAnimationListener());
				animation.setFillAfter(false);// True:图片停在动画结束位置
				animation.setDuration(300);
				stripRight.startAnimation(animation);
			}
			if (currIndex == 1)//from left to right
			{
				Animation animation = new TranslateAnimation(0,halfScreenWidth, 0, 0);
				animation.setAnimationListener(new MyAnimationListener());
				animation.setFillAfter(false);// True:图片停在动画结束位置
				animation.setDuration(300);
				stripLeft.startAnimation(animation);
			}

			switch (arg0) {
			case 1:
				SpitTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.gray));
				break;
			case 0:
				SpitTextView
				.setTextColor(getResources().getColor(R.color.gray));
				AdviceTextView.setTextColor(getResources().getColor(
						R.color.toolbar));
				break;

			default:
				break;
			}

		}
	}

	class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if (currIndex == 0) {
				stripLeft.setVisibility(View.VISIBLE);
				stripRight.setVisibility(View.GONE);
			}
			if (currIndex == 1) {
				stripRight.setVisibility(View.VISIBLE);
				stripLeft.setVisibility(View.GONE);

			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
		}

	}

	void saveParams() {
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("adviceString", kczlApplication.AdviceString);
		editor.putString("spitString", kczlApplication.SpitString);
		editor.commit();
	}
}
