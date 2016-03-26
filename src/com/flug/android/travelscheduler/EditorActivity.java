package com.flug.android.travelscheduler;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends Activity {

	// 編集中のスケジュールID
	int mScheduleId;

	// データベース
	SQLiteDatabase mDB;
	Cursor mCursor;

	// リストのアダプター
	EntryCursorAdapter mAdapter;

	// クエリ文字列
	String mSelectEntriesQuery;

	// 長押し時のポップアップメニュー呼び出しクラス
	private class ItemLongClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick( AdapterView<?> parent, View view, final int position, long id ) {

			// PopupMenuのインスタンスを作成
			PopupMenu popup = new PopupMenu( EditorActivity.this, view );

			// popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
			popup.getMenuInflater().inflate( R.menu.popup_editor, popup.getMenu() );

			// ポップアップメニューを表示
			popup.show();

			// ポップアップメニューのメニュー項目のクリック処理
			popup.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick( MenuItem item ) {

					switch( item.getItemId() ) {
					case R.id.mi_delete:
						showEntryDeleteDialog( position );
						break;
					}

					return true;
				}
			} );

			return true;
		}

	}

	// フォーカスチェンジを受け取る
	private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange( View view, boolean hasFocus ) {
			int position = (Integer)view.getTag();

			if( mCursor.getCount() > position ) {
				mCursor.moveToPosition( position );
				int entryId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );

				ViewGroup viewGroup = (ViewGroup)view.getParent();

				ContentValues val = new ContentValues();
//				val.put( "item", holder.sp_item.getSelectedItemId() );

				boolean bUpdate = false;
				switch( view.getId() ) {
				case R.id.et_cost:
					EditText et_cost = (EditText)viewGroup.findViewById( R.id.et_cost );
					val.put( "cost", et_cost.getText().toString() );
					Log.d( "onFocusChange" + view.getId(), "cost" );
					bUpdate = true;
					break;

				case R.id.et_use:
					EditText et_use = (EditText)viewGroup.findViewById( R.id.et_use );
					val.put( "use", et_use.getText().toString() );
					Log.d( "onFocusChange" + view.getId(), "use" );
					bUpdate = true;
					break;

				case R.id.et_desc:
					EditText et_desc = (EditText)viewGroup.findViewById( R.id.et_desc );
					val.put( "desc", et_desc.getText().toString() );
					Log.d( "onFocusChange" + view.getId(), "desc" );
					bUpdate = true;
					break;

				case R.id.et_start_name:
					EditText et_start_name = (EditText)viewGroup.findViewById( R.id.et_start_name );
					val.put( "start_name", et_start_name.getText().toString() );
					Log.d( "onFocusChange" + view.getId(), "start_name" );
					bUpdate = true;
					break;

				case R.id.et_end_name:
					EditText et_end_name = (EditText)viewGroup.findViewById( R.id.et_end_name );
					val.put( "end_name", et_end_name.getText().toString() );
					Log.d( "onFocusChange" + view.getId(), "end_name" );
					bUpdate = true;
					break;

				case R.id.et_memo:
					EditText et_memo = (EditText)viewGroup.findViewById( R.id.et_memo );
					val.put( "memo", et_memo.getText().toString() );
					Log.d( "onFocusChange" + view.getId(), "memo" );
					bUpdate = true;
					break;
				}

				if( bUpdate == true ) {
					mDB.update( "entries", val, "_id=" + entryId, null );

					// リストビュー表示の更新
					mCursor = mDB.rawQuery( mSelectEntriesQuery, null );
					mAdapter.changeCursor( mCursor );
				}
			}
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_editor );

		// インテント取得
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String scheduleName = getString( R.string.title_activity_editor );
		if( extras != null ) {
			mScheduleId = extras.getInt( "schedule_id" );
			scheduleName = extras.getString( "schedule_name" );
		}
		// クエリ文字列作成
		mSelectEntriesQuery = "select * from entries where schedule_id=" + mScheduleId + " order by date, start_time";

		// DB 再取得
		MyDbHelper helper = new MyDbHelper( this );
		mDB = helper.getWritableDatabase();

		// select
		/*
		 * mCursor = mDB.query( "entries", null, "schedule_id=" + scheduleId,
		 * null, null, null, "date, start_time");
		 */
		mCursor = mDB.rawQuery( mSelectEntriesQuery, null );

		// adapter生成
		try {
			mAdapter = new EntryCursorAdapter( this, R.layout.entry_item, mCursor, 0 );

		} catch( Exception e ) {
			Log.e( "EntryCursorAdapter", e.toString() );
		}

		// bindして表示
		EntriesListView lv_entries = (EntriesListView)findViewById( R.id.lv_entries );
		lv_entries.setAdapter( mAdapter );

		// 長押しで表示されるメニュー
		lv_entries.setOnItemLongClickListener( new ItemLongClickListener() );

//		lv_entries.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
//				if( mCursor.getCount() > position ) {
//					if( view.getTag() instanceof ItemViewHolder ) {
//						ItemViewHolder holder = (ItemViewHolder)view.getTag();
//
//						mCursor.moveToPosition( position );
//						int entryId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );
//
//						// データベース更新
//						updateEntryToDB( holder, entryId );
//					}
//				}
//			}
//		} );

		// アイテム内のボタンを処理
		lv_entries.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick( View view ) {
				int position = (Integer)view.getTag();

				if( mCursor.getCount() > position ) {
					mCursor.moveToPosition( position );
					int entryId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );
					// int scheduleId = mCursor.getInt( mCursor.getColumnIndex(
					// "schedule_id" ) );

					switch( view.getId() ) {
					case R.id.bt_date:
						tryUpdateLastFocusView();
						showDatePickerDialog( view, entryId );
						break;

					case R.id.bt_start_time:
						tryUpdateLastFocusView();
						showTimePickerDialog( view, R.id.tv_start_time, "start_time", entryId );
						break;

					case R.id.bt_end_time:
						tryUpdateLastFocusView();
						showTimePickerDialog( view, R.id.tv_end_time, "end_time", entryId );
						break;
					}
				}
			}
		} );


		// EditText の変更を察知する
		lv_entries.setOnFocusChangeListener( mOnFocusChangeListener );

		// Spinner の変更を得る
		lv_entries.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected( AdapterView<?> parent, View view, int itemId, long id ) {
				if( parent.getId() == R.id.sp_item ) {
					int position = (Integer)parent.getTag();

					if( mCursor.getCount() > position ) {
						mCursor.moveToPosition( position );
						int entryId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );

						ContentValues val = new ContentValues();
						val.put( "item", itemId );
						mDB.update( "entries", val, "_id=" + entryId, null );

//						tryUpdateLastFocusView();
					}
				}
			}

			@Override
			public void onNothingSelected( AdapterView<?> parent ) {

			}

		} );

//		lv_schedule_list.setOnItemChangedListener( new EntriesListView.OnItemChangedListener() {
//
//			@Override
//			public void onItemChanged( View view, int position, long id ) {
//				EntryCursorAdapter.ViewHolder holder = (EntryCursorAdapter.ViewHolder)view.getTag();
//
//				mCursor.moveToPosition( position );
//				int entryId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );
//
//				updateEntryToDB( holder, entryId );
//			}
//		} );

		// 一個もエントリーがなかったら一つ作る
		if( mCursor.getCount() <= 0 ) {
			addEntryToDB( 0 );
		}

		// 追加ボタン
		ImageButton ib_add = (ImageButton)findViewById( R.id.ib_add );
		ib_add.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick( View view ) {
				tryUpdateLastFocusView();
				addEntryToDB( mCursor.getCount() );

				Toast.makeText( EditorActivity.this, "追加されました", Toast.LENGTH_SHORT ).show();
			}
		} );

		// 戻るボタン設定
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled( true );
		actionBar.setTitle( scheduleName );
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tryUpdateLastFocusView();
	}


	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch( id ) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected( item );
	}

	private void addEntryToDB( int index ) {
		// 登録データ設定
		ContentValues val = new ContentValues();
		if( index == 0 ) {
			val.put( "schedule_id", mScheduleId );
			val.put( "date", new Date().getSerial() );
			val.put( "item", 0 );
			val.put( "cost", 0 );
			val.put( "use", "" );
			val.put( "desc", "" );
			val.put( "start_name", "" );
			val.put( "end_name", "" );
			val.put( "start_time", new Time( 6, 0 ).getSerial() );
			val.put( "end_time", new Time( 18, 0 ).getSerial() );
			val.put( "memo", "" );
		} else {
			// 前の項目から値を取得してみる
			mCursor.moveToPosition( index - 1 );
			val.put( "schedule_id", mScheduleId );
			val.put( "date", mCursor.getInt( mCursor.getColumnIndex( "date" ) ) );
			val.put( "item", 0 );
			val.put( "cost", 0 );
			val.put( "use", "" );
			val.put( "desc", "" );
			val.put( "start_name", "" );
			val.put( "end_name", "" );
			val.put( "start_time", mCursor.getInt( mCursor.getColumnIndex( "end_time" ) ) );
			Time endTime = new Time( mCursor.getInt( mCursor.getColumnIndex( "end_time" ) ) );
			endTime.setHour( endTime.getHour() + 1 );
			val.put( "end_time", endTime.getSerial() );
			val.put( "memo", "" );
		}
		// データ登録
		mDB.insert( "entries", null, val );

		// リストビュー表示の更新
		mCursor = mDB.rawQuery( mSelectEntriesQuery, null );
		mAdapter.changeCursor( mCursor );
	}

	// 最後にフォーカスを持っていた View の更新をしてみる
	private void tryUpdateLastFocusView() {
		View view = mAdapter.getLastFocusView();
		if( view != null ) {
			mOnFocusChangeListener.onFocusChange( view, false );
		}
	}


	// 項目の保存（一部除く）
//	private void updateEntryToDB( ItemViewHolder holder, int entryId ) {
//		// 保存してないものだけ保存する
//		ContentValues val = new ContentValues();
//		val.put( "item", holder.sp_item.getSelectedItemId() );
//		val.put( "cost", holder.et_cost.getText().toString() );
//		val.put( "use", holder.et_use.getText().toString() );
//		val.put( "desc", holder.et_desc.getText().toString() );
//		val.put( "start_name", holder.et_start_name.getText().toString() );
//		val.put( "end_name", holder.et_end_name.getText().toString() );
//		val.put( "memo", holder.et_memo.getText().toString() );
//		mDB.update( "entries", val, "_id=" + entryId, null );
//	}


	// 日付選択ダイアログの表示
	public void showDatePickerDialog( View view, final int entryId ) {
		TextView tv_date = (TextView)(((ViewGroup)view.getParent()).findViewById( R.id.tv_date ));
		Date date = new Date( (Integer)tv_date.getTag() );

		DatePickerDialog dialog = new DatePickerDialog( EditorActivity.this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet( DatePicker picker, int year, int month, int day ) {
				Date date = new Date( year, month + 1, day );

				// mDB.rawQuery( "update entries set date = " + date.getSerial()
				// + " where _id = " + entryId, null );
				ContentValues val = new ContentValues();
				val.put( "date", date.getSerial() );
				mDB.update( "entries", val, "_id=" + entryId, null );

				// リストビュー表示の更新
				mCursor = mDB.rawQuery( mSelectEntriesQuery, null );
				mAdapter.changeCursor( mCursor );
			}
		}, date.getYear(), date.getMonth() - 1, date.getDay() );
		dialog.setCancelable( false );
		dialog.show();
	}

	// 時間選択ダイアログの表示
	public void showTimePickerDialog( View view, int resId, final String column_name, final int entryId ) {
		TextView tv_time = (TextView)(((ViewGroup)view.getParent()).findViewById( resId ));
		Time time = new Time( (Integer)tv_time.getTag() );

//		TimePickerDialog dialog = new TimePickerDialog( EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
		SelectTimePickerDialog dialog = new SelectTimePickerDialog( EditorActivity.this, new SelectTimePickerDialog.OnTimeSetListener() {
//			public void onTimeSet( TimePicker picker, int hour, int min ) {
			@Override
			public void onTimeSet( int hour, int min ) {
				Time time = new Time( hour, min );

				// mDB.rawQuery( "update entries set " + column_name + " = " + date.getSerial()
				// + " where _id = " + entryId, null );
				ContentValues val = new ContentValues();
				val.put( column_name, time.getSerial() );
				mDB.update( "entries", val, "_id=" + entryId, null );

				// リストビュー表示の更新
				mCursor = mDB.rawQuery( mSelectEntriesQuery, null );
				mAdapter.changeCursor( mCursor );
			}
//		}, time.getHour(), time.getSecond(), true );
		}, time.getHour(), time.getSecond() );
		dialog.setCancelable( false );
		dialog.show();
	}

	// 削除の表示
	public void showEntryDeleteDialog( int position ) {
		mCursor.moveToPosition( position );
		final int entryId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );

		AlertDialog.Builder dialog = new AlertDialog.Builder( this );
		dialog.setTitle( getString( R.string.entry_delete_title ) );
		dialog.setMessage( getString( R.string.entry_delete_message ) );
		dialog.setPositiveButton( getString( R.string.ok ), new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int whichButton ) {

				// エントリーからも削除
				mDB.delete( "entries", "_id=" + entryId, null );

				// リストビュー表示の更新
				mCursor = mDB.rawQuery( mSelectEntriesQuery, null );
				mAdapter.changeCursor( mCursor );

				Toast.makeText( EditorActivity.this, getString( R.string.entry_delete_toast ), Toast.LENGTH_SHORT ).show();
			}
		} );
		dialog.setNegativeButton( getString( R.string.cancel ), new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int whichButton ) {
			}
		} );
		dialog.setCancelable( false );
		dialog.show();
	}

}
