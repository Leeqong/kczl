package com.njut.activity;

import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.changePasswordService;
import org.nutlab.webService.feedBackService;

import com.njut.R;
import com.njut.activity.ChangePasswordActivity.ChangePasswordThread;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FeedBackActivity extends Activity {
	private Button backButton;
	private Button submitButton;

	private EditText et;

	private String content;

	private ProgressDialog progressDialog;
	private String TAG = "FeedBackActivity";
	protected final int FB_FINISHED = 1;
	protected final String RETURN_STRING = "returnString";

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case FB_FINISHED: {
				Bundle bundle = msg.getData();
				try {
					finishFBOperation(bundle.getString(RETURN_STRING));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
				break;
			}
			super.handleMessage(msg);
		}
	};

	class FeedBackThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = FB_FINISHED;
			feedBackService fbs = new feedBackService();
			String from = "";
			if(kczlApplication.IsStudent == 1)
				from = kczlApplication.Person.getSchoolnumber();
			else if(kczlApplication.IsStudent ==2)
				from = kczlApplication.PersonTeacher.getTeachernumber();
			String msg = fbs.upload(from,
					"[Android]"+content);
			Bundle bundle = new Bundle();
			bundle.putString(RETURN_STRING, msg);
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	protected void finishFBOperation(String mStringReturnStr) {

		if (mStringReturnStr.equals("{\"msg\":\"feedback succeed\"}")) {
			new AlertDialog.Builder(this)
					.setTitle("反馈意见")
					.setMessage("反馈意见成功！")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

									imm.hideSoftInputFromWindow(et.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
									FeedBackActivity.this.finish();
									overridePendingTransition(R.anim. slide_in_left , R.anim. slide_out_right);  
								}
							}).show();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("反馈意见")
					.setMessage("反馈意见失败！")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									et = (EditText) findViewById(R.id.feedback_notice_EditText);
									et.setText("");
								}
							}).show();

		}

	}

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
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 
		setContentView(R.layout.feedback);
		backButton = (Button) findViewById(R.id.abort_button);
		submitButton = (Button) findViewById(R.id.submit_button);
		et = (EditText) findViewById(R.id.feedback_notice_EditText);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.hideSoftInputFromWindow(et.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				FeedBackActivity.this.finish();
				overridePendingTransition(R.anim. slide_in_left , R.anim. slide_out_right);  
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener() {// 完成按钮点击事件

					@Override
					public void onClick(View v) {
						content = et.getText().toString().trim();
						if (content.length() > 0) {
							FeedBackThread fbt = new FeedBackThread();
							progressDialog = ProgressDialog.show(
									FeedBackActivity.this,
									getString(R.string.state),
									getString(R.string.uploading), true);
							fbt.start();
						} else {
							new AlertDialog.Builder(FeedBackActivity.this)
									.setTitle("反馈意见").setMessage("反馈意见不能为空！")
									.setPositiveButton("是", null).show();
						}
					}
				});

	}

}