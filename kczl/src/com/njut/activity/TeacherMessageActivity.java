package com.njut.activity;

import java.util.ArrayList;
import java.util.List;

import org.nutlab.kczl.kczlApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.njut.R;
import com.njut.activity.MessageActivity.SMessageDBThread;
import com.njut.data.AdviceElement;
import com.njut.data.MessageElement;
import com.njut.data.SpitElement;
import com.njut.database.SMessageDBTask;
import com.njut.database.TMessageDBTask;
import com.njut.view.MessageAdapter;

public class TeacherMessageActivity extends Activity{

	private Button clearall_btn;
	private final int LOADER_ID = 0;
	private ListView message_lv;
	private MessageAdapter listAdapter;
	private List<MessageElement> messageList;
	private String isFromXgMessage;
	private final int READ_DB_FINISHED = 123;

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READ_DB_FINISHED:
				listAdapter.notifyDataSetChanged();
				break;
			}
		};
	};
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.teacher_message);
		isFromXgMessage = "";
		if (getIntent().getStringExtra("isFromXgMessage") != null
				&& getIntent().getStringExtra("isFromXgMessage").equals(
						"true"))
			isFromXgMessage= "true";
		clearall_btn = (Button) findViewById(R.id.clear_btn);
		clearall_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(new ContextThemeWrapper(TeacherMessageActivity.this,android.R.style.Theme_Holo_Light_Dialog))
				.setMessage("确认清除所有消息？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						TMessageDBTask.removeAll();
						new TMessageDBThread().start();
					}
				})
				.setNegativeButton("取消", null).show();
			}
		});
		message_lv = (ListView) this.findViewById(R.id.message_lv);
		message_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
		messageList= new ArrayList<MessageElement>();
		View emptyView = findViewById(R.id.emptyView);
		message_lv.setEmptyView(emptyView);
		listAdapter= new MessageAdapter(TeacherMessageActivity.this, messageList);
		message_lv.setAdapter(listAdapter);
		message_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MessageElement message = messageList.get(position);
				int targetPosition = -1;
				SpitElement spitElement = null;
				AdviceElement adviceElement = null;
				Boolean toRefresh = false;
				if(message.getViewed().equals("false"))
				{
					toRefresh = true;
					message.setViewed("true");
					TMessageDBTask.addOrUpdateMessage(message);
				}
				if(message.getType().equals("SPIT")&&kczlApplication.SpitElements != null)
				{
					// 没用Sqlite数据库的下场,服务器没有ctid，只能两重循环
					for(int i = 0; i< kczlApplication.teacherlist.size();i++)
					{
						List<SpitElement> spitlist = kczlApplication.teacherlist.get(i).getSpitlist();
						for(int j = 0;j<spitlist.size();j++)
						{
							if(spitlist.get(j).getSpitid().equals(message.getEdid()))
							{
								spitElement = spitlist.get(j);
								targetPosition = j;
								break;
							}
						}
						if(targetPosition != -1)
							break;
					}
					if(targetPosition == -1)
						return;
					else
					{
						Intent intent = new Intent(TeacherMessageActivity.this, CommentActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("spitElement", spitElement);
						bundle.putInt("position", targetPosition);
						bundle.putString("isFromXgMessage", isFromXgMessage);
						bundle.putBoolean("toRefresh", toRefresh);
						intent.putExtras(bundle);
						startActivity(intent);
						overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left ); 
					}
				}
				if(message.getType().equals("ADVICE")&&kczlApplication.AdviceElements != null)
				{
					for(int i = 0; i< kczlApplication.teacherlist.size();i++)
					{
						List<AdviceElement> advicelist = kczlApplication.teacherlist.get(i).getAdvicelist();
						for(int j = 0;j<advicelist.size();j++)
						{
							if(advicelist.get(j).getCaid().equals(message.getEdid()))
							{
								adviceElement = advicelist.get(j);
								targetPosition = j;
								break;
							}
						}
						if(targetPosition != -1)
							break;
					}
					if(targetPosition == -1)
						return;
					else
					{
						Intent intent = new Intent(TeacherMessageActivity.this, CommentAdviceActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("adviceElement", adviceElement);
						bundle.putInt("position", targetPosition);
						bundle.putString("isFromXgMessage", isFromXgMessage);
						bundle.putBoolean("toRefresh", toRefresh);
						intent.putExtras(bundle);
						startActivity(intent);
						overridePendingTransition( R.anim.slide_in_right ,R.anim.slide_out_left ); 
					}
				}
			}
		});
		//getLoaderManager().initLoader(LOADER_ID, null, this);
		new TMessageDBThread().start();
	}

	//	@Override
	//	public Loader<List<MessageElement>> onCreateLoader(int id, Bundle args) {
	//		// TODO Auto-generated method stub
	//		return new TMessageDBLoader(TeacherMessageActivity.this,args);	
	//	}
	//
	//	@Override
	//	public void onLoadFinished(Loader<List<MessageElement>> loader,
	//			List<MessageElement> data) {
	//		messageList.clear();
	//		messageList.addAll(data);
	//		listAdapter.notifyDataSetChanged();
	//	}
	//
	//	@Override
	//	public void onLoaderReset(Loader<List<MessageElement>> loader) {
	//		// TODO Auto-generated method stub
	//		messageList.clear();
	//		listAdapter.notifyDataSetChanged();
	//	}

	//	private static class TMessageDBLoader extends AsyncTaskLoader<List<MessageElement>> {
	//
	//		public TMessageDBLoader(Context context, Bundle args) {
	//			super(context);
	//		}
	//
	//		@Override
	//		protected void onStartLoading() {
	//			super.onStartLoading();
	//			forceLoad();
	//		}
	//
	//		public List<MessageElement> loadInBackground() {
	//			return TMessageDBTask.getTMessageList();
	//		}
	//	}
	class TMessageDBThread extends Thread
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			List<MessageElement> data = TMessageDBTask.getTMessageList();
			messageList.clear();
			messageList.addAll(data);
			myHandler.sendEmptyMessage(READ_DB_FINISHED);
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new TMessageDBThread().start();
	}
}
