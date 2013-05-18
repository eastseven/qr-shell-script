package com.quickride.customer.report.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.util.graphics.ViewUtil;
import ac.mm.android.view.ScrollPagingListView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.common.util.DateUtil;
import com.quickride.customer.endpoint.EndpointClient;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-1-30
 * @version 1.0
 */

public class MyOrderListActivity extends MGestureSwitchPageActivity {
	public static String Tag = "MyOrderListActivity";

	private String waitExecuteOrderTabString = "waitExecuteOrderTab";
	private String inServiceOrderTabString = "inServiceOrderTab";
	private String finishedOrderTabString = "finishedOrderTab";
	private String canceledOrderTabString = "canceledOrderTab";

	// private RentOrderDao orderDao;

	private TabHost tabHost;

	private ScrollPagingListView.Adapter<Map<String, ?>> orderListAdapter;

	private EndpointClient endpointClient;

	private DateUtil dateUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.order_list);

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

		// orderDao = new RentOrderDao(this);

		setupTabHost();
	}

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(R.id.order_tabhost);
		tabHost.setup();

		TabSpec waitExecuteOrderTab = tabHost.newTabSpec(waitExecuteOrderTabString)
				.setIndicator(getString(R.string.wait_execute)).setContent(R.id.wait_execute_order);
		tabHost.addTab(waitExecuteOrderTab);

		TabSpec inServiceOrderTab = tabHost.newTabSpec(inServiceOrderTabString)
				.setIndicator(getString(R.string.in_service)).setContent(R.id.in_service_order);
		tabHost.addTab(inServiceOrderTab);

		TabSpec finishedOrderTab = tabHost.newTabSpec(finishedOrderTabString)
				.setIndicator(getString(R.string.finished)).setContent(R.id.finished_order);
		tabHost.addTab(finishedOrderTab);

		TabSpec canceledOrderTab = tabHost.newTabSpec(canceledOrderTabString)
				.setIndicator(getString(R.string.canceled)).setContent(R.id.canceled_order);
		tabHost.addTab(canceledOrderTab);

		ViewUtil.textCentered(tabHost.getTabWidget());

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (waitExecuteOrderTabString.equals(tabId)) {
					orderListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.wait_execute_order_list), tabId);
				} else if (inServiceOrderTabString.equals(tabId)) {
					orderListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.in_service_order_list), tabId);
				} else if (finishedOrderTabString.equals(tabId)) {
					orderListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.finished_order_list), tabId);
				} else if (canceledOrderTabString.equals(tabId)) {
					orderListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.canceled_order_list), tabId);
				}
			}
		});

		orderListAdapter = setupOrderListViewAdapter((ScrollPagingListView) findViewById(R.id.wait_execute_order_list),
				waitExecuteOrderTabString);
	}

	private ScrollPagingListView.Adapter<Map<String, ?>> setupOrderListViewAdapter(
			final ScrollPagingListView orderListView, final String tabId) {
		TextView blankPageAlertView = new TextView(this);
		if (waitExecuteOrderTabString.equals(tabId)) {
			blankPageAlertView.setText("您还没有待服务的订单");
		} else if (inServiceOrderTabString.equals(tabId)) {
			blankPageAlertView.setText("您还没有服务中的订单");
		} else if (finishedOrderTabString.equals(tabId)) {
			blankPageAlertView.setText("您还没有已完成的订单");
		} else if (canceledOrderTabString.equals(tabId)) {
			blankPageAlertView.setText("您还没有已取消的订单");
		}

		blankPageAlertView.setTextSize(18);
		blankPageAlertView.setGravity(Gravity.CENTER);
		blankPageAlertView.setTextColor(Color.BLACK);
		orderListView.setBlankPageAlertFooterView(blankPageAlertView);
		orderListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int itemId, long position) {
				Map<String, ?> order = (Map<String, ?>) adapterView.getAdapter().getItem(itemId);

				Intent intent = new Intent(MyOrderListActivity.this, MyOrderDetailActivity.class);
				intent.putExtra("order", (Serializable) order);

				startActivityForResult(intent, 0);
			}
		});

		ScrollPagingListView.Adapter<Map<String, ?>> adapter = new ScrollPagingListView.Adapter<Map<String, ?>>() {
			@Override
			protected List<Map<String, ?>> getNextPageItemDataList(int arg0, int pageNo) {
				Map<String, ?> result = null;

				Map<String, String> listOrdersInfo = new HashMap<String, String>();
				listOrdersInfo.put("page_pageNo", String.valueOf(pageNo));
				listOrdersInfo.put("page_pageSize", "10");

				if (waitExecuteOrderTabString.equals(tabId)) {
					listOrdersInfo.put("page_orderBy", "pickupTime_asc");
					listOrdersInfo.put("status",
							String.valueOf(StatusCode.BOOK_CAR) + "," + String.valueOf(StatusCode.BOOKED_CAR));

					result = endpointClient.listOrders(listOrdersInfo);
				} else if (inServiceOrderTabString.equals(tabId)) {
					listOrdersInfo.put("page_orderBy", "pickupTime_asc");
					listOrdersInfo.put("status", String.valueOf(StatusCode.IN_SERVICE));

					result = endpointClient.listOrders(listOrdersInfo);
				} else if (finishedOrderTabString.equals(tabId)) {
					listOrdersInfo.put("page_orderBy", "alias0.time_desc");
					listOrdersInfo.put("status", String.valueOf(StatusCode.FINISHED_ORDER));

					result = endpointClient.listOrders(listOrdersInfo);
				} else if (canceledOrderTabString.equals(tabId)) {
					listOrdersInfo.put("page_orderBy", "alias0.time_desc");
					listOrdersInfo.put("status", String.valueOf(StatusCode.UNSUBSCRIBED_CAR));

					result = endpointClient.listOrders(listOrdersInfo);
				}

				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))
						|| null == result.get("totalPage")) {
					return null;
				}

				int totalPage = (Integer) result.get("totalPage");

				if (pageNo >= totalPage) {
					orderListView.notifyIsLastPage();
				}

				List<Map<String, ?>> orderList = (List<Map<String, ?>>) result.get("orderList");

				Log.d(Tag, "totalPage=" + totalPage + ", nextPageCount=" + pageNo + ", orderList=" + orderList);

				return orderList;
			}

			@Override
			protected View getView(int arg0, Map<String, ?> order, View arg2, ViewGroup arg3) {
				LinearLayout orderItem = (LinearLayout) getLayoutInflater().inflate(R.layout.order_item, null);

				Integer orderStatus = (Integer) order.get("orderStatus");

				((TextView) orderItem.findViewById(R.id.rent_car_status)).setText(getString(R.string.rent_car_status)
						+ "：" + getResources().getStringArray(R.array.order_status)[orderStatus - 2]);

				if (orderStatus == StatusCode.FINISHED_ORDER) {
					Integer grade = (Integer) order.get("rate");
					if (null == grade || grade == 0) {
						((TextView) orderItem.findViewById(R.id.grade_text)).setVisibility(View.VISIBLE);
					} else {
						RatingBar gradeRatingBar = (RatingBar) orderItem.findViewById(R.id.grade_rating_bar);
						gradeRatingBar.setRating(grade);
						gradeRatingBar.setVisibility(View.VISIBLE);
					}
				} else {
					TextView payTextView = ((TextView) orderItem.findViewById(R.id.pay));

					Integer payType = (Integer) order.get("payType");
					Integer isPay = (Integer) order.get("isPay");

					if (null == payType || (1 == payType && 0 == isPay)) {
						if (null != payType && orderStatus == StatusCode.UNSUBSCRIBED_CAR) {
							payTextView.setText(getResources().getStringArray(R.array.pay_type)[payType]);
						}
					} else if (1 == isPay) {
						payTextView.setText(R.string.paied);

						payTextView.setTextColor(getResources().getColor(R.color.littlegreen));
					} else {
						payTextView.setText(getResources().getStringArray(R.array.pay_type)[payType]);

						payTextView.setTextColor(getResources().getColor(R.color.littlegreen));
					}

					payTextView.setVisibility(View.VISIBLE);
				}

				TextView carTimeTextView = (TextView) orderItem.findViewById(R.id.order_car_time);

				if (orderStatus == StatusCode.BOOK_CAR || orderStatus == StatusCode.BOOKED_CAR
						|| orderStatus == StatusCode.IN_SERVICE) {
					carTimeTextView.setText(getString(R.string.get_on_car_time) + "："
							+ dateUtil.getDateString(Long.valueOf((String) order.get("pickupTime"))));
				} else if (orderStatus == StatusCode.UNSUBSCRIBED_CAR) {
					carTimeTextView.setText(getString(R.string.order_car_time) + "："
							+ dateUtil.getDateString(Long.valueOf((String) order.get("orderTime"))));
				} else if (orderStatus == StatusCode.FINISHED_ORDER) {
					carTimeTextView.setText(getString(R.string.arrived_time) + "："
							+ dateUtil.getDateString(Long.valueOf((String) order.get("unloadTime"))));
				}

				TextView carLocationTextView = (TextView) orderItem.findViewById(R.id.get_on_car_location);

				if (orderStatus == StatusCode.BOOK_CAR || orderStatus == StatusCode.BOOKED_CAR) {
					carLocationTextView.setText(getString(R.string.start_place) + "：" + order.get("pickupAddress"));
				} else {
					carLocationTextView.setText(getString(R.string.end_place) + "：" + order.get("unloadAddress"));
				}

				return orderItem;
			}

			@Override
			protected void onPageChanged(List<Map<String, ?>> arg0) {
				// TODO Auto-generated method stub
			}
		};

		orderListView.setAdapter(adapter);

		return adapter;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case RESULT_OK:
			Map<String, ?> order = (Map<String, ?>) data.getSerializableExtra("order");

			List<Map<String, ?>> rentOrderList = orderListAdapter.getItemDataList();

			for (Map<String, ?> rentOrder : rentOrderList) {
				if (rentOrder.get("orderNo").equals(order.get("orderNo"))) {
					if (data.getBooleanExtra("unsubscribe", false)) {
						rentOrderList.remove(rentOrder);
					} else {
						rentOrderList.set(rentOrderList.indexOf(rentOrder), order);
					}

					orderListAdapter.notifyDataSetChanged();

					break;
				}
			}

			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// if (null != orderDao) {
		// orderDao.close();
		// }
	}
}
