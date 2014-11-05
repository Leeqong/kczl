package com.njut.xgpush;

import org.json.JSONException;
import org.json.JSONObject;
import org.nutlab.kczl.kczlApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.njut.R;
import com.njut.activity.loadingActivity;
import com.njut.data.MessageElement;
import com.njut.database.SMessageDBTask;
import com.njut.database.TMessageDBTask;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class CustomPushReceiver extends XGPushBaseReceiver{

	public static final int NEW_COMMENT = 1;
	private static final int S_INSERT_FINISHED=123345;
	private static final int T_INSERT_FINISHED=133345;
	private MessageElement messageElement;
	private NotificationManager manager;
	private Context mContext;


	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case S_INSERT_FINISHED:
				Notification s_notice = new Notification();
				s_notice.icon = R.drawable.icon;
				s_notice.flags |= Notification.FLAG_AUTO_CANCEL;//在通知栏上点击此通知后自动清除此通知
				s_notice.tickerText = messageElement.getContent();
				Intent s_target = new Intent(mContext,loadingActivity.class);
				s_target.putExtra("isFromXgMessage", "true");
				s_target.putExtra("ID",messageElement.getId());
				String title = "你有了新的回复";
				if(messageElement.getUserType().equals("S"))
					title = "来自匿名用户的新回复";
				if(messageElement.getUserType().equals("T"))
					title = "来自任课教师的新回复";
				s_notice.setLatestEventInfo(mContext, title, s_notice.tickerText, PendingIntent
						.getActivity(mContext, 0, s_target, 0));// 即将跳转页面，还没跳转
				manager.notify(0, s_notice);
				break;
			case T_INSERT_FINISHED:
				Notification notice = new Notification();
				notice.icon = R.drawable.icon;
				notice.flags |= Notification.FLAG_AUTO_CANCEL;//在通知栏上点击此通知后自动清除此通知
				notice.tickerText = messageElement.getContent();
				Intent target = new Intent(mContext,loadingActivity.class);
				target.putExtra("isFromXgMessage", "true");
				target.putExtra("ID",messageElement.getId());
				notice.setLatestEventInfo(mContext, "来自匿名用户的新回复", notice.tickerText, PendingIntent
						.getActivity(mContext, 0, target, 0));// 即将跳转页面，还没跳转
				manager.notify(0, notice);
			}
		}
	};


	private void show(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifactionClickedResult(Context arg0,
			XGPushClickedResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifactionShowedResult(Context arg0, XGPushShowedResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegisterResult(Context context, int errorCode,
			XGPushRegisterResult message) {

		if (context == null || message == null) {
			return;
		}

		String text = null;
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = message + "注册成功";
			// 在这里拿token
			String token = message.getToken();
		} else {
			text = message + "注册失败，错误码：" + errorCode;
		}
		//show(context, text);

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		// TODO Auto-generated method stub
		//收到消息:TPushTextMessage [title=null, content=testjson!!!!!, customContent=null]
		if (context == null || message == null) {
			return;
		}
		mContext = context;	
		String text = "收到消息:" + message.toString();
		// 获取自定义key-value
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				//{"timestamp":"2014-5-27 10:53:27","order":"1"}
				JSONObject customJson = new JSONObject(customContent);
				// key1为前台配置的key
				if (!customJson.isNull("order")) {
					String order = customJson.getString("order");
					String timestamp = customJson.getString("timestamp");
					String content = message.getContent();
					//content=[S,SPIT,1049]12345机智
					String temp = content.substring(1, content.indexOf("]"));
					String temps[] = temp.split(",");
					String user_type = temps[0];
					String type = temps[1];
					String edid = temps[2];
					String commentContent = content.substring(content.indexOf("]")+1);

					messageElement = new MessageElement();
					messageElement.setContent(commentContent);
					messageElement.setEdid(edid);
					messageElement.setType(type);
					messageElement.setUserType(user_type);
					messageElement.setId("-1");
					messageElement.setViewed("false");
					messageElement.setTimestamp(timestamp);
					manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
					if(kczlApplication.IsStudent == 2)
					{
						new Thread(){
							public void run() {
								TMessageDBTask.addOrUpdateMessage(messageElement);
								handler.sendEmptyMessage(T_INSERT_FINISHED);

							};
						}.start();
					}
					if(kczlApplication.IsStudent == 1)
					{
						new Thread(){
							public void run() {
								SMessageDBTask.addOrUpdateMessage(messageElement);
								handler.sendEmptyMessage(S_INSERT_FINISHED);
							};
						}.start();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Log.d("GPGPGP", message.toString());
	}


	void notifyOrder(Context context,String content)
	{

	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
