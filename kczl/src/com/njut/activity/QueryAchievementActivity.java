package com.njut.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.achievementService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.njut.R;
import com.njut.data.AchievementElement;
import com.njut.utility.AppUtils;
import com.njut.utility.GPACal;
import com.njut.utility.JsonParse;
import com.njut.utility.sortHelper;
import com.njut.view.AchievementAdapter;
import com.umeng.analytics.MobclickAgent;

/*��ѯ�ɼ�*/
public class QueryAchievementActivity extends Activity {
	private PopMenu popMenu;
	private Context context;
	private ListView achievementListView;
	private Button backButton;
	private Button okButton;
	private View abortButton;
	private ProgressDialog progressDialog;
	private String year;// ѧ��
	private String term;// ѧ��
	private TextView gpaTV1;
	private TextView gpaTV2;

	protected final int ACHIEVEMENT_GET_FINISHED = 1;
	private final int NO_NETWORK = 4;
	protected final String RETURN_STRING = "returnString";

	private String TAG = "QueryAchievementActivity";

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case ACHIEVEMENT_GET_FINISHED: {
				refreshAchievement();
			}
			break;
			case NO_NETWORK: {
				Toast.makeText(getApplicationContext(), "�����������ã�",
						Toast.LENGTH_SHORT).show();
			}
			break;
			}
			super.handleMessage(msg);
		}
	};
	private RelativeLayout tip;
	private List<AchievementElement> list;
	private AchievementAdapter adapter;

	class AchievementGetThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = ACHIEVEMENT_GET_FINISHED;
			achievementService as = new achievementService();

			String termStr;
			String msg;
			if (year.equals("����ѧ��")) {
				msg = as.get(kczlApplication.Person.getSchoolnumber());
			} else {
				if (term.equals("����ѧ��")) {
					msg = as.get(kczlApplication.Person.getSchoolnumber(), year);
				} else {
					if (term.equals("��һѧ��"))
						termStr = "1";
					else
						termStr = "2";
					msg = as.get(year, termStr, "",
							kczlApplication.Person.getSchoolnumber());
				}
			}
			Log.v("hahah", "��ɼ����ص���Ϣ" + msg);
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("IsLogined", kczlApplication.IsLogined);
			editor.commit();

			try {
				finishAchievementGetOperation(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myHandler.sendMessage(message);
		}
	}

	private void finishAchievementGetOperation(String mStringReturnStr)// ��Ҫд��UI�߳���
			throws JSONException {
		list.clear();
		kczlApplication.AchievementString = mStringReturnStr;
		JSONArray jsonObjs = new JSONArray(mStringReturnStr);
		JsonParse jp = new JsonParse();
		for (int i = 0; i < jsonObjs.length(); i++) {
			list.add(jp.jsonToAchievementElement((JSONObject) jsonObjs.opt(i)));
		}
		// ComparatorAchievementElement comparator = new
		// ComparatorAchievementElement();
		// Collections.sort(list, comparator);
		sortHelper sHelper = new sortHelper();
		list = sHelper.AchievementElementDESC(list);
		kczlApplication.AchievementElements = list;

	}

	private void refreshAchievement() {// ɾ���������
		adapter.notifyDataSetChanged();
		gpaTV1 = (TextView) findViewById(R.id.gpa1);
		gpaTV2 = (TextView) findViewById(R.id.gpa2);
		gpaTV1.setVisibility(View.VISIBLE);
		gpaTV2.setVisibility(View.VISIBLE);
		DecimalFormat df = new DecimalFormat("#0.00");
		GPACal gpaCal = new GPACal();
		double wholeGPA = gpaCal.getWholeGPA(list);
		double degreeGPA = gpaCal.getDegreeGPA(list);
		if (degreeGPA == -1000.0) {
			gpaTV1.setText("ȫ��GPA��" + df.format(wholeGPA));
			gpaTV2.setText("���ṩ2010���Ժ�ѧλGPA��ѯ��");
		} else {
			gpaTV1.setText("ȫ��GPA��" + df.format(wholeGPA));
			gpaTV2.setText("ѧλGPA��" + df.format(degreeGPA));
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {

		// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
		// Intent intent = new Intent(QueryAchievementActivity.this,
		// CourseEveryWeekActivity.class);
		// startActivityForResult(intent, 0);
		// // QueryAchievementActivity.this.finish();
		//
		// }
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.query_achievement);
		context = QueryAchievementActivity.this;
		popMenu = new PopMenu(context);
		// �˵�����������
		// popMenu.setOnItemClickListener(popmenuItemClickListener);
		tip = (RelativeLayout) findViewById(R.id.tip_RelativeLayout);
		final TextView button1 = (TextView) findViewById(R.id.term_textView);
		backButton = (Button) findViewById(R.id.toolbar_nav_button);
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (year.equals(""))
					year = "����ѧ��";
				if (term.equals(""))
					term = "����ѧ��";
				popMenu.showAsDropDown(button1);
				abortButton = (Button) popMenu.getView().findViewById(
						R.id.abort_popup_button);
				abortButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						popMenu.dismiss();
					}
				});
				okButton = (Button) popMenu.getView().findViewById(
						R.id.ok_popup_button);
				okButton.setOnClickListener(new View.OnClickListener() {// ȷ����ť����¼�������ѧ��ѧ�ڡ�������

					@Override
					public void onClick(View v) {
						if (AppUtils
								.isOpenNetwork(QueryAchievementActivity.this)) {
							if (tip.getVisibility() == View.VISIBLE)
								tip.setVisibility(View.GONE);
							AchievementGetThread AGT = new AchievementGetThread();
							progressDialog = ProgressDialog.show(
									new ContextThemeWrapper(QueryAchievementActivity.this,
											android.R.style.Theme_Holo_Light_Dialog)
									,
									null,
									getString(R.string.loading), true);
							AGT.start();
							Log.v("hahah", "ѧ��" + year);
							Log.v("hahah", "ѧ��" + term);
							popMenu.dismiss();
						} else
							myHandler.sendEmptyMessage(NO_NETWORK);
					}
				});
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.menu.showMenu();

			}
		});
		achievementListView = (ListView) findViewById(R.id.achievement_ListView);
		list = new ArrayList<AchievementElement>();
		// list = kczlApplication.AchievementElements;
		/*
		 * list.add(new AchievementElement("΢��ԭ����ӿڼ���", 63, "���޿�", 4));//�ɼ����ݰ�
		 * list.add(new AchievementElement("רҵӢ��", 86, "���޿�", 2)); list.add(new
		 * AchievementElement("�㷨��������", 70, "���޿�", 4)); list.add(new
		 * AchievementElement("���ݽṹ", 98, "���޿�", 2));
		 */

		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String Year = settings.getString("Year", "");
		String Term = settings.getString("Term", "");
		adapter = new AchievementAdapter(this, list);
		achievementListView.setAdapter(adapter);
		// if (Year.length() > 1 && Term.length() > 1) {
		// year = Year;
		// term = Term;
		// if (AppUtils.isOpenNetwork(QueryAchievementActivity.this)) {
		// AchievementGetThread AGT = new AchievementGetThread();
		// progressDialog = ProgressDialog.show(
		// QueryAchievementActivity.this,
		// getString(R.string.state), getString(R.string.loading),
		// true);
		// AGT.start();
		// }
		// else
		// myHandler.sendEmptyMessage(NO_NETWORK);
		// }
//		achievementListView.setOnTouchListener(new OnTouchListener() {
//			int pointX, pointY, endX;
//			int position;
//
//			@Override
//			public boolean onTouch(View arg0, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					// ��ָ����,���㽹��λ��ListView���Ǹ���Ŀ
//					pointX = (int) event.getX();
//					pointY = (int) event.getY();
//					// ��ע1
//					position = achievementListView.pointToPosition(pointX,
//							pointY);
//					break;
//				case MotionEvent.ACTION_MOVE:
//					break;
//				case MotionEvent.ACTION_UP:
//					endX = (int) event.getX();
//					if (endX - pointX > 300) {
//						// ��ȡ��ListView��һ���ɼ���Ŀ��position
//						int firstVisiblePosition = achievementListView
//								.getFirstVisiblePosition();
//						// ɾ����Ŀ�Ķ���
//						Animation animation = AnimationUtils
//								.loadAnimation(QueryAchievementActivity.this,
//										R.anim.slide_del);
//						animation.setAnimationListener(new AnimationListener() {
//							@Override
//							public void onAnimationStart(Animation arg0) {
//								// TODO Auto-generated method stub
//							}
//
//							@Override
//							public void onAnimationRepeat(Animation arg0) {
//								// TODO Auto-generated method stub
//							}
//
//							@Override
//							public void onAnimationEnd(Animation arg0) {
//								// TODO Auto-generated method stub
//								try {
//									list.remove(position);
//								} catch (ArrayIndexOutOfBoundsException e) {
//									Log.i("MainActivity",
//											"��������Խ���쳣��    position = "
//													+ position);// --------------��ע4
//								}
//								// adapter.notifyDataSetChanged();
//								refreshAchievement();
//							}
//						});
//						// --------------��ע2
//						View view = achievementListView.getChildAt(position
//								- firstVisiblePosition);
//						try {
//							view.startAnimation(animation);
//						} catch (NullPointerException e) {
//							Log.i("QueryAchievementActivity", "������ָ���쳣��");// --------------��ע3
//						}
//					}
//					break;
//				default:
//					break;
//				}
//				return false;
//			}
//		});
	}

	// �����˵�������
	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// System.out.println("�����˵����" + position);
			Toast.makeText(view.getContext(), "press--->>", Toast.LENGTH_LONG)
			.show();
			popMenu.dismiss();
		}
	};

	public void setYear(String year) {
		this.year = year;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
