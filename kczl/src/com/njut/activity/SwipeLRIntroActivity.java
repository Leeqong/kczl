package com.njut.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.njut.R;

public class SwipeLRIntroActivity extends Activity {
	
	private Button confirm_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewpager_intro);
		confirm_btn = (Button) findViewById(R.id.confirm_btn);
		confirm_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String PREFS_NAME = "org.nutlab.kczl";
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("ViewpagerIntroKnown", true);
				editor.commit();
				finish();
			}
		});
	}
}
