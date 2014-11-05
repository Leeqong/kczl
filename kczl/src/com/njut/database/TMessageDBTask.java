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
import com.njut.database.table.TMessageTable;

public class TMessageDBTask {
	private TMessageDBTask()
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


	public static String addOrUpdateMessage(MessageElement message)//会在插入数据成功后修改MessageElement的id为自增生成的id
	{
		ContentValues cv = new ContentValues();
		cv.put(TMessageTable.CONTENT,message.getContent());
		cv.put(TMessageTable.EDID,message.getEdid());
		cv.put(TMessageTable.TYPE,message.getType());
		cv.put(TMessageTable.USERTYPE, message.getUserType());
        cv.put(TMessageTable.VIEWED, message.getViewed());
        cv.put(TMessageTable.TIMESTAMP, message.getTimestamp());

		//本来是要用服务器推送的commentid作为标识的，现在判断message的id是否为-1
		if(!message.getId().equals("-1"))//update
		{
			String[] args = { message.getId() };
			getWsd().update(TMessageTable.TABLE_NAME, cv,
					TMessageTable.ID + "=?", args);
			return "update_successfully";
		}
		else//add
		{
			getWsd().insert(TMessageTable.TABLE_NAME,
					TMessageTable.EDID, cv);
			Cursor c = getWsd().rawQuery("select last_insert_rowid() from " + TMessageTable.TABLE_NAME,null);
			if(c.moveToFirst())    
				message.setId(String.valueOf(c.getInt(0))); 
            c.close();

			return "add_successfuly";
		}
	}
	public static List<MessageElement> getTMessageList() {
		List<MessageElement> messageList = new ArrayList<MessageElement>();
		String sqlunviewed = "select * from " + TMessageTable.TABLE_NAME 
				+" where "+ TMessageTable.VIEWED + " = 'false'"
				+" order by " +TMessageTable.TIMESTAMP +" desc";
		String sqlviewed = "select * from " + TMessageTable.TABLE_NAME 
				+" where "+ TMessageTable.VIEWED + " = 'true'"
				+ " order by " +TMessageTable.TIMESTAMP +" desc";
		Cursor c = getWsd().rawQuery(sqlunviewed, null);
		if (c == null)
			return messageList;
		while (c.moveToNext()) {
			MessageElement message = new MessageElement();

			int colid = c.getColumnIndex(TMessageTable.ID);
			message.setId(String.valueOf(c.getInt(colid)));

			colid = c.getColumnIndex(TMessageTable.CONTENT);
			message.setContent(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.EDID);
			message.setEdid(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.TYPE);
			message.setType(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.USERTYPE);
			message.setUserType(c.getString(colid));
			
			colid = c.getColumnIndex(TMessageTable.VIEWED);
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

			int colid = c.getColumnIndex(TMessageTable.ID);
			message.setId(String.valueOf(c.getInt(colid)));

			colid = c.getColumnIndex(TMessageTable.CONTENT);
			message.setContent(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.EDID);
			message.setEdid(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.TYPE);
			message.setType(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.USERTYPE);
			message.setUserType(c.getString(colid));
			
			colid = c.getColumnIndex(TMessageTable.VIEWED);
			message.setViewed(c.getString(colid));
			
			colid = c.getColumnIndex(TMessageTable.TIMESTAMP);
			message.setTimestamp(c.getString(colid));

			messageList.add(message);
		}
		c.close();
		return messageList;
	}

	public static MessageElement getMessageElement(String ID) {
		String sql = "select * from " + TMessageTable.TABLE_NAME + " where "
				+ TMessageTable.ID + " = " + ID;
		Cursor c = getRsd().rawQuery(sql, null);
		if (c.moveToNext()) {
			MessageElement message = new MessageElement();

			int colid = c.getColumnIndex(TMessageTable.ID);
			message.setId(TMessageTable.ID);

			colid = c.getColumnIndex(TMessageTable.CONTENT);
			message.setContent(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.EDID);
			message.setEdid(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.TYPE);
			message.setType(c.getString(colid));

			colid = c.getColumnIndex(TMessageTable.USERTYPE);
			message.setUserType(c.getString(colid));
			
			colid = c.getColumnIndex(TMessageTable.VIEWED);
			message.setViewed(c.getString(colid));
			
			colid = c.getColumnIndex(TMessageTable.TIMESTAMP);
			message.setTimestamp(c.getString(colid));

			return message;
		}
		c.close();
		return null;
	}

	public static void removeTMessageElement(String ID) {

		Set<Integer> IDs = new HashSet<Integer>();
		IDs.add(Integer.valueOf(ID));
		removeTMessageElements(IDs);
	}

	public static void removeTMessageElements(Set<Integer> IDs) {
		Integer[] args = IDs.toArray(new Integer[0]);
		String asString = Arrays.toString(args);
		asString = asString.replace("[", "(");
		asString = asString.replace("]", ")");

		String sql = "delete from " + TMessageTable.TABLE_NAME + " where "
				+ TMessageTable.ID + " in " + asString;
		getWsd().execSQL(sql);
	}
	public static void removeAll()
	{
		String sql = "delete from " + TMessageTable.TABLE_NAME;
		getWsd().execSQL(sql);
	}
	public static Integer getUnViewedCount()
	{
		Integer count = null;
		String sql = "select count(*) from " + TMessageTable.TABLE_NAME + " where "
				+ TMessageTable.VIEWED + " = 'true'";
		Cursor c = getRsd().rawQuery(sql, null);
		if (c.moveToNext()) {
			count = c.getInt(0);
		    return count;
		}
		c.close();
		return 0;
	}
}
