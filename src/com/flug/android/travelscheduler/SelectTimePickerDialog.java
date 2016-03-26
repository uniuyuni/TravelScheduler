package com.flug.android.travelscheduler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class SelectTimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener {

	// 時間設定時に呼ばれるリスナー
	public interface OnTimeSetListener {

		void onTimeSet( int hourOfDay, int minute );
	}

	private final OnTimeSetListener mOnTimeSetListener;

	private int mMinute;
	private int mLastSelectMinuteId;
	private int mHour;
	private int mLastSelectHourId;

	private View mViewRoot;

	private static final int[] HOUR_IDS = {
			R.id.hour00, R.id.hour01, R.id.hour02, R.id.hour03, R.id.hour04, R.id.hour05,
			R.id.hour06, R.id.hour07, R.id.hour08, R.id.hour09, R.id.hour10, R.id.hour11,
			R.id.hour12, R.id.hour13, R.id.hour14, R.id.hour15, R.id.hour16, R.id.hour17,
			R.id.hour18, R.id.hour19, R.id.hour20, R.id.hour21, R.id.hour22, R.id.hour23,
	};
	private static final int[] MINUTE_IDS = {
			R.id.minute00, R.id.minute01, R.id.minute02, R.id.minute03, R.id.minute04,
			R.id.minute05, R.id.minute06, R.id.minute07, R.id.minute08, R.id.minute09,
			R.id.minute10, R.id.minute11, R.id.minute12, R.id.minute13, R.id.minute14,
			R.id.minute15, R.id.minute16, R.id.minute17, R.id.minute18, R.id.minute19,
			R.id.minute20, R.id.minute21, R.id.minute22, R.id.minute23, R.id.minute24,
			R.id.minute25, R.id.minute26, R.id.minute27, R.id.minute28, R.id.minute29,
			R.id.minute30, R.id.minute31, R.id.minute32, R.id.minute33, R.id.minute34,
			R.id.minute35, R.id.minute36, R.id.minute37, R.id.minute38, R.id.minute39,
			R.id.minute40, R.id.minute41, R.id.minute42, R.id.minute43, R.id.minute44,
			R.id.minute45, R.id.minute46, R.id.minute47, R.id.minute48, R.id.minute49,
			R.id.minute50, R.id.minute51, R.id.minute52, R.id.minute53, R.id.minute54,
			R.id.minute55, R.id.minute56, R.id.minute57, R.id.minute58, R.id.minute59,
	};

	private static final int CLICK_COLOR = 0xff808080;
	private int mUnclickColor;

	View.OnClickListener mMinuteOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick( View view ) {
			int id = view.getId();

			if( mLastSelectMinuteId != id ) {
				TextView tv_minute = (TextView)mViewRoot.findViewById( mLastSelectMinuteId );
				tv_minute.setBackgroundColor( mUnclickColor );

				view.setBackgroundColor( CLICK_COLOR );
				mLastSelectMinuteId = id;
				mMinute = (Integer)view.getTag();
			}
		}
	};

	View.OnClickListener mHourOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick( View view ) {
			int id = view.getId();

			if( mLastSelectHourId != id ) {
				TextView tv_minute = (TextView)mViewRoot.findViewById( mLastSelectHourId );
				tv_minute.setBackgroundColor( mUnclickColor );

				view.setBackgroundColor( CLICK_COLOR );
				mLastSelectHourId = id;
				mHour = (Integer)view.getTag();
			}
		}
	};

	public SelectTimePickerDialog( Context context, OnTimeSetListener listener, int hour, int minute ) {
		super( context );

		mOnTimeSetListener = listener;
		mMinute = minute % 60;
		mHour   = (hour + minute / 60) % 24;

		LayoutInflater inflater = LayoutInflater.from( context );
		mViewRoot = inflater.inflate( R.layout.select_time_picker, null );
		setView( mViewRoot );
		setButton( BUTTON_POSITIVE, context.getString( R.string.ok ), this );
		setButton( BUTTON_NEGATIVE, context.getString( R.string.cancel ), this );
//		setButtonPanelLayoutHint( LAYOUT_HINT_SIDE );

		if( mViewRoot.getBackground() instanceof ColorDrawable ) {
			mUnclickColor = ((ColorDrawable)mViewRoot.getBackground()).getColor();
		} else {
			// テーマで設定されている背景色を取得
			TypedValue typedValue = new TypedValue();
			context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
			int resourceId = typedValue.resourceId;
			mUnclickColor = context.getResources().getColor(resourceId);
		}

		// クリックリスナーと整数値設定
		for( int i = 0; i < MINUTE_IDS.length; ++i ) {
			TextView tv = (TextView)mViewRoot.findViewById( MINUTE_IDS[i] );
			tv.setClickable( true );
			tv.setTag( i );
			tv.setBackgroundColor( mUnclickColor );
			tv.setOnClickListener( mMinuteOnClickListener );
		}
		for( int i = 0; i < HOUR_IDS.length; ++i ) {
			TextView tv = (TextView)mViewRoot.findViewById( HOUR_IDS[i] );
			tv.setClickable( true );
			tv.setTag( i );
			tv.setBackgroundColor( mUnclickColor );
			tv.setOnClickListener( mHourOnClickListener );
		}

		// 現在選択
		mLastSelectMinuteId = MINUTE_IDS[mMinute];
		mLastSelectHourId   = HOUR_IDS[mHour];
		TextView tv_minute = (TextView)mViewRoot.findViewById( mLastSelectMinuteId );
		tv_minute.setBackgroundColor( CLICK_COLOR );
		TextView tv_hour   = (TextView)mViewRoot.findViewById( mLastSelectHourId );
		tv_hour.setBackgroundColor( CLICK_COLOR );

	}

	@Override
	public void onClick( DialogInterface dialog, int which ) {
		switch( which ) {
		case BUTTON_POSITIVE:
			if( mOnTimeSetListener != null ) {
				mOnTimeSetListener.onTimeSet( mHour, mMinute );
			}
			break;
		case BUTTON_NEGATIVE:
			cancel();
			break;
		}
	}

}
