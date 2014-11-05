package com.njut.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.njut.R;
import com.njut.activity.HomepageActivity.MyAnimationListener;
import com.njut.data.AboutElement;
import com.njut.widget.InfinitePagerAdapter;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends Activity {
	private Button backButton;
	TextView tv;
	private List<AboutElement> aboutlist;
	private ViewPager viewpager;
	private int currIndex;


	/** Called when the activity is first created. */

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		backButton = (Button) findViewById(R.id.abort_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
				overridePendingTransition(R.anim. slide_in_left , R.anim. slide_out_right);  
			}
		});
		viewpager = (ViewPager) findViewById(R.id.vPager);

		AboutElement dhw = new AboutElement("杜宏伟","技术总监","本科生 2011级 ","计算机科学与技术(软件班)",R.drawable.mlab_dhw);
		AboutElement cx = new AboutElement("承骁","产品总监","研究生2013级 ","信息院研究生",R.drawable.mlab_cx);
		AboutElement gp = new AboutElement("高鹏","Android项目总监","本科生 2011级 ","计算机科学与技术(软件班)",R.drawable.mlab_gp);
		AboutElement lxl = new AboutElement("李雪瓅","UI设计师","本科生 2011级 ","计算机科学与技术(软件班)",R.drawable.mlab_lxl);
		AboutElement gc = new AboutElement("高超","Android工程师","本科生 2011级 ","计算机科学与技术(软件班)",R.drawable.mlab_gc);
		AboutElement zcy = new AboutElement("张春元","数据分析师","本科生 2011级 ","水质科学与技术",R.drawable.mlab_zcy);
		aboutlist = new ArrayList<AboutElement>();
		aboutlist.add(dhw);
		aboutlist.add(cx);
		aboutlist.add(gp);
		aboutlist.add(lxl);
		aboutlist.add(gc);
		aboutlist.add(zcy);
		MyPagerAdapter myPagerAdapter = new MyPagerAdapter();
		viewpager.setAdapter(new InfinitePagerAdapter(myPagerAdapter));
		viewpager.setPageTransformer(true, new ZoomOutPageTransformer());
		String PREFS_NAME = "org.nutlab.kczl";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Boolean ViewpagerIntroKnown = settings.getBoolean("ViewpagerIntroKnown", false);
		if(!ViewpagerIntroKnown)
		{
			Intent intent = new Intent(AboutActivity.this, SwipeLRIntroActivity.class);
			startActivity(intent);
		}
	}

	public class MyPagerAdapter extends PagerAdapter {

		public MyPagerAdapter() {
			super();
			mViewList = new ArrayList<View>();
			for(int i =0; i < 6; i++)
			{
				View view = getLayoutInflater().inflate(R.layout.about_item, null);
				mViewList.add(view);
			}
		}

		private List<View> mViewList;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return aboutlist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViewList.get(position));

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = mViewList.get(position);
			if(view == null)
				view = getLayoutInflater().inflate(R.layout.about_item, null);
			TextView name_tv = (TextView) view.findViewById(R.id.name_tv);
			TextView job_tv = (TextView) view.findViewById(R.id.job_tv);
			TextView grade_tv = (TextView) view.findViewById(R.id.grade_tv);
			TextView class_tv = (TextView) view.findViewById(R.id.class_tv);
			ImageView portrait_iv = (ImageView) view.findViewById(R.id.portrait_iv);
			AboutElement aboutElement = aboutlist.get(position);
			name_tv.setText(aboutElement.getName());
			job_tv.setText(aboutElement.getJob());
			grade_tv.setText(aboutElement.getGrade());
			class_tv.setText(aboutElement.getClassName());
			portrait_iv.setImageResource(aboutElement.getImageID());
			container.addView(view);
			return view;
		}

	}

	public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.75f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA +
						(scaleFactor - MIN_SCALE) /
						(1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {

			currIndex = arg0;

		}
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch(keyCode)  
//		{  
//		case KeyEvent.KEYCODE_VOLUME_DOWN:  
//			viewpager.setCurrentItem((currIndex-1)%6);
//			return true; 
//
//		case KeyEvent.KEYCODE_VOLUME_UP:  
//			viewpager.setCurrentItem((currIndex+1)%6);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event); 
//	}


}