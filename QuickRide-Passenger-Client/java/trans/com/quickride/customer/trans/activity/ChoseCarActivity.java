package com.quickride.customer.trans.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.map.SerializablePoiItem;
import ac.mm.android.util.download.image.ImageDownloader;
import ac.mm.android.view.ScrollPagingListView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.common.util.DateUtil;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.payment.activity.ChosePaymentTypeActivity;
import com.quickride.customer.trans.database.dao.AddressDao;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2011-12-15
 * @version 1.0
 */

public class ChoseCarActivity extends MGestureSwitchPageActivity {
	private AddressDao addressDao;

	private ImageDownloader imageDownloader;

	private SerializablePoiItem startPoiItem;
	private SerializablePoiItem endPoiItem;
	private SerializablePoiItem myPoiItem;
	private long pickupTime;

	private ScrollPagingListView carTypeListView;

	private DateUtil dateUtil;

	private EndpointClient endpointClient;

	private String tempLeaseId;

	private static final int ERR_MESSAGE = 1001;
	private static final int NULL_MESSAGE = 1002;

	private Handler handler;

	private class MHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}

			switch (msg.what) {
			case NULL_MESSAGE:
				new AlertDialog.Builder(ChoseCarActivity.this).setIcon(R.drawable.alert).setTitle(R.string.server_busy)
						.setMessage(R.string.confirm_retry)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								setupCarTypeListView();
							}
						}).setNegativeButton(R.string.cancel, null).create().show();

				break;

			case ERR_MESSAGE:
				new AlertDialog.Builder(ChoseCarActivity.this).setIcon(R.drawable.alert).setTitle("超出服务时间")
						.setMessage((String) msg.obj)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								finish();
							}
						}).setOnCancelListener(new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								finish();
							}
						}).show();

				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chose_car_type);

		handler = new MHandler();

		imageDownloader = ((ExpandApplication) getApplication()).getImageDownloader();

		endpointClient = new EndpointClient(this) {
			@Override
			protected void onEndpointClientPostExecute(Map<String, Object> result) {
				// TODO Auto-generated method stub
			}

			@Override
			protected Map<String, Object> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		dateUtil = new DateUtil(this);

		startPoiItem = (SerializablePoiItem) getIntent().getSerializableExtra("startPoiItem");
		endPoiItem = (SerializablePoiItem) getIntent().getSerializableExtra("endPoiItem");
		pickupTime = getIntent().getLongExtra("pickupTime", 0);
		myPoiItem = (SerializablePoiItem) getIntent().getSerializableExtra("myPoiItem");

		addressDao = new AddressDao(this);

		carTypeListView = (ScrollPagingListView) findViewById(R.id.car_type_list);
		TextView blankPageAlertView = new TextView(this);
		blankPageAlertView.setText("没有空闲车辆");
		blankPageAlertView.setTextSize(26);
		blankPageAlertView.setTextColor(Color.BLACK);
		blankPageAlertView.setGravity(Gravity.CENTER);
		carTypeListView.setBlankPageAlertFooterView(blankPageAlertView);

		setupCarTypeListView();
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

	private void setupCarTypeListView() {
		ScrollPagingListView.Adapter<Map<String, ?>> adapter = new ScrollPagingListView.Adapter<Map<String, ?>>() {
			@Override
			protected List<Map<String, ?>> getNextPageItemDataList(int arg0, int pageNo) {
				Map<String, String> rentCarInfo = new HashMap<String, String>();
				rentCarInfo.put("pickupX", String.valueOf(startPoiItem.getLongitudeE6() / 1E6));
				rentCarInfo.put("pickupY", String.valueOf(startPoiItem.getLatitudeE6() / 1E6));

				if (null != myPoiItem) {
					rentCarInfo.put("rentX", String.valueOf(myPoiItem.getLongitudeE6() / 1E6));
					rentCarInfo.put("rentY", String.valueOf(myPoiItem.getLatitudeE6() / 1E6));
				}

				rentCarInfo.put("pickupAddress", startPoiItem.getTitle().replaceAll("\n", ""));
				rentCarInfo.put("pickupTime", String.valueOf(pickupTime));
				rentCarInfo.put("unloadX", String.valueOf(endPoiItem.getLongitudeE6() / 1E6));
				rentCarInfo.put("unloadY", String.valueOf(endPoiItem.getLatitudeE6() / 1E6));
				rentCarInfo.put("unloadAddress", endPoiItem.getTitle().replaceAll("\n", ""));
				rentCarInfo.put("mileage", String.valueOf(getIntent().getIntExtra("mileage", 0)));
				rentCarInfo.put("dischargeMinute", String.valueOf(getIntent().getIntExtra("expectedConsumeTime", 0)));

				Map<String, ?> result = endpointClient.requestLeaseCar(rentCarInfo);

				if (isFinishing()) {
					return null;
				}

				if (null == result) {
					handler.sendMessage(Message.obtain(handler, NULL_MESSAGE));

					return null;
				}

				if (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					handler.sendMessage(Message.obtain(handler, ERR_MESSAGE, (String) result.get("statusMessage")));

					return null;
				}

				carTypeListView.notifyIsLastPage();

				List<Map<String, ?>> carTypeList = (List<Map<String, ?>>) result.get("carTypeList");
				tempLeaseId = (String) result.get("tempLeaseId");

				return carTypeList;
			}

			@Override
			protected View getView(int arg0, Map<String, ?> carType, View arg2, ViewGroup arg3) {
				RelativeLayout carTypeItem = (RelativeLayout) getLayoutInflater().inflate(R.layout.car_type_item, null);

				((TextView) carTypeItem.findViewById(R.id.car_type_name)).setText((CharSequence) carType
						.get("carTypeName"));

				((TextView) carTypeItem.findViewById(R.id.car_type_price)).setText((Integer) carType.get("price")
						+ getString(R.string.yuan));

				TextView favoreMessageTextView = (TextView) carTypeItem.findViewById(R.id.content);
				String favoreMessage = (String) carType.get("favoreMessage");
				if (null == favoreMessage || favoreMessage.trim().length() == 0) {
					favoreMessageTextView.setVisibility(View.GONE);
				} else {
					favoreMessageTextView.setText(favoreMessage);
				}

				imageDownloader.download((String) carType.get("imageLink"),
						(ImageView) carTypeItem.findViewById(R.id.car_type_icon));

				return carTypeItem;
			}

			@Override
			protected void onPageChanged(List<Map<String, ?>> arg0) {
				// TODO Auto-generated method stub
			}
		};

		carTypeListView.setAdapter(adapter);

		carTypeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, final int itemId, long position) {
				final Map<String, ?> carType = (Map<String, ?>) adapterView.getAdapter().getItem(itemId);

				String pickupTimeString = dateUtil.getDateString(getIntent().getLongExtra("pickupTime", 0));

				List<LinearLayout> itemViewList = new ArrayList<LinearLayout>();
				itemViewList.add(createListViewItem(getString(R.string.rent_cost) + "：" + carType.get("price") + " "
						+ getString(R.string.yuan), null));
				itemViewList.add(createListViewItem(getString(R.string.car_type) + "：" + carType.get("carTypeName"),
						null));
				itemViewList.add(createListViewItem(getString(R.string.get_on_car_time) + "：", pickupTimeString));
				itemViewList.add(createListViewItem(getString(R.string.start_place) + "：", startPoiItem.getTitle()));
				itemViewList.add(createListViewItem(getString(R.string.end_place) + "： ", endPoiItem.getTitle()));

				ListView listView = new ListView(ChoseCarActivity.this);
				listView.setCacheColorHint(Color.TRANSPARENT);
				listView.setBackgroundColor(Color.WHITE);
				listView.setAdapter(new ac.mm.android.view.adapter.ListAdapter<LinearLayout>(itemViewList));

				new AlertDialog.Builder(ChoseCarActivity.this).setIcon(R.drawable.ic_menu_more)
						.setTitle(R.string.submit_order).setView(listView)
						.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								submitOrder(tempLeaseId, (String) carType.get("carTypeId"));
							}

						}).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
			}
		});
	}

	private void submitOrder(final String tempLeaseId, final String carTypeId) {
		final ProgressDialog progressDialog = ProgressDialog.show(ChoseCarActivity.this, null, "正在为您配车中，请等待...", true,
				false);

		new EndpointClient(this) {
			@Override
			protected Map<String, Object> doInBackground(Void... arg0) {
				Map<String, String> rentCarInfo = new HashMap<String, String>();
				rentCarInfo.put("tempLeaseId", tempLeaseId);
				rentCarInfo.put("carTypeId", carTypeId);

				return selectLeaseCar(rentCarInfo);
			}

			@Override
			protected void onEndpointClientPostExecute(final Map<String, Object> result) {
				progressDialog.dismiss();

				if (null == result
						|| (!StatusCode.SUCCESS.equals((String) result.get("statusCode"))
								&& !StatusCode.NOT_CAR_ANSWER.equals((String) result.get("statusCode"))
								&& !StatusCode.ORDER_SERVICE_TIME.equals((String) result.get("statusCode")) && !StatusCode.ORDER_PICKUP_TIME
								.equals((String) result.get("statusCode")))) {
					AlertDialog alert = new AlertDialog.Builder(ChoseCarActivity.this).setIcon(R.drawable.alert)
							.setTitle(R.string.server_busy).setMessage(R.string.confirm_retry)
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									submitOrder(tempLeaseId, carTypeId);
								}
							}).setNegativeButton(R.string.cancel, null).create();

					// alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					alert.show();

					return;
				}

				if (StatusCode.NOT_CAR_ANSWER.equals((String) result.get("statusCode"))) {
					AlertDialog alert = new AlertDialog.Builder(ChoseCarActivity.this).setIcon(R.drawable.alert)
							.setTitle("车辆已被预订").setMessage("车辆已被预订，请重新选择车辆")
							.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									setupCarTypeListView();
								}
							}).create();

					// alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					alert.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							setupCarTypeListView();
						}
					});
					alert.show();

					return;
				}

				if (StatusCode.ORDER_SERVICE_TIME.equals((String) result.get("statusCode"))
						|| StatusCode.ORDER_PICKUP_TIME.equals((String) result.get("statusCode"))) {
					AlertDialog alert = new AlertDialog.Builder(ChoseCarActivity.this).setIcon(R.drawable.alert)
							.setTitle("超出服务时间").setMessage(result.get("statusMessage").toString())
							.setPositiveButton(R.string.confirm, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							}).create();
					// alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					alert.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							finish();
						}
					});
					alert.show();

					return;
				}

				addressDao.insertHistoryAddress(startPoiItem.getCity(), startPoiItem.getTitle(),
						startPoiItem.getLatitudeE6(), startPoiItem.getLongitudeE6());
				addressDao.insertHistoryAddress(endPoiItem.getCity(), endPoiItem.getTitle(),
						endPoiItem.getLatitudeE6(), endPoiItem.getLongitudeE6());

				Long pickupTime = Long.valueOf((String) result.get("pickupTime"));

				String pickupTimeString = dateUtil.getDateString(pickupTime);

				final String carNumber = (String) result.get("licenseNo");
				final String carName = (String) result.get("carType");

				List<LinearLayout> itemViewList = new ArrayList<LinearLayout>();

				if (null != carNumber) {
					itemViewList.add(createListViewItem(null,
							getString(R.string.order_id) + " ：" + (String) result.get("orderNo")));
					itemViewList.add(createListViewItem(
							getString(R.string.rent_cost) + "：" + (Integer) result.get("price")
									+ getString(R.string.yuan), null));
					itemViewList.add(createListViewItem(getString(R.string.get_on_car_time) + "：", pickupTimeString));
					itemViewList.add(createListViewItem(getString(R.string.start_place) + "：",
							(String) result.get("pickupAddress")));
					itemViewList.add(createListViewItem(getString(R.string.end_place) + "： ",
							(String) result.get("unloadAddress")));
					itemViewList.add(createListViewItem(getString(R.string.car_type) + "：" + carName, null));
					itemViewList.add(createListViewItem(getString(R.string.car_license) + "：" + carNumber, null));
					itemViewList.add(createListViewItem(
							getString(R.string.predict_mileage) + "：" + (Integer) result.get("mileage")
									+ getString(R.string.km), null));
					itemViewList.add(createListViewItem(
							getString(R.string.driver_grade)
									+ getRatings(Float.valueOf((String) result.get("driverRate"))), null));
					itemViewList.add(createListViewItem(
							getString(R.string.driver_name) + "：" + (String) result.get("driverName"), null));
					itemViewList.add(createListViewItem(
							getString(R.string.driver_id) + "：" + (String) result.get("driverNo"), null));
				} else {
					itemViewList.add(createListViewItem(null,
							getString(R.string.order_id) + " ：" + (String) result.get("orderNo")));
					itemViewList.add(createListViewItem(
							getString(R.string.rent_cost) + "：" + (Integer) result.get("price")
									+ getString(R.string.yuan), null));
					itemViewList.add(createListViewItem(getString(R.string.get_on_car_time) + "：", pickupTimeString));
					itemViewList.add(createListViewItem(getString(R.string.start_place) + "：",
							(String) result.get("pickupAddress")));
					itemViewList.add(createListViewItem(getString(R.string.end_place) + "： ",
							(String) result.get("unloadAddress")));
					itemViewList.add(createListViewItem(getString(R.string.car_type) + "：" + carName, null));
					itemViewList.add(createListViewItem(
							getString(R.string.predict_mileage) + "：" + (Integer) result.get("mileage")
									+ getString(R.string.km), null));
				}

				ListView listView = new ListView(ChoseCarActivity.this);
				listView.setCacheColorHint(Color.TRANSPARENT);
				listView.setBackgroundColor(Color.WHITE);
				listView.setAdapter(new ac.mm.android.view.adapter.ListAdapter<LinearLayout>(itemViewList));

				if (!isFinishing()) {
					AlertDialog alert = new AlertDialog.Builder(ChoseCarActivity.this).setIcon(R.drawable.ic_menu_more)
							.setTitle(R.string.rent_success).setView(listView)
							.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									Intent intent = new Intent(ChoseCarActivity.this, ChosePaymentTypeActivity.class);
									intent.putExtra("order", (Serializable) result);

									startActivity(intent);

									finish();
								}
							}).create();

					alert.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							Intent intent = new Intent(ChoseCarActivity.this, ChosePaymentTypeActivity.class);
							intent.putExtra("order", (Serializable) result);

							startActivity(intent);

							finish();
						}
					});
					// alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					alert.show();

				}
			}
		}.execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != addressDao) {
			addressDao.close();
		}
	}

	private String getRatings(float ratings) {
		StringBuffer ratingsString = new StringBuffer();
		for (int i = 1; i <= 5; i++) {
			for (; i <= ratings; i++) {
				ratingsString.append("★");
			}
			if (i < ratings) {
				ratingsString.append("☆");
			}
		}

		return ratingsString.toString();
	}
}
