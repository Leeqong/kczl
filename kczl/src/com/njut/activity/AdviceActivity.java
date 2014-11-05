package com.njut.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.evaluationService;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class AdviceActivity extends Activity {
	private Button backButton;
	private Button okButton;
	protected final int EVALUATE_FINISHED = 1;
	private ProgressDialog progressDialog;
	private String TAG = "AdviceAci";
	protected final String RETURN_STRING = "returnString";

	// 学习效果
	private String effect;
	// 教学安排编号id
	private String arrangementId;
	// 到课情况
	private String attendance;
	// 教学进度
	private String speed;
	// 其他建议
	private String content;
	private String schoolnumber;
	private String starttime;
	private String endtime;
	private String ctid;


	private String learningEffect[];

	private TextView learningEffectText;
	private RatingBar learningEffectRatingGreen;
	private RatingBar learningEffectRatingRed;

	RadioGroup attendacerRadioGroup;
	RadioButton attendancerRadioButton;
	RadioGroup speedRadioGroup;
	RadioButton speedRadioButton;
	RadioGroup arrangementRadioGroup;
	RadioButton arrangementRadioButton;
	EditText adviceEditText;

	private TextView courseNameTextView;
	private TextView courseNatureTextView;
	private TextView crediTextView;

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

	class EvaluateThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = EVALUATE_FINISHED;
			evaluationService es = new evaluationService();
			String msg = es.upload(schoolnumber, effect, arrangementId,
					attendance, speed, content, ctid, starttime, endtime);
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
			if(tmp.equals("succeed :)"))
				tmp = "发布成功";
			new AlertDialog.Builder(new ContextThemeWrapper(AdviceActivity.this,
					android.R.style.Theme_Holo_Light_Dialog))
					.setTitle(null)
					.setMessage(tmp)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											adviceEditText.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);

									AdviceActivity.this.finish();
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
		Bundle bundle = getIntent().getExtras();
		ctid = bundle.getString("ctid");
		endtime = bundle.getString("endtime");
		starttime = bundle.getString("starttime");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.advice_evaluation);
		learningEffect = getResources().getStringArray(
				R.array.learning_effect_array);

		learningEffectText = (TextView) findViewById(R.id.learning_effect_textView);
		learningEffectRatingGreen = (RatingBar) findViewById(R.id.ratingbarGreen);
		learningEffectRatingRed = (RatingBar) findViewById(R.id.ratingbarRed);
		arrangementRadioGroup = (RadioGroup) findViewById(R.id.tearching_arrangement_radioGroup);
		attendacerRadioGroup = (RadioGroup) findViewById(R.id.attendance_radioGroup);
		speedRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		backButton = (Button) findViewById(R.id.abort_button);
		adviceEditText = (EditText) findViewById(R.id.evaluation_EditText);
		okButton = (Button) findViewById(R.id.ok_button);
		schoolnumber = kczlApplication.Person.getSchoolnumber();

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(adviceEditText.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				AdviceActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  
			}
		});
		okButton.setOnClickListener(new View.OnClickListener() {// 完成按钮点击事件
			@Override
			public void onClick(View v) {
				// eva.setCtid(ctid);
				if (!AppUtils.isOpenNetwork(AdviceActivity.this)) {
					Toast.makeText(AdviceActivity.this, "请检查网络设置！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				int arrangementResourceId = arrangementRadioGroup
						.getCheckedRadioButtonId();

				if (arrangementResourceId == -1) {
					Toast.makeText(AdviceActivity.this, "教学安排不能为空！",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					arrangementRadioButton = (RadioButton) findViewById(arrangementResourceId);
					String arrangement = arrangementRadioButton.getText()
							.toString();
					// eva.setDiscipline(disciplineRadioButton.getText().toString());

					if (arrangement.equals("正常")) {
						arrangementId = "1";
						int attendanceId = attendacerRadioGroup
								.getCheckedRadioButtonId();
						if (attendanceId == -1) {
							Toast.makeText(AdviceActivity.this, "到课率评价不能为空",
									Toast.LENGTH_SHORT).show();
							return;
						} else {
							attendancerRadioButton = (RadioButton) findViewById(attendanceId);
							// eva.setAttendance(attendancerRadioButton.getText().toString());
							attendance = attendancerRadioButton.getText()
									.toString();
							if (attendance.equals("<50%"))
								attendance = "1";
							else {
								if (attendance.equals("≈70%"))
									attendance = "3";
								else if (attendance.equals(">90%"))
									attendance = "5";
							}
						}

						int speedId = speedRadioGroup.getCheckedRadioButtonId();
						if (speedId == -1) {
							Toast.makeText(AdviceActivity.this, "教学进度评价不能为空",
									Toast.LENGTH_SHORT).show();
							return;
						} else {
							speedRadioButton = (RadioButton) findViewById(speedId);
							// eva.setSpeed(speedRadioButton.getText().toString());
							speed = speedRadioButton.getText().toString();
							if (speed.equals("较慢"))
								speed = "1";
							else {
								if (speed.equals("适中"))
									speed = "3";
								else if (speed.equals("较快"))
									speed = "5";
							}
						}
						effect = String.valueOf((int) learningEffectRatingGreen
								.getRating());

						content = adviceEditText.getText().toString();
//						if (content.equals("")) {
//							Toast.makeText(AdviceActivity.this,
//									"正常上课时"+getResources().getString(R.string.toevaluate)+"不能为空", Toast.LENGTH_SHORT)
//									.show();
//							return;
						if (effect.equals("0")) {
							Toast.makeText(AdviceActivity.this, "学习效果评价不能为空",
									Toast.LENGTH_SHORT).show();
							return;
						} else {
							EvaluateThread ET = new EvaluateThread();
							progressDialog = ProgressDialog.show(
									AdviceActivity.this,
									getString(R.string.state),
									getString(R.string.uploading), true);
							ET.start();
						}
					} else {
						if (arrangement.equals("调课")
								|| arrangement.equals("停课")) {
							if (arrangement.equals("调课"))
								arrangementId = "3";
							else
								arrangementId = "5";
							speed = "0";
							attendance = "0";
							content = "";
							effect = "0";
							EvaluateThread ET = new EvaluateThread();
							progressDialog = ProgressDialog.show(
									AdviceActivity.this,
									getString(R.string.state),
									getString(R.string.uploading), true);
							ET.start();
						}
					}
				}
			}
		});
		learningEffectRatingGreen
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						// doing actions
						int rate = (int) rating;
						if (rate != 0)
							learningEffectText
									.setText(learningEffect[rate - 1]);
						else
							learningEffectText.setText("未点评");
						switch (rate) {
						case 0:
							adviceEditText
									.setBackgroundResource(R.drawable.advice_textfield_gray);
							break;
						case 1:
						case 2:
							adviceEditText
									.setBackgroundResource(R.drawable.advice_textfield_red);
							learningEffectRatingGreen
									.setVisibility(View.INVISIBLE);
							learningEffectRatingRed.setVisibility(View.VISIBLE);
							learningEffectRatingRed.setRating(rating);
							break;
						case 3:
						case 4:
						case 5:
							adviceEditText
									.setBackgroundResource(R.drawable.advice_textfield_green);
							learningEffectRatingGreen
									.setVisibility(View.VISIBLE);
							learningEffectRatingRed
									.setVisibility(View.INVISIBLE);
							learningEffectRatingGreen.setRating(rating);
							learningEffectRatingRed.setRating(rating);
							break;

						}
					}
				});
		learningEffectRatingRed
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						// doing actions
						int rate = (int) rating;
						if (rate != 0)
							learningEffectText
									.setText(learningEffect[rate - 1]);
						else
							learningEffectText.setText("未点评");
						switch (rate) {
						case 1:
						case 2:
							adviceEditText
									.setBackgroundResource(R.drawable.advice_textfield_red);
							learningEffectRatingGreen
									.setVisibility(View.INVISIBLE);
							learningEffectRatingRed.setVisibility(View.VISIBLE);
							learningEffectRatingRed.setRating(rating);
							break;
						case 3:
						case 4:
						case 5:
							adviceEditText
									.setBackgroundResource(R.drawable.advice_textfield_green);
							learningEffectRatingGreen
									.setVisibility(View.VISIBLE);
							learningEffectRatingRed
									.setVisibility(View.INVISIBLE);
							learningEffectRatingGreen.setRating(rating);
							learningEffectRatingRed.setRating(rating);
							break;

						}
					}
				});
		arrangementRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						int rgdisabled[] = {
								R.drawable.evaluation_option_button_left_disabled,
								R.drawable.evaluation_option_button_middle_disabled,
								R.drawable.evaluation_option_button_right_disabled };
						int rgnormal[] = {
								R.drawable.evaluation_option_button_left,
								R.drawable.evaluation_option_button_middle,
								R.drawable.evaluation_option_button_right };

						if (checkedId != R.id.teaching_arrangement1) {
							for (int i = 0; i < 3; i++) {
								attendacerRadioGroup.getChildAt(i).setEnabled(
										false);
								attendacerRadioGroup.getChildAt(i)
										.setBackgroundResource(rgdisabled[i]);

								speedRadioGroup.getChildAt(i).setEnabled(false);
								speedRadioGroup.getChildAt(i)
										.setBackgroundResource(rgdisabled[i]);// ！需要一张disabled图片
							}
							learningEffectRatingGreen.setEnabled(false);

							adviceEditText.setEnabled(false);
						} else {
							for (int i = 0; i < 3; i++) {
								attendacerRadioGroup.getChildAt(i).setEnabled(
										true);
								speedRadioGroup.getChildAt(i).setEnabled(true);
								attendacerRadioGroup.getChildAt(i)
										.setBackgroundResource(rgnormal[i]);
								speedRadioGroup.getChildAt(i)
										.setBackgroundResource(rgnormal[i]);
							}
							learningEffectRatingGreen.setEnabled(true);
							adviceEditText.setEnabled(true);
						}
					}
				});

	}
}
