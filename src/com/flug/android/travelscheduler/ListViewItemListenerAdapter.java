package com.flug.android.travelscheduler;

import android.view.View;
import android.widget.AdapterView;

public class ListViewItemListenerAdapter {

	// Buttonなどのクリックイベント
	interface OnClickListener {
		public void onClick( View view, int position );
	}

	// Spinnerなどのアイテムセレクトイベント
	interface OnItemSelectedListener {
		public void onItemSelected( AdapterView<?> parent, View view, int position, int itemId, long id );
	}

	// EditTextなどのフォーカスが外れたときに呼ばれるイベント
	interface OnFocusChangeListener {
		public void onFocusChange( View view, int position, boolean hasFocus );
	}

	OnClickListener			mOnClickListener;
	OnItemSelectedListener	mOnItemSelectedListener;
	OnFocusChangeListener	mOnFocusChangeListener;


	public void setOnClickListener( OnClickListener listener ) {
		mOnClickListener = listener;
	}

	public void setOnItemSelectedListener( OnItemSelectedListener listener ) {
		mOnItemSelectedListener = listener;
	}

	public void setOnFocusChangeListener( OnFocusChangeListener listener ) {
		mOnFocusChangeListener = listener;
	}

	public void performOnClickListener( View view, int position ) {
		if( mOnClickListener != null ) {
			mOnClickListener.onClick( view, position );
		}
	}

	public void performOnItemSelectedListener( AdapterView<?> parent, View view, int position, int itemId, long id ) {
		if( mOnItemSelectedListener != null ) {
			mOnItemSelectedListener.onItemSelected( parent, view, position, itemId, id );
		}
	}

	public void performOnFocusChangeListener( View view, int position, boolean hasFocus ) {
		if( mOnFocusChangeListener != null ) {
			mOnFocusChangeListener.onFocusChange( view, position, hasFocus );
		}
	}
}
