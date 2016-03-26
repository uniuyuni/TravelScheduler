package com.flug.android.travelscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String SELECT_SCHEDULES = "select * from schedules";

	// データベース
	SQLiteDatabase mDB;
	Cursor mCursor;

	// リストのアダプター
	SimpleCursorAdapter mAdapter;

	// 長押し時のポップアップメニュー呼び出しクラス
	private class ItemLongClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick( AdapterView<?> parent, View view, final int position, long id ) {

			// PopupMenuのインスタンスを作成
			PopupMenu popup = new PopupMenu( MainActivity.this, view );

			// popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
			popup.getMenuInflater().inflate( R.menu.popup_main, popup.getMenu() );

			// ポップアップメニューを表示
			popup.show();

			// ポップアップメニューのメニュー項目のクリック処理
			popup.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick( MenuItem item ) {

					switch( item.getItemId() ) {
					case R.id.mi_changename:
						showScheduleNameChangeDialog( position );
						break;

					case R.id.mi_delete:
						showScheduleDeleteDialog( position );
						break;
					}

					return true;
				}
			} );

			return true;
		}

	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// db
		MyDbHelper helper = new MyDbHelper( this );
		mDB = helper.getWritableDatabase();

		// select
		mCursor = mDB.rawQuery( SELECT_SCHEDULES, null );

		// adapter生成
		final String[] from = { "schedule_name" };	// 表示するカラム名
		final int[] to = { R.id.tv_schedule_name };	// バインドするViewリソース
		try {
			mAdapter = new SimpleCursorAdapter( this, R.layout.schedule_item, mCursor, from, to, 0 );

		} catch( Exception e ) {
			Log.e( "SimpleCursorAdapter", e.toString() );
		}

		// bindして表示
		ListView lv_schedules = (ListView)findViewById( R.id.lv_schedules );
		lv_schedules.setAdapter( mAdapter );
		lv_schedules.setOnItemLongClickListener( new ItemLongClickListener() );
		lv_schedules.setOnItemClickListener( new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {

				// スケジュールID取得
				mCursor.moveToPosition( position );
				int scheduleId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );
				String scheduleName = mCursor.getString( mCursor.getColumnIndex( "schedule_name" ) );

				// インテント準備
				Intent intent = new Intent( MainActivity.this, EditorActivity.class );
				intent.putExtra( "schedule_id", scheduleId );
				intent.putExtra( "schedule_name", scheduleName );

				// アクティビティ起動
				startActivity( intent );
			}

		} );

		// 追加ボタン
		ImageButton ib_add = (ImageButton)findViewById( R.id.ib_add );
		ib_add.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick( View v ) {
//				showEditDialog();
				showScheduleInsertDialog();
			}
		} );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if( id == R.id.action_settings ) {
			return true;
		}
		return super.onOptionsItemSelected( item );
	}

	// 新規名前入力の表示
	public void showScheduleInsertDialog() {
		final EditText editText = new EditText( this );
		AlertDialog.Builder dialog = new AlertDialog.Builder( this );
		dialog.setTitle( getString( R.string.schedule_insert_title ) );
		dialog.setMessage( getString( R.string.schedule_insert_message) );
		dialog.setView( editText );
		dialog.setPositiveButton( getString( R.string.ok ), new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int whichButton ) {
				String inputStr = editText.getText().toString();
				// mDb.execSQL(
				// "insert into schedule_list(schedule_name) values('" +
				// inputStr + "')" );

				// 登録データ設定
				ContentValues val = new ContentValues();
				val.put( "schedule_name", inputStr );
				// データ登録
				mDB.insert( "schedules", null, val );

				// リストビュー表示の更新
				mCursor = mDB.rawQuery( SELECT_SCHEDULES, null );
				mAdapter.changeCursor( mCursor );

				Toast.makeText( MainActivity.this, String.format(  getString( R.string.schedule_insert_toast ), inputStr ), Toast.LENGTH_SHORT ).show();
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

	// スケジュールエントリダイアログの表示
//	public void showEditDialog() {
//		LayoutInflater inflater = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//		View view = inflater.inflate( R.layout.entry_item, null, false);
//
//		AlertDialog.Builder dialog
//					= new AlertDialog.Builder(this);
//		dialog.setTitle("テキストダイアログ");
//		dialog.setMessage("テキストを入力してください。");
//		dialog.setView(view);
//		dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog,int whichButton) {
//				Log.d( "showEditDialog", "which=" + whichButton );
//			}
//		});
//		dialog.show();
//	}

	// 名前変更の表示
	public void showScheduleNameChangeDialog( int position ) {
		mCursor.moveToPosition( position );
		final int scheduleId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );
		final String scheduleName = mCursor.getString( mCursor.getColumnIndex( "schedule_name" ) );

		final EditText editText = new EditText( this );
		editText.setText( scheduleName );
		AlertDialog.Builder dialog = new AlertDialog.Builder( this );
		dialog.setTitle( getString( R.string.schedule_change_title ) );
		dialog.setMessage( getString( R.string.schedule_change_message ) );
		dialog.setView( editText );
		dialog.setPositiveButton( getString( R.string.ok ), new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int whichButton ) {
				String inputStr = editText.getText().toString();

				if( !scheduleName.equals( inputStr ) ) {

					// 登録データ設定
					ContentValues val = new ContentValues();
					val.put( "schedule_name", inputStr );
					// データ登録
					mDB.update( "schedules", val, "_id=" + scheduleId, null );

					// リストビュー表示の更新
					mCursor = mDB.rawQuery( SELECT_SCHEDULES, null );
					mAdapter.changeCursor( mCursor );

					Toast.makeText( MainActivity.this, getString( R.string.schedule_change_toast ), Toast.LENGTH_SHORT ).show();
				}
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

	// 削除の表示
	public void showScheduleDeleteDialog( int position ) {
		mCursor.moveToPosition( position );
		final int scheduleId = mCursor.getInt( mCursor.getColumnIndex( "_id" ) );
		String scheduleName = mCursor.getString( mCursor.getColumnIndex( "schedule_name" ) );

		AlertDialog.Builder dialog = new AlertDialog.Builder( this );
		dialog.setTitle( getString( R.string.schedule_delete_titele ) );
		dialog.setMessage( String.format( getString( R.string.schedule_delete_message ), scheduleName ) );
		dialog.setPositiveButton( getString( R.string.ok ), new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int whichButton ) {

				// 登録データ削除
				mDB.delete( "schedules", "_id=" + scheduleId, null );

				// エントリーからも削除
				mDB.delete( "entries", "schedule_id=" + scheduleId, null );

				// リストビュー表示の更新
				mCursor = mDB.rawQuery( SELECT_SCHEDULES, null );
				mAdapter.changeCursor( mCursor );

				Toast.makeText( MainActivity.this, getString( R.string.schedule_delete_toast ), Toast.LENGTH_SHORT ).show();
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
