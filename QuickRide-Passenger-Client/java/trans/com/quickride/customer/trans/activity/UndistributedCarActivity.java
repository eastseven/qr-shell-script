package com.quickride.customer.trans.activity;

import java.util.HashMap;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MBaseActivity;
import com.quickride.customer.common.domain.MenuExitable;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.report.activity.MyOrderListActivity;
import com.quickride.customer.trans.view.MenuButton;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-3-11
 * @version 1.0
 */

public class UndistributedCarActivity extends MBaseActivity implements MenuExitable {
	private Button waitExecuteOrderRemindButton;

	private MenuButton menuButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.undistributed_car);

		menuButton = (MenuButton) findViewById(R.id.menu);

		waitExecuteOrderRemindButton = (Button) findViewById(R.id.alert);
		waitExecuteOrderRemindButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UndistributedCarActivity.this, MyOrderListActivity.class);

				startActivity(intent);
			}
		});

		Button orderButton = (Button) findViewById(R.id.my_orders);
		orderButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UndistributedCarActivity.this, MyOrderListActivity.class);

				startActivity(intent);
			}
		});

		Button rentCarButton = (Button) findViewById(R.id.rent_car);
		rentCarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UndistributedCarActivity.this, RentCarWithMapAbcActivity.class);

				startActivity(intent);

				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		getOrderInfo();
	}

	private void getOrderInfo() {
		waitExecuteOrderRemindButton.setText(getString(R.string.loading));

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> listOrdersInfo = new HashMap<String, String>();
				listOrdersInfo.put("page_pageNo", "1");
				listOrdersInfo.put("page_pageSize", "1");
				listOrdersInfo.put("status",
						String.valueOf(StatusCode.BOOK_CAR) + "," + String.valueOf(StatusCode.BOOKED_CAR));

				return listOrders(listOrdersInfo);
			}

			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					new AlertDialog.Builder(UndistributedCarActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									getOrderInfo();
								}
							}).setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									exit(true);
								}
							}).setCancelable(false).show();

					return;
				}

				Integer waitExecuteOrderCount = (Integer) result.get("totalCount");

				if (null == waitExecuteOrderCount) {
					return;
				}

				int waitExecuteOrderCountPlaces = waitExecuteOrderCount.toString().length();

				SpannableString sp = new SpannableString(getString(R.string.wait_execute_order_remind,
						waitExecuteOrderCount));
				sp.setSpan(new ForegroundColorSpan(Color.RED), 2, 2 + waitExecuteOrderCountPlaces,
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2 + waitExecuteOrderCountPlaces,
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

				waitExecuteOrderRemindButton.setText(sp);
			}
		}.execute();
	}

	@Override
	public void onBackPressed() {
		if (!menuButton.isShowing()) {
			exit(null);
		} else {
			menuButton.dismiss();
		}
	}

	@Override
	public void exit(final Boolean input) {
		new AlertDialog.Builder(this).setIcon(R.drawable.ic_menu_more).setTitle(R.string.exit_app)
				.setMessage(R.string.confirm_exit)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (menuButton.isShowing()) {
							menuButton.dismiss();
						}

						new EndpointClient(UndistributedCarActivity.this) {
							@Override
							protected Map<String, Object> doInBackground(Void... arg0) {
								return logout();
							}

							@Override
							protected void onEndpointClientPostExecute(Map<String, Object> result) {
								// TODO Auto-generated method stub
							}
						}.execute();

						((ExpandApplication) getApplication()).exitApp();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						if (null != input && true == input) {
							getOrderInfo();
						}
					}
				}).setCancelable(false).show();
	}
}
