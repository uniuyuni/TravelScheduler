package com.flug.android.travelscheduler;

import java.util.Calendar;

public class Time {
	private int mHour;
	private int mSecond;

	public Time() {
		Calendar calendar = Calendar.getInstance();
		mHour  = calendar.get(Calendar.HOUR);
		mSecond = calendar.get(Calendar.SECOND);
	}

	public Time( int hour, int second ) {
		mHour   = hour;
		mSecond = second;
	}

	public Time( int serial ) {
		setSerial( serial );
	}

	// 整数値にまとめる
	public int getSerial() {
		return (mHour * 10000) + (mSecond * 100);
	}

	// 整数値で取り出す
	public void setSerial( int serial ) {
		mHour   = serial / 10000;
		mSecond = (serial / 100) % 100;
	}

	// 文字列化する
	@Override
	public String toString() {
		return String.format( "%02d:%02d", mHour, mSecond );
	}

	public int getHour() {
		return mHour;
	}

	public void setHour( int hour ) {
		mHour = hour % 24;
	}

	public int getSecond() {
		return mSecond;
	}

	public void setSecond( int second ) {
		mSecond = second % 60;
	}


}
