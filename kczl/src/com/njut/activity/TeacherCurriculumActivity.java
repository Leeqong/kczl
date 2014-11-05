package com.njut.activity;

import java.util.ArrayList;
import java.util.List;

import org.nutlab.kczl.kczlApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.njut.R;
import com.njut.data.CourseTeacherElement;
import com.njut.utility.ClassCoverClass;
import com.njut.view.CurriculumAdapter;

public class TeacherCurriculumActivity extends Activity{

	private String TAG = "CurriculumActivity";

	private ListView curriculumListView; 
	private List<CourseTeacherElement> list;	
	private CurriculumAdapter CurriculumAdapter;
	private TextView title_tv;

	protected final int CURRICULUM_TEACHER_GET_FINISHED = 3;

	protected final String RETURN_STRING = "returnString";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.teacher_curriculum);
		kczlApplication.activitiesList.add(this);
		ClassCoverClass c2c = new ClassCoverClass();
		list = new ArrayList<CourseTeacherElement>();
		list.addAll(kczlApplication.CourseTeacherElements);
		CurriculumAdapter = new CurriculumAdapter(this, list);
		title_tv = (TextView)findViewById(R.id.title_tv);
		curriculumListView = (ListView) findViewById(R.id.curriculum_ListView);
		curriculumListView.setAdapter(CurriculumAdapter);
		curriculumListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id ) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TeacherCurriculumActivity.this,TeacherHomepageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("ctid", list.get(position).getCtid());
				bundle.putString("coursename", list.get(position).getCoursename());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		int year = Integer.parseInt(kczlApplication.PersonTeacher.getBegindate()
				.split("-")[0]);
		int month = Integer.parseInt(kczlApplication.PersonTeacher.getBegindate()
				.split("-")[1]);
		if (month > 7) {
			kczlApplication.Year = year + "-" + (year + 1);
			kczlApplication.Term = "第一学期";
		} else {
			kczlApplication.Year = (year - 1) + "-" + year;
			kczlApplication.Term = "第二学期";
		}

		title_tv.setText(kczlApplication.Year+ "学年 " + kczlApplication.Term);
	}
	//	//2014-02-24
	//	public static String getTermByBeginDate(String begindate) {
	//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
	//		try {
	//			Date beginDate = sdf.parse(begindate);
	//			int beginMonth = beginDate.getMonth();
	//			
	//		} catch (ParseException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		
	//		
	//
	//	}
	
	
}
