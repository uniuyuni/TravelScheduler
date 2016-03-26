package com.flug.android.travelscheduler;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ItemViewHolder {
	int			position = -1;

	TextView	tv_date;
	Spinner		sp_item;
	EditText	et_cost;
	EditText	et_start_name;
	EditText	et_end_name;
	EditText	et_use;
	EditText	et_desc;
	TextView	tv_start_time;
	TextView	tv_end_time;
	EditText	et_memo;
	Button		bt_date;
	Button		bt_start_time;
	Button		bt_end_time;

	TextView	tv_use;
	TextView	tv_start_name;
	TextView	tv_end_name;
	TextView	tv_start_time_title;
	TextView	tv_end_time_title;
}
