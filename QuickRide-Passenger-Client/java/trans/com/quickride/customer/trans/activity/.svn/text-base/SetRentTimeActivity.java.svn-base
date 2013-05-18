package com.quickride.customer.trans.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ac.mm.android.map.SerializablePoiItem;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.common.util.DateUtil;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-12-14
 * @version 1.0
 */

public class SetRentTimeActivity extends MGestureSwitchPageActivity {
	private int bookIntervalTime = 1;

	private Calendar dateAndTimeCalendar;

	private SerializablePoiItem startPoiItem;
	private SerializablePoiItem endPoiItem;
	// private SerializablePoiItem myPoiItem;

	private ProgressDialog progressDialog;

	private Button meetMeButton;
	private Button bookRentTimeButton;

	private DateUtil dateUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_rent_time);

		dateUtil = new DateUtil(this);

		String serviceTime = getIntent().getStringExtra("serviceTime");
		if (null != serviceTime) {
			((TextView) findViewById(R.id.service_time)).setText(serviceTime);
		}

		startPoiItem = (SerializablePoiItem) getIntent().getSerializableExtra("startPoiItem");
		endPoiItem = (SerializablePoiItem) getIntent().getSerializableExtra("endPoiItem");
		// myPoiItem = (SerializablePoiItem)
		// getIntent().getSerializableExtra("myPoiItem");

		meetMeButton = (Button) findViewById(R.id.meet_me);
		meetMeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dateAndTimeCalendar = null;

				showRentInfoDialog();
			}
		});

		final TimePickerDialog.OnTimeSetListener timePickerOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Calendar tempDateAndTimeCalendar = Calendar.getInstance();
				tempDateAndTimeCalendar.set(Calendar.YEAR, dateAndTimeCalendar.get(Calendar.YEAR));
				tempDateAndTimeCalendar.set(Calendar.MONTH, dateAndTimeCalendar.get(Calendar.MONTH));
				tempDateAndTimeCalendar.set(Calendar.DAY_OF_MONTH, dateAndTimeCalendar.get(Calendar.DAY_OF_MONTH));
				tempDateAndTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay - bookIntervalTime);
				tempDateAndTimeCalendar.set(Calendar.MINUTE, minute);

				if (Calendar.getInstance().compareTo(tempDateAndTimeCalendar) > 0) {
					showTimePickerDialog(this);

					Toast.makeText(SetRentTimeActivity.this, R.string.book_riding_time_error, Toast.LENGTH_SHORT)
							.show();

					return;
				}

				dateAndTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				dateAndTimeCalendar.set(Calendar.MINUTE, minute);

				showRentInfoDialog();
			}
		};

		final DatePickerDialog.OnDateSetListener datePickerOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar tempDateAndTimeCalendar = Calendar.getInstance();
				tempDateAndTimeCalendar.set(Calendar.YEAR, year);
				tempDateAndTimeCalendar.set(Calendar.MONTH, monthOfYear);
				tempDateAndTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				tempDateAndTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
				tempDateAndTimeCalendar.set(Calendar.MINUTE, 0);
				tempDateAndTimeCalendar.set(Calendar.MILLISECOND, 0);

				dateAndTimeCalendar = Calendar.getInstance();
				dateAndTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
				dateAndTimeCalendar.set(Calendar.MINUTE, 0);
				dateAndTimeCalendar.set(Calendar.MILLISECOND, 0);

				if (dateAndTimeCalendar.compareTo(tempDateAndTimeCalendar) > 0) {
					showDatePickerDialog(this);

					Toast.makeText(SetRentTimeActivity.this, R.string.book_riding_date_error, Toast.LENGTH_SHORT)
							.show();

					return;
				}

				dateAndTimeCalendar.set(Calendar.YEAR, year);
				dateAndTimeCalendar.set(Calendar.MONTH, monthOfYear);
				dateAndTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				showTimePickerDialog(timePickerOnTimeSetListener);
			}
		};

		bookRentTimeButton = (Button) findViewById(R.id.order_car_time);
		bookRentTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(datePickerOnDateSetListener);
			}
		});

		Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRentInfoDialog();
			}
		});

		Button resetButton = (Button) findViewById(R.id.cancel);
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dateAndTimeCalendar = null;

				findViewById(R.id.rootLayout).setVisibility(View.GONE);

				meetMeButton.setVisibility(View.VISIBLE);
				bookRentTimeButton.setVisibility(View.VISIBLE);
			}
		});
	}

	private LinearLayout createListViewItem(String title, String content) {
		TextView titleTextView = new TextView(this);
		titleTextView.setText(title);
		titleTextView.setTextSize(20);
		titleTextView.setTextColor(Color.BLACK);

		TextView contentTextView = new TextView(this);
		contentTextView.setText(content);
		contentTextView.setTextSize(14);
		contentTextView.setTextColor(Color.BLACK);

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setMinimumHeight(60);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		linearLayout.setPadding(12, 0, 12, 0);

		if (title != null) {
			linearLayout.addView(titleTextView);
		}

		if (content != null) {
			linearLayout.addView(contentTextView);
		}

		return linearLayout;
	}

	private void showRentInfoDialog() {
		final String rentTime = dateUtil.getDateString(null == dateAndTimeCalendar ? 0 : dateAndTimeCalendar.getTime()
				.getTime());

		List<LinearLayout> itemViewList = new ArrayList<LinearLayout>();
		itemViewList.add(createListViewItem(getString(R.string.get_on_car_time) + "：", rentTime));
		itemViewList.add(createListViewItem(getString(R.string.start_place) + "：", startPoiItem.getTitle()));
		itemViewList.add(createListViewItem(getString(R.string.end_place) + "： ", endPoiItem.getTitle()));

		ListView listView = new ListView(this);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setBackgroundColor(Color.WHITE);
		listView.setAdapter(new ac.mm.android.view.adapter.ListAdapter<LinearLayout>(itemViewList));

		switchToSetTimePage(rentTime);

		new AlertDialog.Builder(SetRentTimeActivity.this).setTitle(R.string.confirm_rent_info)
				.setIcon(R.drawable.ic_menu_more).setView(listView)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent(SetRentTimeActivity.this, ChoseCarActivity.class);
						intent.putExtras(getIntent());
						intent.putExtra("pickupTime", null == dateAndTimeCalendar ? 0 : dateAndTimeCalendar.getTime()
								.getTime());

						startActivity(intent);
					}
				}).setNegativeButton(R.string.reset, null).setCancelable(false).show();
	}

	private void switchToSetTimePage(final String rentTime) {
		meetMeButton.setVisibility(View.GONE);
		bookRentTimeButton.setVisibility(View.GONE);
		((TextView) findViewById(R.id.time)).setText(rentTime);
		findViewById(R.id.rootLayout).setVisibility(View.VISIBLE);
	}

	private void showTimePickerDialog(final TimePickerDialog.OnTimeSetListener timePickerOnTimeSetListener) {
		Calendar dateAndTimeCalendar = Calendar.getInstance();
		dateAndTimeCalendar.set(Calendar.HOUR_OF_DAY, dateAndTimeCalendar.get(Calendar.HOUR_OF_DAY) + bookIntervalTime);

		new TimePickerDialog(SetRentTimeActivity.this, timePickerOnTimeSetListener,
				dateAndTimeCalendar.get(Calendar.HOUR_OF_DAY), dateAndTimeCalendar.get(Calendar.MINUTE), true).show();
	}

	private void showDatePickerDialog(final DatePickerDialog.OnDateSetListener datePickerOnDateSetListener) {
		Calendar dateAndTimeCalendar = Calendar.getInstance();

		new DatePickerDialog(SetRentTimeActivity.this, datePickerOnDateSetListener,
				dateAndTimeCalendar.get(Calendar.YEAR), dateAndTimeCalendar.get(Calendar.MONTH),
				dateAndTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != progressDialog) {
			progressDialog.dismiss();
		}
	}
}
