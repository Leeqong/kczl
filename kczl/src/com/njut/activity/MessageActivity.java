package com.njut.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.nutlab.kczl.kczlApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.njut.data.AdviceElement;
import com.njut.data.MessageElement;
import com.njut.data.SpitElement;
import com.njut.database.SMessageDBTask;
import com.njut.database.TMessageDBTask;
import com.njut.utility.AppUtils;
import com.njut.view.MessageAdapter;

/*测试用*/
public class MessageActivity extends Activity {
	private Button backButton;
	private Button clearall_btn;
	private List<MessageElement> messageList;

	private final int LOADER_ID = 0;
	private ListView message_lv;
	private MessageAdapter listAdapter;
	private String isFromXgMessage;
	private final int READ_DB_FINISHED = 123;

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READ_DB_FINISHED:
				listAdapter.notifyDataSetChanged();
				((MainActivity)MessageActivity.this.getParent()).updateBadge();
				break;
			}
		};
	};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.message);
		isFromXgMessage = "";
		if (getIntent().getStringExtra("isFromXgMessage") != null
				&& getIntent().getStringExtra("isFromXgMessage").equals(
						"true"))
			isFromXgMessage= "true";
		//		if(isFromXgMessage!=null)
		//			Toast.makeText(MessageActivity.this, "isFromXgMessage:"+isFromXgMessage, Toast.LENGTH_LONG);
		backButton = (Button) findViewById(R.id.toolbar_nav_button);
		clearall_btn = (Button) findViewById(R.id.clear_btn);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.menu.showMenu();
			}
		});
		clearall_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(new ContextThemeWrapper(MessageActivity.this,android.R.style.Theme_Holo_Light_Dialog))
				.setMessage("确认清除所有消息？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SMessageDBTask.removeAll();
						new SMessageDBThread().start();
						//						MessageActivity.this.getLoaderManager().restartLoader(LOADER_ID, null, MessageActivity.this); 
						//MessageActivity.this.getParent().getLoaderManager().restartLoader(LOADER_ID, null,(LoaderManager.LoaderCallbacks<Integer>)getParent());
					}
				})
				.setNegativeButton("取消", null).show();
			}
		});
		message_lv = (ListView) this.findViewById(R.id.message_lv);
		messageList= new ArrayList<MessageElement>();
		listAdapter= new MessageAdapter(MessageActivity.this, messageList);
		View emptyView = findViewById(R.id.emptyView);
		message_lv.setEmptyView(emptyView);
		message_lv.setAdapter(listAdapter);


		//		getLoaderManager().initLoader(LOADER_ID, null, this);
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
					SMessageDBTask.addOrUpdateMessage(message);
				}

				if(message.getType().equals("SPIT")&&kczlApplication.SpitElements != null)
				{
					// 没用Sqlite数据库的下场
					for(int i = 0; i< kczlApplication.SpitElements.size();i++)
					{
						if(kczlApplication.SpitElements.get(i).getSpitid().equals(message.getEdid()))
						{
							spitElement = kczlApplication.SpitElements.get(i);
							targetPosition = i;
							break;
						}
					}
					if(targetPosition == -1)
						return;
					else
					{
						Intent intent = new Intent(MessageActivity.this, CommentActivity.class);
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
					for(int i = 0; i< kczlApplication.AdviceElements.size();i++)
					{
						if(kczlApplication.AdviceElements.get(i).getCaid().equals(message.getEdid()))
						{
							adviceElement = kczlApplication.AdviceElements.get(i);
							targetPosition = i;
							break;
						}
					}
					if(targetPosition == -1)
						return;
					else
					{
						Intent intent = new Intent(MessageActivity.this, CommentAdviceActivity.class);
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
        new SMessageDBThread().start();
        
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new SMessageDBThread().start();
	}
	
	
	//	@Override
	//	public Loader<List<MessageElement>> onCreateLoader(int id, Bundle args) {
	//		// TODO Auto-generated method stub
	//		return new SMessageDBLoader(MessageActivity.this, args);
	//	}
	//
	//	@Override
	//	public void onLoadFinished(Loader<List<MessageElement>> loader,
	//			List<MessageElement> data) {
	//		// TODO Auto-generated method stub
	//		messageList.clear();
	//		messageList.addAll(data);
	//		listAdapter.notifyDataSetChanged();
	//	}
	//
	//	@Override
	//	public void onLoaderReset(Loader<List<MessageElement>> arg0) {
	//		// TODO Auto-generated method stub
	//		messageList.clear();
	//		listAdapter.notifyDataSetChanged();
	//	}

	//	private static class SMessageDBLoader extends AsyncTaskLoader<List<MessageElement>> {
	//
	//		public SMessageDBLoader(Context context, Bundle args) {
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
	//			return SMessageDBTask.getSMessageList();
	//		}
	//	}

	class SMessageDBThread extends Thread
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			List<MessageElement> data = SMessageDBTask.getSMessageList();
			messageList.clear();
			messageList.addAll(data);
			myHandler.sendEmptyMessage(READ_DB_FINISHED);
		}
	}

}