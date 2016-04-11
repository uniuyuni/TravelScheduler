package com.flug.android.travelscheduler;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class EntryCursorAdapter extends ResourceCursorAdapter {

	private class FocusOutManager implements View.OnFocusChangeListener {

		private View mLastFocusView = null;
		private View.OnFocusChangeListener mOnFocusChangeListener = null;

		public View getLastFocusView() {
			return mLastFocusView;
		}

		@Override
		public void onFocusChange( View view, boolean hasFocus ) {
			setFocusChange( view, hasFocus );
		}

		// フォーカスチェンジを監視
		public void setFocusChange( View view, boolean hasFocus ) {
			if(	// (mLastFocusPosition != position)
			(mLastFocusView != view) ) {
				if( (mOnFocusChangeListener != null) && (mLastFocusView != null) ) {
					mOnFocusChangeListener.onFocusChange( mLastFocusView, false );
				}
				mLastFocusView = view;
			}
		}

		public void setFocusChangeListener( View.OnFocusChangeListener listener ) {
			mOnFocusChangeListener = listener;
		}
	}

	private FocusOutManager mFocusOutManager;
	private Context mContext;

	public EntryCursorAdapter( Context context, int layout, Cursor c, int flags ) {
		super( context, layout, c, flags );
		mContext = context;
		mFocusOutManager = new FocusOutManager();
	}

	// 最後にフォーカスを持っていたビューの取得
	public View getLastFocusView() {
		return mFocusOutManager.getLastFocusView();
	}

	// 全子要素にリスナーと position を設定
	private void setListenerAndPositionToView( View view, int position ) {
		if( view instanceof ViewGroup ) {
			ViewGroup viewGroup = (ViewGroup)view;
			for( int i = 0; i < viewGroup.getChildCount(); ++i ) {
				setListenerAndPositionToView( viewGroup.getChildAt( i ), position );
			}
		}

		// 例外検索
		int id = view.getId();
		if( (id == R.id.tv_date) || (id == R.id.tv_start_time) || (id == R.id.tv_end_time) || (id == R.id.ll_entry) ) {
		} else {
			view.setTag( position );
			view.setOnFocusChangeListener( mFocusOutManager );
		}

	}


	// 全子要素を検索し、指定のビューが見つかったら true を返す
	private boolean findItemViewByView( View view, View findView ) {
		if( view instanceof ViewGroup ) {
			ViewGroup viewGroup = (ViewGroup)view;
			for( int i = 0; i < viewGroup.getChildCount(); ++i ) {
				if( findItemViewByView( viewGroup.getChildAt( i ), findView ) == true ) {
					return true;
				}
			}
		}
		if( view == findView ) {
			return true;
		}

		return false;
	}

	// item 項目の変更に伴うほかの View の変更
	private void changeStringFromItem( View itemView, int itemId ) {
		ItemViewHolder holder = (ItemViewHolder)itemView.getTag();
		Resources resources = mContext.getResources();

		holder.tv_use.setText( resources.getStringArray( R.array.sa_use )[itemId] );
//		holder.tv_start_name.setText( resources.getStringArray( R.array.sa_start_name )[itemId] );
//		holder.tv_end_name.setText( resources.getStringArray( R.array.sa_end_name )[itemId] );
//		holder.tv_start_time_title.setText( resources.getStringArray( R.array.sa_start_time_title )[itemId] );
//		holder.tv_end_time_title.setText( resources.getStringArray( R.array.sa_end_time_title )[itemId] );
	}

	// 以下何回も何回も呼ばれるのでフラグで振り分けたくなるが
	// リサイクルされてくるので再設定しまくるので正解
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		// リサイクルされた Item にフォーカスがあっているものがあったら
		// フォーカスが外れたことを知らせる
		if( convertView != null ) {
			if( findItemViewByView( convertView, mFocusOutManager.getLastFocusView() ) ) {
				mFocusOutManager.setFocusChange( null, false );
			}
		}

		// 現在のビュー（リストビュー内の Item）
		final View itemView = super.getView( position, convertView, parent );

		// 親のListView
		final EntriesListView entriesListView = (EntriesListView)parent;

		// リストビュー内の位置情報設定
		ItemViewHolder holder = (ItemViewHolder)itemView.getTag();
		holder.position = position;

		// フォーカスチェンジが呼ばれまくるのをなんとかしてまとめる
		setListenerAndPositionToView( itemView, position );
		mFocusOutManager.setFocusChangeListener( entriesListView );

		// アイテムクリック設定
		itemView.setOnLongClickListener( new View.OnLongClickListener() {

			@Override
			public boolean onLongClick( View itemView ) {
				int position = ((ItemViewHolder)itemView.getTag()).position;
				Log.d( "ClickListener", "position=" + position );
				entriesListView.performItemLongClick( itemView, position, itemView.getId() );

				return true;
			}
		} );

		// スピナーの変更イベントリスナー設定
		holder.sp_item.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
				if( parent.isFocusable() == false ) {
					parent.setFocusable( true );
				} else {
					changeStringFromItem( itemView, position );
					entriesListView.performItemSelected( parent, view, position, id );
				}
			}

			@Override
			public void onNothingSelected( AdapterView<?> parent ) {
			}

		} );

		// entry_item内のボタンクリックリスナー設定、親のリストビューに回す
		holder.bt_date.setOnClickListener( entriesListView );
		holder.bt_start_time.setOnClickListener( entriesListView );
		holder.bt_end_time.setOnClickListener( entriesListView );

		return itemView;
	}

	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parent ) {
		View itemView = super.newView( context, cursor, parent );

		// 情報設定ホルダー作成
		ItemViewHolder holder = new ItemViewHolder();

		// ホルダーの内容設定、ただし mPosition は getView() で行っている
		holder.tv_date				= (TextView)itemView.findViewById( R.id.tv_date );			// 日付
		holder.sp_item				= (Spinner)itemView.findViewById( R.id.sp_item );			// 項目
		holder.et_cost				= (EditText)itemView.findViewById( R.id.et_cost );			// コスト
		holder.et_start_name		= (EditText)itemView.findViewById( R.id.et_start_name );	// 移動元
		holder.et_end_name			= (EditText)itemView.findViewById( R.id.et_end_name );		// 移動先
		holder.et_use				= (EditText)itemView.findViewById( R.id.et_use );			// 使用
		holder.et_desc				= (EditText)itemView.findViewById( R.id.et_desc );			// 追加説明
		holder.tv_start_time		= (TextView)itemView.findViewById( R.id.tv_start_time );	// 移動元
		holder.tv_end_time			= (TextView)itemView.findViewById( R.id.tv_end_time );		// 移動先
		holder.et_memo				= (EditText)itemView.findViewById( R.id.et_memo );			// メモ
		holder.bt_date				= (Button)itemView.findViewById( R.id.bt_date );			// 日付ボタン
		holder.bt_start_time		= (Button)itemView.findViewById( R.id.bt_start_time );	// 開始時刻ボタン
		holder.bt_end_time			= (Button)itemView.findViewById( R.id.bt_end_time );		// 終了時刻ボタン
		holder.tv_use				= (TextView)itemView.findViewById( R.id.tv_use );
		holder.tv_start_name		= (TextView)itemView.findViewById( R.id.tv_start_name );
		holder.tv_end_name			= (TextView)itemView.findViewById( R.id.tv_end_name );
		holder.tv_start_time_title	= (TextView)itemView.findViewById( R.id.tv_start_time_title );
		holder.tv_end_time_title	= (TextView)itemView.findViewById( R.id.tv_end_time_title );

		// ホルダー設定
		itemView.setTag( holder );

		return itemView;
	}

	@Override
	public void bindView( View itemView, Context context, Cursor cursor ) {

		// リストビュー内の位置情報設定
		ItemViewHolder holder = (ItemViewHolder)itemView.getTag();

		// 日付
		Date date = new Date( cursor.getInt( cursor.getColumnIndex( "date" ) ) );
		holder.tv_date.setTag( date.getSerial() );
		holder.tv_date.setText( date.toString() );

		// 項目
		int item_id = cursor.getInt( cursor.getColumnIndex( "item" ) );
		holder.sp_item.setSelection( item_id );
		changeStringFromItem( itemView, item_id );

		// コスト
		int cost = cursor.getInt( cursor.getColumnIndex( "cost" ) );
		holder.et_cost.setText( "" + cost );

		// 移動元
		String start_name = cursor.getString( cursor.getColumnIndex( "start_name" ) );
		holder.et_start_name.setText( start_name );

		// 移動先
		String end_name = cursor.getString( cursor.getColumnIndex( "end_name" ) );
		holder.et_end_name.setText( end_name );

		// 使用
		String use = cursor.getString( cursor.getColumnIndex( "use" ) );
		holder.et_use.setText( use );

		// 追加説明
		String desc = cursor.getString( cursor.getColumnIndex( "desc" ) );
		holder.et_desc.setText( desc );

		// 移動元時間
		Time start_time = new Time( cursor.getInt( cursor.getColumnIndex( "start_time" ) ) );
		holder.tv_start_time.setTag( start_time.getSerial() );
		holder.tv_start_time.setText( start_time.toString() );

		// 移動先時間
		Time end_time = new Time( cursor.getInt( cursor.getColumnIndex( "end_time" ) ) );
		holder.tv_end_time.setTag( end_time.getSerial() );
		holder.tv_end_time.setText( end_time.toString() );

		// メモ
		String memo = cursor.getString( cursor.getColumnIndex( "memo" ) );
		holder.et_memo.setText( memo );
	}

}
