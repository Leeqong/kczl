package com.njut.database;

import org.nutlab.kczl.kczlApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.njut.database.table.SMessageTable;
import com.njut.database.table.TMessageTable;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "kczl.db";

    private static final int DATABASE_VERSION = 01;
    
    private static DatabaseHelper singleton = null;
    
    private final static String CREATE_S_MESSAGE_TABLE_SQL = "create table "+SMessageTable.TABLE_NAME
    		+"("
    		+SMessageTable.ID + " integer primary key autoincrement,"
    		+SMessageTable.CONTENT+" text,"
    		+SMessageTable.EDID +" text,"
    		+SMessageTable.TYPE +" text,"
    		+SMessageTable.USERTYPE +" text,"
    		+SMessageTable.VIEWED + " text,"
    		+SMessageTable.TIMESTAMP + " text"
    		+ ");";
    
    private final static String CREATE_T_MESSAGE_TABLE_SQL = "create table "+TMessageTable.TABLE_NAME
    		+"("
    		+TMessageTable.ID + " integer primary key autoincrement,"
    		+TMessageTable.CONTENT+" text,"
    		+TMessageTable.EDID +" text,"
    		+TMessageTable.TYPE +" text,"
    		+TMessageTable.USERTYPE +" text,"
    		+TMessageTable.VIEWED + " text,"
    		+TMessageTable.TIMESTAMP + " text"
    		+ ");";
    
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
    public static synchronized DatabaseHelper getInstance() {
        if (singleton == null) {
            singleton = new DatabaseHelper(kczlApplication.getInstance());
        }
        return singleton;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_S_MESSAGE_TABLE_SQL);
		db.execSQL(CREATE_T_MESSAGE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	

}
