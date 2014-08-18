package com.frame.easyandroid.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库的管理类
 * 
 * @author zhao.liu
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String DBNAME = "EasyAndroid.db";
	public static final int DBVERSION = 2;

	public DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION);
	}

	/**
	 * 通用主键
	 */
	public static final String TABLE_ID_COL = "_ID";// 主键

	/**
	 * 书
	 */
	public static final String TABLE_BOOK = "book";
	public static final String TABLE_BOOK_NAME_COL = "name";// 书的名称
	public static final String TABLE_BOOK_SUMMARY = "summary";// 书的简介

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_BOOK + " (" + //
				TABLE_ID_COL + " integer primary key autoincrement, " + //
				TABLE_BOOK_NAME_COL + " VARCHAR(200))"//
		);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 先将原有的表删除
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
		db.execSQL("CREATE TABLE " + TABLE_BOOK + " (" + //
				TABLE_ID_COL + " integer primary key autoincrement, " + //
				TABLE_BOOK_NAME_COL + " VARCHAR(200)," + //
				TABLE_BOOK_SUMMARY + " VARCHAR(200))"//
		);

	}

}
