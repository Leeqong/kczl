package com.njut.activity;

import org.nutlab.kczl.kczlApplication;
import org.nutlab.webService.changePasswordService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.njut.R;
import com.umeng.analytics.MobclickAgent;

public class ChangePasswordActivity extends Activity {
	private Button backButton;
	private Button submitButton;

	private TextView oldPwd;
	private TextView newPwd;
	private TextView newPwdAgain;

	private String originalPassWord;
	private String newPassWord1;
	private String newPassWord2;

	private ProgressDialog progressDialog;
	private String TAG = "ChangePasswordActivity";
	protected final int CP_FINISHED = 1;
	protected final String RETURN_STRING = "returnString";
	private AppMsg.Style style;

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (progressDialog != null)
				progressDialog.dismiss();
			switch (msg.what) {
			case CP_FINISHED: {
				Bundle bundle = msg.getData();
				try {
					finishCPOperation(bundle.getString(RETURN_STRING));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			break;
			}
			super.handleMessage(msg);
		}
	};

	class ChangePasswordThread extends Thread {
		public void run() {
			Message message = new Message();
			message.what = CP_FINISHED;
			changePasswordService cps = new changePasswordService();
			String msg = cps.upload(originalPassWord, newPassWord1);
			Bundle bundle = new Bundle();
			bundle.putString(RETURN_STRING, msg);
			message.setData(bundle);
			myHandler.sendMessage(message);
		}
	}

	protected void finishCPOperation(String mStringReturnStr) {

		if (mStringReturnStr.equals("{\"msg\":\"change password succeed\"}")) {
			new AlertDialog.Builder(new ContextThemeWrapper(ChangePasswordActivity.this,
					android.R.style.Theme_Holo_Light_Dialog))
			.setTitle(null)
			.setMessage("密码修改成功！")
			.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					kczlApplication.IsLogined = 0;
					String PREFS_NAME = "org.nutlab.kczl";
					SharedPreferences settings = getSharedPreferences(
							PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings
							.edit();
					editor.putInt("IsLogined", 0);
					editor.commit();
					// 返回登陆界面
					Intent intent = new Intent(
							ChangePasswordActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
			}).show();
		} else {
			new AlertDialog.Builder(new ContextThemeWrapper(ChangePasswordActivity.this,
					android.R.style.Theme_Holo_Light_Dialog))
			.setTitle(null)
			.setMessage("密码修改失败！")
			.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					oldPwd.setText("");
					newPwd.setText("");
					newPwdAgain.setText("");
				}
			}).show();

		}

	}

	/** Called when the activity is first created. */

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.hideSoftInputFromWindow(oldPwd.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				imm.hideSoftInputFromWindow(newPwd.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				imm.hideSoftInputFromWindow(newPwdAgain.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			else
			{
				ChangePasswordActivity.this.finish();
				overridePendingTransition(R.anim. slide_in_left , R.anim. slide_out_right);  
			}
		}
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.change_password);
		oldPwd = (TextView) findViewById(R.id.old_pwd_EditText);
		newPwd = (TextView) findViewById(R.id.new_pwd_EditText);
		newPwdAgain = (TextView) findViewById(R.id.new_pwd_again_EditText);

		backButton = (Button) findViewById(R.id.abort_button);
		submitButton = (Button) findViewById(R.id.submit_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.hideSoftInputFromWindow(oldPwd.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				imm.hideSoftInputFromWindow(newPwd.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				imm.hideSoftInputFromWindow(newPwdAgain.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				ChangePasswordActivity.this.finish();
				overridePendingTransition(R.anim. slide_in_left , R.anim. slide_out_right);  
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener() {// 完成按钮点击事件

			@Override
			public void onClick(View v) {
				originalPassWord = oldPwd.getText().toString().trim();
				newPassWord1 = newPwd.getText().toString().trim();
				newPassWord2 = newPwdAgain.getText().toString().trim();
				if(originalPassWord.equals("")||newPassWord1.equals("")||newPassWord2.equals(""))
				{
					style = new AppMsg.Style(1000, R.color.alert);
					AppMsg.makeText(ChangePasswordActivity.this, "不能为空！",
							style).show();
				}
				else 
				{
					if (newPassWord1.equals(newPassWord2)) {
						ChangePasswordThread cpt = new ChangePasswordThread();
						progressDialog = ProgressDialog.show(
								new ContextThemeWrapper(ChangePasswordActivity.this,android.R.style.Theme_Holo_Light_Dialog),
								getString(R.string.state),
								getString(R.string.uploading), true);
						cpt.start();
					} else {
						style = new AppMsg.Style(2000, R.color.alert);
						AppMsg.makeText(ChangePasswordActivity.this, "两次密码不一致，请重新输入！",
								style).show();
						newPwd.setText("");
						newPwdAgain.setText("");
						newPwd.requestFocus();

					}
				}
			}
		});
	}

}