package com.flug.android.travelscheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "TravelScheduler.db";

	// コンストラクタ定義
	public MyDbHelper( Context context ) {
		// SQLiteOpenHelperのコンストラクタ呼び出し
		super( context, "TravelScheduler.db", null, 1 );
	}

	// onCreateメソッド
	@Override
	public void onCreate( SQLiteDatabase db ) {
		// スケジュールリストテーブル
		db.execSQL(   "create table schedules("
					+ "_id integer primary key autoincrement,"
					+ "schedule_name text)" );

		db.execSQL(   "create table entries("
					+ "_id integer primary key autoincrement,"
					+ "schedule_id integer,"
					+ "date integer,"
					+ "item integer,"
					+ "cost integer,"
					+ "use text,"
					+ "desc text,"
					+ "start_name text,"
					+ "end_name text,"
					+ "start_time integer,"
					+ "end_time integer,"
					+ "uri text,"
					+ "memo text)" );

		db.execSQL(   "create table trash("
					+ "_id integer primary key autoincrement,"
					+ "entry_id integer)" );
	}

	// onUpgradeメソッド
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldversion, int newversion ) {
	}

}
