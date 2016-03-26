package com.flug.android.travelscheduler;

import java.util.Calendar;

public class Date {

	private int mYear;
	private int mMonth;
	private int mDay;

	Date() {
		Calendar calendar = Calendar.getInstance();
		mYear  = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH)+1;
		mDay   = calendar.get(Calendar.DAY_OF_MONTH);
	}

	Date( int year, int month, int day ) {
		mYear  = year;
		mMonth = month;
		mDay   = day;
	}

	Date( int serial ) {
		setSerial( serial );
	}

	// 整数値にまとめる
	public int getSerial() {
		return (mYear * 10000) + (mMonth * 100) + mDay;
	}

	// 整数値で取り出す
	public void setSerial( int serial ) {
		mDay   = serial % 100;
		mMonth = (serial / 100) % 100;
		mYear  = serial / 10000;
	}

	// 文字列化する
	@Override
	public String toString() {
		return String.format( "%4d/%02d/%02d", mYear, mMonth, mDay );
	}

	public int getYear() {
		return mYear;
	}

	public void setYear( int year ) {
		mYear = year;
	}

	public int getMonth() {
		return mMonth;
	}

	public void setMonth( int month ) {
		mMonth = month;
	}

	public int getDay() {
		return mDay;
	}

	public void setDay( int day ) {
		mDay = day;
	}
}
