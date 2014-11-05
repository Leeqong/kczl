package com.njut.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.summaryService;

import com.njut.R;
import com.njut.utility.AppUtils;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SpitActivity extends Activity {
	private Button backButton;
	private Button okButton;
	protected final int EVALUATE_FINISHED = 1;
	private ProgressDialog progressDialog;
	private String TAG = "SpitActivity";
	protected final String RETURN_STRING = "returnString";

	private String courseName;
	private String courseNature;
	private String schoolnumber;
	private String starttime;
	private String endtime;
	private String credit;
	private String ctid;

	private String summaryString;
	private EditText summaryEditText;

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case EVALUATE_FINISHED: {
				Bundle bundle = msg.getData();
				try {
					finishEvaluateOperation(bundle.getString(RETURN_STRING));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
				break;
			}
			super.handleMessage(msg);
		}
	};

	class SummaryThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = EVALUATE_FINISHED;
			summaryService ss = new summaryService();
			String msg = ss.upload(schoolnumber, ctid, summaryString,
					starttime, endtime);
			String PREFS_NAME = "org.nutlab.kczl";
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("IsLogined", kczlApplication.IsLogined);
			editor.commit();
			Bundle bundle = new Bundle();
			bundle.putString(RETURN_STRING, msg);
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	protected void finishEvaluateOperation(String mStringReturnStr) {
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(mStringReturnStr);
			String tmp = jsonObj.getString("msg");
			if(tmp.equals("out of coursetime scope"))
				tmp = "亲，现在不能反馈哦~";
			if(tmp.equals("succeed"))
				tmp = "发布成功";
			new AlertDialog.Builder(new ContextThemeWrapper(SpitActivity.this,
					android.R.style.Theme_Holo_Light_Dialog))
					.setTitle(null)
					.setMessage(tmp)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(summaryEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

									SpitActivity.this.finish();
									overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  
								}
							}).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.spit_evaluation);
		Bundle bundle = getIntent().getExtras();
		courseName = bundle.getString("coursename");
		courseNature = bundle.getString("courseNature");
		credit = bundle.getString("credit");
		ctid = bundle.getString("ctid");
		endtime = bundle.getString("endtime");
		starttime = bundle.getString("starttime");
		summaryEditText = (EditText) findViewById(R.id.summary_EditText);
		schoolnumber = kczlApplication.Person.getSchoolnumber();
		backButton = (Button) findViewById(R.id.abort_button);
		okButton = (Button) findViewById(R.id.ok_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(summaryEditText.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				SpitActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  
			}
		});
		okButton.setOnClickListener(new View.OnClickListener() {// 完成按钮点击事件
			@Override
			public void onClick(View v) {
				summaryString = summaryEditText.getText().toString().trim();
				if (summaryString.equals(""))
					Toast.makeText(SpitActivity.this, "说一说内容不能为空",
							Toast.LENGTH_SHORT).show();
				else {
					if (AppUtils.isOpenNetwork(SpitActivity.this)) {
					SummaryThread ST = new SummaryThread();
					progressDialog = ProgressDialog.show(new ContextThemeWrapper(SpitActivity.this,
							android.R.style.Theme_Holo_Light_Dialog),
							getString(R.string.state),
							getString(R.string.uploading), true);
					ST.start();
				}
					else
					{
						Toast.makeText(SpitActivity.this, "请检查网络设置！",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

	}
}
