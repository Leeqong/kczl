package com.njut.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.njut.R;
import com.njut.data.CourseTeacherElement;
import com.njut.utility.JsonParse;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

public class CurriculumTabActivity extends TabActivity{

	private TabHost tabHost;
	
	private View view1;
	private View view2;
	private View view3;
	private Boolean isExit = false;
	private Boolean hasTask = false;
	Timer tExit = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isExit = false;
			hasTask = true;
		}
	};
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
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				if (!hasTask) {
					tExit.schedule(task, 2000);//
				}
			} else {
				for (Iterator it = kczlApplication.activitiesList.iterator(); it
						.hasNext();) {
					Activity mActivity = (Activity) it.next();
					if (mActivity != null) {
						mActivity.finish();
					}
				}
			}
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null != savedInstanceState) {
			JsonParse jp = new JsonParse();
			kczlApplication.UserName = savedInstanceState
					.getString("UserName");
			kczlApplication.PassWord = savedInstanceState
					.getString("PassWord");
			kczlApplication.ServerUri = savedInstanceState
					.getString("ServerUri");
			kczlApplication.IsLogined = savedInstanceState
					.getInt("IsLogined");
			kczlApplication.isClocked = savedInstanceState
					.getInt("isClocked");
			kczlApplication.isNotification = savedInstanceState
					.getInt("isNotification");
			kczlApplication.IsOffLine = savedInstanceState
					.getInt("IsOffLine");
			kczlApplication.IsStudent = savedInstanceState
					.getInt("IsStudent");
			kczlApplication.CurriculumtString = savedInstanceState.getString("CurriculumtString");
			String teacherliststring = savedInstanceState.getString("TeacherListString");
			kczlApplication.PersonTeacherString = savedInstanceState.getString("PersonTeacherString");
			JSONArray teacherArray;
			try {
				teacherArray = new JSONArray(teacherliststring);
				kczlApplication.teacherlist.clear();
				for (int i = 0; i < teacherArray.length(); i++) {
					kczlApplication.teacherlist.add(jp.jsonToTeacherListElemet((JSONObject)teacherArray.opt(i)));
				}
				kczlApplication.PersonTeacher = jp.jsonToPersonTeacherElement(kczlApplication.PersonTeacherString);
				JSONArray jsonObjs = new JSONArray(kczlApplication.CurriculumtString);
				List<CourseTeacherElement> curriculumt = new ArrayList<CourseTeacherElement>();
				for (int i = 0; i < jsonObjs.length(); i++) {
					curriculumt.add(jp.jsonToCourseTeacherElement((JSONObject) jsonObjs.opt(i)));
				}
				kczlApplication.CourseTeacherElements.clear();
				kczlApplication.CourseTeacherElements.addAll(curriculumt);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		XGPushConfig.enableDebug(this, true);
//        String temp = kczlApplication.PersonTeacher.getPersonnelnumber();
//        String teachernumber = temp.substring(temp.length()-4, temp.length());
		XGPushManager.registerPush(this,kczlApplication.PersonTeacher.getTeachernumber());
		XGPushManager.setTag(CurriculumTabActivity.this, "teacher");
		String token = XGPushConfig.getToken(CurriculumTabActivity.this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tabcurriculum);
		kczlApplication.activitiesList.add(this);
		tabHost = this.getTabHost();
		Intent intent = new Intent(this, TeacherCurriculumActivity.class);
		Intent intent1 = new Intent(this, TeacherMessageActivity.class);
		Intent intent2 = new Intent(this, TeacherConfigActivity.class);
		
		view1 = View.inflate(CurriculumTabActivity.this, R.layout.tabteachercontent, null);
		final ImageView image1 = (ImageView)view1.findViewById(R.id.tab_star_icon);
		final TextView tab1 = (TextView) view1.findViewById(R.id.tab_star_TextView);
		tab1.setText(R.string.get_evaluation);
		image1.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_curriculum_tab1));
		TabHost.TabSpec spec1 = tabHost.newTabSpec(
				getResources().getString(R.string.get_evaluation))
				.setIndicator(view1)
				.setContent(intent);
		tabHost.addTab(spec1);
		
		view2 = View.inflate(CurriculumTabActivity.this, R.layout.tabteachercontent, null);
		final ImageView image2 = (ImageView)view2.findViewById(R.id.tab_star_icon);
		final TextView tab2 = (TextView) view2.findViewById(R.id.tab_star_TextView);
		tab2.setText(R.string.message);
		image2.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_curriculum_tab2));
		
		TabHost.TabSpec spec2 = tabHost.newTabSpec(
				getResources().getString(R.string.message))
				.setIndicator(view2)
				.setContent(intent1);
		tabHost.addTab(spec2);
		
		view3 = View.inflate(CurriculumTabActivity.this, R.layout.tabteachercontent, null);
		final ImageView image3 = (ImageView)view3.findViewById(R.id.tab_star_icon);
		final TextView tab3 = (TextView) view3.findViewById(R.id.tab_star_TextView);
		image3.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_curriculum_tab3));
		tab3.setText(R.string.config);
		TabHost.TabSpec spec3 = tabHost.newTabSpec(
				getResources().getString(R.string.config))
				.setIndicator(view3)
				.setContent(intent2);
		tabHost.addTab(spec3);
		String isFromXgMessage = getIntent().getStringExtra("isFromXgMessage");
		if(isFromXgMessage!=null && isFromXgMessage.equals("true"))
		{
			tabHost.setCurrentTabByTag(getResources().getString(R.string.message));
		}
	
	}
}
