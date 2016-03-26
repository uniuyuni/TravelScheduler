package com.flug.android.travelscheduler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class EntriesListView extends ListView implements View.OnClickListener, View.OnFocusChangeListener {

	// 上書きリスナー
	View.OnClickListener mOnClickListener = null;
	View.OnFocusChangeListener mOnFocusChangeListener = null;
	AdapterView.OnItemSelectedListener mOnItemSelectedListener = null;
	AdapterView.OnItemLongClickListener mOnItemLongClickListener = null;

	public EntriesListView( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
	}

	public EntriesListView( Context context, AttributeSet attrs ) {
		super( context, attrs );
	}

	public EntriesListView( Context context ) {
		super( context );
	}

	// リスナーの呼び出し
	public void performItemSelected( AdapterView<?> parent, View view, int position, long id ) {
		if( mOnItemSelectedListener != null ) {
			mOnItemSelectedListener.onItemSelected( parent, view, position, id );
		}
	}

	public void performItemLongClick( View view, int position, long id ) {
		if( mOnItemLongClickListener != null ) {
			mOnItemLongClickListener.onItemLongClick( this, view, position, id );
		}
	}

	// オンクリックリスナーからの呼び出しを流す
	@Override
	public void onClick( View view ) {
		if( mOnClickListener != null ) {
			mOnClickListener.onClick( view );
		}
	}

	// フォーカスチェンジリスナーの呼び出しを流す
	@Override
	public void onFocusChange( View v, boolean hasFocus ) {
		if( mOnFocusChangeListener != null ) {
			mOnFocusChangeListener.onFocusChange( v, hasFocus );
		}
	}

	// クリックリスナー上書き
	@Override
	public void setOnClickListener( View.OnClickListener listener ) {
		mOnClickListener = listener;
	}

	// アイテムの長押し
	@Override
	public void setOnItemLongClickListener( AdapterView.OnItemLongClickListener listener ) {
		mOnItemLongClickListener = listener;
	}

	// フォーカスチェンジリスナー設定
	@Override
	public void setOnFocusChangeListener( View.OnFocusChangeListener listener ) {
		mOnFocusChangeListener = listener;
	}

	// スピナー用選択リスナー設定
	@Override
	public void setOnItemSelectedListener( AdapterView.OnItemSelectedListener listener ) {
		mOnItemSelectedListener = listener;
	}

}
