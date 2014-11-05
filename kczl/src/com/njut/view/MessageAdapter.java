package com.njut.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.njut.R;
import com.njut.data.MessageElement;

public class MessageAdapter extends BaseAdapter{

	private List<MessageElement> messageList;
	private Context context;
	private LayoutInflater inflater;

	public MessageAdapter(Context context,List<MessageElement> messageList) {
		this.messageList = messageList;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = inflater
			.inflate(R.layout.message_list_item, null);

		TextView from_tv = (TextView) convertView.findViewById(R.id.from_tv);
		TextView time_tv = (TextView) convertView.findViewById(R.id.time_tv);
		TextView content_tv = (TextView) convertView.findViewById(R.id.content_tv);
		LinearLayout block_viewed = (LinearLayout) convertView.findViewById(R.id.block_viewed);

		MessageElement message = messageList.get(position);
		if(message.getUserType().equals("S"))
			from_tv.setText("匿名学生");
		else if(message.getUserType().equals("T"))
			from_tv.setText("任课老师");
		content_tv.setText(message.getContent());
		block_viewed.setBackgroundResource(message.getViewed().equals("false")?R.color.toolbar:R.color.kindOfGray);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sf.parse(message.getTimestamp());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sf1 = new SimpleDateFormat("M月dd日 HH:mm");
		time_tv.setText(sf1.format(date));
		return convertView;
	}

}
