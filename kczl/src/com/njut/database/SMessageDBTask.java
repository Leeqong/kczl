package com.njut.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njut.data.MessageElement;
import com.njut.database.table.SMessageTable;
import com.njut.database.table.TMessageTable;

public class SMessageDBTask {

	private SMessageDBTask()
	{

	}

	private static SQLiteDatabase getWsd() {

		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getWritableDatabase();
	}

	private static SQLiteDatabase getRsd() {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getReadableDatabase();
	}


	public static String addOrUpdateMessage(MessageElement message)//���ڲ������ݳɹ����޸�MessageElement��idΪ�������ɵ�id
	{
		ContentValues cv = new ContentValues();
		cv.put(SMessageTable.CONTENT,message.getContent());
		cv.put(SMessageTable.EDID,message.getEdid());
		cv.put(SMessageTable.TYPE,message.getType());
		cv.put(SMessageTable.USERTYPE, message.getUserType());
		cv.put(SMessageTable.VIEWED, message.getViewed());
		cv.put(TMessageTable.TIMESTAMP, message.getTimestamp());

		//������Ҫ�÷��������͵�commentid��Ϊ��ʶ�ģ������ж�message��id�Ƿ�Ϊ-1
		if(!message.getId().equals("-1"))//update
		{
			String[] args = { message.getId() };
			getWsd().update(SMessageTable.TABLE_NAME, cv,
					SMessageTable.ID + "=?", args);
			return "update_successfully";
		}
		else//add
		{
			getWsd().insert(SMessageTable.TABLE_NAME,
					SMessageTable.EDID, cv);
			Cursor c = getWsd().rawQuery("select last_insert_rowid() from " + SMessageTable.TABLE_NAME,null);
			if(c.moveToFirst())    
				message.setId(String.valueOf(c.getInt(0))); 
			c.close();

			return "add_successfuly";
		}
	}
	//��ѡ��unviewed�İ�ʱ����������Զ������ѡviewed
	public static List<MessageElement> getSMessageList() {
		List<MessageElement> messageList = new ArrayList<MessageElement>();

		String sqlunviewed = "select * from " + SMessageTable.TABLE_NAME 
				+" where "+ SMessageTable.VIEWED + " = 'false'"
				+" order by " +SMessageTable.TIMESTAMP +" desc";
		String sqlviewed = "select * from " + SMessageTable.TABLE_NAME 
				+" where "+ SMessageTable.VIEWED + " = 'true'"
				+ " order by " +SMessageTable.TIMESTAMP +" desc";
		Cursor c = getWsd().rawQuery(sqlunviewed, null);
		if (c == null)
			return messageList;
		while (c.moveToNext()) {
			MessageElement message = new MessageElement();

			int colid = c.getColumnIndex(SMessageTable.ID);
			message.setId(String.valueOf(c.getInt(colid)));

			colid = c.getColumnIndex(SMessageTable.CONTENT);
			message.setContent(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.EDID);
			message.setEdid(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.TYPE);
			message.setType(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.USERTYPE);
			message.setUserType(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.VIEWED);
			message.setViewed(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.TIMESTAMP);
			message.setTimestamp(c.getString(colid));

			messageList.add(message);
		}
		c = getWsd().rawQuery(sqlviewed, null);
		if (c == null)
			return messageList;
		while (c.moveToNext()) {
			MessageElement message = new MessageElement();

			int colid = c.getColumnIndex(SMessageTable.ID);
			message.setId(String.valueOf(c.getInt(colid)));

			colid = c.getColumnIndex(SMessageTable.CONTENT);
			message.setContent(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.EDID);
			message.setEdid(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.TYPE);
			message.setType(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.USERTYPE);
			message.setUserType(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.VIEWED);
			message.setViewed(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.TIMESTAMP);
			message.setTimestamp(c.getString(colid));

			messageList.add(message);
		}
		c.close();
		return messageList;
	}

	public static MessageElement getMessageElement(String ID) {
		String sql = "select * from " + SMessageTable.TABLE_NAME + " where "
				+ SMessageTable.ID + " = " + ID;
		Cursor c = getRsd().rawQuery(sql, null);
		if (c.moveToNext()) {
			MessageElement message = new MessageElement();

			int colid = c.getColumnIndex(SMessageTable.ID);
			message.setId(SMessageTable.ID);

			colid = c.getColumnIndex(SMessageTable.CONTENT);
			message.setContent(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.EDID);
			message.setEdid(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.TYPE);
			message.setType(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.USERTYPE);
			message.setUserType(c.getString(colid));

			colid = c.getColumnIndex(SMessageTable.VIEWED);
			message.setViewed(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.TIMESTAMP);
			message.setTimestamp(c.getString(colid));

			return message;
		}
		c.close();
		return null;
	}

	public static void removeSMessageElement(String ID) {

		Set<Integer> IDs = new HashSet<Integer>();
		IDs.add(Integer.valueOf(ID));
		removeSMessageElements(IDs);
	}

	public static void removeSMessageElements(Set<Integer> IDs) {
		Integer[] args = IDs.toArray(new Integer[0]);
		String asString = Arrays.toString(args);
		asString = asString.replace("[", "(");
		asString = asString.replace("]", ")");

		String sql = "delete from " + SMessageTable.TABLE_NAME + " where "
				+ SMessageTable.ID + " in " + asString;
		getWsd().execSQL(sql);
	}
	public static void removeAll()
	{
		String sql = "delete from " + SMessageTable.TABLE_NAME;
		getWsd().execSQL(sql);
	}

	public static Integer getUnViewedCount()
	{
		Integer count = 0;
		String sql = "select count(*) from " + SMessageTable.TABLE_NAME	+ " where "
				+ SMessageTable.VIEWED + " = 'false'";
		Cursor c = getRsd().rawQuery(sql, null);
		if (c.moveToNext()) {
			count = c.getInt(0);
		}
		c.close();
		return count;
	}
}
