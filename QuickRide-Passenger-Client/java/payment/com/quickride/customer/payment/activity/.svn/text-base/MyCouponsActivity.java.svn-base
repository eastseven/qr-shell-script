package com.quickride.customer.payment.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.mm.android.app.ExpandApplication;
import ac.mm.android.util.download.image.ImageDownloader;
import ac.mm.android.util.graphics.ViewUtil;
import ac.mm.android.view.ScrollPagingListView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.quickride.customer.R;
import com.quickride.customer.common.activity.MGestureSwitchPageActivity;
import com.quickride.customer.common.domain.StatusCode;
import com.quickride.customer.endpoint.EndpointClient;
import com.quickride.customer.report.activity.MyOrderDetailActivity;

/**
 * 类说明：
 * 
 * @author WPM
 * @date 2012-5-16
 * @version 1.0
 */

public class MyCouponsActivity extends MGestureSwitchPageActivity {
	public static String Tag = "MyCouponsActivity";

	private String notUseTabString = "notUseTab";
	private String usedTabString = "usedTab";
	private String expiredOrderTabString = "expiredOrderTab";

	private TabHost tabHost;

	private ScrollPagingListView.Adapter<Map<String, ?>> myCouponsListAdapter;

	private EndpointClient endpointClient;

	private ImageDownloader imageDownloader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.my_coupons);

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

		findViewById(R.id.get_coupons).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyCouponsActivity.this, GetCouponsActivity.class);
				startActivity(intent);
			}
		});

		setupTabHost();
	}

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(R.id.order_tabhost);
		tabHost.setup();

		TabSpec notUseTab = tabHost.newTabSpec(notUseTabString).setIndicator(getString(R.string.not_use))
				.setContent(R.id.wait_execute_order);
		tabHost.addTab(notUseTab);

		TabSpec usedTab = tabHost.newTabSpec(usedTabString).setIndicator(getString(R.string.used))
				.setContent(R.id.finished_order);
		tabHost.addTab(usedTab);

		TabSpec expiredOrderTab = tabHost.newTabSpec(expiredOrderTabString).setIndicator(getString(R.string.expired))
				.setContent(R.id.canceled_order);
		tabHost.addTab(expiredOrderTab);

		ViewUtil.textCentered(tabHost.getTabWidget());

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (notUseTabString.equals(tabId)) {
					myCouponsListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.wait_execute_order_list), tabId);
				} else if (usedTabString.equals(tabId)) {
					myCouponsListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.finished_order_list), tabId);
				} else if (expiredOrderTabString.equals(tabId)) {
					myCouponsListAdapter = setupOrderListViewAdapter(
							(ScrollPagingListView) findViewById(R.id.canceled_order_list), tabId);
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		myCouponsListAdapter = setupOrderListViewAdapter(
				(ScrollPagingListView) findViewById(R.id.wait_execute_order_list), notUseTabString);
	}

	private ScrollPagingListView.Adapter<Map<String, ?>> setupOrderListViewAdapter(
			final ScrollPagingListView couponsListView, final String tabId) {
		TextView blankPageAlertView = new TextView(this);
		if (notUseTabString.equals(tabId)) {
			blankPageAlertView.setText("您没有未使用的券");
		} else if (usedTabString.equals(tabId)) {
			blankPageAlertView.setText("您没有已使用的券");
		} else if (expiredOrderTabString.equals(tabId)) {
			blankPageAlertView.setText("您没有已过期的券");
		}

		blankPageAlertView.setTextSize(18);
		blankPageAlertView.setGravity(Gravity.CENTER);
		blankPageAlertView.setTextColor(Color.BLACK);
		couponsListView.setBlankPageAlertFooterView(blankPageAlertView);

		ScrollPagingListView.Adapter<Map<String, ?>> adapter = new ScrollPagingListView.Adapter<Map<String, ?>>() {
			@Override
			protected List<Map<String, ?>> getNextPageItemDataList(int arg0, int pageNo) {

				Map<String, ?> result = null;

				Map<String, String> listMyCouponsInfo = new HashMap<String, String>();
				listMyCouponsInfo.put("pageNo", String.valueOf(pageNo));
				listMyCouponsInfo.put("pageSize", "10");

				if (notUseTabString.equals(tabId)) {
					listMyCouponsInfo.put("page_orderBy", "getTime_desc");
					listMyCouponsInfo.put("search_EQI_status", String.valueOf(StatusCode.COUPONS_NOT_USE));

					result = endpointClient.getMyCoupons(listMyCouponsInfo);
				} else if (usedTabString.equals(tabId)) {
					listMyCouponsInfo.put("page_orderBy", "useTime_desc,transferTime_desc");
					listMyCouponsInfo.put("search_INI_status", StatusCode.COUPONS_USED + ","
							+ StatusCode.COUPONS_TRANSFER);

					result = endpointClient.getMyCoupons(listMyCouponsInfo);
				} else if (expiredOrderTabString.equals(tabId)) {
					listMyCouponsInfo.put("page_orderBy", "couponItem_endDate_desc");
					listMyCouponsInfo.put("search_EQI_status", String.valueOf(StatusCode.COUPONS_EXPIRED));

					result = endpointClient.getMyCoupons(listMyCouponsInfo);
				}

				if (null == result || !StatusCode.SUCCESS.equals((String) result.get("statusCode"))) {
					return null;
				}

				int totalPage = (Integer) result.get("totalPage");

				if (pageNo >= totalPage) {
					couponsListView.notifyIsLastPage();
				}

				List<Map<String, ?>> couponsList = (List<Map<String, ?>>) result.get("result");

				Log.d(Tag, "totalPage=" + totalPage + ", nextPageCount=" + pageNo + ", couponsList=" + couponsList);

				return couponsList;
			}

			@Override
			protected View getView(int arg0, final Map<String, ?> coupons, View arg2, ViewGroup arg3) {
				LinearLayout couponsItem = (LinearLayout) getLayoutInflater().inflate(R.layout.my_coupons_item, null);

				Integer couponsStatus = (Integer) coupons.get("couponStatus");

				imageDownloader.download((String) coupons.get("logoUrl"),
						((ImageView) couponsItem.findViewById(R.id.logo)));

				TextView nameTextView = (TextView) couponsItem.findViewById(R.id.name);
				nameTextView.setText((String) coupons.get("name"));

				TextView infoTextView = (TextView) couponsItem.findViewById(R.id.info);
				infoTextView.setText((String) coupons.get("description"));

				TextView timeTextView = (TextView) couponsItem.findViewById(R.id.time);
				TextView endTimeTextView = (TextView) couponsItem.findViewById(R.id.end_time);
				TextView contentTextView = (TextView) couponsItem.findViewById(R.id.content);

				if (couponsStatus == StatusCode.COUPONS_NOT_USE) {
					TextView shareTextView = (TextView) couponsItem.findViewById(R.id.share);
					// SpannableString sp = new SpannableString();
					// sp.setSpan(new ForegroundColorSpan(Color.RED), 0, 1,
					// Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					shareTextView.setText("我要转让>");
					shareTextView.setVisibility(View.VISIBLE);
					shareTextView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							transferCoupon(coupons);
						}
					});

					timeTextView.setText("开始时间:" + coupons.get("beginDate"));
					endTimeTextView.setText("过期时间:" + coupons.get("endDate"));
					endTimeTextView.setVisibility(View.VISIBLE);
					contentTextView.setText("使用说明：" + coupons.get("useDesc"));
				} else if (couponsStatus == StatusCode.COUPONS_USED) {
					contentTextView.setVisibility(View.GONE);
					timeTextView.setText("使用时间:" + coupons.get("useDate"));

					SpannableString sp = new SpannableString("订单号:" + coupons.get("orderNo") + "\n点此查看详情\n");
					sp.setSpan(new ForegroundColorSpan(Color.RED), sp.length() - 7, sp.length(),
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

					endTimeTextView.setText(sp);
					endTimeTextView.setVisibility(View.VISIBLE);
					endTimeTextView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MyCouponsActivity.this, MyOrderDetailActivity.class);
							intent.putExtra("orderNo", (String) coupons.get("orderNo"));
							startActivity(intent);
						}
					});
				} else if (couponsStatus == StatusCode.COUPONS_TRANSFER) {
					contentTextView.setVisibility(View.GONE);
					timeTextView.setText("转让时间:" + coupons.get("useDate"));
					endTimeTextView.setText("转让给：" + coupons.get("receiverName") + "\n");
					endTimeTextView.setVisibility(View.VISIBLE);
				} else if (couponsStatus == StatusCode.COUPONS_EXPIRED) {
					timeTextView.setText("过期时间:" + coupons.get("endDate"));
					contentTextView.setText("使用说明：" + coupons.get("useDesc"));
				}

				return couponsItem;
			}

			@Override
			protected void onPageChanged(List<Map<String, ?>> arg0) {
				// TODO Auto-generated method stub
			}
		};

		couponsListView.setAdapter(adapter);

		return adapter;
	}

	private void transferCoupon(Map<String, ?> coupons) {
		Intent intent = new Intent(this, TransferCouponsActivity.class);
		intent.putExtra("coupons", (Serializable) coupons);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case RESULT_OK:
			String couponNo = data.getStringExtra("couponNo");

			if (null == couponNo || couponNo.length() == 0) {
				return;
			}

			List<Map<String, ?>> myCouponsList = myCouponsListAdapter.getItemDataList();

			for (Map<String, ?> coupons : myCouponsList) {
				if (couponNo.equals(coupons.get("couponNo"))) {
					myCouponsList.remove(coupons);

					myCouponsListAdapter.notifyDataSetChanged();

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
